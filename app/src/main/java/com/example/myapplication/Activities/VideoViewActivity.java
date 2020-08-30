package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.myapplication.R;

public class VideoViewActivity extends AppCompatActivity {
    private MediaController mediaControls;

    private VideoView myVideoView;

    private String path;

    private int position = 0;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        if (getIntent() != null && getIntent().hasExtra("path")) {
            this.path = getIntent().getStringExtra("path");
            if (this.mediaControls == null)
                this.mediaControls = new MediaController(this);
            this.myVideoView = (VideoView)findViewById(R.id.video_view);
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setMessage("Loading...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
            this.myVideoView.setMediaController(this.mediaControls);
            this.myVideoView.setVideoURI(Uri.parse(this.path));
            this.myVideoView.requestFocus();
            this.myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer param1MediaPlayer) {
                    VideoViewActivity.this.progressDialog.dismiss();
                    VideoViewActivity.this.myVideoView.seekTo(VideoViewActivity.this.position);
                    if (VideoViewActivity.this.position == 0) {
                        VideoViewActivity.this.myVideoView.start();
                        return;
                    }
                    VideoViewActivity.this.myVideoView.pause();
                }
            });
        }

    }
    public void onRestoreInstanceState(Bundle paramBundle) {
        super.onRestoreInstanceState(paramBundle);
        this.position = paramBundle.getInt("Position");
        this.myVideoView.seekTo(this.position);
    }

    public void onSaveInstanceState(Bundle paramBundle) {
        super.onSaveInstanceState(paramBundle);
        paramBundle.putInt("Position", this.myVideoView.getCurrentPosition());
        this.myVideoView.pause();
    }
}
