package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.myapplication.GroupChats.CreateGroupChannelActivity;
import com.example.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
  create an instance of this fragment.
 */
public class SelectDistinctFragment extends Fragment {

    private CheckBox mCheckBox;
    /* access modifiers changed from: private */
    public DistinctSelectedListener mListener;

    public interface DistinctSelectedListener {
        void onDistinctSelected(boolean z);
    }

    public static SelectDistinctFragment newInstance() {
        return new SelectDistinctFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_select_distinct, container, false);
        ((CreateGroupChannelActivity) getActivity()).setState(1);
        this.mListener = (CreateGroupChannelActivity) getActivity();
        this.mCheckBox = (CheckBox) rootView.findViewById(R.id.checkbox_select_distinct);
        this.mCheckBox.setChecked(true);
        this.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SelectDistinctFragment.this.mListener.onDistinctSelected(isChecked);
            }
        });
        return rootView;
    }
}
