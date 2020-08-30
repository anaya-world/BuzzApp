package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Fragments.EventSelectedUserFragment;
import com.example.myapplication.Fragments.SelectDistinctFragment;
import com.example.myapplication.Fragments.SendGifFragment;
import com.example.myapplication.R;
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

import java.util.ArrayList;
import java.util.List;

public class SendGifActivity extends AppCompatActivity implements EventSelectedUserFragment.UsersSelectedListener, SelectDistinctFragment.DistinctSelectedListener, View.OnClickListener {
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
    public static String gifURL;
    private String userNameS, date, month, calleridS, check, scheduler, message, time1, fullDate, sendto, gifURl;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_gif);
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

        this.send_message_scheduled = (TextView) findViewById(R.id.send_message_scheduled);
        send_message_scheduled.setOnClickListener(SendGifActivity.this);
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
                        .GIFConfig(GiphyGifProvider.create(SendGifActivity.this, "564ce7370bf347f2b7c0e4746593c179"))
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
                                Glide.with(SendGifActivity.this)
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
                SendGifActivity.this.finish();
            }
        });
        this.share_with_recyclerview = (RecyclerView) findViewById(R.id.share_with_recyclerview);
        this.mSelectedIds = new ArrayList();

        userNameS = intent.getStringExtra("userName");
        month = intent.getStringExtra("month").toLowerCase();
        date = intent.getStringExtra("date");
        calleridS = intent.getStringExtra("send_to_callerid");
        gifURL = intent.getStringExtra("gif");
        if (gifURL != null) {
            gifIMg.setVisibility(View.VISIBLE);
            Glide.with(SendGifActivity.this).asGif()
                    .load(gifURL)
                    .into(gifIMg);
        }

        if (check.equals("2")) {
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
                getSupportFragmentManager().beginTransaction().replace(R.id.container_create_group_channel, new SendGifFragment()).commit();
            }

        }
//        else {
//            festival_name.setText(userNameS);
//            festival_type.setText(userNameS);
//            scheduler ="Your message will be sent out at: ";
//            scheduleer_at_time.setText(scheduler);
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction().replace(R.id.container_create_group_channel, EventSelectedUserFragment.newinstance(intent.getStringExtra("send_to_callerid"),intent.getStringExtra("img_url"),"1")).commit();
//            }
        //   }

    }

//    private void schedule_Api() {
//        Log.d("selecetedids","3"+gifURl);
//        String message1=message_content.getText().toString();
//        if(check.equals("2")){
//            for(int i=0;i<mSelectedIds.size();i++) {
//                Log.d("selecetedids","1"+this.mSelectedIds);
//
//            }
//            Log.d("selecetedids","1"+this.mSelectedIds);
//            sendto= TextUtils.join(",",mSelectedIds);
//        }
//        else if(check.equals("3")){
//            for(int i=0;i<mSelectedIds.size();i++) {
//                Log.d("selecetedids","1"+this.mSelectedIds);
//
//            }
//            Log.d("selecetedids","1"+this.mSelectedIds);
//            sendto= TextUtils.join(",",mSelectedIds);
//            Log.d("selecetedids","1"+this.sendto);
//        }
//        if (gifURl == null) {
//            gifURl="";
//        }
//
//
//        fullDate=date+"-"+month;
//
//
//        if(sendto==null){
//            Toast.makeText(this, "Select the friends to set Schedule!", Toast.LENGTH_SHORT).show();
//        }
//        else if(time1==null){
//            Toast.makeText(this, "Select time for the Schedule!", Toast.LENGTH_SHORT).show();
//        }
//        else if(message_content.getText().toString()==null){
//            Toast.makeText(this, "Enter Message Content!", Toast.LENGTH_SHORT).show();
//        }
//
//        else{
//
//            Log.d("selecetedids",""+sendto);
//            StringRequest r2 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
//                public void onResponse(String response) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(response);
//                        Toast.makeText(GifSendActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                        Log.d("jsonObject",""+jsonObject.getString("success"));
//                        Log.d("jsonObject","1"+gifURl);
//                        Log.d("msg","1"+message_content.getText().toString());
//                        Log.d("jsonObject","1"+ SharedPrefManager.getInstance(GifSendActivity.this).getUser().getUser_id());
//                        Log.d("fest","1"+festival_type.getText().toString());
//                        if(jsonObject.getString("success").equals("true")) {
//                            Toast.makeText(GifSendActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(GifSendActivity.this, "", Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                /* access modifiers changed from: protected */
//                public Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> logParams = new HashMap<>();
//                    logParams.put("festival_type",festival_type.getText().toString());
//                    logParams.put("date",fullDate);
//                    logParams.put("message",message1);
//                    logParams.put("userid", SharedPrefManager.getInstance(GifSendActivity.this).getUser().getUser_id());
//                    logParams.put("time", time1);
//                    logParams.put("sendTo",sendto);
//                    logParams.put("gif",gifURl);
//                    logParams.put("action","create_schedule");
//                    return logParams;
//                }
//            };
//            MySingleTon.getInstance(GifSendActivity.this).addToRequestQue(r2);
//        }
    //  }

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
                    SendGifActivity.this.setResult(-1, intent);
                    SendGifActivity.this.finish();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message_scheduled:
//                schedule_Api();
//                break;
//            case R.id.iv_timezone:
//
//                if(check.equals("3")) {
//                    month="";
//                    date="";
//                    fullDate="";
//                    time1="";
//                    scheduler ="Your message will be sent out at: ";
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//                    alertDialog.setTitle("Timer");
//                    alertDialog.setMessage("Enter time to set Timer..");
//
//                    LinearLayout lp = new LinearLayout(this);
//                    lp.setOrientation(LinearLayout.VERTICAL);
//                    lp.setGravity(Gravity.CENTER);
//                    final EditText time = new EditText(this);
//                    final EditText date1 = new EditText(this);
//                    lp.addView(time);
//                    lp.addView(date1);
//                    time.setGravity(Gravity.CENTER);
//                    time.setHint("Enter Time");
//                    date1.setGravity(Gravity.CENTER);
//                    date1.setHint("Enter Date");
//                    alertDialog.setView(lp);
//
//                    alertDialog.setIcon(R.drawable.ic_alarm_black_24dp);
//                    date1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Calendar c1=Calendar.getInstance();
//                            int dt=c1.get(Calendar.DAY_OF_MONTH);
//                            int mn=c1.get(Calendar.MONTH);
//                            int yy=c1.get(Calendar.YEAR);
//                            DatePickerDialog datePickerDialog=new DatePickerDialog(GifSendActivity.this, new DatePickerDialog.OnDateSetListener() {
//                                @Override
//                                public void onDateSet(DatePicker view, int year, int month1, int dayOfMonth) {
//                                    month=new DateFormatSymbols().getMonths()[month1].substring(0,3).toLowerCase();
//
//                                    date=String.valueOf(dayOfMonth);
//                                    fullDate=date+"-"+month;
//                                    date1.setText(fullDate);
//
//
//                                }
//                            },yy,mn,dt);
//                            datePickerDialog.show();
//
//                        }
//                    });
//                    time.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            // Get Current Time
//                            final Calendar c = Calendar.getInstance();
//                            int mHour = c.get(Calendar.HOUR_OF_DAY);
//                            int mMinute = c.get(Calendar.MINUTE);
//
//                            // Launch Time Picker Dialog
//                            TimePickerDialog timePickerDialog = new TimePickerDialog(GifSendActivity.this,
//                                    new TimePickerDialog.OnTimeSetListener() {
//
//                                        @Override
//                                        public void onTimeSet(TimePicker view, int hourOfDay,
//                                                              int minute) {
////                                            if (hourOfDay < 12) {
//                                            String min;
//                                            if(minute<10){
//                                                min="0"+minute;
//                                            }
//                                            else{
//                                                min=String.valueOf(minute);
//                                            }
//                                            //new SimpleDateFormat("HH:mm").format(hourOfDay+":"+minute);
//                                            time.setText(hourOfDay + ":" + min );
////                                            } else {
////                                                time.setText((hourOfDay - 12) + ":" + minute + " PM");
////                                            }
//
//                                        }
//                                    }, mHour, mMinute, true);
//                            timePickerDialog.show();
//                        }
//                    });
//                    alertDialog.setPositiveButton("Set",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    time1 = time.getText().toString();
//                                    if(time1.equals("")){
//                                        Toast.makeText(GifSendActivity.this, "Please Enter time...!" , Toast.LENGTH_SHORT).show();
//
//                                    }
//                                    else if(month.equals("")){
//
//                                        Toast.makeText(GifSendActivity.this, "Please Enter Date...", Toast.LENGTH_SHORT).show();
//                                    }else {
//
//
//                                        scheduler = scheduler + " " + fullDate + " " + time1;
//                                        scheduleer_at_time.setText(scheduler);
//                                        tv_month.setText(month);
//                                        tv_date.setText(date);
//                                        //   BirthdayAdapter.this.friendListInterface.Schedule(adpPos,date1,time1);
//                                    }
//                                }
//                            });
//
//                    alertDialog.setNegativeButton("Cancel",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            });
//
//                    alertDialog.show();
//                }else{
//                    time1="";
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//                    alertDialog.setTitle("Timer");
//                    alertDialog.setMessage("Enter time to set Timer..");
//
//                    LinearLayout lp = new LinearLayout(this);
//                    lp.setOrientation(LinearLayout.VERTICAL);
//                    lp.setGravity(Gravity.CENTER);
//                    final EditText time = new EditText(this);
//
//                    lp.addView(time);
//                    time.setGravity(Gravity.CENTER);
//                    time.setHint("Enter Time");
//                    alertDialog.setView(lp);
//
//                    alertDialog.setIcon(R.drawable.ic_alarm_black_24dp);
//                    time.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            // Get Current Time
//                            final Calendar c = Calendar.getInstance();
//                            int mHour = c.get(Calendar.HOUR_OF_DAY);
//                            int mMinute = c.get(Calendar.MINUTE);
//
//                            // Launch Time Picker Dialog
//                            TimePickerDialog timePickerDialog = new TimePickerDialog(GifSendActivity.this,
//                                    new TimePickerDialog.OnTimeSetListener() {
//
//                                        @Override
//                                        public void onTimeSet(TimePicker view, int hourOfDay,
//                                                              int minute) {
////                                            if (hourOfDay < 12) {
//                                            String min;
//                                            if(minute<10){
//                                                min="0"+minute;
//                                            }
//                                            else{
//                                                min=String.valueOf(minute);
//                                            }
//                                            time.setText(hourOfDay + ":" + min );
////                                            } else {
////                                                time.setText((hourOfDay - 12) + ":" + minute + " PM");
////                                            }
//
//                                        }
//                                    }, mHour, mMinute, true);
//                            timePickerDialog.show();
//                        }
//                    });
//                    alertDialog.setPositiveButton("Set",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    time1 = time.getText().toString();
//                                    scheduler = scheduler + " " + time1;
//                                    scheduleer_at_time.setText(scheduler);
//                                    //   BirthdayAdapter.this.friendListInterface.Schedule(adpPos,date1,time1);
//
//                                }
//                            });
//
//                    alertDialog.setNegativeButton("Cancel",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            });
//
//                    alertDialog.show();
//                }
//
        }
    }
}
