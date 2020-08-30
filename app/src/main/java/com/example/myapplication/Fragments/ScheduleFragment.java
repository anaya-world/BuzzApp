package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Adapter.ScheduleAdapter;
import com.example.myapplication.Database.DatabaseHelperSchedule;
import com.example.myapplication.Intefaces.NotifyINf;
import com.example.myapplication.Intefaces.TabNotify;
import com.example.myapplication.ModelClasses.Models;
import com.example.myapplication.ModelClasses.Search_result;
import com.example.myapplication.ModelClasses.Send_data;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.myapplication.Utils.Common.isConnected;
import static com.example.myapplication.Utils.Common.showAlertOkay;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment implements NotifyINf {
    List<Models> listes = new ArrayList<>();
    String send_to_id;
    RecyclerView recyclerView;
    LinearLayout llmsgView;
    ScheduleAdapter scheduleAdapter;
    JSONArray search;
    private TabNotify tabNotify;
    private ArrayList<Search_result> search_results = new ArrayList<>();
    LinearLayout sch_progress;

    DatabaseHelperSchedule databaseHelperSchedule;

    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        llmsgView = view.findViewById(R.id.msg_view);
        tabNotify = (TabNotify) getActivity();
        tabNotify.notifyTab(4);
        databaseHelperSchedule = new DatabaseHelperSchedule(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.rc_Schedules);
        sch_progress = view.findViewById(R.id.progress_lay_sch);
        sch_progress.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        if (isConnected(getActivity())) {
            callScheduleDataService();
        } else {
            sch_progress.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            showAlertOkay(getActivity(), "No Internet Connection",
                    "Please check your internet connection. You can see the data offline mode.",
                    getString(android.R.string.ok));

            List<Search_result> data = new ArrayList<>();

            data = databaseHelperSchedule.getData1();
            if (data.size() > 0) {
                llmsgView.setVisibility(View.GONE);
                search_results.addAll(data);
                scheduleAdapter = new ScheduleAdapter(getContext(), search_results, ScheduleFragment.this);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(scheduleAdapter);
                recyclerView.setHasFixedSize(true);
            } else {
                llmsgView.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    private void callScheduleDataService() {
        final StringRequest r2 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                sch_progress.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {

                        Log.d("gif", "2" + response);
                        if (jsonObject.getString("success").equals("true")) {

                            search = jsonObject.getJSONArray("search_result");
                            JSONArray sendUser = jsonObject.getJSONArray("sendUser");
                            for (int i = 0; i < search.length(); i++) {
                                Search_result search_result = new Search_result();
                                JSONObject jsonObject1 = search.getJSONObject(i);
                                search_result.setDate(jsonObject1.getString("date"));
                                search_result.setFestivalType(jsonObject1.getString("festival_type"));
                                search_result.setId(jsonObject1.getString("id"));
                                search_result.setMessage(jsonObject1.getString("message"));
                                search_result.setScheduleId(jsonObject1.getString("schedule_id"));
                                search_result.setTime(jsonObject1.getString("time"));
                                search_result.setSendTo(jsonObject1.getString("sendTo"));
                                search_result.setUserid(jsonObject1.getString("userid"));
                                search_result.setMobileno(jsonObject1.getString("mobileno"));
                                search_result.setGif(jsonObject1.getString("gif"));
                                Log.d("gif", "2" + jsonObject1.getString("gif"));
                                JSONArray elem = sendUser.getJSONArray(i);


                                ArrayList<Send_data> send_datas = new ArrayList<>();
                                for (int y = 0; y < elem.length(); y++) {
                                    Send_data send_data = new Send_data();
                                    JSONObject obj = elem.getJSONObject(y);
                                    send_data.setPerentScheduleId(obj.getString("perent_schedule_id"));
                                    send_data.setName(obj.getString("name"));
                                    send_data.setUserImg(obj.getString("user_img"));
                                    send_data.setId(obj.getString("id"));
                                    send_datas.add(send_data);

                                }
                                search_result.setSend_data(send_datas);
                                search_results.add(search_result);

                            }

                            if (search_results.size() > 0) {
                                llmsgView.setVisibility(View.GONE);
                                scheduleAdapter = new ScheduleAdapter(getContext(), search_results, ScheduleFragment.this);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                layoutManager.setReverseLayout(true);
                                layoutManager.setStackFromEnd(true);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(scheduleAdapter);
                                recyclerView.setHasFixedSize(true);
                            }
                        } else {
                            llmsgView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        llmsgView.setVisibility(View.VISIBLE);
                        // llmsgView.setText(jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    llmsgView.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }
        }, error -> {
            llmsgView.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Unable to get schedule data from server.",
                    Toast.LENGTH_LONG).show();
            getActivity().onBackPressed();
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("userid", SharedPrefManager.getInstance(getActivity()).getUser().getUser_id());
                logParams.put("action", "get_schedule");
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r2);
    }

    @Override
    public void delete_Schedule(int position) {
        Log.d("pos1", "" + position);
        search_results.remove(position);

        scheduleAdapter.notifyDataSetChanged();
    }
}
