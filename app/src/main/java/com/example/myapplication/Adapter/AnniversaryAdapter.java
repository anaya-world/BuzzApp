package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Activities.Anniversary_Gif_Activity;
import com.example.myapplication.Activities.SendMessageFromEventActivity;
import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ActivityUtils;
import com.example.myapplication.call.CallActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnniversaryAdapter extends RecyclerView.Adapter<AnniversaryAdapter.AnniverSaryHolder> {
    List<FriendListModel> anniversaryModelList;
    FriendListInterface friendListInterface;
    Context mcontext;

    public class AnniverSaryHolder extends RecyclerView.ViewHolder {
        TextView btn_unfriend;
        ImageView call_friend;
        CircleImageView iv_image;
        ImageView iv_timezone;
        ImageView send_friend_message;
        TextView text_date;
        TextView tv_anniversary;
        TextView tv_name;

        public AnniverSaryHolder(View itemView) {
            super(itemView);
            this.text_date = (TextView) itemView.findViewById(R.id.text_date);
            this.tv_anniversary = (TextView) itemView.findViewById(R.id.tv_anniversary);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.iv_image = (CircleImageView) itemView.findViewById(R.id.iv_image);
            this.call_friend = (ImageView) itemView.findViewById(R.id.call_friend);
            this.send_friend_message = (ImageView) itemView.findViewById(R.id.send_friend_message);
            this.iv_timezone = (ImageView) itemView.findViewById(R.id.iv_timezone);
            this.btn_unfriend = (TextView) itemView.findViewById(R.id.btn_unfriend);
            this.btn_unfriend.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendListModel birthdayModel = anniversaryModelList.get(getAdapterPosition());

                    Intent intent=new Intent(AnniversaryAdapter.this.mcontext, Anniversary_Gif_Activity.class);
                    intent.putExtra("EXTRA_CALLER_ID", birthdayModel.getCaller_id());//CallID
                    intent.putExtra("EXTRA_NAME", birthdayModel.getName());//Name
                    mcontext.startActivity(intent);

                   // AnniversaryAdapter.this.friendListInterface.sendGif(v, AnniverSaryHolder.this.getAdapterPosition());


                }
            });

            this.send_friend_message.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnniversaryAdapter.this.friendListInterface.messageFriend(v, AnniverSaryHolder.this.getAdapterPosition());
                }
            } );
            iv_timezone.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos= AnniversaryAdapter.AnniverSaryHolder.this.getAdapterPosition();

                    Intent intent=new Intent(AnniversaryAdapter.this.mcontext, SendMessageFromEventActivity.class);
                    intent.putExtra("userName",anniversaryModelList.get(pos).getName());
                    intent.putExtra("month",text_date.getText().toString());
                    intent.putExtra("date",tv_anniversary.getText().toString());
                    intent.putExtra("send_to_callerid",anniversaryModelList.get(pos).getCaller_id());
                    intent.putExtra("img_url",anniversaryModelList.get(pos).getImage());
                    intent.putExtra("check","1");

                    AnniversaryAdapter.this.mcontext.startActivity(intent );                }
            });
        }

    }

    public AnniversaryAdapter(ArrayList<FriendListModel> anniversaryModelList2, Context mcontext2, FriendListInterface friendListInterface2) {
        this.anniversaryModelList = anniversaryModelList2;
        this.mcontext = mcontext2;
        this.friendListInterface = friendListInterface2;

    }

    @NonNull
    public AnniverSaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AnniverSaryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.anniversary_items, parent, false));
    }

    public void onBindViewHolder(@NonNull AnniverSaryHolder holder, final int position) {
        final FriendListModel anniversaryModel = (FriendListModel) this.anniversaryModelList.get(position);
        try {
            String caller_id = anniversaryModel.getCaller_id();
            holder.tv_name.setText(anniversaryModel.getName());
            Date dt1 = new SimpleDateFormat("dd-MM-yyyy").parse(anniversaryModel.getAnniversary());
            String finalDay = new SimpleDateFormat("dd").format(dt1);
            String finalDay1 = new SimpleDateFormat("MMM").format(dt1);
            holder.tv_anniversary.setText(finalDay);
            holder.text_date.setText(finalDay1);
            Glide.with(mcontext).load(anniversaryModel.getImage()).apply(new RequestOptions().placeholder(R.drawable.ic_logo_pink).error(R.drawable.buzzplaceholder)).into(holder.iv_image);
        } catch (Exception e) {
        }
        holder.call_friend.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                    ArrayList<Integer> oppnentlist1 = new ArrayList<>();
                    oppnentlist1.add(Integer.valueOf(anniversaryModel.getUser_id()));
//                    QBConferenceType conferenceType = QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
//                    QBRTCClient qbrtcClient = QBRTCClient.getInstance(AnniversaryAdapter.this.mcontext);
//                    if (QBChatService.getInstance().isLoggedIn()) {
//                        WebRtcSessionManager.getInstance(AnniversaryAdapter.this.mcontext).setCurrentSession(qbrtcClient.createNewSessionWithOpponents(oppnentlist1, conferenceType));
                      //  CallActivity.start(AnniversaryAdapter.this.mcontext, false);
                ActivityUtils.startCallActivityAsCaller(mcontext, anniversaryModel.getUser_id(),anniversaryModel.getName(), false);

                long date = System.currentTimeMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
                        SimpleDateFormat sdf1 = new SimpleDateFormat("kk:mm");
                        String dateString = sdf.format(Long.valueOf(date));
                        String time = sdf1.format(Long.valueOf(date)).toLowerCase();
                        DatabaseHelper databaseHelper = new DatabaseHelper(AnniversaryAdapter.this.mcontext);
                        Random random = new Random(System.nanoTime());
                        int randomInt = random.nextInt(1000000000);
                        String name = ((FriendListModel) AnniversaryAdapter.this.anniversaryModelList.get(position)).getName();
                        ArrayList arrayList = oppnentlist1;
                        String image = ((FriendListModel) AnniversaryAdapter.this.anniversaryModelList.get(position)).getImage();
                      //  QBConferenceType qBConferenceType = conferenceType;
                        StringBuilder sb = new StringBuilder();
                        sb.append(dateString);
                     //   QBRTCClient qBRTCClient = qbrtcClient;
                        sb.append(", ");
                        sb.append(time);
                        Random random2 = random;
                        databaseHelper.insertdata(randomInt, name, image, "outgoing", sb.toString(), ((FriendListModel) AnniversaryAdapter.this.anniversaryModelList.get(position)).getUser_id());
                        return;

            }
        });
        Glide.with(this.mcontext).load(anniversaryModel.getImage()).into((ImageView) holder.iv_image);
    }

    public int getItemCount() {
        return this.anniversaryModelList.size();
    }
}
