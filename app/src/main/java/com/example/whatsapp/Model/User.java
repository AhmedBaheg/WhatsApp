package com.example.whatsapp.Model;

public class User {

    private String name , status , uid , imageUrl;

    public User(String name, String status, String uid , String imageUrl) {
        this.name = name;
        this.status = status;
        this.uid = uid;
        this.imageUrl = imageUrl;
    }

    public User(String name, String status, String uid) {
        this.name = name;
        this.status = status;
        this.uid = uid;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
