package com.example.myjavaapplication.controllers;

import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONException;
import org.json.JSONObject;
public class Crud {
    public JSONObject getRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = conn.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                StringBuilder responseBuilder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    responseBuilder.append(scanner.nextLine());
                }
                scanner.close();
                return new JSONObject(responseBuilder.toString());
            } else {
                System.out.println("Error: " + conn.getResponseCode());
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public JSONObject postRequest(String urlString, Map<String, String> data) {
        System.out.println("Crud File: " + urlString + data);
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(entry.getKey());
                postData.append('=');
                postData.append(entry.getValue());
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            // Get response code
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postDataBytes);
                    os.flush();
                }

                // Read response
                InputStream inputStream = conn.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                StringBuilder responseBuilder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    responseBuilder.append(scanner.nextLine());
                }
                scanner.close();

                String responseString = responseBuilder.toString();
                // Check for empty response before creating JSONObject
                if (responseString.isEmpty()) {
                    throw new JSONException("Empty response received from server");
                }
                return new JSONObject(responseString);
            } else {
                System.out.println("Error: " + responseCode);
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public JSONObject postRequestWithFile(String urlString, Map<String, String> data, File file) {
        // Implement this method according to your file uploading logic in Java.
        return null;
    }
}



