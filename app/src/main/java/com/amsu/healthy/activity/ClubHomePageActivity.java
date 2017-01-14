package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;

public class ClubHomePageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_home_page);

        initView();
    }

    private void initView() {
        // 透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        RelativeLayout rl_detial_number = (RelativeLayout) findViewById(R.id.rl_detial_number);
        RelativeLayout rl_detial_activity = (RelativeLayout) findViewById(R.id.rl_detial_activity);
        RelativeLayout rl_detial_group = (RelativeLayout) findViewById(R.id.rl_detial_group);
        RelativeLayout rl_detial_more = (RelativeLayout) findViewById(R.id.rl_detial_more);
        RelativeLayout rl_homepage_rank = (RelativeLayout) findViewById(R.id.rl_homepage_rank);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        rl_detial_number.setOnClickListener(myOnClickListener);
        rl_detial_activity.setOnClickListener(myOnClickListener);
        rl_detial_group.setOnClickListener(myOnClickListener);
        rl_detial_more.setOnClickListener(myOnClickListener);
        rl_homepage_rank.setOnClickListener(myOnClickListener);
    }


    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_detial_number:
                    startActivity(new Intent(ClubHomePageActivity.this,ClubNumberActivity.class));
                    break;
                case R.id.rl_detial_activity:
                    startActivity(new Intent(ClubHomePageActivity.this,ClubCampaignActivity.class));
                    break;
                case R.id.rl_detial_group:
                    startActivity(new Intent(ClubHomePageActivity.this,ClubGroupActivity.class));
                    break;
                case R.id.rl_detial_more:
                    startActivity(new Intent(ClubHomePageActivity.this,ClubMoreActivity.class));
                    break;
                case R.id.rl_homepage_rank:
                    startActivity(new Intent(ClubHomePageActivity.this,ClubNumberRankActivity.class));
                    break;
            }
        }
    }
}
