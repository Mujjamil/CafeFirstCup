package com.example.cafefirstcup;

public class OrderItem {
    private String itemName;
    private double itemPrice;
    private int quantity;

    private int imgUrl;

    public OrderItem(String itemName, double itemPrice, int quantity, int imgUrl) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getImgUrl() {
        return imgUrl;
    }

}
