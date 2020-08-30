package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.CountryPicker.CountryPicker;
import com.example.myapplication.CountryPicker.CountryPickerListener;
import com.example.myapplication.ModelClasses.User;
import com.example.myapplication.R;
import com.example.myapplication.Services.MyFirebaseMessagingService;
import com.example.myapplication.Utils.BaseUrl_ConstantClass;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.Consts;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.PrefUtils;
import com.example.myapplication.Utils.PreferenceUtils;
import com.example.myapplication.Utils.PushUtils;
import com.example.myapplication.Utils.SharedPrefManager;
import com.example.myapplication.Utils.SyncManagerUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBirdPushHelper;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.syncmanager.SendBirdSyncManager;
import com.sendbird.syncmanager.handler.CompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginRegistrationActivity extends BaseActivity implements View.OnClickListener {
    String TAG = "LifeCycle";
    private static final String URL = BaseUrl_ConstantClass.BASE_URL;
    static String deviceId = "NA";
    TextView Sign_in_SignUp_text;
    AlertDialog alertDialog;
    Button button_SignUp_regiter;
    ImageView check_id, check_id_mobile, check_id_email;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String useridpattern = "[a-zA-Z0-9._-]";
    ImageView close_id, close_id_mobile, close_id_email;
    TextView countryDialCode;
    ImageView countryIcon;
    String country_code;
    ImageView country_code_popup_iv;
    EditText current_user_id;
    EditText current_user_passkey;
    UUID deviceid;
    EditText email;
    boolean firstRun;
    LinearLayout forget_layout;
    TextView forget_password;
    ImageView hide_password;
    public Boolean isClicked = Boolean.valueOf(false);
    ImageView iv_view_passkey;
    LinearLayout ll_Passkey;
    LinearLayout ll_secret_id;
    String message;
    EditText mobile_no;
    //  QBUser newUser;
    EditText passkey;
    ProgressDialog pd;
    CountryPicker picker;
    PopupWindow popupWindow;
    String printmessage;
    ProgressDialog progressDialog;
    //  QBUser qbUser;
    RelativeLayout relativeLayout_login;
    // protected QBResRequestExecutor requestExecutor;
    EditText secret_ID;
    //
    //SessionManager sessionManager;
    SharedPreferences settings;
    // SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
    Button signIn_with_finger_print;
    Button sign_in;
    Button sign_in_page_button;
    Button sign_up_button;
    TextView sign_up_text;
    TelephonyManager telephonyManager;
    TextView tv_PassKey;
    TextView tv_secret_id;
    // private QBUser userForSave;
    String user_countryDialCode;
    String user_email;
    String user_id;
    String user_img;
    LinearLayout user_login_layout;
    String user_mobile;
    String user_name;
    LinearLayout user_registration_layout;
    View view;
    private TextView tv_terms_and_cond;
    private TextView tv_privacy_policy;
    private String name;
    String em;
    String mobile;


    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);

        if (!checkPermission()) {
            requestPermission();
        }

        this.countryDialCode = (TextView) findViewById(R.id.countryDialCode);
        this.countryIcon = (ImageView) findViewById(R.id.countryIcon);
        //  this.requestExecutor = new QBResRequestExecutor();
        this.picker = CountryPicker.newInstance("Select Country");
        this.picker.setListener(new CountryPickerListener() {
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                LoginRegistrationActivity.this.countryIcon.getLayoutParams().height = 50;
                LoginRegistrationActivity.this.countryIcon.getLayoutParams().width = 50;
                LoginRegistrationActivity.this.countryDialCode.setText(dialCode);
                LoginRegistrationActivity.this.user_countryDialCode = LoginRegistrationActivity.this.countryDialCode.getText().toString().trim();
                LoginRegistrationActivity.this.countryIcon.setImageResource(flagDrawableResID);
                LoginRegistrationActivity.this.picker.dismiss();
            }
        });
        //  this.sessionManager = new SessionManager(getApplicationContext());
        hideSoftKeyboard();
        this.close_id_mobile = (ImageView) findViewById(R.id.close_id_mobile);
        this.close_id_email = (ImageView) findViewById(R.id.close_id_email);
        this.Sign_in_SignUp_text = (TextView) findViewById(R.id.Sign_in_SignUp_text);
        this.tv_PassKey = (TextView) findViewById(R.id.tv_PassKey);
        this.tv_secret_id = (TextView) findViewById(R.id.tv_secret_id);
        this.button_SignUp_regiter = (Button) findViewById(R.id.button_SignUp_regiter);
        this.button_SignUp_regiter.setOnClickListener(this);
        this.iv_view_passkey = (ImageView) findViewById(R.id.iv_view_passkey);
        this.tv_terms_and_cond = (TextView) findViewById(R.id.tv_terms_and_cond);
        this.tv_privacy_policy = (TextView) findViewById(R.id.tv_privacy_policy);
        tv_privacy_policy.setOnClickListener(this);
        tv_terms_and_cond.setOnClickListener(this);
        this.check_id = (ImageView) findViewById(R.id.check_id);
        this.check_id_email = (ImageView) findViewById(R.id.check_id_email);
        this.check_id_mobile = (ImageView) findViewById(R.id.check_id_mobile);
        this.close_id = (ImageView) findViewById(R.id.close_id);
        this.iv_view_passkey.setOnClickListener(this);
        this.telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        /*if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != 0) {
            requestPermissions(new String[]{"android.permission.READ_PHONE_STATE"}, 1);
        } else {
            //deviceId = this.telephonyManager.getDeviceId();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                deviceId = Settings.Secure.getString(
                        getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } else {
                if (telephonyManager.getDeviceId() != null) {
                    deviceId = telephonyManager.getDeviceId();
                } else {
                    deviceId = Settings.Secure.getString(
                            getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
            }
        }

        this.ll_secret_id = (LinearLayout) findViewById(R.id.ll_secret_id);
        this.ll_secret_id.setOnClickListener(this);
        this.ll_Passkey = (LinearLayout) findViewById(R.id.ll_Passkey);
        this.ll_Passkey.setOnClickListener(this);

        this.hide_password = (ImageView) findViewById(R.id.hide_password);
        this.hide_password.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginRegistrationActivity.this.isClicked = Boolean.valueOf(!LoginRegistrationActivity.this.isClicked.booleanValue());
                if (LoginRegistrationActivity.this.isClicked.booleanValue()) {
                    LoginRegistrationActivity.this.current_user_passkey.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    LoginRegistrationActivity.this.current_user_passkey.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        this.iv_view_passkey = (ImageView) findViewById(R.id.iv_view_passkey);
        this.iv_view_passkey.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginRegistrationActivity.this.isClicked = Boolean.valueOf(!LoginRegistrationActivity.this.isClicked.booleanValue());
                if (LoginRegistrationActivity.this.isClicked.booleanValue()) {
                    LoginRegistrationActivity.this.passkey.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    LoginRegistrationActivity.this.passkey.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        this.relativeLayout_login = (RelativeLayout) findViewById(R.id.relativeLayout_login);
        this.sign_up_button = (Button) findViewById(R.id.sign_up_button);
        this.sign_up_button.setOnClickListener(this);
        this.email = (EditText) findViewById(R.id.user_email);
        this.mobile_no = (EditText) findViewById(R.id.user_mobile);
        this.secret_ID = (EditText) findViewById(R.id.secret_id);
        this.passkey = (EditText) findViewById(R.id.user_passkey);
        this.user_registration_layout = (LinearLayout) findViewById(R.id.user_registration_layout);
        this.user_login_layout = (LinearLayout) findViewById(R.id.user_login_layout);
        this.sign_in_page_button = (Button) findViewById(R.id.sign_in_page_button);
        this.forget_layout = (LinearLayout) findViewById(R.id.forget_layout);
        this.sign_in_page_button.setOnClickListener(this);
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("Loading...");
        this.progressDialog.setCancelable(false);
        this.progressDialog.setIndeterminate(true);
        this.signIn_with_finger_print = (Button) findViewById(R.id.signIn_with_finger_print);
        this.signIn_with_finger_print.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(LoginRegistrationActivity.this, SignInWithFingerPrint.class);
                intent.putExtra("DEVICE_ID", LoginRegistrationActivity.deviceId);
                //intent.putExtra("USER_ID",user_id);
                LoginRegistrationActivity.this.startActivity(intent);
            }
        });
        this.forget_layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(LoginRegistrationActivity.this.getBaseContext(), ForgotPasswordActivity.class);
                intent.putExtra("EXTRA_SESSION_ID", "secret");
                LoginRegistrationActivity.this.startActivity(intent);
                LoginRegistrationActivity.this.current_user_id.setText("");
                LoginRegistrationActivity.this.current_user_passkey.setText("");
            }
        });
        this.tv_secret_id.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(LoginRegistrationActivity.this.getBaseContext(), ForggotBuzzid.class);
                intent.putExtra("EXTRA_SESSION_ID", "secret");
                LoginRegistrationActivity.this.startActivity(intent);
                LoginRegistrationActivity.this.current_user_id.setText("");
                LoginRegistrationActivity.this.current_user_passkey.setText("");
            }
        });
        this.tv_PassKey.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(LoginRegistrationActivity.this.getBaseContext(), ForgotPasswordActivity.class);
                intent.putExtra("EXTRA_SESSION_ID", "secret");
                LoginRegistrationActivity.this.startActivity(intent);
                LoginRegistrationActivity.this.current_user_id.setText("");
                LoginRegistrationActivity.this.current_user_passkey.setText("");
            }
        });
        this.current_user_id = (EditText) findViewById(R.id.current_user_id);
        this.current_user_passkey = (EditText) findViewById(R.id.current_user_passkey);
        this.sign_in = (Button) findViewById(R.id.sign_in_button);
        this.sign_up_text = (TextView) findViewById(R.id.sign_up_button);
        this.sign_up_text.setOnClickListener(this);
        this.sign_in.setOnClickListener(this);
        if (SharedPrefManager.getInstance(this).isConnected() == null) {

            return;
        }
        if (SharedPrefManager.getInstance(this).isConnected().equals("true")) {

            SharedPrefManager.getInstance(this).save("check", 2);
            Log.d("updateCurrentUserInfo", "login" + CommonUtils.check);

            Intent intent = new Intent(this, UserNavgation.class);
            startActivity(intent);
            finish();
            return;
        }
        SharedPrefManager.getInstance(this).logout(this);

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE",
                        "android.permission.CAMERA",
                        "android.permission.READ_CONTACTS",
                        "android.permission.CALL_PHONE",
                        "android.permission.RECORD_AUDIO",
                        "android.permission.ACCESS_FINE_LOCATION",
                        "android.permission.READ_PHONE_STATE"},
                5);
    }

    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0
                && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0
                && ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") == 0
                && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0
                && ContextCompat.checkSelfPermission(this, "android.permission.CALL_PHONE") == 0
                && ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS") == 0
                && ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") == 0
                && ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") == 0
                ;
    }

    @SuppressLint("HardwareIds")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == 0 && ActivityCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") == 0) {
            //deviceId = this.telephonyManager.getDeviceId();
            //TODO: App will not get device id in Android >= Q
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                deviceId = Settings.Secure.getString(
                        getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } else {
                if (telephonyManager.getDeviceId() != null) {
                    deviceId = telephonyManager.getDeviceId();
                } else {
                    deviceId = Settings.Secure.getString(
                            getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
            }


        }
    }

    @SuppressLint({"ResourceType"})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_SignUp_regiter) {
            // deviceId = this.telephonyManager.getDeviceId();
            registerNewUser();


        } else if (id == R.id.tv_PassKey) {
            Intent intent = new Intent(getBaseContext(), ForgotPasswordActivity.class);
            intent.putExtra("EXTRA_SESSION_ID", "pass");
            startActivity(intent);
        } else if (id == R.id.tv_terms_and_cond) {
            Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/blog/terms-conditions-mobile-apps/"));
            startActivity(Getintent);
        } else if (id == R.id.tv_privacy_policy) {
            Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.iubenda.com/en/help/11552-privacy-policy-for-android-apps"));
            startActivity(Getintent);
        } else if (id != R.id.tv_secret_id) {
            switch (id) {
                case R.id.sign_in_button /*2131297055*/:
                    close_id.setVisibility(View.GONE);
                    check_id.setVisibility(View.GONE);
                    check_id_mobile.setVisibility(View.GONE);
                    close_id_mobile.setVisibility(View.GONE);
                    check_id_email.setVisibility(View.GONE);
                    close_id_email.setVisibility(View.GONE);


                    logInUser();

                    return;
                case R.id.sign_in_page_button /*2131297056*/:
                    close_id.setVisibility(View.GONE);
                    check_id.setVisibility(View.GONE);
                    this.secret_ID.setError(null);
                    this.mobile_no.setError(null);
                    this.email.setError(null);
                    this.passkey.setError(null);
                    this.secret_ID.setText("");
                    this.mobile_no.setText("");
                    this.email.setText("");
                    this.passkey.setText("");
                    this.countryIcon.setVisibility(8);
                    this.countryDialCode.setText("+44");
                    this.user_registration_layout.setVisibility(8);
                    this.user_login_layout.setVisibility(0);
                    this.sign_up_button.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.signin_button_voilet));
                    this.sign_up_button.setTextColor(Color.parseColor("#FFFFFF"));
                    this.sign_in_page_button.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.signin_button));
                    this.sign_in_page_button.setTextColor(Color.parseColor("#000000"));
                    this.Sign_in_SignUp_text.setText("Sign In");
                    return;
                case R.id.sign_up_button /*2131297057*/:
                    this.current_user_id.setText("");
                    this.current_user_id.setError(null);
                    this.current_user_passkey.setText("");
                    this.countryIcon.setVisibility(0);
                    this.countryIcon.getLayoutParams().height = 50;
                    this.countryIcon.getLayoutParams().width = 50;
                    this.countryIcon.setImageResource(R.drawable.flag_fx);
                    this.current_user_passkey.setError(null);
                    this.user_registration_layout.setVisibility(0);
                    this.user_login_layout.setVisibility(8);
                    this.sign_up_button.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.signin_button));
                    this.sign_in_page_button.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.signin_button_voilet));
                    this.sign_up_button.setTextColor(Color.parseColor("#000000"));
                    this.sign_in_page_button.setTextColor(Color.parseColor("#FFFFFF"));
                    this.Sign_in_SignUp_text.setText("Sign up");
                    return;
                default:
                    return;
            }
        } else {
            Intent intent2 = new Intent(getBaseContext(), ForgotPasswordActivity.class);
            intent2.putExtra("EXTRA_SESSION_ID", "secret");
            startActivity(intent2);
        }
    }

    private void changePassword() {

    }

    private void logInUser() {

        Log.d(TAG, "logInUser");
        String currentUser = this.current_user_id.getText().toString().trim();
        final String currentUserPasskey = this.current_user_passkey.getText().toString().trim();
        String str = BaseUrl_ConstantClass.BASE_URL;
        if (TextUtils.isEmpty(currentUser)) {
            this.current_user_id.setError("Buzz ID can't be empty ");
            this.current_user_id.requestFocus();
        } else if (TextUtils.isEmpty(currentUserPasskey)) {
            this.current_user_passkey.setError("Passkey can't  be empty");
            this.current_user_passkey.requestFocus();
        } else {
            this.pd = new ProgressDialog(this);
            this.pd.setProgressStyle(0);
            this.pd.setMessage("Verifying your details....");
            this.pd.setIndeterminate(true);
            this.pd.setCancelable(false);
            this.pd.show();
            final String str2 = currentUser;
            final String str3 = currentUserPasskey;
            Log.d(TAG, "logInUser 1");
            StringRequest r1 = new StringRequest(Request.Method.POST, BaseUrl_ConstantClass.BASE_URL, new Response.Listener<String>() {
                @SuppressLint("ResourceType")
                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onResponse(String response) {
                    Log.d(TAG, "1" + response.toString());

                    try {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("success");
                            if (status.equals("true")) {
                                JSONObject user_details = jsonObject.getJSONObject("user_details");
                                user_id = user_details.getString("id");
                                name = user_details.getString("name");
                                em = user_details.getString("email");
                                mobile = user_details.getString("mobileno");
                                String user_secrate_id = user_details.getString("secrate_id");
                                String password = user_details.getString("password");
                                String resident = user_details.getString("resident");
                                // String resident = user_details.getString("");
                                String string = user_details.getString("call_user");
                                String caller_id = user_details.getString("caller_id");
                                user_img = user_details.getString("user_img");
                                // LoginRegistrationActivity.this.sessionManager.createLoginSession(user_id);
                                String caller_id2 = caller_id;
                                String str = status;
                                String user_secrate_id2 = user_secrate_id;

                                User user = new User(user_id,
                                        name,
                                        em,
                                        mobile,
                                        password,
                                        user_secrate_id,
                                        user_img,
                                        resident,
                                        "true", caller_id2);
                                SharedPrefManager.getInstance(LoginRegistrationActivity.this).userLogin(user);
                                PreferenceUtils.setUserId(user_id);
                                PreferenceUtils.setNickname(user_name);

                                LoginRegistrationActivity.this.loginToChat1(user_id, currentUserPasskey, name);

                                return;
                            }
                            final AlertDialog.Builder ab = new AlertDialog.Builder(LoginRegistrationActivity.this);
                            ab.setMessage((CharSequence) "Please enter your valid Buzz ID or Passkey");
                            ab.setCancelable(false);
                            ab.setNegativeButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ab.setCancelable(true);
                                    pd.dismiss();
                                }
                            });
                            ab.create();
                            ab.show();
                        } catch (JSONException e) {
                            e = e;
                            e.printStackTrace();
                        }
                    } catch (Exception e2) {
                        Exception e = e2;
                        String str2 = response;
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "logInUser" + error);
                    pd.dismiss();
                    //    Toast.makeText(LoginRegistrationActivity.this,  R.drawable.call,Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginRegistrationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();


                }
            }) {

                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> logParams = new HashMap<>();
                    logParams.put("action", "login");
                    logParams.put("secrate_id", str2);
                    logParams.put("password", str3);
                    return logParams;
                }
            };
            MySingleTon.getInstance(this).addToRequestQue(r1);
            this.progressDialog.dismiss();
        }
    }

    //comment
//    public void startSignUpNewUser(QBUser newUser2) {
//        Log.d("startSignUpNewUser",newUser2.toString());
//        this.requestExecutor.signUpNewUser(newUser2, new QBEntityCallback<QBUser>() {
//            @Override
//            public void onSuccess(QBUser qbUser, Bundle bundle) {
//                Log.d("startSignUpNewUser 2 ",qbUser.toString());
//                loginToChat(qbUser);
//                saveUserData(qbUser);
//                callerid(qbUser);
//                //  LoginRegistrationActivity.this.loginToChat(qbUser);
//                //  LoginRegistrationActivity.this.saveUserData(qbUser);
//            }
//
//            @Override
//            public void onError(QBResponseException e) {
//                Log.d("startSignUpNewUser 2 ",qbUser.toString());
//                if (e.getHttpStatusCode() == 422) {
//                    LoginRegistrationActivity.this.signInCreatedUser(newUser2, false);
//                }
//            }
//        });
//    }

    /* access modifiers changed from: private */
//    public void signInCreatedUser(QBUser user, boolean deleteCurrentUser) {
//        Log.d("signInCreatedUser","signInCreatedUser"+user+deleteCurrentUser);
//        this.requestExecutor.signInUser(user, new QBEntityCallbackImpl<QBUser>() {
//            public void onSuccess(QBUser result, Bundle params) {
//                updateUserOnServer ( user );
//                LoginRegistrationActivity.this.removeAllUserData(result);  //comment
//            }
//
//            public void onError(QBResponseException responseException) {
//                Toast.makeText(LoginRegistrationActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    /* access modifiers changed from: private */
//    public void removeAllUserData(QBUser user)
//    {
//        Log.d("deleteCurrentUser",""+user.getId());
//        this.requestExecutor.deleteCurrentUser(user.getId(), new QBEntityCallback<Void>()
//        {
//            public void onSuccess(Void aVoid, Bundle bundle) {
//                UsersUtils.removeUserData(LoginRegistrationActivity.this.getApplicationContext());   //comment
//                LoginRegistrationActivity.this.startSignUpNewUser(createUserWithEnteredData());
//            }
//
//            public void onError(QBResponseException e) {
//                Toast.makeText(LoginRegistrationActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    /* access modifiers changed from: private */
//    public void loginToChat(QBUser qbUser2)
//    {
//        qbUser2.setPassword(com.example.hp.chatapplication.VideoCall.utils.Consts.DEFAULT_USER_PASSWORD);
//        this.userForSave = qbUser2;
//        startLoginService(qbUser2);
//    }

//    private void startLoginService(QBUser qbUser2) {
//        Log.d("startSignUpNewUser","6"+qbUser2);
//        // CallService.start(this, qbUser2, createPendingResult(1002, new Intent(this, CallService.class), 0));
//        finish();
//    }
//
//    /* access modifiers changed from: private */
//    public QBUser createUserWithEnteredData() {
//        return createQBUserWithCurrentData(SharedPrefManager.getInstance(this).getUser().getUser_secret_id(), String.valueOf("Buzz"));
//    }
//
//    private QBUser createQBUserWithCurrentData(String userName, String chatRoomName) {
//        Log.d("createQBUser",""+userName+" "+chatRoomName);
//        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(chatRoomName))
//        {
//            return null;
//        }
//        StringifyArrayList<String> userTags = new StringifyArrayList<> ();
//        userTags.add(chatRoomName);
//        QBUser qbUser2 = new QBUser();
//        qbUser2.setFullName(userName);
//        this.deviceid = UUID.randomUUID();
//        qbUser2.setLogin(this.secret_ID.getText().toString().trim());
//        qbUser2.setPassword(com.example.hp.chatapplication.VideoCall.utils.Consts.DEFAULT_USER_PASSWORD);
//        qbUser2.setTags(userTags);
//        return qbUser2;
//    }
//
//    /* access modifiers changed from: private */
//    public void saveUserData(QBUser qbUser2)
//    {
//        Log.d("startSignUpNewUser","7"+qbUser2);
//        SharedPrefsHelper sharedPrefsHelper2 = SharedPrefsHelper.getInstance();
//        sharedPrefsHelper2.save(com.example.hp.chatapplication.VideoCall.utils.Consts.PREF_CURREN_ROOM_NAME, "Buzz");
//        sharedPrefsHelper2.saveQbUser(qbUser2);
//    }
//
//    private String getCurrentDeviceId()
//    {
//        return Utils.generateDeviceId(this);
//    }

//    public void callerid(QBUser newUser2) {
//
//        final QBUser qBUser = newUser2;
//        StringRequest r0 = new StringRequest (1, URL, new Response.Listener<String> () {
//            public void onResponse(String response) {
//
//                try {
//                    Log.d("startSignUpNewUser", response);
//                   JSONObject jsonObject = new JSONObject(response);
//                   if (jsonObject.getString("success").equals(InternalLogger.EVENT_PARAM_EXTRAS_TRUE)) {
//                      JSONObject user_details = jsonObject.getJSONObject("user_details");
//                      LoginRegistrationActivity.this.user_id = user_details.getString("id");
//                      LoginRegistrationActivity.this.sessionManager.createLoginSession(LoginRegistrationActivity.this.user_id);
//                   }
//               } catch (JSONException e) {
//                  e.printStackTrace();
//              }
//           }
//       }, new Response.ErrorListener() {
//           public void onErrorResponse(VolleyError error) {
//
//              Log.d("startSignUpNewUser", error.getMessage());
//              LoginRegistrationActivity.this.pd.dismiss();
//              Toast.makeText(LoginRegistrationActivity.this, " Failed.."+error.getMessage(), Toast.LENGTH_SHORT).show();
//
//          }
//       }) {
//           /* access modifiers changed from: protected */
//            public Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> logParams = new HashMap<>();
//                logParams.put("userid", LoginRegistrationActivity.this.user_id);
//                logParams.put("call_id ", qBUser.getId().toString());
//                return logParams;
//            }
//        };
//        MySingleTon.getInstance(this).addToRequestQue(r0);
//        this.progressDialog.dismiss();
//   }

    // comment
    /* access modifiers changed from: private */
    public void loginToChat1(String currentuser, String pass, String name) {
        Log.d("lifecycle", "2" + currentuser);

        connectsendBirds();
        LoginRegistrationActivity.this.pd.dismiss();
        updateCurrentUserInfo(name, user_img);
        SharedPrefManager.getInstance(LoginRegistrationActivity.this).save("check", 1);
        SharedPrefManager.getInstance(LoginRegistrationActivity.this).save("checkautostart", 1);

        Intent intent = new Intent(LoginRegistrationActivity.this, UserNavgation.class);
        intent.putExtra("profile_pic", user_img);
        startActivity(intent);
        finish();
//            }
//
//            public void onError(QBResponseException responseException) {
//                Log.d("deleteCurrentUser","4"+responseException);
//                LoginRegistrationActivity.this.pd.dismiss();
//                Toast.makeText(LoginRegistrationActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
//            }
//        });
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1002) {
            boolean isLoginSuccess = data.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);
            String stringExtra = data.getStringExtra(Consts.EXTRA_LOGIN_ERROR_MESSAGE);
            if (isLoginSuccess) {
                finish();
                // saveUserData(this.userForSave);
                //  signInCreatedUser(this.userForSave, true);    ///false
            }
            if (SharedPrefManager.getInstance(this).isLoggedIn() && !SharedPrefManager.getInstance(this).isLoggedIn()) {

                SharedPrefManager.getInstance(this).logout(this);
            }
        }
    }

    private void connectsendBirds() {

        Log.d("lifecycle", "8");
        String userId = SharedPrefManager.getInstance(this).getUser().getUser_id().toString();

        connectToSendBird(userId.replaceAll("\\s", ""), SharedPrefManager.getInstance(this).getUser().getUser_name().toString());
    }

    private void connectToSendBird(String userId, final String userNickname) {

        Log.d("lifecycle", "10" + userId + userNickname);
        PrefUtils.setUserId(LoginRegistrationActivity.this, null);
        PrefUtils.setAccessToken(LoginRegistrationActivity.this, null);
        PrefUtils.setCalleeId(LoginRegistrationActivity.this, null);
        PrefUtils.setPushToken(LoginRegistrationActivity.this, null);
        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(com.sendbird.android.User user, SendBirdException e) {

                Log.d("lifecycle", "LoginRegistrationActivity onCompleted" + e);
                //  FirebaseApp chats = FirebaseApp.getInstance("chats");
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginRegistrationActivity.this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d("lifecycle", "LoginRegistrationActivity pushu onSuccess" + instanceIdResult.getToken());
                        SendBird.registerPushTokenForCurrentUser(instanceIdResult.getToken(), new SendBird.RegisterPushTokenWithStatusHandler() {
                            @Override
                            public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
                                if (e != null) {        // Error.
                                    return;
                                }
                                SendBirdPushHelper.registerPushHandler(new MyFirebaseMessagingService());
                            }
                        });
                    }
                });
                if (e == null) {

                } else {

                    PreferenceUtils.setConnected(false);
                }
            }
        });
        SyncManagerUtils.setup(LoginRegistrationActivity.this, userId, new CompletionHandler() {
            @Override
            public void onCompleted(SendBirdException e) {
                Log.d("lifecycle", "login onCompleted" + e);
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                SendBirdSyncManager.getInstance().resumeSync();
                PreferenceUtils.setConnected(true);

            }
        });
        PushUtils.getPushToken(this, (pushToken, e) -> {
            if (e != null) {
                Log.d(TAG, "authenticate() => Failed (e: " + e.getMessage() + ")");

                return;
            }
            Log.d(TAG, "authenticate() => authenticate()" + pushToken);
            SendBirdCall.authenticate(new AuthenticateParams(userId).setAccessToken(""), (user, e1) -> {
                if (e1 != null) {
                    Log.d(TAG, "authenticate() => authenticate() => Failed (e1: " + e1.getMessage() + ")");


                    return;
                }

                Log.d(TAG, "authenticate() => registerPushToken()");
                SendBirdCall.registerPushToken(pushToken, false, e2 -> {
                    if (e2 != null) {
                        Log.d(TAG, "authenticate() => registerPushToken() => Failed (e2: " + e2.getMessage() + ")");

                        return;
                    }

                    PrefUtils.setAppId(this, SendBirdCall.getApplicationId());
                    PrefUtils.setUserId(this, userId);
                    PrefUtils.setPushToken(this, pushToken);

                    Log.d(TAG, "authenticate() => authenticate() => OK");

                });
            });
        });

    }

    public void updateCurrentUserInfo(String userNickname, String user_pic) {

        SendBird.updateCurrentUserInfo(userNickname, user_pic, new SendBird.UserInfoUpdateHandler() {

            public void onUpdated(SendBirdException e) {
                if (e == null) {
                }
            }
        });
    }

    private void registerNewUser() {


        String user_mobile2 = this.mobile_no.getText().toString().trim();
        String user_email2 = this.email.getText().toString().trim();
        String user_secret_ID = this.secret_ID.getText().toString().trim();
        String user_passkey = this.passkey.getText().toString().trim();
        if (TextUtils.isEmpty(user_secret_ID)) {
            this.secret_ID.setError("Buzz ID can't be empty");
            this.secret_ID.requestFocus();
        } else if (user_secret_ID.contains(" ")) {
            this.secret_ID.setError("Buzz ID Shouldn't contains Blank spaces");
            this.secret_ID.requestFocus();
        } else if (TextUtils.isEmpty(user_mobile2)) {
            this.mobile_no.setError("Mobile Number can't be empty");
            this.mobile_no.requestFocus();

        } else if (TextUtils.isEmpty(user_email2)) {
            this.email.setError("Email ID can't  be empty");
            this.email.requestFocus();
        } else if (!user_email2.matches(emailPattern)) {
            this.email.setError("Please Enter Valid Email ID");
            this.email.requestFocus();
        } else if (TextUtils.isEmpty(user_passkey)) {
            this.passkey.setError("Passkey can't be empty");
            this.passkey.requestFocus();
        } else if (user_passkey.length() < 8) {
            this.passkey.setError("Passkey should be between 8 to 16 characters");
            this.passkey.requestFocus();
        } else {
            this.pd = new ProgressDialog(this);
            this.pd.setProgressStyle(0);
            this.pd.setMessage("Please Wait...");
            this.pd.setIndeterminate(true);
            this.pd.setCancelable(false);
            this.pd.show();
            final String str = user_mobile2;
            final String str2 = user_secret_ID;
            final String str3 = user_email2;
            final String str4 = user_passkey;
            StringRequest r1 = new StringRequest(Request.Method.POST, BaseUrl_ConstantClass.BASE_URL, new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void onResponse(String response) {
                    Log.d("response", "response" + response);
                    String str = response;
                    LoginRegistrationActivity.this.pd.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        String status = jsonObject.getString("success");
                        Log.d("1", "status" + status);

                        LoginRegistrationActivity.this.printmessage = jsonObject.getString("message");


                        if (LoginRegistrationActivity.this.printmessage.matches("Buzz id is already registered")) {
                            LoginRegistrationActivity.this.close_id.setVisibility(View.VISIBLE);
                            LoginRegistrationActivity.this.check_id.setVisibility(View.GONE);
                        } else if (LoginRegistrationActivity.this.printmessage.matches("Mobile no is already registered")) {

                            LoginRegistrationActivity.this.close_id_mobile.setVisibility(View.VISIBLE);
                            LoginRegistrationActivity.this.check_id.setVisibility(View.GONE);
                        } else if (LoginRegistrationActivity.this.printmessage.matches("Email id is already registered")) {
                            LoginRegistrationActivity.this.close_id_email.setVisibility(View.VISIBLE);

                            LoginRegistrationActivity.this.check_id.setVisibility(View.GONE);
                        } else if (LoginRegistrationActivity.this.printmessage.equalsIgnoreCase(" Email id already registerd")) {
                            LoginRegistrationActivity.this.close_id_email.setVisibility(View.VISIBLE);
                            LoginRegistrationActivity.this.check_id.setVisibility(View.GONE);
                            LoginRegistrationActivity.this.close_id.setVisibility(View.GONE);

                        } else if (LoginRegistrationActivity.this.printmessage.equalsIgnoreCase("Email or Mobile number already registered")) {
                            LoginRegistrationActivity.this.close_id_email.setVisibility(View.VISIBLE);

                            LoginRegistrationActivity.this.close_id_mobile.setVisibility(View.VISIBLE);
                            LoginRegistrationActivity.this.close_id.setVisibility(View.GONE);
                        } else if (LoginRegistrationActivity.this.printmessage.equalsIgnoreCase("Email already registerd")) {

                            LoginRegistrationActivity.this.close_id_email.setVisibility(View.VISIBLE);
                            LoginRegistrationActivity.this.close_id.setVisibility(View.GONE);
                        } else {

                            close_id.setVisibility(View.VISIBLE);
                        }
                        Log.d("hgchsgj", str);
                        if (status.equals("true")) {


                            Log.d("1", "5" + status);
                            User user = new User(jsonObject.getString("userid"), " ", email.getText().toString(), mobile_no.getText().toString(), "", jsonObject.getString("secrate_id"), "", "", "false", "");
                            check_id.setVisibility(View.VISIBLE);
                            SharedPrefManager.getInstance(LoginRegistrationActivity.this).userLogin(user);
                            check_id.setVisibility(View.VISIBLE);


                            startActivity(new Intent(LoginRegistrationActivity.this, AccountVarificationViaMobile.class));
                            finish();

                            //  alertDialogSuccess();
                            check_id.setVisibility(View.VISIBLE);
                            close_id.setVisibility(View.GONE);
                            check_id_mobile.setVisibility(View.VISIBLE);
                            close_id_mobile.setVisibility(View.GONE);
                            check_id_email.setVisibility(View.VISIBLE);
                            close_id_email.setVisibility(View.GONE);

                            return;
                        }
                        LoginRegistrationActivity.this.alertDialog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //  check_id.setVisibility(View.VISIBLE);
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginRegistrationActivity.this, "", Toast.LENGTH_SHORT).show();
                    //  close_id.setImageResource ( R.drawable.close );
                    //   close_id.setVisibility ( View.VISIBLE );

                }
            }) {
                /* access modifiers changed from: protected */
                public Map<String, String> getParams() throws AuthFailureError {
                    StringBuilder sb = new StringBuilder();
                    sb.append(LoginRegistrationActivity.this.user_countryDialCode);
                    sb.append(" ");
                    sb.append(str);
                    String mobile1 = sb.toString();
                    Map<String, String> params = new HashMap<>();
                    params.put("action", "register");
                    params.put("user_name", "User Name");
                    params.put("secrate_id", str2);
                    params.put("email", str3);
                    params.put("mobile", mobile1);
                    params.put("password", str4);
                    params.put("device_id", LoginRegistrationActivity.deviceId);
                    return params;
                }
            };
            MySingleTon.getInstance(this).addToRequestQue(r1);
        }
    }

    public void openPicker(View view2) {
        this.picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
    }

    /* access modifiers changed from: private */
    public void alertDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) this.printmessage);
        alertDialogBuilder.setTitle((CharSequence) "Error");
        alertDialogBuilder.setNegativeButton((CharSequence) "Close", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LoginRegistrationActivity.this.alertDialog.dismiss();
            }
        });
        this.alertDialog = alertDialogBuilder.create();
        this.alertDialog.show();
    }

    public void hideSoftKeyboard() {
//        if (getCurrentFocus() != null) {
//            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//        }
    }


    public void onBackPressed() {
        finish();
        finishAffinity();

    }


    private abstract class LoginEditTextWatcher implements TextWatcher {
        private EditText editText;

        private LoginEditTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            editText.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public String getMyuserid() {
        return user_id;
    }


}
