package com.example.alfareed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminDeliveryAdapter extends RecyclerView.Adapter<AdminDeliveryAdapter.ViewHolder> {

    private List<OrderDetails> orderList;

    public AdminDeliveryAdapter(List<OrderDetails> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_delivery, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetails order = orderList.get(position);
        holder.name.setText(order.getName());
        holder.address.setText(order.getAddress());
        holder.phone.setText(order.getPhone());
        holder.total.setText("Total: Rs " + order.getTotal());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, phone, total;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.orderUserName);
            address = itemView.findViewById(R.id.orderUserAddress);
            phone = itemView.findViewById(R.id.orderUserPhone);
            total = itemView.findViewById(R.id.orderTotalPrice);
        }
    }
}