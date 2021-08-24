package com.example.chau_admin.models;

import static com.example.chau_admin.utils.Constants.IMG_URL;
public class Category {

    private String img_url,category;

    public Category() {
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImg_url() { return IMG_URL+img_url; }

    public void setImg_url(String img_url) { this.img_url = img_url; }
}