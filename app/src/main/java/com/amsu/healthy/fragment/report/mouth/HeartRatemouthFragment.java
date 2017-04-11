package com.amsu.healthy.fragment.report.mouth;


import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.MyReportActivity;
import com.amsu.healthy.bean.FullReport;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.FoldLineViewWithText;
import com.amsu.healthy.view.FoldLineViewWithTextOne;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HeartRatemouthFragment extends Fragment {

    private FoldLineViewWithTextOne mLineChart;
    private View inflate;
    private TextView tv_heartRatemouth_date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_heart_ratemouth, container, false);
        initView();
        return inflate;
    }



    private void initView() {
        tv_heartRatemouth_date = (TextView) inflate.findViewById(R.id.tv_heartRatemouth_date);
        mLineChart = (FoldLineViewWithTextOne) inflate.findViewById(R.id.spread_line_chart);

        tv_heartRatemouth_date.setText(MyUtil.getCurrentYearAndMouthr());



    }

    private void initData() {
        if (MyReportActivity.mMouthFullReport!=null){
            List<FullReport.HRrepBean> hRrep = MyReportActivity.mMouthFullReport.errDesc.HRrep;
            if (hRrep!=null && hRrep.size()>0){
                int[] datas = new int[hRrep.size()];
                String[] labels = new String[hRrep.size()];
                int i =0;
                for (FullReport.HRrepBean hRrepBean:hRrep){
                    datas[i] = Integer.parseInt(hRrepBean.AHR);
                    labels[i] = MyUtil.getReportDateStingForMouthAndDay(hRrepBean.datatime);
                    i++;
                }
                mLineChart.setData(datas,labels);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();


        initData();

        /*int[] datas =    {67,59,54,67,60,60,61};  //心率数据
        String[] labels = {"2日","4日","5日","6日","9日","10日","12日"};  //心率数据，需要保证数据长度的一致
        mLineChart.setData(datas,labels);*/




    }



}
