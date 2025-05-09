package com.example.alfareed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MenuItemsAdapter extends RecyclerView.Adapter<MenuItemsAdapter.MenuItemViewHolder> {

    private final Context context;
    private final List<MenuItem> menuItemList;
    private final ImageDatabaseHelper imageDatabaseHelper;

    public MenuItemsAdapter(Context context, List<MenuItem> menuItemList) {
        this.context = context;
        this.menuItemList = menuItemList;
        this.imageDatabaseHelper = new ImageDatabaseHelper(context);
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_row, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItem menuItem = menuItemList.get(position);

        holder.itemNameTextView.setText(menuItem.getName());
        holder.itemPriceTextView.setText("Rs " + menuItem.getPrice());
        holder.itemDescriptionTextView.setText(menuItem.getDescription());

        // Defensive image loading from SQLite using imageId
        byte[] image = imageDatabaseHelper.getImage(menuItem.getImageId());
        if (image != null && image.length > 0) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                holder.itemImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.itemImageView.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            holder.itemImageView.setImageResource(R.drawable.placeholder_image);
        }

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteMenuItem(menuItem, position);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return menuItemList != null ? menuItemList.size() : 0;
    }

    private void deleteMenuItem(MenuItem menuItem, int position) {
        String itemId = menuItem.getId();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("menu_items");
        databaseReference.child(itemId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    menuItemList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, menuItemList.size());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        final ImageView itemImageView;
        final TextView itemNameTextView, itemPriceTextView, itemDescriptionTextView;
        final ImageButton deleteButton;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemPriceTextView = itemView.findViewById(R.id.itemPriceTextView);
            itemDescriptionTextView = itemView.findViewById(R.id.itemDescriptionTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}