package com.example.alfareed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Make sure the Google Play Services auth library is added to your build.gradle
// implementation 'com.google.android.gms:play-services-auth:20.7.0'
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    // Admin credentials
    private static final String ADMIN_EMAIL = "admin@ex.com";
    private static final String ADMIN_PASSWORD = "123";

    EditText emailInput, passwordInput;
    Button btnLogin;
    TextView goToSignUp;
    CardView googleSignInCard; // Renamed to match the XML ID
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    GoogleSignInClient googleSignInClient;

    ImageButton showPasswordButton;
    boolean isPasswordVisible = false;

    // Activity result launcher for Google Sign-In
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        // Google Sign-In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        // Google Sign-In failed
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Google sign in failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.logo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        emailInput = findViewById(R.id.et_email);
        passwordInput = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        goToSignUp = findViewById(R.id.account);

        // Password show/hide logic
        showPasswordButton = findViewById(R.id.btn_show_password);
        isPasswordVisible = false;

        showPasswordButton.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordButton.setImageResource(R.drawable.closedeyeicon); // closed eye
                isPasswordVisible = false;
            } else {
                passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordButton.setImageResource(R.drawable.openeyeicon); // open eye (use your own icon)
                isPasswordVisible = true;
            }
            passwordInput.setSelection(passwordInput.length());
        });

        // Update this to match the ID in your layout file
        googleSignInCard = findViewById(R.id.btn_google_signin);

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        // Configure Google Sign-In
        configureGoogleSignIn();

        // Login button logic
        btnLogin.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String pass = passwordInput.getText().toString().trim();

            if (email.isEmpty()) {
                emailInput.setError("Email is required");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Enter a valid email");
                return;
            }
            if (pass.isEmpty()) {
                passwordInput.setError("Password is required");
                return;
            }

            // Check if admin credentials were entered
            if (email.equals(ADMIN_EMAIL) && pass.equals(ADMIN_PASSWORD)) {
                // Admin login successful
                Toast.makeText(MainActivity.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, AdminPanel.class));
                finish();
                return;
            }

            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, Home.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Google Sign-In button click listener
        googleSignInCard.setOnClickListener(view -> signInWithGoogle());

        // TextView: Go to SignUp screen
        goToSignUp.setOnClickListener(this::goToSignUp);
    }

    private void configureGoogleSignIn() {
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("378221177394-ujl85oltegrbkhkblnf9kf07meo5h3d0.apps.googleusercontent.com") // Replace with your actual web client ID from Firebase
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        progressDialog.setMessage("Google Sign-In...");
        progressDialog.show();

        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success
                            Toast.makeText(MainActivity.this, "Google Sign-In Successful",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, Home.class));
                            finish();
                        } else {
                            // Sign in failed
                            Toast.makeText(MainActivity.this, "Authentication Failed: " +
                                            (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void goToSignUp(View view) {
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        startActivity(intent);
    }
}