package com.example.myapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.ModelClasses.CommentModel;
import com.example.myapplication.R;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends Adapter<CommentAdapter.MyHolder> {
    ArrayList<CommentModel> commentModels;
    Context context;

    public class MyHolder extends ViewHolder {
        CircleImageView ivprofileimagessss;
        TextView tv_comment;
        TextView tv_username;
        TextView tvcommenttime;

        public MyHolder(View itemView) {
            super(itemView);
            this.ivprofileimagessss = (CircleImageView) itemView.findViewById(R.id.ivprofileimagesssssds);
            this.tv_username = (TextView) itemView.findViewById(R.id.tv_username);
            this.tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            this.tvcommenttime = (TextView) itemView.findViewById(R.id.tvcommenttime);
        }
    }

    public CommentAdapter(ArrayList<CommentModel> commentModels2, Context context2) {
        this.commentModels = commentModels2;
        this.context = context2;
    }

    @NonNull
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_list, parent, false));
    }

    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        CommentModel commentModelpostiong = (CommentModel) this.commentModels.get(position);

        holder.tv_username.setText(commentModelpostiong.getName());
        Log.d("names",""+commentModelpostiong.getName());
        Log.d("Secrate_id",""+commentModelpostiong.getSecrate_id());
        Log.d("Secrate_id",""+commentModelpostiong);
        holder.tv_comment.setText(commentModelpostiong.getPost_comments());
        Log.d("comments",""+commentModelpostiong.getPost_comments());

        holder.tvcommenttime.setText(commentModelpostiong.getPost_date());
        Log.d("date",""+commentModelpostiong.getPost_date());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_img);
        requestOptions.error(R.drawable.buzzplaceholder);
        Glide.with(this.context).applyDefaultRequestOptions ( requestOptions )
                .load(commentModelpostiong.getUser_img()).into(holder.ivprofileimagessss);
        Log.d("images",""+commentModelpostiong.getUser_img());
    }

    public int getItemCount() {
        return this.commentModels.size();
    }
}
