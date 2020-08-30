package com.example.myapplication.ModelClasses;


import android.app.Application;

import com.sendbird.android.SendBird;


//@ReportsCrashes(mailTo = "viveksingh0301@gmail.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text
//)
public class BaseApplication extends Application {
    private static BaseApplication mInstance;

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        SendBird.init("49D553F2-AD16-4747-B48A-BAB896480899", getApplicationContext());
        //ACRA.init(this);

    }
}
