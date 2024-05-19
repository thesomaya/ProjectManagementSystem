package com.example.myjavaapplication.controllers;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void onDragStarted(RecyclerView.ViewHolder viewHolder);

    void onDragEnded(RecyclerView.ViewHolder viewHolder);
}

