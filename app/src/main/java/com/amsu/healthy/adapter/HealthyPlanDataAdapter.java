package com.amsu.healthy.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.HealthyPlan;

import java.util.List;

/**
 * Created by HP on 2016/12/19.
 */
public class HealthyPlanDataAdapter extends BaseAdapter{
    private List<HealthyPlan> healthyPlanList;
    private Context context;

    public HealthyPlanDataAdapter(Context context, List<HealthyPlan> healthyPlanList) {
        this.context = context;
        this.healthyPlanList = healthyPlanList;
    }

    @Override
    public int getCount() {
        Log.i("HealthyPlanData","getCount:"+healthyPlanList.size());
        return healthyPlanList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HealthyPlan healthyPlan = healthyPlanList.get(position);
        View inflate = View.inflate(context, R.layout.item_list_healthyplan, null);
        TextView tv_item_titlt = (TextView) inflate.findViewById(R.id.tv_item_titlt);
        TextView tv_item_date = (TextView) inflate.findViewById(R.id.tv_item_date);
        TextView tv_item_content = (TextView) inflate.findViewById(R.id.tv_item_content);

        tv_item_titlt.setText(healthyPlan.getTitle());
        tv_item_date.setText(healthyPlan.getDate());
        //tv_item_content.setText(healthyPlan.getContent());
        return inflate;
    }
}
