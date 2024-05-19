package com.example.myjavaapplication.controllers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_BOARD_ID = "boardId";

    private SharedPreferences sharedPreferences;

    public SharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void setBoardId(String boardId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_BOARD_ID, boardId);
        editor.apply();
    }

    public String getBoardId() {
        return sharedPreferences.getString(KEY_BOARD_ID, null);
    }
}
