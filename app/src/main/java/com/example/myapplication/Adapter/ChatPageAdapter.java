package com.example.myapplication.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myapplication.GroupChats.GroupChannelListFragment;
import com.example.myapplication.OpenChats.OpenChannelListFragment;

public class ChatPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ChatPageAdapter(FragmentManager fm2, int NumOfTabs) {
        super(fm2);
        this.mNumOfTabs = NumOfTabs;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new GroupChannelListFragment();
            case 1:
                return new OpenChannelListFragment();
            default:
                return null;
        }
    }

    public int getCount() {
        return this.mNumOfTabs;
    }
}
