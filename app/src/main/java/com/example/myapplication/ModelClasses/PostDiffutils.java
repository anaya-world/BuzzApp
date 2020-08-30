package com.example.myapplication.ModelClasses;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class PostDiffutils extends DiffUtil.Callback {

    ArrayList<PostListModel> oldlist;
    ArrayList<PostListModel> newlist;
    @Override
    public int getOldListSize() {
        return oldlist.size ();
    }

    @Override
    public int getNewListSize() {
        return newlist.size ();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition , int newItemPosition) {
        return oldlist.size ()==newlist.size ();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition , int newItemPosition) {
        return oldlist.get ( oldItemPosition )==newlist.get ( newItemPosition );
    }
}
