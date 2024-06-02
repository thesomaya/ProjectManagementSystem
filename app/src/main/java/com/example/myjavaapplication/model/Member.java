package com.example.myjavaapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Member implements Parcelable {
    private int user_id;
    private String user_name;
    private int board_id;
    private String board_name;



    protected Member(Parcel in) {
        user_id=in.readInt();
        user_name = in.readString();
        board_id = in.readInt();
        board_name = in.readString();
    }

    public Member(int user_id, String user_name, int board_id, String board_name) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.board_id = board_id;
        this.board_name = board_name;
    }

    public Member(int user_id, String user_name) {
        this.user_id = user_id;
        this.user_name = user_name;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(user_id);
        dest.writeString(user_name);
        dest.writeInt(board_id);
        dest.writeString(board_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    // Getters and setters for private fields

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getBoard_id() {
        return board_id;
    }

    public void setBoard_id(int board_id) {
        this.board_id = board_id;
    }

    public String getBoard_name() {
        return board_name;
    }

    public void setBoard_name(String board_name) {
        this.board_name = board_name;
    }
}
