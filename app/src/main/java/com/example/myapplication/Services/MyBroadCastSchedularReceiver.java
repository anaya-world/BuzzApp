package com.example.myapplication.Services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.myapplication.Database.DatabaseHelperSchedule;
import com.example.myapplication.ModelClasses.Search_result;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MyBroadCastSchedularReceiver extends BroadcastReceiver {

    DatabaseHelperSchedule databaseHelperSchedule;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
         /*   Intent serviceIntent = new Intent(context, MySchedularService.class);
            context.startService(serviceIntent);*/
            Intent mIntent = new Intent(context, MySchedularService.class);
            MySchedularService.enqueueWork(context, mIntent);

        } else {
            databaseHelperSchedule = new DatabaseHelperSchedule(context);

            Log.d("dialog", "service2");
            String timestamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            String month1 = new DateFormatSymbols().getMonths()[month].substring(0, 3).toLowerCase();
            String datestamp = date + "-" + month1;

            Log.d("dialog", "" + timestamp + "-" + datestamp);
            List<Search_result> data = new ArrayList<>();

            data = databaseHelperSchedule.getData1();
            List<Search_result> data1 = databaseHelperSchedule.getData(datestamp, timestamp);
            String caller = SharedPrefManager.getInstance(context).getUser().getCaller_id().toString();

            String sendtoid = SharedPrefManager.getInstance(context).getUser().getUser_id().toString();
            String name = SharedPrefManager.getInstance(context).getUser().getUser_name().toString();
            String imgurl = SharedPrefManager.getInstance(context).getUser().getUser_image().toString();
            Log.d("dialog", "" + data + "-" + data1.size());
            //Log.i("data1", "Outside data1.size() => " + data1.size());
            if (data1.size() > 0) {
           //     Log.i("data1", "inside data1.size() => " + data1.size());
                for (int i = 0; i < data1.size(); i++) {
                    Log.i("data1", "name => " + name);
                    caller = data1.get(i).getSendTo();
                    sendtoid = data1.get(i).getSendtoid();
                    name = data1.get(i).getSecrateId();
                    imgurl = data1.get(i).getUserImg();
                    Log.d("dialog", caller + "" + sendtoid);

                    List<String> sendid = Arrays.asList(sendtoid.split(","));
                    List<String> users = new ArrayList<>(sendid);
                    Log.d("dialog", "12" + users.size());
                    List<String> names = Arrays.asList(name.split(","));
                    List<String> nameslist = new ArrayList<>(names);
                    Log.d("dialog", "12" + nameslist.size() + nameslist);
                    List<String> callerid = Arrays.asList(caller.split(","));
                    Log.d("dialog", "12" + callerid);
                    List<String> users1 = new ArrayList<>(callerid);
                    Log.d("dialog", "12" + users1 + users1.size());
                    int finalI = i;
                    if (data1.get(finalI).getHasMsgSent() == 0) {
                        for (int k = 0; k < nameslist.size(); k++) {

                            GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(true)
                                            .addUserId(users1.get(k)).setName(nameslist.get(k))
                                    , new GroupChannel.GroupChannelCreateHandler() {
                                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                            if (e == null) {
                                            /*Intent intent = new Intent(context, Main2Activity.class);
                                            intent.putExtra("URL", groupChannel.getUrl());
                                            Log.d("grp", "1" + groupChannel.getMembers());
                                            Log.d("grp", "2" + SendBird.getCurrentUser().getFriendDiscoveryKey());
                                            Log.d("grp", "3" + groupChannel);
                                            Log.d("grp", "3" + SendBird.getCurrentUser().getUserId());
                                            context.startActivity(intent);
                                            Log.d("dialog", "12" + finalI);*/
                                                groupChannel.sendUserMessage(data1.get(finalI).getMessage(), new BaseChannel.SendUserMessageHandler() {
                                                    @Override
                                                    public void onSent(UserMessage userMessage, SendBirdException e) {
                                                        Log.e("Nassa", "userMessage" + userMessage.toString());
                                                        //Intent serviceIntent = new Intent(context, MySchedularService.class);
                                                        // context.startService(serviceIntent);
                                                        databaseHelperSchedule.UpdateData(data1.get(finalI).getScheduleId());
                                                    }
                                                });

                                            }

                                        }
                                    });
                        }
                    }
                }

            }
            if (!isMyServiceRunning(context)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // context.startForegroundService(new Intent(context, MySchedularService.class));
                    Intent mIntent = new Intent(context, MySchedularService.class);
                    MySchedularService.enqueueWork(context, mIntent);
                } else {
                    context.startService(new Intent(context, MySchedularService.class));
                }
            }
//            Intent serviceIntent = new Intent(context, MySchedularService.class);
//            context.startService(serviceIntent);
       /*     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               // context.startForegroundService(new Intent(context, MySchedularService.class));
                Intent mIntent = new Intent(context, MySchedularService.class);
                MySchedularService.enqueueWork(context, mIntent);
            } else {
                context.startService(new Intent(context, MySchedularService.class));
            }*/
        }

    }

    private boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : Objects.requireNonNull(manager).
                getRunningServices(Integer.MAX_VALUE)) {
            if (MySchedularService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
