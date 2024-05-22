package com.example.myjavaapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myjavaapplication.R;
import com.example.myjavaapplication.adapters.CardListItemsAdapter;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.databinding.ActivityCardListBinding;
import com.example.myjavaapplication.model.Card;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CardListActivity extends AppCompatActivity {

    private ActivityCardListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupActionBar();
        fetchCardsFromDatabase();

        binding.fabCreateCard.setOnClickListener(v -> {
            Intent intent=new Intent(this, CreateCardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupActionBar() {
        setSupportActionBar(binding.toolbarCardListActivity);
        if (getSupportActionBar() != null) {
            SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            String boardName = sharedPref.getString("BOARD_NAME", null);
            String taskName = sharedPref.getString("TASK_NAME", null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            getSupportActionBar().setTitle(boardName+", "+taskName);
        }

        binding.toolbarCardListActivity.setNavigationOnClickListener(v -> onBackPressed());
    }
    private void fetchCardsFromDatabase() {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int taskIdSh = sharedPref.getInt("TASK_ID", -1);

        // Instantiate VolleyRequest
        VolleyRequest volleyRequest = new VolleyRequest(this);

        // Define URL for GET request
        String url = links.LINK_READ_CARD + "?task_id=" + taskIdSh;


        // Make the GET request
        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                if (status.equals("success")) {
                    JSONArray dataArray = response.getJSONArray("data");
                    ArrayList<Card> cardsList = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject cardObject = dataArray.getJSONObject(i);
                        int cardId = cardObject.getInt("card_id");
                        int cardOrder = cardObject.getInt("card_order");
                        String taskName = cardObject.getString("name");
                        int createdBy = cardObject.getInt("created_by");
                        String assigned_to=cardObject.getString("user_name");
                        String due_date = cardObject.getString("due_date");
                        String due_time = cardObject.getString("due_time");
                        String label = cardObject.getString("label");
                        Card card = new Card(cardId, cardOrder, taskName, String.valueOf(createdBy),due_date, due_time,assigned_to,label);
                        cardsList.add(card);

                    }

                    populateTasksListToUI(cardsList);

                } else {
                    Log.e("CardActivity", status.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("CardActivity", e.getMessage());
            }
        }, errorMessage -> Log.e("errorUploadingCards", errorMessage));



    }

    private void populateTasksListToUI(ArrayList<Card> cardsList) {
        if (cardsList.size() > 0) {
            binding.tvNoCardsAvailable.setVisibility(View.GONE);
            binding.rvCardList.setVisibility(View.VISIBLE);
            binding.rvCardList.setLayoutManager(new LinearLayoutManager(this));
            CardListItemsAdapter adapter = new CardListItemsAdapter(this, cardsList);
            binding.rvCardList.setAdapter(adapter);
        } else {
            binding.rvCardList.setVisibility(View.GONE);
            binding.tvNoCardsAvailable.setVisibility(View.VISIBLE);
        }
    }
}