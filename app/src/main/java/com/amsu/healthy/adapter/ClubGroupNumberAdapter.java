package com.amsu.healthy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.ClubNumber;
import com.amsu.healthy.view.CircleImageView;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by HP on 2017/1/12.
 */

public class ClubGroupNumberAdapter extends BaseAdapter {
    Context context;
    List<ClubNumber> clubList;
    BitmapUtils bitmapUtils;

    public ClubGroupNumberAdapter(Context context, List<ClubNumber> clubList) {
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
        ClubNumber ClubNumber = clubList.get(position);

        View inflate = View.inflate(context, R.layout.list_clubs_group_number_item, null);
        CircleImageView iv_group_image = (CircleImageView) inflate.findViewById(R.id.iv_group_image);
        ImageView iv_group_sex = (ImageView) inflate.findViewById(R.id.iv_group_sex); //性别图表
        TextView tv_group_city = (TextView) inflate.findViewById(R.id.tv_group_city);
        final RelativeLayout rl_group_nojion = (RelativeLayout) inflate.findViewById(R.id.rl_group_nojion);
        final TextView tv_group_jioned = (TextView) inflate.findViewById(R.id.tv_group_jioned);

        bitmapUtils.display(iv_group_image,ClubNumber.getSmiallImageUrl());


        tv_group_city.setText(ClubNumber.getCity());

        rl_group_nojion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_group_nojion.setVisibility(View.GONE);
                tv_group_jioned.setVisibility(View.VISIBLE);
            }
        });

        return inflate;
    }

    //往clubList里添加数据
    public void addClubGroup(ClubNumber ClubNumber) {
        clubList.add(ClubNumber);
    }

    public List<ClubNumber> getClubNumberList() {
        return clubList;
    }
}
