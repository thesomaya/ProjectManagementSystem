

package com.example.myjavaapplication.activities;

import static com.example.myjavaapplication.controllers.links.LINK_CREATE_BOARD;
import static com.example.myjavaapplication.controllers.links.LINK_UPLOAD_IMAGES_BOARD;
import static com.example.myjavaapplication.controllers.links.LINK_UPLOAD_IMAGES_USER;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myjavaapplication.R;
import com.example.myjavaapplication.controllers.SharedPreference;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.databinding.ActivityCreateBoardBinding;
import com.example.myjavaapplication.model.Board;
import com.example.myjavaapplication.utils.Constants;
import com.google.protobuf.StringValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateBoardActivity extends BaseActivity {

    private Uri mSelectedImageFileUri;
    private String mUserName;
    private ActivityCreateBoardBinding binding;
    SharedPreference sharedPreference;
    SharedPreferences sharedPref;
    String base64Image = null;

    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreference = new SharedPreference(CreateBoardActivity.this);
        sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("USER_ID", -1);
        setupActionBar();

        if (getIntent().hasExtra(Constants.NAME)) {
            mUserName = getIntent().getStringExtra(Constants.NAME);
        }

        binding.ivBoardImage.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            sActivityResultLauncher.launch(intent);

        });

        binding.btnCreate.setOnClickListener(view -> {

            try {
                createNewBoard(mSelectedImageFileUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
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

    private void boardCreatedSuccessfully() {
        Log.e("boardCreatedSuccessfully1","boardCreatedSuccessfully");
        hideProgressDialog();
        Toast.makeText(getApplicationContext(),"Board is Created Successfully",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void createNewBoard (Uri imageUri) throws IOException {

        if (imageUri != null) {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = readInputStreamToByteArray(inputStream);
            base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = LINK_CREATE_BOARD;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("success")) {
                                Toast.makeText(CreateBoardActivity.this, "Board created successfully", Toast.LENGTH_SHORT).show();
                                boardCreatedSuccessfully();
                            } else {
                                String error = jsonResponse.getString("error");
                                Toast.makeText(CreateBoardActivity.this, "Failed to create board: " + error, Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CreateBoardActivity.this, "Response parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateBoardActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> paramV = new HashMap<>();
                paramV.put("created_by", String.valueOf(userId));
                paramV.put("name", binding.etBoardName.getText().toString());
                if (base64Image != null)
                    paramV.put("image", base64Image);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }
    ActivityResultLauncher<Intent> sActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        mSelectedImageFileUri = data.getData();
                        binding.ivBoardImage.setImageURI(mSelectedImageFileUri);
                    }
                }
            }
    );
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

}
