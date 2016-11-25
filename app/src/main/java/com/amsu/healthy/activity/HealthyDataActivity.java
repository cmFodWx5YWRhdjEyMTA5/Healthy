package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.amsu.healthy.R;

public class HealthyDataActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_data);

        initView();
        initData();
    }

    private void initView() {
        initHeadView();


    }

    private void initValue() {

    }

    private void initData() {
        setCenterText("健康数据");
        setRightText("我的设备");
        setLeftImage(R.drawable.back_icon);

        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public void startSoS(View view) {
        startActivity(new Intent(this,MoveStateActivity.class));
    }
}
