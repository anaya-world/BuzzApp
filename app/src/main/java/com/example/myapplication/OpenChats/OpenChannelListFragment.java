package com.example.myapplication.OpenChats;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Activities.Main3Activity;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.OpenChannelListQuery;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpenChannelListFragment extends Fragment implements SearchView.OnQueryTextListener {
    public static final int CHANNEL_LIST_LIMIT = 15;
    public static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_OPEN_CHANNEL_LIST";
    public static final String EXTRA_OPEN_CHANNEL_URL = "OPEN_CHANNEL_URL";
    public static final int INTENT_REQUEST_NEW_OPEN_CHANNEL = 402;
    /* access modifiers changed from: private */
    public List<OpenChannel> mChannelList;
    /* access modifiers changed from: private */
    public OpenChannelListAdapter mChannelListAdapter;
    private OpenChannelListQuery mChannelListQuery;
    private FloatingActionButton mCreateChannelFab;
    /* access modifiers changed from: private */
    public LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    /* access modifiers changed from: private */
    public SwipeRefreshLayout mSwipeRefresh;

    public static OpenChannelListFragment newInstance() {
        return new OpenChannelListFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_open_channel_list, container, false);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        this.mChannelList = new ArrayList();
        this.mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_open_channel_list);
        this.mChannelListAdapter = new OpenChannelListAdapter(getContext());
        this.mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout_open_channel_list);
        refresh();
        this.mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                OpenChannelListFragment.this.mSwipeRefresh.setRefreshing(true);
                OpenChannelListFragment.this.refresh();
            }
        });
        this.mCreateChannelFab = (FloatingActionButton) rootView.findViewById(R.id.fab_open_channel_list);
        this.mCreateChannelFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OpenChannelListFragment.this.startActivityForResult(new Intent(OpenChannelListFragment.this.getActivity(), CreateOpenChannelActivity.class), 402);
            }
        });
        setUpRecyclerView();
        setUpChannelListAdapter();
        return rootView;
    }
    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 402 && resultCode == -1) {
            refresh();
        }
    }
    public void setUpRecyclerView() {
        this.mLayoutManager = new LinearLayoutManager(getContext());
        this.mRecyclerView.setLayoutManager(this.mLayoutManager);
        this.mRecyclerView.setAdapter(this.mChannelListAdapter);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (OpenChannelListFragment.this.mLayoutManager.findLastVisibleItemPosition() == OpenChannelListFragment.this.mChannelListAdapter.getItemCount() - 1) {
                    OpenChannelListFragment.this.loadNextChannelList();
                }
            }
        });
    }
    private void setUpChannelListAdapter() {
        mChannelListAdapter.setOnItemClickListener(new OpenChannelListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OpenChannel channel) {
                String channelUrl = channel.getUrl();
                Intent intent = new Intent(OpenChannelListFragment.this.getActivity(), Main3Activity.class);
                intent.putExtra("URL", channelUrl);
                OpenChannelListFragment.this.startActivity(intent);
            }
        });
        mChannelListAdapter.setOnItemLongClickListener(new OpenChannelListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongPress(OpenChannel openChannel) {

            }
        });

    }

    /* access modifiers changed from: private */
    public void refresh() {
        refreshChannelList(15);
    }

    /* access modifiers changed from: 0000 */
    public void refreshChannelList(int numChannels) {
        this.mChannelListQuery = OpenChannel.createOpenChannelListQuery();
        this.mChannelListQuery.setLimit(numChannels);
        this.mChannelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
            public void onResult(List<OpenChannel> list, SendBirdException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                OpenChannelListFragment.this.mChannelList = list;
                OpenChannelListFragment.this.mChannelListAdapter.setOpenChannelList(list);
                if (OpenChannelListFragment.this.mSwipeRefresh.isRefreshing()) {
                    OpenChannelListFragment.this.mSwipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void loadNextChannelList() {
        if (this.mChannelListQuery != null) {
            this.mChannelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
                public void onResult(List<OpenChannel> list, SendBirdException e) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    for (OpenChannel channel : list) {
                        OpenChannelListFragment.this.mChannelListAdapter.addLast(channel);
                    }
                }
            });
        }
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
                OpenChannelListFragment.this.mChannelListAdapter.setSearchResult(OpenChannelListFragment.this.mChannelList);
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

    private List<OpenChannel> filter(List<OpenChannel> models, String query) {
        String query2 = query.toLowerCase();
        List<OpenChannel> filteredModelList = new ArrayList<>();
        for (OpenChannel model : models) {
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
}
