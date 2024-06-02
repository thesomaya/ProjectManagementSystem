package com.example.myjavaapplication.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.activities.TaskListActivity;
import com.example.myjavaapplication.adapters.CardListItemsAdapter;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.model.Card;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private static final int REQUEST_CODE_PICK_FILE = 1;
    private ArrayList<Card> list;
    List<String> labels = new ArrayList<>();
    List<String> members = new ArrayList<>();
    TextView tv_card_name, et_card_name_details, et_due_date_details, et_due_time_details, tv_document_status;
    ImageView iv_delete_card, iv_attachment_icon;
    Spinner member_spinner, label_spinner;
    Button btn_update_card;
    CardView card_card_view_details;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.card_details_bottom_sheet, container, false);
        card_card_view_details = view.findViewById(R.id.card_card_view_details);
        tv_card_name = view.findViewById(R.id.tv_card_name);
        et_card_name_details = view.findViewById(R.id.et_card_name_details);
        et_due_date_details = view.findViewById(R.id.et_due_date_details);
        et_due_time_details = view.findViewById(R.id.et_due_time_details);
        tv_document_status = view.findViewById(R.id.tv_document_status);
        iv_attachment_icon = view.findViewById(R.id.iv_attachment_icon);
        btn_update_card = view.findViewById(R.id.btn_update_card);
        iv_delete_card = view.findViewById(R.id.iv_delete_card);
        member_spinner = view.findViewById(R.id.member_spinner);
        label_spinner = view.findViewById(R.id.label_spinner);
        addMembersSpinner(member_spinner);
        addLabelsSpinner(label_spinner);

        fetchCardsFromDatabase(getArguments().getInt("card_id"), tv_card_name, et_card_name_details, member_spinner,
                label_spinner, et_due_date_details,  et_due_time_details, card_card_view_details);

        et_due_date_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(et_due_date_details);
            }
        });

        et_due_time_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(et_due_time_details);
            }
        });

        btn_update_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_card_name_details.getText().toString().trim();
                String dueDate = et_due_date_details.getText().toString().trim();
                String dueTime = et_due_time_details.getText().toString().trim();
                String label = label_spinner.getSelectedItem().toString();

                int memberId = -1;
                String selectedMember = member_spinner.getSelectedItem().toString();
                if (!selectedMember.equals("none")) {
                    String[] memberInfo = selectedMember.split(" ", 2);
                    memberId = Integer.parseInt(memberInfo[0]);
                }

                Uri attachmentUri = null;

            }
        });

        return view;
    }

    private void showDatePicker(TextView et_due_date_details) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        et_due_date_details.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void showTimePicker(TextView et_due_time_details) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        et_due_time_details.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void addMembersSpinner(Spinner member_spinner) {
        SharedPreferences sharedPref = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int boardIdSh = sharedPref.getInt("BOARD_ID", -1);

        VolleyRequest volleyRequest = new VolleyRequest(getContext());

        String url = links.LINK_READ_MEMBER + "?board_id=" + boardIdSh;

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                if (status.equals("success")) {
                    members.add("none");
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject userNameObject = dataArray.getJSONObject(i);
                        String user_name = userNameObject.getString("user_name");
                        int user_id = userNameObject.getInt("user_id");
                        members.add(user_id+" "+user_name);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                            members);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    member_spinner.setAdapter(adapter);
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

    private void addLabelsSpinner(Spinner member_spinner) {
        labels.add("not-started");
        labels.add("in-progress");
        labels.add("done");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        member_spinner.setAdapter(adapter);

    }

    private void fetchCardsFromDatabase(int card_id, TextView tv_card_name, TextView tv_card_name_details, Spinner member_spinner,
                                        Spinner label_spinner,
                                        TextView et_due_date_details, TextView et_due_time_details,
                                        CardView card_card_view_details) {
        Log.e("cardNameId", String.valueOf(card_id));
        // Instantiate VolleyRequest
        VolleyRequest volleyRequest = new VolleyRequest(getContext());

        // Define URL for GET request
        String url = links.LINK_READ_CARD_BY_ID + "?card_id=" + card_id;


        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                if (status.equals("success")) {
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject cardObject = dataArray.getJSONObject(i);
                        int cardId = cardObject.getInt("card_id");
                        int cardOrder = cardObject.getInt("card_order");
                        int member_id = cardObject.getInt("member_id");
                        String cardName = cardObject.getString("name");
                        int createdBy = cardObject.getInt("created_by");
                        String assigned_to = cardObject.getString("user_name");
                        String due_date = cardObject.getString("due_date");
                        String due_time = cardObject.getString("due_time");
                        String label = cardObject.getString("label");
                        String document = cardObject.getString("document");

                        tv_card_name.setText(cardName);
                        tv_card_name_details.setText(cardName);
                        et_due_date_details.setText(due_date);
                        et_due_time_details.setText(due_time);

                        for (i=0 ; i<labels.size() ; i++){
                            if(label.equals(labels.get(i))){
                                label_spinner.setSelection(i);
                            }
                        }

                        setCardColor(label, due_date, card_card_view_details);

                        for (i=0 ; i<members.size() ; i++){
                            String member = members.get(i);
                            if (member != "none") {
                                String[] arrOfStr = member.split(" ", 2);
                                int memberId = Integer.parseInt(arrOfStr[0]);
                                if (member_id == memberId) {
                                    member_spinner.setSelection(i);
                                }
                            }
                        }

                    }

                } else {
                    Log.e("CardActivity", status.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("CardActivity", e.getMessage());
            }
        }, errorMessage -> Log.e("errorUploadingCards", errorMessage));



    }

    private void setCardColor(String label, String due_date, CardView cardView) {
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
        String date_time = due_date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        if (due_date.isEmpty()){
            setCardColorBasedOnLabel(label, cardView);
        }
        try {
            // Parsing the strings into Date objects
            Date dueDateTime = dateFormat.parse(date_time);
            Date currentTime = dateFormat.parse(timeStamp);

            // Comparing the dates
            if (dueDateTime.before(currentTime)) {
                cardView.setCardBackgroundColor(Color.rgb(254, 179, 174));
            } else {
                setCardColorBasedOnLabel(label, cardView);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private static void setCardColorBasedOnLabel(String label, CardView cardView) {
        if(label.equals("in-progress")){
            cardView.setCardBackgroundColor(Color.rgb(211, 249, 250));
        }else if(label.equals("done")){
            cardView.setCardBackgroundColor(Color.rgb(226, 249, 197));
        }else{
            cardView.setCardBackgroundColor(Color.WHITE);
        }
    }

    public void updateCardDetails(int cardId, String name, int member_id, String due_date, String due_time, String label, Uri uri) {
        VolleyRequest volleyRequest = new VolleyRequest(getContext());
        String url = links.LINK_UPDATE_CARD_DETAILS + "?card_id="+cardId + "&name=" + name + "&member_id=" + member_id
                + "&due_date=" + due_date + "&due_time=" + due_time + "&label=" + label+ "&document=" + uri;

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                String message = response.getString("message");
                if (status.equals("success")) {
                    Log.i("updateCardDetails", message);
                    getContext().startActivity(new Intent(getContext(), TaskListActivity.class));
                    if (getContext() instanceof TaskListActivity) {
                        ((TaskListActivity) getContext()).finish();
                    }

                } else {
                    Log.e("updateCardDetails", message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("updateCardDetails", "Error: " + e.getMessage());
            }
        }, errorMessage -> {
            Log.e("updateCardDetails", "Error: " + errorMessage);
        });
    }
    public void deleteCardById(int cardId) {
        VolleyRequest volleyRequest = new VolleyRequest(getContext());
        String url = links.LINK_DELETE_CARD + "?card_id=" + cardId;

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                String message = response.getString("message");
                if (status.equals("success")) {
                    Toast.makeText(getContext(),"Task Deleted Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getContext(), TaskListActivity.class);
                    getContext().startActivity(intent);
                    if (getContext() instanceof TaskListActivity) {
                        ((TaskListActivity) getContext()).finish();
                    }
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

    private void alertDialogForDeleteList(int cardId, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Alert");
        builder.setMessage("Are you sure you want to delete " + title + ".");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Yes", (dialogInterface, which) -> {
            deleteCardById(cardId);
        });

        builder.setNegativeButton("No", (dialogInterface, which) -> dialogInterface.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }




}
