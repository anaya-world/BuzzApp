package com.example.myapplication.Adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Database.DatabaseHelperSchedule;
import com.example.myapplication.Fragments.ScheduleFragment;
import com.example.myapplication.ModelClasses.Search_result;
import com.example.myapplication.ModelClasses.Send_data;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Dialog_Schedule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.myapplication.Utils.Common.isConnected;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    Context context;
    ScheduleFragment frag;
    JSONArray search_array, send_Array;
    String imgurl;
    String names;
    JSONArray user_obj;
    JSONObject search_obj;
    Dialog_Schedule cs = null;
    Dialog_Schedule cs1 = null;
    ArrayList<Search_result> searchResults;
    private String sendtoids;


    public ScheduleAdapter(Context context, ArrayList<Search_result> search_results, ScheduleFragment scheduleFragment) {
        this.context = context;
        this.frag = scheduleFragment;
        this.searchResults = search_results;

    }

    @NonNull
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sechdule_items, parent, false);
        Log.d("array12", "" + searchResults);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, int position) {
        // for (int i=0;i<searchResults.size();i++) {
        String schedule_id = searchResults.get(position).getScheduleId();
        ArrayList<Send_data> sendData = searchResults.get(position).getSend_data();
        ArrayList arrayList = new ArrayList();
        ArrayList sendid = new ArrayList();
        if (!isConnected(context)) {
            names = searchResults.get(position).getSecrateId();
            imgurl = searchResults.get(position).getUserImg();

        } else {
            Log.d("array", schedule_id + " a " + sendData.size());
            for (int j = 0; j < sendData.size(); j++) {
                String parent_id = sendData.get(j).getPerentScheduleId();
                Log.d("array", "a" + parent_id);
                if (parent_id.equals(schedule_id)) {
                    arrayList.add(sendData.get(j).getName());
                    sendid.add(sendData.get(j).getId());
                    Log.d("array", "a" + sendData.get(j).getUserImg());
                    imgurl = sendData.get(j).getUserImg();
                    Log.d("array", "a" + imgurl);
                }
            }
            names = TextUtils.join(",", arrayList);
            sendtoids = TextUtils.join(",", sendid);
        }
       /* RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_img);
        requestOptions.error(R.drawable.buzzplaceholder);*/

       if(!imgurl.equals("No")) {
           Glide.with(context)
                   /*.setDefaultRequestOptions(requestOptions)*/
                   .load(imgurl.trim()).into(holder.profile);
       }

        holder.names.setText(names);
        holder.festival_type.setText(searchResults.get(position).getFestivalType());
        holder.date.setText(searchResults.get(position).getDate());
        holder.time.setText(searchResults.get(position).getTime());
        String gif = searchResults.get(position).getGif().trim();
        Log.d("gif", "12" + gif);
        if (gif.equals("null")) {
            Log.d("gif", "null or empty");
            holder.send_message.setVisibility(View.VISIBLE);
            holder.send_gif.setVisibility(View.GONE);
        } else {
            Log.d("gif", "not null");
            holder.send_message.setVisibility(View.GONE);
            holder.send_gif.setVisibility(View.VISIBLE);
        }
        Log.d("id", searchResults.get(position).getId());

        //  Log.d("userid",searchResults.get(position).getUserid());

        // Log.d("userid",searchResults.get(position).getUserid());

        Log.d("schid", searchResults.get(position).getScheduleId());
        Log.d("festival_type", searchResults.get(position).getFestivalType());
        Log.d("sendTo", searchResults.get(position).getSendTo());
        Log.d("date", searchResults.get(position).getDate());
        Log.d("message", searchResults.get(position).getMessage());
        Log.d("secretid", names);
        Log.d("time", searchResults.get(position).getTime());
        Log.d("gif", searchResults.get(position).getGif());
        Log.d("mobileno", searchResults.get(position).getMobileno());
        Log.d("user_img", imgurl);
        Log.d("has_msg_sent", "" + 0);//By Default.
        Random random = new Random(System.nanoTime());
        int randomInt = random.nextInt(1000000000);
        DatabaseHelperSchedule databaseHelperSchedule = new DatabaseHelperSchedule(context);

        databaseHelperSchedule.InsertData(randomInt,
                searchResults.get(position).getScheduleId(),
                searchResults.get(position).getFestivalType(),
                searchResults.get(position).getSendTo(),
                searchResults.get(position).getDate(),
                searchResults.get(position).getMessage(),
                sendtoids,
                searchResults.get(position).getGif(),
                searchResults.get(position).getTime(),
                names,
                searchResults.get(position).getMobileno(),
                imgurl,
                0 //Not sent yet
        );


        holder.rlDetailPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timestamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                String month1 = new DateFormatSymbols().getMonths()[month].substring(0, 3).toLowerCase();
                String datestamp = date + "-" + month1;

                Log.d("dialog", "" + timestamp + "-" + datestamp);
                List<Search_result> data = new ArrayList<>();

                data = databaseHelperSchedule.getData1();
                List<Search_result> data1 = databaseHelperSchedule.getData(datestamp, timestamp);
                List<Search_result> data2 = databaseHelperSchedule.getData("25-jan", "1:05");
                Log.d("dialog", "" + data + "-" + data1);
                Log.d("dialog1", "" + data2 + "-" + data1);


                try {
                    ScheduleAdapter.this.cs = new Dialog_Schedule(context, searchResults, position, frag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cs.show();
            }
        });
        holder.send_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Log.d("dialog", "" + position);

                    ScheduleAdapter.this.cs1 = new Dialog_Schedule(context, searchResults, position, frag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cs1.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile, chat;
        RelativeLayout rlDetailPopUp;
        ImageView send_message;
        TextView festival_type, names, time, date;
        TextView send_gif;

        public ViewHolder(View itemView) {
            super(itemView);
            rlDetailPopUp = itemView.findViewById(R.id.rl_detail_popup);
            send_message = itemView.findViewById(R.id.send_friend_message);
            date = itemView.findViewById(R.id.tv_date);
            profile = (CircleImageView) itemView.findViewById(R.id.cirimageviewprofile);
            festival_type = (TextView) itemView.findViewById(R.id.tv_birthdays);
            names = (TextView) itemView.findViewById(R.id.tv_names);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            send_gif = itemView.findViewById(R.id.send_gif);

        }
    }
}
