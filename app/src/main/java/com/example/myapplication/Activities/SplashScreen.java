package com.example.myapplication.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.App;
import com.example.myapplication.R;
import com.example.myapplication.Utils.PreferenceUtils;
import com.example.myapplication.Utils.ProgressLogin;
import com.example.myapplication.Utils.SharedPrefManager;
import com.example.myapplication.Utils.SyncManagerUtils;
import com.sendbird.android.SendBirdException;
import com.sendbird.syncmanager.SendBirdSyncManager;
import com.sendbird.syncmanager.handler.CompletionHandler;

public class SplashScreen extends AppCompatActivity {
    private SharedPrefManager sharedPrefManager;
  //  ProgressLogin progressLogin;
   // App state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // state=(App)getApplicationContext();
   //     progressLogin=new ProgressLogin(this);

        sharedPrefManager = SharedPrefManager.getInstance(this);
        PreferenceUtils.init(SplashScreen.this);
        if (!isConnected(SplashScreen.this)) buildDialog(SplashScreen.this).show();
        else {
            //Toast.makeText(SplashScreen.this,"Welcome", Toast.LENGTH_SHORT).show();


            Log.d("sharedPrefsHelper","12"+sharedPrefManager.isLoggedIn());
            if (sharedPrefManager.isLoggedIn()) {
             //   progressLogin.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                       // startLoginService(sharedPrefsHelper.getQbUser());
                        setUpSyncManager();
                        //     ();
                        startActivity(new Intent(SplashScreen.this, LoginRegistrationActivity.class));
                        finish();
                    }
                },3000);
            } else {
                setContentView(R.layout.activity_splash_screen);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreen.this, LoginRegistrationActivity.class));
                        finish();
                    }
                }, 4000);
            }
        }

    }

    private void setUpSyncManager() {
        Log.d("lifecycle","spalsh"+sharedPrefManager.getUser().getUser_id());
        if (sharedPrefManager.getUser().getUser_id() != null) {
            String userId = String.valueOf(this.sharedPrefManager.getUser().getUser_id()).replaceAll("\\s", "");

            SyncManagerUtils.setup(SplashScreen.this, userId, new CompletionHandler() {
                @Override
                public void onCompleted(SendBirdException e) {
                    Log.d("lifecycle","spalsh onCompleted"+e);
                    if (e != null) {
                        Toast.makeText(SplashScreen.this, "Cannot Setup SyncManager", Toast.LENGTH_SHORT).show();
                        SendBirdSyncManager.getInstance().clearCache();
                        Intent intent = new Intent(SplashScreen.this, LoginRegistrationActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  progressLogin.dismiss();
    }



    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();


        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else
                return false;

        } else

            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit or Cancel to Enter to Buzzapp Without internet");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setContentView(R.layout.activity_splash_screen);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("sharedPrefsHelper","12"+sharedPrefManager.isLoggedIn());
                        if (sharedPrefManager.isLoggedIn()) {
                           // startLoginService(sharedPrefsHelper.getQbUser());
                            //     ();
                            startActivity(new Intent(SplashScreen.this, LoginRegistrationActivity.class));
                            finish();
                            return;
                        } else {
                            startActivity(new Intent(SplashScreen.this, LoginRegistrationActivity.class));
                            finish();
                        }


                    }
                }, 4000);
            }
        });

        return builder;
    }
}
