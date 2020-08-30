package com.example.myapplication.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.myapplication.Activities.Main2Activity;
import com.example.myapplication.Database.DatabaseHelperSchedule;
import com.example.myapplication.ModelClasses.Search_result;
import com.example.myapplication.R;
import com.sendbird.android.ApplicationUserListQuery;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserListQuery;
import com.sendbird.android.UserMessage;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CheckOnlineStatusService extends Service {
    public final static String ACTION_USER_ONLINE_UPDATE = "android.intent.action.BUZZ_APP_ONLINE_STATUS";

    private Handler handler;
    private Runnable run;

    public CheckOnlineStatusService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("dialog", "service1");
        //databaseHelperSchedule=new DatabaseHelperSchedule(getBaseContext());
        if (intent != null) {
            String strUserId = intent.getStringExtra("user_id");
            if (strUserId != null) {
                handler = new Handler();
                run = new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<String> userIds = new ArrayList<>();
                        userIds.add(strUserId);

                        ApplicationUserListQuery applicationUserListQuery = SendBird.createApplicationUserListQuery();
                        applicationUserListQuery.setUserIdsFilter(userIds);
                        applicationUserListQuery.next(new UserListQuery.UserListQueryResultHandler() {
                            @Override
                            public void onResult(List<User> list, SendBirdException e) {
                                if (e != null) {    // Error.
                                    return;
                                }

                                Intent intent = new Intent();
                                intent.setAction(ACTION_USER_ONLINE_UPDATE);
                                intent.putExtra("connection_status", ""+list.get(0).getConnectionStatus());
                                sendBroadcast(intent);
                            }
                        });
                        handler.postDelayed(this, 5000);
                    }

                };
                handler.post(run);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
