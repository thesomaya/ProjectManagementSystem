package com.example.myjavaapplication.controllers;

import android.content.SharedPreferences;

import com.example.myjavaapplication.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController {

    private final Crud crud = new Crud();
    private final String signupUrl = links.LINK_SIGNUP;
    private final String loginUrl = links.LINK_AUTHUSER;
    private final SharedPreference sharedPreference;

    public UserController(SharedPreference sharedPreference) {
        this.sharedPreference = sharedPreference;
    }

    public boolean saveUserData(User user) {
        try {
            Map<String, String> requestData = new HashMap<>();
            requestData.put("name", user.getName());
            requestData.put("email", user.getEmail());

            JSONObject response = crud.postRequest(signupUrl, requestData);

            System.out.println(response); // Print the response to check its structure
            if (response.getString("status").equals("success")) {
                if (response.has("user_id")) {
                    String userId = response.getString("user_id");
                    sharedPreference.setUserId(userId);
                    return true; // Operation was successful
                } else {
                    System.out.println("ID not found in the response"); // Check this print statement
                }
            } else {
                System.out.println("Addition failed");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false; // Operation failed
    }

    public List<User> retrieveUserData() {
        try {
            // Check for internet connectivity
            boolean isOnline = true; // Check internet connectivity in Java

            if (isOnline) {
                Map<String, String> requestData = new HashMap<>();
                requestData.put("user_id", sharedPreference.getUserId());

                JSONObject response = crud.postRequest(loginUrl, requestData);
                System.out.println(response);

                if (response.getString("status").equals("success") && response.has("data")) {
                    // Parse the data and return it
                    List<User> userDataList = new ArrayList<>();
                    for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                        JSONObject item = response.getJSONArray("data").getJSONObject(i);
                        userDataList.add(User.fromJson(item));
                    }
                    return userDataList;
                } else {
                    System.out.println("Error: Failed to retrieve user data");
                }
            } else {
                System.out.println("Error: No internet connection");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return new ArrayList<>(); // Return an empty list in case of an error
    }
}
