package com.example.myapplication.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.AnniversaryGifModel;
import com.example.myapplication.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SpecialDayGifAdapter extends RecyclerView.Adapter<SpecialDayGifAdapter.ViewHolder> {
    Context context;
    ArrayList<AnniversaryGifModel> arrayList;
    Activity activity;
    FriendListInterface friendListInterface;
    AnniversaryGifModel imgurl;
    public static final int PERMISSION_WRITE = 0;
//    public BirthdayGifAdapter(int[] arr) {
//        this.arr = arr;
//    }

    public SpecialDayGifAdapter(Context context, ArrayList<AnniversaryGifModel> arrayList, Activity activity, FriendListInterface friendListInterface) {
        this.context = context;
        this.arrayList = arrayList;
        this.activity = activity;
        this.friendListInterface = friendListInterface;
    }

    @NonNull
    @Override
    public SpecialDayGifAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anniversarygif_adapter,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).asGif()
                .load(arrayList.get(position).getImage())
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgurl=arrayList.get(position);
                SpecialDayGifAdapter.this.friendListInterface.sendGif(view ,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=   itemView.findViewById(R.id.anniversary_gif);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpecialDayGifAdapter.this.friendListInterface.sendGif(view, SpecialDayGifAdapter.ViewHolder.this.getAdapterPosition());
                }
            });

        }

    }

    public class Downloading extends AsyncTask<String, Integer, String> {

        ProgressDialog dialog = new ProgressDialog(activity);
        @Override
        public void onPreExecute() {
            super .onPreExecute();
            dialog.setMessage("Please wait");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            File mydir = new File(Environment.getExternalStorageDirectory() + "/11zon");
            if (!mydir.exists()) {
                mydir.mkdirs();
            }

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(url[0]);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            SimpleDateFormat dateFormat = new SimpleDateFormat("mmddyyyyhhmmss");
            String date = dateFormat.format(new Date());

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle("Downloading")
                    .setDestinationInExternalPublicDir("/11zon", date + ".jpg");

            manager.enqueue(request);
            return mydir.getAbsolutePath() + File.separator + date + ".jpg";
        }

        @Override
        public void onPostExecute(String s) {
            super .onPostExecute(s);
            dialog.dismiss();
            Toast.makeText(context, "Image Downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    //runtime storage permission
    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_WRITE);
            return false;
        }
        return true;
    }

    public AnniversaryGifModel imagrurl(){
        return imgurl;
    }
}