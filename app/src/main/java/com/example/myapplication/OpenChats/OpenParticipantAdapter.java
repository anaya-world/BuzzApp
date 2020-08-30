package com.example.myapplication.OpenChats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.GroupChats.GroupParticipantActivity;
import com.example.myapplication.Intefaces.BlockUserInterface;
import com.example.myapplication.R;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;

import java.util.ArrayList;
import java.util.List;

public class OpenParticipantAdapter extends Adapter<OpenParticipantAdapter.GroupHolder> {
    BlockUserInterface blockUserInterface;
    Context context;
    GroupChannel groupChannel;
    GroupChannel mchannel;
    Member member;
    String loggedInUserId;
    boolean isAdminLogin;
    List<Member> memberslist;

    public class GroupHolder extends ViewHolder {
        TextView group_members_name;
        ImageView profile_bimg;
        ImageView ivIsAdmin, ivLeaveGroup;

        public GroupHolder(View itemView) {
            super(itemView);
            this.group_members_name = (TextView) itemView.findViewById(R.id.group_members_name);
            this.profile_bimg = (ImageView) itemView.findViewById(R.id.profile_bimg);
            this.ivIsAdmin = itemView.findViewById(R.id.iv_admin);
            this.ivLeaveGroup = itemView.findViewById(R.id.iv_leave_group);

            ivLeaveGroup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isAdminLogin) {
                        ((GroupParticipantActivity) context).showChannelOptionsDialog(
                                memberslist.get(getAdapterPosition()).getUserId());
                    }
                }
            });
        }
    }

    public OpenParticipantAdapter(List<Member> memberslist2, boolean isAdminLogin,
                                  String loggedInUserId, Context context2,
                                  BlockUserInterface blockUserInterface2) {
        this.memberslist = memberslist2;
        this.context = context2;
        this.isAdminLogin = isAdminLogin;
        this.loggedInUserId = loggedInUserId;
        this.blockUserInterface = blockUserInterface2;
    }

    @NonNull
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.groupparticipantitem, parent, false));
    }

    @SuppressLint("CheckResult")
    public void onBindViewHolder(@NonNull GroupHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_img);
        requestOptions.error(R.drawable.buzzplaceholder);

        Member memberInfo = this.memberslist.get(position);
        holder.group_members_name.setText(memberInfo.getNickname());

        Glide.with(this.context)
                .setDefaultRequestOptions(requestOptions)
                .load((memberInfo).getProfileUrl()).into(holder.profile_bimg);
        //Glide.with(this.context).load(((Member) this.memberslist.get(position)).getProfileUrl()).into(holder.profile_bimg);
        new ArrayList();
        holder.group_members_name.setOnClickListener(view ->
                OpenParticipantAdapter.this.blockUserInterface.onBlock(view, position));

        if (isAdminLogin) {
            if (memberInfo.getUserId().equalsIgnoreCase(loggedInUserId)) {
                holder.ivIsAdmin.setVisibility(View.VISIBLE);
                holder.ivLeaveGroup.setVisibility(View.GONE);
            } else {
                holder.ivIsAdmin.setVisibility(View.GONE);
                holder.ivLeaveGroup.setVisibility(View.VISIBLE);
            }
        } else if (memberInfo.getUserId().equalsIgnoreCase(loggedInUserId)) {
            holder.ivLeaveGroup.setVisibility(View.VISIBLE);
            holder.ivIsAdmin.setVisibility(View.GONE);
        } else if (memberInfo.getRole().getValue().toLowerCase()
                .equalsIgnoreCase("operator")) {
            holder.ivIsAdmin.setVisibility(View.VISIBLE);
            holder.ivLeaveGroup.setVisibility(View.GONE);
        } /*else {
            holder.ivIsAdmin.setVisibility(View.GONE);
            holder.ivLeaveGroup.setVisibility(View.GONE);
        }*/
    }

    public int getItemCount() {
        return this.memberslist.size();
    }
}
