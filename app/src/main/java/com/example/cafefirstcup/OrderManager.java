package com.example.cafefirstcup;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private static List<OrderItem> cartItems = new ArrayList<>();

    public static void addToCart(OrderItem item) {
        cartItems.add(item);
    }

    public static List<OrderItem> getCartItems() {
        return new ArrayList<>(cartItems); // Return a copy to avoid modification issues
    }

    public static int getTotalPrice() {
        int total = 0;
        for (OrderItem item : cartItems) {
            total += item.getItemPrice() * item.getQuantity();
        }
        return total;

    }
    public static void removeFromCart(OrderItem item){
        cartItems.remove(item);
    }
    public static void clearCart() {
        cartItems.clear();
    }
}

