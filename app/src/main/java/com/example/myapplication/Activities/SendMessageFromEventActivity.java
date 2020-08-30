package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Database.DatabaseHelperSchedule;
import com.example.myapplication.Fragments.BirthdaySelectedUser;
import com.example.myapplication.Fragments.EventSelectedUserFragment;
import com.example.myapplication.Fragments.SelectDistinctFragment;
import com.example.myapplication.ModelClasses.Search_result;
import com.example.myapplication.ModelClasses.Send_data;
import com.example.myapplication.R;
import com.example.myapplication.Services.MyBroadCastSchedularReceiver;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import com.google.android.material.textfield.TextInputEditText;
import com.kevalpatel2106.emoticongifkeyboard.EmoticonGIFKeyboardFragment;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.Emoticon;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.EmoticonSelectListener;
import com.kevalpatel2106.emoticongifkeyboard.gifs.Gif;
import com.kevalpatel2106.emoticongifkeyboard.gifs.GifSelectListener;
import com.kevalpatel2106.emoticonpack.android8.Android8EmoticonProvider;
import com.kevalpatel2106.gifpack.giphy.GiphyGifProvider;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SendMessageFromEventActivity extends AppCompatActivity implements EventSelectedUserFragment.UsersSelectedListener, SelectDistinctFragment.DistinctSelectedListener, View.OnClickListener {
    public static final String EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL";
    static final int STATE_SELECT_DISTINCT = 1;
    static final int STATE_SELECT_USERS = 0;
    ImageView chat_backarrow;
    private boolean enableCreate = false;
    TextView festival_name;
    TextView festival_type;
    FrameLayout groupframe, key;
    ImageView iv_timezone;
    private Button mCreateButton;
    private int mCurrentState;
    private boolean mIsDistinct = true;
    private TextInputEditText mNameEditText;
    private Button mNextButton;
    private List<String> mSelectedIds;
    private Toolbar mToolbar;
    EditText message_content;
    EmoticonGIFKeyboardFragment emoticonGIFKeyboardFragment = null;
    TextView scheduleer_at_time;
    TextView send_gif;
    ImageView send_message, gifIMg;
    TextView send_message_scheduled;
    RecyclerView share_with_recyclerview;
    TextView tv_date;
    TextView tv_month;
    private String userNameS, date, month, calleridS, check, scheduler, message, time1, fullDate, sendto, gifURl;

//    AlarmManager alarmManager;
//    PendingIntent pendingIntent;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_from_event);
        Intent intent = getIntent();
        check = intent.getStringExtra("check");
        this.chat_backarrow = (ImageView) findViewById(R.id.chat_backarrow);
        this.iv_timezone = (ImageView) findViewById(R.id.iv_timezone);
        this.scheduleer_at_time = (TextView) findViewById(R.id.scheduleer_at_time);
        this.tv_date = (TextView) findViewById(R.id.tv_date);
        this.tv_month = (TextView) findViewById(R.id.tv_month);
        message_content = findViewById(R.id.message_conten);
        this.festival_name = (TextView) findViewById(R.id.festival_name);
        this.festival_type = (TextView) findViewById(R.id.festival_type);
        //this.send_message = (ImageView) findViewById(R.id.send_message);
        this.send_gif = (TextView) findViewById(R.id.send_gif);
        this.key = findViewById(R.id.key);
        this.gifIMg = findViewById(R.id.gifImg);

//        //Init Scheduler service.
//        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent alarmIntent = new Intent(this, MyBroadCastSchedularReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        this.send_message_scheduled = (TextView) findViewById(R.id.send_message_scheduled);
        send_message_scheduled.setOnClickListener(SendMessageFromEventActivity.this);
        iv_timezone.setOnClickListener(this);

        send_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick", "" + key.getVisibility());
                if (key.getVisibility() == View.VISIBLE) {
                    key.setVisibility(View.GONE);
                    return;
                }

                key.setVisibility(View.VISIBLE);

                EmoticonGIFKeyboardFragment.EmoticonConfig emoticonConfig = new EmoticonGIFKeyboardFragment.EmoticonConfig()
                        .setEmoticonProvider(Android8EmoticonProvider.create())
                        /*
                          NOTE: The process of removing last character when user preses back space will handle
                          by library if your edit text is in focus.
                         */
                        .setEmoticonSelectListener(new EmoticonSelectListener() {

                            @Override
                            public void emoticonSelected(Emoticon emoticon) {
                                //Do something with new emoticon.
                            }

                            @Override
                            public void onBackSpace() {

                            }


                        });
                EmoticonGIFKeyboardFragment.GIFConfig gifConfig = new EmoticonGIFKeyboardFragment

        /*
          Here we are using GIPHY to provide GIFs. Create Giphy GIF provider by passing your key.
          It is required to set GIF provider before adding fragment into container.
         */
                        .GIFConfig(GiphyGifProvider.create(SendMessageFromEventActivity.this, "564ce7370bf347f2b7c0e4746593c179"))
                        .setGifSelectListener(new GifSelectListener() {
                            @Override
                            public void onGifSelected(@NonNull Gif gif) {
                                //Do something with the selected GIF.
                                Log.d("tag", "onGifSelected: " + gif.getGifUrl());

                                key.setVisibility(View.GONE);
                                gifIMg.setVisibility(View.VISIBLE);
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.placeholder(R.drawable.profile_img);
                                requestOptions.error(R.drawable.profile_dummy);
                                Log.d("array", "a" + gif.getGifUrl());
                                Glide.with(SendMessageFromEventActivity.this)
                                        .setDefaultRequestOptions(requestOptions)
                                        .load(gif.getGifUrl().trim()).into(gifIMg);
                                gifURl = gif.getGifUrl();

                            }
                        });


                emoticonGIFKeyboardFragment = EmoticonGIFKeyboardFragment
                        .getNewInstance(findViewById(R.id.key), emoticonConfig, gifConfig);

//Adding the keyboard fragment to keyboard_container.
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.key, emoticonGIFKeyboardFragment.getNewInstance(v, emoticonConfig, gifConfig)).commit();
            }

        });

        this.chat_backarrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SendMessageFromEventActivity.this.finish();
            }
        });
        this.share_with_recyclerview = (RecyclerView) findViewById(R.id.share_with_recyclerview);
        this.mSelectedIds = new ArrayList();

        userNameS = intent.getStringExtra("userName");
        month = intent.getStringExtra("month").toLowerCase();
        date = intent.getStringExtra("date");
        calleridS = intent.getStringExtra("send_to_callerid");
        if (check.equals("0")) {

            festival_name.setText(userNameS);
            tv_month.setText(month);
            tv_date.setText(date);
            festival_type.setText("BirthDay");

            message = "Wish u a Very Happy BirthDay.!!";
            //message_content.setText(message);
            scheduler = "Your message will be sent out at: " + intent.getStringExtra("date") + "-" +
                    intent.getStringExtra("month");
            scheduleer_at_time.setText(scheduler);
            sendto = intent.getStringExtra("send_to_callerid");
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_create_group_channel, BirthdaySelectedUser.newinstance(intent.getStringExtra("userName"), intent.getStringExtra("img_url"))).commit();

            }


        } else if (check.equals("1")) {
            festival_name.setText(userNameS);
            tv_month.setText(month);
            tv_date.setText(date);
            festival_type.setText("Anniversary");

            message = "Wish u a Very Happy Anniversary.!!";
            // message_content.setText(message);
            scheduler = "Your message will be sent out at: " + intent.getStringExtra("date") + "-" +
                    intent.getStringExtra("month");
            scheduleer_at_time.setText(scheduler);
            sendto = intent.getStringExtra("send_to_callerid");
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_create_group_channel, BirthdaySelectedUser.newinstance(intent.getStringExtra("userName"), intent.getStringExtra("img_url"))).commit();

            }

        } else if (check.equals("2")) {
            festival_name.setText(userNameS);
            tv_month.setText(month);
            tv_date.setText(date);
            festival_type.setText("Festival-" + userNameS);

            message = "Wish u a Very Happy " + userNameS + "!!";
            // message_content.setText(message);
            scheduler = "Your message will be sent out at: " + intent.getStringExtra("date") + "-" +
                    intent.getStringExtra("month");
            scheduleer_at_time.setText(scheduler);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_create_group_channel, new EventSelectedUserFragment()).commit();
            }

        } else {
            sendto = intent.getStringExtra("send_to_callerid");
            festival_name.setText(userNameS);
            festival_type.setText(userNameS);
            scheduler = "Your message will be sent out at: ";
            scheduleer_at_time.setText(scheduler);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_create_group_channel, EventSelectedUserFragment.newinstance(intent.getStringExtra("send_to_callerid"), intent.getStringExtra("img_url"), "1")).commit();
            }
        }

    }

    private void schedule_Api() {
        Log.d("selecetedids", "3" + gifURl);
        String message1 = message_content.getText().toString();
        if (check.equals("2")) {
            for (int i = 0; i < mSelectedIds.size(); i++) {
                Log.d("selecetedids", "1" + this.mSelectedIds);

            }
            Log.d("selecetedids", "1" + this.mSelectedIds);
            sendto = TextUtils.join(",", mSelectedIds);
        }/* else if (check.equals("3")) {
            for (int i = 0; i < mSelectedIds.size(); i++) {
                Log.d("selecetedids", "1" + this.mSelectedIds);

            }

            Log.d("selecetedids", "1" + this.mSelectedIds);
            sendto = TextUtils.join(",", mSelectedIds);
            Log.d("selecetedids", "1" + this.sendto);
        }*/
        if (gifURl == null) {
            gifURl = "";
        }


        fullDate = date + "-" + month;


        if (sendto == null) {
            Toast.makeText(this, "Select the friends to set Schedule!", Toast.LENGTH_SHORT).show();
        } else if (time1 == null) {
            Toast.makeText(this, "Select time for the Schedule!", Toast.LENGTH_SHORT).show();
        } else if (message_content.getText().toString() == null) {
            Toast.makeText(this, "Enter Message Content!", Toast.LENGTH_SHORT).show();
        } else {

            Log.d("selecetedids", "" + sendto);
            StringRequest r2 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(SendMessageFromEventActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        Log.d("jsonObject", "" + jsonObject.getString("success"));
                        Log.d("jsonObject", "1" + gifURl);
                        Log.d("msg", "1" + message_content.getText().toString());
                        Log.d("jsonObject", "1" + SharedPrefManager.getInstance(SendMessageFromEventActivity.this).getUser().getUser_id());
                        Log.d("fest", "1" + festival_type.getText().toString());
                        if (jsonObject.getString("success").equals("true")) {
                            Toast.makeText(SendMessageFromEventActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            getAllSchduleAndUpdateDatabase();
                            //finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SendMessageFromEventActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }) {
                /* access modifiers changed from: protected */
                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> logParams = new HashMap<>();
                    logParams.put("festival_type", festival_type.getText().toString());
                    logParams.put("date", fullDate);
                    logParams.put("message", message1);
                    logParams.put("userid", SharedPrefManager.getInstance(SendMessageFromEventActivity.this).getUser().getUser_id());
                    logParams.put("time", time1);
                    logParams.put("sendTo", sendto);
                    logParams.put("gif", gifURl);
                    logParams.put("action", "create_schedule");
                    return logParams;
                }
            };
            MySingleTon.getInstance(SendMessageFromEventActivity.this).addToRequestQue(r2);
        }
    }

    public void getAllSchduleAndUpdateDatabase() {
        final StringRequest r2 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
//                sch_progress.setVisibility(View.GONE);
//                recyclerView.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("gif", "2" + response);
                    if (jsonObject.getString("success").equals("true")) {

                        JSONArray search = jsonObject.getJSONArray("search_result");
                        JSONArray sendUser = jsonObject.getJSONArray("sendUser");
                        for (int i = 0; i < search.length(); i++) {
                            Search_result search_result = new Search_result();
                            JSONObject jsonObject1 = search.getJSONObject(i);
                           /* search_result.setDate(jsonObject1.getString("date"));
                            search_result.setFestivalType(jsonObject1.getString("festival_type"));
                            search_result.setId(jsonObject1.getString("id"));
                            search_result.setMessage(jsonObject1.getString("message"));
                            search_result.setScheduleId(jsonObject1.getString("schedule_id"));
                            search_result.setTime(jsonObject1.getString("time"));
                            search_result.setSendTo(jsonObject1.getString("sendTo"));
                            search_result.setUserid(jsonObject1.getString("userid"));
                            search_result.setMobileno(jsonObject1.getString("mobileno"));
                            search_result.setGif(jsonObject1.getString("gif"));
                            Log.d("gif", "2" + jsonObject1.getString("gif"));*/
                            JSONArray elem = sendUser.getJSONArray(i);


                            ArrayList<Send_data> send_datas = new ArrayList<>();
                            Send_data send_data = null;
                            String imgurl = null;
                            String names;
                            for (int y = 0; y < elem.length(); y++) {
                                send_data = new Send_data();
                                JSONObject obj = elem.getJSONObject(y);
                                send_data.setPerentScheduleId(obj.getString("perent_schedule_id"));
                                send_data.setName(obj.getString("name"));
                                send_data.setUserImg(obj.getString("user_img"));
                                send_data.setId(obj.getString("id"));
                                send_datas.add(send_data);

                            }

                            ArrayList arrayList = new ArrayList();
                            ArrayList sendid = new ArrayList();
                            String sendtoids;
                            String schedule_id = jsonObject1.getString("schedule_id");
                            Log.d("array", "" + send_datas.size());
                            for (int j = 0; j < send_datas.size(); j++) {
                                String parent_id = send_datas.get(j).getPerentScheduleId();
                                Log.d("array", "a" + parent_id);
                                if (parent_id.equals(schedule_id)) {
                                    arrayList.add(send_datas.get(j).getName());
                                    sendid.add(send_datas.get(j).getId());
                                    Log.d("array", "a" + send_datas.get(j).getUserImg());
                                    imgurl = send_datas.get(j).getUserImg();
                                    Log.d("array", "a" + imgurl);
                                }
                            }
                            names = TextUtils.join(",", arrayList);
                            sendtoids = TextUtils.join(",", sendid);

                            search_result.setSend_data(send_datas);
                            Random random = new Random(System.nanoTime());
                            int randomInt = random.nextInt(1000000000);
                            DatabaseHelperSchedule databaseHelperSchedule = new DatabaseHelperSchedule(SendMessageFromEventActivity.this);

                            databaseHelperSchedule.InsertData(randomInt,
                                    jsonObject1.getString("schedule_id")/*searchResults.get(position).getScheduleId()*/,
                                    jsonObject1.getString("festival_type")/*searchResults.get(position).getFestivalType()*/,
                                    jsonObject1.getString("sendTo")/*searchResults.get(position).getSendTo()*/,
                                    jsonObject1.getString("date")/*searchResults.get(position).getDate()*/,
                                    jsonObject1.getString("message")/*searchResults.get(position).getMessage()*/,
                                    sendtoids,
                                    jsonObject1.getString("gif"),
                                    jsonObject1.getString("time")/*searchResults.get(position).getTime()*/,
                                    names,
                                    jsonObject1.getString("mobileno")/*searchResults.get(position).getMobileno()*/,
                                    imgurl,
                                    0 //Not sent yet
                            );
                        }
                        //startSchedularService();
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("userid", SharedPrefManager.getInstance(SendMessageFromEventActivity.this).getUser().getUser_id());
                logParams.put("action", "get_schedule");
                return logParams;
            }
        };
        MySingleTon.getInstance(SendMessageFromEventActivity.this).addToRequestQue(r2);
    }

    public void onUserSelected(boolean selected, String userId) {
        if (selected) {
            this.mSelectedIds.add(userId);
        } else {
            this.mSelectedIds.remove(userId);
        }
        this.mSelectedIds.size();

    }

    public void onDistinctSelected(boolean distinct) {
        this.mIsDistinct = distinct;
    }

    public void createGroupChannel(List<String> userIds, boolean distinct) {
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(false).addUserIds(userIds).setName(this.mNameEditText.getText().toString()), new GroupChannel.GroupChannelCreateHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e == null) {
                    Intent intent = new Intent();
                    intent.putExtra("EXTRA_NEW_CHANNEL_URL", groupChannel.getUrl());
                    SendMessageFromEventActivity.this.setResult(-1, intent);
                    SendMessageFromEventActivity.this.finish();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message_scheduled:
                schedule_Api();
                break;

            case R.id.iv_timezone:

                if (check.equals("3")) {
                    month = "";
                    date = "";
                    fullDate = "";
                    time1 = "";
                    scheduler = "Your message will be sent out at: ";
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("Timer");
                    alertDialog.setMessage("Enter time to set Timer..");

                    LinearLayout lp = new LinearLayout(this);
                    lp.setOrientation(LinearLayout.VERTICAL);
                    lp.setGravity(Gravity.CENTER);
                    final EditText time = new EditText(this);
                    final EditText date1 = new EditText(this);
                    lp.addView(time);
                    lp.addView(date1);
                    time.setGravity(Gravity.CENTER);
                    time.setHint("Enter Time");

                    date1.setGravity(Gravity.CENTER);
                    date1.setHint("Enter Date");
                    alertDialog.setView(lp);

                    alertDialog.setIcon(R.drawable.ic_alarm_black_24dp);
                    date1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar c1 = Calendar.getInstance();
                            int dt = c1.get(Calendar.DAY_OF_MONTH);
                            int mn = c1.get(Calendar.MONTH);
                            int yy = c1.get(Calendar.YEAR);
                            DatePickerDialog datePickerDialog = new DatePickerDialog(SendMessageFromEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month1, int dayOfMonth) {
                                    month = new DateFormatSymbols().getMonths()[month1].substring(0, 3).toLowerCase();

                                    date = String.valueOf(dayOfMonth);
                                    fullDate = date + "-" + month;
                                    date1.setText(fullDate);


                                }
                            }, yy, mn, dt);
                            datePickerDialog.show();

                        }
                    });
                    time.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get Current Time
                            final Calendar c = Calendar.getInstance();
                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                            int mMinute = c.get(Calendar.MINUTE);

                            // Launch Time Picker Dialog
                            TimePickerDialog timePickerDialog = new TimePickerDialog(SendMessageFromEventActivity.this,
                                    new TimePickerDialog.OnTimeSetListener() {

                                        @SuppressLint({"SetTextI18n", "DefaultLocale"})
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay,
                                                              int minute) {
//                                            if (hourOfDay < 12) {
                                            String min;
                                            if (minute < 10) {
                                                min = "0" + minute;
                                            } else {
                                                min = String.valueOf(minute);
                                            }

                                            String strHour = "" + hourOfDay;

                                            time.setText(String.format("%02d:%02d", hourOfDay, minute));
//                                            } else {
//                                                time.setText((hourOfDay - 12) + ":" + minute + " PM");
//                                            }

                                        }
                                    }, mHour, mMinute, true);
                            timePickerDialog.show();
                        }
                    });
                    date1.setKeyListener(null);
                    time.setKeyListener(null);
                    alertDialog.setPositiveButton("Set",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    time1 = time.getText().toString();
                                    if (time1.equals("")) {
                                        Toast.makeText(SendMessageFromEventActivity.this, "Please Enter time...!", Toast.LENGTH_SHORT).show();

                                    } else if (month.equals("")) {

                                        Toast.makeText(SendMessageFromEventActivity.this, "Please Enter Date...", Toast.LENGTH_SHORT).show();
                                    } else {


                                        scheduler = scheduler + " " + fullDate + " " + time1;
                                        scheduleer_at_time.setText(scheduler);
                                        tv_month.setText(month);
                                        tv_date.setText(date);
                                        //   BirthdayAdapter.this.friendListInterface.Schedule(adpPos,date1,time1);
                                    }
                                }
                            });

                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                } else {
                    time1 = "";
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("Timer");
                    alertDialog.setMessage("Enter time to set Timer..");

                    LinearLayout lp = new LinearLayout(this);
                    lp.setOrientation(LinearLayout.VERTICAL);
                    lp.setGravity(Gravity.CENTER);
                    final EditText time = new EditText(this);

                    lp.addView(time);
                    time.setGravity(Gravity.CENTER);
                    time.setHint("Enter Time");
                    alertDialog.setView(lp);

                    alertDialog.setIcon(R.drawable.ic_alarm_black_24dp);
                    time.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get Current Time
                            final Calendar c = Calendar.getInstance();
                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                            int mMinute = c.get(Calendar.MINUTE);

                            // Launch Time Picker Dialog
                            TimePickerDialog timePickerDialog = new TimePickerDialog(SendMessageFromEventActivity.this,
                                    new TimePickerDialog.OnTimeSetListener() {

                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay,
                                                              int minute) {
//                                            if (hourOfDay < 12) {
                                            String min;
                                            if (minute < 10) {
                                                min = "0" + minute;
                                            } else {
                                                min = String.valueOf(minute);
                                            }
                                            time.setText(hourOfDay + ":" + min);
//                                            } else {
//                                                time.setText((hourOfDay - 12) + ":" + minute + " PM");
//                                            }

                                        }
                                    }, mHour, mMinute, true);
                            timePickerDialog.show();
                        }
                    });
                    alertDialog.setPositiveButton("Set",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    time1 = time.getText().toString();
                                    scheduler = scheduler + " " + time1;
                                    scheduleer_at_time.setText(scheduler);
                                    //   BirthdayAdapter.this.friendListInterface.Schedule(adpPos,date1,time1);

                                }
                            });

                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                }

        }
    }

//    private void startSchedularService() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
//        } else {
//            alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
//        }
//        finish();
//    }
}
