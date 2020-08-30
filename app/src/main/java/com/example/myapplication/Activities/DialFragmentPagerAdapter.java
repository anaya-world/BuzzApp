package com.example.myapplication.Activities;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapplication.Fragments.DialFragment;
import com.example.myapplication.Fragments.SettingsFragment;
import com.example.myapplication.R;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.User;

import java.util.ArrayList;
import java.util.List;

class DialFragmentPagerAdapter extends FragmentPagerAdapter {

    private final List<FragmentInfo> mFragmentList = new ArrayList<>();

    private static class FragmentInfo {
        final String mTitle;
        final Fragment mFragment;

        FragmentInfo(String title, Fragment fragment) {
            mTitle = title;
            mFragment = fragment;
        }
    }

    DialFragmentPagerAdapter(@NonNull Context context, @NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);

        String nicknameOrUserId = "";
        User currentUser = SendBirdCall.getCurrentUser();
        if (currentUser != null) {
            nicknameOrUserId = currentUser.getNickname();
            if (TextUtils.isEmpty(nicknameOrUserId)) {
                nicknameOrUserId = currentUser.getUserId();
            }
        }

        mFragmentList.add(new FragmentInfo(nicknameOrUserId, new DialFragment()));
//        if (MainActivity.sHistoryFeature) {
//            mFragmentList.add(new FragmentInfo(context.getString(R.string.calls_history), new HistoryFragment()));
//        }
        mFragmentList.add(new FragmentInfo(context.getString(R.string.calls_settings), new SettingsFragment()));
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position).mFragment;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentList.get(position).mTitle;
    }
}
