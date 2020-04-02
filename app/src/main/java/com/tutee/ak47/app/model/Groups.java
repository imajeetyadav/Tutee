package com.tutee.ak47.app.model;


public class Groups {

    private String date,message,name,time,type,Count;



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Groups(String date, String message, String name, String time, String type, String count) {
        this.date = date;
        this.message = message;
        this.name = name;
        this.time = time;
        this.type = type;

        Count = count;
    }

    public Groups() {
    }


    public String getCount() {
        return Count;
    }
}
