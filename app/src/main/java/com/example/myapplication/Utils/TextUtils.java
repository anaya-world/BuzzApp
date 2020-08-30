package com.example.myapplication.Utils;

import android.content.Context;
import android.util.Log;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class TextUtils {
    public static String getGroupChannelTitle(GroupChannel channel, Context context) {
        List<Member> members = channel.getMembers();
        Log.d("shared","a"+"--"+members.size());
        if (members.size() < 2 ) {
            return "No Members";
        }
        if (members.size() == 2) {
            StringBuffer names = new StringBuffer();

            for (Member member : members) {

                if (!member.getUserId().equals(SharedPrefManager.getInstance(context).getUser().getUser_id())) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(", ");
                    sb.append(member.getNickname());
                    names.append(sb.toString());

                }
            }
            return names.delete(0, 2).toString();
        }
        int count = 0;
        StringBuffer names2 = new StringBuffer();
        for (User member2 : members) {
            if (!member2.getUserId().equals(SharedPrefManager.getInstance(context).getUser().getUser_id())) {
                count++;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(", ");
                sb2.append(member2.getNickname());
                names2.append(sb2.toString());
                if (count >= 10) {
                    break;
                }
            }
        }
        return names2.delete(0, 2).toString();
    }

    public static String generateMD5(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(data.getBytes());
        byte[] messageDigest = digest.digest();
        StringBuffer hexString = new StringBuffer();
        for (byte b : messageDigest) {
            hexString.append(Integer.toHexString(b & 255));
        }
        return hexString.toString();
    }
}
