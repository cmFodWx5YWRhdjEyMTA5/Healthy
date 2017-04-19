package com.amsu.healthy.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.view.FoldLineViewWithPoint;
import com.amsu.healthy.view.FoldLineViewWithTextOne;

/**
 * Created by HP on 2017/4/14.
 */

public class TestActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        FoldLineViewWithPoint spread_line_chart = (FoldLineViewWithPoint) findViewById(R.id.spread_line_chart);
        final TextView tv_mouth_value = (TextView) findViewById(R.id.tv_mouth_value);
        final TextView tv_mouth_datetime = (TextView) findViewById(R.id.tv_mouth_datetime);

        int[] datas =    {67,59,54,67,60,60,61};  //心率数据
        String[] datetime =    {"10月1日","10月2日","10月3日","10月4日","10月5日","10月6日","10月7日"};  //心率数据
        spread_line_chart.setData(datas,datetime);

        spread_line_chart.setOnDateTimeChangeListener(new FoldLineViewWithPoint.OnDateTimeChangeListener() {
            @Override
            public void onDateTimeChange(int heartRate, String dateTime) {
                tv_mouth_value.setText(heartRate+"");
                tv_mouth_datetime.setText(dateTime);
            }
        });
    }
}
