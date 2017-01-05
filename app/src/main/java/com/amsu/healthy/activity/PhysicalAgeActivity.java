package com.amsu.healthy.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.view.DashboardView;

public class PhysicalAgeActivity extends BaseActivity {

    private DashboardView dv_main_compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_age);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("生理年龄");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dv_main_compass = (DashboardView) findViewById(R.id.dv_main_compass);

        dv_main_compass.setAgeData(70);
    }
}
