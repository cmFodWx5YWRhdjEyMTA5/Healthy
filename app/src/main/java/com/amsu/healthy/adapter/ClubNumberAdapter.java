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
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.CircleImageView;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by HP on 2017/1/12.
 */

public class ClubNumberAdapter extends BaseAdapter {
    Context context;
    List<ClubNumber> clubList;
    BitmapUtils bitmapUtils;

    public ClubNumberAdapter(Context context, List<ClubNumber> clubList) {
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
        ClubNumber clubNumber = clubList.get(position);

        View inflate = View.inflate(context, R.layout.list_clubs_number_item, null);
        CircleImageView iv_number_image = (CircleImageView) inflate.findViewById(R.id.iv_number_image);
        ImageView iv_number_sex = (ImageView) inflate.findViewById(R.id.iv_number_sex); //性别图表
        TextView tv_number_name = (TextView) inflate.findViewById(R.id.tv_number_name);
        TextView tv_number_city = (TextView) inflate.findViewById(R.id.tv_number_city);
        TextView tv_number_coachType = (TextView) inflate.findViewById(R.id.tv_number_coachType);
        final RelativeLayout rl_number_nojion = (RelativeLayout) inflate.findViewById(R.id.rl_number_nojion);
        final TextView tv_number_jioned = (TextView) inflate.findViewById(R.id.tv_number_jioned);

        bitmapUtils.display(iv_number_image,clubNumber.getSmiallImageUrl());

        if (!MyUtil.isEmpty(clubNumber.getCoachType())){
            tv_number_coachType.setVisibility(View.VISIBLE);
            tv_number_coachType.setText(clubNumber.getCoachType());
        }

        tv_number_city.setText(clubNumber.getCity());
        tv_number_name.setText(clubNumber.getPickName());

        rl_number_nojion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_number_nojion.setVisibility(View.GONE);
                tv_number_jioned.setVisibility(View.VISIBLE);
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
