package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.ClubNumber;

public class ClubMyInfoActivity extends BaseActivity {
    private TextView tv_numberdetial_name;
    private TextView tv_numberdetial_sex;
    private TextView tv_numberdetial_phone;
    private TextView tv_numberdetial_realname;
    private TextView tv_numberdetial_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_my_info);
        initView();
        initData();
    }




    private void initView() {
        initHeadView();
        setCenterText("我的会员信息");
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
                startActivity(new Intent(ClubMyInfoActivity.this,ClubMyInfoEditActivity.class));
            }
        });


        tv_numberdetial_name = (TextView) findViewById(R.id.tv_numberdetial_name);
        tv_numberdetial_sex = (TextView) findViewById(R.id.tv_numberdetial_sex);
        tv_numberdetial_phone = (TextView) findViewById(R.id.tv_numberdetial_phone);
        tv_numberdetial_realname = (TextView) findViewById(R.id.tv_numberdetial_realname);
        tv_numberdetial_description = (TextView) findViewById(R.id.tv_numberdetial_description);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        ClubNumber clubNumber = null;
        if (bundle!=null){
            clubNumber =  bundle.getParcelable("clubNumber");
        }

        if (clubNumber!=null){
            tv_numberdetial_name.setText(clubNumber.getPickName());
            tv_numberdetial_sex.setText(clubNumber.getSex());
            tv_numberdetial_phone.setText(clubNumber.getPhone());
            tv_numberdetial_realname.setText(clubNumber.getRealName());
            tv_numberdetial_description.setText(clubNumber.getIntroduction());

        }
    }

}
