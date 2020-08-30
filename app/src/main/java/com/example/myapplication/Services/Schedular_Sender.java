package com.example.myapplication.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.myapplication.Activities.Main2Activity;
import com.example.myapplication.Database.DatabaseHelperSchedule;
import com.example.myapplication.ModelClasses.Search_result;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Schedular_Sender extends Service
{
    private Handler handler;
    private Runnable run;
    DatabaseHelperSchedule databaseHelperSchedule;
    public Schedular_Sender() {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("dialog","service1");
        databaseHelperSchedule=new DatabaseHelperSchedule(getBaseContext());
        if(intent!=null){
            boolean start=intent.getBooleanExtra("start",false);
            if(start){
                handler=new Handler();
                run= new Runnable() {
                    @Override
                    public void run() {
                        Log.d("dialog","service2");
                        String timestamp=new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                        int month=Calendar.getInstance().get(Calendar.MONTH);
                        int date=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                        String month1=new DateFormatSymbols().getMonths()[month].substring(0,3).toLowerCase();
                        String datestamp=date+"-"+month1;

                        Log.d("dialog",""+timestamp+"-"+datestamp);
                        List<Search_result> data = new ArrayList<>();

                        data=databaseHelperSchedule.getData1();
                        List<Search_result> data1 = databaseHelperSchedule.getData(datestamp, timestamp);
                        String caller= SharedPrefManager.getInstance(getApplicationContext()).getUser().getCaller_id().toString();

                        String sendtoid=SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id().toString();
                        String name=SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_name().toString();
                        String imgurl= SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_image().toString();
                        Log.d("dialog",""+data+"-"+data1.size());
                        if( data1.size()>0) {

                            for (int i= 0; i < data1.size(); i++) {
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
                                Log.d("dialog", "12" + nameslist.size()+nameslist);
                                List<String> callerid = Arrays.asList(caller.split(","));
                                Log.d("dialog", "12" + callerid);
                                List<String> users1 = new ArrayList<>(callerid);
                                Log.d("dialog", "12" + users1+users1.size());
                                int finalI = i;
                                for(int k=0;k<nameslist.size();k++) {

                                    GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(true)
                                                    .addUserId(users1.get(k)) .setName(nameslist.get(k))
                                            , new GroupChannel.GroupChannelCreateHandler() {
                                                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                                    if (e == null) {
                                                        Intent intent = new Intent(Schedular_Sender.this, Main2Activity.class);
                                                        intent.putExtra("URL", groupChannel.getUrl());
                                                        Log.d("grp", "1" + groupChannel.getMembers());
                                                        Log.d("grp", "2" + SendBird.getCurrentUser().getFriendDiscoveryKey());
                                                        Log.d("grp", "3" + groupChannel);
                                                        Log.d("grp", "3" + SendBird.getCurrentUser().getUserId());
                                                        startActivity(intent);
                                                        Log.d("dialog", "12" + finalI);
                                                        groupChannel.sendUserMessage(data1.get(finalI).getMessage(), new BaseChannel.SendUserMessageHandler() {
                                                            @Override
                                                            public void onSent(UserMessage userMessage, SendBirdException e) {

                                                            }
                                                        });

                                                    }

                                                }
                                            });
                                }
                            }

                        }
                        handler.postDelayed(this,30000);
                    }

                };
                handler.post(run);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
