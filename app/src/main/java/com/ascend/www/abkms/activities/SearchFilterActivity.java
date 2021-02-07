package com.ascend.www.abkms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.ascend.www.abkms.R;
import com.ascend.www.abkms.adapters.GroupListCountAdapter;
import com.ascend.www.abkms.model.YadiListModel;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;
import com.ascend.www.abkms.webservices.RestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class SearchFilterActivity extends AppCompatActivity {

    String ListFilterBy = "",Search_filter_by="";
    ImageView back_btn_toolbar;
    String LanguageSelected="eng";
    SearchView editTextSearch;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recycler_list;
    RecyclerView.LayoutManager layoutManager;
    private GroupListCountAdapter groupListCountAdapter;
    ArrayList<YadiListModel> yadiModel_list = new ArrayList<>();
    YadiListModel yadiListModel;
    ProgressDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_count_search);

        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("search_filter_by")!= null)
        {
            Search_filter_by = bundle.getString("search_filter_by");
        }
        Log.d("Search_filter_by:",""+Search_filter_by);


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
        if(Search_filter_by.equalsIgnoreCase("state")){
            til_text.setText(getResources().getString(R.string.by_state));
        }else if(Search_filter_by.equalsIgnoreCase("city")){
            til_text.setText(getResources().getString(R.string.by_city));
        }else if(Search_filter_by.equalsIgnoreCase("affiliated_sabha")){
            til_text.setText(getResources().getString(R.string.by_affiliated_sabha));
        }
        LanguageSelected = UtilitySharedPreferences.getPrefs(getApplicationContext(),"LanguageSelected");
        UtilitySharedPreferences.setPrefs(getApplicationContext(),"SearchFilterBy",Search_filter_by);


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
                        yadiModel_list.clear();
                        showData();
                        groupListCountAdapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                },1000);
                recycler_list.setItemAnimator(new DefaultItemAnimator());
            }
        });


        myDialog = new ProgressDialog(SearchFilterActivity.this);
        myDialog.setMessage(getResources().getString(R.string.please_wait));
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(true);

        showData();

    }


    private void showData() {
        myDialog.show();
        if(yadiModel_list!=null && yadiModel_list.size()>0){
            yadiModel_list = new ArrayList<>();
            groupListCountAdapter = new GroupListCountAdapter(SearchFilterActivity.this, yadiModel_list, refreshLayout);
            recycler_list.setAdapter(groupListCountAdapter);
            groupListCountAdapter.notifyDataSetChanged();

        }
        String Uiid_id = UUID.randomUUID().toString();


        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = RestClient.ROOT_URL + "search_filter_list_count.php?_" + Uiid_id + "&lang=" + LanguageSelected + "&search_filter="+Search_filter_by;
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
                                String name = customer_inspect_obj.getString("name");
                                String value_name = customer_inspect_obj.getString("value_name");
                                String count = customer_inspect_obj.getString("cnt");
                                name = name.trim();
                                value_name = value_name.trim();
                                //name = name.replace(" ","");
                                if (name != null && !name.equalsIgnoreCase("") && !name.equalsIgnoreCase("null") && !name.equalsIgnoreCase("-")) {

                                    yadiListModel = new YadiListModel();
                                    yadiListModel.setList_name(name);
                                    yadiListModel.setActual_name(value_name);
                                    yadiListModel.setList_count(count);
                                    yadiListModel.setFilter_by(Search_filter_by);

                                    yadiModel_list.add(yadiListModel);
                                }
                            }

                        }

                        groupListCountAdapter = new GroupListCountAdapter(SearchFilterActivity.this, yadiModel_list, refreshLayout);
                        recycler_list.setAdapter(groupListCountAdapter);
                        groupListCountAdapter.notifyDataSetChanged();


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



    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (groupListCountAdapter != null){
                    groupListCountAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }

}
