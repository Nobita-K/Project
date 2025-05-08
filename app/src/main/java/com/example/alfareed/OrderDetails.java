package com.example.alfareed;

public class OrderDetails {
    private String name;
    private String address;
    private String phone;
    private int total; // or String if you prefer
    // ... other fields like items, status, etc.

    public OrderDetails() {}

    public OrderDetails(String name, String address, String phone, int total) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.total = total;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}