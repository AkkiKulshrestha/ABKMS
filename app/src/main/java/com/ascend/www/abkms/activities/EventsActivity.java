package com.ascend.www.abkms.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;

public class EventsActivity extends AppCompatActivity implements View.OnClickListener {


    ImageView back_btn_toolbar;
    TextView til_text;
    String LanguageSelected;
    LinearLayout ParentLayoutEventsList;
    ProgressDialog myDialog;
    String IS_ADMIN;
    Button BtnAddEvent;
    Dialog DialogAddEvent;
    EditText Edt_EventDate;
    String StrEventTitle="",StrEventDescription="",StrEventTime="",StrEventDate="";
    private DatePickerDialog dobDatePickerDialog;
    private SimpleDateFormat dateFormatter, dateFormatter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        init();
    }

    private void init() {

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
        til_text.setText(getResources().getString(R.string.events));
        LanguageSelected = UtilitySharedPreferences.getPrefs(getApplicationContext(),"LanguageSelected");

        ParentLayoutEventsList = (LinearLayout)findViewById(R.id.ParentLayoutEventsList);
        myDialog = new ProgressDialog(EventsActivity.this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        BtnAddEvent = (Button)findViewById(R.id.BtnAddEvent);

        if(IS_ADMIN!=null && !IS_ADMIN.equalsIgnoreCase("") && !IS_ADMIN.equalsIgnoreCase("null")){
            if(IS_ADMIN.equalsIgnoreCase("1")){
                BtnAddEvent.setVisibility(View.VISIBLE);
            }else {
                BtnAddEvent.setVisibility(View.GONE);
            }

        }

        BtnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpToAddEvent();
            }
        });

        fetchEventsList();
        
    }



    private void showPopUpToAddEvent() {

        DialogAddEvent = new Dialog(EventsActivity.this);
        DialogAddEvent.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogAddEvent.setCanceledOnTouchOutside(false);
        DialogAddEvent.setCancelable(false);
        DialogAddEvent.setContentView(R.layout.layout_dailog_add_event);
        Objects.requireNonNull(DialogAddEvent.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView)DialogAddEvent.findViewById(R.id.title) ;
        ImageView iv_close = (ImageView)DialogAddEvent.findViewById(R.id.iv_close);

        EditText EdtEventTitle = (EditText)DialogAddEvent.findViewById(R.id.EdtEventTitle);
        EditText EdtEventDescription = (EditText)DialogAddEvent.findViewById(R.id.EdtEventDescription);
        EditText EdtEventTime = (EditText)DialogAddEvent.findViewById(R.id.EdtEventTime);
        Edt_EventDate = (EditText)DialogAddEvent.findViewById(R.id.Edt_EventDate);
        InputMethodManager mgr1 = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr1 != null) {
            mgr1.showSoftInput(Edt_EventDate, InputMethodManager.SHOW_FORCED);
        }
        setDateTimeField();
        Button Btn_submit = (Button)DialogAddEvent.findViewById(R.id.Btn_submit);


        DialogAddEvent.show();
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DialogAddEvent!=null && DialogAddEvent.isShowing()) {
                    DialogAddEvent.dismiss();
                }
            }
        });



        Btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrEventTitle = EdtEventTitle.getText().toString();
                StrEventDescription = EdtEventDescription.getText().toString();
                StrEventTime = EdtEventTime.getText().toString();
                StrEventDate = Edt_EventDate.getText().toString();

                if(StrEventDate!=null && !StrEventDate.equalsIgnoreCase("")) {

                    if(StrEventTime!=null && !StrEventTime.equalsIgnoreCase("")) {

                        if(StrEventTitle!=null && !StrEventTitle.equalsIgnoreCase("")) {

                            if(StrEventDescription!=null && !StrEventDescription.equalsIgnoreCase("")) {

                                API_SAVE_EVENT_INFO();

                            }else {
                                CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Event Description");
                            }


                        }else {
                            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Event Name");
                        }

                    }else {
                        CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Event Time");
                    }

                }else {
                    CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Event Date");
                }

            }
        });

    }

    private void setDateTimeField() {

        Edt_EventDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();


        dobDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                StrEventDate = dateFormatter1.format(newDate.getTime());

                Edt_EventDate.setText(StrEventDate);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dobDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.Edt_EventDate) {

            dobDatePickerDialog.show();
        }
    }

    private void API_SAVE_EVENT_INFO() {

        String Uiid_id = UUID.randomUUID().toString();
        String URL_ADD_DONOR_DETAIL = ROOT_URL + "add_event.php?_"+Uiid_id;
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
                                        if(DialogAddEvent!=null && DialogAddEvent.isShowing()) {
                                            DialogAddEvent.dismiss();
                                        }

                                        fetchEventsList();

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
                        params.put("event_date",StrEventDate);
                        params.put("event_time",StrEventTime);
                        params.put("event_name",StrEventTitle);
                        params.put("event_description",StrEventDescription);
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


    private void fetchEventsList() {

        myDialog.show();

        if(ParentLayoutEventsList!=null && ParentLayoutEventsList.getChildCount()>0){
            ParentLayoutEventsList.removeAllViews();
        }
        String Uiid_id = UUID.randomUUID().toString();

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            String URL = ROOT_URL+"eventlist.php?_" + Uiid_id;
            Log.d("EventList:",""+URL);
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

                                            String ID = jsonObject.getString("ID");
                                            final String EVENT_TITLE = jsonObject.getString("EVENT_TITLE");
                                            final String EVENT_DESCRIPTION = jsonObject.getString("EVENT_DESCRIPTION");
                                            final String EVENT_DATE = jsonObject.getString("EVENT_DATE");
                                            final String EVENT_TIME = jsonObject.getString("EVENT_TIME");
                                            String EVENT_IMAGE = jsonObject.getString("EVENT_IMAGE");

                                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            final View rowView = inflater.inflate(R.layout.event_layout_list, null);

                                            TextView row_event_date = (TextView)rowView.findViewById(R.id.row_event_date);
                                            TextView row_id = (TextView)rowView.findViewById(R.id.row_id);
                                            TextView row_event_time = (TextView)rowView.findViewById(R.id.row_event_time);
                                            final TextView row_event_title = (TextView) rowView.findViewById(R.id.row_event_title);
                                            final TextView row_event_desc = (TextView) rowView.findViewById(R.id.row_event_desc);
                                            final ImageView Iv_Event_Image = (ImageView) rowView.findViewById(R.id.Iv_Event_Image);

                                            if(EVENT_DATE!=null && !EVENT_DATE.equalsIgnoreCase("null") && !EVENT_DATE.equalsIgnoreCase("")) {
                                                row_event_date.setText(EVENT_DATE);
                                            }

                                            if(EVENT_TIME!=null && !EVENT_TIME.equalsIgnoreCase("null") && !EVENT_TIME.equalsIgnoreCase("")) {
                                                row_event_time.setText(EVENT_TIME);
                                            }

                                            if(EVENT_TITLE!=null && !EVENT_TITLE.equalsIgnoreCase("null") && !EVENT_TITLE.equalsIgnoreCase("")) {
                                                row_event_title.setText(EVENT_TITLE);
                                            }


                                            if(EVENT_DESCRIPTION!=null && !EVENT_DESCRIPTION.equalsIgnoreCase("null") && !EVENT_DESCRIPTION.equalsIgnoreCase("")) {
                                                //EVENT_DESCRIPTION.replace("\\n","\n");
                                                row_event_desc.setText(Html.fromHtml(EVENT_DESCRIPTION));
                                            }

                                            if(ID!=null && !ID.equalsIgnoreCase("null") && !ID.equalsIgnoreCase("")) {
                                                row_id.setText(ID);
                                            }

                                            if(EVENT_IMAGE!=null && !EVENT_IMAGE.equalsIgnoreCase("null") && !EVENT_IMAGE.equalsIgnoreCase("")) {
                                                Glide.with(EventsActivity.this)
                                                        .load(EVENT_IMAGE) // image url
                                                        //.override(200, 200) // resizing
                                                        //.centerCrop()
                                                        .into(Iv_Event_Image);
                                            }

                                            ImageView iv_delete = (ImageView)rowView.findViewById(R.id.iv_delete);
                                            if(IS_ADMIN!=null && !IS_ADMIN.equalsIgnoreCase("") && !IS_ADMIN.equalsIgnoreCase("null")){
                                                if(IS_ADMIN.equalsIgnoreCase("1")){
                                                    iv_delete.setVisibility(View.VISIBLE);
                                                }else {
                                                    iv_delete.setVisibility(View.GONE);
                                                }

                                            }
                                            iv_delete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(EventsActivity.this);
                                                    builder.setTitle("Delete !!!");
                                                    builder.setMessage("Are you sure you want to delete this entry ?");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            DeleteEventDetails(row_id.getText().toString());
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



                                            ParentLayoutEventsList .addView(rowView);
                                        }

                                    } else {
                                        TextView tv_no_data = new TextView(getApplicationContext());
                                        tv_no_data.setText("No Events & Programs Found");
                                        tv_no_data.setPadding(10,10,10,10);
                                        ParentLayoutEventsList.addView(tv_no_data);
                                        //Toast.makeText(getApplicationContext(), "Sorry No data is available", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    TextView tv_no_data = new TextView(getApplicationContext());
                                    tv_no_data.setText("No Events & Programs Found");
                                    tv_no_data.setPadding(10,10,10,10);
                                    ParentLayoutEventsList.addView(tv_no_data);
                                    //Toast.makeText(getApplicationContext(), "Sorry No data is available", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                TextView tv_no_data = new TextView(getApplicationContext());
                                tv_no_data.setText("No Events & Programs Found");
                                tv_no_data.setPadding(10,10,10,10);
                                ParentLayoutEventsList.addView(tv_no_data);
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
                                tv_no_data.setText("No Events & Programs Found");
                                tv_no_data.setPadding(10,10,10,10);
                                ParentLayoutEventsList.addView(tv_no_data);
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


    private void DeleteEventDetails(String eventId) {

        Log.d("DeleteID",""+eventId);
        String Uiid_id = UUID.randomUUID().toString();

        String API_DeleteDonationInfo = ROOT_URL + "delete_event_info.php?_" + Uiid_id;
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
                                        fetchEventsList();
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
                        params.put("eventId", eventId);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

