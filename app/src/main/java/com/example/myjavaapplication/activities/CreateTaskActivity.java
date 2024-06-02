package com.example.myjavaapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.databinding.ActivityCreateBoardBinding;
import com.example.myjavaapplication.databinding.ActivityCreateTaskBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateTaskActivity extends BaseActivity {

    private ActivityCreateTaskBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupActionBar();

        binding.btnCreate.setOnClickListener(v -> {
            createNewTask();
        });
    }
    private void setupActionBar() {
        setSupportActionBar(binding.toolbarCreateTaskActivity);
        getSupportActionBar().setTitle("Create Task");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
        binding.toolbarCreateTaskActivity.setNavigationOnClickListener(view -> onBackPressed());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, TaskListActivity.class);
        startActivity(intent);
        finish();
    }
    private void taskCreatedSuccessfully() {
        hideProgressDialog();
        finish();
    }

    private void createNewTask() {
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
            requestBody.put("name", binding.etTaskName.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", e.getMessage());
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
                Log.e("cretatasker",error);
            }


        });

    }
}