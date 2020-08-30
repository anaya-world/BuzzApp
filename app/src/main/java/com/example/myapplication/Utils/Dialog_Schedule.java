package com.example.myapplication.Utils;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Activities.Birthday_Gif_Activity;
import com.example.myapplication.Database.DatabaseHelperSchedule;
import com.example.myapplication.Fragments.ScheduleFragment;
import com.example.myapplication.Intefaces.NotifyINf;
import com.example.myapplication.ModelClasses.Search_result;
import com.example.myapplication.ModelClasses.Send_data;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.myapplication.Utils.Common.isConnected;

public class Dialog_Schedule extends Dialog implements View.OnClickListener {
    private final ScheduleFragment frag;
    private NotifyINf notifyINf;
    Context context;

    CircleImageView imgprof;
    TextView festival_type, usersname;
    EditText message, date, time;
    String imgurl, schedule_id;
    RelativeLayout relGif;
    ImageView gifimg;
    DatabaseHelperSchedule databaseHelperSchedule;
    int position;
    String names;
    Button deleteSCH, updateSch;
    ArrayList<Search_result> searchResult = new ArrayList<>();


    public Dialog_Schedule(Context context, ArrayList<Search_result> searchResults, int position, ScheduleFragment frag) {
        super(context);
        this.context = context;
        this.position = position;
        this.frag = frag;
        this.searchResult = searchResults;
        databaseHelperSchedule = new DatabaseHelperSchedule(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_schedule);
        notifyINf = (NotifyINf) frag;
        init();

        schedule_id = searchResult.get(position).getScheduleId();
        ArrayList arrayList = new ArrayList();
        if (!isConnected(context)) {
            names = searchResult.get(position).getSecrateId();
            imgurl = searchResult.get(position).getUserImg();
        } else {
            ArrayList<Send_data> sendData = new ArrayList<>();
            sendData = searchResult.get(position).getSend_data();
            for (int i = 0; i < sendData.size(); i++) {
                String parent_id = sendData.get(i).getPerentScheduleId();
                if (parent_id.equals(schedule_id)) {
                    arrayList.add(sendData.get(i).getName());
                    imgurl = sendData.get(i).getUserImg();
                }
            }
            names = TextUtils.join(",", arrayList);
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_img);
        requestOptions.error(R.drawable.buzzplaceholder);
        Glide.with(getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(imgurl.trim()).into(imgprof);

        usersname.setText(names);
        message.setText(searchResult.get(position).getMessage());
        festival_type.setText(searchResult.get(position).getFestivalType());
        date.setText(searchResult.get(position).getDate());
        time.setText(searchResult.get(position).getTime());
        String gifurl = searchResult.get(position).getGif();
        Log.d("gifurl", "12" + gifurl);
        if (gifurl.equals("null")) {
            relGif.setVisibility(View.GONE);
        } else {
            relGif.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(gifurl).into(gifimg);
        }

        deleteSCH.setOnClickListener(this);
        updateSch.setOnClickListener(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void init() {
        imgprof = findViewById(R.id.cirimageviewprofile);
        message = findViewById(R.id.tv_message);
        date = findViewById(R.id.tv_date);
        time = findViewById(R.id.tv_time);
        usersname = findViewById(R.id.tv_names);
        festival_type = findViewById(R.id.tv_birthdays);
        deleteSCH = findViewById(R.id.sch_deletebtn);
        updateSch = findViewById(R.id.sch_updateebtn);
        relGif = findViewById(R.id.laygif);
        gifimg = findViewById(R.id.imgGif1);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sch_deletebtn) {
            deleteSchedule();
        }
    }

    private void deleteSchedule() {
        Log.d("abc", "" + schedule_id);
        Common.progressOn(context);
        StringRequest r2 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(getContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    Dialog_Schedule.this.dismiss();
                    Log.d("pos", "" + position);
                    notifyINf.delete_Schedule(position);
                    databaseHelperSchedule.deleteSchedule(schedule_id);
                    Common.progressOff();
                } catch (Exception e) {
                    Common.progressOff();
                    e.printStackTrace();
                }
            }
        }, error -> {
            Common.progressOff();
            Common.showAlertOkayCallBack(context,
                    context.getString(R.string.error_title_alert),
                    context.getString(R.string.error_server_error),
                    context.getString(android.R.string.ok), this::onBackPressed);
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("schedule_id", schedule_id);
                logParams.put("action", "delete_schedule");
                return logParams;
            }
        };
        MySingleTon.getInstance(getContext()).addToRequestQue(r2);
    }
}
