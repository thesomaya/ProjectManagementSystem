package com.example.myjavaapplication.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.activities.TaskListActivity;
import com.example.myjavaapplication.controllers.CardFetchCallback;
import com.example.myjavaapplication.model.Card;
import com.example.myjavaapplication.model.Task;

import java.util.ArrayList;
import java.util.Collections;

public class TaskListItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Task> list;
    private int mPositionDraggedFrom = -1
    ,mPositionDraggedTo = -1;

    public TaskListItemsAdapter(Context context, ArrayList<Task> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) (parent.getWidth() * 0.7),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ((LinearLayout.LayoutParams) layoutParams).setMargins((int) (15 * context.getResources().getDisplayMetrics().density), 0, (int) (40 * context.getResources().getDisplayMetrics().density), 0);
        view.setLayoutParams(layoutParams);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Task model = list.get(position);
        TextView tv_add_task_list = holder.itemView.findViewById(R.id.tv_add_task_list);
        LinearLayout ll_task_item = holder.itemView.findViewById(R.id.ll_task_item);
        TextView tv_task_list_title = holder.itemView.findViewById(R.id.tv_task_list_title);
        CardView cv_add_task_list_name = holder.itemView.findViewById(R.id.cv_add_task_list_name);
        ImageButton ib_close_list_name = holder.itemView.findViewById(R.id.ib_close_list_name);
        ImageButton ib_done_list_name = holder.itemView.findViewById(R.id.ib_done_list_name);
        TextView et_task_list_name = holder.itemView.findViewById(R.id.et_task_list_name);
        ImageButton ib_edit_list_name = holder.itemView.findViewById(R.id.ib_edit_list_name);
        LinearLayout ll_title_view = holder.itemView.findViewById(R.id.ll_title_view);
        CardView cv_edit_task_list_name = holder.itemView.findViewById(R.id.cv_edit_task_list_name);
        TextView et_edit_task_list_name = holder.itemView.findViewById(R.id.et_edit_task_list_name);
        ImageButton ib_close_editable_view = holder.itemView.findViewById(R.id.ib_close_editable_view);
        ImageButton ib_done_edit_list_name = holder.itemView.findViewById(R.id.ib_done_edit_list_name);
        ImageButton ib_delete_list = holder.itemView.findViewById(R.id.ib_delete_list);
        TextView tv_add_card = holder.itemView.findViewById(R.id.tv_add_card);
        CardView cv_add_card = holder.itemView.findViewById(R.id.cv_add_card);
        ImageButton ib_done_card_name = holder.itemView.findViewById(R.id.ib_done_card_name);
        TextView et_card_name = holder.itemView.findViewById(R.id.et_card_name);
        ImageButton ib_close_card_name = holder.itemView.findViewById(R.id.ib_close_card_name);
        RecyclerView rv_card_list = holder.itemView.findViewById(R.id.rv_card_list);

        if (holder instanceof MyViewHolder) {
            if (position == list.size()-1) {
                tv_add_task_list.setVisibility(View.VISIBLE);
                ll_task_item.setVisibility(View.GONE);
            } else {
                tv_add_task_list.setVisibility(View.GONE);
                ll_task_item.setVisibility(View.VISIBLE);
            }
            tv_task_list_title.setText(model.getName());

            tv_add_task_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_add_task_list.setVisibility(View.GONE);
                    cv_add_task_list_name.setVisibility(View.VISIBLE);
                }
            });

            ib_close_list_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_add_task_list.setVisibility(View.VISIBLE);
                    cv_add_task_list_name.setVisibility(View.GONE);
                }
            });

            ib_done_list_name.setOnClickListener(v -> {
                String listName = et_task_list_name.getText().toString();

                if (!listName.isEmpty()) {
                    if (context instanceof TaskListActivity) {
                        ((TaskListActivity) context).createNewTask(listName);
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show();
                }
            });

            ib_edit_list_name.setOnClickListener(v -> {
                et_edit_task_list_name.setText(model.getName());
                ll_title_view.setVisibility(View.GONE);
                cv_edit_task_list_name.setVisibility(View.VISIBLE);
            });

            ib_close_editable_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_title_view.setVisibility(View.VISIBLE);
                    cv_edit_task_list_name.setVisibility(View.GONE);
                }
            });

            ib_done_edit_list_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String listName = et_edit_task_list_name.getText().toString();

                    if (!listName.isEmpty()) {
                        if (context instanceof TaskListActivity) {
                            ((TaskListActivity) context).updateTaskName(model.getId(), listName);
                        }
                    } else {
                        Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ib_delete_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogForDeleteList(model.getId(), model.getName());
                }
            });

            tv_add_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_add_card.setVisibility(View.GONE);
                    cv_add_card.setVisibility(View.VISIBLE);
                    ib_close_card_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_add_card.setVisibility(View.VISIBLE);
                            cv_add_card.setVisibility(View.GONE);
                        }
                    });

                    ib_done_card_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int taskId=model.getId();
                            String cardName = et_card_name.getText().toString();

                            if (!cardName.isEmpty()) {
                                if (context instanceof TaskListActivity) {
                                    ((TaskListActivity) context).createNewCard(taskId, cardName);
                                }
                            } else {
                                Toast.makeText(context, "Please Enter Card Detail.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            if (context instanceof TaskListActivity) {
                ((TaskListActivity) context).fetchCardsFromDatabase(model.getId(), new CardFetchCallback() {
                    @Override
                    public void onCardsFetched(ArrayList<Card> cards) {
                        rv_card_list.setLayoutManager(new LinearLayoutManager(context));
                        rv_card_list.setHasFixedSize(true);

                        CardListItemsAdapter adapter = new CardListItemsAdapter(context, cards);
                        rv_card_list.setAdapter(adapter);




                        //drag and drop
                        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

                            @Override
                            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
                                int draggedPosition = dragged.getAdapterPosition();
                                int targetPosition = target.getAdapterPosition();

                                if (mPositionDraggedFrom == -1) {
                                    mPositionDraggedFrom = draggedPosition;
                                }
                                mPositionDraggedTo = targetPosition;

                                Collections.swap(cards, draggedPosition, targetPosition);
                                adapter.notifyItemMoved(draggedPosition, targetPosition);

                                return false; // true if moved, false otherwise
                            }

                            @Override
                            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                                // remove from adapter
                            }

                            @Override
                            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                                super.clearView(recyclerView, viewHolder);
                                if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 &&
                                        mPositionDraggedFrom != mPositionDraggedTo) {
                                    ((TaskListActivity) context).updateCardsInTaskList(model.getId(), cards);
                                }

                                // Reset the global variables
                                mPositionDraggedFrom = -1;
                                mPositionDraggedTo = -1;
                            }
                        });

                        helper.attachToRecyclerView(rv_card_list);
                    }


                    @Override
                    public void onError(String errorMessage) {
                        Log.e("fetchCards "+model.getId(), errorMessage);
                    }
                });


            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    private void alertDialogForDeleteList(int position, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setMessage("Are you sure you want to delete " + title + ".");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Yes", (dialogInterface, which) -> {


            if (context instanceof TaskListActivity) {
                ((TaskListActivity) context).deleteTaskById(position);
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("No", (dialogInterface, which) -> dialogInterface.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        MyViewHolder(View view) {
            super(view);
        }
    }
}
