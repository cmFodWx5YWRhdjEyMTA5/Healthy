package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.Club;
import com.amsu.healthy.bean.ClubCampaign;
import com.amsu.healthy.view.RoundRectImageView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;

public class ClubInfomationActivity extends BaseActivity {

    private TextView tv_setpclub_name;
    private TextView tv_setpclub_sporttype;
    private TextView tv_setpclub_clubtype;
    private TextView tv_setpclub_description;
    private RoundRectImageView iv_setpclub_addiocn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_infomation);
        initView();
        initData();

    }

    private void initView() {
        initHeadView();
        setCenterText("创建俱乐部");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setRightText("编辑");

        iv_setpclub_addiocn = (RoundRectImageView) findViewById(R.id.iv_setpclub_addiocn);
        tv_setpclub_name = (TextView) findViewById(R.id.tv_setpclub_name);
        tv_setpclub_sporttype = (TextView) findViewById(R.id.tv_setpclub_sporttype);
        tv_setpclub_clubtype = (TextView) findViewById(R.id.tv_setpclub_clubtype);
        tv_setpclub_description = (TextView) findViewById(R.id.tv_setpclub_description);


    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        Club club =  bundle.getParcelable("club");

        if (club!=null){
            tv_setpclub_name.setText(club.getName());
            tv_setpclub_sporttype.setText(club.getType());
            tv_setpclub_clubtype.setText(club.getType());
            tv_setpclub_description.setText("描述");

           BitmapUtils bitmapUtils = new BitmapUtils(this);
           bitmapUtils.display(iv_setpclub_addiocn,club.getSimallImageUrl());
        }

    }


    public void jionClub(View view) {
        startActivity(new Intent(this,ApplyJionClubActivity.class));

    }
}
