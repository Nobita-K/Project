package com.example.alfareed;

public class MenuItem {
    private String id;
    private String name;
    private String price;
    private String description;
    private String imageId;
    private int quantity = 1; // Default quantity to 1

    public MenuItem() {}

    public MenuItem(String id, String name, String price, String description, String imageId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageId = imageId;
        this.quantity = 1;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImageId() { return imageId; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImageId(String imageId) { this.imageId = imageId; }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}