package com.example.myapplication.Utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.example.myapplication.R;

public class ProgressLogin extends Dialog {
    private  ProgressDialog progressDialog;
    Context context;

    public ProgressLogin(Context context) {
        super(context);
        this.context=context;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progresslogin);
    }

}
