package com.ascend.www.abkms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ascend.www.abkms.NewDashboard;
import com.ascend.www.abkms.R;
import com.ascend.www.abkms.adapters.MemberListAdapter;
import com.ascend.www.abkms.model.MatdarListModel;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;
import com.ascend.www.abkms.webservices.RestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class MembersListActivity extends AppCompatActivity {

    String SearchFilterBy = "",ListFilterBy="",SearchKey="";
    ImageView back_btn_toolbar;
    String LanguageSelected="eng";
    EditText editTextSearch;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recycler_list;
    RecyclerView.LayoutManager layoutManager;
    private MemberListAdapter memberListAdapter;
    ArrayList<MatdarListModel> matdarModel_list = new ArrayList<>();
    MatdarListModel matdaarModel;
    ProgressDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_search);

        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("filter_by")!= null)
        {
            SearchFilterBy = bundle.getString("filter_by");
        }else {
            SearchFilterBy = "all_list";
        }
        Log.d("SearchFilterBy:",""+SearchFilterBy);


        init();
    }

    private void init() {

        back_btn_toolbar = (ImageView)findViewById(R.id.back_btn_toolbar);
        back_btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView til_text = (TextView)findViewById(R.id.til_text);
        if(SearchFilterBy.equalsIgnoreCase("all_list")){
            til_text.setText(getResources().getString(R.string.entire_list));
        }else if(SearchFilterBy.equalsIgnoreCase("state")){
            til_text.setText(getResources().getString(R.string.by_state));
        }else if(SearchFilterBy.equalsIgnoreCase("city")){
            til_text.setText(getResources().getString(R.string.by_city));
        }else if(SearchFilterBy.equalsIgnoreCase("mobile")){
            til_text.setText(getResources().getString(R.string.by_mobile));
        }else if(SearchFilterBy.equalsIgnoreCase("active_members")){
            til_text.setText(getResources().getString(R.string.active_members));
        }else {
            til_text.setText(SearchFilterBy);
        }

        LanguageSelected = UtilitySharedPreferences.getPrefs(getApplicationContext(),"LanguageSelected");
        UtilitySharedPreferences.setPrefs(getApplicationContext(),"SearchFilterBy",SearchFilterBy);

        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        //search(editTextSearch);
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (editTextSearch.getText().toString().length()>0) {
                        String key = editTextSearch.getText().toString().trim();
                        API_SearchByKey(key);
                    }else {
                        showData();
                    }
                }
                return false;
            }
        });

        ListFilterBy = UtilitySharedPreferences.getPrefs(getApplicationContext(),"yadi_filter_by");
        if(ListFilterBy==null){
            ListFilterBy = "";
        }
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
                        matdarModel_list.clear();
                        showData();
                        memberListAdapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                },1000);
                recycler_list.setItemAnimator(new DefaultItemAnimator());
            }
        });


        myDialog = new ProgressDialog(MembersListActivity.this);
        myDialog.setMessage(getResources().getString(R.string.please_wait));
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(true);
        //showData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        showData();
    }

    private void API_SearchByKey(final String key) {
        myDialog.show();
        if(matdarModel_list!=null && matdarModel_list.size()>0){
            matdarModel_list = new ArrayList<>();
            memberListAdapter = new MemberListAdapter(MembersListActivity.this, matdarModel_list, refreshLayout);
            recycler_list.setAdapter(memberListAdapter);
            memberListAdapter.notifyDataSetChanged();

        }

        String Uiid_id = UUID.randomUUID().toString();

        if (ListFilterBy != null && !ListFilterBy.equalsIgnoreCase("null") && !ListFilterBy.equalsIgnoreCase("")) {
            ListFilterBy = ListFilterBy;
        } else {
            ListFilterBy = "";
        }
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = RestClient.ROOT_URL + "membersearch.php?_"+Uiid_id+"&lang="+LanguageSelected+"&filter="+SearchFilterBy+"&yadi_filter="+ListFilterBy+"&search="+key;
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

                                //Customer vehicle info
                                String ID = customer_inspect_obj.getString("ID");
                                String NAME = customer_inspect_obj.getString("NAME");
                                String FATHER_NAME = customer_inspect_obj.getString("FATHER_NAME");
                                String SPOUSE_NAME = customer_inspect_obj.getString("SPOUSE_NAME");
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
                                String WHATSAPP_NO = customer_inspect_obj.getString("WHATSAPP_NO");
                                String AFFLIATED_ABKMS = customer_inspect_obj.getString("AFFLIATED_ABKMS");

                                matdaarModel = new MatdarListModel();
                                matdaarModel.setID(ID);
                                matdaarModel.setNAME(NAME);
                                matdaarModel.setFATHER_NAME(FATHER_NAME);
                                matdaarModel.setSPOUSE_NAME(SPOUSE_NAME);
                                matdaarModel.setFAMILY_MEMBER_COUNT(FAMILY_MEMBER_COUNT);
                                matdaarModel.setGENDER(GENDER);
                                matdaarModel.setAGE(AGE);
                                matdaarModel.setDOB(DateOfBirth);
                                matdaarModel.setADDRESS1(ADDRESS1);
                                matdaarModel.setADDRESS2(ADDRESS2);
                                matdaarModel.setADDRESS3(ADDRESS3);
                                matdaarModel.setCITY(CITY);
                                matdaarModel.setSTATE(STATE);
                                matdaarModel.setPINCODE(PINCODE);
                                matdaarModel.setMOBILE_NO(MOBILE_NO);
                                matdaarModel.setMOBILE_NO1(MOBILE_NO1);
                                matdaarModel.setWHATSAPP_NO(WHATSAPP_NO);
                                matdaarModel.setAFFLIATED_ABKMS(AFFLIATED_ABKMS);
                                matdarModel_list.add(matdaarModel);

                            }

                        }

                        memberListAdapter = new MemberListAdapter(MembersListActivity.this, matdarModel_list, refreshLayout);
                        recycler_list.setAdapter(memberListAdapter);
                        memberListAdapter.notifyDataSetChanged();


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

    private void showData() {
        myDialog = new ProgressDialog(MembersListActivity.this);
        myDialog.setMessage(getResources().getString(R.string.please_wait));
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.show();
        if(matdarModel_list!=null && matdarModel_list.size()>0){
            matdarModel_list = new ArrayList<>();
            memberListAdapter = new MemberListAdapter(MembersListActivity.this, matdarModel_list, refreshLayout);
            recycler_list.setAdapter(memberListAdapter);
            memberListAdapter.notifyDataSetChanged();

        }
        String Uiid_id = UUID.randomUUID().toString();

        if (ListFilterBy != null && !ListFilterBy.equalsIgnoreCase("null") && !ListFilterBy.equalsIgnoreCase("")) {
            ListFilterBy = ListFilterBy;
        } else {
            ListFilterBy = "";
        }
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = RestClient.ROOT_URL + "memberlist.php?_"+Uiid_id+"&lang="+LanguageSelected+"&filter="+SearchFilterBy+"&yadi_filter="+ListFilterBy;
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

                                //Customer vehicle info
                                String ID = customer_inspect_obj.getString("ID");
                                String NAME = customer_inspect_obj.getString("NAME");
                                String FATHER_NAME = customer_inspect_obj.getString("FATHER_NAME");
                                String SPOUSE_NAME = customer_inspect_obj.getString("SPOUSE_NAME");
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
                                String WHATSAPP_NO = customer_inspect_obj.getString("WHATSAPP_NO");
                                String AFFLIATED_ABKMS = customer_inspect_obj.getString("AFFLIATED_ABKMS");

                                matdaarModel = new MatdarListModel();
                                matdaarModel.setID(ID);
                                matdaarModel.setNAME(NAME);
                                matdaarModel.setFATHER_NAME(FATHER_NAME);
                                matdaarModel.setSPOUSE_NAME(SPOUSE_NAME);
                                matdaarModel.setFAMILY_MEMBER_COUNT(FAMILY_MEMBER_COUNT);
                                matdaarModel.setGENDER(GENDER);
                                matdaarModel.setAGE(AGE);
                                matdaarModel.setDOB(DateOfBirth);
                                matdaarModel.setADDRESS1(ADDRESS1);
                                matdaarModel.setADDRESS2(ADDRESS2);
                                matdaarModel.setADDRESS3(ADDRESS3);
                                matdaarModel.setCITY(CITY);
                                matdaarModel.setSTATE(STATE);
                                matdaarModel.setPINCODE(PINCODE);
                                matdaarModel.setMOBILE_NO(MOBILE_NO);
                                matdaarModel.setMOBILE_NO1(MOBILE_NO1);
                                matdaarModel.setWHATSAPP_NO(WHATSAPP_NO);
                                matdaarModel.setAFFLIATED_ABKMS(AFFLIATED_ABKMS);
                                matdarModel_list.add(matdaarModel);

                            }

                        }

                        memberListAdapter = new MemberListAdapter(MembersListActivity.this, matdarModel_list, refreshLayout);
                        recycler_list.setAdapter(memberListAdapter);
                        memberListAdapter.notifyDataSetChanged();


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
            });
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_inspections,menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);

        //    mInprogressInspectionAdapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }*/

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (memberListAdapter != null){
                    memberListAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), NewDashboard.class);
        intent.putExtra("lang_flag", LanguageSelected);
        startActivity(intent);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }

    }

}
