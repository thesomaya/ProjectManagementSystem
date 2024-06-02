package com.example.myjavaapplication.activities;

import static com.example.myjavaapplication.controllers.links.LINK_UPLOAD_IMAGES_USER;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myjavaapplication.R;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.databinding.ActivityMyProfileBinding;
import com.example.myjavaapplication.model.User;
import com.example.myjavaapplication.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MyProfileActivity extends BaseActivity {

    private static final int READ_STORAGE_PERMISSION_CODE = 300;
    private Uri mSelectedImageFileUri;
    private User user;
    int userId;
    SharedPreferences sharedPref;
    private ActivityMyProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupActionBar();
        sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("USER_ID", -1);
        getUserData(userId);

        binding.ivProfileUserImage.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            sActivityResultLauncher.launch(intent);

        });

        binding.btnUpdate.setOnClickListener(v -> {
            if (mSelectedImageFileUri != null) {
                try {
                    uploadImage(mSelectedImageFileUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            updateUserProfileData();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(MyProfileActivity.this);
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
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

        Glide.with(MyProfileActivity.this)
                .load(user.getImage())
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivProfileUserImage);

        binding.etName.setText(user.getName());
        binding.etEmail.setText(user.getEmail());
        if (user.getMobile() != "null") {
            binding.etMobile.setText(user.getMobile());
        }
    }
    private void uploadImage (Uri imageUri) throws IOException {
        if (imageUri != null) {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = readInputStreamToByteArray(inputStream);
            final String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = LINK_UPLOAD_IMAGES_USER;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success")) {
                                Toast.makeText(MyProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                profileUpdateSuccess();
                            } else {
                                Toast.makeText(MyProfileActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MyProfileActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                protected Map<String, String> getParams(){
                    Map<String, String> paramV = new HashMap<>();
                    paramV.put("user_id", String.valueOf(userId));
                    paramV.put("image", base64Image);
                    return paramV;
                }
            };
            queue.add(stringRequest);

        }
    }

    private byte[] readInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        inputStream.close();
        return buffer.toByteArray();
    }

    private void updateUserProfileData() {
        VolleyRequest volleyRequest = new VolleyRequest(this);
        String url = links.LINK_UPDATE_USER;
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("user_id", userId);
            requestBody.put("name", binding.etName.getText().toString());
            requestBody.put("email", binding.etEmail.getText().toString());
            requestBody.put("mobile", binding.etMobile.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException",e.getMessage());
        }

        volleyRequest.postRequest(url, requestBody, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.has("status") && response.getString("status").equals("success")) {
                        profileUpdateSuccess();
                    } else {
                        hideProgressDialog();
                        String errorMessage = response.getString("error");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    hideProgressDialog();
                    e.printStackTrace();
                    Log.e("UpdateUserActivityJSONException", e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                hideProgressDialog();
                Log.e("ErrorUpdatingUser",error);
            }
        });
    }

    public void profileUpdateSuccess() {
        hideProgressDialog();
        Toast.makeText(MyProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_OK);
        finish();
    }
    ActivityResultLauncher<Intent> sActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        mSelectedImageFileUri = data.getData();
                        binding.ivProfileUserImage.setImageURI(mSelectedImageFileUri);
                    }
                }
            }
    );

    private void getUserData(int userId) {
        VolleyRequest volleyRequest = new VolleyRequest(this);

        String url = links.LINK_READ_USER + "?user_id=" + userId;

        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                if (status.equals("success")) {
                    JSONObject userData = response.getJSONObject("data");

                    // Extract user data
                    String userName = userData.getString("name");
                    String userEmail = userData.getString("email");
                    String userMobile = userData.getString("mobile");
                    String userImage = userData.getString("image");

                    // Create User object
                    user = new User(userId, userName, userEmail, userImage, userMobile);

                    runOnUiThread(() -> setUserDataInUI(user));
                } else {
                    runOnUiThread(() -> Toast.makeText(MyProfileActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MyProfileActivity.this, "JSON parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }, null);
    }

}
