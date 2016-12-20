package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.HealthyPlanDataAdapter;
import com.amsu.healthy.bean.HealthyPlan;

import java.util.ArrayList;
import java.util.List;

public class HealthyPlanActivity extends BaseActivity {

    private ListView lv_healthplan_plan;
    private List<HealthyPlan> healthyPlanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_plan);

        initView();
        initData();
    }



    private void initView() {
        initHeadView();
        setCenterText("健康计划");
        setLeftImage(R.drawable.back_icon);
        setRightImage(R.drawable.plan_calendar);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HealthyPlanActivity.this,HealthyPlanCalenActivity.class));
                finish();
            }
        });


        lv_healthplan_plan = (ListView) findViewById(R.id.lv_healthplan_plan);



    }

    private void initData() {
        healthyPlanList = new ArrayList<>();
        healthyPlanList.add(new HealthyPlan("hhh","hhhh","hhhh"));
        HealthyPlanDataAdapter healthyPlanDataAdapter = new HealthyPlanDataAdapter(this,healthyPlanList);
        lv_healthplan_plan.setAdapter(healthyPlanDataAdapter);
    }


}
