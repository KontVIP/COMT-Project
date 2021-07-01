package com.example.stylishclothes.auth;

public class User {

    public String fullName, age, email, profilePhotoPath;

    public User() {

    }

    public User(String fullName, String age, String email) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
    }

    public User(String fullName, String age, String email, String profilePhotoPath) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.profilePhotoPath = profilePhotoPath;
    }

}
