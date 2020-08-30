package com.example.myapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.ModelClasses.Social_user_name;
import com.example.myapplication.R;
import com.example.myapplication.Utils.BaseUrl_ConstantClass;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class Social_userlistAdapter extends RecyclerView.Adapter<Social_userlistAdapter.SocialHolder> {
    ArrayList<Social_user_name> userlist = new ArrayList<>();
    Context mcontext;
    Menu item1;
    View view;
    Menu menu;
    PopupMenu popup;
    public Social_userlistAdapter(ArrayList<Social_user_name> userlist, Context mcontext) {
        this.userlist = userlist;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public Social_userlistAdapter.SocialHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_userlistitem, parent, false);
        SocialHolder socialHolder = new SocialHolder(view);
        return socialHolder;
    }
    public void follow(String frnid, String userid) {
        String str = "";

        Log.d ( "followon","1"+ frnid);
        Log.d ( "followon","2"+ userid);
        final String str2 = frnid;
        final String str3 = userid;
        StringRequest r1 = new StringRequest(Request.Method.POST, BaseUrl_ConstantClass.BASE_URL, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject= new JSONObject(response);
                    Toast.makeText(Social_userlistAdapter.this.mcontext, "user "+jsonObject.getString ("message" ), Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "followuser");
                logParams.put("frnid", str2);
                logParams.put("userid", str3);
                return logParams;
            }
        };
        MySingleTon.getInstance(this.mcontext).addToRequestQue(r1);
    }
    @Override
    public void onBindViewHolder(@NonNull Social_userlistAdapter.SocialHolder holder, final int position) {
        //  Social_user_name social_user_name=userlist.get(position);
        holder.text_user_list_nickname.setText(userlist.get(position).getText_user_list_nickname());

        //inflating menu from xml resource



        // Glide.with(mcontext).load(social_user_name.getImage_user_list_profile()).into(holder.image_user_list_profile);
        //
        //  holder.text_user_list_nickname.setText(social_user_name.getText_user_list_nickname());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_img);
        requestOptions.error(R.drawable.buzzplaceholder);

        Glide.with(mcontext)
                .setDefaultRequestOptions(requestOptions)
                .load(userlist.get(position).getImage_user_list_profile()).into(holder.image_user_list_profile);


        holder.image_user_list_profile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                popup = new PopupMenu(mcontext, view);
                popup.inflate(R.menu.menu_1);

                if(userlist.get ( position ).getFollowerStatus ()==true){
                    popup.getMenu ().findItem ( R.id.follow ).setTitle ("Unfollow"  );
                }else
                {
                    popup.getMenu ().findItem ( R.id.follow ).setTitle ("Follow"  );
                }

                if(userlist.get ( position ).getFriendStatus ().equals ( "accepted" )){
                    popup.getMenu ().findItem ( R.id.friend ).setTitle ("Already Friends!"  );

                }
                else if(userlist.get ( position ).getFriendStatus ().equals ( "pending" ))
                {
                    popup.getMenu ().findItem ( R.id.friend ).setTitle ("Request Already Sent!"  );


                }
                else
                {
                    popup.getMenu ().findItem ( R.id.friend ).setTitle ("Send Friend Request"  );
                }

                //adding click listener
                item1=popup.getMenu ();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.follow:


                                follow(userlist.get(position).getFriendid(), SharedPrefManager.getInstance(mcontext).getUser().getUser_id());

                                //handle menu1 click
                                break;
                            case R.id.friend:
                                sendFriendRequest(SharedPrefManager.getInstance(mcontext).getUser().getUser_id(),userlist.get(position).getFriendid());
                                //handle menu2 click

                                break;

                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
                return false;
            }
        });
    }
    public void sendFriendRequest(String user_id, String friend_id) {
        String str = CommonUtils.baseUrl;
        final String str2 = user_id;
        final String str3 = friend_id;
        StringRequest r1 = new StringRequest(Request.Method.POST, BaseUrl_ConstantClass.BASE_URL, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("success");
                    String string = jsonObject.getString("message");
                    if (status.equals("true")) {
                        Toast.makeText(mcontext, string, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mcontext, string, Toast.LENGTH_SHORT).show();
                    }
                    notifyDataSetChanged ();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mcontext, "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "friendrequest");
                logParams.put("userid", str2);
                logParams.put("frnid", str3);
                return logParams;
            }
        };
        MySingleTon.getInstance(this.mcontext).addToRequestQue(r1);
    }
    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class SocialHolder extends RecyclerView.ViewHolder {
        CircleImageView image_user_list_profile;
        TextView text_user_list_nickname;

        public SocialHolder(View itemView) {
            super(itemView);
            image_user_list_profile = itemView.findViewById(R.id.user_list_profile);
            text_user_list_nickname = itemView.findViewById(R.id.user_list_nickname);
//            image_user_list_profile.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    PopupMenu popup = new PopupMenu(Social_userlistAdapter.this.mcontext, image_user_list_profile);
//                    //popup.inflate(R.menu.more_menu);
//
//
//                    return false;
//                }
//            });
        }
    }
}
