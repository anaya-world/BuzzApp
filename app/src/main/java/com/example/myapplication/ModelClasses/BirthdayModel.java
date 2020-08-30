package com.example.myapplication.ModelClasses;

public class BirthdayModel {
    private String name;
    private String dob;
    private String img;

    public BirthdayModel(String name, String dob, String img) {
        this.name = name;
        this.dob = dob;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
