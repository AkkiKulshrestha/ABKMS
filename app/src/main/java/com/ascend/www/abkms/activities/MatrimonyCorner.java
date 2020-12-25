package com.ascend.www.abkms.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.ascend.www.abkms.adapters.MatrimonyProfileAdapter;
import com.ascend.www.abkms.model.MatrimonyProfileListModel;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.utils.MyValidator;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;

public class MatrimonyCorner extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


        ImageView back_btn_toolbar;
        TextView til_text;
        String LanguageSelected;
        LinearLayout ParentLayoutProfileList;
        ProgressDialog myDialog;
        String IS_ADMIN;
        Button BtnAddProfile;
        Dialog DialogAddJob;
        EditText Edt_JobDate;
            SearchView editTextSearch;
        SwipeRefreshLayout refreshLayout;
        RecyclerView recycler_list;
       RecyclerView.LayoutManager layoutManager;
        private MatrimonyProfileAdapter matrimonyProfileAdapter;
        ArrayList<MatrimonyProfileListModel> matrimonyModel_list = new ArrayList<>();
        MatrimonyProfileListModel matrimonyProfileListModel;
        String StrMemberId = "", StrApplicantName = "", StrFatherName = "", StrMotherName = "", StrGender = "",StrGotra="", StrFamilyOrigin = "",
                StrResidingPlace="",StrWorkingStatus="",StrWorkingAs="",StrAnnunalIncome="",StrContactNo="",StrWhatsAppNo="",StrDateOfBirth="";
        private DatePickerDialog dobDatePickerDialog;
        private SimpleDateFormat dateFormatter, dateFormatter1;

        EditText EdtApplicantName,EdtFatherName,EdtMotherName,EdtFamilyOriginPlace,EdtResidencePlace,EdtContactNo,EdtGotra,EdtWhatAppNo,Edt_DateOfBirth;
        Spinner Spn_Gender,Spn_WorkingStatus,Spn_WorkingAs,Spn_AnnualIncome;
        int calculated_age=0;


        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_metrimony_list);

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
            til_text.setText(getResources().getString(R.string.matrimony));
            LanguageSelected = UtilitySharedPreferences.getPrefs(getApplicationContext(), "LanguageSelected");

           /* ParentLayoutProfileList = (LinearLayout) findViewById(R.id.ParentLayoutProfileList);*/
                editTextSearch = (SearchView) findViewById(R.id.editTextSearch);

                search(editTextSearch);

                recycler_list = (RecyclerView) findViewById(R.id.recycler_list);
                recycler_list.setHasFixedSize(true);
                refreshLayout= (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
                layoutManager = new LinearLayoutManager(this);
                recycler_list.setLayoutManager(layoutManager);



                refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                matrimonyModel_list.clear();
                                fetchProfileList();
                                matrimonyProfileAdapter.notifyDataSetChanged();
                                refreshLayout.setRefreshing(false);
                            }
                        },1000);
                        recycler_list.setItemAnimator(new DefaultItemAnimator());
                    }
                });


            myDialog = new ProgressDialog(MatrimonyCorner.this);
            myDialog.setMessage("Please wait...");
            myDialog.setCancelable(false);
            myDialog.setCanceledOnTouchOutside(false);

            BtnAddProfile = (Button) findViewById(R.id.BtnAddProfile);

            BtnAddProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopUpToAddProfile();
                }
            });

            fetchProfileList();

    }


    private void showPopUpToAddProfile () {

    DialogAddJob = new Dialog(MatrimonyCorner.this);
    DialogAddJob.requestWindowFeature(Window.FEATURE_NO_TITLE);
    DialogAddJob.setCanceledOnTouchOutside(false);
    DialogAddJob.setCancelable(false);
    DialogAddJob.setContentView(R.layout.layout_dailog_add_matrimony_profile);
    Objects.requireNonNull(DialogAddJob.getWindow()).setBackgroundDrawable(
            new ColorDrawable(android.graphics.Color.TRANSPARENT));
    TextView title = (TextView) DialogAddJob.findViewById(R.id.title);
    ImageView iv_close = (ImageView) DialogAddJob.findViewById(R.id.iv_close);


    EdtApplicantName = (EditText) DialogAddJob.findViewById(R.id.EdtApplicantName);
    EdtFatherName = (EditText) DialogAddJob.findViewById(R.id.EdtFatherName);
    EdtMotherName = (EditText) DialogAddJob.findViewById(R.id.EdtMotherName);
    EdtGotra = (EditText) DialogAddJob.findViewById(R.id.EdtGotra);
    EdtFamilyOriginPlace = (EditText) DialogAddJob.findViewById(R.id.EdtFamilyOriginPlace);
    EdtResidencePlace = (EditText) DialogAddJob.findViewById(R.id.EdtResidencePlace);
    EdtContactNo = (EditText) DialogAddJob.findViewById(R.id.EdtContactNo);
    EdtWhatAppNo = (EditText) DialogAddJob.findViewById(R.id.EdtWhatAppNo);
    Edt_DateOfBirth= (EditText) DialogAddJob.findViewById(R.id.Edt_DateOfBirth);
    InputMethodManager mgr1 = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    if (mgr1 != null) {
        mgr1.showSoftInput(Edt_DateOfBirth, InputMethodManager.SHOW_FORCED);
    }
    setDateTimeField();
    Spn_Gender = (Spinner) DialogAddJob.findViewById(R.id.Spn_Gender);
    Spn_WorkingStatus = (Spinner) DialogAddJob.findViewById(R.id.Spn_WorkingStatus);
    Spn_WorkingAs = (Spinner) DialogAddJob.findViewById(R.id.Spn_WorkingAs);
    Spn_AnnualIncome = (Spinner) DialogAddJob.findViewById(R.id.Spn_AnnualIncome);

    Spn_Gender.setOnItemSelectedListener(this);
    Spn_WorkingStatus.setOnItemSelectedListener(this);
    Spn_WorkingAs.setOnItemSelectedListener(this);
    Spn_AnnualIncome.setOnItemSelectedListener(this);

    Button Btn_submit = (Button) DialogAddJob.findViewById(R.id.Btn_submit);


    DialogAddJob.show();
    iv_close.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (DialogAddJob != null && DialogAddJob.isShowing()) {
                DialogAddJob.dismiss();
            }
        }
    });


    Btn_submit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(isValidFields()) {

                StrApplicantName = EdtApplicantName.getText().toString();
                StrFatherName = EdtFatherName.getText().toString();
                StrMotherName = EdtMotherName.getText().toString();
                StrFamilyOrigin = EdtFamilyOriginPlace.getText().toString();
                StrResidingPlace = EdtResidencePlace.getText().toString();
                StrContactNo = EdtContactNo.getText().toString();
                StrWhatsAppNo = EdtWhatAppNo.getText().toString();
                StrDateOfBirth = Edt_DateOfBirth.getText().toString();
                StrGotra = EdtGotra.getText().toString();
                StrGender = Spn_Gender.getSelectedItem().toString().toLowerCase();
                StrWorkingStatus = Spn_WorkingStatus.getSelectedItem().toString().toLowerCase();
                StrWorkingAs = Spn_WorkingAs.getSelectedItem().toString().toLowerCase();
                StrAnnunalIncome = Spn_AnnualIncome.getSelectedItem().toString().toLowerCase();
                API_SAVE_NEW_APPLICANT_PROFILE();

            }

        }
    });

}

    private void setDateTimeField() {

    Edt_DateOfBirth.setOnClickListener(this);

    Calendar newCalendar = Calendar.getInstance();
    newCalendar.add(Calendar.YEAR, -18);

    dobDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            StrDateOfBirth = dateFormatter1.format(newDate.getTime());

            Edt_DateOfBirth.setText(StrDateOfBirth);
        }

    }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    dobDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());
}

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.Edt_DateOfBirth) {

            dobDatePickerDialog.show();
        }
    }


    private boolean isValidFields() {
    boolean result = true;

    if (!MyValidator.isValidName(EdtApplicantName)) {
        EdtApplicantName.requestFocus();
        CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Valid Name");
        result = false;
    }

    if (!MyValidator.isValidName(EdtFatherName)) {
        EdtFatherName.requestFocus();
        CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Valid Father's Name");
        result = false;
    }

    if (!MyValidator.isValidName(EdtMotherName)) {
        EdtMotherName.requestFocus();
        CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Valid Mother's Name");
        result = false;
    }

    if (!MyValidator.isValidField(EdtFamilyOriginPlace)) {
        EdtFamilyOriginPlace.requestFocus();
        CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Family Origin Place");
        result = false;
    }

    if (!MyValidator.isValidField(EdtResidencePlace)) {
        EdtResidencePlace.requestFocus();
        CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Residing Place");
        result = false;
    }

    if (!MyValidator.isValidSpinner(Spn_Gender)) {
        CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Select Gender");
        result = false;
    }

    if (!MyValidator.isValidSpinner(Spn_WorkingStatus)) {
        CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Select Working Status");
        result = false;
    }

    if (!MyValidator.isValidSpinner(Spn_WorkingAs)) {
        CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Select Working As");
        result = false;
    }

    if (!MyValidator.isValidSpinner(Spn_AnnualIncome)) {
        CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Select Annual Income");
        result = false;
    }



    if (!MyValidator.isValidMobile(EdtContactNo)) {
        EdtContactNo.requestFocus();
        CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Valid Contact No.");
        result = false;
    }


    if (!MyValidator.isValidField(Edt_DateOfBirth)) {
        Edt_DateOfBirth.requestFocus();
        CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Valid Date of Birth");
        result = false;
    }



    return  result;
}


    private void API_SAVE_NEW_APPLICANT_PROFILE () {

        String Uiid_id = UUID.randomUUID().toString();
        String URL_ADD_DONOR_DETAIL = ROOT_URL + "add_new_matrimony_profile.php?_" + Uiid_id;
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
                                        if (DialogAddJob != null && DialogAddJob.isShowing()) {
                                            DialogAddJob.dismiss();
                                        }

                                        fetchProfileList();

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
                        params.put("profile_name", StrApplicantName);
                        params.put("father_name", StrFatherName);
                        params.put("mother_name", StrMotherName);
                        params.put("gender", StrGender);
                        params.put("gotra", CommonMethods.SanitizeString(StrGotra));
                        params.put("family_origin_place",  CommonMethods.SanitizeString(StrFamilyOrigin));
                        params.put("residing_city",  CommonMethods.SanitizeString(StrResidingPlace));
                        params.put("working_status", StrWorkingStatus);
                        params.put("working_as", StrWorkingAs);
                        params.put("annunal_income", StrAnnunalIncome);
                        params.put("contact_no", StrContactNo);
                        params.put("whatsapp_no", StrWhatsAppNo);
                        params.put("date_of_birth",StrDateOfBirth);
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


    private void fetchProfileList () {
        myDialog = new ProgressDialog(MatrimonyCorner.this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.show();

        if(matrimonyModel_list!=null && matrimonyModel_list.size()>0){
            matrimonyModel_list = new ArrayList<>();
            matrimonyProfileAdapter = new MatrimonyProfileAdapter(MatrimonyCorner.this, matrimonyModel_list, refreshLayout);
            recycler_list.setAdapter(matrimonyProfileAdapter);
            matrimonyProfileAdapter.notifyDataSetChanged();

        }

        String Uiid_id = UUID.randomUUID().toString();

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            String URL = ROOT_URL + "matrimony_profile_list.php?_" + Uiid_id;
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
                                            final String profile_name = jsonObject.getString("profile_name");
                                            final String father_name = jsonObject.getString("father_name");
                                            final String mother_name = jsonObject.getString("mother_name");
                                            final String gender = jsonObject.getString("gender");
                                            final String date_of_birth = jsonObject.getString("date_of_birth");
                                            String gotra = jsonObject.getString("gotra");
                                            String family_origin_place = jsonObject.getString("family_origin_place");
                                            String residing_city = jsonObject.getString("residing_city");
                                            String working_status = jsonObject.getString("working_status");
                                            String working_as = jsonObject.getString("working_as");
                                            String annunal_income = jsonObject.getString("annunal_income");
                                            String contact_no = jsonObject.getString("contact_no");
                                            String whatsapp_no = jsonObject.getString("whatsapp_no");
                                            String created_by = jsonObject.getString("created_by");

                                            if(date_of_birth!=null && date_of_birth.contains("-")) {
                                                String[] separated = date_of_birth.split("-");
                                                String date = separated[0];
                                                String month = separated[1];
                                                String year = separated[2];
                                                calculated_age = CommonMethods.getAge(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(year));
                                            }

                                            matrimonyProfileListModel = new MatrimonyProfileListModel();
                                            matrimonyProfileListModel.setID(ID);
                                            matrimonyProfileListModel.setProfile_name(profile_name);
                                            matrimonyProfileListModel.setFather_name(father_name);
                                            matrimonyProfileListModel.setMother_name(mother_name);

                                            matrimonyProfileListModel.setGender(gender);
                                            if(date_of_birth!=null && date_of_birth.contains("-")) {
                                                matrimonyProfileListModel.setDate_of_birth(date_of_birth);
                                                matrimonyProfileListModel.setAge(String.valueOf(calculated_age));
                                            }else {
                                                matrimonyProfileListModel.setDate_of_birth("");
                                                matrimonyProfileListModel.setAge(date_of_birth);
                                            }
                                            matrimonyProfileListModel.setFamily_origin_place(family_origin_place);
                                            matrimonyProfileListModel.setResiding_city(residing_city);
                                            matrimonyProfileListModel.setWorking_as(working_as);
                                            matrimonyProfileListModel.setWorking_status(working_status);
                                            matrimonyProfileListModel.setContact_no(contact_no);
                                            matrimonyProfileListModel.setWhatsapp_no(whatsapp_no);
                                            matrimonyProfileListModel.setCreated_by(created_by);
                                            matrimonyProfileListModel.setAnnunal_income(annunal_income);
                                            matrimonyProfileListModel.setGotra(gotra);
                                            matrimonyModel_list.add(matrimonyProfileListModel);
                                        }



                                    }

                                    matrimonyProfileAdapter = new MatrimonyProfileAdapter(MatrimonyCorner.this, matrimonyModel_list, refreshLayout);
                                    recycler_list.setAdapter(matrimonyProfileAdapter);
                                    matrimonyProfileAdapter.notifyDataSetChanged();
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


/*
    private void DeleteProfileDetails (String matrimony_profileId){

        Log.d("DeleteID", "" + matrimony_profileId);
        String Uiid_id = UUID.randomUUID().toString();

        String API_DeleteDonationInfo = ROOT_URL + "delete_matrimony_profile.php?_" + Uiid_id;
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
                                        fetchProfileList();
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
                        params.put("matrimony_profileId", matrimony_profileId);
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
*/
        private void search(SearchView searchView) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (matrimonyProfileAdapter != null){
                        matrimonyProfileAdapter.getFilter().filter(newText);
                    }
                    return true;
                }
            });

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int id = adapterView.getId();



        if(id== R.id.Spn_Gender){

           StrGender = Spn_Gender.getSelectedItem().toString().toLowerCase();
        }else if(id== R.id.Spn_WorkingStatus){

            StrWorkingStatus = Spn_WorkingStatus.getSelectedItem().toString().toLowerCase();
        }else if(id== R.id.Spn_WorkingAs){

            StrWorkingAs = Spn_WorkingAs.getSelectedItem().toString().toLowerCase();
        }else if(id== R.id.Spn_AnnualIncome){

            StrAnnunalIncome = Spn_AnnualIncome.getSelectedItem().toString().toLowerCase();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }

    }
}