package com.example.cafefirstcup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<OrderItem> orderItems;
    private TextView tvTotalPrice;
    private Context context;

    public OrderAdapter(List<OrderItem> orderItems, TextView tvTotalPrice, Context context) {
        this.orderItems = orderItems;
        this.tvTotalPrice = tvTotalPrice;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        holder.tvItemName.setText(item.getName());
        holder.tvItemPrice.setText("₹" + (item.getItemPrice() * item.getQuantity()));
        holder.ivThumbnail.setImageResource(item.getImgUrl());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));


        // Increase Quantity
        holder.btnIncrease.setOnClickListener(v -> {
            int quantity = item.getQuantity();
            quantity++;
            item.setQuantity(quantity);
            holder.tvQuantity.setText(String.valueOf(quantity));
            holder.tvItemPrice.setText("₹" + (item.getItemPrice() * quantity));

            // Update Total Price
            updateTotalPrice();
        });

        // Decrease Quantity
        holder.btnDecrease.setOnClickListener(v -> {
            int quantity = item.getQuantity();
            if (quantity > 1) {
                quantity--;
                item.setQuantity(quantity);
                holder.tvQuantity.setText(String.valueOf(quantity));
                holder.tvItemPrice.setText("₹" + (item.getItemPrice() * quantity));

                // Update Total Price
                updateTotalPrice();
            }
        });
        //Remove Item From Cart
        holder.btnRemove.setOnClickListener(v -> {
            int positionn = holder.getAdapterPosition();

            if (positionn != RecyclerView.NO_POSITION) {
                OrderItem itemm = orderItems.get(positionn);

                OrderManager.removeFromCart(itemm);  // Remove from cart list
                orderItems.remove(positionn);  // Remove from RecyclerView list

                notifyItemRemoved(positionn);
                notifyItemRangeChanged(positionn, orderItems.size());

                // ✅ Update Total Price
                tvTotalPrice.setText("Total: ₹" + OrderManager.getTotalPrice());

                // ✅ Ensure RecyclerView refreshes
                if (orderItems.isEmpty()) {
                    notifyDataSetChanged();  // Refresh RecyclerView when empty
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    private void updateTotalPrice() {
        if (tvTotalPrice != null) {
            tvTotalPrice.setText("Total: ₹" + OrderManager.getTotalPrice());
        } else {
            Log.e("OrderAdapter", "Error: Total Price TextView not found");
            Toast.makeText(context, "Error: Total Price TextView not found", Toast.LENGTH_SHORT).show();
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemPrice, tvQuantity;
        Button btnIncrease, btnDecrease;
        ImageView ivThumbnail , btnRemove;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            ivThumbnail = itemView.findViewById(R.id.iv_order_item);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btn_remove_order_item);

        }
    }
}
