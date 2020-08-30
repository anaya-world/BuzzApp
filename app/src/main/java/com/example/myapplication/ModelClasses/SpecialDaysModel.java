package com.example.myapplication.ModelClasses;

public class SpecialDaysModel {
    private String date;

    private String festival_name;

    private String festival_type;

    private String month;

    public SpecialDaysModel(String paramString1, String paramString2, String paramString3, String paramString4) {
        this.month = paramString1;
        this.date = paramString2;
        this.festival_name = paramString3;
        this.festival_type = paramString4;
    }

    public String getDate() { return this.date; }

    public String getFestival_name() { return this.festival_name; }

    public String getFestival_type() { return this.festival_type; }

    public String getMonth() { return this.month; }

    public void setDate(String paramString) { this.date = paramString; }

    public void setFestival_name(String paramString) { this.festival_name = paramString; }

    public void setFestival_type(String paramString) { this.festival_type = paramString; }

    public void setMonth(String paramString) { this.month = paramString; }
}
