package com.example.myapplication.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Adapter.SpecialDayGifAdapter;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.AnniversaryGifModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Common;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecialDaysGifActivity extends AppCompatActivity {
    SpecialDayGifAdapter specialDayGifAdapter;
    RecyclerView recyclerView;
    Toolbar tbGIFToolbar;
    ImageView ivBack;
    ArrayList<AnniversaryGifModel> arrayList;
    public static final int PERMISSION_WRITE = 0;

    int selectedPos;
    String intentMonth, intentFestivalName, intentDate, intentCheck, intentListPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_days_gif_);

        tbGIFToolbar = findViewById(R.id.tb_gif);
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> onBackPressed());

        if (getIntent() == null) {
            return;
        }
        intentFestivalName = getIntent().getStringExtra("userName");
        intentMonth = getIntent().getStringExtra("month");
        intentDate = getIntent().getStringExtra("date");
        intentCheck = getIntent().getStringExtra("check");
        intentListPosition = getIntent().getStringExtra("list_position");
        assert intentListPosition != null;
//        selectedPos = Integer.parseInt(intentListPosition);

        recyclerView = findViewById(R.id.anniversary_gif_recycleer);
        if (intentFestivalName.length() <= 0) {
            return;
        }
        if (Common.isConnected(SpecialDaysGifActivity.this)) {
            getAllGifImages();
        } else {
            Common.showAlertOkayCallBack(SpecialDaysGifActivity.this,
                    getString(R.string.error_title_no_internet),
                    getString(R.string.error_no_internet_msg),
                    getString(android.R.string.ok), this::onBackPressed);
        }
    }

    private void getAllGifImages() {
        Common.progressOn(SpecialDaysGifActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    arrayList = new ArrayList<>();
                    JSONObject outerObject = new JSONObject(response);
                    JSONArray jsonArray = outerObject.getJSONArray(intentFestivalName);//Get by festival name.
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String image = jsonArray.getString(i);
                        AnniversaryGifModel anniversaryGifModel = new AnniversaryGifModel();
                        anniversaryGifModel.setImage(image);
                        SpecialDaysGifActivity.this.arrayList.add(anniversaryGifModel);
                    }

                    SpecialDaysGifActivity.this.specialDayGifAdapter = new SpecialDayGifAdapter(getApplicationContext(), SpecialDaysGifActivity.this.arrayList, SpecialDaysGifActivity.this, new FriendListInterface() {
                        @Override
                        public void messageFriend(View view, int position) {
                        }

                        @Override
                        public void unfriedFriends(View view, int position) {
                        }

                        @Override
                        public void sendGif(View v, int adapterPosition) {
                            SpecialDaysGifActivity.this.sendGif((AnniversaryGifModel) SpecialDaysGifActivity.this.arrayList.get(adapterPosition));
                        }
                    });

                    checkPermission();
                    recyclerView.setLayoutManager(new GridLayoutManager(SpecialDaysGifActivity.this, 2));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(SpecialDaysGifActivity.this.specialDayGifAdapter);
                    Common.progressOff();
                } catch (Exception e) {
                    e.printStackTrace();
                    Common.progressOff();
                    onBackPressed();
                }
            }
        }, (Response.ErrorListener) error -> {
            Common.progressOff();
            Common.showAlertOkayCallBack(SpecialDaysGifActivity.this,
                    getString(R.string.error_title_alert),
                    getString(R.string.error_server_error),
                    getString(android.R.string.ok), this::onBackPressed);
        }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logparams = new HashMap<>();
                logparams.put("action", "SpacialDayGif");
                //logparams.put("user_id", SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id().toString());
                return logparams;
            }
        };
        MySingleTon.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
    }

    private void sendGif(AnniversaryGifModel anniversaryGifModel) {
        List<String> users = new ArrayList<>();
        users.add(anniversaryGifModel.getCaller_id());
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false)
                .setDistinct(true).addUserIds(users).setName(anniversaryGifModel.getName())
                .setCoverUrl(anniversaryGifModel.getImg()), new GroupChannel.GroupChannelCreateHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e == null) {
                    //TODO: Change intent activity.
                    String gif = (specialDayGifAdapter.imagrurl()).getImage();
                   /* Intent intent = new Intent(SpecialDaysGifActivity.this, Main2Activity.class);
                    intent.putExtra("URL", groupChannel.getUrl());
                    intent.putExtra("gif", gif);
                    SpecialDaysGifActivity.this.startActivity(intent);*/

                    Intent intent = new Intent(SpecialDaysGifActivity.this, SendGifActivity.class);
                    intent.putExtra("userName", intentFestivalName);//festival name
                    intent.putExtra("month", intentMonth);
                    intent.putExtra("date", intentDate);
                    intent.putExtra("check", "2");
                    intent.putExtra("gif", gif);
                    startActivity(intent);
                }
            }
        });
    }

    //  runtime storage permission
    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_WRITE);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //do somethings
        }
    }
}