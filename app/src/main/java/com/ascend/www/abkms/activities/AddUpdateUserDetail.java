package com.ascend.www.abkms.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ascend.www.abkms.NewDashboard;
import com.ascend.www.abkms.R;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.utils.MyValidator;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

import static com.ascend.www.abkms.webservices.RestClient.Development;
import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;

public class AddUpdateUserDetail extends AppCompatActivity {

    ImageView back_btn_toolbar;
    String LanguageSelected = "eng", MemberId = "",StrEditMemberId="";
    ProgressDialog myDialog;
    EditText edt_member_name,edt_father_name,edt_spouse_name,edt_password,edt_member_mobile,edt_alternate_mobile,edt_spouse_mobile,edt_dob,edt_address1,edt_address2,edt_address3,
            edt_pincode,edt_city,edt_state,edt_whatsapp_mobile,edt_member_email,edt_works_with;
    String Str_member_name,Str_father_name,StrMaritalStatus,Str_spouse_name,Str_gender,Str_member_mobile,StrAlternateMobile,Str_spouse_mobile,Str_dob,Str_address1,Str_address2,Str_address3,
            Str_pincode,Str_city,Str_state,StrAge,Str_whatsapp_mobile,Str_member_email,Str_works_with,StrPassword;
    Spinner Spn_MaritalStatus,Spn_Gender;
    DatePickerDialog DobDatePickerDialog;
    String DateOfBirth="",StrFamilyMemberCount;
    Button BtnSubmit;
    LinearLayout LayoutPassword;
    private SimpleDateFormat dateFormatter,dateFormatter1,dateFormatter2,dateFormatter3;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail_add_update);


        init();
    }

    private void init() {

        back_btn_toolbar = (ImageView) findViewById(R.id.back_btn_toolbar);
        back_btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView til_text = (TextView) findViewById(R.id.til_text);
        til_text.setText(getResources().getString(R.string.detail_of_member));
        MemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");

        StrEditMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "EditProfileId");
        if(StrEditMemberId==null){
            StrEditMemberId = "";
        }
        edt_member_name = (EditText) findViewById(R.id.edt_member_name);
        edt_father_name = (EditText) findViewById(R.id.edt_father_name);
        edt_spouse_name = (EditText) findViewById(R.id.edt_spouse_name);
        edt_member_mobile = (EditText) findViewById(R.id.edt_member_mobile);
        edt_alternate_mobile = (EditText) findViewById(R.id.edt_alternate_mobile);
        edt_spouse_mobile = (EditText) findViewById(R.id.edt_spouse_mobile);
        edt_dob = (EditText) findViewById(R.id.edt_dob);
        edt_password = (EditText)findViewById(R.id.edt_password);
        edt_address1 = (EditText) findViewById(R.id.edt_address1);
        edt_address2 = (EditText) findViewById(R.id.edt_address2);
        edt_address3 = (EditText) findViewById(R.id.edt_address3);
        edt_pincode = (EditText) findViewById(R.id.edt_pincode);
        edt_city = (EditText) findViewById(R.id.edt_city);
        edt_state = (EditText) findViewById(R.id.edt_state);
        edt_member_email =  (EditText) findViewById(R.id.edt_member_email);
        edt_works_with  =  (EditText) findViewById(R.id.edt_works_with);
        edt_whatsapp_mobile = (EditText)findViewById(R.id.edt_whatsapp_mobile);

        LayoutPassword = (LinearLayout)findViewById(R.id.LayoutPassword);
        LayoutPassword.setVisibility(View.VISIBLE);
        Spn_MaritalStatus = (Spinner)findViewById(R.id.Spn_MaritalStatus);
        Spn_MaritalStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               StrMaritalStatus = Spn_MaritalStatus.getSelectedItem().toString().trim();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spn_Gender = (Spinner)findViewById(R.id.Spn_Gender);
        Spn_Gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              String StrGender = Spn_Gender.getSelectedItem().toString().trim();
              if(StrGender.equalsIgnoreCase("Male")){
                  Str_gender = "M";
              }else if(StrGender.equalsIgnoreCase("Female")){
                  Str_gender = "F";
              }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edt_dob.setInputType(InputType.TYPE_NULL);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        edt_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*DialogFragment dFragment = new DatePickerFragment();

                // Show the date picker dialog fragment
                dFragment.show(getFragmentManager(), "Date Picker");*/
                DobDatePickerDialog = new DatePickerDialog(AddUpdateUserDetail.this,
                        android.R.style.Theme_Holo_Light_Dialog,new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;

                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

                        String dob  = year +"-"+ (monthOfYear + 1) + "-" + dayOfMonth ;
                        StrAge= String.valueOf(CommonMethods.getAge(year,monthOfYear+1,dayOfMonth));
                        Date date = null;
                        try {
                            date = inputFormat.parse(dob);
                            DateOfBirth = outputFormat.format(date);

                            edt_dob.setText(DateOfBirth);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }



                    }
                }, mYear, mMonth+1, -1);

                DobDatePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                DobDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                //((ViewGroup) datePickerDialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);

                DobDatePickerDialog.show();
            }
        });


        edt_pincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==6){

                    String StrPincode = s.toString();
                    Log.d("StrPincode",StrPincode);
                    ApiToGetCityState(StrPincode);


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        myDialog = new ProgressDialog(AddUpdateUserDetail.this);
        myDialog.setMessage(getResources().getString(R.string.please_wait));
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(true);

        if (StrEditMemberId != null && !StrEditMemberId.equalsIgnoreCase("") && !StrEditMemberId.equalsIgnoreCase("null")) {
            GetMemberDetail();
        }

        BtnSubmit = (Button)findViewById(R.id.BtnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidFields()){
                    API_ADD_UPDATE_USER_INFO();

                }
            }
        });
    }

    private void API_ADD_UPDATE_USER_INFO() {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_whatsapp_mobile.getWindowToken(), 0);
        Str_member_name = edt_member_name.getText().toString();
        Str_father_name = edt_father_name.getText().toString();
        Str_spouse_name = edt_spouse_name.getText().toString();
        Str_member_mobile = edt_member_mobile.getText().toString();
        StrAlternateMobile = edt_alternate_mobile.getText().toString();
        Str_spouse_mobile = edt_spouse_mobile.getText().toString();

        if(LayoutPassword!=null && LayoutPassword.getVisibility()==View.VISIBLE) {
            String EnteredPassword = edt_password.getText().toString();
            StrPassword = CommonMethods.md5(EnteredPassword);
        }
        DateOfBirth = edt_dob.getText().toString();
        Str_address1 = edt_address1.getText().toString();
        Str_address2 = edt_address2.getText().toString();
        Str_address3 = edt_address3.getText().toString();
        Str_pincode = edt_pincode.getText().toString();
        Str_city = edt_city.getText().toString();
        Str_state = edt_state.getText().toString();
        Str_works_with = edt_works_with.getText().toString();
        String StrGender = Spn_Gender.getSelectedItem().toString().trim();
        if(StrGender.equalsIgnoreCase("Male")){
            Str_gender = "M";
        }else if(StrGender.equalsIgnoreCase("Female")){
            Str_gender = "F";
        }

        Str_member_email = edt_member_email.getText().toString();
        Str_whatsapp_mobile = edt_whatsapp_mobile.getText().toString();
        myDialog.show();

        String Uiid_id = UUID.randomUUID().toString();


        String URL_user_info = ROOT_URL + "addupdateuserinfo.php?_" + Uiid_id;
        try {
            Log.d("URL_USerInfo",URL_user_info);

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_user_info,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                 Log.d("mainResponse", response);

                                try {

                                    JSONObject jObj = new JSONObject(response);

                                    boolean status = jObj.getBoolean("status");

                                    if (status) {

                                        Toasty.success(getApplicationContext(),"Profile Updated Successfully.",Toast.LENGTH_LONG).show();
                                        onBackPressed();


                                    }else {
                                        Toast.makeText(getApplicationContext(), "Unable to save data Plz try again", Toast.LENGTH_SHORT).show();
                                    }


                                } catch (JSONException e) {
                                    if (myDialog != null && myDialog.isShowing()) {
                                        myDialog.dismiss();
                                    }
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                        VolleyLog.d("volley", "Error: " + error.getMessage());
                        error.printStackTrace();

                    }
                }) {


                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("member_id", StrEditMemberId);
                        params.put("member_name", Str_member_name);
                        params.put("father_name", Str_father_name);
                        params.put("marital_status",StrMaritalStatus);
                        params.put("spouse_name", Str_spouse_name);
                        params.put("member_mobile",Str_member_mobile);
                        params.put("alternate_mobile",StrAlternateMobile);
                        params.put("spouse_mobile",Str_spouse_mobile);
                        params.put("password",StrPassword);
                        params.put("gender",Str_gender);
                        params.put("date_of_birth",DateOfBirth);
                        params.put("age",StrAge);
                        params.put("address1", Str_address1);
                        params.put("address2",Str_address2);
                        params.put("address3",Str_address3);
                        params.put("pincode",Str_pincode);
                        params.put("city",Str_city);
                        params.put("state",Str_state);
                        params.put("member_works_with",Str_works_with);
                        params.put("member_email",Str_member_email);
                        params.put("member_whatsapp",Str_whatsapp_mobile);
                        Log.d("ParrasUserRegi", params.toString());
                        return params;


                        /*  = edt_member_name.getText().toString();
         = edt_father_name.getText().toString();
         = edt_spouse_name.getText().toString();
         = edt_member_mobile.getText().toString();
         = edt_spouse_mobile.getText().toString();
         = edt_address1.getText().toString();
         = edt_address2.getText().toString();
        Str_address2 = edt_address3.getText().toString();
         = edt_pincode.getText().toString();
         = edt_city.getText().toString();
         = edt_state.getText().toString();
         = edt_works_with.getText().toString();
        String StrGender = Spn_Gender.getSelectedItem().toString().trim();
        if(StrGender.equalsIgnoreCase("Male")){
             = "M";
        }else if(StrGender.equalsIgnoreCase("Female")){
            Str_gender = "F";
        }

         = edt_member_email.getText().toString();
         = edt_whatsapp_mobile.getText().toString();*/

                    }
                };

                int socketTimeout = 50000; //30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);

            } else {
                if(myDialog!=null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
                CommonMethods.DisplayToast(getApplicationContext(), "Please check your internet connection");
            }
        } catch (Exception e) {

            e.printStackTrace();

        }



    }

    private boolean isValidFields() {
        boolean result = true;

        if (!MyValidator.isValidName(edt_member_name)) {
            edt_member_name.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Member Full Name");
            result = false;
        }

        if (!MyValidator.isValidName(edt_father_name)) {
            edt_father_name.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Father / Mother Name");
            result = false;
        }

        if (!MyValidator.isValidSpinner(Spn_MaritalStatus)) {
            Spn_MaritalStatus.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Select Marital Status");

            result = false;
        }

        if (!MyValidator.isValidMobile(edt_member_mobile)) {
            edt_member_mobile.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Member Mobile");
            result = false;
        }


        if (!MyValidator.isValidSpinner(Spn_Gender)) {
            Spn_Gender.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Select Gender");

            result = false;
        }

        if (!MyValidator.isValidField(edt_works_with)) {
            edt_works_with.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Place where Member Works with");

            result = false;
        }

        if (!MyValidator.isValidField(edt_address1)) {
            edt_address1.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Address 1");

            result = false;
        }

        if (!MyValidator.isValidField(edt_address2)) {
            edt_address2.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Address 2");

            result = false;
        }

        if (!MyValidator.isValidField(edt_pincode)) {
            edt_pincode.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Pincode");

            result = false;
        }

        if (!MyValidator.isValidField(edt_city)) {
            edt_city.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter City");

            result = false;
        }

        if (!MyValidator.isValidField(edt_state)) {
            edt_state.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter State");

            result = false;
        }

        if(LayoutPassword!=null && LayoutPassword.getVisibility()==View.VISIBLE){
            if (!MyValidator.isValidPassword(edt_password)) {
                edt_password.requestFocus();
                CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Password");

                result = false;
            }


        }

        if(edt_alternate_mobile!=null && edt_alternate_mobile.getText().toString().length()>0){
            if (!MyValidator.isValidMobile(edt_alternate_mobile)) {
                edt_alternate_mobile.requestFocus();
                CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Valid Mobile");
                result = false;
            }
        }
        return  result;
    }

    private void ApiToGetCityState(String strPincode) {

        String URL_Check_Pincode = Development+"getCityStateForPincode?pincode="+strPincode;

        try {
            Log.d("URL",URL_Check_Pincode);

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_Check_Pincode,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response",response);
                                try {
                                    JSONObject jobj = new JSONObject(response);
                                    int status = jobj.getInt("status");
                                    if(status==200) {
                                        JSONObject pincodeObject = jobj.getJSONObject("pincodeObject");

                                        String city = pincodeObject.getString("city");
                                        String state = pincodeObject.getString("state");


                                        if (city != null && !city.equalsIgnoreCase("")) {
                                            edt_city.setText(city);
                                        } else {
                                            edt_city.setText("");
                                        }

                                        if (state != null && !state.equalsIgnoreCase("")) {
                                            edt_state.setText(state);
                                        } else {
                                            edt_state.setText("");
                                        }
                                    }
                                } catch (Exception e) {

                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("volley", "Error: " + error.getMessage());
                        error.printStackTrace();

                    }
                });

                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);



            } else {
                CommonMethods.DisplayToast(this, "Please check your internet connection");
            }
        } catch (Exception e) {

            e.printStackTrace();

        }


    }

    private void GetMemberDetail() {

        myDialog.show();

        String Uiid_id = UUID.randomUUID().toString();

        String VoterDetailUrl = ROOT_URL + "memberdetail.php?_" + Uiid_id + "&lang=" + LanguageSelected + "&id="+StrEditMemberId;
        Log.d("URL", "--> " + VoterDetailUrl);
        StringRequest postrequest = new StringRequest(Request.Method.GET, VoterDetailUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    Log.d("URL Response", "--> " + response);

                    JSONObject jsonresponse = new JSONObject(response);
                    boolean status = jsonresponse.getBoolean("status");
                    if (status) {
                        JSONObject resultObj = jsonresponse.getJSONObject("result");
                        String ID = resultObj.getString("ID");
                         Str_member_name = resultObj.getString("NAME");
                        Str_father_name = resultObj.getString("FATHER_NAME");
                        StrMaritalStatus = resultObj.getString("MARITAL_STATUS");
                        Str_spouse_name = resultObj.getString("SPOUSE_NAME");
                        Str_works_with = resultObj.getString("WORKS_WITH");
                        StrPassword = resultObj.getString("PASSWORD");
                        String IS_PASSWORD_SET = resultObj.getString("IS_PASSWORD_SET");

                        Str_gender = resultObj.getString("GENDER");
                        StrAge= resultObj.getString("AGE");
                        DateOfBirth = resultObj.getString("DOB");
                        Str_member_email = resultObj.getString("EMAIL_ID");
                        StrFamilyMemberCount = resultObj.getString("FAMILY_MEMBER_COUNT");
                        Str_address1 = resultObj.getString("ADDRESS1");
                        Str_address2 = resultObj.getString("ADDRESS2");
                        Str_address3 = resultObj.getString("ADDRESS3");
                        Str_city = resultObj.getString("CITY");
                        Str_state = resultObj.getString("STATE");
                        Str_pincode = resultObj.getString("PINCODE");
                        Str_member_mobile = resultObj.getString("MOBILE_NO");
                        StrAlternateMobile = resultObj.getString("MOBILE_NO1");
                        Str_spouse_mobile = resultObj.getString("SPOUSE_MOBILE_NO");
                        Str_whatsapp_mobile = resultObj.getString("WHATSAPP_NO");

                        if((StrPassword!=null && !StrPassword.equalsIgnoreCase("")  && !StrPassword.equalsIgnoreCase("null"))||(IS_PASSWORD_SET!=null && IS_PASSWORD_SET.equalsIgnoreCase("1"))){
                            LayoutPassword.setVisibility(View.GONE);
                        }else if(IS_PASSWORD_SET!=null && IS_PASSWORD_SET.equalsIgnoreCase("0")){
                            LayoutPassword.setVisibility(View.VISIBLE);
                        }else {
                            LayoutPassword.setVisibility(View.VISIBLE);
                        }


                        if(Str_member_name!=null && !Str_member_name.equalsIgnoreCase("") && !Str_member_name.equalsIgnoreCase("null")){
                            edt_member_name.setText(Str_member_name.toUpperCase());

                        }else {
                            edt_member_name.setText("");
                        }

                        if(Str_father_name!=null && !Str_father_name.equalsIgnoreCase("") && !Str_father_name.equalsIgnoreCase("null")){
                            edt_father_name.setText(Str_father_name.toUpperCase());

                        }else {
                            edt_father_name.setText("");
                        }

                        if(Str_spouse_name!=null && !Str_spouse_name.equalsIgnoreCase("") && !Str_spouse_name.equalsIgnoreCase("null")){
                            edt_spouse_name.setText(Str_spouse_name.toUpperCase());

                        }else {
                            edt_spouse_name.setText("");
                        }

                        if(Str_member_mobile!=null && !Str_member_mobile.equalsIgnoreCase("") && !Str_member_mobile.equalsIgnoreCase("null")){
                            edt_member_mobile.setText(Str_member_mobile);

                        }else {
                            edt_member_mobile.setText("");
                        }

                        if(Str_spouse_mobile!=null && !Str_spouse_mobile.equalsIgnoreCase("") && !Str_spouse_mobile.equalsIgnoreCase("null")){
                            edt_spouse_mobile.setText(Str_spouse_mobile);

                        }else {
                            edt_spouse_mobile.setText("");
                        }

                        if(StrAlternateMobile!=null && !StrAlternateMobile.equalsIgnoreCase("") && !StrAlternateMobile.equalsIgnoreCase("null")){
                            edt_alternate_mobile.setText(StrAlternateMobile);

                        }else {
                            edt_alternate_mobile.setText("");
                        }

                        if(DateOfBirth!=null && !DateOfBirth.equalsIgnoreCase("") && !DateOfBirth.equalsIgnoreCase("null")){
                            edt_dob.setText(DateOfBirth);
                        }else {
                            edt_dob.setText("");
                        }

                        if(Str_works_with!=null && !Str_works_with.equalsIgnoreCase("") && !Str_works_with.equalsIgnoreCase("null")){
                            edt_works_with.setText(Str_works_with);

                        }else {
                            edt_works_with.setText("");
                        }

                        if(Str_address1!=null && !Str_address1.equalsIgnoreCase("") && !Str_address1.equalsIgnoreCase("null")){
                            edt_address1.setText(Str_address1);

                        }else {
                            edt_address1.setText("");
                        }

                        if(Str_address2!=null && !Str_address2.equalsIgnoreCase("") && !Str_address2.equalsIgnoreCase("null")){
                            edt_address2.setText(Str_address2);

                        }else {
                            edt_address2.setText("");
                        }

                        if(Str_address3!=null && !Str_address3.equalsIgnoreCase("") && !Str_address3.equalsIgnoreCase("null")){
                            edt_address3.setText(Str_address3);

                        }else {
                            edt_address3.setText("");
                        }

                        if(Str_city!=null && !Str_city.equalsIgnoreCase("") && !Str_city.equalsIgnoreCase("null")){
                            edt_city.setText(Str_city);

                        }else {
                            edt_city.setText("");
                        }

                        if(Str_state!=null && !Str_state.equalsIgnoreCase("") && !Str_state.equalsIgnoreCase("null")){
                            edt_state.setText(Str_state);

                        }else {
                            edt_state.setText("");
                        }

                        if(Str_pincode!=null && !Str_pincode.equalsIgnoreCase("") && !Str_pincode.equalsIgnoreCase("null")){
                            edt_pincode.setText(Str_pincode);
                            ApiToGetCityState(Str_pincode);
                        }else {
                            edt_pincode.setText("");
                        }

                        if(StrMaritalStatus!=null && !StrMaritalStatus.equalsIgnoreCase("") && !StrMaritalStatus.equalsIgnoreCase("null")){
                            int i = getIndex(Spn_MaritalStatus, StrMaritalStatus);
                            Spn_MaritalStatus.setSelection(i);
                        }

                        if(Str_gender!=null && !Str_gender.equalsIgnoreCase("") && !Str_gender.equalsIgnoreCase("null")) {
                            if (Str_gender.equalsIgnoreCase("F")) {
                                String Gender = "FEMALE";
                                if (Gender != null && !Gender.equalsIgnoreCase("") && !Gender.equalsIgnoreCase("null")) {
                                    int i = getIndex(Spn_Gender, Gender);
                                    Spn_Gender.setSelection(i);
                                }
                            } else if (Str_gender.equalsIgnoreCase("M")) {
                                String Gender = "MALE";
                                if (Gender != null && !Gender.equalsIgnoreCase("") && !Gender.equalsIgnoreCase("null")) {
                                    int i = getIndex(Spn_Gender, Gender);
                                    Spn_Gender.setSelection(i);
                                }
                            }
                        }

                        if(Str_whatsapp_mobile!=null && !Str_whatsapp_mobile.equalsIgnoreCase("") && !Str_whatsapp_mobile.equalsIgnoreCase("null")){
                            edt_whatsapp_mobile.setText(Str_whatsapp_mobile);
                        }else {
                            edt_whatsapp_mobile.setText("");
                        }

                        if(Str_member_email!=null && !Str_member_email.equalsIgnoreCase("") && !Str_member_email.equalsIgnoreCase("null")){
                            edt_member_email.setText(Str_member_email);
                        }else {
                            edt_member_email.setText("");
                        }



                    }



                } catch (JSONException e) {
                    if(myDialog!=null && myDialog.isShowing()){
                        myDialog.dismiss();
                    }
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(myDialog!=null && myDialog.isShowing()){
                    myDialog.dismiss();
                }
                volleyError.printStackTrace();
                CommonMethods.DisplayToastWarning(getApplicationContext(), "Something goes wrong. Please try again");
                //Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });
        int socketTimeout = 50000; //30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postrequest.setRetryPolicy(policy);
        // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(postrequest);
    }

    private int getIndex(Spinner spinner, String searchString) {

        if (searchString == null || spinner.getCount() == 0) {

            return -1; // Not found

        } else {

            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(searchString)) {
                    return i; // Found!
                }
            }

            return -1; // Not found
        }
    }

    @Override
    public void onBackPressed() {

        if(MemberId!=null && !MemberId.equalsIgnoreCase("")) {
            Intent intent = new Intent(getApplicationContext(), NewDashboard.class);
            intent.putExtra("lang_flag", LanguageSelected);
            startActivity(intent);
            overridePendingTransition(R.animator.left_right, R.animator.right_left);
            finish();
        }else {
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.left_right, R.animator.right_left);
            finish();
        }
    }
}
