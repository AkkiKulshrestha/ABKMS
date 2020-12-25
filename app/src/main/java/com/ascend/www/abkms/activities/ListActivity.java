package com.ascend.www.abkms.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import com.ascend.www.abkms.model.MatdarListModel;
import com.ascend.www.abkms.model.Member;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.utils.MyValidator;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;
import com.ascend.www.abkms.webservices.RestClient;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.ascend.www.abkms.utils.CommonMethods.EXCEL_FILE_PATH;
import static com.ascend.www.abkms.utils.CommonMethods.ucFirst;
import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;

public class ListActivity extends AppCompatActivity {

    ImageView back_btn_toolbar;
    TextView til_text;
    CardView CardOverAllList,CardSearchByState,CardSearchByCity,CardSearchByAffiliatedSabha,CardSearchByMobile,CardFamousKulshrestha,CardDownloadReport,CardAddMember,CardSearchActiveMembers;
    String LanguageSelected,StrMemberId,StrEditMemberId="",IS_ADMIN;
    Dialog DialogFilters;
    SearchableSpinner Spn_AffiliatedAbkms;
    Spinner Spn_FilterMonthlyIncome,Spn_FilterGender,Spn_FilterMaritalStatus;
    String StrAffiliatedAbkms,StrMonthlyIncome,StrFilterGender,StrMaritalStatus;
    ProgressDialog myDialog;
    ArrayList<String> affiliatedSabhaNameList = new ArrayList<String>();
    ArrayList<String> affiliatedSabhaCodeList = new ArrayList<String>();
    boolean showWhere = false;
    ArrayList<MatdarListModel> matdarModel_list = new ArrayList<>();
    MatdarListModel matdaarModel;
    ArrayList<String> filterList = new ArrayList<String>();
    int filter_size = 0;
    private static List<Member> members = new ArrayList<Member>();
    String SystemOtp,StrMobileNo="",StrEmailId="",StrName="",SavedPassword="",StrPassword="",IS_PASSWORD_SET="";
    EditText edt_member_name,edt_member_mobile,edt_member_email,edt_password;
    Dialog DialogRegisterUser;
    public static String[] columns = { "SNo", "Name / Marital Status", "Father / Origin From (Mool Niwas)",
            "DOB (Age)", "Occupation" , "Mob No. / Alternate Mob. No." , "Spouse Name / Occupation / Mob. No", "Children Status", "Email",
            "Postal Address", "CONTRIBUTION", "FAMILY MONTHLY INCOME", "AFFILIATED ABKMS" };

    private final static int FILE_REQUEST_CODE = 1;
   // private FileListAdapter fileListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yadya_list_filters);

        findViewByIds();
    }

    private void findViewByIds() {

        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");
        IS_ADMIN = UtilitySharedPreferences.getPrefs(getApplicationContext(),"IsAdmin");

        back_btn_toolbar = (ImageView)findViewById(R.id.back_btn_toolbar);
        back_btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView til_text = (TextView)findViewById(R.id.til_text);
        til_text.setText(getResources().getString(R.string.list));
        LanguageSelected = UtilitySharedPreferences.getPrefs(getApplicationContext(),"LanguageSelected");

        CardOverAllList = (CardView) findViewById(R.id.CardOverAllList);
        CardOverAllList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitySharedPreferences.setPrefs(getApplicationContext(),"yadi_filter_by","");
                Intent intent = new Intent(getApplicationContext(), MembersListActivity.class);
                intent.putExtra("filter_by", "all_list");
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
            }
        });

        CardSearchByState  = (CardView) findViewById(R.id.CardSearchByState);
        CardSearchByState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SearchFilterActivity.class);
                intent.putExtra("search_filter_by", "state");
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });

        CardSearchByCity= (CardView) findViewById(R.id.CardSearchByCity);
        CardSearchByCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchFilterActivity.class);
                intent.putExtra("search_filter_by", "city");
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });

        CardSearchByAffiliatedSabha= (CardView) findViewById(R.id.CardSearchByAffiliatedSabha);
        CardSearchByAffiliatedSabha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchFilterActivity.class);
                intent.putExtra("search_filter_by", "affiliated_sabha");
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });

        CardSearchByMobile  = (CardView) findViewById(R.id.CardSearchByMobile);
        CardSearchByMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitySharedPreferences.setPrefs(getApplicationContext(),"yadi_filter_by","");
                Intent intent = new Intent(getApplicationContext(), MembersListActivity.class);
                intent.putExtra("filter_by", "all_list");
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
            }
        });

        CardSearchActiveMembers = (CardView)findViewById(R.id.CardSearchActiveMembers);
        CardSearchActiveMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitySharedPreferences.setPrefs(getApplicationContext(),"yadi_filter_by","");
                Intent intent = new Intent(getApplicationContext(), MembersListActivity.class);
                intent.putExtra("filter_by", "active_members");
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
            }
        });


        CardFamousKulshrestha = (CardView) findViewById(R.id.CardFamousKulshrestha);
        CardFamousKulshrestha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FamousPersonsList.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });

        CardAddMember = (CardView) findViewById(R.id.CardAddMember);
        CardAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrEditMemberId = "";
                showPopupToRegisterUser();
            }
        });

        CardDownloadReport= (CardView) findViewById(R.id.CardDownloadReport);
        if(IS_ADMIN!=null && !IS_ADMIN.equalsIgnoreCase("") && !IS_ADMIN.equalsIgnoreCase("null")){
            if(IS_ADMIN.equalsIgnoreCase("1")){
                CardDownloadReport.setVisibility(View.VISIBLE);
            }else {
                CardDownloadReport.setVisibility(View.GONE);

            }
        }
        CardDownloadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ViewFiltersDialog();

                Intent intent3 = new Intent(ListActivity.this, PdfActivity.class);
                startActivity(intent3);

            }
        });


        getAffiliatedAbkmsList();


    }



    private void getAffiliatedAbkmsList() {

        affiliatedSabhaNameList = new ArrayList<>();
        affiliatedSabhaCodeList = new ArrayList<>();
        affiliatedSabhaCodeList.add("0");
        affiliatedSabhaNameList.add("Select Affiliated Sabha");

        String Uiid_id = UUID.randomUUID().toString();

        String URL_FetchQuaListAPI = ROOT_URL+"get_affiliated_abkms_list.php?_"+Uiid_id;
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
                                            String name = jsonObject.getString("city_name");


                                            affiliatedSabhaCodeList.add(id);
                                            affiliatedSabhaNameList.add(name);



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


    private void showPopupToRegisterUser() {

        DialogRegisterUser = new Dialog(ListActivity.this);
        DialogRegisterUser.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogRegisterUser.setCanceledOnTouchOutside(true);
        DialogRegisterUser.setCancelable(true);
        DialogRegisterUser.setContentView(R.layout.pop_register_user);
        DialogRegisterUser.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView)DialogRegisterUser.findViewById(R.id.title) ;
        TextView tv_member_id = (TextView)DialogRegisterUser.findViewById(R.id.tv_member_id) ;
        edt_member_name = (EditText)DialogRegisterUser.findViewById(R.id.edt_member_name);
        edt_member_email = (EditText)DialogRegisterUser.findViewById(R.id.edt_member_email);
        edt_member_mobile = (EditText)DialogRegisterUser.findViewById(R.id.edt_member_mobile);
        edt_password = (EditText)DialogRegisterUser.findViewById(R.id.edt_password);

        ImageView iv_close = (ImageView)DialogRegisterUser.findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DialogRegisterUser!=null && DialogRegisterUser.isShowing()){
                    DialogRegisterUser.dismiss();
                }

            }
        });

        Button btn_create_profile = (Button)DialogRegisterUser.findViewById(R.id.BtnSubmit);

        DialogRegisterUser.show();

        btn_create_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isValidRegisterFields()) {
                    StrName = edt_member_name.getText().toString().trim();
                    StrMobileNo = edt_member_mobile.getText().toString().trim();
                    StrEmailId = edt_member_email.getText().toString();
                    StrPassword = edt_password.getText().toString();

                    registerUserAPI();
                }
            }
        });


    }

    private boolean isValidRegisterFields() {

        boolean result = true;

        if (!MyValidator.isValidName(edt_member_name)) {
            edt_member_name.requestFocus();
            result = false;
        }

        if (!MyValidator.isValidEmail(edt_member_email)) {
            edt_member_email.requestFocus();
            result = false;
        }

        if (!MyValidator.isValidMobile(edt_member_mobile)) {
            edt_member_mobile.requestFocus();
            result = false;
        }

        if (!MyValidator.isValidPassword(edt_password)) {
            edt_password.requestFocus();
            result = false;
        }


        return  result;
    }

    private void registerUserAPI() {
        myDialog = new ProgressDialog(ListActivity.this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();
        String URL_FetchDetails = ROOT_URL + "register_user.php?_" + Uiid_id;
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

                                    if (status) {


                                        if(DialogRegisterUser!=null && DialogRegisterUser.isShowing()){
                                            DialogRegisterUser.dismiss();
                                        }
                                        CommonMethods.DisplayToastSuccess(getApplicationContext(),"User Added Successfully.");

                                    }else {
                                        CommonMethods.DisplayToastWarning(getApplicationContext(),"Failed to register. Please try again later.");
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
                        params.put("member_id",StrEditMemberId);
                        params.put("member_name",StrName);
                        params.put("member_email", StrEmailId);
                        params.put("password",CommonMethods.md5(StrPassword));
                        params.put("member_mobile", StrMobileNo);
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


    private void ViewFiltersDialog() {


        DialogFilters = new Dialog(ListActivity.this);
        DialogFilters.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogFilters.setCanceledOnTouchOutside(true);
        DialogFilters.setCancelable(true);
        DialogFilters.setContentView(R.layout.popup_filters);
        Objects.requireNonNull(DialogFilters.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView)DialogFilters.findViewById(R.id.title) ;
        ImageView iv_close = (ImageView)DialogFilters.findViewById(R.id.iv_close);


        Spn_AffiliatedAbkms = (SearchableSpinner) DialogFilters.findViewById(R.id.Spn_AffiliatedAbkms);
        Spn_FilterMonthlyIncome = (Spinner)DialogFilters.findViewById(R.id.Spn_FilterMonthlyIncome);
        Spn_FilterGender = (Spinner) DialogFilters.findViewById(R.id.Spn_FilterGender);
        Spn_FilterMaritalStatus = (Spinner) DialogFilters.findViewById(R.id.Spn_FilterMaritalStatus);

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, affiliatedSabhaNameList);
        Spn_AffiliatedAbkms.setAdapter(countryAdapter);


        if(StrAffiliatedAbkms!=null && !StrAffiliatedAbkms.equalsIgnoreCase("")){
            int i = getIndex1(Spn_AffiliatedAbkms, StrAffiliatedAbkms);
            Spn_AffiliatedAbkms.setSelection(i);
        }


        if(StrMonthlyIncome!=null && !StrMonthlyIncome.equalsIgnoreCase("")){
            int i = getIndex(Spn_FilterMonthlyIncome, StrMonthlyIncome);
            Spn_FilterMonthlyIncome.setSelection(i);
        }


        if(StrFilterGender!=null && !StrFilterGender.equalsIgnoreCase("")){
            int i = getIndex(Spn_FilterGender, StrFilterGender);
            Spn_FilterGender.setSelection(i);
        }


        if(StrMaritalStatus!=null && !StrMaritalStatus.equalsIgnoreCase("")){
            int i = getIndex(Spn_FilterMaritalStatus, StrMaritalStatus);
            Spn_FilterMaritalStatus.setSelection(i);
        }

        Button BtnDownloadReport = (Button)DialogFilters.findViewById(R.id.BtnDownloadReport);
        Button BtnDownloadCompleteReport= (Button)DialogFilters.findViewById(R.id.BtnDownloadCompleteReport);
        DialogFilters.show();
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DialogFilters!=null && DialogFilters.isShowing()) {
                    DialogFilters.dismiss();
                }

            }
        });

        BtnDownloadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterList = new ArrayList<>();
                if(Spn_AffiliatedAbkms.getSelectedItemPosition()>0){
                    StrAffiliatedAbkms = Spn_AffiliatedAbkms.getSelectedItem().toString();
                    showWhere = true;
                    filterList.add("AFFILIATED_ABKMS");
                }else {
                    showWhere = false;
                    StrAffiliatedAbkms = "";
                }

                if(Spn_FilterMonthlyIncome.getSelectedItemPosition()>0){
                    StrMonthlyIncome = Spn_FilterMonthlyIncome.getSelectedItem().toString();
                    showWhere = true;
                    filterList.add("INCOME");
                }else {
                    showWhere = false;
                    StrMonthlyIncome = "";
                }


                if(Spn_FilterGender.getSelectedItemPosition()>0){
                    StrFilterGender = Spn_FilterGender.getSelectedItem().toString();
                    if(StrFilterGender.equalsIgnoreCase("Male")){
                        StrFilterGender = "M";
                    }else if(StrFilterGender.equalsIgnoreCase("Female")){
                        StrFilterGender = "F";
                    }
                    showWhere = true;
                    filterList.add("GENDER");
                }else {
                    showWhere = false;
                    StrFilterGender = "";
                }

                if(Spn_FilterMaritalStatus.getSelectedItemPosition()>0){
                    StrMaritalStatus = Spn_FilterMaritalStatus.getSelectedItem().toString();
                    showWhere = true;
                    filterList.add("MARITAL_STATUS");
                }else {
                    showWhere = false;
                    StrMaritalStatus = "";
                }

                filter_size = filterList.size();
                String sql_str = "";
                StringBuilder sb1 = new StringBuilder("SELECT * FROM `members`");

                // Appending the boolean value
                if(showWhere){
                    sb1.append(" WHERE ");
                    if(StrAffiliatedAbkms!=null && !StrAffiliatedAbkms.equalsIgnoreCase("")) {
                        if(filterList.contains("AFFILIATED_ABKMS") && filterList.size() > 1){
                            sb1.append("`AFFLIATED_ABKMS` = '" + StrAffiliatedAbkms + "' AND ");
                            //checkArray_N_REmove("AFFLIATED_ABKMS");
                            if(filterList!=null){

                                for(int k = 0; k < filter_size ; k++){

                                    String content_name = filterList.get(k).toString();
                                    Log.d("Filter_applied_name",""+content_name);
                                    if(content_name.equalsIgnoreCase("AFFLIATED_ABKMS")){
                                        filterList.remove("AFFLIATED_ABKMS");
                                    }

                                }

                                Log.d("AfterRemove",""+filterList.toString());
                                filter_size = filterList.size();
                            }
                        }else {
                            sb1.append("`AFFLIATED_ABKMS` = '" + StrAffiliatedAbkms + "'");
                        }

                    }

                    if(StrMonthlyIncome!=null && !StrMonthlyIncome.equalsIgnoreCase("")) {

                        if(filterList.contains("INCOME") && filter_size > 1){
                            sb1.append("`MONTHLY_FAMILY_INCOME` = '"+StrMonthlyIncome+"' AND ");
                            //checkArray_N_REmove("INCOME");
                            if(filterList!=null){

                                for(int k = 0; k < filter_size ; k++){

                                    String content_name = filterList.get(k).toString();
                                    Log.d("Filter_applied_name",""+content_name);
                                    if(content_name.equalsIgnoreCase("INCOME")){
                                        filterList.remove("INCOME");
                                    }

                                }

                                Log.d("AfterRemove",""+filterList.toString());
                                filter_size = filterList.size();
                            }
                        }else {
                            sb1.append("`MONTHLY_FAMILY_INCOME` = '"+StrMonthlyIncome+"'");
                        }
                    }

                    if(StrFilterGender!=null && !StrFilterGender.equalsIgnoreCase("")) {
                        if(filterList.contains("GENDER") && filter_size > 1){
                            sb1.append("`GENDER` = '"+StrFilterGender+"' AND ");
                            //checkArray_N_REmove("GENDER");
                            if(filterList!=null){

                                for(int k = 0; k < filter_size ; k++){

                                    String content_name = filterList.get(k).toString();
                                    Log.d("Filter_applied_name",""+content_name);
                                    if(content_name.equalsIgnoreCase("GENDER")){
                                        filterList.remove("GENDER");
                                    }

                                }

                                Log.d("AfterRemove",""+filterList.toString());
                                filter_size = filterList.size();
                            }
                        }else {
                            sb1.append("`GENDER` = '"+StrFilterGender+"'");
                        }

                    }


                    if(StrMaritalStatus!=null && !StrMaritalStatus.equalsIgnoreCase("")) {
                        if(filterList.contains("MARITAL_STATUS") && filter_size > 1){
                            sb1.append("`MARITAL_STATUS` = '"+StrMaritalStatus+"' AND ");
                            //checkArray_N_REmove("MARITAL_STATUS");
                            if(filterList!=null){

                                for(int k = 0; k < filter_size ; k++){

                                    String content_name = filterList.get(k).toString();
                                    Log.d("Filter_applied_name",""+content_name);
                                    if(content_name.equalsIgnoreCase("MARITAL_STATUS")){
                                        filterList.remove("MARITAL_STATUS");
                                    }

                                }

                                Log.d("AfterRemove",""+filterList.toString());
                                filter_size = filterList.size();
                            }
                        }else {
                            sb1.append("`MARITAL_STATUS` = '"+StrMaritalStatus+"'");
                        }

                    }

                    sb1.append(" ORDER BY `NAME_EN`");

                    sql_str = sb1.toString();
                    Log.d("sql_str", ""+sql_str);
                    if(sql_str.contains("AND  ORDER BY")){
                        sql_str = sql_str.replace("AND  ORDER BY"," ORDER BY");
                    }

                    if(sql_str.contains("WHERE  ORDER BY")){
                        sql_str = sql_str.replace("WHERE  ORDER BY"," ORDER BY");
                    }

                    Log.d("replaced_sql_str", ""+sql_str);
                }else {

                    sb1.append(" ORDER BY `NAME_EN`");
                    sql_str = sb1.toString();
                    Log.d("sql_str", ""+sql_str);
                }

                API_GET_DATA(sql_str);

            }
        });

        BtnDownloadCompleteReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sql_str = "SELECT * FROM `members` ORDER BY `NAME_EN`";

                API_GET_DATA(sql_str);
            }
        });
    }

    private void checkArray_N_REmove(String remove_object) {

        if(filterList!=null){

            for(int k = 0; k < filter_size ; k++){

                String content_name = filterList.get(k).toString();
                Log.d("Filter_applied_name",""+content_name);
                if(content_name.equalsIgnoreCase(remove_object)){
                    filterList.remove(remove_object);
                }

            }

            Log.d("AfterRemove",""+filterList.toString());
        }

    }

    private void API_GET_DATA(String Sql_Statement) {

        myDialog = new ProgressDialog(ListActivity.this);
        myDialog.setMessage(getResources().getString(R.string.please_wait));
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.show();
        if(matdarModel_list!=null && matdarModel_list.size()>0){
            matdarModel_list = new ArrayList<>();

        }
        String Uiid_id = UUID.randomUUID().toString();

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = RestClient.ROOT_URL + "download_report_list.php?_"+Uiid_id;
            Log.d("URL", "--> " + URL);
            StringRequest postrequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                        Log.d("URL Response", "--> " + response);

                        JSONObject jsonresponse = new JSONObject(response);

                        String result = jsonresponse.getString("result");
                        JSONArray resultArry = new JSONArray(result);
                        if (resultArry.length() > 0) {


                            for (int i = 0; i < resultArry.length(); i++) {

                                JSONObject customer_inspect_obj = resultArry.getJSONObject(i);

                                //Customer vehicle info
                                String ID = customer_inspect_obj.getString("ID");
                                String NAME = customer_inspect_obj.getString("NAME");
                                String FATHER_NAME = customer_inspect_obj.getString("FATHER_NAME");
                                String MOTHER_NAME = customer_inspect_obj.getString("MOTHER_NAME");
                                String MARITAL_STATUS = customer_inspect_obj.getString("MARITAL_STATUS");
                                String SPOUSE_NAME = customer_inspect_obj.getString("SPOUSE_NAME");
                                String SPOUSE_OCCUPATION = customer_inspect_obj.getString("SPOUSE_OCCUPATION");
                                String SPOUSE_MOBILE = customer_inspect_obj.getString("SPOUSE_MOBILE_NO");
                                String GENDER = customer_inspect_obj.getString("GENDER");
                                String AGE = customer_inspect_obj.getString("AGE");
                                String DateOfBirth = customer_inspect_obj.getString("DOB");
                                String FAMILY_MEMBER_COUNT = customer_inspect_obj.getString("FAMILY_MEMBER_COUNT");
                                String ADDRESS1 = customer_inspect_obj.getString("ADDRESS1");
                                String ADDRESS2 = customer_inspect_obj.getString("ADDRESS2");
                                String ADDRESS3 = customer_inspect_obj.getString("ADDRESS3");
                                String CITY = customer_inspect_obj.getString("CITY");
                                String STATE = customer_inspect_obj.getString("STATE");
                                String PINCODE = customer_inspect_obj.getString("PINCODE");
                                String MOBILE_NO = customer_inspect_obj.getString("MOBILE_NO");
                                String MOBILE_NO1 = customer_inspect_obj.getString("MOBILE_NO1");
                                String EMAIL_ID = customer_inspect_obj.getString("EMAIL_ID");
                                String WHATSAPP_NO = customer_inspect_obj.getString("WHATSAPP_NO");
                                String FAMILY_ORIGIN = customer_inspect_obj.getString("FAMILY_ORIGIN");
                                String OCCUPATION = customer_inspect_obj.getString("OCCUPATION");
                                String WORKS_WITH = customer_inspect_obj.getString("WORKS_WITH");
                                String MONTHLY_FAMILY_INCOME = customer_inspect_obj.getString("MONTHLY_FAMILY_INCOME");
                                String AFFLIATED_ABKMS = customer_inspect_obj.getString("AFFLIATED_ABKMS");
                                String CONTRIBUTION_FOR_ABKMS = customer_inspect_obj.getString("CONTRIBUTION_FOR_ABKMS");
                                String HAVE_CHILDREN = customer_inspect_obj.getString("HAVE_CHILDREN");
                                String CHILDREN_INFO = customer_inspect_obj.getString("CHILDREN_INFO");

                                Member member = new Member();
                                member.setSR_No(i+1);
                                member.setID(ID);
                                member.setNAME(NAME);
                                member.setMARITAL_STATUS(CommonMethods.SanitizeString(MARITAL_STATUS));
                                member.setFATHER_NAME(FATHER_NAME);
                                member.setSPOUSE_NAME(SPOUSE_NAME);
                                member.setGENDER(GENDER);
                                member.setDOB(DateOfBirth);
                                member.setAGE(AGE);
                                member.setFAMILY_MEMBER_COUNT(FAMILY_MEMBER_COUNT);
                                member.setADDRESS1(CommonMethods.SanitizeString(ADDRESS1));
                                member.setADDRESS2(CommonMethods.SanitizeString(ADDRESS2));
                                member.setADDRESS3(CommonMethods.SanitizeString(ADDRESS3));
                                member.setCITY(CITY);
                                member.setSTATE(STATE);
                                member.setPINCODE(PINCODE);
                                member.setMOBILE_NO(MOBILE_NO);
                                member.setMOBILE_NO1(MOBILE_NO1);
                                member.setWHATSAPP_NO(WHATSAPP_NO);
                                member.setEMAIL_ID(EMAIL_ID);
                                member.setAFFLIATED_ABKMS(AFFLIATED_ABKMS);
                                member.setMOTHER_NAME(MOTHER_NAME);
                                member.setSPOUSE_OCCUPATION(CommonMethods.SanitizeString(SPOUSE_OCCUPATION));
                                member.setSPOUSE_MOBILE(SPOUSE_MOBILE);
                                member.setFAMILY_ORIGIN(CommonMethods.SanitizeString(FAMILY_ORIGIN));
                                member.setOCCUPATION(CommonMethods.SanitizeString(OCCUPATION));
                                member.setWORKS_WITH(CommonMethods.SanitizeString(WORKS_WITH));
                                member.setMONTHLY_FAMILY_INCOME(CommonMethods.SanitizeString(MONTHLY_FAMILY_INCOME));
                                member.setCONTRIBUTION_FOR_ABKMS(CommonMethods.SanitizeString(CONTRIBUTION_FOR_ABKMS));
                                member.setHAVE_CHILDREN(CommonMethods.SanitizeString(HAVE_CHILDREN));
                                member.setCHILDREN_INFO(CommonMethods.SanitizeString(CHILDREN_INFO));

                                members.add(member);
                                /*members.add(new Member(i+1, ID, NAME,MARITAL_STATUS, FATHER_NAME, SPOUSE_NAME, GENDER, DateOfBirth,
                                        AGE,FAMILY_MEMBER_COUNT,ADDRESS1,ADDRESS2,ADDRESS3, CITY,
                                        STATE, PINCODE, MOBILE_NO, MOBILE_NO1, WHATSAPP_NO,EMAIL_ID, AFFLIATED_ABKMS,MOTHER_NAME,
                                        SPOUSE_OCCUPATION,SPOUSE_MOBILE,FAMILY_ORIGIN, OCCUPATION,WORKS_WITH,MONTHLY_FAMILY_INCOME,
                                        CONTRIBUTION_FOR_ABKMS,HAVE_CHILDREN, CHILDREN_INFO));*/


                            }

                        }

                        try {
                            exportEmailInCSV(members,EXCEL_FILE_PATH);
                            //CreateExcelFile(members,EXCEL_FILE_PATH);
                        } catch (IOException e) {
                            e.printStackTrace();
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
            }){

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }
                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    //params.put("Pan", StrClientPan);
                    params.put("sql", Sql_Statement);
                    Log.d("ParrasSQLdata",params.toString() );

                    return params;
                }



            };
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

   /* private void CreateExcelFile(List<Member> members, String excelFilePath) throws IOException {

        if(members!=null && members.size()>0) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String  filePath =  excelFilePath+"MEMBER_LIST_" + timestamp.getTime() + ".xls";
            Log.d("filePath",""+filePath);


            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet();

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            //headerFont.setColor(IndexedColors.RED.getIndex());

            //CellStyle headerCellStyle = workbook.createCellStyle();
            //headerCellStyle.setFont(headerFont);

            // Create a Row
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
               // cell.setCellStyle(headerCellStyle);
            }

            // Create Other rows and cells with contacts data
            int rowNum = 1;

            for (Member member : members) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(member.getSR_No());
                row.createCell(1).setCellValue(member.getNAME() + " \n" + member.getMARITAL_STATUS());
                row.createCell(2).setCellValue(member.getFATHER_NAME() + "\n" + member.getFAMILY_ORIGIN());
                row.createCell(3).setCellValue(member.getDOB() + " (" + member.getAGE() + ")");
                row.createCell(4).setCellValue(member.getOCCUPATION() + " \nAT " + member.getWORKS_WITH());
                row.createCell(5).setCellValue(member.getMOBILE_NO() + " / " + member.getMOBILE_NO1());
                row.createCell(6).setCellValue(member.getSPOUSE_NAME() + "\n" + member.getSPOUSE_OCCUPATION() + "\n " + member.getSPOUSE_MOBILE());
                row.createCell(7).setCellValue(ucFirst(member.getHAVE_CHILDREN()) + "\n" + member.getCHILDREN_INFO());
                row.createCell(8).setCellValue(member.getEMAIL_ID());
                row.createCell(9).setCellValue(member.getADDRESS1() + ", " + member.getADDRESS2() + ", " + member.getADDRESS3() + ", " + member.getCITY()
                        + ", " + member.getSTATE() + " - " + member.getPINCODE());
                row.createCell(10).setCellValue(member.getCONTRIBUTION_FOR_ABKMS());
                row.createCell(11).setCellValue(member.getMONTHLY_FAMILY_INCOME());
                row.createCell(12).setCellValue(member.getAFFLIATED_ABKMS());


            }

            // Resize all columns to fit the content size
         *//*   for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }*//*



            try (FileOutputStream fout = new FileOutputStream(filePath)) {
                workbook.write(fout);
                fout.flush();
                fout.close();
            }

        }


    }*/

    public void exportEmailInCSV(List<Member> members, String excelFilePath) throws IOException {
        {

            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/ABKMS");

            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();

            System.out.println("" + var);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
           // String  filePath =  excelFilePath+"MEMBER_LIST_" + timestamp.getTime() + ".xls";

            final String filename = folder.toString() + "/" + "MEMBER_LIST_" + timestamp.getTime() + ".csv";
            Log.d("filename",""+filename);
            // show waiting screen
            CharSequence contentTitle = getString(R.string.app_name);
            final ProgressDialog progDailog = ProgressDialog.show(
                    ListActivity.this, contentTitle, "Please wait...",
                    true);//please wait
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {     }
            };

            new Thread() {
                public void run() {
                    try {

                        FileWriter fw = new FileWriter(filename);

                        fw.append("SNo");
                        fw.append(',');

                        fw.append("Name - Marital Status");
                        fw.append(',');

                        fw.append("Father - Origin From (Mool Niwas)");
                        fw.append(',');

                        fw.append("DOB (Age)");
                        fw.append(',');

                        fw.append("Occupation");
                        fw.append(',');

                        fw.append("Mob No. - Alternate Mob. No.");
                        fw.append(',');

                        fw.append("Spouse Name - Occupation & Mob. No");
                        fw.append(',');

                        fw.append("Children Status");
                        fw.append(',');

                        fw.append("Email");
                        fw.append(',');

                        fw.append("Postal Address");
                        fw.append(',');

                        fw.append("CONTRIBUTION");
                        fw.append(',');

                        fw.append("FAMILY MONTHLY INCOME");
                        fw.append(',');

                        fw.append("AFFILIATED ABKMS");
                        fw.append(',');


                        fw.append('\n');

                        int rowNum = 1;

                        //for (Member member : members) {
                            //Keyboard.Row row = sheet.createRow(rowNum++);
                        for(int k=0;k < members.size(); k++ ){
                            fw.append(String.valueOf(members.get(k).getSR_No()));
                            fw.append(',');

                            if(members.get(k).getMARITAL_STATUS()!=null && !members.get(k).getMARITAL_STATUS().equalsIgnoreCase("") && !members.get(k).getMARITAL_STATUS().equalsIgnoreCase("null")) {
                                fw.append(members.get(k).getNAME() + " - " + members.get(k).getMARITAL_STATUS());
                            }else {
                                fw.append(members.get(k).getNAME());
                            }
                            fw.append(',');

                            if(members.get(k).getFAMILY_ORIGIN()!=null && !members.get(k).getFAMILY_ORIGIN().equalsIgnoreCase("") && !members.get(k).getFAMILY_ORIGIN().equalsIgnoreCase("null")) {
                                fw.append(members.get(k).getFATHER_NAME() + " - " + CommonMethods.SanitizeString(members.get(k).getFAMILY_ORIGIN()));
                            }else if(members.get(k).getFATHER_NAME()!=null && !members.get(k).getFATHER_NAME().equalsIgnoreCase("") && !members.get(k).getFATHER_NAME().equalsIgnoreCase("null")) {
                                fw.append(members.get(k).getFATHER_NAME());
                            }else {
                                fw.append("");
                            }
                            fw.append(',');

                            if(members.get(k).getAGE()!=null && !members.get(k).getAGE().equalsIgnoreCase("") && !members.get(k).getAGE().equalsIgnoreCase("null")) {
                                fw.append(members.get(k).getDOB() + " (" + members.get(k).getAGE() + ")");
                            }else if(members.get(k).getDOB()!=null && !members.get(k).getDOB().equalsIgnoreCase("") && !members.get(k).getDOB().equalsIgnoreCase("null")) {
                                fw.append(members.get(k).getDOB());
                            }else {
                                fw.append("");
                            }
                            fw.append(',');

                            if(members.get(k).getWORKS_WITH()!=null && !members.get(k).getWORKS_WITH().equalsIgnoreCase("") && !members.get(k).getWORKS_WITH().equalsIgnoreCase("null")) {
                                fw.append(CommonMethods.SanitizeString(members.get(k).getOCCUPATION()) + " AT " + CommonMethods.SanitizeString(members.get(k).getWORKS_WITH()));
                            }else if(members.get(k).getOCCUPATION()!=null && !members.get(k).getOCCUPATION().equalsIgnoreCase("") && !members.get(k).getOCCUPATION().equalsIgnoreCase("null")) {
                                fw.append(members.get(k).getOCCUPATION());
                            }else {
                                fw.append("");
                            }

                            fw.append(',');

                            if(members.get(k).getMOBILE_NO1()!=null && !members.get(k).getMOBILE_NO1().equalsIgnoreCase("") && !members.get(k).getMOBILE_NO1().equalsIgnoreCase("null")) {
                                fw.append(members.get(k).getMOBILE_NO() + " / " + members.get(k).getMOBILE_NO1());
                            }else if(members.get(k).getMOBILE_NO()!=null && !members.get(k).getMOBILE_NO().equalsIgnoreCase("") && !members.get(k).getMOBILE_NO().equalsIgnoreCase("null")) {
                                fw.append(members.get(k).getMOBILE_NO());
                            }else {
                                fw.append("");
                            }
                            fw.append(',');

                            if(members.get(k).getSPOUSE_NAME()!=null && !members.get(k).getSPOUSE_NAME().equalsIgnoreCase("") && !members.get(k).getSPOUSE_NAME().equalsIgnoreCase("null")) {
                                if(members.get(k).getSPOUSE_OCCUPATION()!=null && !members.get(k).getSPOUSE_OCCUPATION().equalsIgnoreCase("") && !members.get(k).getSPOUSE_OCCUPATION().equalsIgnoreCase("null")) {
                                    if(members.get(k).getSPOUSE_MOBILE()!=null && !members.get(k).getSPOUSE_MOBILE().equalsIgnoreCase("") && !members.get(k).getSPOUSE_MOBILE().equalsIgnoreCase("null")) {
                                        fw.append(members.get(k).getSPOUSE_NAME() + " : " + members.get(k).getSPOUSE_OCCUPATION() + " - " + members.get(k).getSPOUSE_MOBILE());
                                    }else {
                                        fw.append(members.get(k).getSPOUSE_NAME() + " : " + CommonMethods.SanitizeString(members.get(k).getSPOUSE_OCCUPATION()));
                                    }
                                }else {
                                    fw.append(members.get(k).getSPOUSE_NAME());
                                }
                            }else {
                                fw.append("");
                            }
                            fw.append(',');

                            if(members.get(k).getHAVE_CHILDREN()!=null && !members.get(k).getHAVE_CHILDREN().equalsIgnoreCase("") && !members.get(k).getHAVE_CHILDREN().equalsIgnoreCase("null")) {
                                if(members.get(k).getCHILDREN_INFO()!=null && !members.get(k).getCHILDREN_INFO().equalsIgnoreCase("") && !members.get(k).getCHILDREN_INFO().equalsIgnoreCase("null")) {
                                    fw.append(ucFirst(members.get(k).getHAVE_CHILDREN()) + " ( " + members.get(k).getCHILDREN_INFO() +" )");
                                }else {
                                    fw.append(ucFirst(members.get(k).getHAVE_CHILDREN()));
                                }
                            }else {
                                fw.append("");
                            }
                            fw.append(',');

                            if(members.get(k).getEMAIL_ID()!=null && !members.get(k).getEMAIL_ID().equalsIgnoreCase("") && !members.get(k).getEMAIL_ID().equalsIgnoreCase("null")) {
                                fw.append(members.get(k).getEMAIL_ID());
                            }else {
                                fw.append("");
                            }
                            fw.append(',');

                            if(members.get(k).getADDRESS1()!=null && !members.get(k).getADDRESS1().equalsIgnoreCase("") && !members.get(k).getADDRESS1().equalsIgnoreCase("null")) {
                                if(members.get(k).getADDRESS2()!=null && !members.get(k).getADDRESS2().equalsIgnoreCase("") && !members.get(k).getADDRESS2().equalsIgnoreCase("null")) {
                                    if(members.get(k).getADDRESS3()!=null && !members.get(k).getADDRESS3().equalsIgnoreCase("") && !members.get(k).getADDRESS3().equalsIgnoreCase("null")) {
                                        if(members.get(k).getCITY()!=null && !members.get(k).getCITY().equalsIgnoreCase("") && !members.get(k).getCITY().equalsIgnoreCase("null")) {
                                            if(members.get(k).getSTATE()!=null && !members.get(k).getSTATE().equalsIgnoreCase("") && !members.get(k).getSTATE().equalsIgnoreCase("null")) {
                                                if(members.get(k).getPINCODE()!=null && !members.get(k).getPINCODE().equalsIgnoreCase("") && !members.get(k).getPINCODE().equalsIgnoreCase("null")) {
                                                    fw.append(members.get(k).getADDRESS1() + " " + members.get(k).getADDRESS2() + " " + members.get(k).getADDRESS3() + " " + members.get(k).getCITY()
                                                            + " " + members.get(k).getSTATE() + " - " + members.get(k).getPINCODE());
                                                }else {
                                                    fw.append(members.get(k).getADDRESS1() + " " + members.get(k).getADDRESS2() + " " + members.get(k).getADDRESS3() + " " + members.get(k).getCITY()
                                                            + " " + members.get(k).getSTATE());
                                                }
                                            }else {
                                                fw.append(members.get(k).getADDRESS1() + " " + members.get(k).getADDRESS2() + " " + members.get(k).getADDRESS3() + " " + members.get(k).getCITY());
                                            }
                                        }else {
                                            fw.append(members.get(k).getADDRESS1() + " " + members.get(k).getADDRESS2() + " " + members.get(k).getADDRESS3());
                                        }
                                    }else {
                                        fw.append(members.get(k).getADDRESS1() + " " + members.get(k).getADDRESS2());
                                    }
                                }else {
                                    fw.append(members.get(k).getADDRESS1());
                                }
                            }else {
                                fw.append("");
                            }

                            fw.append(',');

                            if(members.get(k).getCONTRIBUTION_FOR_ABKMS()!=null && !members.get(k).getCONTRIBUTION_FOR_ABKMS().equalsIgnoreCase("") && !members.get(k).getCONTRIBUTION_FOR_ABKMS().equalsIgnoreCase("null")) {
                                fw.append(members.get(k).getCONTRIBUTION_FOR_ABKMS());
                            }else {
                                fw.append("");
                            }
                            fw.append(',');

                            if(members.get(k).getMONTHLY_FAMILY_INCOME()!=null && !members.get(k).getMONTHLY_FAMILY_INCOME().equalsIgnoreCase("") && !members.get(k).getMONTHLY_FAMILY_INCOME().equalsIgnoreCase("null")) {
                                fw.append(members.get(k).getMONTHLY_FAMILY_INCOME());
                            }else {
                                fw.append("");
                            }
                            fw.append(',');

                            if(members.get(k).getAFFLIATED_ABKMS()!=null && !members.get(k).getAFFLIATED_ABKMS().equalsIgnoreCase("") && !members.get(k).getAFFLIATED_ABKMS().equalsIgnoreCase("null")) {
                                fw.append(members.get(k).getAFFLIATED_ABKMS());
                            }else {
                                fw.append("");
                            }
                            fw.append(',');

                            fw.append('\n');


                        }


                        // fw.flush();
                        fw.close();

                    } catch (Exception e) {
                    }
                    handler.sendEmptyMessage(0);
                    progDailog.dismiss();
                }
            }.start();

        }

    }


    private int getIndex1(SearchableSpinner spinner, String searchString) {

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
        Intent intent = new Intent(getApplicationContext(), NewDashboard.class);
        intent.putExtra("lang_flag", LanguageSelected);
        startActivity(intent);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }
}
