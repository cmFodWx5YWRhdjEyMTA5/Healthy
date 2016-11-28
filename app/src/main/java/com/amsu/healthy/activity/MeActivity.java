package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;

public class MeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        initView();


    }

    private void initView() {
        ImageView iv_me_back = (ImageView) findViewById(R.id.iv_me_back);
        ImageView iv_me_headicon = (ImageView) findViewById(R.id.iv_me_headicon);

        RelativeLayout rl_me_report = (RelativeLayout) findViewById(R.id.rl_me_report);
        RelativeLayout rl_me_healthplan = (RelativeLayout) findViewById(R.id.rl_me_healthplan);
        RelativeLayout rl_me_follow = (RelativeLayout) findViewById(R.id.rl_me_follow);
        RelativeLayout rl_me_help = (RelativeLayout) findViewById(R.id.rl_me_help);
        RelativeLayout rl_me_setting = (RelativeLayout) findViewById(R.id.rl_me_setting);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        iv_me_back.setOnClickListener(myOnClickListener);
        iv_me_headicon.setOnClickListener(myOnClickListener);
        rl_me_healthplan.setOnClickListener(myOnClickListener);
        rl_me_follow.setOnClickListener(myOnClickListener);
        rl_me_help.setOnClickListener(myOnClickListener);
        rl_me_setting.setOnClickListener(myOnClickListener);


    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_me_back:
                    //返回
                    finish();
                    break;
                case R.id.iv_me_headicon:
                    startActivity(new Intent(MeActivity.this,RegisterSetp1Activity.class));

                    break;
                case R.id.rl_me_report:

                    break;
                case R.id.rl_me_healthplan:

                    break;
                case R.id.rl_me_follow:

                    break;
                case R.id.rl_me_help:

                    break;
                case R.id.rl_me_setting:

                    break;
            }
        }
    }
}
