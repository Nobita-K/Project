package com.example.alfareed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminPanel extends AppCompatActivity {

    private CardView logOutButton;
    private CardView allItemMenuButton;
    private CardView addMenuButton;
    private CardView outForDeliveryButton;
    private CardView adminFeedbackButton;
    private CardView adminUserButton;

    // UI for stats
    private TextView tvOrderCount, tvTotalEarnings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_panel);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        // Find stats TextViews
        tvOrderCount = findViewById(R.id.tvOrderCount);
        tvTotalEarnings = findViewById(R.id.tvTotalEarnings);

        fetchOrderStats();

        setupClickListeners();
    }

    private void fetchOrderStats() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("user_orders");

        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int orderCount = 0;
                int totalEarnings = 0;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                        orderCount++;
                        Object totalObj = orderSnapshot.child("total").getValue();
                        if (totalObj != null) {
                            try {
                                int orderTotal = Integer.parseInt(totalObj.toString());
                                totalEarnings += orderTotal;
                            } catch (NumberFormatException e) {
                                // skip bad value
                            }
                        }
                    }
                }

                tvOrderCount.setText(String.valueOf(orderCount));
                tvTotalEarnings.setText("Rs " + totalEarnings);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                tvOrderCount.setText("?");
                tvTotalEarnings.setText("?");
            }
        });
    }

    private void initViews() {
        logOutButton = findViewById(R.id.logOutButton);
        allItemMenuButton = findViewById(R.id.allItemMenuButton);
        addMenuButton = findViewById(R.id.addMenuButton);
        outForDeliveryButton = findViewById(R.id.outForDeliveryButton);
        adminFeedbackButton = findViewById(R.id.feedbackButton);
        adminUserButton = findViewById(R.id.createNewUserButton);
    }

    private void setupClickListeners() {
        logOutButton.setOnClickListener(v -> performLogout());
        allItemMenuButton.setOnClickListener(v -> navigateToAdminItems());
        addMenuButton.setOnClickListener(v -> navigateToAddMenu());
        outForDeliveryButton.setOnClickListener(v -> navigateToAdminDelivery());
        adminFeedbackButton.setOnClickListener(v -> navigateToAdminFeedback());
        adminUserButton.setOnClickListener(v -> navigateToAdminUser());
    }

    private void navigateToAdminMenu() {
        Intent intent = new Intent(AdminPanel.this, AdminMenu.class);
        startActivity(intent);
    }
    private void navigateToAdminItems() {
        Intent intent = new Intent(AdminPanel.this, AdminItems.class);
        startActivity(intent);
    }
    private void navigateToAddMenu() {
        Intent intent = new Intent(AdminPanel.this, AdminMenu.class);
        intent.putExtra("action", "add_menu");
        startActivity(intent);
    }
    private void navigateToAdminDelivery() {
        Intent intent = new Intent(AdminPanel.this, AdminDelivery.class);
        startActivity(intent);
    }
    private void navigateToAdminFeedback() {
        Intent intent = new Intent(AdminPanel.this, AdminFeedback.class);
        startActivity(intent);
    }
    private void navigateToAdminUser() {
        Intent intent = new Intent(AdminPanel.this, AdminUser.class);
        startActivity(intent);
    }
    private void performLogout() {
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(AdminPanel.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminPanel.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}