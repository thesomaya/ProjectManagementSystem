package com.example.myjavaapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.databinding.ActivityCreateCardBinding;
import com.example.myjavaapplication.databinding.ActivityCreateTaskBinding;
import com.example.myjavaapplication.model.Member;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateCardActivity extends BaseActivity {

    private ActivityCreateCardBinding binding;
    private
    ArrayList<Member> membersArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        membersArrayList=new ArrayList<>();
        Log.e("membersArrayList", String.valueOf(membersArrayList));
        setupActionBar();
        binding.btnCreate.setOnClickListener(v -> {
            createNewCard();
        });

        //due date edit text
        binding.etDueDate.setOnClickListener(v -> {
            // Get current date
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and show it
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                // Handle date selection
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        binding.etDueDate.setText(selectedDate);
            }, year, month, day);
            datePickerDialog.show();
        });

        //member edit text
        addMembersSpinner();
        Log.e("membersArrayList", String.valueOf(membersArrayList));

    }
    private void setupActionBar() {
        setSupportActionBar(binding.toolbarCreateCardActivity);
        getSupportActionBar().setTitle("Create Card");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
        binding.toolbarCreateCardActivity.setNavigationOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, CardListActivity.class);
        startActivity(intent);
        finish();
    }

    private void cardCreatedSuccessfully() {
        hideProgressDialog();
        Toast.makeText(getApplicationContext(),"Card is Created Successfully",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(), CardListActivity.class);
        startActivity(intent);
        finish();
    }

    private void createNewCard() {
        showProgressDialog(getResources().getString(R.string.please_wait));

        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int taskId = sharedPref.getInt("TASK_ID", -1);
        int userId = sharedPref.getInt("USER_ID", -1);


        VolleyRequest volleyRequest = new VolleyRequest(this);
        String url = links.LINK_CREATE_CARD;
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("task_id", taskId);
            requestBody.put("created_by", userId);
            String[] parts = binding.membersSpinner.getSelectedItem().toString().split("\\s+");
            String member_id_string = parts[0];
            try {
                int member_id = Integer.parseInt(member_id_string);
                Log.e("member_id", String.valueOf(member_id));
                requestBody.put("member_id", member_id);
                requestBody.put("name", binding.etCardName.getText().toString());
                requestBody.put("due_date", binding.etDueDate.getText().toString());
                Log.e("member_id", binding.etDueDate.getText().toString());
            } catch (NumberFormatException e) {
                Log.e("CardActivityIntegerConvert","member_id_string is not a valid integer");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("CardActivityJSONException", e.getMessage());
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
    private void addMembersSpinner() {
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
                    members.add("Select Member");
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject userNameObject = dataArray.getJSONObject(i);
                        String user_name = userNameObject.getString("user_name");
                        int user_id = userNameObject.getInt("user_id");
                        members.add(user_id+" "+user_name);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                            members);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.membersSpinner.setAdapter(adapter);
                } else {
                    Log.e("addMembersSpinner", "Server returned failure status: " + status);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("addMembersSpinner", "JSON parsing error: " + e.getMessage());
            }
        }, errorMessage -> {
            Log.e("addMembersSpinner", "Volley request error: " + errorMessage);
        });
    }






}