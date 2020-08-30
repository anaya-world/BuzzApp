package com.example.myapplication.ModelClasses;

public class Models {
    public Models(int images, String names) {
        this.images = images;
        this.names = names;
    }

    int images;
    String names;

    public int getImages() {
        return images;
    }

    public void setImages(int images) {
        this.images = images;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }
}
