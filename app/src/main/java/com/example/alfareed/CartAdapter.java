package com.example.alfareed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Assuming MenuItem has a quantity field
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context context;
    private List<MenuItem> cartItemList;
    private ImageDatabaseHelper imageDatabaseHelper;
    private String userId = "default_user"; // Replace with FirebaseAuth UID if available

    // Listener for cart changes
    private OnCartChangedListener cartChangedListener;

    public CartAdapter(Context context, List<MenuItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.imageDatabaseHelper = new ImageDatabaseHelper(context);
    }

    public CartAdapter(Context context, List<MenuItem> cartItemList, OnCartChangedListener listener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.imageDatabaseHelper = new ImageDatabaseHelper(context);
        this.cartChangedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem item = cartItemList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDescription.setText(item.getDescription());
        holder.tvPrice.setText("Rs " + item.getPrice());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity() > 0 ? item.getQuantity() : 1));

        // Defensive image loading from SQLite using imageId
        if (item.getImageId() != null && !item.getImageId().isEmpty()) {
            byte[] image = imageDatabaseHelper.getImage(item.getImageId());
            if (image != null && image.length > 0) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    holder.ivImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    holder.ivImage.setImageResource(R.drawable.placeholder_image);
                }
            } else {
                holder.ivImage.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            holder.ivImage.setImageResource(R.drawable.placeholder_image);
        }

        // Plus button
        holder.btnPlus.setOnClickListener(v -> {
            int qty = item.getQuantity() > 0 ? item.getQuantity() : 1;
            qty++;
            item.setQuantity(qty);
            updateItemInFirebase(item);
            holder.tvQuantity.setText(String.valueOf(qty));
            if (cartChangedListener != null) cartChangedListener.onCartChanged();
        });

        // Minus button
        holder.btnMinus.setOnClickListener(v -> {
            int qty = item.getQuantity() > 0 ? item.getQuantity() : 1;
            if (qty > 1) {
                qty--;
                item.setQuantity(qty);
                updateItemInFirebase(item);
                holder.tvQuantity.setText(String.valueOf(qty));
                if (cartChangedListener != null) cartChangedListener.onCartChanged();
            }
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            deleteItemFromFirebase(item);
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                cartItemList.remove(pos);
                notifyItemRemoved(pos);
                Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
                if (cartChangedListener != null) cartChangedListener.onCartChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    private void updateItemInFirebase(MenuItem item) {
        // Find and update the item in Firebase (you may want to use the item key instead of push)
        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("cart").child(userId);

        // For simplicity, this assumes name is unique. Prefer using a unique key in production.
        cartRef.orderByChild("name").equalTo(item.getName()).get()
                .addOnSuccessListener(snapshot -> {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        snap.getRef().setValue(item);
                    }
                });
    }

    private void deleteItemFromFirebase(MenuItem item) {
        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("cart").child(userId);

        cartRef.orderByChild("name").equalTo(item.getName()).get()
                .addOnSuccessListener(snapshot -> {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        snap.getRef().removeValue();
                    }
                });
    }

    // Add this interface in CartAdapter
    public interface OnCartChangedListener {
        void onCartChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivImage;
        TextView tvName, tvDescription, tvPrice, tvQuantity;
        CardView btnPlus, btnMinus;
        ImageButton btnDelete;
        LinearLayout quantityControl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.cart_item_image);
            tvName = itemView.findViewById(R.id.cart_item_name);
            tvDescription = itemView.findViewById(R.id.cart_item_description);
            tvPrice = itemView.findViewById(R.id.cart_item_price);
            tvQuantity = itemView.findViewById(R.id.txt_quantity);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnDelete = itemView.findViewById(R.id.delete_button);
            quantityControl = itemView.findViewById(R.id.quantity_control);
        }
    }
}