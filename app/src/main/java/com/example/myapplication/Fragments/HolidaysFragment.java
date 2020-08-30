package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Adapter.SpecialDaysAdapter;
import com.example.myapplication.ModelClasses.SpecialDaysModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidaysFragment extends Fragment {
    SpecialDaysAdapter specialDaysAdapter;
    SpecialDaysModel specialDaysModel;
    List<SpecialDaysModel> specialDaysModelList;
    RecyclerView rvSpecialDay;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_holidays, container, false);
        this.rvSpecialDay = (RecyclerView) view.findViewById(R.id.specialdays_recyclerview);
        this.specialDaysModelList = new ArrayList();
      //  this.specialDaysModelList.add(new SpecialDaysModel("Jan", "14", "Lohri", "Indian Festival"));
        this.specialDaysModelList.add(new SpecialDaysModel("Feb", "14", "valentines", "International"));
        this.specialDaysModelList.add(new SpecialDaysModel("Feb", "10", "teddy", "International"));
        specialDaysAdapter = new SpecialDaysAdapter(getActivity(), specialDaysModelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvSpecialDay.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        rvSpecialDay.setAdapter(specialDaysAdapter);
        rvSpecialDay.setLayoutManager(linearLayoutManager);
        //getHolidayGifImages(eventData);
        return view;
    }

    /*public void getHolidayGifImages(SpecialDaysAdapter.EventData eventData) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    *//*for (int i = 0; i < jsonArray.length(); i++) {
                        String image = jsonArray.getString(i);
                        AnniversaryGifModel anniversaryGifModel = new AnniversaryGifModel();
                        anniversaryGifModel.setImage(image);
                        arrayList.add(anniversaryGifModel);
                    }*//*

                    *//*Anniver = new AnniversaryGifAdapter(getApplicationContext(), arrayList, Anniversary_Gif_Activity.this, new FriendListInterface() {
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
                    recyclerView.setAdapter(Anniver);*//*
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> logparams = new HashMap<>();
                logparams.put("action", "SpacialDayGif");
                //logparams.put("user_id", SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString());
                return logparams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(stringRequest);
    }*/
}
