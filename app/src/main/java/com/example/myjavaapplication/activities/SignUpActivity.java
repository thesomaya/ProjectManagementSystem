package com.example.myjavaapplication.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.controllers.SharedPreference;
import com.example.myjavaapplication.controllers.UserController;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.databinding.ActivitySignUpBinding;
import com.example.myjavaapplication.model.User;


import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends BaseActivity {
    private ActivitySignUpBinding binding;
    SharedPreference sharedPreference;
    UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreference = new SharedPreference(this);
        userController = new UserController(sharedPreference);

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setupActionBar();

        binding.btnSignUp.setOnClickListener(view -> registerUser());
    }

    private void setupActionBar() {
        setSupportActionBar(binding.toolbarSignUpActivity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp);

        binding.toolbarSignUpActivity.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void registerUser() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (validateForm(name, email, password)) {
            showProgressDialog(getResources().getString(R.string.please_wait));

            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("name", name);
                requestBody.put("email", email);
                requestBody.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = links.LINK_SIGNUP;
            VolleyRequest volleyRequest = new VolleyRequest(this);
            volleyRequest.postRequest(url, requestBody, new VolleyRequest.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        int userId = response.getInt("user_id");

                        SharedPreferences sharedPreference = getSharedPreferences(
                                "MyAppPrefs", Context.MODE_PRIVATE
                        );
                        SharedPreferences.Editor editor = sharedPreference.edit();
                        editor.putInt("USER_ID", userId);
                        editor.apply();

                        hideProgressDialog();

                        userRegisteredSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("SignUpActivity", "Error parsing JSON: " + e.getMessage()); // Log JSON exception
                        Toast.makeText(SignUpActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(SignUpActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    Log.e("SignUpActivity", "Volley error: " + error); // Log Volley error
                    hideProgressDialog();
                }
            });
        }
    }


    private boolean validateForm(String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            showErrorSnackBar("Please enter name.");
            return false;
        } else if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.");
            return false;
        }
        return true;
    }

    public void userRegisteredSuccess() {
        Toast.makeText(
            SignUpActivity.this,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show();

        hideProgressDialog();
        finish();
    }
}