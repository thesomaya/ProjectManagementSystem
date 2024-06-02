package com.example.myjavaapplication.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myjavaapplication.controllers.SharedPreference;
import com.example.myjavaapplication.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // This is used to get the file from the assets folder and set it to the title textView.

        new Handler().postDelayed(() -> {
            // Check if there is a current user ID stored in shared preferences
            SharedPreference sharedPreference = new SharedPreference(this);
            String currentUserID = sharedPreference.getUserId();

            if (currentUserID != null && !currentUserID.isEmpty()) {
                // User is logged in, navigate to MainActivity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                // No current user, navigate to IntroActivity
                startActivity(new Intent(SplashActivity.this, IntroActivity.class));
            }
            finish();
        }, 2500);
    }
}
