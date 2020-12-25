package com.ascend.www.abkms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;

public class ProfileActivity extends AppCompatActivity {

    ImageView back_btn;
    String StrMobileNo,StrUserName,StrUserId,StrUserEmail,StrUniqueUserId,StrPassword;
    EditText edt_User_Name,edt_Email_Addresss,edt_Mobile_Number,edt_UserId,edt_Password,edt_Confirm_Password;
    Button btnSubmit;
    ProgressDialog myDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        init();


    }

    private void init() {

        back_btn = (ImageView) findViewById(R.id.back_btn_toolbar);
        back_btn.setVisibility(View.INVISIBLE);
        /*back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        TextView til_text = (TextView)findViewById(R.id.til_text);
        til_text.setText(getResources().getString(R.string.view_update_profile));

        StrMobileNo = UtilitySharedPreferences.getPrefs(getApplicationContext(),"UserMobile");
        StrUserName = UtilitySharedPreferences.getPrefs(getApplicationContext(),"UserName");
        StrUserId =  UtilitySharedPreferences.getPrefs(getApplicationContext(),"UserId");
        StrUserEmail = UtilitySharedPreferences.getPrefs(getApplicationContext(),"UserEmail");
        StrUniqueUserId  = UtilitySharedPreferences.getPrefs(getApplicationContext(),"UniqueUserId");

        edt_User_Name = (EditText)findViewById(R.id.edt_User_Name);
        edt_Email_Addresss = (EditText)findViewById(R.id.edt_Email_Addresss);
        edt_Mobile_Number = (EditText)findViewById(R.id.edt_Mobile_Number);
        edt_UserId = (EditText)findViewById(R.id.edt_UserId);
        edt_Password = (EditText)findViewById(R.id.edt_Password);
        edt_Confirm_Password = (EditText)findViewById(R.id.edt_Confirm_Password);

        if(StrMobileNo!=null && !StrMobileNo.equalsIgnoreCase("") && !StrMobileNo.equalsIgnoreCase("null")){
            edt_Mobile_Number.setText(StrMobileNo);
            edt_Mobile_Number.setEnabled(false);
        }else {
            edt_Mobile_Number.setText("");
            edt_Mobile_Number.setEnabled(true);
        }

        if(StrUserEmail!=null && !StrUserEmail.equalsIgnoreCase("") && !StrUserEmail.equalsIgnoreCase("null")){
            edt_Email_Addresss.setText(StrUserEmail);
            edt_Email_Addresss.setEnabled(false);
        }else {
            edt_Email_Addresss.setText("");
            edt_Email_Addresss.setEnabled(true);
        }

        if(StrUserName!=null && !StrUserName.equalsIgnoreCase("") && !StrUserName.equalsIgnoreCase("null")){
            edt_User_Name.setText(StrUserName);
        }else {
            edt_User_Name.setText("");
        }

        if(StrUniqueUserId!=null && !StrUniqueUserId.equalsIgnoreCase("") && !StrUniqueUserId.equalsIgnoreCase("null")){
            edt_UserId.setText(StrUniqueUserId);
        }else {
            edt_UserId.setText("");
        }


        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidFields()){
                    StrMobileNo = edt_Mobile_Number.getText().toString();
                    StrUserName = edt_User_Name.getText().toString();
                    StrUniqueUserId = edt_UserId.getText().toString();
                    StrPassword = edt_Password.getText().toString();
                    StrUserEmail = edt_Email_Addresss.getText().toString();

                    CheckUserIdAvailability();
                }
            }
        });

        myDialog = new ProgressDialog(ProfileActivity.this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

    }

    private void CheckUserIdAvailability() {

        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();
        String URL_FetchDetails = ROOT_URL + "user/checkUserIdAvailability";
        try {
            Log.d("URL_FetchDetails",URL_FetchDetails);

            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_FetchDetails,
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
                                    String message = jobj.getString("message");

                                    if (!status) {

                                       edt_UserId.setError(message);
                                    }else {
                                        UpdateProfileData();
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
                        params.put("UserId",StrUniqueUserId);

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

    private void UpdateProfileData() {

        //myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();
        String URL_FetchDetails = ROOT_URL + "user/updateProfileData";
        try {
            Log.d("URL_FetchDetails",URL_FetchDetails);

            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_FetchDetails,
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
                                    String message = jobj.getString("message");

                                    if (status) {
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(),"UserMobile",StrMobileNo);
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(),"UserName",StrUserName);
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(),"UserId",StrUserId);
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(),"UserEmail",StrUserEmail);
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(),"UniqueUserId",StrUniqueUserId);

                                        Intent i = new Intent(getApplicationContext(), NewDashboard.class);
                                        startActivity(i);
                                        overridePendingTransition(R.animator.move_left, R.animator.move_right);
                                        finish();
                                    }else {
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
                        params.put("Id",StrUserId);
                        params.put("Name",StrUserName);
                        params.put("Email",StrUserEmail);
                        params.put("Mobile",StrMobileNo);
                        params.put("Password",StrPassword);
                        params.put("UserId",StrUniqueUserId);
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

    private boolean isValidFields(){
        boolean result = true;

        if (!MyValidator.isValidName(edt_User_Name)) {
            edt_User_Name.requestFocus();
            result = false;
        }


        if (!MyValidator.isValidField(edt_UserId)) {
            edt_UserId.requestFocus();
            result = false;
        }

        if (!MyValidator.isValidEmail(edt_Email_Addresss)) {
            edt_Email_Addresss.requestFocus();
            result = false;
        }

        if (!MyValidator.isValidMobile(edt_Mobile_Number)) {
            edt_Mobile_Number.requestFocus();
            result = false;
        }

        if (!MyValidator.isValidField(edt_Password)) {
            edt_Password.requestFocus();
            result = false;
        }

        if (!MyValidator.isValidField(edt_Confirm_Password)) {
            edt_Confirm_Password.requestFocus();
            result = false;
        }

        if(!edt_Password.getText().toString().trim().equals(edt_Confirm_Password.getText().toString().trim())){
            edt_Confirm_Password.setError("Password & Confirm Password Does not Match.");
            edt_Confirm_Password.requestFocus();
            result = false;
        }


        return  result;

    }


}
