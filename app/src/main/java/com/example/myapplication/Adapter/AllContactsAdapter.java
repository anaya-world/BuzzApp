package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.example.myapplication.Activities.AllContacts;
import com.example.myapplication.R;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.PhoneNumber;

import java.util.List;

public class AllContactsAdapter extends Adapter<AllContactsAdapter.ContactViewHolder> {
    private List<Contact> contactVOList;
    /* access modifiers changed from: private */
    public Context mContext;

    public static class ContactViewHolder extends ViewHolder {
        RelativeLayout contactlayout;
        ImageView ivContactImage;
        TextView tvContactName;
        TextView tvPhoneNumber;

        public ContactViewHolder(View itemView) {
            super(itemView);
            this.ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            this.tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            this.tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            this.contactlayout = (RelativeLayout) itemView.findViewById(R.id.contactlayout);
        }
    }

    public AllContactsAdapter(List<Contact> contactVOList2, Context mContext2) {
        this.contactVOList = contactVOList2;
        this.mContext = mContext2;
    }

    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.single_contact_view, null));
    }

    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final Contact contactVO = (Contact) this.contactVOList.get(position);
        holder.tvContactName.setText(contactVO.getDisplayName());
        holder.tvPhoneNumber.setText(((PhoneNumber) contactVO.getPhoneNumbers().get(0)).getNumber());
        if (contactVO.getPhotoUri() == null) {
            holder.ivContactImage.setImageResource(R.drawable.ic_person_black_24dp);
        } else {
            Glide.with(this.mContext).load(contactVO.getPhotoUri()).into(holder.ivContactImage);
        }
        holder.contactlayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = ((AllContacts) AllContactsAdapter.this.mContext).getIntent();
                intent.putExtra("contactname_key", contactVO.getDisplayName());
                intent.putExtra("phonenum_key", ((PhoneNumber) contactVO.getPhoneNumbers().get(0)).getNumber());
                ((AllContacts) AllContactsAdapter.this.mContext).setResult(-1, intent);
                ((AllContacts) AllContactsAdapter.this.mContext).finish();
            }
        });
    }

    public int getItemCount() {
        return this.contactVOList.size();
    }
}
