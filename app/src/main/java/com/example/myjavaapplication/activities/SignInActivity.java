package com.example.myjavaapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.databinding.ActivitySignInBinding;
import com.example.myjavaapplication.model.User;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends BaseActivity {

    private ActivitySignInBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setupActionBar();

        binding.btnSignIn.setOnClickListener(view -> signinRegisteredUser());
    }

    private void setupActionBar() {
        setSupportActionBar(binding.toolbarSignInActivity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp);

        binding.toolbarSignInActivity.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void signinRegisteredUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (validateForm(email, password)) {
            showProgressDialog(getResources().getString(R.string.please_wait));

            String url = links.LINK_AUTHUSER;

            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("email", email);
                requestBody.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            VolleyRequest volleyRequest = new VolleyRequest(this);
            volleyRequest.postRequest(url, requestBody, new VolleyRequest.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            int userId = response.getInt("user_id");

                            SharedPreferences sharedPreference = getSharedPreferences(
                                    "MyAppPrefs", Context.MODE_PRIVATE
                            );
                            SharedPreferences.Editor editor = sharedPreference.edit();
                            editor.putInt("USER_ID", userId);
                            editor.apply();
                            signInSuccess();
                        } else {
                            Toast.makeText(SignInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SignInActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    } finally {
                        hideProgressDialog();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(SignInActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            });
        }
    }


    private boolean validateForm(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.");
            return false;
        }
        return true;
    }

    public void signInSuccess() {
        hideProgressDialog();
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }
}