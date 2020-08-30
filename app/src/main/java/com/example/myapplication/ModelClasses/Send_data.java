package com.example.myapplication.ModelClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Send_data {

    @SerializedName("perent_schedule_id")
    @Expose
    private String perentScheduleId;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("secrate_id")
    @Expose
    private String secrateId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("user_img")
    @Expose
    private String userImg;
    @SerializedName("caller_id")
    @Expose
    private String callerId;

    public String getPerentScheduleId() {
        return perentScheduleId;
    }

    public void setPerentScheduleId(String perentScheduleId) {
        this.perentScheduleId = perentScheduleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecrateId() {
        return secrateId;
    }

    public void setSecrateId(String secrateId) {
        this.secrateId = secrateId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }
}
