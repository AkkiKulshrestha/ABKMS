package com.ascend.www.abkms;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ascend.www.abkms.activities.CompleteDetailedProfileInfo;
import com.ascend.www.abkms.activities.ContactUsActivity;
import com.ascend.www.abkms.activities.DonationActivity;
import com.ascend.www.abkms.activities.EducationCorner;
import com.ascend.www.abkms.activities.EventsActivity;
import com.ascend.www.abkms.activities.JobCorner;
import com.ascend.www.abkms.activities.ListActivity;
import com.ascend.www.abkms.activities.MatrimonyCorner;
import com.ascend.www.abkms.activities.MembersListActivity;
import com.ascend.www.abkms.activities.SettingsActivity;
import com.ascend.www.abkms.activities.SuggestionRequest;
import com.ascend.www.abkms.adapters.ImageSliderAdapter;
import com.ascend.www.abkms.model.SliderItem;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;
import com.ascend.www.abkms.webservices.RestClient;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;

/**
 * Created by Akshay on 11-06-2018.
 */

public class NewDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    String Language_selected = "";
    SliderView sliderView;
    CardView CardEvents, CardList, CardOptions, CardSuggestion, CardEducation, CardJob, CardMatrimony, CardDonation, CardTotalCount, CardActiveCount, CardUpdateProfile;
    String StrUserName = "", StrEmail = "", StrMobileNo, StrMemberId;
    Button BtnSignInSignUp, BtnLogout;
    Boolean isLoggedIn = false;
    ProgressDialog myDialog;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    ImageSliderAdapter adapter;
    TextView tv_total_count, tv_total_active_count;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient googleApiClient;
    private String[] IMAGES;
   /* private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        init();
    }

    private boolean isFirstTime() {


        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();
            //ChangeLanguage();

        }
        return ranBefore;

    }

    private void init() {

        MobileAds.initialize(this);

        TextView txt_firm_name = (TextView) findViewById(R.id.txt_firm_name);
        txt_firm_name.setText(getResources().getString(R.string.app_name1));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String IsLoginUser = UtilitySharedPreferences.getPrefs(getApplicationContext(), "IsLoggedIn");
        StrMobileNo = UtilitySharedPreferences.getPrefs(getApplicationContext(), "UserMobile");
        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");
        StrUserName = UtilitySharedPreferences.getPrefs(getApplicationContext(), "UserName");


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
            }
        };


        if (IsLoginUser != null && !IsLoginUser.equalsIgnoreCase("") && !IsLoginUser.equalsIgnoreCase("null")) {
            if (IsLoginUser.equalsIgnoreCase("true")) {
                isLoggedIn = true;
            } else {
                isLoggedIn = false;
            }
        } else {
            isLoggedIn = false;
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View hView = navigationView.getHeaderView(0);
        TextView nav_header_userName = (TextView) hView.findViewById(R.id.nav_header_userName);
        TextView nav_user_email = (TextView) hView.findViewById(R.id.nav_Email);
        if (StrUserName != null && !StrUserName.equalsIgnoreCase("")) {
            nav_header_userName.setText(StrUserName.toUpperCase());
        }
        nav_user_email.setText("Member Id: ABKMS" + StrMemberId + "\nMob No.: " + StrMobileNo);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        BtnSignInSignUp = (Button) navigationView.findViewById(R.id.BtnSignInSignUp);
        BtnLogout = (Button) navigationView.findViewById(R.id.BtnLogout);
        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitySharedPreferences.clearPref(getApplicationContext());
                UtilitySharedPreferences.setPrefs(getApplicationContext(), "IsLoggedIn", "false");
                Intent i = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(i);
                overridePendingTransition(R.animator.left_right, R.animator.right_left);
                finish();
            }
        });
        if (isLoggedIn) {
            BtnLogout.setVisibility(View.VISIBLE);
            BtnSignInSignUp.setVisibility(View.GONE);
        } else {
            BtnSignInSignUp.setVisibility(View.VISIBLE);
            BtnLogout.setVisibility(View.GONE);
        }


        String slider_ary = UtilitySharedPreferences.getPrefs(getApplicationContext(), "SliderImagesArray");

        try {
            JSONObject jsonObject = new JSONObject(slider_ary);

            boolean status = jsonObject.getBoolean("status");
            if (status) {
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for (int slider_image_no = 0; slider_image_no < jsonArray.length(); slider_image_no++) {

                    JSONObject jsonObject1 = jsonArray.getJSONObject(slider_image_no);

                    String ImageName = jsonObject1.getString("image_name");
                    String image_url = jsonObject1.getString("image_url");
                    image_url = RestClient.ROOT_URL + image_url;
                    stringArrayList.add(image_url);
                }
                IMAGES = stringArrayList.toArray(new String[stringArrayList.size()]);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        sliderView = (SliderView) findViewById(R.id.imageSlider);
        sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(getResources().getColor(R.color.colorPrimary));
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                sliderView.setCurrentPagePosition(position);
            }
        });

        adapter = new ImageSliderAdapter(getApplicationContext());
        List<SliderItem> sliderItemList = new ArrayList<>();

        for (String imageUrl : IMAGES) {

            SliderItem sliderItem = new SliderItem();
            Log.d("URL - ", "---> " + imageUrl);
            sliderItem.setImageUrl(imageUrl);
            sliderItemList.add(sliderItem);
            adapter.addItem(sliderItem);
        }

        sliderView.setSliderAdapter(adapter);

        CardEvents = (CardView) findViewById(R.id.CardEvents);
        CardEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EventsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });

        CardList = (CardView) findViewById(R.id.CardList);
        CardList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });

        CardOptions = (CardView) findViewById(R.id.CardOptions);
        CardOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });

        CardSuggestion = (CardView) findViewById(R.id.CardSuggestion);
        CardSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SuggestionRequest.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();

            }
        });


        CardEducation = (CardView) findViewById(R.id.CardEducation);
        CardEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EducationCorner.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();

            }
        });

        CardJob = (CardView) findViewById(R.id.CardJob);
        CardJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JobCorner.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();

            }
        });

        CardMatrimony = (CardView) findViewById(R.id.CardMatrimony);
        CardMatrimony.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MatrimonyCorner.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();

            }
        });

        CardDonation = (CardView) findViewById(R.id.CardDonation);
        CardDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DonationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();

            }
        });

        tv_total_count = (TextView) findViewById(R.id.tv_total_count);
        tv_total_active_count = (TextView) findViewById(R.id.tv_total_active_count);


        CardTotalCount = (CardView) findViewById(R.id.CardTotalCount);
        CardTotalCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitySharedPreferences.setPrefs(getApplicationContext(), "yadi_filter_by", "");
                Intent intent = new Intent(getApplicationContext(), MembersListActivity.class);
                intent.putExtra("filter_by", "all_list");
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);

            }
        });

        CardActiveCount = (CardView) findViewById(R.id.CardActiveCount);
        CardActiveCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitySharedPreferences.setPrefs(getApplicationContext(), "yadi_filter_by", "");
                Intent intent = new Intent(getApplicationContext(), MembersListActivity.class);
                intent.putExtra("filter_by", "active_members");
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
            }
        });


        CardUpdateProfile = (CardView) findViewById(R.id.CardUpdateProfile);
        CardUpdateProfile.setVisibility(View.GONE);
        CardUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberId", StrMemberId);
                Intent i = new Intent(getApplicationContext(), CompleteDetailedProfileInfo.class);
                startActivity(i);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);


        myDialog = new ProgressDialog(NewDashboard.this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        getTotalMembersCuntAPI();

        String ApplicationFormData = UtilitySharedPreferences.getPrefs(getApplicationContext(), "ApplicationData");
        String IS_COMPLETE_PROFILE_UPDATED = UtilitySharedPreferences.getPrefs(getApplicationContext(), "IS_COMPLETE_PROFILE_UPDATED");
        if (IS_COMPLETE_PROFILE_UPDATED != null && !IS_COMPLETE_PROFILE_UPDATED.equalsIgnoreCase("")) {
            if (IS_COMPLETE_PROFILE_UPDATED.equalsIgnoreCase("0")) {
                CardUpdateProfile.setVisibility(View.VISIBLE);
            } else if (IS_COMPLETE_PROFILE_UPDATED.equalsIgnoreCase("1")) {
                CardUpdateProfile.setVisibility(View.GONE);
            }
        }

        if (ApplicationFormData == null || ApplicationFormData.equalsIgnoreCase("")) {
            getDataForApplicant();
        }


    }

    private void getTotalMembersCuntAPI() {
        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            String URL = ROOT_URL + "total_member_count.php?_" + Uiid_id;
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
                                    JSONObject result = jsonObj.getJSONObject("result");
                                    String TotalMembersCount = result.getString("total_count");
                                    String ActiveMembersCount = result.getString("active_count");

                                    tv_total_count.setText(TotalMembersCount);
                                    tv_total_active_count.setText(ActiveMembersCount);


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

                    //Toast.makeText(getApplicationContext(), "Some Error occurred by fetching data. Please try again later.", Toast.LENGTH_SHORT).show();

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


    private void getDataForApplicant() {
        String Uiid_id = UUID.randomUUID().toString();

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            String URL_GetPosData = RestClient.ROOT_URL + "getApplicantDetail.php?_" + Uiid_id + "&member_id=" + StrMemberId;
            Log.d("URL_GetPosData:", "" + URL_GetPosData);
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, URL_GetPosData,
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
                                if (status) {
                                    JSONObject result = jsonObj.getJSONObject("result");
                                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "ApplicationData", result.toString());
                                    String IS_COMPLETE_PROFILE_UPDATED = result.getString("IS_COMPLETE_PROFILE_UPDATED");

                                    String AFFLIATED_ABKMS = result.getString("AFFLIATED_ABKMS");

                                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "AFFLIATED_ABKMS", AFFLIATED_ABKMS);
                                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "IS_COMPLETE_PROFILE_UPDATED", IS_COMPLETE_PROFILE_UPDATED);
                                    if (IS_COMPLETE_PROFILE_UPDATED != null && !IS_COMPLETE_PROFILE_UPDATED.equalsIgnoreCase("")) {
                                        if (IS_COMPLETE_PROFILE_UPDATED.equalsIgnoreCase("0")) {
                                            CardUpdateProfile.setVisibility(View.VISIBLE);
                                        } else if (IS_COMPLETE_PROFILE_UPDATED.equalsIgnoreCase("1")) {
                                            CardUpdateProfile.setVisibility(View.GONE);
                                        }
                                    }

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

                    //Toast.makeText(getApplicationContext(), "Some Error occurred by fetching data. Please try again later.", Toast.LENGTH_SHORT).show();

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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_events) {
            Intent intent = new Intent(getApplicationContext(), EventsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.move_left, R.animator.move_right);
            finish();
        } else if (id == R.id.nav_list) {
            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.move_left, R.animator.move_right);
            finish();
        } else if (id == R.id.nav_education) {
            Intent intent = new Intent(getApplicationContext(), EducationCorner.class);
            startActivity(intent);
            overridePendingTransition(R.animator.move_left, R.animator.move_right);
            finish();
        } else if (id == R.id.nav_job) {
            Intent intent = new Intent(getApplicationContext(), JobCorner.class);
            startActivity(intent);
            overridePendingTransition(R.animator.move_left, R.animator.move_right);
            finish();
        } else if (id == R.id.nav_matrimony) {
            Intent intent = new Intent(getApplicationContext(), MatrimonyCorner.class);
            startActivity(intent);
            overridePendingTransition(R.animator.move_left, R.animator.move_right);
            finish();
        } else if (id == R.id.nav_donation) {
            Intent intent = new Intent(getApplicationContext(), DonationActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.move_left, R.animator.move_right);
            finish();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.move_left, R.animator.move_right);
            finish();
        } else if (id == R.id.nav_update_profile) {
            UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberId", StrMemberId);
            Intent i = new Intent(getApplicationContext(), CompleteDetailedProfileInfo.class);
            startActivity(i);
            overridePendingTransition(R.animator.move_left, R.animator.move_right);
            finish();
        } else if (id == R.id.nav_susggestion) {
            Intent intent = new Intent(getApplicationContext(), SuggestionRequest.class);
            startActivity(intent);
            overridePendingTransition(R.animator.move_left, R.animator.move_right);
            finish();
        } else if (id == R.id.nav_contact_us) {
            Intent i = new Intent(getApplicationContext(), ContactUsActivity.class);
            startActivity(i);
            overridePendingTransition(R.animator.move_left, R.animator.move_right);
            finish();
        } else if (id == R.id.nav_share) {
            Context context = getApplicationContext();
            final String appPackageName = context.getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Download " + getResources().getString(R.string.app_name) + " -  https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");

            context.startActivity(sendIntent);
        } else if (id == R.id.nav_logout) {

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mAuth.signOut();
                // Google sign out
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            }


            UtilitySharedPreferences.clearPref(getApplicationContext());
            UtilitySharedPreferences.setPrefs(getApplicationContext(), "IsLoggedIn", "false");
            Intent i = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(i);
            overridePendingTransition(R.animator.left_right, R.animator.right_left);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void ChangeLanguage() {
        final Dialog dialoglang = new Dialog(this);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialoglang.setTitle(R.string.language_change);
        dialoglang.setCanceledOnTouchOutside(true);
        dialoglang.setContentView(R.layout.popup_multilang);
        RadioGroup rg = (RadioGroup) dialoglang.findViewById(R.id.radio_group);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        Language_selected = btn.getText().toString();
                        Log.e("selected RadioButton->", btn.getText().toString());


                    }
                }
            }
        });
        Button btn_set = (Button) dialoglang.findViewById(R.id.set);
        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Language_selected.equalsIgnoreCase(getResources().getString(R.string.english))) {
                    String languageToLoad = "eng"; // your language
                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "LanguageSelected", languageToLoad);
                    Locale locale = new Locale(languageToLoad);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());
                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "lang", languageToLoad);
                    Intent intent = new Intent(getApplicationContext(), NewDashboard.class);
                    intent.putExtra("lang_flag", languageToLoad);
                    startActivity(intent);
                    overridePendingTransition(R.animator.move_left, R.animator.move_right);
                    finish();
                    dialoglang.dismiss();
                } else if (Language_selected.equalsIgnoreCase(getResources().getString(R.string.hindi))) {
                    String languageToLoad = "hin"; // your language
                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "LanguageSelected", languageToLoad);

                    Locale locale = new Locale(languageToLoad);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());
                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "lang", languageToLoad);
                    Intent intent = new Intent(getApplicationContext(), NewDashboard.class);
                    intent.putExtra("lang_flag", languageToLoad);
                    startActivity(intent);
                    overridePendingTransition(R.animator.move_left, R.animator.move_right);
                    finish();
                    dialoglang.dismiss();
                }

            }
        });
        dialoglang.show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
