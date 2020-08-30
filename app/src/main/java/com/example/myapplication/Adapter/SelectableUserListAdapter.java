package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Activities.BlockedMembersListActivity;
import com.example.myapplication.Activities.Main2Activity;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ImageUtils;
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

public class SelectableUserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL";
    /* access modifiers changed from: private */
    public static List<String> mSelectedUserIds;
    FriendListInterface friendListInterface;
    private OnItemCheckedChangeListener mCheckedChangeListener;
    /* access modifiers changed from: private */
    public Context mContext;
    private boolean mIsBlockedList;
    private SelectableUserHolder mSelectableUserHolder;
    private boolean mShowCheckBox;
    /* access modifiers changed from: private */
    public List<FriendListModel> mUsers = new ArrayList();
    SharedPrefManager sharedPrefManager;
    //private SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
    User user;

    public interface OnItemCheckedChangeListener {
        void OnItemChecked(FriendListModel friendListModel, boolean z);
    }

    private class SelectableUserHolder extends RecyclerView.ViewHolder {
        public ImageView blockedImage;
        public CheckBox checkbox;
        public ImageView iv_message;
        public boolean mIsBlockedList;
        public boolean mShowCheckBox;
        public TextView nameText;
        public ImageView profileImage;

        public SelectableUserHolder(View itemView, boolean isBlockedList, boolean hideCheckBox) {
            super(itemView);
            setIsRecyclable(false);
            this.mIsBlockedList = isBlockedList;
            this.mShowCheckBox = hideCheckBox;
            this.nameText = (TextView) itemView.findViewById(R.id.text_selectable_user_list_nickname);
            this.profileImage = (ImageView) itemView.findViewById(R.id.image_selectable_user_list_profile);
            this.blockedImage = (ImageView) itemView.findViewById(R.id.image_user_list_blocked);
            this.iv_message = (ImageView) itemView.findViewById(R.id.iv_message);
            this.checkbox = (CheckBox) itemView.findViewById(R.id.checkbox_selectable_user_list);
        }

        public void setShowCheckBox(boolean showCheckBox) {
            this.mShowCheckBox = showCheckBox;
        }

        /* access modifiers changed from: private */
        public void bind(Context context, final FriendListModel user, boolean isSelected, final OnItemCheckedChangeListener listener) {
            this.nameText.setText(user.getName());
            ImageUtils.displayRoundImageFromUrl(context, user.getImage(), this.profileImage);
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
                        SelectableUserListAdapter.mSelectedUserIds.add(user.getUser_id());
                    } else {
                        SelectableUserListAdapter.mSelectedUserIds.remove(user.getUser_id());
                    }
                }
            });
        }
    }

    public SelectableUserListAdapter(List<FriendListModel> mUsers2, Context mContext2, FriendListInterface friendListInterface2, boolean isBlockedList, boolean showCheckBox) {
        this.mUsers = mUsers2;
        this.mContext = mContext2;
        this.friendListInterface = friendListInterface2;
        new ArrayList<>();
        mSelectedUserIds = new ArrayList();
        this.mIsBlockedList = isBlockedList;
        this.mShowCheckBox = showCheckBox;
    }

    public SelectableUserListAdapter(Context context, boolean isBlockedList, boolean showCheckBox) {
        this.mContext = context;
        this.mUsers = new ArrayList();
        mSelectedUserIds = new ArrayList();
        this.mIsBlockedList = isBlockedList;
        this.mShowCheckBox = showCheckBox;
    }

    public void setItemCheckedChangeListener(OnItemCheckedChangeListener listener) {
        this.mCheckedChangeListener = listener;
    }

    public void setUserList(List<FriendListModel> users) {
        this.mUsers = users;
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
                            if (index >= SelectableUserListAdapter.this.mUsers.size()) {
                                break;
                            }
                            if (userId.equals(((FriendListModel) SelectableUserListAdapter.this.mUsers.get(index)).getUser_id())) {
                                SelectableUserListAdapter.this.mUsers.remove(index);
                                break;
                            }
                            index++;
                        }
                        ((BlockedMembersListActivity) SelectableUserListAdapter.this.mContext).blockedMemberCount(SelectableUserListAdapter.this.mUsers.size());
                        SelectableUserListAdapter.this.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mSelectableUserHolder = new SelectableUserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_selectable_user, parent, false), this.mIsBlockedList, this.mShowCheckBox);
        return this.mSelectableUserHolder;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((SelectableUserHolder) holder).bind(this.mContext, (FriendListModel) this.mUsers.get(position), isSelected((FriendListModel) this.mUsers.get(position)), this.mCheckedChangeListener);
        String caller_id = ((FriendListModel) this.mUsers.get(position)).getCaller_id();
        ((SelectableUserHolder) holder).iv_message.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                List<String> mSelectedIds = new ArrayList<>();
                mSelectedIds.add(((FriendListModel) SelectableUserListAdapter.this.mUsers.get(position)).getUser_id());
                SelectableUserListAdapter.this.createGroupChannel(mSelectedIds, false, ((FriendListModel) SelectableUserListAdapter.this.mUsers.get(position)).getUser_id());
            }
        });
    }

    public int getItemCount() {
        return this.mUsers.size();
    }

    public boolean isSelected(FriendListModel user2) {
        return mSelectedUserIds.contains(user2.getUser_id());
    }

    public void addLast(FriendListModel user2) {
        this.mUsers.add(user2);
        notifyDataSetChanged();
    }

    public void createGroupChannel(List<String> userIds, boolean distinct, String name) {
        GroupChannel.createChannel(new GroupChannelParams().setPublic(false).setEphemeral(false).setDistinct(true).addUserIds(userIds).setName(name), new GroupChannelCreateHandler() {
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e == null) {
                    Intent intent = new Intent(SelectableUserListAdapter.this.mContext, Main2Activity.class);
                    intent.putExtra("URL", groupChannel.getUrl());
                    SelectableUserListAdapter.this.mContext.startActivity(intent);
                }
            }
        });
    }
}
