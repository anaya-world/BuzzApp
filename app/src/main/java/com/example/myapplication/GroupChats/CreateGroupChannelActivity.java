package com.example.myapplication.GroupChats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.myapplication.Activities.BaseActivity;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.Fragments.SelectDistinctFragment;
import com.example.myapplication.Fragments.SelectUserFragment;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.FileUtils;
import com.example.myapplication.Utils.MimeType;
import com.example.myapplication.Utils.SharedPrefManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class CreateGroupChannelActivity extends BaseActivity implements SelectUserFragment.UsersSelectedListener, SelectDistinctFragment.DistinctSelectedListener {
    FrameLayout attachment_container;
    /* access modifiers changed from: private */
    public boolean enableCreate = false;
    File file;
    ImageView group_image;
    RelativeLayout groupframe;
    /* access modifiers changed from: private */
    public boolean isClicked = false;
    ImageView iv_camera;
    ImageView iv_gallery;
    /* access modifiers changed from: private */
    public Button mCreateButton;
    String mCurrentPhotoPath;
    /* access modifiers changed from: private */
    public int mCurrentState;
    private boolean mIsDistinct = true;
    private TextInputEditText mNameEditText;
    private Button mNextButton;
    RelativeLayout mRootLayout;
    /* access modifiers changed from: private */
    Map<String, FriendListModel> mSelectedIds;
    // public List<String> mSelectedIds;
    private Toolbar mToolbar;
    Uri prfileuri;
    String profile_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_channel);
        this.mNameEditText = (TextInputEditText) findViewById(R.id.edittext_create_open_channel_name);
        this.groupframe = (RelativeLayout) findViewById(R.id.groupframe);
        this.group_image = (ImageView) findViewById(R.id.group_image);
        this.iv_camera = (ImageView) findViewById(R.id.iv_camera);
        this.iv_gallery = (ImageView) findViewById(R.id.iv_gallery);
        this.attachment_container = (FrameLayout) findViewById(R.id.attachment_container);
        this.mRootLayout = (RelativeLayout) findViewById(R.id.layout_group_chat_root);
        this.group_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                isClicked = !isClicked;
                if (isClicked) {
                    attachment_container.setVisibility(View.VISIBLE);
                } else {
                    attachment_container.setVisibility(View.GONE);
                }
            }
        });
        this.iv_camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(CreateGroupChannelActivity.this, "android.permission.CAMERA") == -1) {
                    ActivityCompat.requestPermissions(CreateGroupChannelActivity.this, new String[]{"android.permission.CAMERA"}, 1);
                    return;
                }
                attachment_container.setVisibility(View.GONE);
                dispatchTakePictureIntent();
            }
        });
        this.iv_gallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                requestMedia();
                attachment_container.setVisibility(View.GONE);
            }
        });
        this.mSelectedIds = new HashMap<String, FriendListModel>();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_create_group_channel, SelectUserFragment.newInstance()).commit();
        }
        this.mNextButton = (Button) findViewById(R.id.button_create_group_channel_next);
        this.mNextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (CreateGroupChannelActivity.this.mCurrentState == 0) {
                    CreateGroupChannelActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.container_create_group_channel, SelectDistinctFragment.newInstance()).addToBackStack(null).commit();
                }
            }
        });
        this.mNextButton.setEnabled(false);
        this.mCreateButton = (Button) findViewById(R.id.button_create_group_channel_create);
        this.mCreateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (CreateGroupChannelActivity.this.mCurrentState == 0) {
                    CreateGroupChannelActivity.this.createGroupChannel(CreateGroupChannelActivity.this.mSelectedIds, true);
                }
            }
        });
        this.mCreateButton.setEnabled(false);
        this.mToolbar = (Toolbar) findViewById(R.id.toolbar_create_group_channel);
        setSupportActionBar(this.mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator((int) R.drawable.ic_arrow_left_white_24_dp);
        }
        ImageView chat_backarrow = (ImageView) findViewById(R.id.chat_backarrow);
        chat_backarrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        this.mNameEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    if (CreateGroupChannelActivity.this.enableCreate) {
                        CreateGroupChannelActivity.this.mCreateButton.setEnabled(false);
                        CreateGroupChannelActivity.this.enableCreate = false;
                    }
                } else if (!CreateGroupChannelActivity.this.enableCreate) {
                    CreateGroupChannelActivity.this.enableCreate = true;
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void setState(int state) {
        if (state == 0) {
            this.mCurrentState = 0;
            this.mCreateButton.setVisibility(View.VISIBLE);
            this.groupframe.setVisibility(View.VISIBLE);
            this.mNextButton.setVisibility(View.GONE);
        } else if (state == 1) {
            this.mCurrentState = 1;
            this.mCreateButton.setVisibility(View.VISIBLE);
            this.groupframe.setVisibility(View.VISIBLE);
            this.mNextButton.setVisibility(View.GONE);
        }
    }

    public void onUserSelected(boolean selected, String userId, FriendListModel name) {
        if (selected) {

            this.mSelectedIds.put(userId, name);
        } else {
            this.mSelectedIds.remove(userId);
        }
        if (this.mSelectedIds.size() > 0) {
            this.mCreateButton.setEnabled(true);
            this.groupframe.setVisibility(View.VISIBLE);
            return;
        }
        this.mCreateButton.setEnabled(false);
        this.groupframe.setVisibility(View.GONE);
    }

    public void onDistinctSelected(boolean distinct) {
        this.mIsDistinct = distinct;
    }

    public void createGroupChannel(Map<String, FriendListModel> userId, boolean distinct) {
        String friendid = "";
        String dateString = new SimpleDateFormat("dd-MMM-yyyy").format(Long.valueOf(System.currentTimeMillis()));
        ArrayList<User> op = new ArrayList<>();
        List<String> userIds = new ArrayList<>(userId.keySet());
        op.add(SendBird.getCurrentUser());
        Log.d("SendBird.getCurrentUser", "" + SendBird.getCurrentUser());
        StringBuilder sb = new StringBuilder();
        sb.append("Created by ");
        sb.append(SharedPrefManager.getInstance(this).getUser().getUser_name());
        sb.append(" ");
        sb.append(dateString);
        Log.d("createGroupChannel ", "<" + userIds + " <" + sb.toString() + "" +
                " <" + op + " <" + SharedPrefManager.getInstance(this).getUser().getCaller_id() + " " +
                "<" + this.mNameEditText.getText().toString());
        Log.d("createGroupChannel ", "<" + userId.get(userIds.get(0)).getName()
                + "<" + SharedPrefManager.getInstance(this).getUser().getUser_image() +
                "<" + userId.get(userIds.get(0)).getImage()
                + "<" + SharedPrefManager.getInstance(this).getUser().getUser_name()
        );

        if (userIds.size() > 1) {
            Log.d("createGroupChannel ", "<" + 1);
            if (!mNameEditText.getText().toString().trim().equalsIgnoreCase("")) {

                String strGroupName = this.mNameEditText.getText().toString();
                String strAdminName = SharedPrefManager.getInstance(this).getUser().getUser_name();
                GroupChannel.createChannel(new GroupChannelParams()
                        .setPublic(false)
                        .setEphemeral(false)
                        .setDistinct(false)
                        .addUserIds(userIds)
                        .setData(sb.toString())
                        .setOperators(op)
                        .setCustomType(SharedPrefManager.getInstance(this).getUser().getUser_id())
                        .setName(strGroupName)
                        .setCoverImage(this.file), new GroupChannel.GroupChannelCreateHandler() {
                    public void onResult(GroupChannel groupChannel, SendBirdException e) {
                        if (e == null) {
                            ProgressDialog progressDialog = new ProgressDialog(CreateGroupChannelActivity.this);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setMessage("Creating a group...");
                            progressDialog.setIndeterminate(true);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Log.d("createGroupChannel ", "<" + groupChannel.getUrl());
                                    Intent intent = new Intent();
                                    intent.putExtra("EXTRA_NEW_CHANNEL_URL", groupChannel.getUrl());
                                    intent.putExtra("EXTRA_NEW_CHANNEL", true);
                                    intent.putExtra("EXTRA_NEW_CHANNEL_MESSAGE", strAdminName + " has created a new group.");
                                    CreateGroupChannelActivity.this.setResult(-1, intent);
                                    CreateGroupChannelActivity.this.finish();
                                }
                            }, 3000);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please enter Group name first", Toast.LENGTH_SHORT).show();
            }

        } else {
            for (int i = 0; i < userIds.size(); i++) {
                if (userIds.get(i).equals(SharedPrefManager.getInstance(this).getUser().getUser_id())) {
                    friendid = userIds.get(i);
                }
            }
            if (file == null && mNameEditText.getText().toString().equals("")) {
                Log.d("createGroupChannel ", "<" + 2);
                GroupChannel.createChannel(new GroupChannelParams()
                                .setPublic(false)
                                .setCoverUrl("NO")
                                .setEphemeral(false)
                                .setDistinct(true)
                                .addUserIds(userIds)
                                .setData(sb.toString())
                                .setOperators(op)
                                .setCustomType(SharedPrefManager.getInstance(this).getUser().getUser_id())
                                .setCustomType(friendid)
                                .setName("NO")

                        // .setCoverImage(this.file)
                        , new GroupChannel.GroupChannelCreateHandler() {
                            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                Log.d("createGroupChannel", "" + groupChannel.getUrl());
                                if (e == null) {
                                    Intent intent = new Intent();
                                    intent.putExtra("EXTRA_NEW_CHANNEL_URL", groupChannel.getUrl());
                                    intent.putExtra("is_init_created", false);
                                    CreateGroupChannelActivity.this.setResult(-1, intent);
                                    CreateGroupChannelActivity.this.finish();
                                }
                            }
                        });
            } else if (file != null && mNameEditText.getText().toString().equals("")) {
                Log.d("createGroupChannel ", "<" + 3);
                GroupChannel.createChannel(new GroupChannelParams()
                                .setPublic(false)
                                .setEphemeral(false)
                                .setDistinct(false)
                                .addUserIds(userIds)
                                .setData(sb.toString())
                                .setOperators(op)
                                .setCustomType(SharedPrefManager.getInstance(this).getUser().getUser_id())
                                .setName("NO")
                                .setCoverImage(this.file)
                        , new GroupChannel.GroupChannelCreateHandler() {
                            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                Log.d("createGroupChannel", "" + groupChannel.getUrl());
                                if (e == null) {
                                    Intent intent = new Intent();
                                    intent.putExtra("EXTRA_NEW_CHANNEL_URL", groupChannel.getUrl());
                                    intent.putExtra("is_init_created", false);
                                    CreateGroupChannelActivity.this.setResult(-1, intent);
                                    CreateGroupChannelActivity.this.finish();
                                }
                            }
                        });


            } else if (file == null && !mNameEditText.getText().toString().equalsIgnoreCase("")) {
                Log.d("createGroupChannel ", "<" + 4);
                GroupChannel.createChannel(new GroupChannelParams()
                                .setPublic(false)
                                .setEphemeral(false)
                                .setDistinct(false)
                                .addUserIds(userIds)
                                .setData(sb.toString())
                                .setOperators(op)
                                .setCustomType(SharedPrefManager.getInstance(this).getUser().getCaller_id())
                                .setName(mNameEditText.getText().toString())
                                .setCoverUrl("NO")
                        , new GroupChannel.GroupChannelCreateHandler() {
                            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                Log.d("createGroupChannel", "" + groupChannel.getUrl());
                                if (e == null) {
                                    Intent intent = new Intent();
                                    intent.putExtra("EXTRA_NEW_CHANNEL_URL", groupChannel.getUrl());
                                    intent.putExtra("is_init_created", false);
                                    CreateGroupChannelActivity.this.setResult(-1, intent);
                                    CreateGroupChannelActivity.this.finish();
                                }
                            }
                        });
            } else {
                Log.d("createGroupChannel ", "<" + 5);
                GroupChannel.createChannel(new GroupChannelParams()
                                .setPublic(false)
                                .setEphemeral(false)
                                .setDistinct(false)
                                .addUserIds(userIds)
                                .setData(sb.toString())
                                .setOperators(op)
                                .setCustomType(SharedPrefManager.getInstance(this).getUser().getCaller_id())
                                .setName(mNameEditText.getText().toString())
                                .setCoverImage(this.file)
                        , new GroupChannel.GroupChannelCreateHandler() {
                            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                Log.d("createGroupChannel", "" + groupChannel.getUrl());
                                if (e == null) {
                                    Intent intent = new Intent();
                                    intent.putExtra("EXTRA_NEW_CHANNEL_URL", groupChannel.getUrl());
                                    intent.putExtra("is_init_created", false);
                                    CreateGroupChannelActivity.this.setResult(-1, intent);
                                    CreateGroupChannelActivity.this.finish();
                                }
                            }
                        });
            }
        }

    }

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            Snackbar.make((View) this.mRootLayout, (CharSequence) "Storage access permissions are required to upload/download files.", Snackbar.LENGTH_LONG).setAction((CharSequence) "Okay", (View.OnClickListener) new View.OnClickListener() {
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        CreateGroupChannelActivity.this.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 13);
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
            timeStamp = new android.icu.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
                    this.file = new File((String) info.get("path"));
                    Glide.with((AppCompatActivity) this).load(bitmap).into(this.group_image);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPic() {
        int targetW = this.group_image.getWidth();
        int targetH = this.group_image.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(this.mCurrentPhotoPath, bmOptions);
        int scaleFactor = Math.min(bmOptions.outWidth / targetW, bmOptions.outHeight / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(this.mCurrentPhotoPath, bmOptions);
        this.group_image.setImageBitmap(null);
        this.group_image.destroyDrawingCache();
        this.file = new File(this.mCurrentPhotoPath);
        Glide.with((AppCompatActivity) this).load(bitmap).into(this.group_image);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        this.profile_url = Base64.encodeToString(baos.toByteArray(), 0);
        Toast.makeText(this, "Picture uploaded successfully", Toast.LENGTH_SHORT).show();
    }
}
