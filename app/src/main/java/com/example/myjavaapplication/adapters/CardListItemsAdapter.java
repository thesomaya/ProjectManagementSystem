package com.example.myjavaapplication.adapters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.activities.TaskListActivity;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.model.Card;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CardListItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Card> list;
    List<String> labels = new ArrayList<>();
    List<String> members = new ArrayList<>();

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

            if(model.getAssignedTo() != "null") {
                LinearLayout member_name_container = myViewHolder.itemView.findViewById(R.id.member_name_container);
                member_name_container.setVisibility(View.VISIBLE);
                TextView memberName = myViewHolder.itemView.findViewById(R.id.tv_member_name);
                memberName.setText(model.getAssignedTo());
            }

            if(!(model.getDue_date().isEmpty()) || !(model.getDue_time().isEmpty())) {
                LinearLayout due_date_container = myViewHolder.itemView.findViewById(R.id.due_date_container);
                due_date_container.setVisibility(View.VISIBLE);
                TextView dueDate = myViewHolder.itemView.findViewById(R.id.tv_due_date);
                dueDate.setText(model.getDue_date() + " " + model.getDue_time());
            }

            CardView card_card_view = myViewHolder.itemView.findViewById(R.id.card_card_view);
            setCardColor(model.getLabel(), model.getDue_date(), card_card_view);

            card_card_view.setOnClickListener(v -> {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                View cardDetailsView=LayoutInflater.from(context).inflate(R.layout.card_details_bottom_sheet,
                        null);
                bottomSheetDialog.setContentView(cardDetailsView);
                bottomSheetDialog.show();

                CardView card_card_view_details = cardDetailsView.findViewById(R.id.card_card_view_details);
                TextView et_due_date_details = cardDetailsView.findViewById(R.id.et_due_date_details);
                et_due_date_details.setOnClickListener(v1 -> {
                    showDatePicker(et_due_date_details);
                });

                TextView et_due_time_details = cardDetailsView.findViewById(R.id.et_due_time_details);
                et_due_time_details.setOnClickListener(v1 -> {
                    showTimePicker(et_due_time_details);
                });

                Spinner member_spinner = cardDetailsView.findViewById(R.id.member_spinner);
                addMembersSpinner(member_spinner);

                Spinner label_spinner = cardDetailsView.findViewById(R.id.label_spinner);
                addLabelsSpinner(label_spinner);

                TextView tv_card_name = cardDetailsView.findViewById(R.id.tv_card_name);
                TextView et_card_name_details = cardDetailsView.findViewById(R.id.et_card_name_details);
                fetchCardsFromDatabase(model.getId(), tv_card_name, et_card_name_details, member_spinner, label_spinner,
                        et_due_date_details, et_due_time_details, card_card_view_details);



                Button btn_update_card =cardDetailsView.findViewById(R.id.btn_update_card);
                btn_update_card.setOnClickListener(v1 -> {
                    String member = member_spinner.getSelectedItem().toString();
                    int memberId=0;
                    if (member != "none") {
                        String[] arrOfStr = member.split(" ", 2);
                         memberId = Integer.parseInt(arrOfStr[0]);
                    }
                    updateCardDetails(model.getId(), et_card_name_details.getText().toString(), memberId,
                            et_due_date_details.getText().toString(), et_due_time_details.getText().toString(),
                            label_spinner.getSelectedItem().toString());
                });

                cardDetailsView.findViewById(R.id.iv_delete_card).setOnClickListener(v2 -> {
                    alertDialogForDeleteList(model.getId(), model.getName()+" Card");
                });
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
    private void showDatePicker(TextView et_due_date_details) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        et_due_date_details.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void showTimePicker(TextView et_due_time_details) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        et_due_time_details.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void addMembersSpinner(Spinner member_spinner) {
        SharedPreferences sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int boardIdSh = sharedPref.getInt("BOARD_ID", -1);

        VolleyRequest volleyRequest = new VolleyRequest(context);

        String url = links.LINK_READ_MEMBER + "?board_id=" + boardIdSh;

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                if (status.equals("success")) {
                    members.add("none");
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject userNameObject = dataArray.getJSONObject(i);
                        String user_name = userNameObject.getString("user_name");
                        int user_id = userNameObject.getInt("user_id");
                        members.add(user_id+" "+user_name);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                            members);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    member_spinner.setAdapter(adapter);
                } else {
                    Log.e("addMembersSpinner", "Server returned failure status: " + status);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("addMembersSpinner", "JSON parsing error: " + e.getMessage());
            }
        }, errorMessage -> {
            Log.e("addMembersSpinner", "Volley request error: " + errorMessage);
        });
    }

    private void addLabelsSpinner(Spinner member_spinner) {
        labels.add("not-started");
        labels.add("in-progress");
        labels.add("done");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        member_spinner.setAdapter(adapter);

    }

    private void fetchCardsFromDatabase(int card_id, TextView tv_card_name, TextView tv_card_name_details, Spinner member_spinner,
                                        Spinner label_spinner,
                                        TextView et_due_date_details, TextView et_due_time_details,
                                        CardView card_card_view_details) {
        Log.e("cardNameId", String.valueOf(card_id));
        // Instantiate VolleyRequest
        VolleyRequest volleyRequest = new VolleyRequest(context);

        // Define URL for GET request
        String url = links.LINK_READ_CARD_BY_ID + "?card_id=" + card_id;


        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                if (status.equals("success")) {
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject cardObject = dataArray.getJSONObject(i);
                        int cardId = cardObject.getInt("card_id");
                        int cardOrder = cardObject.getInt("card_order");
                        int member_id = cardObject.getInt("member_id");
                        String cardName = cardObject.getString("name");
                        int createdBy = cardObject.getInt("created_by");
                        String assigned_to = cardObject.getString("user_name");
                        String due_date = cardObject.getString("due_date");
                        String due_time = cardObject.getString("due_time");
                        String label = cardObject.getString("label");

                        tv_card_name.setText(cardName);
                        tv_card_name_details.setText(cardName);
                        et_due_date_details.setText(due_date);
                        et_due_time_details.setText(due_time);

                        for (i=0 ; i<labels.size() ; i++){
                            if(label.equals(labels.get(i))){
                                label_spinner.setSelection(i);
                            }
                        }

                        setCardColor(label, due_date, card_card_view_details);

                        for (i=0 ; i<members.size() ; i++){
                            String member = members.get(i);
                            if (member != "none") {
                                String[] arrOfStr = member.split(" ", 2);
                                int memberId = Integer.parseInt(arrOfStr[0]);
                                if (member_id == memberId) {
                                    member_spinner.setSelection(i);
                                }
                            }
                        }

                    }

                } else {
                    Log.e("CardActivity", status.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("CardActivity", e.getMessage());
            }
        }, errorMessage -> Log.e("errorUploadingCards", errorMessage));



    }

    private void setCardColor(String label, String due_date, CardView cardView) {
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
        String date_time = due_date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        if (due_date.isEmpty()){
            setCardColorBasedOnLabel(label, cardView);
        }
        try {
            // Parsing the strings into Date objects
            Date dueDateTime = dateFormat.parse(date_time);
            Date currentTime = dateFormat.parse(timeStamp);

            // Comparing the dates
            if (dueDateTime.before(currentTime)) {
                cardView.setCardBackgroundColor(Color.rgb(254, 179, 174));
            } else {
                setCardColorBasedOnLabel(label, cardView);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private static void setCardColorBasedOnLabel(String label, CardView cardView) {
        if(label.equals("in-progress")){
            cardView.setCardBackgroundColor(Color.rgb(211, 249, 250));
        }else if(label.equals("done")){
            cardView.setCardBackgroundColor(Color.rgb(226, 249, 197));
        }else{
            cardView.setCardBackgroundColor(Color.WHITE);
        }
    }

    public void updateCardDetails(int cardId, String name, int member_id, String due_date, String due_time, String label) {
        VolleyRequest volleyRequest = new VolleyRequest(context);
        String url = links.LINK_UPDATE_CARD_DETAILS + "?card_id="+cardId + "&name=" + name + "&member_id=" + member_id
                + "&due_date=" + due_date + "&due_time=" + due_time + "&label=" + label;

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                String message = response.getString("message");
                if (status.equals("success")) {
                    Log.i("updateCardDetails", message);
                    context.startActivity(new Intent(context, TaskListActivity.class));
                    if (context instanceof TaskListActivity) {
                        ((TaskListActivity) context).finish();
                    }

                } else {
                    Log.e("updateCardDetails", message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("updateCardDetails", "Error: " + e.getMessage());
            }
        }, errorMessage -> {
            Log.e("updateCardDetails", "Error: " + errorMessage);
        });
    }
    public void deleteCardById(int cardId) {
        VolleyRequest volleyRequest = new VolleyRequest(context);
        String url = links.LINK_DELETE_CARD + "?card_id=" + cardId;

        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                String message = response.getString("message");
                if (status.equals("success")) {
                    Toast.makeText(context,"Task Deleted Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(context, TaskListActivity.class);
                    context.startActivity(intent);
                    if (context instanceof TaskListActivity) {
                        ((TaskListActivity) context).finish();
                    }
                } else {
                    Log.e("DeleteCard", message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("DeleteCard", "Error: " + e.getMessage());
            }
        }, errorMessage -> {
            Log.e("DeleteCard", "Error: " + errorMessage);
        });
    }

    private void alertDialogForDeleteList(int cardId, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setMessage("Are you sure you want to delete " + title + ".");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Yes", (dialogInterface, which) -> {
            deleteCardById(cardId);
        });

        builder.setNegativeButton("No", (dialogInterface, which) -> dialogInterface.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}
