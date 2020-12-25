package com.ascend.www.abkms.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

import static androidx.databinding.DataBindingUtil.setContentView;
import static com.ascend.www.abkms.utils.CommonMethods.md5;
import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;


public class SignIn_SignUpActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    String DeviceId,IsVerifiedUser="0";
    int DeviceVersion;
    String client_id,client_name,client_pan,client_mobile,client_email,active,bse_active,kyc;
    String email_verified,mobile_verified;
    GoogleSignInClient mGoogleSignInClient;
    EditText edt_verification_number,EdtMobile,EdtPassword;
    LinearLayout LayoutSignUp;
    ProgressDialog myDialog;
    String SystemOtp,StrMemberId="",StrMobileNo="",StrEmailId="",StrName="",SavedPassword="",StrPassword="",IS_ADMIN="",IS_PASSWORD_SET="";
    SignInButton signInButton;
    Button signOutButton;
    Dialog DialogVerifyMobile,DialogRegisterUser,DailogEnterPassword;
    TextView Tv_ForgetPassword;

    AlertDialog alert,alert1,alert2;
    final ViewGroup nullParent = null;
    String Str_SignupPassword,str_registered_mobile_no="";
    TelephonyManager mTelephonyManager;
    boolean IsGoogleSignIn = false;
    private static final String TAG = "Google";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    String ClientName,ClientEmail;
    EditText edt_member_name,edt_member_mobile,edt_member_email,edt_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_new);

        init();
    }

    @SuppressLint("MissingPermission")

    private void init() {


        EdtMobile = (EditText)findViewById(R.id.EdtMobile);


        Tv_ForgetPassword = (TextView)findViewById(R.id.ForgotPassword);
        Tv_ForgetPassword.setOnClickListener(this);

        //Firebase Initialize
        mAuth = FirebaseAuth.getInstance();


        // Button listeners
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        signInButton.setOnClickListener(this);

        signOutButton = (Button) findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(user);
            }
        };

        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            mAuth.signOut();
            // Google sign out
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            UtilitySharedPreferences.clearPref(getApplicationContext());
        }
        // [END build_client]





        LayoutSignUp = (LinearLayout) findViewById(R.id.LayoutSignUp);
        LayoutSignUp.setOnClickListener(this);


        myDialog = new ProgressDialog(SignIn_SignUpActivity.this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.sign_in_button) {

           signIn();

        }else if (id == R.id.LayoutSignUp) {


            if(isValidFeild()){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(EdtMobile.getWindowToken(), 0);
                }
                StrMobileNo = EdtMobile.getText().toString();
                StrName = "";
                StrEmailId = "";
                IsVerifiedUser = "0";
                IsGoogleSignIn = false;
                fetchUserDetailFromMobile();
            }


        }else if(id==R.id.signOutButton) {
            signOut();
        }else if(id == R.id.ForgotPassword){

            popUpForgotPassword();

        }



    }




    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        myDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                  Log.d(TAG,"Not Successful: "+task.getException().getMessage());
                }
                if(myDialog!=null && myDialog.isShowing()){
                    myDialog.dismiss();
                }
            }
        });
    }



    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.logout);
        alert.setCancelable(false);
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Firebase sign out
                mAuth.signOut();
                // Google sign out
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                updateUI(null);
                            }
                        }
                );
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                UtilitySharedPreferences.setPrefs(getApplicationContext(),"ClientPhotoUrl",user.getPhotoUrl().toString());
                new DownloadImageTask().execute(user.getPhotoUrl().toString());
            }
            Log.d(TAG, "DisplayName: " + user.getDisplayName() + "\n\n"+ "Email: " + user.getEmail()+ "\n\n" + "Firebase ID: " + user.getUid());
            StrName = user.getDisplayName();
            StrEmailId = user.getEmail();
            StrMobileNo = "";
            //IsVerifiedUser = "1";
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            fetchUserDetailFromEmail();

        } else {

            if(myDialog!=null && myDialog.isShowing()){
                myDialog.dismiss();
            }
           signInButton.setVisibility(View.VISIBLE);
           signOutButton.setVisibility(View.GONE);
        }



    }

    private void PopupToVerifyMobileWithOTP() {
        DialogVerifyMobile = new Dialog(SignIn_SignUpActivity.this);
        DialogVerifyMobile.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogVerifyMobile.setCanceledOnTouchOutside(false);
        DialogVerifyMobile.setContentView(R.layout.custom_dialog_for_entering_otp);
        DialogVerifyMobile.getWindow().getAttributes().width = ActionBar.LayoutParams.MATCH_PARENT;



        DialogVerifyMobile.show();
        TextView til_text = (TextView)DialogVerifyMobile.findViewById(R.id.til_text);
        til_text.setText("OTP to verify your mobile is sent on your Mobile No.");

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

                OtpVerifyApi(OTP_Entered,false);
            }
        });

    }

    private void popUpForgotPassword() {
        AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_for_entering_registered_mobile_no, nullParent);
        dialogBuilder1.setView(dialogView);
        dialogBuilder1.setCancelable(false);
        alert1 = dialogBuilder1.create();

        EditText edt_registered_mobile_no  = (EditText) dialogView.findViewById(R.id.edt_registered_mobile_no);

        ImageView iv_close = (ImageView)dialogView.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myDialog!=null && myDialog.isShowing()){
                    myDialog.dismiss();
                }
                alert1.dismiss();
            }
        });


        Button btn_get_otp=(Button) dialogView.findViewById(R.id.btn_get_otp);
        btn_get_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_registered_mobile_no.getText().toString().length()==10) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(edt_registered_mobile_no.getWindowToken(), 0);
                    }
                    StrMobileNo  = edt_registered_mobile_no.getText().toString();
                    GetAPI_ForgotPassword();

                }else {
                    edt_registered_mobile_no.setError("Please Enter Valid Mobile No.");
                }

            }
        });
        alert1.show();
    }

    private void GetAPI_ForgotPassword() {

        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();
        String URL_SendPasswordOTP = ROOT_URL + "send_password_otp.php?_" + Uiid_id;

        try {
            Log.d("URL_SendPasswordOTP",URL_SendPasswordOTP);

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SendPasswordOTP,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(myDialog!=null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                try{
                                    if (response != null) {
                                        JSONObject resObj = new JSONObject(response);
                                        boolean status = resObj.getBoolean("status");
                                        if (status){
                                            String message = resObj.getString("message");

                                            JSONObject dataObj = resObj.getJSONObject("result");
                                            String OTP = dataObj.getString("OTP");
                                            Toast.makeText(getApplicationContext(),"OTP FOR VERIFYING MOBILE IS: "+OTP,Toast.LENGTH_LONG).show();
                                            if(alert1!=null && alert1.isShowing()){
                                                alert1.dismiss();
                                            }
                                            pop_up_otp_verification(true);
                                        }else {
                                            String message = resObj.getString("message");
                                            CommonMethods.DisplayToastError(getApplicationContext(),message);
                                        }
                                    }
                                }catch(Exception e){

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
                        params.put("Mobile", StrMobileNo);
                        Log.d("ParrasUserRegi", params.toString());
                        return params;

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

    private void pop_up_otp_verification(boolean showPopupResetPassword) {
        AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_for_entering_otp, nullParent);
        dialogBuilder1.setView(dialogView);
        dialogBuilder1.setCancelable(false);
        alert1 = dialogBuilder1.create();

        TextView til_text = (TextView)dialogView.findViewById(R.id.til_text) ;
        til_text.setText("Mobile No.: "+StrMobileNo);

        edt_verification_number  = (EditText) dialogView.findViewById(R.id.edt_verification_number);

        ImageView iv_close = (ImageView)dialogView.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myDialog!=null && myDialog.isShowing()){
                    myDialog.dismiss();
                }
                alert1.dismiss();
            }
        });


        TextView Verify_otp=(TextView)dialogView.findViewById(R.id.Verify_otp);
        Verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(edt_verification_number.getWindowToken(), 0);
                }
                String OTP_Entered = edt_verification_number.getText().toString();

                OtpVerifyApi(OTP_Entered,showPopupResetPassword);
            }
        });
        alert1.show();
    }

    private void OtpVerifyApi(final String OTP_Entered,boolean showPopupResetPassword) {

        String Uiid_id = UUID.randomUUID().toString();

        String verifyOTP_Api = ROOT_URL + "verifyUserMobile.php?_" + Uiid_id;
        try {
            Log.d("verifyOTP_Api",verifyOTP_Api);

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, verifyOTP_Api,
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
                                    if(status){
                                        edt_verification_number.setError(null);
                                        if(DialogVerifyMobile!=null && DialogVerifyMobile.isShowing()){
                                            DialogVerifyMobile.dismiss();
                                        }

                                        if(!showPopupResetPassword) {

                                            fetchUserDetailFromMobile();
                                        }else {
                                            if(alert1!=null && alert1.isShowing()){
                                                alert1.dismiss();
                                            }

                                            pop_up_reset_password();

                                        }



                                    }else {
                                        edt_verification_number.setError("Invalid OTP.");


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
                        params.put("Mobile", StrMobileNo);
                        params.put("OTP", OTP_Entered);
                        Log.d("ParrasUserRegi", params.toString());
                        return params;

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

    private void pop_up_reset_password() {

        AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_for_reset_password, nullParent);
        dialogBuilder1.setView(dialogView);
        dialogBuilder1.setCancelable(true);
        alert2 = dialogBuilder1.create();

        TextView tv_registered_mobile_no = (TextView) dialogView.findViewById(R.id.tv_registered_mobile_no);
        tv_registered_mobile_no.setText(str_registered_mobile_no);
        final EditText etNewPassword  = (EditText) dialogView.findViewById(R.id.etNewPassword);
        final EditText etConfirmPassword  = (EditText) dialogView.findViewById(R.id.etConfirmPassword);



        ImageView iv_close = (ImageView)dialogView.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myDialog!=null && myDialog.isShowing()){
                    myDialog.dismiss();
                }
                alert2.dismiss();
            }
        });


        Button btn_reset=(Button) dialogView.findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(etConfirmPassword.getWindowToken(), 0);
                }


                if((etNewPassword.getText().toString()!=null && etNewPassword.getText().toString().length()!=0) && (etConfirmPassword.getText().toString()!=null && etConfirmPassword.getText().toString().length()!=0)){

                    String StrNewPassword = etNewPassword.getText().toString();
                    String StrConfirmPassword = etConfirmPassword.getText().toString();
                    if(StrNewPassword.equalsIgnoreCase(StrConfirmPassword)){

                        ResetPasswordApi(StrNewPassword,StrConfirmPassword);

                    }else {

                        etConfirmPassword.setError("New Password & Confirm Password does not Match.");
                    }

                }


            }
        });
        alert2.show();

    }

    private void ResetPasswordApi(final String strNewPassword,String strConfirmPassword) {

        myDialog.show();

        String Uiid_id = UUID.randomUUID().toString();

        String URL_ResetPassword = ROOT_URL + "change_password.php?_" + Uiid_id;
        try {
            Log.d("URL_ResetPassword",URL_ResetPassword);

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ResetPassword,
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
                                    if (status){
                                        String message = jObj.getString("message");
                                        CommonMethods.DisplayToastSuccess(getApplicationContext(),message);
                                        if(alert2!=null && alert2.isShowing()){
                                            alert2.dismiss();
                                        }

                                    }else {
                                        String message = jObj.getString("message");
                                        CommonMethods.DisplayToastError(getApplicationContext(),message);
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
                        params.put("Mobile", StrMobileNo);
                        params.put("Password", md5(strNewPassword));
                        Log.d("ParrasUserRegi", params.toString());
                        return params;

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

    private void fetchUserDetailFromEmail() {


        String Uiid_id = UUID.randomUUID().toString();
        String URL_FetchDetails = ROOT_URL + "fetch_user_detail_from_email.php?_"+Uiid_id+"&email_id="+StrEmailId;
        try {
            Log.d("URL_FetchDetails",URL_FetchDetails);

            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FetchDetails,
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

                                        JSONObject jsonObject = jobj.getJSONObject("result");
                                        StrMemberId = jsonObject.getString("ID");
                                        String PASSWORD = jsonObject.getString("PASSWORD");
                                        StrMobileNo = jsonObject.getString("MOBILE_NO");
                                        String mobile_otp = jsonObject.getString("OTP");
                                        String IS_MOBILE_VERIFIED = jsonObject.getString("IS_MOBILE_VERIFIED");
                                        IS_PASSWORD_SET = jsonObject.getString("IS_PASSWORD_SET");
                                        StrEmailId = jsonObject.getString("EMAIL_ID");

                                        StrName = jsonObject.getString("NAME");
                                        String FATHER_NAME = jsonObject.getString("FATHER_NAME");
                                        String MARITAL_STATUS = jsonObject.getString("MARITAL_STATUS");
                                        String SPOUSE_NAME = jsonObject.getString("SPOUSE_NAME");

                                        String FAMILY_MEMBER_COUNT = jsonObject.getString("FAMILY_MEMBER_COUNT");
                                        String WORKS_WITH = jsonObject.getString("WORKS_WITH");
                                        String GENDER = jsonObject.getString("GENDER");
                                        String AGE = jsonObject.getString("AGE");
                                        String DOB = jsonObject.getString("DOB");
                                        String LANDLINE = jsonObject.getString("LANDLINE");
                                        String MOBILE_NO1 = jsonObject.getString("MOBILE_NO1");
                                        String SPOUSE_MOBILE_NO = jsonObject.getString("SPOUSE_MOBILE_NO");
                                        String WHATSAPP_NO = jsonObject.getString("WHATSAPP_NO");

                                        String ADDRESS1 = jsonObject.getString("ADDRESS1");
                                        String ADDRESS2 = jsonObject.getString("ADDRESS2");
                                        String ADDRESS3 = jsonObject.getString("ADDRESS3");
                                        String CITY = jsonObject.getString("CITY");
                                        String STATE = jsonObject.getString("STATE");
                                        String PINCODE = jsonObject.getString("PINCODE");
                                        IS_ADMIN = jsonObject.getString("IS_ADMIN");


                                        if(IS_PASSWORD_SET!=null && IS_PASSWORD_SET.equalsIgnoreCase("1") && PASSWORD!=null && !PASSWORD.equalsIgnoreCase("") && !PASSWORD.equalsIgnoreCase("null")){
                                            SavedPassword = jsonObject.getString("PASSWORD");
                                            PopupVerifyPassword();


                                        }else if((IS_PASSWORD_SET!=null && !IS_PASSWORD_SET.equalsIgnoreCase("") && IS_PASSWORD_SET.equalsIgnoreCase("0"))){

                                            CreateProfileDailog();

                                        }
                                    }else {

                                        StrMemberId = "";
                                        CreateProfileDailog();

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
                }) ;

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

    private void fetchUserDetailFromMobile() {

        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();
        String URL_FetchDetails = ROOT_URL + "fetch_user_detail_from_mobile.php?_"+Uiid_id+"&mobile="+StrMobileNo;
        try {
            Log.d("URL_FetchDetails",URL_FetchDetails);

            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FetchDetails,
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

                                        JSONObject jsonObject = jobj.getJSONObject("result");
                                        StrMemberId = jsonObject.getString("ID");
                                        String PASSWORD = jsonObject.getString("PASSWORD");
                                        StrMobileNo = jsonObject.getString("MOBILE_NO");
                                        String mobile_otp = jsonObject.getString("OTP");
                                        String IS_MOBILE_VERIFIED = jsonObject.getString("IS_MOBILE_VERIFIED");
                                        IS_PASSWORD_SET = jsonObject.getString("IS_PASSWORD_SET");
                                        StrEmailId = jsonObject.getString("EMAIL_ID");

                                        StrName = jsonObject.getString("NAME");
                                        String FATHER_NAME = jsonObject.getString("FATHER_NAME");
                                        String MARITAL_STATUS = jsonObject.getString("MARITAL_STATUS");
                                        String SPOUSE_NAME = jsonObject.getString("SPOUSE_NAME");

                                        String FAMILY_MEMBER_COUNT = jsonObject.getString("FAMILY_MEMBER_COUNT");
                                        String WORKS_WITH = jsonObject.getString("WORKS_WITH");
                                        String GENDER = jsonObject.getString("GENDER");
                                        String AGE = jsonObject.getString("AGE");
                                        String DOB = jsonObject.getString("DOB");
                                        String LANDLINE = jsonObject.getString("LANDLINE");
                                        String MOBILE_NO1 = jsonObject.getString("MOBILE_NO1");
                                        String SPOUSE_MOBILE_NO = jsonObject.getString("SPOUSE_MOBILE_NO");
                                        String WHATSAPP_NO = jsonObject.getString("WHATSAPP_NO");

                                        String ADDRESS1 = jsonObject.getString("ADDRESS1");
                                        String ADDRESS2 = jsonObject.getString("ADDRESS2");
                                        String ADDRESS3 = jsonObject.getString("ADDRESS3");
                                        String CITY = jsonObject.getString("CITY");
                                        String STATE = jsonObject.getString("STATE");
                                        String PINCODE = jsonObject.getString("PINCODE");
                                        IS_ADMIN = jsonObject.getString("IS_ADMIN");


                                        if(IS_PASSWORD_SET!=null && IS_PASSWORD_SET.equalsIgnoreCase("1") && PASSWORD!=null && !PASSWORD.equalsIgnoreCase("") && !PASSWORD.equalsIgnoreCase("null")){
                                            SavedPassword = jsonObject.getString("PASSWORD");
                                            PopupVerifyPassword();


                                        }else if((IS_PASSWORD_SET!=null && !IS_PASSWORD_SET.equalsIgnoreCase("") && IS_PASSWORD_SET.equalsIgnoreCase("0"))){

                                                CreateProfileDailog();

                                        }
                                    }else {

                                        StrMemberId = "";
                                        CreateProfileDailog();

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
                }) ;

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

    private void PopupVerifyPassword() {
        String msg= "Please Enter Your Password.";

        DailogEnterPassword = new Dialog(SignIn_SignUpActivity.this);
        DailogEnterPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DailogEnterPassword.setCanceledOnTouchOutside(false);
        DailogEnterPassword.setCancelable(false);
        DailogEnterPassword.setContentView(R.layout.custom_dialog_for_password);
        Objects.requireNonNull(DailogEnterPassword.getWindow()).getAttributes().width = ActionBar.LayoutParams.MATCH_PARENT;



        DailogEnterPassword.show();

        TextView text= (TextView)DailogEnterPassword.findViewById(R.id.text);
        text.setVisibility(View.GONE);
        TextView text1 = (TextView)DailogEnterPassword.findViewById(R.id.text1);
        text1.setVisibility(View.GONE);

        ImageView iv_close = (ImageView) DailogEnterPassword.findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DailogEnterPassword!=null && DailogEnterPassword.isShowing()){
                    DailogEnterPassword.dismiss();
                }

                if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
                    mAuth.signOut();
                    // Google sign out
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    UtilitySharedPreferences.clearPref(getApplicationContext());
                }

            }
        });

        String UserDetail = "Name: "+StrName + "\nMobile No.: "+StrMobileNo;

        TextView tv_user_detail = (TextView)DailogEnterPassword.findViewById(R.id.tv_user_detail);
        tv_user_detail.setText(UserDetail);

        EdtPassword = (EditText)DailogEnterPassword.findViewById(R.id.etPassword);
        TextView verify=(TextView)DailogEnterPassword.findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EdtPassword.getText().toString()!=null && !EdtPassword.getText().toString().equalsIgnoreCase("")){

                    String md_HashPassword = md5(EdtPassword.getText().toString());
                    Log.d("md5 Hash Psss",""+md_HashPassword);
                    if (md_HashPassword != null) {
                        if(md_HashPassword.equalsIgnoreCase(SavedPassword)){
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(EdtPassword.getWindowToken(), 0);
                            }
                            if(DailogEnterPassword!=null && DailogEnterPassword.isShowing()){
                                DailogEnterPassword.dismiss();
                            }
                            moveToDashboard();

                        }else {

                            EdtPassword.setError("Password does not match. Please try again.");
                        }
                    }

                }
            }
        });


    }

    private void moveToDashboard() {
        UtilitySharedPreferences.setPrefs(getApplicationContext(), "UserMobile", StrMobileNo);
        UtilitySharedPreferences.setPrefs(getApplicationContext(), "UserName", StrName);
        UtilitySharedPreferences.setPrefs(getApplicationContext(), "UserEmail", StrEmailId);
        UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberId",StrMemberId);
        UtilitySharedPreferences.setPrefs(getApplicationContext(),"IsLoggedIn","true");
        UtilitySharedPreferences.setPrefs(getApplicationContext(),"IsAdmin",IS_ADMIN);

        Intent i = new Intent(getApplicationContext(), NewDashboard.class);
        startActivity(i);
        overridePendingTransition(R.animator.move_left, R.animator.move_right);
        finish();
    }

    private void CreateProfileDailog() {
        final Dialog dialog = new Dialog(SignIn_SignUpActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.pop_create_profile);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView)dialog.findViewById(R.id.title) ;
        TextView Upgrade_text = (TextView)dialog.findViewById(R.id.Upgrade_text) ;
        Button btn_try_with_other_no = (Button)dialog.findViewById(R.id.btn_try_with_other_no);
        Button btn_create_profile = (Button)dialog.findViewById(R.id.btn_create_profile);

        dialog.show();
        btn_try_with_other_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
                    mAuth.signOut();
                    // Google sign out
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    UtilitySharedPreferences.clearPref(getApplicationContext());
                }
            }
        });

        btn_create_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showPopupToRegisterUser();
            }
        });
    }

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

                                        JSONObject  result = jsonObject.getJSONObject("result");
                                        SystemOtp = result.getString("OTP");
                                        Toasty.success(getApplicationContext(),"OTP to verify Mobile is : "+SystemOtp, Toast.LENGTH_SHORT).show();

                                        PopupToVerifyMobileWithOTP();

                                        //registerUserAPI();



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
                        params.put("Mobile", StrMobileNo);
                        params.put("MEMBER_ID", StrMemberId);
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


    private void showPopupToRegisterUser() {

        DialogRegisterUser = new Dialog(SignIn_SignUpActivity.this);
        DialogRegisterUser.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogRegisterUser.setCanceledOnTouchOutside(false);
        DialogRegisterUser.setCancelable(false);
        DialogRegisterUser.setContentView(R.layout.pop_register_user);
        DialogRegisterUser.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView)DialogRegisterUser.findViewById(R.id.title) ;
        TextView tv_member_id = (TextView)DialogRegisterUser.findViewById(R.id.tv_member_id) ;
        edt_member_name = (EditText)DialogRegisterUser.findViewById(R.id.edt_member_name);
        edt_member_email = (EditText)DialogRegisterUser.findViewById(R.id.edt_member_email);
        edt_member_mobile = (EditText)DialogRegisterUser.findViewById(R.id.edt_member_mobile);
        edt_password = (EditText)DialogRegisterUser.findViewById(R.id.edt_password);

        if(StrMemberId!=null && !StrMemberId.equalsIgnoreCase("") && !StrMemberId.equalsIgnoreCase("null")){
            tv_member_id.setText(StrMemberId);
        }

        if(StrName!=null && !StrName.equalsIgnoreCase("") && !StrName.equalsIgnoreCase("null")){
            edt_member_name.setText(StrName);
        }

        if(StrEmailId!=null && !StrEmailId.equalsIgnoreCase("") && !StrEmailId.equalsIgnoreCase("null")){
            edt_member_email.setText(StrEmailId);
        }

        if(StrMobileNo!=null && !StrMobileNo.equalsIgnoreCase("") && !StrMobileNo.equalsIgnoreCase("null")){
            edt_member_mobile.setText(StrMobileNo);
        }



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

        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();
        String URL_FetchDetails = ROOT_URL + "register_user.php";
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

                                        JSONObject jsonObject = jobj.getJSONObject("result");
                                        StrMemberId = jsonObject.getString("ID");
                                        String PASSWORD = jsonObject.getString("PASSWORD");
                                        StrMobileNo = jsonObject.getString("MOBILE_NO");
                                        String mobile_otp = jsonObject.getString("OTP");
                                        String IS_MOBILE_VERIFIED = jsonObject.getString("IS_MOBILE_VERIFIED");
                                        IS_PASSWORD_SET = jsonObject.getString("IS_PASSWORD_SET");
                                        StrEmailId = jsonObject.getString("EMAIL_ID");

                                        StrName = jsonObject.getString("NAME");
                                        String FATHER_NAME = jsonObject.getString("FATHER_NAME");
                                        String MARITAL_STATUS = jsonObject.getString("MARITAL_STATUS");
                                        String SPOUSE_NAME = jsonObject.getString("SPOUSE_NAME");

                                        String FAMILY_MEMBER_COUNT = jsonObject.getString("FAMILY_MEMBER_COUNT");
                                        String WORKS_WITH = jsonObject.getString("WORKS_WITH");
                                        String GENDER = jsonObject.getString("GENDER");
                                        String AGE = jsonObject.getString("AGE");
                                        String DOB = jsonObject.getString("DOB");
                                        String LANDLINE = jsonObject.getString("LANDLINE");
                                        String MOBILE_NO1 = jsonObject.getString("MOBILE_NO1");
                                        String SPOUSE_MOBILE_NO = jsonObject.getString("SPOUSE_MOBILE_NO");
                                        String WHATSAPP_NO = jsonObject.getString("WHATSAPP_NO");

                                        String ADDRESS1 = jsonObject.getString("ADDRESS1");
                                        String ADDRESS2 = jsonObject.getString("ADDRESS2");
                                        String ADDRESS3 = jsonObject.getString("ADDRESS3");
                                        String CITY = jsonObject.getString("CITY");
                                        String STATE = jsonObject.getString("STATE");
                                        String PINCODE = jsonObject.getString("PINCODE");
                                        IS_ADMIN = jsonObject.getString("IS_ADMIN");

                                        if(DialogRegisterUser!=null && DialogRegisterUser.isShowing()){
                                            DialogRegisterUser.dismiss();
                                        }

                                        if(IS_PASSWORD_SET!=null && IS_PASSWORD_SET.equalsIgnoreCase("1") && PASSWORD!=null && !PASSWORD.equalsIgnoreCase("") && !PASSWORD.equalsIgnoreCase("null")){
                                            SavedPassword = jsonObject.getString("PASSWORD");
                                            PopupVerifyPassword();


                                        }else if((IS_PASSWORD_SET!=null && !IS_PASSWORD_SET.equalsIgnoreCase("") && IS_PASSWORD_SET.equalsIgnoreCase("0"))){

                                            CreateProfileDailog();

                                        }
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
                        params.put("member_id",StrMemberId);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap mIcon = null;
            try {
                InputStream in = new URL(urls[0]).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                Log.d(TAG,"IMG BItmap: "+result);
                /*ImageView mImageView = new ImageView(getApplicationContext());
                mImageView.getLayoutParams().width = (getResources().getDisplayMetrics().widthPixels / 100) * 24;
                mImageView.setImageBitmap(result);*/

            }
        }
    }






    private boolean isValidFeild() {
        boolean result = true;

        if (!MyValidator.isValidMobile(EdtMobile)) {
            EdtMobile.requestFocus();
            result = false;
        }


        return  result;
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }



}