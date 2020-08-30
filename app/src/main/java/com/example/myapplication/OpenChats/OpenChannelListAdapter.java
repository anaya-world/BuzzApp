package com.example.myapplication.OpenChats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.myapplication.R;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.ParticipantListQuery;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserListQuery;

import java.util.ArrayList;
import java.util.List;

public class OpenChannelListAdapter extends Adapter<ViewHolder> {
    private List<OpenChannel> mChannelList = new ArrayList();
    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    private class ChannelHolder extends ViewHolder {
        private String[] colorList = {"#ff2de3e1", "#ff35a3fb", "#ff805aff", "#ffcf47fb", "#ffe248c3"};
        ImageView coloredDecorator;
        TextView nameText;
        TextView tvGroupType;
        TextView participantCountText;

        ChannelHolder(View itemView) {
            super(itemView);
            this.nameText = (TextView) itemView.findViewById(R.id.text_open_channel_list_name);
            this.tvGroupType = itemView.findViewById(R.id.text_group_type);
            this.participantCountText = (TextView) itemView.findViewById(R.id.text_open_channel_list_participant_count);
            this.coloredDecorator = (ImageView) itemView.findViewById(R.id.image_open_channel_list_decorator);
        }

        /* access modifiers changed from: 0000 */
        @SuppressLint("SetTextI18n")
        public void bind(Context context, final OpenChannel channel, int position, @Nullable final OnItemClickListener clickListener, @Nullable final OnItemLongClickListener longClickListener) {
            this.nameText.setText(channel.getName());
            this.tvGroupType.setText(channel.getData());
            ParticipantListQuery participantListQuery = channel.createParticipantListQuery();
            participantListQuery.next(new UserListQuery.UserListQueryResultHandler() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResult(List<User> list, SendBirdException e) {
                    if (e != null) {
                        e.printStackTrace();// Error.
                        return;
                    }
                    participantCountText.setText(list.size() + " Participants");
                }
            });

            this.coloredDecorator.setBackgroundColor(Color.parseColor(this.colorList[position % this.colorList.length]));
            if (clickListener != null) {
                this.itemView.setOnClickListener(v -> clickListener.onItemClick(channel));
            }
            if (longClickListener != null) {
                this.itemView.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        longClickListener.onItemLongPress(channel);
                        return true;
                    }
                });
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(OpenChannel openChannel);
    }

    public interface OnItemLongClickListener {
        void onItemLongPress(OpenChannel openChannel);
    }

    public OpenChannelListAdapter(Context context) {
        this.mContext = context;
    }

    public ChannelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChannelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_open_channel, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        ((ChannelHolder) holder).bind(this.mContext, (OpenChannel) this.mChannelList.get(position), position, this.mItemClickListener, this.mItemLongClickListener);
    }

    public int getItemCount() {
        return this.mChannelList.size();
    }

    /* access modifiers changed from: 0000 */
    public void setOpenChannelList(List<OpenChannel> channelList) {
        this.mChannelList = channelList;
        notifyDataSetChanged();
    }

    /* access modifiers changed from: 0000 */
    public void addLast(OpenChannel channel) {
        this.mChannelList.add(channel);
        notifyDataSetChanged();
    }

    /* access modifiers changed from: 0000 */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    /* access modifiers changed from: 0000 */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setSearchResult(List<OpenChannel> result) {
        this.mChannelList = result;
        notifyDataSetChanged();
    }
}
