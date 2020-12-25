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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ascend.www.abkms.activities.MembersListActivity;
import com.ascend.www.abkms.R;
import com.ascend.www.abkms.model.YadiListModel;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;

import java.util.ArrayList;

public class YaadiListCountAdapter extends RecyclerView.Adapter<YaadiListCountAdapter.YaadiViewHolder> {

    Context mCtx;
    private ArrayList<YadiListModel> yaadiArrayList;
    private ArrayList<YadiListModel> yaadiFilteredList;
    SwipeRefreshLayout refreshLayout;
    int lastPosition = -1;

    public  YaadiListCountAdapter(Context mCtx, ArrayList<YadiListModel> yaadiArrayList, SwipeRefreshLayout refreshLayout) {
        this.mCtx = mCtx;
        this.yaadiArrayList = yaadiArrayList;
        this.yaadiFilteredList = yaadiArrayList;
        this.refreshLayout = refreshLayout;
    }


    @Override
    public YaadiListCountAdapter.YaadiViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_yadi_list_count, viewGroup, false);
        return new YaadiListCountAdapter.YaadiViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final YaadiListCountAdapter.YaadiViewHolder viewHolder, final int position) {


        YadiListModel dataObject = yaadiFilteredList.get(position);

        //viewHolder.tv_member_id.setText(yaadiFilteredList.get(position).getId());

        viewHolder.tv_name.setText(yaadiFilteredList.get(position).getList_name().toUpperCase());
        viewHolder.tv_actual_name.setText(yaadiFilteredList.get(position).getActual_name().toUpperCase());
        viewHolder.tv_count.setText(mCtx.getResources().getString(R.string.total)+ ": " +yaadiFilteredList.get(position).getList_count());
        viewHolder.tv_search_filter_by.setText(yaadiFilteredList.get(position).getFilter_by());
        Animation animation = AnimationUtils.loadAnimation(mCtx, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        viewHolder.itemView.startAnimation(animation);
        lastPosition = position;

        viewHolder.recycleritem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UtilitySharedPreferences.setPrefs(mCtx,"yadi_filter_by",viewHolder.tv_search_filter_by.getText().toString());
                Intent i = new Intent(mCtx, MembersListActivity.class);
                i.putExtra("filter_by", viewHolder.tv_actual_name.getText().toString());
                mCtx.startActivity(i);

            }
        });
    }

    @Override
    public void onViewDetachedFromWindow(YaadiListCountAdapter.YaadiViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    yaadiFilteredList = yaadiArrayList;
                    //mFilteredList = mArrayList;
                } else {

                    ArrayList<YadiListModel> filteredList = new ArrayList<>();

                    for (YadiListModel inprogressInspectionmod : yaadiArrayList) {

                        if (inprogressInspectionmod.getActual_name().toLowerCase().contains(charString)) {

                            filteredList.add(inprogressInspectionmod);
                        }
                    }

                    yaadiFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = yaadiFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                yaadiFilteredList = (ArrayList<YadiListModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return yaadiFilteredList.size();
    }


    public class YaadiViewHolder extends RecyclerView.ViewHolder {

        //customer vehicle info
        TextView tv_id, tv_name,tv_actual_name, tv_count,tv_search_filter_by;
        RelativeLayout recycleritem;
        private ObjectAnimator anim;

        public YaadiViewHolder(View itemView) {
            super(itemView);
            recycleritem = (RelativeLayout) itemView.findViewById(R.id.recycleritem);
            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_actual_name = (TextView) itemView.findViewById(R.id.tv_actual_name);
            tv_count = (TextView) itemView.findViewById(R.id.tv_count);
            tv_search_filter_by = (TextView) itemView.findViewById(R.id.tv_search_filter_by);

        }



    }
}