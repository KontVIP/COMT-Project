package com.example.stylishclothes.catalog;

import android.content.Intent;

public class Category {

    private String mTitle;
    private int mImage;
    private Intent mIntent;

    public Category(String title, int image, Intent intent) {
        mTitle = title;
        mImage = image;
        mIntent = intent;
    }


    public String getTitle() {
        return mTitle;
    }

    public int getImage() {
        return mImage;
    }

    public Intent getIntent() {
        return mIntent;
    }
}