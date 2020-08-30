package com.example.myapplication.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForggotBuzzid extends AppCompatActivity {

    Button reset_password;
    RelativeLayout relative_layout_mobile_or_email, relative_layout_passkey;
    Spinner alphabets_spinner_forgorpassword;
    EditText numeric_code_forgot, passkey_ed, et_buzz_id;
    String security_code = "";
    String alphabeticalletters;
    int length;
    String sec = "";
    ProgressDialog pd;
    String var7 = "";
    Spinner alphabets_spinner_numeric_code;
    int i;
    ArrayList<String> aListNumbers;
    ArrayAdapter<String> adapter;
    AlertDialog alertDialog;
    ImageView question_mark_hint;
    private String type = "Secret Id";
    private String buzz_id, numeric_CODE;
    TextView termandconditions,privacypolicy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forggot_buzzid);
        question_mark_hint = (ImageView) findViewById(R.id.question_mark_hint);
        passkey_ed = (EditText)findViewById(R.id.et_passkey);
        privacypolicy=(TextView)findViewById(R.id.privacypolicy);
        termandconditions=(TextView)findViewById(R.id.termandcondition);
        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.iubenda.com/en/help/11552-privacy-policy-for-android-apps"));
                startActivity(Getintent);

            }
        });
        termandconditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/blog/terms-conditions-mobile-apps/"));
                startActivity(Getintent);

            }
        });
        question_mark_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsQuestionMarkPopup();
            }
        });


        Integer[] testArray = new Integer[1000];
        aListNumbers = new ArrayList<String>();

        for (i = 0; i < testArray.length; i++) {
            testArray[i] = i;
            String formated = String.format("%03d", i);//here all list print one by one with loop i have to pass in spinner
            aListNumbers.add(formated);
        }


        alphabets_spinner_numeric_code = (Spinner) findViewById(R.id.alphabets_spinner_numeric_code);
        alphabets_spinner_numeric_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    /*Toast.makeText(CompleteYourProfile.this, ""+ adapter_interest.getItem(position).toString(), Toast.LENGTH_LONG).show();
                    var1=adapter_interest.getItem(position).toString();
                    arrayList_spinner_interest.add(var1);*/
                ((TextView) parent.getChildAt(0)).setTextColor(ForggotBuzzid.this.getResources().getColor(R.color.white));
                var7 = alphabets_spinner_numeric_code.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        et_buzz_id = (EditText) findViewById(R.id.et_buzz_id);
        reset_password = (Button) findViewById(R.id.reset_password);
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buzz_id = et_buzz_id.getText().toString().trim();
                String value = buzz_id;
              /*  numeric_CODE= numeric_code_forgot.getText().toString();
                length = numeric_code_forgot.getText().length();*/
/*

                if (TextUtils.isEmpty(numeric_CODE))
                {
                    numeric_code_forgot.setError("Numeric code  Can't  be empty");
                    numeric_code_forgot.requestFocus();
                    return;
                }
                else if (length<3){
                    numeric_code_forgot.setError("Enter min 3 Numbers");
                    numeric_code_forgot.requestFocus();
                }
*/
                Log.d("null1",""+type+"--"+buzz_id+"--"+value);


                if ((!Patterns.EMAIL_ADDRESS.matcher(value).matches()) && ((!Patterns.PHONE.matcher(value).matches()) && (value.length() < 10))) {

                    et_buzz_id.setError("Please enter valid email/number");
                } else
                    forgotPasswodApiPasskey();



            }
        });


        // numeric_code_forgot=(EditText)findViewById(R.id.numeric_code_forgot);

        alphabets_spinner_forgorpassword = (Spinner) findViewById(R.id.alphabets_spinner_forgorpassword);

        alphabets_spinner_forgorpassword.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(ForggotBuzzid.this.getResources().getColor(R.color.white));
                alphabeticalletters = (String) parent.getItemAtPosition(position);
                security_code=alphabets_spinner_forgorpassword.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        List<String> list = new ArrayList<String>();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        list.add("H");
        list.add("I");
        list.add("J");
        list.add("K");
        list.add("L");
        list.add("M");
        list.add("N");
        list.add("O");
        list.add("P");
        list.add("Q");
        list.add("R");
        list.add("S");
        list.add("T");
        list.add("U");
        list.add("V");
        list.add("W");
        list.add("X");
        list.add("Y");
        list.add("Z");

        ArrayAdapter<String> adapter_alphabet = new ArrayAdapter<String>(this, R.layout.spinner_item, list);
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, aListNumbers);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        adapter_alphabet.setDropDownViewResource(R.layout.spinner_item);

        alphabets_spinner_forgorpassword.setAdapter(adapter_alphabet);
        alphabets_spinner_numeric_code.setAdapter(adapter);


        relative_layout_mobile_or_email = (RelativeLayout) findViewById(R.id.relative_layout_mobile_or_email);
        // relative_layout_mobile_or_email.setVisibility(View.VISIBLE);
        relative_layout_passkey = (RelativeLayout) findViewById(R.id.relative_layout_passkey);
        // relative_layout_passkey.setVisibility(View.GONE);


//        button_passkey = (Button) findViewById(R.id.button_passkey);
//        button_passkey.setOnClickListener(this);
//
//        button_secret_id = (Button) findViewById(R.id.button_secret_id);
//        button_secret_id.setOnClickListener(this);

        type = "Secret Id";
        /*String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
        if (sessionId.equals("secret")) {
            relative_layout_passkey.setVisibility(View.GONE);
            relative_layout_mobile_or_email.setVisibility(View.VISIBLE);
            // startActivity(new Intent(this,RegistrationActivity.class));
            button_secret_id.setBackgroundDrawable(ContextCompat.getDrawable(ForgotPasswordActivity.this, R.drawable.signin_button));
            button_passkey.setBackgroundDrawable(ContextCompat.getDrawable(ForgotPasswordActivity.this, R.drawable.signin_button_voilet));
            button_secret_id.setTextColor((Color.parseColor("#000000")));
            button_passkey.setTextColor(getResources().getColor(R.color.white));

        } else {
            type = "Email";

            relative_layout_passkey.setVisibility(View.VISIBLE);
            relative_layout_mobile_or_email.setVisibility(View.GONE);
            button_secret_id.setBackgroundDrawable(ContextCompat.getDrawable(ForgotPasswordActivity.this, R.drawable.signin_button_voilet));
            button_secret_id.setTextColor((Color.parseColor("#000000")));
            button_passkey.setBackgroundDrawable(ContextCompat.getDrawable(ForgotPasswordActivity.this, R.drawable.signin_button));
            button_passkey.setTextColor(getResources().getColor(R.color.white));


        }*/
    }


    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
           /* case R.id.button_secret_id:
                relative_layout_passkey.setVisibility(View.GONE);
                relative_layout_mobile_or_email.setVisibility(View.VISIBLE);
                // startActivity(new Intent(this,Registratiobutton_passkeynActivity.class));
                button_secret_id.setBackgroundDrawable(ContextCompat.getDrawable(ForgotPasswordActivity.this, R.drawable.signin_button));
                button_passkey.setBackgroundDrawable(ContextCompat.getDrawable(ForgotPasswordActivity.this, R.drawable.signin_button_voilet));
                button_secret_id.setTextColor((Color.parseColor("#000000")));
                button_passkey.setTextColor((Color.parseColor("#FFFFFF")));

                type = "Secret Id";
                break;


            case R.id.button_passkey:
                relative_layout_passkey.setVisibility(View.VISIBLE);
                relative_layout_mobile_or_email.setVisibility(View.GONE);
                button_secret_id.setBackgroundDrawable(ContextCompat.getDrawable(ForgotPasswordActivity.this, R.drawable.signin_button_voilet));
                button_secret_id.setTextColor((Color.parseColor("#FFFFFF")));
                button_passkey.setBackgroundDrawable(ContextCompat.getDrawable(ForgotPasswordActivity.this, R.drawable.signin_button));
                button_passkey.setTextColor(getResources().getColor(R.color.white));


                type = "Email";


                break;
        }*/
        }
    }

    private void detailsQuestionMarkPopup() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("please select security code from the dropdowm list that was used on the profile screen");
        alertDialogBuilder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


//    private void    forgotPasswodApiBuzzid(String value){
//        final String LOGIN_URL = BaseUrl_ConstantClass.BASE_URL;
//        pd = new ProgressDialog(this);
//        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pd.setMessage("Please Wait...");
//        pd.setIndeterminate(true);
//        pd.setCancelable(false);
//        pd.show();
//
//
//        if( security_code.equals("") || var7.equals("")){
//            Toast.makeText(this, "Select security code first..!!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        sec = security_code + var7;
//        Log.d("null1",sec);
//        StringRequest stringRequestLogIn = new StringRequest(Request.Method.POST, LOGIN_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        pd.dismiss();
//                        JSONObject jsonObject = null;
//                        try {
//                            jsonObject = new JSONObject(response);
//                            String status = jsonObject.getString("success");
//                            String message = jsonObject.getString("message");
//                            emailedSuccessDialog(message,status);
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        pd.dismiss();
//                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            Toast.makeText(ForggotBuzzid.this, "it seems your network is slow" ,
//                                    Toast.LENGTH_LONG).show();
//                        } else if (error instanceof AuthFailureError) {
//                            //TODO
//                        } else if (error instanceof ServerError) {
//                            Toast.makeText(ForggotBuzzid.this, "sorry for inconvenience, Server is not working" ,
//                                    Toast.LENGTH_LONG).show();
//                        } else if (error instanceof NetworkError) {
//                            Toast.makeText(ForggotBuzzid.this, "it seems your network is slow",
//                                    Toast.LENGTH_LONG).show();
//                        } else if (error instanceof ParseError) {
//                            //TODO
//                        }
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                Map<String, String> logParams = new HashMap<>();
//                logParams.put("action", "forgot_password");
//                logParams.put("email", value);
//                logParams.put("securitycode", sec);
//                return logParams;
//            }
//        };
//        MySingleTon.getInstance(ForggotBuzzid.this).addToRequestQue(stringRequestLogIn);
//    }

    private void forgotPasswodApiPasskey() {
        if (TextUtils.isEmpty(buzz_id)) {
            et_buzz_id.setError("Email ID can't  be empty ");
            et_buzz_id.requestFocus();
            return;
        } else {
            final String PPASSKEY_URL = BaseUrl_ConstantClass.BASE_URL;
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Please Wait...");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();

            StringRequest stringRequestpasskey = new StringRequest(Request.Method.POST, PPASSKEY_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            JSONObject jsonObject1 = null;
                            try {
                                jsonObject1 = new JSONObject(response);
                                String status = jsonObject1.getString("success");
                                String message = jsonObject1.getString("message");
                                if (status.equals("false")) {

                                    //   Toast.makeText(ForgotPasswordActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                                    emailedSuccessDialog(message,status);

                                    // finish();

                                } else {
                                    emailedSuccessDialog(message,status);
                                    //Toast.makeText(LoginActivity.this, "Please Enter Valid User Id or Password", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            pd.dismiss();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                         //       Toast.makeText(ForggotBuzzid.this, "it sesms your internet is slow" ,
                                    //    Toast.LENGTH_LONG).show();
                            } else if (error instanceof AuthFailureError) {
                                //TODO
                            } else if (error instanceof ServerError) {
                                Toast.makeText(ForggotBuzzid.this, "sorry for inconvenience, Server is not working" ,
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(ForggotBuzzid.this, "it seems your internet is slow" ,
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof ParseError) {
                                //TODO
                            }
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    String sec = "";
                    sec = security_code + var7;


                    Map<String, String> logParams = new HashMap<>();
                    logParams.put("action", "forgot_passkey");
                    logParams.put("buzzid", buzz_id);
                    logParams.put("securitycode", sec);


                    return logParams;
                }
            };

            MySingleTon.getInstance(ForggotBuzzid.this).addToRequestQue(stringRequestpasskey);


        }

    }

    private void  emailedSuccessDialog(final String message, String status) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              //  Toast.makeText(getApplicationContext()," Buzz ID has been sent to your registered Email ID",Toast.LENGTH_SHORT).show();
                // if (message.equals("Email send successfully...")) {
                if(status.equals("true")) {
                    Intent intent = new Intent(ForggotBuzzid.this, LoginRegistrationActivity.class);
                    startActivity(intent);
                }
                else {
                    et_buzz_id.requestFocus();
                }
                //} else {
                alertDialog.dismiss();
                // }


            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
