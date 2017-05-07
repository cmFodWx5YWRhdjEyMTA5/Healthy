package com.amsu.healthy.fragment.report.quarter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.MyReportActivity;
import com.amsu.healthy.bean.FullReport;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.FoldLineViewWithPoint;
import com.amsu.healthy.view.FoldLineViewWithTextOne;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HRRQuarterFragment extends Fragment {
    private FoldLineViewWithPoint mLineChart;
    private View inflate;
    private TextView tv_mouth_value;
    private TextView tv_mouth_datetime;


    public HRRQuarterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        inflate = inflater.inflate(R.layout.fragment_hrr_quarter, container, false);

        initView();

        return inflate;
    }



    private void initView() {
        TextView tv_ecgmouth_type = (TextView) inflate.findViewById(R.id.tv_ecgmouth_type);
        tv_ecgmouth_type.setText("季度HRR报告");
        mLineChart = (FoldLineViewWithPoint) inflate.findViewById(R.id.spread_line_chart);
        //initChart();
        TextView tv_hrrmouth_date = (TextView) inflate.findViewById(R.id.tv_hrrmouth_date);
        tv_hrrmouth_date.setText(MyUtil.getCurrentYearAndQuarter());

        tv_mouth_value = (TextView) inflate.findViewById(R.id.tv_mouth_value);
        tv_mouth_datetime = (TextView) inflate.findViewById(R.id.tv_mouth_datetime);

        mLineChart.setOnDateTimeChangeListener(new FoldLineViewWithPoint.OnDateTimeChangeListener() {
            @Override
            public void onDateTimeChange(int heartRate, String dateTime) {
                tv_mouth_value.setText(heartRate+"");
                tv_mouth_datetime.setText(dateTime);
            }
        });

    }

    private void initData() {
        if (MyReportActivity.mQuarterFullReport!=null){
            List<FullReport.HRRrepBean> hRrep = MyReportActivity.mQuarterFullReport.errDesc.HRRrep;
            if (hRrep!=null && hRrep.size()>0){
                List<Integer> dataList = new ArrayList<>();
                List<String> labeList = new ArrayList<>();
                for (FullReport.HRRrepBean hrRrepBean:hRrep){
                    if (Integer.parseInt(hrRrepBean.RA)>0){
                        dataList.add(Integer.parseInt(hrRrepBean.RA));
                        labeList.add(MyUtil.getReportDateStingForMouthAndDay(hrRrepBean.datatime));
                    }
                }

                int[] datas = new int[dataList.size()];
                String[] labels = new String[dataList.size()];
                for (int j=0;j<dataList.size();j++){
                    datas[j] = dataList.get(j);
                    labels[j] = labeList.get(j);
                }

                if (dataList.size()>0){
                    mLineChart.setData(datas,labels);
                    tv_mouth_value.setText(datas[datas.length-1]+"");
                    tv_mouth_datetime.setText(labels[labels.length-1]);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();


        /*int[] datas =    {67,59,54,67,60,60,61};  //心率数据
        String[] datetime =    {"10月1日","10月2日","10月3日","10月4日","10月5日","10月6日","10月7日"};  //心率数据
        mLineChart.setData(datas,datetime);

        mLineChart.setOnDateTimeChangeListener(new FoldLineViewWithPoint.OnDateTimeChangeListener() {
            @Override
            public void onDateTimeChange(int heartRate, String dateTime) {
                tv_mouth_value.setText(heartRate+"");
                tv_mouth_datetime.setText(dateTime);
            }
        });*/


    }


}
