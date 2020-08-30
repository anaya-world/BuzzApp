package com.example.myapplication.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ImageUtils;

public class PhotoViewerActivity extends AppCompatActivity {
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    ImageView imageView;
    int check=0;
    private ImageView rotate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        String url = getIntent().getStringExtra("url");
        String type = getIntent().getStringExtra("type");
        imageView=(ImageView)findViewById(R.id.main_image_view);
        rotate=(ImageView)findViewById(R.id.rotate);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("rotateclick",""+check);
                if(check==0){
                    imageView.animate().rotation(90).start();
                    check=1;
                }else if(check==1){
                    imageView.animate().rotation(180).start();
                    check=2;
                }else if(check==2){
                    imageView.animate().rotation(270).start();
                    check=3;
                }else if(check==3){
                    imageView.animate().rotation(360).start();
                    check=0;
                }
                else {
                    imageView.animate().rotation(0).start();
                    check=0;
                }
            }
        });
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        progressBar.setVisibility(View.VISIBLE);
        Log.d("onResourceReady","1"+url+type);
        if (type != null && type.toLowerCase().contains("gif")) {
            Log.d("onResourceReady","2"+url);
            ImageUtils.displayGifImageFromUrl((Context) this, url, imageView, (Drawable) null, (RequestListener) new RequestListener() {
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }

                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            });
        } else if (type == null
                || !type.toLowerCase().contains("bitmap")
        ) {
            Log.d("onResourceReady","3"+url);
            ImageUtils.displayImageFromUrl(this, url, imageView, null, new RequestListener() {
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    Log.d("onResourceReady","5"+url);
                    progressBar.setVisibility(View.GONE);
                    return false;
                }

                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    Log.d("onResourceReady","6"+url);
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            Log.d("onResourceReady","4"+url);
            Glide.with((FragmentActivity) this).load(url).into(imageView);
        }


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;

    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(Math.min(mScaleFactor, 10.0f),1.0f );
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }
}
