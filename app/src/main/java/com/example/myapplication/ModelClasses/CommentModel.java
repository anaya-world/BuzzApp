package com.example.myapplication.ModelClasses;

public class CommentModel {
    public String mobileno;

    public String name;

    public String post_comments;

    public String post_date;

    public String secrate_id;

    public String user_img;

    public String userid;

    public CommentModel(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7) {
        this.userid = paramString1;
        this.name = paramString2;
        this.secrate_id = paramString3;
        this.mobileno = paramString4;
        this.post_comments = paramString5;
        this.post_date = paramString6;
        this.user_img = paramString7;
    }

    public String getMobileno() { return this.mobileno; }

    public String getName() { return this.name; }

    public String getPost_comments() { return this.post_comments; }

    public String getPost_date() { return this.post_date; }

    public String getSecrate_id() { return this.secrate_id; }

    public String getUser_img() { return this.user_img; }

    public String getUserid() { return this.userid; }

    public void setMobileno(String paramString) { this.mobileno = paramString; }

    public void setName(String paramString) { this.name = paramString; }

    public void setPost_comments(String paramString) { this.post_comments = paramString; }

    public void setPost_date(String paramString) { this.post_date = paramString; }

    public void setSecrate_id(String paramString) { this.secrate_id = paramString; }

    public void setUser_img(String paramString) { this.user_img = paramString; }

    public void setUserid(String paramString) { this.userid = paramString; }
}
