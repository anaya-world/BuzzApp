package com.example.myapplication.Activities;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.AllContactsAdapter;
import com.example.myapplication.ModelClasses.ContactVO;
import com.example.myapplication.R;
import com.github.tamir7.contacts.Query;

import java.util.ArrayList;
import java.util.List;


public class AllContacts extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    String DISPLAY_NAME = "display_name";
    String NUMBER = "data1";
    private int contactrequestcode = 1;
    RecyclerView rvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contacts);
        this.rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED) {
            getSupportLoaderManager().initLoader(0, null, this);
            return;
        }
        requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, this.contactrequestcode);
    }
    private void getAllContacts() {
        List<ContactVO> contactVOList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "display_name ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("has_phone_number"))) > 0) {
                    String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String name = cursor.getString(cursor.getColumnIndex("display_name"));
                    String img = cursor.getString(cursor.getColumnIndex("photo_thumb_uri"));
                    ContactVO contactVO = new ContactVO();
                    contactVO.setContactName(name);
                    contactVO.setContactImage(img);
                    ContentResolver contentResolver2 = contentResolver;
                    Cursor phoneCursor = contentResolver2.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "contact_id = ?", new String[]{id}, null);
                    if (phoneCursor.moveToNext()) {
                        contactVO.setContactNumber(phoneCursor.getString(phoneCursor.getColumnIndex("data1")));
                    }
                    phoneCursor.close();
                    ContentResolver contentResolver3 = contentResolver;
                    Cursor cursor2 = phoneCursor;
                    Cursor emailCursor = contentResolver3.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, "contact_id = ?", new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        emailCursor.getString(emailCursor.getColumnIndex("data1"));
                    }
                    contactVOList.add(contactVO);
                }
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == 0) {
            getAllContacts();
        }
    }

    @NonNull
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] strArr = {"display_name", "data1", "data4"};
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Uri uri2 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        StringBuilder sb = new StringBuilder();
        sb.append(this.DISPLAY_NAME);
        sb.append(" ASC ");
        CursorLoader cursorLoader = new CursorLoader(this, uri2, null, null, null, sb.toString());
        return cursorLoader;
    }

    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        try {
            Query q = com.github.tamir7.contacts.Contacts.getQuery();
            q.hasPhoneNumber();
            AllContactsAdapter contactAdapter = new AllContactsAdapter(q.find(), this);
            this.rvContacts.setLayoutManager(new LinearLayoutManager(this));
            this.rvContacts.setAdapter(contactAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }
}
