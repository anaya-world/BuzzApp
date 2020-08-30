package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Activities.CompleteYourProfile;
import com.example.myapplication.Adapter.AdvanceSearchAdapter;
import com.example.myapplication.Adapter.SearchAdapter;
import com.example.myapplication.CountryPicker.CountryPicker;
import com.example.myapplication.CountryPicker.CountryPickerListener;
import com.example.myapplication.Intefaces.RecyclerViewAddFriendClickListener;
import com.example.myapplication.ModelClasses.FriendListModel;
import com.example.myapplication.ModelClasses.SearchedUsersModel;
import com.example.myapplication.R;
import com.example.myapplication.Utils.BaseUrl_ConstantClass;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MySingleTon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class AdvanceSearchFragment extends Fragment {
    String userId;
    ArrayList<FriendListModel> friendListModelArrayList;
    // FriendListAdapter friendListAdapter;
    AdvanceSearchAdapter advanceSearchAdapter;

    RecyclerView rv_advance_search;
    List<String> gameList = new ArrayList();
    EditText et_company_name ;
    String company_name, interest;
    Button search;
    private Spinner spinner_interest_one;
    TextView resident,et_interests;
    private String var1;
    private ArrayAdapter<String> adapter_interest;
    private ArrayList<SearchedUsersModel> searchedUsersModelArrayList;
    private SearchAdapter searchingDetailsAdapter;
    public CountryPicker picker;
    ImageView country_spinner;
    String residence;




    public AdvanceSearchFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_advance_search, container, false);

        getAllInterests();
        this.spinner_interest_one = (Spinner)view.findViewById(R.id.spinner_interest_one);
        this.picker = CountryPicker.newInstance("Select Country");
        picker.setListener(new CountryPickerListener() {
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                AdvanceSearchFragment.this.resident.setText(name);
                AdvanceSearchFragment.this.picker.dismiss();
            }
        });

        this.spinner_interest_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                var1 = spinner_interest_one.getSelectedItem().toString();
                et_interests.setText(var1);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        resident=(TextView)view.findViewById(R.id.et_country);
        resident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker.show(getFragmentManager(), "COUNTRY_PICKER");
                residence=resident.getText().toString();

            }
        });
        country_spinner=(ImageView) view.findViewById(R.id.spinner_country);
        country_spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                picker.show(getFragmentManager(), "COUNTRY_PICKER");
                residence=resident.getText().toString();

            }
        });

        adapter_interest = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, gameList);
        adapter_interest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_interest_one.setAdapter(adapter_interest);
        et_company_name = (EditText) view.findViewById(R.id.et_company_name);
        et_interests = (TextView) view.findViewById(R.id.et_interests);

        search = (Button) view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AdvanceSearchFragment","onClick"+et_company_name.getText()+ var1);
                    loadAllFriends(et_company_name.getText().toString(), var1,residence);



            }
        });

        rv_advance_search = (RecyclerView) view.findViewById(R.id.rv_advance_search);
        rv_advance_search.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_advance_search.setLayoutManager(layoutManager);

        return view;

    }

    private void loadAllFriends( String company_name,  String interests,String residence) {

        final String LOGIN_URL = BaseUrl_ConstantClass.BASE_URL;
        searchedUsersModelArrayList = new ArrayList<>();

        StringRequest stringRequestLogIn = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        Log.d("AdvanceSearchFragment","onResponse"+response);
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("search_result");
                            for (int i = 0; i <= jsonArray.length(); i++) {

                                JSONObject user_details = jsonArray.getJSONObject(i);
                                String name = user_details.optString("name");
                                //  Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
                                String user_img = user_details.optString("user_img");
                                String mobileno = user_details.optString("mobileno");
                                String f_id = user_details.optString("frnid");
                                String gender = user_details.optString("gender");
                                String secret_id = user_details.optString("secrate_id");
                                String friend_status = user_details.optString("frnstatus");
                                if(friend_status.equals("pending")){

                                }
                                else {
                                    SearchedUsersModel searchedDetails = new SearchedUsersModel(name, user_img, mobileno, gender, secret_id, friend_status, f_id);
                                    //adding the hero to searchedlIst
                                    Log.d("AdvanceSearchFragment","else" +searchedDetails);
                                    searchedUsersModelArrayList.add(searchedDetails);
                                    searchingDetailsAdapter = new SearchAdapter(searchedUsersModelArrayList, getContext(), new RecyclerViewAddFriendClickListener() {
                                        @Override
                                        public void onAddFriend(View view, int position) {

                                        }

                                        @Override
                                        public void onCancleFriendRequest(View view, int position) {

                                        }
                                    });
                                    if(searchedUsersModelArrayList.size()<=0){
                                        Toast.makeText(getContext(),"No match found",Toast.LENGTH_SHORT).show();
                                    }

                                }
                                rv_advance_search.setAdapter(searchingDetailsAdapter);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getActivity(), "it seems your network is slow" ,
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getActivity(), "sorry for inconvenience, Server is not working" ,
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getActivity(), "it seems your network is slow" ,
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "searchfriendsadvance");
                logParams.put("company", company_name);
                logParams.put("intrest", interests);
                logParams.put("residence",residence);

                return logParams;
            }
        };

        MySingleTon.getInstance(getActivity()).addToRequestQue(stringRequestLogIn);

    }
    private void getAllInterests() {
        String str = CommonUtils.baseUrl;
        StringRequest r1 = new StringRequest(Request.Method.POST, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {

                try {
                    Log.d("Interests",""+response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            gameList.add(jsonArray.getString(i));
                            adapter_interest.notifyDataSetChanged();
                        }
                        gameList.add(0, "Select Interests");
                        return;
                    }
                    Toast.makeText(getActivity(), "Please enter your valid Secret Id or Passkey", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "get_intrest");
                return logParams;
            }
        };
        MySingleTon.getInstance(getActivity()).addToRequestQue(r1);
    }
}
