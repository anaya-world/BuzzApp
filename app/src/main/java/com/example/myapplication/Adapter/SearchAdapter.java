package com.example.myapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.myapplication.Intefaces.RecyclerViewAddFriendClickListener;
import com.example.myapplication.ModelClasses.SearchedUsersModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.BaseUrl_ConstantClass;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends Adapter<SearchAdapter.ViewHolder> {
    private Context context;
    private Boolean isClicked = Boolean.valueOf(false);
    private RecyclerViewAddFriendClickListener mListener;
    ArrayList<SearchedUsersModel> searchedUsersModelArrayList;
    private String user_id;
    private boolean check;
    final String LOGIN_URL = BaseUrl_ConstantClass.BASE_URL;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView cancle_friend_request;
        /* access modifiers changed from: private */
        public RecyclerViewAddFriendClickListener mListener;
        public TextView searched_user_gender;
        public CircleImageView searched_user_image;
        public TextView searched_user_name;
        public TextView searched_user_secretKey;
        public ImageView send_friend_request;

        public ViewHolder(View itemView, RecyclerViewAddFriendClickListener recyclerViewAddFriendClickListener) {
            super(itemView);
            this.mListener = recyclerViewAddFriendClickListener;
            this.searched_user_secretKey = (TextView) itemView.findViewById(R.id.searched_user_secretKey);
            this.searched_user_gender = (TextView) itemView.findViewById(R.id.searched_user_gender);
            this.searched_user_image = (CircleImageView) itemView.findViewById(R.id.searched_user_image);
            this.searched_user_name = (TextView) itemView.findViewById(R.id.searched_user_name);
            this.send_friend_request = (ImageView) itemView.findViewById(R.id.send_friend_request);
            this.cancle_friend_request = (ImageView) itemView.findViewById(R.id.cancle_friend_request);


        }
    }

    public SearchAdapter(ArrayList<SearchedUsersModel> searchedUsersModelArrayList2, Context context2, RecyclerViewAddFriendClickListener mListener2) {
        this.searchedUsersModelArrayList = searchedUsersModelArrayList2;
        this.mListener = mListener2;
        this.context = context2;
        Log.d("findfriends", "SearchAdapter" + mListener2);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("findfriends", "onCreateViewHolder" + mListener);
        user_id = SharedPrefManager.getInstance(context).getUser().getUser_id().toString();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.searched_user_list_items, parent, false), this.mListener);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("findfriends", "onBindViewHolder" + mListener);
        SearchedUsersModel item = (SearchedUsersModel) this.searchedUsersModelArrayList.get(position);
        String user_name = item.getName();
        String sex = item.getGender();
        String secret_id = item.getSecret_id();
        String user_image = item.getImage();
        holder.searched_user_name.setText(user_name);
        holder.searched_user_gender.setText(sex);
        holder.searched_user_secretKey.setText(secret_id);
        Glide.with(this.context).load(user_image).into((ImageView) holder.searched_user_image);
        if (((SearchedUsersModel) this.searchedUsersModelArrayList.get(position)).getStatus().equals("")) {
            holder.cancle_friend_request.setVisibility(View.GONE);
            holder.send_friend_request.setVisibility(View.VISIBLE);
        } else if (((SearchedUsersModel) this.searchedUsersModelArrayList.get(position)).getStatus().equals("pending")) {
            holder.cancle_friend_request.setVisibility(View.VISIBLE);
            holder.send_friend_request.setVisibility(View.GONE);
        } else if (((SearchedUsersModel) this.searchedUsersModelArrayList.get(position)).getSecret_id().equals(SharedPrefManager.getInstance(this.context).getUser().getUser_id())) {
            holder.cancle_friend_request.setVisibility(View.GONE);
            holder.send_friend_request.setVisibility(View.GONE);
        } else {
            holder.cancle_friend_request.setVisibility(View.GONE);
            holder.send_friend_request.setVisibility(View.GONE);
        }
        holder.send_friend_request.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {


                StringRequest stringRequestLogIn = new StringRequest(Request.Method.POST, LOGIN_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("success");
                                    String message = jsonObject.getString("message");
                                    if (status.equals("true")) {
                                        holder.cancle_friend_request.setVisibility(View.VISIBLE);
                                        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
                                    } else {

                                        Toast.makeText(context, "Failed to add friend", Toast.LENGTH_SHORT).show();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                check = false;
                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    Toast.makeText(context, "it seems your internet is slow",
                                            Toast.LENGTH_LONG).show();
                                } else if (error instanceof AuthFailureError) {
                                    //TODO
                                } else if (error instanceof ServerError) {
                                    Toast.makeText(context, "sorry for inconvenience, Server is not working",
                                            Toast.LENGTH_LONG).show();
                                } else if (error instanceof NetworkError) {
                                    Toast.makeText(context, "it seems your internet is slow",
                                            Toast.LENGTH_LONG).show();
                                } else if (error instanceof ParseError) {
                                    //TODO
                                }
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> logParams = new HashMap<>();
                        logParams.put("action", "friendrequest");
                        logParams.put("userid", user_id);
                        logParams.put("frnid", searchedUsersModelArrayList.get(position).getFriend_id());

                        return logParams;
                    }
                };

                MySingleTon.getInstance(context).addToRequestQue(stringRequestLogIn);
            }
        });
        holder.cancle_friend_request.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                StringRequest stringRequestLogIn = new StringRequest(Request.Method.POST, LOGIN_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("success");
                                    String message = jsonObject.getString("message");
                                    if (status.equals("true")) {
                                        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
                                        holder.cancle_friend_request.setVisibility(View.GONE);
                                        holder.send_friend_request.setVisibility(View.VISIBLE);

                                    } else {
                                        check = false;
                                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                check = false;
                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    Toast.makeText(context, "it seems your network is slow",
                                            Toast.LENGTH_LONG).show();
                                } else if (error instanceof AuthFailureError) {
                                    //TODO
                                } else if (error instanceof ServerError) {
                                    Toast.makeText(context, "sorry for inconvenience, Server is not working",
                                            Toast.LENGTH_LONG).show();
                                } else if (error instanceof NetworkError) {
                                    Toast.makeText(context, "it seems your network is slow",
                                            Toast.LENGTH_LONG).show();
                                } else if (error instanceof ParseError) {
                                    //TODO
                                }
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> logParams = new HashMap<>();
                        logParams.put("action", "removefriend");
                        logParams.put("userid", user_id);
                        logParams.put("frnid", searchedUsersModelArrayList.get(position).getFriend_id());
                        return logParams;
                    }
                };

                MySingleTon.getInstance(context).addToRequestQue(stringRequestLogIn);
            }
        });
    }

    public int getItemCount() {
        return this.searchedUsersModelArrayList.size();
    }
}
