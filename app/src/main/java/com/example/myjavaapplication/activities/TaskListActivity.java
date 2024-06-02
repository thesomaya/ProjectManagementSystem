package com.example.myjavaapplication.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.adapters.CardListItemsAdapter;
import com.example.myjavaapplication.adapters.TaskListItemsAdapter;
import com.example.myjavaapplication.controllers.CardFetchCallback;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.databinding.ActivityTaskListBinding;
import com.example.myjavaapplication.model.Card;
import com.example.myjavaapplication.model.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskListActivity extends BaseActivity implements CardListItemsAdapter.OnFileSelectedListener{

    private ActivityTaskListBinding binding;
    TextView tv_document_status;
    private static final int REQUEST_CODE_PICK_FILE = 1;
    CardListItemsAdapter cardListItemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupActionBar();
        fetchTasksFromDatabase();

        tv_document_status = findViewById(R.id.tv_document_status);
        binding.ivAddMember.setOnClickListener(v -> {
            fetchMemberMenuItemsAndShowPopup();
        });

    }

    private void fetchMemberMenuItemsAndShowPopup() {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int boardIdSh = sharedPref.getInt("BOARD_ID", -1);

        VolleyRequest volleyRequest = new VolleyRequest(this);

        String url = links.LINK_READ_MEMBER + "?board_id=" + boardIdSh;

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                if (status.equals("success")) {
                    List<String> members = new ArrayList<>();
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject userNameObject = dataArray.getJSONObject(i);
                        String user_name = userNameObject.getString("user_name");
                        int user_id = userNameObject.getInt("user_id");
                        int userIdSh = sharedPref.getInt("USER_ID",-1);
                        if (user_id == userIdSh){
                            members.add("Me");
                        }else {
                            members.add(user_name);
                        }

                    }

                    runOnUiThread(() -> createPopupMenu(members)); // Ensure UI operations are performed on the UI thread
                } else {
                    Log.e("fetchMemberMenuItemsAndShowPopup", status);
                    // Handle failure case (e.g., show an error message)
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("fetchMemberMenuItemsAndShowPopup", "JSON parsing error: " + e.getMessage());
                // Handle JSON parsing error (e.g., show an error message)
            }
        }, errorMessage -> {
            Log.e("fetchMemberMenuItemsAndShowPopup", "Volley request error: " + errorMessage);
            // Handle Volley request error (e.g., show an error message)
        });
    }

    private void createPopupMenu(List<String> members) {
        PopupMenu popupMenu = new PopupMenu(this, binding.ivAddMember);
        Menu menu = popupMenu.getMenu();

        // Add menu items dynamically for members
        for (int i = 0; i < members.size(); i++) {
            menu.add(Menu.NONE, i, Menu.NONE, members.get(i));
        }


        // Add the additional item with an icon
        menu.add(Menu.NONE, members.size() + 1, Menu.NONE, "Add new member").setIcon(R.drawable.ic_members);

        // Set click listener for menu items
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == members.size() + 1) {
                Intent intent= new Intent(this, CreateMemberActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }


    private void setupActionBar() {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String boardName = sharedPref.getString("BOARD_NAME", null);
        setSupportActionBar(binding.toolbarTaskListActivity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            getSupportActionBar().setTitle(boardName);
            binding.toolbarTaskListActivity.setNavigationOnClickListener(view -> onBackPressed());
        }

    }


    private void fetchTasksFromDatabase() {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int boardIdSh = sharedPref.getInt("BOARD_ID", -1);

        // Instantiate VolleyRequest
        VolleyRequest volleyRequest = new VolleyRequest(this);

        // Define URL for GET request
        String url = links.LINK_READ_TASK + "?board_id=" + boardIdSh;
        ArrayList<Task> tasksList = new ArrayList<>();
        tasksList.add(new Task());
        populateTasksListToUI(tasksList);

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                if (status.equals("success")) {
                    JSONArray dataArray = response.getJSONArray("data");
                    tasksList.clear();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject boardObject = dataArray.getJSONObject(i);
                        int boardId = boardObject.getInt("board_id");
                        int taskId = boardObject.getInt("task_id");
                        String taskName = boardObject.getString("task_name");
                        String createdBy = boardObject.getString("user_name");

                        Task task = new Task(taskId, boardId, taskName, createdBy);
                        tasksList.add(task);
                    }

                    tasksList.add(new Task());
                    populateTasksListToUI(tasksList);

                } else {
                    Log.e("TaskActivity", status);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("TaskActivity", Objects.requireNonNull(e.getMessage()));
            }
        }, errorMessage -> Log.e("errorUploadingTasks", errorMessage));

    }

    private void populateTasksListToUI(ArrayList<Task> tasksList) {
        binding.rvTaskList.setVisibility(View.VISIBLE);
        binding.rvTaskList.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false));
        TaskListItemsAdapter adapter = new TaskListItemsAdapter(this, tasksList);
        binding.rvTaskList.setAdapter(adapter);
    }

    public void taskCreatedSuccessfully() {
        hideProgressDialog();
        finish();
    }

    public void createNewTask(String taskName) {
        showProgressDialog(getResources().getString(R.string.please_wait));

        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int boardId = sharedPref.getInt("BOARD_ID", -1);
        int userId = sharedPref.getInt("USER_ID", -1);
        String boardName=sharedPref.getString("BOARD_NAME",null);

        VolleyRequest volleyRequest = new VolleyRequest(this);
        String url = links.LINK_CREATE_TASK;
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("board_id", boardId);
            requestBody.put("created_by", userId);
            requestBody.put("name", taskName);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", Objects.requireNonNull(e.getMessage()));
        }

        // Make the POST request
        volleyRequest.postRequest(url, requestBody, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                taskCreatedSuccessfully();
                Toast.makeText(getApplicationContext(),"Task is Created Successfully",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(), TaskListActivity.class);
                intent.putExtra("boardName",boardName);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                hideProgressDialog();
                Log.e("createNewTask",error);
            }


        });

    }

    public void cardCreatedSuccessfully() {
        hideProgressDialog();
        Toast.makeText(getApplicationContext(),"Card is Created Successfully",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(), TaskListActivity.class);
        startActivity(intent);
        finish();
    }
    public void cardsUpdatedSuccessfully() {
        hideProgressDialog();
        Toast.makeText(getApplicationContext(),"Cards Updated Successfully",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(), TaskListActivity.class);
        startActivity(intent);
        finish();
    }

    public void createNewCard(int taskId, String cardName) {
        showProgressDialog(getResources().getString(R.string.please_wait));

        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int userId = sharedPref.getInt("USER_ID", -1);


        VolleyRequest volleyRequest = new VolleyRequest(this);
        String url = links.LINK_CREATE_CARD;
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("task_id", taskId);
            requestBody.put("created_by", userId);
            requestBody.put("name", cardName);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("CardActivityJSONException", Objects.requireNonNull(e.getMessage()));
        }

        // Make the POST request
        volleyRequest.postRequest(url, requestBody, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                cardCreatedSuccessfully();
            }

            @Override
            public void onError(String error) {
                hideProgressDialog();
                Log.e("CardActivityError",error);
            }


        });

    }

    public void fetchCardsFromDatabase(int taskId, CardFetchCallback callback) {
        VolleyRequest volleyRequest = new VolleyRequest(this);

        String url = links.LINK_READ_CARD + "?task_id=" + taskId;

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            ArrayList<Card> cardsList = new ArrayList<>();
            try {
                String status = response.getString("status");
                if (status.equals("success")) {
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject cardObject = dataArray.getJSONObject(i);
                        int cardId = cardObject.getInt("card_id");
                        int card_order = cardObject.getInt("card_order");
                        String cardName = cardObject.getString("name");
                        int createdBy = cardObject.getInt("created_by");
                        String assigned_to = cardObject.getString("user_name");
                        String due_date = cardObject.getString("due_date");
                        String due_time = cardObject.getString("due_time");
                        String label = cardObject.getString("label");
                        String document = cardObject.getString("document");
                        Card card = new Card(cardId, card_order, cardName, String.valueOf(createdBy),
                                due_date, due_time, assigned_to, label, document);
                        cardsList.add(card);
                        Log.e("CardActivityCard", cardName);
                    }
                    callback.onCardsFetched(cardsList);
                } else {
                    callback.onError(status);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onError(e.getMessage());
            }
        }, callback::onError);
    }


    public void updateCardsInTaskList(final int taskId, final ArrayList<Card> cards) {
        // Start by deleting the cards for the given task ID
        setCardOrderZero(taskId, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                // Cards deleted successfully, start updating cards sequentially
                updateCardSequentially(taskId, cards, 0);
            }

            @Override
            public void onError(String error) {
                // Log the error, cards cannot be updated without deletion
                Log.e("DeleteCard", "Error: " + error);
            }
        });
    }

    private void updateCardSequentially(final int taskId, final ArrayList<Card> cards, final int index) {
        if (index >= cards.size()) {
            // All cards have been updated
            return;
        }

        // Update the card at the current index
        updateCardOrder(taskId, cards.get(index), new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                // Card updated successfully, move to the next card
                updateCardSequentially(taskId, cards, index + 1);
            }

            @Override
            public void onError(String error) {
                // Log the error and stop updating further cards
                Log.e("CardUpdateError", "Error updating card: " + error);
            }
        });
    }

    public void updateCardOrder(int taskId, Card card, final VolleyRequest.VolleyCallback callback) {
        VolleyRequest volleyRequest = new VolleyRequest(this);
        String url = links.LINK_UPDATE_CARD_ORDER + "?task_id=" + taskId +"&card_id="+card.getId();

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                String message = response.getString("message");
                if (status.equals("success")) {
                    Log.i("updateCardOrder", message);
                    callback.onSuccess(response); // Call the success callback
                } else {
                    Log.e("updateCardOrder", message);
                    callback.onError("updateCardOrder failed");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("updateCardOrder", "Error: " + e.getMessage());
                callback.onError("JSON parsing error");
            }
        }, errorMessage -> {
            Log.e("updateCardOrder", "Error: " + errorMessage);
            callback.onError("Network error");
        });
    }
    private void setCardOrderZero(int taskId, final VolleyRequest.VolleyCallback callback) {
        VolleyRequest volleyRequest = new VolleyRequest(this);
        String url = links.LINK_SET_CARD_ORDER_ZERO + "?task_id=" + taskId;

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                String message = response.getString("message");
                if (status.equals("success")) {
                    Log.i("setCardOrderZero", message);
                    callback.onSuccess(response); // Call the success callback
                } else {
                    Log.e("setCardOrderZero", message);
                    callback.onError("setCardOrderZero failed");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("setCardOrderZero", "Error: " + e.getMessage());
                callback.onError("JSON parsing error");
            }
        }, errorMessage -> {
            Log.e("setCardOrderZero", "Error: " + errorMessage);
            callback.onError("Network error");
        });
    }
    public void deleteTaskById(int taskId) {
        VolleyRequest volleyRequest = new VolleyRequest(this);
        String url = links.LINK_DELETE_TASK + "?task_id=" + taskId;

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                String message = response.getString("message");
                if (status.equals("success")) {
                    Log.i("DeleteCard", message);
                    taskDeletedSuccessfully();
                } else {
                    Log.e("DeleteCard", message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("DeleteCard", "Error: " + e.getMessage());
            }
        }, errorMessage -> {
            Log.e("DeleteCard", "Error: " + errorMessage);
        });
    }
    private void taskDeletedSuccessfully() {
        hideProgressDialog();
        Toast.makeText(getApplicationContext(),"Task Deleted Successfully",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(), TaskListActivity.class);
        startActivity(intent);
        finish();
    }

    public void updateTaskName(int taskId, String newName) {
        VolleyRequest volleyRequest = new VolleyRequest(this);
        String url = links.LINK_UPDATE_TASK + "?task_id=" + taskId + "&new_name=" + newName;

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                String message = response.getString("message");
                if (status.equals("success")) {
                    Log.i("updateTask", message);
                    taskUpdatedSuccessfully();
                } else {
                    Log.e("updateTask", message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("updateTask", "Error: " + e.getMessage());
            }
        }, errorMessage -> {
            Log.e("updateTask", "Error: " + errorMessage);
        });
    }
    private void taskUpdatedSuccessfully() {
        hideProgressDialog();
        //Toast.makeText(getApplicationContext(),"Task Name Updated Successfully",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(), TaskListActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println("111111");

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                System.out.println("222222");
                Uri uri = data.getData();

                if (uri != null) {
                    //cardListItemsAdapter = new CardListItemsAdapter(this,uri);
                    CardListItemsAdapter.setSelectedFileUri(uri);
                    onFileSelected(uri);
                    String fileName = getFileName(uri);
                    System.out.println("filename: "+fileName);

                }
            }
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        System.out.println("nnnnnnn");
        String result = null;
        if (uri.getScheme().equals("content")) {
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = null;
            try {
                cursor = contentResolver.query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        System.out.println(result);

        return result;
    }

    @Override
    public void onFileSelected(Uri uri) {
        String fileName = getFileName(uri);
        System.out.println("Selected file: " + fileName);
    }
}



