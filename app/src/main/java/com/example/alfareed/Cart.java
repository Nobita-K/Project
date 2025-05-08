package com.example.alfareed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Cart extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<MenuItem> cartItemList = new ArrayList<>();
    private DatabaseReference cartReference;
    private TextView subTotalValue, deliveryChargeValue, totalValue;
    private static final int DELIVERY_CHARGE = 200;
    private DatabaseReference ordersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        subTotalValue = findViewById(R.id.subTotalValue);
        deliveryChargeValue = findViewById(R.id.deliveryChargeValue);
        totalValue = findViewById(R.id.totalValue);

        deliveryChargeValue.setText("Rs " + DELIVERY_CHARGE);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItemList, this::recalculateBill);
        cartRecyclerView.setAdapter(cartAdapter);

        String userId = "default_user";
        cartReference = FirebaseDatabase.getInstance().getReference("cart").child(userId);
        ordersReference = FirebaseDatabase.getInstance().getReference("orders").child(userId);

        loadCartItems();

        androidx.cardview.widget.CardView proceedButton = findViewById(R.id.proceedButtonCard);
        proceedButton.setOnClickListener(v -> {
            if (cartItemList.isEmpty()) {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            passOrderToOrderScreen();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(Cart.this, Profile.class));
                return true;
            } else if (itemId == R.id.nav_menu) {
                startActivity(new Intent(Cart.this, Menu.class));
                return true;
            } else if (itemId == R.id.nav_delivery) {
                startActivity(new Intent(Cart.this, Order.class));
                return true;
            } else if (itemId == R.id.nav_cart) {
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(Cart.this, Home.class));
                return true;
            }
            return false;
        });
    }

    private void loadCartItems() {
        cartReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                cartItemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MenuItem item = dataSnapshot.getValue(MenuItem.class);
                    if (item != null) {
                        cartItemList.add(item);
                    }
                }
                cartAdapter.notifyDataSetChanged();
                recalculateBill();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Cart", "Failed to load cart items: " + error.getMessage());
            }
        });
    }

    // Modified: Instead of saving directly, pass order data to Order screen
    private void passOrderToOrderScreen() {
        int subTotal = 0;
        ArrayList<HashMap<String, Object>> orderItems = new ArrayList<>();
        for (MenuItem item : cartItemList) {
            int price = 0;
            try {
                price = Integer.parseInt(item.getPrice().replaceAll("[^\\d]", ""));
            } catch (Exception e) {}
            int qty = item.getQuantity() > 0 ? item.getQuantity() : 1;
            subTotal += price * qty;

            HashMap<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getId());
            itemMap.put("name", item.getName());
            itemMap.put("price", price);
            itemMap.put("quantity", qty);
            itemMap.put("description", item.getDescription());
            itemMap.put("imageId", item.getImageId());
            orderItems.add(itemMap);
        }
        int total = subTotal + DELIVERY_CHARGE;

        Intent intent = new Intent(Cart.this, Order.class);
        intent.putExtra("orderItems", orderItems); // Serializable
        intent.putExtra("subTotal", subTotal);
        intent.putExtra("deliveryCharge", DELIVERY_CHARGE);
        intent.putExtra("total", total);
        startActivity(intent);
    }

    public void recalculateBill() {
        int subTotal = 0;
        for (MenuItem item : cartItemList) {
            int price = 0;
            try {
                price = Integer.parseInt(item.getPrice().replaceAll("[^\\d]", ""));
            } catch (Exception e) {}
            int qty = item.getQuantity() > 0 ? item.getQuantity() : 1;
            subTotal += price * qty;
        }
        subTotalValue.setText("Rs " + subTotal);
        int total = subTotal + DELIVERY_CHARGE;
        totalValue.setText("Rs " + total);
    }



}
