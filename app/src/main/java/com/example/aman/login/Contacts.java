package com.example.aman.login;

public class Contacts {
    public String name, image ,bio,request_type;

    public Contacts() {

    }

    public Contacts(String request_type) {
        this.request_type = request_type;
    }


    public Contacts(String name, String image, String bio) {

        this.name = name;
        this.image = image;
        this.bio =bio;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

}
