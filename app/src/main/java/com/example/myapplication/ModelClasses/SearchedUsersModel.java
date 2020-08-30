package com.example.myapplication.ModelClasses;

public class SearchedUsersModel {

    String name;
    String image;
    String mobile;
    String gender;
    String secret_id;
    String status;
    String friend_id;

    public SearchedUsersModel(String name, String image, String mobile, String gender, String secret_id, String status, String friend_id) {
        this.name = name;
        this.image = image;
        this.mobile = mobile;
        this.gender = gender;
        this.secret_id = secret_id;
        this.status = status;
        this.friend_id = friend_id;
    }

    public SearchedUsersModel(String name, String user_img) {
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSecret_id() {
        return secret_id;
    }

    public void setSecret_id(String secret_id) {
        this.secret_id = secret_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }


}
