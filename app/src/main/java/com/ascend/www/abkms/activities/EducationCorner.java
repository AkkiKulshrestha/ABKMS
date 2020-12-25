package com.ascend.www.abkms.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.Spinner;
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
import com.ascend.www.abkms.utils.MyValidator;
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

import static com.ascend.www.abkms.utils.CommonMethods.ucFirst;
import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;

public class EducationCorner extends AppCompatActivity  {


    ImageView back_btn_toolbar;
    TextView til_text;
    String LanguageSelected;
    LinearLayout ParentLayoutEducationList;
    ProgressDialog myDialog;
    String IS_ADMIN;
    Button BtnAddEducation;
    Dialog DialogAddEducation;
    EditText Edt_JobDate;
    String StrMemberId = "", StrEducationTitle = "", StrEducationDescription = "", StrEducationURL = "",StrEducationType ="",StrContactNo="";
    private DatePickerDialog dobDatePickerDialog;
    private SimpleDateFormat dateFormatter, dateFormatter1;
    EditText EdtEducationTitle,EdtEducationDescription,EdtEducationURL,EdtContactNo;
    Spinner Spn_EducationType;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_list);

        init();
    }

    private void init () {
        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");

        IS_ADMIN = UtilitySharedPreferences.getPrefs(getApplicationContext(), "IsAdmin");
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
        til_text.setText(getResources().getString(R.string.education));
        LanguageSelected = UtilitySharedPreferences.getPrefs(getApplicationContext(), "LanguageSelected");

        ParentLayoutEducationList = (LinearLayout) findViewById(R.id.ParentLayoutEducationList);
        myDialog = new ProgressDialog(EducationCorner.this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        BtnAddEducation = (Button) findViewById(R.id.BtnAddEducation);

        BtnAddEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpToAddProfile();
            }
        });

        fetchEducationList();

    }


    private void showPopUpToAddProfile () {

        DialogAddEducation = new Dialog(EducationCorner.this);
        DialogAddEducation.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogAddEducation.setCanceledOnTouchOutside(false);
        DialogAddEducation.setCancelable(false);
        DialogAddEducation.setContentView(R.layout.layout_dailog_add_education_detail);
        Objects.requireNonNull(DialogAddEducation.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) DialogAddEducation.findViewById(R.id.title);
        ImageView iv_close = (ImageView) DialogAddEducation.findViewById(R.id.iv_close);

        Spn_EducationType = (Spinner) DialogAddEducation.findViewById(R.id.Spn_EducationType);
        EdtEducationTitle = (EditText) DialogAddEducation.findViewById(R.id.EdtEducationTitle);
        EdtEducationDescription = (EditText) DialogAddEducation.findViewById(R.id.EdtEducationDescription);
        EdtEducationURL = (EditText) DialogAddEducation.findViewById(R.id.EdtEducationURL);
        EdtContactNo = (EditText) DialogAddEducation.findViewById(R.id.EdtContactNo);

        Button Btn_submit = (Button) DialogAddEducation.findViewById(R.id.Btn_submit);


        DialogAddEducation.show();
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DialogAddEducation != null && DialogAddEducation.isShowing()) {
                    DialogAddEducation.dismiss();
                }
            }
        });


        Btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isValidFields()) {

                    StrEducationTitle = EdtEducationTitle.getText().toString();
                    StrEducationDescription = EdtEducationDescription.getText().toString();
                    StrEducationURL = EdtEducationURL.getText().toString();
                    StrEducationType = Spn_EducationType.getSelectedItem().toString().toLowerCase();
                    StrContactNo = EdtContactNo.getText().toString();
                    API_SAVE_NEW_EDUCATION_DETAIL();

                }

            }
        });

    }




    private boolean isValidFields() {
        boolean result = true;

        if (!MyValidator.isValidField(EdtEducationTitle)) {
            EdtEducationTitle.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Valid Title");
            result = false;
        }

        if (!MyValidator.isValidField(EdtEducationDescription)) {
            EdtEducationDescription.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Valid Description");
            result = false;
        }

        if (!MyValidator.isValidSpinner(Spn_EducationType)) {
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Select Education Type");
            result = false;
        }
        if (!MyValidator.isValidMobile(EdtContactNo)) {
            EdtContactNo.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Contact No.");
            result = false;
        }




        return  result;
    }


    private void API_SAVE_NEW_EDUCATION_DETAIL () {

        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();
        String URL_ADD_DONOR_DETAIL = ROOT_URL + "add_education_detail.php?_" + Uiid_id;
        try {
            Log.d("URL_FetchDetails", URL_ADD_DONOR_DETAIL);

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
                                        if (DialogAddEducation != null && DialogAddEducation.isShowing()) {
                                            DialogAddEducation.dismiss();
                                        }

                                        fetchEducationList();

                                    } else {
                                        String message = jobj.getString("message");

                                        CommonMethods.DisplayToastWarning(getApplicationContext(), message);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (myDialog != null && myDialog.isShowing()) {
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
                }) {

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("education_type", StrEducationType);
                        params.put("education_title", StrEducationTitle);
                        params.put("education_description", StrEducationDescription);
                        params.put("education_url", StrEducationURL);
                        params.put("contact_no", StrContactNo);
                        params.put("created_by", StrMemberId);

                        Log.d("ParrasOtpdata", params.toString());

                        return params;
                    }


                };

                int socketTimeout = 50000; //30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

            } else {
                if (myDialog != null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
                CommonMethods.DisplayToast(this, "Please check your internet connection");
            }
        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    private void fetchEducationList () {

        myDialog.show();

        if (ParentLayoutEducationList != null && ParentLayoutEducationList.getChildCount() > 0) {
            ParentLayoutEducationList.removeAllViews();
        }
        String Uiid_id = UUID.randomUUID().toString();

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            String URL = ROOT_URL + "education_list.php?_" + Uiid_id;
            Log.d("JobList:", "" + URL);
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d("TAG", "responsedata:" + response);
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }

                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                final boolean status = jsonObj.getBoolean("status");
                                String message = jsonObj.getString("message");
                                if (status) {
                                    JSONArray resultArray = jsonObj.getJSONArray("result");
                                    if (resultArray.length() > 0) {


                                        for (int i = 0; i < resultArray.length(); i++) {


                                            JSONObject jsonObject = resultArray.getJSONObject(i);

                                            String ID = jsonObject.getString("id");
                                            final String education_type = jsonObject.getString("education_type");
                                            final String education_title = jsonObject.getString("education_title");
                                            final String education_description = jsonObject.getString("education_description");
                                            final String education_url = jsonObject.getString("education_url");
                                            final String contact_no = jsonObject.getString("contact_no");
                                            final String created_by = jsonObject.getString("created_by");



                                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            final View rowView = Objects.requireNonNull(inflater).inflate(R.layout.education_row_layout, null);


                                            TextView row_id = (TextView) rowView.findViewById(R.id.row_id);
                                            TextView row_education_title = (TextView) rowView.findViewById(R.id.row_education_title);

                                            TextView row_education_type = (TextView) rowView.findViewById(R.id.row_education_type);
                                            TextView row_education_description = (TextView) rowView.findViewById(R.id.row_education_description);
                                            TextView row_education_url = (TextView) rowView.findViewById(R.id.row_education_url);
                                            TextView row_contact_no = (TextView) rowView.findViewById(R.id.row_contact_no);



                                            LinearLayout LinearCall = (LinearLayout) rowView.findViewById(R.id.LinearCall);

                                            if (ID != null && !ID.equalsIgnoreCase("null") && !ID.equalsIgnoreCase("")) {
                                                row_id.setText(ID);
                                            }

                                            if (education_type != null && !education_type.equalsIgnoreCase("null") && !education_type.equalsIgnoreCase("")) {
                                                row_education_type.setText(ucFirst(education_type));
                                            }

                                            if (education_title != null && !education_title.equalsIgnoreCase("null") && !education_title.equalsIgnoreCase("")) {
                                                row_education_title.setText(education_title.toUpperCase());
                                            }

                                            if (education_description != null && !education_description.equalsIgnoreCase("null") && !education_description.equalsIgnoreCase("")) {
                                                row_education_description.setText(ucFirst(education_description));
                                            }

                                            if (education_url != null && !education_url.equalsIgnoreCase("null") && !education_url.equalsIgnoreCase("")) {
                                                row_education_url.setText(education_url);
                                                Linkify.addLinks(row_education_url, Linkify.WEB_URLS);


                                            }


                                            if (contact_no != null && !contact_no.equalsIgnoreCase("null") && !contact_no.equalsIgnoreCase("")) {
                                                row_contact_no.setText(contact_no);
                                            }





                                            LinearCall.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                                    callIntent.setData(Uri.parse("tel: "+row_contact_no.getText().toString()));
                                                    startActivity(callIntent);
                                                }
                                            });

                                            ImageView iv_delete = (ImageView) rowView.findViewById(R.id.iv_delete);
                                            if (IS_ADMIN != null && !IS_ADMIN.equalsIgnoreCase("") && !IS_ADMIN.equalsIgnoreCase("null")) {
                                                if (IS_ADMIN.equalsIgnoreCase("1")) {
                                                    iv_delete.setVisibility(View.VISIBLE);
                                                } else {
                                                    if (created_by != null && !created_by.equalsIgnoreCase("")) {
                                                        if (created_by.equalsIgnoreCase(StrMemberId)) {
                                                            iv_delete.setVisibility(View.VISIBLE);
                                                        }
                                                    } else {
                                                        iv_delete.setVisibility(View.GONE);
                                                    }

                                                }

                                            }


                                            iv_delete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(EducationCorner.this);
                                                    builder.setTitle("Delete !!!");
                                                    builder.setMessage("Are you sure you want to delete this entry ?");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            DeleteEducationDetails(row_id.getText().toString());
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


                                            ParentLayoutEducationList.addView(rowView);
                                        }

                                    } else {
                                        TextView tv_no_data = new TextView(getApplicationContext());
                                        tv_no_data.setText("No Education Detail Found.");
                                        tv_no_data.setPadding(10, 10, 10, 10);
                                        ParentLayoutEducationList.addView(tv_no_data);
                                        //Toast.makeText(getApplicationContext(), "Sorry No data is available", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    TextView tv_no_data = new TextView(getApplicationContext());
                                    tv_no_data.setText("No Education Detail Found.");
                                    tv_no_data.setPadding(10, 10, 10, 10);
                                    ParentLayoutEducationList.addView(tv_no_data);
                                    //Toast.makeText(getApplicationContext(), "Sorry No data is available", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                TextView tv_no_data = new TextView(getApplicationContext());
                                tv_no_data.setText("No Education Detail Found.");
                                tv_no_data.setPadding(10, 10, 10, 10);
                                ParentLayoutEducationList.addView(tv_no_data);
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
                            if (!success) {
                                TextView tv_no_data = new TextView(getApplicationContext());
                                tv_no_data.setText("No Education Detail Found.");
                                tv_no_data.setPadding(10, 10, 10, 10);
                                ParentLayoutEducationList.addView(tv_no_data);
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


    private void DeleteEducationDetails (String eduId){

        Log.d("DeleteID", "" + eduId);
        String Uiid_id = UUID.randomUUID().toString();

        String API_DeleteDonationInfo = ROOT_URL + "delete_education_detail.php?_" + Uiid_id;
        try {

            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();


            if (isInternetPresent) {
                Log.d("URL", API_DeleteDonationInfo);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, API_DeleteDonationInfo,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean status = jsonObject.getBoolean("status");
                                    if (status) {
                                        fetchEducationList();
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
                        }) {

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        //params.put("Pan", StrClientPan);
                        params.put("eduId", eduId);
                        Log.d("ParrasOtpdata", params.toString());

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
    public void onBackPressed () {
        Intent intent = new Intent(getApplicationContext(), NewDashboard.class);
        intent.putExtra("lang_flag", LanguageSelected);
        startActivity(intent);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
