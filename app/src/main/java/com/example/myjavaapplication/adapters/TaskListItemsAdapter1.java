package com.example.myjavaapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.activities.CardListActivity;
import com.example.myjavaapplication.model.Task;

import java.util.ArrayList;

public class TaskListItemsAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Task> list;

    public TaskListItemsAdapter1(Context context, ArrayList<Task> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            Task model = list.get(holder.getAdapterPosition());

            TextView taskNameView = myViewHolder.itemView.findViewById(R.id.tv_name);
            taskNameView.setText(model.getName());
            ((TextView) myViewHolder.itemView.findViewById(R.id.tv_created_by)).setText("Created By : " + model.getCreatedBy());

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CardListActivity.class);
                    intent.putExtra("taskName", String.valueOf(model.getName()));
                    int taskId=model.getId();
                    SharedPreferences sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("TASK_ID", taskId);
                    editor.putString("TASK_NAME", model.getName());
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
