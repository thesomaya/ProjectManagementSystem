package com.example.myjavaapplication.controllers;

import com.example.myjavaapplication.model.Board;
import com.example.myjavaapplication.model.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardsController {

    private final Crud crud = new Crud();
    private final String createBoard = links.LINK_CREATE_BOARD;
    private final String readData = links.LINK_VIEW_BOARD;
    private final String deleteRecord = links.LINK_DELETE_BOARD;

    private final SharedPreference sharedPreference;

    public BoardsController(SharedPreference sharedPreference) {
        this.sharedPreference = sharedPreference;
    }

    public boolean saveUserData(Board board) {
        try {
            Map<String, String> requestData = new HashMap<>();
            requestData.put("name", board.getName());
            requestData.put("created_by", sharedPreference.getUserId());

            JSONObject response = crud.postRequest(createBoard, requestData);

            System.out.println(response); // Print the response to check its structure
            if (response.getString("status").equals("success")) {
                if (response.has("board_id")) {
                    String boardId = response.getString("board_id");
                    sharedPreference.setBoardId(boardId);
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

    public List<Board> retrieveUserData() {
        try {
            // Check for internet connectivity
            boolean isOnline = true; // Check internet connectivity in Java

            if (isOnline) {
                Map<String, String> requestData = new HashMap<>();
                requestData.put("user_id", sharedPreference.getUserId());

                JSONObject response = crud.postRequest(readData, requestData);
                System.out.println(response);

                if (response.getString("status").equals("success") && response.has("data")) {
                    // Parse the data and return it
                    List<Board> boardDataList = new ArrayList<>();
                    for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                        JSONObject item = response.getJSONArray("data").getJSONObject(i);
                        boardDataList.add(Board.fromJson(item));
                    }
                    return boardDataList;
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
    public boolean deleteBoardData(String boardId) {
        try {
            Map<String, String> requestData = new HashMap<>();
            requestData.put("board_id", boardId);

            JSONObject response = crud.postRequest(deleteRecord, requestData);

            System.out.println(response); // Print the response to check its structure
            if (response.getString("status").equals("success")) {
                return true; // Operation was successful
            } else {
                System.out.println("Deletion failed");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false; // Operation failed
    }

}

