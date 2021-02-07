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

import com.ascend.www.abkms.R;
import com.ascend.www.abkms.activities.MembersListActivity;
import com.ascend.www.abkms.model.YadiListModel;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;

import java.util.ArrayList;

public class GroupListCountAdapter extends RecyclerView.Adapter<GroupListCountAdapter.GroupViewHolder> {

    Context mCtx;
    SwipeRefreshLayout refreshLayout;
    int lastPosition = -1;
    private ArrayList<YadiListModel> groupArrayList;
    private ArrayList<YadiListModel> groupFilteredList;

    public GroupListCountAdapter(Context mCtx, ArrayList<YadiListModel> groupArrayList, SwipeRefreshLayout refreshLayout) {
        this.mCtx = mCtx;
        this.groupArrayList = groupArrayList;
        this.groupFilteredList = groupArrayList;
        this.refreshLayout = refreshLayout;
    }


    @Override
    public GroupListCountAdapter.GroupViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_yadi_list_count, viewGroup, false);
        return new GroupViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final GroupListCountAdapter.GroupViewHolder viewHolder, final int position) {


        YadiListModel dataObject = groupFilteredList.get(position);

        //viewHolder.tv_member_id.setText(groupFilteredList.get(position).getId());

        viewHolder.tv_name.setText(groupFilteredList.get(position).getList_name().toUpperCase());
        viewHolder.tv_actual_name.setText(groupFilteredList.get(position).getActual_name().toUpperCase());
        viewHolder.tv_count.setText(mCtx.getResources().getString(R.string.total) + ": " + groupFilteredList.get(position).getList_count());
        viewHolder.tv_search_filter_by.setText(groupFilteredList.get(position).getFilter_by());
        Animation animation = AnimationUtils.loadAnimation(mCtx, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        viewHolder.itemView.startAnimation(animation);
        lastPosition = position;

        viewHolder.recycleritem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UtilitySharedPreferences.setPrefs(mCtx, "yadi_filter_by", viewHolder.tv_search_filter_by.getText().toString());
                Intent i = new Intent(mCtx, MembersListActivity.class);
                i.putExtra("filter_by", viewHolder.tv_actual_name.getText().toString());
                mCtx.startActivity(i);

            }
        });
    }

    @Override
    public void onViewDetachedFromWindow(GroupListCountAdapter.GroupViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    groupFilteredList = groupArrayList;
                    //mFilteredList = mArrayList;
                } else {

                    ArrayList<YadiListModel> filteredList = new ArrayList<>();

                    for (YadiListModel inprogressInspectionmod : groupArrayList) {

                        if (inprogressInspectionmod.getActual_name().toLowerCase().contains(charString)) {

                            filteredList.add(inprogressInspectionmod);
                        }
                    }

                    groupFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = groupFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                groupFilteredList = (ArrayList<YadiListModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return groupFilteredList.size();
    }


    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        //customer vehicle info
        TextView tv_id, tv_name, tv_actual_name, tv_count, tv_search_filter_by;
        RelativeLayout recycleritem;
        private ObjectAnimator anim;

        public GroupViewHolder(View itemView) {
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