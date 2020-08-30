package com.example.myapplication.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.GroupChats.GroupChannelListFragment;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.PrefUtils;
import com.example.myapplication.Utils.PreferenceUtils;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.syncmanager.SendBirdSyncManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
    Switch chat_alert;
    Switch friends_alert;
    Switch group_alert;
    ImageView iv_back;
    TextView terminate;
    TextView logout;
    TextView privacy;
    TextView term;
    TextView Faq;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.iv_back = ( ImageView ) findViewById ( R.id.iv_back );
        this.iv_back.setOnClickListener ( this );
        this.friends_alert = ( Switch ) findViewById ( R.id.friends_alert );
        this.friends_alert.setOnCheckedChangeListener ( this );
        this.chat_alert = ( Switch ) findViewById ( R.id.chat_alert );
        this.chat_alert.setOnCheckedChangeListener ( this );
        this.group_alert = ( Switch ) findViewById ( R.id.group_alert );
        this.group_alert.setOnCheckedChangeListener ( this );
//        this.terminate = ( TextView ) findViewById ( R.id.terminateAccount );
//        this.logout = ( TextView ) findViewById ( R.id.tv_logouts );
        this.term = ( TextView ) findViewById ( R.id.termof_service );
        this.Faq = ( TextView ) findViewById ( R.id.faq );
        this.privacy = findViewById ( R.id.privacy_policy );
        SharedPreferences sharedPreferences=getSharedPreferences ( "save",MODE_PRIVATE );
        group_alert.setChecked ( sharedPreferences.getBoolean ( "value",true ) );
        chat_alert.setChecked ( sharedPreferences.getBoolean ( "value",true ) );
        friends_alert.setChecked ( sharedPreferences.getBoolean ( "value",true ) );




        privacy.setOnClickListener ( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.iubenda.com/en/help/11552-privacy-policy-for-android-apps"));
                startActivity(Getintent);

            }
            //      https://www.iubenda.com/en/help/11552-privacy-policy-for-android-apps

        } );
        Faq.setOnClickListener ( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent Getintent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://ibuildapp.com/faq-android/"));
                startActivity(Getintent);
            }

        } );
        term.setOnClickListener ( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent Getintent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.termsfeed.com/blog/terms-conditions-mobile-apps/"));
                startActivity(Getintent);

            }
        } );


//        logout.setOnClickListener ( new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                logout ();
//
//
//            }
//
//        } );


//        terminate.setOnClickListener ( new View.OnClickListener()
//        {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
//            @Override
//            public void onClick(View view)
//            {
//
//                deleteAccountpopup ();
//
//
//            }
//
//        });
    }

    public void onCheckedChanged(CompoundButton buttonView , boolean isChecked) {

        int id = buttonView.getId();
        if (id != R.id.chat_alert) {
            if (id != R.id.friends_alert) {
                if (id != R.id.group_alert) {

                    return;
                }
            } else if (isChecked) {
                this.friends_alert.setText("On");
                //  Toast.makeText ( getApplicationContext (),"Notification on", LENGTH_SHORT ).show ();
                final Intent emptyIntent = new Intent(SettingsActivity.this,
                        UserNavgation.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_on)
                        .setContentTitle("New message")
                        .setContentText("New messgae")
                        .setContentIntent(pendingIntent);
                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
                notificationManager.notify(0, mBuilder.build());

                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("value", true);
                editor.apply();
                this.friends_alert.setChecked(true);


                return;
            } else {
                this.friends_alert.setText("Off");
                //   Toast.makeText ( getApplicationContext (),"Notification of" , LENGTH_SHORT).show ();
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();

                editor.putBoolean("value", false);
                editor.apply();
                this.friends_alert.setChecked(false);

                return;
            }
        } else if (isChecked){

//        {
//            this.chat_alert.setText ( "On" );
//            //  Toast.makeText ( getApplicationContext (),"Notification on", LENGTH_SHORT ).show ();
//            final Intent emptyIntent = new Intent(getApplicationContext (), UserNavgation.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext (),0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            NotificationCompat.Builder mBuilder =
//                    new NotificationCompat.Builder(this)
//                            .setSmallIcon(R.drawable.notification_on)
//                            .setContentTitle("My notification")
//                            .setContentText("Hello World!")
//                            .setContentIntent(pendingIntent);
//            mBuilder.setSound(RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION));
//            NotificationManager notificationManager = ( NotificationManager ) getSystemService(getApplicationContext ().NOTIFICATION_SERVICE);
//            notificationManager.notify( 0, mBuilder.build());
//
//            SharedPreferences.Editor editor=getSharedPreferences ( "save",MODE_PRIVATE ).edit ();
//            editor.putBoolean ( "value",true );
//            editor.apply ();
//            this.chat_alert.setChecked ( true );
//
//
//
//        }
    }else
        {
            this.chat_alert.setText ( "Off" );
            //    Toast.makeText ( getApplicationContext (),"Notification of" , LENGTH_SHORT).show ();
            SharedPreferences.Editor editor=getSharedPreferences ( "save",MODE_PRIVATE ).edit ();
            editor.putBoolean ( "value" ,false);
            editor.apply ();
            this. chat_alert.setChecked ( false );
            return;
        }
        if (isChecked==true)
        {
            this.group_alert.setText ( "On" );
            //  Toast.makeText ( getApplicationContext (),"Notification on", LENGTH_SHORT ).show ();
            SharedPreferences.Editor editor=getSharedPreferences ( "save",MODE_PRIVATE ).edit ();
            editor.putBoolean ( "value",true );
            editor.apply ();
            this.group_alert.setChecked ( true );


        }else

        {
            this.group_alert.setText ( "Of" );
            //  Toast.makeText ( getApplicationContext (),"Notification of" , LENGTH_SHORT).show ();
            SharedPreferences.Editor editor=getSharedPreferences ( "save",MODE_PRIVATE ).edit ();
            editor.putBoolean ( "value" ,false);
            editor.apply ();
            this.group_alert.setChecked ( false );
        }
    }


    public void onClick(View v) {
        if (v.getId () == R.id.iv_back)
        {
            finish ();
        }



    }


    public void show(FragmentManager supportFragmentManager, String country_picker) {


//            AlertDialog.Builder builder = new AlertDialog.Builder ( getApplicationContext () );
//            LayoutInflater layoutInflater = this.getLayoutInflater ();
//            View view = layoutInflater.inflate ( R.layout.areyousure , null );
//            builder.setView ( view );
//
//            areyou = view.findViewById ( R.id.areyou );
//
//
//            builder.setMessage ( R.string.dialog_fire_missiles )
//                    .setPositiveButton ( R.string.fire , new DialogInterface.OnClickListener () {
//                        public void onClick(DialogInterface dialog , int id) {
//
//                            // FIRE ZE MISSILES!
//                        }
//                    } )
//                    .setNegativeButton ( R.string.cancel , new DialogInterface.OnClickListener () {
//                        public void onClick(DialogInterface dialog , int id) {
//                            // User cancelled the dialog
//                        }
//                    } ).setPositiveButton ( "Post" , new DialogInterface.OnClickListener () {
//
//                @Override
//                public void onClick(DialogInterface dialogInterface , int i) {
//
//
//
//                }
//            } );
//            // Create the AlertDialog object and return it
//            builder.create ().show ();
//        }
//

    }
    public  void terminate()
    {
        String str = SharedPrefManager.getInstance(getApplicationContext ()).getUser().getUser_id().toString();

        StringRequest r2 = new StringRequest( Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String> ()
        {
            public void onResponse(String response)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    String id= jsonObject.optString("id");
                    String status = jsonObject.optString ("success");

                    if (!SharedPrefManager.getInstance ( getApplicationContext () ).isLoggedIn ())
                    {
                        startActivity ( new Intent ( getApplicationContext () , LoginRegistrationActivity.class ) );
                        finish ();
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

                    SendBirdCall.deauthenticate(PrefUtils.getPushToken(SettingsActivity.this), new com.sendbird.calls.handler.CompletionHandler() {
                        @Override
                        public void onResult(com.sendbird.calls.SendBirdException e) {
                            PrefUtils.setUserId(SettingsActivity.this, null);
                            PrefUtils.setAccessToken(SettingsActivity.this, null);
                            PrefUtils.setCalleeId(SettingsActivity.this, null);
                            PrefUtils.setPushToken(SettingsActivity.this, null);
                        }
                    });
                    SharedPrefManager.getInstance ( getApplicationContext () ).logout ( SettingsActivity.this );


                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        }, new Response.ErrorListener ()
        {
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext (), "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "deactivate");
                logParams.put("userid", str);
                return logParams;
            }
        };
        MySingleTon.getInstance(getApplicationContext ()).addToRequestQue(r2);


    }
    public void logout()
    {


        if (!SharedPrefManager.getInstance ( getApplicationContext () ).isLoggedIn ())
        {
            startActivity ( new Intent ( getApplicationContext () , LoginRegistrationActivity.class ) );
            SendBird.disconnect(new SendBird.DisconnectHandler() {
                @Override
                public void onDisconnected() {


                }
            });
            finish ();
            return;
        }
//        SharedPrefsHelper.getInstance ().save ( SharedPrefsHelper.CHANNEL_LIST , null );
//        SharedPrefsHelper.getInstance ().save ( SharedPrefsHelper.SEND_BIRD_USER_ID , null );
        SharedPrefManager.getInstance ( getApplicationContext () ).logout ( SettingsActivity.this );

    }
    public void deleteAccountpopup()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder (this);
        alertDialogBuilder.setMessage((CharSequence) "Are you sure want to delete your account?");
        alertDialogBuilder.setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                SettingsActivity.this.alertDialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                SettingsActivity.this.terminate ();
                SettingsActivity.this.alertDialog.dismiss();
//                Intent intent=new Intent (SettingsActivity.this,LoginRegistrationActivity.class);
//                startActivity (intent);
            }

        });

        this.alertDialog = alertDialogBuilder.create();
        this.alertDialog.show();

    }
//    public void showChannelOptionsDialog(final GroupChannel channel) {
//        final boolean pushCurrentlyEnabled = channel.isPushEnabled();
//        String[] options = pushCurrentlyEnabled ? new String[]{
//
//                "Delete channel", "Turn push notifications OFF"} : new String[]{
//
//                "Delete channel", "Turn push notifications ON"
//
//
//        };
//        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//        builder.setTitle((CharSequence) "Channel options").setItems((CharSequence[]) options, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == 0) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this.getApplicationContext());
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("Delete channel ");
//                    sb.append(channel.getName());
//                    sb.append("?");
//                    builder.setTitle((CharSequence) sb.toString()).setPositiveButton((CharSequence) "Delete", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                        //    leaveChannel(channel);
//                        }
//                    }).setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) null).create().show();
//                } else if (which == 1) {
//                    SettingsActivity.this.setChannelPushPreferences(channel, true ^ pushCurrentlyEnabled);
//                }
//            }
//        });
//
//
//
//
//        builder.create().show();
//    }
//
//    /* access modifiers changed from: private */
//    public void setChannelPushPreferences(GroupChannel channel, final boolean on) {
//        channel.setPushPreference(on, new GroupChannel.GroupChannelSetPushPreferenceHandler() {
//            public void onResult(SendBirdException e) {
//                if (e != null) {
//                    e.printStackTrace();
//                    FragmentActivity activity = SettingsActivity.this;
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("Error: ");
//                    sb.append(e.getMessage());
//                    Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//
//                Toast.makeText(SettingsActivity.this, on ? "Push notifications have been turned ON" : "Push notifications have been turned OFF", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

}
