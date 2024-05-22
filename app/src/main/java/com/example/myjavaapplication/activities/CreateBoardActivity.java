package com.example.myjavaapplication.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.controllers.BoardsController;
import com.example.myjavaapplication.controllers.SharedPreference;
import com.example.myjavaapplication.controllers.UserController;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.databinding.ActivityCreateBoardBinding;
import com.example.myjavaapplication.model.Board;
import com.example.myjavaapplication.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreateBoardActivity extends BaseActivity {

    private Uri mSelectedImageFileUri;
    private String mUserName;
    private String mBoardImageURL = "";
    private ActivityCreateBoardBinding binding;
    SharedPreference sharedPreference;
    BoardsController boardsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreference = new SharedPreference(CreateBoardActivity.this);
        boardsController = new BoardsController(sharedPreference);
        setupActionBar();

        if (getIntent().hasExtra(Constants.NAME)) {
            mUserName = getIntent().getStringExtra(Constants.NAME);
        }

        binding.ivBoardImage.setOnClickListener(view -> {

    });

        binding.btnCreate.setOnClickListener(view -> {
        if (mSelectedImageFileUri != null) {
            uploadBoardImage();
        } else {
            createNewBoard();
        }
    });
    }



    private void setupActionBar() {
        setSupportActionBar(binding.toolbarCreateBoardActivity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
        binding.toolbarCreateBoardActivity.setNavigationOnClickListener(view -> onBackPressed());
        getSupportActionBar().setTitle("Create Board");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void uploadBoardImage() {

    }

    private void boardCreatedSuccessfully() {
        Log.e("boardCreatedSuccessfully1","boardCreatedSuccessfully");
        hideProgressDialog();
        Toast.makeText(getApplicationContext(),"Board is Created Successfully",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void createNewBoard() {
        showProgressDialog(getResources().getString(R.string.please_wait));

        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int userId = sharedPref.getInt("USER_ID", -1);

        VolleyRequest volleyRequest = new VolleyRequest(this);
        String url = links.LINK_CREATE_BOARD;
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("created_by", userId);
            requestBody.put("name", binding.etBoardName.getText().toString());
            //requestBody.put("image", mBoardImageURL);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException",e.getMessage());
        }

        // Make the POST request
        volleyRequest.postRequest(url, requestBody, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.has("status") && response.getString("status").equals("success")) {
                        boardCreatedSuccessfully();
                    } else {
                        // If the response has an error message, display it as a toast
                        hideProgressDialog();
                        String errorMessage = response.getString("error");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    hideProgressDialog();
                    e.printStackTrace();
                    Log.e("CreateBoardActivityJSONException", e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                hideProgressDialog();
                Log.e("ErrorAddingBoard",error);
            }


        });

    }


}