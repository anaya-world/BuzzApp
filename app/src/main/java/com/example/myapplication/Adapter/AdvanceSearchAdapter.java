package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdvanceSearchAdapter extends RecyclerView.Adapter<AdvanceSearchAdapter.ViewHolder> {

    ArrayList<FriendListModel> friendListModelArrayList;
    private Context context;

    public AdvanceSearchAdapter(ArrayList<FriendListModel> friendListModelArrayList, Context context) {
        this.friendListModelArrayList = friendListModelArrayList;
        this.context = context;
    }

    @Override
    public AdvanceSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list, parent, false);
        return new AdvanceSearchAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AdvanceSearchAdapter.ViewHolder holder, final int position) {
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
        public ImageView send_friend_request, cancle_friend_request;



        public ViewHolder(final View itemView) {
            super(itemView);

            searched_user_secretKey = (TextView) itemView.findViewById(R.id.searched_user_secretKey);
            searched_user_gender = (TextView) itemView.findViewById(R.id.searched_user_gender);

            searched_user_image = (CircleImageView) itemView.findViewById(R.id.searched_user_image);
            searched_user_name = (TextView) itemView.findViewById(R.id.searched_user_name);
            send_friend_request = ( ImageView ) itemView.findViewById(R.id.send_friend_request);
            cancle_friend_request = (ImageView ) itemView.findViewById(R.id.cancle_friend_request);
            // searched_user_mobile = (TextView) itemView.findViewById(R.id.searched_user_mobile);

        }
    }
}

