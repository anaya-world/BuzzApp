package com.example.myapplication.OpenChats;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Activities.UserNavgation;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.GroupChats.GroupParticipantAdapter;
import com.example.myapplication.GroupChats.InviteMemberActivity;
import com.example.myapplication.Intefaces.BlockUserInterface;
import com.example.myapplication.R;
import com.example.myapplication.Services.MyFirebaseMessagingService;
import com.example.myapplication.Utils.FileUtils;
import com.example.myapplication.Utils.MimeType;
import com.example.myapplication.Utils.SharedPrefManager;
import com.google.android.material.snackbar.Snackbar;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBirdPushHelper;
import com.sendbird.android.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class OpenParticipantActivity extends AppCompatActivity {
    static final String EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL";
    public static final String EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL";
    public static final int INTENT_REQUEST_CHOOSE_MEDIA = 301;
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    FrameLayout attachment_container;
    ImageView bg_image;
    ImageView deletechat_group;
    EditText et_group_fullname;
    OpenParticipantAdapter groupParticipantAdapter;
    ImageView group_image;
    RecyclerView open_member_list;
    RelativeLayout groupheader_layout;
    ImageView iv_add_more_members;
    ImageView iv_back_to_groupchat;
    ImageView iv_camera;
    ImageView iv_edit_fullname;
    ImageView iv_gallery;
    //    ImageView iv_leave_group;
    ImageView iv_mute_notification;
    ImageView iv_unmute_notification;
    /* access modifiers changed from: private */
    public GroupChannel mChannel;
    String mChannelUrl;
    String mCurrentPhotoPath;
    RelativeLayout mRootLayout;
    int membercount;
    boolean isAdminLogin = false;
    Uri prfileuri;
    String profile_url;
    boolean pushCurrentlyEnabled;
    TextView tv_add_more_member;
    TextView tv_admin;
    TextView tv_dlete_group;
    TextView tv_group_fullname;
    TextView tv_group_members_count;
    RelativeLayout rlAddNewMemberView;

    private Dialog threeBtnDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_participant);
        this.rlAddNewMemberView = findViewById(R.id.rl_add_new_member_view);
        this.open_member_list = (RecyclerView) findViewById(R.id.open_member_list);
        this.iv_back_to_groupchat = (ImageView) findViewById(R.id.iv_back_to_open_chat);
        this.tv_add_more_member = (TextView) findViewById(R.id.tv_add_more_member);
        this.attachment_container = (FrameLayout) findViewById(R.id.attachment_container);
        this.tv_group_fullname = (TextView) findViewById(R.id.tv_open_fullname);
        this.tv_admin = (TextView) findViewById(R.id.tv_admin);
        this.tv_dlete_group = (TextView) findViewById(R.id.tv_delete_open);
        this.iv_add_more_members = (ImageView) findViewById(R.id.iv_add_more_members);
        this.iv_camera = (ImageView) findViewById(R.id.iv_camera);
        this.iv_gallery = (ImageView) findViewById(R.id.iv_gallery);
        //  this.iv_leave_group = (ImageView) findViewById(R.id.iv_leave_group);
        this.deletechat_group = (ImageView) findViewById(R.id.deletechat_open);
        this.group_image = (ImageView) findViewById(R.id.open_image);
        this.iv_edit_fullname = (ImageView) findViewById(R.id.iv_edit_fullname);
        this.iv_mute_notification = (ImageView) findViewById(R.id.iv_mute_notification);
        this.iv_unmute_notification = (ImageView) findViewById(R.id.iv_unmute_notification);
        this.mRootLayout = (RelativeLayout) findViewById(R.id.open_header_layout);
        this.et_group_fullname = (EditText) findViewById(R.id.et_open_fullname);
        this.tv_group_fullname.setText(getIntent().getStringExtra("groupname_key"));
        this.deletechat_group.setVisibility(View.VISIBLE);
        this.tv_dlete_group.setVisibility(View.VISIBLE);
        this.iv_mute_notification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                OpenParticipantActivity.this.setChannelPushPreferences(OpenParticipantActivity.this.mChannel, !OpenParticipantActivity.this.mChannel.isPushEnabled());
                Toast.makeText(OpenParticipantActivity.this, "Notification off", Toast.LENGTH_SHORT).show();
                OpenParticipantActivity.this.iv_mute_notification.setVisibility(View.GONE);
                OpenParticipantActivity.this.iv_unmute_notification.setVisibility(View.VISIBLE);
            }
        });
        this.iv_unmute_notification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                OpenParticipantActivity.this.setChannelPushPreferences(OpenParticipantActivity.this.mChannel, OpenParticipantActivity.this.pushCurrentlyEnabled);
                Toast.makeText(OpenParticipantActivity.this, "Notification on", Toast.LENGTH_SHORT).show();
                OpenParticipantActivity.this.iv_unmute_notification.setVisibility(View.GONE);
                OpenParticipantActivity.this.iv_mute_notification.setVisibility(View.VISIBLE);
            }
        });
        this.deletechat_group.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                OpenParticipantActivity.this.showChanneldeleteDialog(OpenParticipantActivity.this.mChannel);
            }
        });
        this.tv_dlete_group.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                OpenParticipantActivity.this.showChanneldeleteDialog(OpenParticipantActivity.this.mChannel);
            }
        });
        this.iv_edit_fullname.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                OpenParticipantActivity.this.et_group_fullname.setVisibility(View.VISIBLE);
                final Button btn_save = (Button) OpenParticipantActivity.this.findViewById(R.id.btn_save);
                btn_save.setVisibility(View.VISIBLE);
                OpenParticipantActivity.this.tv_group_fullname.setVisibility(View.GONE);
                OpenParticipantActivity.this.iv_edit_fullname.setVisibility(View.GONE);
                btn_save.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        OpenParticipantActivity.this.updategroupname();
                        OpenParticipantActivity.this.et_group_fullname.setVisibility(View.GONE);
                        OpenParticipantActivity.this.tv_group_fullname.setVisibility(View.VISIBLE);
                        OpenParticipantActivity.this.tv_group_fullname.setText(OpenParticipantActivity.this.et_group_fullname.getText().toString());
                        btn_save.setVisibility(View.GONE);
                        OpenParticipantActivity.this.iv_edit_fullname.setVisibility(View.GONE);
                    }
                });
            }
        });
        this.group_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                OpenParticipantActivity.this.attachment_container.setVisibility(View.VISIBLE);
            }
        });
        this.iv_camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(OpenParticipantActivity.this, "android.permission.CAMERA") == -1) {
                    ActivityCompat.requestPermissions(OpenParticipantActivity.this, new String[]{"android.permission.CAMERA"}, 1);
                    return;
                }
                OpenParticipantActivity.this.attachment_container.setVisibility(View.GONE);
                OpenParticipantActivity.this.dispatchTakePictureIntent();
            }
        });
        this.iv_gallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                OpenParticipantActivity.this.requestMedia();
                OpenParticipantActivity.this.attachment_container.setVisibility(View.GONE);
            }
        });
       /* this.iv_leave_group.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                GroupParticipantActivity.this.showChannelOptionsDialog(GroupParticipantActivity.this.mChannel);
            }
        });*/
        this.tv_add_more_member.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(OpenParticipantActivity.this, InviteMemberActivity.class);
                intent.putExtra("EXTRA_CHANNEL_URL", OpenParticipantActivity.this.mChannelUrl);
                OpenParticipantActivity.this.startActivity(intent);
            }
        });
        this.rlAddNewMemberView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(OpenParticipantActivity.this, InviteMemberActivity.class);
                intent.putExtra("EXTRA_CHANNEL_URL", OpenParticipantActivity.this.mChannelUrl);
                OpenParticipantActivity.this.startActivity(intent);
            }
        });
        this.iv_back_to_groupchat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                OpenParticipantActivity.this.finish();
            }
        });
        this.mChannelUrl = getIntent().getStringExtra("members_key");
        refresh();
    }

    public void refresh() {
        if (this.mChannel == null) {
            GroupChannel.getChannel(this.mChannelUrl, new GroupChannel.GroupChannelGetHandler() {
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    OpenParticipantActivity.this.mChannel = groupChannel;
                    OpenParticipantActivity.this.tv_admin.setText(OpenParticipantActivity.this.mChannel.getData().toString());
                    final List<Member> member = OpenParticipantActivity.this.mChannel.getMembers();
                    OpenParticipantActivity.this.open_member_list.setHasFixedSize(true);
                    OpenParticipantActivity.this.membercount = member.size();
                    OpenParticipantActivity.this.tv_group_members_count = (TextView) OpenParticipantActivity.this.findViewById(R.id.tv_group_members_count);
                    OpenParticipantActivity.this.tv_group_members_count.setText(String.valueOf(OpenParticipantActivity.this.membercount));
                    OpenParticipantActivity.this.bg_image = (ImageView) OpenParticipantActivity.this.findViewById(R.id.bg_image);

                    String loggedInUserId = null;
                    if (SharedPrefManager.getInstance(OpenParticipantActivity.this).isLoggedIn()) {
                        loggedInUserId = String.valueOf(SharedPrefManager
                                .getInstance(OpenParticipantActivity.this).getUser()
                                .getUser_id()).replaceAll("\\s", "");

                        for (Member memberInfo : member) {
                            if (memberInfo.getRole().getValue().toLowerCase().equals("operator")
                                    && loggedInUserId.equalsIgnoreCase(memberInfo.getUserId())) {
                                isAdminLogin = true;
                            }
                        }
                    }

                    if (isAdminLogin) {
                        updateAdminView();
                    } else {
                        updateNonAdminView();
                    }

                    Glide.with((FragmentActivity) OpenParticipantActivity.this).load(OpenParticipantActivity.this.mChannel.getCoverUrl()).into(OpenParticipantActivity.this.bg_image);
                    OpenParticipantActivity.this.groupParticipantAdapter = new OpenParticipantAdapter(
                            member, isAdminLogin, loggedInUserId, OpenParticipantActivity.this, new BlockUserInterface() {
                        public void onBlock(View view, int position) {
                            OpenParticipantActivity.this.mChannel.banUserWithUserId(((Member) member.get(position)).getUserId(), "BAD USER", 8888888, new GroupChannel.GroupChannelBanHandler() {
                                public void onResult(SendBirdException e) {
                                }
                            });
                            Toast.makeText(OpenParticipantActivity.this, "Member is removed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    OpenParticipantActivity.this.open_member_list.setLayoutManager(new LinearLayoutManager(OpenParticipantActivity.this, 1, false));
                    OpenParticipantActivity.this.open_member_list.setAdapter(OpenParticipantActivity.this.groupParticipantAdapter);
                    OpenParticipantActivity.this.pushCurrentlyEnabled = OpenParticipantActivity.this.mChannel.isPushEnabled();
                    if (!OpenParticipantActivity.this.pushCurrentlyEnabled) {
                        OpenParticipantActivity.this.iv_unmute_notification.setVisibility(View.VISIBLE);
                        OpenParticipantActivity.this.iv_mute_notification.setVisibility(View.GONE);
                    } else {
                        OpenParticipantActivity.this.iv_unmute_notification.setVisibility(View.GONE);
                        OpenParticipantActivity.this.iv_mute_notification.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            this.mChannel.refresh(new GroupChannel.GroupChannelRefreshHandler() {
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void updateNonAdminView() {
        this.rlAddNewMemberView.setVisibility(View.GONE);
        this.group_image.setVisibility(View.GONE);
        this.iv_edit_fullname.setVisibility(View.GONE);
    }

    private void updateAdminView() {
        this.rlAddNewMemberView.setVisibility(View.VISIBLE);
        this.group_image.setVisibility(View.VISIBLE);
        this.iv_edit_fullname.setVisibility(View.VISIBLE);
    }

    /* access modifiers changed from: private */
    public void leaveChannel(GroupChannel channel) {
        channel.leave(new GroupChannel.GroupChannelLeaveHandler() {
            public void onResult(SendBirdException e) {
                if (e == null) {
                    OpenParticipantActivity.this.refresh();
                }
            }
        });
        finish();
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            Snackbar.make((View) this.mRootLayout, (CharSequence) "Storage access permissions are required to upload/download files.", Snackbar.LENGTH_LONG).setAction((CharSequence) "Okay", (View.OnClickListener) new View.OnClickListener() {
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        OpenParticipantActivity.this.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 13);
                    }
                }
            }).show();
        } else if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 13);
        }
    }

    /* access modifiers changed from: private */
    public void requestMedia() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
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

    private File createImageFile() throws IOException {
        String timeStamp = null;
        if (Build.VERSION.SDK_INT >= 24) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("JPEG_");
        sb.append(timeStamp);
        sb.append("_");
        File image = File.createTempFile(sb.toString(), ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        this.mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /* access modifiers changed from: private */
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".filepicker.provider", photoFile);
                this.prfileuri = photoURI;
                takePictureIntent.putExtra("output", photoURI);
                startActivityForResult(takePictureIntent, 100);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == -1) {
            setPic();
        } else if (requestCode == 301 && resultCode == -1 && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                this.profile_url = Base64.encodeToString(baos.toByteArray(), 0);
                this.prfileuri = data.getData();
                Hashtable<String, Object> info = FileUtils.getFileInfo(this, this.prfileuri);
                if (info == null) {
                    Toast.makeText(this, "Extracting file information failed.", Toast.LENGTH_LONG).show();
                } else {
                    final File file = new File((String) info.get("path"));
                    SendBird.connect(SendBird.getCurrentUser().getUserId(), new SendBird.ConnectHandler() {
                        public void onConnected(User user, SendBirdException e) {
                            if (e == null) {
                                SendBirdPushHelper.registerPushHandler(new MyFirebaseMessagingService());

                                OpenParticipantActivity.this.mChannel.updateChannel(OpenParticipantActivity.this.mChannel.getName(), file, OpenParticipantActivity.this.mChannel.getData(), new GroupChannel.GroupChannelUpdateHandler() {
                                    public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                        if (e != null) {
                                            e.printStackTrace();
                                        } else {
                                            Glide.with((FragmentActivity) OpenParticipantActivity.this).load(groupChannel.getCoverUrl()).into(OpenParticipantActivity.this.bg_image);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPic() {
        int targetW = this.bg_image.getWidth();
        int targetH = this.bg_image.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(this.mCurrentPhotoPath, bmOptions);
        int scaleFactor = Math.min(bmOptions.outWidth / targetW, bmOptions.outHeight / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(this.mCurrentPhotoPath, bmOptions);
        this.bg_image.setImageBitmap(null);
        this.bg_image.destroyDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        this.profile_url = Base64.encodeToString(baos.toByteArray(), 0);
        Toast.makeText(this, "Picture uploaded successfully", Toast.LENGTH_SHORT).show();
        SendBird.connect(SendBird.getCurrentUser().getUserId(), new SendBird.ConnectHandler() {
            public void onConnected(User user, SendBirdException e) {
                if (e == null) {
                    SendBirdPushHelper.registerPushHandler(new MyFirebaseMessagingService());

                    OpenParticipantActivity.this.mChannel.updateChannel(OpenParticipantActivity.this.mChannel.getName(), new File(OpenParticipantActivity.this.mCurrentPhotoPath), OpenParticipantActivity.this.mChannel.getData(), new GroupChannel.GroupChannelUpdateHandler() {
                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                            if (e != null) {
                                e.printStackTrace();
                            } else {
                                Glide.with((FragmentActivity) OpenParticipantActivity.this).load(groupChannel.getCoverUrl()).into(OpenParticipantActivity.this.bg_image);
                            }
                        }
                    });
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void showChannelOptionsDialog(/*final GroupChannel channel*/String selectedUserId) {
        if (mChannel != null) {

            //   showTwoButtonDialogWithListener(this, "", "");
            dialogBoxList(selectedUserId);

           /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
            StringBuilder sb = new StringBuilder();
            sb.append("Leave channel ");
            sb.append(mChannel.getName());
            sb.append("?");
            builder.setTitle((CharSequence) sb.toString()).setPositiveButton((CharSequence) "Leave",
                    (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    GroupParticipantActivity.this.leaveChannel(mChannel);
                }
            }).setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) null).create().show();*/
        }
    }

    public void showTwoButtonDialogWithListener(Activity activity, String message, String title) {
        try {
            try {
                if (threeBtnDialog != null && threeBtnDialog.isShowing()) {
                    threeBtnDialog.dismiss();
                }
            } catch (Exception e) {

            }

            threeBtnDialog = new Dialog(activity, R.style.DialogTheme);
            threeBtnDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //threeBtnDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            threeBtnDialog.setContentView(R.layout.dialog_make_remove);

            WindowManager.LayoutParams layoutParams = threeBtnDialog.getWindow().getAttributes();
            threeBtnDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;

            threeBtnDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            threeBtnDialog.getWindow().setAttributes(layoutParams);
            threeBtnDialog.setCancelable(true);
            threeBtnDialog.setCanceledOnTouchOutside(true);


            Button btnMake = (Button) threeBtnDialog.findViewById(R.id.btnMake);
            Button mBtnCancel = (Button) threeBtnDialog.findViewById(R.id.btnCancel);
            Button btnJoin = (Button) threeBtnDialog.findViewById(R.id.btnJoin);

            TextView titiletv = (TextView) threeBtnDialog.findViewById(R.id.tvTitle);
            TextView tvMessage = (TextView) threeBtnDialog.findViewById(R.id.tvMessage);
            titiletv.setText(title);
            titiletv.setVisibility(View.GONE);
            tvMessage.setText(message);
            tvMessage.setVisibility(View.GONE);
            btnMake.setOnClickListener(view -> {

                threeBtnDialog.dismiss();
            });
            btnJoin.setOnClickListener(view -> {

                threeBtnDialog.dismiss();
            });
            mBtnCancel.setOnClickListener(view -> threeBtnDialog.dismiss());


            threeBtnDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* access modifiers changed from: private */
    public void makeAdmin(GroupChannel channel) {
       /* channel.leave(new GroupChannel.GroupChannelLeaveHandler() {
            public void onResult(SendBirdException e) {
                if (e == null) {
                    GroupParticipantActivity.this.refresh();
                }
            }
        });
        finish();*/

     /*   channel.addOperators(USER_IDS, new AddOperatorsHandler() {
            @Override
            public void onResult(SendBirdException e) {
                if (e != null) {
                    // TODO: Custom implementation for what should be done upon failure.
                    return;
                }

                // TODO: Custom implementation for what should be done when the operation succeeded.
            }
        });*/
    }

    public void dialogBoxList(String selectedUserId) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Choose an animal");
        // add a list
        String[] alertOptions = {"Make group admin", "Remove"};
        builder.setItems(alertOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        makeAdmin(mChannel);
                        break;

                    case 1:
                        removeMemberFromGroup(mChannel, selectedUserId);
                        break;

                }
            }
        });
// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeMemberFromGroup(GroupChannel mChannel, String selectedUserId) {
        mChannel.banUserWithUserId(selectedUserId, "", -1, new GroupChannel.GroupChannelBanHandler() {
            @Override
            public void onResult(SendBirdException e) {
                if (e != null) {
                    return;
                }
                OpenParticipantActivity.this.refresh();
                finish();
            }
        });
    }

    /* access modifiers changed from: private */
    public void showChanneldeleteDialog(final GroupChannel channel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        StringBuilder sb = new StringBuilder();
        sb.append("Delete channel ");
        sb.append(channel.getName());
        sb.append("?");
        builder.setTitle((CharSequence) sb.toString()).setPositiveButton((CharSequence) "Delete", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                OpenParticipantActivity.this.leaveChannel(channel);
                OpenParticipantActivity.this.finish();
                OpenParticipantActivity.this.startActivity(new Intent(OpenParticipantActivity.this, UserNavgation.class));
            }
        }).setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) null).create().show();
    }

    public void updategroupname() {
        SendBird.connect(SendBird.getCurrentUser().getUserId(), new SendBird.ConnectHandler() {
            public void onConnected(User user, SendBirdException e) {
                if (e == null) {
                    SendBirdPushHelper.registerPushHandler(new MyFirebaseMessagingService());

                    OpenParticipantActivity.this.mChannel.updateChannel(OpenParticipantActivity.this.et_group_fullname.getText().toString(), OpenParticipantActivity.this.mChannel.getCoverUrl(), OpenParticipantActivity.this.mChannel.getData(), new GroupChannel.GroupChannelUpdateHandler() {
                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void setChannelPushPreferences(GroupChannel channel, boolean on) {
        channel.setPushPreference(on, new GroupChannel.GroupChannelSetPushPreferenceHandler() {
            public void onResult(SendBirdException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }
}
