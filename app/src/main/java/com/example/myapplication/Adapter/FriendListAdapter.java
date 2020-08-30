package com.example.myapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ActivityUtils;
import com.example.myapplication.call.CallActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    ArrayList<FriendListModel> friendListModelArrayList;
    FriendListInterface friendListInterface;
    private Context context;

    public FriendListAdapter(ArrayList<FriendListModel> friendListModelArrayList, Context context, FriendListInterface friendListInterface) {
        this.friendListModelArrayList = friendListModelArrayList;
        this.context = context;
        this.friendListInterface = friendListInterface;
    }

    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list, parent, false);
        return new FriendListAdapter.ViewHolder(v, friendListInterface);
    }

    @Override
    public void onBindViewHolder(final FriendListAdapter.ViewHolder holder, final int position) {
        final FriendListModel item = friendListModelArrayList.get(position);
        String user_name = item.getName();
        String sex = item.getGender();
        String secret_id = item.getSecret_id();
        String user_image = item.getImage();

        holder.searched_user_name.setText(user_name);
        holder.searched_user_gender.setText(sex);
        holder.searched_user_secretKey.setText(secret_id);
        //  holder.searched_user_mobile.setText(mobile_no);
        Glide.with(context).load(user_image).into(holder.searched_user_image);
        final String callerid =  item.getCaller_id();
        holder.call_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                    ArrayList<Integer> oppnentlist1 = new ArrayList<>();
                    List<String> items = Arrays.asList(callerid.split("\\s*,\\s*"));
                    for (String s : items) {
                        oppnentlist1.add(Integer.valueOf(s));
                    }
//                    QBRTCTypes.QBConferenceType conferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
//                    QBRTCClient qbrtcClient = QBRTCClient.getInstance(FriendListAdapter.this.context);
//                    Log.d("callingdetais","1 calligadapter");
//                    if (QBChatService.getInstance().isLoggedIn()) {
//                        Log.d("callingdetais","2 calligadapter");
//                        WebRtcSessionManager.getInstance(FriendListAdapter.this.context).setCurrentSession(qbrtcClient.createNewSessionWithOpponents(oppnentlist1, conferenceType));
   //                     CallActivity.start(FriendListAdapter.this.context, false);
                ActivityUtils.startCallActivityAsCaller(context, item.getUser_id(),item.getName(), false);

                long date = System.currentTimeMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
                        SimpleDateFormat sdf1 = new SimpleDateFormat("kk:mm");
                        String dateString = sdf.format(Long.valueOf(date));
                        String time = sdf1.format(Long.valueOf(date)).toLowerCase();
                        DatabaseHelper databaseHelper = new DatabaseHelper(FriendListAdapter.this.context);
                        Random random = new Random(System.nanoTime());
                        int randomInt = random.nextInt(1000000000);
                        ArrayList arrayList = oppnentlist1;
                      //  App.getInstance().setSomeVariable(String.valueOf(randomInt));
                        String name = item.getName();
                        List list = items;
                        String profile_image = item.getImage();
                        StringBuilder sb = new StringBuilder();
                        sb.append(dateString);
                      //  QBRTCTypes.QBConferenceType qBConferenceType = conferenceType;
                        sb.append(", ");
                        sb.append(time);
                        Random random2 = random;
                        String str = profile_image;
                        databaseHelper.insertdata(randomInt, name, str, "outgoing", sb.toString(), item.getUser_id());

//                    }
//                    List list2 = items;
//                    QBRTCTypes.QBConferenceType qBConferenceType2 = conferenceType;
//                    Log.d(QBRTCClient.TAG, "USER NOT LOGGED IN ");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendListModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView searched_user_image;
        public TextView searched_user_secretKey, searched_user_name, searched_user_gender;
        public ImageView send_friend_request, cancle_friend_request,call_btn;
        FriendListInterface friendListInterface;


        public ViewHolder(final View itemView, final FriendListInterface friendListInterface) {
            super(itemView);

            this.friendListInterface = friendListInterface;
            call_btn=(ImageView)itemView.findViewById(R.id.phone_call);
            searched_user_secretKey = (TextView) itemView.findViewById(R.id.searched_user_secretKey);
            searched_user_gender = (TextView) itemView.findViewById(R.id.searched_user_gender);

            searched_user_image = (CircleImageView) itemView.findViewById(R.id.searched_user_image);
            searched_user_name = (TextView) itemView.findViewById(R.id.searched_user_name);
            send_friend_request = (ImageView) itemView.findViewById(R.id.send_friend_request);
            cancle_friend_request = (ImageView) itemView.findViewById(R.id.cancle_friend_request);
            send_friend_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friendListInterface.messageFriend(view, getAdapterPosition());
                }
            });

//            cancle_friend_request.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    friendListInterface.unfriedFriends(view, getAdapterPosition());
//                }
//            });
            // searched_user_mobile = (TextView) itemView.findViewById(R.id.searched_user_mobile);

        }
    }
}
