package com.example.myapplication.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.myapplication.R;
import com.sendbird.calls.User;

public class UserInfoUtils {

    public static void setProfileImage(Context context, User user, ImageView imageViewProfile) {
        if (user != null && imageViewProfile != null) {
            String profileUrl = user.getProfileUrl();
            if (TextUtils.isEmpty(profileUrl)) {
                imageViewProfile.setBackgroundResource(R.drawable.buzzlogo);
            } else {
                Glide.with(context)
                        .asBitmap()
                        .apply(new RequestOptions().centerCrop().dontAnimate())
                        .load(user.getProfileUrl())
                        .into(new BitmapImageViewTarget(imageViewProfile) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                imageViewProfile.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }
        }
    }

    public static void setUserId(User user, TextView textViewUserId) {
        if (user != null && textViewUserId != null) {
            textViewUserId.setText(user.getNickname());
        }
    }
}
