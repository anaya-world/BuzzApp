package com.example.myapplication.Activities;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.GroupChats.GroupChatFragment;
import com.example.myapplication.Intefaces.OnBackPressedListener;
import com.example.myapplication.Intefaces.OnStartFileDownloadListener;
import com.example.myapplication.R;
import com.example.myapplication.Services.CheckOnlineStatusService;
import com.example.myapplication.Utils.Common;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.stfalcon.multiimageview.MultiImageView;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.example.myapplication.GroupChats.GroupChatFragment.REQUEST_TYPE_MENU_UNFRIEND;
import static com.example.myapplication.Utils.Common.showAlertOkay;

public class Main2Activity extends BaseActivity implements OnStartFileDownloadListener {
    public static ImageView chat_backarrow;
    public static MultiImageView chat_personimg;
    public static String contactname;
    public static GroupChannel mChannel;
    public static String phonenum;
    public static TextView tv_chatperson_names;
    public static TextView tv_groupname;
    public static ImageView tv_onlinetime;
    //public static ImageView iv_chatcall;
    String channelUrl;
    boolean isNewChannel = false;
    String strAdminChannelMessage = "";
    private String happy1;
    private String gif;
    private String anniversary1;
    String gifurl;
    /* access modifiers changed from: private */
    public long downloadID;
    private Menu menuInstance;
    DownloadManager downloadManager;
    /* access modifiers changed from: private */
    public GroupChatFragment fragment;
    public static Intent CheckServiceStatusIntent = null;
    protected OnBackPressedListener onBackPressedListener;
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra("extra_download_id", -1);
            if ("android.intent.action.DOWNLOAD_COMPLETE".equals(intent.getAction())) {
                if (Main2Activity.this.progressDialog != null && Main2Activity.this.progressDialog.isShowing()) {
                    Main2Activity.this.progressDialog.dismiss();
                }
                if (Main2Activity.this.downloadID == id) {
                    Toast.makeText(Main2Activity.this, "File is downloaded.", Toast.LENGTH_SHORT).show();
                    if (Main2Activity.this.fragment != null) {
                        Main2Activity.this.fragment.notifyAdapter();
                    }
                }
            }
        }
    };


    private BroadcastReceiver onUpdateOnlineStatus = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String strConnectionStatus = intent.getStringExtra("connection_status");
            if (fragment != null) {
                fragment.updateOnlineStatus(Objects.requireNonNull(strConnectionStatus));
            }
        }
    };

    /* access modifiers changed from: private */
    public ProgressDialog progressDialog;
    //
    // private SharedPrefsHelper sharedPrefsHelper;

    //    public static void start(Context context, boolean isRunForCall) {
//        Intent intent = new Intent(context, OpponentsActivity.class);
//        // intent.addFlags(268566528);
//        intent.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, isRunForCall);
//        context.startActivity(intent);
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.d("Main2Activity", "onCreate");
        registerReceiver(this.onDownloadComplete, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
        registerReceiver(this.onUpdateOnlineStatus, new IntentFilter(CheckOnlineStatusService.ACTION_USER_ONLINE_UPDATE));
//        PreferenceUtils.init(Main2Activity.this);
//        SyncManagerUtils.setup(Main2Activity.this, SharedPrefManager.getInstance(Main2Activity.this).getUser().getUser_id(), new CompletionHandler() {
//            @Override
//            public void onCompleted(SendBirdException e) {
//                Log.d("SyncManager","abc 4");
//                if (e != null) {
//                    e.printStackTrace();
//                    return;
//                }
//                PreferenceUtils.setConnected(true);
//            }
//        });
        Intent intent = getIntent();
        this.channelUrl = getIntent().getStringExtra("URL");
        this.isNewChannel = getIntent().getBooleanExtra("EXTRA_NEW_CHANNEL", false);
        this.strAdminChannelMessage = getIntent().getStringExtra("EXTRA_NEW_CHANNEL_MESSAGE");
        this.happy1 = getIntent().getStringExtra("happy");
        Intent intent1 = getIntent();
        this.anniversary1 = intent1.getStringExtra("anniversary");
        this.gif = intent.getStringExtra("gif");
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setProgressStyle(0);
        //    this.progressDialog.setMessage("File is downloading, Please wait...");  comment
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setCancelable(true);
        this.fragment = GroupChatFragment.newInstance(this.channelUrl, isNewChannel, strAdminChannelMessage);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_group_channel, this.fragment).addToBackStack(null).commit();
        tv_onlinetime = (ImageView) findViewById(R.id.tv_onlinetime);
        chat_backarrow = (ImageView) findViewById(R.id.chat_backarrow);
        chat_personimg = (MultiImageView) findViewById(R.id.chat_personimg);
        //iv_chatcall = (ImageView)findViewById(R.id.iv_chatcall);
        tv_chatperson_names = (TextView) findViewById(R.id.tv_chatperson_name);
        // = (TextView) findViewById(R.id.tv_groupname);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_group_channel));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        chat_backarrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Main2Activity.this.finish();
            }
        });
        // connect();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater minf = getMenuInflater();
        this.menuInstance = menu;
        minf.inflate(R.menu.more_chatopen, menu);
        updateMenuItems(REQUEST_TYPE_MENU_UNFRIEND, this.iMemberSize);
        return super.onCreateOptionsMenu(menu);
    }

   /* @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menuInstance = menu;

        invalidateOptionsMenu();
        return true;
    }*/

    public void updateMenuItems(int requestType, int size) {
        switch (requestType) {
            case REQUEST_TYPE_MENU_UNFRIEND:
                if (menuInstance != null) {
                    if (size <= 2) {
                        menuInstance.findItem(R.id.iv_chat_call).setVisible(true);
                        menuInstance.findItem(R.id.menu_mute_unmute).setVisible(true);
                        menuInstance.findItem(R.id.menu_follow_unfollow).setVisible(true);
                        menuInstance.findItem(R.id.menu_block).setVisible(true);
                        menuInstance.findItem(R.id.menu_remove_unfriend).setVisible(false);//true
                        menuInstance.findItem(R.id.menu_email_chat).setVisible(true);
                        menuInstance.findItem(R.id.menu_delete_chat).setVisible(true);
                        menuInstance.findItem(R.id.menu_leave_group).setVisible(false);

                        String strLoginUserId = String.valueOf(SharedPrefManager.getInstance(Main2Activity.this).
                                getUser().getUser_id()).replaceAll("\\s", "");
                        if (mChannel != null) {
                            List<Member> memberList = mChannel.getMembers();
                            for (int i = 0; i < memberList.size(); i++) {
                                try {
                                    if (!memberList.get(i).getUserId().equals(strLoginUserId)) {
                                       // String userId2 =memberList.get(i).getUserId();
                                            if(memberList.get(i).isBlockedByMe()){
                                                menuInstance.findItem(R.id.menu_block).setTitle(getString(R.string.menu_unblock));
                                            } else {
                                                menuInstance.findItem(R.id.menu_block).setTitle(getString(R.string.menu_block));
                                            }
                                    }
                                } catch (Exception e) {
                                    Common.showToast(Main2Activity.this, "Failed!");
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        menuInstance.findItem(R.id.menu_block).setVisible(false);
                        menuInstance.findItem(R.id.iv_chat_call).setVisible(false);
                        menuInstance.findItem(R.id.menu_mute_unmute).setVisible(true);
                        menuInstance.findItem(R.id.menu_follow_unfollow).setVisible(false);
                        menuInstance.findItem(R.id.menu_remove_unfriend).setVisible(false);
                        menuInstance.findItem(R.id.menu_email_chat).setVisible(false);
                        menuInstance.findItem(R.id.menu_leave_group).setVisible(true);
                        String strMutedState = mChannel.getMyMutedState().toString();
                        if (strMutedState.equals(getString(R.string.sendbird_key_state_mute))) {
                            menuInstance.findItem(R.id.menu_mute_unmute).setTitle(getString(R.string.menu_unmute));
                        } else {
                            menuInstance.findItem(R.id.menu_mute_unmute).setTitle(getString(R.string.menu_mute));
                        }
                    }
                }
                //invalidateOptionsMenu();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.iv_chat_call:
                if (fragment != null) {
                    fragment.startCall(false);
                }
                break;

            case R.id.menu_mute_unmute:
                updateMuteUnmuteService();
                break;

            case R.id.menu_delete_chat:
                if (mChannel != null) {
                    deleteChat();
                }
                break;

            case R.id.menu_leave_group:
                if (mChannel != null) {
                    fragment.leaveChannel(mChannel);
                }
                break;

            case R.id.menu_remove_unfriend:
                fragment.removeFriend();
                break;

            case R.id.menu_block:
                fragment.blockUser();
                break;
        }
        return true;
    }

    AlertDialog alertDialog = null;

    private void deleteChat() {
        mChannel.resetMyHistory(new GroupChannel.GroupChannelResetMyHistoryHandler() {
            public void onResult(SendBirdException e) {
                if (e != null) {
                    Common.showToast(Main2Activity.this, "Unable to delete chat.");
                    return;
                }

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main2Activity.this);
                alertDialogBuilder.setMessage("Chat deleted successfully.");
                alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                        alertDialog.dismiss();
                    }
                });
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                /*Common.showToast(Main2Activity.this, "Chat deleted successfully.");*/
//                        GroupChatFragment.this.sharedPrefsHelper.saveNull(GroupChatFragment.this.mChannelUrl, null);
//                        GroupChatFragment.this.mChatAdapter.load(GroupChatFragment.this.mChannelUrl);
            }
        });
    }

    private void updateMuteUnmuteService() {
        User user = null;
        String strUserID = SharedPrefManager.getInstance(Main2Activity.this).getUser().getUser_id();
        if (mChannel != null && strUserID != null) {
            String strMutedState = mChannel.getMyMutedState().toString();
            if (strMutedState.equals(getString(R.string.sendbird_key_state_unmute))) { //Mute State enable
                mChannel.muteUserWithUserId(strUserID, "", -1, new GroupChannel.GroupChannelMuteHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {    // Error.
                            return;
                        }
                        invalidateOptionsMenu();
                        String strMessage = mChannel.getName() + " muted successfully.";
                        showAlertOkay(Main2Activity.this, "",
                                strMessage, getString(android.R.string.ok));
                    }
                });
            } else { //Un-mute State enable here.
                mChannel.unmuteUserWithUserId(strUserID, new GroupChannel.GroupChannelUnmuteHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {    // Error.
                            return;
                        }
                        invalidateOptionsMenu();
                        String strMessage = mChannel.getName() + " unmuted successfully.";
                        showAlertOkay(Main2Activity.this, "",
                                strMessage, getString(android.R.string.ok));
                    }
                });
            }

           /* mChannel.unmuteUserWithUserId(strUserID, new GroupChannel.GroupChannelUnmuteHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {    // Error.
                        return;
                    }
                }
            });*/
        }
       /* GroupChannel.getChannel(mChannel, new GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {    // Error.
                    return;
                }
                groupChannel.getMembers().get(0).getUserId();
                // Muting and unmuting a user
                groupChannel.muteUser(user, new GroupChannel.GroupChannelMuteHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {    // Error.
                            return;
                        }

                        // TODO: Custom implementation for what should be done after muting.
                    }
                });

                groupChannel.unmuteUser(USER, new GroupChannel.GroupChannelUnmuteHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {    // Error.
                            return;
                        }

                        // TODO: Custom implementation for what should be done after unmuting.
                    }
                });
            }
        });*/
    }

    int iMemberSize;

    public int getiMemberSize() {
        return iMemberSize;
    }

    public void setiMemberSize(int iMemberSize) {
        this.iMemberSize = iMemberSize;
    }

    private void connect() {
        Log.d("Main2Activity", "connect");
//        SendBird.connect(SharedPrefManager.getInstance(Main2Activity.this).getUser().getCaller_id(), new SendBird.ConnectHandler() {
//            @Override
//            public void onConnected(User user, SendBirdException e) {
//                if (e != null) {
//                   // Toast.makeText(context, context.getString(R.string.sendbird_error_with_code, e.getCode(), e.getMessage()), Toast.LENGTH_SHORT).show();
//
//                }
//                Log.d("Main2Activity","connect1"+e);
//                SyncManagerUtils.setup(Main2Activity.this, SharedPrefManager.getInstance(Main2Activity.this).getUser().getUser_id(), new CompletionHandler() {
//                    @Override
//                    public void onCompleted(SendBirdException e) {
//                        Log.d("Main2Activity","connect2"+e);
//                        SendBirdSyncManager.getInstance().resumeSync();
//                    }
//                });
//
//                PushUtils.registerPushTokenForCurrentUser();
//
//            }
//        });
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener2) {
        this.onBackPressedListener = onBackPressedListener2;
    }

    public void onBackPressed() {
        if (this.onBackPressedListener != null) {
            this.onBackPressedListener.doBack();
        } else {
            super.onBackPressed();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.onDownloadComplete);
        unregisterReceiver(this.onUpdateOnlineStatus);
        if (Main2Activity.CheckServiceStatusIntent != null) {
            stopService(Main2Activity.CheckServiceStatusIntent);
        }
    }

    @RequiresApi(api = 24)
    public void onStartFileDownload(FileMessage fileMessage) {
        beginDownload(fileMessage);
    }

    @RequiresApi(api = 24)
    private void beginDownload(FileMessage fileMessage) {
        String filename = fileMessage.getName();
        String downloadUrlOfImage = fileMessage.getUrl();
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        sb.append("/BuzzApp/Images");
        File directImage = new File(sb.toString());
        Log.d("lifecycle", "beginDownload" + directImage.exists());
        if (!directImage.exists()) {
            Log.d("lifecycle", "beginDownload-1" + directImage.exists());
            directImage.mkdirs();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(Environment.getExternalStorageDirectory());
        sb2.append("/BuzzApp/Videos");
        File directVideo = new File(sb2.toString());
        if (!directVideo.exists()) {
            directVideo.mkdirs();
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(Environment.getExternalStorageDirectory());
        sb3.append("/BuzzApp/Documents");
        File directDocuments = new File(sb3.toString());
        if (!directDocuments.exists()) {
            directDocuments.mkdirs();
        }
        StringBuilder sb4 = new StringBuilder();
        sb4.append(Environment.getExternalStorageDirectory());
        sb4.append("/BuzzApp/Audio");
        File directAudio = new File(sb4.toString());
        if (!directAudio.exists()) {
            directAudio.mkdirs();
        }
        if (checkForFileToDownload(fileMessage)) {
//            if (fileMessage.getType().startsWith(QBAttachment.IMAGE_TYPE)) {
//                Intent i = new Intent(this, PhotoViewerActivity.class);
//                i.putExtra("url", fileMessage.getUrl());
//                i.putExtra("type", fileMessage.getType());
//                startActivity(i);
//            } else if (fileMessage.getType().startsWith(QBAttachment.VIDEO_TYPE)) {
//                Intent intent = new Intent(this, VideoViewActivity.class);
//                StringBuilder sb5 = new StringBuilder();
//                sb5.append(Environment.getExternalStorageDirectory());
//                sb5.append("/BuzzApp/Videos/");
//                sb5.append(fileMessage.getName());
//                intent.putExtra("path", sb5.toString());
//                startActivity(intent);
//            }
        } else if (Common.isConnected(this)) {
            this.progressDialog.show();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrlOfImage));
            request.setAllowedOverRoaming(true).setAllowedOverMetered(true).setTitle(filename).setMimeType("*/*");
            String pathInternalStorage = Environment.getExternalStorageDirectory().toString();
            if (fileMessage.getType().toLowerCase().startsWith("image")) {
                request.setDestinationInExternalPublicDir(pathInternalStorage + "/BuzzApp/Images", filename);
            } else if (fileMessage.getType().toLowerCase().startsWith("video")) {
                request.setDestinationInExternalPublicDir(pathInternalStorage + "/BuzzApp/Videos", filename + ".mp4");
            } else if (fileMessage.getType().toLowerCase().startsWith("audio")) {
                request.setDestinationInExternalPublicDir(pathInternalStorage + "/BuzzApp/Audio", filename + ".mp3");
                /*request.setDestinationInExternalPublicDir(pathInternalStorage
                        + File.separator + "BuzzApp" + File.separator + "Audio/", filename + ".mp3");*/
//                request.setDestinationInExternalPublicDir(
//                        pathInternalStorage
//                                + File.separator + "BuzzApp" + File.separator + "Audio/", filename + ".mp3");
            } else if (fileMessage.getType().toLowerCase().startsWith("application/")) {
                request.setDestinationInExternalPublicDir(pathInternalStorage + "/BuzzApp/Documents", filename);
            } else {
                request.setDestinationInExternalPublicDir(pathInternalStorage + "/BuzzApp", filename);
            }
            this.downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            this.downloadID = this.downloadManager.enqueue(request);
        }
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
                Toast.makeText(this, "This audio is already downloaded.", Toast.LENGTH_SHORT).show();
                return true;
            } else if (fileMessage.getType().toLowerCase().startsWith("application/")) {
                if (!checkToDownload(filename, "/BuzzApp/Documents")) {
                    return false;
                }
                Toast.makeText(this, "This doc is already downloaded.", Toast.LENGTH_SHORT).show();
                return true;
            } else if (!checkToDownload(filename, "/BuzzApp")) {
                return false;
            } else {
                Toast.makeText(this, "This file is already downloaded.", Toast.LENGTH_SHORT).show();
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

    public String getMyData() {
        return happy1;
    }

    public String getAnniversary() {
        return anniversary1;

    }

    public String getGiff() {
        return gif;
    }

    public static GroupChannel getmChannel() {
        return mChannel;
    }

    public static void setmChannel(GroupChannel mChannel) {
        Main2Activity.mChannel = mChannel;
    }
}
