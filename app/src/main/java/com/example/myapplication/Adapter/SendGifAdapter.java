package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Activities.SendMessageFromEventActivity;
import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ActivityUtils;
import com.kevalpatel2106.emoticongifkeyboard.EmoticonGIFKeyboardFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SendGifAdapter extends RecyclerView.Adapter<SendGifAdapter.GIfHolder> {
    List<FriendListModel> birthdaylist;
    FriendListInterface friendListInterface;
    Context mcontext;
    ImageView call_friend;
    ImageView iv_timezone;
    ImageView send_friend_message;
    TextView text_date,text_gif;
    TextView tv_date;
    FrameLayout groupframe,key;
    TextView tv_date_of_birth;
    TextView tv_name;
    ImageView user_img;
    RelativeLayout relativeLayout;
    ImageView send_message,gifIMg;
    private String gifURl;
    EmoticonGIFKeyboardFragment emoticonGIFKeyboardFragment=null;


    @Override
    public SendGifAdapter.GIfHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SendGifAdapter.GIfHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.send_gif_item, parent, false));

    }

    public SendGifAdapter(ArrayList<FriendListModel> birthdayModelArrayList, FragmentActivity activity, FriendListInterface friendListInterface) {
        this.birthdaylist = birthdayModelArrayList;
        this.mcontext = activity;
        this.friendListInterface = friendListInterface;


    }

    @Override
    public void onBindViewHolder(@NonNull SendGifAdapter.GIfHolder holder, int position) {

            final FriendListModel birthdayModel = (FriendListModel) this.birthdaylist.get(position);
            try {
                String caller_id = birthdayModel.getCaller_id();
                tv_name.setText(birthdayModel.getName());
                Log.d("image","1"+birthdaylist.get(position).getImage());
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.profile_img);
                requestOptions.error(R.drawable.buzzplaceholder);

                Glide.with(mcontext)
                        .setDefaultRequestOptions(requestOptions)
                        .load(birthdaylist.get(position).getImage()).into(user_img);

                Date dt1 = new SimpleDateFormat("yyyy-MM-dd").parse(birthdayModel.getDob());
                String finalDay = new SimpleDateFormat("dd").format(dt1);
                String finalDay1 = new SimpleDateFormat("MMM").format(dt1);
                tv_date.setText(finalDay);
                text_date.setText(finalDay1);

            } catch (Exception e) {
            }
            call_friend.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    ArrayList<Integer> oppnentlist1 = new ArrayList<>();
                    oppnentlist1.add(Integer.valueOf(birthdayModel.getUser_id()));
//                    QBConferenceType conferenceType = QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
//                        QBRTCClient qbrtcClient = QBRTCClient.getInstance(BirthdayAdapter.this.mcontext);
//                        if (QBChatService.getInstance().isLoggedIn()) {
//                            WebRtcSessionManager.getInstance(BirthdayAdapter.this.mcontext).setCurrentSession(qbrtcClient.createNewSessionWithOpponents(oppnentlist1, conferenceType));
                    //       CallActivity.start(BirthdayAdapter.this.mcontext, false);
                    ActivityUtils.startCallActivityAsCaller(mcontext, birthdayModel.getUser_id(),birthdayModel.getName(), false);

                    long date = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("kk:mm");
                    String dateString = sdf.format(Long.valueOf(date));
                    String time = sdf1.format(Long.valueOf(date)).toLowerCase();
                    DatabaseHelper databaseHelper = new DatabaseHelper(SendGifAdapter.this.mcontext);
                    Random random = new Random(System.nanoTime());
                    int randomInt = random.nextInt(1000000000);
                    String name = ((FriendListModel) SendGifAdapter.this.birthdaylist.get(position)).getName();
                    ArrayList arrayList = oppnentlist1;
                    String image = ((FriendListModel) SendGifAdapter.this.birthdaylist.get(position)).getImage();
                    //QBConferenceType qBConferenceType = conferenceType;
                    StringBuilder sb = new StringBuilder();
                    sb.append(dateString);
                    // QBRTCClient qBRTCClient = qbrtcClient;
                    sb.append(", ");
                    sb.append(time);
                    Random random2 = random;
                    databaseHelper.insertdata(randomInt, name, image, "outgoing", sb.toString(), ((FriendListModel) SendGifAdapter.this.birthdaylist.get(position)).getUser_id());
                    return;

                }
            });

        }




    @Override
    public int getItemCount() {
        return birthdaylist.size();
    }

    public class GIfHolder extends RecyclerView.ViewHolder{
        public GIfHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout=itemView.findViewById(R.id.tv_dates);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            text_date = (TextView) itemView.findViewById(R.id.text_date);
            text_gif = (TextView) itemView.findViewById(R.id.btn_unfriend);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            user_img = (ImageView) itemView.findViewById(R.id.profile_bimg);
            call_friend = (ImageView) itemView.findViewById(R.id.call_friend);
            iv_timezone = (ImageView) itemView.findViewById(R.id.iv_timezone);
            send_friend_message = (ImageView) itemView.findViewById(R.id.send_friend_message);

            text_gif.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    SendGifAdapter.this.friendListInterface.sendGif(v, SendGifAdapter.GIfHolder.this.getAdapterPosition());

//                   ; Intent intent=new Intent(mcontext.getApplicationContext(), GifActivity.class);
//
//                    mcontext.startActivity(intent);3fwaq/

                }
            });
            send_friend_message.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    SendGifAdapter.this.friendListInterface.messageFriend(view, SendGifAdapter.GIfHolder.this.getAdapterPosition());

                }
            });
            iv_timezone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //alert_Box(BirthdayHolder.this.getAdapterPosition());
                    Log.d("adapter","a"+getAdapterPosition());
                    Log.d("adapter","a"+tv_name.getText());
                    Log.d("adapter","a"+text_date.getText());
                    Log.d("adapter","a"+tv_date.getText());
//                    Log.d("adapter","a"+tv_date_of_birth.getText());
                    int pos= SendGifAdapter.GIfHolder.this.getAdapterPosition();

                    Intent intent=new Intent(SendGifAdapter.this.mcontext, SendMessageFromEventActivity.class);
                    intent.putExtra("userName",birthdaylist.get(pos).getName());
                    intent.putExtra("month",text_date.getText().toString());
                    intent.putExtra("date",tv_date.getText().toString());
                    intent.putExtra("send_to_callerid",birthdaylist.get(pos).getCaller_id());
                    intent.putExtra("img_url",birthdaylist.get(pos).getImage());
                    intent.putExtra("check","0");

                    SendGifAdapter.this.mcontext.startActivity(intent );


                }
            });
        }

    }
        }


