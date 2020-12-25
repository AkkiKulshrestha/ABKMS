package com.ascend.www.abkms.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ascend.www.abkms.R;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.utils.MyValidator;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;
import com.ascend.www.abkms.webservices.RestClient;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;


public class BasicDetailsFragment extends Fragment implements BlockingStep, AdapterView.OnItemSelectedListener, Serializable, View.OnClickListener {


    View rootView;
    Context context;

    EditText edt_Full_Name,edt_FatherFullName,edt_MotherFullName,edt_Dob,edt_Email_Address,edt_Mobile_Number,edt_Mobile_Number1;
    Spinner Spn_Gender;
    StepperLayout.OnNextClickedCallback mCallback;


    ProgressDialog myDialog;
    String EmployeeObj;

    private DatePickerDialog dobDatePickerDialog;
    private SimpleDateFormat dateFormatter, dateFormatter1;
    String StrMemberId="",StrUsername="", YearOfBorn = "",StrDob="";
    String StrSelectedGender,StrSalutationFather,StrSalutationMother,StrApplicantFullName,StrFatherName, StrMotherName,
            StrEmailAddress, StrMobileNo="",StrMobileNo1="";
    int years; int monthOfYears;int dayOfMonths;
    int calculated_age =0 ;


    public BasicDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for context fragment

        rootView = inflater.inflate(R.layout.fragment_basic_details_info, container, false);

        context = getContext();

        init();


        return rootView;

    }

    private void init() {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateFormatter1 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        StrMemberId = UtilitySharedPreferences.getPrefs(context, "MemberId");
        String ApplicationFormData = UtilitySharedPreferences.getPrefs(context,"ApplicationData");

        edt_Full_Name= (EditText)rootView.findViewById(R.id.edt_Full_Name);
        edt_FatherFullName= (EditText)rootView.findViewById(R.id.edt_FatherFullName);
        edt_MotherFullName= (EditText)rootView.findViewById(R.id.edt_MotherFullName);
        edt_Dob= (EditText)rootView.findViewById(R.id.edt_Dob);
        edt_Mobile_Number= (EditText)rootView.findViewById(R.id.edt_Mobile_Number);
        edt_Mobile_Number1= (EditText)rootView.findViewById(R.id.edt_Mobile_Number1);
        edt_Email_Address= (EditText)rootView.findViewById(R.id.edt_Email_Address);


        Spn_Gender= (Spinner)rootView.findViewById(R.id.Spn_Gender);

        Spn_Gender.setOnItemSelectedListener(this);





        myDialog = new ProgressDialog(context);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        InputMethodManager mgr1 = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr1 != null) {
            mgr1.showSoftInput(edt_Dob, InputMethodManager.SHOW_FORCED);
        }
        setDateTimeField();

        getDataForApplicant();



    }


    private void getDataForApplicant() {
        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();

        ConnectionDetector cd = new ConnectionDetector(context);
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            String URL_GetPosData = RestClient.ROOT_URL + "getApplicantDetail.php?_" + Uiid_id+"&member_id="+StrMemberId;
            Log.d("URL_GetPosData:",""+URL_GetPosData);
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, URL_GetPosData,
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
                                if(status) {
                                    JSONObject result = jsonObj.getJSONObject("result");
                                    UtilitySharedPreferences.setPrefs(context,"ApplicationData",result.toString());
                                    setDataToFields();

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

                    //Toast.makeText(context, "Some Error occurred by fetching data. Please try again later.", Toast.LENGTH_SHORT).show();

                    error.printStackTrace();

                }
            });

            int socketTimeout = 50000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);

        } else {
            if (myDialog != null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
            CommonMethods.DisplayToast(context, "Please check your internet connection");
        }


    }


    private void setDataToFields() {

        String ApplicationData= UtilitySharedPreferences.getPrefs(context,"ApplicationData");
        //String StrcustomerDetailObj = UtilitySharedPreferences.getPrefs(context,"customerDetailObj");

        try {

            if(ApplicationData!=null && !ApplicationData.equalsIgnoreCase("")) {
                JSONObject customerDetailObj = new JSONObject(ApplicationData);
                StrEmailAddress = customerDetailObj.getString("EMAIL_ID");
                StrMobileNo = customerDetailObj.getString("MOBILE_NO");
                StrMobileNo1 = customerDetailObj.getString("MOBILE_NO1");
                StrApplicantFullName = customerDetailObj.getString("NAME_EN");
                StrFatherName = customerDetailObj.getString("FATHER_NAME");
                StrMotherName = customerDetailObj.getString("MOTHER_NAME");
                StrSelectedGender = customerDetailObj.getString("GENDER");
                StrDob = customerDetailObj.getString("DOB");

                if (StrApplicantFullName != null && !StrApplicantFullName.equalsIgnoreCase("") && !StrApplicantFullName.equalsIgnoreCase("null")) {
                    edt_Full_Name.setText(StrApplicantFullName);
                }

                if (StrFatherName != null && !StrFatherName.equalsIgnoreCase("") && !StrFatherName.equalsIgnoreCase("null")) {
                    edt_FatherFullName.setText(StrFatherName);
                }

                if (StrMotherName != null && !StrMotherName.equalsIgnoreCase("") && !StrMotherName.equalsIgnoreCase("null")) {
                    edt_MotherFullName.setText(StrMotherName);
                }

                if (StrDob != null && !StrDob.equalsIgnoreCase("") && !StrDob.equalsIgnoreCase("null")) {
                    edt_Dob.setText(StrDob);

                    if(StrDob.contains("-")){
                        String [] dob_arr = StrDob.split("-");
                        String day = dob_arr[0];
                        String month = dob_arr[1];
                        String year = dob_arr[2];

                        calculated_age = CommonMethods.getAge(Integer.valueOf(year),Integer.valueOf(month),Integer.valueOf(day));

                    }
                }

                if (StrSelectedGender != null && !StrSelectedGender.equalsIgnoreCase("") && !StrSelectedGender.equalsIgnoreCase("null")) {

                    if(StrSelectedGender.equalsIgnoreCase("M")) {
                        StrSelectedGender = "Male";
                        int i = getIndex(Spn_Gender, StrSelectedGender);
                        Spn_Gender.setSelection(i);
                    }else if(StrSelectedGender.equalsIgnoreCase("F")){
                        StrSelectedGender = "Female";
                        int i = getIndex(Spn_Gender, StrSelectedGender);
                        Spn_Gender.setSelection(i);
                    }
                }

                if (StrMobileNo != null && !StrMobileNo.equalsIgnoreCase("") && !StrMobileNo.equalsIgnoreCase("null")) {
                    edt_Mobile_Number.setText(StrMobileNo);
                }

                if (StrMobileNo1 != null && !StrMobileNo1.equalsIgnoreCase("") && !StrMobileNo1.equalsIgnoreCase("null")) {
                    edt_Mobile_Number1.setText(StrMobileNo1);
                }

                if (StrEmailAddress != null && !StrEmailAddress.equalsIgnoreCase("") && !StrEmailAddress.equalsIgnoreCase("null")) {
                    edt_Email_Address.setText(StrEmailAddress);
                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
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


    private void setDateTimeField() {

        edt_Dob.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        newCalendar.add(Calendar.YEAR, -18);


        dobDatePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                StrDob = dateFormatter1.format(newDate.getTime());
                YearOfBorn = String.valueOf(year);
                Log.d("YearOfBornBasicDetail", "" + YearOfBorn);
                calculated_age = CommonMethods.getAge(year, monthOfYear, dayOfMonth);
                UtilitySharedPreferences.setPrefs(context,"YearOfBirth",YearOfBorn);
                edt_Dob.setText(StrDob);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dobDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.edt_Dob) {

            dobDatePickerDialog.show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int id = adapterView.getId();
       if (id == R.id.Spn_Gender) {
            StrSelectedGender = Spn_Gender.getSelectedItem().toString().trim();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        mCallback = callback;
        if (IsValidFields()) {
            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(edt_Email_Address.getWindowToken(), 0);
            }
            String ApplicationData= UtilitySharedPreferences.getPrefs(context,"ApplicationData");

            StrEmailAddress = edt_Email_Address.getText().toString();
            StrMobileNo = edt_Mobile_Number.getText().toString();
            StrMobileNo1 = edt_Mobile_Number1.getText().toString();
            StrApplicantFullName = CommonMethods.SanitizeString(edt_Full_Name.getText().toString());
            StrFatherName = CommonMethods.SanitizeString(edt_FatherFullName.getText().toString());
            StrMotherName = CommonMethods.SanitizeString(edt_MotherFullName.getText().toString());
            StrDob = edt_Dob.getText().toString();
            StrSelectedGender = CommonMethods.SanitizeString(Spn_Gender.getSelectedItem().toString());
            if(StrSelectedGender!=null && StrSelectedGender.equalsIgnoreCase("Male")){
                StrSelectedGender = "M";
            }else if(StrSelectedGender!=null && StrSelectedGender.equalsIgnoreCase("Female")){
                StrSelectedGender = "F";
            }
            JSONObject customerDetailObj = null;



            try {

                if(ApplicationData!=null) {
                    customerDetailObj = new JSONObject(ApplicationData);
                }else {
                    customerDetailObj = new JSONObject();
                }
                customerDetailObj.put("NAME_EN",StrApplicantFullName);
                customerDetailObj.put("FATHER_NAME",StrFatherName);
                customerDetailObj.put("MOTHER_NAME",StrMotherName);
                customerDetailObj.put("GENDER",StrSelectedGender);
                customerDetailObj.put("DOB",StrDob);
                customerDetailObj.put("MOBILE_NO",StrMobileNo);
                customerDetailObj.put("MOBILE_NO1",StrMobileNo1);
                customerDetailObj.put("EMAIL_ID",StrEmailAddress);
                customerDetailObj.put("AGE",String.valueOf(calculated_age));



            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (customerDetailObj != null) {
                UtilitySharedPreferences.setPrefs(context,"ApplicationData",customerDetailObj.toString());
                Log.d("AppLicationData",""+customerDetailObj.toString());

            }
            callback.goToNextStep();
        }
    }

    private boolean IsValidFields() {
        boolean result = true;

        if (!MyValidator.isValidName(edt_Full_Name)) {
            edt_Full_Name.requestFocus();
            CommonMethods.DisplayToastWarning(context, "Please Enter Full Name");
            result = false;
        }

        if (!MyValidator.isValidName(edt_FatherFullName)) {
            edt_FatherFullName.requestFocus();
            CommonMethods.DisplayToastWarning(context, "Please Enter Father's Name");
            result = false;
        }

        if (!MyValidator.isValidName(edt_MotherFullName)) {
            edt_MotherFullName.requestFocus();
            CommonMethods.DisplayToastWarning(context, "Please Enter Mother's Name");
            result = false;
        }

        if (!MyValidator.isValidSpinner(Spn_Gender)) {
            CommonMethods.DisplayToastWarning(context, "Please Select Gender");
            result = false;
        }

        if (!MyValidator.isValidField(edt_Dob)) {
            edt_Dob.requestFocus();
            result = false;
        }


        if (!MyValidator.isValidMobile(edt_Mobile_Number)) {
            edt_Mobile_Number.requestFocus();
            CommonMethods.DisplayToastWarning(context, "Please Enter Valid Mobile No");
            result = false;
        }




        return result;
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {

    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        if (!IsValidFields()) {
            return new VerificationError("");

        }
        return null;

    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }


}
