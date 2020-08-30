package com.example.myapplication.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.SpannableString;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.Intefaces.NoInternetListener;

import java.io.File;

public class Common {
    private static ProgressDialog mProgress;
    public static NoInternetListener mInternetListener;
    public static final String DIALOG_EXTRA = "Dialog";
    static AlertDialog alertDialog = null;

//    public static String createChatDialogName(List<Integer> qbUsers) {
//        List<QBUser> qbUsers1 = QBUsersHolder.getInstance().getUsersByIds(qbUsers);
//        StringBuilder name = new StringBuilder();
//        for (QBUser user : qbUsers1) {
//            name.append(user.getFullName());
//            name.append(" ");
//        }
//        if (name.length() > 30) {
//            name = name.replace(30, name.length() - 1, "...");
//        }
//        return name.toString();
//    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        /*if (netinfo == null || !netinfo.isConnectedOrConnecting()) {
            showNoInternetToast(context);
            return false;
        }
        NetworkInfo wifi = cm.getNetworkInfo(1);
        NetworkInfo mobile = cm.getNetworkInfo(0);
        if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) {
            return true;
        }
        showNoInternetToast(context);
        return false;*/
        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
        } else
            return false;
    }

    public static boolean isConnected(Context context, boolean isShowToast) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo == null || !netinfo.isConnectedOrConnecting()) {
            if (isShowToast) {
                showNoInternetToast(context);
            }
            return false;
        }
        NetworkInfo wifi = cm.getNetworkInfo(1);
        NetworkInfo mobile = cm.getNetworkInfo(0);
        if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) {
            return true;
        }
        if (isShowToast) {
            showNoInternetToast(context);
        }
        return false;
    }

    public static void showNoInternetToast(Context context) {
        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static boolean checkImageIsDownloadedOrNot(String filename) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory());
            sb.append("/BuzzApp/Images/");
            sb.append(filename);
            if (new File(sb.toString()).exists()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkVideoIsDownloadedOrNot(String filename) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory());
            sb.append("/BuzzApp/Videos/");
            sb.append(filename);
            if (new File(sb.toString()).exists()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkSentVideoIsDownloadedOrNot(String filename) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory());
            sb.append("/BuzzApp/Videos/Sent/");
            sb.append(filename);
            if (new File(sb.toString()).exists()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static void showAlertOkay(Context mContext, String strTitle,
                                     String strMessage, String btnOkayText) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(strMessage);
        alertDialogBuilder.setNegativeButton(btnOkayText, (dialog, which) -> alertDialog.dismiss());
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void showAlertOkayCallBack(Context mContext, String strTitle,
                                             String strMessage, String btnOkayText, NoInternetListener mListener) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(strTitle);
        alertDialogBuilder.setMessage(strMessage);
        mInternetListener = mListener;
        alertDialogBuilder.setNegativeButton(btnOkayText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mInternetListener!=null){
                    mInternetListener.onHandle();
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void progressOn(Context mContext) {
        if (mProgress == null || !mProgress.isShowing()) {
            mProgress = new ProgressDialog(mContext);
            mProgress.setMessage("Loading, please wait...");
            mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //progressBar.setVisibility(View.VISIBLE);
            mProgress.setIndeterminate(true);
            mProgress.setCancelable(false);
            mProgress.show();
        }
    }

    public static void progressOff() {
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
    }
}
