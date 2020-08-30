package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.Adapter.SelectableUserListAdapter;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.sendbird.android.UserListQuery;

import java.util.ArrayList;
import java.util.List;

public class BlockedMembersListActivity extends BaseActivity {
    private static final int STATE_EDIT = 1;
    private static final int STATE_NORMAL = 0;
    ArrayList<FriendListModel> friendListModelArrayList;
    private Button mButtonEdit;
    private Button mButtonUnblock;
    private int mCurrentState;
    private LinearLayoutManager mLayoutManager;
    /* access modifiers changed from: private */
    public SelectableUserListAdapter mListAdapter;
    private RecyclerView mRecyclerView;
    private List<String> mSelectedIds;
    private Toolbar mToolbar;
    private UserListQuery mUserListQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_members_list);
        this.mSelectedIds = new ArrayList();
        this.mButtonEdit = (Button) findViewById(R.id.button_edit);
        this.mButtonEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BlockedMembersListActivity.this.setState(1);
            }
        });
        this.mButtonEdit.setEnabled(false);
        this.mButtonUnblock = (Button) findViewById(R.id.button_unblock);
        this.mButtonUnblock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BlockedMembersListActivity.this.mListAdapter.unblock();
                BlockedMembersListActivity.this.setState(0);
            }
        });
        this.mButtonUnblock.setEnabled(false);
        this.mToolbar = (Toolbar) findViewById(R.id.toolbar_blocked_members_list);
        setSupportActionBar(this.mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator((int) R.drawable.ic_arrow_left_white_24_dp);
        }
        this.mRecyclerView = (RecyclerView) findViewById(R.id.recycler_select_user);
        this.mListAdapter = new SelectableUserListAdapter(this, true, false);
        this.mListAdapter.setItemCheckedChangeListener(new SelectableUserListAdapter.OnItemCheckedChangeListener() {
            public void OnItemChecked(FriendListModel user, boolean checked) {
                if (checked) {
                    BlockedMembersListActivity.this.userSelected(true, user.getUser_id());
                } else {
                    BlockedMembersListActivity.this.userSelected(false, user.getUser_id());
                }
            }
        });
        this.mLayoutManager = new LinearLayoutManager(this);
        this.mRecyclerView.setLayoutManager(this.mLayoutManager);
        this.mRecyclerView.setAdapter(this.mListAdapter);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void setState(int state) {
        if (state == 1) {
            this.mCurrentState = 1;
            this.mButtonUnblock.setVisibility(View.VISIBLE);
            this.mButtonEdit.setVisibility(View.GONE);
            this.mListAdapter.setShowCheckBox(true);
        } else if (state == 0) {
            this.mCurrentState = 0;
            this.mButtonUnblock.setVisibility(View.GONE);
            this.mButtonEdit.setVisibility(View.VISIBLE);
            this.mListAdapter.setShowCheckBox(false);
        }
    }

    public void userSelected(boolean selected, String userId) {
        if (selected) {
            this.mSelectedIds.add(userId);
        } else {
            this.mSelectedIds.remove(userId);
        }
        if (this.mSelectedIds.size() > 0) {
            this.mButtonUnblock.setEnabled(true);
        } else {
            this.mButtonUnblock.setEnabled(false);
        }
    }

    public void onBackPressed() {
        if (this.mCurrentState == 1) {
            setState(0);
        } else {
            super.onBackPressed();
        }
    }

    public void blockedMemberCount(int size) {
        this.mButtonEdit.setEnabled(size > 0);
    }
}
