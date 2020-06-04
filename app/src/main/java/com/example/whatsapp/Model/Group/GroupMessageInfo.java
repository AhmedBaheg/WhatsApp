package com.example.whatsapp.Model.Group;

public class GroupMessageInfo {

    private String name, date, time, message;

    public GroupMessageInfo(String name, String date, String time, String message) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.message = message;
    }

    public GroupMessageInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
