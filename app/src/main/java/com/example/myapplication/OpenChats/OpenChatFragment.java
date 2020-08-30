package com.example.myapplication.OpenChats;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activities.AllContacts;
import com.example.myapplication.Activities.Main3Activity;
import com.example.myapplication.Activities.MapsActivity;
import com.example.myapplication.Activities.MediaPlayerActivity;
import com.example.myapplication.Activities.PhotoViewerActivity;
import com.example.myapplication.Adapter.FileListAdapter;
import com.example.myapplication.GroupChats.GroupChatAdapter;
import com.example.myapplication.GroupChats.GroupParticipantActivity;
import com.example.myapplication.Intefaces.OnBackPressedListener;
import com.example.myapplication.Intefaces.OnStartFileDownloadListener;
import com.example.myapplication.R;
import com.example.myapplication.Utils.FileUtils;
import com.example.myapplication.Utils.MimeType;
import com.example.myapplication.Utils.PreferenceUtils;
import com.example.myapplication.Utils.UrlPreviewInfo;
import com.google.android.material.snackbar.Snackbar;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.kevalpatel2106.emoticongifkeyboard.EmoticonGIFKeyboardFragment;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.Emoticon;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.EmoticonSelectListener;
import com.kevalpatel2106.emoticongifkeyboard.gifs.Gif;
import com.kevalpatel2106.emoticongifkeyboard.gifs.GifSelectListener;
import com.kevalpatel2106.emoticonpack.android8.Android8EmoticonProvider;
import com.kevalpatel2106.gifpack.giphy.GiphyGifProvider;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.ParticipantListQuery;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserListQuery;
import com.sendbird.android.UserMessage;
import com.sendbird.syncmanager.MessageCollection;
import com.sendbird.syncmanager.MessageFilter;
import com.sendbird.syncmanager.handler.CompletionHandler;
import com.sendbird.syncmanager.handler.FetchCompletionHandler;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import br.com.safety.audio_recorder.AudioListener;
import br.com.safety.audio_recorder.AudioRecordButton;
import br.com.safety.audio_recorder.AudioRecording;
import br.com.safety.audio_recorder.RecordingItem;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class OpenChatFragment extends Fragment {
    List<BaseMessage> messagelist = new ArrayList();

    public static final int CAMERA_PIC_REQUEST = 100;
    public static final int INTENT_REQUEST_CHOOSE_MEDIA = 301;
    public int REQUEST_CODE_DOC = 404;
    final MessageFilter mMessageFilter = new MessageFilter(BaseChannel.MessageTypeFilter.ALL, null, null);
    private long mLastRead;
    static final String EXTRA_CHANNEL_URL = "CHANNEL_URL";
    private static final String LOG_TAG = OpenChatFragment.class.getSimpleName();
    private static final int CHANNEL_LIST_LIMIT = 30;
    private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_OPEN_CHAT";
    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_OPEN_CHAT";
    private static final int STATE_NORMAL = 0;
    private static final int STATE_EDIT = 1;
    private static final int INTENT_REQUEST_CHOOSE_IMAGE = 300;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    private final static int FILE_REQUEST_CODE = 1;
    EmoticonGIFKeyboardFragment emoticonGIFKeyboardFragment;
    FrameLayout attachment_container, keyboard_container;
    EditText chatsearchbar;
    AudioRecordButton audio_record_button;
    FrameLayout recordcontainer, searchframe;
    ImageView chat_backarrow, iv_chatsearch;
    private InputMethodManager mIMM;
    private RecyclerView mRecyclerView;
    private OpenChatAdapter mChatAdapter;
    private LinearLayoutManager mLayoutManager;
    private View mRootLayout;
    private EditText mMessageEditText;
    private Button mMessageSendButton;
    private ImageButton mUploadFileButton;
    private View mCurrentEventLayout;
    private TextView mCurrentEventText, tv_chatperson_name;
    private ImageView emoj, iv_chat_more;
    private OpenChannel mChannel;
    private String mChannelUrl;
    private PreviousMessageListQuery mPrevMessageListQuery;
    private int mCurrentState = STATE_NORMAL;
    private BaseMessage mEditingMessage = null;
    private ImageView iv_document, iv_camera, iv_gallery, iv_record, iv_audio, iv_location, iv_contact, iv_schedule;
    private AudioRecordButton mAudioRecordButton;
    private AudioRecording audioRecording;
    private MessageCollection mMessageCollection;
    private FileListAdapter fileListAdapter;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private List<BaseMessage> mMessageList;
    private String urls;
    private HashMap<BaseChannel.SendFileMessageWithProgressHandler, FileMessage> mFileProgressHandlerMap;
    private String contactname;
    private String phonenum;
    private OnStartFileDownloadListener onStartFileDownloadListener;
    private boolean mIsTyping;

    /**
     * To create an instance of this fragment, a Channel URL should be passed.
     */
    public static OpenChatFragment newInstance(@NonNull String channelUrl) {
        OpenChatFragment fragment = new OpenChatFragment();

        Bundle args = new Bundle();
        args.putString(OpenChannelListFragment.EXTRA_OPEN_CHANNEL_URL, channelUrl);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIMM = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        ((Main3Activity) getActivity()).setOnBackPressedListener(new OnBackPressedListener() {
            @Override
            public void doBack() {
                try {
                    if (attachment_container.getVisibility() == View.VISIBLE) {
                        attachment_container.setVisibility(View.GONE);
                    } else if (emoticonGIFKeyboardFragment.isAdded()) {
                        if (emoticonGIFKeyboardFragment.isOpen()) {
                            emoticonGIFKeyboardFragment.close();
                        } else {
                            getActivity().finish();
                        }
                    } else {
                        getActivity().finish();
                    }
                } catch (Exception e) {
                }

            }
        });
        this.mFileProgressHandlerMap = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_open_chat, container, false);
        // getActivity().getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);

        setRetainInstance(true);

        setHasOptionsMenu(true);
        attachment_container = rootView.findViewById(R.id.attachment_container);
        keyboard_container = rootView.findViewById(R.id.keyboard_container);
        mRootLayout = rootView.findViewById(R.id.layout_open_chat_root);
        //comment
        ImageView chat_backarrow = rootView.findViewById(R.id.chat_backarrow);
        iv_document = (ImageView) rootView.findViewById(R.id.iv_document);
        iv_camera = (ImageView) rootView.findViewById(R.id.iv_camera);
        iv_gallery = (ImageView) rootView.findViewById(R.id.iv_gallery);
        iv_record = (ImageView) rootView.findViewById(R.id.ic_recorder);
        iv_audio = (ImageView) rootView.findViewById(R.id.iv_audio);
        iv_contact = (ImageView) rootView.findViewById(R.id.iv_contact);
        iv_location = (ImageView) rootView.findViewById(R.id.iv_location);
        iv_schedule = (ImageView) rootView.findViewById(R.id.iv_schedule);
        iv_chatsearch = (ImageView) rootView.findViewById(R.id.iv_chatsearch);
        chat_backarrow = (ImageView) ((Main3Activity) getActivity()).findViewById(R.id.chat_backarrow);
        recordcontainer = (FrameLayout) rootView.findViewById(R.id.recordcontainer);
        searchframe = (FrameLayout) rootView.findViewById(R.id.searchframe);

        //audio_record_button
        audio_record_button = (AudioRecordButton) rootView.findViewById(R.id.audio_record_button);

        chatsearchbar = (EditText) rootView.findViewById(R.id.searchbar);

        setUpChatListAdapter();

        /*this.audio_record_button.setOnAudioListener(new AudioListener() {
            public void onStop(RecordingItem recordingItem) {
                audioRecording.play(recordingItem);
                urls = recordingItem.getFilePath();
                recordcontainer.setVisibility(View.GONE);
                sendRecordedFile(urls);
            }

            public void onCancel() {
                recordcontainer.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }

            public void onError(Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Error: ");
                sb.append(e.getMessage());
                Log.d("MainActivity", sb.toString());
            }
        });
*/
        this.audio_record_button.setOnAudioListener(new AudioListener() {
            public void onStop(RecordingItem recordingItem) {
                OpenChatFragment.this.audioRecording.play(recordingItem);
                OpenChatFragment.this.urls = recordingItem.getFilePath();
                OpenChatFragment.this.recordcontainer.setVisibility(View.GONE);
                OpenChatFragment.this.sendRecordedFile(OpenChatFragment.this.urls);
            }

            public void onCancel() {
                OpenChatFragment.this.recordcontainer.setVisibility(View.GONE);
                Toast.makeText(OpenChatFragment.this.getContext(),
                        "Cancel", Toast.LENGTH_SHORT).show();
            }

            public void onError(Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Error: ");
                sb.append(e.getMessage());
                Log.d("MainActivity", sb.toString());
            }
        });
        iv_chatsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchframe.setVisibility(View.VISIBLE);
            }
        });
        //comment
        // sendUserMessage(chatsearchbar.getText().toString());
        chatsearchbar.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // you can call or do what you want with your EditText here
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendUserMessage(chatsearchbar.getText().toString());
            }
        });

        audioRecording = new AudioRecording(getActivity());

        initView();
        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, READ_EXTERNAL_STORAGE}, 0);
        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE}, 0);
        chat_backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Main3Activity) getActivity()).finish();
            }
        });

        iv_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* recordcontainer.setVisibility(View.VISIBLE);
                attachment_container.setVisibility(View.GONE);*/
                OpenChatFragment.this.recordcontainer.setVisibility(View.VISIBLE);
                OpenChatFragment.this.attachment_container.setVisibility(View.GONE);
            }
        });
        //comment
      /*  this.mAudioRecordButton.setOnAudioListener(new AudioListener() {
            @Override
            public void onStop(RecordingItem recordingItem) {
                Toast.makeText(getContext(), "Audio..", Toast.LENGTH_SHORT).show();
                audioRecording.play(recordingItem);
            }

            @Override
            public void onCancel() {
                recordcontainer.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Log.d("MainActivity", "Error: " + e.getMessage());
            } //comment
        });*/
        iv_audio.setOnClickListener(new View.OnClickListener() {
            @Override
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


        this.iv_document.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
                intent.addCategory("android.intent.category.OPENABLE");
                intent.setType("*/*");
                intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/plain", "application/pdf", "application/zip", "application/apk", "application/txt"});

                OpenChatFragment.this.startActivityForResult(intent, OpenChatFragment.this.REQUEST_CODE_DOC);
                OpenChatFragment.this.attachment_container.setVisibility(View.GONE);

            }
        });

        // camera click
        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //comment
                startActivity(cameraIntent);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);


            }
        });

        iv_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), AllContacts.class), 3);
                attachment_container.setVisibility(View.GONE);
            }
        });

        iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), MapsActivity.class), 44);
                attachment_container.setVisibility(View.GONE);
            }

        });
        iv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestMedia();
                attachment_container.setVisibility(View.GONE);
            }
        });

        chat_backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
                getActivity().finish();
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_open_channel_chat);

        mCurrentEventLayout = rootView.findViewById(R.id.layout_open_chat_current_event);
        mCurrentEventText = (TextView) rootView.findViewById(R.id.text_open_chat_current_event);
        tv_chatperson_name = (TextView) ((Main3Activity) getActivity()).findViewById(R.id.tv_chatperson_name);
        iv_chat_more = (ImageView) rootView.findViewById(R.id.iv_chat_more);

        tv_chatperson_name.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                ParticipantListQuery participantListQuery = mChannel.createParticipantListQuery();
                participantListQuery.next(new UserListQuery.UserListQueryResultHandler() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResult(List<User> list, SendBirdException e) {
                        if (e != null) {
                            e.printStackTrace();// Error.
                            return;
                        }
                        Log.d("OpenChat", " Total members : " + list.size());
                        //if (OpenChatFragment.this.mChannel.getMembers().size() > 2) {

                        Intent intent = new Intent(OpenChatFragment.this.getActivity(), OpenParticipantActivity.class);
                        intent.putExtra("members_key", OpenChatFragment.this.mChannelUrl);
                        intent.putExtra("groupname_key", OpenChatFragment.this.mChannel.getName());
                        OpenChatFragment.this.startActivity(intent);
                        //}
                    }
                });
            }
        });

//        ((Main3Activity) getActivity()).setActionBarTitle(mChannel.getName());//comment


        setUpRecyclerView();

        mMessageSendButton = (Button) rootView.findViewById(R.id.button_open_channel_chat_send);
        mMessageEditText = (EditText) rootView.findViewById(R.id.edittext_chat_message);
        this.mIsTyping = false;
        this.mMessageEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mIsTyping) {
                    setTypingStatus(true);
                }
                if (s.length() == 0) {
                    setTypingStatus(false);
                }
            }

            public void afterTextChanged(Editable s) {
                String s1 = s.toString().trim();
                if (s1.length() <= 0 || s1.equals("")) {
                    mMessageSendButton.setEnabled(false);
                } else {
                    mMessageSendButton.setEnabled(true);
                }
            }
        });
//        if(!Common.isConnected(getContext())){
        // createMessageCollection(mChannelUrl);
//        }

        this.mMessageEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // attachment_container.setVisibility(View.VISIBLE);
                keyboard_container.setVisibility(View.GONE);

            }
        });

        mUploadFileButton = (ImageButton) rootView.findViewById(R.id.button_open_channel_chat_upload);
        mUploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachment_container.setVisibility(View.VISIBLE);
                keyboard_container.setVisibility(View.GONE);
            }
        });
        mMessageSendButton.setEnabled(false);
        mMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentState == STATE_EDIT) {
                    String userInput = mMessageEditText.getText().toString();
                    if (userInput.length() > 0) {
                        if (mEditingMessage != null) {
                            editMessage(mEditingMessage, mMessageEditText.getText().toString());
                        }
                    }
                    setState(STATE_NORMAL, null, -1);
                } else {
                    String userInput = mMessageEditText.getText().toString().trim();
                    if (userInput.length() > 0) {
                        sendUserMessage(userInput);
                        mMessageEditText.setText("");
                    }
                }
            }
        });


        // Gets channel from URL user requested
        mChannelUrl = getArguments().getString(OpenChannelListFragment.EXTRA_OPEN_CHANNEL_URL);
        refreshFirst();

        EmoticonGIFKeyboardFragment.EmoticonConfig emoticonConfig = new EmoticonGIFKeyboardFragment.EmoticonConfig()
                .setEmoticonProvider(Android8EmoticonProvider.create())
                /*
                  NOTE: The process of removing last character when user preses back space will handle
                  by library if your edit text is in focus.
                 */
                .setEmoticonSelectListener(new EmoticonSelectListener() {

                    @Override
                    public void emoticonSelected(Emoticon emoticon) {
                        //Do something with new emoticon.
                        //     Log.d(TAG, "emoticonSelected: " + emoticon.getUnicode());
                        //   editText.append(emoticon.getUnicode(),
                        //         editText.getSelectionStart(),
                        //       editText.getSelectionEnd());
                        mMessageEditText.append(emoticon.getUnicode());

                    }

                    @Override
                    public void onBackSpace() {
                        //Do something here to handle backspace event.
                        //The process of removing last character when user preses back space will handle
                        //by library if your edit text is in focus.

                    }
                });
        EmoticonGIFKeyboardFragment.GIFConfig gifConfig = new EmoticonGIFKeyboardFragment

        /*
          Here we are using GIPHY to provide GIFs. Create Giphy GIF provider by passing your key.
          It is required to set GIF provider before adding fragment into container.
         */
                .GIFConfig(GiphyGifProvider.create(getActivity(), "564ce7370bf347f2b7c0e4746593c179"))
                .setGifSelectListener(new GifSelectListener() {
                    @Override
                    public void onGifSelected(@NonNull Gif gif) {
                        //Do something with the selected GIF.
                        Log.d("GUF", "onGifSelected: " + gif.getGifUrl());
                        //sendUserMessage(gif.getGifUrl());
                        showUploadConfirmDialog(/*null,*/ gif /*,REQUEST_CODE_SHARE_GIF*/);
                    }
                });
        emoticonGIFKeyboardFragment = EmoticonGIFKeyboardFragment
                .getNewInstance(rootView.findViewById(R.id.keyboard_container), emoticonConfig, gifConfig);
        emoj = (ImageView) rootView.findViewById(R.id.emoji_open_close_btn);
        emoj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*  getActivity().getWindow().setSoftInputMode(
                        SOFT_INPUT_ADJUST_PAN);*/
                keyboard_container.setVisibility(View.VISIBLE);
                attachment_container.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

//Adding the keyboard fragment to keyboard_container.
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.keyboard_container, emoticonGIFKeyboardFragment)
                        .commit();
                emoticonGIFKeyboardFragment.open();
            }
        });


        return rootView;
    }

    /**
     * This upload alert will be used as confirmation
     * when user want to upload contact and Gif images.
     */
    private void showUploadConfirmDialog(/*Intent data, */Gif gif/*, int requestCode*/) {
        new AlertDialog.Builder(getActivity()).setMessage((CharSequence) "Upload file?").setPositiveButton((int) R.string.upload,
                (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       /* if (requestCode ==  REQUEST_CODE_SHARE_CONTACT && data != null) {
                            contactname = data.getStringExtra("contactname_key");
                            phonenum = data.getStringExtra("phonenum_key");
                            sendContact(contactname, phonenum);
                        } else if (requestCode == REQUEST_CODE_SHARE_GIF) {*/
                        StringBuilder sb = new StringBuilder();
                        sb.append("onGifSelected: ");
                        sb.append(gif.getGifUrl());
                        Log.d("GUF", sb.toString());
                        OpenChatFragment.this.sendUserMessage(gif.getGifUrl());
                        /* }*/

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


                            }
                        });
                    }
                });
            }
        });
    }

    //    public void createMessageCollection(final String channelUrl) {
//        Log.d("SyncManagerUtils","msg-"+channelUrl);
//        GroupChannel.getChannel(channelUrl, new GroupChannel.GroupChannelGetHandler() {
//            @Override
//            public void onResult(GroupChannel groupChannel, SendBirdException e) {
//                Log.d("SyncManagerUtils","msg-"+groupChannel);
//                if (e != null) {
//                    MessageCollection.create(channelUrl, mMessageFilter, mLastRead, new MessageCollectionCreateHandler() {
//                        @Override
//                        public void onResult(MessageCollection messageCollection, SendBirdException e) {
//                            if (e == null) {
//                                if (mMessageCollection != null) {
//                                    mMessageCollection.remove();
//                                }
//
//                                mMessageCollection = messageCollection;
//                                mMessageCollection.setCollectionHandler(mMessageCollectionHandler);
//
//                                mChannel = mMessageCollection.getChannel();
//                                mChatAdapter.setChannel(mChannel);
//
//                                if (getActivity() == null) {
//                                    return;
//                                }
//
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mChatAdapter.clear();
//                                        updateActionBarTitle();
//                                    }
//                                });
//
//                                fetchInitialMessages();
//                            } else {
//                                //Toast.makeText(getContext(), getString(R.string.get_channel_failed), Toast.LENGTH_SHORT).show();
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        getActivity().onBackPressed();
//                                    }
//                                }, 1000);
//                            }
//                        }
//                    });
//                } else {
//                    if (mMessageCollection != null) {
//                        mMessageCollection.remove();
//                    }
//                    Log.d("SyncManagerUtils","msg1");
//                    mMessageCollection = new MessageCollection(groupChannel, mMessageFilter, mLastRead);
//                    mMessageCollection.setCollectionHandler(mMessageCollectionHandler);
//
//                    mChannel = groupChannel;
//                    mChatAdapter.setChannel(mChannel);
//                    mChatAdapter.clear();
//                    updateActionBarTitle();
//
//                    fetchInitialMessages();
//                }
//            }
//        });
//    }
//    private void updateLastSeenTimestamp(List<BaseMessage> messages) {
//        long lastSeenTimestamp = mLastRead == Long.MAX_VALUE ? 0 : mLastRead;
//        for (BaseMessage message : messages) {
//            if (lastSeenTimestamp < message.getCreatedAt()) {
//                lastSeenTimestamp = message.getCreatedAt();
//            }
//        }
//
//        if (lastSeenTimestamp > mLastRead) {
//            PreferenceUtils.setLastRead(mChannelUrl, lastSeenTimestamp);
//            mLastRead = lastSeenTimestamp;
//        }
//    }
//    private MessageCollectionHandler mMessageCollectionHandler = new MessageCollectionHandler() {
//        @Override
//        public void onMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
//        }
//
//        @Override
//        public void onSucceededMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
//            Log.d("SyncManager", "onSucceededMessageEvent: size = " + messages.size() + ", action = " + action);
//
//            if (getActivity() == null) {
//                return;
//            }
//
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    switch (action) {
//                        case INSERT:
//                            mChatAdapter.insertSucceededMessages(messages);
//                            mChatAdapter.markAllMessagesAsRead();
//                            break;
//
//                        case REMOVE:
//                            mChatAdapter.removeSucceededMessages(messages);
//                            break;
//
//                        case UPDATE:
//                            mChatAdapter.updateSucceededMessages(messages);
//                            break;
//
//                        case CLEAR:
//                            mChatAdapter.clear();
//                            break;
//                    }
//                }
//            });
//
//            updateLastSeenTimestamp(messages);
//        }
//        @Override
//        public void onPendingMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
//            Log.d("SyncManager", "onPendingMessageEvent: size = " + messages.size() + ", action = " + action);
//            if (getActivity() == null) {
//                return;
//            }
//
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    switch (action) {
//                        case INSERT:
//                            List<BaseMessage> pendingMessages = new ArrayList<>();
//                            for (BaseMessage message : messages) {
//                                if (!mChatAdapter.failedMessageListContains(message)) {
//                                    pendingMessages.add(message);
//                                }
//                            }
//                            mChatAdapter.insertSucceededMessages(pendingMessages);
//                            break;
//
//                        case REMOVE:
//                            mChatAdapter.removeSucceededMessages(messages);
//                            break;
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onFailedMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action, final FailedMessageEventActionReason reason) {
//            Log.d("SyncManager", "onFailedMessageEvent: size = " + messages.size() + ", action = " + action);
//            if (getActivity() == null) {
//                return;
//            }
//
//            getActivity().runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run() {
//                    switch (action) {
//                        case INSERT:
//                            mChatAdapter.insertFailedMessages(messages);
//                            break;
//
//                        case REMOVE:
//                            mChatAdapter.removeFailedMessages(messages);
//                            break;
//                        case UPDATE:
//                            if (reason == FailedMessageEventActionReason.UPDATE_RESEND_FAILED) {
//                                mChatAdapter.updateFailedMessages(messages);
//                            }
//                            break;
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onNewMessage(MessageCollection collection, BaseMessage message) {
//            Log.d("SyncManager", "onNewMessage: message = " + message);
//            //show when the scroll position is bottom ONLY.
//            if (mLayoutManager.findFirstVisibleItemPosition() != 0) {
//                if (message instanceof UserMessage) {
//                    if (!((UserMessage) message).getSender().getUserId().equals(PreferenceUtils.getUserId())) {
//                        // mNewMessageText.setText("New Message = " + ((UserMessage) message).getSender().getNickname() + " : " + ((UserMessage) message).getMessage());
//                        // mNewMessageText.setVisibility(View.VISIBLE);
//                    }
//                } else if (message instanceof FileMessage) {
//                    if (!((FileMessage) message).getSender().getUserId().equals(PreferenceUtils.getUserId())) {
//                        // mNewMessageText.setText("New Message = " + ((FileMessage) message).getSender().getNickname() + "Send a File");
//                        // mNewMessageText.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//        }
//    };
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
                                // mChatAdapter.removeFailedMessages(Collections.singletonList(message));
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
                                //  mChatAdapter.removeFailedMessages(Collections.singletonList(message));
                            }
                        }
                    }
                }).show();
    }

    public void sendFileWithThumbnail(Uri uri) {
        Log.d("datain", "1" + uri + "--");
        Uri uri2 = uri;
        ArrayList arrayList = new ArrayList();
        arrayList.add(new FileMessage.ThumbnailSize(240, 240));
        arrayList.add(new FileMessage.ThumbnailSize(320, 320));
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
            BaseChannel.SendFileMessageWithProgressHandler r11 = new BaseChannel.SendFileMessageWithProgressHandler() {
                public void onProgress(int bytesSent, int totalBytesSent, int totalBytesToSend) {
                    Log.d("datain", "6" + uri + "--");
                    if (mFileProgressHandlerMap != null) {
                        Log.d("datain", "7" + uri + "--");
                        FileMessage fileMessage = (FileMessage) mFileProgressHandlerMap.get(this);
                        if (fileMessage != null && totalBytesToSend > 0) {
                            Log.d("datain", "8" + uri + "--");
                            mChatAdapter.setFileProgressPercent(fileMessage, (totalBytesSent * 100) / totalBytesToSend);
                        }
                    }
                }

                public void onSent(FileMessage fileMessage, SendBirdException e) {
                    if (e != null) {
                        Log.e("onSent doc", String.valueOf(e));
                        try {
                            FragmentActivity activity = getActivity();
                            StringBuilder sb = new StringBuilder();
                            sb.append("");
                            sb.append(e.getCode());
                            sb.append(":");
                            sb.append(e.getMessage());
                            Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
                            mChatAdapter.markMessageFailed(fileMessage.getRequestId());
                        } catch (Exception e2) {
                        }
                        return;
                    }
                    Log.e("onSent doc", fileMessage.getName());
                    mChatAdapter.markMessageSent(fileMessage);
                }
            };
            if (this.mChannel != null) {
                BaseChannel.SendFileMessageWithProgressHandler sendFileMessageWithProgressHandler = r11;
                FileMessage tempFileMessage = this.mChannel.sendFileMessage(file, name, mime, size, "", (String) null, (List<FileMessage.ThumbnailSize>) arrayList, r11);
                this.mFileProgressHandlerMap.put(sendFileMessageWithProgressHandler, tempFileMessage);
                this.mChatAdapter.addTempFileMessageInfo(tempFileMessage, uri2);
                this.mChatAdapter.addFirst(tempFileMessage);
            }
        }
    }

    public void setTypingStatus(boolean typing) {
        if (this.mChannel != null) {
            if (typing) {
                this.mIsTyping = true;
                // this.mChannel.startTyping();
            } else {
                this.mIsTyping = false;
                // this.mChannel.endTyping();
            }
        }
    }

    private void setUpChatListAdapter() {
        mChatAdapter = new OpenChatAdapter(getActivity(), mChannelUrl);
        mChatAdapter.setItemClickListener(new OpenChatAdapter.OnItemClickListener() {
            @Override
            public void onUserMessageItemClick(UserMessage message) {
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
                if (mChatAdapter.isFailedMessage(message)) {
                    retryFailedMessage(message);
                    return;
                }

                // Message is sending. Do nothing on click event.
                if (mChatAdapter.isTempMessage(message)) {
                    return;
                }


                onFileMessageClicked(message);
            }

            @Override
            public void onAdminMessageItemClick(AdminMessage message) {

            }
        });

        mChatAdapter.setItemLongClickListener(new OpenChatAdapter.OnItemLongClickListener() {
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

    /*public void sendRecordedFile(String uri) {
        File file = new File(uri);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new FileMessage.ThumbnailSize(240, 240));
        arrayList.add(new FileMessage.ThumbnailSize(320, 320));
        if (uri.equals("")) {
            Toast.makeText(getActivity(), "File must be located in local storage.", Toast.LENGTH_LONG).show();
            return;
        }
        BaseChannel.SendFileMessageWithProgressHandler r10 = new BaseChannel.SendFileMessageWithProgressHandler() {
            public void onProgress(int bytesSent, int totalBytesSent, int totalBytesToSend) {
                FileMessage fileMessage = (FileMessage) mFileProgressHandlerMap.get(this);
                if (fileMessage != null && totalBytesToSend > 0) {
                    mChatAdapter.setFileProgressPercent(fileMessage, (totalBytesSent * 100) / totalBytesToSend);
                }
            }

            public void onSent(FileMessage fileMessage, SendBirdException e) {
                if (e != null) {
                    FragmentActivity activity = getActivity();
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    sb.append("Send Failed with an error");
                    sb.append(":");
                    sb.append(e.getMessage());
                    Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
                    // mChatAdapter.markMessageFailed(fileMessage.getRequestId());
                    return;
                }
                mChatAdapter.markMessageSent(fileMessage);
            }
        };
        if (this.mChannel != null) {
            FileMessage tempFileMessage = this.mChannel.sendFileMessage(file, "Recording", "audio/mpeg", 346923, "", (String) null, (List<FileMessage.ThumbnailSize>) arrayList, (BaseChannel.SendFileMessageWithProgressHandler) r10);
            this.mFileProgressHandlerMap.put(r10, tempFileMessage);
            this.mChatAdapter.addTempFileMessageInfo(tempFileMessage, Uri.parse(uri));
            this.mChatAdapter.addFirst(tempFileMessage);
        }
    }*/

    public void sendRecordedFile(String uri) {
        File file = new File(uri);
        ArrayList<FileMessage.ThumbnailSize> arrayList = new ArrayList<>();
        arrayList.add(new FileMessage.ThumbnailSize(240, 240));
        arrayList.add(new FileMessage.ThumbnailSize(320, 320));
        if (uri.equals("")) {
            Toast.makeText(getActivity(), "File must be located in local storage.", Toast.LENGTH_LONG).show();
            return;
        }
        BaseChannel.SendFileMessageWithProgressHandler r10 = new BaseChannel.SendFileMessageWithProgressHandler() {
            public void onProgress(int bytesSent, int totalBytesSent, int totalBytesToSend) {
                FileMessage fileMessage = (FileMessage) OpenChatFragment.this.mFileProgressHandlerMap.get(this);
                if (fileMessage != null && totalBytesToSend > 0) {
                    OpenChatFragment.this.mChatAdapter.setFileProgressPercent(fileMessage, (totalBytesSent * 100) / totalBytesToSend);
                }
            }

            public void onSent(FileMessage fileMessage, SendBirdException e) {
                if (e != null) {
                    FragmentActivity activity = OpenChatFragment.this.getActivity();
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    sb.append(e.getCode());
                    sb.append(":");
                    sb.append(e.getMessage());
                    Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
                    OpenChatFragment.this.mChatAdapter.markMessageFailed(fileMessage.getRequestId());
                    return;
                }
                OpenChatFragment.this.mChatAdapter.markMessageSent(fileMessage);
            }
        };
        if (this.mChannel != null) {
            FileMessage tempFileMessage = this.mChannel.sendFileMessage(file, "Recording", "audio/mpeg", 346923, "", (String) null, arrayList, (BaseChannel.SendFileMessageWithProgressHandler) r10);
            this.mFileProgressHandlerMap.put(r10, tempFileMessage);
            this.mChatAdapter.addTempFileMessageInfo(tempFileMessage, Uri.parse(uri));
            this.mChatAdapter.addFirst(tempFileMessage);
        }
    }

    public void requestMedia() {
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            requestStoragePermissions();
            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 19) {
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{MimeType.IMAGE_MIME, "video/*"});
        } else {
            intent.setType("image/* video/*");
        }
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Media"), 301);
        SendBird.setAutoBackgroundDetection(false);
    }


    private void initView() {
        //this.mAudioRecordButton = audio_record_button;//comment
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE_EXTERNAL_STORAGE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission granted.
                    Snackbar.make(mRootLayout, "Storage permissions granted. You can now upload or download files.",
                            Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    // Permission denied.
                    Snackbar.make(mRootLayout, "Permissions denied.",
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
            this.contactname = data.getStringExtra("contactname_key");
            this.phonenum = data.getStringExtra("phonenum_key");
            sendContact(this.contactname, this.phonenum);
        } else if (requestCode == 44 && resultCode == -1) {
            if (data == null) {
                Log.d(LOG_TAG, "data is null!");
                return;
            }
            sendUserMessage(data.getStringExtra("latlong_key"));
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
        return Uri.parse(MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null));
    }

    private void sendContact(String name, String number) {
        this.mChatAdapter.addFirst(this.mChannel.sendUserMessage(name, number, "Contact", null, new BaseChannel.SendUserMessageHandler() {
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    Log.e(OpenChatFragment.LOG_TAG, e.toString());
                    FragmentActivity activity = getActivity();
                    StringBuilder sb = new StringBuilder();
                    sb.append("Send failed with error ");
                    sb.append(e.getCode());
                    sb.append(": ");
                    sb.append(e.getMessage());
                    Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
                    // mChatAdapter.markMessageFailed(userMessage.getRequestId());
                    return;
                }
                mChatAdapter.markMessageSent(userMessage);
            }
        }));
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
        refreshFirst();


       /* ConnectionManager.addConnectionManagementHandler(CONNECTION_HANDLER_ID, new ConnectionManager.ConnectionManagementHandler() {
            @Override
            public void onConnected(boolean reconnect) {
                if (reconnect) {
                    refresh();
                } else {
                    refreshFirst();
                }
            }
        });*/

        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                // Add new message to view
                if (baseChannel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.addFirst(baseMessage);
                }
            }

            @Override
            public void onMessageDeleted(BaseChannel baseChannel, long msgId) {
                super.onMessageDeleted(baseChannel, msgId);
                if (baseChannel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.delete(msgId);
                }
            }

            @Override
            public void onMessageUpdated(BaseChannel channel, BaseMessage message) {
                super.onMessageUpdated(channel, message);
                if (channel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.update(message);
                }
            }
        });
    }


    @Override
    public void onPause() {
        //  ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID);
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mChannel != null) {
            mChannel.exit(new OpenChannel.OpenChannelExitHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }

        super.onDestroyView();
    }

//
//    private void setUpChatAdapter() {
//        mChatAdapter = new OpenChatAdapter(getActivity(),mChannelUrl);
//        mChatAdapter.setOnItemClickListener(new OpenChatAdapter.OnItemClickListener() {
//            @Override
//            public void onUserMessageItemClick(UserMessage message) {
//            }
//
//            @Override
//            public void onFileMessageItemClick(FileMessage message) {
//                onFileMessageClicked(message);
//            }
//
//            @Override
//            public void onAdminMessageItemClick(AdminMessage message) {
//            }
//        });
//
//        mChatAdapter.setOnItemLongClickListener(new OpenChatAdapter.OnItemLongClickListener() {
//            @Override
//            public void onBaseMessageLongClick(final BaseMessage message, int position) {
//                showMessageOptionsDialog(message, position);
//            }
//        });
//    }

    private void showMessageOptionsDialog(final BaseMessage message, final int position) {
        String[] options = new String[]{"Edit message", "Delete message"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setState(STATE_EDIT, message, position);
                } else if (which == 1) {
                    deleteMessage(message);
                }
            }
        });
        builder.create().show();
    }

    private void setState(int state, BaseMessage editingMessage, final int position) {
        switch (state) {
            case STATE_NORMAL:
                mCurrentState = STATE_NORMAL;
                mEditingMessage = null;

                mUploadFileButton.setVisibility(View.VISIBLE);
                mMessageSendButton.setText("SEND");
                mMessageEditText.setText("");
                break;

            case STATE_EDIT:
                mCurrentState = STATE_EDIT;
                mEditingMessage = editingMessage;

                mUploadFileButton.setVisibility(View.GONE);
                mMessageSendButton.setText("SAVE");
                String messageString = ((UserMessage) editingMessage).getMessage();
                if (messageString == null) {
                    messageString = "";
                }
                mMessageEditText.setText(messageString);
                if (messageString.length() > 0) {
                    mMessageEditText.setSelection(0, messageString.length());
                }

                mMessageEditText.requestFocus();
                mMessageEditText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIMM.showSoftInput(mMessageEditText, 0);

                        mRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.scrollToPosition(position);
                            }
                        }, 500);
                    }
                }, 100);
                break;
        }
    }

   /* @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      *//*  getActivity().setOnBackPressedListener(new OpenChannelActivity.onBackPressedListener() {
            @Override
            public boolean onBack() {
                if (mCurrentState == STATE_EDIT) {
                    setState(STATE_NORMAL, null, -1);
                    return true;
                }

                mIMM.hideSoftInputFromWindow(mMessageEditText.getWindowToken(), 0);
                return false;
            }
        });*//*
    }*/

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mChatAdapter);

        // Load more messages when user reaches the top of the current message list.
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if (mLayoutManager.findFirstVisibleItemPosition() <= 0) {
//                        mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.NEXT, null);
//                        //  mNewMessageText.setVisibility(View.GONE);
//                    }
//
//                    if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
//                        mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.PREVIOUS, null);
//                    }
//
//                }
                if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                    loadNextMessageList(CHANNEL_LIST_LIMIT);
                }
                Log.v(LOG_TAG, "onScrollStateChanged");
            }
        });
    }

    private void onFileMessageClicked(FileMessage message) {
        String type = message.getType().toLowerCase();
        if (type.startsWith("image")) {
            Intent i = new Intent(getActivity(), PhotoViewerActivity.class);
            i.putExtra("url", message.getUrl());
            i.putExtra("type", message.getType());
            startActivity(i);
        } else if (type.startsWith("video")) {
            Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
            intent.putExtra("url", message.getUrl());
            startActivity(intent);
        } else {
            showDownloadConfirmDialog(message);
        }
    }

    private void showDownloadConfirmDialog(final FileMessage message) {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Download file?")
                    .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                // FileUtils.downloadFile(getActivity(), message.getUrl(), message.getName());
                                onStartFileDownloadListener.onStartFileDownload(message);
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.onStartFileDownloadListener = (OnStartFileDownloadListener) context;

    }

    private void showUploadConfirmDialog(final Uri uri) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Upload file?")
                .setPositiveButton(R.string.upload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {

                            // Specify two dimensions of thumbnails to generate
                            List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
                            thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
                            thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

                            sendImageWithThumbnail(uri, thumbnailSizes);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null).show();
    }

    private void requestImage() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("image/* video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Media"), INTENT_REQUEST_CHOOSE_IMAGE);

            // Set this as false to maintain connection
            // even when an external Activity is started.
            SendBird.setAutoBackgroundDetection(false);
        }
    }

    @SuppressLint("NewApi")
    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(mRootLayout, "Storage access permissions are required to upload/download files.",
                    Snackbar.LENGTH_LONG)
                    .setAction("Okay", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            // Permission has not been granted yet. Request it directly.
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void refreshFirst() {
        enterChannel(mChannelUrl);
    }

    /**
     * Enters an Open Channel.
     * <p>
     * A user must successfully enter a channel before being able to load or send messages
     * within the channel.
     *
     * @param channelUrl The URL of the channel to enter.
     */
    private void enterChannel(String channelUrl) {
        OpenChannel.getChannel(channelUrl, new OpenChannel.OpenChannelGetHandler() {
            @Override
            public void onResult(final OpenChannel openChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    return;
                }

                // Enter the channel
                openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {
                            // Error!
                            e.printStackTrace();
                            return;
                        }

                        mChannel = openChannel;

                        if (getActivity() != null) {
                            // Set action bar title to name of channel
                            // ((Main3Activity) getActivity()).setActionBarTitle(mChannel.getName());
                            ((Main3Activity) getActivity()).setActionBarTitle(mChannel.getName());
                            //Toast.makeText(getContext(), ""+mChannel.getName(), Toast.LENGTH_SHORT).show();
                            tv_chatperson_name.setText(mChannel.getName());


                        }

                        refresh();
                    }
                });
            }
        });
    }

    private void sendUserMessage(String text) {
        mChannel.sendUserMessage(text, new BaseChannel.SendUserMessageHandler() {
            @Override
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    // Error!
                    Log.e(LOG_TAG, e.toString());
                    Toast.makeText(
                            getActivity(),
                            "Send failed with error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                // Display sent message to RecyclerView
                mChatAdapter.addFirst(userMessage);
            }
        });
    }

    /**
     * Sends a File Message containing an image file.
     * Also requests thumbnails to be generated in specified sizes.
     *
     * @param uri The URI of the image, which in this case is received through an Intent request.
     */
    private void sendImageWithThumbnail(Uri uri, List<FileMessage.ThumbnailSize> thumbnailSizes) {
        Hashtable<String, Object> info = FileUtils.getFileInfo(getActivity(), uri);
        final String path = (String) info.get("path");
        final File file = new File(path);
        final String name = file.getName();
        final String mime = (String) info.get("mime");
        final int size = (Integer) info.get("size");

        if (path.equals("")) {
            Toast.makeText(getActivity(), "File must be located in local storage.", Toast.LENGTH_LONG).show();
        } else {
            // Send image with thumbnails in the specified dimensions
            mChannel.sendFileMessage(file, name, mime, size, "", null, thumbnailSizes, new BaseChannel.SendFileMessageHandler() {
                @Override
                public void onSent(FileMessage fileMessage, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mChatAdapter.addFirst(fileMessage);
                }
            });
        }
    }

    private void refresh() {
        if (this.mChannel == null) {
            OpenChannel.getChannel(mChannelUrl, new OpenChannel.OpenChannelGetHandler() {
                @Override
                public void onResult(OpenChannel openChannel, SendBirdException e) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    mChannel = openChannel;

                    OpenChatFragment.this.mChatAdapter.setChannel(OpenChatFragment.this.mChannel);
                    OpenChatFragment.this.mChatAdapter.loadLatestMessages(30, new BaseChannel.GetMessagesHandler() {
                        public void onResult(List<BaseMessage> list, SendBirdException e) {

                            OpenChatFragment.this.messagelist = list;
                            //OpenChatFragment.this.mChatAdapter.markAllMessagesAsRead();
                            OpenChatFragment.this.mChatAdapter.notifyDataSetChanged();
                        }
                    });
                    try {

                        tv_chatperson_name.setText(mChannel.getName());


                        // OpenChatFragment.this.chat_personimg.setImageResource(R.drawable.ic_logo_pink);
                    } catch (Exception e2) {
                    }

                    // Op
                }
            });


        }

    }

    /**
     * Replaces current message list with new list.
     * Should be used only on initial load.
     */
    private void loadInitialMessageList(int numMessages) {

        mPrevMessageListQuery = mChannel.createPreviousMessageListQuery();
        mPrevMessageListQuery.load(numMessages, true, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e) {
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    return;
                }

                mChatAdapter.setMessageList(list);
            }
        });

    }

    /**
     * Loads messages and adds them to current message list.
     * <p>
     * A PreviousMessageListQuery must have been already initialized through {@link #loadInitialMessageList(int)}
     */
    private void loadNextMessageList(int numMessages) throws NullPointerException {

        if (mChannel == null) {
            Log.d("OpenChatFragment", "No more Channel ");
        }


//        mPrevMessageListQuery.load(numMessages, true, new PreviousMessageListQuery.MessageListQueryResult() {
//            @Override
//            public void onResult(List<BaseMessage> list, SendBirdException e) {
//                if (e != null) {
//                    // Error!
//                    e.printStackTrace();
//                    return;
//                }
//
//                for (BaseMessage message : list) {
//                    mChatAdapter.addLast((message));
//                }
//            }
//        });

        OpenChatFragment.this.mChatAdapter.loadLatestMessages(numMessages, new BaseChannel.GetMessagesHandler() {
            public void onResult(List<BaseMessage> list, SendBirdException e) {

                OpenChatFragment.this.messagelist = list;
                //OpenChatFragment.this.mChatAdapter.markAllMessagesAsRead();
                OpenChatFragment.this.mChatAdapter.notifyDataSetChanged();
            }
        });
    }

    private void editMessage(final BaseMessage message, String editedMessage) {
        mChannel.updateUserMessage(message.getMessageId(), editedMessage, null, null, new BaseChannel.UpdateUserMessageHandler() {
            @Override
            public void onUpdated(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    // Error!
                    Toast.makeText(getActivity(), "Error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                refresh();
            }
        });
    }

    /**
     * Deletes a message within the channel.
     * Note that users can only delete messages sent by oneself.
     *
     * @param message The message to delete.
     */
    private void deleteMessage(final BaseMessage message) {
        mChannel.deleteMessage(message, new BaseChannel.DeleteMessageHandler() {
            @Override
            public void onResult(SendBirdException e) {
                if (e != null) {
                    // Error!
                    Toast.makeText(getActivity(), "Error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                refresh();
            }
        });
    }

    public void notifyAdapter() {
        this.mChatAdapter.notifyDataSetChanged();
    }

}

