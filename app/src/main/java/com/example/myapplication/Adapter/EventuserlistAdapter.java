package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Activities.BlockedMembersListActivity;
import com.example.myapplication.Activities.Main2Activity;
import com.example.myapplication.Activities.SendGifActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannel.GroupChannelCreateHandler;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBird.UserUnblockHandler;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventuserlistAdapter extends Adapter<ViewHolder> {
    public static final String EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL";
    /* access modifiers changed from: private */
    public static List<String> mSelectedUserIds;
    private OnItemCheckedChangeListener mCheckedChangeListener;
    /* access modifiers changed from: private */
    public Context mContext;
    String[] userid;
    String friendid="";
    private boolean mIsBlockedList;
    private SelectableUserHolder mSelectableUserHolder;
    private boolean mShowCheckBox;
    /* access modifiers changed from: private */
    public List<User> mUsers;
    /* access modifiers changed from: private */
    public SharedPrefManager sharedPrefsHelper = SharedPrefManager.getInstance(mContext);

    public EventuserlistAdapter(FragmentActivity activity, boolean b, boolean b1, String userids) {
        this.mContext = activity;
        this.mUsers = new ArrayList();
        userid=userids.split(",");
        mSelectedUserIds = new ArrayList();

        this.mIsBlockedList = b;
        this.mShowCheckBox = b1;
    }

    public interface OnItemCheckedChangeListener {
        void OnItemChecked(User user, boolean z);
    }

    private class SelectableUserHolder extends ViewHolder {
        private ImageView blockedImage;
        /* access modifiers changed from: private */
        public CheckBox checkbox;
        /* access modifiers changed from: private */
        public ImageView iv_message;
        private boolean mIsBlockedList;
        /* access modifiers changed from: private */
        public boolean mShowCheckBox;
        private TextView nameText;
        private CircleImageView profileImage;

        public SelectableUserHolder(View itemView, boolean isBlockedList, boolean hideCheckBox) {
            super(itemView);
            setIsRecyclable(false);
            this.mIsBlockedList = isBlockedList;
            this.mShowCheckBox = hideCheckBox;
            this.nameText = (TextView) itemView.findViewById(R.id.text_selectable_user_list_nickname);
            this.profileImage = (CircleImageView) itemView.findViewById(R.id.image_selectable_user_list_profile);
            this.blockedImage = (ImageView) itemView.findViewById(R.id.image_user_list_blocked);
            this.iv_message = (ImageView) itemView.findViewById(R.id.iv_message);
            this.checkbox = (CheckBox) itemView.findViewById(R.id.checkbox_selectable_user_list);
        }

        public void setShowCheckBox(boolean showCheckBox) {
            this.mShowCheckBox = showCheckBox;
        }

        /* access modifiers changed from: private */
        public void bind(Context context, final User user, boolean isSelected, final OnItemCheckedChangeListener listener) {
            this.nameText.setText(user.getNickname());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_img);
            requestOptions.error(R.drawable.buzzplaceholder);

            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(user.getProfileUrl()).into(this.profileImage);
            //ImageUtils.displayRoundImageFromUrl(context, user.getProfileUrl(), this.profileImage);
            if (this.mIsBlockedList) {
                this.blockedImage.setVisibility(View.VISIBLE);
            } else {
                this.blockedImage.setVisibility(View.GONE);
            }
            if (this.mShowCheckBox) {
                this.checkbox.setVisibility(View.VISIBLE);
            } else {
                this.checkbox.setVisibility(View.GONE);
            }
            if (isSelected) {
                this.checkbox.setChecked(true);
            } else {
                this.checkbox.setChecked(false);
            }
            if (this.mShowCheckBox) {
                this.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (SelectableUserHolder.this.mShowCheckBox) {
                            SelectableUserHolder.this.checkbox.setChecked(!SelectableUserHolder.this.checkbox.isChecked());
                        }
                    }
                });
            }
            this.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.OnItemChecked(user, isChecked);
                    if (isChecked) {
                        EventuserlistAdapter.mSelectedUserIds.add(user.getUserId());
                    } else {
                        EventuserlistAdapter.mSelectedUserIds.remove(user.getUserId());
                    }
                }
            });
        }
    }

    public EventuserlistAdapter(Context context, boolean isBlockedList, boolean showCheckBox) {
        this.mContext = context;
        this.mUsers = new ArrayList();
        mSelectedUserIds = new ArrayList();
        this.mIsBlockedList = isBlockedList;
        this.mShowCheckBox = showCheckBox;
    }

    public void setItemCheckedChangeListener(OnItemCheckedChangeListener listener) {
        this.mCheckedChangeListener = listener;
    }

    public void setUserList(List<User> users) {
        mUsers.clear();
        if(userid!=null){
            Log.d("send","1"+userid[0]);
            Log.d("send","1"+userid.length);
            Log.d("send","1"+users.size());
            for(int i=0;i<userid.length;i++){
                for(int j=0;j<users.size();j++) {
                   Log.d("send","2"+userid[i]);
                //    Log.d("send","3"+userid[j]);
//                    Log.d("send","4"+users.get(j).getUserId());
               //     Log.d("send","5"+users.get(i).getUserId());
                    if (userid[i].equals(users.get(j).getUserId())) {
                        this.mUsers.add(users.get(j));
                    }
                }
            }
        }
        else {
            this.mUsers = users;
        }
        notifyDataSetChanged();
    }

    public void setShowCheckBox(boolean showCheckBox) {
        this.mShowCheckBox = showCheckBox;
        if (this.mSelectableUserHolder != null) {
            this.mSelectableUserHolder.setShowCheckBox(showCheckBox);
        }
        notifyDataSetChanged();
    }

    public void unblock() {
        for (final String userId : mSelectedUserIds) {
            SendBird.unblockUserWithUserId(userId, new UserUnblockHandler() {
                public void onUnblocked(SendBirdException e) {
                    if (e == null) {
                        int index = 0;
                        while (true) {
                            if (index >= EventuserlistAdapter.this.mUsers.size()) {
                                break;
                            }
                            if (userId.equals(((User) EventuserlistAdapter.this.mUsers.get(index)).getUserId())) {
                                EventuserlistAdapter.this.mUsers.remove(index);
                                break;
                            }
                            index++;
                        }
                        ((BlockedMembersListActivity) EventuserlistAdapter.this.mContext).blockedMemberCount(EventuserlistAdapter.this.mUsers.size());
                        EventuserlistAdapter.this.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mSelectableUserHolder = new SelectableUserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.eventuser_item, parent, false), this.mIsBlockedList, this.mShowCheckBox);
        return this.mSelectableUserHolder;
    }

    public void onBindViewHolder(ViewHolder holder, final int position) {
        ((SelectableUserHolder) holder).bind(this.mContext, (User) this.mUsers.get(position), isSelected((User) this.mUsers.get(position)), this.mCheckedChangeListener);
        ((SelectableUserHolder) holder).iv_message.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                List<String> mSelectedIds = new ArrayList<>();
                mSelectedIds.add(((User) EventuserlistAdapter.this.mUsers.get(position)).getUserId());
                if (EventuserlistAdapter.this.sharedPrefsHelper != null) {
                    mSelectedIds.add(EventuserlistAdapter.this.sharedPrefsHelper.getUser().getUser_id().toString());
                }
                EventuserlistAdapter.this.createGroupChannel(mSelectedIds, false, ((User) EventuserlistAdapter.this.mUsers.get(position)).getNickname());
            }
        });
    }

    public int getItemCount() {
        return this.mUsers.size();
    }

    public boolean isSelected(User user) {
        return mSelectedUserIds.contains(user.getUserId());
    }

    public void addLast(User user) {
        this.mUsers.add(user);
        notifyDataSetChanged();
    }

    public void createGroupChannel(List<String> userIds, boolean distinct, String name) {

        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(true).addUserIds(userIds).setName(name), new GroupChannelCreateHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e == null) {
                    Intent intent = new Intent(EventuserlistAdapter.this.mContext, Main2Activity.class);
                    intent.putExtra("URL", groupChannel.getUrl());
                    intent.putExtra("gif", SendGifActivity.gifURL);
                    EventuserlistAdapter.this.mContext.startActivity(intent);
                }
            }
        });
    }
}
