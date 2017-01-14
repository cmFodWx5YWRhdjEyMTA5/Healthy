package com.amsu.healthy.fragment.report;


import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.R;
import com.amsu.healthy.view.PieChart;

/**
 * A simple {@link Fragment} subclass.
 */
public class EcgMouthFragment extends Fragment {


    private static final String TAG = "EcgMouthFragment";
    private PieChart pc_ecg_chart;
    private View inflate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflate = inflater.inflate(R.layout.fragment_ecg_mouth, container, false);
        initView();

        return inflate;
    }

    private void initView() {
        pc_ecg_chart = (PieChart) inflate.findViewById(R.id.pc_ecg_chart);
        //PieData mPieData = getPieData();
       // showChart(mChart, mPieData);

        int[] datas = {16,5,7,2};
        pc_ecg_chart.setDatas(datas);


    }


    /*private void showChart(PieChart pieChart, PieData pieData) {
        pieChart.setHoleRadius(40f);  //半径
        //pieChart.setCenterTextRadiusPercent(dimen1);
        pieChart.setTransparentCircleRadius(0); // 半透明圈
        //pieChart.setHoleRadius(0)  //实心圆

        //pieChart.setDescription("测试饼状图");

        // mChart.setDrawYValues(true);
        pieChart.setDrawCenterText(false);  //饼状图中间可以添加文字

        pieChart.setDrawHoleEnabled(true);


        pieChart.setRotationAngle(90); // 初始旋转角度

        // draws the corresponding description value into the slice
        // mChart.setDrawXValues(true);

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(false); // 可以手动旋转

        mChart.setDrawSliceText(false);

        // display percentage values
        pieChart.setUsePercentValues(true);  //显示成百分比
        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
//      mChart.setOnChartValueSelectedListener(this);
        // mChart.setTouchEnabled(false);

//      mChart.setOnAnimationListener(this);

        //pieChart.setCenterText("Quarterly Revenue");  //饼状图中间的文字

        //设置数据
        pieChart.setData(pieData);

        pieChart.setEntryLabelTextSize(MyUtil.dp2px(getActivity(),45));

        // undo all highlights
//      pieChart.highlightValues(null);
//      pieChart.invalidate();

        Legend mLegend = pieChart.getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);  //最右边显示
//      mLegend.setForm(LegendForm.LINE);  //设置比例图的形状，默认是方形
        mLegend.setXEntrySpace(20f);
        mLegend.setYEntrySpace(5f);
        *//*mLegend.setFormSize(MyUtil.getDimen(getActivity(),R.dimen.x40));
        mLegend.setTextSize(MyUtil.getDimen(getActivity(),R.dimen.x40));
        mLegend.setTextColor(Color.parseColor("#6b6b6b"));*//*

        pieChart.animateXY(1000, 1000);  //设置动画
        // mChart.spin(2000, 0, 360);
    }

    private PieData getPieData() {
        ArrayList<PieEntry> yValues = new ArrayList<>();  //yVals用来表示封装每个饼块的实际数据
        float quarterly1 = 14;
        float quarterly2 = 14;
        float quarterly3 = 34;
        float quarterly4 = 38;

        yValues.add(new PieEntry(quarterly1, "正常"));
        yValues.add(new PieEntry(quarterly2, "异常"));
        yValues.add(new PieEntry(quarterly3, "漏博"));
        yValues.add(new PieEntry(quarterly4, "早搏"));

        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "");*//*显示在比例图上*//*
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离

        ArrayList<Integer> colors = new ArrayList<>();
        // 饼图颜色
        colors.add(Color.parseColor("#00a352"));
        colors.add(Color.parseColor("#f15a24"));
        colors.add(Color.parseColor("#f5a623"));
        colors.add(Color.parseColor("#5c4eaf"));

        pieDataSet.setColors(colors);


        *//*DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度*//*

        PieData pieData = new PieData( pieDataSet);


        return pieData;
    }*/
}
