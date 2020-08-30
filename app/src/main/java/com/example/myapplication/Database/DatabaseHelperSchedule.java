package com.example.myapplication.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.myapplication.ModelClasses.Search_result;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelperSchedule extends SQLiteOpenHelper {
    private static final String DATABASE = "schdule.db";

    private static String COL_1 = "id";
    private static String COL_2 = "schedule_id";
    private static String COL_3 = "festival_type";
    private static String COL_4 = "sendTo";
    private static String COL_5 = "date";
    private static String COL_6 = "message";
    private static String COL_7 = "userid";
    private static String COL_8 = "gif";
    private static String COL_9 = "time";
    private static String COL_10 = "secrate_id";
    private static String COL_11 = "mobileno";
    private static String COL_12 = "user_img";
    private static String COL_13 = "has_msg_sent";
    private static final String TABLENAME = "mytable2";


    String f3br;
    private ArrayList<Search_result> searchResults;
    private String imgurl;


    public DatabaseHelperSchedule(@Nullable Context context) {
        super(context, DATABASE, null, 15);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(TABLENAME);
        sb.append("(");
        sb.append(COL_1);
        sb.append(" Text,");
        sb.append(COL_2);
        sb.append(" Text UNIQUE, ");
        sb.append(COL_3);
        sb.append(" Text, ");
        sb.append(COL_4);
        sb.append(" Text, ");
        sb.append(COL_5);
        sb.append(" Text,");
        sb.append(COL_6);
        sb.append(" Text,");

        sb.append(COL_7);
        sb.append(" Text,");
        sb.append(COL_8);
        sb.append(" Text,");
        sb.append(COL_9);
        sb.append(" Text,");
        sb.append(COL_10);
        sb.append(" Text,");
        sb.append(COL_11);
        sb.append(" Text,");
        sb.append(COL_12);
        sb.append(" Text,");
        sb.append(COL_13);
        sb.append("  Integer);");
        this.f3br = sb.toString();
        db.execSQL(this.f3br);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLENAME);
        onCreate(db);


    }

    public boolean InsertData(int randomInt, String scheduleId, String festivalType, String sendTo,
                              String date, String message, String userid, String gif, String time,
                              String names, String mobileno, String imgurl, int hasMessageSent) {


        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, randomInt);
        contentValues.put(COL_2, scheduleId);
        contentValues.put(COL_3, festivalType);
        contentValues.put(COL_4, sendTo);
        contentValues.put(COL_5, date);
        contentValues.put(COL_6, message);
        contentValues.put(COL_7, userid);
        contentValues.put(COL_8, gif);
        contentValues.put(COL_9, time);
        contentValues.put(COL_10, names);
        contentValues.put(COL_11, mobileno);
        contentValues.put(COL_12, imgurl);
        contentValues.put(COL_13, hasMessageSent);

        long NEWROWID = db.insert(TABLENAME, null, contentValues);
        db.close();
        if (NEWROWID == -1) {
            return false;
        } else {
            return true;
        }


    }

    public List<Search_result> getData(String datestamp, String timestamp) {
        String dates = datestamp;
        String times = timestamp;
        List<Search_result> data = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ");
        sb.append(TABLENAME);
        sb.append(" WHERE ");
        sb.append(COL_5);
        sb.append(" = '");
        sb.append(dates);
        sb.append("' AND ");
        sb.append(COL_9);
        sb.append("= '");
        sb.append(times + "'");

        Cursor cursor = db.rawQuery(sb.toString(), null);
        StringBuffer stringBuffer = new StringBuffer();
        while (cursor.moveToNext()) {
            Search_result dataModel = new Search_result();
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String schedule_id = cursor.getString(cursor.getColumnIndexOrThrow("schedule_id"));
            String festival_type = cursor.getString(cursor.getColumnIndexOrThrow("festival_type"));
            String sendTo = cursor.getString(cursor.getColumnIndexOrThrow("sendTo"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
            String userid = cursor.getString(cursor.getColumnIndexOrThrow("userid"));
            String gif = cursor.getString(cursor.getColumnIndexOrThrow("gif"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            String secrate_id = cursor.getString(cursor.getColumnIndexOrThrow("secrate_id"));
            String mobileno = cursor.getString(cursor.getColumnIndexOrThrow("mobileno"));
            String user_img = cursor.getString(cursor.getColumnIndexOrThrow("user_img"));
            int hasMessageSent = cursor.getInt(cursor.getColumnIndexOrThrow("has_msg_sent"));

            dataModel.setId(id);
            dataModel.setScheduleId(schedule_id);
            dataModel.setFestivalType(festival_type);
            dataModel.setSendTo(sendTo);
            dataModel.setDate(date);
            dataModel.setMessage(message);
            dataModel.setSendtoid(userid);
            dataModel.setGif(gif);
            dataModel.setTime(time);
            dataModel.setSecrateId(secrate_id);
            dataModel.setMobileno(mobileno);
            dataModel.setUserImg(user_img);
            dataModel.setHasMsgSent(hasMessageSent);
            stringBuffer.append(dataModel);
            data.add(dataModel);
        }
        return data;
    }

    public void cleartable() {
        this.getWritableDatabase().delete(TABLENAME, null, null);
    }

    public void deleteSchedule(String scheduleid) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLENAME, COL_2 + "=" + scheduleid, null);
    }

    public List<Search_result> getData1() {
        List<Search_result> data = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ");
        sb.append(TABLENAME);
        Cursor cursor = db.rawQuery(sb.toString(), null);
        StringBuffer stringBuffer = new StringBuffer();
        while (cursor.moveToNext()) {
            Search_result dataModel = new Search_result();
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String schedule_id = cursor.getString(cursor.getColumnIndexOrThrow("schedule_id"));
            String festival_type = cursor.getString(cursor.getColumnIndexOrThrow("festival_type"));
            String sendTo = cursor.getString(cursor.getColumnIndexOrThrow("sendTo"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
            String userid = cursor.getString(cursor.getColumnIndexOrThrow("userid"));
            String gif = cursor.getString(cursor.getColumnIndexOrThrow("gif"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            String secrate_id = cursor.getString(cursor.getColumnIndexOrThrow("secrate_id"));
            String mobileno = cursor.getString(cursor.getColumnIndexOrThrow("mobileno"));
            String user_img = cursor.getString(cursor.getColumnIndexOrThrow("user_img"));
            int hasMessageSent = cursor.getInt(cursor.getColumnIndexOrThrow("has_msg_sent"));

            dataModel.setId(id);
            dataModel.setScheduleId(schedule_id);
            dataModel.setFestivalType(festival_type);
            dataModel.setSendTo(sendTo);
            dataModel.setDate(date);
            dataModel.setMessage(message);
            dataModel.setSendtoid(userid);
            dataModel.setGif(gif);
            dataModel.setTime(time);
            dataModel.setSecrateId(secrate_id);
            dataModel.setMobileno(mobileno);
            dataModel.setUserImg(user_img);
            dataModel.setHasMsgSent(hasMessageSent);
            stringBuffer.append(dataModel);
            data.add(dataModel);
        }
        return data;
    }

    public boolean UpdateData(String scheduleId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_13, 1);
        long NEWROWID = db.update(TABLENAME, contentValues, COL_2 + "=" + scheduleId, null);

        db.close();
        return NEWROWID != -1;
    }

    public boolean deleteData(String scheduleId) {
        SQLiteDatabase db = getWritableDatabase();
        long NEWROWID = db.delete(TABLENAME, COL_2 + "=" + scheduleId, null);
        db.close();
        return NEWROWID != -1;
    }
}
