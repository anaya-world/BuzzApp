package com.example.myapplication.FingerPrintHandler;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.AuthenticationCallback;
import android.hardware.fingerprint.FingerprintManager.AuthenticationResult;
import android.hardware.fingerprint.FingerprintManager.CryptoObject;
import android.os.CancellationSignal;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Activities.SecurityCodeValidationActivity;
import com.example.myapplication.Activities.UserNavgation;
import com.example.myapplication.Intefaces.OnFingerPrintRegisterListener;
import com.example.myapplication.ModelClasses.User;
import com.example.myapplication.Utils.BaseUrl_ConstantClass;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = 23)
public class FingerprintHandler extends AuthenticationCallback {
    /* access modifiers changed from: private */
    public Context context;
    /* access modifiers changed from: private */


    private boolean isAttached;
    public String user_id;
    private OnFingerPrintRegisterListener onFingerPrintRegisterListener;


    public String device_id1;

    public FingerprintHandler(Context mContext, String device_id) {
        this.context = mContext;
        this.device_id1 = device_id;
        Log.e("msg", "1" + device_id1);

    }


    public FingerprintHandler(Context mContext, String deviceID, String myuserid) {
        Log.e("userid", "user" + myuserid);
        this.context = mContext;
        this.device_id1 = deviceID;
        this.user_id = myuserid;
    }

    public FingerprintHandler(Context mContext, String device_id, boolean isAttached2, OnFingerPrintRegisterListener onFingerPrintRegisterListener2) {
        this.context = mContext;
        this.device_id1 = device_id;
        this.isAttached = isAttached2;
        this.onFingerPrintRegisterListener = onFingerPrintRegisterListener2;


    }

    public void startAuth(FingerprintManager manager, CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(this.context, "android.permission.USE_FINGERPRINT") == 0) {
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }
    }

    public void onAuthenticationError(int errMsgId, CharSequence errString) {

        StringBuilder sb1 = new StringBuilder();
        sb1.append("Fingerprint Authentication error\n");
        sb1.append(errString);
        if (this.isAttached) {
            this.onFingerPrintRegisterListener.onFingerPrintRegister(false, sb1.toString());
            return;
        }
        update(sb1.toString(), false);
    }

    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {

        StringBuilder sb = new StringBuilder();
        sb.append("Fingerprint Authentication help\n");
        sb.append(helpString);
        if (this.isAttached) {
            this.onFingerPrintRegisterListener.onFingerPrintRegister(false, sb.toString());
            return;
        }
        update(sb.toString(), false);

    }

    public void onAuthenticationFailed() {
        if (this.isAttached) {
            this.onFingerPrintRegisterListener.onFingerPrintRegister(false, "Fingerprint didn't Match..Try Again");
            return;
        }
        update("Fingerprint didn't match..Try again", false);
    }

    public void onAuthenticationSucceeded(AuthenticationResult result) {
        if (this.isAttached) {
            this.onFingerPrintRegisterListener.onFingerPrintRegister(true, "success");
            return;
        }
        update("", true);
        login();

    }

    public void update(String e, Boolean success) {
        Log.d("onAuthentication", "" + e + success);
//        TextView textView = (TextView) ((AppCompatActivity) this.context).findViewById(R.id.errorText);
//        textView.setText(e);
//        textView.setTextColor(ContextCompat.getColor(this.context, R.color.color_white));

        if (success) {

            Log.d("onAuthentication", "" + e + success);
            login();
        } else {
            Log.d("onAuthentication", "" + e + success);
            Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void signInWithFingerPrintApi(String device_id, Context context2) {
    }

    private void login() {

        String str = BaseUrl_ConstantClass.BASE_URL;
        //  user_id=SharedPrefManager.getInstance(FingerprintHandler.this.context).getUser().getUser_id().toString();
        StringRequest r1 = new StringRequest(Request.Method.POST, BaseUrl_ConstantClass.BASE_URL, new Listener<String>() {
            public void onResponse(String response) {

                Log.d("res", "1" + response);
                try {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("success").equals("true")) {
                            SharedPrefManager.getInstance(FingerprintHandler.this.context).save("check", 1);
                            SharedPrefManager.getInstance(context).save("checkautostart", 1);
                            JSONObject details = jsonObject.getJSONObject("user_details");
                            String id = details.optString("id");
                            Log.d("id", "ids" + id);
                            String secrate_id = details.optString("secrate_id");
                            String mobileno = details.optString("mobileno");
                            String email = details.optString("email");
                            String name = details.optString("name");
                            String caller_id = details.optString("caller_id");
                            String security_code = details.optString("security_code");
                            User user = new User(id, name, email, mobileno, " ", secrate_id, "", "", "false", caller_id);
                            User user2 = user;

                            // Intent i = new Intent(FingerprintHandler.this.context, UserNavgation.class);

                            Intent i = new Intent(FingerprintHandler.this.context, SecurityCodeValidationActivity.class);
                            i.putExtra("id", id);
                            i.putExtra("name", name);
                            i.putExtra("email", email);
                            i.putExtra("mobile", mobileno);
                            i.putExtra("secreet", secrate_id);
                            i.putExtra("caller_id", caller_id);

                            i.putExtra("User", user2);
                            i.putExtra("security_codekey", security_code);
                            FingerprintHandler.this.context.startActivity(i);


                            return;
                        }
                        Toast.makeText(FingerprintHandler.this.context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        Log.d("resd", "2");
                    } catch (JSONException e) {
                        e = e;
                        e.printStackTrace();
                    }
                } catch (Exception e2) {

                    String str = response;
                    e2.printStackTrace();
                }

            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FingerprintHandler.this.context, "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "finger_login");

                logParams.put("device_id", FingerprintHandler.this.device_id1);
//                logParams.put("user_id",FingerprintHandler.this.user_id);
                return logParams;
            }

        };

        MySingleTon.getInstance(this.context).addToRequestQue(r1);
    }




}
