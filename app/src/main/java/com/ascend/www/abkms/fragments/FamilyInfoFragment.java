package com.ascend.www.abkms.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FamilyInfoFragment extends Fragment implements BlockingStep, AdapterView.OnItemSelectedListener{

    Context context;
    View rootView;
    ImageView Iv_AddMore;
    EditText Edt_FamilySize,edt_spouse_name,edt_spouse_mobile,Edt_Child1Name,Edt_Child1Age;
    Spinner Spn_Spouse_Occupation, Spn_MaritalStatus,Spn_HaveChildren,Spn_Child1MaritalStatus,Spn_Child1Occupation;
    LinearLayout MarriedLayout,ParentLayoutChildrenInfo;
    ProgressDialog myDialog;
    String EmployeeObj,StrEmployeeId;
    String StrMemberId="",StrMaritalStatus,FAMILY_SIZE,Str_SPOUSE_NAME,SPOUSE_OCCUPATION,SPOUSE_MOBILE_NO,HAVE_CHILDREN,CHILDREN_INFO;
    private DatePickerDialog spouseDobDatePickerDialog;
    private SimpleDateFormat dateFormatter, dateFormatter1;
    String StrUsername="",Date_of_born = "", YearOfBorn = "",StrDob="",StrAGE="";
    String StrSelectedGender,StrSalutationFather,StrSalutationMother,StrApplicantFullName,StrFatherName, StrMotherName,
            StrEmailAddress, StrMobileNo="",StrMobileNo1="";
    String StrFamilyOrigin,Str_Address1,Str_Address2,Str_Address3,Str_Pincode,Str_State,Str_City,Str_StateId,Str_CityId;
    String StrSelectedAffiliatedAbkms, StrWorkWith,Str_Education,Str_Occupation,Str_FamilyIncome,StrContributionForAbkms;

    int child_row_count=0;
    int calculated_age =0 ;
    public FamilyInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_family_info, container, false);

        context = getContext();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        dateFormatter1 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        init();

        return rootView;
    }

    private void init() {
        StrMemberId = UtilitySharedPreferences.getPrefs(context, "MemberId");
        String ApplicationFormData = UtilitySharedPreferences.getPrefs(context,"ApplicationData");

        Spn_MaritalStatus= (Spinner)rootView.findViewById(R.id.Spn_MaritalStatus);
        Spn_MaritalStatus.setOnItemSelectedListener(this);

        Edt_FamilySize = (EditText)rootView.findViewById(R.id.Edt_FamilySize);

        MarriedLayout = (LinearLayout)rootView.findViewById(R.id.MarriedLayout);
        ParentLayoutChildrenInfo = (LinearLayout)rootView.findViewById(R.id.ParentLayoutChildrenInfo);
        edt_spouse_name  = (EditText) rootView.findViewById(R.id.edt_spouse_name);
        edt_spouse_mobile  = (EditText) rootView.findViewById(R.id.edt_spouse_mobile);

        Spn_Spouse_Occupation= (Spinner) rootView.findViewById(R.id.Spn_Spouse_Occupation);
        Spn_Spouse_Occupation.setOnItemSelectedListener(this);


        Spn_HaveChildren= (Spinner) rootView.findViewById(R.id.Spn_HaveChildren);
        Spn_HaveChildren.setOnItemSelectedListener(this);

        Edt_Child1Name = (EditText) rootView.findViewById(R.id.Edt_Child1Name);
        Edt_Child1Age = (EditText) rootView.findViewById(R.id.Edt_Child1Age);

        Spn_Child1MaritalStatus= (Spinner) rootView.findViewById(R.id.Spn_Child1MaritalStatus);
        Spn_Child1Occupation= (Spinner) rootView.findViewById(R.id.Spn_Child1Occupation);

        Spn_Child1MaritalStatus.setOnItemSelectedListener(this);
        Spn_Child1Occupation.setOnItemSelectedListener(this);

        Iv_AddMore = (ImageView)rootView.findViewById(R.id.Iv_AddMore);
        Iv_AddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddQuoteRow();
            }
        });

        myDialog = new ProgressDialog(getActivity());
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setMessage("Please Wait...");

        setValuesToView();
    }




    private void setValuesToView() {

        String ApplicationData= UtilitySharedPreferences.getPrefs(context,"ApplicationData");

        try {

            if(ApplicationData!=null && !ApplicationData.equalsIgnoreCase("")) {
                JSONObject customerDetailObj = new JSONObject(ApplicationData);

                StrMaritalStatus = customerDetailObj.getString("MARITAL_STATUS");
                FAMILY_SIZE = customerDetailObj.getString("FAMILY_SIZE");
                Str_SPOUSE_NAME = customerDetailObj.getString("SPOUSE_NAME");
                SPOUSE_OCCUPATION = customerDetailObj.getString("SPOUSE_OCCUPATION");
                SPOUSE_MOBILE_NO = customerDetailObj.getString("SPOUSE_MOBILE_NO");
                HAVE_CHILDREN = customerDetailObj.getString("HAVE_CHILDREN");
                CHILDREN_INFO = customerDetailObj.getString("CHILDREN_INFO");


                if (StrMaritalStatus != null && !StrMaritalStatus.equalsIgnoreCase("") && !StrMaritalStatus.equalsIgnoreCase("null")) {
                    int i = getIndex(Spn_MaritalStatus, StrMaritalStatus);
                    Spn_MaritalStatus.setSelection(i);
                    if(StrMaritalStatus!=null && !StrMaritalStatus.equalsIgnoreCase("") && !StrMaritalStatus.equalsIgnoreCase("null")){
                        if(StrMaritalStatus.equalsIgnoreCase("Married")){
                            MarriedLayout.setVisibility(View.VISIBLE);
                        }else {
                            MarriedLayout.setVisibility(View.GONE);
                        }
                    }

                }

                if (FAMILY_SIZE != null && !FAMILY_SIZE.equalsIgnoreCase("") && !FAMILY_SIZE.equalsIgnoreCase("null")) {
                    Edt_FamilySize.setText(FAMILY_SIZE);
                }

                if(MarriedLayout!=null && MarriedLayout.getVisibility()==View.VISIBLE){

                    if (SPOUSE_OCCUPATION != null && !SPOUSE_OCCUPATION.equalsIgnoreCase("") && !SPOUSE_OCCUPATION.equalsIgnoreCase("null")) {
                        int i = getIndex(Spn_Spouse_Occupation, SPOUSE_OCCUPATION);
                        Spn_Spouse_Occupation.setSelection(i);
                    }

                    if (Str_SPOUSE_NAME != null && !Str_SPOUSE_NAME.equalsIgnoreCase("") && !Str_SPOUSE_NAME.equalsIgnoreCase("null")) {
                        edt_spouse_name.setText(Str_SPOUSE_NAME);
                    }

                    if (SPOUSE_MOBILE_NO != null && !SPOUSE_MOBILE_NO.equalsIgnoreCase("") && !SPOUSE_MOBILE_NO.equalsIgnoreCase("null")) {
                        edt_spouse_mobile.setText(SPOUSE_MOBILE_NO);
                    }

                    if (HAVE_CHILDREN != null && !HAVE_CHILDREN.equalsIgnoreCase("") && !HAVE_CHILDREN.equalsIgnoreCase("null")) {
                        int i = getIndex(Spn_HaveChildren, HAVE_CHILDREN);
                        Spn_HaveChildren.setSelection(i);
                    }

                    if(CHILDREN_INFO!=null && !CHILDREN_INFO.equalsIgnoreCase("")){

                        JSONArray childArr = new JSONArray(CHILDREN_INFO);
                         for (int i = 0; i< childArr.length(); i++){

                             JSONObject childObj = childArr.getJSONObject(i);

                             String child_name = childObj.getString("child_name");
                             String child_age = childObj.getString("child_age");
                             String child_marital_status = childObj.getString("child_marital_status");
                             String child_occupation = childObj.getString("child_occupation");

                             if(i==0){

                                 if (child_name != null && !child_name.equalsIgnoreCase("") && !child_name.equalsIgnoreCase("null")) {
                                     Edt_Child1Name.setText(child_name);
                                 }

                                 if (child_age != null && !child_age.equalsIgnoreCase("") && !child_age.equalsIgnoreCase("null")) {
                                     Edt_Child1Age.setText(child_age);
                                 }

                                 if (child_marital_status != null && !child_marital_status.equalsIgnoreCase("") && !child_marital_status.equalsIgnoreCase("null")) {
                                     int k = getIndex(Spn_Child1MaritalStatus, child_marital_status);
                                     Spn_Child1MaritalStatus.setSelection(k);
                                 }

                                 if (child_occupation != null && !child_occupation.equalsIgnoreCase("") && !child_occupation.equalsIgnoreCase("null")) {
                                     int k = getIndex(Spn_Child1Occupation, child_occupation);
                                     Spn_Child1Occupation.setSelection(k);
                                 }

                             }else {

                                 for(int m = childArr.length()-1; m >0 ; m-- ){


                                     LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                     final View rowView = inflater.inflate(R.layout.row_add_children, null);
                                     // Add the new row before the add field button.

                                     TextView tv_row_id = (TextView)rowView.findViewById(R.id.tv_row_id);
                                     tv_row_id.setText(String.valueOf(child_row_count));

                                     EditText row_Edt_ChildName = (EditText) rowView.findViewById(R.id.row_Edt_ChildName);
                                     EditText row_Edt_ChildAge = (EditText) rowView.findViewById(R.id.row_Edt_ChildAge);

                                     Spinner Spn_ChildMaritalStatus= (Spinner) rowView.findViewById(R.id.Spn_ChildMaritalStatus);
                                     Spinner Spn_ChildOccupation= (Spinner) rowView.findViewById(R.id.Spn_ChildOccupation);

                                     if (child_name != null && !child_name.equalsIgnoreCase("") && !child_name.equalsIgnoreCase("null")) {
                                         row_Edt_ChildName.setText(child_name);
                                     }

                                     if (child_age != null && !child_age.equalsIgnoreCase("") && !child_age.equalsIgnoreCase("null")) {
                                         row_Edt_ChildAge.setText(child_age);
                                     }

                                     if (child_marital_status != null && !child_marital_status.equalsIgnoreCase("") && !child_marital_status.equalsIgnoreCase("null")) {
                                         int k1 = getIndex(Spn_ChildMaritalStatus, child_marital_status);
                                         Spn_ChildMaritalStatus.setSelection(k1);
                                     }

                                     if (child_occupation != null && !child_occupation.equalsIgnoreCase("") && !child_occupation.equalsIgnoreCase("null")) {
                                         int k1 = getIndex(Spn_ChildOccupation, child_occupation);
                                         Spn_ChildOccupation.setSelection(k1);
                                     }



                                     ImageView row_delete = (ImageView)rowView.findViewById(R.id.row_delete);
                                     row_delete.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             DeleteView(tv_row_id.getText().toString());
                                         }
                                     });

                                     ParentLayoutChildrenInfo.addView(rowView,ParentLayoutChildrenInfo.getChildCount() - 1);
                                     Log.d("row_index",""+(ParentLayoutChildrenInfo.getChildCount()));

                                     child_row_count++;


                                 }








                             }


                         }

                    }




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

            return -1; // Not Found
        }
    }




    @Nullable
    @Override
    public VerificationError verifyStep() {
        if(!validateFields()) {
            return new VerificationError("");
        }

        return null;
    }



    private boolean validateFields() {
        boolean result = true;

        if (!MyValidator.isValidSpinner(Spn_MaritalStatus)) {
            CommonMethods.DisplayToastWarning(context,"Select Valid Marital Status");
            result = false;
        }




        if(MarriedLayout.getVisibility()==View.VISIBLE){

            if (!MyValidator.isValidName(edt_spouse_name)) {
                edt_spouse_name.requestFocus();
                CommonMethods.DisplayToastWarning(context,"Enter Valid Spouse Name");
                result = false;
            }

            if (!MyValidator.isValidSpinner(Spn_Spouse_Occupation)) {

                CommonMethods.DisplayToastWarning(context,"Select Spouse Occupation");
                result = false;
            }

            if (!MyValidator.isValidSpinner(Spn_HaveChildren)) {

                CommonMethods.DisplayToastWarning(context,"Select do you have child");
                result = false;
            }



        }




        return  result;
    }

    @Override
    public void onSelected() {
        Log.d("OnNext","SelectedStep --- > U r here");
    }

    @Override
    public void onError(@NonNull VerificationError verificationError) {
        Log.d("OnNext","ErrorStep --- > U r here");
    }

    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback onNextClickedCallback) {


    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback onCompleteClickedCallback) {

        if (validateFields()) {
            ApiSaveFamilyDetails(onCompleteClickedCallback);

        }
    }


    private void ApiSaveFamilyDetails(final StepperLayout.OnCompleteClickedCallback onNextClickedCallback) {

        JSONArray childArray = new JSONArray();
        String ApplicationData= UtilitySharedPreferences.getPrefs(context,"ApplicationData");
        StrMaritalStatus = Spn_MaritalStatus.getSelectedItem().toString().trim();
        FAMILY_SIZE = Edt_FamilySize.getText().toString();
        if(MarriedLayout.getVisibility()==View.VISIBLE) {
            HAVE_CHILDREN = Spn_HaveChildren.getSelectedItem().toString().trim();
            Str_SPOUSE_NAME = edt_spouse_name.getText().toString();
            SPOUSE_MOBILE_NO = edt_spouse_mobile.getText().toString();
            SPOUSE_OCCUPATION = Spn_Spouse_Occupation.getSelectedItem().toString();



            if(HAVE_CHILDREN!=null && HAVE_CHILDREN.equalsIgnoreCase("yes")){

                String StrChild1Name =  Edt_Child1Name.getText().toString();
                String StrChild1Age =  Edt_Child1Age.getText().toString();
                String StrChild1MaritalStatus = Spn_Child1MaritalStatus.getSelectedItem().toString();
                String StrChild1Occupation = Spn_Child1Occupation.getSelectedItem().toString();


                JSONObject childObj = new JSONObject();
                try {
                    childObj.put("child_name",StrChild1Name);
                    childObj.put("child_age",StrChild1Age);
                    childObj.put("child_marital_status",StrChild1MaritalStatus);
                    childObj.put("child_occupation",StrChild1Occupation);
                    childArray.put(childObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("ChildDataObj",""+childArray.toString());
                if(ParentLayoutChildrenInfo.getChildCount()>0){

                    for(int m = 0; m < ParentLayoutChildrenInfo.getChildCount();m++) {
                        View child_view = ParentLayoutChildrenInfo.getChildAt(m);
                        JSONObject childObj1  = new JSONObject();

                        EditText row_Edt_ChildName = (EditText) child_view.findViewById(R.id.row_Edt_ChildName);
                        EditText row_Edt_ChildAge = (EditText) child_view.findViewById(R.id.row_Edt_ChildAge);

                        Spinner Spn_ChildMaritalStatus= (Spinner) child_view.findViewById(R.id.Spn_ChildMaritalStatus);
                        Spinner Spn_ChildOccupation= (Spinner) child_view.findViewById(R.id.Spn_ChildOccupation);

                        String str_row_child_name = row_Edt_ChildName.getText().toString();
                        String str_row_child_age = row_Edt_ChildAge.getText().toString();
                        String spn_child_marital_status = Spn_ChildMaritalStatus.getSelectedItem().toString();
                        String spn_child_occupation = Spn_ChildOccupation.getSelectedItem().toString();

                        try {
                            childObj1.put("child_name",str_row_child_name);
                            childObj1.put("child_age",str_row_child_age);
                            childObj1.put("child_marital_status",spn_child_marital_status);
                            childObj1.put("child_occupation",spn_child_occupation);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        childArray.put(childObj1);

                    }

                    Log.d("CHoldArray",""+childArray.toString());

                }

                CHILDREN_INFO = ""+childArray.toString();


            }else {

                HAVE_CHILDREN = "no";


                String StrChild1Name =  "";
                String StrChild1Age =  "";
                String StrChild1MaritalStatus = "";
                String StrChild1Occupation = "";
                CHILDREN_INFO = "";

            }


        }else {
            HAVE_CHILDREN = "no";
            Str_SPOUSE_NAME = "";
            SPOUSE_MOBILE_NO = "";
            SPOUSE_OCCUPATION = "";

            String StrChild1Name =  "";
            String StrChild1Age =  "";
            String StrChild1MaritalStatus = "";
            String StrChild1Occupation = "";
            CHILDREN_INFO = "";

        }

        JSONObject application_detailObj = null ;

        try {

            if(ApplicationData!=null) {
                application_detailObj = new JSONObject(ApplicationData);
            }else {
                application_detailObj = new JSONObject();
            }
            application_detailObj.put("FAMILY_SIZE",FAMILY_SIZE);
            application_detailObj.put("MARITAL_STATUS",StrMaritalStatus);
            application_detailObj.put("SPOUSE_NAME",Str_SPOUSE_NAME);
            application_detailObj.put("SPOUSE_OCCUPATION",SPOUSE_OCCUPATION);
            application_detailObj.put("SPOUSE_MOBILE_NO",SPOUSE_MOBILE_NO);
            application_detailObj.put("HAVE_CHILDREN",HAVE_CHILDREN);
            application_detailObj.put("CHILDREN_INFO",CHILDREN_INFO);

            StrEmailAddress = application_detailObj.getString("EMAIL_ID");
            StrMobileNo = application_detailObj.getString("MOBILE_NO");
            StrMobileNo1 = application_detailObj.getString("MOBILE_NO1");
            StrApplicantFullName = application_detailObj.getString("NAME_EN");
            StrFatherName = application_detailObj.getString("FATHER_NAME");
            StrMotherName = application_detailObj.getString("MOTHER_NAME");
            StrSelectedGender = application_detailObj.getString("GENDER");
            StrDob = application_detailObj.getString("DOB");
            StrAGE = application_detailObj.getString("AGE");
            StrFamilyOrigin = application_detailObj.getString("FAMILY_ORIGIN");
            Str_Address1 = application_detailObj.getString("ADDRESS1");
            Str_Address2 = application_detailObj.getString("ADDRESS2");
            Str_Address3 = application_detailObj.getString("ADDRESS3");
            Str_Pincode = application_detailObj.getString("PINCODE");
            Str_State = application_detailObj.getString("STATE");
            Str_City = application_detailObj.getString("CITY");
            StrWorkWith = application_detailObj.getString("WORKS_WITH");
            Str_Education = application_detailObj.getString("QUALIFICATION");
            Str_Occupation = application_detailObj.getString("OCCUPATION");
            Str_FamilyIncome = application_detailObj.getString("MONTHLY_FAMILY_INCOME");
            StrContributionForAbkms = application_detailObj.getString("CONTRIBUTION_FOR_ABKMS");
            StrSelectedAffiliatedAbkms = application_detailObj.getString("AFFLIATED_ABKMS");

        } catch (JSONException e) {
            e.printStackTrace();
        }

         Log.d("ApplicationData",""+application_detailObj.toString());

        String Uiid_id = UUID.randomUUID().toString();

       myDialog.show();
       String URL_user_info = ROOT_URL + "update_complete_profile.php?_"+Uiid_id;
        try {
            Log.d("URL_USerInfo",URL_user_info);

            ConnectionDetector cd = new ConnectionDetector(getActivity());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_user_info,
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
                                    if(status){

                                        JSONObject result = jobj.getJSONObject("result");

                                        String IS_COMPLETE_PROFILE_UPDATED = result.getString("IS_COMPLETE_PROFILE_UPDATED");
                                        String AFFLIATED_ABKMS = result.getString("AFFLIATED_ABKMS");

                                        UtilitySharedPreferences.setPrefs(context,"AFFLIATED_ABKMS",AFFLIATED_ABKMS);
                                        UtilitySharedPreferences.setPrefs(context,"ApplicationData",result.toString());
                                        UtilitySharedPreferences.setPrefs(context,"IS_COMPLETE_PROFILE_UPDATED",IS_COMPLETE_PROFILE_UPDATED);

                                        CommonMethods.DisplayToastSuccess(context,"Profile Updated Successfully.");

                                        Intent intent = new Intent(context, NewDashboard.class);
                                        startActivity(intent);
                                        ((Activity)context).overridePendingTransition(R.animator.left_right, R.animator.right_left);
                                        ((Activity)context).finish();

                                    }else {

                                        String message = jobj.getString("message");
                                        CommonMethods.DisplayToastWarning(context,""+message);
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
                        params.put("member_id", StrMemberId);
                        params.put("member_name", StrApplicantFullName);
                        params.put("father_name", StrFatherName);
                        params.put("mother_name", StrMotherName);
                        params.put("marital_status",StrMaritalStatus);
                        params.put("spouse_name", Str_SPOUSE_NAME);
                        params.put("member_mobile",StrMobileNo);
                        params.put("alternate_mobile",StrMobileNo1);
                        params.put("spouse_occupation",SPOUSE_OCCUPATION);
                        params.put("spouse_mobile",SPOUSE_MOBILE_NO);
                        params.put("gender",StrSelectedGender);
                        params.put("date_of_birth",StrDob);
                        params.put("age",StrAGE);
                        params.put("address1", Str_Address1);
                        params.put("address2",Str_Address2);
                        params.put("address3",Str_Address3);
                        params.put("pincode",Str_Pincode);
                        params.put("city",Str_City);
                        params.put("state",Str_State);
                        params.put("member_works_with",StrWorkWith);
                        params.put("member_email",StrEmailAddress);
                        params.put("education",Str_Education);
                        params.put("occupation",Str_Occupation);
                        params.put("family_origin",StrFamilyOrigin);
                        params.put("family_income",Str_FamilyIncome);
                        params.put("contribution",StrContributionForAbkms);
                        params.put("affiliated_abkms",StrSelectedAffiliatedAbkms);
                        params.put("family_size",FAMILY_SIZE);
                        params.put("have_children",HAVE_CHILDREN);
                        params.put("children_info",CHILDREN_INFO);



                        Log.d("ParrasContact", params.toString());
                        return params;

                    }
                };

                int socketTimeout = 50000; //30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(stringRequest);

            } else {
                if(myDialog!=null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
                CommonMethods.DisplayToast(getActivity(), "Please check your internet connection");
            }
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void AddQuoteRow() {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row_add_children, null);
        // Add the new row before the add field button.

        TextView tv_row_id = (TextView)rowView.findViewById(R.id.tv_row_id);
        tv_row_id.setText(String.valueOf(child_row_count));

        EditText row_Edt_ChildName = (EditText) rowView.findViewById(R.id.row_Edt_ChildName);
        EditText row_Edt_ChildAge = (EditText) rowView.findViewById(R.id.row_Edt_ChildAge);

        Spinner Spn_ChildMaritalStatus= (Spinner) rowView.findViewById(R.id.Spn_ChildMaritalStatus);
        Spinner Spn_ChildOccupation= (Spinner) rowView.findViewById(R.id.Spn_ChildOccupation);





        ImageView row_delete = (ImageView)rowView.findViewById(R.id.row_delete);
            row_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteView(tv_row_id.getText().toString());
                }
            });

        ParentLayoutChildrenInfo.addView(rowView,ParentLayoutChildrenInfo.getChildCount() - 1);
        Log.d("row_index",""+(ParentLayoutChildrenInfo.getChildCount()));

        child_row_count++;



    }

    public void  DeleteView(String deleted_row_id){

        if(ParentLayoutChildrenInfo.getChildCount()>0){

            for(int i = 0; i< ParentLayoutChildrenInfo.getChildCount(); i++) {
                View rowView = ParentLayoutChildrenInfo.getChildAt(i);
                TextView tv_row_id = (TextView) rowView.findViewById(R.id.tv_row_id);
                String card_row_id = tv_row_id.getText().toString();

                if(card_row_id.equalsIgnoreCase(deleted_row_id)){

                    ParentLayoutChildrenInfo.removeViewAt(i);
                    child_row_count = ParentLayoutChildrenInfo.getChildCount();
                }



            }

        }
      /*  if(deleted_row_id!=null && Integer.valueOf(deleted_row_id)>=0){
            ParentLayoutChildrenInfo.removeViewAt(Integer.valueOf(deleted_row_id));
            //ParentLayoutChildrenInfo.removeView((View) view.getParent());
            child_row_count--;
        }*/

    }



    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback onBackClickedCallback) {
        onBackClickedCallback.goToPrevStep();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int id =adapterView.getId();
        if (id == R.id.Spn_MaritalStatus) {
            StrMaritalStatus = Spn_MaritalStatus.getSelectedItem().toString().trim().toUpperCase();

            if(StrMaritalStatus!=null && !StrMaritalStatus.equalsIgnoreCase("") && !StrMaritalStatus.equalsIgnoreCase("null")){
                if(StrMaritalStatus.equalsIgnoreCase("Married")){
                    MarriedLayout.setVisibility(View.VISIBLE);
                }else {
                    MarriedLayout.setVisibility(View.GONE);
                }
            }
        }else if (id == R.id.Spn_HaveChildren) {
            HAVE_CHILDREN = Spn_HaveChildren.getSelectedItem().toString().trim();
        }else if(id == R.id.Spn_Spouse_Occupation){
            SPOUSE_OCCUPATION = Spn_Spouse_Occupation.getSelectedItem().toString();
        }
    }

    private void CreateViewToAddChildren(String strNoOfChildren) {

        int no_of_children = Integer.valueOf(strNoOfChildren);
        if(ParentLayoutChildrenInfo.getChildCount()>0) {
            ParentLayoutChildrenInfo.removeAllViews();
        }

        for(int k=0; k< no_of_children; k++){

            LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView1 = inflater1.inflate(R.layout.row_add_children, null);

            EditText row_Edt_ChildName = (EditText) rowView1.findViewById(R.id.row_Edt_ChildName);
            EditText row_Edt_ChildAge = (EditText) rowView1.findViewById(R.id.row_Edt_ChildAge);
            Spinner Spn_ChildMaritalStatus= (Spinner)rowView1.findViewById(R.id.Spn_ChildMaritalStatus);
            Spinner Spn_ChildOccupation= (Spinner)rowView1.findViewById(R.id.Spn_ChildOccupation);

            row_Edt_ChildName.setHint("Enter Child "+(k+1) +"  Name");

            ParentLayoutChildrenInfo.addView(rowView1);
        }



    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
