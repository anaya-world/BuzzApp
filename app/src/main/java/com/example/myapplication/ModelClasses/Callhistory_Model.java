package com.example.myapplication.ModelClasses;

public class Callhistory_Model {
    private String callerid;

    private String datetime;

    private String duration;

    private String id;

    private String incoutg_image;

    private String name;

    private String profile_image;
    private int callername,calling_time,calling_status_text;

    public Callhistory_Model() {}

    public Callhistory_Model(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7) {
        this.id = paramString1;
        this.name = paramString2;
        this.profile_image = paramString3;
        this.incoutg_image = paramString4;
        this.datetime = paramString5;
        this.callerid = paramString6;
        this.duration = paramString7;
    }

    public Callhistory_Model(String vivek_singh, String s, String s1) {
    }

    public String getCallerid() { return this.callerid; }

    public String getDatetime() { return this.datetime; }

    public String getDuration() { return this.duration; }

    public String getId() { return this.id; }

    public String getIncoutg_image() { return this.incoutg_image; }

    public String getName() { return this.name; }

    public String getProfile_image() { return this.profile_image; }

    public void setCallerid(String paramString) { this.callerid = paramString; }

    public void setDatetime(String paramString) { this.datetime = paramString; }

    public void setDuration(String paramString) { this.duration = paramString; }

    public void setId(String paramString) { this.id = paramString; }

    public void setIncoutg_image(String paramString) { this.incoutg_image = paramString; }

    public void setName(String paramString) { this.name = paramString; }

    public void setProfile_image(String paramString) 
    { this.profile_image = paramString; }

    public int getCallername()
    {
        return this.callername;
    }

    public int getCalling_time() {
        return this.calling_time;
    }

    public int getCalling_status_text() {
        return this.calling_status_text;
    }
}
