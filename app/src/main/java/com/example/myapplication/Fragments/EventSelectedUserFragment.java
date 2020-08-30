package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapter.EventuserlistAdapter;
import com.example.myapplication.Adapter.SelectableUserListAdapter;
import com.example.myapplication.R;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserListQuery;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventSelectedUserFragment extends Fragment {
    private static String username,imgurl,check1;
    public LinearLayoutManager mLayoutManager;
    public EventuserlistAdapter mListAdapter;
    public UsersSelectedListener mListener;
    private RecyclerView mRecyclerView;
    public UserListQuery mUserListQuery;

    public static EventSelectedUserFragment newinstance(String userName, String img_url,String check) {
        username=userName;
        imgurl=img_url;
        check1=check;
        return new EventSelectedUserFragment();
    }

    public interface UsersSelectedListener {
        void onUserSelected(boolean z, String str);
    }

    public static EventSelectedUserFragment newInstance() {
        return new EventSelectedUserFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event_selected_user, container, false);
        this.mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_select_user);
        if(check1==null){
            this.mListAdapter = new EventuserlistAdapter(getActivity(), false, true);

        }
        else if(check1.equals("1")){
            this.mListAdapter=new EventuserlistAdapter(getActivity(), false, true,username);
        }
        this.mListAdapter.setItemCheckedChangeListener(new EventuserlistAdapter.OnItemCheckedChangeListener() {
            public void OnItemChecked(User user, boolean checked) {
                if (checked) {
                    EventSelectedUserFragment.this.mListener.onUserSelected(true, user.getUserId());
                } else {
                    EventSelectedUserFragment.this.mListener.onUserSelected(false, user.getUserId());
                }
            }
        });
        this.mListener = (UsersSelectedListener) getActivity();
        setUpRecyclerView();
        loadInitialUserList(15);
        return rootView;
    }

    private void setUpRecyclerView() {
        this.mLayoutManager = new LinearLayoutManager(getActivity());
        this.mRecyclerView.setLayoutManager(this.mLayoutManager);
        this.mRecyclerView.setAdapter(this.mListAdapter);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 1));
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (EventSelectedUserFragment.this.mLayoutManager.findLastVisibleItemPosition() == EventSelectedUserFragment.this.mListAdapter.getItemCount() - 1) {
                    EventSelectedUserFragment.this.loadNextUserList(10);
                }
            }
        });
    }

    private void loadInitialUserList(int size) {
        this.mUserListQuery = SendBird.createUserListQuery();
        this.mUserListQuery.setLimit(size);
        this.mUserListQuery.next(new UserListQuery.UserListQueryResultHandler() {
            public void onResult(List<User> list, SendBirdException e) {
                if (e == null) {
                    EventSelectedUserFragment.this.mListAdapter.setUserList(list);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void loadNextUserList(int size) {
        this.mUserListQuery.setLimit(size);
        this.mUserListQuery.next(new UserListQuery.UserListQueryResultHandler() {
            public void onResult(List<User> list, SendBirdException e) {
                if (e == null) {
                    for (User user : list) {

                        EventSelectedUserFragment.this.mListAdapter.addLast(user);
                    }
                }
            }
        });
    }
}
