package com.example.myapplication.OpenChats;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Common;
import com.example.myapplication.Utils.DateUtils;
import com.example.myapplication.Utils.ImageUtils;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;
import com.sendbird.calls.shadow.kotlin.jvm.internal.LongCompanionObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.example.myapplication.Utils.SyncManagerUtils.getMyUserId;

/**
 * An adapter for a RecyclerView that displays messages in an Open Channel.
 */

class OpenChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER_MESSAGE = 10;
    private static final int VIEW_TYPE_FILE_MESSAGE = 20;
    private  String channelurl;
    public HashMap<FileMessage, CircleProgressBar> mFileMessageMap;
    private Hashtable<String, Uri> mTempFileMessageUriTable = new Hashtable<>();
    private Context mContext;
    private OpenChannel openChannel1;
    private List<BaseMessage> mMessageList;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    //
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
    private OpenChannel mChannel;
    /* access modifiers changed from: private */

    private ArrayList<String> mFailedMessageIdList = new ArrayList<>();
    /* access modifiers changed from: private */

    private boolean mIsMessageListLoading;

    /* access modifiers changed from: private */

   // private SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
    private List<BaseMessage> mFailedMessageList;
    private Set<String> mResendingMessageSet;
    
    
    OpenChatAdapter(Context context, String chanelUrl) {
        this.channelurl=chanelUrl;
        mMessageList = new ArrayList<>();
        this.mFileMessageMap = new HashMap<>();
        mContext = context;
        this.mResendingMessageSet=new HashSet<>();
        OpenChannel.getChannel(chanelUrl, new OpenChannel.OpenChannelGetHandler() {


            @Override
            public void onResult(OpenChannel openChannel, SendBirdException e) {
                if (e != null) {    // Error.
                    openChannel1=openChannel;
                }

                // TODO: Implement what is needed with the contents of the response in the openChannel parameter.
            }
        });
    }

   

    void setMessageList(List<BaseMessage> messages) {
        mMessageList = messages;
        notifyDataSetChanged();
    }

    void addFirst(BaseMessage message) {
        mMessageList.add(0, message);
        notifyDataSetChanged();
    }

    void addLast(BaseMessage message) {
        mMessageList.add(message);
        notifyDataSetChanged();
    }
    public boolean isResendingMessage(BaseMessage message) {
        if (message == null) {
            return false;
        }

        return mResendingMessageSet.contains(getRequestId(message));
    }
    private String getRequestId(BaseMessage message) {
        if (message instanceof UserMessage) {
            return ((UserMessage) message).getRequestId();
        } else if (message instanceof FileMessage) {
            return ((FileMessage) message).getRequestId();
        }

        return "";
    }
    void delete(long msgId) {
        for (BaseMessage msg : mMessageList) {
            if (msg.getMessageId() == msgId) {
                mMessageList.remove(msg);
                notifyDataSetChanged();
                break;
            }
        }
    }

    void update(BaseMessage message) {
        BaseMessage baseMessage;
        for (int index = 0; index < mMessageList.size(); index++) {
            baseMessage = mMessageList.get(index);
            if (message.getMessageId() == baseMessage.getMessageId()) {
                mMessageList.remove(index);
                mMessageList.add(index, message);
                notifyDataSetChanged();
                break;
            }
        }
    }
   
    private class MyAudioFileMessageHolder extends RecyclerView.ViewHolder {
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

    private class MyContactFileMessageHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        ImageView ivContactImage;
        TextView timeText;
        TextView tvContactName;
        TextView tvPhoneNumber;

        public MyContactFileMessageHolder(View itemView)
        {
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

        void bind(Context context, final FileMessage message, OpenChannel channel, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, Uri tempFileMessageUri, final OnItemClickListener listener) {
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
//
//                if (channel != null) {
//                    int readReceipt = channel.getReadReceipt(message);
//                    if (readReceipt > 0) {
//                        readReceiptText.setVisibility(View.VISIBLE);
//                        readReceiptText.setText(String.valueOf(readReceipt));
//                    } else {
//                        readReceiptText.setVisibility(View.INVISIBLE);
//                    }
//                }

            }
//            if (channel != null) {
//                int readReceipt2 = channel.getReadReceipt(message);
//                if (readReceipt2 > 0) {
//                    this.status_img.setImageResource(R.drawable.yellowdot);
//                    this.readReceiptText.setVisibility(View.GONE);
//                    this.readReceiptText.setText(String.valueOf(readReceipt2));
//                } else {
//                    this.readReceiptText.setVisibility(View.GONE);
//                    this.status_img.setImageResource(R.drawable.green_dot);
//                }
//            }
            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
        }
    }
    private class AdminMessageHolder extends RecyclerView.ViewHolder {
        private TextView dateText;
        private TextView messageText;

        AdminMessageHolder(View itemView) {
            super(itemView);
            this.messageText = (TextView) itemView.findViewById(R.id.text_group_chat_message);
            this.dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
        }

        /* access modifiers changed from: 0000 */
        public void bind(Context context, AdminMessage message, OpenChannel channel, boolean isNewDay) {
            this.messageText.setText(message.getMessage());
            if (isNewDay) {
                this.dateText.setVisibility(View.VISIBLE);
                this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
                return;
            }
            this.dateText.setVisibility(View.GONE);
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

        void bind(Context context, final FileMessage message, OpenChannel channel, boolean isNewDay, boolean isContinuous, final OnItemClickListener listener) {
            fileNameText.setText(message.getName());
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
            this.profileImage.setMinimumWidth(25);
//            fileSizeText.setText(String.valueOf(message.getSize()));

            // Since setChannel is set slightly after adapter is created, check if null.
//            if (channel != null) {
//                int readReceipt = channel.getReadReceipt(message);
//                if (readReceipt > 0) {
//                    readReceiptText.setVisibility(View.VISIBLE);
//                    readReceiptText.setText(String.valueOf(readReceipt));
//                } else {
//                    readReceiptText.setVisibility(View.INVISIBLE);
//                }
//            }

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
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
        }
    }


    private class MyFileMessageHolder extends RecyclerView.ViewHolder {
        /* access modifiers changed from: private */
        public Runnable UpdateSongTime = new Runnable() {
            public void run() {
                startTime = (double) OpenChatAdapter.MyFileMessageHolder.this.mediaPlayer.getCurrentPosition();
                playtime.setText(String.format("%d min, %d sec", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) OpenChatAdapter.MyFileMessageHolder.this.startTime)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) OpenChatAdapter.MyFileMessageHolder.this.startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) OpenChatAdapter.MyFileMessageHolder.this.startTime)))}));
                seekBar.setProgress((int) OpenChatAdapter.MyFileMessageHolder.this.startTime);
                OpenChatAdapter.MyFileMessageHolder.this.myHandler.postDelayed(this, 100);
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
        public void bind(final Context context, final FileMessage message, OpenChannel channel, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, Uri tempFileMessageUri, final OpenChatAdapter.OnItemClickListener listener, final OpenChatAdapter.OnItemLongClickListener longClickListener, final int position) throws IOException {
            //  this.fileNameText.setText(message.getName());
            this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
//            if (channel != null) {
//                int readReceipt = channel.getReadReceipt(message);
//                if (readReceipt > 0) {
//                    this.readReceiptText.setVisibility(View.VISIBLE);
//                    this.readReceiptText.setText(String.valueOf(readReceipt));
//                } else {
//                    this.readReceiptText.setVisibility(View.INVISIBLE);
//                }
//            }
            if (isNewDay) {
                this.dateText.setVisibility(View.VISIBLE);
                this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                this.dateText.setVisibility(View.GONE);
            }
//            if (channel != null) {
//                int readReceipt2 = channel.getReadReceipt(message);
//                if (readReceipt2 > 0) {
//                    this.status_img.setImageResource(R.drawable.yellowdot);
//                    this.readReceiptText.setVisibility(View.GONE);
//                    this.readReceiptText.setText(String.valueOf(readReceipt2));
//                } else {
//                    this.readReceiptText.setVisibility(View.GONE);
//                    this.status_img.setImageResource(R.drawable.green_dot);
//                }
//            }
            if (message.getType().contains("audio") || message.getType().contains("application/ogg")) {
                //        this.layout_group_chat_file_message.setVisibility(View.GONE);
                this.audiolayout.setVisibility(View.VISIBLE);
                //   this.contactlayout.setVisibility(View.VISIBLE);
                this.playbutton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        OpenChatAdapter.MyFileMessageHolder.this.mediaPlayer = MediaPlayer.create(context, Uri.parse(message.getUrl()));
                        if (OpenChatAdapter.MyFileMessageHolder.this.mediaPlayer != null) {
                            OpenChatAdapter.MyFileMessageHolder.this.mediaPlayer.start();
                            OpenChatAdapter.MyFileMessageHolder.this.finalTime = (double) OpenChatAdapter.MyFileMessageHolder.this.mediaPlayer.getDuration();
                            OpenChatAdapter.MyFileMessageHolder.this.startTime = (double) OpenChatAdapter.MyFileMessageHolder.this.mediaPlayer.getCurrentPosition();
                            if (OpenChatAdapter.MyFileMessageHolder.this.oneTimeOnly == 0) {
                                OpenChatAdapter.MyFileMessageHolder.this.seekBar.setMax((int) OpenChatAdapter.MyFileMessageHolder.this.finalTime);
                                OpenChatAdapter.MyFileMessageHolder.this.oneTimeOnly = 1;
                            }
                            OpenChatAdapter.MyFileMessageHolder.this.playtime.setText(String.format("%d min, %d sec", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) OpenChatAdapter.MyFileMessageHolder.this.startTime)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) OpenChatAdapter.MyFileMessageHolder.this.startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) OpenChatAdapter.MyFileMessageHolder.this.startTime)))}));
                            OpenChatAdapter.MyFileMessageHolder.this.seekBar.setProgress((int) OpenChatAdapter.MyFileMessageHolder.this.startTime);
                            OpenChatAdapter.MyFileMessageHolder.this.myHandler.postDelayed(OpenChatAdapter.MyFileMessageHolder.this.UpdateSongTime, 100);
                            OpenChatAdapter.MyFileMessageHolder.this.pausebutton.setVisibility(View.VISIBLE);
                            OpenChatAdapter.MyFileMessageHolder.this.playbutton.setVisibility(View.GONE);
                        }
                    }
                });
                this.pausebutton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (OpenChatAdapter.MyFileMessageHolder.this.mediaPlayer != null) {
                            OpenChatAdapter.MyFileMessageHolder.this.mediaPlayer.pause();
                            OpenChatAdapter.MyFileMessageHolder.this.playbutton.setVisibility(View.VISIBLE);
                            OpenChatAdapter.MyFileMessageHolder.this.pausebutton.setVisibility(View.GONE);
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
                    OpenChatAdapter.this.mFileMessageMap.remove(message);
                } else if (isTempMessage) {
                    this.readReceiptText.setText(R.string.message_sending);
                    this.readReceiptText.setVisibility(View.GONE);
                    this.circleProgressBar.setVisibility(View.VISIBLE);
                    OpenChatAdapter.this.mFileMessageMap.put(message, this.circleProgressBar);
                } else {
                    this.circleProgressBar.setVisibility(View.GONE);
                    OpenChatAdapter.this.mFileMessageMap.remove(message);
//                    if (channel != null) {
//                        int readReceipt3 = channel.getReadReceipt(message);
//                        if (readReceipt3 > 0) {
//                            this.readReceiptText.setVisibility(View.GONE);
//                            this.readReceiptText.setText(String.valueOf(readReceipt3));
//                        } else {
//                            this.readReceiptText.setVisibility(View.GONE);
//                        }
//                    }
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
            this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    longClickListener.onFileMessageItemLongClick(message, position);
                    return true;
                }
            });
        }
    }
    public interface OnItemLongClickListener {
        void onAdminMessageItemLongClick(AdminMessage adminMessage);

        void onFileMessageItemLongClick(FileMessage fileMessage, int i);

        void onUserMessageItemLongClick(UserMessage userMessage, int i);
    }
    private class MyImageFileMessageHolder extends RecyclerView.ViewHolder {
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
        public void bind(Context context, final FileMessage message, OpenChannel channel, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, Uri tempFileMessageUri, final OpenChatAdapter.OnItemClickListener listener, final OpenChatAdapter.OnItemLongClickListener mItemLongClickListener, final int position) {
            this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
            if (isFailedMessage) {
                this.readReceiptText.setText(R.string.message_failed);
                this.readReceiptText.setVisibility(View.VISIBLE);
                this.circleProgressBar.setVisibility(View.GONE);
                OpenChatAdapter.this.mFileMessageMap.remove(message);
            } else if (isTempMessage) {
                this.readReceiptText.setText(R.string.message_sending);
                this.readReceiptText.setVisibility(View.GONE);
                this.circleProgressBar.setVisibility(View.VISIBLE);
                OpenChatAdapter.this.mFileMessageMap.put(message, this.circleProgressBar);
            } else {
                this.circleProgressBar.setVisibility(View.GONE);
                OpenChatAdapter.this.mFileMessageMap.remove(message);
//                if (channel != null) {
//                    int readReceipt = channel.getReadReceipt(message);
//                    if (readReceipt > 0) {
//                        this.readReceiptText.setVisibility(View.GONE);
//                        this.readReceiptText.setText(String.valueOf(readReceipt));
//                    } else {
//                        this.readReceiptText.setVisibility(View.GONE);
//                    }
//                }
//                if (channel != null) {
//                    int readReceipt2 = channel.getReadReceipt(message);
//                    if (readReceipt2 > 0) {
//                        this.status_img.setImageResource(R.drawable.yellowdot);
//                        this.readReceiptText.setVisibility(View.GONE);
//                        this.readReceiptText.setText(String.valueOf(readReceipt2));
//                    } else {
//                        this.readReceiptText.setVisibility(View.GONE);
//                        this.status_img.setImageResource(R.drawable.green_dot);
//                    }
//                }
            }
            if (isNewDay) {
                this.dateText.setVisibility(View.VISIBLE);
                this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                this.dateText.setVisibility(View.GONE);
            }
            if (!isTempMessage || tempFileMessageUri == null) {
                ArrayList<FileMessage.Thumbnail> thumbnails = (ArrayList) message.getThumbnails();
                if (thumbnails.size() > 0) {
                    if (message.getType().toLowerCase().contains("gif")) {
                        ImageUtils.displayGifImageFromUrl(context, message.getUrl(), this.fileThumbnailImage, ((FileMessage.Thumbnail) thumbnails.get(0)).getUrl(), this.fileThumbnailImage.getDrawable());
                    } else {
                        ImageUtils.displayImageFromUrl(context, ((FileMessage.Thumbnail) thumbnails.get(0)).getUrl(), this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
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
                this.itemView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
            if (mItemLongClickListener != null) {
                this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        mItemLongClickListener.onFileMessageItemLongClick(message, position);
                        return true;
                    }
                });
            }
        }
    }

    private class MyUserMessageHolder extends RecyclerView.ViewHolder {
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
        public void bind(final Context context, final UserMessage message, OpenChannel channel, boolean isContinuous, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, OpenChatAdapter.OnItemClickListener clickListener, OpenChatAdapter.OnItemLongClickListener longClickListener, int position) {
            UserMessage userMessage = message;
            OpenChannel OpenChannel = channel;
            final OpenChatAdapter.OnItemLongClickListener onItemLongClickListener = longClickListener;
            if (OpenChannel != null) {
//                int readReceipt = OpenChannel.getReadReceipt(message);
//                if (readReceipt > 0) {
//                    this.status_img.setImageResource(R.drawable.yellowdot);
//                    this.readReceiptText.setVisibility(View.GONE);
//                    this.readReceiptText.setText(String.valueOf(readReceipt));
//                } else {
//                    this.readReceiptText.setVisibility(View.GONE);
//                    this.status_img.setImageResource(R.drawable.green_dot);
//                }
            }
            if (message.getCustomType().contains("Contact")) {
                this.contactlayout.setVisibility(View.VISIBLE);
                this.tvContactName.setText(message.getMessage());
                this.tvPhoneNumber.setText(message.getData());
                this.messageText.setVisibility(View.GONE);
                this.card_group_chat_messagex.setVisibility(View.GONE);
                Context context2 = context;
            } else {
                if (message.getMessage().contains("giphy.com")) {
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
                this.messageText.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (OpenChatAdapter.MyUserMessageHolder.this.messageText.getText().toString().contains("http://maps.google.com/maps?")) {
                            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(message.getMessage())));
                        }
                    }
                });
                if (onItemLongClickListener != null) {
                    final int i = position;
                    this.messageText.setOnLongClickListener(new View.OnLongClickListener() {
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
    private class OtherAudioFileMessageHolder extends RecyclerView.ViewHolder {
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

    private class OtherContactFileMessageHolder extends RecyclerView.ViewHolder {
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

    private class OtherFileMessageHolder extends RecyclerView.ViewHolder {
        /* access modifiers changed from: private */
        public Runnable UpdateSongTime = new Runnable() {
            public void run() {
                OpenChatAdapter.OtherFileMessageHolder.this.startTime = (double) OpenChatAdapter.OtherFileMessageHolder.this.mediaPlayer.getCurrentPosition();
                OpenChatAdapter.OtherFileMessageHolder.this.playtime.setText(String.format("%d min, %d sec", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) OpenChatAdapter.OtherFileMessageHolder.this.startTime)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) OpenChatAdapter.OtherFileMessageHolder.this.startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) OpenChatAdapter.OtherFileMessageHolder.this.startTime)))}));
                OpenChatAdapter.OtherFileMessageHolder.this.seekBar.setProgress((int) OpenChatAdapter.OtherFileMessageHolder.this.startTime);
                OpenChatAdapter.OtherFileMessageHolder.this.myHandler.postDelayed(this, 100);
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
        public void bind(final Context context, final FileMessage message, OpenChannel channel, boolean isNewDay, boolean isContinuous, final OpenChatAdapter.OnItemClickListener listener, final OpenChatAdapter.OnItemLongClickListener longClickListener, final int position) {
            this.fileNameText.setText(message.getName());
            this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
            if (message.getType().contains("audio") || message.getType().contains("application/ogg")) {
                this.layout_group_chat_file_message.setVisibility(View.GONE);
                this.layout_group_chat_file_message.setVisibility(View.GONE);
                this.audiolayout.setVisibility(View.VISIBLE);
                this.profileImage.setVisibility(View.INVISIBLE);
                this.nicknameText.setVisibility(View.INVISIBLE);
                this.playbutton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        OpenChatAdapter.OtherFileMessageHolder.this.mediaPlayer = MediaPlayer.create(context, Uri.parse(message.getUrl()));
                        OpenChatAdapter.OtherFileMessageHolder.this.mediaPlayer.start();
                        OpenChatAdapter.OtherFileMessageHolder.this.finalTime = (double) OpenChatAdapter.OtherFileMessageHolder.this.mediaPlayer.getDuration();
                        OpenChatAdapter.OtherFileMessageHolder.this.startTime = (double) OpenChatAdapter.OtherFileMessageHolder.this.mediaPlayer.getCurrentPosition();
                        if (OpenChatAdapter.OtherFileMessageHolder.this.oneTimeOnly == 0) {
                            OpenChatAdapter.OtherFileMessageHolder.this.seekBar.setMax((int) OpenChatAdapter.OtherFileMessageHolder.this.finalTime);
                            OpenChatAdapter.OtherFileMessageHolder.this.oneTimeOnly = 1;
                        }
                        OpenChatAdapter.OtherFileMessageHolder.this.playtime.setText(String.format("%d min, %d sec", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) OpenChatAdapter.OtherFileMessageHolder.this.startTime)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) OpenChatAdapter.OtherFileMessageHolder.this.startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) OpenChatAdapter.OtherFileMessageHolder.this.startTime)))}));
                        OpenChatAdapter.OtherFileMessageHolder.this.seekBar.setProgress((int) OpenChatAdapter.OtherFileMessageHolder.this.startTime);
                        OpenChatAdapter.OtherFileMessageHolder.this.myHandler.postDelayed(OpenChatAdapter.OtherFileMessageHolder.this.UpdateSongTime, 100);
                        OpenChatAdapter.OtherFileMessageHolder.this.pausebutton.setVisibility(View.VISIBLE);
                        OpenChatAdapter.OtherFileMessageHolder.this.playbutton.setVisibility(View.GONE);
                    }
                });
                this.pausebutton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        OpenChatAdapter.OtherFileMessageHolder.this.mediaPlayer.pause();
                        OpenChatAdapter.OtherFileMessageHolder.this.playbutton.setVisibility(View.VISIBLE);
                        OpenChatAdapter.OtherFileMessageHolder.this.pausebutton.setVisibility(View.GONE);
                    }
                });
                this.audiolayout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        OpenChatAdapter.this.mItemClickListener.onFileMessageItemClick(message);
                    }
                });
            } else {
                this.audiolayout.setVisibility(View.GONE);
                this.layout_group_chat_file_message.setVisibility(View.VISIBLE);
//                if (channel != null) {
//                    int readReceipt = channel.getReadReceipt(message);
//                    if (readReceipt > 0) {
//                        this.readReceiptText.setVisibility(View.GONE);
//                        this.readReceiptText.setText(String.valueOf(readReceipt));
//                    } else {
//                        this.readReceiptText.setVisibility(View.GONE);
//                    }
//                }
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
                this.layout_group_chat_file_message.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
//                        Intent intent = new Intent(context, DocumentActivity.class);
//                        intent.putExtra("docurl_key", message.getUrl());
//                        context.startActivity(intent);
                    }
                });
                this.button_group_chat_file_download.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
            this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    longClickListener.onFileMessageItemLongClick(message, position);
                    return true;
                }
            });
        }
    }

    private class OtherImageFileMessageHolder extends RecyclerView.ViewHolder {
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
        public void bind(Context context, final FileMessage message, OpenChannel channel, boolean isNewDay, boolean isContinuous, final OpenChatAdapter.OnItemClickListener listener, final OpenChatAdapter.OnItemLongClickListener mItemLongClickListener, final int position) {
            this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
            this.profileImage.setMinimumWidth(25);
//            if (channel != null) {
//                int readReceipt = channel.getReadReceipt(message);
//                if (readReceipt > 0) {
//                    this.readReceiptText.setVisibility(View.GONE);
//                    this.readReceiptText.setText(String.valueOf(readReceipt));
//                } else {
//                    this.readReceiptText.setVisibility(View.GONE);
//                }
//            }
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
            ArrayList<FileMessage.Thumbnail> thumbnails = (ArrayList) message.getThumbnails();
            if (thumbnails.size() > 0) {
                if (message.getType().toLowerCase().contains("gif")) {
                    ImageUtils.displayGifImageFromUrl(context, message.getUrl(), this.fileThumbnailImage, ((FileMessage.Thumbnail) thumbnails.get(0)).getUrl(), this.fileThumbnailImage.getDrawable());
                } else {
                    ImageUtils.displayImageFromUrl(context, ((FileMessage.Thumbnail) thumbnails.get(0)).getUrl(), this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
                }
            } else if (message.getType().toLowerCase().contains("gif")) {
                ImageUtils.displayGifImageFromUrl(context, message.getUrl(), this.fileThumbnailImage, (String) null, this.fileThumbnailImage.getDrawable());
            } else {
                ImageUtils.displayImageFromUrl(context, message.getUrl(), this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
            }
            if (listener != null) {
                this.itemView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
            if (mItemLongClickListener != null) {
                this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        mItemLongClickListener.onFileMessageItemLongClick(message, position);
                        return true;
                    }
                });
            }
        }
    }

    private class OtherUserMessageHolder extends RecyclerView.ViewHolder {
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
            Log.d("abcdas","onitemviewiewHolder"+itemView.findViewById(R.id.text_group_chat_message));
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
        public void bind(final Context context, final UserMessage message, OpenChannel channel, boolean isNewDay, boolean isContinuous, OpenChatAdapter.OnItemClickListener clickListener, OpenChatAdapter.OnItemLongClickListener longClickListener, int position) {
            Context context2 = context;
            UserMessage userMessage = message;
            this.profileImage.setMinimumWidth(25);
            OpenChannel OpenChannel = channel;
            final OpenChatAdapter.OnItemLongClickListener onItemLongClickListener = longClickListener;
            if (message.getCustomType().contains("Contact")) {
                this.contactlayout.setVisibility(View.VISIBLE);
                this.tvContactName.setText(message.getMessage());
                this.tvPhoneNumber.setText(message.getData());
                this.messageText.setVisibility(View.GONE);
                this.group_chat_message_container.setVisibility(View.GONE);
                this.card_group_chat_messagex.setVisibility(View.GONE);
                this.contactlayout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent intent = new Intent("android.intent.action.INSERT");
                        intent.setType("vnd.android.cursor.dir/raw_contact");
                        intent.putExtra("name", message.getMessage()).putExtra("phone", message.getData()).putExtra("phone_type", 3);
                        context.startActivity(intent);
                    }
                });
                this.group_chat_message_container.setVisibility(View.GONE);
//                if (OpenChannel != null) {
//                    int readReceipt = OpenChannel.getReadReceipt(message);
//                    if (readReceipt > 0) {
//                        this.readReceiptText.setVisibility(View.GONE);
//                        this.readReceiptText.setText(String.valueOf(readReceipt));
//                    } else {
//                        this.readReceiptText.setVisibility(View.GONE);
//                    }
//                }
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
                if (message.getMessage().contains("giphy.com")) {
                    this.card_group_chat_messagex.setVisibility(View.VISIBLE);
                    Glide.with(context).load(message.getMessage()).into(this.img_gif);
                    this.messageText.setVisibility(View.GONE);
                    this.contactlayout.setVisibility(View.GONE);
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
//                if (OpenChannel != null) {
//                    int readReceipt2 = OpenChannel.getReadReceipt(message);
//                    if (readReceipt2 > 0) {
//                        this.readReceiptText.setVisibility(View.GONE);
//                        this.readReceiptText.setText(String.valueOf(readReceipt2));
//                    } else {
//                        this.readReceiptText.setVisibility(View.GONE);
//                    }
//                }
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
                this.itemView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (OpenChatAdapter.OtherUserMessageHolder.this.messageText.getText().toString().contains("http://maps.google.com/maps?")) {
                            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(message.getMessage())));
                        }
                    }
                });
                if (onItemLongClickListener != null) {
                    final int i = position;
                    this.messageText.setOnLongClickListener(new View.OnLongClickListener() {
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

    private class OtherVideoFileMessageHolder extends RecyclerView.ViewHolder {
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
        public void bind(Context context, final FileMessage message, OpenChannel channel, boolean isNewDay, boolean isContinuous, final OpenChatAdapter.OnItemClickListener listener, final OpenChatAdapter.OnItemLongClickListener mItemLongClickListener, final int position) {
            this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
//            if (channel != null) {
//                int readReceipt = channel.getReadReceipt(message);
//                if (readReceipt > 0) {
//                    this.readReceiptText.setVisibility(View.GONE);
//                    this.readReceiptText.setText(String.valueOf(readReceipt));
//                } else {
//                    this.readReceiptText.setVisibility(View.GONE);
//                }
//            }
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
            ArrayList<FileMessage.Thumbnail> thumbnails = (ArrayList) message.getThumbnails();
            if (thumbnails.size() > 0) {
                ImageUtils.displayImageFromUrl(context, ((FileMessage.Thumbnail) thumbnails.get(0)).getUrl(), this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
            } else {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.isMemoryCacheable();
                Glide.with(context).setDefaultRequestOptions(requestOptions)
                        .load(message.getUrl()).into(this.fileThumbnailImage);
            }

            if (listener != null) {
                this.itemView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
            if (mItemLongClickListener != null) {
                this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        mItemLongClickListener.onFileMessageItemLongClick(message, position);
                        return true;
                    }
                });
            }
        }
    }

    private class MyVideoFileMessageHolder extends RecyclerView.ViewHolder {
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
            this.card_group_chat_message =  itemView.findViewById(R.id.card_group_chat_message);
        }

        /* access modifiers changed from: 0000 */
        public void bind(Context context, final FileMessage message, OpenChannel channel, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, Uri tempFileMessageUri, final OpenChatAdapter.OnItemClickListener listener, final OpenChatAdapter.OnItemLongClickListener mItemLongClickListener, final int position) {
            this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
            this.card_group_chat_message.setVisibility(View.VISIBLE);
            if (isFailedMessage) {
                this.readReceiptText.setText(R.string.message_failed);
                this.readReceiptText.setVisibility(View.VISIBLE);
                this.circleProgressBar.setVisibility(View.GONE);
                OpenChatAdapter.this.mFileMessageMap.remove(message);
            } else if (isTempMessage) {
                this.readReceiptText.setText(R.string.message_sending);
                this.readReceiptText.setVisibility(View.GONE);
                this.circleProgressBar.setVisibility(View.VISIBLE);
                OpenChatAdapter.this.mFileMessageMap.put(message, this.circleProgressBar);
            } else {
                this.circleProgressBar.setVisibility(View.GONE);
                OpenChatAdapter.this.mFileMessageMap.remove(message);
//                if (channel != null) {
//                    int readReceipt = channel.getReadReceipt(message);
//                    if (readReceipt > 0) {
//                        this.readReceiptText.setVisibility(View.VISIBLE);
//                        this.readReceiptText.setText(String.valueOf(readReceipt));
//                    } else {
//                        this.readReceiptText.setVisibility(View.GONE);
//                    }
//                }
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
                    Glide.with(OpenChatAdapter.this.mContext).asBitmap().load(Uri.fromFile(myFile)).into(this.fileThumbnailImage);
                } else if (!isTempMessage || tempFileMessageUri == null) {
                    ArrayList<FileMessage.Thumbnail> thumbnails = (ArrayList) message.getThumbnails();
                    if (thumbnails.size() > 0) {
                        ImageUtils.displayImageFromUrl(context, ((FileMessage.Thumbnail) thumbnails.get(0)).getUrl(), this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
                    }
                } else {
                    ImageUtils.displayImageFromUrl(context, tempFileMessageUri.toString(), this.fileThumbnailImage, null);
                }
            } else if (!isTempMessage || tempFileMessageUri == null) {
                ArrayList<FileMessage.Thumbnail> thumbnails2 = (ArrayList) message.getThumbnails();
                if (thumbnails2.size() > 0) {
                    ImageUtils.displayImageFromUrl(context, ((FileMessage.Thumbnail) thumbnails2.get(0)).getUrl(), this.fileThumbnailImage, this.fileThumbnailImage.getDrawable());
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
                this.itemView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
            if (mItemLongClickListener != null) {
                this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        mItemLongClickListener.onFileMessageItemLongClick(message, position);
                        return true;
                    }
                });
            }
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d("abcdas","onCreateViewHolder"+viewType);
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

    @Override
    public int getItemViewType(int position) {
        Log.d("getItemViewType",""+position);
        BaseMessage message = mMessageList.get(position);
        boolean isMyMessage = false;

        if (message instanceof UserMessage) {
            UserMessage.RequestState requestState = ((UserMessage) message).getRequestState();
            if (requestState == UserMessage.RequestState.PENDING
                    || requestState == UserMessage.RequestState.FAILED
                    || ((UserMessage) message).getSender().getUserId().equals(getMyUserId())) {
                isMyMessage = true;
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

    public int getItemCount() {
        return this.mMessageList.size();
    }

    private synchronized boolean isMessageListLoading() {
        return this.mIsMessageListLoading;
    }

    public synchronized void setMessageListLoading(boolean tf) {
        this.mIsMessageListLoading = tf;
    }

    public void loadLatestMessages(int limit, final BaseChannel.GetMessagesHandler handler) {
        if (!isMessageListLoading()) {
            setMessageListLoading(true);
            this.mChannel.getPreviousMessagesByTimestamp(LongCompanionObject.MAX_VALUE, true, limit, true, BaseChannel.MessageTypeFilter.ALL, null, new BaseChannel.GetMessagesHandler() {
                public void onResult(List<BaseMessage> list, SendBirdException e) {
                    if (handler != null) {
                        handler.onResult(list, e);
                    }
                    OpenChatAdapter.this.setMessageListLoading(false);
                    if (e != null) {
                        e.printStackTrace();
                    } else if (list.size() > 0) {
                        for (BaseMessage message : OpenChatAdapter.this.mMessageList) {
                            if (OpenChatAdapter.this.isTempMessage(message) || OpenChatAdapter.this.isFailedMessage(message)) {
                                list.add(0, message);
                            }
                        }
                        OpenChatAdapter.this.mMessageList.clear();
                        for (BaseMessage message2 : list) {
                            OpenChatAdapter.this.mMessageList.add(message2);
                        }
                        OpenChatAdapter.this.notifyDataSetChanged();
                    }
                }
            });
        }
    }
    public void setChannel(OpenChannel channel) {
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseMessage message = mMessageList.get(position);
        boolean isContinuous = false;
        boolean isNewDay = false;
        boolean isTempMessage = false;
        boolean isFailedMessage = false;
        Uri tempFileMessageUri = null;
        Log.d("onBindViewHolder",""+mMessageList.size());
       // Log.d("onBindViewHolder","-"+mFailedMessageList.size());
        // If there is at least one item preceding the current one, check the previous message.
        if (position < mMessageList.size() - 1) {
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
        Log.d("abcdas","onbindViewHolder"+holder.getItemViewType());

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_USER_MESSAGE_ME:
                ((OpenChatAdapter.MyUserMessageHolder) holder).bind(mContext, (UserMessage) message, mChannel, isContinuous, isNewDay, isTempMessage, isFailedMessage, mItemClickListener, mItemLongClickListener, position);
                break;
            case VIEW_TYPE_USER_MESSAGE_OTHER:
                ((OpenChatAdapter.OtherUserMessageHolder) holder).bind(mContext, (UserMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener, mItemLongClickListener, position);
                break;
            case VIEW_TYPE_ADMIN_MESSAGE:
                ((OpenChatAdapter.AdminMessageHolder) holder).bind(mContext, (AdminMessage) message, mChannel, isNewDay);
                break;


            case VIEW_TYPE_FILE_MESSAGE_ME:
                ((OpenChatAdapter.MyFileMessageHolderFile) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_OTHER:
                ((OpenChatAdapter.OtherFileMessageHolderFile) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener);
                break;


            case VIEW_TYPE_FILE_MESSAGE_IMAGE_ME:
                ((OpenChatAdapter.MyImageFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener,mItemLongClickListener,position);
                break;
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER:
                ((OpenChatAdapter.OtherImageFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener,mItemLongClickListener,position);
                break;
            case VIEW_TYPE_FILE_MESSAGE_VIDEO_ME:
                ((OpenChatAdapter.MyVideoFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener,mItemLongClickListener,position);
                break;
            case VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER:
                ((OpenChatAdapter.OtherVideoFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener,mItemLongClickListener,position);
                break;


            case VIEW_TYPE_FILE_MESSAGE_AUDIO_ME:
                try {
                    ((OpenChatAdapter.MyFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener,mItemLongClickListener,position);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case VIEW_TYPE_FILE_MESSAGE_AUDIO_OTHER:
                ((OpenChatAdapter.OtherFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener,mItemLongClickListener,position);
                break;


            default:
                break;
        }
    }


    interface OnItemClickListener {
        void onUserMessageItemClick(UserMessage message);

        void onFileMessageItemClick(FileMessage message);

        void onAdminMessageItemClick(AdminMessage message);
    }

 
//    private class UserMessageHolder extends RecyclerView.ViewHolder {
//        private final TextView tvContactName;
//        private final TextView tvPhoneNumber;
//        private final LinearLayout contactlayout;
//        private final CardView card_group_chat_messagex;
//        private final LinearLayout group_chat_message_container;
//        private final ViewGroup urlPreviewContainer;
//        TextView nicknameText, messageText, editedText, timeText, dateText;
//        ImageView profileImage, img_gif;
//
//        UserMessageHolder(View itemView) {
//            super(itemView);
//
//            nicknameText = (TextView) itemView.findViewById(R.id.text_open_chat_nickname);
//            messageText = (TextView) itemView.findViewById(R.id.text_open_chat_message);
//            editedText = (TextView) itemView.findViewById(R.id.text_open_chat_edited);
//            timeText = (TextView) itemView.findViewById(R.id.text_open_chat_time);
//            profileImage = (ImageView) itemView.findViewById(R.id.image_open_chat_profile);
//            dateText = (TextView) itemView.findViewById(R.id.text_open_chat_date);
//            this.img_gif = (ImageView) itemView.findViewById(R.id.image_open_chat_file_thumbnail);
//            this.tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
//            this.tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
//            this.contactlayout = (LinearLayout) itemView.findViewById(R.id.contactlayout);
//            this.card_group_chat_messagex = (CardView) itemView.findViewById(R.id.card_group_chat_messagex);
//            this.group_chat_message_container = (LinearLayout) itemView.findViewById(R.id.group_chat_message_container);
//            this.urlPreviewContainer = (ViewGroup) itemView.findViewById(R.id.url_preview_container);
//           //img_gif = (ImageView) itemView.findViewById(R.id.image_open_chat_file_thumbnail);
//        }
//
//        // Binds message details to ViewHolder item
//        void bind(Context context, final UserMessage message, boolean isNewDay,
//                  @Nullable final OnItemClickListener clickListener,
//                  @Nullable final OnItemLongClickListener longClickListener, final int postion) {
//
//            User sender = message.getSender();
//
//            // If current user sent the message, display name in different color
//            if (sender.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
//                nicknameText.setTextColor(ContextCompat.getColor(context, R.color.openChatNicknameMe));
//            } else {
//                nicknameText.setTextColor(ContextCompat.getColor(context, R.color.openChatNicknameOther));
//            }
//
//            // Show the date if the message was sent on a different date than the previous one.
//            if (isNewDay) {
//                dateText.setVisibility(View.VISIBLE);
//                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
//            } else {
//                dateText.setVisibility(View.GONE);
//            }
//
//            nicknameText.setText(message.getSender().getNickname());
//
////            if (message.getMessage().contains("giphy.com")) {
////            //    img_gif.setVisibility(View.VISIBLE);
////              //  Glide.with(context).load(message.getMessage()).into(img_gif);
////                messageText.setVisibility(View.GONE);
////            } else {
////             //   img_gif.setVisibility(View.GONE);
////                messageText.setVisibility(View.VISIBLE);
////                messageText.setText(message.getMessage());
////            }
//
//            if (message.getCustomType().contains("Contact")) {
//                this.contactlayout.setVisibility(View.VISIBLE);
//                this.tvContactName.setText(message.getMessage());
//                this.tvPhoneNumber.setText(message.getData());
//                this.messageText.setVisibility(View.GONE);
//                this.group_chat_message_container.setVisibility(View.GONE);
//                this.card_group_chat_messagex.setVisibility(View.GONE);
//                this.contactlayout.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View view) {
//                        Intent intent = new Intent("android.intent.action.INSERT");
//                        intent.setType("vnd.android.cursor.dir/raw_contact");
//                        intent.putExtra("name", message.getMessage()).putExtra("phone", message.getData()).putExtra("phone_type", 3);
//                        context.startActivity(intent);
//                    }
//                });
//                this.group_chat_message_container.setVisibility(View.GONE);
//
//                if (isNewDay) {
//                    this.dateText.setVisibility(View.VISIBLE);
//                    this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
//                } else {
//                    this.dateText.setVisibility(View.GONE);
//                }
//
//            } else {
//                this.contactlayout.setVisibility(View.GONE);
//                if (message.getMessage().contains("giphy.com")) {
//                    this.card_group_chat_messagex.setVisibility(View.VISIBLE);
//                    Glide.with(context).load(message.getMessage()).into(this.img_gif);
//                    this.messageText.setVisibility(View.GONE);
//                    this.contactlayout.setVisibility(View.GONE);
//                    this.group_chat_message_container.setVisibility(View.VISIBLE);
//                } else {
//                    this.messageText.setVisibility(View.VISIBLE); //visible
//                    this.messageText.setText(message.getMessage());
//                    this.card_group_chat_messagex.setVisibility(View.GONE);
//                    this.group_chat_message_container.setVisibility(View.VISIBLE);
//                }
//                this.timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
//                if (message.getUpdatedAt() > 0) {
//                    this.editedText.setVisibility(View.VISIBLE);
//                } else {
//                    this.editedText.setVisibility(View.GONE);
//                }
//                this.urlPreviewContainer.setVisibility(View.GONE);
//
//                if (isNewDay) {
//                    this.dateText.setVisibility(View.VISIBLE);
//                    this.dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
//                } else {
//                    this.dateText.setVisibility(View.GONE);
//                }
//
//                this.itemView.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View view) {
//                        if (UserMessageHolder.this.messageText.getText().toString().contains("http://maps.google.com/maps?")) {
//                            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(message.getMessage())));
//                        }
//                    }
//                });
//
//            }
//
//            // Get profile image and display it
//            ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), profileImage);
//
//            if (clickListener != null) {
//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        clickListener.onUserMessageItemClick(message);
//                    }
//                });
//            }
//
////            if (longClickListener != null) {
////                itemView.setOnLongClickListener(new View.OnLongClickListener() {
////                    @Override
////                    public boolean onLongClick(View v) {
////                        longClickListener.onBaseMessageLongClick(message, postion);
////                        return true;
////                    }
////                });
////            }
//        }
//    }
    

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
    public void addTempFileMessageInfo(FileMessage message, Uri uri) {
        this.mTempFileMessageUriTable.put(message.getRequestId(), uri);
    }
    public void setItemLongClickListener(OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }
    /*public void markMessageSent(BaseMessage message) {
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
    }*/
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

}
