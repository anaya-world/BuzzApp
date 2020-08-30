package com.example.myapplication.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Activities.ImageCroperActivty;
import com.example.myapplication.Adapter.PostListAdapter;
import com.example.myapplication.Adapter.Social_userlistAdapter;
import com.example.myapplication.Intefaces.HideInterface;
import com.example.myapplication.Intefaces.LikeInterface;
import com.example.myapplication.Intefaces.LimitIncrease;
import com.example.myapplication.Intefaces.TabNotify;
import com.example.myapplication.ModelClasses.PostListModel;
import com.example.myapplication.ModelClasses.Social_user_name;
import com.example.myapplication.R;
import com.example.myapplication.Utils.AppHelper;
import com.example.myapplication.Utils.BaseUrl_ConstantClass;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MimeType;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;
import com.example.myapplication.Utils.VolleyMultipartRequest;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kevalpatel2106.emoticongifkeyboard.EmoticonGIFKeyboardFragment;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.Emoticon;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.EmoticonSelectListener;
import com.kevalpatel2106.emoticongifkeyboard.gifs.Gif;
import com.kevalpatel2106.emoticongifkeyboard.gifs.GifSelectListener;
import com.kevalpatel2106.emoticonpack.android8.Android8EmoticonProvider;
import com.kevalpatel2106.gifpack.giphy.GiphyGifProvider;
import com.sendbird.android.SendBird;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialFragment extends Fragment implements SearchView.OnQueryTextListener, LimitIncrease {

    TabNotify tabNotify;

    private static final int CAMERA_PIC_REQUEST = 100;
    public static final int INTENT_REQUEST_CHOOSE_MEDIA = 301;
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    private View view;
    public static final int PICK_FROM_GALLERY = 1;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 501;
    FrameLayout attachment_container;
    String content = "";
   // String userid = SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString();
    Calendar calendar=Calendar.getInstance();
    ImageView dialog_imageview;
    String post_title = "buzzideo";
    private Uri mVideoURI;
    Uri downloadUri;
    private HashMap<String, PostListModel> localpostdata = new HashMap<> ();
    private HashMap<String,PostListModel> newpostdata = new HashMap<> ();
    String mCoverImage;
    Uri downloadUriforVideo;
    Uri downloadmultiimage;
    EmoticonGIFKeyboardFragment emoticonGIFKeyboardFragment;
    File file;
    String filetype = "";
    boolean flag = true;
    ImageView imageview;
    int mAvatarImage;
    /* access modifiers changed from: private */
    public boolean isClicked = false;
    ImageView iv_camera;
    ImageView iv_gallery;
    ImageView iv_import;
    ImageView iv_sticker;
    private RelativeLayout mRootLayout;
    private ImageButton mUploadFileButton;
    String media_type = "image";
    // DatabaseReference multiimagereference;
    String name;
    LinearLayout poplinear;
    PopupWindow popupWindow;
    PostListAdapter postListAdapter;
    ArrayList<PostListModel> postListModelArrayList=new ArrayList<>();
    /* access modifiers changed from: private */
    public RecyclerView postRecycler;
    private ImageView post_emoji_open_close_btn;
    FrameLayout post_keyboardcontainer;
    Uri prfileuri;
    CircleImageView profile_img;
    ProgressDialog progressDialog;
    ProgressBar progress_social;
    ArrayList<Social_user_name> social_user_names = new ArrayList<>();
    FrameLayout socialfragment;
    /* access modifiers changed from: private */
    public EditText statusPost;
    private ImageView statusSend;
    private EditText statusTitle;
    String userId;
    String offset="0";
    String limit="20";
    TextView user_name;
    /* access modifiers changed from: private */
    public RecyclerView userlist_recycler;
    private ImageView imageViewphoto;
    private ImageView iv_Video;
    private String filemanagerstring;
    private VideoView playbackView;
    private ProgressBar bar;
    private int progressStatus = 0;
    private Handler pHandler = new Handler();
    private ProgressBar progerss_user;
    private ProgressBar progerss_post;
    LinearLayout social_nolist;

    public SocialFragment() {
        // Required empty public constructor
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabNotify=(TabNotify)getActivity();
        tabNotify.notifyTab(2);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.userId = SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString();
        this.name = SharedPrefManager.getInstance(getActivity()).getUser().getUser_name().toString();
        view =  inflater.inflate(R.layout.fragment_social, container, false);
        this.progerss_user=(ProgressBar)view.findViewById(R.id.userlistpro);
        this.progerss_post=(ProgressBar)view.findViewById(R.id.userlistpost);
        this.progerss_post.setVisibility(View.GONE);

        this.post_keyboardcontainer = (FrameLayout) view.findViewById(R.id.postkeyboard_container);
        this.profile_img = (CircleImageView) view.findViewById(R.id.profile_img);
        this.mUploadFileButton = (ImageButton) view.findViewById(R.id.button_group_chat_upload);
        this.progress_social = (ProgressBar) view.findViewById(R.id.progress_social);
        this.postRecycler = (RecyclerView) view.findViewById(R.id.postRecycler);
        this.userlist_recycler = (RecyclerView) view.findViewById(R.id.userlist_recycler);
        this.statusPost = (EditText) view.findViewById(R.id.statusPost);
        this.statusSend = (ImageView) view.findViewById(R.id.statusSend);
        this.socialfragment = (FrameLayout) view.findViewById(R.id.socialfragment);
        this.iv_camera = (ImageView) view.findViewById(R.id.iv_camera);
        this.iv_gallery = (ImageView) view.findViewById(R.id.iv_gallery);
        this.iv_Video = (ImageView) view.findViewById(R.id.iv_Video);
        this.attachment_container = (FrameLayout) view.findViewById(R.id.attachment_container);
        this.iv_import = (ImageView) view.findViewById(R.id.iv_import);
        this.dialog_imageview = (ImageView) view.findViewById(R.id.dialog_imageview);
        bar = (ProgressBar) view.findViewById(R.id.progressBar2);
        social_nolist=view.findViewById(R.id.nolist_social);


        loadPost();
        loadImage();
        loadUserlist();

        //  load_imagesocial();

        iv_Video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);

            }
        });
        this.iv_import.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                SocialFragment.this.popupShow();
            }
        });
        this.statusPost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (SocialFragment.this.emoticonGIFKeyboardFragment.isAdded()) {
                    SocialFragment.this.emoticonGIFKeyboardFragment.close();
                } else if (SocialFragment.this.attachment_container.getVisibility() == View.VISIBLE) {
                    SocialFragment.this.attachment_container.setVisibility(View.GONE);
                }
            }
            public  void progressBar(){
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (progressStatus < 100)
                        {
                            progressStatus++;
                            SystemClock.sleep(50);
                            pHandler.post(new Runnable()
                            {
                                @Override
                                public void run() {
                                    bar.setProgress(progressStatus);
                                }
                            });

                        }
                        pHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // loading.setText("COMPLETE");
                            }
                        });
                    }
                }).start();
            }
        });
        new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        this.postRecycler.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(getActivity(), 1);
        layoutManager.setSpanSizeLookup(    new GridLayoutManager.SpanSizeLookup(){
            @Override
            public int getSpanSize(int position) {
                return position % 2 ==1 ? 1 :1;
            }
        });

        this.postRecycler.setLayoutManager(layoutManager);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            postRecycler.setOnScrollChangeListener(new View.OnScrollChangeListener() {

                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                    Log.d("onScrollChange",i+" "+i1+" "+i2+" "+i3);
                }
            });
        }
        this.mUploadFileButton.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                if (!checkPermission1()) {
                    requestPermission1();
                }
//                Intent intent = new Intent();
//                intent.setType("video/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_GALLERY);
//
//
//                Intent intent = new Intent();
//                intent.setType("images/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_GALLERY);
                SocialFragment.this.isClicked = !SocialFragment.this.isClicked;
                if (SocialFragment.this.isClicked) {
                    SocialFragment.this.attachment_container.setVisibility(View.VISIBLE);
                    SocialFragment.this.post_keyboardcontainer.setVisibility(View.GONE);

                    return;
                } else

                    SocialFragment.this.attachment_container.setVisibility(View.GONE);
                SocialFragment.this.post_keyboardcontainer.setVisibility(View.GONE);
//                load_videosocial();
//                load_imagesocial();

            }


        });


        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,101);

//                final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
//                File newdir = new File(dir);
//                newdir.mkdirs();
//                int count = 0;
//
//                String file = dir+count+".jpg";
//                File newfile = new File(file);
//                try {
//                    newfile.createNewFile();
//                }
//                catch (IOException e)
//                {
//                }
//
//                Uri outputFileUri = Uri.fromFile(newfile);count++;
//
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                Log.d("setOnClickListener", String.valueOf(cameraIntent.resolveActivity(getActivity().getPackageManager())));
//                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//
//                    // Create the File where the photo should go
//                    File photoFile = null;
//                    try {
//
//                        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
//                        String imageFileName = "JPEG_" + timeStamp + "_";
//                        File storageDir = Environment.getExternalStoragePublicDirectory(
//                                Environment.DIRECTORY_PICTURES);
//                        File image = File.createTempFile(imageFileName,  // prefix
//                                ".jpg",         // suffix
//                                storageDir      // directory
//
//                        );
//
//                        // Save a file: path for use with ACTION_VIEW intents
//                        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//                        photoFile = image;
//                    } catch (IOException ex) {
//                        // Error occurred while creating the File
//                        Log.i(TAG, "IOException");
//                    }
//                    // Continue only if the File was successfully created
//                    if (photoFile != null) {
//                        cameraIntent.putExtra(MediaStore.EXTRA        at com.bumptech.glide.load.engine.DecodeJob.onDataFetcherFailed(DecodeJob.java:397)_OUTPUT, Uri.fromFile(photoFile));
//                        startActivityForResult(cameraIntent, 101);
//                    }
//                }
            }
        });
        this.iv_gallery.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                SocialFragment.this.requestMedia();
                SocialFragment.this.attachment_container.setVisibility(View.GONE);
            }
        });
        this.statusSend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                SocialFragment.this.content = SocialFragment.this.statusPost.getText().toString();
                SocialFragment.this.attachment_container.setVisibility(View.GONE);
                SocialFragment.this.post_keyboardcontainer.setVisibility(View.GONE);
                if (!SocialFragment.this.content.isEmpty()) {

                    SocialFragment.this.sendPost();
                    statusPost.setText("");
                } else {
                    Toast.makeText(SocialFragment.this.getActivity(), "Please Enter status", Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.emoticonGIFKeyboardFragment = EmoticonGIFKeyboardFragment.getNewInstance(this.post_keyboardcontainer, new EmoticonGIFKeyboardFragment.EmoticonConfig().setEmoticonProvider(Android8EmoticonProvider.create()).setEmoticonSelectListener(new EmoticonSelectListener() {
            public void emoticonSelected(Emoticon emoticon) {
                SocialFragment.this.statusPost.append(emoticon.getUnicode());
            }

            public void onBackSpace() {
                SocialFragment.this.emoticonGIFKeyboardFragment.close();
            }
        }), new EmoticonGIFKeyboardFragment.GIFConfig(GiphyGifProvider.create(getActivity(), "564ce7370bf347f2b7c0e4746593c179")).setGifSelectListener(new GifSelectListener() {
            public void onGifSelected(@NonNull Gif gif) {
                StringBuilder sb = new StringBuilder();
                sb.append("onGifSelected: ");
                sb.append(gif.getGifUrl());
                Log.d("GUF", sb.toString());
                show_image(gif);
            }
        }));
        this.post_emoji_open_close_btn = (ImageView) view.findViewById(R.id.post_emoji_open_close_btn);
        this.post_keyboardcontainer.setVisibility(View.VISIBLE);
        this.attachment_container.setVisibility(View.GONE);
        this.post_emoji_open_close_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SocialFragment.this.isClicked = !SocialFragment.this.isClicked;
                if (SocialFragment.this.isClicked) {
                    SocialFragment.this.attachment_container.setVisibility(View.GONE);
                    SocialFragment.this.post_keyboardcontainer.setVisibility(View.VISIBLE);
                    ((InputMethodManager) SocialFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                    SocialFragment.this.getFragmentManager().beginTransaction().replace(R.id.postkeyboard_container, SocialFragment.this.emoticonGIFKeyboardFragment).commit();
                    SocialFragment.this.emoticonGIFKeyboardFragment.open();
                    return;
                }
                SocialFragment.this.attachment_container.setVisibility(View.GONE);
                SocialFragment.this.post_keyboardcontainer.setVisibility(View.GONE);
            }
        });
        return view;
    }
    @Override
    public void onPause() {
        if(postListAdapter!=null) {
            postListAdapter.stopallvideo();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        //  postListAdapter.startVideos();
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
            intent.setType("image/*");
        }
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Media"), INTENT_REQUEST_CHOOSE_MEDIA);
        SendBird.setAutoBackgroundDetection(false);
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE")) {
            Snackbar.make((View) this.mRootLayout, (CharSequence) "Storage access permissions are required to upload/download files.", BaseTransientBottomBar.LENGTH_LONG).setAction((CharSequence) "Okay", (View.OnClickListener) new View.OnClickListener() {
                public void onClick(View view) {
                    SocialFragment.this.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 13);
                }
            }).show();
            return;
        }
        requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 13);
    }

    private void loadUserlist() {


        this.progerss_user.setVisibility(View.VISIBLE);
        this.progerss_user.invalidate();
        StringRequest r1 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                progerss_user.setVisibility(View.GONE);
                try {
                    JSONArray jsonArray = new JSONObject(response).getJSONArray("search_result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject user_details = jsonArray.getJSONObject(i);
                        SocialFragment.this.social_user_names.add(new Social_user_name(user_details.getString("user_img"),
                                user_details.optString("name"),
                                user_details.optString("frnid"),
                                user_details.getBoolean ("follower_status"),
                                user_details.getString("frnstatus")));
                        Social_userlistAdapter social_userlistAdapter = new Social_userlistAdapter(SocialFragment.this.social_user_names, SocialFragment.this.getContext());
                        SocialFragment.this.userlist_recycler.setLayoutManager(new LinearLayoutManager(SocialFragment.this.getActivity(), RecyclerView.HORIZONTAL, false));
                        SocialFragment.this.userlist_recycler.setAdapter(social_userlistAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                progerss_user.setVisibility(View.GONE);
                Toast.makeText(SocialFragment.this.getActivity(), "Not Connected to Internet", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "searchfriends");
                logParams.put("searchkey", "");
                logParams.put("userid", SocialFragment.this.userId);
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r1);
    }

    /* access modifiers changed from: private */
    public void sendPost(Gif gif,String cont) {
        String str = CommonUtils.baseUrl;

        StringRequest r1 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {

                try {
                    if (new JSONObject(response).getString("success").equals("true")) {
                        SocialFragment.this.loadPost();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SocialFragment.this.getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "addpost");
                logParams.put("post_time",calendar.getTime().toString());

                logParams.put("userid", SocialFragment.this.userId);
                logParams.put("post_title", "title");
                if(cont!=null) {
                    logParams.put("content", cont);
                }else{
                    logParams.put("content", "");
                }
                logParams.put("media_type","gif");
//                if (SocialFragment.this.downloadUri != null) {
//                    logParams.put("mediacontent", SocialFragment.this.downloadUri.toString());
//                } else {
                logParams.put("mediacontent", gif.getGifUrl());
//                }
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r1);
    }
    public void sendPost() {
        String str = CommonUtils.baseUrl;
        String posttime=calendar.getTime().toString();

        StringRequest r1 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {

                try {
                    if (new JSONObject(response).getString("success").equals("true")) {
                        SocialFragment.this.loadPost();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SocialFragment.this.getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "addpost");
                logParams.put("userid", SocialFragment.this.userId);
                logParams.put("post_title", "title");
                logParams.put("post_time",posttime);
                logParams.put("content", SocialFragment.this.content);
                logParams.put("media_type","");
                if (SocialFragment.this.downloadUri != null) {
                    logParams.put("mediacontent", SocialFragment.this.downloadUri.toString());
                } else {
                    logParams.put("mediacontent", "");
                }
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r1);
    }

    /* access modifiers changed from: private */
    public void loadPost() {

        offset="0";
        limit="20";

        this.progerss_post.setVisibility(View.VISIBLE);
        this.postListModelArrayList = new ArrayList<>();
        StringRequest r2 = new StringRequest(Request.Method.POST, BaseUrl_ConstantClass.BASE_URL, new Response.Listener<String>() {
            public void onResponse(String response) {
                progerss_post.setVisibility(View.GONE);
                Date date;
                JSONException e;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("post_result");
                        int i = 0;
                        while (true) {
                            int i2 = i;
                            if (i2 < jsonArray.length()) {
                                JSONObject user_details = jsonArray.getJSONObject(i2);
                                String post_id = user_details.optString("post_id");
                                String media_video = user_details.optString("media_video");
                                String post_title = user_details.optString("post_title");
                                String content = user_details.optString("content");
                                String postedby = user_details.optString("postedby");
                                String postedby_name = user_details.optString("postedby_name");
                                String post_likes = user_details.optString("post_likes");
                                String you_unliked = user_details.optString("you_unliked");
                                String posted_date = user_details.optString("posted_date");
                                String post_profileimg = user_details.optString("user_img");
                                String you_liked = user_details.optString("you_liked");
                                String post_deslikes = user_details.optString("post_dislikes");
                                String mediaimage = user_details.optString("media_img");
                                String media_type = user_details.optString("media_type");
                                String post_hide = user_details.optString("post_hide");
                                String thumbnails = user_details.optString("video_imagethumbnail");
                                String postedbyuserid = user_details.optString("postedbyuserid");
                                String comment=user_details.optString ( "comment" );
                                Log.d("SimpleDateFormat","1"+posted_date);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    date = sdf.parse(posted_date);
                                } catch (ParseException e2) {
                                    e2.printStackTrace();
                                    date = null;
                                }
                                String post_hide2 = post_hide;
                                String str = posted_date;
                                Log.d("SimpleDateFormat","2"+date);
                                PostListModel postListModel = new PostListModel(comment,media_video,post_id, post_title, content, postedby, postedby_name, post_likes, DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), 0).toString(), post_profileimg, you_liked, mediaimage, media_type, post_deslikes, post_hide2, postedbyuserid, you_unliked,thumbnails);
                                PostListModel postListModel2 = postListModel;
                                Date date2 = date;
                                SocialFragment.this.postListModelArrayList.add(postListModel2);
                                SimpleDateFormat simpleDateFormat = sdf;
                                String post_hide3 = post_hide2;
                                if (post_hide3.equals("Yes")) {
                                    if (SocialFragment.this.postListModelArrayList.contains(postListModel2)) {
                                        SocialFragment.this.postListModelArrayList.remove(postListModel2);
                                    }
                                }
                                String str2 = post_hide3;

                            } else {
                                return;
                            }

                            JSONObject jsonObject2 = jsonObject;
                            try {
                                JSONArray jsonArray2 = jsonArray;
                                // JSONObject jSONObject = user_details;
                                SocialFragment.this.postListAdapter = new PostListAdapter(SocialFragment.this,SocialFragment.this.postListModelArrayList, SocialFragment.this.getActivity(), new LikeInterface() {
                                    public void likePost(View view, int position, String type) {
                                        Log.d("position",""+position);
                                        try {


                                            if (type.equals("like")) {
                                                SocialFragment.this.likeFragments(((PostListModel) SocialFragment.this.postListModelArrayList.get(position)).getPost_id(),position);
                                            } else {


                                                SocialFragment.this.deslikepost(((PostListModel) SocialFragment.this.postListModelArrayList.get(position)).getPost_id());

                                            }
                                        }catch (IndexOutOfBoundsException e){
                                            e.printStackTrace();
                                        }

                                    }
                                }, new HideInterface() {
                                    public void HidePost(String hide) {
                                        SocialFragment.this.loadPost();
                                    }
                                });

                                for(int j=0;j<SocialFragment.this.postListModelArrayList.size ();j++){

                                    newpostdata.put ( SocialFragment.this.postListModelArrayList.get ( j ).getPost_id (),
                                            SocialFragment.this.postListModelArrayList.get ( j ) );
                                }
                                //   ArrayList<PostListModel> friendListModelArrayList=new ArrayList<>(newpostdata.values ());
                                //  Collections.sort(friendListModelArrayList,Collections.reverseOrder ());

                                SocialFragment.this.postListAdapter.setData( postListModelArrayList);
                                Log.d("postListModelArrayList","1"+postListModelArrayList.size ());
                                //                  SocialFragment.this.postListAdapter.setData ( postListModelArrayList );
// if(SocialFragment.this.postRecycler.getAdapter()==null){
                                SocialFragment.this.postRecycler.setAdapter(SocialFragment.this.postListAdapter);
                                SocialFragment.this.postRecycler.invalidate ();
// }
                                SocialFragment.this.postListAdapter.notifyDataSetChanged();


                                i = i2 + 1;
                                jsonObject = jsonObject2;
                                jsonArray = jsonArray2;
                            } catch (Exception E) {
                                E.printStackTrace();
                            }
                        }
                    } catch (JSONException e3) {
                        e = e3;
                        e.printStackTrace();
                    }
                } catch (JSONException e4) {
                    e = e4;
                    e.printStackTrace();
                }
                if(postListModelArrayList.size()<=0)
                {
                    social_nolist.setVisibility(View.VISIBLE);
                    postRecycler.setVisibility(View.GONE);
                }else
                {
                    social_nolist.setVisibility(View.GONE);
                    postRecycler.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

                progerss_post.setVisibility(View.GONE);
                Toast.makeText(SocialFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getpost");
                logParams.put("offset", offset);
                logParams.put("limit", limit);
                logParams.put("userid", SocialFragment.this.userId);
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r2);

    }
    public void loadPostfirst(String offset , String limit) {
        //   this.progerss_post1=(ProgressBar)view.findViewById(R.id.userlistpost1);
        this.progerss_post.setVisibility(View.VISIBLE);
        //  this.postListModelArrayList = new ArrayList<>();
        Log.d("loadPostfirst","1");
        StringRequest r3 = new StringRequest(Request.Method.POST, BaseUrl_ConstantClass.BASE_URL, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d("loadPostfirst","2"+response+progerss_post.getVisibility()+progress_social.getVisibility());
                progerss_post.setVisibility(View.GONE);
                Log.d("loadPostfirst","2"+response+progerss_post.getVisibility()+progress_social.getVisibility()
                        +progerss_user.getVisibility());
                Date date;
                JSONException e;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("post_result");
                        int i = 0;
                        while (true) {
                            int i2 = i;
                            if (i2 < jsonArray.length()) {
                                JSONObject user_details = jsonArray.getJSONObject(i2);
                                String post_id = user_details.optString("post_id");
                                String media_video = user_details.optString("media_video");
                                String post_title = user_details.optString("post_title");
                                String content = user_details.optString("content");
                                String postedby = user_details.optString("postedby");
                                String postedby_name = user_details.optString("postedby_name");
                                String post_likes = user_details.optString("post_likes");
                                String you_unliked = user_details.optString("you_unliked");
                                String posted_date = user_details.optString("posted_date");
                                String post_profileimg = user_details.optString("user_img");
                                String you_liked = user_details.optString("you_liked");
                                String post_deslikes = user_details.optString("post_dislikes");
                                String mediaimage = user_details.optString("media_img");
                                String media_type = user_details.optString("media_type");
                                String post_hide = user_details.optString("post_hide");
                                String postedbyuserid = user_details.optString("postedbyuserid");
                                String thumbnails = user_details.optString("video_imagethumbnail");
                                String comment = user_details.optString ( "comment" );
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    date = sdf.parse(posted_date);
                                } catch (ParseException e2) {
                                    e2.printStackTrace();
                                    date = null;
                                }
                                String post_hide2 = post_hide;
                                String str = posted_date;
                                PostListModel postListModel = new PostListModel(comment,media_video,post_id, post_title, content, postedby, postedby_name, post_likes, DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), 0).toString(), post_profileimg, you_liked, mediaimage, media_type, post_deslikes, post_hide2, postedbyuserid, you_unliked,thumbnails);
                                PostListModel postListModel2 = postListModel;
                                Date date2 = date;
                                SocialFragment.this.postListModelArrayList.add(postListModel2);
                                SimpleDateFormat simpleDateFormat = sdf;
                                String post_hide3 = post_hide2;
                                if (post_hide3.equals("Yes")) {
                                    if (SocialFragment.this.postListModelArrayList.contains(postListModel2)) {
                                        SocialFragment.this.postListModelArrayList.remove(postListModel2);
                                    }
                                }
                                String str2 = post_hide3;

                            } else {
                                return;
                            }

                            JSONObject jsonObject2 = jsonObject;
                            try {
                                JSONArray jsonArray2 = jsonArray;
                                // JSONObject jSONObject = user_details;
//                                SocialFragment.this.postListAdapter = new PostListAdapter(SocialFragment.this,SocialFragment.this.postListModelArrayList, SocialFragment.this.getActivity(), new LikeInterface() {
//                                    public void likePost(View view, int position, String type) {
//                                        Log.d("position",""+position);
//                                        try {
//
//
//                                            if (type.equals("like")) {
//                                                SocialFragment.this.likeFragments(((PostListModel) SocialFragment.this.postListModelArrayList.get(position)).getPost_id());
//                                                postListAdapter.notifyDataSetChanged ();
//                                            } else {
//
//                                                SocialFragment.this.deslikepost(((PostListModel) SocialFragment.this.postListModelArrayList.get(position)).getPost_id());
//
//                                            }
//                                        }catch (IndexOutOfBoundsException e){
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                }, new HideInterface() {
//                                    public void HidePost(String hide) {
//                                        SocialFragment.this.loadPost();
//                                    }
//                                });



                                for(int j=0;j<SocialFragment.this.postListModelArrayList.size ();j++){

                                    newpostdata.put ( SocialFragment.this.postListModelArrayList.get ( j ).getPost_id (),
                                            SocialFragment.this.postListModelArrayList.get ( j ) );
                                }
                                //   ArrayList<PostListModel> friendListModelArrayList=new ArrayList<>(newpostdata.values ());
                                //  Collections.sort(friendListModelArrayList,Collections.reverseOrder ());

                                SocialFragment.this.postListAdapter.setData( postListModelArrayList);
                                //                  SocialFragment.this.postListAdapter.setData ( postListModelArrayList );
// if(SocialFragment.this.postRecycler.getAdapter()==null){
                                //  SocialFragment.this.postRecycler.setAdapter(SocialFragment.this.postListAdapter);
                                SocialFragment.this.postRecycler.invalidate ();
// }
                                SocialFragment.this.postListAdapter.notifyDataSetChanged();


                                i = i2 + 1;
                                jsonObject = jsonObject2;
                                jsonArray = jsonArray2;
                            } catch (Exception E) {
                                E.printStackTrace();
                            }
                        }
                    } catch (JSONException e3) {
                        e = e3;
                        e.printStackTrace();
                    }
                } catch (JSONException e4) {
                    e = e4;
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("loadPostfirst","3"+error);
                // progerss_post.setVisibility(View.GONE);
                Toast.makeText(SocialFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getpost");
                logParams.put("offset", offset);
                logParams.put("limit", limit);
                logParams.put("userid", SocialFragment.this.userId);
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r3);
    }

    /* access modifiers changed from: private */
    public void likeFragments(String postId, int position) {
        String str = CommonUtils.baseUrl;
        final String str2 = postId;
        StringRequest r1 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onResponse(String response) {
                Log.d ( "likeFragments ","" +response);
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString ( "success" ).trim ().equals ( "true" )){
                        //SocialFragment.this.loadPostfirst ();
                        JSONObject jsonObject1=jsonObject.getJSONObject ( "data" );

                        String post_id = jsonObject1.optString("post_id");
                        String likes = jsonObject1.optString("post_likes");
                        String disliked = jsonObject1.optString("post_dislikes");
                        String you_unliked = jsonObject1.optString("you_unliked");
                        String you_liked = jsonObject1.optString("you_liked");


                        PostListModel postListModeln=newpostdata.get ( post_id );
                        postListModeln.setPost_likes ( likes );
                        postListModeln.setPost_deslikes ( disliked );
                        postListModeln.setYou_liked ( you_liked );
                        postListModeln.setYou_unliked ( you_unliked );


                        // newpostdata.replace(post_id,postListModel);

                        SocialFragment.this.postListAdapter.setNewData (postListModeln,post_id);




                    }else{
                        Toast.makeText(SocialFragment.this.getActivity(),jsonObject.getString ( "message"), Toast.LENGTH_SHORT).show();
                    }


                    // SocialFragment.this.loadPost();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SocialFragment.this.getActivity(), "Already Liked", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "likepost");
                logParams.put("post_id", str2);
                logParams.put("userid", SocialFragment.this.userId);
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r1);
    }

    /* access modifiers changed from: private */
    public void deslikepost(String postId) {
        String str = CommonUtils.baseUrl;
        final String str2 = postId;
        StringRequest r1 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d("dislike", response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString ( "success" ).trim ().equals ( "true" )){
                        //SocialFragment.this.loadPostfirst ();
                        JSONObject jsonObject1=jsonObject.getJSONObject ( "data" );

                        String post_id = jsonObject1.optString("post_id");
                        String likes = jsonObject1.optString("post_likes");
                        String disliked = jsonObject1.optString("post_dislikes");
                        String you_unliked = jsonObject1.optString("you_unliked");
                        String you_liked = jsonObject1.optString("you_liked");
                        Log.d("setNewData", post_id+" post_id145" + you_liked);
                        Log.d("setNewData", post_id+" post_id155" + you_unliked);

                        PostListModel postListModeln=newpostdata.get ( post_id );
                        Log.d("setNewData", post_id+" post_id145" + postListModeln+"-"+likes+disliked);
                        postListModeln.setPost_likes ( likes );
                        postListModeln.setPost_deslikes ( disliked );
                        postListModeln.setYou_liked ( you_liked );
                        postListModeln.setYou_unliked ( you_unliked );
                        Log.d("setNewData", post_id+" post_id145" + postListModeln.getYou_liked ());
                        Log.d("setNewData", post_id+" post_id155" + postListModeln.getYou_unliked ());

                        // newpostdata.replace(post_id,postListModel);

                        SocialFragment.this.postListAdapter.setNewData (postListModeln,post_id);




                    }else{
                        Toast.makeText(SocialFragment.this.getActivity(),jsonObject.getString ( "message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SocialFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "dislikepost");
                logParams.put("post_id", str2);
                logParams.put("userid", SocialFragment.this.userId);
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r1);
    }

    private void loadImage() {
        String str = SharedPrefManager.getInstance(getActivity ()).getUser().getUser_id().toString();
        Log.d("1user image", "" + str);
        StringRequest r2 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.optString("success");
                    String optString = jsonObject.optString("message");
                    String user_image = jsonObject.optString("user_img");
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.profile_img);
                    requestOptions.error(R.drawable.buzzplaceholder);

                    Glide.with(getActivity ())
                            .setDefaultRequestOptions(requestOptions)
                            .load(user_image.trim()).into(profile_img);
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity (), "Error in loading Image", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getimg");
                logParams.put("userid", str);
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity ()).addToRequestQue(r2);
    }


    /* access modifiers changed from: private */
    public void popupShow() {
        if (checkPermission1()) {
            View customView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.social_pop_attached, null);
            this.popupWindow = new PopupWindow(customView, -1, -2);
            this.poplinear = (LinearLayout) customView.findViewById(R.id.poplinear);
            this.popupWindow.showAtLocation(this.poplinear, 17, 0, 0);
            ((ViewGroup.MarginLayoutParams) customView.getLayoutParams()).setMargins(30, 0, 30, 0);
            ((ImageView) customView.findViewById(R.id.cancel_img)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SocialFragment.this.popupWindow.dismiss();
                }
            });
            ((ImageView) customView.findViewById(R.id.iv_image)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SocialFragment.this.attachment_container.setVisibility(View.GONE);
                    if (ContextCompat.checkSelfPermission(SocialFragment.this.getActivity(), "android.permission.CAMERA") == -1) {
                        ActivityCompat.requestPermissions(SocialFragment.this.getActivity(), new String[]{"android.permission.CAMERA"}, 1);
                        return;
                    }
                    SocialFragment.this.startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), 100);
                }
            });
            this.iv_gallery = (ImageView) customView.findViewById(R.id.iv_gallery);
            this.iv_gallery.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(SocialFragment.this.getContext(), ImageCroperActivty.class);
                    intent.putExtra("c", "Two");
                    SocialFragment.this.startActivity(intent);
                    SocialFragment.this.popupWindow.dismiss();
                }
            });
            return;
        }
        requestPermission1();
    }

    private void requestPermission1() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 5);
    }

    public boolean checkPermission1() {
        return ContextCompat.checkSelfPermission(getActivity(), "android.permission.READ_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(getActivity(), "android.permission.CAMERA") == 0;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        ((SearchView) MenuItemCompat.getActionView(item)).setOnQueryTextListener(this);
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                                           @Override
                                           public boolean onMenuItemActionExpand(MenuItem menuItem) {
                                               return true;
                                           }

                                           @Override
                                           public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                                               SocialFragment.this.postListAdapter.setSearchResult(SocialFragment.this.postListModelArrayList);
                                               return true;
                                           }
                                       }
        );
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        this.postListAdapter.setSearchResult(filter(this.postListModelArrayList, newText));
        return true;
    }

    private ArrayList<PostListModel> filter(ArrayList<PostListModel> models, String query) {
        String query2 = query.toLowerCase();
        ArrayList arrayList = new ArrayList();
        Iterator it = models.iterator();
        while (it.hasNext()) {
            PostListModel model = (PostListModel) it.next();
            if (model.getPostedby().toLowerCase().contains(query2)) {
                arrayList.add(model);
            }
        }
        return arrayList;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SendBird.setAutoBackgroundDetection(true);
        Log.d("onActivityResult",resultCode+"  "+requestCode+"  "+data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == INTENT_REQUEST_CHOOSE_MEDIA) {
            Uri aa = data.getData();
            mVideoURI = Uri.parse(String.valueOf(aa));
            show_image(aa,INTENT_REQUEST_CHOOSE_MEDIA);
            //Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),aa);
            //  image.setImageBitmap(bitmap);
        } if (requestCode == 101) {
            Log.d("onActivityResult 101",""+data);
            Bitmap bitmap =(Bitmap)data.getExtras().get("data");
            // Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(mCurrentPhotoPath));
            SocialFragment.this.attachment_container.setVisibility(View.GONE);
            show_image(bitmap);

        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Uri aa = data.getData();
                mVideoURI = Uri.parse(String.valueOf(aa));
                show_image(aa,REQUEST_TAKE_GALLERY_VIDEO);
            }
        }
    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
    private void show_image(Gif gif) {
        Log.d("onActivityResult"," show_image "+gif.getGifUrl());
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.image,null);
        builder.setView(view);
        imageViewphoto=(ImageView)view.findViewById(R.id.image_view_social);
        EditText editText=(EditText)view.findViewById(R.id.ed_social);
        this.playbackView = (VideoView) view.findViewById(R.id.playbackView);
        this.playbackView.setVisibility(View.GONE);
        this.imageViewphoto.setVisibility(View.VISIBLE);
        // imageViewphoto.setImageResource(gif);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_img);
        requestOptions.error(R.drawable.profile_dummy);
        Log.d("array","a"+gif.getGifUrl());
        Glide.with(getActivity())
                .setDefaultRequestOptions(requestOptions)
                .load(gif.getGifUrl().trim()).into(imageViewphoto);

        builder.setMessage(R.string.dialog_fire_missiles)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                }).setPositiveButton("Post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // SocialFragment.this.attachment_container.setVisibility(View.GONE);
                sendPost(gif,editText.getText().toString().trim());
                dialogInterface.dismiss();

            }
        });
        // Create the AlertDialog object and return it
        builder.create().show();
    }
    private void show_image(Bitmap bitmap) {
        Log.d("onActivityResult"," show_image "+bitmap);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.image,null);
        builder.setView(view);
        imageViewphoto=(ImageView)view.findViewById(R.id.image_view_social);
        EditText editText=(EditText)view.findViewById(R.id.ed_social);
        imageViewphoto.setImageBitmap(bitmap);

        builder.setMessage(R.string.dialog_fire_missiles)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                }).setPositiveButton("Post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SocialFragment.this.attachment_container.setVisibility(View.GONE);
                load_imagesocial(imageViewphoto,editText.getText().toString().trim());
                dialogInterface.dismiss();

            }
        });
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    public void show_image(Uri mVideoURI, int intentRequestChooseMedia){

        Log.d("onActivityResult"," show_image "+mVideoURI);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.image,null);
        builder.setView(view);
        imageViewphoto=(ImageView)view.findViewById(R.id.image_view_social);
        EditText editText=(EditText)view.findViewById(R.id.ed_social);
        this.playbackView = (VideoView) view.findViewById(R.id.playbackView);
        if(intentRequestChooseMedia==INTENT_REQUEST_CHOOSE_MEDIA){
            imageViewphoto.setVisibility(View.VISIBLE);
            imageViewphoto.setImageURI(mVideoURI);
            playbackView.setVisibility(View.GONE);
        }
        if(intentRequestChooseMedia==REQUEST_TAKE_GALLERY_VIDEO){
            playbackView.setVisibility(View.VISIBLE);
            imageViewphoto.setVisibility(View.GONE);
            playbackView.setVideoURI(mVideoURI);
            playbackView.requestFocus();
            playbackView.start();
        }

        builder.setMessage(R.string.dialog_fire_missiles)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        attachment_container.setVisibility(View.GONE);
                    }
                }).setPositiveButton("Post", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SocialFragment.this.attachment_container.setVisibility(View.GONE);
                if(intentRequestChooseMedia==INTENT_REQUEST_CHOOSE_MEDIA){
                    load_imagesocial(imageViewphoto,editText.getText().toString().trim());
                }
                if(intentRequestChooseMedia==REQUEST_TAKE_GALLERY_VIDEO){
                    load_videosocial(mVideoURI,editText.getText().toString().trim());
                }

                dialogInterface.dismiss();

            }
        });
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    private void load_videosocial(Uri playbackView, String trim) {

        final String str2 = SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString();
        VolleyMultipartRequest r2 = new VolleyMultipartRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<NetworkResponse>()  {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);
                    Log.d("chkk ",""+resultResponse);
                    String status = jsonObject.optString("success");
                    String message=jsonObject.optString("message");
                    if (status.equals("true")) {
                        SocialFragment.this.loadPost();
                        Toast.makeText(SocialFragment.this.getActivity(), message, Toast.LENGTH_SHORT).show();
                    }else{
                        message=jsonObject.optString("message");
                        Toast.makeText(SocialFragment.this.getActivity(), message, Toast.LENGTH_SHORT).show();
                    }




                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SocialFragment.this.getActivity(), "Failed to Add Video!", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "addVideo");
                logParams.put("userid", str2);
              //  Log.d("userid",""+userid);
                logParams.put("post_time",calendar.getTime().toString());
                logParams.put("content",content);
                Log.d("",""+content);

                logParams.put("post_title",post_title);
                Log.d("post_title",""+post_title);

                logParams.put("media_type","video");
                Log.d("media_type",""+media_type);



                return logParams;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();


                params.put("video", new DataPart("file_avatar.mp4", AppHelper.getFileDataFromDrawable(getActivity(),playbackView), "vido/mp4"));
                // params.put("cover", new DataPart(" file_avatar.mp4", AppHelper.getFileDataFromDrawable(getActivity(), .getDrawable()), "video/mp4"));

                return params;
            }

        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r2);

    }
    private void load_imagesocial(ImageView imageViewphoto,String cont) {

        if(cont!="") {
            String str = CommonUtils.baseUrl;

            final String str2 = SharedPrefManager.getInstance(getActivity()).getUser().getUser_id().toString();
            VolleyMultipartRequest r2 = new VolleyMultipartRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(resultResponse);
                        Log.d("chkk ", "ff" + resultResponse);
                        String status = jsonObject.optString("success");
                        String message = jsonObject.optString("message");
                        if (status.equals("true")) {
                            SocialFragment.this.loadPost();
                            Toast.makeText(SocialFragment.this.getActivity(), "Image addded successfully", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SocialFragment.this.getActivity(), "Failed to add Image!", Toast.LENGTH_SHORT).show();
                }
            }) {
                /* access modifiers changed from: protected */
                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> logParams = new HashMap<>();
                    logParams.put("action", "addImages");
                    logParams.put("userid", userId);
                    logParams.put("post_time",calendar.getTime().toString());
                    logParams.put("content", cont);

                    logParams.put("post_title", post_title);
                    logParams.put("media_type", "image");
                    return logParams;
                }

                @Override
                protected Map<String, DataPart> getByteData() throws AuthFailureError {
                    Map<String, DataPart> params = new HashMap<>();


                    params.put("image", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getActivity(), imageViewphoto.getDrawable()), "image/jpeg"));
                    // params.put("cover", new DataPart(" file_avatar.mp4", AppHelper.getFileDataFromDrawable(getActivity(), .getDrawable()), "video/mp4"));

                    return params;
                }

            };
            MySingleTon.getInstance(getActivity()).addToRequestQue(r2);

        }else{
            Toast.makeText(getActivity(),"Status can't be empty.",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void loadNext() {
        Log.d("offset",""+offset+"-"+limit);
        offset=limit;
        limit=limit+20;
        loadPostfirst (offset,limit);
    }

}
