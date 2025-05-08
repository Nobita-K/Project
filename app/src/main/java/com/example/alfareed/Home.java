package com.example.alfareed;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.image_slider), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });

        // "View More" click to Menu screen
        TextView tvViewMore = findViewById(R.id.tvViewMore);
        tvViewMore.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Menu.class));
        });

        // Image Slider Code
        ImageSlider imageSlider = findViewById(R.id.image_slider);

        List<SlideModel> imagelist = new ArrayList<>();

        imagelist.add(new SlideModel(R.drawable.banner3, ScaleTypes.FIT));
        imagelist.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
        imagelist.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));

        imageSlider.setImageList(imagelist,ScaleTypes.FIT);

// bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(Home.this, Profile.class));
                return true;
            }

            else if (itemId == R.id.nav_menu) {
                startActivity(new Intent(Home.this, Menu.class));
                return true;
            }

            else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(Home.this, Cart.class));
                return true;
            }

            else if (itemId == R.id.nav_delivery) {
                startActivity(new Intent(Home.this, Order.class));
                return true;
            }

            // Optional: handle other navigation items
            else if (itemId == R.id.nav_home) {
                return true; // Already on home
            }

            return false;
        });

    }



}