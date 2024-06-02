package com.example.myjavaapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Task implements Parcelable {
    private int id;
    private int board_id;
    private String name;
    private String createdBy;
    private ArrayList<Card> cardList;

    public Task() {
        // Empty constructor required by Parcelable
    }

    public Task(int id, int board_id, String name, String createdBy) {
        this.id = id;
        this.board_id = board_id;
        this.name = name;
        this.createdBy = createdBy;
        //this.cardList = new ArrayList<>(); // Initialize cardList
    }

    protected Task(Parcel in) {
        id=in.readInt();
        board_id=in.readInt();
        name = in.readString();
        createdBy = in.readString();
        cardList = in.createTypedArrayList(Card.CREATOR); // Read the cardList from the Parcel
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(board_id);
        dest.writeString(name);
        dest.writeString(createdBy);
        dest.writeTypedList(cardList); // Write the cardList to the Parcel
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    // Getters and setters for private fields

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBoard_id() {
        return board_id;
    }

    public void setBoard_id(int board_id) {
        this.board_id = board_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    // Methods for manipulating cardList

    public ArrayList<Card> getCardList() {
        return cardList;
    }

    public void setCardList(ArrayList<Card> cardList) {
        this.cardList = cardList;
    }


}
