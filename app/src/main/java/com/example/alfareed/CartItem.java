package com.example.alfareed;

public class CartItem {
    private String itemId;
    private String name;
    private String imageUrl; // or imageId if you use local DB
    private int quantity;
    private double price;
    private double totalPrice;

    public CartItem() {
        // Default constructor required for calls to DataSnapshot.getValue(CartItem.class)
    }

    public CartItem(String itemId, String name, String imageUrl, int quantity, double price) {
        this.itemId = itemId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = price * quantity;
    }

    // Getters and setters

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = price * quantity;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        this.price = price;
        this.totalPrice = price * quantity;
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}