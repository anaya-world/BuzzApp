package com.example.myapplication.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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
import com.example.myapplication.Adapter.BirthdayGifAdapter;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.Intefaces.NoInternetListener;
import com.example.myapplication.ModelClasses.BirthdayGifModel;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Common;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Birthday_Gif_Activity extends AppCompatActivity {
    BirthdayGifAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<BirthdayGifModel> arrayList;
    Toolbar tbGIFToolbar;
    ImageView ivBack;
    ArrayList<FriendListModel> friendListModels;
    public static final int PERMISSION_WRITE = 0;

    String callId, nameToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday__gif_);

        tbGIFToolbar = findViewById(R.id.tb_gif);
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> onBackPressed());

        if(getIntent()==null){
            return;
        }
        callId = getIntent().getStringExtra("EXTRA_CALLER_ID");
        nameToSend = getIntent().getStringExtra("EXTRA_NAME");

        recyclerView = findViewById(R.id.birthday_gif_recycleer);
        if(Common.isConnected(Birthday_Gif_Activity.this)) {
            getAllBirthdayGif();
        } else {
            Common.showAlertOkayCallBack(Birthday_Gif_Activity.this,
                    getString(R.string.error_title_no_internet),
                    getString(R.string.error_no_internet_msg),
                    getString(android.R.string.ok), this::onBackPressed);
        }
    }

    private void getAllBirthdayGif() {
        Common.progressOn(Birthday_Gif_Activity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CommonUtils.baseUrl,
                (Response.Listener<String>) response -> {
                    try {
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(response);
                            if(jsonObject.getString("success").equals("true")) {
                                arrayList = new ArrayList<>();
                                JSONArray jsonArray = jsonObject.getJSONArray("search_result");
                                JSONArray jsonArrayGif = jsonObject.getJSONArray("gif");
                                for (int i = 0; i < jsonArrayGif.length(); i++) {
                                    String image = jsonArrayGif.getString(i);
                                    BirthdayGifModel birthdayGifModel = new BirthdayGifModel();
                                    birthdayGifModel.setImage(image);
                                    arrayList.add(birthdayGifModel);
                                }

                                adapter = new BirthdayGifAdapter(getApplicationContext(), arrayList,
                                        Birthday_Gif_Activity.this, new FriendListInterface() {
                                    @Override
                                    public void messageFriend(View view, int position) {
                                    }

                                    @Override
                                    public void unfriedFriends(View view, int position) {
                                    }

                                    @Override
                                    public void sendGif(View v, int adapterPosition) {
                                        Birthday_Gif_Activity.this.sendGif((BirthdayGifModel)
                                                Birthday_Gif_Activity.this.arrayList.get(adapterPosition));
                                    }
                                });
                                checkPermission();

                                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(adapter);
                                Common.progressOff();
                            } else {
                                Common.showAlertOkayCallBack(Birthday_Gif_Activity.this,
                                        "Alert", jsonObject.getString("message"),
                                        getResources().getString(android.R.string.ok),
                                        new NoInternetListener() {
                                            @Override
                                            public void onHandle() {
                                                onBackPressed();
                                            }
                                        });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Common.progressOff();
                            Common.showAlertOkayCallBack(Birthday_Gif_Activity.this,
                                    "Alert",
                                    "Something went wrong. Please try again later.",
                                    getResources().getString(android.R.string.ok),
                                    () -> onBackPressed());
                        }
                    } catch (Exception e) {
                        Common.progressOff();
                        e.printStackTrace();
                    }
                }, (Response.ErrorListener) error -> {
            Common.progressOff();
            Common.showAlertOkayCallBack(Birthday_Gif_Activity.this,
                    getString(R.string.error_title_alert),
                    getString(R.string.error_server_error),
                    getString(android.R.string.ok), this::onBackPressed);
        }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logparams = new HashMap<>();
                logparams.put("action", "BirthdayGif");
                logparams.put("user_id", SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id().toString());
                return logparams;
            }
        };
        MySingleTon.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
    }


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

    public void sendGif(BirthdayGifModel birthdayGifModel) {
        List<String> users = new ArrayList<>();
        users.add(callId/*birthdayGifModel.getCaller_id()*/);
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false)
                .setDistinct(true).addUserIds(users)
                .setName(nameToSend/*birthdayGifModel.getName()*/)
                .setCoverUrl(birthdayGifModel.getImg()), (groupChannel, e) -> {
                    if (e == null) {

                        String gif = (adapter.imagrurl()).getImage();

                        Intent intent = new Intent(Birthday_Gif_Activity.this, Main2Activity.class);
                        intent.putExtra("URL", groupChannel.getUrl());
                        intent.putExtra("gif", gif);
                        Birthday_Gif_Activity.this.startActivity(intent);
                        this.finish();
                    }
                });
    }


}