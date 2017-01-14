package com.amsu.healthy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.ClubCampaign;
import com.amsu.healthy.bean.ClubGroup;
import com.amsu.healthy.view.CircleImageView;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

import static com.amsu.healthy.R.id.tv_activity_name;
import static com.amsu.healthy.R.id.tv_activity_type;

/**
 * Created by HP on 2017/1/12.
 */

public class ClubGroupAdapter extends BaseAdapter {
    Context context;
    List<ClubGroup> clubList;
    BitmapUtils bitmapUtils;

    public ClubGroupAdapter(Context context, List<ClubGroup> clubList) {
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
        ClubGroup clubGroup = clubList.get(position);

        View inflate = View.inflate(context, R.layout.list_clubs_group_item, null);
        CircleImageView iv_group_image = (CircleImageView) inflate.findViewById(R.id.iv_group_image);
        TextView tv_group_number = (TextView) inflate.findViewById(R.id.tv_group_number);
        TextView tv_group_name = (TextView) inflate.findViewById(R.id.tv_group_name);
        final RelativeLayout rl_group_nojion = (RelativeLayout) inflate.findViewById(R.id.rl_group_nojion);
        final TextView tv_group_jioned = (TextView) inflate.findViewById(R.id.tv_group_jioned);

        bitmapUtils.display(iv_group_image,clubGroup.getSmiallImageUrl());
        tv_group_name.setText(clubGroup.getName());
        tv_group_number.setText(clubGroup.getNumber());

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
    public void addClubGroup(ClubGroup clubGroup) {
        clubList.add(clubGroup);
    }

    public List<ClubGroup> getClubGroupList() {
        return clubList;
    }
}
