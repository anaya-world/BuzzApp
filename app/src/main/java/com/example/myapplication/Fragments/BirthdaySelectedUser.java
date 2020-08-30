package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BirthdaySelectedUser extends Fragment {
    View v;
    TextView nameText;
    CircleImageView profileImage;
    static String usersend,imgurl;
    public BirthdaySelectedUser() {
        // Required empty public constructor
    }

    public static BirthdaySelectedUser newinstance(String username,String url){
        usersend=username;
        imgurl=url;

        return new BirthdaySelectedUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_birthday_selected_user, container, false);
        init();
        RequestOptions requestOptions = new RequestOptions();

        requestOptions.placeholder(R.drawable.profile_img);
        requestOptions.error(R.drawable.buzzplaceholder);

        Glide.with(getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(imgurl.trim()).into(profileImage);
        nameText.setText(usersend);
        return v;
    }

    private void init() {

        this.nameText = (TextView) v.findViewById(R.id.text_selectable_user_list_nickname);
        this.profileImage =  v.findViewById(R.id.cirimageviewprofile);

    }
}
