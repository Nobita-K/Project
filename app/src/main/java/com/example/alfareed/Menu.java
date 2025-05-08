package com.example.alfareed;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {

    private RecyclerView itemsRecyclerView;
    private MenuScreenAdapter adapter;
    private List<MenuItem> menuItemList = new ArrayList<>();
    private List<MenuItem> filteredMenuItemList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(Menu.this, Home.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_menu) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(Menu.this, Profile.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(Menu.this, Cart.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_delivery) {
                startActivity(new Intent(Menu.this, Order.class));
                finish();
                return true;
            }
            return false;
        });

        // Hide BottomNavigationView when keyboard shows
        final View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) { // keyboard is opened
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

        // Setup RecyclerView
        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuScreenAdapter(this, filteredMenuItemList);
        itemsRecyclerView.setAdapter(adapter);

        // Setup Search
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMenuItems(s.toString());
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("menu_items");
        loadMenuItems();
    }

    private void loadMenuItems() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                menuItemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MenuItem menuItem = dataSnapshot.getValue(MenuItem.class);
                    if (menuItem != null) {
                        menuItemList.add(menuItem);
                    }
                }
                // Show all items by default
                filteredMenuItemList.clear();
                filteredMenuItemList.addAll(menuItemList);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Menu", "Failed to load menu items: " + error.getMessage());
            }
        });
    }

    private void filterMenuItems(String text) {
        filteredMenuItemList.clear();
        if (text.isEmpty()) {
            filteredMenuItemList.addAll(menuItemList);
        } else {
            String lowerText = text.toLowerCase();
            for (MenuItem item : menuItemList) {
                if (item.getName() != null && item.getName().toLowerCase().contains(lowerText)) {
                    filteredMenuItemList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}