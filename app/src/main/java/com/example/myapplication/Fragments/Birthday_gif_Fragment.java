//package com.example.myapplication.Fragments;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.pm.PackageManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.DefaultItemAnimator;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.example.myapplication.Adapter.BirthdayGifAdapter;
//import com.example.myapplication.ModelClasses.BirthdayGifModel;
//import com.example.myapplication.R;
//import com.example.myapplication.Utils.CommonUtils;
//import com.example.myapplication.Utils.MySingleTon;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.webrtc.ContextUtils.getApplicationContext;
//
//public class Birthday_gif_Fragment extends Fragment {
//    BirthdayGifAdapter birthdayGifAdapter;
//    RecyclerView recyclerView;
//    ArrayList<BirthdayGifModel> arrayList;
//    public static final int PERMISSION_WRITE = 0;
//
//
//
//
//
//    public Birthday_gif_Fragment() {
//    }
//
//
//    // TODO: Rename and change types and number of parameters
//
//
////int[]arr={R.drawable.birthday,R.drawable.birthday,R.drawable.birthday,R.drawable.birthday,R.drawable.birthday,R.drawable.birthday};
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view= inflater.inflate(R.layout.fragment_birthday_gif_, container, false);
//      //  getAllInterests();
//        recyclerView=view.findViewById(R.id.birthday_gif_recycleer);
//        //getAllInterests();
//
//
//        checkPermission();
//
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        arrayList = new ArrayList<>();
//        new fetchData().execute();
//        return view;
//    }
//    public class fetchData extends AsyncTask<String, String, String> {
//
//        @Override
//        public void onPreExecute() {
//            super .onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            arrayList.clear();
//            String result = null;
//            try {
//                URL url = new URL("https://reqres.in/api/users?page=2");
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.connect();
//
//                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
//                    BufferedReader reader = new BufferedReader(inputStreamReader);
//                    StringBuilder stringBuilder = new StringBuilder();
//                    String temp;
//
//                    while ((temp = reader.readLine()) != null) {
//                        stringBuilder.append(temp);
//                    }
//                    result = stringBuilder.toString();
//                }else  {
//                    result = "error";
//                }
//
//            } catch (Exception  e) {
//                e.printStackTrace();
//            }
//            return result;
//        }
//
//        @Override
//        public void onPostExecute(String s) {
//            super .onPostExecute(s);
//            try {
//                JSONObject object = new JSONObject(s);
//                JSONArray array = object.getJSONArray("data");
//
//                for (int i = 0; i < array.length(); i++) {
//
//                    JSONObject jsonObject = array.getJSONObject(i);
//                    String image = jsonObject.getString("avatar");
//
//                    BirthdayGifModel model = new BirthdayGifModel("image");
//                    model.setImage(image);
//                    arrayList.add(model);
//                }
//            } catch (JSONException  e) {
//                e.printStackTrace();
//            }
//
//            BirthdayGifAdapter adapter = new BirthdayGifAdapter();
//            recyclerView.setAdapter(adapter);
//
//        }
//    }
//    //runtime storage permission
//    public boolean checkPermission() {
//        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
//        if((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
//            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_WRITE);
//            return false;
//        }
//        return true;
//    }
//
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode==PERMISSION_WRITE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            //do somethings
//        }
//    }
////    private void getAllInterests() {
////        String str = CommonUtils.baseUrl;
////        StringRequest r1 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
////            public void onResponse(String response) {
////
////                try {
////                    Log.d("Interests",""+response);
////                    JSONObject jsonObject = new JSONObject(response);
////                    if (jsonObject.getString("success").equals("true")) {
////                        JSONArray jsonArray = jsonObject.getJSONArray("data");
////                        for (int i = 0; i < jsonArray.length(); i++) {
////
////
////                            gameList.add(jsonArray.getInt(i));
////                            birthdayGifAdapter.notifyDataSetChanged();
////                        }
////
////                        return;
////                    }
////                 } catch (JSONException e) {
////                    e.printStackTrace();
////                }
////            }
////        }, new Response.ErrorListener() {
////            public void onErrorResponse(VolleyError error) {
////                // Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
////            }
////        }) {
////            /* access modifiers changed from: protected */
////            public Map<String, String> getParams() throws AuthFailureError {
////                Map<String, String> logParams = new HashMap<>();
////                logParams.put("action", "BirthdayGif");
////                return logParams;
////            }
////        };
////        MySingleTon.getInstance(getActivity()).addToRequestQue(r1);
////    }
////private void getAllInterests() {
////    String str = CommonUtils.baseUrl;
////    StringRequest r1 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
////        public void onResponse(String response) {
////
////            try {
////                Log.d("Interests",""+response);
////                JSONObject jsonObject = new JSONObject(response);
////                if (jsonObject.getString("success").equals("true")) {
////                    JSONArray jsonArray = jsonObject.getJSONArray("data");
////                    for (int i = 0; i < jsonArray.length(); i++) {
////                        JSONObject user_details = jsonArray.getJSONObject(i);
////                        String Gif_images=user_details.optString("Gif");
////                        gameList.add(Gif_images);
////                        birthdayAdapter.notifyDataSetChanged();
////                    }
////
////                    return;
////                }
////                Toast.makeText(getActivity(), "Please enter your valid Secret Id or Passkey", Toast.LENGTH_SHORT).show();
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
////        }
////    }, new Response.ErrorListener() {
////        public void onErrorResponse(VolleyError error) {
////            // Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
////        }
////    }) {
////        /* access modifiers changed from: protected */
////        public Map<String, String> getParams() throws AuthFailureError {
////            Map<String, String> logParams = new HashMap<>();
////            logParams.put("action", "BirthdayGif");
////            return logParams;
////        }
////    };
////    MySingleTon.getInstance(getActivity()).addToRequestQue(r1);
////}
//}
//
