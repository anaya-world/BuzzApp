package com.example.myapplication.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.FragmentActivity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.CountryPicker.CountryPicker;
import com.example.myapplication.CountryPicker.CountryPickerListener;
import com.example.myapplication.ModelClasses.User;
import com.example.myapplication.R;
import com.example.myapplication.Services.MyFirebaseMessagingService;
import com.example.myapplication.Utils.BaseUrl_ConstantClass;
import com.example.myapplication.Utils.CommonUtils;
import com.example.myapplication.Utils.MimeType;
import com.example.myapplication.Utils.MySingleTon;
import com.example.myapplication.Utils.PrefUtils;
import com.example.myapplication.Utils.PreferenceUtils;
import com.example.myapplication.Utils.PushUtils;
import com.example.myapplication.Utils.SharedPrefManager;
import com.example.myapplication.Utils.SyncManagerUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBirdPushHelper;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.syncmanager.SendBirdSyncManager;
import com.sendbird.syncmanager.handler.CompletionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteYourProfile extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    /* access modifiers changed from: private */
    public String aDay = "";
    ArrayList<String> aListNumbers;
    /* access modifiers changed from: private */
    public String aMonth = "";
    /* access modifiers changed from: private */
    public String aYear = "";
    ArrayAdapter<String> adapter_interest;
    Spinner alphabetical_letters;
    String user_id2;
    String mCurrentPhotoPath;
    String alphabeticalletters;
    LinearLayout anniversary_relative;
    ArrayList<String> arrayList_spinner_interest = new ArrayList<>();
    /* access modifiers changed from: private */
    public String bDay = "";
    /* access modifiers changed from: private */
    public String bMonth = "";
    /* access modifiers changed from: private */
    public String bYear = "";
    final Calendar c = Calendar.getInstance();
    String caller_id;
    String check;
    ImageView date_anniversary;
    TextView date_of_birth;
    String email;
    TextView et_anniversary;
    TextView et_resident;
    EditText et_user_name;
    EditText et_work_for;
    int minDay,minMonth,MinYear;
    List<String> gameList = new ArrayList();
    String gender1 = "Male";
    int i;
    String user_id1 ;
    String secrate_id;
    LinearLayout liner_security_code_hide;
    private ArrayList<String> list;
    int mDay = c.get(Calendar.DATE);
    final int mMonth = c.get(Calendar.MONTH);
    int mYear = c.get(Calendar.YEAR);
    String marital_status = "Single";
    String mobile_number;
    Spinner number_spinner;
    /* access modifiers changed from: private */
    public CountryPicker picker;
    RadioButton radioF;
    RadioGroup radioGrp_marital;
    RadioGroup radioGrp_sex;
    RadioButton radioM;
    RadioButton radioSexButton;
    RadioButton radio_Single;
    RadioButton radio_married;
    RadioButton radiomaritalstatus;
    String sec;
    String secret_id;
    String security_code = ExifInterface.GPS_MEASUREMENT_IN_PROGRESS;
    int selectedId;
    Button send_data;
    ScrollView relativeLayout;
    ProgressBar progressBar;
    Spinner spinner_interest_five;
    Spinner spinner_interest_four;
    Spinner spinner_interest_one;
    Spinner spinner_interest_six;
    Spinner spinner_interest_three;
    Spinner spinner_interest_two;
    String user_id;
    String var1 = "";
    String var2 = "";
    String var3 = "";
    String var4 = "";
    String var5 = "";
    String var6 = "";
    String var7 = "";
    private ImageView settings_image_view;
    private ImageView pop_up_close;
    private PopupWindow popupWindow;
    private LinearLayout linearLayout1;
    private ImageView choose_image_iv;
    private ImageView iv_gallery_choose;
    private ImageView delete_profile_image;
    private CircleImageView user_profile_image_view;
    private AlertDialog alertDialog;
    private Bitmap bt;
    private String imageurl;
    private ProgressDialog progressDialog;
    private String profile_pic;
  //  private SharedPrefsHelper sharedPrefsHelper;
    private UUID deviceid;
//    private QBUser userForSave;
//    private SessionManager sessionManager;
//    private QBResRequestExecutor requestExecutor;
    private String qb_name;
    private String TAG="CompleteYourProfile";
    RequestOptions requestOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_your_profile);

        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_img);
        requestOptions.error(R.drawable.buzzplaceholder);


        relativeLayout=(ScrollView) findViewById(R.id.relLAy);
        progressBar=(ProgressBar)findViewById(R.id.progress);
        this.radioGrp_marital = (RadioGroup) findViewById(R.id.radioGrp_marital);
        this.radioF = (RadioButton) findViewById(R.id.radioF);
        this.radioM = (RadioButton) findViewById(R.id.radioM);
        this.radio_married = (RadioButton) findViewById(R.id.radio_married);
        this.radio_Single = (RadioButton) findViewById(R.id.radio_Single);
        relativeLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        this.secret_id = SharedPrefManager.getInstance(this).getUser().getUser_secret_id().toString();

        this.liner_security_code_hide = (LinearLayout) findViewById(R.id.liner_security_code_hide);
        this.check = getIntent().getStringExtra("CHECK");
        if (this.check.equals("0")) {
            this.liner_security_code_hide.setVisibility(View.GONE);
            this.sec = getIntent().getStringExtra("SECURITY_CODE");
            check="3";
        } else {
            this.liner_security_code_hide.setVisibility(View.VISIBLE);
        }
        Integer[] testArray = new Integer[1000];
        this.aListNumbers = new ArrayList<>();
        this.i = 0;
        while (this.i < testArray.length) {
            testArray[this.i] = Integer.valueOf(this.i);
            this.aListNumbers.add(String.format("%03d", new Object[]{Integer.valueOf(this.i)}));
            this.i++;
        }
        this.aListNumbers.add(0, "");
        this.number_spinner = (Spinner) findViewById(R.id.number_spinner);
        this.spinner_interest_one = (Spinner) findViewById(R.id.spinner_interest_one);
        this.spinner_interest_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(CompleteYourProfile.this.getResources().getColor(R.color.black));
                CompleteYourProfile.this.var1 = CompleteYourProfile.this.spinner_interest_one.getSelectedItem().toString();


            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        this.spinner_interest_two = (Spinner) findViewById(R.id.spinner_interest_two);
        this.spinner_interest_two.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(CompleteYourProfile.this.getResources().getColor(R.color.black));
                CompleteYourProfile.this.var2 = (String) parent.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.spinner_interest_three = (Spinner) findViewById(R.id.spinner_interest_three);
        this.spinner_interest_three.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(CompleteYourProfile.this.getResources().getColor(R.color.black));
                CompleteYourProfile.this.var3 = (String) parent.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.spinner_interest_four = (Spinner) findViewById(R.id.spinner_interest_four);
        this.spinner_interest_four.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(CompleteYourProfile.this.getResources().getColor(R.color.black));
                CompleteYourProfile.this.var4 = (String) parent.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.spinner_interest_five = (Spinner) findViewById(R.id.spinner_interest_five);
        this.spinner_interest_five.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(CompleteYourProfile.this.getResources().getColor(R.color.black));
                CompleteYourProfile.this.var5 = (String) parent.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.spinner_interest_six = (Spinner) findViewById(R.id.spinner_interest_six);
        this.spinner_interest_six.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(CompleteYourProfile.this.getResources().getColor(R.color.black));
                CompleteYourProfile.this.var6 = (String) parent.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.user_id = SharedPrefManager.getInstance(this).getUser().getUser_id().toString();
        this.mobile_number = SharedPrefManager.getInstance(this).getUser().getUser_mobile_no().toString();
        this.email = SharedPrefManager.getInstance(this).getUser().getUser_email().toString();

        this.radioGrp_marital.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                CompleteYourProfile.this.selectedId = CompleteYourProfile.this.radioGrp_marital.getCheckedRadioButtonId();
                CompleteYourProfile.this.radioSexButton = (RadioButton) CompleteYourProfile.this.findViewById(CompleteYourProfile.this.selectedId);
                if (CompleteYourProfile.this.radioSexButton.getText().toString().equals("Single")) {
                    CompleteYourProfile.this.marital_status = "Single";
                    CompleteYourProfile.this.anniversary_relative.setVisibility(View.GONE);
                } else if (CompleteYourProfile.this.radioSexButton.getText().toString().equals("Married")) {
                    CompleteYourProfile.this.marital_status = "Married";
                    CompleteYourProfile.this.anniversary_relative.setVisibility(View.VISIBLE);
                }
            }
        });
        this.alphabetical_letters = (Spinner) findViewById(R.id.alphabets_spinner);
        this.list = new ArrayList<>();
        this.list.add("");
        this.list.add(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS);
        this.list.add("B");
        this.list.add("C");
        this.list.add("D");
        this.list.add(ExifInterface.LONGITUDE_EAST);
        this.list.add("F");
        this.list.add("G");
        this.list.add("H");
        this.list.add("I");
        this.list.add("J");
        this.list.add("K");
        this.list.add("L");
        this.list.add("M");
        this.list.add("N");
        this.list.add("O");
        this.list.add("P");
        this.list.add("Q");
        this.list.add("R");
        this.list.add(ExifInterface.LATITUDE_SOUTH);
        this.list.add(ExifInterface.GPS_DIRECTION_TRUE);
        this.list.add("U");
        this.list.add(ExifInterface.GPS_MEASUREMENT_INTERRUPTED);
        this.list.add(ExifInterface.LONGITUDE_WEST);
        this.list.add("X");
        this.list.add("Y");
        this.list.add("Z");


        getAllInterests();
        genderFun();
        loadAllDetails();
        init();
        setSpinnerData();
        // relativeLayout.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                relativeLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        },2000);
    }

    private void annversaryPickerDialog() {

        Calendar cal1=Calendar.getInstance() ;
        cal1.set(Integer.valueOf(bYear),Integer.valueOf(bMonth)-1,Integer.valueOf(bDay));
        Log.d("annversaryPickerDialog","11-"+bYear+bMonth+bDay);

        Log.d("annversaryPickerDialog","1-"+System.currentTimeMillis());
        Log.d("annversaryPickerDialog","2-"+cal1.getTimeInMillis());
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = 24)
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int month_of_year = monthOfYear + 1;
                if (dayOfMonth < 10) {
                    CompleteYourProfile completeYourProfile = CompleteYourProfile.this;
                    StringBuilder sb = new StringBuilder();
                    sb.append("0");
                    sb.append(dayOfMonth);
                    completeYourProfile.aDay = sb.toString();
                } else {
                    CompleteYourProfile completeYourProfile2 = CompleteYourProfile.this;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("");
                    sb2.append(dayOfMonth);
                    completeYourProfile2.aDay = sb2.toString();
                }
                if (month_of_year < 10) {
                    CompleteYourProfile completeYourProfile3 = CompleteYourProfile.this;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("0");
                    sb3.append(month_of_year);
                    completeYourProfile3.aMonth = sb3.toString();
                } else {
                    CompleteYourProfile completeYourProfile4 = CompleteYourProfile.this;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("");
                    sb4.append(month_of_year);
                    completeYourProfile4.aMonth = sb4.toString();
                }
                CompleteYourProfile completeYourProfile5 = CompleteYourProfile.this;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("");
                sb5.append(year);
                completeYourProfile5.aYear = sb5.toString();
                TextView textView = CompleteYourProfile.this.et_anniversary;
                StringBuilder sb6 = new StringBuilder();
                sb6.append(CompleteYourProfile.this.aDay);
                sb6.append("-");
                sb6.append(CompleteYourProfile.this.aMonth);
                sb6.append("-");
                sb6.append(CompleteYourProfile.this.aYear);
                textView.setText(sb6.toString());
            }
        }, this.mYear, this.mMonth, this.mDay);
        Log.d("annversaryPickerDialog","3-"+System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMinDate(cal1.getTimeInMillis());

        Log.d("annversaryPickerDialog","4-"+cal1.getTimeInMillis());
        datePickerDialog.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        Log.d("annversaryPickerDialog","5-"+cal1.getTimeInMillis());
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    private void dateOfBirth() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = 24)
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int month_of_year = monthOfYear + 1;
                if (dayOfMonth < 10) {
                    CompleteYourProfile completeYourProfile = CompleteYourProfile.this;
                    StringBuilder sb = new StringBuilder();
                    sb.append("0");
                    sb.append(dayOfMonth);
                    completeYourProfile.bDay = sb.toString();
                } else {
                    CompleteYourProfile completeYourProfile2 = CompleteYourProfile.this;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("");
                    sb2.append(dayOfMonth);
                    completeYourProfile2.bDay = sb2.toString();
                }
                if (month_of_year < 10) {
                    CompleteYourProfile completeYourProfile3 = CompleteYourProfile.this;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("0");
                    sb3.append(month_of_year);
                    completeYourProfile3.bMonth = sb3.toString();
                } else {
                    CompleteYourProfile completeYourProfile4 = CompleteYourProfile.this;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("");
                    sb4.append(month_of_year);
                    completeYourProfile4.bMonth = sb4.toString();
                }
                CompleteYourProfile completeYourProfile5 = CompleteYourProfile.this;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("");
                sb5.append(year);
                completeYourProfile5.bYear = sb5.toString();
                TextView textView = CompleteYourProfile.this.date_of_birth;
                StringBuilder sb6 = new StringBuilder();
                sb6.append(CompleteYourProfile.this.bDay);
                sb6.append("-");
                sb6.append(CompleteYourProfile.this.bMonth);
                sb6.append("-");
                sb6.append(CompleteYourProfile.this.bYear);
                textView.setText(sb6.toString());
            }
        }, this.mYear, this.mMonth, this.mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
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
                        gameList.add(0, "Select");
                        return;
                    }
                    Toast.makeText(CompleteYourProfile.this, "Please enter your valid Secret Id or Passkey", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CompleteYourProfile.this, "", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> logParams = new HashMap<>();
                logParams.put("action", "get_intrest");
                return logParams;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(r1);
    }

    private void setSpinnerData() {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item, list) {
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                }
                return true;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(-7829368);
                } else {
                    tv.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                }
                return view;
            }
        };
        adapter1.setDropDownViewResource(R.layout.spinner_item);
        this.alphabetical_letters.setAdapter(adapter1);
        this.alphabetical_letters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(CompleteYourProfile.this.getResources().getColor(R.color.black));
                if (position == 0) {
                    CompleteYourProfile.this.alphabeticalletters = "";
                    return;
                }
                CompleteYourProfile.this.alphabeticalletters = (String) parent.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, this.aListNumbers) {
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                }
                return true;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(-7829368);
                } else {
                    tv.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_item);
        this.number_spinner.setAdapter(adapter);
        this.number_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(CompleteYourProfile.this.getResources().getColor(R.color.black));
                if (position == 0) {
                    CompleteYourProfile.this.var7 = "";
                    return;
                }
                CompleteYourProfile.this.var7 = CompleteYourProfile.this.number_spinner.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        adapter_interest = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gameList);
        adapter_interest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, aListNumbers);

        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
        alphabetical_letters.setAdapter(adapter1);

        spinner_interest_one.setAdapter(adapter_interest);
        spinner_interest_two.setAdapter(adapter_interest);
        spinner_interest_three.setAdapter(adapter_interest);
        spinner_interest_four.setAdapter(adapter_interest);
        spinner_interest_five.setAdapter(adapter_interest);
        spinner_interest_six.setAdapter(adapter_interest);
        number_spinner.setAdapter(adapter);
    }

    private void sendData() {
        String recident = this.et_resident.getText().toString().trim();

        String user_name = this.et_user_name.getText().toString().trim();
        StringBuilder sb = new StringBuilder();
        sb.append(this.bDay);
        sb.append("-");
        sb.append(this.bMonth);
        sb.append("-");
        sb.append(this.bYear);
        String DOB = sb.toString();
        String trim = this.radioSexButton.getText().toString().trim();
        String work_for = this.et_work_for.getText().toString().trim();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.aDay);
        sb2.append("-");
        sb2.append(this.aMonth);
        sb2.append("-");
        sb2.append(this.aYear);
        String anniversary_date = sb2.toString();
        if (user_name.equals("")) {
            this.et_user_name.setError("User name Can't be empty");
            this.et_user_name.requestFocus();
        } else if (work_for.equals("")) {
            this.et_work_for.setError("Work for Can't be empty");
            this.et_work_for.requestFocus();
        } else if (recident.equals("")) {
            this.et_resident.setError("Resident Can't be empty");
            this.et_resident.requestFocus();
        } else if (check.equals("1") && TextUtils.isEmpty(this.alphabeticalletters) && TextUtils.isEmpty(this.var7)) {
            Toast.makeText(this, "Please select security code.", Toast.LENGTH_LONG).show();
        }else if(var1.matches("Select")){
            Toast.makeText(getApplicationContext(),"please select interest",Toast.LENGTH_SHORT).show();
            spinner_interest_one.requestFocus();
        } else if (var2.matches("Select") ) {
            Toast.makeText(getApplicationContext(), "please select interest", Toast.LENGTH_SHORT).show();

        }else if(var3.matches("Select")){
            Toast.makeText(this, "Please select interest", Toast.LENGTH_SHORT).show();

        } else if(var4.matches("Select")){
            Toast.makeText(this, "please select interest", Toast.LENGTH_SHORT).show();

        }else if(var5.matches("Select")){
            Toast.makeText(this, "Please select interest", Toast.LENGTH_SHORT).show();

        }else if(var6.matches("Select")){
            Toast.makeText(getApplicationContext(),"please select interest",Toast.LENGTH_SHORT).show();
        }
        else {
            genderFun();
            String str = CommonUtils.baseUrl;
            final String str2 = user_name;
            final String str3 = DOB;
            final String str4 = anniversary_date;
            final String str5 = work_for;
            final String str6 = recident;
            StringRequest r0 = new StringRequest(1, CommonUtils.baseUrl, new Response.Listener<String>() {
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("success");
                        String message = jsonObject.getString("message");
                        if (status.equals("true")) {
                            if(check.equals("1")) {
                                connectToSendBird(SharedPrefManager.getInstance(CompleteYourProfile.this).getUser().getUser_id());
                                check="2";
                            }
                             Toast.makeText(CompleteYourProfile.this, message, Toast.LENGTH_SHORT).show();
                            CompleteYourProfile.this.startActivity(new Intent(CompleteYourProfile.this, UserDetailsActivity.class));
                            CompleteYourProfile.this.finish();

                            return;
                        }
                        Toast.makeText(CompleteYourProfile.this, message != null ? message : "Unable to update your data.", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CompleteYourProfile.this, "", Toast.LENGTH_SHORT).show();
                }
            }) {
                /* access modifiers changed from: protected */
                public Map<String, String> getParams() throws AuthFailureError {
                    StringBuilder sb = new StringBuilder();
                    sb.append(CompleteYourProfile.this.alphabeticalletters);
                    sb.append(CompleteYourProfile.this.var7);
                    String sec1;
                    if(check.equals("0")){
                        sec1=sec;

                    }
                    else {
                        sec1= sb.toString();
                    }

                    JSONArray mJSONArray = new JSONArray(Arrays.asList(new String[]{CompleteYourProfile.this.var1, CompleteYourProfile.this.var2, CompleteYourProfile.this.var3, CompleteYourProfile.this.var4, CompleteYourProfile.this.var5, CompleteYourProfile.this.var6}));
                    Map<String, String> logParams = new HashMap<>();
                    logParams.put("action", "update_profile");
                    logParams.put("userid", CompleteYourProfile.this.user_id);
                    logParams.put("mobileno", CompleteYourProfile.this.mobile_number);
                    logParams.put("email", CompleteYourProfile.this.email);
                    logParams.put("name", str2);
                    logParams.put("marital", CompleteYourProfile.this.marital_status);
                    logParams.put("gender", CompleteYourProfile.this.gender1);
                    logParams.put("dob", str3);
                    logParams.put("anniversary", str4);
                    logParams.put("workfor", str5);
                    logParams.put("resident", str6);
                    logParams.put("instrests", mJSONArray.toString());
                    logParams.put("security_code", sec1);
                    logParams.put("secret_id", CompleteYourProfile.this.secret_id);
                    User user = new User(CompleteYourProfile.this.user_id,
                            str2,
                            CompleteYourProfile.this.email,
                            CompleteYourProfile.this.mobile_number,
                            " ", CompleteYourProfile.this.secret_id,
                            " ", str6, "true",caller_id);
                    SharedPrefManager.getInstance(CompleteYourProfile.this).userLogin(user);
                    return logParams;
                }
            };
            MySingleTon.getInstance(this).addToRequestQue(r0);
        }
    }
    private void connectToSendBird(String userId)
    {
        Log.d("lifecycle","10"+userId);

        PrefUtils.setUserId(CompleteYourProfile.this, null);
        PrefUtils.setAccessToken(CompleteYourProfile.this, null);
        PrefUtils.setCalleeId(CompleteYourProfile.this, null);
        PrefUtils.setPushToken(CompleteYourProfile.this, null);

        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(com.sendbird.android.User user, SendBirdException e) {

                SendBirdPushHelper.registerPushHandler(new MyFirebaseMessagingService());

                Log.d("lifecycle","LoginRegistrationActivity onCompleted"+e);
                //  FirebaseApp chats = FirebaseApp.getInstance("chats");
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(CompleteYourProfile.this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d("lifecycle","LoginRegistrationActivity pushu onSuccess"+instanceIdResult.getToken()+e);
                        SendBird.registerPushTokenForCurrentUser(instanceIdResult.getToken(), new SendBird.RegisterPushTokenWithStatusHandler() {
                            @Override
                            public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
                                if (e != null) {
                                    Log.d("lifecycle","LoginRegistrationActivity pushu onRegistered"+instanceIdResult.getToken()+e);
                                    // Error.
                                    return;
                                }
                            }
                        });
                    }
                });
                if (e == null) {

                } else {

                    PreferenceUtils.setConnected(false);
                }
            }
        });
        SyncManagerUtils.setup(CompleteYourProfile.this, userId, new CompletionHandler() {
            @Override
            public void onCompleted(SendBirdException e) {
                Log.d("lifecycle","login onCompleted"+e);
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                SendBirdSyncManager.getInstance().resumeSync();
                PreferenceUtils.setConnected(true);

            }
        });
        PushUtils.getPushToken(this, (pushToken, e) -> {
            if (e != null) {
                Log.d(TAG, "authenticate() => Failed (e: " + e.getMessage() + ")");

                return;
            }
            Log.d(TAG, "authenticate() => authenticate()"+pushToken);
            SendBirdCall.authenticate(new AuthenticateParams(userId).setAccessToken(""), (user, e1) -> {
                if (e1 != null) {
                    Log.d(TAG, "authenticate() => authenticate() => Failed (e1: " + e1.getMessage() + ")");



                    return;
                }

                Log.d(TAG, "authenticate() => registerPushToken()");
                SendBirdCall.registerPushToken(pushToken, false, e2 -> {
                    if (e2 != null) {
                        Log.d(TAG, "authenticate() => registerPushToken() => Failed (e2: " + e2.getMessage() + ")");

                        return;
                    }

                    PrefUtils.setAppId(this, SendBirdCall.getApplicationId());
                    PrefUtils.setUserId(this, userId);
                    PrefUtils.setPushToken(this, pushToken);

                    Log.d(TAG, "authenticate() => authenticate() => OK");

                });
            });
        });

    }

    public void genderFun() {

        this.radioGrp_sex = (RadioGroup) findViewById(R.id.radioGrp_sex);
        this.radioSexButton = (RadioButton) findViewById(this.radioGrp_sex.getCheckedRadioButtonId());
        this.radioGrp_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioM) {
                    CompleteYourProfile.this.gender1 = "Male";
                    return;
                }
                CompleteYourProfile.this.gender1 = "Female";
            }
        });
        if (this.radioSexButton.getText().toString().equals("Male")) {
            this.gender1 = "Male";
        } else if (this.radioSexButton.getText().toString().equals("Female")) {
            this.gender1 = "Female";
        }
    }
    private void loadAllDetails() {

        final String LOGIN_URL = BaseUrl_ConstantClass.BASE_URL;

        StringRequest stringRequestLogIn = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("loadAllDetails",""+response);

                        int i = 0;
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("success");
                            if (status.equals("true")) {
                                JSONObject user_details = jsonObject.getJSONObject("user_details");
                                user_id2 = user_details.optString("id");
                                secrate_id = user_details.optString("secrate_id");
                                String mobileno = user_details.optString("mobileno");
                                String email = user_details.optString("email");
                                String name = user_details.optString("name");
                                String marital = user_details.optString("marital");
                                String gender = user_details.optString("gender");
                                String dob = user_details.optString("dob");
                                String anniversary = user_details.optString("anniversary");
                                String workfor = user_details.optString("workfor");
                                String resident = user_details.optString("resident");
                                String security_code = user_details.optString("security_code");
                                //        JSONArray security_code1=user_details.getJSONArray("instrests");
                                profile_pic = user_details.optString("user_img");

                                if (profile_pic != null) {
                                    updateCurrentUserInfo(name, profile_pic);
                                    Glide.with(CompleteYourProfile.this).setDefaultRequestOptions(requestOptions).load(profile_pic).into(user_profile_image_view);

                                } else {
                                    updateCurrentUserInfo(name, "");
                                    user_profile_image_view.setBackgroundResource(R.drawable.app_buzz_logo);


                                }
                                et_user_name.setText(name);
                                if (dob.equalsIgnoreCase("null")) {
                                    date_of_birth.setHint("Date of birth");
                                } else {
                                    date_of_birth.setText(dob);
                                    Log.d("date_of_birth", dob + dob.substring(0, 4) + dob.substring(5, 7) + dob.substring(8, 10));
                                    bDay=dob.substring(0, 2);
                                    bMonth=dob.substring(3, 5);
                                    bYear=dob.substring(6, 10);

                                }
                                if (gender.equals("Male")) {
                                    radioM.setChecked(true);
                                } else {
                                    radioF.setChecked(true);
                                }
                                if (marital.equals("Single")) {
                                    radio_Single.setChecked(true);

                                } else {
                                    radio_married.setChecked(true);
                                }
                                if (anniversary.equalsIgnoreCase("null")) {
                                    et_anniversary.setHint("Anniversary Date");
                                }
                                else {
                                    et_anniversary.setText(anniversary);
                                }
                                if (workfor.contains("No")) {
                                    et_work_for.setHint("Fill work detail");
                                } else
                                    et_work_for.setText(workfor);
                                if (resident.contains("No"))
                                    et_resident.setHint("Fill Residential detail");
                                else et_resident.setText(resident);

                                if(check.equals("1")) {
                                    Log.d("startSignUpNewUser","1");
                                    SharedPrefManager.getInstance(CompleteYourProfile.this).save("check",1);
                                    SharedPrefManager.getInstance(CompleteYourProfile.this).save("checkautostart",1);
                                  //  startSignUpNewUser(createUserWithEnteredData());
                                }

                                String instrestsArray = user_details.getString("instrests").replace("[","").replace("]","");
                                String[] vs=instrestsArray.split(",");

                            } else {
                                Toast.makeText(CompleteYourProfile.this, "Please enter your valid Secret ID or Passkey", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CompleteYourProfile.this, "it seems your network is slow" ,
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                        } else if (error instanceof ServerError) {
                            Toast.makeText(CompleteYourProfile.this, "sorry for inconvenience, Server is not working" ,
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(CompleteYourProfile.this, "it seems your network is slow" ,
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
                logParams.put("action", "getuser");
                logParams.put("userid", user_id);
                return logParams;
            }
        };

        MySingleTon.getInstance(CompleteYourProfile.this).addToRequestQue(stringRequestLogIn);

    }

    public void updateCurrentUserInfo(String userNickname, String image_url) {
        SendBird.updateCurrentUserInfo(userNickname, image_url, new SendBird.UserInfoUpdateHandler() {
            public void onUpdated(SendBirdException e) {
                if (e == null) {
                }
            }
        });

    }

    private void init() {
       // this.sharedPrefsHelper = SharedPrefsHelper.getInstance();
        user_profile_image_view=findViewById(R.id.user_profile_image_view);
        this.linearLayout1 = (LinearLayout) findViewById(R.id.linearLay);
        this.settings_image_view = (ImageView) findViewById(R.id.settings_image_view);
        this.settings_image_view.setOnClickListener(this);
        this.anniversary_relative = (LinearLayout) findViewById(R.id.anniversary_relative);
        this.et_anniversary = (TextView) findViewById(R.id.et_anniversary);
        this.et_resident = (TextView) findViewById(R.id.et_resident);
        this.date_anniversary = (ImageView) findViewById(R.id.date_anniversary);
        this.date_anniversary.setOnClickListener(this);
        this.et_resident.setOnClickListener(this);
        this.picker = CountryPicker.newInstance("Select Country");
        this.picker.setListener(new CountryPickerListener() {
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                CompleteYourProfile.this.et_resident.setText(name);
                CompleteYourProfile.this.picker.dismiss();
            }
        });
        this.send_data = (Button) findViewById(R.id.send_data);
        this.send_data.setOnClickListener(this);
        this.et_user_name = (EditText) findViewById(R.id.et_user_name);
        this.et_work_for = (EditText) findViewById(R.id.et_work_for);
        this.date_of_birth = (TextView) findViewById(R.id.date_of_birth);
        this.date_of_birth.setOnClickListener(this);
        Calendar c2 = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(c2.get(Calendar.YEAR));
        this.bYear = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("");
        sb2.append(c2.get(Calendar.YEAR));
        this.aYear = sb2.toString();
        if (c2.get(Calendar.MONTH) + 1 < 10) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("0");
            sb3.append(c2.get(Calendar.MONTH) + 1);
            this.bMonth = sb3.toString();
            StringBuilder sb4 = new StringBuilder();
            sb4.append("0");
            sb4.append(c2.get(Calendar.MONTH) + 1);
            this.aMonth = sb4.toString();
        } else {
            StringBuilder sb5 = new StringBuilder();
            sb5.append("");
            sb5.append(c2.get(Calendar.MONTH) + 1);
            this.bMonth = sb5.toString();
            StringBuilder sb6 = new StringBuilder();
            sb6.append("");
            sb6.append(c2.get(Calendar.MONTH) + 1);
            this.aMonth = sb6.toString();
        }
        if (c2.get(Calendar.DATE) < 10) {
            StringBuilder sb7 = new StringBuilder();
            sb7.append("0");
            sb7.append(c2.get(Calendar.DATE));
            this.bDay = sb7.toString();
            StringBuilder sb8 = new StringBuilder();
            sb8.append("0");
            sb8.append(c2.get(Calendar.DATE));
            this.aDay = sb8.toString();
        } else {
            StringBuilder sb9 = new StringBuilder();
            sb9.append("");
            sb9.append(c2.get(Calendar.DATE));
            this.bDay = sb9.toString();
            StringBuilder sb10 = new StringBuilder();
            sb10.append("");
            sb10.append(c2.get(Calendar.DATE));
            this.aDay = sb10.toString();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_anniversary /*2131296498*/:
                annversaryPickerDialog();
                return;
            case R.id.date_of_birth /*2131296499*/:
                dateOfBirth();
                return;
            case R.id.et_resident /*2131296570*/:
                this.picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                return;
            case R.id.send_data /*2131297041*/:
                           if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != 0) {
                               ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
                           }else {
                               sendData();
                           }

                return;
            case R.id.settings_image_view /*2131297048*/:
                popupShow();
                return;
            default:
                return;
        }
    }

    private void popupShow() {

        View customView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_setting_layout, null);
        this.pop_up_close = (ImageView) customView.findViewById(R.id.pop_up_close);
        this.popupWindow = new PopupWindow(customView, -1, -2);
        this.popupWindow.showAtLocation(this.linearLayout1, 17, 0, 0);
        this.pop_up_close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (CompleteYourProfile.this.popupWindow.isShowing()) {
                    CompleteYourProfile.this.popupWindow.dismiss();
                }
            }
        });
        this.choose_image_iv = (ImageView) customView.findViewById(R.id.choose_image_iv);
        this.choose_image_iv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog dialog;
                AlertDialog.Builder builder= new AlertDialog.Builder(CompleteYourProfile.this);
                builder.setTitle("Do u really want to Change Profile Picture?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CompleteYourProfile.this.dispatchTakePictureIntent();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog=builder.create();
                dialog.show();

                if (CompleteYourProfile.this.popupWindow.isShowing()) {
                    CompleteYourProfile.this.popupWindow.dismiss();
                }
            }
        });
        this.iv_gallery_choose = (ImageView) customView.findViewById(R.id.iv_gallery_choose);
        this.iv_gallery_choose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog dialog;
                AlertDialog.Builder builder= new AlertDialog.Builder(CompleteYourProfile.this);
                builder.setTitle("Do u really want to Change Profile Picture?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CompleteYourProfile.this.requestMedia();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog=builder.create();
                dialog.show();

                if (CompleteYourProfile.this.popupWindow.isShowing()) {
                    CompleteYourProfile.this.popupWindow.dismiss();
                }

            }
        });
        this.delete_profile_image = (ImageView) customView.findViewById(R.id.delete_profile_image);
        if(user_profile_image_view.getDrawable()==null){
            delete_profile_image.setVisibility(View.GONE);
        }
        else {
            delete_profile_image.setVisibility(View.VISIBLE);
        }
        this.delete_profile_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CompleteYourProfile.this.removeprofileimageDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data2) {

        super.onActivityResult(requestCode, resultCode, data2);
        if (requestCode == 1 && resultCode == -1) {
            setPic();
        } else if (requestCode == 301 && resultCode == -1 && data2 != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data2.getData());
                this.bt = bitmap;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                String encoded = Base64.encodeToString(baos.toByteArray(), 0);
                this.imageurl = encoded;
                imageUpdate(encoded, "upload");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void imageUpdate(String encoded,String check) {
        this.progressDialog = new ProgressDialog(this);
        if (check.equals("remove")){
            this.progressDialog.setMessage("Removing..please wait...!!");
        }
        else{
            this.progressDialog.setMessage("Uploading..please wait...!!");
        }

        this.progressDialog.show();
        final String str = encoded;
        StringRequest r1 = new StringRequest(1, CommonUtils.baseUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                CompleteYourProfile.this.progressDialog.dismiss();
                Log.e("result", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("success");
                    String string = jsonObject.getString("message");
                    CompleteYourProfile.this.profile_pic = jsonObject.optString("user_img");
                    String callerId = jsonObject.optString("caller_id");
                    if (CompleteYourProfile.this.popupWindow.isShowing()) {
                        CompleteYourProfile.this.popupWindow.dismiss();
                    }
                    if (!status.equals("true")) {
                        Toast.makeText(CompleteYourProfile.this, "", Toast.LENGTH_SHORT).show();
                    } else if (CompleteYourProfile.this.profile_pic == null || CompleteYourProfile.this.profile_pic.equals("")) {
                        Glide.with((FragmentActivity) CompleteYourProfile.this).setDefaultRequestOptions(requestOptions).load("").into((ImageView) CompleteYourProfile.this.user_profile_image_view);
                        Toast.makeText(CompleteYourProfile.this, "Unable to upload picture please try again.", Toast.LENGTH_SHORT).show();
                    } else {
                        Glide.with((FragmentActivity) CompleteYourProfile.this).setDefaultRequestOptions(requestOptions).load(CompleteYourProfile.this.profile_pic).into((ImageView) CompleteYourProfile.this.user_profile_image_view);
                        String userNickname = SharedPrefManager.getInstance(CompleteYourProfile.this).getUser().getUser_name().toString();
                        String userId = String.valueOf(SharedPrefManager.getInstance(CompleteYourProfile.this).getUser().getUser_id()).replaceAll("\\s", "");
                        StringBuilder sb = new StringBuilder();
                        sb.append(userId);
                        sb.append(" and login user id = ");
                        sb.append(callerId);
                        Log.e("completeplife1", userId+"-"+callerId);

                        if (userId.equalsIgnoreCase(callerId)) {
                            Log.e("completeplife1", userId+"--"+callerId);
                            SendBird.updateCurrentUserInfo(userNickname, CompleteYourProfile.this.profile_pic, new SendBird.UserInfoUpdateHandler() {
                                public void onUpdated(SendBirdException e) {
                                    Log.e("completeplife1", "1"+e);
                                    if (e != null) {
                                        Toast.makeText(CompleteYourProfile.this, "Unable to upload picture please try again.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (check.equals("remove")) {
                                          //  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CompleteYourProfile.this);
                                           // alertDialogBuilder.setMessage((CharSequence) "Profile Picture Removed!");
                                         //   alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                          //      @Override
                                         //       public void onClick(DialogInterface dialog, int which) {
                                         //           alertDialog.dismiss();
                                        //        }
                                       //     });
                                       //     alertDialog = alertDialogBuilder.create();
                                       //     alertDialog.show();
                                        } else {
                                            Toast.makeText(CompleteYourProfile.this, "Picture updated successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(CompleteYourProfile.this, "Picture updated successfully..!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
          //      CompleteYourProfile userDetailsActivity = CompleteYourProfile.this;
           //     StringBuilder sb = new StringBuilder();
           //     sb.append("Some error occurred -> ");
            //    sb.append(volleyError);
             //   Toast.makeText(userDetailsActivity, sb.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("action", "upload_avtar");
                parameters.put("userid", CompleteYourProfile.this.user_id);
                parameters.put("img", str);
                Log.d("strng",""+str);
                return parameters;
            }
        };
        MySingleTon.getInstance(this).addToRequestQue(r1);
    }
    private void setPic() {
        int targetW = this.user_profile_image_view.getWidth();
        int targetH = this.user_profile_image_view.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        //bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(this.mCurrentPhotoPath, bmOptions);
//        int scaleFactor = Math.min(bmOptions.outWidth / targetW, bmOptions.outHeight / targetH);
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(this.mCurrentPhotoPath, bmOptions);
        Bitmap newBit=rotateImage(bitmap,0);
        this.user_profile_image_view.setImageBitmap(null);
        this.user_profile_image_view.destroyDrawingCache();
        Glide.with((FragmentActivity) this).setDefaultRequestOptions(requestOptions).load(newBit).into((ImageView) this.user_profile_image_view);
        this.bt = bitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newBit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String encoded = Base64.encodeToString(baos.toByteArray(), 0);
        this.imageurl = encoded;
        imageUpdate(encoded,"upload");
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
    public void removeprofileimageDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "Remove profile picture ?");
        alertDialogBuilder.setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                CompleteYourProfile.this.alertDialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton((CharSequence) "Remove", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                CompleteYourProfile.this.bt = null;
                CompleteYourProfile.this.imageUpdate("","remove");
                CompleteYourProfile.this.alertDialog.dismiss();

                //Toast.makeText(UserDetailsActivity.this, "Profile Picture removed successfully", Toast.LENGTH_SHORT).show();
            }
        });
        this.alertDialog = alertDialogBuilder.create();
        this.alertDialog.show();
    }
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d("dispatchTake","1"+takePictureIntent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra("output", FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".filepicker.provider", photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            Log.d("dispatchTake","1"+photoFile);

        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = null;
        if (Build.VERSION.SDK_INT >= 24) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("JPEG_");
        sb.append(timeStamp);
        sb.append("_");
        File image = File.createTempFile(sb.toString(), ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        this.mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public void requestMedia() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            requestStoragePermissions();
            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 19) {
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{MimeType.IMAGE_MIME, "video/*"});
        } else {
            intent.setType("image/* video/*");
        }
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Media"), 301);
        SendBird.setAutoBackgroundDetection(false);
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            Snackbar.make((View) this.linearLayout1, (CharSequence) "Storage access permissions are required to upload/download files.", Snackbar.LENGTH_LONG).setAction((CharSequence) "Okay", (View.OnClickListener) new View.OnClickListener() {
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        CompleteYourProfile.this.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 13);
                    }
                }
            }).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 13);
        }
    }
    @Override
    public void onBackPressed() {
        //   super.onBackPressed();
        if(check.equals("1")){
            Toast.makeText(CompleteYourProfile.this,"Press Submit to Complete your signup Process..!!",Toast.LENGTH_LONG).show();
        }else {
            startActivity(new Intent(this, UserDetailsActivity.class));
        }
    }
}
