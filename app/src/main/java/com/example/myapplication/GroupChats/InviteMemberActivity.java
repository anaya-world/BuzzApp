package com.example.myapplication.GroupChats;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Activities.BaseActivity;
import com.example.myapplication.Adapter.SelectableUserListAdapter;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserListQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InviteMemberActivity extends BaseActivity {
    ArrayList<FriendListModel> friendListModelArrayList;
    /* access modifiers changed from: private */
    public GroupChannel mChannel;
    private String mChannelUrl;
    private Button mInviteButton;
    private LinearLayoutManager mLayoutManager;
    /* access modifiers changed from: private */
    public SelectableUserListAdapter mListAdapter;
    /* access modifiers changed from: private */
    public RecyclerView mRecyclerView;
    /* access modifiers changed from: private */
    public List<String> mSelectedUserIds;
    private Toolbar mToolbar;
    private UserListQuery mUserListQuery;
    List<Member> member = new ArrayList();
    ArrayList<FriendListModel> temp;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_member);
        this.mSelectedUserIds = new ArrayList();
        this.userId = SharedPrefManager.getInstance(this).getUser().getUser_id().toString();
        this.mRecyclerView = (RecyclerView) findViewById(R.id.recycler_invite_member);
        this.mListAdapter = new SelectableUserListAdapter(this, false, true);
        this.mToolbar = (Toolbar) findViewById(R.id.toolbar_invite_member);
        setSupportActionBar(this.mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator((int) R.drawable.ic_arrow_left_white_24_dp);
        }
        this.mChannelUrl = getIntent().getStringExtra("EXTRA_CHANNEL_URL");
        refresh();
        this.mInviteButton = (Button) findViewById(R.id.button_invite_member);
        this.mInviteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (InviteMemberActivity.this.mSelectedUserIds.size() > 0) {
                    InviteMemberActivity.this.inviteAndUnBanUserWithUserID(InviteMemberActivity.this.mSelectedUserIds);
                }
            }
        });
        this.mLayoutManager = new LinearLayoutManager(this);
        this.mRecyclerView.setLayoutManager(this.mLayoutManager);
        loadAllFriends();
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
    }

    private void refresh() {
        if (this.mChannel == null) {
            GroupChannel.getChannel(this.mChannelUrl, new GroupChannel.GroupChannelGetHandler() {
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    InviteMemberActivity.this.mChannel = groupChannel;
                    InviteMemberActivity.this.member = InviteMemberActivity.this.mChannel.getMembers();
                }
            });
        } else {
            this.mChannel.refresh(new GroupChannel.GroupChannelRefreshHandler() {
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    /* access modifiers changed from: private */
    int iCount;

    public void inviteAndUnBanUserWithUserID(List<String> mSelectedUserIds) {
        iCount = 0;
        // List<String> mSelectedUserIds = mSelectedUserIds;
        for (String strUserId : mSelectedUserIds) {
            mChannel.unbanUserWithUserId(strUserId, new GroupChannel.GroupChannelUnbanHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    /*if (e != null) {
                        return;
                    }*/
                    iCount++;
                    inviteSelectedMembersWithUser(strUserId, iCount);
                }
            });
        }
    }

    public void inviteSelectedMembersWithUser(String strUserID, int totalCount) {
        GroupChannel.getChannel(this.mChannelUrl, new GroupChannel.GroupChannelGetHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e == null) {
                    groupChannel.inviteWithUserId(strUserID, new GroupChannel.GroupChannelInviteHandler() {
                        @Override
                        public void onResult(SendBirdException e) {
                            if (e == null && totalCount == mSelectedUserIds.size()) {
                                InviteMemberActivity.this.finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private void loadAllFriends() {
        String str = CommonUtils.baseUrl;
        this.friendListModelArrayList = new ArrayList<>();
        this.temp = new ArrayList<>();
        StringRequest r2 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onResponse(String response) {
                String str = response;
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Log.d("myressponse", str);
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
                        String optString2 = user_details.optString("resident");
                        String optString3 = user_details.optString("workfor");
                        FriendListModel friendListModel = new FriendListModel(name, user_image, gender, secrate_id, caller_id, dob, anniversary, email, marital, mobileno, user_id, user_details.optString("frnid"), user_details.optString("frns"));
                        FriendListModel friendListModel2 = friendListModel;
                        JSONArray jsonArray2 = jsonArray;
                        InviteMemberActivity.this.friendListModelArrayList.add(friendListModel2);
                        InviteMemberActivity.this.temp.add(friendListModel2);
                        i++;
                        jsonArray = jsonArray2;
                    }
                    Iterator it = InviteMemberActivity.this.temp.iterator();
                    while (it.hasNext()) {
                        FriendListModel model = (FriendListModel) it.next();
                        for (Member member1 : InviteMemberActivity.this.member) {
                            if (model.getCaller_id().equals(member1.getUserId()) && InviteMemberActivity.this.friendListModelArrayList.contains(model)) {
                                InviteMemberActivity.this.friendListModelArrayList.remove(model);
                            }
                        }
                    }
                    InviteMemberActivity inviteMemberActivity = InviteMemberActivity.this;
                    SelectableUserListAdapter selectableUserListAdapter = new SelectableUserListAdapter(InviteMemberActivity.this.friendListModelArrayList, InviteMemberActivity.this, new FriendListInterface() {


                        public void messageFriend(View view, int position) {
                            InviteMemberActivity.this.mSelectedUserIds.add(((FriendListModel) InviteMemberActivity.this.friendListModelArrayList.get(position)).getCaller_id());
                        }

                        public void unfriedFriends(View view, int position) {
                        }

                        @Override
                        public void sendGif(View v, int adapterPosition) {

                        }
                    }, false, true);
                    inviteMemberActivity.mListAdapter = selectableUserListAdapter;
                    InviteMemberActivity.this.mListAdapter.setItemCheckedChangeListener(new SelectableUserListAdapter.OnItemCheckedChangeListener() {
                        public void OnItemChecked(FriendListModel user, boolean checked) {
                            if (checked) {
                                InviteMemberActivity.this.mSelectedUserIds.add(user.getCaller_id());
                            } else {
                                InviteMemberActivity.this.mSelectedUserIds.remove(user.getCaller_id());
                            }
                        }
                    });
                    InviteMemberActivity.this.mRecyclerView.setAdapter(InviteMemberActivity.this.mListAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(InviteMemberActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getfriendlist");
                logParams.put("userid", InviteMemberActivity.this.userId);
                logParams.put("requesttype", "accepted");
                return logParams;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(r2);
    }
}
