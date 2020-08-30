package com.example.myapplication.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.example.myapplication.Activities.CallUserActivity;
import com.example.myapplication.Adapter.CallingAdapter;
import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.Intefaces.TabNotify;
import com.example.myapplication.ModelClasses.Callhistory_Model;
import com.example.myapplication.R;
import com.example.myapplication.Activities.UserNavgation;
import com.example.myapplication.Utils.RecyclerItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallFragment extends Fragment implements SearchView.OnQueryTextListener {
    private TabNotify tabNotify;
    private static final String TAG = "Call Fragment";
    private RecyclerView call_history_recycler;
    private FloatingActionButton call_userlist;
    CallingAdapter callingAdapter;
    /* access modifiers changed from: private */
    public List<Callhistory_Model> calllist = new ArrayList();
    Menu context_menu;
    DatabaseHelper database;
    private LinearLayout nolist;
    EditText et_search;
    AlertDialog alertDialog;
    boolean isMultiSelect = false;
    ImageView iv_search;
    ActionMode mActionMode;

    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_multi_select_call, menu);
            menu.findItem(R.id.action_call).setIcon(R.drawable.delete_white);
            CallFragment.this.context_menu = menu;
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() != R.id.action_call) {
                return false;
            }
            DatabaseHelper database = new DatabaseHelper(CallFragment.this.getActivity());
            for (int i = 0; i < CallFragment.this.multiselect_list.size(); i++) {
                database.deleteEntry(((Callhistory_Model) CallFragment.this.multiselect_list.get(i)).getId());
                CallFragment.this.calllist.remove(CallFragment.this.multiselect_list.get(i));
                CallFragment.this.callingAdapter.notifyDataSetChanged();
                database.close();
            }
            if (CallFragment.this.mActionMode != null) {
                CallFragment.this.mActionMode.finish();
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            CallFragment.this.mActionMode = null;
            CallFragment.this.isMultiSelect = false;
            CallFragment.this.multiselect_list = new ArrayList();
            CallFragment.this.refreshAdapter();
            ((UserNavgation) CallFragment.this.getActivity()).getSupportActionBar().show();
        }
    };
    List<Callhistory_Model> multiselect_list = new ArrayList();


    public CallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_call, container, false);
        tabNotify=(TabNotify)getActivity();
        tabNotify.notifyTab(1);
        this.call_history_recycler = (RecyclerView) rootView.findViewById(R.id.call_history_recycler);
        this.call_userlist = (FloatingActionButton) rootView.findViewById(R.id.call_userlist);
        this.et_search = (EditText) rootView.findViewById(R.id.et_search);
        this.iv_search = (ImageView) rootView.findViewById(R.id.iv_search);
        nolist=(LinearLayout)rootView.findViewById(R.id.nolist);
        this.call_userlist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CallFragment.this.startActivity(new Intent(CallFragment.this.getActivity(), CallUserActivity.class));
                CallFragment.this.mActionMode = null;
            }
        });
        return rootView;
    }

    public void onResume() {
        super.onResume();
        this.calllist = new ArrayList();
        this.database = new DatabaseHelper(getContext());
        this.calllist = this.database.getdata();
       // database.cleartable();
        Log.d("CallFragment",""+calllist.size());
        if(calllist.size()<=0){
            nolist.setVisibility(View.VISIBLE);
            call_history_recycler.setVisibility(View.GONE);
        }else {
            nolist.setVisibility(View.GONE);
            call_history_recycler.setVisibility(View.VISIBLE);
        }
        this.callingAdapter = new CallingAdapter(getContext(), this.calllist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        this.call_history_recycler.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        this.call_history_recycler.setLayoutManager(layoutManager);
        this.callingAdapter.notifyDataSetChanged();
        this.call_history_recycler.setAdapter(this.callingAdapter);
        this.call_history_recycler.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this.call_history_recycler, new RecyclerItemClickListener.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (CallFragment.this.isMultiSelect) {
                    CallFragment.this.multi_select(position);
                }
            }

            public void onItemLongClick(View view, int position) {
                String[] options = new String[]{
                        "Clear Histroy"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle((CharSequence) "Call History options").setItems((CharSequence[]) options, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            StringBuilder sb = new StringBuilder();
                            sb.append("Clear All ");
                            sb.append("?");
                            builder.setTitle((CharSequence) sb.toString()).setPositiveButton((CharSequence) "Clear", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            callingAdapter = new CallingAdapter(getContext(), calllist);
                                            new DatabaseHelper(getContext()).cleartable();
                                            if (calllist != null) {
                                                calllist.clear();
                                            }
                                            callingAdapter.notifyDataSetChanged();
                                            alertDialog.dismiss();
                                        }

                            }).setNegativeButton((CharSequence) "Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                }
                            }).create().show();

                    }
                });
               alertDialog= builder.create();
               alertDialog.show();
            }
        }));
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        menu.findItem(R.id.action_clearall).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem menuItem) {
                new DatabaseHelper(CallFragment.this.getContext()).cleartable();
                if (CallFragment.this.calllist != null) {
                    CallFragment.this.calllist.clear();
                }
                CallFragment.this.callingAdapter.notifyDataSetChanged();
                return true;
            }
        });
        searchView.setOnQueryTextListener(this);
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener()  {
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            public boolean onMenuItemActionCollapse(MenuItem item) {
                CallFragment.this.callingAdapter.setSearchResult(CallFragment.this.calllist);
                return true;
            }
        });
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        this.callingAdapter.setSearchResult(filter(this.calllist, newText));
        return true;
    }

    private List<Callhistory_Model> filter(List<Callhistory_Model> models, String query) {
        String query2 = query.toLowerCase();
        List<Callhistory_Model> filteredModelList = new ArrayList<>();
        for (Callhistory_Model model : models) {
            if (model.getName().toLowerCase().contains(query2)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.action_clearall) {
            return super.onOptionsItemSelected(item);
        }
        this.callingAdapter = new CallingAdapter(getContext(), this.calllist);
        new DatabaseHelper(getContext()).cleartable();
        if (this.calllist != null) {
            this.calllist.clear();
        }
        this.callingAdapter.notifyDataSetChanged();
        return true;
    }

    public void multi_select(int position) {
        if (position >= 0 && this.mActionMode != null) {
            if (this.multiselect_list.contains(this.calllist.get(position))) {
                this.multiselect_list.remove(this.calllist.get(position));
            } else {
                this.multiselect_list.add(this.calllist.get(position));
            }
            if (this.multiselect_list.size() > 0) {
                ActionMode actionMode = this.mActionMode;
                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(this.multiselect_list.size());
                actionMode.setTitle(sb.toString());
            } else {
                this.mActionMode.setTitle("");
            }
            refreshAdapter();
        }
    }

    public void refreshAdapter() {
        this.callingAdapter.selectedcalls = this.multiselect_list;
        this.callingAdapter.calllist = this.calllist;
        this.callingAdapter.notifyDataSetChanged();
    }
}
