package com.example.stylishclothes.auth;

public class User {

    public String fullName, phone, email, profilePhotoPath;

    public User() {

    }

    public User(String fullName, String phone, String email) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

    public User(String fullName, String phone, String email, String profilePhotoPath) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.profilePhotoPath = profilePhotoPath;
    }

}
