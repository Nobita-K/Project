package com.example.alfareed;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminDelivery extends AppCompatActivity {

    private RecyclerView deliveryRecyclerView;
    private AdminDeliveryAdapter adapter;
    private List<OrderDetails> orderDetailsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_delivery);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.backButton), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminDelivery.this, AdminPanel.class));
            finish();
        });

        // Setup RecyclerView
        deliveryRecyclerView = findViewById(R.id.deliveryRecyclerView);
        deliveryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminDeliveryAdapter(orderDetailsList);
        deliveryRecyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        fetchOrdersFromFirebase();
    }

    private void fetchOrdersFromFirebase() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("user_orders");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                orderDetailsList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                        OrderDetails order = orderSnapshot.getValue(OrderDetails.class);
                        if (order != null) {
                            orderDetailsList.add(order);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}