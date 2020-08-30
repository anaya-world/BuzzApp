package com.example.myapplication.Utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class MySingleTon {

    private static MySingleTon mySingleTon;
    private static Context mctx;
    private RequestQueue requestQueue;

    private MySingleTon(Context context) {
        mctx = context;
        requestQueue = getRequestQueue();

    }

    public static synchronized MySingleTon getInstance(Context context) {
        if (mySingleTon == null) {
            mySingleTon = new MySingleTon(context);
        }
        return mySingleTon;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQue(Request<T> request) {
        requestQueue.add(request);

    }

}
