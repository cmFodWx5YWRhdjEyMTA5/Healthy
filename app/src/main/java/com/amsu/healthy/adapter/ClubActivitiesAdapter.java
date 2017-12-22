package com.amsu.healthy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.ClubCampaign;
import com.amsu.healthy.view.CircleImageView;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by HP on 2017/1/12.
 */

public class ClubActivitiesAdapter extends BaseAdapter {
    Context context;
    List<ClubCampaign> clubList;
    BitmapUtils bitmapUtils;

    public ClubActivitiesAdapter(Context context, List<ClubCampaign> clubList) {
        this.context = context;
        this.clubList = clubList;
        bitmapUtils = new BitmapUtils(context);
    }

    @Override
    public int getCount() {
        return clubList.size();
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
        ClubCampaign clubActivity = clubList.get(position);

        View inflate = View.inflate(context, R.layout.list_clubs_activty_item, null);
        CircleImageView iv_activity_image = (CircleImageView) inflate.findViewById(R.id.iv_activity_image);
        TextView tv_activity_name = (TextView) inflate.findViewById(R.id.tv_activity_name);
        TextView tv_activity_type = (TextView) inflate.findViewById(R.id.tv_activity_type);
        TextView tv_activity_date = (TextView) inflate.findViewById(R.id.tv_activity_date);
        TextView tv_activity_time = (TextView) inflate.findViewById(R.id.tv_activity_time);
        TextView tv_activity_jion = (TextView) inflate.findViewById(R.id.tv_activity_jion);

        bitmapUtils.display(iv_activity_image,clubActivity.getSmiallImageUrl());
        tv_activity_name.setText(clubActivity.getName());
        tv_activity_type.setText(clubActivity.getType());
        tv_activity_date.setText(clubActivity.getDate());
        tv_activity_time.setText(clubActivity.getTime());
        tv_activity_jion.setText(clubActivity.getJionNumber()+"/"+clubActivity.getAllNumber());
        return inflate;
    }

    //往clubList里添加数据
    public void addClubCampaign(ClubCampaign clubCampaign) {
        clubList.add(clubCampaign);
    }

    public List<ClubCampaign> getClubList() {
        return clubList;
    }
}
