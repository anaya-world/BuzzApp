package com.example.myapplication.Utils;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.Services.MyFirebaseMessagingService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBirdPushHelper;
import com.sendbird.android.User;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.syncmanager.SendBirdSyncManager;
import com.sendbird.syncmanager.handler.CompletionHandler;

public class ConnectionManager {
    private static final String TAG = "ConnectionManager";
    public static boolean isLogin() {
        return PreferenceUtils.getConnected();
    }

    public static void connect(final Context context, String userId, String userNickname, String profile_pic, final SendBird.ConnectHandler handler) {
        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                Log.d("lifecycle","connect onCompleted"+e+context);
                if (e != null) {

                    SendBirdPushHelper.registerPushHandler(new MyFirebaseMessagingService());

                    ///Toast.makeText(context, context.getString(R.string.sendbird_error_with_code, e.getCode(), e.getMessage()), Toast.LENGTH_SHORT).show();
                    SendBird.updateCurrentUserInfo(userNickname, profile_pic, new SendBird.UserInfoUpdateHandler() {
                        public void onUpdated(SendBirdException e) {
                            Log.d("lifecycle", "1-"+e);
                            if (e == null) {
                            }
                        }
                    });
                    if (handler != null) {
                        handler.onConnected(user, e);
                    }
                    return;
                }

                SyncManagerUtils.setup(context, userId, new CompletionHandler() {
                    @Override
                    public void onCompleted(SendBirdException e) {
                        Log.d("lifecycle","connect setup onCompleted"+e+context);
                        SendBirdSyncManager.getInstance().resumeSync();
                    }
                });

            }
        });
    }
    public static void authenticate(Context context, String userId, String accessToken) {
        Log.d(TAG, "authenticate()");



        PushUtils.getPushToken(context, (pushToken, e) -> {
            if (e != null) {
                Log.d(TAG, "authenticate() => Failed (e: " + e.getMessage() + ")");
                return;
            }

            Log.d(TAG, "authenticate() => authenticate()");
            SendBirdCall.authenticate(new AuthenticateParams(userId).setAccessToken(accessToken), (user, e1) -> {
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

                    PrefUtils.setAppId(context, SendBirdCall.getApplicationId());
                    PrefUtils.setUserId(context, userId);
                    PrefUtils.setAccessToken(context, accessToken);
                    PrefUtils.setPushToken(context, pushToken);

                    Log.d(TAG, "authenticate() => authenticate() => OK");

                });
            });
        });
    }

    public static void disconnect(final SendBird.DisconnectHandler handler) {
        SendBird.disconnect(new SendBird.DisconnectHandler() {
            @Override
            public void onDisconnected() {
                SendBirdSyncManager.getInstance().pauseSync();

                if (handler != null) {
                    handler.onDisconnected();
                }
            }
        });
    }
    public interface ConnectionManagementHandler {
        /**
         * A callback for when connected or reconnected to refresh.
         *
         * @param reconnect Set false if connected, true if reconnected.
         */
        void onConnected(boolean reconnect);
    }
    public static void addConnectionManagementHandler(String handlerId, final ConnectionManagementHandler handler) {
        SendBird.addConnectionHandler(handlerId, new SendBird.ConnectionHandler() {
            @Override
            public void onReconnectStarted() {
            }

            @Override
            public void onReconnectSucceeded() {
                if (handler != null) {
                    handler.onConnected(true);
                }
            }

            @Override
            public void onReconnectFailed() {
            }
        });

        if (SendBird.getConnectionState() == SendBird.ConnectionState.OPEN) {
            if (handler != null) {
                handler.onConnected(false);
            }
        } else if (SendBird.getConnectionState() == SendBird.ConnectionState.CLOSED) { // push notification or system kill
            String userId = PreferenceUtils.getUserId();
            SendBird.connect(userId, new SendBird.ConnectHandler() {
                @Override
                public void onConnected(User user, SendBirdException e) {
                    SendBirdPushHelper.registerPushHandler(new MyFirebaseMessagingService());

                    if (e != null) {
                        return;
                    }

                    if (handler != null) {
                        handler.onConnected(false);
                    }
                }
            });
        }
    }
    public static void removeConnectionManagementHandler(String handlerId) {
        SendBird.removeConnectionHandler(handlerId);
    }
}

