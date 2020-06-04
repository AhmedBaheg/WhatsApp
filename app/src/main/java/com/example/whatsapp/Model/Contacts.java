package com.example.whatsapp.Model;

public class Contacts {

    private String name , status , imageUrl;

    public Contacts(String name, String status, String imageUrl) {
        this.name = name;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public Contacts() {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
