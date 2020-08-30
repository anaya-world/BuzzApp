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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Adapter.AnniversaryGifAdapter;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.AnniversaryGifModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Common;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Anniversary_Gif_Activity extends AppCompatActivity {
    AnniversaryGifAdapter Anniver;
    RecyclerView recyclerView;
    Toolbar tbGIFToolbar;
    ImageView ivBack;
    ArrayList<AnniversaryGifModel> arrayList;
    public static final int PERMISSION_WRITE = 0;

    String callId, nameToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anniversary__gif_);

        tbGIFToolbar = findViewById(R.id.tb_gif);
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> onBackPressed());

        if(getIntent()==null){
            return;
        }
        callId = getIntent().getStringExtra("EXTRA_CALLER_ID");
        nameToSend = getIntent().getStringExtra("EXTRA_NAME");

        recyclerView = findViewById(R.id.anniversary_gif_recycleer);
        if (Common.isConnected(Anniversary_Gif_Activity.this)) {
            getAllAnniversaryGifs();
        } else {
            Common.showAlertOkayCallBack(Anniversary_Gif_Activity.this,
                    getString(R.string.error_title_no_internet),
                    getString(R.string.error_no_internet_msg),
                    getString(android.R.string.ok), this::onBackPressed);
        }
    }

    private void getAllAnniversaryGifs() {
        Common.progressOn(Anniversary_Gif_Activity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, (Response.Listener<String>) response -> {
            try {
                arrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String image = jsonArray.getString(i);
                    AnniversaryGifModel anniversaryGifModel = new AnniversaryGifModel();
                    anniversaryGifModel.setImage(image);
                    arrayList.add(anniversaryGifModel);
                }

                Anniver = new AnniversaryGifAdapter(getApplicationContext(), arrayList, Anniversary_Gif_Activity.this, new FriendListInterface() {
                    @Override
                    public void messageFriend(View view, int position) {
                    }

                    @Override
                    public void unfriedFriends(View view, int position) {
                    }

                    @Override
                    public void sendGif(View v, int adapterPosition) {
                        Anniversary_Gif_Activity.this.sendGif((AnniversaryGifModel) Anniversary_Gif_Activity.this.arrayList.get(adapterPosition));
                    }
                });
                checkPermission();
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(Anniver);
                Common.progressOff();
            } catch (Exception e) {
                Common.progressOff();
                e.printStackTrace();
                onBackPressed();
            }
        }, (Response.ErrorListener) error -> {
            Common.progressOff();
            Common.showAlertOkayCallBack(Anniversary_Gif_Activity.this,
                    getString(R.string.error_title_alert),
                    getString(R.string.error_server_error),
                    getString(android.R.string.ok), this::onBackPressed);
        }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logparams = new HashMap<>();
                logparams.put("action", "AnniversaryGif");
                logparams.put("user_id", SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id().toString());
                return logparams;
            }
        };
        MySingleTon.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
    }

    private void sendGif(AnniversaryGifModel anniversaryGifModel) {
        List<String> users = new ArrayList<>();
        users.add(callId/*anniversaryGifModel.getCaller_id()*/);
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false)
                .setDistinct(true).addUserIds(users).setName(nameToSend)
                .setCoverUrl(anniversaryGifModel.getImg()), (groupChannel, e) -> {
                    if (e == null) {
                        String gif = (Anniver.imagrurl()).getImage();
                        Intent intent = new Intent(Anniversary_Gif_Activity.this, Main2Activity.class);
                        intent.putExtra("URL", groupChannel.getUrl());
                        intent.putExtra("gif", gif);
                        Anniversary_Gif_Activity.this.startActivity(intent);
                        finish();
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