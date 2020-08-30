package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapter.EventPagerAdapter1;
import com.example.myapplication.Intefaces.TabNotify;
import com.example.myapplication.ModelClasses.PostListModel;
import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {
    private TabNotify tabNotify;
    ViewPager simpleViewPager;
    TabLayout tabLayout;
    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_events, container, false);
        tabNotify=(TabNotify)getActivity();
        tabNotify.notifyTab(3);
        this.simpleViewPager = (ViewPager) view.findViewById(R.id.eventViewPager);
        this.tabLayout = (TabLayout) view.findViewById(R.id.eventTabLayout);
        TabLayout.Tab firstTab = this.tabLayout.newTab();
        firstTab.setText((CharSequence) "Birthday");
        this.tabLayout.addTab(firstTab);
        TabLayout.Tab secondTab = this.tabLayout.newTab();
        secondTab.setText((CharSequence) "Anniversary");
        this.tabLayout.addTab(secondTab);
        TabLayout.Tab thirdTab = this.tabLayout.newTab();
        thirdTab.setText((CharSequence) "Special Days");
        this.tabLayout.addTab(thirdTab);
        this.simpleViewPager.setAdapter(new EventPagerAdapter1(getFragmentManager(), this.tabLayout.getTabCount()));
        this.simpleViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(this.tabLayout));
        this.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                EventsFragment.this.simpleViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }

            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        View actionView = MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private ArrayList<PostListModel> filter(ArrayList<PostListModel> models, String query) {
        String query2 = query.toLowerCase();
        ArrayList arrayList = new ArrayList();
        Iterator it = models.iterator();
        while (it.hasNext()) {
            PostListModel model = (PostListModel) it.next();
            if (model.getPostedby_name().toLowerCase().contains(query2)) {
                arrayList.add(model);
            }
        }
        return arrayList;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
