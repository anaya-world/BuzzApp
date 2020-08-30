package com.example.myapplication.Intefaces;

import android.view.View;

public interface FriendListInterface {

    void messageFriend(View view, int position);

    void unfriedFriends(View view, int position);

    void sendGif(View v, int adapterPosition);
}
