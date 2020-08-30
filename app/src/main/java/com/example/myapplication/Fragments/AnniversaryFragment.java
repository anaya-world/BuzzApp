package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Activities.Main2Activity;
import com.example.myapplication.Adapter.AnniversaryAdapter;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannel.GroupChannelCreateHandler;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnniversaryFragment extends Fragment {
    AnniversaryAdapter anniversaryAdapter;
    RecyclerView anniversary_Recycler;
    ProgressBar anniversary_progress;
    ArrayList<FriendListModel> friendListModelArrayList;
    String userId;
    LinearLayout anv_progress;
    LinearLayout nofriendsanniversary;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anniversary, container, false);
        this.userId = SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString();
        this.anniversary_Recycler = (RecyclerView) view.findViewById(R.id.anniversary_Recycler);
        nofriendsanniversary=view.findViewById(R.id.nolist_friendlist_anni);
        anv_progress=view.findViewById(R.id.progress_lay_anv);
        anv_progress.setVisibility(View.VISIBLE);
        anniversary_Recycler.setVisibility(View.GONE);
        loadAllFriends();
        this.anniversary_Recycler.setHasFixedSize(true);
        this.anniversary_Recycler.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        this.anniversary_Recycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        return view;
    }

    private void loadAllFriends() {
        String str = CommonUtils.baseUrl;
        this.friendListModelArrayList = new ArrayList<>();
        StringRequest r2 = new StringRequest(1, CommonUtils.baseUrl, new Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onResponse(String response) {
                anv_progress.setVisibility(View.GONE);
                anniversary_Recycler.setVisibility(View.VISIBLE);
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
                            if(!marital.equals("Single")) {
                                AnniversaryFragment.this.friendListModelArrayList.add(friendListModel);
                            }
                            JSONObject jsonObject2 = jsonObject;
                            try {
                                JSONObject jSONObject = user_details;
                                String str = interests;
                                AnniversaryFragment.this.anniversaryAdapter = new AnniversaryAdapter(AnniversaryFragment.this.friendListModelArrayList, AnniversaryFragment.this.getActivity(), new FriendListInterface() {




                                    public void messageFriend(View view, int position) {
                                        AnniversaryFragment.this.message((FriendListModel) AnniversaryFragment.this.friendListModelArrayList.get(position));
                                    }

                                    public void unfriedFriends(View view, int position) {
                                    }

                                    @Override
                                    public void sendGif(View view, int position) {
                                        AnniversaryFragment.this.sendGif((FriendListModel) AnniversaryFragment.this.friendListModelArrayList.get(position));


                                    }
                                });
                                AnniversaryFragment.this.anniversary_Recycler.setAdapter(AnniversaryFragment.this.anniversaryAdapter);
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
                    } catch (JSONException e2) {

                        JSONObject jSONObject3 = jsonObject;
                        e2.printStackTrace();
                    }
                } catch (JSONException e3) {

                    e3.printStackTrace();
                }
                if(friendListModelArrayList.size()<=0)
                {
                    nofriendsanniversary.setVisibility(View.VISIBLE);
                    anniversary_Recycler.setVisibility(View.GONE);
                }else
                {
                    nofriendsanniversary.setVisibility(View.GONE);
                    anniversary_Recycler.setVisibility(View.VISIBLE);
                }
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AnniversaryFragment.this.getActivity(), "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getfriendlist");
                logParams.put("userid", AnniversaryFragment.this.userId);
                logParams.put("requesttype", "accepted");
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r2);
    }

    private void sendGif(FriendListModel friendListModel) {
        List<String> users = new ArrayList<>();
        users.add(friendListModel.getCaller_id());
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(true).addUserIds(users).setName(friendListModel.getName()).setCoverUrl(friendListModel.getImage()), new GroupChannelCreateHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e == null) {
                    String gif="Click Gif";

                    Intent intent = new Intent(AnniversaryFragment.this.getActivity(), Main2Activity.class);
                    intent.putExtra("URL", groupChannel.getUrl());
                    intent.putExtra("gif",gif);
                    AnniversaryFragment.this.startActivity(intent);
                }
            }
        });
    }

    public void  message(FriendListModel friendListModel) {
        List<String> users = new ArrayList<>();
        users.add(friendListModel.getCaller_id());
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(true).addUserIds(users).setName(friendListModel.getName()).setCoverUrl(friendListModel.getImage()), new GroupChannelCreateHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e == null) {
                    String anniversary="Wish you a very happy anniversary";
                    Intent intent = new Intent(AnniversaryFragment.this.getActivity(), Main2Activity.class);
                    intent.putExtra("URL", groupChannel.getUrl());
                    intent.putExtra("anniversary",anniversary);
                    AnniversaryFragment.this.startActivity(intent);
                }
            }
        });
    }
}
