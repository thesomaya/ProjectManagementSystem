package com.example.myjavaapplication.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myjavaapplication.databinding.ActivityIntroBinding;

public class IntroActivity extends AppCompatActivity {
    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        binding.btnSignInIntro.setOnClickListener(view -> {
        startActivity(new Intent(IntroActivity.this, SignInActivity.class));
    });

        binding.btnSignUpIntro.setOnClickListener(view -> {
        startActivity(new Intent(IntroActivity.this, SignUpActivity.class));
    });
    }
}