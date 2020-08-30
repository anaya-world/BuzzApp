package com.example.myapplication.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import com.example.myapplication.ModelClasses.Callhistory_Model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    String paramString1;
    SQLiteDatabase sqLiteDatabase;

    public static String CALLERID;

    public static String DATABASE = "calldatabase.db";

    private static final int DATABASE_VERSION = 15;

    public static String DATETIME;

    public static String DURATION;

    public static String ID;

    public static String IMAGE;

    public static String INCOMINGOUTGOING_IMAGE;

    public static String NAME;

    public static String TABLEs = "mytable1";

    String br;

    static  {
        NAME = "name";
        IMAGE = "profile_image";
        INCOMINGOUTGOING_IMAGE = "incoutg_image";
        DATETIME = "datetime";
        CALLERID = "callerid";
        ID = "id";
        DURATION = "duration";
    }



    public DatabaseHelper(Context paramContext) { super(paramContext, DATABASE, null, 15); }

    public void cleartable() { getWritableDatabase().delete(TABLEs, null, null); }

    public void deleteEntry(String paramString) {
        SQLiteDatabase sQLiteDatabase = getWritableDatabase();
        String str = TABLEs;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ID);
        stringBuilder.append("=");
        stringBuilder.append(paramString);
        sQLiteDatabase.delete(str, stringBuilder.toString(), null);
    }

    public List<Callhistory_Model> getdata() {
        ArrayList arrayList = new ArrayList();
        SQLiteDatabase sQLiteDatabase = getWritableDatabase();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select * from ");
        stringBuilder.append(TABLEs);
        stringBuilder.append(" order by datetime desc ;");
        Cursor cursor = sQLiteDatabase.rawQuery(stringBuilder.toString(), null);
        StringBuffer stringBuffer = new StringBuffer();
        while (cursor.moveToNext()) {
            Callhistory_Model callhistory_Model = new Callhistory_Model();
            String str1 = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String str2 = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String str3 = cursor.getString(cursor.getColumnIndexOrThrow("profile_image"));
            String str4 = cursor.getString(cursor.getColumnIndexOrThrow("incoutg_image"));
            String str5 = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));
            String str6 = cursor.getString(cursor.getColumnIndexOrThrow("callerid"));
            String str7 = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
            callhistory_Model.setId(str1);
            callhistory_Model.setName(str2);
            callhistory_Model.setProfile_image(str3);
            callhistory_Model.setIncoutg_image(str4);
            callhistory_Model.setDatetime(str5);
            callhistory_Model.setCallerid(str6);
            callhistory_Model.setDuration(str7);
            stringBuffer.append(callhistory_Model);
            arrayList.add(callhistory_Model);
        }
        return arrayList;
    }

    public void insertdata(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
        PrintStream printStream = System.out;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Hello ");
        stringBuilder.append(this.br);
        printStream.print(stringBuilder.toString());
        try {
            SQLiteDatabase sQLiteDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ID, Integer.valueOf(paramInt));
            contentValues.put(NAME, paramString1);
            contentValues.put(IMAGE, paramString2);
            contentValues.put(INCOMINGOUTGOING_IMAGE, paramString3);
            contentValues.put(DATETIME, paramString4);
            contentValues.put(CALLERID, paramString5);
            sQLiteDatabase.insert(TABLEs, null, contentValues);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE ");
        stringBuilder.append(TABLEs);
        stringBuilder.append("(");
        stringBuilder.append(ID);
        stringBuilder.append(" Text,");
        stringBuilder.append(NAME);
        stringBuilder.append(" Text, ");
        stringBuilder.append(IMAGE);
        stringBuilder.append(" Text, ");
        stringBuilder.append(INCOMINGOUTGOING_IMAGE);
        stringBuilder.append(" Text, ");
        stringBuilder.append(DATETIME);
        stringBuilder.append(" Text,");
        stringBuilder.append(CALLERID);
        stringBuilder.append(" Text,'");
        stringBuilder.append(DURATION);
        stringBuilder.append("'  Text);");
        this.br = stringBuilder.toString();
        paramSQLiteDatabase.execSQL(this.br);
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {
        if (paramInt1 > paramInt2) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DROP TABLE IF EXISTS ");
            stringBuilder.append(TABLEs);
            paramSQLiteDatabase.execSQL(stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append("CREATE TABLE ");
            stringBuilder.append(TABLEs);
            stringBuilder.append("(");
            stringBuilder.append(ID);
            stringBuilder.append(" Text,");
            stringBuilder.append(NAME);
            stringBuilder.append(" Text, ");
            stringBuilder.append(IMAGE);
            stringBuilder.append(" Text, ");
            stringBuilder.append(INCOMINGOUTGOING_IMAGE);
            stringBuilder.append(" Text, ");
            stringBuilder.append(DATETIME);
            stringBuilder.append(" Text,");
            stringBuilder.append(CALLERID);
            stringBuilder.append(" Text ,'");
            stringBuilder.append(DURATION);
            stringBuilder.append("'  Text);");
            this.br = stringBuilder.toString();
            paramSQLiteDatabase.execSQL(this.br);
        }
    }

    public void updateduration(String paramString1, String paramString2) {
        final SQLiteDatabase sQLiteDatabase = getWritableDatabase();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE ");
        stringBuilder.append(TABLEs);
        stringBuilder.append(" SET ");
        stringBuilder.append(DURATION);
        stringBuilder.append(" = ");
        stringBuilder.append(paramString2);
        stringBuilder.append(" WHERE ");
        stringBuilder.append(ID);
        stringBuilder.append(" = ");
        stringBuilder.append(paramString1);
        paramString1 = stringBuilder.toString();
        sqLiteDatabase.beginTransaction();
        final SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(paramString1);
        try {
            sqLiteStatement.execute();
            sqLiteDatabase.setTransactionSuccessful();
            return;
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
}
