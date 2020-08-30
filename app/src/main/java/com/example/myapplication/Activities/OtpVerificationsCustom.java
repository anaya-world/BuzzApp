package com.example.myapplication.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.myapplication.R;
import com.example.myapplication.Utils.BaseUrl_ConstantClass;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OtpVerificationsCustom extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progressDialog;
    Button button_validate_OTP, button_resend_otp;
    String email_user_saved;
    AlertDialog alertDialog;
    String get_entered_otp;
    ProgressBar otp_progress;
    private String OTP;
    private EditText et_OTP;
    private String otp;
    private TextView tv_terms_and_cond;
    private TextView tv_privacy_policy;

    /**
     * Called when the activity has detected the user's press of the back
     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
     * chance to handle the back button before the default behavior of
     * {@link Activity#onBackPressed()} is invoked.
     *
     * @see #getOnBackPressedDispatcher()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verifications_custom);
        et_OTP = findViewById(R.id.et_OTP);
        otp_progress = findViewById(R.id.otp_progress);
        this.tv_terms_and_cond=(TextView)findViewById(R.id.tv_terms_and_cond);
        this.tv_privacy_policy=(TextView)findViewById(R.id.tv_privacy_policy);
        tv_privacy_policy.setOnClickListener(this);
        tv_terms_and_cond.setOnClickListener(this);
        email_user_saved = SharedPrefManager.getInstance(OtpVerificationsCustom.this).getUser().getUser_email().toString();


        button_resend_otp = (Button) findViewById(R.id.button_resend_otp);
        button_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                get_entered_otp = et_OTP.getText().toString();
                et_OTP.setText("");
                et_OTP.setError(null);
                resendOTP();

            }
        });

        button_validate_OTP = (Button) findViewById(R.id.button_validate_OTP);
        button_validate_OTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_entered_otp = et_OTP.getText().toString();
                if(TextUtils.isEmpty(get_entered_otp)){
                    Toast.makeText(OtpVerificationsCustom.this, "Please enter otp", Toast.LENGTH_SHORT).show();
                }else {
                    validateYourOTP();
                }


            }
        });



       /* et_OTP=(TextView) findViewById(R.id.et_OTP);
        OTP = getIntent().getStringExtra("OTP");
        et_OTP.setText(email_user_saved);*/
    }


    private void resendOTP() {
        final String user_id = SharedPrefManager.getInstance(OtpVerificationsCustom.this).getUser().getUser_id().toString();
        final String LOGIN_URL = BaseUrl_ConstantClass.BASE_URL;
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Connecting...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequestLogIn = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (status.equals("true")) {
                                Toast.makeText ( getApplicationContext (),"New OTP has been resent to your Email ID",Toast.LENGTH_SHORT ).show ();

                                otp = jsonObject.getString("otp");
                                // Toast.makeText(AccountVarificationViaMobile.this, "Your Otp is:" + message + otp, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OtpVerificationsCustom.this, "Please enter your valid Secret Id or Passkey", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        progressDialog.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(OtpVerificationsCustom.this, "it seems your network is slow" ,
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                        } else if (error instanceof ServerError) {
                            Toast.makeText(OtpVerificationsCustom.this, "sorry for inconvenience, Server is not working" ,
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(OtpVerificationsCustom.this, "it seems your network is slow" ,
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
                logParams.put("action", "send_otp");
                logParams.put("userid", user_id);
                logParams.put("actiontype", "mobile");


                return logParams;
            }
        };

        MySingleTon.getInstance(OtpVerificationsCustom.this).addToRequestQue(stringRequestLogIn);
        progressDialog.dismiss();
    }


    private void validateYourOTP() {

        final String user_id = SharedPrefManager.getInstance(OtpVerificationsCustom.this).getUser().getUser_id().toString();
        final String LOGIN_URL = BaseUrl_ConstantClass.BASE_URL;
        if (TextUtils.isEmpty(get_entered_otp)) {
            et_OTP.setError("OTP can't  be empty ");
            et_OTP.requestFocus();
            return;
        } else {
            otp_progress.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Verifying...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            StringRequest stringRequestLogIn = new StringRequest(Request.Method.POST, LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                otp_progress.setVisibility(View.INVISIBLE);
                                String status = jsonObject.getString("success");
                                String message = jsonObject.getString("message");

                                if (status.equals("true")) {
                              /*  Intent intent = new Intent(OtpVerificationsCustom.this,CompleteYourProfile.class);
                                Toast.makeText(OtpVerificationsCustom.this, "Message"+message, Toast.LENGTH_SHORT).show();
                                startActivity(intent);*/
                                    alertVarificationSuccess();
                                } else {
                                    Toast.makeText(OtpVerificationsCustom.this, "Please enter valid otp", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            progressDialog.dismiss();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(OtpVerificationsCustom.this, "it seems your network is slow" ,
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof AuthFailureError) {
                                //TODO
                            } else if (error instanceof ServerError) {
                                Toast.makeText(OtpVerificationsCustom.this, "sorry for inconvenience, Server is not working" ,
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(OtpVerificationsCustom.this, "it seems your network is slow" ,
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
                    logParams.put("action", "validate_otp");
                    logParams.put("userid", user_id);
                    logParams.put("otp", get_entered_otp);

                    return logParams;
                }
            };

            MySingleTon.getInstance(OtpVerificationsCustom.this).addToRequestQue(stringRequestLogIn);
            progressDialog.dismiss();


        }

    }

    private void alertVarificationSuccess() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Your account has been successfully verified");
        alertDialogBuilder.setNegativeButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(OtpVerificationsCustom.this, CompleteYourProfile.class);
                intent.putExtra("CHECK", "1");
                startActivity(intent);
                finish();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_terms_and_cond:
                Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/blog/terms-conditions-mobile-apps/"));
                startActivity(Getintent);
                break;
            case R.id.tv_privacy_policy:
                Intent Getintent1 = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.iubenda.com/en/help/11552-privacy-policy-for-android-apps"));
                startActivity(Getintent1);
                break;
        }
    }
}
