package com.example.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.myapplication.FingerPrintHandler.FingerprintHandler;
import com.example.myapplication.Fragments.CallFragment;
import com.example.myapplication.Fragments.ChatFragment;
import com.example.myapplication.Fragments.EventsFragment;
import com.example.myapplication.Fragments.ScheduleFragment;
import com.example.myapplication.Fragments.SocialFragment;
import com.example.myapplication.Intefaces.OnBackPressedListener;
import com.example.myapplication.Intefaces.OnFingerPrintRegisterListener;
import com.example.myapplication.Intefaces.TabNotify;
import com.example.myapplication.R;
import com.example.myapplication.Services.MyBroadCastSchedularReceiver;
import com.example.myapplication.Services.MyFirebaseMessagingService;
import com.example.myapplication.Services.Schedular_Sender;
import com.example.myapplication.Utils.ActivityUtils;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.Consts;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.PrefUtils;
import com.example.myapplication.Utils.PreferenceUtils;
import com.example.myapplication.Utils.SharedPrefManager;
import com.example.myapplication.Utils.SyncManagerUtils;
import com.example.myapplication.call.CallActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.judemanutd.autostarter.AutoStartPermissionHelper;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBirdPushHelper;
import com.sendbird.android.User;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.handler.SendBirdCallListener;
import com.sendbird.syncmanager.SendBirdSyncManager;
import com.sendbird.syncmanager.handler.CompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.myapplication.GroupChats.GroupChannelListFragment.EXTRA_GROUP_CHANNEL_URL;

@RequiresApi(api = Build.VERSION_CODES.M)
public class UserNavgation extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnFingerPrintRegisterListener, TabNotify {
//    EditText name;
//    Button btn;
    private static final String TAG = "lifecycle";
    public static final String APP_ID = "35A602C1-6766-46FE-94BE-7553074C1011";
    private final int REQUEST_OVERLAY_PERMISSION = 204;
    private ProgressDialog progressDialog;
    private TabLayout tabLayout;
    private ImageButton search_button_chat;
    private Menu menu;
    private CircleImageView user_imageView_nav;
    private TextView user_name_id_nav;
    private TextView user_email_nav;
    private TextView user_resident;
    private String userId;
//    private Context context;
public static final String KEY_NAME = "buzz_key";
    public static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHANNEL_ACTIVITY";
    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    // static final String DEFAULT_KEY_NAME = "default_key";
    /* access modifiers changed from: private */
    public Cipher cipher;
    int count = 0;
    private boolean isRunForCall;
    private KeyStore keyStore;
    protected OnBackPressedListener onBackPressedListener;
    public String user_id;
    AlertDialog alertDialog;
    private KeyGenerator keyGenerator;
    private Cipher defaultCipher;
    private Cipher cipherNotInvalidated;
    private String deviceId;
    private TelephonyManager telephonyManager;

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    public static void start(Context context, boolean isRunForCall2) {
        Intent intent = new Intent(context, UserNavgation.class);
        // intent.addFlags(268566528);
        intent.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, isRunForCall2);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       PreferenceUtils.init(this);
        setContentView( R.layout.activity_user_navgation);
        PreferenceUtils.init(UserNavgation.this);

        MyFirebaseMessagingService.strArrList.clear();

        //Init Scheduler service.
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, MyBroadCastSchedularReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        //startService(new Intent(this, Schedular_Sender.class).putExtra("start",true));

        if ((getIntent().getFlags() & 4194304) != 0) {
            moveTaskToBack(true);
            return;
        }
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setProgressStyle(0);
        this.progressDialog.setMessage("Loading...Please Wait");
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setCancelable(true);
            commonMethodToSetData();

        connect();
        startSchedularService();
    }

    public void commonMethodToSetData() {
        initFields();
        if(getSupportFragmentManager().getFragments() instanceof ChatFragment){



        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).addToBackStack(null).commit();


        }


        this.tabLayout = (TabLayout) findViewById(R.id.tabs);
        this.search_button_chat = (ImageButton) findViewById(R.id.search_button_chat);
        this.search_button_chat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(UserNavgation.this, FirebaseAnalytics.Event.SEARCH, Toast.LENGTH_SHORT).show();
              //  UserNavgation.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragments()).addToBackStack(null).commit();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Snackbar.make(view, (CharSequence) "Replace with your own action", Snackbar.LENGTH_LONG).setAction((CharSequence) "Action", (View.OnClickListener) null).show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ImageButton open_nav=(ImageButton)findViewById(R.id.open_nav);
        open_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        View headerview = navigationView.getHeaderView(0);
        this.menu = navigationView.getMenu();
        if (SharedPrefManager.getInstance(this).isFingerPrintOn()) {
            this.menu.findItem(R.id.nav_finger_login).setTitle("Fingerprint login: On");
        } else
            {
            this.menu.findItem(R.id.nav_finger_login).setTitle("Fingerprint login: Off");

        }
        this.user_imageView_nav = (CircleImageView) headerview.findViewById(R.id.user_imageView_nav);
        this.user_name_id_nav = (TextView) headerview.findViewById(R.id.user_name_id_nav);
        String secret_id = SharedPrefManager.getInstance(this).getUser().getUser_name().toString();
        if (secret_id != null) {
            this.user_name_id_nav.setText(secret_id);
        }
        this.user_email_nav = (TextView) headerview.findViewById(R.id.user_email_nav);
        this.user_email_nav.setText(SharedPrefManager.getInstance(this).getUser().getUser_secret_id().toString());
        this.user_resident = (TextView) headerview.findViewById(R.id.user_resident);
        this.user_resident.setText(SharedPrefManager.getInstance(this).getUser().getResident().toString());
//        if (this.isRunForCall && this.webRtcSessionManager.getCurrentSession() != null) {
//            CallActivity.start(this, true);
//        }
        this.user_imageView_nav.setOnClickListener(this);
        String profilepic = SharedPrefManager.getInstance(this).getUser().getUser_image();
        if(profilepic!=null) {
            Glide.with(UserNavgation.this).load(profilepic).into(user_imageView_nav);

        }
        includetabs();
    }
    private void connect() {

        Log.d("lifecycle","connect"+SharedPrefManager.getInstance(UserNavgation.this).getUser().getUser_id());
         userId = String.valueOf(SharedPrefManager.getInstance(UserNavgation.this).getUser().getUser_id()).replaceAll("\\s", "");
        String username=SharedPrefManager.getInstance(UserNavgation.this).getUser().getUser_name();

        if (SendBird.getConnectionState() != SendBird.ConnectionState.OPEN) {
            SendBird.connect(userId, new SendBird.ConnectHandler() {
                @Override
                public void onConnected(User user, SendBirdException e) {

                    SendBirdPushHelper.registerPushHandler(new MyFirebaseMessagingService());

                    int check=SharedPrefManager.getInstance(UserNavgation.this).get("check",1);

                    Log.d("lifecycle","usernavigation onCompleted"+e+check);
                    if (e != null) {
                        e.printStackTrace();
                    } else {
                        Log.d("lifecycle", "else --" + getIntent().getStringExtra("profile_pic"));

                        if(check==1){


                            Log.d("lifecycle", "2--" + getIntent().getStringExtra("profile_pic"));
                            SharedPrefManager.getInstance(UserNavgation.this).save("check",2);
                        }
                        checkExtra();
                    }
                }
            });

        } else {
            checkExtra();
        }
        SyncManagerUtils.setup(UserNavgation.this, userId, new CompletionHandler()
        {
            @Override
            public void onCompleted(SendBirdException e) {
                Log.d("lifecycle","usernavigatio setup onCompleted"+e);
                if (e != null) {


                    e.printStackTrace();
                    return;
                }
                PreferenceUtils.setConnected(true);

            }
        });
    }
    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
//        if (intent.getExtras() != null) {
//            this.isRunForCall = intent.getExtras().getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
//            if (this.isRunForCall && this.webRtcSessionManager.getCurrentSession() != null)
//            {
//                CallActivity.start(this, true);
//            }
//        }
        connect();
    }

    private void initFields()
    {
        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            this.isRunForCall = extras.getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
//        }
//        Log.d("callingdetais","2 usernav");
//        this.webRtcSessionManager = WebRtcSessionManager.getInstance(getApplicationContext());
    }
    private void includetabs() {
        this.tabLayout.addTab(this.tabLayout.newTab().setText((CharSequence) "Chat"));
        this.tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.app_color_voilet));
        this.tabLayout.addTab(this.tabLayout.newTab().setText((CharSequence) "Call"));
        this.tabLayout.addTab(this.tabLayout.newTab().setText((CharSequence) "Social"));
        this.tabLayout.addTab(this.tabLayout.newTab().setText((CharSequence) "Event"));
        this.tabLayout.addTab(this.tabLayout.newTab().setText((CharSequence) "Timer"));
        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                if (UserNavgation.this.tabLayout.getSelectedTabPosition() == 0) {
                    UserNavgation.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).addToBackStack(null).commit();
                } else if (UserNavgation.this.tabLayout.getSelectedTabPosition() == 1) {
                    UserNavgation.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CallFragment()).addToBackStack(null).commit();
                } else if (UserNavgation.this.tabLayout.getSelectedTabPosition() == 2) {
                    UserNavgation.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SocialFragment()).addToBackStack(null).commit();
                } else if (UserNavgation.this.tabLayout.getSelectedTabPosition() == 3) {
                   UserNavgation.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EventsFragment()).addToBackStack(null).commit();
                } else if (UserNavgation.this.tabLayout.getSelectedTabPosition() == 4) {
                   UserNavgation.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScheduleFragment()).addToBackStack(null).commit();
                }
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }

            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(Settings.canDrawOverlays(this)){

            }
        }

    }


    public void updateCurrentUserInfo(String userNickname, String profile_pic) {
        SendBird.updateCurrentUserInfo(userNickname, profile_pic, new SendBird.UserInfoUpdateHandler() {
            public void onUpdated(SendBirdException e) {
                Log.d("lifecycle", "1-"+e);
                if (e == null) {
                }
            }
        });

    }
    
    public boolean initSendBirdCall(String appId) {
        Log.d(TAG, "initSendBirdCall(appId: " + appId + ")");
        Context context = getApplicationContext();

        if (TextUtils.isEmpty(appId)) {
            appId = APP_ID;
        }

        if (SendBirdCall.init(context, appId)) {
            SendBirdCall.removeAllListeners();
            SendBirdCall.addListener(UUID.randomUUID().toString(), new SendBirdCallListener() {
                @Override
                public void onRinging(DirectCall call) {
                    Log.d(TAG, "onRinging() => callId: " + call.getCallId());
                    if (CallActivity.sIsRunning) {
                        call.end();
                        return;
                    }
                    ActivityUtils.startCallActivityAsCallee(context, call);
                }
            });
            return true;
        }
        return false;
    }
    public void updateCurrentUserPushToken(final String userId, final String userNickname) {
        Log.d("firebase",""+ FirebaseInstanceId.getInstance().getToken());
        SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), (SendBird.RegisterPushTokenWithStatusHandler) new SendBird.RegisterPushTokenWithStatusHandler() {
            public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                if (e != null) {
                    UserNavgation userNavgation = UserNavgation.this;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Push token registered problem ");
                    sb.append(e.getCode());
                    sb.append(":");
                    sb.append(e.getMessage());
                    // Toast.makeText(userNavgation, sb.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                // UserNavgation.this.updateCurrentUserInfo(userNickname, userId);
                //Toast.makeText(UserNavgation.this, "Push token registered.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            startActivity(new Intent(this, UserDetailsActivity.class));
        } else if (id == R.id.nav_dashboard) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_posts) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SocialFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_findfriends) {
            startActivity(new Intent(this, FriendsActivity.class));
        } else if (id == R.id.nav_share_me) {
            Intent sharingIntent = new Intent("android.intent.action.SEND");
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra("android.intent.extra.SUBJECT", "Subject here");
            sharingIntent.putExtra("android.intent.extra.TEXT", "I am using Buzzapp chat and want you to join me. Download buzzapp from Android Play Store: https://play.google.com/store/apps/details?id=" +"com.example.hp.chatapplication" );
            startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(this, HelpActivity.class));
        } else if (id == R.id.nav_finger_login) {
            if (SharedPrefManager.getInstance(this).isFingerPrintOn()) {

                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setMessage((CharSequence) "Are you sure you want to deactivate the fingerprint login.");
                ab.setCancelable(false);
                ab.setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                ab.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        final String iduser=SharedPrefManager.getInstance(UserNavgation.this).getUser().getUser_id();
                        StringRequest r2 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.optString("success");
                                    if (status.equals("true")){
                                        Toast.makeText(UserNavgation.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        SharedPrefManager.getInstance(UserNavgation.this).saveFingerPrintOn(false);

                                        if (SharedPrefManager.getInstance(UserNavgation.this).isFingerPrintOn()) {
                                            menu.findItem(R.id.nav_finger_login).setTitle("Fingerprint login: On");
                                        } else {
                                            menu.findItem(R.id.nav_finger_login).setTitle("Fingerprint login: Off");

                                        }
                                    }
                                } catch (JSONException e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                //  Toast.makeText(UserNavgation.this, "Something Went Wong", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            /* access modifiers changed from: protected */
                            public Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> logParams = new HashMap<>();
                                logParams.put("action", "removedeviceid");
                                logParams.put("userid", iduser);
                                return logParams;
                            }
                        };
                        MySingleTon.getInstance(UserNavgation.this).addToRequestQue(r2);



                    }
                });
                ab.create();
                ab.show();
            } else {
                AlertDialog.Builder ab2 = new AlertDialog.Builder(this);
                ab2.setMessage((CharSequence) "Press Ok and Place your finger over the sensor to link your buzzapp account.");
                ab2.setCancelable(false);
                ab2.setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                ab2.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (Build.VERSION.SDK_INT >= 23) {
                            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                            KeyguardManager keyguardManager = (KeyguardManager) UserNavgation.this.getSystemService(Context.KEYGUARD_SERVICE);
                            if (fingerprintManager==null) {
                                UserNavgation.this.showDialog("Your device does not have a fingerprint sensor");
                            } else if (!fingerprintManager.isHardwareDetected()) {
                                UserNavgation.this.showDialog("Your device does not have a fingerprint sensor");
                            } else if (ActivityCompat.checkSelfPermission(UserNavgation.this, "android.permission.USE_FINGERPRINT") != 0) {
                                UserNavgation.this.showDialog("Fingerprint authentication permission not enabled");
                            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                                UserNavgation.this.showDialog("Register at least one fingerprint in Settings");
                            } else if (!keyguardManager.isKeyguardSecure()) {
                                UserNavgation.this.showDialog("Lock screen security not enabled in Settings");
                            } else {
                                UserNavgation.this.progressDialog = new ProgressDialog(UserNavgation.this);
                                UserNavgation.this.progressDialog.setProgressStyle(0);
                                UserNavgation.this.progressDialog.setMessage("Place your finger over the sensor to link your buzzapp account.");
                                UserNavgation.this.progressDialog.setIndeterminate(true);
                                UserNavgation.this.progressDialog.setCancelable(true);
                                UserNavgation.this.progressDialog.show();
                                UserNavgation.this.generateKey();
                                Log.d("figerprint","value"+"-"+SharedPrefManager.getInstance(UserNavgation.this).isFingerPrintOn());
//                                if (initCipher(defaultCipher, KEY_NAME)) {
//                                    FingerprintAuthenticationDialogFragment fragment = new FingerprintAuthenticationDialogFragment ();
//                                    fragment.setCryptoObject(new FingerprintManager.CryptoObject(cipher));
//                                    fragment.setStage(
//                                            FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
//                                    fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
//                                }
                                if (initCipher(defaultCipher, KEY_NAME)) {


                                    new FingerprintHandler(UserNavgation.this, "NA", true, UserNavgation.this).startAuth(fingerprintManager, new FingerprintManager.CryptoObject(cipher));

                                }
                            }
                        }
                        else {
                            UserNavgation.this.showDialog("Your device does not have a fingerprint sensor");
                        }
                    }
                });
                ab2.create();
                ab2.show();
            }
        } else if (id == R.id.nav_logout) {

            logout();

        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer((int) GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onFingerPrintRegister(boolean isRegister,String message) {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
        Log.d("abc",""+SharedPrefManager.getInstance(this).isFingerPrintOn()+isRegister);

        if (!SharedPrefManager.getInstance(this).isFingerPrintOn()) {
            // this.menu.findItem(R.id.nav_finger_login).setTitle("Fingerprint login: On");
            this.telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != 0) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
            } else {
                deviceId = this.telephonyManager.getDeviceId();
            }
            if(isRegister) {
                show_alert();
            }
           else {
                Toast.makeText(UserNavgation.this,message,Toast.LENGTH_LONG).show();
           }



            return;
        }
        this.menu.findItem(R.id.nav_finger_login).setTitle("Fingerprint login: Off");
    }

    private void show_alert() {
        Log.d("abc","sjow alert");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserNavgation.this);
        alertDialog.setTitle("PASSWORD");
        alertDialog.setMessage("Enter Password");

        final EditText input = new EditText(UserNavgation.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.flag_id);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String password = input.getText().toString();
                        if (password.compareTo("") != 0) {
                            send_request(password);
                        }
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void send_request(String pass) {
        Log.d("pass",""+pass);
        Log.d("username",""+SharedPrefManager.getInstance(UserNavgation.this).getUser().getUser_id().toString());
        Log.d("pass",""+deviceId);


        StringRequest sr=new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp","e"+response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equals("true")){
                        Toast.makeText(UserNavgation.this, "Your Fingerprint Has been Setup..", Toast.LENGTH_SHORT).show();
                        SharedPrefManager.getInstance(UserNavgation.this).saveFingerPrintOn(true);
                        if (SharedPrefManager.getInstance(UserNavgation.this).isFingerPrintOn()) {
                            UserNavgation.this.menu.findItem(R.id.nav_finger_login).setTitle("Fingerprint login: On");

                        }
                    }
                }catch (Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("eror","e"+error);

            }
        }){
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "setupfingerprint");
                logParams.put("device_id",deviceId);
                logParams.put("password",pass);
                logParams.put("user_id",SharedPrefManager.getInstance(UserNavgation.this).getUser().getUser_id().toString());
                return logParams;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(sr);

    }


    @Override
    public void notifyTab(int position) {
    //    this.tabLayout.getTabAt(position).select();
    }

    private void checkExtra() {
        if (getIntent().hasExtra(EXTRA_GROUP_CHANNEL_URL)) {
            String extraChannelUrl = getIntent().getStringExtra(EXTRA_GROUP_CHANNEL_URL);
            Intent mainIntent = new Intent(UserNavgation.this, Main2Activity.class);
            mainIntent.putExtra(EXTRA_GROUP_CHANNEL_URL, extraChannelUrl);
            startActivity(mainIntent);
        }
    }
    @Override
    protected void onResume() {
        SendBird.addConnectionHandler(getConnectionHandlerId(), new SendBird.ConnectionHandler() {
            @Override
            public void onReconnectStarted() {
                SendBirdSyncManager.getInstance().pauseSync();
            }

            @Override
            public void onReconnectSucceeded() {
                SendBirdSyncManager.getInstance().resumeSync();
            }

            @Override
            public void onReconnectFailed() {
            }
        });
        super.onResume();
        Log.d("lifecycle"," user onResume"+SharedPrefManager.getInstance(this).get("checkautostart",1)
                + AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this)+"="
        );
//        int checkautostart=0;
//        if(AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this)){
//            checkautostart =SharedPrefManager.getInstance(this).get("checkautostart",1);
//            String manufacturer = android.os.Build.MANUFACTURER;
//
//            if (checkautostart == 1) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Request Permission")
//                        .setMessage("App require Autostart permission for Calling service..Press OK to enable it..!!")
//                        .setPositiveButton("OK",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        alertDialog.dismiss();
//                                        SharedPrefManager.getInstance(UserNavgation.this).save("checkautostart", 2);
//                                        AutoStartPermissionHelper.getInstance().getAutoStartPermission(UserNavgation.this);
//
//                                    }
//                                });
//                alertDialog = builder.create();
//                alertDialog.show();
//            }
//            if ("xiaomi" .equalsIgnoreCase(manufacturer)) {
//                if (checkautostart == 2) {
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("Request Permission")
//                            .setMessage("App require Show Popup in Background permission for Calling service..Press OK to enable it..!!")
//                            .setPositiveButton("OK",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            SharedPrefManager.getInstance(UserNavgation.this).save("checkautostart", 3);
//                                            try {
//                                                // MIUI 8
//                                                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//                                                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
//                                                localIntent.putExtra("extra_pkgname", getPackageName());
//                                                alertDialog.dismiss();
//                                                startActivity(localIntent);
//
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
//                    alertDialog = builder.create();
//                    alertDialog.show();
//                }
//            }
//        }
    }

    @Override
    protected void onPause() {
        Log.d("lifecycle"," user onPause");
         SendBird.removeConnectionHandler(getConnectionHandlerId());
        super.onPause();
    }

    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
    /* access modifiers changed from: protected */
    @TargetApi(23)
    public void generateKey() {
        try {
            this.keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            try {
                this.keyStore.load(null);
                keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_VERIFY).setBlockModes(new String[]{"CBC"}).setUserAuthenticationRequired(true).setEncryptionPaddings(new String[]{"PKCS7Padding"}).build());
                keyGenerator.generateKey();
            } catch (IOException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | CertificateException e2) {
                throw new RuntimeException(e2);
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException e3) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e3);
        }
        // defaultCipher;
        //  cipherNotInvalidated;
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipherNotInvalidated = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }
        FingerprintManager fingerprintManager = getSystemService(FingerprintManager.class);
        KeyguardManager keyguardManager = getSystemService(KeyguardManager.class);
        createKey(KEY_NAME, true);
        createKey(KEY_NAME_NOT_INVALIDATED, false);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            keyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showDialog(String s) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage((CharSequence) s);
        ab.setCancelable(false);
        ab.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        ab.create();
        ab.show();
    }


    private void logout() {

        if (!SharedPrefManager.getInstance(this).isLoggedIn())
        {

            startActivity(new Intent(UserNavgation.this, LoginRegistrationActivity.class));
            finish();
            return;
        }
//        SendBird.unregisterPushTokenAllForCurrentUser(new SendBird.UnregisterPushTokenHandler() {
//            @Override
//            public void onUnregistered(SendBirdException e) {
//                SendBird.disconnect(new SendBird.DisconnectHandler() {
//                    @Override
//                    public void onDisconnected() {
//                        Toast.makeText(getApplicationContext(),"on disconnect navi",Toast.LENGTH_SHORT).show();
//
////                        SendBirdSyncManager.getInstance().pauseSync();
////                        String userId = PreferenceUtils.getUserId();
////                        // if you want to clear cache of specific user when disconnect, you can do like this.
////
////                        SendBirdSyncManager.getInstance().clearCache(userId);
////
////                        PreferenceUtils.setConnected(false);
//
//                    }
//                });
//            }
//        });
//
//        SendBirdCall.deauthenticate(PrefUtils.getPushToken(UserNavgation.this), new com.sendbird.calls.handler.CompletionHandler() {
//            @Override
//            public void onResult(com.sendbird.calls.SendBirdException e) {
//                Toast.makeText(getApplicationContext(),"on result",Toast.LENGTH_SHORT).show();
//
//                PrefUtils.setUserId(UserNavgation.this, null);
//               PrefUtils.setAccessToken(UserNavgation.this, null);
//               PrefUtils.setCalleeId(UserNavgation.this, null);
//                PrefUtils.setPushToken(UserNavgation.this, null);
//
//            }
//        });
        SharedPrefManager.getInstance(UserNavgation.this).save(SharedPrefManager.KEY_USER_ID);
         SharedPrefManager.getInstance(UserNavgation.this).logout(UserNavgation.this);




    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (this.onBackPressedListener != null) {
                this.onBackPressedListener.doBack();
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen((int) GravityCompat.START)) {
            drawer.closeDrawer((int) GravityCompat.START);
        } else if (this.onBackPressedListener != null) {
            this.onBackPressedListener.doBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected String getConnectionHandlerId() {
        return "CONNECTION_HANDLER_GROUP_CHANNEL_ACTIVITY";
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!canDrawOverlays(this)) {
            requestPermission(REQUEST_OVERLAY_PERMISSION);
        }
    }

    private void requestPermission(int requestCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return true;
        } else {
            return Settings.canDrawOverlays(context);
        }
    }

    private void startSchedularService() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
        }
    }
}
