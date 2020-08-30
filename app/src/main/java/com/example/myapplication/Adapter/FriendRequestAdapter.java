package com.example.myapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.Intefaces.RecyclerViewAddFriendClickListener;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    ArrayList<FriendListModel> friendListModelArrayList;
    private Context context;
    private RecyclerViewAddFriendClickListener mListener;

    public FriendRequestAdapter(ArrayList<FriendListModel> friendListModelArrayList, Context context, RecyclerViewAddFriendClickListener mListener) {
        this.friendListModelArrayList = friendListModelArrayList;
        this.mListener = mListener;
        this.context = context;
        Log.d("findfriends","FriendRequestAdapter" );
    }

    @Override
    public FriendRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_items, parent, false);

        return new FriendRequestAdapter.ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(final FriendRequestAdapter.ViewHolder holder, final int position) {
        final FriendListModel item = friendListModelArrayList.get(position);
        String user_name = item.getName();
        String sex = item.getGender();
        String secret_id = item.getSecret_id();
        String user_image = item.getImage();
        holder.searched_user_name.setText(user_name);
        holder.searched_user_gender.setText(sex);
        holder.searched_user_secretKey.setText(secret_id);
        //  holder.searched_user_mobile.setText(mobile_no);
        Glide.with(context).load(user_image).into(holder.searched_user_image);

    }

    @Override
    public int getItemCount() {
        return friendListModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView searched_user_image;
        public TextView searched_user_secretKey, searched_user_name, searched_user_gender;
        public ImageView send_friend_request, btn_unfriend;
        private RecyclerViewAddFriendClickListener mListener;


        public ViewHolder(final View itemView, RecyclerViewAddFriendClickListener recyclerViewAddFriendClickListener) {
            super(itemView);
            this.mListener = recyclerViewAddFriendClickListener;
            searched_user_secretKey = (TextView) itemView.findViewById(R.id.searched_user_secretKey);
            searched_user_gender = (TextView) itemView.findViewById(R.id.searched_user_gender);

            searched_user_image = (CircleImageView) itemView.findViewById(R.id.searched_user_image);
            searched_user_name = (TextView) itemView.findViewById(R.id.searched_user_name);
            send_friend_request = (ImageView) itemView.findViewById(R.id.send_friend_request);
            btn_unfriend = (ImageView) itemView.findViewById(R.id.btn_unfriend);
            // searched_user_mobile = (TextView) itemView.findViewById(R.id.searched_user_mobile);
            btn_unfriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onCancleFriendRequest(v, getAdapterPosition());
                }
            });

            send_friend_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onAddFriend(v, getAdapterPosition());
                }
            });

        }
    }


}

