package com.amsu.healthy.fragment.report;


import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.R;
import com.amsu.healthy.view.FoldLineViewWithText;


/**
 * A simple {@link Fragment} subclass.
 */
public class HeartRatemouthFragment extends Fragment {

    private FoldLineViewWithText mLineChart;
    private View inflate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_heart_ratemouth, container, false);
        initView();
        return inflate;
    }

    private void initView() {
        mLineChart = (FoldLineViewWithText) inflate.findViewById(R.id.spread_line_chart);
        //initChart();



    }

    @Override
    public void onResume() {
        super.onResume();

        int[] datas =    {67,59,54,67,60,60,61};  //心率数据
        int[] data_max = {97,71,67,89,95,80,75};
        String[] labels = {"2日","4日","5日","6日","9日","10日","12日"};  //心率数据，需要保证数据长度的一致
        mLineChart.setData(datas,data_max,labels);
    }



}
