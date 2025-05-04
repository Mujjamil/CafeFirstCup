package com.example.cafefirstcup;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private Context context;
    private List<Meal> mealList;

    public MealAdapter(Context context, List<Meal> mealList) {
        this.context = context;
        this.mealList = mealList;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.tvMealName.setText(meal.getName());
        holder.tvMealPrice.setText("₹" + meal.getPrice());
        holder.ivMeal.setImageResource(meal.getImageResId());

        // Click listener for the "+" button
        holder.btnAddToCart.setOnClickListener(v -> {
            addMealToCart(meal);
            Toast.makeText(context, meal.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        });
    }

    private void addMealToCart(Meal meal) {
        OrderManager.addToCart(new OrderItem(meal.getName(), Double.parseDouble(meal.getPrice()), 1, meal.getImageResId()));
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvMealName, tvMealPrice;
        ImageView ivMeal, btnAddToCart;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMealName = itemView.findViewById(R.id.tv_meal_name);
            tvMealPrice = itemView.findViewById(R.id.tv_meal_price);
            ivMeal = itemView.findViewById(R.id.iv_meal);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }
}
