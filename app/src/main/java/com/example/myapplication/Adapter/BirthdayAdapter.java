package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Activities.Birthday_Gif_Activity;
import com.example.myapplication.Activities.SendMessageFromEventActivity;
import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.ModelClasses.SpecialDaysModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ActivityUtils;
import com.kevalpatel2106.emoticongifkeyboard.EmoticonGIFKeyboardFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class BirthdayAdapter extends RecyclerView.Adapter<BirthdayAdapter.BirthdayHolder> {
    List<FriendListModel> birthdaylist;
    FriendListInterface friendListInterface;

    Context mcontext;

    public BirthdayAdapter(List<SpecialDaysModel> specialDaysModelList, FragmentActivity activity, FriendListInterface friendListInterface2) {
    }


    public class BirthdayHolder extends RecyclerView.ViewHolder {
        ImageView call_friend;
        ImageView iv_timezone;
        ImageView send_friend_message;
        TextView text_date, text_gif;
        TextView tv_date;
        FrameLayout groupframe, key;
        TextView tv_date_of_birth;
        TextView tv_name;
        ImageView user_img;
        ImageView send_message, gifIMg;
        private String gifURl;
        EmoticonGIFKeyboardFragment emoticonGIFKeyboardFragment = null;

        public BirthdayHolder(View itemView) {
            super(itemView);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.text_date = (TextView) itemView.findViewById(R.id.text_date);
            this.text_gif = (TextView) itemView.findViewById(R.id.btn_unfriend);
            this.tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            this.user_img = (ImageView) itemView.findViewById(R.id.profile_bimg);
            this.call_friend = (ImageView) itemView.findViewById(R.id.call_friend);
            this.iv_timezone = (ImageView) itemView.findViewById(R.id.iv_timezone);
            this.send_friend_message = (ImageView) itemView.findViewById(R.id.send_friend_message);

            this.text_gif.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
//                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
//                    Birthday_gif_Fragment birthday_gif_fragment = new Birthday_gif_Fragment();
//                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, birthday_gif_fragment).addToBackStack(null).commit();


                    FriendListModel birthdayModel = birthdaylist.get(getAdapterPosition());

                    Intent intent = new Intent(BirthdayAdapter.this.mcontext, Birthday_Gif_Activity.class);
                    intent.putExtra("EXTRA_CALLER_ID", birthdayModel.getCaller_id());//CallID
                    intent.putExtra("EXTRA_NAME", birthdayModel.getName());//Name
                    mcontext.startActivity(intent);
                    //      BirthdayAdapter.this.friendListInterface.sendGif(v, BirthdayHolder.this.getAdapterPosition());

//                   ; Intent intent=new Intent(mcontext.getApplicationContext(), GifActivity.class);
//
//                    mcontext.startActivity(intent);3fwaq/

                }
            });
            this.send_friend_message.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    BirthdayAdapter.this.friendListInterface.messageFriend(view, BirthdayHolder.this.getAdapterPosition());

                }
            });
            this.iv_timezone.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //alert_Box(BirthdayHolder.this.getAdapterPosition());
                    Log.d("adapter", "a" + getAdapterPosition());
                    Log.d("adapter", "a" + tv_name.getText());
                    Log.d("adapter", "a" + text_date.getText());
                    Log.d("adapter", "a" + tv_date.getText());
//                    Log.d("adapter","a"+tv_date_of_birth.getText());
                    int pos = BirthdayHolder.this.getAdapterPosition();

                    Intent intent = new Intent(BirthdayAdapter.this.mcontext, SendMessageFromEventActivity.class);
                    intent.putExtra("userName", birthdaylist.get(pos).getName());
                    intent.putExtra("month", text_date.getText().toString());
                    intent.putExtra("date", tv_date.getText().toString());
                    intent.putExtra("send_to_callerid", birthdaylist.get(pos).getCaller_id());
                    intent.putExtra("img_url", birthdaylist.get(pos).getImage());
                    intent.putExtra("check", "0");

                    BirthdayAdapter.this.mcontext.startActivity(intent);


                }
            });
        }
    }

    public BirthdayAdapter(ArrayList<FriendListModel> birthdaylist2, Context mcontext2, FriendListInterface friendListInterface2) {
        this.birthdaylist = birthdaylist2;
        this.mcontext = mcontext2;
        this.friendListInterface = friendListInterface2;
    }

    @NonNull
    public BirthdayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BirthdayHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.birthdayitem, parent, false));
    }

    public void onBindViewHolder(@NonNull BirthdayHolder holder, final int position) {
        holder.text_gif.setVisibility(View.VISIBLE);
        final FriendListModel birthdayModel = (FriendListModel) this.birthdaylist.get(position);
        try {
            String caller_id = birthdayModel.getCaller_id();
            holder.tv_name.setText(birthdayModel.getName());
            Log.d("image", "1" + birthdaylist.get(position).getImage());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_img);
            requestOptions.error(R.drawable.buzzplaceholder);

            Glide.with(mcontext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(birthdaylist.get(position).getImage()).into(holder.user_img);

            Date dt1 = new SimpleDateFormat("dd-MM-yyyy").parse(birthdayModel.getDob());
            String finalDay = new SimpleDateFormat("dd").format(dt1);
            String finalDay1 = new SimpleDateFormat("MMM").format(dt1);
            holder.tv_date.setText(finalDay);
            holder.text_date.setText(finalDay1);
        } catch (Exception e) {
        }
        holder.call_friend.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                ArrayList<Integer> oppnentlist1 = new ArrayList<>();
                oppnentlist1.add(Integer.valueOf(birthdayModel.getUser_id()));
//                    QBConferenceType conferenceType = QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
//                        QBRTCClient qbrtcClient = QBRTCClient.getInstance(BirthdayAdapter.this.mcontext);
//                        if (QBChatService.getInstance().isLoggedIn()) {
//                            WebRtcSessionManager.getInstance(BirthdayAdapter.this.mcontext).setCurrentSession(qbrtcClient.createNewSessionWithOpponents(oppnentlist1, conferenceType));
                //       CallActivity.start(BirthdayAdapter.this.mcontext, false);
                ActivityUtils.startCallActivityAsCaller(mcontext, birthdayModel.getUser_id(), birthdayModel.getName(), false);

                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
                SimpleDateFormat sdf1 = new SimpleDateFormat("kk:mm");
                String dateString = sdf.format(Long.valueOf(date));
                String time = sdf1.format(Long.valueOf(date)).toLowerCase();
                DatabaseHelper databaseHelper = new DatabaseHelper(BirthdayAdapter.this.mcontext);
                Random random = new Random(System.nanoTime());
                int randomInt = random.nextInt(1000000000);
                String name = ((FriendListModel) BirthdayAdapter.this.birthdaylist.get(position)).getName();
                ArrayList arrayList = oppnentlist1;
                String image = ((FriendListModel) BirthdayAdapter.this.birthdaylist.get(position)).getImage();
                //QBConferenceType qBConferenceType = conferenceType;
                StringBuilder sb = new StringBuilder();
                sb.append(dateString);
                // QBRTCClient qBRTCClient = qbrtcClient;
                sb.append(", ");
                sb.append(time);
                Random random2 = random;
                databaseHelper.insertdata(randomInt, name, image, "outgoing", sb.toString(), ((FriendListModel) BirthdayAdapter.this.birthdaylist.get(position)).getUser_id());
                return;

            }
        });
    }

    public int getItemCount() {
        return this.birthdaylist.size();
    }
}
