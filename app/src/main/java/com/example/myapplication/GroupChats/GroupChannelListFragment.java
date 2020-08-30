package com.example.myapplication.GroupChats;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.Activities.Main2Activity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ConnectionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.syncmanager.ChannelCollection;
import com.sendbird.syncmanager.ChannelEventAction;
import com.sendbird.syncmanager.SendBirdSyncManager;
import com.sendbird.syncmanager.handler.ChannelCollectionHandler;
import com.sendbird.syncmanager.handler.CompletionHandler;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChannelListFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_LIST";
    public static final String EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL";
    private static final int CHANNEL_LIST_LIMIT = 15;
    public static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHANNEL_LIST_FRAGMENT";
    public static final String EXTRA_GROUP_CHANNEL_URL = "GROUP_CHANNEL_URL";
    public static final String EXTRA_GROUP_IS_NEW_CHANNEL = "EXTRA_NEW_CHANNEL";
    public static final String EXTRA_GROUP_CHANNEL_ADMIN_MESSAGE = "EXTRA_NEW_CHANNEL_MESSAGE";
    private static final int INTENT_REQUEST_NEW_GROUP_CHANNEL = 302;
    int count = 0;
    String strChannelMessage = "";
    ChannelCollection mChannelCollection;
     LinearLayout nolist,progress_lay;
    /* access modifiers changed from: private */
    public List<GroupChannel> mChannelList = new ArrayList();
    /* access modifiers changed from: private */
    public GroupChannelListAdapter mChannelListAdapter;
    private GroupChannelListQuery mChannelListQuery;
    private FloatingActionButton mCreateChannelFab;
    /* access modifiers changed from: private */
    public LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    /* access modifiers changed from: private */
    private SwipeRefreshLayout mSwipeRefresh;
   // SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();

    public static GroupChannelListFragment newInstance() {
        Log.d("GroupChannelList","GroupChannelListFragment new");
        return new GroupChannelListFragment();
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d("GroupChannelList","onCreate");
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("LIFECYCLE", "GroupChannelListFragment onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_group_channel_list, container, false);
        setRetainInstance(true);
        this.mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_group_channel_list);
        this.progress_lay=rootView.findViewById(R.id.progress_lay_chat);

        this.mCreateChannelFab = (FloatingActionButton) rootView.findViewById(R.id.fab_group_channel_list);
        this.mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout_group_channel_list);
        nolist=(LinearLayout)rootView.findViewById(R.id.nolist);




        this.mCreateChannelFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GroupChannelListFragment.this.startActivityForResult(new Intent(GroupChannelListFragment.this.getContext(),
                        CreateGroupChannelActivity.class), 302);
            }
        });
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("mSwipeRefresh", "onRefresh");
                mSwipeRefresh.setRefreshing(true);
                refresh();

            }



        });




        mChannelListAdapter = new GroupChannelListAdapter(getActivity());

        setUpRecyclerView();
        setUpChannelListAdapter();
     //   refresh();
        return rootView;
    }


    public void onResume() {
        super.onResume();
        ConnectionManager.addConnectionManagementHandler(CONNECTION_HANDLER_ID, new ConnectionManager.ConnectionManagementHandler() {
            @Override
            public void onConnected(boolean reconnect) {
                refresh();  //comment this shows duplicate values

            }
        });

        Log.d("LIFECYCLE", "GroupChannelListFragment onResume()");
        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                Log.d("LIFECYCLE", "GroupChannelListFragment onMessageReceived()");
                refresh();    //comment
            //    mChannelListAdapter.notifyDataSetChanged();//comment

            }



            public void onChannelChanged(BaseChannel channel) {
                Log.d("LIFECYCLE", "GroupChannelListFragment onChannelChanged()");
                mChannelListAdapter.clearMap();
                mChannelListAdapter.updateOrInsert(channel);
//                GroupChannelListFragment.this.mChannelListAdapter.clearMap();
//                GroupChannelListFragment.this.mChannelListAdapter.updateOrInsert(channel);

            }

            public void onTypingStatusUpdated(GroupChannel channel) {
                Log.d("LIFECYCLE", "GroupChannelListFragment onTypingStatusUpdated()");
                mChannelListAdapter.notifyDataSetChanged();
            }
        });
        refresh();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("LIFECYCLE", "GroupChannelListFragment onResume()");
    }

    public void onPause() {
        Log.d("LIFECYCLE", "GroupChannelListFragment onPause()");
         SendBird.removeConnectionHandler(CONNECTION_HANDLER_ID);
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
        super.onPause();
    }

    public void onDestroy() {
        if(mChannelCollection!=null){
            mChannelCollection.setCollectionHandler(null);
            mChannelCollection.remove();
        }
        super.onDestroy();


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_REQUEST_NEW_GROUP_CHANNEL) {
            if (resultCode == RESULT_OK) {
                String newChannelUrl = data.getStringExtra("EXTRA_NEW_CHANNEL_URL");
                boolean isNewChannel = data.getBooleanExtra("EXTRA_NEW_CHANNEL", false);
                if(isNewChannel) {
                    strChannelMessage = data.getStringExtra("EXTRA_NEW_CHANNEL_MESSAGE");
                }
                if (newChannelUrl != null) {
                    //enterGroupChannel(newChannelUrl);
                    enterGroupChannel(newChannelUrl, isNewChannel, strChannelMessage);
                }
            } else {
                Log.d("GrChLIST", "resultCode not STATUS_OK");
            }
        }

    }
    private void setUpRecyclerView() {

        this.mLayoutManager = new LinearLayoutManager(getContext());
        this.mRecyclerView.setLayoutManager(this.mLayoutManager);
        this.mRecyclerView.setAdapter(this.mChannelListAdapter);
        progress_lay.setVisibility(View.GONE);

        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (GroupChannelListFragment.this.mLayoutManager.findLastVisibleItemPosition() == GroupChannelListFragment.this.mChannelListAdapter.getItemCount() - 1) {
//                    // GroupChannelListFragment.this.loadNextChannelList();
//                }
                Log.d("mSwipeRefresh", "mRecyclerView"+mChannelListAdapter.getItemCount()+mLayoutManager.findLastVisibleItemPosition());
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mLayoutManager.findLastVisibleItemPosition() == mChannelListAdapter.getItemCount() - 1) {
                        if (mChannelCollection != null) {
                            mChannelCollection.fetch(new CompletionHandler() {
                                @Override
                                public void onCompleted(SendBirdException e) {
//                                    getActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
                                            if (mSwipeRefresh.isRefreshing()) {
                                                Log.d("mSwipeRefresh", "mRecyclerView11" + mSwipeRefresh.isRefreshing());
                                                mSwipeRefresh.setRefreshing(false);
                                            }

//                                        }
//                                    });
                                }

                            });
                        }
                    }
                }

            }
        });

    }

    private void setUpChannelListAdapter() {
        this.mChannelListAdapter.setOnItemClickListener(new GroupChannelListAdapter.OnItemClickListener() {

            public void onItemClick(GroupChannel channel) {
                enterGroupChannel(channel);
            }

        });
        this.mChannelListAdapter.setOnItemLongClickListener(new GroupChannelListAdapter.OnItemLongClickListener() {
            public void onItemLongClick(GroupChannel channel) {

                showChannelOptionsDialog(channel);
            }

        });

    }

    /* access modifiers changed from: private */
    public void showChannelOptionsDialog(final GroupChannel channel) {
        final boolean pushCurrentlyEnabled = channel.isPushEnabled();
        String[] options = pushCurrentlyEnabled ? new String[]{

                "Delete channel", "Turn push notifications OFF"} : new String[]{

                "Delete channel", "Turn push notifications ON"


        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle((CharSequence) "Channel options").setItems((CharSequence[]) options, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupChannelListFragment.this.getActivity());
                    StringBuilder sb = new StringBuilder();
                    sb.append("Delete channel ");
                    sb.append(channel.getName());
                    sb.append("?");
                    builder.setTitle((CharSequence) sb.toString()).setPositiveButton((CharSequence) "Delete", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                           leaveChannel(channel);
                        }
                    }).setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) null).create().show();
                } else if (which == 1) {
                    GroupChannelListFragment.this.setChannelPushPreferences(channel, true ^ pushCurrentlyEnabled);
                }
            }
        });




        builder.create().show();
    }

    /* access modifiers changed from: private */
    public void setChannelPushPreferences(GroupChannel channel, final boolean on) {
        channel.setPushPreference(on, new GroupChannel.GroupChannelSetPushPreferenceHandler() {
            public void onResult(SendBirdException e) {
                if (e != null) {
                    e.printStackTrace();
                    FragmentActivity activity = GroupChannelListFragment.this.getActivity();
                    StringBuilder sb = new StringBuilder();
                    sb.append("Error: ");
                    sb.append(e.getMessage());
                    Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }


                Toast.makeText(GroupChannelListFragment.this.getActivity(), on ? "Push notifications have been turned ON" : "Push notifications have been turned OFF", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void enterGroupChannel(GroupChannel channel) {
        enterGroupChannel(channel.getUrl());
    }

    /* access modifiers changed from: 0000 */
    public void enterGroupChannel(String channelUrl) {
        Log.d("channelUrl",""+channelUrl);
        Intent intent = new Intent(getActivity(), Main2Activity.class);
        intent.putExtra("URL", channelUrl);
        startActivity(intent);
    }

    /* access modifiers changed from: 0000 */
    public void enterGroupChannel(String channelUrl,  boolean isNew, String message) {
        Log.d("channelUrl",""+channelUrl);
        Intent intent = new Intent(getActivity(), Main2Activity.class);
        intent.putExtra("URL", channelUrl);
        intent.putExtra("EXTRA_NEW_CHANNEL", isNew);
        intent.putExtra("EXTRA_NEW_CHANNEL_MESSAGE", message);
        startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void refresh() {
        // refreshChannelList(15);
        if (mChannelCollection != null) {
            Log.d("lifecycle","refresh 1");
            mChannelCollection.remove();
        }
        Log.d("lifecycle","2");
        mChannelListAdapter.clearMap();
        mChannelListAdapter.clearChannelList();
        GroupChannelListQuery query = GroupChannel.createMyGroupChannelListQuery();
        query.setLimit(CHANNEL_LIST_LIMIT);
        Log.d("lifecycle", "qe"+query);
        mChannelCollection = new ChannelCollection(query);
        mChannelCollection.setCollectionHandler(mChannelCollectionHandler);

        mChannelCollection.fetch(new CompletionHandler() {
            @Override
            public void onCompleted(SendBirdException e) {
                Log.d("lifecycle", "onCompleted"+e);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mSwipeRefresh.isRefreshing()) {

                            mSwipeRefresh.setRefreshing(false);
                        }
                    }
                });
            }
        });





    }

    public void leaveChannel(GroupChannel channel) {
        channel.leave(new GroupChannel.GroupChannelLeaveHandler() {
            public void onResult(SendBirdException e) {
                if (e != null) {
return;

                }
                refresh();
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        ((SearchView) MenuItemCompat.getActionView(item)).setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            public boolean onMenuItemActionCollapse(MenuItem item) {
                GroupChannelListFragment.this.mChannelListAdapter.setSearchResult(GroupChannelListFragment.this.mChannelList);
                return true;
            }
        });
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        this.mChannelListAdapter.setSearchResult(filter(this.mChannelList, newText));
        return true;
    }

    private List<GroupChannel> filter(List<GroupChannel> models, String query) {
        String query2 = query.toLowerCase();
        List<GroupChannel> filteredModelList = new ArrayList<>();
        for (GroupChannel model : models) {
            if (model.getName().toLowerCase().contains(query2)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clearall) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    ChannelCollectionHandler mChannelCollectionHandler = new ChannelCollectionHandler() {


        @Override
        public void onChannelEvent(final ChannelCollection channelCollection, final List<GroupChannel> list, final ChannelEventAction channelEventAction) {
            Log.d("SyncManager", "onChannelEvent: size = " + list.size() + ", action = " + channelEventAction);
            if (getActivity() == null) {
                return;
            }


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSwipeRefresh.isRefreshing()) {
                        mSwipeRefresh.setRefreshing(false);
                    }


                    switch (channelEventAction) {
                        case INSERT:
                            mChannelListAdapter.clearMap();
                            mChannelListAdapter.insertChannels(list, channelCollection.getQuery().getOrder());
                            break;

                        case UPDATE:
                            mChannelListAdapter.clearMap();
                            mChannelListAdapter.updateChannels(list);
                            break;

                        case MOVE:
                            mChannelListAdapter.clearMap();
                            mChannelListAdapter.moveChannels(list, channelCollection.getQuery().getOrder());
                            break;

                        case REMOVE:
                            mChannelListAdapter.clearMap();
                            mChannelListAdapter.removeChannels(list);
                            break;

                        case CLEAR:
                            mChannelListAdapter.clearMap();
                            mChannelListAdapter.clearChannelList();
                            break;
                    }
//                    if(mChannelList.size()<=0)
//                    {
//                        nolist.setVisibility(View.VISIBLE);
//                        mRecyclerView.setVisibility(View.GONE);
//                    }else
//                    {
//                        nolist.setVisibility(View.GONE);
//                        mRecyclerView.setVisibility(View.VISIBLE);
//                    }

                }

            });

        }
    };


}
