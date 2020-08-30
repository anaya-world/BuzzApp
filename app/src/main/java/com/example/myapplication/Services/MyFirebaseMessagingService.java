package com.example.myapplication.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.Activities.UserNavgation;
import com.example.myapplication.R;
import com.example.myapplication.Utils.PrefUtils;
import com.example.myapplication.Utils.PushUtils;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBirdPushHandler;
import com.sendbird.android.SendBirdPushHelper;
import com.sendbird.calls.SendBirdCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.webrtc.ContextUtils.getApplicationContext;

public class MyFirebaseMessagingService /*extends FirebaseMessagingService*/extends SendBirdPushHandler {

    public static final String NOTIFICATION_CHANNEL_ID = MyFirebaseMessagingService.class.getSimpleName();
    private static final String TAG = "MyFirebaseMessagingServ";

    private static final AtomicReference<String> pushToken = new AtomicReference<>();

    String strMessage;
    public static ArrayList<String> strArrList = new ArrayList<>();

    public interface ITokenResult {
        void onPushTokenReceived(String pushToken, SendBirdException e);
    }

    @Override
    public void onNewToken(String token) {
        pushToken.set(token);
    }

    @Override
    protected boolean isUniquePushToken() {
        return false;
    }

    public static void getPushToken(ITokenResult listener) {
        String token = pushToken.get();
        if (!TextUtils.isEmpty(token)) {
            listener.onPushTokenReceived(token, null);
            return;
        }

        SendBirdPushHelper.getPushToken((newToken, e) -> {
            if (listener != null) {
                listener.onPushTokenReceived(newToken, e);
            }

            if (e == null) {
                pushToken.set(newToken);
            }
        });

        if (SendBirdCall.getCurrentUser() != null)  {
            PushUtils.registerPushToken(getApplicationContext(), token, e -> {
                if (e != null) {
                    Log.d(TAG, "registerPushTokenForCurrentUser() => e: " + e.getMessage());
                }
            });
        } else {
            PrefUtils.setPushToken(getApplicationContext(), token);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(Context context, RemoteMessage remoteMessage) {
        Log.d(TAG, "[SendBirdCall Message] onMessageReceived() => " + remoteMessage.getData().toString());

        if (SendBirdCall.handleFirebaseMessageData(remoteMessage.getData())) {
            Log.d(TAG, "[SendBirdCall Message] onMessageReceived() => " + remoteMessage.getData().toString());
        } else {
            Log.d(TAG, "onMessageReceived() => From: " + remoteMessage.getFrom());
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "onMessageReceived() => Data: " + remoteMessage.getData().toString());
            }
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "onMessageReceived() => Notification Body: " + remoteMessage.getNotification().getBody());
            }
            String channelUrl = null;
            try {
                JSONObject sendBird = new JSONObject(remoteMessage.getData().get("sendbird"));
                JSONObject channel = (JSONObject) sendBird.get("channel");
                channelUrl = (String) channel.get("channel_url");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            strMessage = remoteMessage.getData().get("message");
            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
            if(strMessage.length()>0) {
                strArrList.add(strMessage);
                sendNotification(context, remoteMessage.getData().get("message"), channelUrl);
            }
        }
    }

   // @RequiresApi(api = Build.VERSION_CODES.M)
  /*  @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "[SendBirdCall Message] onMessageReceived() => " + remoteMessage.getData().toString());

        if (SendBirdCall.handleFirebaseMessageData(remoteMessage.getData())) {
            Log.d(TAG, "[SendBirdCall Message] onMessageReceived() => " + remoteMessage.getData().toString());
        } else {
            Log.d(TAG, "onMessageReceived() => From: " + remoteMessage.getFrom());
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "onMessageReceived() => Data: " + remoteMessage.getData().toString());
            }
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "onMessageReceived() => Notification Body: " + remoteMessage.getNotification().getBody());
            }
            String channelUrl = null;
            try {
                JSONObject sendBird = new JSONObject(remoteMessage.getData().get("sendbird"));
                JSONObject channel = (JSONObject) sendBird.get("channel");
                channelUrl = (String) channel.get("channel_url");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            strMessage = remoteMessage.getData().get("message");
            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
            sendNotification(this, remoteMessage.getData().get("message"), channelUrl);
        }
    }*/

   /* @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "onNewToken(token: " + token + ")");

        if (SendBirdCall.getCurrentUser() != null)  {
            PushUtils.registerPushToken(getApplicationContext(), token, e -> {
                if (e != null) {
                    Log.d(TAG, "registerPushTokenForCurrentUser() => e: " + e.getMessage());
                }
            });
        } else {
            PrefUtils.setPushToken(getApplicationContext(), token);
        }
    }*/


    /*@RequiresApi(api = Build.VERSION_CODES.M)
    public void sendNotification(Context context, String messageBody, String channelUrl) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);

        final String CHANNEL_ID = "CHANNEL_ID";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // Build.VERSION_CODES.O
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(mChannel);

        }

        Intent intent = new Intent(context, UserNavgation.class);
        intent.putExtra("URL", channelUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 *//* Request code *//*, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_buzz_logo)
                .setColor(Color.parseColor("#7469C4"))  // small icon background color
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_buzz_logo))
                .setColor(Color.parseColor("#7469C4"))
                .setContentTitle(messageBody)
                 .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_ALL).setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);
        notificationBuilder.setSound(RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION));



        notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());
    }*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendNotification(Context context, String messageBody, String channelUrl) {
        NotificationCompat.Builder notificationBuilder;
        NotificationManager notifManager = null;
        NotificationChannel mChannel = null;
        CharSequence name = context.getString(R.string.app_name);
        notifManager = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder;

            Intent intent = new Intent(context, UserNavgation.class);
            intent.putExtra("URL", channelUrl);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent;

            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel
                    ("0", name, importance);
            mChannel.setDescription(strArrList.get(0));
            mChannel.enableVibration(true);
            notifManager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(context, "0");

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 1251
                    , intent, PendingIntent.FLAG_UPDATE_CURRENT);


           /* PendingIntent snoozePendingIntent = PendingIntent.getService(this
                    , 0, dismissIntent, 0);*/
            /*NotificationCompat.Action dismissAction =
                    new NotificationCompat.Action.Builder(
                            R.mipmap.ic_tick,
                            getString(R.string.message_dismiss),
                            snoozePendingIntent)
                            .build();*/

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                    .setBigContentTitle(context.getResources().getString(R.string.app_name))
                    .setSummaryText(getUnexpandedContentText(strArrList.size()));

            for (int iCount = 0; iCount < strArrList.size(); iCount++) {
                inboxStyle.addLine(strArrList.get(iCount));
            }

            builder.setSmallIcon(R.drawable.app_buzz_logo) // required
                    .setContentText(getUnexpandedContentText(strArrList.size()))  // required
                    .setStyle(inboxStyle)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setNumber(strArrList.size())
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_buzz_logo))
                   /* .setBadgeIconType(R.mipmap.ic_launcher)*/
                    .setContentIntent(pendingIntent)
                    .setColor(Color.parseColor("#7469C4"))
                    .setSubText(Integer.toString(strArrList.size()))
                    /*.addAction(dismissAction)*/
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION));

            Notification notification = builder.build();
            notifManager.notify(0, notification);
        } else {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
           /* Intent intent = new Intent(this, ClubNewsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0  *//*Request code*//* , intent, 0);*/
            Intent intent = new Intent(context, UserNavgation.class);
            intent.putExtra("URL", channelUrl);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntentLow = PendingIntent.getActivity(context, 1251
                    , intent, PendingIntent.FLAG_UPDATE_CURRENT);


            notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.app_buzz_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_buzz_logo))
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText(getUnexpandedContentText(strArrList.size()))
                    .setAutoCancel(true)
                    .setNumber(strArrList.size())
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntentLow)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(getExpandedNotificationStyle(context, strArrList));

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            for (int iCount = 0; iCount < strArrList.size(); iCount++) {
                if(strArrList.size()<7) {
                    inboxStyle.addLine(strArrList.get(iCount));
                }
            }
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    /**
     * Create custom method to display notification like inbox style.
     */
    private NotificationCompat.Style getExpandedNotificationStyle (Context context, ArrayList< String > names) {
        NotificationCompat.InboxStyle expandedNotificationStyle = new NotificationCompat.InboxStyle();
        expandedNotificationStyle.setBigContentTitle(context.getResources().getString(R.string.app_name));

        /**
         * There seems to be a bug in the notification display system where, unless you set
         * summary text, single line expanded inbox state will not expand when the notif
         * drawer is fully pulled down. However, it still works in the lock-screen.
         */
        expandedNotificationStyle.setSummaryText(strArrList.size() + " messages received.");

        for (String name : names) {
            expandedNotificationStyle.addLine(name);
        }
        return expandedNotificationStyle;
    }

    /**
     * Implements method for unExpanded content area.
     */
    private String getUnexpandedContentText ( int numOfNotifications){
        return numOfNotifications + " messages received";
    }
}
