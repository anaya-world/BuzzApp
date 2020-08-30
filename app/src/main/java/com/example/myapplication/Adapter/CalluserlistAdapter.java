package com.example.myapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Activities.BlockedMembersListActivity;
import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ActivityUtils;
import com.example.myapplication.call.CallActivity;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBird.UserUnblockHandler;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

;

public class CalluserlistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL";
    private static List<String> mSelectedUserIds;
    public FriendListInterface friendListInterface;
    LinearLayout listitem_layout;
    private SelectableUserListAdapter.OnItemCheckedChangeListener mCheckedChangeListener;
    /* access modifiers changed from: private */
    public Context mContext;
    private boolean mIsBlockedList;
    private SelectableUserHolder mSelectableUserHolder;
    private boolean mShowCheckBox;
    public List<FriendListModel> mUsers;
    public List<FriendListModel> selectedmUsers = new ArrayList();
    //private SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();

    public interface OnItemCheckedChangeListener {
        void OnItemChecked(User user, boolean z);
    }

    private class SelectableUserHolder extends RecyclerView.ViewHolder {
        private ImageView blockedImage;
        /* access modifiers changed from: private */
        public ImageView iv_calls;
        /* access modifiers changed from: private */
        public LinearLayout listitem_layout;
        private boolean mIsBlockedList;
        private boolean mShowCheckBox;
        private TextView nameText;
        /* access modifiers changed from: private */
        public ImageView profileImage;

        public SelectableUserHolder(View itemView, boolean isBlockedList, boolean hideCheckBox) {
            super(itemView);
            setIsRecyclable(false);
            this.mIsBlockedList = isBlockedList;
            this.mShowCheckBox = hideCheckBox;
            this.nameText = (TextView) itemView.findViewById(R.id.text_selectable_user_list_nickname);
            this.profileImage = (ImageView) itemView.findViewById(R.id.image_selectable_user_list_profile);
            this.blockedImage = (ImageView) itemView.findViewById(R.id.image_user_list_blocked);
            this.iv_calls = (ImageView) itemView.findViewById(R.id.iv_calls);
            this.listitem_layout = (LinearLayout) itemView.findViewById(R.id.listitem_layout);
        }

        /* access modifiers changed from: private */
        public void bind(Context context, FriendListModel user, boolean isSelected, SelectableUserListAdapter.OnItemCheckedChangeListener listener) {
            this.nameText.setText(user.getName());


           RequestOptions requestOptions = new RequestOptions();
           requestOptions.placeholder(R.drawable.profile_img);
           requestOptions.error(R.drawable.buzzplaceholder);


            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(mUsers.get ( getAdapterPosition () ).getImage ()).into(profileImage);


            }  }


    public CalluserlistAdapter(List<FriendListModel> mUsers2, Context mcontext, FriendListInterface friendListInterface2) {
        this.mUsers = mUsers2;
        this.mContext = mcontext;
        this.friendListInterface = friendListInterface2;
    }

    public CalluserlistAdapter(Context context, boolean isBlockedList, boolean showCheckBox) {
        this.mContext = context;
        this.mUsers = new ArrayList();
        mSelectedUserIds = new ArrayList();
        this.mIsBlockedList = isBlockedList;
        this.mShowCheckBox = showCheckBox;
    }

    public void setItemCheckedChangeListener(SelectableUserListAdapter.OnItemCheckedChangeListener listener) {
        this.mCheckedChangeListener = listener;
    }

    public void setUserList(List<FriendListModel> users) {
        this.mUsers = users;
        notifyDataSetChanged();
    }

    public void unblock() {
        for (final String userId : mSelectedUserIds) {
            SendBird.unblockUserWithUserId(userId, new UserUnblockHandler() {
                public void onUnblocked(SendBirdException e) {
                    if (e == null) {
                        int index = 0;
                        while (true) {
                            if (index >= CalluserlistAdapter.this.mUsers.size()) {
                                break;
                            }
                            if (userId.equals(((FriendListModel) CalluserlistAdapter.this.mUsers.get(index)).getCaller_id())) {
                                CalluserlistAdapter.this.mUsers.remove(index);
                                break;
                            }
                            index++;
                        }
                        ((BlockedMembersListActivity) CalluserlistAdapter.this.mContext).blockedMemberCount(CalluserlistAdapter.this.mUsers.size());
                        CalluserlistAdapter.this.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mSelectableUserHolder = new SelectableUserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.call_user_itemlist, parent, false), this.mIsBlockedList, this.mShowCheckBox);
        return this.mSelectableUserHolder;
    }

    @SuppressLint({"ResourceAsColor"})
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            RequestOptions requestOptions = new RequestOptions();
           requestOptions.placeholder(R.drawable.profile_img);
            requestOptions.error(R.drawable.buzzplaceholder);


        ((SelectableUserHolder) holder).bind(this.mContext, (FriendListModel) this.mUsers.get(position), isSelected((FriendListModel) this.mUsers.get(position)), this.mCheckedChangeListener);
        String caller_id = ((FriendListModel) this.mUsers.get(position)).getCaller_id();
        if (this.selectedmUsers.contains(this.mUsers.get(position))) {
            (( SelectableUserHolder ) holder).listitem_layout.setBackgroundColor ( ContextCompat.getColor ( this.mContext , R.color.tab_col ) );
            (( SelectableUserHolder ) holder).profileImage.setImageResource ( R.drawable.circle_with_check );



                return;
        }
        ((SelectableUserHolder) holder).listitem_layout.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.white));
        ((SelectableUserHolder) holder).iv_calls.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                    List<String> arrayList = new ArrayList<>();
                    arrayList.add(((FriendListModel) CalluserlistAdapter.this.mUsers.get(position)).getCaller_id());
                    ((FriendListModel) CalluserlistAdapter.this.mUsers.get(position)).getImage();
                    ArrayList<Integer> oppnentlist1 = new ArrayList<>();
                    oppnentlist1.add(Integer.valueOf(((FriendListModel) CalluserlistAdapter.this.mUsers.get(position)).getCaller_id()));
//                    QBConferenceType conferenceType = QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
//                    QBRTCClient qbrtcClient = QBRTCClient.getInstance(CalluserlistAdapter.this.mContext);
//                    if (QBChatService.getInstance().isLoggedIn()) {
//                        WebRtcSessionManager.getInstance(CalluserlistAdapter.this.mContext).setCurrentSession(qbrtcClient.createNewSessionWithOpponents(oppnentlist1, conferenceType));
//                        CallActivity.start(CalluserlistAdapter.this.mContext, false);
                Log.d("lifecycle","CalluserlistAdapter"+mUsers.get(position).getUser_id());
                ActivityUtils.startCallActivityAsCaller(mContext, mUsers.get(position).getUser_id(),mUsers.get(position).getName(), false);
                        long date = System.currentTimeMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
                        SimpleDateFormat sdf1 = new SimpleDateFormat("kk:mm");
                        String dateString = sdf.format(Long.valueOf(date));
                        String time = sdf1.format(Long.valueOf(date)).toLowerCase();
                        DatabaseHelper databaseHelper = new DatabaseHelper(CalluserlistAdapter.this.mContext);
                        Random random = new Random(System.nanoTime());
                        int randomInt = random.nextInt(1000000000);
                        ArrayList arrayList2 = (ArrayList) arrayList;
                        String name = ((FriendListModel) CalluserlistAdapter.this.mUsers.get(position)).getName();
                        ArrayList arrayList3 = oppnentlist1;
                        String image = ((FriendListModel) CalluserlistAdapter.this.mUsers.get(position)).getImage();
                        StringBuilder sb = new StringBuilder();
                        sb.append(dateString);
                      //  QBConferenceType qBConferenceType = conferenceType;
                        sb.append(", ");
                        sb.append(time);
                        Random random2 = random;
                        String str = image;
                        databaseHelper.insertdata(randomInt, name, str, "outgoing", sb.toString(), ((FriendListModel) CalluserlistAdapter.this.mUsers.get(position)).getUser_id());

            }
        });
    }

    public int getItemCount() {
        return this.mUsers.size();
    }

    public void setfilter(List<FriendListModel> musers) {
        this.mUsers = new ArrayList();
        this.mUsers.add((FriendListModel) musers);
        notifyDataSetChanged();
    }

    public boolean isSelected(FriendListModel user) {
        return mSelectedUserIds.contains(user.getUser_id());
    }

    public void addLast(FriendListModel user) {
        this.mUsers.add(user);
        notifyDataSetChanged();
    }

    public void filterList(List<FriendListModel> users) {
        this.mUsers = users;
        notifyDataSetChanged();
    }

    public void setSearchResult(List<FriendListModel> result) {
        this.mUsers = result;
        notifyDataSetChanged();
    }
}
