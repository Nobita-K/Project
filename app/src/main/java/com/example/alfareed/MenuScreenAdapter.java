package com.example.alfareed;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MenuScreenAdapter extends RecyclerView.Adapter<MenuScreenAdapter.ViewHolder> {
    private Context context;
    private List<MenuItem> menuItemList;
    private ImageDatabaseHelper imageDatabaseHelper;

    public MenuScreenAdapter(Context context, List<MenuItem> menuItemList) {
        this.context = context;
        this.menuItemList = menuItemList;
        this.imageDatabaseHelper = new ImageDatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem item = menuItemList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("Rs " + item.getPrice());
        holder.tvDescription.setText(item.getDescription());

        // Fetch image from SQLite using imageId
        if (item.getImageId() != null && !item.getImageId().isEmpty()) {
            Bitmap bitmap = imageDatabaseHelper.getImageById(item.getImageId());
            if (bitmap != null) {
                holder.ivMenuImage.setImageBitmap(bitmap);
            } else {
                holder.ivMenuImage.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            holder.ivMenuImage.setImageResource(R.drawable.placeholder_image);
        }

        // Add to Cart button logic
        holder.btnAddToCart.setOnClickListener(v -> {
            // Here, you can use FirebaseAuth.getInstance().getCurrentUser().getUid() for user-specific cart
            String userId = "default_user"; // Replace with actual user ID if using auth
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId);

            // Optionally, you can use push() for unique cart item IDs
            cartRef.push().setValue(item)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return menuItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvDescription;
        ImageView ivMenuImage;
        Button btnAddToCart;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMenuItemName);
            tvPrice = itemView.findViewById(R.id.tvMenuItemPrice);
            tvDescription = itemView.findViewById(R.id.tvMenuItemDescription);
            ivMenuImage = itemView.findViewById(R.id.ivMenuItemImage);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}