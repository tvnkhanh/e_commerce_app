package com.example.e_commerce_app.models;

public class Category {
    private String id;
    private String category;
    private String imageUrl;

    public Category() {
    }

    public Category(String id, String category, String imageUrl) {
        this.id = id;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
