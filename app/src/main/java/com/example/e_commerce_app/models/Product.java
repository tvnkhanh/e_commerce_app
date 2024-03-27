package com.example.e_commerce_app.models;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String id;
    private String productName;
    private String uid;
    private String categoryId;
    private String desc;
    private String price;
    private int quantity;
    private List<String> listImageUrl;
    private List<Rating> ratings;

    public Product() {
    }

    public Product(String id, String productName, String uid, String categoryId,
                   String desc, String price, int quantity, List<String> listImageUrl, List<Rating> ratings) {
        this.id = id;
        this.productName = productName;
        this.uid = uid;
        this.categoryId = categoryId;
        this.desc = desc;
        this.price = price;
        this.quantity = quantity;
        this.listImageUrl = listImageUrl;
        this.ratings = ratings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<String> getListImageUrl() {
        return listImageUrl;
    }

    public void setListImageUrl(List<String> listImageUrl) {
        this.listImageUrl = listImageUrl;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
}
