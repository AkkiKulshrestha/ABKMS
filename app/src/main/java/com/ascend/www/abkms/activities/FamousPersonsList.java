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
import com.ascend.www.abkms.adapters.FamousPersonListAdapter;
import com.ascend.www.abkms.model.FamousPersonListModel;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.webservices.RestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class FamousPersonsList extends AppCompatActivity {

    ImageView back_btn_toolbar;
    String LanguageSelected="eng";
    SearchView editTextSearch;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recycler_list_famous_person_list;
    RecyclerView.LayoutManager layoutManager;
    private FamousPersonListAdapter famousListAdapter;
    ArrayList<FamousPersonListModel> famous_person_list = new ArrayList<>();
    FamousPersonListModel famousModel;
    ProgressDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_famous_person_list);



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
        til_text.setText(getResources().getString(R.string.famous_kulshrestha_hin));



        editTextSearch = (SearchView) findViewById(R.id.editTextSearch);

        search(editTextSearch);

        recycler_list_famous_person_list = (RecyclerView) findViewById(R.id.recycler_list_famous_person_list);
        recycler_list_famous_person_list.setHasFixedSize(true);
        refreshLayout= (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        layoutManager = new LinearLayoutManager(this);
        recycler_list_famous_person_list.setLayoutManager(layoutManager);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        famous_person_list.clear();
                        showData();
                        famousListAdapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                },1000);
                recycler_list_famous_person_list.setItemAnimator(new DefaultItemAnimator());
            }
        });

        myDialog = new ProgressDialog(FamousPersonsList.this);
        myDialog.setMessage(getResources().getString(R.string.please_wait));
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(true);
        showData();
    }

    private void showData() {
        myDialog.show();
        if(famous_person_list!=null && famous_person_list.size()>0){
            famous_person_list = new ArrayList<>();
            famousListAdapter = new FamousPersonListAdapter(FamousPersonsList.this, famous_person_list, refreshLayout);
            recycler_list_famous_person_list.setAdapter(famousListAdapter);
            famousListAdapter.notifyDataSetChanged();

        }
        String Uiid_id = UUID.randomUUID().toString();


        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = RestClient.ROOT_URL + "famous_person_list.php?_" + Uiid_id;
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
                                String city_name = customer_inspect_obj.getString("city_name");

                                String president_name = customer_inspect_obj.getString("president_name");
                                String president_contact_no = customer_inspect_obj.getString("president_contact_no");

                                String secretary_name = customer_inspect_obj.getString("secretary_name");
                                String secretary_contact_no = customer_inspect_obj.getString("secretary_contact_no");

                                String tresurar_name = customer_inspect_obj.getString("tresurar_name");
                                String tresurar_contact_no = customer_inspect_obj.getString("tresurar_contact_no");

                                famousModel = new FamousPersonListModel();
                                famousModel.setID(id);
                                famousModel.setCITY(city_name);
                                famousModel.setPRESIDENT_NAME(president_name);
                                famousModel.setPRESIDENT_CONTACT(president_contact_no);
                                famousModel.setSECRETARY_NAME(secretary_name);
                                famousModel.setSECRETARY_CONTACT(secretary_contact_no);
                                famousModel.setTRESURAR_NAME(tresurar_name);
                                famousModel.setTRESURAR_CONTACT(tresurar_contact_no);

                                famous_person_list.add(famousModel);
                            }

                        }

                        famousListAdapter = new FamousPersonListAdapter(FamousPersonsList.this, famous_person_list, refreshLayout);
                        recycler_list_famous_person_list.setAdapter(famousListAdapter);
                        famousListAdapter.notifyDataSetChanged();


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
                if (famousListAdapter != null){
                    famousListAdapter.getFilter().filter(newText);
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
