package com.amsu.healthy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.Club;
import com.amsu.healthy.bean.ClubGroup;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.RoundRectImageView;
import com.baidu.mapapi.map.Text;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by HP on 2017/1/12.
 */

public class AllClubsAdapter extends BaseAdapter {
    Context context;
    List<Club> clubList;
    BitmapUtils bitmapUtils;

    public AllClubsAdapter(Context context, List<Club> clubList) {
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
        Club club = clubList.get(position);

        View inflate = View.inflate(context, R.layout.list_clubs_item, null);
        RoundRectImageView iv_clubs_image = (RoundRectImageView) inflate.findViewById(R.id.iv_clubs_image);
        TextView tv_clubs_name = (TextView) inflate.findViewById(R.id.tv_clubs_name);
        TextView tv_clubs_number = (TextView) inflate.findViewById(R.id.tv_clubs_number);
        TextView tv_clubs_type = (TextView) inflate.findViewById(R.id.tv_clubs_type);

        if (!MyUtil.isEmpty(club.getSimallImageUrl())){
            bitmapUtils.display(iv_clubs_image,club.getSimallImageUrl());
        }

        tv_clubs_name.setText(club.getName());
        tv_clubs_number.setText(club.getNumber());
        tv_clubs_type.setText(club.getType());



        return inflate;
    }


    //往clubList里添加数据
    public void addClubGroup(Club clubGroup) {
        clubList.add(clubGroup);
    }

    public List<Club> getClubGroupList() {
        return clubList;
    }
}
