package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Activities.Main2Activity;
import com.example.myapplication.Adapter.FriendListAdapter;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragments extends Fragment {
    FriendListAdapter friendListAdapter;
    ArrayList<FriendListModel> friendListModelArrayList;
    RecyclerView rv_friendList;
    String userId;
    private LinearLayout nolistfriends;
    public FriendsListFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.userId = SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString();
        View view = inflater.inflate(R.layout.fragment_friends_list_fragments, container, false);
        loadAllFriends();
        this.rv_friendList = (RecyclerView) view.findViewById(R.id.rv_friendList);
        nolistfriends=view.findViewById(R.id.nolist_friendlist);
        this.rv_friendList.setHasFixedSize(true);
        this.rv_friendList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        return view;
    }

    private void loadAllFriends() {

        String str = CommonUtils.baseUrl;
        this.friendListModelArrayList = new ArrayList<>();
        StringRequest r2 = new StringRequest(1, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
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
                            FriendsListFragments.this.friendListModelArrayList.add(friendListModel);
                            JSONObject jsonObject2 = jsonObject;
                            try {
                                JSONObject jSONObject = user_details;
                                String str = interests;
                                FriendsListFragments.this.friendListAdapter = new FriendListAdapter(FriendsListFragments.this.friendListModelArrayList, FriendsListFragments.this.getActivity(), new FriendListInterface() {



                                    public void messageFriend(View view, int position) {
                                        FriendsListFragments.this.message((FriendListModel) FriendsListFragments.this.friendListModelArrayList.get(position));
                                    }

                                    public void unfriedFriends(View view, int position) {
                                    }

                                    @Override
                                    public void sendGif(View v, int adapterPosition) {

                                    }
                                });
                                FriendsListFragments.this.rv_friendList.setAdapter(FriendsListFragments.this.friendListAdapter);
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
                if(friendListModelArrayList.size()<=0)
                {
                    nolistfriends.setVisibility(View.VISIBLE);
                    rv_friendList.setVisibility(View.GONE);
                }else
                {
                    nolistfriends.setVisibility(View.GONE);
                    rv_friendList.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                if (FriendsListFragments.this.getActivity() != null) {
                    Toast.makeText(FriendsListFragments.this.getActivity(), "", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getfriendlist");
                logParams.put("userid", FriendsListFragments.this.userId);
                logParams.put("requesttype", "accepted");
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r2);
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
   //  * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();


    }

    public void message(FriendListModel friendListModel) {
        List<String> users = new ArrayList<>();
        users.add(friendListModel.getCaller_id());
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(true).addUserIds(users).setName(friendListModel.getName()).setCoverUrl(friendListModel.getImage()), new GroupChannel.GroupChannelCreateHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e == null) {
                    Intent intent = new Intent(FriendsListFragments.this.getActivity(), Main2Activity.class);
                    intent.putExtra("URL", groupChannel.getUrl());
                    FriendsListFragments.this.startActivity(intent);
                }
            }
        });
    }
}
