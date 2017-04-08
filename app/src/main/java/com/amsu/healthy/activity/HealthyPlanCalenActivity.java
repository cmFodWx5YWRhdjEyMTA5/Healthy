package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.view.MyCalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HealthyPlanCalenActivity extends BaseActivity {

    private static final String TAG = "HealthyPlanCalen";
    private MyCalendarView vl_healthycalen_calen;
    private TextView tv_plancalen_yearndmouth;
    private TextView tv_healthycalen_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_plan_calen);

        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText("健康计划");
        setLeftImage(R.drawable.back_icon);
        setRightImage(R.drawable.plan_list);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HealthyPlanCalenActivity.this,HealthyPlanActivity.class));
                finish();
            }
        });
        vl_healthycalen_calen = (MyCalendarView) findViewById(R.id.vl_healthycalen_calen);
        tv_plancalen_yearndmouth = (TextView) findViewById(R.id.tv_plancalen_yearndmouth);
        tv_healthycalen_day = (TextView) findViewById(R.id.tv_healthycalen_day);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //设置控件监听，可以监听到点击的每一天（大家也可以在控件中根据需求设定）
        vl_healthycalen_calen.setOnItemClickListener(new MyCalendarView.OnItemClickListener() {

            @Override
            public void OnItemClick(Date selectedStartDate, Date selectedEndDate, Date downDate) {
                if(vl_healthycalen_calen.isSelectMore()){

                    Log.i(TAG,"downDate:isSelectMore   "+simpleDateFormat.format(downDate));
                    //Toast.makeText(getApplicationContext(), format.format(selectedStartDate)+"到"+format.format(selectedEndDate), Toast.LENGTH_SHORT).show();
                }else{
                    Log.i(TAG,"downDate:"+simpleDateFormat.format(downDate));
                    //Toast.makeText(getApplicationContext(), format.format(downDate), Toast.LENGTH_SHORT).show();
                }
                tv_healthycalen_day.setText(downDate.getDate()+"");
            }
        });
        tv_healthycalen_day.setText(new Date().getDate()+"");


        int[] planDays = {1,22,23,24};
        vl_healthycalen_calen.setPlanDays(planDays);
    }

    private void initData() {

    }

    //点击上一月 同样返回年月
    public void preMouth(View view) {

        int[] planDays = {3,27};
        vl_healthycalen_calen.setPlanDays(planDays);

        String leftYearAndmonth = vl_healthycalen_calen.clickLeftMonth();
        String[] ya = leftYearAndmonth.split("-");
        tv_plancalen_yearndmouth.setText(ya[0]+"年"+ya[1]+"月");

    }

    //点击下一月
    public void nextMouth(View view) {
        int[] planDays = {2,11};
        vl_healthycalen_calen.setPlanDays(planDays);

        String rightYearAndmonth = vl_healthycalen_calen.clickRightMonth();
        String[] ya = rightYearAndmonth.split("-");
        tv_plancalen_yearndmouth.setText(ya[0]+"年"+ya[1]+"月");
    }
}
