package com.example.stylishclothes.catalog;

import android.net.Uri;

public class Product {

    private String id, title, titleDescription, description, price, productCode, titleImagePath, firstImagePath, secondImagePath, thirdImagePath, fourthImagePath, fifthImagePath;
    private boolean size_S, size_M, size_L, size_XL, size_XXL, available;

    public Product() {
    }

    public boolean isAvailable() {
        return available;
    }

    public String getProductCode() {
        return productCode;
    }

    public boolean isCheckedSizeS() {
        return size_S;
    }

    public boolean isCheckedSizeM() {
        return size_M;
    }

    public boolean isCheckedSizeL() {
        return size_L;
    }

    public boolean isCheckedSizeXL() {
        return size_XL;
    }

    public boolean isCheckedSizeXXL() {
        return size_XXL;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleImagePath() {
        return titleImagePath;
    }

    public String getFirstImagePath() {
        return  firstImagePath;
    }

    public String getSecondImagePath() {
        return  secondImagePath;
    }

    public String getThirdImagePath() {
        return  thirdImagePath;
    }

    public String getFourthImagePath() {
        return  fourthImagePath;
    }

    public String getFifthImagePath() {
        return  fifthImagePath;
    }

    public String getId() {
        return id;
    }

    public String getTitleDescription() {
        return titleDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitleImagePath(String path) {
        titleImagePath = path;
    }

    public void setFirstImagePath(String path) {
        firstImagePath = path;
    }

    public void setSecondImagePath(String path) {
        secondImagePath = path;
    }

    public void setThirdImagePath(String path) {
        thirdImagePath = path;
    }

    public void setFourthImagePath(String path) {
        fourthImagePath = path;
    }

    public void setFifthImagePath(String path) {
        fifthImagePath = path;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitleDescription(String titleDescription) {
        this.titleDescription = titleDescription;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSizeS(boolean size) {
       size_S = size;
    }

    public void setSizeM(boolean size) {
        size_M = size;
    }

    public void setSizeL(boolean size) {
        size_L = size;
    }

    public void setSizeXL(boolean size) {
        size_XL = size;
    }

    public void setSizeXXL(boolean size) {
        size_XXL = size;
    }

    public void setProductCode(String code) {
        productCode = code;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
