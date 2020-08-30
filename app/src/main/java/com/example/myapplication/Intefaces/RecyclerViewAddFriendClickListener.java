package com.example.myapplication.Intefaces;

import android.view.View;

public interface RecyclerViewAddFriendClickListener {
    void onAddFriend(View view, int position);

    void onCancleFriendRequest(View view, int position);
}
