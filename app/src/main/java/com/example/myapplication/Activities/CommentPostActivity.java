package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Adapter.CommentAdapter;
import com.example.myapplication.ModelClasses.CommentModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.BaseUrl_ConstantClass;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentPostActivity extends AppCompatActivity {
    ImageView button_group_comment;
    ArrayList<CommentModel> commentModelslist;
    ImageView iv_profilepic;
    ImageView ivprofileimage;
    String postId;
    RecyclerView recyclerView;
    EditText textcontent;
    TextView tvfriendname;
    String content;

    String userId;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_post);
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this.textcontent = (EditText) findViewById(R.id.edittext_comment);
        this.ivprofileimage = (ImageView) findViewById(R.id.ivprofileimage);
        this.iv_profilepic = (ImageView) findViewById(R.id.iv_profilepic);
        this.tvfriendname = (TextView) findViewById(R.id.tvfriendname);
        this.button_group_comment = (ImageView) findViewById(R.id.button_group_comment);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this, 1, false));
        Intent intent = getIntent();
        this.postId = intent.getStringExtra("postid");
        this.userId = intent.getStringExtra("userid");
        this.button_group_comment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                CommentPostActivity.this.content = CommentPostActivity.this.textcontent.getText().toString();
                if(!content.equals ( "" )){
                    CommentPostActivity.this.commentpost();



                }else {
                    Toast.makeText(CommentPostActivity.this.getApplication(),"Please enter comment",Toast.LENGTH_SHORT).show();
                }


                // textcontent.setText("");

            }
        });
        loadComments();
        loadImage();


    }

    private void loadImage() {
        String str = CommonUtils.baseUrl;
        user_id = SharedPrefManager.getInstance(this).getUser().getUser_id().toString();
        this.tvfriendname.setText(SharedPrefManager.getInstance(this).getUser().getUser_name().toString());
        final String str2 = user_id;
        StringRequest r2 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.optString("success");
                    String optString = jsonObject.optString("message");
                    String user_image = jsonObject.optString("user_img");
                    if (!status.equals("true")) {
                        return;
                    }
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.profile_img);
                    requestOptions.error(R.drawable.buzzplaceholder);

                    Glide.with(CommentPostActivity.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(user_image.trim()).into(ivprofileimage);
                    Glide.with(CommentPostActivity.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(user_image.trim()).into(iv_profilepic);

//                    if (user_image != null) {
//                        Glide.with((FragmentActivity) CommentPostActivity.this).load(user_image).into(CommentPostActivity.this.c);
//                        Glide.with((FragmentActivity) CommentPostActivity.this).load(user_image).into(CommentPostActivity.this.iv_profilepic);
//                        return;
//                    }
//                    CommentPostActivity.this.ivprofileimage.setImageResource(R.drawable.ic_logo_pink);
//                    CommentPostActivity.this.iv_profilepic.setImageResource(R.drawable.ic_logo_pink);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CommentPostActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getimg");
                logParams.put("userid", str2);
                return logParams;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(r2);
    }

    /* access modifiers changed from: private */
    public void commentpost() {
        StringRequest r1 = new StringRequest(StringRequest.Method.POST, BaseUrl_ConstantClass.BASE_URL , new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    textcontent.setText("");
                    CommentPostActivity.this.loadComments();
                    new JSONObject(response);
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
                logParams.put("action", "postcomments");
                logParams.put("userid", user_id);
                logParams.put("post_id", CommentPostActivity.this.postId);
                logParams.put("content", CommentPostActivity.this.textcontent.getText().toString());
                return logParams;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(r1);
    }

    /* access modifiers changed from: private */
    public void loadComments() {
        String str = CommonUtils.baseUrl;
        this.commentModelslist = new ArrayList<>();
        StringRequest r2 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONObject(response).getJSONArray("search_result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject user_details = jsonArray.getJSONObject(i);
                        CommentModel commentModel = new CommentModel(user_details.optString("userid"), user_details.optString("name"), user_details.optString("secrate_id"), user_details.optString("mobileno"), user_details.optString("post_comments"), user_details.optString("post_date"), user_details.optString("user_img"));
                        CommentPostActivity.this.commentModelslist.add(commentModel);
                    }
                    CommentAdapter commentAdapter = new CommentAdapter(CommentPostActivity.this.commentModelslist, CommentPostActivity.this);
                    LinearLayoutManager layoutManager1 = new LinearLayoutManager(CommentPostActivity.this);
                    layoutManager1.setReverseLayout(true);
                    CommentPostActivity.this.recyclerView.setLayoutManager(layoutManager1);
                    CommentPostActivity.this.recyclerView.setAdapter(commentAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CommentPostActivity.this, "Something Went Wong", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getcomments");
                logParams.put("post_id", CommentPostActivity.this.postId);
                return logParams;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(r2);
    }
}
