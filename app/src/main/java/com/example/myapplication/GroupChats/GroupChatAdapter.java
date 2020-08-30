package com.example.myapplication.GroupChats;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Common;
import com.example.myapplication.Utils.DateUtils;
import com.example.myapplication.Utils.ImageUtils;
import com.example.myapplication.Utils.SyncManagerUtils;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel.GetMessagesHandler;
import com.sendbird.android.BaseChannel.MessageTypeFilter;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.FileMessage.Thumbnail;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;
import com.sendbird.calls.shadow.kotlin.jvm.internal.LongCompanionObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.example.myapplication.Utils.SyncManagerUtils.getMyUserId;

public class GroupChatAdapter extends /*RecyclerView.Adapter<ViewHolder>*/Adapter<ViewHolder>
        implements Filterable {
    private static final int FILE_MESSAGE_AUDIO_ME = 1;

    private static final int FILE_MESSAGE_AUDIO_OTHER = 2;
    private static final int FILE_MESSAGE_CONTACT_ME = 101;
    private static final int FILE_MESSAGE_CONTACT_OTHER = 102;
    public static final String URL_PREVIEW_CUSTOM_TYPE = "url_preview";
    private static final int VIEW_TYPE_ADMIN_MESSAGE = 30;
    private static final int VIEW_TYPE_FILE_MESSAGE_IMAGE_ME = 22;
    private static final int VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER = 23;
    private static final int VIEW_TYPE_FILE_MESSAGE_ME = 20;
    private static final int VIEW_TYPE_FILE_MESSAGE_OTHER = 21;
    private static final int VIEW_TYPE_FILE_MESSAGE_VIDEO_ME = 24;
    private static final int VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER = 25;
    private static final int VIEW_TYPE_USER_MESSAGE_ME = 10;
    private static final int VIEW_TYPE_USER_MESSAGE_OTHER = 11;
    private static final int VIEW_TYPE_FILE_MESSAGE_AUDIO_ME = 26;
    private static final int VIEW_TYPE_FILE_MESSAGE_AUDIO_OTHER = 27;
    private GroupChannel mChannel;
    /* access modifiers changed from: private */
    public Context mContext;
    private ArrayList<String> mFailedMessageIdList = new ArrayList<>();
    /* access modifiers changed from: private */
    public HashMap<FileMessage, CircleProgressBar> mFileMessageMap;
    private boolean mIsMessageListLoading;
    public OnItemClickListener mItemClickListener;
    public OnItemLongClickListener mItemLongClickListener;
    /* access modifiers changed from: private */
    public List<BaseMessage> mMessageList;
    public List<BaseMessage> mMessageListFiltered;
    private Hashtable<String, Uri> mTempFileMessageUriTable = new Hashtable<>();
    // private SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
    private List<BaseMessage> mFailedMessageList;
    private Set<String> mResendingMessageSet;


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mMessageListFiltered = mMessageList;
                } else {
                    List<BaseMessage> filteredList = new ArrayList<>();
                    for (BaseMessage row : mMessageList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getMessage().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mMessageListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mMessageListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mMessageListFiltered = (ArrayList<BaseMessage>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    private class AdminMessageHolder extends ViewHolder {
        private TextView dateText;
        private TextView messageText;

        AdminMessageHolder(View itemView) {
            super(itemView);
            this.messageText = (TextView) itemView.findViewById(R.id.text_group_chat_message);
            this.dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
        }

        /* access modifiers changed from: 0000 */
        public void bind(Context context, AdminMessage message, GroupChannel channel, boolean isNewDay) {
            this.messageText.setText(message.getMessage());
            if (isNewDay) {
                this.dateText.setVisibility(View.VISIBLE);
                this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
                return;
            }
            this.dateText.setVisibility(View.GONE);
        }

        /* access modifiers changed from: 0000 */
        public void bind(Context context, UserMessage message, GroupChannel channel, boolean isNewDay) {
            this.messageText.setText(message.getMessage());
            if (isNewDay) {
                this.dateText.setVisibility(View.VISIBLE);
                this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
                return;
            }
            this.dateText.setVisibility(View.GONE);
        }
    }

    private class MyAudioFileMessageHolder extends ViewHolder {
        ImageView pausebutton;
        ImageView playbutton;
        TextView playtime;
        SeekBar seekBar;

        public MyAudioFileMessageHolder(View itemView) {
            super(itemView);
            this.pausebutton = (ImageView) itemView.findViewById(R.id.image_group_chat_file_thumbnail);
            this.playbutton = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            this.playtime = (TextView) itemView.findViewById(R.id.playtime);
            this.seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
        }
    }

    private class MyContactFileMessageHolder extends ViewHolder {
        TextView dateText;
        ImageView ivContactImage;
        TextView timeText;
        TextView tvContactName;
        TextView tvPhoneNumber;

        public MyContactFileMessageHolder(View itemView) {
            super(itemView);
            this.tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            this.tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            this.timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            this.dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            this.ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
        }

        /* access modifiers changed from: 0000 */
        public void bind(String name, String number) {
        }
    }

    private class MyFileMessageHolderFile extends RecyclerView.ViewHolder {
        private final ImageView status_img;
        TextView fileNameText, timeText, readReceiptText, dateText;
        CircleProgressBar circleProgressBar;

        public MyFileMessageHolderFile(View itemView) {
            super(itemView);

            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            fileNameText = (TextView) itemView.findViewById(R.id.text_group_chat_file_name);
            readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            circleProgressBar = (CircleProgressBar) itemView.findViewById(R.id.circle_progress);
            dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            status_img = (ImageView) itemView.findViewById(R.id.status_img);
        }

        void bind(Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, Uri tempFileMessageUri, final OnItemClickListener listener) {
            fileNameText.setText(message.getName());
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));

            if (isFailedMessage) {
                readReceiptText.setText(R.string.message_failed);
                readReceiptText.setVisibility(View.VISIBLE);

                circleProgressBar.setVisibility(View.GONE);
                mFileMessageMap.remove(message);
            } else if (isTempMessage) {
                readReceiptText.setText(R.string.message_sending);
                readReceiptText.setVisibility(View.GONE);

                circleProgressBar.setVisibility(View.VISIBLE);
                mFileMessageMap.put(message, circleProgressBar);
            } else {
                circleProgressBar.setVisibility(View.GONE);
                mFileMessageMap.remove(message);

                if (channel != null) {
                    int readReceipt = channel.getReadReceipt(message);
                    if (readReceipt > 0) {
                        readReceiptText.setVisibility(View.VISIBLE);
                        readReceiptText.setText(String.valueOf(readReceipt));
                    } else {
                        readReceiptText.setVisibility(View.INVISIBLE);
                    }
                }

            }
            if (channel != null) {
                int readReceipt2 = channel.getReadReceipt(message);
                if (readReceipt2 > 0) {
                    this.status_img.setImageResource(R.drawable.yellowdot);
                    this.readReceiptText.setVisibility(View.GONE);
                    this.readReceiptText.setText(String.valueOf(readReceipt2));
                } else {
                    this.readReceiptText.setVisibility(View.GONE);
                    this.status_img.setImageResource(R.drawable.green_dot);
                }
            }
            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            if (listener != null) {
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
        }
    }

    private class OtherFileMessageHolderFile extends RecyclerView.ViewHolder {
        TextView nicknameText, timeText, fileNameText, fileSizeText, dateText, readReceiptText;
        ImageView profileImage;

        public OtherFileMessageHolderFile(View itemView) {
            super(itemView);

            nicknameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            fileNameText = (TextView) itemView.findViewById(R.id.text_group_chat_file_name);
//            fileSizeText = (TextView) itemView.findViewById(R.id.text_group_chat_file_size);

            profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
        }

        void bind(Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isContinuous, final OnItemClickListener listener) {
            fileNameText.setText(message.getName());
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
//            fileSizeText.setText(String.valueOf(message.getSize()));

            // Since setChannel is set slightly after adapter is created, check if null.
            if (channel != null) {
                int readReceipt = channel.getReadReceipt(message);
                if (readReceipt > 0) {
                    readReceiptText.setVisibility(View.VISIBLE);
                    readReceiptText.setText(String.valueOf(readReceipt));
                } else {
                    readReceiptText.setVisibility(View.INVISIBLE);
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            // Hide profile image and nickname if the previous message was also sent by current sender.
            if (isContinuous) {
                profileImage.setVisibility(View.INVISIBLE);
                nicknameText.setVisibility(View.GONE);
            } else {
                profileImage.setVisibility(View.VISIBLE);
                ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), profileImage);

                nicknameText.setVisibility(View.VISIBLE);
                nicknameText.setText(message.getSender().getNickname());
            }

            if (listener != null) {
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
        }
    }


    private class MyFileMessageHolder extends ViewHolder {
        /* access modifiers changed from: private */
        public Runnable UpdateSongTime = new Runnable() {
            public void run() {
                MyFileMessageHolder.this.startTime = (double) MyFileMessageHolder.this.mediaPlayer.getCurrentPosition();
                MyFileMessageHolder.this.playtime.setText(String.format("%d min, %d sec", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) MyFileMessageHolder.this.startTime)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) MyFileMessageHolder.this.startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) MyFileMessageHolder.this.startTime)))}));
                MyFileMessageHolder.this.seekBar.setProgress((int) MyFileMessageHolder.this.startTime);
                MyFileMessageHolder.this.myHandler.postDelayed(this, 100);
            }
        };
        CardView audiolayout;
        //     Button button_group_chat_file_download;
        CircleProgressBar circleProgressBar;
        LinearLayout contactlayout;
        TextView dateText;
        // TextView fileNameText;
        /* access modifiers changed from: private */
        public double finalTime = 0.0d;
        ImageView ivContactImage;
        // LinearLayout layout_group_chat_file_message;
        MediaPlayer mediaPlayer;
        /* access modifiers changed from: private */
        public Handler myHandler = new Handler();
        int oneTimeOnly = 0;
        ImageView pausebutton;
        ImageView playbutton;
        TextView playtime;
        TextView readReceiptText;
        SeekBar seekBar;
        /* access modifiers changed from: private */
        public double startTime = 0.0d;
        ImageView status_img;
        TextView timeText;
        TextView tvContactName;
        TextView tvPhoneNumber;

        public MyFileMessageHolder(View itemView) {
            super(itemView);
            this.timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            //  this.fileNameText = (TextView) itemView.findViewById(R.id.text_group_chat_file_name);
            this.readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            this.circleProgressBar = (CircleProgressBar) itemView.findViewById(R.id.circle_progress);
            this.dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            this.playtime = (TextView) itemView.findViewById(R.id.playtime);
            this.playbutton = (ImageView) itemView.findViewById(R.id.playbutton);
            this.pausebutton = (ImageView) itemView.findViewById(R.id.pausebutton);
            this.seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
            this.audiolayout = (CardView) itemView.findViewById(R.id.audiolayout);
            this.ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            this.tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            this.tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            this.contactlayout = (LinearLayout) itemView.findViewById(R.id.contactlayout);
            //        this.button_group_chat_file_download = (Button) itemView.findViewById(R.id.button_group_chat_file_download);
            //      this.layout_group_chat_file_message = (LinearLayout) itemView.findViewById(R.id.layout_group_chat_file_message);
            this.status_img = (ImageView) itemView.findViewById(R.id.status_img);
        }

        /* access modifiers changed from: 0000 */
        public void bind(final Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, Uri tempFileMessageUri, final OnItemClickListener listener, final OnItemLongClickListener longClickListener, final int position) throws IOException {
            //  this.fileNameText.setText(message.getName());
            this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
            if (channel != null) {
                int readReceipt = channel.getReadReceipt(message);
                if (readReceipt > 0) {
                    this.readReceiptText.setVisibility(View.VISIBLE);
                    this.readReceiptText.setText(String.valueOf(readReceipt));
                } else {
                    this.readReceiptText.setVisibility(View.INVISIBLE);
                }
            }
            if (isNewDay) {
                this.dateText.setVisibility(View.VISIBLE);
                this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                this.dateText.setVisibility(View.GONE);
            }
            if (channel != null) {
                int readReceipt2 = channel.getReadReceipt(message);
                if (readReceipt2 > 0) {
                    this.status_img.setImageResource(R.drawable.yellowdot);
                    this.readReceiptText.setVisibility(View.GONE);
                    this.readReceiptText.setText(String.valueOf(readReceipt2));
                } else {
                    this.readReceiptText.setVisibility(View.GONE);
                    this.status_img.setImageResource(R.drawable.green_dot);
                }
            }
            if (message.getType().contains("audio") || message.getType().contains("application/ogg")) {
                //        this.layout_group_chat_file_message.setVisibility(View.GONE);
                this.audiolayout.setVisibility(View.VISIBLE);
                //   this.contactlayout.setVisibility(View.VISIBLE);
                this.playbutton.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        MyFileMessageHolder.this.mediaPlayer = MediaPlayer.create(context, Uri.parse(message.getUrl()));
                        if (MyFileMessageHolder.this.mediaPlayer != null) {
                            MyFileMessageHolder.this.mediaPlayer.start();
                            MyFileMessageHolder.this.finalTime = (double) MyFileMessageHolder.this.mediaPlayer.getDuration();
                            MyFileMessageHolder.this.startTime = (double) MyFileMessageHolder.this.mediaPlayer.getCurrentPosition();
                            if (MyFileMessageHolder.this.oneTimeOnly == 0) {
                                MyFileMessageHolder.this.seekBar.setMax((int) MyFileMessageHolder.this.finalTime);
                                MyFileMessageHolder.this.oneTimeOnly = 1;
                            }
                            MyFileMessageHolder.this.playtime.setText(String.format("%d min, %d sec", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) MyFileMessageHolder.this.startTime)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) MyFileMessageHolder.this.startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) MyFileMessageHolder.this.startTime)))}));
                            MyFileMessageHolder.this.seekBar.setProgress((int) MyFileMessageHolder.this.startTime);
                            MyFileMessageHolder.this.myHandler.postDelayed(MyFileMessageHolder.this.UpdateSongTime, 100);
                            MyFileMessageHolder.this.pausebutton.setVisibility(View.VISIBLE);
                            MyFileMessageHolder.this.playbutton.setVisibility(View.GONE);
                        }
                    }
                });
                this.pausebutton.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (MyFileMessageHolder.this.mediaPlayer != null) {
                            MyFileMessageHolder.this.mediaPlayer.pause();
                            MyFileMessageHolder.this.playbutton.setVisibility(View.VISIBLE);
                            MyFileMessageHolder.this.pausebutton.setVisibility(View.GONE);
                        }
                    }
                });
            } else if (message.getType().contains("text")) {
                this.contactlayout.setVisibility(View.GONE);
                //     this.layout_group_chat_file_message.setVisibility(View.VISIBLE);
                this.audiolayout.setVisibility(View.GONE);
                this.contactlayout.setVisibility(View.GONE);
                this.tvContactName.setText(message.getName());
                this.tvPhoneNumber.setText(message.getUrl());
            } else {
                this.audiolayout.setVisibility(View.GONE);
                //     this.fileNameText.setText(message.getName());
                this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
                if (isFailedMessage) {
                    this.readReceiptText.setText(R.string.message_failed);
                    this.readReceiptText.setVisibility(View.GONE);
                    this.circleProgressBar.setVisibility(View.GONE);
                    GroupChatAdapter.this.mFileMessageMap.remove(message);
                } else if (isTempMessage) {
                    this.readReceiptText.setText(R.string.message_sending);
                    this.readReceiptText.setVisibility(View.GONE);
                    this.circleProgressBar.setVisibility(View.VISIBLE);
                    GroupChatAdapter.this.mFileMessageMap.put(message, this.circleProgressBar);
                } else {
                    this.circleProgressBar.setVisibility(View.GONE);
                    GroupChatAdapter.this.mFileMessageMap.remove(message);
                    if (channel != null) {
                        int readReceipt3 = channel.getReadReceipt(message);
                        if (readReceipt3 > 0) {
                            this.readReceiptText.setVisibility(View.GONE);
                            this.readReceiptText.setText(String.valueOf(readReceipt3));
                        } else {
                            this.readReceiptText.setVisibility(View.GONE);
                        }
                    }
                }
                if (isNewDay) {
                    this.dateText.setVisibility(View.VISIBLE);
                    this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
                } else {
                    this.dateText.setVisibility(View.GONE);
                }
            }
//            this.layout_group_chat_file_message.setOnClickListener(new OnClickListener() {
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, DocumentActivity.class);
//                    intent.putExtra("docurl_key", message.getUrl());
//                    context.startActivity(intent);
//                }
//            });
//            this.button_group_chat_file_download.setOnClickListener(new OnClickListener() {
//                public void onClick(View view) {
//                    listener.onFileMessageItemClick(message);
//                }
//            });
            this.itemView.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    longClickListener.onFileMessageItemLongClick(message, position);
                    return true;
                }
            });
        }
    }

    private class MyImageFileMessageHolder extends ViewHolder {
        CircleProgressBar circleProgressBar;
        TextView dateText;
        ImageView fileThumbnailImage;
        TextView readReceiptText;
        ImageView status_img;
        TextView timeText;

        public MyImageFileMessageHolder(View itemView) {
            super(itemView);
            this.timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            this.fileThumbnailImage = (ImageView) itemView.findViewById(R.id.image_group_chat_file_thumbnail);
            this.readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            this.circleProgressBar = (CircleProgressBar) itemView.findViewById(R.id.circle_progress);
            this.dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            this.status_img = (ImageView) itemView.findViewById(R.id.status_img);
        }

        /* access modifiers changed from: 0000 */
        public void bind(Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, Uri tempFileMessageUri, final OnItemClickListener listener, final OnItemLongClickListener mItemLongClickListener, final int position) {
            this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
            if (isFailedMessage) {
                this.readReceiptText.setText(R.string.message_failed);
                this.readReceiptText.setVisibility(View.VISIBLE);
                this.circleProgressBar.setVisibility(View.GONE);
                GroupChatAdapter.this.mFileMessageMap.remove(message);
            } else if (isTempMessage) {
                this.readReceiptText.setText(R.string.message_sending);
                this.readReceiptText.setVisibility(View.GONE);
                this.circleProgressBar.setVisibility(View.VISIBLE);
                GroupChatAdapter.this.mFileMessageMap.put(message, this.circleProgressBar);
            } else {
                this.circleProgressBar.setVisibility(View.GONE);
                GroupChatAdapter.this.mFileMessageMap.remove(message);
                if (channel != null) {
                    int readReceipt = channel.getReadReceipt(message);
                    if (readReceipt > 0) {
                        this.readReceiptText.setVisibility(View.GONE);
                        this.readReceiptText.setText(String.valueOf(readReceipt));
                    } else {
                        this.readReceiptText.setVisibility(View.GONE);
                    }
                }
                if (channel != null) {
                    int readReceipt2 = channel.getReadReceipt(message);
                    if (readReceipt2 > 0) {
                        this.status_img.setImageResource(R.drawable.yellowdot);
                        this.readReceiptText.setVisibility(View.GONE);
                        this.readReceiptText.setText(String.valueOf(readReceipt2));
                    } else {
                        this.readReceiptText.setVisibility(View.GONE);
                        this.status_img.setImageResource(R.drawable.green_dot);
                    }
                }
            }
            if (isNewDay) {
                this.dateText.setVisibility(View.VISIBLE);
                this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                this.dateText.setVisibility(View.GONE);
            }
            if (!isTempMessage || tempFileMessageUri == null) {
                ArrayList<Thumbnail> thumbnails = (ArrayList) message.getThumbnails();
                if (thumbnails.size() > 0) {
                    if (message.getType().toLowerCase().contains("gif")) {
                        ImageUtils.displayGifImageFromUrl(context, message.getUrl(), this.fileThumbnailImage, ((Thumbnail) thumbnails.get(0)).getUrl(), this.fileThumbnailImage.getDrawable());
                    } else {
                        ImageUtils.displayImageFromUrl(context, ((Thumbnail) thumbnails.get(0)).getUrl(), this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
                    }
                } else if (message.getType().toLowerCase().contains("gif")) {
                    ImageUtils.displayGifImageFromUrl(context, message.getUrl(), this.fileThumbnailImage, (String) null, this.fileThumbnailImage.getDrawable());
                } else {
                    ImageUtils.displayImageFromUrl(context, message.getUrl(), this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
                }
            } else {
                ImageUtils.displayImageFromUrl(context, tempFileMessageUri.toString(), this.fileThumbnailImage, null);
            }
            if (listener != null) {
                this.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
            if (mItemLongClickListener != null) {
                this.itemView.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        mItemLongClickListener.onFileMessageItemLongClick(message, position);
                        return true;
                    }
                });
            }
        }
    }

    private class MyUserMessageHolder extends ViewHolder {
        CardView card_group_chat_messagex;
        LinearLayout contactlayout;
        TextView dateText;
        TextView editedText;
        ImageView img_gif;
        ImageView ivContactImage;
        TextView messageText;
        View padding;
        TextView readReceiptText;
        ImageView status_img;
        TextView timeText;
        TextView tvContactName;
        TextView tvPhoneNumber;
        ViewGroup urlPreviewContainer;
        TextView urlPreviewDescriptionText;
        ImageView urlPreviewMainImageView;
        TextView urlPreviewSiteNameText;
        TextView urlPreviewTitleText;

        MyUserMessageHolder(View itemView) {
            super(itemView);
            this.messageText = (TextView) itemView.findViewById(R.id.text_group_chat_message);
            this.editedText = (TextView) itemView.findViewById(R.id.text_group_chat_edited);
            this.timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            this.readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            this.dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            this.img_gif = (ImageView) itemView.findViewById(R.id.image_open_chat_file_thumbnail);
            this.card_group_chat_messagex = (CardView) itemView.findViewById(R.id.card_group_chat_messagex);
            this.urlPreviewContainer = (ViewGroup) itemView.findViewById(R.id.url_preview_container);
            this.urlPreviewSiteNameText = (TextView) itemView.findViewById(R.id.text_url_preview_site_name);
            this.urlPreviewTitleText = (TextView) itemView.findViewById(R.id.text_url_preview_title);
            this.urlPreviewDescriptionText = (TextView) itemView.findViewById(R.id.text_url_preview_description);
            this.urlPreviewMainImageView = (ImageView) itemView.findViewById(R.id.image_url_preview_main);
            this.status_img = (ImageView) itemView.findViewById(R.id.status_img);
            this.tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            this.tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            this.contactlayout = (LinearLayout) itemView.findViewById(R.id.contactlayout);
            this.padding = itemView.findViewById(R.id.view_group_chat_padding);
        }

        /* access modifiers changed from: 0000 */
        public void bind(final Context context, final UserMessage message, GroupChannel channel, boolean isContinuous, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, OnItemClickListener clickListener, OnItemLongClickListener longClickListener, int position) {
            UserMessage userMessage = message;
            GroupChannel groupChannel = channel;
            final OnItemLongClickListener onItemLongClickListener = longClickListener;
            if (groupChannel != null) {
                int readReceipt = groupChannel.getReadReceipt(message);
                if (readReceipt > 0) {
                    this.status_img.setImageResource(R.drawable.yellowdot);
                    this.readReceiptText.setVisibility(View.GONE);
                    this.readReceiptText.setText(String.valueOf(readReceipt));
                } else {
                    this.readReceiptText.setVisibility(View.GONE);
                    this.status_img.setImageResource(R.drawable.green_dot);
                }
            }
            if (message.getCustomType().contains("Contact")) {
                this.contactlayout.setVisibility(View.VISIBLE);
                this.tvContactName.setText(message.getMessage());
                this.tvPhoneNumber.setText(message.getData());
                this.messageText.setVisibility(View.GONE);
                this.card_group_chat_messagex.setVisibility(View.GONE);
                Context context2 = context;
            } else {
                if (message.getMessage().contains("giphy.com") || message.getMessage().contains("gif")) {
                    this.card_group_chat_messagex.setVisibility(View.VISIBLE);
                    Glide.with(context).load(message.getMessage()).into(this.img_gif);
                    this.messageText.setVisibility(View.GONE);
                    this.contactlayout.setVisibility(View.GONE);
                } else {
                    this.card_group_chat_messagex.setVisibility(View.GONE);
                    this.messageText.setVisibility(View.VISIBLE);
                    this.messageText.setText(message.getMessage());
                    this.contactlayout.setVisibility(View.GONE);
                }
                this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
                if (message.getUpdatedAt() > 0) {
                    this.editedText.setVisibility(View.VISIBLE);
                } else {
                    this.editedText.setVisibility(View.GONE);
                }
                if (isFailedMessage) {
                    this.readReceiptText.setText(R.string.message_failed);
                    this.readReceiptText.setVisibility(View.GONE);
                } else if (isTempMessage) {
                    this.readReceiptText.setText(R.string.message_sending);
                    this.readReceiptText.setVisibility(View.GONE);
                } else if (groupChannel != null) {
                    int readReceipt2 = groupChannel.getReadReceipt(message);
                    if (readReceipt2 > 0) {
                        this.status_img.setImageResource(R.drawable.yellowdot);
                        this.readReceiptText.setVisibility(View.GONE);
                        this.readReceiptText.setText(String.valueOf(readReceipt2));
                    } else {
                        this.readReceiptText.setVisibility(View.GONE);
                        this.status_img.setImageResource(R.drawable.green_dot);
                    }
                }
                if (isContinuous) {
                    this.padding.setVisibility(View.GONE);
                } else {
                    this.padding.setVisibility(View.VISIBLE);
                }
                if (isNewDay) {
                    this.dateText.setVisibility(View.VISIBLE);
                    this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
                } else {
                    this.dateText.setVisibility(View.GONE);
                }
                this.urlPreviewContainer.setVisibility(View.GONE);
                Context context3 = context;
                this.messageText.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (MyUserMessageHolder.this.messageText.getText().toString().contains("http://maps.google.com/maps?")) {
                            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(message.getMessage())));
                        }
                    }
                });
                if (onItemLongClickListener != null) {
                    final int i = position;
                    this.messageText.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            onItemLongClickListener.onUserMessageItemLongClick(message, i);
                            return true;
                        }
                    });
                    return;
                }
            }
            int i2 = position;
        }
    }

    private class MyVideoFileMessageHolder extends ViewHolder {
        RelativeLayout card_group_chat_message;
        CircleProgressBar circleProgressBar;
        TextView dateText;
        ImageView fileThumbnailImage;
        TextView readReceiptText;
        ImageView status_img;
        TextView timeText;

        public MyVideoFileMessageHolder(View itemView) {
            super(itemView);
            this.timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            this.fileThumbnailImage = (ImageView) itemView.findViewById(R.id.image_group_chat_file_thumbnail);
            this.status_img = (ImageView) itemView.findViewById(R.id.status_img);
            this.readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            this.circleProgressBar = (CircleProgressBar) itemView.findViewById(R.id.circle_progress);
            this.dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            this.card_group_chat_message = (RelativeLayout) itemView.findViewById(R.id.card_group_chat_message);
        }

        /* access modifiers changed from: 0000 */
        public void bind(Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, Uri tempFileMessageUri, final OnItemClickListener listener, final OnItemLongClickListener mItemLongClickListener, final int position) {
            this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
            this.card_group_chat_message.setVisibility(View.VISIBLE);
            if (isFailedMessage) {
                this.readReceiptText.setText(R.string.message_failed);
                this.readReceiptText.setVisibility(View.VISIBLE);
                this.circleProgressBar.setVisibility(View.GONE);
                GroupChatAdapter.this.mFileMessageMap.remove(message);
            } else if (isTempMessage) {
                this.readReceiptText.setText(R.string.message_sending);
                this.readReceiptText.setVisibility(View.GONE);
                this.circleProgressBar.setVisibility(View.VISIBLE);
                GroupChatAdapter.this.mFileMessageMap.put(message, this.circleProgressBar);
            } else {
                this.circleProgressBar.setVisibility(View.GONE);
                GroupChatAdapter.this.mFileMessageMap.remove(message);
                if (channel != null) {
                    int readReceipt = channel.getReadReceipt(message);
                    if (readReceipt > 0) {
                        this.readReceiptText.setVisibility(View.VISIBLE);
                        this.readReceiptText.setText(String.valueOf(readReceipt));
                    } else {
                        this.readReceiptText.setVisibility(View.GONE);
                    }
                }
            }
            if (isNewDay) {
                this.dateText.setVisibility(View.VISIBLE);
                this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                this.dateText.setVisibility(View.GONE);
            }
            if (Common.checkSentVideoIsDownloadedOrNot(message.getName())) {
                StringBuilder sb = new StringBuilder();
                sb.append(Environment.getExternalStorageDirectory());
                sb.append("/BuzzApp/Videos/Sent/");
                sb.append(message.getName());
                File myFile = new File(sb.toString());
                if (myFile.exists()) {
                    Glide.with(GroupChatAdapter.this.mContext).asBitmap().load(Uri.fromFile(myFile)).into(this.fileThumbnailImage);
                } else if (!isTempMessage || tempFileMessageUri == null) {
                    ArrayList<Thumbnail> thumbnails = (ArrayList) message.getThumbnails();
                    if (thumbnails.size() > 0) {
                        ImageUtils.displayImageFromUrl(context, ((Thumbnail) thumbnails.get(0)).getUrl(), this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
                    }
                } else {
                    ImageUtils.displayImageFromUrl(context, tempFileMessageUri.toString(), this.fileThumbnailImage, null);
                }
            } else if (!isTempMessage || tempFileMessageUri == null) {
                ArrayList<Thumbnail> thumbnails2 = (ArrayList) message.getThumbnails();
                if (thumbnails2.size() > 0) {
                    ImageUtils.displayImageFromUrl(context, ((Thumbnail) thumbnails2.get(0)).getUrl(), this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
                } else {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.isMemoryCacheable();
                    Glide.with(context).setDefaultRequestOptions(requestOptions)
                            .load(message.getUrl()).into(this.fileThumbnailImage);
                }
            } else {
                ImageUtils.displayImageFromUrl(context, tempFileMessageUri.toString(), this.fileThumbnailImage, null);
            }
            if (listener != null) {
                this.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
            if (mItemLongClickListener != null) {
                this.itemView.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        mItemLongClickListener.onFileMessageItemLongClick(message, position);
                        return true;
                    }
                });
            }
        }
    }

    public interface OnItemClickListener {
        void onFileMessageItemClick(FileMessage fileMessage);

        void onUserMessageItemClick(UserMessage userMessage);
    }

    public interface OnItemLongClickListener {
        void onAdminMessageItemLongClick(AdminMessage adminMessage);

        void onFileMessageItemLongClick(FileMessage fileMessage, int i);

        void onUserMessageItemLongClick(UserMessage userMessage, int i);
    }

    private class OtherAudioFileMessageHolder extends ViewHolder {
        ImageView pausebutton;
        ImageView playbutton;
        TextView playtime;
        SeekBar seekBar;

        public OtherAudioFileMessageHolder(View itemView) {
            super(itemView);
            this.pausebutton = (ImageView) itemView.findViewById(R.id.image_group_chat_file_thumbnail);
            this.playbutton = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            this.playtime = (TextView) itemView.findViewById(R.id.playtime);
            this.seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
        }
    }

    private class OtherContactFileMessageHolder extends ViewHolder {
        ImageView ivContactImage;
        TextView text_group_chat_time;
        TextView tvContactName;
        TextView tvPhoneNumber;

        public OtherContactFileMessageHolder(View itemView) {
            super(itemView);
            this.tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            this.tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            this.text_group_chat_time = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            this.ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
        }
    }

    private class OtherFileMessageHolder extends ViewHolder {
        /* access modifiers changed from: private */
        public Runnable UpdateSongTime = new Runnable() {
            public void run() {
                OtherFileMessageHolder.this.startTime = (double) OtherFileMessageHolder.this.mediaPlayer.getCurrentPosition();
                OtherFileMessageHolder.this.playtime.setText(String.format("%d min, %d sec", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) OtherFileMessageHolder.this.startTime)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) OtherFileMessageHolder.this.startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) OtherFileMessageHolder.this.startTime)))}));
                OtherFileMessageHolder.this.seekBar.setProgress((int) OtherFileMessageHolder.this.startTime);
                OtherFileMessageHolder.this.myHandler.postDelayed(this, 100);
            }
        };
        CardView audiolayout;
        Button button_group_chat_file_download;
        TextView dateText;
        TextView fileNameText;
        TextView fileSizeText;
        /* access modifiers changed from: private */
        public double finalTime = 0.0d;
        LinearLayout layout_group_chat_file_message;
        MediaPlayer mediaPlayer;
        /* access modifiers changed from: private */
        public Handler myHandler = new Handler();
        TextView nicknameText;
        int oneTimeOnly = 0;
        ImageView pausebutton;
        ImageView playbutton;
        TextView playtime;
        ImageView profileImage;
        TextView readReceiptText;
        SeekBar seekBar;
        /* access modifiers changed from: private */
        public double startTime = 0.0d;
        TextView timeText;

        public OtherFileMessageHolder(View itemView) {
            super(itemView);
            this.nicknameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            this.timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            this.fileNameText = (TextView) itemView.findViewById(R.id.text_group_chat_file_name);
            this.profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            this.dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            this.readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            this.playtime = (TextView) itemView.findViewById(R.id.playtime);
            this.playbutton = (ImageView) itemView.findViewById(R.id.playbutton);
            this.pausebutton = (ImageView) itemView.findViewById(R.id.pausebutton);
            this.seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
            this.audiolayout = (CardView) itemView.findViewById(R.id.audiolayout);
            this.layout_group_chat_file_message = (LinearLayout) itemView.findViewById(R.id.layout_group_chat_file_message);
            this.button_group_chat_file_download = (Button) itemView.findViewById(R.id.button_group_chat_file_download);
        }

        /* access modifiers changed from: 0000 */
        public void bind(final Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isContinuous, final OnItemClickListener listener, final OnItemLongClickListener longClickListener, final int position) {
            this.fileNameText.setText(message.getName());
            this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
            if (message.getType().contains("image") || message.getType().contains("application/ogg")) {
                this.layout_group_chat_file_message.setVisibility(View.GONE);
                this.layout_group_chat_file_message.setVisibility(View.GONE);
                this.audiolayout.setVisibility(View.VISIBLE);
                this.profileImage.setVisibility(View.INVISIBLE);
                this.nicknameText.setVisibility(View.INVISIBLE);
                this.playbutton.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        OtherFileMessageHolder.this.mediaPlayer = MediaPlayer.create(context, Uri.parse(message.getUrl()));
                        OtherFileMessageHolder.this.mediaPlayer.start();
                        OtherFileMessageHolder.this.finalTime = (double) OtherFileMessageHolder.this.mediaPlayer.getDuration();
                        OtherFileMessageHolder.this.startTime = (double) OtherFileMessageHolder.this.mediaPlayer.getCurrentPosition();
                        if (OtherFileMessageHolder.this.oneTimeOnly == 0) {
                            OtherFileMessageHolder.this.seekBar.setMax((int) OtherFileMessageHolder.this.finalTime);
                            OtherFileMessageHolder.this.oneTimeOnly = 1;
                        }
                        OtherFileMessageHolder.this.playtime.setText(String.format("%d min, %d sec", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) OtherFileMessageHolder.this.startTime)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) OtherFileMessageHolder.this.startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) OtherFileMessageHolder.this.startTime)))}));
                        OtherFileMessageHolder.this.seekBar.setProgress((int) OtherFileMessageHolder.this.startTime);
                        OtherFileMessageHolder.this.myHandler.postDelayed(OtherFileMessageHolder.this.UpdateSongTime, 100);
                        OtherFileMessageHolder.this.pausebutton.setVisibility(View.VISIBLE);
                        OtherFileMessageHolder.this.playbutton.setVisibility(View.GONE);
                    }
                });
                this.pausebutton.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        OtherFileMessageHolder.this.mediaPlayer.pause();
                        OtherFileMessageHolder.this.playbutton.setVisibility(View.VISIBLE);
                        OtherFileMessageHolder.this.pausebutton.setVisibility(View.GONE);
                    }
                });
                this.audiolayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        GroupChatAdapter.this.mItemClickListener.onFileMessageItemClick(message);
                    }
                });
            } else if (message.getType().contains("audio") /*|| message.getType().contains("audio/ogg")*/) {
                this.layout_group_chat_file_message.setVisibility(View.GONE);
                this.layout_group_chat_file_message.setVisibility(View.GONE);
                this.audiolayout.setVisibility(View.VISIBLE);
                this.profileImage.setVisibility(View.INVISIBLE);
                this.nicknameText.setVisibility(View.INVISIBLE);
                this.playbutton.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        OtherFileMessageHolder.this.mediaPlayer = MediaPlayer.create(context, Uri.parse(message.getUrl()));
                        OtherFileMessageHolder.this.mediaPlayer.start();
                        OtherFileMessageHolder.this.finalTime = (double) OtherFileMessageHolder.this.mediaPlayer.getDuration();
                        OtherFileMessageHolder.this.startTime = (double) OtherFileMessageHolder.this.mediaPlayer.getCurrentPosition();
                        if (OtherFileMessageHolder.this.oneTimeOnly == 0) {
                            OtherFileMessageHolder.this.seekBar.setMax((int) OtherFileMessageHolder.this.finalTime);
                            OtherFileMessageHolder.this.oneTimeOnly = 1;
                        }
                        OtherFileMessageHolder.this.playtime.setText(String.format("%d min, %d sec", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) OtherFileMessageHolder.this.startTime)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) OtherFileMessageHolder.this.startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) OtherFileMessageHolder.this.startTime)))}));
                        OtherFileMessageHolder.this.seekBar.setProgress((int) OtherFileMessageHolder.this.startTime);
                        OtherFileMessageHolder.this.myHandler.postDelayed(OtherFileMessageHolder.this.UpdateSongTime, 100);
                        OtherFileMessageHolder.this.pausebutton.setVisibility(View.VISIBLE);
                        OtherFileMessageHolder.this.playbutton.setVisibility(View.GONE);
                    }
                });
                this.pausebutton.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        OtherFileMessageHolder.this.mediaPlayer.pause();
                        OtherFileMessageHolder.this.playbutton.setVisibility(View.VISIBLE);
                        OtherFileMessageHolder.this.pausebutton.setVisibility(View.GONE);
                    }
                });
                this.audiolayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        GroupChatAdapter.this.mItemClickListener.onFileMessageItemClick(message);
                    }
                });
            } else {
                this.audiolayout.setVisibility(View.GONE);
                this.layout_group_chat_file_message.setVisibility(View.VISIBLE);
                if (channel != null) {
                    int readReceipt = channel.getReadReceipt(message);
                    if (readReceipt > 0) {
                        this.readReceiptText.setVisibility(View.GONE);
                        this.readReceiptText.setText(String.valueOf(readReceipt));
                    } else {
                        this.readReceiptText.setVisibility(View.GONE);
                    }
                }
                if (isNewDay) {
                    this.dateText.setVisibility(View.VISIBLE);
                    this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
                } else {
                    this.dateText.setVisibility(View.GONE);
                }
                if (isContinuous) {
                    this.profileImage.setVisibility(View.INVISIBLE);
                    this.nicknameText.setVisibility(View.GONE);
                } else {
                    this.profileImage.setVisibility(View.VISIBLE);
                    ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), this.profileImage);
                    this.nicknameText.setVisibility(View.VISIBLE);
                    this.nicknameText.setText(message.getSender().getNickname());
                }
                this.layout_group_chat_file_message.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
//                        Intent intent = new Intent(context, DocumentActivity.class);
//                        intent.putExtra("docurl_key", message.getUrl());
//                        context.startActivity(intent);
                    }
                });
                this.button_group_chat_file_download.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
            this.itemView.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    longClickListener.onFileMessageItemLongClick(message, position);
                    return true;
                }
            });
        }
    }

    private class OtherImageFileMessageHolder extends ViewHolder {
        TextView dateText;
        ImageView fileThumbnailImage;
        LinearLayout llBeforeDownload;
        LinearLayout llBeforeDownloadTop;
        TextView nicknameText;
        ImageView profileImage;
        TextView readReceiptText;
        TextView timeText;
        TextView tvFileSize;

        public OtherImageFileMessageHolder(View itemView) {
            super(itemView);
            this.timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            this.nicknameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            this.fileThumbnailImage = (ImageView) itemView.findViewById(R.id.image_group_chat_file_thumbnail);
            this.profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            this.readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            this.dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            this.tvFileSize = (TextView) itemView.findViewById(R.id.tvFileSize);
            this.llBeforeDownload = (LinearLayout) itemView.findViewById(R.id.llBeforeDownload);
            this.llBeforeDownloadTop = (LinearLayout) itemView.findViewById(R.id.llBeforeDownloadTop);
        }

        /* access modifiers changed from: 0000 */
        public void bind(Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isContinuous, final OnItemClickListener listener, final OnItemLongClickListener mItemLongClickListener, final int position) {
            this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
            if (channel != null) {
                int readReceipt = channel.getReadReceipt(message);
                if (readReceipt > 0) {
                    this.readReceiptText.setVisibility(View.GONE);
                    this.readReceiptText.setText(String.valueOf(readReceipt));
                } else {
                    this.readReceiptText.setVisibility(View.GONE);
                }
            }
            if (isNewDay) {
                this.dateText.setVisibility(View.VISIBLE);
                this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                this.dateText.setVisibility(View.GONE);
            }
            if (channel != null && channel.getMemberCount() <= 2) {
                this.nicknameText.setVisibility(View.GONE);
            } else {
                this.nicknameText.setVisibility(View.VISIBLE);
            }
            if (isContinuous) {
                this.profileImage.setVisibility(View.INVISIBLE);
               // this.nicknameText.setVisibility(View.GONE);
            } else {
                this.profileImage.setVisibility(View.VISIBLE);
                ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), this.profileImage);
               // this.nicknameText.setVisibility(View.VISIBLE);
                this.nicknameText.setText(message.getSender().getNickname());
            }
            this.tvFileSize.setVisibility(View.GONE);
            if (message.getSize() < 1000) {
                TextView textView = this.tvFileSize;
                StringBuilder sb = new StringBuilder();
                sb.append(message.getSize());
                sb.append(" KB");
                textView.setText(sb.toString());
            } else {
                double size = (double) (message.getSize() / 1024);
                TextView textView2 = this.tvFileSize;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(size);
                sb2.append(" MB");
                textView2.setText(sb2.toString());
            }
            //TODO:We are not loading images from Internal storage so hide this code.
           /* if (Common.checkImageIsDownloadedOrNot(message.getName())) {*/
                this.llBeforeDownload.setVisibility(View.GONE);
                this.llBeforeDownloadTop.setVisibility(View.GONE);
           /* } else {
                this.llBeforeDownload.setVisibility(View.VISIBLE);
                this.llBeforeDownloadTop.setVisibility(View.VISIBLE);
            }*/
            ArrayList<Thumbnail> thumbnails = (ArrayList) message.getThumbnails();
            if (thumbnails.size() > 0) {
                if (message.getType().toLowerCase().contains("gif")) {
                    ImageUtils.displayGifImageFromUrl(context, message.getUrl(), this.fileThumbnailImage, ((Thumbnail) thumbnails.get(0)).getUrl(), this.fileThumbnailImage.getDrawable());
                } else {
                    ImageUtils.displayImageFromUrl(context, ((Thumbnail) thumbnails.get(0)).getUrl(), this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
                }
            } else if (message.getType().toLowerCase().contains("gif")) {
                ImageUtils.displayGifImageFromUrl(context, message.getUrl(), this.fileThumbnailImage, (String) null, this.fileThumbnailImage.getDrawable());
            } else {
                ImageUtils.displayImageFromUrl(context, message.getUrl(), this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
            }
            if (listener != null) {
                this.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
            if (mItemLongClickListener != null) {
                this.itemView.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        mItemLongClickListener.onFileMessageItemLongClick(message, position);
                        return true;
                    }
                });
            }
        }
    }

    private class OtherUserMessageHolder extends ViewHolder {
        CardView card_group_chat_messagex;
        LinearLayout contactlayout;
        TextView dateText;
        TextView editedText;
        LinearLayout group_chat_message_container;
        ImageView img_gif;
        ImageView ivContactImage;
        TextView messageText;
        TextView nicknameText;
        ImageView profileImage;
        TextView readReceiptText;
        TextView timeText;
        TextView tvContactName;
        TextView tvPhoneNumber;
        ViewGroup urlPreviewContainer;
        TextView urlPreviewDescriptionText;
        ImageView urlPreviewMainImageView;
        TextView urlPreviewSiteNameText;
        TextView urlPreviewTitleText;

        public OtherUserMessageHolder(View itemView) {
            super(itemView);
            Log.d("abcdas", "onitemviewiewHolder" + itemView.findViewById(R.id.text_group_chat_message));
            this.messageText = (TextView) itemView.findViewById(R.id.text_group_chat_message);
            this.editedText = (TextView) itemView.findViewById(R.id.text_group_chat_edited);
            this.timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            this.nicknameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            this.profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            this.readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            this.dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            this.img_gif = (ImageView) itemView.findViewById(R.id.image_open_chat_file_thumbnail);
            this.urlPreviewContainer = (ViewGroup) itemView.findViewById(R.id.url_preview_container);
            this.urlPreviewSiteNameText = (TextView) itemView.findViewById(R.id.text_url_preview_site_name);
            this.urlPreviewTitleText = (TextView) itemView.findViewById(R.id.text_url_preview_title);
            this.urlPreviewDescriptionText = (TextView) itemView.findViewById(R.id.text_url_preview_description);
            this.urlPreviewMainImageView = (ImageView) itemView.findViewById(R.id.image_url_preview_main);
            this.tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            this.tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            this.contactlayout = (LinearLayout) itemView.findViewById(R.id.contactlayout);
            this.card_group_chat_messagex = (CardView) itemView.findViewById(R.id.card_group_chat_messagex);
            this.group_chat_message_container = (LinearLayout) itemView.findViewById(R.id.group_chat_message_container);
        }

        /* access modifiers changed from: 0000 */
        public void bind(final Context context, final UserMessage message, GroupChannel channel, boolean isNewDay, boolean isContinuous, OnItemClickListener clickListener, OnItemLongClickListener longClickListener, int position) {
            Context context2 = context;
            UserMessage userMessage = message;
            GroupChannel groupChannel = channel;
            final OnItemLongClickListener onItemLongClickListener = longClickListener;
            if (message.getCustomType().contains("Contact")) {
                this.contactlayout.setVisibility(View.VISIBLE);
                this.tvContactName.setText(message.getMessage());
                this.tvPhoneNumber.setText(message.getData());
                this.messageText.setVisibility(View.GONE);
                this.group_chat_message_container.setVisibility(View.GONE);
                this.card_group_chat_messagex.setVisibility(View.GONE);
                this.contactlayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        Intent intent = new Intent("android.intent.action.INSERT");
                        intent.setType("vnd.android.cursor.dir/raw_contact");
                        intent.putExtra("name", message.getMessage()).putExtra("phone", message.getData()).putExtra("phone_type", 3);
                        context.startActivity(intent);
                    }
                });
                this.group_chat_message_container.setVisibility(View.GONE);
                if (groupChannel != null) {
                    int readReceipt = groupChannel.getReadReceipt(message);
                    if (readReceipt > 0) {
                        this.readReceiptText.setVisibility(View.GONE);
                        this.readReceiptText.setText(String.valueOf(readReceipt));
                    } else {
                        this.readReceiptText.setVisibility(View.GONE);
                    }
                }
                if (isNewDay) {
                    this.dateText.setVisibility(View.VISIBLE);
                    this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
                } else {
                    this.dateText.setVisibility(View.GONE);
                }
                if (isContinuous) {
                    this.profileImage.setVisibility(View.INVISIBLE);
                    this.nicknameText.setVisibility(View.GONE);
                } else {
                    this.profileImage.setVisibility(View.VISIBLE);
                    ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), this.profileImage);
                    this.nicknameText.setVisibility(View.VISIBLE);
                    this.nicknameText.setText(message.getSender().getNickname());
                }
            } else {
                this.contactlayout.setVisibility(View.GONE);
                if (message.getMessage().contains("giphy.com") || message.getMessage().contains("gif")) {
                    this.card_group_chat_messagex.setVisibility(View.VISIBLE);
                    Glide.with(context).load(message.getMessage()).into(this.img_gif);
                    this.messageText.setVisibility(View.GONE);
                    this.contactlayout.setVisibility(View.VISIBLE);
                    this.group_chat_message_container.setVisibility(View.VISIBLE);
                } else {
                    this.messageText.setVisibility(View.VISIBLE); //visible
                    this.messageText.setText(message.getMessage());
                    this.card_group_chat_messagex.setVisibility(View.GONE);
                    this.group_chat_message_container.setVisibility(View.VISIBLE);
                }
                this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
                if (message.getUpdatedAt() > 0) {
                    this.editedText.setVisibility(View.VISIBLE);
                } else {
                    this.editedText.setVisibility(View.GONE);
                }
                this.urlPreviewContainer.setVisibility(View.GONE);
                if (groupChannel != null) {
                    int readReceipt2 = groupChannel.getReadReceipt(message);
                    if (readReceipt2 > 0) {
                        this.readReceiptText.setVisibility(View.GONE);
                        this.readReceiptText.setText(String.valueOf(readReceipt2));
                    } else {
                        this.readReceiptText.setVisibility(View.GONE);
                    }
                }
                if (isNewDay) {
                    this.dateText.setVisibility(View.VISIBLE);
                    this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
                } else {
                    this.dateText.setVisibility(View.GONE);
                }
                if (isContinuous) {
                    this.profileImage.setVisibility(View.INVISIBLE);
                    this.nicknameText.setVisibility(View.GONE);
                } else {
                    this.profileImage.setVisibility(View.VISIBLE);
                    ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), this.profileImage);
                    if (groupChannel != null && groupChannel.getMemberCount() <= 2) {
                        this.nicknameText.setVisibility(View.GONE);
                    } else {
                        this.nicknameText.setVisibility(View.VISIBLE);
                    }
                    this.nicknameText.setText(message.getSender().getNickname());
                }
                this.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (OtherUserMessageHolder.this.messageText.getText().toString().contains("http://maps.google.com/maps?")) {
                            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(message.getMessage())));
                        }
                    }
                });
                if (onItemLongClickListener != null) {
                    final int i = position;
                    this.messageText.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            onItemLongClickListener.onUserMessageItemLongClick(message, i);
                            return true;
                        }
                    });
                    return;
                }
            }
            int i2 = position;
        }
    }

    private class OtherVideoFileMessageHolder extends ViewHolder {
        TextView dateText;
        ImageView fileThumbnailImage;
        LinearLayout llBeforeDownload;
        LinearLayout llBeforeDownloadTop;
        TextView nicknameText;
        ImageView profileImage;
        TextView readReceiptText;
        TextView timeText;
        TextView tvFileSize;

        public OtherVideoFileMessageHolder(View itemView) {
            super(itemView);
            this.timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            this.nicknameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            this.fileThumbnailImage = (ImageView) itemView.findViewById(R.id.image_group_chat_file_thumbnail);
            this.profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            this.readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            this.dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            this.tvFileSize = (TextView) itemView.findViewById(R.id.tvFileSize);
            this.llBeforeDownload = (LinearLayout) itemView.findViewById(R.id.llBeforeDownload);
            this.llBeforeDownloadTop = (LinearLayout) itemView.findViewById(R.id.llBeforeDownloadTop);
        }

        /* access modifiers changed from: 0000 */
        public void bind(Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isContinuous, final OnItemClickListener listener, final OnItemLongClickListener mItemLongClickListener, final int position) {
            this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
            if (channel != null) {
                int readReceipt = channel.getReadReceipt(message);
                if (readReceipt > 0) {
                    this.readReceiptText.setVisibility(View.GONE);
                    this.readReceiptText.setText(String.valueOf(readReceipt));
                } else {
                    this.readReceiptText.setVisibility(View.GONE);
                }
            }
            if (isNewDay) {
                this.dateText.setVisibility(View.VISIBLE);
                this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                this.dateText.setVisibility(View.GONE);
            }
            if (channel != null && channel.getMemberCount() <= 2) {
                this.nicknameText.setVisibility(View.GONE);
            } else {
                this.nicknameText.setVisibility(View.VISIBLE);
            }
            if (isContinuous) {
                this.profileImage.setVisibility(View.INVISIBLE);
                //this.nicknameText.setVisibility(View.GONE);
            } else {
                this.profileImage.setVisibility(View.VISIBLE);
                ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), this.profileImage);
                //this.nicknameText.setVisibility(View.VISIBLE);
                this.nicknameText.setText(message.getSender().getNickname());
            }
            this.tvFileSize.setVisibility(View.GONE);
            if (message.getSize() < 1000) {
                TextView textView = this.tvFileSize;
                StringBuilder sb = new StringBuilder();
                sb.append(message.getSize());
                sb.append(" KB");
                textView.setText(sb.toString());
            } else {
                double size = (double) (message.getSize() / 1024);
                TextView textView2 = this.tvFileSize;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(size);
                sb2.append(" MB");
                textView2.setText(sb2.toString());
            }
            if (Common.checkVideoIsDownloadedOrNot(message.getName())) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(Environment.getExternalStorageDirectory());
                sb3.append("/BuzzApp/Videos/");
                sb3.append(message.getName());
                File myFile = new File(sb3.toString());
                /*if (myFile.exists()) {
                    this.llBeforeDownload.setVisibility(View.GONE);
                    this.llBeforeDownloadTop.setVisibility(View.GONE);
                    Glide.with(GroupChatAdapter.this.mContext).asBitmap().load(Uri.fromFile(myFile)).into(this.fileThumbnailImage);
               /* } else {
                    this.llBeforeDownload.setVisibility(View.VISIBLE);
                    this.llBeforeDownloadTop.setVisibility(View.VISIBLE);
                }
            } else {
                this.llBeforeDownload.setVisibility(View.VISIBLE);
                this.llBeforeDownloadTop.setVisibility(View.VISIBLE);
            }*/
            }
            this.llBeforeDownload.setVisibility(View.GONE);
            this.llBeforeDownloadTop.setVisibility(View.GONE);
            ArrayList<Thumbnail> thumbnails = (ArrayList) message.getThumbnails();
            if (thumbnails.size() > 0) {
                ImageUtils.displayImageFromUrl(context, ((Thumbnail) thumbnails.get(0)).getUrl(),
                        this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
            } else {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.isMemoryCacheable();
                Glide.with(context).setDefaultRequestOptions(requestOptions)
                        .load(message.getUrl()).into(this.fileThumbnailImage);
            }
            if (listener != null) {
                this.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
            if (mItemLongClickListener != null) {
                this.itemView.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        mItemLongClickListener.onFileMessageItemLongClick(message, position);
                        return true;
                    }
                });
            }
        }
    }

    public GroupChatAdapter(Context context) {
        this.mContext = context;
        this.mFileMessageMap = new HashMap<>();
        this.mMessageList = new ArrayList();
        this.mMessageListFiltered = new ArrayList<>();
        this.mFailedMessageList = new ArrayList<>();
        this.mResendingMessageSet = new HashSet<>();
        Log.d("GroupChatAdapter", "cinstuct");
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

//    public void load(String channelUrl) {
//        try {
//            //String content = (String) this.sharedPrefsHelper.get(channelUrl, null);
//            Log.d("grp","123"+content);
//            if (content != null) {
//                String[] dataArray = content.split(ToStringHelper.SEPARATOR);
//                this.mChannel = (GroupChannel) GroupChannel.buildFromSerializedData(Base64.decode(dataArray[0], 2));
//                this.mMessageList.clear();
//                for (int i = 1; i < dataArray.length; i++) {
//                    this.mMessageList.add(BaseMessage.buildFromSerializedData(Base64.decode(dataArray[i], 2)));
//                }
//                notifyDataSetChanged();
//                return;
//            }
//            // this.mMessageList.clear();
//            notifyDataSetChanged();
//        } catch (Exception e) {
//        }
//    }
//
//    public void save(String channelUrl) {
//        String ChannelKey = channelUrl;
//        try {
//            StringBuilder sb = new StringBuilder();
//            if (this.mChannel != null) {
//                sb.append(Base64.encodeToString(this.mChannel.serialize(), 2));
//                for (int i = 0; i < Math.min(this.mMessageList.size(), 100); i++) {
//                    BaseMessage message = (BaseMessage) this.mMessageList.get(i);
//                    if (!isTempMessage(message)) {
//                        sb.append(",");
//                        sb.append(Base64.encodeToString(message.serialize(), 2));
//                    }
//                }
////                this.sharedPrefsHelper.save(ChannelKey, sb.toString());
//////                Log.d("grp","1234"+SharedPrefsHelper.SEND_BIRD_USER_ID+ SendBird.getCurrentUser().getUserId());
////                this.sharedPrefsHelper.save(SharedPrefsHelper.SEND_BIRD_USER_ID, SendBird.getCurrentUser().getUserId());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mChannel.markAsRead();
        Log.d("GroupChatAdapter", "onCreateViewHolder" + viewType);
        switch (viewType) {
            case VIEW_TYPE_USER_MESSAGE_ME:
                View myUserMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_user_me, parent, false);
                return new MyUserMessageHolder(myUserMsgView);
            case VIEW_TYPE_USER_MESSAGE_OTHER:
                View otherUserMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_user_other, parent, false);
                return new OtherUserMessageHolder(otherUserMsgView);

            case VIEW_TYPE_ADMIN_MESSAGE:
                View adminMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_admin, parent, false);
                return new AdminMessageHolder(adminMsgView);


            case VIEW_TYPE_FILE_MESSAGE_AUDIO_ME:
                View myFileMsgViewn = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_audio_file_me, parent, false);
                return new MyFileMessageHolder(myFileMsgViewn);
            case VIEW_TYPE_FILE_MESSAGE_AUDIO_OTHER:
                View otherFileMsgViewn = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_audio_file_other, parent, false);
                return new OtherFileMessageHolder(otherFileMsgViewn);

            case VIEW_TYPE_FILE_MESSAGE_ME:
                View myFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_me, parent, false);
                return new MyFileMessageHolderFile(myFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_OTHER:
                View otherFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_other, parent, false);
                return new OtherFileMessageHolderFile(otherFileMsgView);

            case VIEW_TYPE_FILE_MESSAGE_IMAGE_ME:
                View myImageFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_image_me, parent, false);
                return new MyImageFileMessageHolder(myImageFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER:
                View otherImageFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_image_other, parent, false);
                return new OtherImageFileMessageHolder(otherImageFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_VIDEO_ME:
                View myVideoFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_video_me, parent, false);
                return new MyVideoFileMessageHolder(myVideoFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER:
                View otherVideoFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_video_other, parent, false);
                return new OtherVideoFileMessageHolder(otherVideoFileMsgView);

            default:
                return null;

        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0063, code lost:
        r5 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d("GroupChatAdapter", "onBindViewHolder" + position);
        //BaseMessage message = mMessageList.get(position);
        BaseMessage message = getMessage(position);
        boolean isContinuous = false;
        boolean isNewDay = false;
        boolean isTempMessage = false;
        boolean isFailedMessage = false;
        Uri tempFileMessageUri = null;

        // If there is at least one item preceding the current one, check the previous message.
        if (position < mMessageList.size() + mFailedMessageList.size() - 1) {
            BaseMessage prevMessage = mMessageList.get(position + 1);

            // If the date of the previous message is different, display the date before the message,
            // and also set isContinuous to false to show information such as the sender's nickname
            // and profile image.
            if (!DateUtils.hasSameDate(message.getCreatedAt(), prevMessage.getCreatedAt())) {
                isNewDay = true;
                isContinuous = false;
            } else {
                isContinuous = isContinuous(message, prevMessage);
            }
        } else if (position == mMessageList.size() - 1) {
            isNewDay = true;
        }

        isTempMessage = isTempMessage(message);
        tempFileMessageUri = getTempFileMessageUri(message);
        isFailedMessage = isFailedMessage(message);


        switch (holder.getItemViewType()) {
            case VIEW_TYPE_USER_MESSAGE_ME:
                ((MyUserMessageHolder) holder).bind(mContext, (UserMessage) message, mChannel, isContinuous, isNewDay, isTempMessage, isFailedMessage, mItemClickListener, mItemLongClickListener, position);
                break;
            case VIEW_TYPE_USER_MESSAGE_OTHER:
                ((OtherUserMessageHolder) holder).bind(mContext, (UserMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener, mItemLongClickListener, position);
                break;
            case VIEW_TYPE_ADMIN_MESSAGE:
                if(message instanceof AdminMessage){
                    ((AdminMessageHolder) holder).bind(mContext, (AdminMessage) message, mChannel, isNewDay);
                } else {
                    ((AdminMessageHolder) holder).bind(mContext, (UserMessage) message, mChannel, isNewDay);
                }
                break;


            case VIEW_TYPE_FILE_MESSAGE_ME:
                ((MyFileMessageHolderFile) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_OTHER:
                ((OtherFileMessageHolderFile) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener);
                break;


            case VIEW_TYPE_FILE_MESSAGE_IMAGE_ME:
                ((MyImageFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener, mItemLongClickListener, position);
                break;
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER:
                ((OtherImageFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener, mItemLongClickListener, position);
                break;
            case VIEW_TYPE_FILE_MESSAGE_VIDEO_ME:
                ((MyVideoFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener, mItemLongClickListener, position);
                break;
            case VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER:
                ((OtherVideoFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener, mItemLongClickListener, position);
                break;


            case VIEW_TYPE_FILE_MESSAGE_AUDIO_ME:
                try {
                    ((MyFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener, mItemLongClickListener, position);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case VIEW_TYPE_FILE_MESSAGE_AUDIO_OTHER:
                ((OtherFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener, mItemLongClickListener, position);
                break;


            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("GroupChatAdapter", "getItemViewType" + position);

        BaseMessage message = getMessage(position);
        boolean isMyMessage = false;

        if (message instanceof UserMessage) {
            if(message.getCustomType().equals("ADMM")){
                return VIEW_TYPE_ADMIN_MESSAGE;
            } else {
                UserMessage.RequestState requestState = ((UserMessage) message).getRequestState();
                if (requestState == UserMessage.RequestState.PENDING
                        || requestState == UserMessage.RequestState.FAILED
                        || ((UserMessage) message).getSender().getUserId().equals(getMyUserId())) {
                    isMyMessage = true;
                }
            }
        } else if (message instanceof FileMessage) {
            FileMessage.RequestState requestState = ((FileMessage) message).getRequestState();
            if (requestState == FileMessage.RequestState.PENDING
                    || requestState == FileMessage.RequestState.FAILED
                    || ((FileMessage) message).getSender().getUserId().equals(getMyUserId())) {
                isMyMessage = true;
            }
        }

        if (message instanceof UserMessage) {
            if (isMyMessage) {
                return VIEW_TYPE_USER_MESSAGE_ME;
            } else {
                return VIEW_TYPE_USER_MESSAGE_OTHER;
            }
        } else if (message instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) message;
            if (fileMessage.getType().toLowerCase().startsWith("image")) {
                // If the sender is current user
                if (isMyMessage) {
                    return VIEW_TYPE_FILE_MESSAGE_IMAGE_ME;
                } else {
                    return VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER;
                }
            } else if (fileMessage.getType().toLowerCase().startsWith("video")) {
                if (isMyMessage) {
                    return VIEW_TYPE_FILE_MESSAGE_VIDEO_ME;
                } else {
                    return VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER;
                }
            } else if (fileMessage.getType().toLowerCase().startsWith("audio")) {
                if (isMyMessage) {
                    return VIEW_TYPE_FILE_MESSAGE_AUDIO_ME;
                } else {
                    return VIEW_TYPE_FILE_MESSAGE_AUDIO_OTHER;
                }
            } else {
                if (isMyMessage) {
                    return VIEW_TYPE_FILE_MESSAGE_ME;
                } else {
                    return VIEW_TYPE_FILE_MESSAGE_OTHER;
                }
            }
        } else if (message instanceof AdminMessage) {
            return VIEW_TYPE_ADMIN_MESSAGE;
        }

        return -1;
    }

    private BaseMessage getMessage(int position) {
        if (position < mFailedMessageList.size()) {
            return mFailedMessageList.get(position);
        } else if (position < mFailedMessageList.size() + mMessageList.size()) {
            return mMessageList.get(position - mFailedMessageList.size());
        } else {
            return null;
        }
    }

    public int getItemCount() {

        Log.d("GroupChatAdapter", "getItemCount" + this.mMessageList.size() + mFailedMessageList.size());
        return mMessageList.size() + mFailedMessageList.size();
    }

    /*@Override
    public int getItemCount() {
        Log.d("GroupChatAdapter", "getItemCount" + this.mMessageListFiltered.size()
                + mFailedMessageList.size());
        return mMessageListFiltered.size() + + mFailedMessageList.size();
    }*/

    public void setChannel(GroupChannel channel) {

        Log.d("GroupChatAdapter", "setChannel" + channel);
        this.mChannel = channel;
    }

    public boolean isTempMessage(BaseMessage message) {
        return message.getMessageId() == 0;
    }

    public boolean isFailedMessage(BaseMessage message) {
        if (!isTempMessage(message)) {
            return false;
        }
        if (message instanceof UserMessage) {
            if (this.mFailedMessageIdList.indexOf(((UserMessage) message).getRequestId()) >= 0) {
                return true;
            }
        } else if (!(message instanceof FileMessage) || this.mFailedMessageIdList.indexOf(((FileMessage) message).getRequestId()) < 0) {
            return false;
        } else {
            return true;
        }
        return false;
    }

    public Uri getTempFileMessageUri(BaseMessage message) {
        if (isTempMessage(message) && (message instanceof FileMessage)) {
            return (Uri) this.mTempFileMessageUriTable.get(((FileMessage) message).getRequestId());
        }
        return null;
    }

    public void markMessageFailed(String requestId) {
        this.mFailedMessageIdList.add(requestId);
        notifyDataSetChanged();
    }

    public void removeFailedMessage(BaseMessage message) {
        if (message instanceof UserMessage) {
            this.mFailedMessageIdList.remove(((UserMessage) message).getRequestId());
            this.mMessageList.remove(message);
        } else if (message instanceof FileMessage) {
            this.mFailedMessageIdList.remove(((FileMessage) message).getRequestId());
            this.mTempFileMessageUriTable.remove(((FileMessage) message).getRequestId());
            this.mMessageList.remove(message);
        }
        notifyDataSetChanged();
    }

    public void setFileProgressPercent(FileMessage message, int percent) {
        int i = this.mMessageList.size() - 1;
        while (i >= 0) {
            BaseMessage msg = (BaseMessage) this.mMessageList.get(i);
            if (!(msg instanceof FileMessage) || !message.getRequestId().equals(((FileMessage) msg).getRequestId())) {
                i--;
            } else {
                CircleProgressBar circleProgressBar = (CircleProgressBar) this.mFileMessageMap.get(message);
                if (circleProgressBar != null) {
                    circleProgressBar.setProgress(percent);
                    return;
                }
                return;
            }
        }
    }

    public void markMessageSent(BaseMessage message) {
        for (int i = this.mMessageList.size() - 1; i >= 0; i--) {
            BaseMessage msg = (BaseMessage) this.mMessageList.get(i);
            if (!(message instanceof UserMessage) || !(msg instanceof UserMessage)) {
                if ((message instanceof FileMessage) && (msg instanceof FileMessage) && ((FileMessage) msg).getRequestId().equals(((FileMessage) message).getRequestId())) {
                    this.mTempFileMessageUriTable.remove(((FileMessage) message).getRequestId());
                    this.mMessageList.set(i, message);
                    notifyDataSetChanged();
                    return;
                }
            } else if (((UserMessage) msg).getRequestId().equals(((UserMessage) message).getRequestId())) {
                this.mMessageList.set(i, message);
                notifyDataSetChanged();
                return;
            }
        }
    }

    public void addTempFileMessageInfo(FileMessage message, Uri uri) {
        this.mTempFileMessageUriTable.put(message.getRequestId(), uri);
    }

    public void addFirst(BaseMessage message) {
        this.mMessageList.add(0, message);
        notifyDataSetChanged();
    }

    public void delete(long msgId) {
        for (BaseMessage msg : this.mMessageList) {
            if (msg.getMessageId() == msgId) {
                this.mMessageList.remove(msg);
                notifyDataSetChanged();
                return;
            }
        }
    }

    public void update(BaseMessage message) {
        for (int index = 0; index < this.mMessageList.size(); index++) {
            if (message.getMessageId() == ((BaseMessage) this.mMessageList.get(index)).getMessageId()) {
                this.mMessageList.remove(index);
                this.mMessageList.add(index, message);
                notifyDataSetChanged();
                return;
            }
        }
    }

    private synchronized boolean isMessageListLoading() {
        return this.mIsMessageListLoading;
    }

    /* access modifiers changed from: private */
    public synchronized void setMessageListLoading(boolean tf) {
        this.mIsMessageListLoading = tf;
    }

    public void markAllMessagesAsRead() {
        if (this.mChannel != null) {
            this.mChannel.markAsRead();
        }
        notifyDataSetChanged();
    }

    public int getLastReadPosition(long lastRead) {
        for (int i = 0; i < mMessageList.size(); i++) {
            if (mMessageList.get(i).getCreatedAt() == lastRead) {
                return i + mFailedMessageList.size();
            }
        }

        return 0;
    }

    public void loadPreviousMessages(int limit, final GetMessagesHandler handler) {
        if (!isMessageListLoading()) {
            long oldestMessageCreatedAt = LongCompanionObject.MAX_VALUE;
            if (this.mMessageList.size() > 0) {
                oldestMessageCreatedAt = ((BaseMessage) this.mMessageList.get(this.mMessageList.size() - 1)).getCreatedAt();
            }
            setMessageListLoading(true);
            this.mChannel.getPreviousMessagesByTimestamp(oldestMessageCreatedAt, false, limit, true, MessageTypeFilter.ALL, null, new GetMessagesHandler() {
                public void onResult(List<BaseMessage> list, SendBirdException e) {
                    if (handler != null) {
                        handler.onResult(list, e);
                    }
                    GroupChatAdapter.this.setMessageListLoading(false);
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    for (BaseMessage message : list) {
                        GroupChatAdapter.this.mMessageList.add(message);
                    }
                    GroupChatAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    public void loadLatestMessages(int limit, final GetMessagesHandler handler) {
        if (!isMessageListLoading()) {
            setMessageListLoading(true);

            this.mChannel.getPreviousMessagesByTimestamp(LongCompanionObject.MAX_VALUE, true, limit, false, MessageTypeFilter.ALL, null, new GetMessagesHandler() {
                public void onResult(List<BaseMessage> list, SendBirdException e) {
                    if (handler != null) {
                        handler.onResult(list, e);
                    }
                    GroupChatAdapter.this.setMessageListLoading(false);
                    if (e != null) {
                        e.printStackTrace();
                    } else if (list.size() > 0) {
                        for (BaseMessage message : GroupChatAdapter.this.mMessageList) {
                            if (GroupChatAdapter.this.isTempMessage(message) || GroupChatAdapter.this.isFailedMessage(message)) {
                                list.add(0, message);
                            }
                        }
                        GroupChatAdapter.this.mMessageList.clear();
                        for (BaseMessage message2 : list) {
                            GroupChatAdapter.this.mMessageList.add(message2);
                        }
                        GroupChatAdapter.this.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void setItemLongClickListener(OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    private boolean isContinuous(BaseMessage currentMsg, BaseMessage precedingMsg) {
        boolean z = false;
        if (currentMsg == null || precedingMsg == null) {
            return false;
        }
        if ((currentMsg instanceof AdminMessage) && (precedingMsg instanceof AdminMessage)) {
            return true;
        }
        User currentUser = null;
        User precedingUser = null;
        if (currentMsg instanceof UserMessage) {
            currentUser = ((UserMessage) currentMsg).getSender();
        } else if (currentMsg instanceof FileMessage) {
            currentUser = ((FileMessage) currentMsg).getSender();
        }
        if (precedingMsg instanceof UserMessage) {
            precedingUser = ((UserMessage) precedingMsg).getSender();
        } else if (precedingMsg instanceof FileMessage) {
            precedingUser = ((FileMessage) precedingMsg).getSender();
        }
        if (!(currentUser == null || precedingUser == null || !currentUser.getUserId().equals(precedingUser.getUserId()))) {
            z = true;
        }
        return z;
    }

    public void setSearchResult(List<BaseMessage> result) {
        this.mMessageList = result;
        notifyDataSetChanged();
    }

    public void insertSucceededMessages(List<BaseMessage> messages) {
        for (BaseMessage message : messages) {
            int index = SyncManagerUtils.findIndexOfMessage(mMessageList, message);
            mMessageList.add(index, message);
        }

        notifyDataSetChanged();
    }

    public void updateSucceededMessages(List<BaseMessage> messages) {
        for (BaseMessage message : messages) {
            int index = SyncManagerUtils.getIndexOfMessage(mMessageList, message);
            if (index != -1) {
                mMessageList.set(index, message);
                notifyItemChanged(index);
            }
        }
    }

    public void removeSucceededMessages(List<BaseMessage> messages) {
        for (BaseMessage message : messages) {
            int index = SyncManagerUtils.getIndexOfMessage(mMessageList, message);
            if (index != -1) {
                mMessageList.remove(index);
            }
        }

        notifyDataSetChanged();
    }

    private String getRequestId(BaseMessage message) {
        if (message instanceof UserMessage) {
            return ((UserMessage) message).getRequestId();
        } else if (message instanceof FileMessage) {
            return ((FileMessage) message).getRequestId();
        }

        return "";
    }

    public void insertFailedMessages(List<BaseMessage> messages) {
        synchronized (mFailedMessageList) {
            for (BaseMessage message : messages) {
                String requestId = getRequestId(message);
                if (requestId.isEmpty()) {
                    continue;
                }

                mResendingMessageSet.add(requestId);
                mFailedMessageList.add(message);
            }

            Collections.sort(mFailedMessageList, new Comparator<BaseMessage>() {
                @Override
                public int compare(BaseMessage m1, BaseMessage m2) {
                    long x = m1.getCreatedAt();
                    long y = m2.getCreatedAt();

                    return (x < y) ? 1 : ((x == y) ? 0 : -1);
                }
            });
        }

        notifyDataSetChanged();
    }

    public void updateFailedMessages(List<BaseMessage> messages) {
        synchronized (mFailedMessageList) {
            for (BaseMessage message : messages) {
                String requestId = getRequestId(message);
                if (requestId.isEmpty()) {
                    continue;
                }

                mResendingMessageSet.remove(requestId);
            }
        }

        notifyDataSetChanged();
    }

    public void removeFailedMessages(List<BaseMessage> messages) {
        synchronized (mFailedMessageList) {
            for (BaseMessage message : messages) {
                String requestId = getRequestId(message);
                mResendingMessageSet.remove(requestId);
                mFailedMessageList.remove(message);
            }
        }

        notifyDataSetChanged();
    }

    public boolean failedMessageListContains(BaseMessage message) {
        if (mFailedMessageList.isEmpty()) {
            return false;
        }
        for (BaseMessage failedMessage : mFailedMessageList) {
            if (message instanceof UserMessage && failedMessage instanceof UserMessage) {
                if (((UserMessage) message).getRequestId().equals(((UserMessage) failedMessage).getRequestId())) {
                    return true;
                }
            } else if (message instanceof FileMessage && failedMessage instanceof FileMessage) {
                if (((FileMessage) message).getRequestId().equals(((FileMessage) failedMessage).getRequestId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void clear() {
        mMessageList.clear();
        mFailedMessageList.clear();
        notifyDataSetChanged();
    }

    public boolean isResendingMessage(BaseMessage message) {
        if (message == null) {
            return false;
        }

        return mResendingMessageSet.contains(getRequestId(message));
    }

}
