package com.example.myapplication.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.call.VideoCallActivity;
import com.example.myapplication.call.VoiceCallActivity;
import com.sendbird.calls.DirectCall;

public class ActivityUtils {

    private static final String TAG = "ActivityUtils";

    public static final String EXTRA_INCOMING_CALL_ID = "incoming_call_id";
    public static final String EXTRA_CALLEE_ID =        "callee_id";
    public static final String EXTRA_IS_VIDEO_CALL =    "is_video_call";
    public static final String EXTRA_CALLEE_NAME =        "callee_name";

    public static final int START_SIGN_IN_MANUALLY_ACTIVITY_REQUEST_CODE = 1;
//
//    public static void startAuthenticateActivityAndFinish(@NonNull Activity activity) {
//        Log.d(TAG, "startAuthenticateActivityAndFinish()");
//        Intent intent = new Intent(activity, AuthenticateActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        activity.startActivity(intent);
//
//        activity.finish();
//    }
//
//    public static void startSignInManuallyActivityForResult(@NonNull Activity activity) {
//        Log.d(TAG, "startSignInManuallyActivityAndFinish()");
//        Intent intent = new Intent(activity, SignInManuallyActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        activity.startActivityForResult(intent, START_SIGN_IN_MANUALLY_ACTIVITY_REQUEST_CODE);
//    }
//
//    public static void startMainActivityAndFinish(@NonNull Activity activity) {
//        Log.d(TAG, "startMainActivityAndFinish()");
//        Intent intent = new Intent(activity, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        activity.startActivity(intent);
//        activity.finish();
//    }
//
//    public static void startApplicationInformationActivity(@NonNull Activity activity) {
//        Log.d(TAG, "startApplicationInformationActivity()");
//        Intent intent = new Intent(activity, ApplicationInformationActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        activity.startActivity(intent);
//    }

    public static void startCallActivityAsCaller(Context context, String calleeId,String calleeName, boolean isVideoCall) {
        Log.d(TAG, "startCallActivityAsCaller()"+calleeName);
        final Intent intent;
        if (isVideoCall) {
            intent = new Intent(context, VideoCallActivity.class);
        } else {
            intent = new Intent(context, VoiceCallActivity.class);
        }
        intent.putExtra(EXTRA_CALLEE_ID, calleeId);
        intent.putExtra(EXTRA_CALLEE_NAME, calleeName);
        intent.putExtra(EXTRA_IS_VIDEO_CALL, isVideoCall);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void startCallActivityAsCallee(Context context, DirectCall call) {
        Log.d(TAG, "startCallActivityAsCallee()"+call.getCallee().getNickname()+call.getCaller().getNickname());
        final Intent intent;
        if (call.isVideoCall()) {
            intent = new Intent(context, VideoCallActivity.class);
        } else {
            intent = new Intent(context, VoiceCallActivity.class);
        }
        intent.putExtra(EXTRA_INCOMING_CALL_ID, call.getCallId());
        intent.putExtra(EXTRA_CALLEE_NAME, call.getCaller().getNickname());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }
}