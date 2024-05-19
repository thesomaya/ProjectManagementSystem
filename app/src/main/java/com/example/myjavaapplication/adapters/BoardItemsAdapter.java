package com.example.myjavaapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myjavaapplication.R;
import com.example.myjavaapplication.activities.TaskListActivity;
import com.example.myjavaapplication.model.Board;

import java.util.ArrayList;

public class BoardItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Board> list;

    public BoardItemsAdapter(Context context, ArrayList<Board> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_board, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            Board model = list.get(holder.getAdapterPosition());

            Glide.with(context)
                    .load(model.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into((ImageView) myViewHolder.itemView.findViewById(R.id.iv_board_image));

            ((TextView) myViewHolder.itemView.findViewById(R.id.tv_name)).setText(model.getName());
            ((TextView) myViewHolder.itemView.findViewById(R.id.tv_created_by)).setText("Created By : " + model.getCreatedBy());

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TaskListActivity.class);
                    intent.putExtra("boardName", String.valueOf(model.getName()));
                    int boardId=model.getId();
                    SharedPreferences sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("BOARD_ID", boardId);

                    editor.putString("BOARD_NAME", model.getName());
                    editor.apply();

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }





    private static class MyViewHolder extends RecyclerView.ViewHolder {
        MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
