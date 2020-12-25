package com.ascend.www.abkms.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import java.util.Locale;
import java.util.Objects;

import static com.ascend.www.abkms.webservices.RestClient.Development;
import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;


public class AddressDetailsFragment extends Fragment implements BlockingStep, AdapterView.OnItemSelectedListener {


    View rootView;
    Context context;
    EditText Edt_FamilyOrigin,Edt_Address1,Edt_Address2,Edt_Address3,Edt_AddressPincode,Edt_State,Edt_City;
    String StrMemberId,StrFamilyOrigin,Str_Address1,Str_Address2,Str_Address3,Str_Pincode,Str_State,Str_City,StrSelectedAffiliatedAbkms,Str_CityId;
    JSONObject stateObj,cityObj;
    StepperLayout.OnNextClickedCallback mCallback;
    SearchableSpinner Spn_AffiliatedAbkms;

    ArrayList<String> affiliatedSabhaNameList = new ArrayList<String>();
    ArrayList<String> affiliatedSabhaCodeList = new ArrayList<String>();
    ProgressDialog myDialog;

    public AddressDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for context fragment

        rootView = inflater.inflate(R.layout.fragment_contact_details_info, container, false);

        context = getContext();
        init();



        return rootView;

    }



    private void init() {


        StrMemberId = UtilitySharedPreferences.getPrefs(context, "MemberId");
        String ApplicationFormData = UtilitySharedPreferences.getPrefs(context,"ApplicationData");

        Edt_FamilyOrigin = (EditText)rootView.findViewById(R.id.Edt_FamilyOrigin);
        Edt_Address1 = (EditText)rootView.findViewById(R.id.Edt_Address1);
        Edt_Address2 = (EditText)rootView.findViewById(R.id.Edt_Address2);
        Edt_Address3 = (EditText)rootView.findViewById(R.id.Edt_Address3);
        Edt_AddressPincode = (EditText)rootView.findViewById(R.id.Edt_AddressPincode);
        Edt_State = (EditText)rootView.findViewById(R.id.Edt_State);
        Edt_City = (EditText)rootView.findViewById(R.id.Edt_City);

        Spn_AffiliatedAbkms = (SearchableSpinner)rootView.findViewById(R.id.Spn_AffiliatedAbkms);

        Spn_AffiliatedAbkms.setOnItemSelectedListener(this);
        Edt_AddressPincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()==6){
                    API_GET_CITY_STATE_BY_PINCODE(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        myDialog = new ProgressDialog(getActivity());
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setMessage("Please Wait...");


        getAffiliatedAbkmsList();
        setDataToFields();


    }

    private void getAffiliatedAbkmsList() {

        affiliatedSabhaNameList = new ArrayList<>();
        affiliatedSabhaCodeList = new ArrayList<>();
        affiliatedSabhaCodeList.add("0");
        affiliatedSabhaNameList.add("Select Affiliated Sabha");

        //Get state json value from assets folder
        String URL_FetchQuaListAPI = ROOT_URL+"get_affiliated_abkms_list.php";
        try {
            Log.d("URL_FetchQuaListAPI",URL_FetchQuaListAPI);

            ConnectionDetector cd = new ConnectionDetector(context);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FetchQuaListAPI,
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
                                        JSONArray dataArray = jobj.getJSONArray("result");

                                        for(int i = 0; i<dataArray.length();i++){

                                            JSONObject jsonObject = dataArray.getJSONObject(i);
                                            String id = jsonObject.getString("id");
                                            String name = jsonObject.getString("city_name");


                                            affiliatedSabhaCodeList.add(id);
                                            affiliatedSabhaNameList.add(name);



                                        }

                                        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, affiliatedSabhaNameList);
                                        Spn_AffiliatedAbkms.setAdapter(countryAdapter);

                                        setDataToFields();

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
                        CommonMethods.DisplayToastWarning(context,"Something went wrong. Please try again later.");
                    }
                });

                int socketTimeout = 50000; //30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                // RequestQueue requestQueue = Volley.newRequestQueue(context, new HurlStack(null, getSocketFactory()));
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);

            } else {
                if(myDialog!=null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
                CommonMethods.DisplayToast(context, "Please check your internet connection");
            }
        } catch (Exception e) {

            e.printStackTrace();

        }



    }

    private void API_GET_CITY_STATE_BY_PINCODE(String strPincode) {

        String URL_Check_Pincode = Development+"getCityStateForPincode?pincode="+strPincode;

        try {
            Log.d("URL",URL_Check_Pincode);

            ConnectionDetector cd = new ConnectionDetector(context);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_Check_Pincode,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response",response);
                                try {
                                    /*  ":"Thane","":"Maharashtra","pincode":"401107","*/
                                    JSONObject jobj = new JSONObject(response);

                                    JSONObject pincodeObject = jobj.getJSONObject("pincodeObject");

                                    String city = pincodeObject.getString("city");
                                    String state = pincodeObject.getString("state");

                                    if(city!=null && !city.equalsIgnoreCase("")){
                                        Edt_City.setText(city);
                                    }else {
                                        Edt_City.setText("");
                                    }

                                    if(state!=null && !state.equalsIgnoreCase("")){
                                        Edt_State.setText(state);
                                    }else{
                                        Edt_State.setText("");
                                    }

                                } catch (Exception e) {

                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("volley", "Error: " + error.getMessage());
                        error.printStackTrace();

                    }
                });

                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);



            } else {
                CommonMethods.DisplayToast(context, "Please check your internet connection");
            }
        } catch (Exception e) {

            e.printStackTrace();

        }


    }

    private void setDataToFields() {

        String ApplicationData= UtilitySharedPreferences.getPrefs(context,"ApplicationData");
        //String StrcustomerDetailObj = UtilitySharedPreferences.getPrefs(context,"customerDetailObj");

        try {

            if(ApplicationData!=null && !ApplicationData.equalsIgnoreCase("")) {
                JSONObject address_detailObj = new JSONObject(ApplicationData);

                StrFamilyOrigin = address_detailObj.getString("FAMILY_ORIGIN");
                Str_Address1 = address_detailObj.getString("ADDRESS1");
                Str_Address2 = address_detailObj.getString("ADDRESS2");
                Str_Address3 = address_detailObj.getString("ADDRESS3");

                Str_Pincode = address_detailObj.getString("PINCODE");
                Str_State = address_detailObj.getString("STATE");
                Str_City = address_detailObj.getString("CITY");
                StrSelectedAffiliatedAbkms = address_detailObj.getString("AFFLIATED_ABKMS");

                if (StrFamilyOrigin != null && !StrFamilyOrigin.equalsIgnoreCase("") && !StrFamilyOrigin.equalsIgnoreCase("null")) {
                    Edt_FamilyOrigin.setText(StrFamilyOrigin);
                }

                if (Str_Address1 != null && !Str_Address1.equalsIgnoreCase("") && !Str_Address1.equalsIgnoreCase("null")) {
                    Edt_Address1.setText(Str_Address1);
                }

                if (Str_Address2 != null && !Str_Address2.equalsIgnoreCase("") && !Str_Address2.equalsIgnoreCase("null")) {
                    Edt_Address2.setText(Str_Address2);
                }

                if (Str_Address3 != null && !Str_Address3.equalsIgnoreCase("") && !Str_Address3.equalsIgnoreCase("null")) {
                    Edt_Address3.setText(Str_Address3);
                }

                if (Str_Pincode != null && !Str_Pincode.equalsIgnoreCase("") && !Str_Pincode.equalsIgnoreCase("null")) {
                    Edt_AddressPincode.setText(Str_Pincode);
                }

                if (Str_State != null && !Str_State.equalsIgnoreCase("") && !Str_State.equalsIgnoreCase("null")) {
                    Edt_State.setText(Str_State);
                }

                if (Str_City != null && !Str_City.equalsIgnoreCase("") && !Str_City.equalsIgnoreCase("null")) {
                    Edt_City.setText(Str_City);
                }

                if (StrSelectedAffiliatedAbkms != null && !StrSelectedAffiliatedAbkms.equalsIgnoreCase("") && !StrSelectedAffiliatedAbkms.equalsIgnoreCase("null")) {
                    int i = getIndex1(Spn_AffiliatedAbkms, StrSelectedAffiliatedAbkms);
                    Spn_AffiliatedAbkms.setSelection(i);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
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

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

        mCallback = callback;

        if (validateFields()) {
            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(Edt_City.getWindowToken(), 0);
            }
            String ApplicationData= UtilitySharedPreferences.getPrefs(context,"ApplicationData");
            StrFamilyOrigin = CommonMethods.SanitizeString(Edt_FamilyOrigin.getText().toString().trim());
            Str_Address1 = CommonMethods.SanitizeString(Edt_Address1.getText().toString());
            Str_Address2 = CommonMethods.SanitizeString(Edt_Address2.getText().toString());
            Str_Address3 = CommonMethods.SanitizeString(Edt_Address3.getText().toString());
            Str_Pincode = CommonMethods.SanitizeString(Edt_AddressPincode.getText().toString());
            Str_State = CommonMethods.SanitizeString(Edt_State.getText().toString());
            Str_City = CommonMethods.SanitizeString(Edt_City.getText().toString());
            StrSelectedAffiliatedAbkms = Spn_AffiliatedAbkms.getSelectedItem().toString();


            JSONObject address_detailObj = null ;

            try {

                if(ApplicationData!=null) {
                    address_detailObj = new JSONObject(ApplicationData);
                }else {
                    address_detailObj = new JSONObject();
                }

                address_detailObj.put("FAMILY_ORIGIN",StrFamilyOrigin);
                address_detailObj.put("ADDRESS1",Str_Address1);
                address_detailObj.put("ADDRESS2",Str_Address2);
                address_detailObj.put("ADDRESS3",Str_Address3);
                address_detailObj.put("PINCODE",Str_Pincode);
                address_detailObj.put("STATE",Str_State);
                address_detailObj.put("CITY",Str_City);
                address_detailObj.put("AFFLIATED_ABKMS",StrSelectedAffiliatedAbkms);



            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (address_detailObj != null) {
                UtilitySharedPreferences.setPrefs(context,"ApplicationData",address_detailObj.toString());
                Log.d("ApplicationData",""+address_detailObj.toString());
                UtilitySharedPreferences.setPrefs(context,"AddressState",Str_State);
                UtilitySharedPreferences.setPrefs(context,"AddressCity",Str_City);

            }

            if(mCallback!=null) {
                mCallback.goToNextStep();
            }
        }

    }

    private boolean validateFields() {
        boolean result = true;

        if (!MyValidator.isValidField(Edt_FamilyOrigin)) {
            Edt_FamilyOrigin.requestFocus();
            CommonMethods.DisplayToastWarning(getContext(),"Please Enter Family Origin");
            result = false;
        }

        if (!MyValidator.isValidField(Edt_Address1)) {
            Edt_Address1.requestFocus();
            CommonMethods.DisplayToastWarning(getContext(),"Please Enter Address 1");
            result = false;
        }

        if (!MyValidator.isValidField(Edt_Address2)) {
            Edt_Address2.requestFocus();
            CommonMethods.DisplayToastWarning(getContext(),"Please Enter Address 2");
            result = false;
        }


        if (!MyValidator.isValidField(Edt_AddressPincode)) {
            Edt_AddressPincode.requestFocus();
            CommonMethods.DisplayToastWarning(getContext(),"Please Enter Pincode");
            result = false;
        }

        if (!MyValidator.isValidField(Edt_State)) {
            Edt_State.requestFocus();
            CommonMethods.DisplayToastWarning(getContext(),"Please Enter State");
            result = false;
        }

        if (!MyValidator.isValidField(Edt_City)) {
            Edt_City.requestFocus();
            CommonMethods.DisplayToastWarning(getContext(),"Please Enter City");
            result = false;
        }

        if (!MyValidator.isValidSearchableSpinner(Spn_AffiliatedAbkms)) {

            CommonMethods.DisplayToastWarning(getContext(),"Please Select Affiliated ABKMS Sabha.");
            result = false;
        }

        return result;
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        if(!validateFields()) {
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.Spn_AffiliatedAbkms) {
            StrSelectedAffiliatedAbkms = Spn_AffiliatedAbkms.getSelectedItem().toString().trim();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}