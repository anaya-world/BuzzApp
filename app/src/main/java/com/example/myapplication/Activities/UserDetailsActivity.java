package com.example.myapplication.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.ModelClasses.User;
import com.example.myapplication.R;
import com.example.myapplication.Utils.BaseUrl_ConstantClass;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MimeType;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.PrefUtils;
import com.example.myapplication.Utils.PreferenceUtils;
import com.example.myapplication.Utils.PushUtils;
import com.example.myapplication.Utils.SharedPrefManager;
import com.example.myapplication.Utils.SyncManagerUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.syncmanager.SendBirdSyncManager;
import com.sendbird.syncmanager.handler.CompletionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailsActivity extends BaseActivity implements View.OnClickListener {
    public static final String MY_PREFS_NAME = "resident";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int contactrequestcode = 501;
    User user1;
    String URL = CommonUtils.baseUrl;
    TextView User_name;
    RelativeLayout account_status;
    String accountstatus;
    ArrayAdapter<String> adapter;
    AlertDialog alertDialog;
    AlertDialog alertDialogs;
    Bitmap bt;
    Button button_private;
    Button button_public;
    final Calendar c = Calendar.getInstance();
    ImageView chat_backarrow;
    ImageView choose_image_iv;
    Button closePopupBtn;
    Context context;
    String data;
    TextView delete_myaccount;
    ImageView delete_profile_image;
    ImageView dialog_imageview;
    EditText et_anniversary;
    TextView et_date_of_birth;
    ImageView image_view_select_date;
    String imageurl;
    ImageView iv_anniversary;
    ImageView iv_gallery_choose;
    LinearLayout linearLayout1;
    String mCurrentPhotoPath;
    int mDay = this.c.get(Calendar.DATE);
    final int mMonth = this.c.get(Calendar.MONTH);
    int mYear = this.c.get(Calendar.YEAR);
    ImageView myImage;
    /* access modifiers changed from: private */
    public ProgressDialog pd;
    private String pictureImagePath = "";
    ImageView pop_up_close;
    PopupWindow popupWindow;
    String profile_pic;
    ProgressDialog progressDialog;
    RelativeLayout relative_anniversary;
    RelativeLayout relative_change_password;
    RelativeLayout relative_log_text;
    String secret_id;
    // SessionManager sessionManager;
    ImageView settings_image_view;
    /* access modifiers changed from: private */
    //  public SharedPrefsHelper sharedPrefsHelper;
    Spinner spinner_all_interests;
    TextView status_user;
    String token;
    TextView tv_all_interests;
    TextView tv_gender;
    TextView tv_recidence;
    TextView tv_securityCode;
    HashMap<String, String> user;
    String user_id;
    TextView user_profile_Mobileno;
    TextView user_profile_email;
    CircleImageView user_profile_image_view;
    TextView user_profile_secret_id;
    TextView work_for_tv;
    private ScrollView relativeLayout;
    private LinearLayout progressBar;
    private TextView tv_progress;
    private RelativeLayout rel_terms_And_cond;
    private String TAG = "UserDetailsActivity";
    RequestOptions requestOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_img);
        requestOptions.error(R.drawable.buzzplaceholder);
        Log.d("UserDetailsActivity", "1");
        init();
        relativeLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        loadAllDetails();
        Log.d("UserDetailsActivity", "4");
        this.chat_backarrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                UserDetailsActivity.this.onBackPressed();
            }
        });
        this.user_id = SharedPrefManager.getInstance(this).getUser().getUser_id().toString();
//        this.sessionManager = new SessionManager(this);
//        this.user = this.sessionManager.getUserDetails();
        //      this.token = (String) this.user.get("token");
        this.account_status = (RelativeLayout) findViewById(R.id.account_status);
        this.account_status.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(UserDetailsActivity.this, CompleteYourProfile.class);
                intent.putExtra("CHECK", "0");
                intent.putExtra("SECURITY_CODE", UserDetailsActivity.this.tv_securityCode.getText().toString());
                UserDetailsActivity.this.startActivity(intent);
            }
        });
        this.delete_myaccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!UserDetailsActivity.this.user_id.equals("")) {
                    UserDetailsActivity.this.alertDialogDeleteVarification();
                }
            }
        });
        //  rel_terms_And_cond.setOnClickListener(this);
        this.relative_change_password = (RelativeLayout) findViewById(R.id.relative_change_password);
        this.relative_change_password.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UserDetailsActivity.this.startActivity(new Intent(UserDetailsActivity.this, ChangePasswordActivity.class));
            }
        });

        if (!checkPermission()) {
            requestPermission();
        }
        Log.d("UserDetailsActivity", "5");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                relativeLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }, 2000);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    /* access modifiers changed from: private */
    public void delete_myaccounts(String userid) {
        StringRequest r0 = new StringRequest(1, this.URL, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("success");
                    String string = jsonObject.getString("message");
                    if (status.equals("true")) {
                        Toast.makeText(UserDetailsActivity.this, "", Toast.LENGTH_LONG).show();
                        UserDetailsActivity.this.logout();
                        return;
                    }
                    Toast.makeText(UserDetailsActivity.this, "", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                UserDetailsActivity userDetailsActivity = UserDetailsActivity.this;
                StringBuilder sb = new StringBuilder();
                sb.append("Some error occurred -> ");
                sb.append(volleyError);
                Toast.makeText(userDetailsActivity, sb.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("action", "deleteprofile");
                parameters.put("userid", UserDetailsActivity.this.user_id);
                return parameters;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(r0);
    }

    private void visibility_myaccounts(String accountstatus2) {
        final String str = accountstatus2;
        StringRequest r0 = new StringRequest(Request.Method.POST, this.URL, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String string = jsonObject.getString("success");
                    jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                UserDetailsActivity userDetailsActivity = UserDetailsActivity.this;
                StringBuilder sb = new StringBuilder();
                sb.append("Some error occurred -> ");
                sb.append(volleyError);
                Toast.makeText(userDetailsActivity, sb.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("action", "make_pripub");
                parameters.put("userid", UserDetailsActivity.this.user_id);
                parameters.put("userfunc", str);
                return parameters;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(r0);
    }

    private void init() {
        Log.d("UserDetailsActivity", "2");
        relativeLayout = (ScrollView) findViewById(R.id.relLAy);
        progressBar = (LinearLayout) findViewById(R.id.progress);
        tv_progress = findViewById(R.id.tv_progress);
        this.user_profile_image_view = (CircleImageView) findViewById(R.id.user_profile_image_view);
        this.user_profile_image_view.setOnClickListener(this);
        this.et_date_of_birth = (TextView) findViewById(R.id.et_date_of_birth);
        this.et_anniversary = (EditText) findViewById(R.id.et_anniversary);
        this.iv_anniversary = (ImageView) findViewById(R.id.iv_anniversary);
        this.iv_anniversary.setOnClickListener(this);
        this.image_view_select_date = (ImageView) findViewById(R.id.image_view_select_date);
        this.image_view_select_date.setOnClickListener(this);
        this.User_name = (TextView) findViewById(R.id.User_name);
        this.user_profile_Mobileno = (TextView) findViewById(R.id.user_profile_Mobileno);
        this.user_profile_Mobileno.setText(SharedPrefManager.getInstance(this).getUser().getUser_mobile_no().toString());
        this.user_profile_email = (TextView) findViewById(R.id.user_profile_email);
        this.user_profile_email.setText(SharedPrefManager.getInstance(this).getUser().getUser_email().toString());
        this.user_profile_secret_id = (TextView) findViewById(R.id.user_profile_secret_id);
        this.secret_id = SharedPrefManager.getInstance(this).getUser().getUser_secret_id().toString();
        this.user_profile_secret_id.setText(this.secret_id);
        this.linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        this.settings_image_view = (ImageView) findViewById(R.id.settings_image_view);
        this.settings_image_view.setOnClickListener(this);
        this.button_public = (Button) findViewById(R.id.button_public);
        this.button_public.setOnClickListener(this);
        this.button_private = (Button) findViewById(R.id.button_private);
        this.button_private.setOnClickListener(this);
        //    this.relative_log_text = (RelativeLayout) findViewById(R.id.relative_log_text);
        //   this.relative_log_text.setOnClickListener(this);
        // this.sharedPrefsHelper = SharedPrefsHelper.getInstance();
        this.relative_anniversary = (RelativeLayout) findViewById(R.id.relative_anniversary);
        //       this.rel_terms_And_cond = (RelativeLayout) findViewById(R.id.rel_terms_and_cond);
        this.tv_all_interests = (TextView) findViewById(R.id.tv_all_interests);
        this.tv_recidence = (TextView) findViewById(R.id.tv_recidence);
        this.tv_gender = (TextView) findViewById(R.id.tv_gender);
        this.work_for_tv = (TextView) findViewById(R.id.work_for_tv);
        this.status_user = (TextView) findViewById(R.id.status_user);
        this.delete_myaccount = (TextView) findViewById(R.id.delete_myaccount);
        this.tv_securityCode = (TextView) findViewById(R.id.user_profile_security_code);
        this.chat_backarrow = (ImageView) findViewById(R.id.chat_backarrow);
        this.delete_profile_image = (ImageView) findViewById(R.id.delete_profile_image);
    }

    @RequiresApi(api = 24)
    public void onClick(View v) {
        switch (v.getId()) {
//                      case R.id.rel_terms_and_cond /*2131296366*/:
//                          Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/blog/terms-conditions-mobile-apps/"));
//                          startActivity(Getintent);
//                          return;

            case R.id.button_private /*2131296366*/:
                this.accountstatus = "Private";
                callToChangeProfileStatus(this.accountstatus);
                return;
            case R.id.button_public /*2131296367*/:
                this.accountstatus = "Public";
                callToChangeProfileStatus(this.accountstatus);
                return;
//                      case R.id.relative_log_text /*2131296942*/:
//                          logout();
//                          return;
            case R.id.settings_image_view /*2131297048*/:
                popupShow();
                return;
            case R.id.user_profile_image_view /*2131297271*/:
                if (this.profile_pic != null) {
                    Intent intent = new Intent(this, PhotoViewerActivity.class);
                    intent.putExtra("url", this.profile_pic);
                    intent.putExtra("type", "image");
                    startActivity(intent);
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void callToChangeProfileStatus(String accountstatus2) {
        this.pd = new ProgressDialog(this);
        this.pd.setProgressStyle(0);
        this.pd.setMessage("Please wait…");
        this.pd.setIndeterminate(true);
        this.pd.setCancelable(false);
        this.pd.show();
        String str = CommonUtils.baseUrl;
        final String str2 = accountstatus2;
        StringRequest r1 = new StringRequest(1, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    if (!UserDetailsActivity.this.isFinishing() && UserDetailsActivity.this.pd != null && UserDetailsActivity.this.pd.isShowing()) {
                        UserDetailsActivity.this.pd.dismiss();
                    }
                } catch (Exception e) {
                }
                Log.d("Userres", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    if (status.equals("true")) {
                        UserDetailsActivity.this.alertDialogPrivate(message);
                        String profileType = jsonObject.getString("profiletype");
                        if (profileType == null || !profileType.equalsIgnoreCase("Private")) {
                            UserDetailsActivity.this.button_private.setBackgroundDrawable(ContextCompat.getDrawable(UserDetailsActivity.this, R.drawable.signin_button_voilet));
                            UserDetailsActivity.this.button_private.setTextColor(Color.parseColor("#FFFFFF"));
                            UserDetailsActivity.this.button_public.setBackgroundDrawable(ContextCompat.getDrawable(UserDetailsActivity.this, R.drawable.signin_button));
                            UserDetailsActivity.this.button_public.setTextColor(Color.parseColor("#000000"));
                        } else {
                            UserDetailsActivity.this.button_private.setBackgroundDrawable(ContextCompat.getDrawable(UserDetailsActivity.this, R.drawable.signin_button));
                            UserDetailsActivity.this.button_public.setBackgroundDrawable(ContextCompat.getDrawable(UserDetailsActivity.this, R.drawable.signin_button_voilet));
                            UserDetailsActivity.this.button_private.setTextColor(Color.parseColor("#000000"));
                            UserDetailsActivity.this.button_public.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        return;
                    }
                    Toast.makeText(UserDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                try {
                    if (!UserDetailsActivity.this.isFinishing() && UserDetailsActivity.this.pd != null && UserDetailsActivity.this.pd.isShowing()) {
                        UserDetailsActivity.this.pd.dismiss();
                    }
                } catch (Exception e) {
                }
                Toast.makeText(UserDetailsActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "changeaccountstatus");
                logParams.put("profiletype", str2);
                logParams.put("userid", UserDetailsActivity.this.user_id);
                return logParams;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(r1);
    }

    private void popupShow() {
        View customView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_setting_layout, null);
        this.pop_up_close = (ImageView) customView.findViewById(R.id.pop_up_close);
        this.popupWindow = new PopupWindow(customView, -1, -2);
        this.popupWindow.showAtLocation(this.linearLayout1, 17, 0, 0);
        this.pop_up_close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (UserDetailsActivity.this.popupWindow.isShowing()) {
                    UserDetailsActivity.this.popupWindow.dismiss();
                }
            }
        });
        this.choose_image_iv = (ImageView) customView.findViewById(R.id.choose_image_iv);
        this.choose_image_iv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailsActivity.this);
                builder.setTitle("Do u really want to Change Profile Picture?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserDetailsActivity.this.dispatchTakePictureIntent();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog = builder.create();
                dialog.show();

                if (UserDetailsActivity.this.popupWindow.isShowing()) {
                    UserDetailsActivity.this.popupWindow.dismiss();
                }
            }
        });
        this.iv_gallery_choose = (ImageView) customView.findViewById(R.id.iv_gallery_choose);
        this.iv_gallery_choose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailsActivity.this);
                builder.setTitle("Do u really want to Change Profile Picture?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserDetailsActivity.this.requestMedia();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog = builder.create();
                dialog.show();

                if (UserDetailsActivity.this.popupWindow.isShowing()) {
                    UserDetailsActivity.this.popupWindow.dismiss();
                }

            }
        });
        this.delete_profile_image = (ImageView) customView.findViewById(R.id.delete_profile_image);
        if (user_profile_image_view.getDrawable() == null) {
            delete_profile_image.setVisibility(View.GONE);
        } else {
            delete_profile_image.setVisibility(View.VISIBLE);
        }
        this.delete_profile_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                UserDetailsActivity.this.removeprofileimageDialog();
            }
        });
    }

    private void alertDialogPublic() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "You are visible, your friends can easily find you on Buzz!");
        alertDialogBuilder.setNegativeButton((CharSequence) "Close", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                UserDetailsActivity.this.alertDialog.dismiss();
            }
        });
        this.alertDialog = alertDialogBuilder.create();
        this.alertDialog.show();
    }

    /* access modifiers changed from: private */
    public void alertDialogPrivate(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) message);
        alertDialogBuilder.setNegativeButton((CharSequence) "Close", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                UserDetailsActivity.this.alertDialog.dismiss();
            }
        });
        this.alertDialog = alertDialogBuilder.create();
        this.alertDialog.show();
    }

    /* access modifiers changed from: private */
    public void logout() {
        if (!SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), LoginRegistrationActivity.class));
            finish();
            return;
        }
        SendBird.unregisterPushTokenAllForCurrentUser(new SendBird.UnregisterPushTokenHandler() {
            @Override
            public void onUnregistered(SendBirdException e) {
                SendBird.disconnect(new SendBird.DisconnectHandler() {
                    @Override
                    public void onDisconnected() {
                        SendBirdSyncManager.getInstance().pauseSync();
                        String userId = PreferenceUtils.getUserId();
                        // if you want to clear cache of specific user when disconnect, you can do like this.

                        SendBirdSyncManager.getInstance().clearCache(userId);

                        PreferenceUtils.setConnected(false);

                    }
                });
            }
        });

        SendBirdCall.deauthenticate(PrefUtils.getPushToken(UserDetailsActivity.this), new com.sendbird.calls.handler.CompletionHandler() {
            @Override
            public void onResult(com.sendbird.calls.SendBirdException e) {
                PrefUtils.setUserId(UserDetailsActivity.this, null);
                PrefUtils.setAccessToken(UserDetailsActivity.this, null);
                PrefUtils.setCalleeId(UserDetailsActivity.this, null);
                PrefUtils.setPushToken(UserDetailsActivity.this, null);
            }
        });
        SharedPrefManager.getInstance(UserDetailsActivity.this).logout(UserDetailsActivity.this);

    }

    @RequiresApi(api = 24)
    private void dobPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = 24)
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String month = new DateFormatSymbols().getMonths()[(monthOfYear + 1) - 1];
                TextView textView = UserDetailsActivity.this.et_date_of_birth;
                StringBuilder sb = new StringBuilder();
                sb.append(dayOfMonth);
                sb.append("-");
                sb.append(month);
                sb.append("-");
                sb.append(year);
                textView.setText(sb.toString());
            }
        }, this.mYear, this.mMonth, this.mDay);
        datePickerDialog.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    private void annversaryPickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = 24)
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String month = new DateFormatSymbols().getMonths()[(monthOfYear + 1) - 1];
                EditText editText = UserDetailsActivity.this.et_anniversary;
                StringBuilder sb = new StringBuilder();
                sb.append(dayOfMonth);
                sb.append("-");
                sb.append(month);
                sb.append("-");
                sb.append(year);
                editText.setText(sb.toString());
            }
        }, this.mYear, this.mMonth, this.mDay);
        datePickerDialog.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    private void loadAllDetails() {
        Log.d("UserDetailsActivity", "3");
        final String LOGIN_URL = BaseUrl_ConstantClass.BASE_URL;

        StringRequest stringRequestLogIn = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Userres", response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("success");
                            if (status.equals("true")) {
                                JSONObject user_details = jsonObject.getJSONObject("user_details");
                                String user_id = user_details.optString("id");
                                String secrate_id = user_details.optString("secrate_id");
                                String mobileno = user_details.optString("mobileno");
                                String email = user_details.optString("email");
                                String name = user_details.optString("name");
                                String marital = user_details.optString("marital");
                                String password = user_details.getString("password");
                                String gender = user_details.optString("gender");
                                String dob = user_details.optString("dob");
                                String anniversary = user_details.optString("anniversary");
                                String workfor = user_details.optString("workfor");
                                String caller_id = user_details.getString("caller_id");
                                String resident = user_details.optString("resident");
                                String security_code = user_details.optString("security_code");
                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("residentid", resident);
                                editor.apply();
                                profile_pic = user_details.optString("user_img");
                                user1 = new User(user_id, name, email, mobileno, password, secrate_id,
                                        profile_pic, resident, "true", caller_id);
                                SharedPrefManager.getInstance(UserDetailsActivity.this).userLogin(user1);


                                if (profile_pic != null) {
                                    updateCurrentUserInfo(name, profile_pic);
                                    Glide.with(UserDetailsActivity.this).setDefaultRequestOptions(requestOptions).load(profile_pic).into(user_profile_image_view);

                                } else {
                                    updateCurrentUserInfo(name, "");
                                    user_profile_image_view.setBackgroundResource(R.drawable.app_buzz_logo);


                                }

                                String instrests = user_details.optString("instrests");
                                JSONArray jsonArray = new JSONArray(instrests);
                                String[] strArr = new String[jsonArray.length()];

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    strArr[i] = jsonArray.getString(i);
                                }

                                System.out.println(Arrays.toString(strArr));
                                String strArray[] = strArr;

                                List<String> interest = new ArrayList<String>(Arrays.asList(strArray));

                                StringBuilder sb = new StringBuilder();
                                int size = interest.size();
                                boolean appendSeparator = false;
                                for (int y = 0; y < size; y++) {
                                    if (appendSeparator)
                                        sb.append(','); // a comma
                                    appendSeparator = true;
                                    sb.append(interest.get(y));
                                }
                                tv_all_interests.setText(sb.toString());

                              /*  List<String> interest = new ArrayList<String>(Arrays.asList(strArray));
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(UserDetailsActivity.this,   android.R.layout.simple_spinner_item, interest);
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                spinner_all_interests.setAdapter(spinnerArrayAdapter);
*/


                                User_name.setText(name);
                                et_date_of_birth.setText(dob);
                                work_for_tv.setText(workfor);
                                tv_recidence.setText(resident);
                                tv_gender.setText(gender);
                                status_user.setText(marital);

                                if (status_user.getText().toString().equals("Single")) {
                                    relative_anniversary.setVisibility(View.GONE);
                                } else {
                                    et_anniversary.setText(anniversary);
                                    relative_anniversary.setVisibility(View.VISIBLE);
                                }

                                tv_securityCode.setText(security_code);


                            } else {
                                Toast.makeText(UserDetailsActivity.this, "Please enter your valid Secret ID or Passkey", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(UserDetailsActivity.this, "it seems your network is slow",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                        } else if (error instanceof ServerError) {
                            Toast.makeText(UserDetailsActivity.this, "sorry for inconvenience, Server is not working",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(UserDetailsActivity.this, "it seems your network is slow",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "getuser");
                logParams.put("userid", user_id);
                return logParams;
            }
        };

        MySingleTon.getInstance(UserDetailsActivity.this).addToRequestQue(stringRequestLogIn);

    }

    /* access modifiers changed from: private */
    public void updateCurrentUserInfo(String userNickname, String image_url) {
        String userId = String.valueOf(SharedPrefManager.getInstance(this).getUser().getUser_id()).replaceAll("\\s", "");

        if (SendBird.getConnectionState() != SendBird.ConnectionState.OPEN) {
            SendBird.connect(userId, new SendBird.ConnectHandler() {
                @Override
                public void onConnected(com.sendbird.android.User user, SendBirdException e) {
                    Log.d("lifecycle", "USERDETAILS sendbird connect Success" + e);
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(UserDetailsActivity.this, new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            SendBird.registerPushTokenForCurrentUser(instanceIdResult.getToken(), new SendBird.RegisterPushTokenWithStatusHandler() {
                                @Override
                                public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
                                    if (e != null) {        // Error.
                                        return;
                                    }
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
            SyncManagerUtils.setup(UserDetailsActivity.this, userId, new CompletionHandler() {
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
            updateCurrentUserInfo(userNickname, image_url);
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
//
                        Log.d(TAG, "authenticate() => authenticate() => OK");

                    });
                });
            });

        } else {
            SendBird.updateCurrentUserInfo(userNickname, image_url, new SendBird.UserInfoUpdateHandler() {
                public void onUpdated(SendBirdException e) {
                    Log.d("Lifecycle", "USERDETAILS updateCurrentUserInfo" + e);
                    if (e == null) {
                    }
                }
            });
        }

    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data2) {
        super.onActivityResult(requestCode, resultCode, data2);
        if (requestCode == 1 && resultCode == -1) {
            setPic();
        } else if (requestCode == 301 && resultCode == -1 && data2 != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data2.getData());
                this.bt = bitmap;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                String encoded = Base64.encodeToString(baos.toByteArray(), 0);
                this.imageurl = encoded;
                imageUpdate(encoded, "upload");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE",
                        "android.permission.CAMERA",
                        "android.permission.READ_CONTACTS",
                        "android.permission.CALL_PHONE",
                        "android.permission.RECORD_AUDIO",
                        "android.permission.ACCESS_FINE_LOCATION"},
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
                ;
    }

    private void openImageOnPopUp() {
        AlertDialog.Builder alertadd = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.image_on_popup, null);
        this.dialog_imageview = (ImageView) view.findViewById(R.id.dialog_imageview);
        Glide.with((FragmentActivity) this).load(this.profile_pic).into(this.dialog_imageview);
        if (this.profile_pic != null) {
            Glide.with((FragmentActivity) this).setDefaultRequestOptions(requestOptions).load(this.profile_pic).into(this.dialog_imageview);
        } else {
            this.user_profile_image_view.setImageResource(R.drawable.buzzlogo);
        }
        alertadd.setView(view);
        alertadd.show();
    }

    private void detailsQuestionMarkPopup() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "Are sure you want to delete the account?");
        alertDialogBuilder.setNegativeButton((CharSequence) "YES", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                UserDetailsActivity.this.alertDialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton((CharSequence) "NO", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                UserDetailsActivity.this.alertDialog.dismiss();
            }
        });
        this.alertDialog = alertDialogBuilder.create();
        this.alertDialog.show();
    }

    /* access modifiers changed from: private */
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d("dispatchTake", "1" + takePictureIntent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra("output", FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".filepicker.provider", photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            Log.d("dispatchTake", "1" + photoFile);

        }
    }

    /* access modifiers changed from: private */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestMedia() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            requestStoragePermissions();
            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 19) {
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{MimeType.IMAGE_MIME});
        } else {
            intent.setType("image/*");
        }
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Media"), 301);
        SendBird.setAutoBackgroundDetection(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            Snackbar.make((View) this.linearLayout1, (CharSequence) "Storage access permissions are required to upload/download files.", Snackbar.LENGTH_LONG).setAction((CharSequence) "Okay", (View.OnClickListener) new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void onClick(View view) {
                    UserDetailsActivity.this.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 13);
                }
            }).show();
            return;
        }
        requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 13);
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

    private void setPic() {
        int targetW = this.user_profile_image_view.getWidth();
        int targetH = this.user_profile_image_view.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        // bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(this.mCurrentPhotoPath, bmOptions);
        //int scaleFactor = Math.min(bmOptions.outWidth / targetW, bmOptions.outHeight / targetH);
        //bmOptions.inJustDecodeBounds = false;
        //bmOptions.inSampleSize = scaleFactor;
        // bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(this.mCurrentPhotoPath, bmOptions);
        Bitmap newBit = rotateImage(bitmap, 90);
        this.user_profile_image_view.setImageBitmap(null);
        this.user_profile_image_view.destroyDrawingCache();
        Glide.with((FragmentActivity) this).load(newBit).into((ImageView) this.user_profile_image_view);
        this.bt = newBit;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newBit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String encoded = Base64.encodeToString(baos.toByteArray(), 0);
        this.imageurl = encoded;
        imageUpdate(encoded, "upload");
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /* access modifiers changed from: private */
    public void imageUpdate(String encoded, String check) {
        this.progressDialog = new ProgressDialog(this);
        if (check.equals("remove")) {
            this.progressDialog.setMessage("Removing..please wait...!!");
        } else {
            this.progressDialog.setMessage("Uploading..please wait...!!");
        }

        this.progressDialog.show();
        final String str = encoded;
        StringRequest r1 = new StringRequest(1, this.URL, new Response.Listener<String>() {
            public void onResponse(String response) {
                UserDetailsActivity.this.progressDialog.dismiss();
                Log.e("result", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    profile_pic = jsonObject.optString("user_img");
                    String callerId = jsonObject.optString("caller_id");
                    user1.setUser_image(profile_pic);
                    SharedPrefManager.getInstance(UserDetailsActivity.this).userLogin(user1);

                    if (UserDetailsActivity.this.popupWindow.isShowing()) {
                        UserDetailsActivity.this.popupWindow.dismiss();
                    }
                    if (!status.equals("true")) {
                        Toast.makeText(UserDetailsActivity.this, "failed to update", Toast.LENGTH_SHORT).show();
                    } else if (profile_pic == null || profile_pic.equals("")) {
                        Glide.with((FragmentActivity) UserDetailsActivity.this).setDefaultRequestOptions(requestOptions).load("").into((ImageView) UserDetailsActivity.this.user_profile_image_view);
                        Toast.makeText(UserDetailsActivity.this, "Unable to upload picture please try again.", Toast.LENGTH_SHORT).show();
                    } else {
                        Glide.with((FragmentActivity) UserDetailsActivity.this).setDefaultRequestOptions(requestOptions).load(UserDetailsActivity.this.profile_pic).into((ImageView) UserDetailsActivity.this.user_profile_image_view);
                        String userNickname = SharedPrefManager.getInstance(UserDetailsActivity.this).getUser().getUser_name().toString();
                        String userId = String.valueOf(SharedPrefManager.getInstance(UserDetailsActivity.this).getUser().getUser_id()).replaceAll("\\s", "");
                        SendBird.updateCurrentUserInfo(userNickname, profile_pic, new SendBird.UserInfoUpdateHandler() {
                            public void onUpdated(SendBirdException e) {
                                if (e != null) {
                                    Toast.makeText(UserDetailsActivity.this, "Unable to upload picture please try again.", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (check.equals("remove")) {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserDetailsActivity.this);
                                        alertDialogBuilder.setMessage((CharSequence) "Profile Picture Removed!");
                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                alertDialog.dismiss();
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    } else {
                                        Toast.makeText(UserDetailsActivity.this, "Picture updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                UserDetailsActivity userDetailsActivity = UserDetailsActivity.this;
                StringBuilder sb = new StringBuilder();
                sb.append("Some error occurred -> ");
                sb.append(volleyError);
                Toast.makeText(userDetailsActivity, sb.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("action", "upload_avtar");
                parameters.put("userid", UserDetailsActivity.this.user_id);
                parameters.put("img", str);
                Log.d("strng", "" + str);
                return parameters;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(r1);
    }

    /* access modifiers changed from: private */
    public void removeprofileimageDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "Remove profile picture ?");
        alertDialogBuilder.setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                UserDetailsActivity.this.alertDialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton((CharSequence) "Remove", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                UserDetailsActivity.this.bt = null;
                UserDetailsActivity.this.imageUpdate("", "remove");
                UserDetailsActivity.this.alertDialog.dismiss();

                //Toast.makeText(UserDetailsActivity.this, "Profile Picture removed successfully", Toast.LENGTH_SHORT).show();
            }
        });
        this.alertDialog = alertDialogBuilder.create();
        this.alertDialog.show();
    }

    /* access modifiers changed from: private */
    public void alertDialogDeleteVarification() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "Are you sure to delete your Buzzapp account?");
        alertDialogBuilder.setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                UserDetailsActivity.this.alertDialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                UserDetailsActivity.this.delete_myaccounts(UserDetailsActivity.this.user_id);
                UserDetailsActivity.this.alertDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Your account deleted successfully", Toast.LENGTH_SHORT).show();
                if (!SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()) {
                    startActivity(new Intent(getApplicationContext(), LoginRegistrationActivity.class));
                    finish();
                    return;
                }
                SendBird.unregisterPushTokenAllForCurrentUser(new SendBird.UnregisterPushTokenHandler() {
                    @Override
                    public void onUnregistered(SendBirdException e) {
                        SendBird.disconnect(new SendBird.DisconnectHandler() {
                            @Override
                            public void onDisconnected() {
                                SendBirdSyncManager.getInstance().pauseSync();
                                String userId = PreferenceUtils.getUserId();
                                // if you want to clear cache of specific user when disconnect, you can do like this.

                                SendBirdSyncManager.getInstance().clearCache(userId);

                                PreferenceUtils.setConnected(false);

                            }
                        });
                    }
                });

                SendBirdCall.deauthenticate(PrefUtils.getPushToken(UserDetailsActivity.this), new com.sendbird.calls.handler.CompletionHandler() {
                    @Override
                    public void onResult(com.sendbird.calls.SendBirdException e) {
                        PrefUtils.setUserId(UserDetailsActivity.this, null);
                        PrefUtils.setAccessToken(UserDetailsActivity.this, null);
                        PrefUtils.setCalleeId(UserDetailsActivity.this, null);
                        PrefUtils.setPushToken(UserDetailsActivity.this, null);
                    }
                });
                SharedPrefManager.getInstance(UserDetailsActivity.this).logout(UserDetailsActivity.this);


            }
        });
        this.alertDialog = alertDialogBuilder.create();
        this.alertDialog.show();
    }

    public void onBackPressed() {
        Log.d("updateCurrentUserInfo", "11--" + CommonUtils.check + profile_pic);

        Intent intent = new Intent(this, UserNavgation.class);
//        intent.addFlags(67108864);
//        intent.addFlags(536870912);


        intent.putExtra("profile_pic", profile_pic);

        startActivity(intent);
        finish();
    }
}
