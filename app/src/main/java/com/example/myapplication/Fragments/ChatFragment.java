package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapter.ChatPageAdapter;
import com.example.myapplication.Adapter.EventPagerAdapter1;
import com.example.myapplication.GroupChats.GroupChannelListFragment;
import com.example.myapplication.Intefaces.TabNotify;
import com.example.myapplication.OpenChats.OpenChannelListFragment;
import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;

public class ChatFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPagerChat;
    TabNotify tabNotify;
    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabNotify=(TabNotify)getActivity();
        tabNotify.notifyTab(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_chat, container, false);
        this.tabLayout = (TabLayout) this.view.findViewById(R.id.tabs);
        this.viewPagerChat = (ViewPager) view.findViewById(R.id.view_pager_chat);

        includeTabs();
        return this.view;
    }
    private void includeTabs() {
        tabNotify.notifyTab(2);
        TabLayout.Tab firstTab = this.tabLayout.newTab();
        firstTab.setText((CharSequence) "Private Chat");
        this.tabLayout.addTab(firstTab);
        TabLayout.Tab secondTab = this.tabLayout.newTab();
        secondTab.setText((CharSequence) "Public Chat");
        this.tabLayout.addTab(secondTab);
        viewPagerChat.setAdapter(new ChatPageAdapter(getFragmentManager(), this.tabLayout.getTabCount()));
        viewPagerChat.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(this.tabLayout));
        this.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerChat.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }

            public void onTabReselected(TabLayout.Tab tab) {
            }
        });



       /* this.tabLayout.addTab(this.tabLayout.newTab().setText((CharSequence) "Private Chat"));
        this.tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.app_color_voilet));
        this.tabLayout.addTab(this.tabLayout.newTab().setText((CharSequence) "Public Chat"));
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_group_channel, new GroupChannelListFragment()).addToBackStack(null).commit();
        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                if (ChatFragment.this.tabLayout.getSelectedTabPosition() == 0) {
                    ChatFragment.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_group_channel, new GroupChannelListFragment()).addToBackStack(null).commit();
                } else if (ChatFragment.this.tabLayout.getSelectedTabPosition() == 1) {
                    ChatFragment.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_group_channel, new OpenChannelListFragment()).addToBackStack(null).commit();
                }
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }

            public void onTabReselected(TabLayout.Tab tab) {
            }
        });*/
    }

}
