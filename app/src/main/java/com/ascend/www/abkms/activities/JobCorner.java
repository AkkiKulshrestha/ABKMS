package com.ascend.www.abkms.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ascend.www.abkms.NewDashboard;
import com.ascend.www.abkms.R;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;

public class JobCorner extends AppCompatActivity {

    ImageView back_btn_toolbar;
    TextView til_text;
    String LanguageSelected;
    LinearLayout ParentLayoutJobList;
    ProgressDialog myDialog;
    String IS_ADMIN;
    Button BtnAddJob;
    Dialog DialogAddJob;
    EditText Edt_JobDate;
    String StrMemberId="",StrJobTitle="",StrJobDescription="",StrJobLocation="",StrExperienceRequired="",StrSalaryBracket="";
    private DatePickerDialog dobDatePickerDialog;
    private SimpleDateFormat dateFormatter, dateFormatter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        init();
    }

    private void init() {
        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");

        IS_ADMIN = UtilitySharedPreferences.getPrefs(getApplicationContext(),"IsAdmin");
        dateFormatter1 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        back_btn_toolbar = (ImageView) findViewById(R.id.back_btn_toolbar);
        back_btn_toolbar.setVisibility(View.VISIBLE);
        back_btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        til_text = (TextView) findViewById(R.id.til_text);
        til_text.setText(getResources().getString(R.string.jobs));
        LanguageSelected = UtilitySharedPreferences.getPrefs(getApplicationContext(),"LanguageSelected");

        ParentLayoutJobList = (LinearLayout)findViewById(R.id.ParentLayoutJobList);
        myDialog = new ProgressDialog(JobCorner.this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        BtnAddJob = (Button)findViewById(R.id.BtnAddJob);

        BtnAddJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpToAddJob();
            }
        });

        fetchJobList();

    }



    private void showPopUpToAddJob() {

        DialogAddJob = new Dialog(JobCorner.this);
        DialogAddJob.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogAddJob.setCanceledOnTouchOutside(false);
        DialogAddJob.setCancelable(false);
        DialogAddJob.setContentView(R.layout.layout_dailog_add_job);
        Objects.requireNonNull(DialogAddJob.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView)DialogAddJob.findViewById(R.id.title) ;
        ImageView iv_close = (ImageView)DialogAddJob.findViewById(R.id.iv_close);

        EditText EdtJobTitle = (EditText)DialogAddJob.findViewById(R.id.EdtJobTitle);
        EditText EdtJobDescription = (EditText)DialogAddJob.findViewById(R.id.EdtJobDescription);
        EditText EdtJobLocation = (EditText)DialogAddJob.findViewById(R.id.EdtJobLocation);
        EditText EdtExperienceRequired = (EditText)DialogAddJob.findViewById(R.id.EdtExperienceRequired);
        EditText EdtExpectedSalary = (EditText)DialogAddJob.findViewById(R.id.EdtExpectedSalary);

        Button Btn_submit = (Button)DialogAddJob.findViewById(R.id.Btn_submit);


        DialogAddJob.show();
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DialogAddJob!=null && DialogAddJob.isShowing()) {
                    DialogAddJob.dismiss();
                }
            }
        });



        Btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrJobTitle = EdtJobTitle.getText().toString();
                StrJobDescription = EdtJobDescription.getText().toString();
                StrJobLocation = EdtJobLocation.getText().toString();
                StrExperienceRequired = EdtExperienceRequired.getText().toString();
                StrSalaryBracket = EdtExpectedSalary.getText().toString();

                if(StrJobTitle!=null && !StrJobTitle.equalsIgnoreCase("")) {

                    if(StrJobDescription!=null && !StrJobDescription.equalsIgnoreCase("")) {

                        if(StrJobLocation!=null && !StrJobLocation.equalsIgnoreCase("")) {

                            if(StrExperienceRequired!=null && !StrExperienceRequired.equalsIgnoreCase("")) {

                                API_SAVE_NEW_JOB();

                            }else {
                                CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Experience Required in Years.");
                            }


                        }else {
                            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Job Location");
                        }

                    }else {
                        CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Job Description");
                    }

                }else {
                    CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Job Title");
                }

            }
        });

    }



    private void API_SAVE_NEW_JOB() {

        String Uiid_id = UUID.randomUUID().toString();
        String URL_ADD_DONOR_DETAIL = ROOT_URL + "add_new_job.php?_"+Uiid_id;
        try {
            Log.d("URL_FetchDetails",URL_ADD_DONOR_DETAIL);

            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_DONOR_DETAIL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                try {
                                    Log.d("mainResponse", response);

                                    JSONObject jobj = new JSONObject(response);
                                    boolean status = jobj.getBoolean("status");

                                    if (status) {
                                        String message = jobj.getString("message");
                                        if(DialogAddJob!=null && DialogAddJob.isShowing()) {
                                            DialogAddJob.dismiss();
                                        }

                                        fetchJobList();

                                    }else {
                                        String message = jobj.getString("message");

                                        CommonMethods.DisplayToastWarning(getApplicationContext(),message);
                                    }

                                }catch (Exception e ){
                                    e.printStackTrace();
                                    if(myDialog!=null && myDialog.isShowing()){
                                        myDialog.dismiss();
                                    }
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
                }){

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("job_title",StrJobTitle);
                        params.put("job_description",StrJobDescription);
                        params.put("job_location",StrJobLocation);
                        params.put("experience_required",StrExperienceRequired);
                        params.put("salary_range",StrSalaryBracket);
                        params.put("created_by",StrMemberId);

                        Log.d("ParrasOtpdata",params.toString() );

                        return params;
                    }



                } ;

                int socketTimeout = 50000; //30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

            } else {
                if(myDialog!=null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
                CommonMethods.DisplayToast(this, "Please check your internet connection");
            }
        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    private void fetchJobList() {

        myDialog.show();

        if(ParentLayoutJobList!=null && ParentLayoutJobList.getChildCount()>0){
            ParentLayoutJobList.removeAllViews();
        }
        String Uiid_id = UUID.randomUUID().toString();

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            String URL = ROOT_URL+"job_list.php?_" + Uiid_id;
            Log.d("JobList:",""+URL);
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d("TAG", "responsedata:"+response);
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }

                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                final boolean status = jsonObj.getBoolean("status");
                                String message = jsonObj.getString("message");
                                if(status) {
                                    JSONArray resultArray = jsonObj.getJSONArray("result");
                                    if (resultArray.length() > 0) {


                                        for (int i = 0; i < resultArray.length(); i++) {


                                            JSONObject jsonObject = resultArray.getJSONObject(i);

                                            String ID = jsonObject.getString("id");
                                            final String job_title = jsonObject.getString("job_title");
                                            final String job_description = jsonObject.getString("job_description");
                                            final String job_location = jsonObject.getString("job_location");
                                            final String job_experience_required = jsonObject.getString("job_experience_required");
                                            String expected_salary = jsonObject.getString("expected_salary");
                                            String created_by = jsonObject.getString("created_by");
                                            String job_created_name = jsonObject.getString("job_created_name");
                                            String job_creator_mobile = jsonObject.getString("job_creator_mobile");

                                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            final View rowView = inflater.inflate(R.layout.job_layout_list, null);

                                            TextView row_id = (TextView)rowView.findViewById(R.id.row_id);
                                            TextView row_job_title = (TextView)rowView.findViewById(R.id.row_job_title);
                                            TextView row_job_location = (TextView)rowView.findViewById(R.id.row_job_location);
                                            final TextView row_job_description = (TextView) rowView.findViewById(R.id.row_job_description);
                                            final TextView row_salary = (TextView) rowView.findViewById(R.id.row_salary);
                                            final TextView row_experience_required = (TextView) rowView.findViewById(R.id.row_experience_required);
                                            final TextView tv_PostedBy = (TextView) rowView.findViewById(R.id.tv_PostedBy);
                                            final TextView tv_ContactNo = (TextView) rowView.findViewById(R.id.tv_ContactNo);


                                            if(ID!=null && !ID.equalsIgnoreCase("null") && !ID.equalsIgnoreCase("")) {
                                                row_id.setText(ID);
                                            }


                                            if(job_title!=null && !job_title.equalsIgnoreCase("null") && !job_title.equalsIgnoreCase("")) {
                                                row_job_title.setText(job_title);
                                            }

                                            if(job_location!=null && !job_location.equalsIgnoreCase("null") && !job_location.equalsIgnoreCase("")) {
                                                row_job_location.setText(job_location);
                                            }



                                            if(job_experience_required!=null && !job_experience_required.equalsIgnoreCase("null") && !job_experience_required.equalsIgnoreCase("")) {
                                                row_experience_required.setText("Min - "+job_experience_required + " Years");
                                            }

                                            if(job_description!=null && !job_description.equalsIgnoreCase("null") && !job_description.equalsIgnoreCase("")) {
                                                row_job_description.setText(job_description);
                                            }


                                            if(expected_salary!=null && !expected_salary.equalsIgnoreCase("null") && !expected_salary.equalsIgnoreCase("")) {
                                                row_salary.setText(expected_salary + " per month");
                                            }

                                            if(job_created_name!=null && !job_created_name.equalsIgnoreCase("null") && !job_created_name.equalsIgnoreCase("")) {
                                                tv_PostedBy.setText(job_created_name);
                                            }

                                            if(job_creator_mobile!=null && !job_creator_mobile.equalsIgnoreCase("null") && !job_creator_mobile.equalsIgnoreCase("")) {
                                                tv_ContactNo.setText(job_creator_mobile);
                                                Linkify.addLinks(tv_ContactNo, Linkify.PHONE_NUMBERS);

                                            }

                                            ImageView iv_delete = (ImageView)rowView.findViewById(R.id.iv_delete);
                                            if(IS_ADMIN!=null && !IS_ADMIN.equalsIgnoreCase("") && !IS_ADMIN.equalsIgnoreCase("null")){
                                                if(IS_ADMIN.equalsIgnoreCase("1")){
                                                    iv_delete.setVisibility(View.VISIBLE);
                                                }else {
                                                    if(created_by!=null && !created_by.equalsIgnoreCase("")){
                                                        if(created_by.equalsIgnoreCase(StrMemberId)){
                                                            iv_delete.setVisibility(View.VISIBLE);
                                                        }
                                                    }else {
                                                        iv_delete.setVisibility(View.GONE);
                                                    }

                                                }

                                            }
                                            iv_delete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(JobCorner.this);
                                                    builder.setTitle("Delete !!!");
                                                    builder.setMessage("Are you sure you want to delete this entry ?");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            DeleteJobDetails(row_id.getText().toString());
                                                        }
                                                    });

                                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Toast.makeText(getApplicationContext(), "You've changed your mind to delete all records", Toast.LENGTH_SHORT).show();

                                                        }
                                                    });

                                                    builder.show();


                                                }
                                            });



                                            ParentLayoutJobList .addView(rowView);
                                        }

                                    } else {
                                        TextView tv_no_data = new TextView(getApplicationContext());
                                        tv_no_data.setText("No Jobs Available");
                                        tv_no_data.setPadding(10,10,10,10);
                                        ParentLayoutJobList.addView(tv_no_data);
                                        //Toast.makeText(getApplicationContext(), "Sorry No data is available", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    TextView tv_no_data = new TextView(getApplicationContext());
                                    tv_no_data.setText("No Jobs Available");
                                    tv_no_data.setPadding(10,10,10,10);
                                    ParentLayoutJobList.addView(tv_no_data);
                                    //Toast.makeText(getApplicationContext(), "Sorry No data is available", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                TextView tv_no_data = new TextView(getApplicationContext());
                                tv_no_data.setText("No Jobs Available");
                                tv_no_data.setPadding(10,10,10,10);
                                ParentLayoutJobList.addView(tv_no_data);
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }

                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.statusCode == 404) {
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            // Now you can use any deserializer to make sense of data
                            JSONObject obj = new JSONObject(res);
                            final boolean success = obj.getBoolean("success");
                            String message = obj.getString("message");
                            if(!success){
                                TextView tv_no_data = new TextView(getApplicationContext());
                                tv_no_data.setText("No Jobs Available");
                                tv_no_data.setPadding(10,10,10,10);
                                ParentLayoutJobList.addView(tv_no_data);
                            }

                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        } catch (JSONException e2) {
                            // returned data is not JSONObject?
                            e2.printStackTrace();
                        }
                    }


                    //Toast.makeText(getApplicationContext(), "Some Error occurred by fetching data. Please try again later.", Toast.LENGTH_SHORT).show();

                    error.printStackTrace();

                }
            });

            int socketTimeout = 50000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);

        } else {
            if (myDialog != null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
            CommonMethods.DisplayToast(getApplicationContext(), "Please check your internet connection");
        }

    }


    private void DeleteJobDetails(String jobId) {

        Log.d("DeleteID",""+jobId);
        String Uiid_id = UUID.randomUUID().toString();

        String API_DeleteDonationInfo = ROOT_URL + "delete_job.php?_" + Uiid_id;
        try {

            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();



            if (isInternetPresent) {
                Log.d("URL",API_DeleteDonationInfo);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, API_DeleteDonationInfo,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response",response);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean status = jsonObject.getBoolean("status");
                                    if(status){
                                        fetchJobList();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }){

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        //params.put("Pan", StrClientPan);
                        params.put("jobId", jobId);
                        Log.d("ParrasOtpdata",params.toString() );

                        return params;
                    }



                };

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


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), NewDashboard.class);
        intent.putExtra("lang_flag", LanguageSelected);
        startActivity(intent);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }


}