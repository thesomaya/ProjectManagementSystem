package com.example.myjavaapplication.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myjavaapplication.R;
import com.example.myjavaapplication.adapters.BoardItemsAdapter;
import com.example.myjavaapplication.controllers.VolleyRequest;
import com.example.myjavaapplication.controllers.links;
import com.example.myjavaapplication.databinding.ActivityMainBinding;
import com.example.myjavaapplication.model.Board;
import com.example.myjavaapplication.model.User;
import com.example.myjavaapplication.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private androidx.appcompat.widget.Toolbar toolbar_main_activity;
    private FloatingActionButton fab_create_board;
    private String mUserName;
    private SharedPreferences mSharedPreferences;

    private RecyclerView rv_boards_list;
    private TextView tv_no_boards_available;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        toolbar_main_activity = findViewById(R.id.toolbar_main_activity);
        fab_create_board = findViewById(R.id.fab_create_board);

        rv_boards_list = findViewById(R.id.rv_boards_list);
        tv_no_boards_available = findViewById(R.id.tv_no_boards_available);

        fetchBoardsFromDatabase();

        setupActionBar();

        binding.navView.setNavigationItemSelectedListener(this);

        fab_create_board.setOnClickListener(view1 -> {
            Intent intent = new Intent(MainActivity.this, CreateBoardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            doubleBackToExit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_my_profile) {
            startActivityForResult(new Intent(MainActivity.this, MyProfileActivity.class), MY_PROFILE_REQUEST_CODE);
            return true;
        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return false;

    }


    private void setupActionBar() {
        setSupportActionBar(toolbar_main_activity);
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu);

        toolbar_main_activity.setNavigationOnClickListener(view -> toggleDrawer());
    }

    private void toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public void updateNavigationUserDetails(User user, boolean readBoardsList) {
        hideProgressDialog();

        mUserName = user.getName();
        View headerView = binding.navView.getHeaderView(0);
        ImageView navUserImage = headerView.findViewById(R.id.iv_user_image);
        Glide.with(this)
                .load(user.getImage())
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(navUserImage);

        TextView navUsername = headerView.findViewById(R.id.tv_username);
        navUsername.setText(user.getName());


    }


    private void fetchBoardsFromDatabase() {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int userId = sharedPref.getInt("USER_ID", -1);
        Log.e("Main Activity", String.valueOf(userId));

        VolleyRequest volleyRequest = new VolleyRequest(this);

        String url = links.LINK_READ_BOARD + "?current_user_id=" + userId;

        volleyRequest.getRequest(url, response -> {
            try {
                String status = response.getString("status");
                if (status.equals("success")) {
                    JSONArray dataArray = response.getJSONArray("data");
                    ArrayList<Board> boardsList = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject boardObject = dataArray.getJSONObject(i);
                        int boardId = boardObject.getInt("board_id");
                        String boardName = boardObject.getString("board_name");
                        String createdBy = boardObject.getString("created_by");
                        String image = boardObject.getString("image");
                        Board board = new Board(boardId,boardName, createdBy,image);
                        // Add the board to the list
                        boardsList.add(board);

                    }
                    populateBoardsListToUI(boardsList);

                } else {
                    Log.e("Main Activity", "errorUploadingBoards");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Main Activity", e.getMessage());
            }
        }, errorMessage -> Log.e("Main Activity", errorMessage));



    }


    public void populateBoardsListToUI(ArrayList<Board> boardsList) {
        hideProgressDialog();

        if (boardsList.size() > 0) {
            rv_boards_list.setVisibility(View.VISIBLE);
            tv_no_boards_available.setVisibility(View.GONE);

            rv_boards_list.setLayoutManager(new LinearLayoutManager(this));
            rv_boards_list.setHasFixedSize(true);

            BoardItemsAdapter adapter = new BoardItemsAdapter(this, boardsList);
            rv_boards_list.setAdapter(adapter);


        } else {
            rv_boards_list.setVisibility(View.GONE);
            tv_no_boards_available.setVisibility(View.VISIBLE);
        }
    }


    public static final int MY_PROFILE_REQUEST_CODE = 11;
    public static final int CREATE_BOARD_REQUEST_CODE = 12;
}