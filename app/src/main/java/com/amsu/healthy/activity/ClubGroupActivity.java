package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.ClubGroupAdapter;
import com.amsu.healthy.bean.ClubCampaign;
import com.amsu.healthy.bean.ClubGroup;

import java.util.ArrayList;
import java.util.List;

public class ClubGroupActivity extends BaseActivity {
    private ListView lv_activities_list;
    private List<ClubGroup> clubGroups;
    private ClubGroupAdapter clubGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_group);

        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText("俱乐部小组");
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
        tv_bottom_name.setText("创建小组");

        lv_activities_list.addFooterView(inflate);
        
        

    }

    private void initData() {
        clubGroups = new ArrayList<>();
        clubGroups.add(new ClubGroup("欢快俱乐部","类型","描述","12","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubGroups.add(new ClubGroup("欢快俱乐部","类型","描述","12","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubGroups.add(new ClubGroup("欢快俱乐部","类型","描述","12","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubGroups.add(new ClubGroup("欢快俱乐部","类型","描述","12","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubGroups.add(new ClubGroup("欢快俱乐部","类型","描述","12","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));


        clubGroupAdapter = new ClubGroupAdapter(this,clubGroups);
        lv_activities_list.setAdapter(clubGroupAdapter);

        lv_activities_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==lv_activities_list.getCount()-1){
                    startActivityForResult(new Intent(ClubGroupActivity.this,SetupGroupActivity.class),105);

                }else {
                    Intent intent = new Intent(ClubGroupActivity.this,ClubGroupDetialActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==105 && resultCode==RESULT_OK){
            //创建活动成果，更新活动列表
            Bundle bundle = data.getBundleExtra("bundle");
            ClubGroup clubGroup = bundle.getParcelable("clubGroup");
            Log.i("clubGroup",clubGroup.toString());
            clubGroupAdapter.addClubGroup(clubGroup);
            clubGroupAdapter.notifyDataSetChanged();

        }
    }
}
