package com.example.myapplication.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Adapter.CalluserlistAdapter;
import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ActivityUtils;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.RecyclerItemClickListener;
import com.example.myapplication.Utils.SharedPrefManager;
import com.example.myapplication.call.CallActivity;
import com.google.android.material.tabs.TabLayout;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserListQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CallUserActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ImageView chat_backarrow;
    String chk = ExifInterface.GPS_MEASUREMENT_2D;
    Menu context_menu;
    EditText et_search;
    boolean isMultiSelect = false;
    ImageView iv_search;
    List<FriendListModel> list1 = new ArrayList();
    ActionMode mActionMode;
    /* access modifiers changed from: private */
    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_multi_select_call, menu);
            CallUserActivity.this.context_menu = menu;
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() != R.id.action_call) {
                return false;
            }
            StringBuilder sb_name = new StringBuilder();
            StringBuilder sb_id = new StringBuilder();
            ArrayList<Integer> oppnentlist1 = new ArrayList<>();
            boolean appendSeparator1 = false;
            boolean appendSeparator = false;
            for (int i = 0; i < CallUserActivity.this.multiselect_list.size(); i++) {
                int size = CallUserActivity.this.multiselect_list.size();
                if (appendSeparator) {
                    sb_name.append(',');
                }
                appendSeparator = true;
                sb_name.append(((FriendListModel) CallUserActivity.this.multiselect_list.get(i)).getName());
                if (appendSeparator1) {
                    sb_id.append(',');
                }
                appendSeparator1 = true;
                sb_id.append(((FriendListModel) CallUserActivity.this.multiselect_list.get(i)).getCaller_id());
                oppnentlist1.add(Integer.valueOf(((FriendListModel) CallUserActivity.this.multiselect_list.get(i)).getCaller_id()));
            }
            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
            SimpleDateFormat sdf1 = new SimpleDateFormat("kk:mm");
            String dateString = sdf.format(Long.valueOf(date));
            String time = sdf1.format(Long.valueOf(date)).toLowerCase();
            int randomInt = new Random(System.nanoTime()).nextInt(1000000000);
            Log.d("lifecycle","CallUserActivity"+oppnentlist1);
            //App.getInstance().setSomeVariable(String.valueOf(randomInt));
//            if (oppnentlist1.size() <= 3) {
                StringBuilder sb = new StringBuilder();
                sb.append(dateString);
                StringBuilder sb2 = sb_name;
                sb.append(", ");
                sb.append(time);
                new DatabaseHelper(CallUserActivity.this).insertdata(randomInt, String.valueOf(sb_name), "", "outgoing", sb.toString(), String.valueOf(sb_id));
               // QBConferenceType conferenceType = QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
                String caller_id = SharedPrefManager.getInstance(CallUserActivity.this).getUser().getCaller_id();
//                QBRTCClient qbrtcClient = QBRTCClient.getInstance(CallUserActivity.this);
//                StringBuilder sb3 = sb_id;
//                QBConferenceType qBConferenceType = conferenceType;
                Log.d("callingdetais","2 calluseract");
//                WebRtcSessionManager.getInstance(CallUserActivity.this).setCurrentSession(qbrtcClient.createNewSessionWithOpponents(oppnentlist1, conferenceType));
//                QBRTCClient qBRTCClient = qbrtcClient;
                //CallActivity.start(CallUserActivity.this, false);
                ActivityUtils.startCallActivityAsCaller(CallUserActivity.this, oppnentlist1.get(0).toString(),multiselect_list.get(0).getName(), false);
//            } else {
//                StringBuilder sb4 = sb_id;
//                Toast.makeText(CallUserActivity.this, "selcted users can't be more than 3", Toast.LENGTH_SHORT).show();
//            }
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            CallUserActivity.this.mActionMode = null;
            CallUserActivity.this.isMultiSelect = false;
            CallUserActivity.this.multiselect_list = new ArrayList();
            CallUserActivity.this.getSupportActionBar().show();
            CallUserActivity.this.refreshAdapter();
        }
    };
    private LinearLayoutManager mLayoutManager;
    public CalluserlistAdapter mListAdapter;
    /* access modifiers changed from: private */
    public RecyclerView mRecyclerView;
    public UserListQuery mUserListQuery;
    List<FriendListModel> multiselect_list = new ArrayList();
    ArrayList<Integer> partipant_oncalllist = new ArrayList<>();
    ProgressDialog progressDialog;
   // private SharedPrefsHelper sharedPrefsHelper;
    TabLayout tabLayout;
    ArrayList<FriendListModel> temp;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_user);
        this.mRecyclerView = (RecyclerView) findViewById(R.id.recycler_select_user);
        Intent intent = getIntent();
        if (getIntent() != null) {
            this.partipant_oncalllist = (ArrayList) getIntent().getSerializableExtra("opponentslist_key");
            if (getIntent().getStringExtra("chk") != null) {
                this.chk = getIntent().getStringExtra("chk");
            }
        }
        this.iv_search = (ImageView) findViewById(R.id.iv_search);
        this.chat_backarrow = (ImageView) findViewById(R.id.chat_backarrow);
        this.mListAdapter = new CalluserlistAdapter((Context) this, false, true);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.userId = SharedPrefManager.getInstance(this).getUser().getUser_id().toString();
        this.mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        this.mRecyclerView.setLayoutManager(layoutManager);
        loadAllFriends();
        this.chat_backarrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CallUserActivity.this.finish();
            }
        });
        this.mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, this.mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (CallUserActivity.this.isMultiSelect) {
                    CallUserActivity.this.multi_select(position);
                }
            }

            public void onItemLongClick(View view, int position) {
                if (!CallUserActivity.this.isMultiSelect) {
                    CallUserActivity.this.multiselect_list = new ArrayList();
                    CallUserActivity.this.isMultiSelect = true;
                    if (CallUserActivity.this.mActionMode == null) {
                        CallUserActivity.this.mActionMode = CallUserActivity.this.startActionMode(CallUserActivity.this.mActionModeCallback);
                        CallUserActivity.this.getSupportActionBar().hide();
                    }
                }
                CallUserActivity.this.multi_select(position);
            }
        }));
    }

    private void filter(String text) {
        List<FriendListModel> filteredList = new ArrayList<>();
        for (FriendListModel user : this.list1) {
            if (user.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(user);
            }
        }
        this.mListAdapter.filterList(filteredList);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menuonly, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        ((SearchView) MenuItemCompat.getActionView(item)).setOnQueryTextListener(this);
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener()  {
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            public boolean onMenuItemActionCollapse(MenuItem item) {
                CallUserActivity.this.mListAdapter.setSearchResult(CallUserActivity.this.list1);
                return true;
            }
        });
        return true;
    }



    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        this.mListAdapter.setSearchResult(filter(this.list1, newText));
        return true;
    }

    private List<FriendListModel> filter(List<FriendListModel> models, String query) {
        String query2 = query.toLowerCase();
        List<FriendListModel> filteredModelList = new ArrayList<>();
        for (FriendListModel model : models) {
            if (model.getName().toLowerCase().contains(query2)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public void multi_select(int position) {
        if (this.mActionMode != null) {
            if (this.multiselect_list.contains(this.list1.get(position))) {
                this.multiselect_list.remove(this.list1.get(position));
            } else {
                this.multiselect_list.add(this.list1.get(position));
            }
            if (this.multiselect_list.size() > 0) {
                ActionMode actionMode = this.mActionMode;
                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(this.multiselect_list.size());
                actionMode.setTitle(sb.toString());
            } else {
                this.mActionMode.setTitle("");
            }
            refreshAdapter();
        }
    }

    public void refreshAdapter() {
        this.mListAdapter.selectedmUsers = this.multiselect_list;
        this.mListAdapter.mUsers = this.list1;
        this.mListAdapter.notifyDataSetChanged();
    }

    private void loadAllFriends() {
        String str = CommonUtils.baseUrl;
        this.list1 = new ArrayList();
        this.temp = new ArrayList<>();
        StringRequest r2 = new StringRequest(1, CommonUtils.baseUrl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onResponse(String response) {
                String str = response;
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    try {
                        Log.d("hgvhgvg", str);
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
                            String optString = user_details.optString(UserDetailsActivity.MY_PREFS_NAME);
                            String optString2 = user_details.optString("workfor");
                            FriendListModel friendListModel = new FriendListModel(name, user_image, gender, secrate_id, caller_id, dob, anniversary, email, marital, mobileno, user_id, user_details.optString("frnid"), user_details.optString("frns"));
                            FriendListModel friendListModel2 = friendListModel;
                            JSONArray jsonArray2 = jsonArray;
                            CallUserActivity.this.list1.add(friendListModel2);
                            JSONObject jsonObject2 = jsonObject;
                            try {
                                JSONObject jSONObject = user_details;
                                String str2 = interests;
                                CallUserActivity.this.mListAdapter = new CalluserlistAdapter(CallUserActivity.this.list1, (Context) CallUserActivity.this, (FriendListInterface) new FriendListInterface() {


                                    public void messageFriend(View view, int position) {
                                        CallUserActivity.this.message((FriendListModel) CallUserActivity.this.list1.get(position));
                                    }

                                    public void unfriedFriends(View view, int position) {
                                    }

                                    @Override
                                    public void sendGif(View v, int adapterPosition) {

                                    }
                                });
                                CallUserActivity.this.temp.add(friendListModel2);
                                i++;
                                jsonArray = jsonArray2;
                                jsonObject = jsonObject2;
                                String str3 = response;
                            } catch (Exception e) {
                                e = e;
                                JSONObject jSONObject2 = jsonObject2;
                                e.printStackTrace();
                            }
                        }
                        JSONObject jsonObject3 = jsonObject;
                        if (CallUserActivity.this.chk.equals("1")) {
                            Iterator it = CallUserActivity.this.temp.iterator();
                            while (it.hasNext()) {
                                FriendListModel model = (FriendListModel) it.next();
                                Iterator it2 = CallUserActivity.this.partipant_oncalllist.iterator();
                                while (it2.hasNext()) {
                                    if (model.getCaller_id().equals(((Integer) it2.next()).toString()) && CallUserActivity.this.list1.contains(model)) {
                                        CallUserActivity.this.list1.remove(model);
                                    }
                                }
                            }
                        }
                        CallUserActivity.this.mRecyclerView.setAdapter(CallUserActivity.this.mListAdapter);
                        JSONObject jSONObject3 = jsonObject3;
                    } catch (JSONException e2) {

                        JSONObject jSONObject4 = jsonObject;
                        e2.printStackTrace();
                    }
                } catch (JSONException e3) {

                    e3.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CallUserActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getfriendlist");
                logParams.put("userid", CallUserActivity.this.userId);
                logParams.put("requesttype", "accepted");
                return logParams;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(r2);
    }

    public void message(FriendListModel friendListModel) {
        List<String> users = new ArrayList<>();
        users.add(friendListModel.getSecret_id());
        users.add(this.userId);
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(false).addUserIds(users).setName(friendListModel.getName()).setCoverUrl(friendListModel.getImage()), new GroupChannel.GroupChannelCreateHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e == null) {
                    Intent intent = new Intent(CallUserActivity.this, Main2Activity.class);
                    intent.putExtra("URL", groupChannel.getUrl());
                    CallUserActivity.this.startActivity(intent);
                }
            }
        });
    }
}
