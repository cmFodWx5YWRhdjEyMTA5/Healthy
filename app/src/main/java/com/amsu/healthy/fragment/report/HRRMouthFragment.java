package com.amsu.healthy.fragment.report;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.R;
import com.amsu.healthy.view.FoldLineViewWithText;
import com.amsu.healthy.view.FoldLineViewWithTextOne;

/**
 * A simple {@link Fragment} subclass.
 */
public class HRRMouthFragment extends Fragment {
    private FoldLineViewWithTextOne mLineChart;
    private View inflate;


    public HRRMouthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        inflate = inflater.inflate(R.layout.fragment_hrrmouth, container, false);
        initView();
        return inflate;
    }

    private void initView() {
        mLineChart = (FoldLineViewWithTextOne) inflate.findViewById(R.id.spread_line_chart);
        //initChart();



    }

    @Override
    public void onResume() {
        super.onResume();

        int[] datas =    {67,59,54,77,85,100,61};  //心率数据
        String[] labels = {"2日","4日","5日","6日","9日","10日","12日"};  //心率数据，需要保证数据长度的一致
        mLineChart.setData(datas,labels);
    }


}
