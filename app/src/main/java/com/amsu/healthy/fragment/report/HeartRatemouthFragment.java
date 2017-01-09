package com.amsu.healthy.fragment.report;


import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeartRatemouthFragment extends Fragment {

    private LineChart mLineChart;
    private View inflate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_heart_ratemouth, container, false);
        initView();
        return inflate;
    }

    private void initView() {
        mLineChart = (LineChart) inflate.findViewById(R.id.spread_line_chart);
        initChart();

    }

    private void initChart() {
        // 是否在折线图上添加边框
        mLineChart.setDrawGridBackground(false);
        mLineChart.setDrawBorders(false);
        //设置透明度
        //mLineChart.setAlpha(0.8f);
        //设置网格底下的那条线的颜色
        //mLineChart.setBorderColor(Color.rgb(213, 216, 214));
        //设置是否可以触摸，如为false，则不能拖动，缩放等
        mLineChart.setTouchEnabled(true);
        //设置是否可以拖拽
        mLineChart.setDragEnabled(false);
        //设置是否可以缩放
        mLineChart.setScaleEnabled(false);
        //设置是否能扩大扩小
        mLineChart.setPinchZoom(false);

        //mLineChart.getAxisLeft().setEnabled(false);
        mLineChart.getAxisRight().setEnabled(false);
        //mLineChart.getXAxis().setEnabled(false);
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getAxisLeft().setDrawGridLines(false);

        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // 加载数据
        LineData data = getLineData();
        mLineChart.setData(data);
        /**
         * ====================3.x，y动画效果和刷新图表等===========================
         */
        //从X轴进入的动画
        //mLineChart.animateX(4000);
        mLineChart.animateY(1000);   //从Y轴进入的动画
        //mLineChart.animateXY(3000, 3000);    //从XY轴一起进入的动画
        //设置最小的缩放
        mLineChart.setScaleMinima(0.5f, 1f);


        Legend legend = mLineChart.getLegend();
        legend.setEnabled(false);
        // 刷新图表
        mLineChart.invalidate();
    }

    private LineData getLineData() {
        String[] xx = {"2", "4", "6", "8", "10", "12", "14", "16", "18"};
        float [] yy = new float[30];
        for (int i=0;i<30;i++){
            yy[i] = (float) (Math.random()*50);
        }

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < xx.length; i++) {
            xVals.add(xx[i]);
        }

        ArrayList<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < yy.length; i++) {
            yVals.add(new Entry(i,yy[i]));
        }

        LineDataSet set1 = new LineDataSet(yVals, "LineChart Test");

        //set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(false);  //设置包括的范围区域填充颜色
        set1.setDrawCircles(true);  //设置有圆点
        set1.setLineWidth(2f);    //设置线的宽度

        set1.setCircleColor(Color.RED);
        set1.setHighLightColor(Color.RED);
        set1.setCircleRadius(2f);//设置小圆的大小
        set1.setDrawCircleHole(false);
        set1.setColor(Color.RED);    //设置曲线的颜色

        return new LineData(set1);
    }



}
