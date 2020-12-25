package com.ascend.www.abkms.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ascend.www.abkms.model.FamousPersonListModel;
import com.ascend.www.abkms.R;

import java.util.ArrayList;

public class FamousPersonListAdapter extends RecyclerView.Adapter<FamousPersonListAdapter.FamousPersonViewHolder> {

    Context mCtx;
    private ArrayList<FamousPersonListModel> famousPerArrayList;
    private ArrayList<FamousPersonListModel> famousPersonFilteredList;
    SwipeRefreshLayout refreshLayout;
    int lastPosition = -1;

    public  FamousPersonListAdapter(Context mCtx, ArrayList<FamousPersonListModel> famousPerArrayList, SwipeRefreshLayout refreshLayout) {
        this.mCtx = mCtx;
        this.famousPerArrayList = famousPerArrayList;
        this.famousPersonFilteredList = famousPerArrayList;
        this.refreshLayout = refreshLayout;
    }


    @Override
    public FamousPersonListAdapter.FamousPersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_famous_person_list, viewGroup, false);
        return new FamousPersonListAdapter.FamousPersonViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final FamousPersonListAdapter.FamousPersonViewHolder viewHolder, final int position) {


        FamousPersonListModel dataObject = famousPersonFilteredList.get(position);

        //viewHolder.tv_member_id.setText(famousPersonFilteredList.get(position).getId());

        viewHolder.tv_id.setText(famousPersonFilteredList.get(position).getID().toUpperCase());
        viewHolder.tv_city.setText(famousPersonFilteredList.get(position).getCITY().toUpperCase());

        viewHolder.tv_president_name.setText(famousPersonFilteredList.get(position).getPRESIDENT_NAME());
        viewHolder.tv_president_contact_no.setText("Contact No.: " +famousPersonFilteredList.get(position).getPRESIDENT_CONTACT());

        viewHolder.tv_secretary_name.setText(famousPersonFilteredList.get(position).getSECRETARY_NAME());
        viewHolder.tv_secretary_contact_no.setText("Contact No.: " + famousPersonFilteredList.get(position).getSECRETARY_CONTACT());


        viewHolder.tv_treasurer_name.setText(famousPersonFilteredList.get(position).getTRESURAR_NAME());
        viewHolder.tv_treasurer_contact_no.setText("Contact No.: " + famousPersonFilteredList.get(position).getTRESURAR_CONTACT());


        Animation animation = AnimationUtils.loadAnimation(mCtx, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        viewHolder.itemView.startAnimation(animation);
        lastPosition = position;

       /* viewHolder.recycleritem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(mCtx, MembersListActivity.class);
                i.putExtra("filter_by", viewHolder.tv_actual_name.getText().toString());
                mCtx.startActivity(i);

            }
        });*/
    }

    @Override
    public void onViewDetachedFromWindow(FamousPersonListAdapter.FamousPersonViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    famousPersonFilteredList = famousPerArrayList;
                    //mFilteredList = mArrayList;
                } else {

                    ArrayList<FamousPersonListModel> filteredList = new ArrayList<>();

                    for (FamousPersonListModel inprogressInspectionmod : famousPerArrayList) {

                        if (inprogressInspectionmod.getCITY().toLowerCase().contains(charString)) {

                            filteredList.add(inprogressInspectionmod);
                        }
                    }

                    famousPersonFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = famousPersonFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                famousPersonFilteredList = (ArrayList<FamousPersonListModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return famousPersonFilteredList.size();
    }


    public class FamousPersonViewHolder extends RecyclerView.ViewHolder {

        //customer vehicle info
        TextView tv_id, tv_city,tv_president_name, tv_president_contact_no,tv_secretary_name,tv_secretary_contact_no,tv_treasurer_name,tv_treasurer_contact_no;
        RelativeLayout recycleritem;
        private ObjectAnimator anim;

        public FamousPersonViewHolder(View itemView) {
            super(itemView);
            recycleritem = (RelativeLayout) itemView.findViewById(R.id.recycleritem);
            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
            tv_city = (TextView) itemView.findViewById(R.id.tv_city);
            tv_president_name = (TextView) itemView.findViewById(R.id.tv_president_name);
            tv_president_contact_no = (TextView) itemView.findViewById(R.id.tv_president_contact_no);
            tv_secretary_name = (TextView) itemView.findViewById(R.id.tv_secretary_name);
            tv_secretary_contact_no= (TextView) itemView.findViewById(R.id.tv_secretary_contact_no);
            tv_treasurer_name= (TextView) itemView.findViewById(R.id.tv_treasurer_name);
            tv_treasurer_contact_no= (TextView) itemView.findViewById(R.id.tv_treasurer_contact_no);

        }



    }


}
