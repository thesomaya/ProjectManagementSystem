package com.example.myjavaapplication.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.myjavaapplication.R;
import com.example.myjavaapplication.databinding.ActivityMyProfileBinding;
import com.example.myjavaapplication.model.User;
import com.example.myjavaapplication.utils.Constants;


import java.util.HashMap;

public class MyProfileActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST_CODE = 200;
    private static final int READ_STORAGE_PERMISSION_CODE = 300;

    private Uri mSelectedImageFileUri;
    private User mUserDetails;
    private String mProfileImageURL = "";
    private ActivityMyProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupActionBar();

        //new FirestoreClass().loadUserData(MyProfileActivity.this, false);

        binding.ivProfileUserImage.setOnClickListener(v -> {
        //if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            //== PackageManager.PERMISSION_GRANTED) {
            Constants.showImageChooser(MyProfileActivity.this);
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                READ_STORAGE_PERMISSION_CODE
//            );
//        }
    });

        binding.btnUpdate.setOnClickListener(v -> {
        if (mSelectedImageFileUri != null) {
            //uploadUserImage();
        } else {
            showProgressDialog(getResources().getString(R.string.please_wait));
            //updateUserProfileData();
        }
    });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data != null
            && data.getData() != null) {
            mSelectedImageFileUri = data.getData();
            Glide.with(MyProfileActivity.this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivProfileUserImage);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Constants.showImageChooser(MyProfileActivity.this);
//            } else {
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    private void setupActionBar() {
        setSupportActionBar(binding.toolbarMyProfileActivity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            getSupportActionBar().setTitle(getResources().getString(R.string.my_profile));
        }
        binding.toolbarMyProfileActivity.setNavigationOnClickListener(v -> onBackPressed());
    }

    public void setUserDataInUI(User user) {
        mUserDetails = user;
        Glide.with(MyProfileActivity.this)
            .load(user.getImage())
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivProfileUserImage);
        binding.etName.setText(user.getName());
        binding.etEmail.setText(user.getEmail());
        if (user.getMobile() != 0L) {
            binding.etMobile.setText(String.valueOf(user.getMobile()));
        }
    }
/*
    private void uploadUserImage() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        if (mSelectedImageFileUri != null) {
            //StorageReference sRef = FirebaseStorage.getInstance().getReference().child(
                "USER_IMAGE" + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(MyProfileActivity.this, mSelectedImageFileUri)
            );
            sRef.putFile(mSelectedImageFileUri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl()
            .addOnSuccessListener(uri -> {
                mProfileImageURL = uri.toString();
                updateUserProfileData();
            }))
            .addOnFailureListener(e -> {
                Toast.makeText(MyProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                hideProgressDialog();
            });
        }
    }

    private void updateUserProfileData() {
        HashMap<String, Object> userHashMap = new HashMap<>();
        if (!mProfileImageURL.isEmpty() && !mProfileImageURL.equals(mUserDetails.getImage())) {
            userHashMap.put(Constants.IMAGE, mProfileImageURL);
        }
        if (!binding.etName.getText().toString().equals(mUserDetails.getName())) {
            userHashMap.put(Constants.NAME, binding.etName.getText().toString());
        }
        if (!binding.etMobile.getText().toString().equals(String.valueOf(mUserDetails.getMobile()))) {
            userHashMap.put(Constants.MOBILE, Long.parseLong(binding.etMobile.getText().toString()));
        }
        new FirestoreClass().updateUserProfileData(MyProfileActivity.this, userHashMap);
    }

    public void profileUpdateSuccess() {
        hideProgressDialog();
        Toast.makeText(MyProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_OK);
        finish();
    }
    */

}
