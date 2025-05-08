package com.example.alfareed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    private EditText etUsername, etEmail, etPhone, etPassword;
    private Button btnCreateAccount;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private ImageButton showPasswordButton;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.logo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone_no);
        etPassword = findViewById(R.id.et_password);
        btnCreateAccount = findViewById(R.id.btn_login);
        showPasswordButton = findViewById(R.id.btn_show_password);
        progressDialog = new ProgressDialog(this);

        // Show/Hide Password Logic
        showPasswordButton.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordButton.setImageResource(R.drawable.closedeyeicon); // closed eye
                isPasswordVisible = false;
            } else {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordButton.setImageResource(R.drawable.openeyeicon); // open eye
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.length()); // Keep cursor at end
        });

        // Handle account creation
        btnCreateAccount.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter a valid email");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                return;
            }
            if (password.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                return;
            }

            progressDialog.setMessage("Creating account...");
            progressDialog.show();

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, "Account created!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this, MainActivity.class)); // Replace with your actual Home activity
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, "Sign Up Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    // This method is linked in the XML for "Already Have an Account?"
    public void goToLogin(View view) {
        startActivity(new Intent(SignUp.this, MainActivity.class)); // Make sure MainActivity is your login screen
        finish();
    }
}