package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Adapter.FriendRequestAdapter;
import com.example.myapplication.Intefaces.RecyclerViewAddFriendClickListener;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequestFragment extends Fragment {
    FriendRequestAdapter friendRequestAdapter;
    ArrayList<FriendListModel> friendRequestAdapterArrayList;
    String frnid_id;
    RecyclerView rv_friend_request;
    String userId;
    LinearLayout friendrequest;

    public FriendRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.userId = SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString();
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);
        loadAllFriends();
        friendrequest=(LinearLayout)view.findViewById(R.id.nolist_friendlist_req);
        this.rv_friend_request = (RecyclerView) view.findViewById(R.id.rv_friend_request);
        this.rv_friend_request.setHasFixedSize(true);
        this.rv_friend_request.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        this.rv_friend_request.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        return view;
    }

    private void loadAllFriends() {
        String str = CommonUtils.baseUrl;
        this.friendRequestAdapterArrayList = new ArrayList<>();
        StringRequest r2 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    Log.d("FriendRequest",""+response);
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
                            FriendRequestFragment.this.friendRequestAdapterArrayList.add(friendListModel);
                            JSONObject jsonObject2 = jsonObject;
                            try {
                                JSONObject jSONObject = user_details;
                                String str = interests;
                                FriendRequestFragment.this.friendRequestAdapter = new FriendRequestAdapter(FriendRequestFragment.this.friendRequestAdapterArrayList, FriendRequestFragment.this.getActivity(), new RecyclerViewAddFriendClickListener() {
                                    public void onAddFriend(View view, int position) {
                                        FriendRequestFragment.this.frnid_id = ((FriendListModel) FriendRequestFragment.this.friendRequestAdapterArrayList.get(position)).getUser_id();
                                        FriendRequestFragment.this.friendRequestAdapterArrayList.remove(position);
                                        FriendRequestFragment.this.friendRequestAdapter.notifyDataSetChanged();
                                        FriendRequestFragment.this.Accept();
                                    }

                                    public void onCancleFriendRequest(View view, int position) {
                                        FriendRequestFragment.this.frnid_id = ((FriendListModel) FriendRequestFragment.this.friendRequestAdapterArrayList.get(position)).getUser_id();
                                        FriendRequestFragment.this.friendRequestAdapterArrayList.remove(position);
                                        FriendRequestFragment.this.friendRequestAdapter.notifyDataSetChanged();
                                        FriendRequestFragment.this.cancleFriendRequest();
                                    }
                                });
                                FriendRequestFragment.this.rv_friend_request.setAdapter(FriendRequestFragment.this.friendRequestAdapter);
                                i++;
                                jsonArray = jsonArray2;
                                jsonObject = jsonObject2;
                                String str2 = response;
                            } catch (Exception e) {
                                e = e;
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
                if(friendRequestAdapterArrayList.size()<=0)
                {
                    friendrequest.setVisibility(View.VISIBLE);
                    rv_friend_request.setVisibility(View.GONE);
                }else
                {
                    friendrequest.setVisibility(View.GONE);
                    rv_friend_request.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FriendRequestFragment.this.getActivity(), "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getfriendlist");
                logParams.put("userid", FriendRequestFragment.this.userId);
                logParams.put("requesttype", "pending");
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r2);
    }

    /* access modifiers changed from: private */
    public void cancleFriendRequest() {
        String str = CommonUtils.baseUrl;
        StringRequest r1 = new StringRequest(1, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("success");
                    String string = jsonObject.getString("message");
                    if (status.equals("true")) {
                        Toast.makeText(FriendRequestFragment.this.getActivity(), "Friend request cancel", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FriendRequestFragment.this.getActivity(), "", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FriendRequestFragment.this.getActivity(), "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "removefriend");
                logParams.put("userid", FriendRequestFragment.this.userId);
                logParams.put("frnid", FriendRequestFragment.this.frnid_id);
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r1);
    }

    /* access modifiers changed from: private */
    public void Accept() {
        String str = CommonUtils.baseUrl;
        StringRequest r1 = new StringRequest(1, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("success");
                    String string = jsonObject.getString("message");
                    if (status.equals("true")) {
                        try {
                            Toast.makeText(FriendRequestFragment.this.getActivity(), "Friend request accepted", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        }
                    } else {
                        Toast.makeText(FriendRequestFragment.this.getActivity(), "", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FriendRequestFragment.this.getActivity(), "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "acceptrequest");
                logParams.put("userid", FriendRequestFragment.this.userId);
                logParams.put("frnid", FriendRequestFragment.this.frnid_id);
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r1);
    }
}
