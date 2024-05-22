package com.example.myjavaapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable {
    private int id;

    private int card_order;
    private String name;
    private String createdBy;
    private String due_date;
    private String due_time;
    private String assignedTo;
    private String label;



    protected Card(Parcel in) {
        id = in.readInt();
        card_order = in.readInt();
        name = in.readString();
        createdBy = in.readString();
        due_date=in.readString();
        due_time=in.readString();
        assignedTo = in.readString();
        label=in.readString();
    }

    public Card(int id, int card_order, String name, String createdBy, String due_date, String due_time, String assignedTo, String label) {
        this.id = id;
        this.card_order = card_order;
        this.name = name;
        this.createdBy = createdBy;
        this.due_date = due_date;
        this.due_time = due_time;
        this.assignedTo = assignedTo;
        this.label = label;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(card_order);
        dest.writeString(name);
        dest.writeString(createdBy);
        dest.writeString(due_date);
        dest.writeString(due_time);
        dest.writeString(assignedTo);
        dest.writeString(label);

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

    public int getCard_order() {
        return card_order;
    }

    public void setCard_order(int card_order) {
        this.card_order = card_order;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDue_time() {
        return due_time;
    }

    public void setDue_time(String due_time) {
        this.due_time = due_time;
    }
}
