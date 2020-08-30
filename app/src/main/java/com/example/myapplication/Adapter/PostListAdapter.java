package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Activities.CommentPostActivity;
import com.example.myapplication.Fragments.SocialFragment;
import com.example.myapplication.Intefaces.HideInterface;
import com.example.myapplication.Intefaces.LikeInterface;
import com.example.myapplication.Intefaces.LimitIncrease;
import com.example.myapplication.ModelClasses.PostListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.BaseUrl_ConstantClass;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import static org.webrtc.ContextUtils.getApplicationContext;

public class PostListAdapter extends Adapter<PostListAdapter.ViewHolder> {
    private SocialFragment socialFragment;
    private  Collection<PostListModel> friendListModelArrayLists;
    private  HashMap<String, PostListModel> friendListModelArrayListMap;
    /* access modifiers changed from: private */
    public Context context;
    LimitIncrease limitIncrease;
    ArrayList<PostListModel> friendListModelArrayList;
    HideInterface hideInterface;
    LikeInterface likeInterface;
    String postid;

    public static final int LOADER = 100;
    public static final int LIST = 200;


    public JCVideoPlayerStandard playbackView;
    SharedPreferences sharedPreferences;
    public static final int THUMBNAIL_HEIGHT = 48;
    public static final int THUMBNAIL_WIDTH = 66;

    public void startVideos() {
        playbackView.ivThumb.performClick();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        /* access modifiers changed from: private */
        public ImageView iv_comment;

        /* access modifiers changed from: private */
        public ImageView iv_delete;
        /* access modifiers changed from: private */
        public ImageView iv_deslike;
        /* access modifiers changed from: private */
        public ImageView iv_like;
        /* access modifiers changed from: private */
        public ImageView iv_more;
        public LikeInterface likeInterface2;
        LikeInterface likeInterface;
        /* access modifiers changed from: private */
        public ImageView mediaimg;
        public JCVideoPlayerStandard playbackView;
        /* access modifiers changed from: private */
        public TextView postBy;
        /* access modifiers changed from: private */
        public TextView postContent;
        /* access modifiers changed from: private */
        public CircleImageView profile_img;
        /* access modifiers changed from: private */
        public TextView tv_date;
        /* access modifiers changed from: private */
        public TextView tv_deslikes;
        /* access modifiers changed from: private */
        public TextView tv_like;
        public  TextView tv_comment;

        public ViewHolder(View itemView) {
            super(itemView);
            this.postContent = (TextView) itemView.findViewById(R.id.postContent);
            this.postBy = (TextView) itemView.findViewById(R.id.postBy);
            this.tv_date = (TextView) itemView.findViewById(R.id.date);
            this.tv_like = (TextView) itemView.findViewById(R.id.tv_likes);
            this.profile_img = (CircleImageView) itemView.findViewById(R.id.post_profile_img);
            iv_like = (ImageView) itemView.findViewById(R.id.iv_likes);
            iv_deslike = (ImageView) itemView.findViewById(R.id.iv_deslike);
            this.iv_comment = (ImageView) itemView.findViewById(R.id.iv_comment);
            this.iv_more = (ImageView) itemView.findViewById(R.id.iv_more);
            // this.iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);
            this.playbackView = (JCVideoPlayerStandard) itemView.findViewById(R.id.playbackView);
            this.mediaimg = (ImageView) itemView.findViewById(R.id.mediaimg);
            // this.iv_deslike = (ImageView) itemView.findViewById(R.id.iv_deslike);
            this.tv_deslikes = (TextView) itemView.findViewById(R.id.tv_deslikes);
            this.tv_deslikes = (TextView) itemView.findViewById(R.id.tv_deslikes);
            this.tv_comment=(TextView)itemView.findViewById(R.id.tv_comment);

//            this.iv_deslike.setOnClickListener(new OnClickListener() {
//                public void onClick(View view) {
//                    likeInterface2.likePost(itemView, ViewHolder.this.getAdapterPosition(), "deslike");
//                }
//            });
//            this.iv_like.setOnClickListener(new OnClickListener() {
//                public void onClick(View v) {
//                    likeInterface2.likePost(itemView, ViewHolder.this.getAdapterPosition(), "like");
//                }
//            });
        }
    }
    public void setData(ArrayList<PostListModel>  newpostdata){
        this.friendListModelArrayList=newpostdata;
    }

    public void setData(HashMap<String, PostListModel> newpostdata){
        this.friendListModelArrayLists = newpostdata.values();
        this.friendListModelArrayList=new ArrayList<>(friendListModelArrayLists);
    }
    public void setNewData(PostListModel newpostdata , String post_id){
        Log.d("setNewData", "call "+post_id);

        for(int j = 0; j< friendListModelArrayList.size (); j++){
            Log.d("setNewData", "post_id "+friendListModelArrayList.get ( j ).getPost_id ());
            if(friendListModelArrayList.get ( j ).getPost_id ().equals ( post_id )){
                friendListModelArrayList.get ( j ).setPost_likes ( newpostdata.getPost_likes () );
                friendListModelArrayList.get ( j ).setPost_deslikes ( newpostdata.getPost_deslikes () );
                friendListModelArrayList.get(j).setComment(newpostdata.getComment());
                // friendListModelArrayList.get ( j ).setYou_unliked ( newpostdata.getYou_unliked () );
                // friendListModelArrayList.get ( j ).setYou_liked ( newpostdata.getYou_liked () );
                Log.d("setNewData", post_id+" post_id12" + friendListModelArrayList.get ( j ).getPost_likes ());
                Log.d("setNewData", post_id+" post_id13" + friendListModelArrayList.get ( j ).getPost_deslikes ());
                Log.d("setNewData", post_id+" post_id14" + friendListModelArrayList.get ( j ).getYou_liked ());
                Log.d("setNewData", post_id+" post_id15" + friendListModelArrayList.get ( j ).getYou_unliked ());
                notifyItemChanged (j);
                //iv_like.setImageResource(R.drawable.heart_red);
//                if (newpostdata.getYou_liked ().contains("Yes")) {
//                    iv_like.setImageResource(R.drawable.heart_red);
//                } else {
//                    iv_like.setImageResource(R.drawable.heartbandw);
//                }
//                if (newpostdata.getYou_liked ().contains("Yes")) {
//                    iv_deslike.setImageResource(R.drawable.download);
//
//                } else {
//                    iv_deslike.setImageResource(R.drawable.ic_deslike);
//                }
            }
        }




//        PostDiffUtils postDiffUtils=new PostDiffUtils (this.friendListModelArrayList,newpostdata);
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff ( postDiffUtils );
//        diffResult.dispatchUpdatesTo ( this );
        //this.friendListModelArrayList = newpostdata;





    }
    public PostListAdapter(SocialFragment socialFragment , ArrayList<PostListModel> friendListModelArrayList2, Context context2, LikeInterface likeInterface2, HideInterface hideInterface2) {
        //   this.friendListModelArrayList = friendListModelArrayList2;
        this.socialFragment=socialFragment;
        limitIncrease=(LimitIncrease)socialFragment;
        this.context = context2;
        this.likeInterface = likeInterface2;
        this.hideInterface = hideInterface2;
    }
    public PostListAdapter(Context context2, LikeInterface likeInterface2, HideInterface hideInterface2) {
        this.friendListModelArrayLists = BaseUrl_ConstantClass.postdata.values();
        this.friendListModelArrayList=new ArrayList<>(friendListModelArrayLists);
        this.context = context2;
        this.likeInterface = likeInterface2;
        this.hideInterface = hideInterface2;
    }
    public PostListAdapter(Map<String,PostListModel> data, Context context2, LikeInterface likeInterface2, HideInterface hideInterface2) {
        this.friendListModelArrayLists = data.values();
        this.friendListModelArrayList=new ArrayList<>(friendListModelArrayLists);
        this.context = context2;
        this.likeInterface = likeInterface2;
        this.hideInterface = hideInterface2;

    }

    @Override
    public int getItemViewType(int position) {
        Log.d ( "postion" ,"1"+position);
        if(position==friendListModelArrayList.size ()){
            return LOADER;
        }
        else{
            return LIST;
        }

    }


    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
//            case LOADER:
//                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.loader_progress, parent, false));
            case LIST:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.postlist1, parent, false));
            default:

                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.postlist1, parent, false));
        }

    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {

        super.onViewAttachedToWindow ( holder );
        if(holder.playbackView!=null) {
            Log.d ( "onViewAttachedToWindow" , "v-" + holder.playbackView.getVisibility () );
            if(holder.playbackView.getVisibility ()==View.VISIBLE) {
                Log.d ( "onViewAttachedToWindow" , "v1-" + holder.playbackView.getVisibility () );
                holder.playbackView.ivThumb.performClick ();

            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {

        super.onViewDetachedFromWindow ( holder );
        Log.d ( "ondetach","1"+holder );

        holder.playbackView.releaseAllVideos ();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        switch (getItemViewType ( position )){
            case LOADER:

                break;
            case LIST:
                final PostListModel postListModel = (PostListModel) this.friendListModelArrayList.get(position);
                Log.d ( "onBindViewHolder ",""+position);
                Log.d ( "onBindViewHolder ","postid" +postListModel.getPost_id ());
                Log.d ( "onBindViewHolder ","postid" +postListModel.getPost_likes ());
                Log.d ( "onBindViewHolder ","postid" +postListModel.getPost_deslikes ());

                holder.postBy.setText(postListModel.getPostedby());
                holder.tv_date.setText(postListModel.getPosted_date());
                holder.tv_like.setText(postListModel.getPost_likes());
                holder.postContent.setText ( postListModel.getContent () );
                holder.tv_comment.setText(postListModel.getComment());


                if(position==friendListModelArrayList.size ()-2){

                    limitIncrease.loadNext ( );
                }

//        holder.iv_deslike.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//                holder.likeInterface2.likePost(view,hol)   // holder.this.getp, "deslike");
//            }
//        });
//        holder.iv_like.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                holder.likeInterface2.likePost(itemView, holder.this.getAdapterPosition(), "like");
//            }
//        });

                holder.iv_comment.setOnClickListener(new OnClickListener()
                {

                    public void onClick(View view) {
                        Intent intent = new Intent(PostListAdapter.this.context, CommentPostActivity.class);
                        intent.putExtra("userid", postListModel.getPosted_byuserid());
                        intent.putExtra("postid", postListModel.getPost_id());
                        PostListAdapter.this.context.startActivity(intent);


                    }

                });
                this.postid = postListModel.getPost_id();
                Log.d("setNewData", " absdf" + postListModel.getMediatype());
                if (postListModel.getMediatype().equals("image")) {
                    holder.mediaimg.setVisibility(View.VISIBLE);
                    holder.playbackView.setVisibility(View.GONE);
                    Glide.with(this.context).load(postListModel.getMediaimage()).into(holder.mediaimg);


//            byte[] imageData = null;
//
//            try
//            {
//
//                final int THUMBNAIL_SIZE = 64;
//
//                FileInputStream fis = new FileInputStream("fgjjg");
//                Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
//
//                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                imageData = baos.toByteArray();
//
//            }
//            catch(Exception ex) {
//
//            }
                } else if (postListModel.getMediatype().equals("gif")) {
                    holder.mediaimg.setVisibility(View.VISIBLE);
                    holder.playbackView.setVisibility(View.GONE);
                    Glide.with(this.context).load(postListModel.getMediaimage()).into(holder.mediaimg);
                }
                else if (postListModel.getMediatype().equals("video")) {
                    holder.mediaimg.setVisibility(View.GONE);
                    holder.playbackView.setVisibility(View.VISIBLE);
                    //ImageLoader.getInstance().displayImage(postListModel.getMediaimage(),  holder.playbackView.ivThumb);
                    Glide.with(this.context).load(postListModel.getThumbnils ()).into(holder.playbackView.ivThumb);
                    holder.playbackView.setUp(postListModel.getMediaVideo(), "");
                    holder.playbackView.releaseAllVideos();
                   // AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    //playbackView.ivStart.performClick();




                    //       playbackView.performClick();
                } else {
                    holder.mediaimg.setVisibility(View.GONE);
                    holder.playbackView.setVisibility(View.GONE);
                }
                holder.iv_more.setOnClickListener(new OnClickListener()
                {
                    public void onClick(View view) {
                        PostListAdapter.this.hidepost(postListModel.getPost_id(), SharedPrefManager.getInstance(PostListAdapter.this.context).getUser().getUser_id().toString());

//                      //  PopupMenu popup = new PopupMenu(PostListAdapter.this.context, holder.iv_more);
//                        popup.inflate(R.menu.more_menu);
//                        popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
//                        {
//                            public boolean onMenuItemClick(MenuItem item)
//                            {
//                                switch (item.getItemId())
//                                {
//                                    case R.id.friendrequest /*2131296618*/:
//                                        PostListAdapter.this.sendFriendRequest(SharedPrefManager.getInstance(PostListAdapter.this.context).getUser().getUser_id().toString(), postListModel.getPosted_byuserid());
//                                        return true;
//                                    case R.id.hidepost /*2131296646*/:
//                                        PostListAdapter.this.hidepost(postListModel.getPost_id(), SharedPrefManager.getInstance(PostListAdapter.this.context).getUser().getUser_id().toString());
//                                        return true;
//                                    case R.id.reportpost /*2131296947*/:
//                                        PostListAdapter.this.reportpost(postListModel.getPost_id(), SharedPrefManager.getInstance(PostListAdapter.this.context).getUser().getUser_id().toString());
//                                        return true;
//                                    case R.id.unfollow /*2131297245*/:
//                                        PostListAdapter.this.follow (postListModel.getPosted_byuserid(),SharedPrefManager.getInstance(PostListAdapter.this.context).getUser().getUser_id().toString());
//                                        return true;
//                                    default:
//                                        return false;
//                                }
//                            }
//                        });
//                        popup.show();
                    }
                });
                if (postListModel.getYou_liked().contains("Yes")) {
                    holder.iv_like.setImageResource(R.drawable.heart_red);
                } else if (postListModel.getYou_liked().matches("You Already Liked")){
                    holder.iv_like.setImageResource(R.drawable.heartbandw);
                }else {
                    holder.iv_like.setImageResource(R.drawable.heartbandw);

                }
                if (postListModel.getYou_unliked().contains("Yes")) {
                    holder.iv_deslike.setImageResource(R.drawable.ic_red_dislike);

                } else if (postListModel.getYou_unliked().matches("You Already Disliked")){
                    holder.iv_deslike.setImageResource(R.drawable.ic_black_dislike);
                }else{
                    holder.iv_deslike.setImageResource(R.drawable.ic_black_dislike);
                }
                holder.tv_deslikes.setText(postListModel.getPost_deslikes());
                if (postListModel.getPosted_byuserid().equals(SharedPrefManager.getInstance(this.context).getUser().getUser_id().toString())) {
//            holder.iv_delete.setVisibility(View.VISIBLE);
                } else {
                    //      holder.iv_delete.setVisibility(View.GONE);
                }
                //     holder.iv_delete.setOnClickListener(new OnClickListener() {
                //     public void onClick(View view) {
                //          PostListAdapter.this.hidepost(postListModel.getPost_id(), SharedPrefManager.getInstance(PostListAdapter.this.context).getUser().getUser_id().toString());
                //       }
                // });
                // ImageUtils.displayRoundImageFromUrl(this.context, postListModel.getPost_profileimg(), holder.profile_img);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.profile_img);
                requestOptions.error(R.drawable.buzzplaceholder);

                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(postListModel.getPost_profileimg ().trim()).into(holder.profile_img);


                holder.iv_deslike.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        likeInterface.likePost(holder.itemView, holder.getAdapterPosition(), "deslike");
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                });
                holder.iv_like.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        likeInterface.likePost(holder.itemView, holder.getAdapterPosition(), "like");
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                });


                break;
        }

    }



    public int getItemCount() {

        return this.friendListModelArrayList.size()+1;
    }


    public void stopallvideo(){

        playbackView.releaseAllVideos ();






    }
    public void setSearchResult(ArrayList<PostListModel> result) {
        this.friendListModelArrayList = result;
        notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void hidepost(String postId, String userid) {
        final String str2 = postId;
        final String str3 = userid;
        StringRequest r1 = new StringRequest(StringRequest.Method.POST, BaseUrl_ConstantClass.BASE_URL, new Listener<String>() {
            public void onResponse(String response) {
                try {
                    new JSONObject(response);
                    PostListAdapter.this.hideInterface.HidePost("hide");
                } catch (JSONException e) {
                }
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "hidepost");
                logParams.put("post_id", str2);
                logParams.put("userid", str3);
                return logParams;
            }
        };
        MySingleTon.getInstance(this.context).addToRequestQue(r1);
    }

    /* access modifiers changed from: private */
    public void reportpost(String postId, String userid) {
        final String str2 = postId;
        final String str3 = userid;
        StringRequest r1 = new StringRequest(Request.Method.POST, BaseUrl_ConstantClass.BASE_URL, new Listener<String>() {
            public void onResponse(String response) {
                try {
                    new JSONObject(response);
                    Toast.makeText(PostListAdapter.this.context, "You have successfully reported a post", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                }
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "reportpost");
                logParams.put("post_id", str2);
                logParams.put("userid", str3);
                return logParams;
            }
        };
        MySingleTon.getInstance(this.context).addToRequestQue(r1);
    }

    /* access modifiers changed from: private */
    public void sendFriendRequest(String user_id, String friend_id) {
        String str = CommonUtils.baseUrl;
        final String str2 = user_id;
        final String str3 = friend_id;
        StringRequest r1 = new StringRequest(Request.Method.POST, BaseUrl_ConstantClass.BASE_URL, new Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("success");
                    String string = jsonObject.getString("message");
                    if (status.equals("true")) {
                        Toast.makeText(PostListAdapter.this.context, "Friend request sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PostListAdapter.this.context, "", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PostListAdapter.this.context, "", Toast.LENGTH_SHORT).show();
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
        MySingleTon.getInstance(this.context).addToRequestQue(r1);
    }

    /* access modifiers changed from: private */
    public void follow(String postId, String userid) {
        String str = "";
        Log.d ( "followon","3"+ postId);
        Log.d ( "followon","4"+ userid);
        final String str2 = postId;
        final String str3 = userid;
        StringRequest r1 = new StringRequest(Request.Method.POST, BaseUrl_ConstantClass.BASE_URL, new Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject= new JSONObject(response);
                    Toast.makeText(PostListAdapter.this.context, "user "+jsonObject.getString ("message" ), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                }
            }
        }, new ErrorListener() {
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
        MySingleTon.getInstance(this.context).addToRequestQue(r1);
    }
}
