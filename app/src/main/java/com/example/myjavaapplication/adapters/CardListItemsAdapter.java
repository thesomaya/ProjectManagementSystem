package com.example.myjavaapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.controllers.ItemTouchHelperAdapter;
import com.example.myjavaapplication.model.Card;

import java.util.ArrayList;

public class CardListItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private ArrayList<Card> list;

    public CardListItemsAdapter(Context context, ArrayList<Card> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            Card model = list.get(holder.getAdapterPosition());

            TextView cardNameView = myViewHolder.itemView.findViewById(R.id.tv_card_name);
            cardNameView.setText(model.getName());
//            ((TextView) myViewHolder.itemView.findViewById(R.id.tv_created_by)).setText("Assigned to : " + model.getAssignedTo());
//            ((TextView) myViewHolder.itemView.findViewById(R.id.tv_due_date)).setText("Due Date : " + model.getDue_date());


        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void onDragEnded(RecyclerView.ViewHolder viewHolder) {

    }


    private static class MyViewHolder extends RecyclerView.ViewHolder {
        MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
