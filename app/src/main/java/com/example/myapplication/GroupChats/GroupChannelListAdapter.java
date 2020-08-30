package com.example.myapplication.GroupChats;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.R;
import com.example.myapplication.Utils.DateUtils;
import com.example.myapplication.Utils.SharedPrefManager;
import com.example.myapplication.Utils.SyncManagerUtils;
import com.example.myapplication.Utils.TypingIndicator;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.Member;
import com.sendbird.android.UserMessage;
import com.stfalcon.multiimageview.MultiImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.hdodenhof.circleimageview.CircleImageView;

class GroupChannelListAdapter extends RecyclerView.Adapter<GroupChannelListAdapter.ChannelHolder> {
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, SparseArray<Bitmap>> mChannelBitmapMap;
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, Integer> mChannelImageNumMap;
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, ImageView> mChannelImageViewMap;
    private List<GroupChannel> mChannelList;
    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    /* access modifiers changed from: private */
    public ConcurrentHashMap<SimpleTarget<Bitmap>, GroupChannel> mSimpleTargetGroupChannelMap;
    /* access modifiers changed from: private */
    public ConcurrentHashMap<SimpleTarget<Bitmap>, Integer> mSimpleTargetIndexMap;
    /* access modifiers changed from: private */
    // public SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
    private String member_name;
    private String member_pic_url;

    class ChannelHolder extends RecyclerView.ViewHolder {
        CircleImageView coverImage;
        TextView dateText;
        TextView lastMessageText;
        TextView memberCountText;
        TextView topicText;
        LinearLayout typingIndicatorContainer;
        TextView unreadCountText;

        ChannelHolder(View itemView) {
            super(itemView);
            Log.d("LIFECYCLE", "GroupChannelListAdapter ChannelHolder()");
            this.topicText = (TextView) itemView.findViewById(R.id.text_group_channel_list_topic);
            this.lastMessageText = (TextView) itemView.findViewById(R.id.text_group_channel_list_message);
            this.unreadCountText = (TextView) itemView.findViewById(R.id.text_group_channel_list_unread_count);
            this.dateText = (TextView) itemView.findViewById(R.id.text_group_channel_list_date);
            this.memberCountText = (TextView) itemView.findViewById(R.id.text_group_channel_list_member_count);
            this.coverImage = (CircleImageView) itemView.findViewById(R.id.image_group_channel_list_cover);
            this.typingIndicatorContainer = (LinearLayout) itemView.findViewById(R.id.container_group_channel_list_typing_indicator);
        }

        /* access modifiers changed from: 0000 */

        public void bind(Context context, final GroupChannel channel, @Nullable final OnItemClickListener clickListener, @Nullable final OnItemLongClickListener longClickListener) {
            this.memberCountText.setText(String.valueOf(channel.getMemberCount()));
            Log.d("channel", channel.getMembers().size() + " " + channel.getCoverUrl());

            Log.d("channel", channel.getName() + " " + channel.getMemberCount());
            try {
                List<Member> member = channel.getMembers();
//                for (int i = 0; i < member.size(); i++) {
//                    if (!(((Member) member.get(i)).getUserId() == null || ((Member) member.get(i)).getProfileUrl() == null || GroupChannelListAdapter.this.sharedPrefsHelper.getQbUser().getId().toString().equals(((Member) member.get(i)).getUserId()))) {
//                        ((Member) member.get(i)).getConnectionStatus();
//                        ((Member) member.get(i)).getConnectionStatus();
//                        ConnectionStatus connectionStatus = ConnectionStatus.ONLINE;
//                        //this.topicText.setText(channel.getName());

                if (member.size() > 2) {
                    this.topicText.setText(channel.getName());
                    /*RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.app_buzz_logo);
                    requestOptions.error(R.drawable.app_buzz_logo);*/
                    Glide.with(context)
                            /*.setDefaultRequestOptions(requestOptions)  */    //comment
                            .load(channel.getCoverUrl().trim()).into(coverImage);
                    Log.d("channel.memer <211", member.get(0).getProfileUrl() + " " + member.get(1).getProfileUrl());
                    //    Picasso.get().load(channel.getCoverUrl()).into(coverImage);
                    Glide.with(context).load(channel.getCoverUrl()).into((ImageView) this.coverImage);
                    Log.d("channel. memer <2", member.size() + " " + channel.getName());
                    //  Log.d("channel. memer <2",member.size()+" "+ TextUtils.getGroupChannelTitle(channel));
                    //  this.topicText.setText(TextUtils.getGroupChannelTitle(channel));


                } else {
                    Log.d("channel.memer <212", member.get(0).getNickname() + "--" + member.get(1).getNickname());
                    Log.d("channel.memer <213", channel.getName() + " -" + channel.getCoverUrl());
                    Log.d("channel.memer <214", member.get(0).getUserId() + "- " + SharedPrefManager.getInstance(context).getUser().getUser_id()
                            + "-" + SharedPrefManager.getInstance(context).getUser().getCaller_id());
                    for (int i = 0; i < member.size(); i++) {

                        if (!member.get(i).getUserId().equals(SharedPrefManager.getInstance(context).getUser().getCaller_id())) { //callerid

                            member_name = member.get(i).getNickname();
                            member_pic_url = member.get(i).getProfileUrl();
                        }
                    }
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.error(R.drawable.app_buzz_logo);
                    if (channel.getName().equals("NO") && channel.getCoverUrl().equals("NO")) {
                        coverImage.setColorFilter(ContextCompat.getColor(context, R.color.black),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                        //  Glide.with(context).load(channel.getCoverUrl()).into((ImageView) this.coverImage);
                        Glide.with(context).setDefaultRequestOptions(requestOptions) //comment
                                .load(member_pic_url)
                                .into((ImageView) this.coverImage);
                        this.topicText.setText(member_name);
                    } else if (!channel.getName().equals("NO") && channel.getCoverUrl().equals("NO")) {
                        coverImage.setColorFilter(ContextCompat.getColor(context, R.color.black),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                        //      Glide.with(context).load(channel.getCoverUrl()).into((ImageView) this.coverImage);
                        Glide.with(context).setDefaultRequestOptions(requestOptions)
                                .load(member_pic_url)
                                .into((ImageView) this.coverImage);
                        this.topicText.setText(member_name); //channel.getname
                    } else if (channel.getName().equals("NO") && !channel.getCoverUrl().equals("NO")) {
                        coverImage.setColorFilter(ContextCompat.getColor(context, R.color.black),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                        //      Glide.with(context).load(channel.getCoverUrl()).into((ImageView) this.coverImage);
                        Glide.with(mContext).setDefaultRequestOptions(requestOptions)
                                .load(member_pic_url)
                                .into((ImageView) this.coverImage);
//comment
                        this.topicText.setText(member_name);
                    } else {
                        coverImage.setColorFilter(ContextCompat.getColor(context, android.R.color.transparent),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                        this.topicText.setText(member_name);
//                                RequestOptions requestOption = new RequestOptions();
//                                requestOptions.placeholder(R.drawable.profile_img);
//                                requestOptions.error(R.drawable.buzzplaceholder);

//                                Glide.with(mContext)
//                                .        .setDefaultRequestOptions(requestOption)
//                                        .load(mChannelList.get(getAdapterPosition()).getCoverUrl()).into(ho);

                        //channel.getname
//                                Glide.with(context).load(channel.getCoverUrl()).into((ImageView) this.coverImage);
//
                        Glide.with(mContext)/*.setDefaultRequestOptions(requestOptions)*/
                                .load(member_pic_url)
                                .into((ImageView) this.coverImage);

                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            if (channel.getUnreadMessageCount() == 0) {
                this.unreadCountText.setVisibility(View.GONE);
            } else {
                this.unreadCountText.setVisibility(View.VISIBLE);
                this.unreadCountText.setText(String.valueOf(channel.getUnreadMessageCount()));
            }
            BaseMessage lastMessage = channel.getLastMessage();
            if (lastMessage != null) {
                this.dateText.setVisibility(View.VISIBLE);
                this.lastMessageText.setVisibility(View.VISIBLE);
                this.dateText.setText(String.valueOf(DateUtils.formatDateTime(lastMessage.getCreatedAt())));
                if (lastMessage instanceof UserMessage) {
                    this.lastMessageText.setText(((UserMessage) lastMessage).getMessage());
                } else if (lastMessage instanceof AdminMessage) {
                    this.lastMessageText.setText(((AdminMessage) lastMessage).getMessage());
                } else if (((FileMessage) lastMessage).getType().contains("image")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(((FileMessage) lastMessage).getSender().getNickname());
                    sb.append(" has uploaded an image");
                    this.lastMessageText.setText(sb.toString());
                } else if (((FileMessage) lastMessage).getType().contains("audio")) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(((FileMessage) lastMessage).getSender().getNickname());
                    sb2.append(" has uploaded an audio");
                    this.lastMessageText.setText(sb2.toString());
                } else if (((FileMessage) lastMessage).getType().contains("video")) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(((FileMessage) lastMessage).getSender().getNickname());
                    sb3.append(" has uploaded a video");
                    this.lastMessageText.setText(sb3.toString());
                } else {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append(((FileMessage) lastMessage).getSender().getNickname());
                    sb4.append(" has uploaded a file");
                    this.lastMessageText.setText(sb4.toString());
                }
            } else {
                this.dateText.setVisibility(View.INVISIBLE);
                this.lastMessageText.setVisibility(View.INVISIBLE);
            }
            ArrayList<ImageView> indicatorImages = new ArrayList<>();
            indicatorImages.add((ImageView) this.typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_1));
            indicatorImages.add((ImageView) this.typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_2));
            indicatorImages.add((ImageView) this.typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_3));
            new TypingIndicator(indicatorImages, 600).animate();
            if (channel.isTyping()) {
                Log.i("Typing", "someone is typing..." + channel.toString());
                if (channel.getMembers().size() <= 2) {
                    String loggedInUserId = null;
                    if (SharedPrefManager.getInstance(context).isLoggedIn()) {
                        loggedInUserId = String.valueOf(SharedPrefManager
                                .getInstance(context).getUser()
                                .getUser_id()).replaceAll("\\s", "");

                        for (Member memberInfo : channel.getMembers()) {
                            if (loggedInUserId.equalsIgnoreCase(memberInfo.getUserId())) {
                                this.typingIndicatorContainer.setVisibility(View.VISIBLE);
                                this.lastMessageText.setText(memberInfo.getNickname() + " is typing");
                            }
                        }
                    }
                } else {
                    this.typingIndicatorContainer.setVisibility(View.VISIBLE);
                    this.lastMessageText.setText("Someone is typing");
                }
            } else {
                this.typingIndicatorContainer.setVisibility(View.GONE);
            }
            if (clickListener != null) {
                this.itemView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        clickListener.onItemClick(channel);
                    }
                });
            }
            if (longClickListener != null) {
                this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        longClickListener.onItemLongClick(channel);
                        return true;
                    }
                });
            }
        }

        private void setChannelImage(Context context, GroupChannel channel, MultiImageView multiImageView) {
            List<Member> members = channel.getMembers();
            if (members != null) {
                int size = members.size();
                if (size >= 1) {
                    int imageNum = size;
                    if (size >= 4) {
                        imageNum = 4;
                    }
                    if (!GroupChannelListAdapter.this.mChannelImageNumMap.containsKey(channel.getUrl())) {
                        GroupChannelListAdapter.this.mChannelImageNumMap.put(channel.getUrl(), Integer.valueOf(imageNum));
                        GroupChannelListAdapter.this.mChannelImageViewMap.put(channel.getUrl(), multiImageView);
                        multiImageView.clear();
                        for (int index = 0; index < imageNum; index++) {
                            SimpleTarget<Bitmap> simpleTarget = new SimpleTarget<Bitmap>() {
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    Integer index = (Integer) GroupChannelListAdapter.this.mSimpleTargetIndexMap.get(this);
                                    if (index != null) {
                                        GroupChannel channel = (GroupChannel) GroupChannelListAdapter.this.mSimpleTargetGroupChannelMap.get(this);
                                        SparseArray sparseArray = (SparseArray) GroupChannelListAdapter.this.mChannelBitmapMap.get(channel.getUrl());
                                        if (sparseArray == null) {
                                            sparseArray = new SparseArray();
                                            GroupChannelListAdapter.this.mChannelBitmapMap.put(channel.getUrl(), sparseArray);
                                        }
                                        sparseArray.put(index.intValue(), resource);
                                        Integer num = (Integer) GroupChannelListAdapter.this.mChannelImageNumMap.get(channel.getUrl());
                                        if (num != null && sparseArray.size() == num.intValue()) {
                                            MultiImageView multiImageView = (MultiImageView) GroupChannelListAdapter.this.mChannelImageViewMap.get(channel.getUrl());
                                            for (int i = 0; i < sparseArray.size(); i++) {
                                                multiImageView.addImage((Bitmap) sparseArray.get(i));
                                            }
                                        }
                                    }
                                }
                            };
                            GroupChannelListAdapter.this.mSimpleTargetIndexMap.put(simpleTarget, Integer.valueOf(index));
                            GroupChannelListAdapter.this.mSimpleTargetGroupChannelMap.put(simpleTarget, channel);
                            Glide.with(context).asBitmap().load(((Member) members.get(index)).getProfileUrl()).apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)).into(simpleTarget);
                        }
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(GroupChannel groupChannel);
    }

    interface OnItemLongClickListener {
        void onItemLongClick(GroupChannel groupChannel);
    }

    GroupChannelListAdapter(Context context) {
        this.mContext = context;
        this.mSimpleTargetIndexMap = new ConcurrentHashMap<>();
        this.mSimpleTargetGroupChannelMap = new ConcurrentHashMap<>();
        this.mChannelImageNumMap = new ConcurrentHashMap<>();
        this.mChannelImageViewMap = new ConcurrentHashMap<>();
        this.mChannelBitmapMap = new ConcurrentHashMap<>();
        this.mChannelList = new ArrayList();
        Log.d("LIFECYCLE", "GroupChannelListAdapter con()");
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    /* access modifiers changed from: 0000 */
    public void clearMap() {
        this.mSimpleTargetIndexMap.clear();
        this.mSimpleTargetGroupChannelMap.clear();
        this.mChannelImageNumMap.clear();
        this.mChannelImageViewMap.clear();
        this.mChannelBitmapMap.clear();
    }

    public void load() {
    }

    public void save() {
    }

    @Override
    public ChannelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("LIFECYCLE", "GroupChannelListAdapter onCreateViewholder()");
        return new ChannelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group_channel, parent, false));
    }


    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(ChannelHolder holder, int position) {
        Log.d("LIFECYCLE", "GroupChannelListAdapter onbindview()");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_img);
        requestOptions.error(R.drawable.buzzplaceholder);
        Glide.with(mContext).setDefaultRequestOptions(requestOptions)
                .load(member_pic_url)
                .into((ImageView) holder.coverImage);

        ((ChannelHolder) holder).bind(this.mContext, (GroupChannel) mChannelList.get(position), this.mItemClickListener, this.mItemLongClickListener);
    }

    public int getItemCount() {
        Log.d("LIFECYCLE", "getItemCount " + mChannelList.size());

        return mChannelList.size();
    }

    /* access modifiers changed from: 0000 */
    public void setGroupChannelList(List<GroupChannel> channelList) {
//        this.mChannelList = channelList;
//        notifyDataSetChanged();
    }

    /* access modifiers changed from: 0000 */
    public void addLast(GroupChannel channel) {
        this.mChannelList.add(channel);
        notifyDataSetChanged();
    }

    /* access modifiers changed from: 0000 */
    public void updateOrInsert(BaseChannel channel) {
        Log.d("LIFECYCLE", "updateOrInsert " + channel.getName());
//        if (channel instanceof GroupChannel) {
//            GroupChannel groupChannel = (GroupChannel) channel;
//            for (int i = 0; i < this.mChannelList.size(); i++) {
//                if (((GroupChannel) this.mChannelList.get(i)).getUrl().equals(groupChannel.getUrl())) {
//                    this.mChannelList.remove(this.mChannelList.get(i));
//                    this.mChannelList.add(0, groupChannel);
//                    notifyDataSetChanged();
//                    Log.v(GroupChannelListAdapter.class.getSimpleName(), "Channel replaced.");
//                    return;
//                }
//            }
//            this.mChannelList.add(0, groupChannel);
//            notifyDataSetChanged();
//        }
    }

    /* access modifiers changed from: 0000 */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    /* access modifiers changed from: 0000 */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    public void filterList(ArrayList<GroupChannel> filteredList) {
//        this.mChannelList = filteredList;
//        notifyDataSetChanged();
    }

    public void setSearchResult(List<GroupChannel> result) {
        this.mChannelList = result;
        notifyDataSetChanged();
    }

    void insertChannels(List<GroupChannel> channels, GroupChannelListQuery.Order order) {
        Log.d("LIFECYCLE", "insertChannels " + channels);

        for (GroupChannel newChannel : channels) {
            int index = SyncManagerUtils.findIndexOfChannel(mChannelList, newChannel, order);
            mChannelList.add(index, newChannel);
        }

        notifyDataSetChanged();
    }

    void updateChannels(List<GroupChannel> channels) {
        Log.d("LIFECYCLE", "updateChannels " + channels);
        for (GroupChannel updatedChannel : channels) {
            int index = SyncManagerUtils.getIndexOfChannel(mChannelList, updatedChannel);
            if (index != -1) {
                mChannelList.set(index, updatedChannel);
                notifyItemChanged(index);
            }
        }
    }

    void moveChannels(List<GroupChannel> channels, GroupChannelListQuery.Order order) {
        for (GroupChannel movedChannel : channels) {
            int fromIndex = SyncManagerUtils.getIndexOfChannel(mChannelList, movedChannel);
            int toIndex = SyncManagerUtils.findIndexOfChannel(mChannelList, movedChannel, order);
            if (fromIndex != -1) {
                mChannelList.remove(fromIndex);
                mChannelList.add(toIndex, movedChannel);
                notifyItemMoved(fromIndex, toIndex);
                notifyItemChanged(toIndex);
            }
        }
    }

    void removeChannels(List<GroupChannel> channels) {
        for (GroupChannel removedChannel : channels) {
            int index = SyncManagerUtils.getIndexOfChannel(mChannelList, removedChannel);
            if (index != -1) {
                mChannelList.remove(index);
                notifyItemRemoved(index);
            }
        }
    }

    void clearChannelList() {
        mChannelList.clear();
        notifyDataSetChanged();
    }
}
