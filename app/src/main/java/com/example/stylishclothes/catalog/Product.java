package com.example.stylishclothes.catalog;

public class Product {

    private String mTitle;
    private byte[] mImage;

    public Product(String title, byte[] image) {
        mTitle = title;
        mImage = image;
    }


    public String getTitle() {
        return mTitle;
    }

    public byte[] getImage() {
        return mImage;
    }
}
