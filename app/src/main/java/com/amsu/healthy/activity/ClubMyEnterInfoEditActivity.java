package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;

public class ClubMyEnterInfoEditActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_my_enter_info_edit);
        initView();
        initData();

    }



    private void initView() {
        initHeadView();
        setCenterText("修改报名信息");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setRightText("完成");
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
