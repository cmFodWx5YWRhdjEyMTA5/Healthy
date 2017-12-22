package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.ClubGroupNumberAdapter;
import com.amsu.healthy.bean.ClubNumber;

import java.util.ArrayList;
import java.util.List;

public class ClubGroupDetialActivity extends BaseActivity {

    private ListView lv_groupdetia_list;
    private List<ClubNumber> clubGroups;
    private ClubGroupNumberAdapter clubGroupNumberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_group_detial);

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


        lv_groupdetia_list = (ListView) findViewById(R.id.lv_groupdetia_list);




    }

    private void initData() {
        clubGroups = new ArrayList<>();
        clubGroups.add(new ClubNumber("青烟绕指柔","hh","广东省  深圳市","hh","hh","hh","hh","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubGroups.add(new ClubNumber("青烟绕指柔","hh","广东省  深圳市","hh","hh","hh","hh","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubGroups.add(new ClubNumber("青烟绕指柔","hh","广东省  深圳市","hh","hh","hh","hh","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubGroups.add(new ClubNumber("青烟绕指柔","hh","广东省  深圳市","hh","hh","hh","hh","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubGroups.add(new ClubNumber("青烟绕指柔","hh","广东省  深圳市","hh","hh","hh","hh","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));


        clubGroupNumberAdapter = new ClubGroupNumberAdapter(this,clubGroups);
        lv_groupdetia_list.setAdapter(clubGroupNumberAdapter);

    }

    /*//加入小组
    public void jionClub(View view) {
        startActivity(new Intent(this,ApplyJoinGroupActivity.class));



    }*/
}
