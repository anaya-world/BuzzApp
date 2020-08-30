package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.myapplication.Activities.SendGifActivity;
import com.example.myapplication.Activities.SendMessageFromEventActivity;
import com.example.myapplication.Activities.SpecialDaysGifActivity;
import com.example.myapplication.ModelClasses.SpecialDaysModel;
import com.example.myapplication.R;

import java.util.List;

public class SpecialDaysAdapter extends Adapter<SpecialDaysAdapter.SpecialDaysHolder> {
    Context mcontext;
    List<SpecialDaysModel> specialDaysModelList;

    public class SpecialDaysHolder extends ViewHolder {
        TextView btn_unfriend;
        ImageView iv_timezone;
        ImageView send_message;
        TextView text_month;
        TextView tv_date;
        TextView tv_festival_name;
        TextView tv_festivaltype;

        public SpecialDaysHolder(View itemView) {
            super(itemView);
            this.text_month = (TextView) itemView.findViewById(R.id.text_date);
            this.tv_date = (TextView) itemView.findViewById(R.id.tv_date_of_birth);
            this.tv_festival_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_festivaltype = (TextView) itemView.findViewById(R.id.tv_text);
            this.btn_unfriend = (TextView) itemView.findViewById(R.id.btn_unfriend);
            this.send_message = (ImageView) itemView.findViewById(R.id.send_message);
            this.iv_timezone = (ImageView) itemView.findViewById(R.id.iv_timezone);
            btn_unfriend.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*EventData eventData = new EventData();
                    eventData.userName = specialDaysModelList.get(getAdapterPosition()).getFestival_name();
                    eventData.month = text_month.getText().toString();
                    eventData.date = tv_date.getText().toString();
                    eventData.check = "2";
                    eventData.listPosition = getAdapterPosition();
                    listener.onEvent(eventData);*/

                    int pos = SpecialDaysAdapter.SpecialDaysHolder.this.getAdapterPosition();
                    Intent intent = new Intent(SpecialDaysAdapter.this.mcontext, SpecialDaysGifActivity.class);
                    intent.putExtra("userName", specialDaysModelList.get(pos).getFestival_name());
                    intent.putExtra("month", text_month.getText().toString());
                    intent.putExtra("date", tv_date.getText().toString());
                    intent.putExtra("check", "2");
                    //intent.putExtra("list_position", getAdapterPosition());
                    SpecialDaysAdapter.this.mcontext.startActivity(intent);
                }
            });
            this.send_message.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = SpecialDaysAdapter.SpecialDaysHolder.this.getAdapterPosition();

                    Intent intent = new Intent(SpecialDaysAdapter.this.mcontext, SendMessageFromEventActivity.class);
                    intent.putExtra("userName", specialDaysModelList.get(pos).getFestival_name());
                    intent.putExtra("month", text_month.getText().toString());
                    intent.putExtra("date", tv_date.getText().toString());
                    intent.putExtra("check", "2");

                    SpecialDaysAdapter.this.mcontext.startActivity(intent);
                }
            });
        }
    }

    public SpecialDaysAdapter(Context mcontext2, List<SpecialDaysModel> specialDaysModelList2) {
        this.mcontext = mcontext2;
        this.specialDaysModelList = specialDaysModelList2;
    }

    @NonNull
    public SpecialDaysHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SpecialDaysHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.specialdaysitem, parent, false));
    }

    public void onBindViewHolder(@NonNull SpecialDaysHolder holder, int position) {
        holder.text_month.setText(((SpecialDaysModel) this.specialDaysModelList.get(position)).getMonth());
        holder.tv_date.setText(((SpecialDaysModel) this.specialDaysModelList.get(position)).getDate());
        holder.tv_festival_name.setText(((SpecialDaysModel) this.specialDaysModelList.get(position)).getFestival_name());
        holder.tv_festivaltype.setText(((SpecialDaysModel) this.specialDaysModelList.get(position)).getFestival_type());

    }

    public int getItemCount() {
        return this.specialDaysModelList.size();
    }

    public class EventData {
        String userName;
        String month;
        String date;
        String check;
        int listPosition;
    }

}
