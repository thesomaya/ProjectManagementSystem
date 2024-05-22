package com.example.myjavaapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Board implements Parcelable {
    private int id;
    private String name;
    private String image;
    private String createdBy;
    private ArrayList<String> assignedTo;
    private String documentId;
    private ArrayList<Task> taskList;

    public Board() {
        // Empty constructor required by Parcelable
    }

    protected Board(Parcel in) {
        id=in.readInt();
        name = in.readString();
        image = in.readString();
        createdBy = in.readString();
        assignedTo = in.createStringArrayList();
        documentId = in.readString();
        taskList = in.createTypedArrayList(Task.CREATOR);
    }
    public Board(String name, ArrayList<String> assignedTo, String createdBy) {
        this.name = name;
        this.assignedTo = assignedTo;
        this.createdBy = createdBy;
    }
    public Board(int id, String name, String createdBy) {
        this.id=id;
        this.name = name;
        this.createdBy = createdBy;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(createdBy);
        dest.writeStringList(assignedTo);
        dest.writeString(documentId);
        dest.writeTypedList(taskList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ArrayList<String> getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(ArrayList<String> assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }
    public static Board fromJson(JSONObject jsonObject) throws JSONException {
        Board board = new Board();
        board.setCreatedBy(jsonObject.getString("board_id"));
        board.setName(jsonObject.getString("name"));
        board.setDocumentId(jsonObject.getString("document_id"));

        // Parse other fields as needed
        return board;
    }
}
