package com.example.myapplication.ModelClasses;

public class AnniversaryModel {

    private String name;
    private String anniversary;
    private String image;

    public AnniversaryModel(String name, String anniversary, String image) {
        this.name = name;
        this.anniversary = anniversary;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnniversary() {
        return anniversary;
    }

    public void setAnniversary(String anniversary) {
        this.anniversary = anniversary;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
