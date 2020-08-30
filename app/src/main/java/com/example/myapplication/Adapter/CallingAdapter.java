package com.example.myapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.ModelClasses.Callhistory_Model;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ActivityUtils;
import com.example.myapplication.Utils.SharedPrefManager;
import com.sendbird.android.GroupChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import de.hdodenhof.circleimageview.CircleImageView;

public class CallingAdapter extends RecyclerView.Adapter<CallingAdapter.CallHolder> {
    private static List<String> mSelectedUserIds;
    Context callcontext;
    public List<Callhistory_Model> calllist = new ArrayList();
    private GroupChannel mChannel;
    public List<Callhistory_Model> selectedcalls = new ArrayList();
    private String TAG="CallingAdapter";
    // private SharedPrefsHelper sharedPrefsHelper;

    public class CallHolder extends RecyclerView.ViewHolder {
        ImageView call_btn;
        TextView call_duration;
        TextView call_time;
        CircleImageView caller_img;
        TextView caller_name;
        CardView card_view_searched_items;
        ImageView log_call;
        RelativeLayout relative_headerlayout;

        public CallHolder(View itemView) {
            super(itemView);
            this.caller_img = (CircleImageView) itemView.findViewById(R.id.caller_img);
            this.log_call = (ImageView) itemView.findViewById(R.id.log_call);
            this.call_btn = (ImageView) itemView.findViewById(R.id.call_btn);
            this.caller_name = (TextView) itemView.findViewById(R.id.caller_name);
            this.call_time = (TextView) itemView.findViewById(R.id.call_time);
            this.call_duration = (TextView) itemView.findViewById(R.id.call_duration);
            this.relative_headerlayout = (RelativeLayout) itemView.findViewById(R.id.parentlayout);
        }
    }

    public CallingAdapter(Context callcontext2, List<Callhistory_Model> calllist2) {
        this.callcontext = callcontext2;
        this.calllist = calllist2;
    }

    public CallingAdapter(Context callcontext2, List<Callhistory_Model> calllist2, List<Callhistory_Model> selectedcalls2) {
        this.callcontext = callcontext2;
        this.calllist = calllist2;
        this.selectedcalls = selectedcalls2;
    }

    @NonNull
    public CallHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CallHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_item, parent, false));
    }

    public void onBindViewHolder(@NonNull CallHolder holder, final int position) {

        holder.caller_name.setText(((Callhistory_Model) this.calllist.get(position)).getName());
        holder.call_time.setText(((Callhistory_Model) this.calllist.get(position)).getDatetime());
        if (((Callhistory_Model) this.calllist.get(position)).getDuration() != null) {
            int millis = Integer.valueOf(((Callhistory_Model) this.calllist.get(position)).getDuration()).intValue();
            holder.call_duration.setText(String.format("%02d:%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toHours((long) millis)), Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long) millis))), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) millis)))}));
        }
        Glide.with(this.callcontext).load(((Callhistory_Model) this.calllist.get(position)).getProfile_image()).apply(new RequestOptions().placeholder(R.drawable.buzzplaceholder).error(R.drawable.buzzplaceholder)).into((ImageView) holder.caller_img);

        if ( ((Callhistory_Model) this.calllist.get(position)).getIncoutg_image().equals("incoming")) {
            //(Callhistory_Model) this.calllist.get(position)).getIncoutg_image()!=null &&
            holder.log_call.setImageResource(R.drawable.icoming_icon);
        } else {
            holder.log_call.setImageResource(R.drawable.outgoing_icon);
        }
        final String callerid = ((Callhistory_Model) this.calllist.get(position)).getId();
        Log.d(TAG, callerid+"--"+calllist.get(position).getCallerid());
        holder.call_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ActivityUtils.startCallActivityAsCaller(callcontext, calllist.get(position).getCallerid(),calllist.get(position).getName(), false);
                long date = System.currentTimeMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
                        SimpleDateFormat sdf1 = new SimpleDateFormat("kk:mm");
                        String dateString = sdf.format(Long.valueOf(date));
                        String time = sdf1.format(Long.valueOf(date)).toLowerCase();
                        DatabaseHelper databaseHelper = new DatabaseHelper(CallingAdapter.this.callcontext);
                        Random random = new Random(System.nanoTime());
                        int randomInt = random.nextInt(1000000000);
                        String name = ((Callhistory_Model) CallingAdapter.this.calllist.get(position)).getName();
                        String profile_image = ((Callhistory_Model) CallingAdapter.this.calllist.get(position)).getProfile_image();
                        StringBuilder sb = new StringBuilder();
                        sb.append(dateString);
                      //  QBConferenceType qBConferenceType = conferenceType;
                        sb.append(", ");
                        sb.append(time);
                        Random random2 = random;
                        String str = profile_image;
                        databaseHelper.insertdata(randomInt, name, str, "outgoing", sb.toString(), ((Callhistory_Model) CallingAdapter.this.calllist.get(position)).getCallerid());
//
            }
        });
        if (this.selectedcalls.contains(this.calllist.get(position))) {
            holder.relative_headerlayout.setBackgroundColor(ContextCompat.getColor(this.callcontext, R.color.tab_col));
            holder.caller_img.setImageResource(R.drawable.circle_with_check);
            return;
        }
        holder.relative_headerlayout.setBackgroundColor(ContextCompat.getColor(this.callcontext, R.color.white));
    }

    public int getItemCount() {
        return this.calllist.size();
    }

    public void filterList(ArrayList<Callhistory_Model> filteredList) {
        this.calllist = filteredList;
        notifyDataSetChanged();
    }

    public void setSearchResult(List<Callhistory_Model> result) {
        this.calllist = result;
        notifyDataSetChanged();
    }
}
