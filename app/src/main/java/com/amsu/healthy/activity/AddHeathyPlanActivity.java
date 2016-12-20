package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.view.DateTimeDialogOnlyYMD;

import java.util.Date;

public class AddHeathyPlanActivity extends BaseActivity implements DateTimeDialogOnlyYMD.MyOnDateSetListener{

    private EditText et_addplan_title;
    private EditText et_addplan_content;
    private TextView tv_addplan_time;

    private static final String TAG = "AddHeathyPlanActivity";
    private DateTimeDialogOnlyYMD dateTimeDialogOnlyYMD;
    private int year;
    private int month;
    private int day;
    String time = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_healyh_plan);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("健康计划");
        setLeftImage(R.drawable.back_icon);
        setRightText("保存");
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        et_addplan_title = (EditText) findViewById(R.id.et_addplan_title);
        et_addplan_content = (EditText) findViewById(R.id.et_addplan_content);
        tv_addplan_time = (TextView) findViewById(R.id.tv_addplan_time);
        dateTimeDialogOnlyYMD = new DateTimeDialogOnlyYMD(this, this, true, true, true);

        tv_addplan_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeDialogOnlyYMD.hideOrShow();
            }
        });


    }

    @Override
    public void onDateSet(Date date) {
        year = date.getYear() + 1900;
        month = date.getMonth() + 1;
        day = date.getDate();

        Log.i(TAG,"onDateSet:"+ year +","+ month +","+ day);
        tv_addplan_time.setText(year +"-"+ month +"-"+ day);   //
        time = year +"-"+ month +"-"+ day;

    }
}
