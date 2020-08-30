package com.example.myapplication.GroupChats;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Activities.AllContacts;
import com.example.myapplication.Activities.Main2Activity;
import com.example.myapplication.Activities.MapsActivity;
import com.example.myapplication.Activities.MediaPlayerActivity;
import com.example.myapplication.Activities.PhotoViewerActivity;
import com.example.myapplication.Activities.SendMessageFromEventActivity;
import com.example.myapplication.Activities.VideoViewActivity;
import com.example.myapplication.Adapter.FileListAdapter;
import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.Intefaces.OnBackPressedListener;
import com.example.myapplication.Intefaces.OnStartFileDownloadListener;
import com.example.myapplication.R;
import com.example.myapplication.Services.CheckOnlineStatusService;
import com.example.myapplication.Utils.ActivityUtils;
import com.example.myapplication.Utils.Common;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.Consts;
import com.example.myapplication.Utils.FileUtils;
import com.example.myapplication.Utils.MimeType;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.PermissionsChecker;
import com.example.myapplication.Utils.PreferenceUtils;
import com.example.myapplication.Utils.SharedPrefManager;
import com.example.myapplication.Utils.TextUtils;
import com.example.myapplication.Utils.UrlPreviewInfo;
import com.example.myapplication.Utils.WebUtils;
import com.google.android.material.snackbar.Snackbar;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.kevalpatel2106.emoticongifkeyboard.EmoticonGIFKeyboardFragment;
import com.kevalpatel2106.emoticongifkeyboard.EmoticonGIFKeyboardFragment.EmoticonConfig;
import com.kevalpatel2106.emoticongifkeyboard.EmoticonGIFKeyboardFragment.GIFConfig;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.Emoticon;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.EmoticonSelectListener;
import com.kevalpatel2106.emoticongifkeyboard.gifs.Gif;
import com.kevalpatel2106.emoticongifkeyboard.gifs.GifSelectListener;
import com.kevalpatel2106.emoticonpack.android8.Android8EmoticonProvider;
import com.kevalpatel2106.gifpack.giphy.GiphyGifProvider;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseChannel.DeleteMessageHandler;
import com.sendbird.android.BaseChannel.GetMessagesHandler;
import com.sendbird.android.BaseChannel.SendFileMessageWithProgressHandler;
import com.sendbird.android.BaseChannel.SendUserMessageHandler;
import com.sendbird.android.BaseChannel.UpdateUserMessageHandler;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.FileMessage.ThumbnailSize;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannel.GroupChannelResetMyHistoryHandler;
import com.sendbird.android.Member;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBird.ChannelHandler;
import com.sendbird.android.SendBird.UserBlockHandler;
import com.sendbird.android.SendBird.UserUnblockHandler;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;
import com.sendbird.android.UserMessageParams;
import com.sendbird.syncmanager.FailedMessageEventActionReason;
import com.sendbird.syncmanager.MessageCollection;
import com.sendbird.syncmanager.MessageEventAction;
import com.sendbird.syncmanager.MessageFilter;
import com.sendbird.syncmanager.handler.CompletionHandler;
import com.sendbird.syncmanager.handler.FetchCompletionHandler;
import com.sendbird.syncmanager.handler.MessageCollectionCreateHandler;
import com.sendbird.syncmanager.handler.MessageCollectionHandler;
import com.stfalcon.multiimageview.MultiImageView;
import com.stfalcon.multiimageview.MultiImageView.Shape;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import br.com.safety.audio_recorder.AudioListener;
import br.com.safety.audio_recorder.AudioRecordButton;
import br.com.safety.audio_recorder.AudioRecording;
import br.com.safety.audio_recorder.RecordingItem;

import static com.example.myapplication.R.drawable.birthday;
import static com.sendbird.android.User.ConnectionStatus.NON_AVAILABLE;
import static com.sendbird.android.User.ConnectionStatus.OFFLINE;
import static com.sendbird.android.User.ConnectionStatus.ONLINE;

public class GroupChatFragment extends Fragment {
    Context conte;
    private static final int AUDIO_PIC_REQUEST = 101;
    private static final int CAMERA_PIC_REQUEST = 100;
    public static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT";
    public static final int CHANNEL_LIST_LIMIT = 30;
    public static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHAT";
    private static final int DOCUMENT_PIC_REQUEST = 11;
    public static final String EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL";
    private static final int FILE_REQUEST_CODE = 1;
    public static final int INTENT_REQUEST_CHOOSE_MEDIA = 301;
    public static final String LOG_TAG = GroupChatFragment.class.getSimpleName();
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    private static final int PLACE_PICKER_REQUEST = 103;
    public static final int REQUEST_TYPE_MENU_UNFRIEND = 201;
    public static final String STATE_CHANNEL_URL = "STATE_CHANNEL_URL";
    public static final int STATE_EDIT = 1;
    public static final int STATE_NORMAL = 0;
    private static final String TAG = "Call Fragment";
    /* access modifiers changed from: private */
    public int REQUEST_CODE_DOC = 404;
    private int REQUEST_CODE_SHARE_CONTACT = 3;
    private int REQUEST_CODE_SHARE_GIF = 4;
    FrameLayout attachment_container;
    /* access modifiers changed from: private */
    public AudioRecording audioRecording;
    AudioRecordButton audio_record_button;
    Button call;
    ImageView chat_backarrow;
    /* access modifiers changed from: private */
    public MultiImageView chat_personimg;
    EditText chatsearchbar;

    final MessageFilter mMessageFilter = new MessageFilter(BaseChannel.MessageTypeFilter.ALL, null, null);
    private MessageCollection mMessageCollection;

    private long mLastRead;


    private PermissionsChecker checker;
    String contactname;
    private int contactrequestcode = 102;
    /* access modifiers changed from: private */
    public Member currentMember;
    // private ArrayList<QBUser> currentOpponentsList;
    // private QBUser currentUser;
    DatabaseHelper databaseHelper;
    // private QbUsersDbManager dbManager;
    private ImageView emoj;
    EmoticonGIFKeyboardFragment emoticonGIFKeyboardFragment;
    private FileListAdapter fileListAdapter;
    private BaseChannel groupChannel;
    /* access modifiers changed from: private */
    private boolean isNewMessageReceived = false;
    public boolean isClicked = false;
    private boolean isRunForCall;
    private ImageView iv_audio;
    private ImageView iv_camera;
    private ImageView iv_chat_more;
    // private ImageView iv_chatcall;
    private ImageView iv_chatsearch;
    private ImageView iv_contact;
    private ImageView iv_document;
    private ImageView iv_gallery;
    private ImageView iv_location;
    private ImageView iv_record;
    private ImageView iv_schedule;
    FrameLayout keyboard_container;
    private AudioRecordButton mAudioRecordButton;
    /* access modifiers changed from: private */
    public GroupChannel mChannel;
    /* access modifiers changed from: private */
    public String mChannelUrl;
    public boolean isNewChannel = false;
    public String strAdminMessage = "";
    /* access modifiers changed from: private */
    public GroupChatAdapter mChatAdapter;
    private View mCurrentEventLayout;
    private TextView mCurrentEventText;
    /* access modifiers changed from: private */
    public int mCurrentState = 0;
    /* access modifiers changed from: private */
    public BaseMessage mEditingMessage = null;
    /* access modifiers changed from: private */
    public HashMap<SendFileMessageWithProgressHandler, FileMessage> mFileProgressHandlerMap;
    /* access modifiers changed from: private */
    public InputMethodManager mIMM;
    /* access modifiers changed from: private */
    public boolean mIsTyping;
    /* access modifiers changed from: private */
    public LinearLayoutManager mLayoutManager;
    /* access modifiers changed from: private */
    public AutoCompleteTextView mMessageEditText;
    /* access modifiers changed from: private */
    public Button mMessageSendButton;
    private PreviousMessageListQuery mPrevMessageListQuery;
    /* access modifiers changed from: private */
    public RecyclerView mRecyclerView;
    private RelativeLayout mRootLayout;
    private RelativeLayout relativeLayouts;
    private ImageButton mUploadFileButton;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    List<Member> members;
    List<BaseMessage> messagelist = new ArrayList<>();
    int more_chatopen = R.menu.more_chatopen;
    //private Menu menuInstance;
    /* access modifiers changed from: private */
    public OnStartFileDownloadListener onStartFileDownloadListener;
    ArrayList<Integer> oppnentlist1;
    // private OpponentsAdapter opponentsAdapter;
    private ListView opponentsListView;
    String phonenum;
    FrameLayout recordcontainer;
    // protected QBResRequestExecutor requestExecutor;
    /* access modifiers changed from: private */
    public FrameLayout searchframe;
    /* access modifiers changed from: private */
    // public SharedPrefsHelper sharedPrefsHelper;
    String[] text = {"Lots of Love", "Laugh out Louder"};
    private TextView tv_chatperson_names;
    TextView tv_groupname;
    ImageView tv_onlinetime;
    String urls;
    /* access modifiers changed from: private */
    public String userId;
    Button video;
    // private WebRtcSessionManager webRtcSessionManager;
    private String member_name;
    private String member_pic_url;
    ImageView tvgif;
    TextView serachfriend;
    private boolean isRunning = false;
    String getgif;

    public void leaveChannel(GroupChannel mChannel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        builder.setTitle("Are you sure want to leave " + mChannel.getName() + " channel?").setPositiveButton((CharSequence) "Leave",
                (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mChannel.leave(new GroupChannel.GroupChannelLeaveHandler() {
                            public void onResult(SendBirdException e) {
                                if (e == null) {
                                    //refresh();
                                }
                                ((Main2Activity) Objects.requireNonNull(getActivity())).onBackPressed();
                            }
                        });
                    }
                }).setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) null).create().show();
    }

    private class filename {
        String ext = "";
        String filename_Without_Ext = "";

        filename(String file) {
            int dotposition = file.lastIndexOf(".");
            this.filename_Without_Ext = file.substring(0, dotposition);
            this.ext = file.substring(dotposition + 1, file.length());
        }

        /* access modifiers changed from: 0000 */
        public String getFilename_Without_Ext() {
            return this.filename_Without_Ext;
        }

        /* access modifiers changed from: 0000 */
        public String getExt() {
            return this.ext;
        }
    }

    @TargetApi(23)
    public static GroupChatFragment newInstance(String channelUrl, boolean isNewChannel, @NonNull String message) {
        GroupChatFragment fragment = new GroupChatFragment();
        Bundle args = new Bundle();
        Log.d("grp", "12" + GroupChannelListFragment.EXTRA_GROUP_CHANNEL_URL + "--" + channelUrl + "--" + args);
        args.putString(GroupChannelListFragment.EXTRA_GROUP_CHANNEL_URL, channelUrl);
        args.putString("EXTRA_NEW_CHANNEL_MESSAGE"/*GroupChannelListFragment.EXTRA_GROUP_CHANNEL_ADMIN_MESSAGE*/, message);
        args.putBoolean("EXTRA_NEW_CHANNEL"/*GroupChannelListFragment.EXTRA_GROUP_IS_NEW_CHANNEL*/, isNewChannel);
        fragment.setArguments(args);
        Log.d("grp", "12" + GroupChannelListFragment.EXTRA_GROUP_CHANNEL_URL + "--" + channelUrl + "--" + args);
        Log.d("grp", "12" + fragment);

        return fragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d("groupchatfragment", "onCreate");

        this.mIMM = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        ((Main2Activity) getActivity()).setOnBackPressedListener(new OnBackPressedListener() {
            public void doBack() {
                try {
                    if (GroupChatFragment.this.attachment_container.getVisibility() == View.VISIBLE) {
                        GroupChatFragment.this.attachment_container.setVisibility(View.GONE);
                    } else if (!GroupChatFragment.this.emoticonGIFKeyboardFragment.isAdded()) {
                        GroupChatFragment.this.getActivity().finish();
                        SendBird.removeAllChannelHandlers();
                    } else if (GroupChatFragment.this.emoticonGIFKeyboardFragment.isOpen()) {
                        GroupChatFragment.this.emoticonGIFKeyboardFragment.close();
                    } else {
                        GroupChatFragment.this.getActivity().finish();
                        SendBird.removeAllChannelHandlers();
                    }
                } catch (Exception e) {
                }
            }
        });
        this.mFileProgressHandlerMap = new HashMap<>();
        if (savedInstanceState != null) {
            this.mChannelUrl = savedInstanceState.getString(STATE_CHANNEL_URL);
        } else {

            this.mChannelUrl = getArguments().getString(GroupChannelListFragment.EXTRA_GROUP_CHANNEL_URL);
            this.isNewChannel = getArguments().getBoolean("EXTRA_NEW_CHANNEL");
            if (isNewChannel) {
                this.strAdminMessage = getArguments().getString("EXTRA_NEW_CHANNEL_MESSAGE");
            }
        }
        this.mChatAdapter = new GroupChatAdapter(getActivity());
        setUpChatListAdapter();
        // this.mChatAdapter.load(this.mChannelUrl);
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);
        initFields();
        setRetainInstance(true);
        Log.d("groupchatfragment", "onCreateView");
        // this.sharedPrefsHelper = SharedPrefsHelper.getInstance();
        this.mRootLayout = (RelativeLayout) rootView.findViewById(R.id.layout_group_chat_root);
        this.mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_group_chat);
        this.mCurrentEventLayout = rootView.findViewById(R.id.layout_group_chat_current_event);
        this.mCurrentEventText = (TextView) rootView.findViewById(R.id.text_group_chat_current_event);
        this.tv_chatperson_names = (TextView) ((Main2Activity) getActivity()).findViewById(R.id.tv_chatperson_names);
        this.tv_groupname = (TextView) ((Main2Activity) getActivity()).findViewById(R.id.tv_groupname);
        this.mMessageEditText = (AutoCompleteTextView) rootView.findViewById(R.id.edittext_group_chat_message);
        this.mMessageSendButton = (Button) rootView.findViewById(R.id.button_group_chat_send);
        this.mUploadFileButton = (ImageButton) rootView.findViewById(R.id.button_group_chat_upload);
        this.attachment_container = (FrameLayout) rootView.findViewById(R.id.attachment_container);
        this.keyboard_container = (FrameLayout) rootView.findViewById(R.id.keyboard_container);
        this.iv_document = (ImageView) rootView.findViewById(R.id.iv_document);
        this.iv_camera = (ImageView) rootView.findViewById(R.id.iv_camera);
        this.iv_gallery = (ImageView) rootView.findViewById(R.id.iv_gallery);
        this.iv_record = (ImageView) rootView.findViewById(R.id.iv_record);
        this.iv_audio = (ImageView) rootView.findViewById(R.id.iv_audio);
        this.iv_contact = (ImageView) rootView.findViewById(R.id.iv_contact);
        this.iv_location = (ImageView) rootView.findViewById(R.id.iv_location);
        this.iv_schedule = (ImageView) rootView.findViewById(R.id.iv_schedule);
        this.iv_chatsearch = (ImageView) rootView.findViewById(R.id.iv_chatsearch);
        // this.iv_chatcall = (ImageView) ((Main2Activity) getActivity()).findViewById(R.id.iv_chatcall);/*rootView.findViewById(R.id.iv_chatcall);*/
        this.chat_backarrow = (ImageView) rootView.findViewById(R.id.chat_backarrow);
        this.tv_onlinetime = (ImageView) ((Main2Activity) getActivity()).findViewById(R.id.tv_onlinetime);
        this.iv_chat_more = (ImageView) rootView.findViewById(R.id.iv_chat_more);
        this.chat_personimg = (MultiImageView) ((Main2Activity) getActivity()).findViewById(R.id.chat_personimg);
        this.recordcontainer = (FrameLayout) rootView.findViewById(R.id.recordcontainer);
        this.audio_record_button = (AudioRecordButton) rootView.findViewById(R.id.audio_record_button);
        this.searchframe = (FrameLayout) rootView.findViewById(R.id.searchframe);
        this.chatsearchbar = (EditText) rootView.findViewById(R.id.searchbar);
        this.tvgif = (ImageView) rootView.findViewById(R.id.tv_gif);
        this.relativeLayouts = rootView.findViewById(R.id.textlayout);
        this.serachfriend = rootView.findViewById(R.id.tv_searchfriend);
        this.chat_personimg.setShape(Shape.CIRCLE);

        Main2Activity activity = (Main2Activity) getActivity();

        getgif = activity.getGiff();
        if (!(getgif == null)) {


            StringBuilder sb = new StringBuilder();
            sb.append("onGifSelected: ");
            sb.append(activity.getGiff());
            Log.d("GUF", sb.toString());
            GroupChatFragment.this.sendUserMessage(activity.getGiff());

//            //   tvgif.setVisibility(View.VISIBLE);
            //   serachfriend.setVisibility(View.VISIBLE);
            //     serachfriend.setText(getgif);
            // tvgif.setImageResource(birthday);
            //       tvgif.setImageResource(Integer.parseInt(getgif));
            //  Glide.with(getContext()).asGif().load(getgif).into(tvgif);
          //  GroupChatFragment.this.sendUserMessage(activity.getGiff());
            //  relativeLayouts.setVisibility(View.GONE);
//            mMessageEditText.setVisibility(View.GONE);
//            mUploadFileButton.setVisibility(View.GONE);
//            emoj.setVisibility(View.GONE);
        }
//            else if (requestCode == REQUEST_CODE_SHARE_GIF) {
//                StringBuilder sb = new StringBuilder();
//                sb.append("onGifSelected: ");
//                sb.append(activity.getGiff());
//                Log.d("GUF", sb.toString());
//                GroupChatFragment.this.sendUserMessage(activity.getGiff());






        String myDataFromActivity = activity.getMyData();
        this.mMessageEditText.setText(myDataFromActivity);
        String anniversary = activity.getAnniversary();
        this.mMessageEditText.setText(anniversary);
        if (mMessageEditText.getText().toString().length() <= 30) {
            mMessageEditText.setText(myDataFromActivity);
        } else {
            mMessageEditText.setText(anniversary);
        }

        if (getArguments() != null) {
            getArguments().getString("latlong_key");
        }
        /*this.iv_chatcall.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                GroupChatFragment.this.startCall(false);

            }
        });*/
        this.iv_record.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                GroupChatFragment.this.recordcontainer.setVisibility(View.VISIBLE);
                GroupChatFragment.this.attachment_container.setVisibility(View.GONE);
            }
        });
        this.iv_chatsearch.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                GroupChatFragment.this.searchframe.setVisibility(View.VISIBLE);
            }
        });
        this.chat_backarrow.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                GroupChatFragment.this.getActivity().finish();
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_dropdown, this.text);
        this.mMessageEditText.setThreshold(1);
        this.mMessageEditText.setAdapter(adapter);
        this.audioRecording = new AudioRecording(getActivity());
        initView();
        ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO", "android.permission.READ_EXTERNAL_STORAGE"}, 0);
        ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
        ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.READ_CONTACTS"}, 0);
        ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 0);


        this.audio_record_button.setOnAudioListener(new AudioListener() {
            public void onStop(RecordingItem recordingItem) {
                GroupChatFragment.this.audioRecording.play(recordingItem);
                GroupChatFragment.this.urls = recordingItem.getFilePath();
                GroupChatFragment.this.recordcontainer.setVisibility(View.GONE);
                GroupChatFragment.this.sendRecordedFile(GroupChatFragment.this.urls);
            }

            public void onCancel() {
                GroupChatFragment.this.recordcontainer.setVisibility(View.GONE);
                Toast.makeText(GroupChatFragment.this.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }

            public void onError(Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Error: ");
                sb.append(e.getMessage());
                Log.d("MainActivity", sb.toString());
            }
        });
        this.iv_document.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
                intent.addCategory("android.intent.category.OPENABLE");
                intent.setType("*/*");
                intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/plain", "application/pdf", "application/zip", "application/apk", "application/txt"});
                GroupChatFragment.this.startActivityForResult(intent, GroupChatFragment.this.REQUEST_CODE_DOC);
                GroupChatFragment.this.attachment_container.setVisibility(View.GONE);
            }
        });
        this.iv_audio.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 101);
//               startActivityForResult(new Intent("android.intent.action.PICK",
//                       Media.EXTERNAL_CONTENT_URI), 101);
                attachment_container.setVisibility(View.GONE);
            }
        });
        this.iv_camera.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                GroupChatFragment.this.attachment_container.setVisibility(View.GONE);
                if (ContextCompat.checkSelfPermission(GroupChatFragment.this.getActivity(), "android.permission.CAMERA") == -1) {
                    ActivityCompat.requestPermissions(GroupChatFragment.this.getActivity(), new String[]{"android.permission.CAMERA"}, 1);
                    return;
                }
                GroupChatFragment.this.startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), 100);
            }
        });
        this.iv_contact.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                GroupChatFragment.this.startActivityForResult(new Intent(GroupChatFragment.this.getActivity(), AllContacts.class), 3);
                GroupChatFragment.this.attachment_container.setVisibility(View.GONE);
            }
        });
        this.iv_location.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                GroupChatFragment.this.startActivityForResult(new Intent(GroupChatFragment.this.getActivity(), MapsActivity.class), 44);
                GroupChatFragment.this.attachment_container.setVisibility(View.GONE);
            }
        });
        this.iv_gallery.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                GroupChatFragment.this.requestMedia();
                GroupChatFragment.this.attachment_container.setVisibility(View.GONE);
            }
        });
        this.mMessageEditText.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    GroupChatFragment.this.emoticonGIFKeyboardFragment.close();
                } catch (Exception e) {
                }
            }
        });
        this.mMessageEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                String s1 = s.toString().trim();
                if (s1.length() <= 0 || s1.equals("")) {
                    GroupChatFragment.this.mMessageSendButton.setEnabled(false);
                } else {
                    GroupChatFragment.this.mMessageSendButton.setEnabled(true);
                }
            }
        });
        this.mMessageSendButton.setEnabled(true); //false
        this.mMessageSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!Common.isConnected(GroupChatFragment.this.getActivity())) {
                    return;
                }
                if (GroupChatFragment.this.mCurrentState == 1) {
                    String userInput = GroupChatFragment.this.mMessageEditText.getText().toString();
                    if (userInput.length() > 0 && GroupChatFragment.this.mEditingMessage != null) {
                        GroupChatFragment.this.editMessage(GroupChatFragment.this.mEditingMessage, userInput);
                    }
                    GroupChatFragment.this.setState(0, null, -1);
                    return;
                }
                String userInput2 = GroupChatFragment.this.mMessageEditText.getText().toString().trim();
                if (userInput2.length() > 0) {
                    GroupChatFragment.this.sendUserMessage(userInput2);
                    GroupChatFragment.this.mMessageEditText.setText("");
                }
//                if(getgif==null){
//                    GroupChatFragment.this.sendUserMessage(getgif);
//
//                }
            }
        });
        this.mUploadFileButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (Common.isConnected(GroupChatFragment.this.getActivity())) {
                    GroupChatFragment.this.isClicked = !GroupChatFragment.this.isClicked;
                    if (GroupChatFragment.this.isClicked) {
                        GroupChatFragment.this.recordcontainer.setVisibility(View.GONE);
                        GroupChatFragment.this.attachment_container.setVisibility(View.VISIBLE);
                        GroupChatFragment.this.keyboard_container.setVisibility(View.GONE);
                        return;
                    }
                    GroupChatFragment.this.attachment_container.setVisibility(View.GONE);
                    GroupChatFragment.this.keyboard_container.setVisibility(View.GONE);
                }
            }
        });
        iv_schedule.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {


                    //     Log.d("schd", "1" + groupChannel.getData());

//                    Log.d("schd", "2" + currentMember.getUserId());
                    Log.d("schd", "3" + mChannel.getMembers().get(1).getUserId());
                    Log.d("schd", "3" + mChannel.getMembers().get(0).getUserId());
                    Log.d("schd", "4" + mChannel.getMemberCount());
                    Log.d("schd", "5" + mChannel.getName());
                    //        Log.d("schd", "4" + currentMember.getProfileUrl());
                    String sendto = "", imgurl = "", name = "";
                    Intent intent;
                    List<Member> member = mChannel.getMembers();
                    if (mChannel.getMemberCount() <= 2) {
                        for (int i = 0; i < member.size(); i++) {
                            if (!member.get(i).getUserId().equals(SharedPrefManager.getInstance(getActivity()).getUser().getUser_id())) {
                                name = member.get(i).getNickname();
                                sendto = member.get(i).getUserId();
                                imgurl = member.get(i).getProfileUrl();
                                Log.d("schd", "6" + name + sendto + imgurl);
                            }
                        }


                    } else {
                        ArrayList<String> send = new ArrayList<String>();
                        ArrayList<String> img = new ArrayList<>();
                        for (int i = 1; i < mChannel.getMemberCount(); i++) {
                            send.add(mChannel.getMembers().get(i).getUserId());
                            img.add(mChannel.getMembers().get(i).getProfileUrl());
                        }
                        sendto = String.join(",", send);
                        imgurl = String.join(",", img);
                        name = mChannel.getName();
                        Log.d("schd", "5" + sendto + "--" + imgurl);

                    }

                    intent = new Intent(getActivity(), SendMessageFromEventActivity.class);
                    intent.putExtra("userName", name);
                    intent.putExtra("month", "");
                    intent.putExtra("date", "");
                    intent.putExtra("send_to_callerid", sendto);
                    intent.putExtra("img_url", imgurl);
                    intent.putExtra("check", "3");

                    getActivity().startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.mIsTyping = false;
        this.mMessageEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!GroupChatFragment.this.mIsTyping) {
                    GroupChatFragment.this.setTypingStatus(true);
                }
                if (s.length() == 0) {
                    GroupChatFragment.this.setTypingStatus(false);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
//        if(!Common.isConnected(getContext())){

//        }
        EmoticonConfig emoticonConfig = new EmoticonConfig().setEmoticonProvider(Android8EmoticonProvider.create()).setEmoticonSelectListener(new EmoticonSelectListener() {
            public void emoticonSelected(Emoticon emoticon) {
                GroupChatFragment.this.mMessageEditText.append(emoticon.getUnicode());
            }

            public void onBackSpace() {
                GroupChatFragment.this.keyboard_container.setVisibility(View.GONE);
            }
        });
        GIFConfig gifConfig = new GIFConfig(GiphyGifProvider.create(getActivity(), "564ce7370bf347f2b7c0e4746593c179")).setGifSelectListener(new GifSelectListener() {
            public void onGifSelected(@NonNull Gif gif) {
                showUploadConfirmDialog(null, gif, REQUEST_CODE_SHARE_GIF);
            }
        });
        this.keyboard_container.setVisibility(View.VISIBLE);
        this.attachment_container.setVisibility(View.GONE);
        this.emoticonGIFKeyboardFragment = EmoticonGIFKeyboardFragment.getNewInstance(rootView.findViewById(R.id.keyboard_container), emoticonConfig, gifConfig);
        this.emoj = (ImageView) rootView.findViewById(R.id.emoji_open_close_btn);
        this.emoj.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (Common.isConnected(GroupChatFragment.this.getActivity())) {
                    GroupChatFragment.this.keyboard_container.setVisibility(View.VISIBLE);
                    GroupChatFragment.this.attachment_container.setVisibility(View.GONE);
                    ((InputMethodManager) GroupChatFragment.this.getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(view.getWindowToken(), 0);
                    GroupChatFragment.this.getFragmentManager().beginTransaction()
                            .replace(R.id.keyboard_container,
                                    GroupChatFragment.this.emoticonGIFKeyboardFragment).commit();
                    GroupChatFragment.this.emoticonGIFKeyboardFragment.open();
                }
            }
        });
        setUpRecyclerView();
        setHasOptionsMenu(true);
        createMessageCollection(mChannelUrl);
        return rootView;
    }

    private void initFields() {
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            this.isRunForCall = extras.getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
        }
    }


    /* access modifiers changed from: private */
//    public boolean isLoggedInChat() {
//        if (QBChatService.getInstance().isLoggedIn()) {
//            return true;
//        }
//        Toast.makeText(getActivity(), R.string.dlg_signal_error, Toast.LENGTH_SHORT).show();
//        tryReLoginToChat();
//        return false;
//    }

//    private void tryReLoginToChat() {
//        if (this.sharedPrefsHelper.hasQbUser()) {
//           // CallService.start(getActivity(), this.sharedPrefsHelper.getQbUser());
//        }
//    }

    /* access modifiers changed from: private */
    public void startCall(boolean isVideoCall) {
        char c;
        try {
            Log.d(TAG, "startCall()");
            this.oppnentlist1 = new ArrayList<>();
            List<Member> member = this.mChannel.getMembers();
            for (int i = 0; i < member.size(); i++) {
                if (!SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString().equals(((Member) member.get(i)).getUserId())) {
                    ((Member) member.get(i)).getProfileUrl();
                    Glide.with(getContext()).load(this.mChannel.getCoverUrl()).into((ImageView) this.chat_personimg);
                    this.oppnentlist1.add(Integer.valueOf(((Member) member.get(i)).getUserId()));
                    if (!((Member) member.get(i)).getUserId().isEmpty() || !((Member) member.get(i)).getProfileUrl().isEmpty()) {
                        long date = System.currentTimeMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
                        SimpleDateFormat sdf1 = new SimpleDateFormat("kk:mm");
                        String format = sdf.format(Long.valueOf(date));
                        String lowerCase = sdf1.format(Long.valueOf(date)).toLowerCase();
                        ((Member) member.get(i)).getFriendName();
                    } else {
                        Toast.makeText(getContext(), "please fill details", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            long date2 = System.currentTimeMillis();
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM");
            SimpleDateFormat sdf12 = new SimpleDateFormat("kk:mm");
            String dateString = sdf2.format(Long.valueOf(date2));
            String time = sdf12.format(Long.valueOf(date2)).toLowerCase();
            int randomInt = new Random(System.nanoTime()).nextInt(1000000000);
            StringBuilder sb_name = new StringBuilder();
            int size = member.size();
            boolean appendSeparator = false;
            int y = 0;
            while (true) {
                c = ',';
                if (y >= size) {
                    break;
                }
                if (appendSeparator) {
                    sb_name.append(',');
                }
                appendSeparator = true;
                sb_name.append(((Member) member.get(y)).getNickname());
                y++;
            }
            StringBuilder sb_id = new StringBuilder();
            boolean appendSeparator1 = false;
            int j = 0;
            while (j < size) {
                if (appendSeparator1) {
                    sb_id.append(c);
                }
                appendSeparator1 = true;
                sb_id.append(((Member) member.get(j)).getUserId());
                j++;
                c = ',';
            }
            // App.getInstance().setSomeVariable(String.valueOf(randomInt));
            if (this.oppnentlist1.size() <= 3) {
                StringBuilder sb_id2 = sb_id;
                DatabaseHelper databaseHelper2 = new DatabaseHelper(getActivity());
                String valueOf = String.valueOf(sb_name);
                String url = this.mChannel.getUrl();
                String valueOf2 = String.valueOf(R.drawable.outgoing_icon);
                StringBuilder sb = new StringBuilder();
                sb.append(dateString);
                List<Member> list = member;
                sb.append(", ");
                sb.append(time);
                String sb2 = sb.toString();
                int size2 = size;
                databaseHelper2.insertdata(randomInt, valueOf, url, valueOf2, sb2, String.valueOf(sb_id2));
                // QBConferenceType conferenceType = isVideoCall ? QBConferenceType.QB_CONFERENCE_TYPE_VIDEO : QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
                String user_id = SharedPrefManager.getInstance(getActivity()).getUser().getUser_id();
                int i2 = size2;
//                QBRTCSession newQbRtcSession = QBRTCClient.getInstance(getActivity()).createNewSessionWithOpponents(this.oppnentlist1, conferenceType);
//                Log.d("callingdetais","2 groupchat");
//                WebRtcSessionManager.getInstance(getActivity()).setCurrentSession(newQbRtcSession);
//                QBRTCSession qBRTCSession = newQbRtcSession;
//                CallActivity.start(getActivity(), false);
                ActivityUtils.startCallActivityAsCaller(getActivity(), oppnentlist1.get(0).toString(),
                        member.get(0).getNickname(), false);

                String str = TAG;
                StringBuilder sb3 = new StringBuilder();
                long j2 = date2;
                sb3.append("conferenceType = ");
                // sb3.append(conferenceType);
                Log.d(str, sb3.toString());
                return;
            }
            long j3 = date2;
            StringBuilder sb4 = sb_id;
            int i3 = size;
            Toast.makeText(getActivity(), "selcted user can't be more than 3", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyAdapter() {
        this.mChatAdapter.notifyDataSetChanged();
    }

//    private void refresh() {
//        if (this.mChannel == null) {
//            GroupChannel.getChannel(this.mChannelUrl, new GroupChannelGetHandler() {
//                public void onResult(GroupChannel groupChannel, SendBirdException e) {
//                    if (e != null) {
//                        e.printStackTrace();
//                        return;
//                    }
//                    GroupChatFragment.this.mChannel = groupChannel;
//                    List<Member> allMember = GroupChatFragment.this.mChannel.getMembers();
//                    if (allMember != null && allMember.size() == 2) {
//                        for (int i = 0; i < allMember.size(); i++) {
//                            try {
//                                if (SharedPrefManager.getInstance(getActivity()).isLoggedIn()) {
//                                    GroupChatFragment.this.userId = String.valueOf(SharedPrefManager.getInstance(getActivity()).getUser().getUser_id());
//                                    GroupChatFragment.this.userId = GroupChatFragment.this.userId.replaceAll("\\s", "");
//                                    String str = SharedPrefManager.getInstance(GroupChatFragment.this.getActivity()).getUser().getUser_name().toString();
//                                    if (!GroupChatFragment.this.userId.equalsIgnoreCase(((Member) allMember.get(i)).getUserId())) {
//                                        GroupChatFragment.this.currentMember = (Member) allMember.get(i);
//                                    }
//                                }
//                            } catch (Exception ex) {
//                                Common.showToast(GroupChatFragment.this.getActivity(), "No internet connection/ No chats");
//                                ex.printStackTrace();
//                            }
//                        }
//                    }
//                    if (GroupChatFragment.this.currentMember == null || !GroupChatFragment.this.currentMember.isBlockedByMe()) {
//                        GroupChatFragment.this.more_chatopen = R.menu.more_chatopen ;
//                        GroupChatFragment.this.getActivity().invalidateOptionsMenu();
//                    } else {
//                        GroupChatFragment.this.more_chatopen = R.menu.more_chatopen_blocked;
//                        GroupChatFragment.this.getActivity().invalidateOptionsMenu();
//                    }
//                    GroupChatFragment.this.mChatAdapter.setChannel(GroupChatFragment.this.mChannel);
//                    GroupChatFragment.this.mChatAdapter.loadLatestMessages(30, new GetMessagesHandler() {
//                        public void onResult(List<BaseMessage> list, SendBirdException e) {
//                            GroupChatFragment.this.messagelist = list;
//                            GroupChatFragment.this.mChatAdapter.markAllMessagesAsRead();
//                            GroupChatFragment.this.mChatAdapter.notifyDataSetChanged();
//                        }
//                    });
//                    Log.d("Lifecycle","Group Chat fragment"+mChannel.getMembers().get(0).getNickname());
//                    try {
//                        if (GroupChatFragment.this.mChannel.getMemberCount() > 2) {
//                            tv_groupname.setText(mChannel.getName());
//
//                            if (GroupChatFragment.this.mChannel.getCoverUrl() != null) {
//                                Glide.with(GroupChatFragment.this.getContext()).load(GroupChatFragment.this.mChannel.getCoverUrl()).into((ImageView) GroupChatFragment.this.chat_personimg);
//                            }
//                        } else  {
//                            Log.d("Lifecycle",""+mChannel.getMembers().get(1).getNickname());
//                            List<Member> member = GroupChatFragment.this.mChannel.getMembers();
//                            RequestOptions requestOptions = new RequestOptions();
//                            requestOptions.error(R.drawable.buzzplaceholder);
//                            for(int i=0;i<member.size();i++){
//                                if(!member.get(i).getUserId().equals(SharedPrefManager.getInstance(getActivity()).getUser().getCaller_id())){
//                                    member_name=member.get(i).getNickname();
//                                    member_pic_url=member.get(i).getProfileUrl();
//                                }
//                            }
//                            if(mChannel.getName().equals("NO") && mChannel.getCoverUrl().equals("NO")) {
//                                Glide.with(GroupChatFragment.this.getContext()).setDefaultRequestOptions(requestOptions)
//                                        .load(member_pic_url)
//                                        .into((ImageView) chat_personimg);
//                                tv_groupname.setText(member_name);
//                            }else if(!mChannel.getName().equals("NO") && mChannel.getCoverUrl().equals("NO")){
//                                Glide.with(GroupChatFragment.this.getContext()).setDefaultRequestOptions(requestOptions)
//                                        .load(member_pic_url)
//                                        .into((ImageView) chat_personimg);
//                                tv_groupname.setText(mChannel.getName());
//                            }
//                            else if(mChannel.getName().equals("NO") && !mChannel.getCoverUrl().equals("NO")){
//                                Glide.with(GroupChatFragment.this.getContext()).setDefaultRequestOptions(requestOptions)
//                                        .load(mChannel.getCoverUrl())
//                                        .into((ImageView)chat_personimg);
//                                tv_groupname.setText(member_name);
//                            }
//                            else{
//                                tv_groupname.setText(mChannel.getName());
//                                Glide.with(GroupChatFragment.this.getContext())
//                                        .setDefaultRequestOptions(requestOptions)
//                                        .load(mChannel.getCoverUrl().trim()).into(chat_personimg);
//                            }
//
//                        }
//
//                        // GroupChatFragment.this.chat_personimg.setImageResource(R.drawable.ic_logo_pink);
//                    } catch (Exception e2) {
//                    }
//                    GroupChatFragment.this.tv_groupname.setOnClickListener(new OnClickListener() {
//                        public void onClick(View view) {
//                            Log.d("tv_groupname",""+mChannel.getMemberCount());
//                            if (GroupChatFragment.this.mChannel.getMembers().size() > 2) {
//                                Intent intent = new Intent(GroupChatFragment.this.getActivity(), GroupParticipantActivity.class);
//                                intent.putExtra("members_key", GroupChatFragment.this.mChannelUrl);
//                                intent.putExtra("groupname_key", GroupChatFragment.this.mChannel.getName());
//                                GroupChatFragment.this.startActivity(intent);
//                            }
//                        }
//                    });
//                    chat_personimg.setOnClickListener(new OnClickListener() {
//                        public void onClick(View view) {
//                            if (GroupChatFragment.this.mChannel.getMembers().size() > 2) {
//                                Intent intent = new Intent(GroupChatFragment.this.getActivity(), GroupParticipantActivity.class);
//                                intent.putExtra("members_key", GroupChatFragment.this.mChannelUrl);
//                                intent.putExtra("groupname_key", GroupChatFragment.this.mChannel.getName());
//                                GroupChatFragment.this.startActivity(intent);
//                            }
//                        }
//                    });
//                    // GroupChatFragment.this.updateActionBarTitle();
//                }
//            });
//        }
//
//    }

    public void onResume() {
        super.onResume();
        mChatAdapter.setContext(getActivity());
        if (mMessageCollection != null) {
            mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.NEXT, null);

        }

        Log.d("groupchatfragment", "onResume1");
        SendBird.addConnectionHandler(CONNECTION_HANDLER_ID, new SendBird.ConnectionHandler() {

            @Override
            public void onReconnectStarted() {
                Log.d("groupchatfragment", "onReconnectStarted");
            }

            @Override
            public void onReconnectSucceeded() {
                Log.d("groupchatfragment", "onReconnectSucceeded" + mLayoutManager.findFirstVisibleItemPosition());
                if (mMessageCollection != null) {
                    if (mLayoutManager.findFirstVisibleItemPosition() <= 0) {
                        mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.NEXT, null);
                    }
                    Log.d("groupchatfragment", "onReconnectSucceeded" + mChatAdapter.getItemCount());

                    if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                        mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.PREVIOUS, new FetchCompletionHandler() {
                            @Override
                            public void onCompleted(boolean hasMore, SendBirdException e) {
                                Log.d("groupchatfragment", "onReconnectSucceeded onCompleted --" + hasMore);
                            }
                        });
                    }
                }
            }

            @Override
            public void onReconnectFailed() {
                Log.d("groupchatfragment", "onReconnectFailed");

            }
        });
        Log.d("groupchatfragment", "onResume2");
        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new ChannelHandler() {
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                Log.d("groupchatfragment", "onMessageReceived");
                if (baseChannel.getUrl().equals(GroupChatFragment.this.mChannelUrl)) {
                    isNewMessageReceived = true;
                    GroupChatFragment.this.mChatAdapter.markAllMessagesAsRead();
                    GroupChatFragment.this.mChatAdapter.addFirst(baseMessage);
                }
            }

            public void onMessageDeleted(BaseChannel baseChannel, long msgId) {
                super.onMessageDeleted(baseChannel, msgId);
                Log.d("groupchatfragment", "onMessageDeleted");
                if (baseChannel.getUrl().equals(GroupChatFragment.this.mChannelUrl)) {
                    GroupChatFragment.this.mChatAdapter.delete(msgId);
                }
            }

            public void onMessageUpdated(BaseChannel channel, BaseMessage message) {
                Log.d("groupchatfragment", "onMessageUpdated");
                super.onMessageUpdated(channel, message);
                if (channel.getUrl().equals(GroupChatFragment.this.mChannelUrl)) {
                    GroupChatFragment.this.mChatAdapter.update(message);
                }
            }

            public void onReadReceiptUpdated(GroupChannel channel) {
                Log.d("groupchatfragment", "onReadReceiptUpdated");
                if (channel.getUrl().equals(GroupChatFragment.this.mChannelUrl)) {
                    GroupChatFragment.this.mChatAdapter.notifyDataSetChanged();
                    if (isNewMessageReceived) {
                        GroupChatFragment.this.mRecyclerView.scrollToPosition(0);
                        isNewMessageReceived = false;
                    }
                }
            }

            public void onTypingStatusUpdated(GroupChannel channel) {
                Log.d("groupchatfragment", "onTypingStatusUpdated");
                if (channel.getUrl().equals(GroupChatFragment.this.mChannelUrl)) {
                    GroupChatFragment.this.displayTyping(channel.getTypingMembers());
                }
            }
        });
        Log.d("groupchatfragment", "onResume3");
    }

    public void onDestroy() {
        Log.d("groupchatfragment", "onDestroy");
        // this.mChatAdapter.save(this.mChannelUrl);
        if (mMessageCollection != null) {
            Log.d("groupchatfragment", "onDestroy1");
            mMessageCollection.setCollectionHandler(null);
            mMessageCollection.remove();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Log.d("groupchatfragment", "onPause");
        super.onPause();
        // SendBird.removeConnectionHandler(CONNECTION_HANDLER_ID);
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
    }


    private Uri getCaptureImageOutputUri() {
        File getImage = getActivity().getExternalCacheDir();
        if (getImage != null) {
            return Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("datain", resultCode + " <data is !> " + requestCode);
        SendBird.setAutoBackgroundDetection(true);
        if (requestCode == 301 && resultCode == -1) {
            if (data == null) {
                Log.d(LOG_TAG, "data is null!");
                return;
            }
            showUploadConfirmDialog(data.getData());
        } else if (requestCode == 100 && resultCode == -1) {
            if (data == null) {
                Log.d(LOG_TAG, "data is null!");
            } else {
                showUploadConfirmDialog(getImageUri(getActivity(), (Bitmap) data.getExtras().get("data")));
            }
        } else if (requestCode == this.REQUEST_CODE_DOC && resultCode == -1) {
            if (data == null) {
                Log.d(LOG_TAG, "data is null!");
                return;
            }
            Log.d("DOC DATA", data + "" + data.getData());
            showUploadConfirmDialog(data.getData());
        } else if (requestCode == 101 && resultCode == -1) {
            if (data == null) {
                Log.d(LOG_TAG, "data is null!");
                return;
            }
            showUploadConfirmDialog(data.getData());
        } else if (requestCode == 3 && resultCode == -1) {
            if (data == null) {
                Log.d(LOG_TAG, "data is null!");
                return;
            }
            showUploadConfirmDialog(data, null, REQUEST_CODE_SHARE_CONTACT);

        } else if (requestCode == 44 && resultCode == -1) {
            if (data == null) {
                Log.d(LOG_TAG, "data is null!");
                return;
            }
            sendUserMessage(data.getStringExtra("latlong_key"));
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        inImage.compress(CompressFormat.JPEG, 100, new ByteArrayOutputStream());
        return Uri.parse(Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null));
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getActivity().getContentResolver() == null) {
            return path;
        }
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return path;
        }
        cursor.moveToFirst();
        String path2 = cursor.getString(cursor.getColumnIndex("_data"));
        cursor.close();
        return path2;
    }

    private void setUpRecyclerView() {
        this.mLayoutManager = new LinearLayoutManager(getActivity());
        this.mLayoutManager.setReverseLayout(true);
        //this.mLayoutManager.setStackFromEnd(true);
        this.mRecyclerView.setLayoutManager(this.mLayoutManager);
        this.mRecyclerView.setAdapter(this.mChatAdapter);
        this.mRecyclerView.addOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                Log.d("groupchatfragment", "mRecyclerView" +
                        newState + "-" + mLayoutManager.findFirstVisibleItemPosition());

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mLayoutManager.findFirstVisibleItemPosition() <= 0) {
                        mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.NEXT, null);

                    }

                    if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                        mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.PREVIOUS, null);
                    }

                }
            }
        });
    }


    private void setUpChatListAdapter() {
        mChatAdapter.setItemClickListener(new GroupChatAdapter.OnItemClickListener() {
            @Override
            public void onUserMessageItemClick(UserMessage message) {
                Log.d("lifecycle", "onUserMessageItemClick" + message);
                if (mChatAdapter.isFailedMessage(message) && !mChatAdapter.isResendingMessage(message)) {
                    retryFailedMessage(message);
                    return;
                }

                // Message is sending. Do nothing on click event.
                if (mChatAdapter.isTempMessage(message)) {
                    return;
                }

                if (message.getCustomType().equals(GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE)) {
                    try {
                        UrlPreviewInfo info = new UrlPreviewInfo(message.getData());
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getUrl()));
                        startActivity(browserIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFileMessageItemClick(FileMessage message) {
                // Load media chooser and removeSucceededMessages the failed message from list.
                Log.d("lifecycle", "onFileMessageItemClick" + message);
                if (mChatAdapter.isFailedMessage(message)) {
                    retryFailedMessage(message);
                    return;
                }

                // Message is sending. Do nothing on click event.
                if (mChatAdapter.isTempMessage(message)) {
                    return;
                }


                //onFileMessageClicked(message);
                onFileMessageClickedNew(message);
            }
        });

        mChatAdapter.setItemLongClickListener(new GroupChatAdapter.OnItemLongClickListener() {
            @Override
            public void onUserMessageItemLongClick(UserMessage message, int position) {
                if (message.getSender().getUserId().equals(PreferenceUtils.getUserId())) {
                    showMessageOptionsDialog(message, position);
                }
            }


            @Override
            public void onAdminMessageItemLongClick(AdminMessage message) {
            }

            @Override
            public void onFileMessageItemLongClick(FileMessage fileMessage, int i) {

            }
        });
    }

    private void onFileMessageClickedNew(FileMessage message) {
        /*if(type.contains("pdf")){
           // File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/example.pdf");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(message.getUrl()), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }*/
        try {
            String url = message.getUrl();
            String type = message.getType().toLowerCase();
            Uri uri = Uri.parse(message.getUrl());

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                //intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"/*"application/vnd.ms-excel"*/);
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip")) {
                // ZIP file
                intent.setDataAndType(uri, "application/zip");
            } else if (url.toString().contains(".rar")){
                // RAR file
                intent.setDataAndType(uri, "application/x-rar-compressed");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                    url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }

    /* access modifiers changed from: private */
    public void showMessageOptionsDialog(final BaseMessage message, final int position) {
        String[] options = {"Edit message", "Delete message"};
        Builder builder = new Builder(getActivity());
        builder.setItems((CharSequence[]) options, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    GroupChatFragment.this.setState(1, message, position);
                } else if (which == 1) {
                    GroupChatFragment.this.deleteMessage(message);
                }
            }
        });
        builder.create().show();
    }

    /* access modifiers changed from: private */
    public void setState(int state, BaseMessage editingMessage, final int position) {
        switch (state) {
            case 0:
                this.mCurrentState = 0;
                this.mEditingMessage = null;
                this.mUploadFileButton.setVisibility(View.VISIBLE);
                this.mMessageSendButton.setVisibility(View.VISIBLE);
                this.mMessageEditText.setText("");
                return;
            case 1:
                this.mCurrentState = 1;
                this.mEditingMessage = editingMessage;
                this.mUploadFileButton.setVisibility(View.GONE);
                this.mMessageSendButton.setVisibility(View.VISIBLE);
                String messageString = ((UserMessage) editingMessage).getMessage();
                if (messageString == null) {
                    messageString = "";
                }
                this.mMessageEditText.setText(messageString);
                if (messageString.length() > 0) {
                    this.mMessageEditText.setSelection(0, messageString.length());
                }
                this.mMessageEditText.requestFocus();
                this.mMessageEditText.postDelayed(new Runnable() {
                    public void run() {
                        GroupChatFragment.this.mIMM.showSoftInput(GroupChatFragment.this.mMessageEditText, 0);
                        GroupChatFragment.this.mRecyclerView.postDelayed(new Runnable() {
                            public void run() {
                                GroupChatFragment.this.mRecyclerView.scrollToPosition(position);
                            }
                        }, 500);
                    }
                }, 100);
                return;
            default:
                return;
        }
    }

    public void onAttach(Context context) {
        Log.d("groupchatfragment", "onAttach");
        super.onAttach(context);
        this.onStartFileDownloadListener = (OnStartFileDownloadListener) context;
    }

    /* access modifiers changed from: private */
//    public void retryFailedMessage(final BaseMessage message) {
//        new Builder(getActivity()).setMessage((CharSequence) "Retry?").setPositiveButton((int) R.string.resend_message, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == -1) {
//                    if (message instanceof UserMessage) {
//                        GroupChatFragment.this.sendUserMessage(((UserMessage) message).getMessage());
//                    } else if (message instanceof FileMessage) {
//                        GroupChatFragment.this.sendFileWithThumbnail(GroupChatFragment.this.mChatAdapter.getTempFileMessageUri(message));
//                    }
//                    GroupChatFragment.this.mChatAdapter.removeFailedMessage(message);
//                }
//            }
//        }).setNegativeButton((int) R.string.delete_message, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == -2) {
//                    GroupChatFragment.this.mChatAdapter.removeFailedMessage(message);
//                }
//            }
//        }).show();
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("groupchatfragment", "onSaveInstanceState");
        outState.putString(STATE_CHANNEL_URL, mChannelUrl);

        super.onSaveInstanceState(outState);
    }

    private void retryFailedMessage(final BaseMessage message) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Retry")
                .setPositiveButton(R.string.resend_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            if (message instanceof UserMessage) {
                                mChannel.resendUserMessage((UserMessage) message, new BaseChannel.ResendUserMessageHandler() {
                                    @Override
                                    public void onSent(UserMessage userMessage, SendBirdException e) {
                                        mMessageCollection.handleSendMessageResponse(userMessage, e);
                                    }
                                });
                            } else if (message instanceof FileMessage) {
                                Uri uri = mChatAdapter.getTempFileMessageUri(message);
                                sendFileWithThumbnail(uri);
                                mChatAdapter.removeFailedMessages(Collections.singletonList(message));
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.delete_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            if (message instanceof UserMessage) {
                                mMessageCollection.deleteMessage(message);
                            } else if (message instanceof FileMessage) {
                                mChatAdapter.removeFailedMessages(Collections.singletonList(message));
                            }
                        }
                    }
                }).show();
    }

    /* access modifiers changed from: private */
    public void displayTyping(List<Member> typingUsers) {
        String string;
        if (typingUsers.size() > 0) {
            this.mCurrentEventLayout.setVisibility(View.VISIBLE);
            if (typingUsers.size() == 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(((Member) typingUsers.get(0)).getNickname());
                sb.append(" is typing");
                string = sb.toString();
            } else if (typingUsers.size() == 2) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(((Member) typingUsers.get(0)).getNickname());
                sb2.append(" ");
                sb2.append(((Member) typingUsers.get(1)).getNickname());
                sb2.append(" is typing");
                string = sb2.toString();
            } else {
                string = "Multiple users are typing";
            }
            this.mCurrentEventText.setText(string);
            return;
        }
        this.mCurrentEventLayout.setVisibility(View.GONE);
    }

    /* access modifiers changed from: private */
    public void requestMedia() {
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            requestStoragePermissions();
            return;
        }
        Intent intent = new Intent();
        if (VERSION.SDK_INT >= 19) {
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{MimeType.IMAGE_MIME, "video/*"});
        } else {
            intent.setType("image/* video/*");
        }
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Media"), 301);
        SendBird.setAutoBackgroundDetection(false);
    }

    private void requestdocument() {
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            requestStoragePermissions();
            return;
        }
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
        if (VERSION.SDK_INT >= 19) {
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/plain", "application/pdf", "application/zip", "application/.docs"});
        } else {
            intent.setType("*/*");
        }
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Media"), 11);
        SendBird.setAutoBackgroundDetection(false);
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE")) {
            Snackbar.make((View) this.mRootLayout, (CharSequence) "Storage access permissions are required to upload/download files.", Snackbar.LENGTH_LONG).setAction((CharSequence) "Okay", (OnClickListener) new OnClickListener() {
                public void onClick(View view) {
                    GroupChatFragment.this.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 13);
                }
            }).show();
            return;
        }
        requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 13);
    }


    private void onFileMessageClicked(FileMessage message) {
        String type = message.getType().toLowerCase();
//        if (type.startsWith("image")) {
//            Intent i = new Intent(getActivity(), PhotoViewerActivity.class);
//            i.putExtra("url", message.getUrl());
//            i.putExtra("type", message.getType());
//            startActivity(i);
//        } else if (type.startsWith("video")) {
//            Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
//            intent.putExtra("url", message.getUrl());
//            startActivity(intent);
//        } else {

        if (checkForFileToDownload(message)) {
            if (message.getType().startsWith("image")) {
                Intent i = new Intent(getActivity(), PhotoViewerActivity.class);
                i.putExtra("url", message.getUrl());
                i.putExtra("type", message.getType());
                startActivity(i);
            } else if (message.getType().startsWith("video")) {
                //Intent intent = new Intent(this, VideoViewActivity.class);
                Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
                // intent.putExtra("url", message.getUrl());
                StringBuilder sb5 = new StringBuilder();
                sb5.append(Environment.getExternalStorageDirectory());
                sb5.append("/BuzzApp/Videos/");
                sb5.append(message.getName());
                intent.putExtra("url", sb5.toString());
                startActivity(intent);
            }
        } else {
            if (message.getType().startsWith("image")) {
                Intent i = new Intent(getActivity(), PhotoViewerActivity.class);
                i.putExtra("url", message.getUrl());
                i.putExtra("type", message.getType());
                startActivity(i);
            } else if (message.getType().startsWith("video")) {
                Intent intent = new Intent(getActivity(), VideoViewActivity.class);
                // Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
                // intent.putExtra("url", message.getUrl());
                intent.putExtra("path", message.getUrl());
                intent.putExtra("type", message.getType());
               /* StringBuilder sb5 = new StringBuilder();
                sb5.append(Environment.getExternalStorageDirectory());
                sb5.append("/BuzzApp/Videos/");
                sb5.append(message.getName());
                intent.putExtra("url", sb5.toString());*/
                startActivity(intent);
            }
        }/*else {
            showDownloadConfirmDialog(message);
        }*/
//        }
    }

    private boolean checkForFileToDownload(FileMessage fileMessage) {
        try {
            String filename = fileMessage.getName();
            if (fileMessage.getType().toLowerCase().startsWith("image")) {
                if (checkToDownload(filename, "/BuzzApp/Images")) {
                    return true;
                }
                return false;
            } else if (fileMessage.getType().toLowerCase().startsWith("video")) {
                if (checkToDownload(filename, "/BuzzApp/Videos")) {
                    return true;
                }
                return false;
            } else if (fileMessage.getType().toLowerCase().startsWith("audio")) {
                if (!checkToDownload(filename, "/BuzzApp/Audio")) {
                    return false;
                }
                Toast.makeText(getActivity(), "This audio is already downloaded.", Toast.LENGTH_SHORT).show();
                return true;
            } else if (fileMessage.getType().toLowerCase().startsWith("application/")) {
                if (!checkToDownload(filename, "/BuzzApp/Documents")) {
                    return false;
                }
                Toast.makeText(getActivity(), "This doc is already downloaded.", Toast.LENGTH_SHORT).show();
                return true;
            } else if (!checkToDownload(filename, "/BuzzApp")) {
                return false;
            } else {
                Toast.makeText(getActivity(), "This file is already downloaded.", Toast.LENGTH_SHORT).show();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkToDownload(String filename, String directory) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory());
            sb.append(directory);
            sb.append("/");
            sb.append(filename);
            if (new File(sb.toString()).exists()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void showDownloadConfirmDialog(final FileMessage message) {
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            requestStoragePermissions();
        } else {
            new Builder(getActivity()).setMessage((CharSequence) "Download file?").setPositiveButton((int) R.string.download, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == -1) {
                        GroupChatFragment.this.onStartFileDownloadListener.onStartFileDownload(message);
                    }
                }
            }).setNegativeButton((int) R.string.cancel, (DialogInterface.OnClickListener) null).show();
        }
    }

    /* access modifiers changed from: private */
    public void updateActionBarTitle() {

        String str = "";
        List<Member> member = new ArrayList<>();
        try {
            if (this.mChannel != null) {
                member = this.mChannel.getMembers();
                Main2Activity.setmChannel(mChannel);
                String title = TextUtils.getGroupChannelTitle(this.mChannel, getActivity());

                if (member.size() <= 2) {
                    String strUserID = "";// Should have userID of another member.
                    isRunning = true;
                    if (!member.get(0).getUserId().equals(SharedPrefManager.getInstance(
                            getActivity()).getUser().getUser_id())) {
                        strUserID = member.get(0).getUserId();
                    } else {
                        strUserID = member.get(1).getUserId();
                    }
                    Main2Activity.CheckServiceStatusIntent = new Intent(getActivity(),
                            CheckOnlineStatusService.class);
                    Main2Activity.CheckServiceStatusIntent.putExtra("user_id", strUserID);
                    Objects.requireNonNull(getActivity()).startService(Main2Activity.CheckServiceStatusIntent);


                    //Hide edit text and input options if user is blocked.
                    String strLoggedInUserId = String.valueOf(SharedPrefManager.getInstance(getActivity()));
                    for(Member memberInfo : member){
                        try {
                            if (!memberInfo.getUserId().equals(strLoggedInUserId)) {
                                if(memberInfo.isBlockingMe()){
                                    relativeLayouts.setVisibility(View.GONE);
                                } else {
                                    relativeLayouts.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            Common.showToast(getActivity(), "Failed!");
                            e.printStackTrace();
                        }
                    }

                    //initOnlineStatusService(member.get(0).getUserId());
                } else {
                    tv_onlinetime.setVisibility(View.GONE);
                   /* if (isNewChannel) {
                        sendAdminMessage(strAdminMessage);
                        isNewChannel = false;
                        strAdminMessage = "";
                    }*/
                }

                if (getActivity() != null) {
                    for (int i = 0; i < member.size(); i++) {

                        if (((Member) member.get(i)).getUserId() == null || ((Member) member.get(i)).getProfileUrl() == null) {
                            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                        } else {

                            Log.d("Lifecycle", "Group Chat fragment-" + mChannel.getMembers().get(0).getNickname());
                            Log.d("Lifecycle", "Group Chat fragment1-" + mChannel.getMembers().get(1).getNickname());

                            if (member.size() > 2) {

                                this.tv_groupname.setText(mChannel.getName()); // mchannel.getname
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.placeholder(R.drawable.app_buzz_logo);
                                requestOptions.error(R.drawable.app_buzz_logo);
                                //       Glide.with(getContext()).load(this.mChannel.getCoverUrl()).into((ImageView) this.chat_personimg);

                                Glide.with(getActivity())  //comment
                                        .setDefaultRequestOptions(requestOptions)
                                        .load(this.mChannel.getCoverUrl().trim()).into(chat_personimg); //comment

                            } else {
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.placeholder(R.drawable.app_buzz_logo);
                                requestOptions.error(R.drawable.app_buzz_logo);

                                for (int j = 0; j < member.size(); j++) {
                                    if (!member.get(j).getUserId().equals(SharedPrefManager.getInstance(getActivity()).getUser().getUser_id())) { //callerid
                                        member_name = member.get(j).getNickname();
                                        member_pic_url = member.get(j).getProfileUrl();

                                        member_name = TextUtils.getGroupChannelTitle(this.mChannel, getActivity());
                                        // for (int j = 0; j < member.size(); j++) {
                                        if (!member.get(j).getUserId().equals(SharedPrefManager.getInstance(getActivity()).getUser().getCaller_id())) {
                                            //  member_name=member.get(j).getNickname();
                                            //   member_pic_url = member.get(j).getProfileUrl();
                                        }
                                    }
                                    if (mChannel.getName().equals("NO") && mChannel.getCoverUrl().equals("NO")) {

                                        if (!SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString().equals(((Member) member.get(i)).getUserId())) {
                                            ((Member) member.get(i)).getProfileUrl();
                                            //               Glide.with(getContext()).load(this.mChannel.getCoverUrl()).into((ImageView) this.chat_personimg);
//                                    }
                                            Glide.with(GroupChatFragment.this.getContext()).setDefaultRequestOptions(requestOptions)
                                                    .load(member_pic_url)
                                                    .into((ImageView) chat_personimg); //comment
                                            tv_groupname.setText(member_name);
                                        }
                                    } else if (!mChannel.getName().equals("NO") && mChannel.getCoverUrl().equals("NO")) {
                                        Glide.with(GroupChatFragment.this.getContext()).setDefaultRequestOptions(requestOptions)
                                                .load(member_pic_url)
                                                .into((ImageView) chat_personimg); //comment
                                        tv_groupname.setText(member_name);


//                                    if (!SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString().equals(((Member) member.get(i)).getUserId())) {
//                                        ((Member) member.get(i)).getProfileUrl();
//                                        Glide.with(getContext()).load(this.mChannel.getCoverUrl()).into((ImageView) this.chat_personimg);
//                                    }
                                        //         Glide.with(GroupChatFragment.this.getContext()).setDefaultRequestOptions(requestOptions)
                                        //                .load(member_pic_url)
                                        //                 .into((ImageView) chat_personimg);

                                    } else if (mChannel.getName().equals("NO") && !mChannel.getCoverUrl().equals("NO")) {
                                        Glide.with(getContext()).load(this.mChannel.getCoverUrl()).into((ImageView) this.chat_personimg);


//                                        Glide.with(getContext()).load(this.mChannel.getCoverUrl()).into((ImageView) this.chat_personimg);
                                        tv_groupname.setText(member_name);
                                        //                               }

                                    } else {

                                        tv_groupname.setText(member_name);
                                        //this.chat_personimg.setImageResource(R.drawable.profile_img);

                                        Glide.with(GroupChatFragment.this.getContext()).setDefaultRequestOptions(requestOptions)
                                                .load(member_pic_url)
                                                .into((ImageView) chat_personimg); //comment

                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        GroupChatFragment.this.tv_groupname.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                Log.d("tv_groupname", "" + mChannel.getMemberCount());
                if (GroupChatFragment.this.mChannel.getMembers().size() > 2) {

                    Intent intent = new Intent(GroupChatFragment.this.getActivity(), GroupParticipantActivity.class);
                    intent.putExtra("members_key", GroupChatFragment.this.mChannelUrl);
                    intent.putExtra("groupname_key", GroupChatFragment.this.mChannel.getName());
                    GroupChatFragment.this.startActivity(intent);
                }
            }
        });
        chat_personimg.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (GroupChatFragment.this.mChannel.getMembers().size() > 2) {

                    Intent intent = new Intent(GroupChatFragment.this.getActivity(), GroupParticipantActivity.class);
                    intent.putExtra("members_key", GroupChatFragment.this.mChannelUrl);
                    intent.putExtra("groupname_key", GroupChatFragment.this.mChannel.getName());
                    GroupChatFragment.this.startActivity(intent);
                }
            }
        });

        ((Main2Activity) getActivity()).setiMemberSize(member.size());
    }

    public void updateOnlineStatus(String onlineStatus) {
       /* while (isRunning) {
            ArrayList<String> userIds = new ArrayList<>();
            userIds.add(userId);

            ApplicationUserListQuery applicationUserListQuery = SendBird.createApplicationUserListQuery();
            applicationUserListQuery.setUserIdsFilter(userIds);
            applicationUserListQuery.next(new UserListQuery.UserListQueryResultHandler() {
                @Override
                public void onResult(List<User> list, SendBirdException e) {
                    if (e != null) {    // Error.
                        return;
                    }
*/
        //Online, Offline, Away status will be set for 1-1 chat only.
        tv_onlinetime.setVisibility(View.VISIBLE);
        if (onlineStatus.toLowerCase().equals("online")) {
            tv_onlinetime.setImageResource(R.drawable.green_dot);
        } else if (onlineStatus.toLowerCase().equals("offline")) {
            tv_onlinetime.setImageResource(R.drawable.yellowdot);
        } else {
            tv_onlinetime.setImageResource(R.drawable.orange_dot);
        }
    }

    private void sendUserMessageWithUrl(final String text2, String url) {
        new WebUtils.UrlPreviewAsyncTask() {
            /* access modifiers changed from: protected */
            public void onPostExecute(UrlPreviewInfo info) {
                UserMessage tempUserMessage;
                SendUserMessageHandler handler = new SendUserMessageHandler() {
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        if (e != null) {
                            Log.e(GroupChatFragment.LOG_TAG, e.toString());
                            FragmentActivity activity = GroupChatFragment.this.getActivity();
                            StringBuilder sb = new StringBuilder();
                            sb.append("Send failed with error ");
                            sb.append(e.getCode());
                            sb.append(": ");
                            sb.append(e.getMessage());
                            if (!Objects.requireNonNull(e.getMessage()).contains("You are muted.")) {
                                Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
                            }
                            GroupChatFragment.this.mChatAdapter.markMessageFailed(userMessage.getRequestId());
                            return;
                        }
                        GroupChatFragment.this.mChatAdapter.markMessageSent(userMessage);
                    }
                };
                try {
                    tempUserMessage = GroupChatFragment.this.mChannel.sendUserMessage(text2, info.toJsonString(), GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE, handler);
                } catch (Exception e) {
                    tempUserMessage = GroupChatFragment.this.mChannel.sendUserMessage(text2, handler);
                }
                GroupChatFragment.this.mChatAdapter.addFirst(tempUserMessage);
            }
        }.execute(new String[]{url});
    }

    private void sendUserContact(String name, String number) {
    }

    private void sendContact(String name, String number) {
        this.mChatAdapter.addFirst(this.mChannel.sendUserMessage(name, number, "Contact", null, new SendUserMessageHandler() {
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    Log.e(GroupChatFragment.LOG_TAG, e.toString());
                    FragmentActivity activity = GroupChatFragment.this.getActivity();
                    StringBuilder sb = new StringBuilder();
                    sb.append("Send failed with error ");
                    sb.append(e.getCode());
                    sb.append(": ");
                    sb.append(e.getMessage());
                    if (!Objects.requireNonNull(e.getMessage()).contains("You are muted.")) {
                        Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
                    }
                    GroupChatFragment.this.mChatAdapter.markMessageFailed(userMessage.getRequestId());
                    return;
                }
                GroupChatFragment.this.mChatAdapter.markMessageSent(userMessage);
            }
        }));
    }

    /* access modifiers changed from: private */
    public void sendUserMessage(String text2) {
        this.keyboard_container.setVisibility(View.GONE);
        List<String> urls2 = WebUtils.extractUrls(text2);
        if (urls2.size() > 0) {
            sendUserMessageWithUrl(text2, (String) urls2.get(0));
            return;
        }
        if(mChannel!=null) {
            this.mChatAdapter.addFirst(this.mChannel.sendUserMessage(text2, (SendUserMessageHandler) new SendUserMessageHandler() {
                public void onSent(UserMessage userMessage, SendBirdException e) {
                    if (e != null) {
                        Log.e(GroupChatFragment.LOG_TAG, e.toString());
                        try {
                            FragmentActivity activity = GroupChatFragment.this.getActivity();
                            StringBuilder sb = new StringBuilder();
                            sb.append("Send failed with error ");
                            sb.append(e.getCode());
                            sb.append(": ");
                            sb.append(e.getMessage());
                            if (!Objects.requireNonNull(e.getMessage()).contains("You are muted.")) {
                                Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
                            }
                            GroupChatFragment.this.mChatAdapter.markMessageFailed(userMessage.getRequestId());
                        } catch (Exception e2) {
                        }
                        return;
                    }
                    GroupChatFragment.this.mChatAdapter.markMessageSent(userMessage);
                }
            }));
        }
    }

    /* access modifiers changed from: private */
    public void setTypingStatus(boolean typing) {
        if (this.mChannel != null) {
            if (typing) {
                this.mIsTyping = true;
                this.mChannel.startTyping();
            } else {
                this.mIsTyping = false;
                this.mChannel.endTyping();
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendRecordedFile(String uri) {
        File file = new File(uri);
        ArrayList<ThumbnailSize> arrayList = new ArrayList<>();
        arrayList.add(new ThumbnailSize(240, 240));
        arrayList.add(new ThumbnailSize(320, 320));
        if (uri.equals("")) {
            Toast.makeText(getActivity(), "File must be located in local storage.", Toast.LENGTH_LONG).show();
            return;
        }
        SendFileMessageWithProgressHandler r10 = new SendFileMessageWithProgressHandler() {
            public void onProgress(int bytesSent, int totalBytesSent, int totalBytesToSend) {
                FileMessage fileMessage = (FileMessage) GroupChatFragment.this.mFileProgressHandlerMap.get(this);
                if (fileMessage != null && totalBytesToSend > 0) {
                    GroupChatFragment.this.mChatAdapter.setFileProgressPercent(fileMessage, (totalBytesSent * 100) / totalBytesToSend);
                }
            }

            public void onSent(FileMessage fileMessage, SendBirdException e) {
                if (e != null) {
                    FragmentActivity activity = GroupChatFragment.this.getActivity();
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    sb.append(e.getCode());
                    sb.append(":");
                    sb.append(e.getMessage());
                    Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
                    GroupChatFragment.this.mChatAdapter.markMessageFailed(fileMessage.getRequestId());
                    return;
                }
                GroupChatFragment.this.mChatAdapter.markMessageSent(fileMessage);
            }
        };
        if (this.mChannel != null) {
            FileMessage tempFileMessage = this.mChannel.sendFileMessage(file, "Recording", "audio/mpeg", 346923, "", (String) null, arrayList, (SendFileMessageWithProgressHandler) r10);
            this.mFileProgressHandlerMap.put(r10, tempFileMessage);
            this.mChatAdapter.addTempFileMessageInfo(tempFileMessage, Uri.parse(uri));
            this.mChatAdapter.addFirst(tempFileMessage);
        }
    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {
        try {
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File from = new File(inputPath);
            StringBuilder sb = new StringBuilder();
            sb.append(outputPath);
            sb.append("/");
            sb.append(inputFile);
            from.renameTo(new File(sb.toString()));
        } catch (Exception e) {
            Log.e("tag1", e.getMessage());
        }
    }

    /* access modifiers changed from: private */
    public void sendFileWithThumbnail(Uri uri) {
        Log.d("datain", "1" + uri + "--");
        Uri uri2 = uri;
        ArrayList<ThumbnailSize> arrayList = new ArrayList<ThumbnailSize>();
        arrayList.add(new ThumbnailSize(240, 240));
        arrayList.add(new ThumbnailSize(320, 320));
        Hashtable<String, Object> info = FileUtils.getFileInfo(getActivity(), uri2);
//        for (Map.Entry<String,Object> entry : info.entrySet()) {
//            Log.d("datain","2"+uri+"--");
//            String key = entry.getKey();
//            Log.e("key doc", key);
//            Object value = entry.getValue();
//            // do stuff
//        }
        if (info == null) {
            Log.d("datain", "3" + uri + "--");
            Toast.makeText(getActivity(), "Extracting file information failed.", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d("datain", "4" + uri + "--");
        String path = (String) info.get("path");
        File file = new File(path);
        String name = (String) info.get("name");
        String mime = (String) info.get("mime");
        int size = ((Integer) info.get("size")).intValue();
        Log.e("key doc", name + "  " + mime + "  " + size + "  " + path);
        if (path.equals("")) {
            Toast.makeText(getActivity(), "File must be located in local storage.", Toast.LENGTH_LONG).show();
        } else {
            Log.d("datain", "5" + uri + "--");
            SendFileMessageWithProgressHandler r11 = new SendFileMessageWithProgressHandler() {
                public void onProgress(int bytesSent, int totalBytesSent, int totalBytesToSend) {
                    Log.d("datain", "6" + uri + "--");
                    if (GroupChatFragment.this.mFileProgressHandlerMap != null) {
                        Log.d("datain", "7" + uri + "--");
                        FileMessage fileMessage = (FileMessage) GroupChatFragment.this.mFileProgressHandlerMap.get(this);
                        if (fileMessage != null && totalBytesToSend > 0) {
                            Log.d("datain", "8" + uri + "--");
                            GroupChatFragment.this.mChatAdapter.setFileProgressPercent(fileMessage, (totalBytesSent * 100) / totalBytesToSend);
                        }
                    }
                }

                public void onSent(FileMessage fileMessage, SendBirdException e) {
                    if (e != null) {
                        Log.e("onSent doc", String.valueOf(e));
                        try {
                            FragmentActivity activity = GroupChatFragment.this.getActivity();
                            StringBuilder sb = new StringBuilder();
                            sb.append("");
                            sb.append(e.getCode());
                            sb.append(":");
                            sb.append(e.getMessage());
                            Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
                            GroupChatFragment.this.mChatAdapter.markMessageFailed(fileMessage.getRequestId());
                        } catch (Exception e2) {
                        }
                        return;
                    }
                    Log.e("onSent doc", fileMessage.getName());
                    GroupChatFragment.this.mChatAdapter.markMessageSent(fileMessage);
                }
            };
            if (this.mChannel != null) {
                SendFileMessageWithProgressHandler sendFileMessageWithProgressHandler = r11;
                FileMessage tempFileMessage = this.mChannel.sendFileMessage(file, name, mime, size, "", (String) null, arrayList, r11);
                this.mFileProgressHandlerMap.put(sendFileMessageWithProgressHandler, tempFileMessage);
                this.mChatAdapter.addTempFileMessageInfo(tempFileMessage, uri2);
                this.mChatAdapter.addFirst(tempFileMessage);
            }
        }
    }

    /* access modifiers changed from: private */
    public void editMessage(BaseMessage message, String editedMessage) {
        this.mChannel.updateUserMessage(message.getMessageId(), editedMessage, null, null, new UpdateUserMessageHandler() {
            public void onUpdated(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    FragmentActivity activity = GroupChatFragment.this.getActivity();
                    StringBuilder sb = new StringBuilder();
                    sb.append("Error ");
                    sb.append(e.getCode());
                    sb.append(": ");
                    sb.append(e.getMessage());
                    Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                GroupChatFragment.this.mChatAdapter.loadLatestMessages(30, new GetMessagesHandler() {
                    public void onResult(List<BaseMessage> list, SendBirdException e) {
                        GroupChatFragment.this.mChatAdapter.markAllMessagesAsRead();
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    public void deleteMessage(BaseMessage message) {
        this.mChannel.deleteMessage(message, new DeleteMessageHandler() {
            public void onResult(SendBirdException e) {
                if (e != null) {
                    FragmentActivity activity = GroupChatFragment.this.getActivity();
                    StringBuilder sb = new StringBuilder();
                    sb.append("Error ");
                    sb.append(e.getCode());
                    sb.append(": ");
                    sb.append(e.getMessage());
                    Toast.makeText(activity, sb.toString(), Toast.LENGTH_LONG).show();
                    return;
                }
                GroupChatFragment.this.mChatAdapter.loadLatestMessages(30, new GetMessagesHandler() {
                    public void onResult(List<BaseMessage> list, SendBirdException e) {
                        GroupChatFragment.this.mChatAdapter.markAllMessagesAsRead();
                    }
                });
            }
        });
    }

    private void initView() {
        //  this.mAudioRecordButton = this.audio_record_button;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void openDocument(String name) {
        Intent intent = new Intent("android.intent.action.VIEW");
        File file = new File(name);
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (extension.equalsIgnoreCase("") || mimetype == null) {
            intent.setDataAndType(Uri.fromFile(file), "text/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype);
        }
        startActivity(Intent.createChooser(intent, "Choose an Application:"));
    }

    private void check() {
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.READ_CONTACTS") == 0) {
            new Thread(new Runnable() {
                public void run() {
                }
            }).start();
        } else if (VERSION.SDK_INT < 23) {
        } else {
            if (shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                Toast.makeText(getActivity(), "Read contacts permission is required to function app correctly", Toast.LENGTH_LONG).show();
                return;
            }
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.READ_CONTACTS"}, 3);
        }
    }

//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
//        this.menuInstance = menu;
//        inflater.inflate(this.more_chatopen, menu);
//    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_searchs /*2131296295*/:
                return true;
            case R.id.block /*2131296333*/:
               /* if (Common.isConnected(getActivity())) {
                    Member currentMember2 = null;
                    List<Member> member = this.mChannel.getMembers();
                    if (member != null && member.size() == 2) {
                        for (int i = 0; i < member.size(); i++) {
                            try {
                                if (SharedPrefManager.getInstance(getActivity()).isLoggedIn()) {
                                    String userId2 = String.valueOf(SharedPrefManager.getInstance(getActivity()).getUser().getUser_id()).replaceAll("\\s", "");
                                    String str = SharedPrefManager.getInstance(getActivity()).getUser().getUser_name().toString();
                                    if (!userId2.equalsIgnoreCase(((Member) member.get(i)).getUserId())) {
                                        currentMember2 = (Member) member.get(i);
                                    }
                                }
                            } catch (Exception e) {
                                Common.showToast(getActivity(), "Failed!");
                                e.printStackTrace();
                            }
                        }
                    }
                    if (currentMember2 != null) {
                        if (currentMember2.isBlockedByMe()) {
                            SendBird.unblockUserWithUserId(currentMember2.getUserId(), new UserUnblockHandler() {
                                public void onUnblocked(SendBirdException e) {
                                    if (e != null) {
                                        Common.showToast(GroupChatFragment.this.getActivity(), "User already unblocked.");
                                        return;
                                    }
                                    GroupChatFragment.this.more_chatopen = R.menu.more_chatopen;
                                    GroupChatFragment.this.getActivity().invalidateOptionsMenu();
                                    Common.showToast(GroupChatFragment.this.getActivity(), "User unblocked successfully.");
                                }
                            });
                        } else {
                            SendBird.blockUserWithUserId(currentMember2.getUserId(), new UserBlockHandler() {
                                public void onBlocked(User user, SendBirdException e) {
                                    if (e != null) {
                                        Common.showToast(GroupChatFragment.this.getActivity(), "User already blocked.");
                                        return;
                                    }
                                    GroupChatFragment.this.more_chatopen = R.menu.more_chatopen_blocked;
                                    GroupChatFragment.this.getActivity().invalidateOptionsMenu();
                                    Common.showToast(GroupChatFragment.this.getActivity(), "User blocked successfully.");
                                }
                            });
                        }
                    }
                }*/
                return true;
            case R.id.deletechat /*2131296504*/:
               /* this.mChannel.resetMyHistory(new GroupChannelResetMyHistoryHandler() {
                    public void onResult(SendBirdException e) {
                        if (e != null) {
                            Common.showToast(GroupChatFragment.this.getActivity(), "Unable to delete chat.");
                            return;
                        }
                        Common.showToast(GroupChatFragment.this.getActivity(), "Chat deleted successfully.");
//                        GroupChatFragment.this.sharedPrefsHelper.saveNull(GroupChatFragment.this.mChannelUrl, null);
//                        GroupChatFragment.this.mChatAdapter.load(GroupChatFragment.this.mChannelUrl);
                    }
                });*/
                return true;
            case R.id.start_audio_call /*2131297086*/:
                startCall(false);
                return true;
            case R.id.start_moreoption /*2131297087*/:
                return true;
            case R.id.unfriend /*2131297246*/:
                /*if (!(this.currentMember == null || this.currentMember.getUserId() == null)) {
                    unFriend(this.currentMember.getUserId());
                }*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void unFriend(String friend_id) {
        String str = CommonUtils.baseUrl;
        final String str2 = friend_id;
        StringRequest r1 = new StringRequest(1, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("success");
                    String string = jsonObject.getString("message");
                    if (status.equals("true")) {
                        Toast.makeText(GroupChatFragment.this.getActivity(), "Friend request canceled", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(GroupChatFragment.this.getActivity(), "Friend Request Cancel Failed!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GroupChatFragment.this.getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "removefriend");
                logParams.put("userid", GroupChatFragment.this.userId);
                logParams.put("frnid", str2);
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r1);
    }

    private void showUploadConfirmDialog(final Uri uri) {
        new Builder(getActivity()).setMessage((CharSequence) "Upload file?").setPositiveButton((int) R.string.upload,
                (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("datain", dialog + " <data is !> " + which + "--" + uri);
                        if (which == -1) {
                            List<ThumbnailSize> thumbnailSizes = new ArrayList<>();
                            thumbnailSizes.add(new ThumbnailSize(240, 240));
                            thumbnailSizes.add(new ThumbnailSize(320, 320));
                            Log.d("datain", "" + uri + "--" + thumbnailSizes);
                            GroupChatFragment.this.sendFileWithThumbnail(uri);
                            GroupChatFragment.this.attachment_container.setVisibility(View.GONE);
                        }
                    }
                }).setNegativeButton((int) R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }

    /**
     * This upload alert will be used as confirmation
     * when user want to upload contact and Gif images.
     */
    private void showUploadConfirmDialog(Intent data, Gif gif, int requestCode) {
        new Builder(getActivity()).setMessage((CharSequence) "Upload file?").setPositiveButton((int) R.string.upload,
                (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /*Log.d("datain", dialog + " <data is !> " + which + "--" + uri);
                        if (which == -1) {
                            List<ThumbnailSize> thumbnailSizes = new ArrayList<>();
                            thumbnailSizes.add(new ThumbnailSize(240, 240));
                            thumbnailSizes.add(new ThumbnailSize(320, 320));
                            Log.d("datain", "" + uri + "--" + thumbnailSizes);
                            GroupChatFragment.this.sendFileWithThumbnail(uri);
                            GroupChatFragment.this.attachment_container.setVisibility(View.GONE);
                        }*/
                        if (requestCode ==  REQUEST_CODE_SHARE_CONTACT && data != null) {
                            contactname = data.getStringExtra("contactname_key");
                            phonenum = data.getStringExtra("phonenum_key");
                            sendContact(contactname, phonenum);
                        } else if (requestCode == REQUEST_CODE_SHARE_GIF) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("onGifSelected: ");
                            sb.append(gif.getGifUrl());
                            Log.d("GUF", sb.toString());
                            GroupChatFragment.this.sendUserMessage(gif.getGifUrl());
                        }

                    }
                }).setNegativeButton((int) R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }

    private void fetchInitialMessages() {
        if (mMessageCollection == null) {
            return;
        }

        mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.PREVIOUS, new FetchCompletionHandler() {
            @Override
            public void onCompleted(boolean hasMore, SendBirdException e) {
                mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.NEXT, new FetchCompletionHandler() {
                    @Override
                    public void onCompleted(boolean hasMore, SendBirdException e) {
                        mMessageCollection.fetchFailedMessages(new CompletionHandler() {
                            @Override
                            public void onCompleted(SendBirdException e) {
                                if (getActivity() == null) {
                                    return;
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mChatAdapter.markAllMessagesAsRead();
                                        mLayoutManager.scrollToPositionWithOffset(mChatAdapter.getLastReadPosition(mLastRead), mRecyclerView.getHeight() / 2);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void createMessageCollection(final String channelUrl) {
        Log.d("SyncManagerUtils", "msg-" + channelUrl);
        GroupChannel.getChannel(channelUrl, new GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                Log.d("SyncManagerUtils", "msg-" + groupChannel);
                if (e != null) {
                    MessageCollection.create(channelUrl, mMessageFilter, mLastRead, new MessageCollectionCreateHandler() {
                        @Override
                        public void onResult(MessageCollection messageCollection, SendBirdException e) {
                            if (e == null) {
                                if (mMessageCollection != null) {
                                    mMessageCollection.remove();
                                }

                                mMessageCollection = messageCollection;
                                mMessageCollection.setCollectionHandler(mMessageCollectionHandler);

                                mChannel = mMessageCollection.getChannel();
                                mChatAdapter.setChannel(mChannel);

                                if (getActivity() == null) {
                                    return;
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mChatAdapter.clear();
                                        updateActionBarTitle();
                                    }
                                });

                                fetchInitialMessages();
                            } else {
                                //Toast.makeText(getContext(), getString(R.string.get_channel_failed), Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getActivity().onBackPressed();
                                    }
                                }, 1000);
                            }
                        }
                    });
                } else {
                    if (mMessageCollection != null) {
                        mMessageCollection.remove();
                    }
                    Log.d("SyncManagerUtils", "msg1");
                    mMessageCollection = new MessageCollection(groupChannel, mMessageFilter, mLastRead);
                    mMessageCollection.setCollectionHandler(mMessageCollectionHandler);

                    mChannel = groupChannel;
                    mChatAdapter.setChannel(mChannel);
                    mChatAdapter.clear();
                    updateActionBarTitle();
                    fetchInitialMessages();
                }
               /* if (mChannel != null && mChannel.getMemberCount() <= 2) {
                    Main2Activity.iv_chatcall.setVisibility(View.VISIBLE);
                }*/
            }
        });
    }

    private void updateLastSeenTimestamp(List<BaseMessage> messages) {
        long lastSeenTimestamp = mLastRead == Long.MAX_VALUE ? 0 : mLastRead;
        for (BaseMessage message : messages) {
            if (lastSeenTimestamp < message.getCreatedAt()) {
                lastSeenTimestamp = message.getCreatedAt();
            }
        }

        if (lastSeenTimestamp > mLastRead) {
            PreferenceUtils.setLastRead(mChannelUrl, lastSeenTimestamp);
            mLastRead = lastSeenTimestamp;
        }
    }

    private MessageCollectionHandler mMessageCollectionHandler = new MessageCollectionHandler() {
        @Override
        public void onMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
        }

        @Override
        public void onSucceededMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
            Log.d("SyncManager", "onSucceededMessageEvent: size = " + messages.size() + ", action = " + action);

            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (action) {
                        case INSERT:
                            mChatAdapter.insertSucceededMessages(messages);
                            mChatAdapter.markAllMessagesAsRead();
                            break;

                        case REMOVE:
                            mChatAdapter.removeSucceededMessages(messages);
                            break;

                        case UPDATE:
                            mChatAdapter.updateSucceededMessages(messages);
                            break;

                        case CLEAR:
                            mChatAdapter.clear();
                            break;
                    }
                }
            });

            updateLastSeenTimestamp(messages);
        }

        @Override
        public void onPendingMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
            Log.d("SyncManager", "onPendingMessageEvent: size = " + messages.size() + ", action = " + action);
            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (action) {
                        case INSERT:
                            List<BaseMessage> pendingMessages = new ArrayList<>();
                            for (BaseMessage message : messages) {
                                if (!mChatAdapter.failedMessageListContains(message)) {
                                    pendingMessages.add(message);
                                }
                            }
                            mChatAdapter.insertSucceededMessages(pendingMessages);
                            break;

                        case REMOVE:
                            mChatAdapter.removeSucceededMessages(messages);
                            break;
                    }
                }
            });
        }

        @Override
        public void onFailedMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action, final FailedMessageEventActionReason reason) {
            Log.d("SyncManager", "onFailedMessageEvent: size = " + messages.size() + ", action = " + action);
            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (action) {
                        case INSERT:
                            mChatAdapter.insertFailedMessages(messages);
                            break;

                        case REMOVE:
                            mChatAdapter.removeFailedMessages(messages);
                            break;
                        case UPDATE:
                            if (reason == FailedMessageEventActionReason.UPDATE_RESEND_FAILED) {
                                mChatAdapter.updateFailedMessages(messages);
                            }
                            break;
                    }
                }
            });
        }

        @Override
        public void onNewMessage(MessageCollection collection, BaseMessage message) {
            Log.d("SyncManager", "onNewMessage: message = " + message);
            //show when the scroll position is bottom ONLY.
            if (mLayoutManager.findFirstVisibleItemPosition() != 0) {
                if (message instanceof UserMessage) {
                    if (!((UserMessage) message).getSender().getUserId().equals(PreferenceUtils.getUserId())) {
                        // mNewMessageText.setText("New Message = " + ((UserMessage) message).getSender().getNickname() + " : " + ((UserMessage) message).getMessage());
                        // mNewMessageText.setVisibility(View.VISIBLE);
                    }
                } else if (message instanceof FileMessage) {
                    if (!((FileMessage) message).getSender().getUserId().equals(PreferenceUtils.getUserId())) {
                        // mNewMessageText.setText("New Message = " + ((FileMessage) message).getSender().getNickname() + "Send a File");
                        // mNewMessageText.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    };

    /**
     * Use this method for sending Admin message.
     */
    public void sendAdminMessage(String message) {
        UserMessageParams userMessageParams = new UserMessageParams();
        userMessageParams.setCustomType("ADMM");
        userMessageParams.setMessage(message);
        this.mChatAdapter.addFirst(this.mChannel.sendUserMessage(userMessageParams,
                new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        if (e != null) {    // Error.
                            try {
                                FragmentActivity activity = GroupChatFragment.this.getActivity();
                                StringBuilder sb = new StringBuilder();
                                sb.append("Send failed with error ");
                                sb.append(e.getCode());
                                sb.append(": ");
                                sb.append(e.getMessage());
                                if (!Objects.requireNonNull(e.getMessage()).contains("You are muted.")) {
                                    Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
                                }
                                GroupChatFragment.this.mChatAdapter.markMessageFailed(userMessage.getRequestId());
                            } catch (Exception e2) {
                            }
                            return;
                        }
                        isNewChannel = false;
                        strAdminMessage = "";
                        GroupChatFragment.this.mChatAdapter.markMessageSent(userMessage);
                    }
                }));
    }

    public void removeFriend() {
        if (!(this.currentMember == null || this.currentMember.getUserId() == null)) {
            unFriend(this.currentMember.getUserId());
        }
    }

    public void blockUser() {
        if (Common.isConnected(getActivity())) {
            Member currentMember2 = null;
            List<Member> member = this.mChannel.getMembers();
            if (member.size() == 2) {
                for (int i = 0; i < member.size(); i++) {
                    try {
                        if (SharedPrefManager.getInstance(getActivity()).isLoggedIn()) {
                            String userId2 = String.valueOf(SharedPrefManager.getInstance(getActivity()).getUser().getUser_id()).replaceAll("\\s", "");
                            String str = SharedPrefManager.getInstance(getActivity()).getUser().getUser_name().toString();
                            if (!userId2.equalsIgnoreCase(((Member) member.get(i)).getUserId())) {
                                currentMember2 = (Member) member.get(i);
                            }
                        }
                    } catch (Exception e) {
                        Common.showToast(getActivity(), "Failed!");
                        e.printStackTrace();
                    }
                }
            }
            if (currentMember2 != null) {
                if (currentMember2.isBlockedByMe()) {
                    SendBird.unblockUserWithUserId(currentMember2.getUserId(), new UserUnblockHandler() {
                        public void onUnblocked(SendBirdException e) {
                            if (e != null) {
                                Common.showToast(GroupChatFragment.this.getActivity(), "User already unblocked.");
                                return;
                            }
                            sendAdminMessage(getString(R.string.admin_msg_you_unblock));
                            Objects.requireNonNull(getActivity()).invalidateOptionsMenu();
                            //Common.showToast(GroupChatFragment.this.getActivity(), "User unblocked successfully.");
                        }
                    });
                } else {
                    SendBird.blockUserWithUserId(currentMember2.getUserId(), new UserBlockHandler() {
                        public void onBlocked(User user, SendBirdException e) {
                            if (e != null) {
                                Common.showToast(GroupChatFragment.this.getActivity(), "User already blocked.");
                                return;
                            }
                            sendAdminMessage(getString(R.string.admin_msg_you_block));
                            Objects.requireNonNull(getActivity()).invalidateOptionsMenu();
                            //Common.showToast(GroupChatFragment.this.getActivity(), "User blocked successfully.");
                        }
                    });
                }
            }
        }
    }
}
