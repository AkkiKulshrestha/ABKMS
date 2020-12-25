package com.ascend.www.abkms.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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
import com.ascend.www.abkms.R;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.utils.MyValidator;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class Education_CareerInfoFragment extends Fragment implements BlockingStep,AdapterView.OnItemSelectedListener {

    Spinner Spn_Education,Spn_Occupation,Spn_FamilyIncome;
    EditText edt_works_with,edt_Contribution_Abkms;


    String StrSelectedAffiliatedAbkms, StrMemberId,StrWorkWith,Str_Education,Str_Occupation,Str_FamilyIncome,StrContributionForAbkms;

    ProgressDialog myDialog;
    Context context;


    View rootView;



    public Education_CareerInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edu_career_info, container, false);
        context = getContext();
        init();

        return rootView;
    }

    private void init() {


        StrMemberId = UtilitySharedPreferences.getPrefs(context, "MemberId");
        String ApplicationFormData = UtilitySharedPreferences.getPrefs(context,"ApplicationData");

        Spn_Education = (Spinner)rootView.findViewById(R.id.Spn_Education);
        Spn_Occupation = (Spinner) rootView.findViewById(R.id.Spn_Occupation);
        Spn_FamilyIncome = (Spinner)rootView.findViewById(R.id.Spn_FamilyIncome);

        edt_works_with = (EditText)rootView.findViewById(R.id.edt_works_with);
        edt_Contribution_Abkms = (EditText)rootView.findViewById(R.id.edt_Contribution_Abkms);

        Spn_Education.setOnItemSelectedListener(this);
        Spn_Occupation.setOnItemSelectedListener(this);
        Spn_FamilyIncome.setOnItemSelectedListener(this);


        myDialog = new ProgressDialog(getActivity());
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setMessage("Please Wait...");


        setDataToFields();
    }




    private void setDataToFields() {
        String ApplicationData= UtilitySharedPreferences.getPrefs(context,"ApplicationData");
        //String StrcustomerDetailObj = UtilitySharedPreferences.getPrefs(context,"customerDetailObj");

        try {

            if(ApplicationData!=null && !ApplicationData.equalsIgnoreCase("")) {
                JSONObject address_detailObj = new JSONObject(ApplicationData);

                StrWorkWith = address_detailObj.getString("WORKS_WITH");
                Str_Education = address_detailObj.getString("QUALIFICATION");
                Str_Occupation = address_detailObj.getString("OCCUPATION");
                Str_FamilyIncome = address_detailObj.getString("MONTHLY_FAMILY_INCOME");

                StrContributionForAbkms = address_detailObj.getString("CONTRIBUTION_FOR_ABKMS");



                if (StrWorkWith != null && !StrWorkWith.equalsIgnoreCase("") && !StrWorkWith.equalsIgnoreCase("null")) {
                   edt_works_with.setText(StrWorkWith);
                }

                if (Str_Education != null && !Str_Education.equalsIgnoreCase("") && !Str_Education.equalsIgnoreCase("null")) {
                    int i = getIndex(Spn_Education, Str_Education);
                    Spn_Education.setSelection(i);
                }

                if (Str_Occupation != null && !Str_Occupation.equalsIgnoreCase("") && !Str_Occupation.equalsIgnoreCase("null")) {
                    int i = getIndex(Spn_Occupation, Str_Occupation);
                    Spn_Occupation.setSelection(i);
                }

                if (Str_FamilyIncome != null && !Str_FamilyIncome.equalsIgnoreCase("") && !Str_FamilyIncome.equalsIgnoreCase("null")) {
                    int i = getIndex(Spn_FamilyIncome, Str_FamilyIncome);
                    Spn_FamilyIncome.setSelection(i);
                }

                if (StrContributionForAbkms != null && !StrContributionForAbkms.equalsIgnoreCase("") && !StrContributionForAbkms.equalsIgnoreCase("null")) {
                    edt_Contribution_Abkms.setText(StrContributionForAbkms);
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




    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {


        switch (adapterView.getId()) {
            case R.id.Spn_Education :

                Str_Education = Spn_Education.getSelectedItem().toString();

                 break;
            case R.id.Spn_Occupation :

                Str_Occupation = Spn_Occupation.getSelectedItem().toString().trim();

                break;

            case R.id.Spn_FamilyIncome :

                Str_FamilyIncome = Spn_FamilyIncome.getSelectedItem().toString().trim();


                break;



        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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

        if (!MyValidator.isValidSpinner(Spn_Education)) {
            Spn_Education.requestFocus();
            CommonMethods.DisplayToastWarning(getContext(),"Please Select Qualification Level.");
            result = false;
        }

        if (!MyValidator.isValidSpinner(Spn_Occupation)) {
            Spn_Occupation.requestFocus();
            CommonMethods.DisplayToastWarning(getContext(),"Please Select Highest Qualification Level.");
            result = false;
        }

        if (!MyValidator.isValidSpinner(Spn_FamilyIncome)) {
            Spn_FamilyIncome.requestFocus();
            CommonMethods.DisplayToastWarning(getContext(),"Please Select Family Income");
            result = false;
        }







        return result;
    }
//Block Steps Until Operation gets Finished

    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback onNextClickedCallback) {

        if (validateFields()) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(edt_Contribution_Abkms.getWindowToken(), 0);
            }
            String ApplicationData= UtilitySharedPreferences.getPrefs(context,"ApplicationData");
            StrWorkWith = CommonMethods.SanitizeString(edt_works_with.getText().toString());
            Str_Education = Spn_Education.getSelectedItem().toString();
            Str_Occupation = Spn_Occupation.getSelectedItem().toString();
            Str_FamilyIncome = Spn_FamilyIncome.getSelectedItem().toString();

            StrContributionForAbkms = CommonMethods.SanitizeString(edt_Contribution_Abkms.getText().toString());



            JSONObject address_detailObj = null ;

            try {

                if(ApplicationData!=null) {
                    address_detailObj = new JSONObject(ApplicationData);
                }else {
                    address_detailObj = new JSONObject();
                }

                address_detailObj.put("WORKS_WITH",StrWorkWith);
                address_detailObj.put("QUALIFICATION",Str_Education);
                address_detailObj.put("OCCUPATION",Str_Occupation);
                address_detailObj.put("MONTHLY_FAMILY_INCOME",Str_FamilyIncome);
                address_detailObj.put("CONTRIBUTION_FOR_ABKMS",StrContributionForAbkms);





            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (address_detailObj != null) {
                UtilitySharedPreferences.setPrefs(context,"ApplicationData",address_detailObj.toString());
                Log.d("ApplicationData",""+address_detailObj.toString());

            }

            if(onNextClickedCallback!=null) {
                onNextClickedCallback.goToNextStep();
            }

        }


    }



    @Override
    public void onCompleteClicked(final StepperLayout.OnCompleteClickedCallback onCompleteClickedCallback) {

        //onCompleteClickedCallback.complete();

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback onBackClickedCallback) {

        onBackClickedCallback.goToPrevStep();
    }



    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError verificationError) {

    }





}
