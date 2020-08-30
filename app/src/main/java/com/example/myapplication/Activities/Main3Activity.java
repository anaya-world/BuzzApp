package com.example.myapplication.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.example.myapplication.Intefaces.OnBackPressedListener;
import com.example.myapplication.Intefaces.OnStartFileDownloadListener;
import com.example.myapplication.OpenChats.OpenChatFragment;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Common;
import com.sendbird.android.FileMessage;

import java.io.File;

public class Main3Activity extends AppCompatActivity implements OnStartFileDownloadListener {
    protected OnBackPressedListener onBackPressedListener;
    String channelUrl;
    OpenChatFragment frag;
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra("extra_download_id", -1);
            if ("android.intent.action.DOWNLOAD_COMPLETE".equals(intent.getAction())) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (downloadID == id) {
                    Toast.makeText(Main3Activity.this, "File is downloaded.", Toast.LENGTH_SHORT).show();
                    if (frag != null) {
                        frag.notifyAdapter();
                    }
                }
            }
        }
    };
    private ProgressDialog progressDialog;
    private long downloadID;
    DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        channelUrl = getIntent().getStringExtra("URL");
        setContentView(R.layout.activity_main3);

        frag = OpenChatFragment.newInstance(channelUrl);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_open_channel, frag)
                .addToBackStack(null)
                .commit();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_open_channel));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setProgressStyle(0);
        this.progressDialog.setMessage("File is downloading, Please wait...");
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setCancelable(true);
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
        else
            super.onBackPressed();
    }

    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onStartFileDownload(FileMessage paramFileMessage) {
        beginDownload(paramFileMessage);
    }

    private void beginDownload(FileMessage fileMessage) {
        String filename = fileMessage.getName();
        String downloadUrlOfImage = fileMessage.getUrl();
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        sb.append("/BuzzApp/Images");
        File directImage = new File(sb.toString());
        if (!directImage.exists()) {
            directImage.mkdirs();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(Environment.getExternalStorageDirectory());
        sb2.append("/BuzzApp/Videos");
        File directVideo = new File(sb2.toString());
        if (!directVideo.exists()) {
            directVideo.mkdirs();
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(Environment.getExternalStorageDirectory());
        sb3.append("/BuzzApp/Documents");
        File directDocuments = new File(sb3.toString());
        if (!directDocuments.exists()) {
            directDocuments.mkdirs();
        }
        StringBuilder sb4 = new StringBuilder();
        sb4.append(Environment.getExternalStorageDirectory());
        sb4.append("/BuzzApp/Audio");
        File directAudio = new File(sb4.toString());
        if (!directAudio.exists()) {
            directAudio.mkdirs();
        }
        if (checkForFileToDownload(fileMessage)) {
            if (fileMessage.getType().startsWith("image")) {
                Intent i = new Intent(this, PhotoViewerActivity.class);
                i.putExtra("url", fileMessage.getUrl());
                i.putExtra("type", fileMessage.getType());
                startActivity(i);
            } else if (fileMessage.getType().startsWith("video")) {
                Intent intent = new Intent(this, VideoViewActivity.class);
                StringBuilder sb5 = new StringBuilder();
                sb5.append(Environment.getExternalStorageDirectory());
                sb5.append("/BuzzApp/Videos/");
                sb5.append(fileMessage.getName());
                intent.putExtra("path", sb5.toString());
                startActivity(intent);
            }
        } else if (Common.isConnected(this)) {
            this.progressDialog.show();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrlOfImage));
            request.setAllowedOverRoaming(true).setAllowedOverMetered(true).setTitle(filename).setMimeType("*/*").setNotificationVisibility(2);
            if (fileMessage.getType().toLowerCase().startsWith("image")) {
                request.setDestinationInExternalPublicDir("/BuzzApp/Images", filename);
            } else if (fileMessage.getType().toLowerCase().startsWith("video")) {
                request.setDestinationInExternalPublicDir("/BuzzApp/Videos", filename);
            } else if (fileMessage.getType().toLowerCase().startsWith("audio")) {
                request.setDestinationInExternalPublicDir("/BuzzApp/Audio", filename);
            } else if (fileMessage.getType().toLowerCase().startsWith("application/")) {
                request.setDestinationInExternalPublicDir("/BuzzApp/Documents", filename);
            } else {
                request.setDestinationInExternalPublicDir("/BuzzApp", filename);
            }
            this.downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            this.downloadID = this.downloadManager.enqueue(request);
        }
    }
    private boolean checkForFileToDownload(FileMessage fileMessage) {
        try {
            String filename = fileMessage.getName();
            if (fileMessage.getType().toLowerCase().startsWith("image")) {
                if (checkToDownload(filename, "/BuzzApp/Images")) {
                    return true;
                }
                return false;
            } else if (fileMessage.getType().toLowerCase().startsWith("video")) {
                if (checkToDownload(filename, "/BuzzApp/Videos")) {
                    return true;
                }
                return false;
            } else if (fileMessage.getType().toLowerCase().startsWith("audio")) {
                if (!checkToDownload(filename, "/BuzzApp/Audio")) {
                    return false;
                }
                Toast.makeText(this, "This audio is already downloaded.", Toast.LENGTH_SHORT).show();
                return true;
            } else if (fileMessage.getType().toLowerCase().startsWith("application/")) {
                if (!checkToDownload(filename, "/BuzzApp/Documents")) {
                    return false;
                }
                Toast.makeText(this, "This doc is already downloaded.", Toast.LENGTH_SHORT).show();
                return true;
            } else if (!checkToDownload(filename, "/BuzzApp")) {
                return false;
            } else {
                Toast.makeText(this, "This file is already downloaded.", Toast.LENGTH_SHORT).show();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
    private boolean checkToDownload(String filename, String directory) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory());
            sb.append(directory);
            sb.append("/");
            sb.append(filename);
            if (new File(sb.toString()).exists()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
