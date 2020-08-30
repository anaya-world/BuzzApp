package com.example.myapplication.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myapplication.Fragments.AnniversaryFragment;
import com.example.myapplication.Fragments.BirthdayFragment;
import com.example.myapplication.Fragments.HolidaysFragment;

public class EventPagerAdapter1 extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public EventPagerAdapter1(FragmentManager fm2, int NumOfTabs) {
        super(fm2);
        this.mNumOfTabs = NumOfTabs;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BirthdayFragment();
            case 1:
                return new AnniversaryFragment();
            case 2:
                return new HolidaysFragment();
            default:
                return null;
        }
    }

    public int getCount() {
        return this.mNumOfTabs;
    }
}
