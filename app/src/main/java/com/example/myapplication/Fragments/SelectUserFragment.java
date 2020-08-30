package com.example.myapplication.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Activities.Main2Activity;
import com.example.myapplication.Adapter.SelectableUserListAdapter;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserListQuery;

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
public class SelectUserFragment extends Fragment {

    ArrayList<FriendListModel> friendListModelArrayList;
    private LinearLayoutManager mLayoutManager;
    public SelectableUserListAdapter mListAdapter;
    public UsersSelectedListener mListener;
    /* access modifiers changed from: private */
    public RecyclerView mRecyclerView;String userId;
    private LinearLayout nolist;

    public interface UsersSelectedListener {
        void onUserSelected(boolean z, String str, FriendListModel name);
    }

    public static SelectUserFragment newInstance() {
        return new SelectUserFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_select_user, container, false);
        this.mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_select_user);
        nolist=(LinearLayout)rootView.findViewById(R.id.nolist);
        this.mListAdapter = new SelectableUserListAdapter(getActivity(), false, true);
        this.userId = SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString();
        this.mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        this.mRecyclerView.setLayoutManager(layoutManager);
        loadAllFriends();
        return rootView;
    }

    private void loadAllFriends() {
        String str = CommonUtils.baseUrl;
        this.friendListModelArrayList = new ArrayList<>();
        StringRequest r2 = new StringRequest(1, CommonUtils.baseUrl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onResponse(String response) {
                String str = response;
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Log.d("myressponse", str);
                    if(jsonObject.getString("success").contains("false")){
                        nolist.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }else {
                        nolist.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
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
                            String optString = user_details.optString("instrests");
                            String marital = user_details.optString("marital");
                            String mobileno = user_details.optString("mobileno");

                            FriendListModel friendListModel = new FriendListModel(name, user_image, gender, secrate_id, caller_id, dob, anniversary, email, marital, mobileno, user_id, user_details.optString("frnid"), user_details.optString("frns"));
                            JSONArray jsonArray2 = jsonArray;
                            SelectUserFragment.this.friendListModelArrayList.add(friendListModel);
                            i++;
                            jsonArray = jsonArray2;
                        }

                    }

                    SelectUserFragment selectUserFragment = SelectUserFragment.this;

                    SelectableUserListAdapter selectableUserListAdapter = new SelectableUserListAdapter(SelectUserFragment.this.friendListModelArrayList, SelectUserFragment.this.getActivity(), new FriendListInterface() {




                        public void messageFriend(View view, int position) {
                            Object obj = SelectUserFragment.this.friendListModelArrayList.get(position);
                        }

                        public void unfriedFriends(View view, int position) {
                        }

                        @Override
                        public void sendGif(View v, int adapterPosition) {

                        }
                    }, false, true);
                    selectUserFragment.mListAdapter = selectableUserListAdapter;
                    SelectUserFragment.this.mRecyclerView.setAdapter(SelectUserFragment.this.mListAdapter);
                    SelectUserFragment.this.mListAdapter.setItemCheckedChangeListener(new SelectableUserListAdapter.OnItemCheckedChangeListener() {
                        public void OnItemChecked(FriendListModel user, boolean checked) {
                            if (checked) {
                                SelectUserFragment.this.mListener.onUserSelected(true, user.getUser_id(),user);
                            } else {
                                SelectUserFragment.this.mListener.onUserSelected(false, user.getUser_id(),user);
                            }
                        }
                    });
                    SelectUserFragment.this.mListener = (UsersSelectedListener) SelectUserFragment.this.getActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getfriendlist");
                logParams.put("userid", SelectUserFragment.this.userId);
                logParams.put("requesttype", "accepted");
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r2);
    }

    public void message(FriendListModel friendListModel) {
        List<String> users = new ArrayList<>();
        users.add(friendListModel.getCaller_id());
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(false).addUserIds(users).setName(friendListModel.getName()).setCoverUrl(friendListModel.getImage()), new GroupChannel.GroupChannelCreateHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e == null) {
                    Intent intent = new Intent(SelectUserFragment.this.getActivity(), Main2Activity.class);
                    intent.putExtra("URL", groupChannel.getUrl());
                    SelectUserFragment.this.startActivity(intent);
                }
            }
        });
    }
}
