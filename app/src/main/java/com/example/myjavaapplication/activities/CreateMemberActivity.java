package com.example.myjavaapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.databinding.ActivityCreateMemberBinding;
import com.example.myjavaapplication.model.Board;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreateMemberActivity extends BaseActivity {
    private ActivityCreateMemberBinding binding;
    private int userId =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupActionBar();

        binding.etMemberEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fetch email suggestions when text changes
                fetchEmailSuggestions(s.toString());
            }


            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this implementation
            }
        });

        binding.btnAddMember.setOnClickListener(v -> {
            getUserId();
        });

    }
    private void fetchEmailSuggestions(String partialEmail) {
        // Perform network request to fetch email suggestions from the server
        // Use Volley or Retrofit for network requests, replace with your own implementation
        VolleyRequest volleyRequest = new VolleyRequest(this);
        String url = links.LINK_EMAIL_SUGGESTION + "?partial_email=" + partialEmail;
        volleyRequest.getRequest(url, response -> {
            try {
                if (response != null) {
                    // Get the array of email suggestions from the JSONObject
                    JSONArray jsonArray = response.getJSONArray("data");
                    // Convert the JSONArray to an ArrayList of email suggestions
                    ArrayList<String> emailSuggestions = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String email = jsonArray.getString(i);
                        emailSuggestions.add(email);
                    }
                    // Populate suggestions into AutoCompleteTextView
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, emailSuggestions);
                    binding.etMemberEmail.setAdapter(adapter);
                } else {
                    // Handle empty response
                    Log.e("getEmailSuggestion", "Empty response");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("getEmailSuggestion", e.getMessage());
            }
        }, error -> {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            Log.e("getEmailSuggestion", error);
        });
    }

    private void setupActionBar() {
        setSupportActionBar(binding.toolbarCreateMemberActivity);
        getSupportActionBar().setTitle("Create Member");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
        binding.toolbarCreateMemberActivity.setNavigationOnClickListener(view -> onBackPressed());
    }


    private void memberAddedSuccessfully() {
        hideProgressDialog();
        Toast.makeText(getApplicationContext(),"Member is Added Successfully",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(), TaskListActivity.class);
        startActivity(intent);
        finish();
    }

    private void addNewMember(int userId) {
        showProgressDialog(getResources().getString(R.string.please_wait));

        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int boardId = sharedPref.getInt("BOARD_ID", -1);
        VolleyRequest volleyRequest = new VolleyRequest(this);
        String url = links.LINK_CREATE_MEMBER;
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("board_id", boardId);
            requestBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("addNewMemberJSONException", e.getMessage());
        }

        // Make the POST request
        volleyRequest.postRequest(url, requestBody, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.has("status") && response.getString("status").equals("success")) {
                        memberAddedSuccessfully();
                    } else {
                        // If the response has an error message, display it as a toast
                        hideProgressDialog();
                        String errorMessage = response.getString("error");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    hideProgressDialog();
                    e.printStackTrace();
                    Log.e("CardActivityJSONException", e.getMessage());
                }

            }
            @Override
            public void onError(String error) {
                hideProgressDialog();
                Log.e("addNewMemberError",error);
            }


        });

    }
    private void getUserId(){
        String member_email = binding.etMemberEmail.getText().toString();
        String url = links.LINK_GET_USER_BY_EMAIL + "?email=" + member_email;
        VolleyRequest volleyRequest = new VolleyRequest(this);
        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                if (status.equals("success")) {
                    JSONObject userIdObject = response.getJSONObject("data");
                    userId = userIdObject.getInt("user_id");
                    addNewMember(userId);
                } else {
                    Log.e("getUserId", status.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("getUserId", e.getMessage());
            }
        }, errorMessage -> Log.e("getUserId", errorMessage));
    }
}