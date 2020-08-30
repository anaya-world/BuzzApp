package com.example.myapplication.ModelClasses;

public class AnniversaryGifModel {

    String Image;
    String caller_id;
    String name;
    String img;


    public AnniversaryGifModel(String image, String caller_id, String name, String img) {
        Image = image;
        this.caller_id = caller_id;
        this.name = name;
        this.img = img;
    }

//    public BirthdayGifModel(String image, String caller_id) {
//        Image = image;
//        this.caller_id = caller_id;
//    }
//

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
//    public BirthdayGifModel(String image) {
//        Image = image;
//    }

    public String getCaller_id() {
        return caller_id;
    }

    public void setCaller_id(String caller_id) {
        this.caller_id = caller_id;
    }

    public AnniversaryGifModel(){

    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
