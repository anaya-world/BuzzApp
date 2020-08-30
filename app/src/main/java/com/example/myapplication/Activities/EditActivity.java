package com.example.myapplication.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class EditActivity extends AppCompatActivity {
    Button edit_email_btn;
    EditText user_mobile, et_emails;
    ProgressDialog progressDialog;
    ProgressBar varifyaccount_social;
    AlertDialog alertDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        edit_email_btn=findViewById ( R.id.edit_button_SignUp);

        et_emails = (EditText) findViewById(R.id.et_edit_emial);
        String emails = SharedPrefManager.getInstance(EditActivity.this).getUser().getUser_email().toString();
        et_emails.setText(emails);


        edit_email_btn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if(!et_emails.getText().toString().matches(emailPattern)) {
                    et_emails.setError("Enter Valid Email Id");
                    et_emails.requestFocus();
                }else if (TextUtils.isEmpty(et_emails.getText().toString())) {
                    Toast.makeText(EditActivity.this, "Email id can't be empty", Toast.LENGTH_SHORT).show();
                    et_emails.requestFocus();

                }else {
                    alertDialogAccountVarification();

                }

            }
        } );
    }

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

    private void validateViaMobile() {
        //varifyaccount_social.setVisibility( View.VISIBLE);
        final String user_id = SharedPrefManager.getInstance(EditActivity.this).getUser().getUser_id().toString();
        final String LOGIN_URL = BaseUrl_ConstantClass.BASE_URL;
        final String email=et_emails.getText().toString();
        progressDialog = new ProgressDialog (this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Connecting...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequestLogIn = new StringRequest( Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            // varifyaccount_social.setVisibility(View.INVISIBLE);
                            String status = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (status.equals("true")) {

                                otp = jsonObject.getString("otp");
                               //alertDialogAccountVarification();

                                Intent intent = new Intent (EditActivity.this, OtpVerificationsCustom.class);
                                intent.putExtra("OTP", otp);
                                startActivity(intent);
                                et_emails.setText ("" );
                                Toast.makeText(EditActivity.this, "OTP has been sent to your new registered email id" , Toast.LENGTH_LONG).show();
                                finish();

                            }else {
                                Toast.makeText(getApplicationContext(),"email id already registered",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(EditActivity.this, "It seems your internet is slow" ,Toast.LENGTH_LONG).show();
                        } else if (error instanceof com.android.volley.AuthFailureError) {
                            //TODO
                        } else if (error instanceof ServerError) {
                            Toast.makeText(EditActivity.this, "sorry for inconvenience, Server is not working" ,
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(EditActivity.this, "It seems your internet is slow",Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "otpOnNewEmail");
                logParams.put("userid", user_id);
                logParams.put("email", email);


                return logParams;
            }
        };

        MySingleTon.getInstance(EditActivity.this).addToRequestQue(stringRequestLogIn);
        progressDialog.dismiss();
    }
    private void alertDialogAccountVarification() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // alertDialogBuilder.setTitle("We will be verifying your Email ID");
        alertDialogBuilder.setMessage("We will be verifying your Email ID");
        alertDialogBuilder.setMessage("Would you like to continue");

        alertDialogBuilder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                validateViaMobile ();
//                Intent intent = new Intent(EditActivity.this,OptVerificationsCustom.class);
//                intent.putExtra("OTP", otp);
//                startActivity(intent);
                alertDialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
