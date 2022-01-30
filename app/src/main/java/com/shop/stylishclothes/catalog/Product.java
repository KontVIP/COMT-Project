package com.shop.stylishclothes.catalog;


public class Product {

    public String currentSize;
    public int quantity;

    private String id, title, titleDescription, description, price, productCode, titleImagePath,
            firstImagePath = "", secondImagePath = "", thirdImagePath = "", fourthImagePath = "",
            fifthImagePath = "", category;
    private boolean size_S = false, size_M = false, size_L = false, size_XL = false, size_XXL = false, available = false;
     public boolean DELETED = false;

    public Product() {
    }

    public Product(Product product) {
        this.id = product.id;
        this.title = product.title;
        this.titleDescription = product.titleDescription;
        this.description = product.description;
        this.price = product.price;
        this.productCode = product.productCode;
        this.titleImagePath = product.titleImagePath;
        this.firstImagePath = product.firstImagePath;
        this.secondImagePath = product.secondImagePath;
        this.thirdImagePath = product.thirdImagePath;
        this.fourthImagePath = product.fourthImagePath;
        this.fifthImagePath = product.fifthImagePath;
        this.category = product.category;
        this.size_S = product.size_S;
        this.size_M = product.size_M;
        this.size_L = product.size_L;
        this.size_XL = product.size_XL;
        this.size_XXL = product.size_XXL;
        this.available = product.available;
        this.DELETED = product.DELETED;
        this.currentSize = product.currentSize;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getCategory() {
        return category;
    }

    public String getProductCode() {
        return productCode;
    }

    public boolean isSize_S() {
        return this.size_S;
    }

    public boolean isSize_M() {
        return size_M;
    }

    public boolean isSize_L() {
        return size_L;
    }

    public boolean isSize_XL() {
        return size_XL;
    }

    public boolean isSize_XXL() {
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

    public void setCategory(String category) {
        this.category = category;
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
       this.size_S = size;
    }

    public void setSizeM(boolean size) {
        this.size_M = size;
    }

    public void setSizeL(boolean size) {
        this.size_L = size;
    }

    public void setSizeXL(boolean size) {
        this.size_XL = size;
    }

    public void setSizeXXL(boolean size) {
        this.size_XXL = size;
    }

    public void setProductCode(String code) {
        productCode = code;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void markDeleted() {
        DELETED = true;
    }

}
