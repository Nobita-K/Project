package com.example.alfareed;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {

    Button btnfeedback, btnLogout, btnSaveInfo;
    EditText nameEditText, emailEditText, phoneEditText, passwordEditText;
    ImageButton showPasswordButton;
    boolean isPasswordVisible = false;
    private String lastSavedName = ""; // <-- to keep last saved name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views
        nameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.useremail);
        phoneEditText = findViewById(R.id.userphone);
        passwordEditText = findViewById(R.id.userpassword);
        showPasswordButton = findViewById(R.id.show_password_button);
        btnfeedback = findViewById(R.id.feedback);
        btnLogout = findViewById(R.id.logout);
        btnSaveInfo = findViewById(R.id.save_info);

        // Password show/hide logic
        showPasswordButton.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordButton.setImageResource(R.drawable.hideeyeicon);
                isPasswordVisible = false;
            } else {
                passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordButton.setImageResource(R.drawable.showpasswordicon);
                isPasswordVisible = true;
            }
            passwordEditText.setSelection(passwordEditText.length());
        });

        // Save Information button
        btnSaveInfo.setOnClickListener(view -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(Profile.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to Firebase under "users"
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
            String userId = userRef.push().getKey();
            if (userId != null) {
                UserData user = new UserData(name, email, phone, password);
                userRef.child(userId).setValue(user)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Profile.this, "Information saved!", Toast.LENGTH_SHORT).show();
                            lastSavedName = name; // <-- Save last name
                            nameEditText.setText("");
                            emailEditText.setText("");
                            phoneEditText.setText("");
                            passwordEditText.setText("");
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(Profile.this, "Failed to save info", Toast.LENGTH_SHORT).show()
                        );
            }
        });

        // Feedback button
        btnfeedback.setOnClickListener(view -> {
            String nameToSend = !lastSavedName.isEmpty() ? lastSavedName : nameEditText.getText().toString().trim();
            Intent intent = new Intent(Profile.this, Feedback.class);
            intent.putExtra("userName", nameToSend);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(view -> {
            Toast.makeText(Profile.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Profile.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(Profile.this, Home.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_menu) {
                startActivity(new Intent(Profile.this, Menu.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_delivery) {
                startActivity(new Intent(Profile.this, Order.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(Profile.this, Cart.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    // User data class for Firebase
    public static class UserData {
        public String name, email, phone, password;
        public UserData() {} // Default for Firebase
        public UserData(String name, String email, String phone, String password) {
            this.name = name; this.email = email; this.phone = phone; this.password = password;
        }
    }
}