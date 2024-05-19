package com.example.myjavaapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable {
    private int id;
    private String name;
    private String createdBy;
    private String due_date;
    private String assignedTo;



    protected Card(Parcel in) {
        id = in.readInt();
        name = in.readString();
        createdBy = in.readString();
        due_date=in.readString();
        assignedTo = in.readString();
    }

    public Card(int id, String name, String createdBy, String due_date, String assignedTo) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.due_date = due_date;
        this.assignedTo = assignedTo;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(createdBy);
        dest.writeString(due_date);
        dest.writeString(assignedTo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    // Getters and setters for private fields

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }
}
