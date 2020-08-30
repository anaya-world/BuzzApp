package com.example.myapplication.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.example.myapplication.Activities.LoginRegistrationActivity;
import com.example.myapplication.Adapter.CallingAdapter;
import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.ModelClasses.User;
import java.util.ArrayList;

public class SharedPrefManager {
    private static final String KEY_CALLERID = "callerid";
    private static final String KEY_CONNECTED = "connected";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_INTEREST = "interest";
    private static final String KEY_MARTIAL = "martial";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_RESIDENT = "resident";
    private static final String KEY_SECURITY = "security";
    private static final String KEY_USERNAME = "name";
    public static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_IMAGE = "user_image";
    private static final String KEY_WORK = "work";
    private static final String PASSWORD = "password";
    private static final String SECRET_ID = "secret_id";
    private static final String SHARED_PREF_NAME = "current_user";
    private static Context mCtx;
    private static SharedPrefManager mInstance;
   // private SharedPrefsHelper sharedPrefsHelper;

    public SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        SharedPrefManager sharedPrefManager;
        synchronized (SharedPrefManager.class) {
            if (mInstance == null) {
                mInstance = new SharedPrefManager(context);
            }
            sharedPrefManager = mInstance;
        }
        return sharedPrefManager;
    }

    public void userLogin(User user) {
        Editor editor = mCtx.getSharedPreferences(SHARED_PREF_NAME, 0).edit();
        editor.putString(KEY_USER_ID, user.getUser_id());
        editor.putString(KEY_USERNAME, user.getUser_name());
        editor.putString(KEY_EMAIL, user.getUser_email());
        editor.putString(KEY_MOBILE, user.getUser_mobile_no());
        editor.putString("password", user.getUser_password());
        editor.putString(SECRET_ID, user.getUser_secret_id());
        editor.putString(KEY_USER_IMAGE, user.getUser_image());
        editor.putString("resident", user.getResident());
        editor.putString(KEY_CONNECTED, user.getConnected());
        editor.putString(KEY_CALLERID, user.getCaller_id());

        editor.apply();
    }

    public boolean isLoggedIn() {
        if (mCtx.getSharedPreferences(SHARED_PREF_NAME, 0).getString("user_id", null) != null) {
            return true;
        }
        return false;
    }

    public String isConnected() {
        return mCtx.getSharedPreferences(SHARED_PREF_NAME, 0).getString(KEY_CONNECTED, null);
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, 0);
        User user = new User(sharedPreferences.getString("user_id", null), sharedPreferences.getString("name", null), sharedPreferences.getString("email", null), sharedPreferences.getString(KEY_MOBILE, null), sharedPreferences.getString("password", null), sharedPreferences.getString(SECRET_ID, null), sharedPreferences.getString(KEY_USER_IMAGE, null), sharedPreferences.getString("resident", null), sharedPreferences.getString(KEY_CONNECTED, null), sharedPreferences.getString(KEY_CALLERID, null));
        return user;
    }

    public void logout( Context mCtx2) {
        DatabaseHelper database = new DatabaseHelper(mCtx2);
        CallingAdapter callingAdapter = new CallingAdapter(mCtx2, new ArrayList<>());
        database.cleartable();
        callingAdapter.notifyDataSetChanged();
        Editor editor = mCtx2.getSharedPreferences(SHARED_PREF_NAME, 0).edit();
        editor.clear();
        editor.apply();

                    Intent intent = new Intent(mCtx2, LoginRegistrationActivity.class);
                    mCtx2.startActivity(intent);
                    ((Activity) mCtx2).finish();


    }

    public void save(String key, Object value) {
        Editor editor = getEditor();
        if (value instanceof Boolean) {
            editor.putBoolean(key, ((Boolean) value).booleanValue());
        } else if (value instanceof Integer) {
            editor.putInt(key, ((Integer) value).intValue());
        } else if (value instanceof Float) {
            editor.putFloat(key, ((Float) value).floatValue());
        } else if (value instanceof Long) {
            editor.putLong(key, ((Long) value).longValue());
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-supported preference");
        }
        editor.commit();
    }

    private Editor getEditor() {
        return mCtx.getSharedPreferences(SHARED_PREF_NAME, 0).edit();
    }

    public <T> T get(String key , int defValue) {
        return (T) mCtx.getSharedPreferences(SHARED_PREF_NAME, 0).getAll().get(key);
    }

    public <T> T get(String key, T defValue) {
        T returnValue = (T) mCtx.getSharedPreferences(SHARED_PREF_NAME, 0).getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public boolean has(String key) {
        return mCtx.getSharedPreferences(SHARED_PREF_NAME, 0).contains(key);
    }

    public void saveFingerPrintOn(boolean b) {
        save("isFingerPrintOn", Boolean.valueOf(b));
    }

    public boolean isFingerPrintOn() {
        return ((Boolean) get("isFingerPrintOn", Boolean.valueOf(false))).booleanValue();
    }

    public String save(String keyUserId) {
        return KEY_USER_ID;
    }
}
