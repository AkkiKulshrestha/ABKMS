package com.ascend.www.abkms.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ascend.www.abkms.utils.MyValidator;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

import static com.ascend.www.abkms.utils.CommonMethods.ucFirst;
import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;

public class SuggestionRequest extends AppCompatActivity {
    ImageView back_btn_toolbar;
    TextView til_text;
    Spinner Spn_Type;
    EditText edt_member_name,edt_title_subject,edt_description;
    TextView txt_attachment;
    Button BtnAttachFile,BtnSubmit;
    private static final int PICKFILE_RESULT_CODE = 1;
    ProgressDialog myDialog;
    String StrType,Str_Base64Image="";
    ImageView Iv_UploadedImg;
    private static final int SELECT_FILE = 2243;
    LinearLayout ll_parent_suggestion_list;
    String IS_ADMIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_request);

        findViewByIds();
    }

    private void findViewByIds() {


        IS_ADMIN = UtilitySharedPreferences.getPrefs(getApplicationContext(),"IsAdmin");

        back_btn_toolbar = (ImageView)findViewById(R.id.back_btn_toolbar);
        back_btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView til_text = (TextView)findViewById(R.id.til_text);
        til_text.setText(getResources().getString(R.string.suggestions));


        Spn_Type = (Spinner)findViewById(R.id.Spn_Type);
        Spn_Type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                StrType = Spn_Type.getSelectedItem().toString().trim();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edt_member_name = (EditText)findViewById(R.id.edt_member_name);
        edt_title_subject = (EditText)findViewById(R.id.edt_title_subject);
        edt_description = (EditText)findViewById(R.id.edt_description);

         txt_attachment = (TextView)findViewById(R.id.txt_attachment);
         BtnAttachFile = (Button)findViewById(R.id.BtnAttachFile);

        Iv_UploadedImg = (ImageView)findViewById(R.id.Iv_UploadedImg);

        BtnSubmit = (Button)findViewById(R.id.BtnSubmit);


        BtnAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryIntent();
            }
        });

        myDialog = new ProgressDialog(SuggestionRequest.this);
        myDialog.setMessage(getResources().getString(R.string.please_wait));
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(true);


        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidFields()){
                    API_ADD_SUGGESTION();
                }
            }
        });


        ll_parent_suggestion_list = (LinearLayout)findViewById(R.id.ll_parent_suggestion_list);

        if(IS_ADMIN!=null && !IS_ADMIN.equalsIgnoreCase("") && !IS_ADMIN.equalsIgnoreCase("null")){
            if(IS_ADMIN.equalsIgnoreCase("1")){
                ll_parent_suggestion_list.setVisibility(View.VISIBLE);
                showData();
            }else {
                ll_parent_suggestion_list.setVisibility(View.GONE);
            }

        }

    }

    private void showData() {
        myDialog.show();

        if(ll_parent_suggestion_list!=null && ll_parent_suggestion_list.getChildCount()>0){
            ll_parent_suggestion_list.removeAllViews();
        }

        String Uiid_id = UUID.randomUUID().toString();


        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = ROOT_URL + "fetch_suggestion_list.php?_" + Uiid_id;
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

                        String result = jsonresponse.getString("result");
                        JSONArray resultArry = new JSONArray(result);
                        if (resultArry.length() > 0) {


                            for (int i = 0; i < resultArry.length(); i++) {

                                JSONObject customer_inspect_obj = resultArry.getJSONObject(i);

                                String id = customer_inspect_obj.getString("id");
                                String type = customer_inspect_obj.getString("type");
                                String user_name = customer_inspect_obj.getString("user_name");
                                String subject_title = customer_inspect_obj.getString("subject_title");
                                String description = customer_inspect_obj.getString("description");
                                String attachment = customer_inspect_obj.getString("attachment");


                                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View rowView = Objects.requireNonNull(inflater).inflate(R.layout.row_suggestion_request, null);
                                rowView.setPadding(10, 10, 10, 10);

                                TextView tv_id = (TextView)rowView.findViewById(R.id.tv_id);
                                TextView tv_type = (TextView)rowView.findViewById(R.id.tv_type);
                                TextView tv_name = (TextView)rowView.findViewById(R.id.tv_name);
                                TextView row_title = (TextView)rowView.findViewById(R.id.row_title);
                                TextView tv_description = (TextView)rowView.findViewById(R.id.tv_description);


                                ImageView iv_attachment = (ImageView)rowView.findViewById(R.id.iv_attachment);


                                tv_id.setText(id);
                                tv_type.setText(type);
                                tv_name.setText(user_name.toUpperCase());
                                row_title.setText(ucFirst(subject_title));
                                tv_description.setText(ucFirst(description));

                                if(attachment!=null && !attachment.equalsIgnoreCase("")){
                                    //encode image to base64 string
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    byte[] imageBytes = baos.toByteArray();

                                    //decode base64 string to image
                                    imageBytes = Base64.decode(attachment, Base64.DEFAULT);
                                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                                    iv_attachment.setImageBitmap(decodedImage);
                                    iv_attachment.setVisibility(View.VISIBLE);

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

                                        AlertDialog.Builder builder = new AlertDialog.Builder(SuggestionRequest.this);
                                        builder.setTitle("Delete !!!");
                                        builder.setMessage("Are you sure you want to delete this entry ?");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DeleteSuggesstionDetails(tv_id.getText().toString());
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


                                ll_parent_suggestion_list.addView(rowView);

                            }

                        }else {
                            TextView textView = new TextView(getApplicationContext());
                            textView.setText("No Data found...");
                            textView.setTextColor(getResources().getColor(R.color.red_close));
                            textView.setPadding(10,10,10,10);

                            ll_parent_suggestion_list.addView(textView);

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


    private void DeleteSuggesstionDetails(String reqId) {

        Log.d("DeleteID",""+reqId);
        String Uiid_id = UUID.randomUUID().toString();

        String API_DeleteDonationInfo = ROOT_URL + "delete_suggesstion.php?_" + Uiid_id;
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
                                        showData();
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
                        params.put("reqId", reqId);
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



    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }

        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Str_Base64Image = "";
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (bm != null) {
            Iv_UploadedImg.setImageBitmap(bm);
            Iv_UploadedImg.setVisibility(View.VISIBLE);
            Str_Base64Image = CommonMethods.getEncoded64ImageStringFromBitmap(bm);
        }else {
            Iv_UploadedImg.setVisibility(View.GONE);
            Str_Base64Image="";
        }

    }

    private void API_ADD_SUGGESTION() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_description.getWindowToken(), 0);
        String MemberName = edt_member_name.getText().toString();
        String StrTitle = edt_title_subject.getText().toString();
        String StrDescription = edt_description.getText().toString();

        String Uiid_id = UUID.randomUUID().toString();


        String URL_user_info = ROOT_URL + "addsuggesstion.php?_" + Uiid_id;
        try {
            Log.d("URL_USerInfo",URL_user_info);

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_user_info,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                Log.d("mainResponse", response);

                                try {

                                    JSONObject jObj = new JSONObject(response);

                                    boolean status = jObj.getBoolean("status");

                                    if (status) {

                                        Toasty.success(getApplicationContext(),"Suggestion / Request Updated Successfully.", Toast.LENGTH_LONG).show();

                                        onBackPressed();

                                    }else {
                                        Toast.makeText(getApplicationContext(), "Unable to save data. Please try again", Toast.LENGTH_SHORT).show();
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
                        params.put("type", StrType);
                        params.put("member_name", MemberName);
                        params.put("subject_title", StrTitle);
                        params.put("description",StrDescription);
                        params.put("attachment",Str_Base64Image);
                        Log.d("ParrasUserRegi", params.toString());
                        return params;


                        /*  = edt_member_name.getText().toString();
         = edt_father_name.getText().toString();
         = edt_spouse_name.getText().toString();
         = edt_member_mobile.getText().toString();
         = edt_spouse_mobile.getText().toString();
         = edt_address1.getText().toString();
         = edt_address2.getText().toString();
        Str_address2 = edt_address3.getText().toString();
         = edt_pincode.getText().toString();
         = edt_city.getText().toString();
         = edt_state.getText().toString();
         = edt_works_with.getText().toString();
        String StrGender = Spn_Gender.getSelectedItem().toString().trim();
        if(StrGender.equalsIgnoreCase("Male")){
             = "M";
        }else if(StrGender.equalsIgnoreCase("Female")){
            Str_gender = "F";
        }

         = edt_member_email.getText().toString();
         = edt_whatsapp_mobile.getText().toString();*/

                    }
                };

                int socketTimeout = 50000; //30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);

            } else {
                if(myDialog!=null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
                CommonMethods.DisplayToast(getApplicationContext(), "Please check your internet connection");
            }
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private boolean isValidFields() {

        boolean result = true;

        if (!MyValidator.isValidSpinner(Spn_Type)) {
            Spn_Type.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Select Type Request or Suggestion ??");

            result = false;
        }

        if (!MyValidator.isValidName(edt_member_name)) {
            edt_member_name.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Member Full Name");
            result = false;
        }

        if (!MyValidator.isValidField(edt_title_subject)) {
            edt_title_subject.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Title / Subject");
            result = false;
        }



        if (!MyValidator.isValidField(edt_description)) {
            edt_description.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(),"Please Enter Description");
            result = false;
        }

        return  result;
    }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), NewDashboard.class);
        startActivity(intent);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }
}
