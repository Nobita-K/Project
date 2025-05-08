package com.example.alfareed;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdminMenu extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;

    private ImageButton backButton, uploadImageButton;
    private ImageView itemImagePreview;
    private EditText itemNameEditText, itemPriceEditText, shortDescriptionEditText;
    private CardView addItemButtonContainer;

    private DatabaseReference databaseReference;
    private ImageDatabaseHelper imageDatabaseHelper;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        databaseReference = FirebaseDatabase.getInstance().getReference("menu_items");
        imageDatabaseHelper = new ImageDatabaseHelper(this);

        backButton = findViewById(R.id.backButton);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        itemImagePreview = findViewById(R.id.itemImagePreview);
        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemPriceEditText = findViewById(R.id.itemPriceEditText);
        shortDescriptionEditText = findViewById(R.id.shortDescriptionEditText);
        addItemButtonContainer = findViewById(R.id.addItemButtonContainer);

        backButton.setOnClickListener(v -> finish());

        uploadImageButton.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                openGallery();
            } else {
                requestStoragePermission();
            }
        });

        addItemButtonContainer.setOnClickListener(v -> validateAndAddItem());
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                itemImagePreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void validateAndAddItem() {
        String itemName = itemNameEditText.getText().toString().trim();
        String itemPrice = itemPriceEditText.getText().toString().trim();
        String itemDescription = shortDescriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(itemName) || TextUtils.isEmpty(itemPrice) || TextUtils.isEmpty(itemDescription) || imageUri == null) {
            showValidationErrorDialog();
            return;
        }

        AlertDialog progressDialog = showProgressDialog("Adding Item", "Please wait while we add your item...");

        Bitmap bitmap = ((BitmapDrawable) itemImagePreview.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        String imageId = String.valueOf(System.currentTimeMillis());
        if (!imageDatabaseHelper.insertImage(imageId, imageBytes)) {
            progressDialog.dismiss();
            Toast.makeText(this, "Failed to save image in SQLite", Toast.LENGTH_SHORT).show();
            return;
        }

        saveItemToFirebase(itemName, itemPrice, itemDescription, imageId, progressDialog);
    }

    private void saveItemToFirebase(String itemName, String itemPrice, String itemDescription, String imageId, AlertDialog progressDialog) {
        String itemId = databaseReference.push().getKey();

        Map<String, Object> itemData = new HashMap<>();
        itemData.put("id", itemId);
        itemData.put("name", itemName);
        itemData.put("price", itemPrice);
        itemData.put("description", itemDescription);
        itemData.put("imageId", imageId);
        itemData.put("timestamp", System.currentTimeMillis());

        databaseReference.child(itemId).setValue(itemData).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(this, "Failed to add item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearForm() {
        itemNameEditText.setText("");
        itemPriceEditText.setText("");
        shortDescriptionEditText.setText("");
        itemImagePreview.setImageResource(android.R.drawable.ic_menu_gallery);
        imageUri = null;
    }

    private AlertDialog showProgressDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        return builder.show();
    }

    private void showValidationErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Validation Error");
        builder.setMessage("Please fill in all fields and select an image.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}