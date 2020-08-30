package com.example.myapplication.ModelClasses;

public class FriendListModel {
    String anniversary;

    String caller_id;

    String dob;

    String email;

    String frnid;

    String frns;

    String gender;

    String image;

    String marital;

    String mobileno;

    String name;

    String secret_id;

    String user_id;

    public FriendListModel(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, String paramString12, String paramString13) {
        this.name = paramString1;
        this.image = paramString2;
        this.gender = paramString3;
        this.secret_id = paramString4;
        this.caller_id = paramString5;
        this.dob = paramString6;
        this.anniversary = paramString7;
        this.email = paramString8;
        this.marital = paramString9;
        this.mobileno = paramString10;
        this.user_id = paramString11;
        this.frnid = paramString12;
        this.frns = paramString13;
    }

    public FriendListModel(String name, String user_image, String gender, String secrate_id, String user_id) {

    }

    public String getAnniversary() { return this.anniversary; }

    public String getCaller_id() { return this.user_id; }

    public String getDob() { return this.dob; }

    public String getEmail() { return this.email; }

    public String getFrnid() { return this.frnid; }

    public String getFrns() { return this.frns; }

    public String getGender() { return this.gender; }

    public String getImage() { return this.image; }

    public String getMarital() { return this.marital; }

    public String getMobileno() { return this.mobileno; }

    public String getName() { return this.name; }

    public String getSecret_id() { return this.secret_id; }

    public String getUser_id() { return this.user_id; }

    public void setAnniversary(String paramString) { this.anniversary = paramString; }

    public void setCaller_id(String paramString) { this.caller_id = paramString; }

    public void setDob(String paramString) { this.dob = paramString; }

    public void setEmail(String paramString) { this.email = paramString; }

    public void setFrnid(String paramString) { this.frnid = paramString; }

    public void setFrns(String paramString) { this.frns = paramString; }

    public void setGender(String paramString) { this.gender = paramString; }

    public void setImage(String paramString) { this.image = paramString; }

    public void setMarital(String paramString) { this.marital = paramString; }

    public void setMobileno(String paramString) { this.mobileno = paramString; }

    public void setName(String paramString) { this.name = paramString; }

    public void setSecret_id(String paramString) { this.secret_id = paramString; }

    public void setUser_id(String paramString) { this.user_id = paramString; }
}
