package com.example.myapplication.Utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdException;

public class AuthenticationUtils {

    private static final String TAG = "AuthenticationUtils";

    public interface AuthenticateHandler {
        void onResult(boolean isSuccess);
    }

    public static void authenticate(Context context, String userId, String accessToken, AuthenticateHandler handler) {
        Log.d(TAG, "authenticate()");

        if (userId == null) {
            Log.d(TAG, "authenticate() => Failed (userId == null)");
            if (handler != null) {
                handler.onResult(false);
            }
            return;
        }

        PushUtils.getPushToken(context, (pushToken, e) -> {
            if (e != null) {
                Log.d(TAG, "authenticate() => Failed (e: " + e.getMessage() + ")");
                if (handler != null) {
                    handler.onResult(false);
                }
                return;
            }

            Log.d(TAG, "authenticate() => authenticate()");
            SendBirdCall.authenticate(new AuthenticateParams(userId).setAccessToken(accessToken), (user, e1) -> {
                if (e1 != null) {
                    Log.d(TAG, "authenticate() => authenticate() => Failed (e1: " + e1.getMessage() + ")");
                    showToastErrorMessage(context, e1);

                    if (handler != null) {
                        handler.onResult(false);
                    }
                    return;
                }

                Log.d(TAG, "authenticate() => registerPushToken()");
                SendBirdCall.registerPushToken(pushToken, false, e2 -> {
                    if (e2 != null) {
                        Log.d(TAG, "authenticate() => registerPushToken() => Failed (e2: " + e2.getMessage() + ")");
                        showToastErrorMessage(context, e2);

                        if (handler != null) {
                            handler.onResult(false);
                        }
                        return;
                    }

                    PrefUtils.setAppId(context, SendBirdCall.getApplicationId());
                    PrefUtils.setUserId(context, userId);
                    PrefUtils.setAccessToken(context, accessToken);
                    PrefUtils.setPushToken(context, pushToken);

                    Log.d(TAG, "authenticate() => authenticate() => OK");
                    if (handler != null) {
                        handler.onResult(true);
                    }
                });
            });
        });
    }

    public interface DeauthenticateHandler {
        void onResult(boolean isSuccess);
    }

    public static void deauthenticate(Context context, DeauthenticateHandler handler) {
        Log.d(TAG, "deauthenticate()");

        String pushToken = PrefUtils.getPushToken(context);
        SendBirdCall.deauthenticate(pushToken, e -> {
            if (e != null) {
                Log.d(TAG, "deauthenticate() => Failed (e: " + e.getMessage() + ")");
                showToastErrorMessage(context, e);
            } else {
                Log.d(TAG, "deauthenticate() => OK");
            }

            PrefUtils.setUserId(context, null);
            PrefUtils.setAccessToken(context, null);
            PrefUtils.setCalleeId(context, null);
            PrefUtils.setPushToken(context, null);

            if (handler != null) {
                handler.onResult(e == null);
            }
        });
    }

    public interface AutoAuthenticateHandler {
        void onResult(String userId);
    }

    public static void autoAuthenticate(Context context, AutoAuthenticateHandler handler) {
        Log.d(TAG, "autoAuthenticate()");

        if (SendBirdCall.getCurrentUser() != null) {
            Log.d(TAG, "autoAuthenticate() => OK (SendBirdCall.getCurrentUser() != null)");
            if (handler != null) {
                handler.onResult(SendBirdCall.getCurrentUser().getUserId());
            }
            return;
        }

        String userId = PrefUtils.getUserId(context);
        String accessToken = PrefUtils.getAccessToken(context);
        String pushToken = PrefUtils.getPushToken(context);
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(pushToken)) {
            Log.d(TAG, "autoAuthenticate() => authenticate()");
            SendBirdCall.authenticate(new AuthenticateParams(userId).setAccessToken(accessToken), (user, e) -> {
                if (e != null) {
                    Log.d(TAG, "autoAuthenticate() => authenticate() => Failed (e: " + e.getMessage() + ")");
                    showToastErrorMessage(context, e);

                    if (handler != null) {
                        handler.onResult(null);
                    }
                    return;
                }

                Log.d(TAG, "autoAuthenticate() => registerPushToken()");
                SendBirdCall.registerPushToken(pushToken, false, e1 -> {
                    if (e1 != null) {
                        Log.d(TAG, "autoAuthenticate() => registerPushToken() => Failed (e1: " + e1.getMessage() + ")");
                        showToastErrorMessage(context, e1);

                        if (handler != null) {
                            handler.onResult(null);
                        }
                        return;
                    }

                    Log.d(TAG, "autoAuthenticate() => authenticate() => OK (Authenticated)");
                    if (handler != null) {
                        handler.onResult(userId);
                    }
                });
            });
        } else {
            Log.d(TAG, "autoAuthenticate() => Failed (No userId and pushToken)");
            if (handler != null) {
                handler.onResult(null);
            }
        }
    }

    private static void showToastErrorMessage(Context context, SendBirdException e) {
        if (context != null) {
            if (e.getCode() == 1800200) {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            } else if(e.getCode() == 800200) {
                Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
