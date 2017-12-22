package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;

public class ClubMyEnterInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_my_enter_info);
        initView();
        initData();

    }



    private void initView() {
        initHeadView();
        setCenterText("我的报名信息");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setRightText("编辑");
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClubMyEnterInfoActivity.this,ClubMyEnterInfoEditActivity.class));
            }
        });


        TextView tv_mynumberinfo_name = (TextView) findViewById(R.id.tv_mynumberinfo_name);
        TextView tv_mynumberinfo_sex = (TextView) findViewById(R.id.tv_mynumberinfo_sex);
        TextView tv_mynumberinfo_phone = (TextView) findViewById(R.id.tv_mynumberinfo_phone);
        TextView tv_mynumberinfo_blood = (TextView) findViewById(R.id.tv_mynumberinfo_blood);
        TextView tv_mynumberinfo_certitype = (TextView) findViewById(R.id.tv_mynumberinfo_certitype);
        TextView tv_mynumberinfo_certinumber = (TextView) findViewById(R.id.tv_mynumberinfo_certinumber);
        TextView tv_mynumberinfo_emergname = (TextView) findViewById(R.id.tv_mynumberinfo_emergname);
        TextView tv_mynumberinfo_emergrlation = (TextView) findViewById(R.id.tv_mynumberinfo_emergrlation);
        TextView tv_mynumberinfo_emergphone = (TextView) findViewById(R.id.tv_mynumberinfo_emergphone);




    }

    private void initData() {

    }
}
