package com.ascend.www.abkms.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ascend.www.abkms.R;
import com.ascend.www.abkms.adapters.MemberListAdapter;
import com.ascend.www.abkms.model.MatdarListModel;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.utils.MyValidator;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;

public class MemberDetailsActivity extends AppCompatActivity {

    String member_id = "", SearchKey = "";
    ImageView back_btn_toolbar;
    String LanguageSelected = "eng", SearchFilterBy = "";
    TextView tv_ac_no_id_detail, tv_member_name, tv_father_name,tv_spouse_name,tv_spouse_mobile_no,tv_mobile_no, tv_address, tv_age_gender,
            tv_pincode, tv_whatsapp_no;
    ImageView Iv_Phone, Iv_Share,Iv_Edit,Iv_Delete;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recycler_list;
    RecyclerView.LayoutManager layoutManager;
    private MemberListAdapter memberListAdapter;
    ArrayList<MatdarListModel> matdarModel_list = new ArrayList<>();
    MatdarListModel matdaarModel;
    ProgressDialog myDialog;
    Dialog DialogVerifyMobile,DailogEnterMobile;
    EditText etMobile,edt_verification_number;
    String UpdatedMobileNo,SystemOtp;
    String StrMemberId,IS_ADMIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("member_id") != null) {
            member_id = bundle.getString("member_id");
        }
        Log.d("member_id:", "" + member_id);


        init();
    }

    private void init() {

        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");

        IS_ADMIN = UtilitySharedPreferences.getPrefs(getApplicationContext(),"IsAdmin");

        back_btn_toolbar = (ImageView) findViewById(R.id.back_btn_toolbar);
        back_btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView til_text = (TextView) findViewById(R.id.til_text);
        til_text.setText(getResources().getString(R.string.detail_of_member));

        LanguageSelected = UtilitySharedPreferences.getPrefs(getApplicationContext(), "LanguageSelected");
        SearchFilterBy = UtilitySharedPreferences.getPrefs(getApplicationContext(), "SearchFilterBy");


        tv_ac_no_id_detail = (TextView) findViewById(R.id.tv_ac_no_id_detail);
        tv_member_name = (TextView) findViewById(R.id.tv_member_name);
        tv_father_name = (TextView) findViewById(R.id.tv_father_name);
        tv_spouse_name = (TextView) findViewById(R.id.tv_spouse_name);
        tv_spouse_mobile_no = (TextView) findViewById(R.id.tv_spouse_mobile_no);
        tv_mobile_no = (TextView) findViewById(R.id.tv_mobile_no);
        tv_age_gender = (TextView) findViewById(R.id.tv_age_gender);
        tv_pincode = (TextView) findViewById(R.id.tv_pincode);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_whatsapp_no = (TextView) findViewById(R.id.tv_whatsapp_no);

        myDialog = new ProgressDialog(MemberDetailsActivity.this);
        myDialog.setMessage(getResources().getString(R.string.please_wait));
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(true);

        Iv_Phone = (ImageView) findViewById(R.id.Iv_Phone);
        Iv_Phone.setVisibility(View.GONE);
        Iv_Share = (ImageView) findViewById(R.id.Iv_Share);
        Iv_Share.setVisibility(View.GONE);

        Iv_Edit  = (ImageView) findViewById(R.id.Iv_Edit);
        Iv_Delete = (ImageView) findViewById(R.id.Iv_Delete);

        if(IS_ADMIN!=null && !IS_ADMIN.equalsIgnoreCase("") && !IS_ADMIN.equalsIgnoreCase("null")){
            if(IS_ADMIN.equalsIgnoreCase("1")){
                Iv_Edit.setVisibility(View.VISIBLE);
                Iv_Delete.setVisibility(View.VISIBLE);
            }else {
                if(member_id!=null && !member_id.equalsIgnoreCase("")){
                    if(member_id.equalsIgnoreCase(StrMemberId)){
                        Iv_Edit.setVisibility(View.VISIBLE);
                    }
                }else {
                    Iv_Edit.setVisibility(View.GONE);
                }
                Iv_Delete.setVisibility(View.GONE);
            }

        }


        if (member_id != null && !member_id.equalsIgnoreCase("")) {
            GetMemberDetail();
        }


    }


    private void GetMemberDetail() {

        myDialog.show();

        String Uiid_id = UUID.randomUUID().toString();

        String VoterDetailUrl = ROOT_URL + "memberdetail.php?_" + Uiid_id + "&lang=" + LanguageSelected + "&id=" + member_id + "&filter=" + SearchFilterBy;
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
                        String NAME = resultObj.getString("NAME");
                        String FATHER_NAME = resultObj.getString("FATHER_NAME");
                        String SPOUSE_NAME = resultObj.getString("SPOUSE_NAME");
                        String GENDER = resultObj.getString("GENDER");
                        String AGE = resultObj.getString("AGE");
                        String DOB = resultObj.getString("DOB");
                        String FAMILY_MEMBER_COUNT = resultObj.getString("FAMILY_MEMBER_COUNT");
                        String ADDRESS1 = resultObj.getString("ADDRESS1");
                        String ADDRESS2 = resultObj.getString("ADDRESS2");
                        String ADDRESS3 = resultObj.getString("ADDRESS3");
                        String CITY = resultObj.getString("CITY");
                        String STATE = resultObj.getString("STATE");
                        String PINCODE = resultObj.getString("PINCODE");
                        String MOBILE_NO = resultObj.getString("MOBILE_NO");
                        String MOBILE_NO1 = resultObj.getString("MOBILE_NO1");
                        String SPOUSE_MOBILE_NO = resultObj.getString("SPOUSE_MOBILE_NO");
                        String WHATSAPP_NO = resultObj.getString("WHATSAPP_NO");



                        tv_member_name.setText(NAME.toUpperCase());
                        tv_father_name.setText(""+FATHER_NAME.toUpperCase());

                        if(SPOUSE_NAME!=null && !SPOUSE_NAME.equalsIgnoreCase("") && !SPOUSE_NAME.equalsIgnoreCase("null")) {
                            tv_spouse_name.setText(""+SPOUSE_NAME.toUpperCase());
                        }else{
                            tv_spouse_name.setText("");
                        }

                        if(MOBILE_NO1!=null && !MOBILE_NO1.equalsIgnoreCase("") && !MOBILE_NO1.equalsIgnoreCase("null")) {
                            tv_mobile_no.setText(MOBILE_NO + " / " + MOBILE_NO1);
                        }else {
                            tv_mobile_no.setText(MOBILE_NO);
                        }


                        tv_spouse_mobile_no.setText(SPOUSE_MOBILE_NO);
                        tv_address.setText(ADDRESS1 + ", "+ADDRESS2 +", "+ADDRESS3 + ", "+CITY +", "+STATE);
                        if (GENDER.equalsIgnoreCase("F")) {
                            String StrAge_Gender = DOB +" ("+ AGE + " Yrs) / " + getResources().getString(R.string.female);
                            tv_age_gender.setText(StrAge_Gender);
                        } else if (GENDER.equalsIgnoreCase("M")) {
                            String StrAge_Gender = DOB +"("+ AGE + " Yrs) / " + getResources().getString(R.string.male);
                            tv_age_gender.setText(StrAge_Gender);
                        }

                        if(WHATSAPP_NO!=null && !WHATSAPP_NO.equalsIgnoreCase("") && !WHATSAPP_NO.equalsIgnoreCase("null")) {
                            tv_whatsapp_no.setText("" + WHATSAPP_NO);
                        }else{
                            tv_whatsapp_no.setText("");
                        }

                        tv_pincode.setText(PINCODE);



                        Iv_Phone.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(View view) {
                                Log.d("MOBILENO: ","-->"+MOBILE_NO);
                                if (MOBILE_NO != null && !MOBILE_NO.equalsIgnoreCase("") && !MOBILE_NO.equalsIgnoreCase("null")) {
                                    if (MyValidator.isValidMobileString(getApplicationContext(),MOBILE_NO)) {
                                        Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + MOBILE_NO));
                                        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    Activity#requestPermissions
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for Activity#requestPermissions for more details.
                                            return;
                                        }
                                        startActivity(phoneIntent);
                                    }
                                }
                            }
                        });

                        Iv_Share.setVisibility(View.VISIBLE);
                        Iv_Share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent shareIntent =  new Intent(android.content.Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,getResources().getString(R.string.share_title));
                                String shareMessage=getResources().getString(R.string.share_header1) + "\n\n"+getResources().getString(R.string.share_header2)+"\n"+
                                        getResources().getString(R.string.share_header3)
                                        +"\n"+getResources().getString(R.string.member_name)+NAME.toUpperCase()
                                        +"\n"+getResources().getString(R.string.father_name)+" - "+FATHER_NAME.toUpperCase()
                                        +"\n"+getResources().getString(R.string.spouse_name)+" - "+SPOUSE_NAME.toUpperCase() +" (M.No.- "+SPOUSE_MOBILE_NO+" )"
                                        +"\n"+getResources().getString(R.string.mobileno)+" - "+MOBILE_NO /*+ " / "+ MOBILE_NO1*/
                                        +"\n"+getResources().getString(R.string.address)+" - "+ADDRESS1 + ", "+ADDRESS2 +", "+ADDRESS3 + ", "+CITY +", "+STATE +" - "+ PINCODE
                                        +"\n----------------------------\n\n\n"+getResources().getString(R.string.share_last);
                                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,shareMessage);
                                startActivity(Intent.createChooser(shareIntent,"Sharing via"));
                            }
                        });


                        Iv_Edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(IS_ADMIN!=null && !IS_ADMIN.equalsIgnoreCase("") && !IS_ADMIN.equalsIgnoreCase("null")){
                                    if(IS_ADMIN.equalsIgnoreCase("1")){
                                        Iv_Edit.setVisibility(View.VISIBLE);
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(),"EditProfileId",member_id);
                                        Intent i = new Intent(getApplicationContext(), AddUpdateUserDetail.class);
                                        startActivity(i);
                                        overridePendingTransition(R.animator.move_left,R.animator.move_right);

                                    }else {
                                        if(member_id!=null && !member_id.equalsIgnoreCase("")){
                                            if(member_id.equalsIgnoreCase(StrMemberId)){
                                                Iv_Edit.setVisibility(View.VISIBLE);
                                                UtilitySharedPreferences.setPrefs(getApplicationContext(),"MemberId",member_id);
                                                Intent i = new Intent(getApplicationContext(), CompleteDetailedProfileInfo.class);
                                                startActivity(i);
                                                overridePendingTransition(R.animator.move_left,R.animator.move_right);
                                                finish();
                                            }
                                        }else {
                                            Iv_Edit.setVisibility(View.GONE);
                                        }

                                    }

                                }


                            }
                        });

                        Iv_Delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MemberDetailsActivity.this);
                                builder.setTitle("Delete !!!");
                                builder.setMessage("Are you sure you want to delete this entry ?");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DeleteProfileApi();
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

    private void DeleteProfileApi() {

        String Uiid_id = UUID.randomUUID().toString();

        String API_DeleteDonationInfo = ROOT_URL + "delete_member_info.php?_" + Uiid_id;
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
                                        onBackPressed();
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
                        params.put("member_id", member_id);
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

    private void PopupToVerifyOTP() {
        DialogVerifyMobile = new Dialog(MemberDetailsActivity.this);
        DialogVerifyMobile.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogVerifyMobile.setCanceledOnTouchOutside(false);
        DialogVerifyMobile.setContentView(R.layout.custom_dialog_for_entering_otp);
        DialogVerifyMobile.getWindow().getAttributes().width = ActionBar.LayoutParams.MATCH_PARENT;



        DialogVerifyMobile.show();
        TextView til_text = (TextView)DialogVerifyMobile.findViewById(R.id.til_text);
        til_text.setText("OTP to verify your mobile is sent on your Mobile No.- "+UpdatedMobileNo);

        edt_verification_number  = (EditText) DialogVerifyMobile.findViewById(R.id.edt_verification_number);
        edt_verification_number.setText(SystemOtp);
        Button Verify_otp =(Button) DialogVerifyMobile.findViewById(R.id.Verify_otp);
        ImageView iv_close = (ImageView)DialogVerifyMobile.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DialogVerifyMobile!=null && DialogVerifyMobile.isShowing()){
                    DialogVerifyMobile.dismiss();
                }

            }
        });



        Verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(edt_verification_number.getWindowToken(), 0);
                }
                String OTP_Entered = edt_verification_number.getText().toString();

                OtpVerifyApi(OTP_Entered);
            }
        });


    }

    private void OtpVerifyApi(final String OTP_Entered) {
        String verifyOTP_Api = ROOT_URL + "verifyUserMobile.php";
        try {

            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();



            if (isInternetPresent) {
                Log.d("URL",verifyOTP_Api);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, verifyOTP_Api,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response",response);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean status = jsonObject.getBoolean("status");
                                    if(status){
                                        edt_verification_number.setError(null);
                                        if(DialogVerifyMobile!=null && DialogVerifyMobile.isShowing()){
                                            DialogVerifyMobile.dismiss();
                                        }

                                        Toasty.success(getApplicationContext(),"Mobile Verified Successfully.", Toast.LENGTH_SHORT).show();
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(),"MemberId",member_id);
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(),"EditProfileId",member_id);
                                        Intent i = new Intent(getApplicationContext(), AddUpdateUserDetail.class);
                                        startActivity(i);
                                        overridePendingTransition(R.animator.move_left,R.animator.move_right);



                                    }else {
                                        edt_verification_number.setError("Invalid OTP.");


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
                        params.put("MEMBER_ID", member_id);
                        params.put("Mobile", UpdatedMobileNo);
                        params.put("OTP", OTP_Entered);
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

  /*  private void PopupUpdateMobileNo() {
        String msg= "Please verify Mobile No & get OTP";

        DailogEnterMobile = new Dialog(MemberDetailsActivity.this);
        DailogEnterMobile.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DailogEnterMobile.setCanceledOnTouchOutside(false);
        DailogEnterMobile.setContentView(R.layout.custom_dialog_for_password);
        DailogEnterMobile.getWindow().getAttributes().width = ActionBar.LayoutParams.MATCH_PARENT;



        DailogEnterMobile.show();

        TextView text= (TextView)DailogEnterMobile.findViewById(R.id.text);
        text.setText(msg);
        TextView text1 = (TextView)DailogEnterMobile.findViewById(R.id.text1);
        text1.setVisibility(View.GONE);

        ImageView iv_close = (ImageView) DailogEnterMobile.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DailogEnterMobile!=null && DailogEnterMobile.isShowing()){
                    DailogEnterMobile.dismiss();
                }

            }
        });

      
        etMobile = (EditText)DailogEnterMobile.findViewById(R.id.etMobile);
        Button btn_verify=(Button) DailogEnterMobile.findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMobile.getText().toString()!=null && !etMobile.getText().toString().equalsIgnoreCase("")){

                    UpdatedMobileNo = etMobile.getText().toString();
                    Log.d("UpdatedMobileNo",UpdatedMobileNo);
                    if(UpdatedMobileNo!=null && !UpdatedMobileNo.equalsIgnoreCase("")) {
                        if (MyValidator.isValidMobileString(getApplicationContext(), UpdatedMobileNo)) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(etMobile.getWindowToken(), 0);
                            //fetchUserDetails();
                            API_UPDATE_MOBILE_GET_OTP();

                        } else {

                            etMobile.setText("Please Enter Valid Mobile No.");
                        }
                    }else {
                        etMobile.setText("Please Enter updated Mobile No.");
                    }

                }
            }
        });

    }
*/
    private void API_UPDATE_MOBILE_GET_OTP() {

        String API_UPDATE_MOBILE_OTP = ROOT_URL + "updatemobile.php";
        try {

            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();



            if (isInternetPresent) {
                Log.d("URL",API_UPDATE_MOBILE_OTP);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, API_UPDATE_MOBILE_OTP,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response",response);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean status = jsonObject.getBoolean("status");
                                    if(status){
                                        if(DailogEnterMobile!=null && DailogEnterMobile.isShowing()){
                                            DailogEnterMobile.dismiss();
                                        }
                                        JSONObject  result = jsonObject.getJSONObject("result");
                                        SystemOtp = result.getString("OTP");
                                        Toasty.success(getApplicationContext(),"OTP to verify Mobile is : "+SystemOtp, Toast.LENGTH_SHORT).show();

                                        PopupToVerifyOTP();

                                        //registerUserAPI();



                                    }else {
                                        edt_verification_number.setError("Invalid OTP.");


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
                        params.put("Mobile", UpdatedMobileNo);
                        params.put("MEMBER_ID", member_id);
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
        super.onBackPressed();
        overridePendingTransition(R.animator.left_right,R.animator.right_left);
    }
}