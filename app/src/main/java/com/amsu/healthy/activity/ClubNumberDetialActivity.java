package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.ClubNumber;

public class ClubNumberDetialActivity extends BaseActivity {

    private TextView tv_numberdetial_name;
    private TextView tv_numberdetial_sex;
    private TextView tv_numberdetial_phone;
    private TextView tv_numberdetial_realname;
    private TextView tv_numberdetial_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_number_detial);
        initView();
        initData();
    }



    private void initView() {
        initHeadView();
        setCenterText("活动详情");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        ClubNumber clubNumber =  bundle.getParcelable("clubNumber");

        if (clubNumber!=null){
            tv_numberdetial_name.setText(clubNumber.getPickName());
            tv_numberdetial_sex.setText(clubNumber.getSex());
            tv_numberdetial_phone.setText(clubNumber.getPhone());
            tv_numberdetial_realname.setText(clubNumber.getRealName());
            tv_numberdetial_description.setText(clubNumber.getIntroduction());

        }

    }

    public void removeClub(View view) {

    }

    public void inviteToCoach(View view) {

    }
}
