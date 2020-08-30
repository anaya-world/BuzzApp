package com.example.myapplication;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.Activities.LoginRegistrationActivity;
import com.example.myapplication.Activities.SplashScreen;
import com.example.myapplication.Services.MyFirebaseMessagingService;
import com.example.myapplication.Utils.ActivityUtils;
import com.example.myapplication.Utils.PreferenceUtils;
import com.example.myapplication.Utils.SharedPrefManager;
import com.example.myapplication.Utils.SyncManagerUtils;
import com.example.myapplication.call.CallActivity;
import com.github.tamir7.contacts.Contacts;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBirdPushHelper;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.handler.SendBirdCallListener;
import com.sendbird.syncmanager.SendBirdSyncManager;
import com.sendbird.syncmanager.handler.CompletionHandler;

import java.util.UUID;


//@ReportsCrashes(mailTo = "vinikum97@gmail.com", mode = ReportingInteractionMode.TOAST, resToastText = 2131689627)
public class App extends Application {
    public static final String TAG = "Lifecycle";
    private static App instance;
   public static final String APP_ID="35A602C1-6766-46FE-94BE-7553074C1011";
    private String someVariable;

    public static App getInstance() {
        return instance;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d(TAG,"onCreate");
        initApplication();
       // FirebaseApp.initializeApp(this);
        initfirebase1();
        initFirebase2();
        PreferenceUtils.init(getApplicationContext());
        SendBird.init(APP_ID, getApplicationContext());
        SendBirdPushHelper.registerPushHandler(new MyFirebaseMessagingService());
        SendBirdCall.init(getApplicationContext(),APP_ID);

        Contacts.initialize(this);
        if (SendBirdCall.init(this, APP_ID)) {
            SendBirdCall.removeAllListeners();
            SendBirdCall.addListener(UUID.randomUUID().toString(), new SendBirdCallListener() {
                @Override
                public void onRinging(DirectCall call) {
                    Log.d(TAG, "onRinging() => callId: " + call.getCallId());

                    if (CallActivity.sIsRunning) {
                        call.end();

                        return;
                    }
                    ActivityUtils.startCallActivityAsCallee(getApplicationContext(), call);
                }
            });
           // setUpSyncManager();
        }
    }

    private void initfirebase1() {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("buzzapp-d7b5a")
                .setApplicationId("1:418273794095:android:560455e46824e554730eda")
                .setApiKey("AIzaSyCx3SOXsP5v9ICYeBH7pRIJ2WYxPTUhgjQ")
                // setDatabaseURL(...)
                // setStorageBucket(...)
                .build();
        FirebaseApp.initializeApp(getApplicationContext(),options);
    }

    private void initFirebase2() {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("buzz-c3780")
                .setApplicationId("1:13107218831:android:8f9eab34b60b49f6cb63fa")
                .setApiKey("AIzaSyA5uyzpn9eKz5px8HNLkX-piY9mY7YDIfI")
                // setDatabaseURL(...)
                // setStorageBucket(...)
                .build();
        FirebaseApp.initializeApp(getApplicationContext(),options,"calls");
    }

    private void initApplication() {
        instance = this;
    }
    private void setUpSyncManager() {
        Log.d("lifecycle","spalsh"+SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id());
        if (SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id() != null) {
            String userId = String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id()).replaceAll("\\s", "");

            SyncManagerUtils.setup(getApplicationContext(), userId, new CompletionHandler() {
                @Override
                public void onCompleted(SendBirdException e) {
                    Log.d("lifecycle","spalsh onCompleted"+e);
                    if (e != null) {
                        Toast.makeText(getApplicationContext(), "Cannot Setup SyncManager", Toast.LENGTH_SHORT).show();
                        SendBirdSyncManager.getInstance().clearCache();
                        Intent intent = new Intent(getApplicationContext(), LoginRegistrationActivity.class);
                        startActivity(intent);
                        return;
                    }

                }
            });
        }
    }
}
