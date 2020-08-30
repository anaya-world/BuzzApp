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
import com.example.myapplication.Activities.Birthday_Gif_Activity;
import com.example.myapplication.Intefaces.FriendListInterface;
import com.example.myapplication.ModelClasses.BirthdayGifModel;
import com.example.myapplication.R;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BirthdayGifAdapter extends RecyclerView.Adapter<BirthdayGifAdapter.ViewHolder> {
//    int[]arr;
    Context context;
    ArrayList<BirthdayGifModel> arrayList;
    Activity activity;
    public static final int PERMISSION_WRITE = 0;
    FriendListInterface friendListInterface;
    BirthdayGifModel imgurl;


    public BirthdayGifAdapter(Context context, ArrayList<BirthdayGifModel> arrayList, FriendListInterface friendListInterface) {
        this.context = context;
        this.arrayList = arrayList;
        this.friendListInterface = friendListInterface;
    }

    public BirthdayGifAdapter(Context applicationContext, ArrayList<BirthdayGifModel> arrayList, Birthday_Gif_Activity birthday_gif_activity, FriendListInterface friendListInterface) {
        this.context = applicationContext;
        this.arrayList = arrayList;
        this.friendListInterface = friendListInterface;
        this.activity=birthday_gif_activity;
    }

//    public BirthdayGifAdapter(Context context, ArrayList<BirthdayGifModel> arrayList, Context context1) {
//        this.context = context;
//        this.arrayList = arrayList;
//    }




    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public BirthdayGifAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.birthday_gif_adapter_item,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BirthdayGifAdapter.ViewHolder holder, int position) {

        Glide.with(context).asGif()
                .load(arrayList.get(position).getImage())
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imgurl=arrayList.get(position);
                 BirthdayGifAdapter.this.friendListInterface.sendGif(view ,holder.getAdapterPosition());


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
         imageView=   itemView.findViewById(R.id.gif_hmm);
         imageView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 BirthdayGifAdapter.this.friendListInterface.sendGif(view,ViewHolder .this.getAdapterPosition());


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
        if((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_WRITE);
            return false;
        }
        return true;
    }
    public BirthdayGifModel imagrurl(){
        return imgurl;

    }

}