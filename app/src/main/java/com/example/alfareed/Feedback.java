package com.example.alfareed;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Feedback extends AppCompatActivity {

    private EditText feedbackEditText;
    private Button sendButton;
    private String userName = "";
    private TextView feedbackDisplayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feedback);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backArrow = findViewById(R.id.imageBack);
        backArrow.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        feedbackEditText = findViewById(R.id.editTextFeedback);
        sendButton = findViewById(R.id.btn_Send);
        feedbackDisplayTextView = findViewById(R.id.textViewFeedbackDisplay);

        // Get Name from Intent
        userName = getIntent().getStringExtra("userName");
        if (userName == null) userName = "";

        sendButton.setOnClickListener(v -> {
            String feedbackText = feedbackEditText.getText().toString().trim();
            if (feedbackText.isEmpty()) {
                feedbackDisplayTextView.setText("Please enter feedback.");
                feedbackDisplayTextView.setVisibility(TextView.VISIBLE);
                Toast.makeText(this, "Please enter feedback", Toast.LENGTH_SHORT).show();
                return;
            }
            saveFeedbackToFirebase(userName, feedbackText);
        });
    }

    private void saveFeedbackToFirebase(String name, String feedback) {
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedback");
        String feedbackId = feedbackRef.push().getKey();
        if (feedbackId != null) {
            FeedbackData feedbackData = new FeedbackData(name, feedback);
            feedbackRef.child(feedbackId).setValue(feedbackData)
                    .addOnSuccessListener(aVoid -> {
                        feedbackDisplayTextView.setText("Feedback sent!");
                        feedbackDisplayTextView.setVisibility(TextView.VISIBLE);
                        feedbackEditText.setText("");
                        Toast.makeText(this, "Feedback sent!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        feedbackDisplayTextView.setText("Failed to send feedback.");
                        feedbackDisplayTextView.setVisibility(TextView.VISIBLE);
                        Toast.makeText(this, "Failed to send feedback", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}