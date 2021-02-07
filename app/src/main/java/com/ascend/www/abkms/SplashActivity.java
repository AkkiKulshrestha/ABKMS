package com.ascend.www.abkms;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ascend.www.abkms.activities.SignIn_SignUpActivity;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;


public class SplashActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 24;
    private static final String TAG = "SplashActivity";
    private static int SPLASH_TIME_OUT = 3000;
    Thread splashTread;
    String Force_Update_flag = "0";
    ;
    int YourApkVersionCode;
    String SystemOtp, StrMemberId = "", StrMobileNo = "", StrEmailId = "", StrName = "", SavedPassword = "", StrPassword = "", IS_ADMIN = "", IS_PASSWORD_SET = "";
    EditText edt_member_name, edt_member_mobile, edt_member_email, edt_password;
    ProgressDialog myDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();
        fetchSliderImageArray();

    }

    private void init() {
        String languageToLoad = "eng"; // your language

        StrMobileNo = UtilitySharedPreferences.getPrefs(getApplicationContext(), "UserMobile");
        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;


        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            force_update();
            //startSpalashScreen();
        } else {
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please check Internet Connection");
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        //checkInternetConnection(null);

    }


    private Boolean checkInternetConnection(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }


        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                force_update();
                //startSpalashScreen();
            } else {
                CommonMethods.DisplayToastWarning(getApplicationContext(), "Please check Internet Connection");
            }

        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }


    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_alert_no_intenet);
        builder.setMessage(R.string.msg_alert_no_internet);
        builder.setCancelable(false);
        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Block the Application Execution until user grants the permissions
                        if (checkInternetConnection(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                                boolean isInternetPresent = cd.isConnectingToInternet();
                                if (isInternetPresent) {
                                    force_update();
                                    //startSpalashScreen();
                                } else {
                                    CommonMethods.DisplayToastWarning(getApplicationContext(), "Please check Internet Connection");
                                }
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }

                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        int permissionState2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     */
    private void requestPermissions() {

        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        boolean shouldProvideRationale2 = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        boolean shouldProvideRationale3 = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2 || shouldProvideRationale3) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(SplashActivity.this,
                                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.CAMERA, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CALL_PHONE}, REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(SplashActivity.this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CALL_PHONE}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    force_update();
                    //startSpalashScreen();
                } else {
                    CommonMethods.DisplayToastWarning(getApplicationContext(), "Please check Internet Connection");
                }
            } else {
                // Permission denied.

                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    private void force_update() {


        int currentVersionCode = getCurrentVersion();
        //Log.d("Current version = ",currentVersionCode);
        String Uiid_id = UUID.randomUUID().toString();
        final String get_latest_version_info = ROOT_URL + "getLatestUpdateVersion.php?_" + Uiid_id;
        Log.d("URL --->", get_latest_version_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_latest_version_info, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Response", "" + response);
                            String latestVersion = "";
                            JSONObject Jobj = new JSONObject(response);
                            String data = Jobj.getString("data");
                            JSONObject jobject = new JSONObject(data);

                            String Id = jobject.getString("id");
                            String VersionCode = jobject.getString("version_code");
                            String VersionName = jobject.getString("version_name");
                            Force_Update_flag = jobject.getString("is_force_update");
                            update_dialog(VersionCode);

                            Log.d("Latest version:", latestVersion);
                        } catch (Exception e) {
                            Log.d("Exception", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d("Vollley Err", volleyError.toString());
                        if (volleyError.toString().equalsIgnoreCase("com.android.volley.ServerError")) {
                            CommonMethods.DisplayToast(getApplicationContext(), "App under maintenance");
                        }
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(20), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                requestQueue.add(stringRequest);
            } else {
                CommonMethods.DisplayToast(this, "No Internet Connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getCurrentVersion() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        String currentVersion = pInfo.versionName;
        YourApkVersionCode = pInfo.versionCode;
        Log.d("YourVersionCode", "" + YourApkVersionCode);
        String version_code = String.valueOf(YourApkVersionCode);
        return YourApkVersionCode;
    }


    private void update_dialog(String versionCode) {
        if (versionCode != "" || versionCode != null) {
            int v_code = Integer.valueOf(versionCode);
            if ((YourApkVersionCode < v_code) && Force_Update_flag.equalsIgnoreCase("1")) {
                Log.d("version code", "" + v_code);
                Log.d("Your APK CODE ", "" + YourApkVersionCode);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Dialog dialog = new Dialog(SplashActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.pop_up_app_update);
                        dialog.getWindow().setBackgroundDrawable(
                                new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        TextView title = (TextView) dialog.findViewById(R.id.title);
                        TextView Upgrade_text = (TextView) dialog.findViewById(R.id.Upgrade_text);
                        TextView tv_ok = (TextView) dialog.findViewById(R.id.tv_ok);
                        dialog.show();
                        tv_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken(getString(R.string.default_web_client_id))
                                        .requestEmail()
                                        .build();
                                mGoogleApiClient = new GoogleApiClient.Builder(SplashActivity.this)
                                        .enableAutoManage(SplashActivity.this, SplashActivity.this)
                                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                                        .build();

                                /**/


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
                                        //updateUI(user);
                                    }
                                };


                                UtilitySharedPreferences.clearPref(getApplicationContext());
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "IsLoggedIn", "false");
                                CommonMethods.deleteCache(getApplicationContext());
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                                finish();
                            }
                        });
                    }
                }, SPLASH_TIME_OUT);
            } else {
                /*UtilitySharedPreferences.clearPref(this);*/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ((StrMemberId != null && !StrMemberId.equalsIgnoreCase("")) || (StrMobileNo != null && !StrMobileNo.equalsIgnoreCase(""))) {
                            getUserCurrentStateStatus();
                        } else {
                            Intent i = new Intent(getApplicationContext(), SignIn_SignUpActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.animator.move_left, R.animator.move_right);
                            finish();
                        }
                    }
                }, SPLASH_TIME_OUT);

            }
        } else {
            if ((StrMemberId != null && !StrMemberId.equalsIgnoreCase("")) || (StrMobileNo != null && !StrMobileNo.equalsIgnoreCase(""))) {
                getUserCurrentStateStatus();
            } else {
                Intent i = new Intent(getApplicationContext(), SignIn_SignUpActivity.class);
                startActivity(i);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();

            }
        }
    }


    private void getUserCurrentStateStatus() {

        String Uiid_id = UUID.randomUUID().toString();
        String URL_FetchDetails = ROOT_URL + "fetch_user_detail_from_mobile.php?_" + Uiid_id + "&mobile=" + StrMobileNo;
        try {
            Log.d("URL_FetchDetails", URL_FetchDetails);

            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FetchDetails,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    Log.d("mainResponse", response);

                                    JSONObject jobj = new JSONObject(response);
                                    boolean status = jobj.getBoolean("status");
                                    String message = jobj.getString("message");

                                    if (status) {

                                        JSONObject jsonObject = jobj.getJSONObject("result");
                                        StrMemberId = jsonObject.getString("ID");
                                        StrPassword = jsonObject.getString("PASSWORD");
                                        StrMobileNo = jsonObject.getString("MOBILE_NO");
                                        String mobile_otp = jsonObject.getString("OTP");
                                        String IS_MOBILE_VERIFIED = jsonObject.getString("IS_MOBILE_VERIFIED");
                                        String IS_PASSWORD_SET = jsonObject.getString("IS_PASSWORD_SET");
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


                                        if ((IS_PASSWORD_SET != null && !IS_PASSWORD_SET.equalsIgnoreCase("") && IS_PASSWORD_SET.equalsIgnoreCase("1"))) {
                                            UtilitySharedPreferences.setPrefs(getApplicationContext(), "UserMobile", StrMobileNo);
                                            UtilitySharedPreferences.setPrefs(getApplicationContext(), "UserName", StrName);
                                            UtilitySharedPreferences.setPrefs(getApplicationContext(), "UserEmail", StrEmailId);
                                            UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberId", StrMemberId);
                                            UtilitySharedPreferences.setPrefs(getApplicationContext(), "IsLoggedIn", "true");

                                            Intent i = new Intent(getApplicationContext(), NewDashboard.class);
                                            startActivity(i);
                                            overridePendingTransition(R.animator.move_left, R.animator.move_right);
                                            finish();
                                        } else {

                                            Intent i = new Intent(getApplicationContext(), SignIn_SignUpActivity.class);
                                            startActivity(i);
                                            overridePendingTransition(R.animator.move_left, R.animator.move_right);
                                            finish();
                                        }
                                    } else {
                                        Intent i = new Intent(getApplicationContext(), SignIn_SignUpActivity.class);
                                        startActivity(i);
                                        overridePendingTransition(R.animator.move_left, R.animator.move_right);
                                        finish();
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

                int socketTimeout = 50000; //30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

            } else {

                CommonMethods.DisplayToast(this, "Please check your internet connection");
            }
        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    private void fetchSliderImageArray() {

        String Uiid_id = UUID.randomUUID().toString();

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = ROOT_URL + "fetch_slider_image.php?_" + Uiid_id;
            Log.d("URL", "--> " + URL);
            StringRequest postrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("URL Response", "--> " + response);
                        JSONObject jsonresponse = new JSONObject(response);
                        UtilitySharedPreferences.setPrefs(getApplicationContext(), "SliderImagesArray", jsonresponse.toString());
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    volleyError.printStackTrace();
                    CommonMethods.DisplayToastWarning(getApplicationContext(), "Something goes wrong. Please try again");
                }
            });
            int socketTimeout = 50000; //30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postrequest.setRetryPolicy(policy);
            // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postrequest);
        } else {
            CommonMethods.DisplayToastWarning(getApplicationContext(), "No Internet Connect");

        }
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.exit)
                .setMessage(R.string.strExit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                }).setNegativeButton(R.string.no, null).show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (splashTread != null && splashTread.isAlive()) {
                splashTread.interrupt();
            }

            finish();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            finish();
            return true;
        } else
            return super.onKeyDown(keyCode, event);


    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAuthListener != null && mAuth != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
