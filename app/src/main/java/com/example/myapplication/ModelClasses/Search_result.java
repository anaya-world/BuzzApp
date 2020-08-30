package com.example.myapplication.ModelClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Search_result {

    @SerializedName("search_result")
    @Expose
    private ArrayList<Send_data> send_data = null;
    @SerializedName("user_img")
    @Expose
    private String userImg;
    private String gif;

    public String getSendtoid() {
        return sendtoid;
    }

    public void setSendtoid(String sendtoid) {
        this.sendtoid = sendtoid;
    }

    private String sendtoid;

    public String getGif() {
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
    }

    @SerializedName("schedule_id")
    @Expose
    private String scheduleId;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("festival_type")
    @Expose
    private String festivalType;
    @SerializedName("sendTo")
    @Expose
    private String sendTo;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("secrate_id")
    @Expose
    private String secrateId;
    @SerializedName("mobileno")
    @Expose
    private String mobileno;

    private int hasMsgSent = 0;

    public ArrayList<Send_data> getSend_data() {
        return send_data;
    }

    public void setSend_data(ArrayList<Send_data> send_data) {
        this.send_data = send_data;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFestivalType() {
        return festivalType;
    }

    public void setFestivalType(String festivalType) {
        this.festivalType = festivalType;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSecrateId() {
        return secrateId;
    }

    public void setSecrateId(String secrateId) {
        this.secrateId = secrateId;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public int getHasMsgSent() {
        return hasMsgSent;
    }

    public void setHasMsgSent(int hasMsgSent) {
        this.hasMsgSent = hasMsgSent;
    }
}
