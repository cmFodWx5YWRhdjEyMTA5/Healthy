package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.ClubActivitiesAdapter;
import com.amsu.healthy.bean.ClubCampaign;
import com.amsu.healthy.utils.MyUtil;

import java.util.ArrayList;
import java.util.List;

public class ClubCampaignActivity extends BaseActivity {

    private ListView lv_activities_list;
    private List<ClubCampaign> clubActivities;
    private ClubActivitiesAdapter mClubActivitiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_activities);


        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText("俱乐部活动");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        lv_activities_list = (ListView) findViewById(R.id.lv_activities_list);
        View inflate = View.inflate(this, R.layout.view_bottom_clublist, null);
        TextView tv_bottom_name = (TextView) inflate.findViewById(R.id.tv_bottom_name);
        tv_bottom_name.setText("发起活动");

        lv_activities_list.addFooterView(inflate);

    }

    private void initData() {
        clubActivities = new ArrayList<>();
        clubActivities.add(new ClubCampaign("厦门国际马拉松赛","2016.1.1","10:00-14:00","铁人三项","20","500","这是一个比赛，这是一个比赛，这是一个比赛","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubActivities.add(new ClubCampaign("厦门国际马拉松赛","2016.1.1","10:00-14:00","铁人三项","20","500","这是一个比赛，这是一个比赛，这是一个比赛","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubActivities.add(new ClubCampaign("厦门国际马拉松赛","2016.1.1","10:00-14:00","铁人三项","20","500","这是一个比赛，这是一个比赛，这是一个比赛","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubActivities.add(new ClubCampaign("厦门国际马拉松赛","2016.1.1","10:00-14:00","铁人三项","20","500","这是一个比赛，这是一个比赛，这是一个比赛","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubActivities.add(new ClubCampaign("厦门国际马拉松赛","2016.1.1","10:00-14:00","铁人三项","20","500","这是一个比赛，这是一个比赛，这是一个比赛","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));


        mClubActivitiesAdapter = new ClubActivitiesAdapter(this,clubActivities);
        lv_activities_list.setAdapter(mClubActivitiesAdapter);

        lv_activities_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==lv_activities_list.getCount()-1){
                    startActivityForResult(new Intent(ClubCampaignActivity.this,SetupCampaignActivity.class),104);

                }else {
                    Intent intent = new Intent(ClubCampaignActivity.this,ClubDetialActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("clubActivity",mClubActivitiesAdapter.getClubList().get(position));
                    intent.putExtra("bundle",bundle);
                    startActivityForResult(intent,103);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==104 && resultCode==RESULT_OK){
            //创建活动成果，更新活动列表
            Bundle bundle = data.getBundleExtra("bundle");
            ClubCampaign clubCampaign = bundle.getParcelable("clubCampaign");
            Log.i("clubCampaign",clubCampaign.toString());
            mClubActivitiesAdapter.addClubCampaign(clubCampaign);
            mClubActivitiesAdapter.notifyDataSetChanged();

        }
    }
}
