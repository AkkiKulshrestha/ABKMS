package com.ascend.www.abkms.adapters;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ascend.www.abkms.activities.MatrimonyCorner;
import com.ascend.www.abkms.R;
import com.ascend.www.abkms.model.MatrimonyProfileListModel;
import com.ascend.www.abkms.utils.CommonMethods;
import com.ascend.www.abkms.utils.ConnectionDetector;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ascend.www.abkms.utils.CommonMethods.ucFirst;
import static com.ascend.www.abkms.webservices.RestClient.ROOT_URL;

public class MatrimonyProfileAdapter extends RecyclerView.Adapter<MatrimonyProfileAdapter.MatrimonyViewHolder> {

    Context mCtx;
    private ArrayList<MatrimonyProfileListModel> matrimonyArrayList;
    private ArrayList<MatrimonyProfileListModel> matrimonyFilteredList;
    SwipeRefreshLayout refreshLayout;
    int lastPosition = -1;
    String StrMemberId,IS_ADMIN;

    public MatrimonyProfileAdapter(Context mCtx, ArrayList<MatrimonyProfileListModel> matrimonyArrayList, SwipeRefreshLayout refreshLayout) {
        this.mCtx = mCtx;
        this.matrimonyArrayList = matrimonyArrayList;
        this.matrimonyFilteredList = matrimonyArrayList;
        this.refreshLayout = refreshLayout;
    }


    @NonNull
    @Override
    public MatrimonyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.matrimony_profile_layout, viewGroup, false);
        return new MatrimonyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MatrimonyViewHolder viewHolder, final int position) {

        StrMemberId = UtilitySharedPreferences.getPrefs(mCtx, "MemberId");
        IS_ADMIN = UtilitySharedPreferences.getPrefs(mCtx, "IsAdmin");
        MatrimonyProfileListModel dataObject = matrimonyFilteredList.get(position);

        viewHolder.tv_profile_id.setText(matrimonyFilteredList.get(position).getID());
        viewHolder.tv_profile_name.setText(matrimonyFilteredList.get(position).getProfile_name().toUpperCase());
        viewHolder.tv_FatherName.setText(ucFirst(matrimonyFilteredList.get(position).getFather_name()));
        viewHolder.tv_MotherName.setText(ucFirst(matrimonyFilteredList.get(position).getMother_name()));
        viewHolder.tv_FamilyOrigin.setText(ucFirst(matrimonyFilteredList.get(position).getFamily_origin_place()));
        viewHolder.tv_ResidingAt.setText(ucFirst(matrimonyFilteredList.get(position).getResiding_city()));
        viewHolder.tv_WorkingStatus.setText(ucFirst((matrimonyFilteredList.get(position).getWorking_status())));
        viewHolder.tv_WorkingAs.setText(ucFirst(matrimonyFilteredList.get(position).getWorking_as()));
        viewHolder.tv_AnnualIncome.setText(matrimonyFilteredList.get(position).getAnnunal_income());
        viewHolder.tv_ContactNo.setText(matrimonyFilteredList.get(position).getContact_no());
        viewHolder.tv_WhatsAppNo.setText(matrimonyFilteredList.get(position).getWhatsapp_no());
        viewHolder.tv_gender.setText(ucFirst(matrimonyFilteredList.get(position).getGender()));
        viewHolder.tv_dateOfBirth.setText(matrimonyFilteredList.get(position).getDate_of_birth() + " ( "+matrimonyFilteredList.get(position).getAge() + " Yrs ) ");
        viewHolder.tv_Gotra.setText(ucFirst(matrimonyFilteredList.get(position).getGotra()));
        viewHolder.tv_created_by.setText(matrimonyFilteredList.get(position).getCreated_by());

        if(viewHolder.tv_gender.getText().toString().equalsIgnoreCase("Male")){
            Glide.with((Activity)mCtx)
                    .load(mCtx.getResources().getDrawable(R.drawable.icons_male_user))
                    .into(viewHolder.iv_profile_img);
        }else  if(viewHolder.tv_gender.getText().toString().equalsIgnoreCase("Female")){
            Glide.with((Activity)mCtx)
                    .load(mCtx.getResources().getDrawable(R.drawable.icons_female))
                    .into(viewHolder.iv_profile_img);
        }



        viewHolder.LinearWhatapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent shareIntent =  new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,mCtx.getResources().getString(R.string.matrimony_title));
                String shareMessage=mCtx.getResources().getString(R.string.share_header1) + "\n\n"+mCtx.getResources().getString(R.string.share_matri_header2)+"\n"+
                        mCtx.getResources().getString(R.string.share_header3)
                        +"\n"+mCtx.getResources().getString(R.string.member_name)+viewHolder.tv_profile_name.getText().toString().toUpperCase()
                        +"\n"+mCtx.getResources().getString(R.string.only_father_name)+" - "+viewHolder.tv_FatherName.getText().toString().toUpperCase()
                        +"\n"+mCtx.getResources().getString(R.string.mother_name)+" - "+viewHolder.tv_MotherName.getText().toString()
                        +"\n"+mCtx.getResources().getString(R.string.mobileno)+" - "+viewHolder.tv_ContactNo.getText().toString() /*+ " / "+ MOBILE_NO1*/
                        +"\n"+mCtx.getResources().getString(R.string.address)+" - "+viewHolder.tv_ResidingAt.getText().toString()
                        +"\n----------------------------\n\n\n"+mCtx.getResources().getString(R.string.share_last);
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,shareMessage);
                mCtx.startActivity(Intent.createChooser(shareIntent,"Sharing via"));

            }
        });

        viewHolder.LinearCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel: "+viewHolder.tv_ContactNo.getText().toString()));
                mCtx.startActivity(callIntent);

            }
        });

        if (IS_ADMIN != null && !IS_ADMIN.equalsIgnoreCase("") && !IS_ADMIN.equalsIgnoreCase("null")) {
            if (IS_ADMIN.equalsIgnoreCase("1")) {
                viewHolder.iv_delete.setVisibility(View.VISIBLE);
            } else {
                if (viewHolder.tv_created_by.getText().toString() != null && !viewHolder.tv_created_by.getText().toString().equalsIgnoreCase("")) {
                    if (viewHolder.tv_created_by.getText().toString().equalsIgnoreCase(StrMemberId)) {
                        viewHolder.iv_delete.setVisibility(View.VISIBLE);
                    }
                } else {
                    viewHolder.iv_delete.setVisibility(View.GONE);
                }

            }

        }

        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setTitle("Delete !!!");
                builder.setMessage("Are you sure you want to delete this entry ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteProfileDetails(viewHolder.tv_profile_id.getText().toString());
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

        Animation animation = AnimationUtils.loadAnimation(mCtx, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        viewHolder.itemView.startAnimation(animation);
        lastPosition = position;

    }

    private void DeleteProfileDetails (String matrimony_profileId){

        Log.d("DeleteID", "" + matrimony_profileId);
        String Uiid_id = UUID.randomUUID().toString();

        String API_DeleteDonationInfo = ROOT_URL + "delete_matrimony_profile.php?_" + Uiid_id;
        try {

            ConnectionDetector cd = new ConnectionDetector(mCtx);
            boolean isInternetPresent = cd.isConnectingToInternet();


            if (isInternetPresent) {
                Log.d("URL", API_DeleteDonationInfo);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, API_DeleteDonationInfo,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean status = jsonObject.getBoolean("status");
                                    if (status) {
                                        Intent intent = new Intent(mCtx, MatrimonyCorner.class);
                                        mCtx.startActivity(intent);
                                        ((Activity) mCtx).overridePendingTransition(R.animator.move_left, R.animator.move_right);
                                        ((Activity) mCtx).finish();
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
                        }) {

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        //params.put("Pan", StrClientPan);
                        params.put("matrimony_profileId", matrimony_profileId);
                        Log.d("ParrasOtpdata", params.toString());

                        return params;
                    }


                };

                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(mCtx);
                requestQueue.add(stringRequest);


            } else {
                CommonMethods.DisplayToast(mCtx, "Please check your internet connection");
            }
        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    @Override
    public void onViewDetachedFromWindow(@NonNull MatrimonyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    matrimonyFilteredList = matrimonyArrayList;
                    //mFilteredList = mArrayList;
                } else {

                    ArrayList<MatrimonyProfileListModel> filteredList = new ArrayList<>();

                    for (MatrimonyProfileListModel inprogressInspectionmod : matrimonyArrayList) {

                        if (inprogressInspectionmod.getGender().toLowerCase().contains(charString) || inprogressInspectionmod.getResiding_city().toLowerCase().contains(charString) || inprogressInspectionmod.getWorking_as().toLowerCase().contains(charString) || inprogressInspectionmod.getAge().toLowerCase().contains(charString) || inprogressInspectionmod.getProfile_name().toLowerCase().contains(charString)) {

                            filteredList.add(inprogressInspectionmod);
                        }
                    }

                    matrimonyFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = matrimonyFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                matrimonyFilteredList = (ArrayList<MatrimonyProfileListModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return matrimonyFilteredList.size();
    }


    class MatrimonyViewHolder extends RecyclerView.ViewHolder {

        //customer vehicle info
        TextView tv_profile_id, tv_profile_name, tv_FatherName,tv_MotherName, tv_FamilyOrigin,tv_ResidingAt,tv_WorkingStatus,tv_WorkingAs,tv_AnnualIncome,
                tv_ContactNo,tv_WhatsAppNo,tv_gender,tv_dateOfBirth,tv_Gotra,tv_created_by;
        ImageView iv_delete,iv_profile_img;
        LinearLayout LinearWhatapp,LinearCall;
        private ObjectAnimator anim;

        MatrimonyViewHolder(View itemView) {
            super(itemView);
             tv_profile_id = (TextView) itemView.findViewById(R.id.tv_profile_id);
             tv_profile_name = (TextView) itemView.findViewById(R.id.tv_profile_name);
             tv_FatherName = (TextView) itemView.findViewById(R.id.tv_FatherName);
             tv_MotherName = (TextView) itemView.findViewById(R.id.tv_MotherName);
             tv_FamilyOrigin = (TextView) itemView.findViewById(R.id.tv_FamilyOrigin);
             tv_ResidingAt = (TextView) itemView.findViewById(R.id.tv_ResidingAt);
             tv_WorkingStatus = (TextView) itemView.findViewById(R.id.tv_WorkingStatus);
             tv_WorkingAs = (TextView) itemView.findViewById(R.id.tv_WorkingAs);
             tv_AnnualIncome = (TextView) itemView.findViewById(R.id.tv_AnnualIncome);
             tv_ContactNo = (TextView) itemView.findViewById(R.id.tv_ContactNo);
             tv_WhatsAppNo = (TextView) itemView.findViewById(R.id.tv_WhatsAppNo);
             tv_gender = (TextView) itemView.findViewById(R.id.tv_gender);
             tv_dateOfBirth = (TextView) itemView.findViewById(R.id.tv_dateOfBirth);
             tv_Gotra = (TextView) itemView.findViewById(R.id.tv_Gotra);
             tv_created_by= (TextView) itemView.findViewById(R.id.tv_created_by);
             iv_profile_img = (ImageView) itemView.findViewById(R.id.iv_profile_img);


              LinearWhatapp = (LinearLayout) itemView.findViewById(R.id.LinearWhatapp);
              LinearCall = (LinearLayout) itemView.findViewById(R.id.LinearCall);

            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);



        }



    }
}