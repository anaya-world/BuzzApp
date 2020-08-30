package com.example.myapplication.ModelClasses;

import java.io.Serializable;

public class User implements Serializable {
    private String caller_id;
    private String connected;
    private String resident;
    private String user_email;
    private String user_id;
    private String user_image;
    private String user_mobile_no;
    private String user_name;
    private String user_password;
    private String user_secret_id;

    public User(String user_id2, String user_name2, String user_email2, String user_mobile_no2, String user_password2, String user_secret_id2, String user_image2, String resident2, String connected2, String caller_id2) {
        this.user_id = user_id2;
        this.user_name = user_name2;
        this.user_email = user_email2;
        this.user_mobile_no = user_mobile_no2;
        this.user_password = user_password2;
        this.user_secret_id = user_secret_id2;
        this.user_image = user_image2;
        this.resident = resident2;
        this.connected = connected2;
        this.caller_id = user_id2;
    }

    public String getCaller_id() {
        return this.user_id;
    }

    public void setCaller_id(String caller_id2) {
        this.caller_id = caller_id2;
    }

    public String getConnected() {
        return this.connected;
    }

    public void setConnected(String connected2) {
        this.connected = connected2;
    }

    public String getResident() {
        return this.resident;
    }

    public void setResident(String resident2) {
        this.resident = resident2;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id2) {
        this.user_id = user_id2;
    }

    public String getUser_name() {
        return this.user_name;
    }

    public void setUser_name(String user_name2) {
        this.user_name = user_name2;
    }

    public String getUser_email() {
        return this.user_email;
    }

    public void setUser_email(String user_email2) {
        this.user_email = user_email2;
    }

    public String getUser_mobile_no() {
        return this.user_mobile_no;
    }

    public void setUser_mobile_no(String user_mobile_no2) {
        this.user_mobile_no = user_mobile_no2;
    }

    public String getUser_password() {
        return this.user_password;
    }

    public void setUser_password(String user_password2) {
        this.user_password = user_password2;
    }

    public String getUser_secret_id() {
        return this.user_secret_id;
    }

    public void setUser_secret_id(String user_secret_id2) {
        this.user_secret_id = user_secret_id2;
    }

    public String getUser_image() {
        return this.user_image;
    }

    public void setUser_image(String user_image2) {
        this.user_image = user_image2;
    }
}
