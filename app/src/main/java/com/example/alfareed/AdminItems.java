package com.example.alfareed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminItems extends AppCompatActivity {

    private RecyclerView itemsRecyclerView;
    private MenuItemsAdapter adapter;
    private List<MenuItem> menuItemList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_items);

        Log.d("AdminItems", "onCreate called");

        // Back Button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AdminItems.this, AdminPanel.class);
            startActivity(intent);
            finish(); // optional: closes current activity
        });

        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuItemsAdapter(this, menuItemList);
        itemsRecyclerView.setAdapter(adapter);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("menu_items");

        // Load data from Firebase
        loadMenuItems();
    }

    private void loadMenuItems() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() { // Single value event
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menuItemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MenuItem menuItem = dataSnapshot.getValue(MenuItem.class);
                    if (menuItem != null) { // Ensure the menuItem is not null
                        menuItemList.add(menuItem);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminItems", "Failed to load menu items: " + error.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("AdminItems", "onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("AdminItems", "onPause called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("AdminItems", "onDestroy called");
    }
}