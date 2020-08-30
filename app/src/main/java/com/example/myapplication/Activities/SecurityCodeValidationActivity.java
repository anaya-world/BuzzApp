package com.example.myapplication.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import com.example.myapplication.ModelClasses.User;
import com.example.myapplication.R;
import com.example.myapplication.Utils.PrefUtils;
import com.example.myapplication.Utils.PreferenceUtils;
import com.example.myapplication.Utils.PushUtils;
import com.example.myapplication.Utils.SharedPrefManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.SendBirdCall;

import java.util.ArrayList;
import java.util.List;

public class SecurityCodeValidationActivity extends AppCompatActivity {
    ArrayList<String> aListNumbers;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter_alphabet;
    String alphabeticalletters;
    Spinner alphabets_spinner_forgorpassword;
    Spinner alphabets_spinner_numeric_code;
    Button button_validate_OTP;
    int i;
  //  private QBUser userForSave;
    String security_code = ExifInterface.GPS_MEASUREMENT_IN_PROGRESS;
    String securitycode;
    String securitycodek;
    String var7 = "";
    private String TAG="SecurityCodeValidationActivity";
    String user_img;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_code_validation);
        this.button_validate_OTP = (Button) findViewById(R.id.button_validate_OTP);
        this.alphabets_spinner_forgorpassword = (Spinner) findViewById(R.id.alphabets_spinner_forgorpassword);
        this.alphabets_spinner_numeric_code = (Spinner) findViewById(R.id.alphabets_spinner_numeric_code);
        this.securitycodek = getIntent().getStringExtra("security_codekey");
        Integer[] testArray = new Integer[1000];
        this.aListNumbers = new ArrayList<>();
        this.i = 0;
        while (this.i < testArray.length) {
            testArray[this.i] = Integer.valueOf(this.i);
            this.aListNumbers.add(String.format("%03d", new Object[]{Integer.valueOf(this.i)}));
            this.i++;
        }
        this.alphabets_spinner_numeric_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SecurityCodeValidationActivity.this.var7 = SecurityCodeValidationActivity.this.alphabets_spinner_numeric_code.getSelectedItem().toString();
                ((TextView) parent.getChildAt(0)).setTextColor(SecurityCodeValidationActivity.this.getResources().getColor(R.color.white));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.alphabets_spinner_forgorpassword.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SecurityCodeValidationActivity.this.alphabeticalletters = (String) parent.getItemAtPosition(position);
                ((TextView) parent.getChildAt(0)).setTextColor(SecurityCodeValidationActivity.this.getResources().getColor(R.color.white));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        List<String> list = new ArrayList<>();
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
        this.adapter_alphabet= new ArrayAdapter<>(this, R.layout.spinner_item, list);
        this.adapter_alphabet.setDropDownViewResource(R.layout.spinner_item);
        this.adapter = new ArrayAdapter<>(this, R.layout.spinner_item, this.aListNumbers);
        this.adapter.setDropDownViewResource(R.layout.spinner_item);
        this.alphabets_spinner_forgorpassword.setAdapter(adapter_alphabet);
        this.alphabets_spinner_numeric_code.setAdapter(this.adapter);
        this.button_validate_OTP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityCodeValidationActivity securityCodeValidationActivity = SecurityCodeValidationActivity.this;
                StringBuilder sb = new StringBuilder();
                sb.append(SecurityCodeValidationActivity.this.alphabeticalletters);
                sb.append(SecurityCodeValidationActivity.this.var7);
                securityCodeValidationActivity.securitycode = sb.toString();
                Log.d("abcde",""+sb.toString());
                Log.d("abcde",""+SecurityCodeValidationActivity.this.securitycodek);
                Log.d("abcde",""+SecurityCodeValidationActivity.this.securitycode);
                if (SecurityCodeValidationActivity.this.securitycodek.equals(SecurityCodeValidationActivity.this.securitycode)) {

                   // SecurityCodeValidationActivity.this.startActivity(new Intent(SecurityCodeValidationActivity.this, UserNavgation.class));//comment

                     String abc=getIntent().getStringExtra("User");
                     user_id=getIntent().getStringExtra("id");
                    String name=getIntent().getStringExtra("name");
                    String email=getIntent().getStringExtra("email");
                    String mobileno=getIntent().getStringExtra("mobile");
                    String secrate_id=getIntent().getStringExtra("secreet");
                    String caller_id=getIntent().getStringExtra("caller_id");

                    User user = new User(user_id, name, email, mobileno, " ", secrate_id, "", "", "true", caller_id);

//                    User user =  (User)

                    SharedPrefManager.getInstance(SecurityCodeValidationActivity.this).userLogin(user);
                    PreferenceUtils.setUserId(user_id);
                    PreferenceUtils.setNickname(name);
                    SecurityCodeValidationActivity.this.loginToChat1(user_id, "", secrate_id);

                    finish();

                }
                else {

                    AlertDialog.Builder ab = new AlertDialog.Builder(SecurityCodeValidationActivity.this);
                    ab.setMessage((CharSequence) "Please select valid Security Code!");
                    ab.setTitle((CharSequence) "Error");
                    ab.setCancelable(false);
                    ab.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    ab.create();
                    ab.show();
                }
            }
        });
    }

    public void loginToChat1(String currentuser, String pass, String name)
    {
        Log.d("lifecycle","2"+currentuser);

        connectToSendBird(currentuser,name);
       // SecurityCodeValidationActivity.this.pd.dismiss();
        updateCurrentUserInfo(name,user_img);
        SharedPrefManager.getInstance(SecurityCodeValidationActivity.this).save("check",1);
        SharedPrefManager.getInstance(SecurityCodeValidationActivity.this).save("checkautostart",1);

        Intent intent=new Intent(SecurityCodeValidationActivity.this, UserNavgation.class);
        intent.putExtra("profile_pic",user_img);
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

    public void updateCurrentUserInfo(String userNickname,String user_pic)
    {

        SendBird.updateCurrentUserInfo(userNickname, user_pic, new SendBird.UserInfoUpdateHandler ()
        {

            public void onUpdated(SendBirdException e) {
                if (e == null) {
                }
            }
        });
    }
//    private void connectsendBirds()
//    {
//        Toast.makeText(getApplicationContext(),"connected send bird",Toast.LENGTH_SHORT).show();
//
//        Log.d("lifecycle","8");
//      //  String userId = SharedPrefManager.getInstance(this).getUser().getUser_id().toString();
//
//        connectToSendBird(user_id.replaceAll("\\s", ""), SharedPrefManager.getInstance(this).getUser().getUser_name().toString());
//    }
//    public void loginToChat(QBUser qbUser2) {
//        qbUser2.setPassword(com.example.hp.chatapplication.VideoCall.utils.Consts.DEFAULT_USER_PASSWORD);
//        this.userForSave = qbUser2;
//        startLoginService(qbUser2);
//    }
//    private void startLoginService(QBUser qbUser2) {
//        CallService.start(this, qbUser2, createPendingResult(1002, new Intent(this, CallService.class), 0));
//    }
//    public void saveUserData(QBUser qbUser2) {
//        SharedPrefsHelper sharedPrefsHelper2 = SharedPrefsHelper.getInstance();
//        sharedPrefsHelper2.save(com.example.hp.chatapplication.VideoCall.utils.Consts.PREF_CURREN_ROOM_NAME, "Buzz");
//        sharedPrefsHelper2.saveQbUser(qbUser2);
//    }



    private void connectToSendBird(String userId, final String userNickname)
    {

        Log.d("lifecycle","10"+userId+userNickname);
        PrefUtils.setUserId(SecurityCodeValidationActivity.this, null);
        PrefUtils.setAccessToken(SecurityCodeValidationActivity.this, null);
        PrefUtils.setCalleeId(SecurityCodeValidationActivity.this, null);
        PrefUtils.setPushToken(SecurityCodeValidationActivity.this, null);
        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(com.sendbird.android.User user, SendBirdException e) {
                Log.d("lifecycle","LoginRegistrationActivity onCompleted"+e);
                //  FirebaseApp chats = FirebaseApp.getInstance("chats");
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SecurityCodeValidationActivity.this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d("lifecycle","LoginRegistrationActivity pushu onSuccess"+instanceIdResult.getToken());
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
//        SyncManagerUtils.setup(SecurityCodeValidationActivity.this, userId, new CompletionHandler() {
//            @Override
//            public void onCompleted(SendBirdException e) {
//                Log.d("lifecycle","login onCompleted"+e);
//                if (e != null) {
//                    e.printStackTrace();
//                    return;
//                }
//                SendBirdSyncManager.getInstance().resumeSync();
//                PreferenceUtils.setConnected(true);
//
//            }
//        });
        PushUtils.getPushToken(this, (pushToken, e) -> {
            if (e != null) {
                Log.d(TAG, "authenticate() => Failed (e: " + e.getMessage() + ")");

                return;
            }
            Log.d(TAG, "authenticate() => authenticate()"+pushToken);
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
}
