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

public class ClubNumberRankAdapter extends BaseAdapter {
    Context context;
    List<ClubNumber> clubList;
    BitmapUtils bitmapUtils;

    public ClubNumberRankAdapter(Context context, List<ClubNumber> clubList) {
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

        View inflate = View.inflate(context, R.layout.list_clubs_numberrank_item, null);
        CircleImageView iv_rank_image = (CircleImageView) inflate.findViewById(R.id.iv_rank_image);
        ImageView iv_rank_sex = (ImageView) inflate.findViewById(R.id.iv_rank_sex); //性别图表
        TextView tv_rank_name = (TextView) inflate.findViewById(R.id.tv_rank_name);
        TextView tv_rank_rank = (TextView) inflate.findViewById(R.id.tv_rank_rank);
        TextView tv_rank_city = (TextView) inflate.findViewById(R.id.tv_rank_city);
        TextView tv_rank_jioned = (TextView) inflate.findViewById(R.id.tv_rank_jioned);


        bitmapUtils.display(iv_rank_image,clubNumber.getSmiallImageUrl());



        tv_rank_city.setText(clubNumber.getCity());
        tv_rank_name.setText(clubNumber.getPickName());
        int i = position + 1;
        int i1 = (10 - position) * 155;
        tv_rank_rank.setText(i + "");
        tv_rank_jioned.setText(i1+"");



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
