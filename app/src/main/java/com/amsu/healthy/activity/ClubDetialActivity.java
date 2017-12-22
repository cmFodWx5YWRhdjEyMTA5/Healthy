package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.ClubCampaign;

public class ClubDetialActivity extends BaseActivity {

    private TextView tv_clubdetial_name;
    private TextView tv_clubdetial_type;
    private TextView tv_clubdetial_date;
    private TextView tv_clubdetial_time;
    private TextView tv_clubdetial_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detial);


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

        tv_clubdetial_name = (TextView) findViewById(R.id.tv_clubdetial_name);
        tv_clubdetial_type = (TextView) findViewById(R.id.tv_clubdetial_type);
        tv_clubdetial_date = (TextView) findViewById(R.id.tv_clubdetial_date);
        tv_clubdetial_time = (TextView) findViewById(R.id.tv_clubdetial_time);
        tv_clubdetial_description = (TextView) findViewById(R.id.tv_clubdetial_description);


    }
    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        ClubCampaign ClubCampaign =  bundle.getParcelable("clubActivity");

        if (ClubCampaign!=null){
            tv_clubdetial_name.setText(ClubCampaign.getName());
            tv_clubdetial_type.setText(ClubCampaign.getType());
            tv_clubdetial_date.setText(ClubCampaign.getDate());
            tv_clubdetial_time.setText(ClubCampaign.getTime());
            tv_clubdetial_description.setText(ClubCampaign.getDescription());

        }

    }

    public void jionClub(View view) {

    }
}
