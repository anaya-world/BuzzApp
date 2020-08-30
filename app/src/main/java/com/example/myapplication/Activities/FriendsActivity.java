package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Fragments.FriendRequestFragment;
import com.example.myapplication.Fragments.FriendsListFragments;
import com.example.myapplication.Fragments.SearchFragments;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsActivity extends AppCompatActivity {
    ImageView iv_back_to_friends;
    ImageButton search_button_chat;
    ArrayList<FriendListModel> searchedUsersModelArrayList;
    TabLayout tabLayout;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        this.user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();
        this.tabLayout = (TabLayout) findViewById(R.id.tabs);
        this.iv_back_to_friends = (ImageView) findViewById(R.id.iv_back_to_friends);
        this.search_button_chat = (ImageButton) findViewById(R.id.search_button_chat);
        this.search_button_chat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FriendsActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragments()).addToBackStack(null).commit();
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FriendsListFragments()).addToBackStack(null).commit();
        this.iv_back_to_friends.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FriendsActivity.this.finish();
            }
        });
        includetabs();
    }

    private void includetabs() {
        this.tabLayout.addTab(this.tabLayout.newTab().setText((CharSequence) "Friends"));
        this.tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.app_color_voilet));
        this.tabLayout.addTab(this.tabLayout.newTab().setText((CharSequence) "Friend Request"));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FriendsListFragments()).addToBackStack(null).commit();
        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                if (FriendsActivity.this.tabLayout.getSelectedTabPosition() == 0) {
                    FriendsActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FriendsListFragments()).addToBackStack(null).commit();
                } else if (FriendsActivity.this.tabLayout.getSelectedTabPosition() == 1) {
                    FriendsActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FriendRequestFragment()).addToBackStack(null).commit();
                }
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }

            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void cancleFriendRequest() {
        String str = CommonUtils.baseUrl;
        this.searchedUsersModelArrayList = new ArrayList<>();
        StringRequest r2 = new StringRequest(1, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    if (status.equals("true")) {
                        FriendsActivity friendsActivity = FriendsActivity.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Success");
                        sb.append(message);
                        Toast.makeText(friendsActivity, sb.toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(FriendsActivity.this, "", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FriendsActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "removefriend");
                logParams.put("userid", FriendsActivity.this.user_id);
                logParams.put("frnid", "");
                return logParams;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(r2);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
