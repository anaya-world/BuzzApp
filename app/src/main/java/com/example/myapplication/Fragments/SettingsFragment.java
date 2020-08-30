package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Utils.AuthenticationUtils;
import com.example.myapplication.Utils.UserInfoUtils;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.User;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        User currentUser = SendBirdCall.getCurrentUser();
        if (currentUser != null) {
            UserInfoUtils.setProfileImage(getActivity(), currentUser, view.findViewById(R.id.image_view_profile));
            ((TextView)view.findViewById(R.id.text_view_user_id)).setText(currentUser.getUserId());
            ((TextView)view.findViewById(R.id.text_view_nickname)).setText(currentUser.getNickname());
        }

        view.findViewById(R.id.linear_layout_application_information).setOnClickListener(view1 -> {
            if (getActivity() != null) {
               // ActivityUtils.startApplicationInformationActivity(getActivity());
            }
        });

        view.findViewById(R.id.linear_layout_sign_out).setOnClickListener(view1 -> {
            AuthenticationUtils.deauthenticate(getActivity(), isSuccess -> {
                if (getActivity() != null) {
                  //  ActivityUtils.startAuthenticateActivityAndFinish(getActivity());
                }
            });
        });
    }
}
