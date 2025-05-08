package com.example.alfareed;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

public class Order extends AppCompatActivity {

    private EditText nameEditText, addressEditText, phoneEditText;
    private MaterialButton placeOrderButton;
    private DatabaseReference ordersReference;

    // Order data from Cart screen
    private ArrayList<HashMap<String, Object>> orderItems;
    private int subTotal = 0;
    private int deliveryCharge = 0;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views
        nameEditText = findViewById(R.id.nameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        // Reference to Firebase orders node (use userId if available)
        String userId = "default_user";
        ordersReference = FirebaseDatabase.getInstance().getReference("user_orders").child(userId);

        // --- Get order data from Intent ---
        orderItems = (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra("orderItems");
        subTotal = getIntent().getIntExtra("subTotal", 0);
        deliveryCharge = getIntent().getIntExtra("deliveryCharge", 0);
        total = getIntent().getIntExtra("total", 0);

        placeOrderButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            // Validate fields
            if (TextUtils.isEmpty(name)) {
                nameEditText.setError("Name is required");
                nameEditText.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(address)) {
                addressEditText.setError("Address is required");
                addressEditText.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(phone)) {
                phoneEditText.setError("Phone is required");
                phoneEditText.requestFocus();
                return;
            }

            // Prepare order details
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("name", name);
            orderDetails.put("address", address);
            orderDetails.put("phone", phone);
            orderDetails.put("timestamp", System.currentTimeMillis());
            orderDetails.put("items", orderItems);
            orderDetails.put("subTotal", subTotal);
            orderDetails.put("deliveryCharge", deliveryCharge);
            orderDetails.put("total", total);

            // Push to Firebase
            ordersReference.push().setValue(orderDetails)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Order.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                        // Optionally clear fields
                        nameEditText.setText("");
                        addressEditText.setText("");
                        phoneEditText.setText("");
                        // Optionally clear the cart in Firebase
                        FirebaseDatabase.getInstance().getReference("cart").child(userId).removeValue();
                        // Optionally finish this activity or go elsewhere
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Order.this, "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_delivery);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(Order.this, Profile.class));
                return true;
            } else if (itemId == R.id.nav_menu) {
                startActivity(new Intent(Order.this, Menu.class));
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(Order.this, Cart.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(Order.this, Home.class));
                return true;
            } else if (itemId == R.id.nav_delivery) {
                return true; // Already on Order screen
            }
            return false;
        });
    }
}