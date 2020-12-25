package com.ascend.www.abkms.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.ascend.www.abkms.utils.UtilitySharedPreferences;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;

public class DonationActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView back_btn_toolbar;
    TextView til_text;

    TextView tv_beneficiary_name,tv_account_no,tv_bank_name,tv_bank_ifsc,tv_bank_branch;
    LinearLayout ll_parent_donor_list,ll_parent_my_donation_list;
    String StrMemberId,StrMemberName,StrAFFLIATED_ABKMS,IS_ADMIN;
    ProgressDialog myDialog;
    Dialog DialogAddDonor,DialogDonation;
    String StrDonorId="",StrDonorName="",StrDonorCityName="",StrDonationFor="",StrDonationAmount="",StrDonationDate="";
    private DatePickerDialog dobDatePickerDialog;
    private SimpleDateFormat dateFormatter, dateFormatter1;
    EditText Edt_DonationDate;
    Button AddDonorInfo,BtnDonate;
    String StrUpiAccountId="",StrUPI_MerchantName="";
    final int UPI_PAYMENT = 0;
    LinearLayout LayoutDonate,LayoutTabAllDonation,LayoutTabMyDonation;
    ArrayList<String> memberNameList = new ArrayList<String>();
    ArrayList<String> memberCodeList = new ArrayList<String>();
    ArrayList<String> memberCityList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_corner);

        findViewByIds();
    }

    private void findViewByIds() {
        getMemberNameList();
        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");
        StrMemberName = UtilitySharedPreferences.getPrefs(getApplicationContext(), "UserName");
        StrAFFLIATED_ABKMS = UtilitySharedPreferences.getPrefs(getApplicationContext(),"AFFLIATED_ABKMS");
        IS_ADMIN = UtilitySharedPreferences.getPrefs(getApplicationContext(),"IsAdmin");

        dateFormatter1 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        back_btn_toolbar = (ImageView)findViewById(R.id.back_btn_toolbar);
        back_btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView til_text = (TextView)findViewById(R.id.til_text);
        til_text.setText(getResources().getString(R.string.donate));


        tv_beneficiary_name= (TextView)findViewById(R.id.tv_beneficiary_name);
        tv_account_no= (TextView)findViewById(R.id.tv_account_no);
        tv_bank_name= (TextView)findViewById(R.id.tv_bank_name);
        tv_bank_ifsc= (TextView)findViewById(R.id.tv_bank_ifsc);
        tv_bank_branch= (TextView)findViewById(R.id.tv_bank_branch);

        AddDonorInfo = (Button)findViewById(R.id.AddDonorInfo);
        LayoutDonate = (LinearLayout)findViewById(R.id.LayoutDonate);
        if(IS_ADMIN!=null && !IS_ADMIN.equalsIgnoreCase("") && !IS_ADMIN.equalsIgnoreCase("null")){
            if(IS_ADMIN.equalsIgnoreCase("1")){
                AddDonorInfo.setVisibility(View.VISIBLE);
            }else {
                AddDonorInfo.setVisibility(View.GONE);
            }

        }


        LayoutDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpDonation();
            }
        });


        AddDonorInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpToAddDonor();
            }
        });


        LayoutTabAllDonation= (LinearLayout)findViewById(R.id.LayoutTabAllDonation);
        LayoutTabMyDonation= (LinearLayout)findViewById(R.id.LayoutTabMyDonation);
        LayoutTabAllDonation.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        LayoutTabMyDonation.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));

        ll_parent_donor_list = (LinearLayout) findViewById(R.id.ll_parent_donor_list);
        ll_parent_my_donation_list= (LinearLayout) findViewById(R.id.ll_parent_my_donation_list);


        LayoutTabAllDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutTabAllDonation.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                LayoutTabMyDonation.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                ll_parent_donor_list.setVisibility(View.VISIBLE);
                ll_parent_my_donation_list.setVisibility(View.GONE);
                fetchAllDonorList();
            }
        });

        LayoutTabMyDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutTabAllDonation.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                LayoutTabMyDonation.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                ll_parent_donor_list.setVisibility(View.GONE);
                ll_parent_my_donation_list.setVisibility(View.VISIBLE);
                fetchMyDonationList();
            }
        });


        myDialog = new ProgressDialog(DonationActivity.this);
        myDialog.setMessage(getResources().getString(R.string.please_wait));
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(true);

        getBankDetail();

        ll_parent_donor_list.setVisibility(View.VISIBLE);
        ll_parent_my_donation_list.setVisibility(View.GONE);
        fetchAllDonorList();
    }

    private void showPopUpDonation() {
        DialogDonation = new Dialog(DonationActivity.this);
        DialogDonation.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogDonation.setCanceledOnTouchOutside(false);
        DialogDonation.setCancelable(false);
        DialogDonation.setContentView(R.layout.layout_dailog_donation_tab);
        Objects.requireNonNull(DialogDonation.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView)DialogDonation.findViewById(R.id.title) ;
        ImageView iv_close = (ImageView)DialogDonation.findViewById(R.id.iv_close);


        TextView tv_member_id = (TextView)DialogDonation.findViewById(R.id.tv_member_id) ;
        tv_member_id.setText(StrMemberId);

        EditText EdtMemberName = (EditText)DialogDonation.findViewById(R.id.EdtMemberName);
        EditText EdtDonationFor = (EditText)DialogDonation.findViewById(R.id.EdtDonationFor);
        EditText EdtDonationAmount = (EditText)DialogDonation.findViewById(R.id.EdtDonationAmount);

        if(StrMemberName!=null && !StrMemberName.equalsIgnoreCase("")){
            EdtMemberName.setText(StrMemberName);
        }

        InputMethodManager mgr1 = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr1 != null) {
            mgr1.showSoftInput(EdtDonationAmount, InputMethodManager.SHOW_FORCED);
        }
        Button Btn_submit = (Button)DialogDonation.findViewById(R.id.Btn_submit);


        DialogDonation.show();
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DialogDonation!=null && DialogDonation.isShowing()) {
                    DialogDonation.dismiss();
                }
            }
        });



        Btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StrDonorId = tv_member_id.getText().toString();
                StrDonorName = EdtMemberName.getText().toString();
                StrDonorCityName = StrAFFLIATED_ABKMS;
                StrDonationFor = EdtDonationFor.getText().toString();
                StrDonationAmount = EdtDonationAmount.getText().toString();
                StrDonationDate = CommonMethods.DisplayCurrentDate();

                if(StrDonorName!=null && !StrDonorName.equalsIgnoreCase("")) {

                    if(StrDonationAmount!=null && !StrDonationAmount.equalsIgnoreCase("")) {

                        Call_UPI();

                    }else {
                        CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Donation Amount");
                    }

                }else {
                     CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Donor Name");
                }

            }
        });
    }

    private void Call_UPI() {



        Uri uri =
                new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", StrUpiAccountId)       // virtual ID
                        .appendQueryParameter("pn", StrUPI_MerchantName)          // name
                        .appendQueryParameter("am", StrDonationAmount)           // amount
                        .appendQueryParameter("cu", "INR")                         // currency
                        .build();

        /*String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
        int GOOGLE_PAY_REQUEST_CODE = 123;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
        startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
        */

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
           CommonMethods.DisplayToastWarning(getApplicationContext(),"No UPI app found, please install one to continue");
        }
       /* String Uiid_id = UUID.randomUUID().toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        double total_amt = Double.valueOf(StrDonationAmount);
        StrDonationAmount = String.format("%.2f", total_amt);
        easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa(StrUpiAccountId)
                .setPayeeName(StrUPI_MerchantName)
                .setTransactionId(timestamp.toString())
                .setTransactionRefId(Uiid_id)
                .setDescription("DONATION")
                .setAmount(StrDonationAmount)
                .build();

        //easyUpiPayment.setDefaultPaymentApp(PaymentApp.GOOGLE_PAY);
        easyUpiPayment.startPayment();

        easyUpiPayment.setPaymentStatusListener(this);*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response "+resultCode );
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }
    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(DonationActivity.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }
            if (status.equals("success")) {
                //Code to handle successful transaction here.

                API_SAVE_DONOR_INFO();
                Log.e("UPI", "payment successfull: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                CommonMethods.DisplayToastWarning(getApplicationContext(), "Payment cancelled by user.");
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
            }
            else {
                CommonMethods.DisplayToastError(getApplicationContext(), "Transaction failed.Please try again");
                Log.e("UPI", "failed payment: "+approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            CommonMethods.DisplayToastInfo(getApplicationContext(), "Internet connection is not available. Please check and try again");
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    private void getMemberNameList() {

        memberNameList = new ArrayList<>();
        memberCodeList = new ArrayList<>();
        memberCityList = new ArrayList<>();
        memberCodeList.add("0");
        memberNameList.add("Select Member Name who has donated");
        memberCityList.add("");

        String Uiid_id = UUID.randomUUID().toString();

        String URL_FetchQuaListAPI = ROOT_URL+"get_member_name_list.php?_"+Uiid_id;
        try {
            Log.d("URL_FetchQuaListAPI",URL_FetchQuaListAPI);

            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FetchQuaListAPI,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    Log.d("mainResponse", response);

                                    JSONObject jobj = new JSONObject(response);
                                    boolean status = jobj.getBoolean("status");
                                    String message = jobj.getString("message");

                                    if (status) {
                                        JSONArray dataArray = jobj.getJSONArray("result");

                                        for(int i = 0; i<dataArray.length();i++){

                                            JSONObject jsonObject = dataArray.getJSONObject(i);
                                            String id = jsonObject.getString("id");
                                            String name = jsonObject.getString("NAME_EN");
                                            String CITY = jsonObject.getString("CITY");

                                            memberCodeList.add(id);
                                            memberNameList.add(name);
                                            memberCityList.add(CITY);


                                        }



                                    }

                                }catch (Exception e ){
                                    e.printStackTrace();

                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        VolleyLog.d("volley", "Error: " + error.getMessage());
                        error.printStackTrace();
                        CommonMethods.DisplayToastWarning(getApplicationContext(),"Something went wrong. Please try again later.");
                    }
                });

                int socketTimeout = 50000; //30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                // RequestQueue requestQueue = Volley.newRequestQueue(context, new HurlStack(null, getSocketFactory()));
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

            } else {

                CommonMethods.DisplayToast(getApplicationContext(), "Please check your internet connection");
            }
        } catch (Exception e) {

            e.printStackTrace();

        }



    }

    private void showPopUpToAddDonor() {

        DialogAddDonor = new Dialog(DonationActivity.this);
        DialogAddDonor.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogAddDonor.setCanceledOnTouchOutside(false);
        DialogAddDonor.setCancelable(false);
        DialogAddDonor.setContentView(R.layout.layout_dailog_donor_info);
        Objects.requireNonNull(DialogAddDonor.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView)DialogAddDonor.findViewById(R.id.title) ;
        ImageView iv_close = (ImageView)DialogAddDonor.findViewById(R.id.iv_close);


        SearchableSpinner Spn_MemberName = (SearchableSpinner) DialogAddDonor.findViewById(R.id.Spn_MemberName);
        EditText EdtDonorCityName = (EditText)DialogAddDonor.findViewById(R.id.EdtDonorCityName);

        EditText EdtDonationFor = (EditText)DialogAddDonor.findViewById(R.id.EdtDonationFor);
        EditText EdtDonationAmount = (EditText)DialogAddDonor.findViewById(R.id.EdtDonationAmount);
        Edt_DonationDate = (EditText)DialogAddDonor.findViewById(R.id.Edt_DonationDate);

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, memberNameList);
        Spn_MemberName.setAdapter(countryAdapter);

        Spn_MemberName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = Spn_MemberName.getSelectedItemPosition();
                if(pos>0){
                    String memberCity = memberCityList.get(pos).toString();
                    EdtDonorCityName.setText(memberCity);
                    StrDonorName = Spn_MemberName.getSelectedItem().toString();
                    StrDonorId = memberCodeList.get(pos).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        InputMethodManager mgr1 = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr1 != null) {
            mgr1.showSoftInput(Edt_DonationDate, InputMethodManager.SHOW_FORCED);
        }
        setDateTimeField();
        Button Btn_submit = (Button)DialogAddDonor.findViewById(R.id.Btn_submit);


        DialogAddDonor.show();
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DialogAddDonor!=null && DialogAddDonor.isShowing()) {
                    DialogAddDonor.dismiss();
                }
            }
        });



        Btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StrDonorName = Spn_MemberName.getSelectedItem().toString();
                StrDonorCityName = EdtDonorCityName.getText().toString();
                StrDonationFor = EdtDonationFor.getText().toString();
                StrDonationAmount = EdtDonationAmount.getText().toString();
                StrDonationDate = Edt_DonationDate.getText().toString();

                if(StrDonorName!=null && !StrDonorName.equalsIgnoreCase("")) {

                    if(StrDonorCityName!=null && !StrDonorCityName.equalsIgnoreCase("")) {

                        if(StrDonationAmount!=null && !StrDonationAmount.equalsIgnoreCase("")) {

                            if(StrDonationDate!=null && !StrDonationDate.equalsIgnoreCase("")) {

                                API_SAVE_DONOR_INFO();

                            }else {
                                CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Date Of Donation");
                            }


                        }else {
                            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Donation Amount");
                        }

                    }else {
                        CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Donor City / State Name");
                    }

                }else {
                    CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Donor Name");
                }

            }
        });

    }

    private void setDateTimeField() {

        Edt_DonationDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        //newCalendar.add(Calendar.YEAR, -18);


        dobDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                StrDonationDate = dateFormatter1.format(newDate.getTime());

                Edt_DonationDate.setText(StrDonationDate);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        //dobDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.Edt_DonationDate) {

            dobDatePickerDialog.show();
        }
    }

    private void API_SAVE_DONOR_INFO() {

        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();
        String URL_ADD_DONOR_DETAIL = ROOT_URL + "add_donor_info.php?_"+Uiid_id;
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
                                        if(DialogAddDonor!=null && DialogAddDonor.isShowing()) {
                                            DialogAddDonor.dismiss();
                                        }
                                        if(DialogDonation!=null && DialogDonation.isShowing()) {
                                            DialogDonation.dismiss();
                                        }



                                        fetchAllDonorList();

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
                        params.put("donor_id",StrDonorId);
                        params.put("donor_name",StrDonorName);
                        params.put("donor_address",StrDonorCityName);
                        params.put("donation_amount",StrDonationAmount);
                        params.put("donation_for",StrDonationFor);
                        params.put("donation_date",StrDonationDate);
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

    private void getBankDetail() {
        String Uiid_id = UUID.randomUUID().toString();
        final String get_bank_details_info = ROOT_URL +"get_bank_details.php?_"+Uiid_id;
        Log.d("URL --->", get_bank_details_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_bank_details_info, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Response",""+response);

                            JSONObject Jobj = new JSONObject(response);

                            boolean status = Jobj.getBoolean("status");

                            if(status) {

                                String data = Jobj.getString("data");
                                JSONObject jobject = new JSONObject(data);

                                String Id = jobject.getString("id");
                                String beneficiary_name = jobject.getString("beneficiary_name");
                                String bank_name = jobject.getString("bank_name");
                                String account_no = jobject.getString("account_no");
                                String ifsc_code = jobject.getString("ifsc_code");
                                StrUpiAccountId = jobject.getString("upi_id");
                                StrUPI_MerchantName = jobject.getString("upi_merchant_name");
                                String branch = jobject.getString("branch");


                                tv_beneficiary_name.setText(beneficiary_name);
                                tv_account_no.setText(account_no);
                                tv_bank_name.setText(bank_name);
                                tv_bank_ifsc.setText(ifsc_code);
                                tv_bank_branch.setText(branch);

                            }

                        } catch (Exception e) {
                            Log.d("Exception",e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        //Log.d("Vollley Err", volleyError.toString());
                       CommonMethods.DisplayToast(getApplicationContext(),"Server Error. Please try again later.");
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(20),0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                requestQueue.add(stringRequest);
            }else {
                CommonMethods.DisplayToast(this,"No Internet Connection");
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }

    private void fetchAllDonorList() {
        myDialog.show();

        if(ll_parent_donor_list!=null && ll_parent_donor_list.getChildCount()>0){
            ll_parent_donor_list.removeAllViews();
        }

        String Uiid_id = UUID.randomUUID().toString();


        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = ROOT_URL + "fetch_donor_list.php?_" + Uiid_id;
            Log.d("URL", "--> " + URL);
            StringRequest postrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }

                        Log.d("URL Response", "--> " + response);

                        JSONObject jsonresponse = new JSONObject(response);

                        boolean status = jsonresponse.getBoolean("status");
                        if(status) {
                            String result = jsonresponse.getString("result");
                            JSONArray resultArry = new JSONArray(result);
                            if (resultArry.length() > 0) {


                                for (int i = 0; i < resultArry.length(); i++) {

                                    JSONObject customer_inspect_obj = resultArry.getJSONObject(i);

                                    String id = customer_inspect_obj.getString("id");
                                    String donation_for = customer_inspect_obj.getString("donation_for");
                                    String donor_name = customer_inspect_obj.getString("donor_name");
                                    String donor_member_id = customer_inspect_obj.getString("donor_member_id");
                                    String donation_amount = customer_inspect_obj.getString("donation_amount");
                                    String donation_date = customer_inspect_obj.getString("donation_date");


                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View rowView = Objects.requireNonNull(inflater).inflate(R.layout.row_donation_info, null);
                                    rowView.setPadding(10, 10, 10, 10);

                                    TextView tv_id = (TextView) rowView.findViewById(R.id.tv_id);
                                    TextView tv_name = (TextView) rowView.findViewById(R.id.tv_name);
                                    TextView row_donation_for = (TextView) rowView.findViewById(R.id.row_donation_for);
                                    TextView tv_donation_date = (TextView) rowView.findViewById(R.id.tv_donation_date);
                                    TextView tv_amount = (TextView) rowView.findViewById(R.id.tv_amount);
                                    LinearLayout LayoutDonationFor = (LinearLayout)rowView.findViewById(R.id.LayoutDonationFor);
                                    ImageView iv_delete = (ImageView) rowView.findViewById(R.id.iv_delete);

                                    if (IS_ADMIN != null && !IS_ADMIN.equalsIgnoreCase("") && !IS_ADMIN.equalsIgnoreCase("null")) {
                                        if (IS_ADMIN.equalsIgnoreCase("1")) {
                                            iv_delete.setVisibility(View.VISIBLE);
                                        } else {
                                            iv_delete.setVisibility(View.GONE);
                                        }

                                    }

                                    tv_id.setText(id);
                                    tv_name.setText(donor_name);
                                    if(donation_for!=null && !donation_for.equalsIgnoreCase("") && !donation_for.equalsIgnoreCase("null")) {
                                        row_donation_for.setText(donation_for);
                                        LayoutDonationFor.setVisibility(View.VISIBLE);
                                    }else {
                                        LayoutDonationFor.setVisibility(View.GONE);
                                    }
                                    tv_donation_date.setText(donation_date);
                                    tv_amount.setText("\u20B9 " + donation_amount);

                                    iv_delete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(DonationActivity.this);
                                            builder.setTitle("Delete !!!");
                                            builder.setMessage("Are you sure you want to delete this entry ?");
                                            builder.setCancelable(false);
                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DeleteDonorDetails(tv_id.getText().toString());
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


                                    ll_parent_donor_list.addView(rowView);
                                    ll_parent_donor_list.setVisibility(View.VISIBLE);
                                    ll_parent_my_donation_list.setVisibility(View.GONE);
                                }

                            } else {
                                TextView textView = new TextView(getApplicationContext());
                                textView.setText("No Data found...");
                                textView.setTextColor(getResources().getColor(R.color.red_close));
                                textView.setPadding(10, 10, 10, 10);

                                ll_parent_donor_list.addView(textView);

                            }
                        }else {
                            TextView textView = new TextView(getApplicationContext());
                            textView.setText("No Data found...");
                            textView.setTextColor(getResources().getColor(R.color.red_close));
                            textView.setPadding(10, 10, 10, 10);

                            ll_parent_donor_list.addView(textView);
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
                public void onErrorResponse(VolleyError volleyError) {
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    volleyError.printStackTrace();
                    CommonMethods.DisplayToastWarning(getApplicationContext(), "Something goes wrong. Please try again");
                    //Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                }
            }) ;
            int socketTimeout = 50000; //30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postrequest.setRetryPolicy(policy);
            // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postrequest);
        }else {
            CommonMethods.DisplayToastWarning(getApplicationContext(),"No Internet Connect");

        }
    }

    private void fetchMyDonationList() {
        myDialog.show();

        if(ll_parent_my_donation_list!=null && ll_parent_my_donation_list.getChildCount()>0){
            ll_parent_my_donation_list.removeAllViews();
        }

        String Uiid_id = UUID.randomUUID().toString();


        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = ROOT_URL + "fetch_my_donation_list.php?_" + Uiid_id+"&member_id="+StrMemberId;
            Log.d("URL", "--> " + URL);
            StringRequest postrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }

                        Log.d("URL Response", "--> " + response);

                        JSONObject jsonresponse = new JSONObject(response);
                        boolean status = jsonresponse.getBoolean("status");
                        if(status) {
                            String result = jsonresponse.getString("result");
                            JSONArray resultArry = new JSONArray(result);
                            if (resultArry.length() > 0) {


                                for (int i = 0; i < resultArry.length(); i++) {

                                    JSONObject customer_inspect_obj = resultArry.getJSONObject(i);

                                    String id = customer_inspect_obj.getString("id");
                                    String donation_for = customer_inspect_obj.getString("donation_for");
                                    String donor_name = customer_inspect_obj.getString("donor_name");

                                    String donation_amount = customer_inspect_obj.getString("donation_amount");
                                    String donation_date = customer_inspect_obj.getString("donation_date");


                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View rowView = Objects.requireNonNull(inflater).inflate(R.layout.row_donation_info, null);
                                    rowView.setPadding(10, 10, 10, 10);

                                    TextView tv_id = (TextView)rowView.findViewById(R.id.tv_id);
                                    TextView tv_name = (TextView)rowView.findViewById(R.id.tv_name);
                                    TextView row_donation_for = (TextView)rowView.findViewById(R.id.row_donation_for);
                                    TextView tv_donation_date = (TextView)rowView.findViewById(R.id.tv_donation_date);
                                    TextView tv_amount = (TextView)rowView.findViewById(R.id.tv_amount);
                                    LinearLayout LayoutDonationFor = (LinearLayout)rowView.findViewById(R.id.LayoutDonationFor);

                                    ImageView iv_delete = (ImageView)rowView.findViewById(R.id.iv_delete);
                                    iv_delete.setVisibility(View.GONE);

                                    tv_id.setText(id);
                                    tv_name.setText(donor_name);
                                    tv_donation_date.setText(donation_date);
                                    tv_amount.setText("\u20B9 "+donation_amount);

                                    if(donation_for!=null && !donation_for.equalsIgnoreCase("") && !donation_for.equalsIgnoreCase("null")) {
                                        row_donation_for.setText(donation_for);
                                        LayoutDonationFor.setVisibility(View.VISIBLE);
                                    }else {
                                        LayoutDonationFor.setVisibility(View.GONE);
                                    }

                                    ll_parent_my_donation_list.addView(rowView);
                                    ll_parent_donor_list.setVisibility(View.GONE);
                                    ll_parent_my_donation_list.setVisibility(View.VISIBLE);
                                }

                            }else {
                                TextView textView = new TextView(getApplicationContext());
                                textView.setText("No Data found...");
                                textView.setTextColor(getResources().getColor(R.color.red_close));
                                textView.setPadding(10,10,10,10);

                                ll_parent_my_donation_list.addView(textView);

                            }
                        }else{
                            TextView textView = new TextView(getApplicationContext());
                            textView.setText("No Data found...");
                            textView.setTextColor(getResources().getColor(R.color.red_close));
                            textView.setPadding(10,10,10,10);

                            ll_parent_my_donation_list.addView(textView);
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
                public void onErrorResponse(VolleyError volleyError) {
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    volleyError.printStackTrace();
                    CommonMethods.DisplayToastWarning(getApplicationContext(), "Something goes wrong. Please try again");
                    //Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                }
            }) ;
            int socketTimeout = 50000; //30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postrequest.setRetryPolicy(policy);
            // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postrequest);
        }else {
            CommonMethods.DisplayToastWarning(getApplicationContext(),"No Internet Connect");

        }
    }

    private void DeleteDonorDetails(String donationId) {

        Log.d("DeleteID",""+donationId);
        String Uiid_id = UUID.randomUUID().toString();

        String API_DeleteDonationInfo = ROOT_URL + "delete_donation_info.php?_" + Uiid_id;
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
                                        fetchAllDonorList();
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
                        params.put("DonationId", donationId);
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
        startActivity(intent);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}