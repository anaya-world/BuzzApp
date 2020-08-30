package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Activities.Main2Activity;
import com.example.myapplication.Adapter.SendGifAdapter;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.myapplication.Activities.SendGifActivity.gifURL;


public class SendGifFragment extends Fragment {
    SendGifAdapter gifAdapter;
    ArrayList<FriendListModel> birthdayModelArrayList;
    RecyclerView birthday_Recycler;
    ProgressBar birthday_progress;
    LinearLayout progress_lay;
    String userId;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public SendGifFragment() {
    }


    // TODO: Rename and change types and number of parameters
    public static SendGifFragment newInstance(String param1, String param2) {
        SendGifFragment fragment = new SendGifFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_gif, container, false);
        this.userId = SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString();
        this.birthday_Recycler = (RecyclerView) view.findViewById(R.id.birthday_Recycler);
        this.progress_lay = view.findViewById(R.id.progress_lay);
        this.birthday_progress = (ProgressBar) view.findViewById(R.id.birthday_progress);

        this.progress_lay.setVisibility(View.VISIBLE);
        birthday_Recycler.setVisibility(View.GONE);
        loadAllFriends();


        this.birthday_Recycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        this.birthday_Recycler.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        this.birthday_Recycler.setLayoutManager(layoutManager);
        return view;
    }

    private void loadAllFriends() {
        String str = CommonUtils.baseUrl;
        this.birthdayModelArrayList = new ArrayList<>();
        StringRequest r2 = new StringRequest(1, CommonUtils.baseUrl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onResponse(String response) {
                progress_lay.setVisibility(View.GONE);
                birthday_Recycler.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("search_result");
                        int i = 0;
                        while (i < jsonArray.length()) {
                            JSONObject user_details = jsonArray.getJSONObject(i);
                            String name = user_details.optString("name");
                            String secrate_id = user_details.optString("secrate_id");
                            String user_image = user_details.optString("user_img");
                            String gender = user_details.optString("gender");
                            String user_id = user_details.optString("userid");
                            String caller_id = user_details.optString("caller_id ");
                            String dob = user_details.optString("dob");
                            String anniversary = user_details.optString("anniversary");
                            String email = user_details.optString("email");
                            String interests = user_details.optString("instrests");
                            String marital = user_details.optString("marital");
                            String mobileno = user_details.optString("mobileno");
                            String optString = user_details.optString("resident");
                            String optString2 = user_details.optString("workfor");
                            FriendListModel friendListModel = new FriendListModel(name, user_image, gender, secrate_id, caller_id, dob, anniversary, email, marital, mobileno, user_id, user_details.optString("frnid"), user_details.optString("frns"));
                            JSONArray jsonArray2 = jsonArray;
                            SendGifFragment.this.birthdayModelArrayList.add(friendListModel);
                            JSONObject jsonObject2 = jsonObject;
                            try {
                                JSONObject jSONObject = user_details;
                                String str = interests;
                                SendGifFragment.this.gifAdapter = new SendGifAdapter(SendGifFragment.this.birthdayModelArrayList, SendGifFragment.this.getActivity(), new FriendListInterface() {


                                    public void messageFriend(View view, int position) {
                                        SendGifFragment.this.message((FriendListModel) SendGifFragment.this.birthdayModelArrayList.get(position));
                                    }

                                    public void unfriedFriends(View view, int position) {
                                    }


                                    public void sendGif(View view, int position) {
                                        SendGifFragment.this.sendGif((FriendListModel) SendGifFragment.this.birthdayModelArrayList.get(position));

                                    }
                                });
                                SendGifFragment.this.birthday_Recycler.setAdapter(SendGifFragment.this.gifAdapter);
                                i++;
                                jsonArray = jsonArray2;
                                jsonObject = jsonObject2;
                                String str2 = response;
                            } catch (Exception e) {
                                JSONObject jSONObject2 = jsonObject2;
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {

                        JSONObject jSONObject3 = jsonObject;
                        e.printStackTrace();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SendGifFragment.this.getActivity(), "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getfriendlist");
                logParams.put("userid", SendGifFragment.this.userId);
                logParams.put("requesttype", "accepted");
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r2);
    }

    private void schedule(FriendListModel friendListModel) {
        List<String> users = new ArrayList<>();
        users.add(friendListModel.getCaller_id());
        Log.d("schedle", "a" + users);
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(true).addUserIds(users).setName(friendListModel.getName()).setCoverUrl(friendListModel.getImage()), new GroupChannel.GroupChannelCreateHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                Log.d("schedle", "a" + e);
                if (e == null) {
//                    Fragment fragment = new ScheduleFragment();
//                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack
//                            (null).commit();
                    Log.d("schedle", "a" + groupChannel.getUrl());
                }
            }
        });
    }

    public void message(FriendListModel friendListModel) {
        List<String> users = new ArrayList<>();
        users.add(friendListModel.getCaller_id());
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(true).addUserIds(users).setName(friendListModel.getName()).setCoverUrl(friendListModel.getImage()), new GroupChannel.GroupChannelCreateHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e == null) {

                    String happy = "Wish you a very happy birthday";
                    Intent intent = new Intent(SendGifFragment.this.getActivity(), Main2Activity.class);
                    intent.putExtra("URL", groupChannel.getUrl());
                    intent.putExtra("happy", happy);
                    Log.d("grp", "1" + groupChannel.getMembers());
                    Log.d("grp", "2" + SendBird.getCurrentUser().getFriendDiscoveryKey());
                    Log.d("grp", "3" + groupChannel);
                    Log.d("grp", "3" + SendBird.getCurrentUser().getUserId());
                    SendGifFragment.this.startActivity(intent);
                }
            }
        });
    }

    public void sendGif(FriendListModel friendListModel) {
        List<String> users = new ArrayList<>();
        users.add(friendListModel.getCaller_id());
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(true).addUserIds(users).setName(friendListModel.getName()).setCoverUrl(friendListModel.getImage()), new GroupChannel.GroupChannelCreateHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e == null) {
                    //String gif = "Click Gif";
                    Intent intent = new Intent(SendGifFragment.this.getActivity(), Main2Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("URL", groupChannel.getUrl());
                    intent.putExtra("gif", gifURL);
                    SendGifFragment.this.startActivity(intent);
                    Objects.requireNonNull(getActivity()).finish();
                }
            }
        });
    }
}