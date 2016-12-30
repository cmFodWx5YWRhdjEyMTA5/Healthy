package com.amsu.healthy.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class TestActivity extends BaseActivity {

    private static final String TAG = "TestActivity";
    private RadarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);



        mChart = (RadarChart) findViewById(R.id.chart1);
        mChart.setBackgroundColor(Color.WHITE);

        mChart.getDescription().setEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.GRAY);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.GRAY);
        //mChart.setWebAlpha(100);

        mChart.setRotationEnabled(false);
        setData();

        mChart.animateXY(1400, 1400, Easing.EasingOption.EaseInOutQuad, Easing.EasingOption.EaseInOutQuad);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.i(TAG,"onValueSelected:"+e.toString()+",h:"+h.toString());
            }

            @Override
            public void onNothingSelected() {

            }
        });

        XAxis xAxis = mChart.getXAxis();

        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(5f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mActivities = new String[]{"BMI", "储备心率", "恢复心率", "心率变异", "过缓/过速","早搏/晨搏","健康储备"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        //xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = mChart.getYAxis();



        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(2f);
        yAxis.setAxisMaximum(100f);
        yAxis.setDrawLabels(false);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        //l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setDrawInside(false);
        l.setXEntrySpace(MyUtil.dp2px(this,10));
        l.setYEntrySpace(5f);
        //l.setTextColor(Color.WHITE);
    }



    public void setData() {

        float mult = 90;
        float min = 10;
        int cnt = 7;

        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
        ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();
        ArrayList<RadarEntry> entries3 = new ArrayList<RadarEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < cnt; i++) {
            float val1 = (float) (Math.random() * mult) + min;
            entries1.add(new RadarEntry(val1));

            float val2 = (float) (Math.random() * mult) + min;
            entries2.add(new RadarEntry(val2));

            float val3 = (float) (Math.random() * mult) + min;
            entries3.add(new RadarEntry(val3));
        }

        RadarDataSet set1 = new RadarDataSet(entries1, "本周");
        set1.setColor(Color.rgb(103, 110, 129));
        set1.setFillColor(Color.rgb(103, 110, 129));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(entries2, "上周");
        set2.setColor(Color.rgb(121, 162, 175));
        set2.setFillColor(Color.rgb(121, 162, 175));
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);

        RadarDataSet set3 = new RadarDataSet(entries3, "上上周");
        set3.setColor(Color.rgb(21, 212, 115));
        set3.setFillColor(Color.rgb(21, 212, 115));
        set3.setDrawFilled(true);
        set3.setFillAlpha(180);
        set3.setLineWidth(2f);
        set3.setDrawHighlightCircleEnabled(true);
        set3.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);
        sets.add(set2);
        sets.add(set3);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.invalidate();
    }

}
