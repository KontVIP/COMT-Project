package com.example.stylishclothes.catalog;

import android.content.Intent;

public class Category {

    public String title;
    public String imagePath = "";

    public Category() {

    }

    public Category(String title, String imagePath) {
        this.title = title;
        this.imagePath = imagePath;
    }

}