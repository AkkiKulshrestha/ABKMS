package com.ascend.www.abkms.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ascend.www.abkms.activities.MemberDetailsActivity;
import com.ascend.www.abkms.R;
import com.ascend.www.abkms.model.MatdarListModel;

import java.util.ArrayList;

public class MatdarListWithSwipeViewAdapter extends RecyclerView.Adapter<MatdarListWithSwipeViewAdapter.MatdaarViewHolder> {

    Context mCtx;
    private ArrayList<MatdarListModel> matdaarArrayList;
    private ArrayList<MatdarListModel> matdaarFilteredList;
    SwipeRefreshLayout refreshLayout;
    int lastPosition = -1;

    public MatdarListWithSwipeViewAdapter(Context mCtx, ArrayList<MatdarListModel> matdarArrayList) {
        this.mCtx = mCtx;
        this.matdaarArrayList = matdarArrayList;
        this.matdaarFilteredList = matdarArrayList;
        this.refreshLayout = refreshLayout;
    }


    @Override
    public MatdaarViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_card_users, viewGroup, false);
        return new MatdaarViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MatdaarViewHolder viewHolder, final int position) {


        MatdarListModel dataObject = matdaarFilteredList.get(position);

        viewHolder.tv_member_id.setText(matdaarFilteredList.get(position).getID());

        viewHolder.tv_name.setText(matdaarFilteredList.get(position).getNAME().toUpperCase());
        if(matdaarFilteredList.get(position).getGENDER().equalsIgnoreCase("F")) {
            viewHolder.age_gender.setText(mCtx.getResources().getString(R.string.age)+ ": " +matdaarFilteredList.get(position).getAGE() + " / " + mCtx.getResources().getString(R.string.female));
        }else if(matdaarFilteredList.get(position).getGENDER().equalsIgnoreCase("M")) {
            viewHolder.age_gender.setText(mCtx.getResources().getString(R.string.age)+ ": " +matdaarFilteredList.get(position).getAGE() + " / " + mCtx.getResources().getString(R.string.male));
        }
        viewHolder.tv_father_name.setText(mCtx.getResources().getString(R.string.father_name)+ ": " +matdaarFilteredList.get(position).getFATHER_NAME());
        viewHolder.tv_spouse_name.setText(mCtx.getResources().getString(R.string.spouse_name)+ ": " +matdaarFilteredList.get(position).getSPOUSE_NAME());
        viewHolder.tv_mobile_no.setText(mCtx.getResources().getString(R.string.mobileno)+ ": " +matdaarFilteredList.get(position).getMOBILE_NO() + " / " + matdaarFilteredList.get(position).getMOBILE_NO1());

        viewHolder.tv_address.setText(mCtx.getResources().getString(R.string.address)+ " : " + matdaarFilteredList.get(position).getADDRESS1() + ", " + matdaarFilteredList.get(position).getADDRESS2() +", "+matdaarFilteredList.get(position).getADDRESS3()
                    + ", "+matdaarFilteredList.get(position).getCITY() + ", "+matdaarFilteredList.get(position).getSTATE() + "-"+ matdaarFilteredList.get(position).getPINCODE());

        Animation animation = AnimationUtils.loadAnimation(mCtx, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        viewHolder.itemView.startAnimation(animation);
        lastPosition = position;

        viewHolder.recycleritem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(mCtx, MemberDetailsActivity.class);
                i.putExtra("member_id", viewHolder.tv_member_id.getText().toString());
                mCtx.startActivity(i);

            }
        });
    }

    @Override
    public void onViewDetachedFromWindow(MatdaarViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    matdaarFilteredList = matdaarArrayList;
                    //mFilteredList = mArrayList;
                } else {

                    ArrayList<MatdarListModel> filteredList = new ArrayList<>();

                    for (MatdarListModel inprogressInspectionmod : matdaarArrayList) {

                        if (inprogressInspectionmod.getNAME().toLowerCase().contains(charString) || inprogressInspectionmod.getMOBILE_NO().toLowerCase().contains(charString) || inprogressInspectionmod.getFATHER_NAME().toLowerCase().contains(charString) || inprogressInspectionmod.getSPOUSE_NAME().toLowerCase().contains(charString)) {

                            filteredList.add(inprogressInspectionmod);
                        }
                    }

                    matdaarFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = matdaarFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                matdaarFilteredList = (ArrayList<MatdarListModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return matdaarFilteredList.size();
    }


    public class MatdaarViewHolder extends RecyclerView.ViewHolder {

        //customer vehicle info
        TextView tv_member_id, tv_name, age_gender,tv_father_name, tv_spouse_name,tv_mobile_no,tv_address;
        ImageView vehicle_thumbnail;
        RelativeLayout recycleritem;
        private ObjectAnimator anim;

        public MatdaarViewHolder(View itemView) {
            super(itemView);
            recycleritem = (RelativeLayout) itemView.findViewById(R.id.recycleritem);
            tv_member_id = (TextView) itemView.findViewById(R.id.tv_member_id);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            age_gender = (TextView) itemView.findViewById(R.id.age_gender);
            tv_father_name = (TextView) itemView.findViewById(R.id.tv_father_name);
            tv_spouse_name = (TextView) itemView.findViewById(R.id.tv_spouse_name);
            tv_mobile_no = (TextView) itemView.findViewById(R.id.tv_mobile_no);
            tv_address =  (TextView) itemView.findViewById(R.id.tv_address);


        }



    }
}