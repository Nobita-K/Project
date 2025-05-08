package com.example.alfareed;

public class UserData {
    public String name, email, phone, password;

    public UserData() {} // Default for Firebase

    public UserData(String name, String email, String phone, String password) {
        this.name = name; this.email = email; this.phone = phone; this.password = password;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}