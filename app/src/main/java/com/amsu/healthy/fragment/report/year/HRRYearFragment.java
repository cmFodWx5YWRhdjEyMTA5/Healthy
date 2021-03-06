package com.amsu.healthy.fragment.report.year;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.MyReportActivity;
import com.amsu.healthy.bean.FullReport;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.FoldLineViewWithPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HRRYearFragment extends BaseFragment {
    private FoldLineViewWithPoint mLineChart;
    private View inflate;
    private TextView tv_mouth_value;
    private TextView tv_mouth_datetime;


    public HRRYearFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        inflate = inflater.inflate(R.layout.fragment_hrr_year, container, false);
        initView();

        return inflate;
    }



    private void initView() {
        TextView tv_ecgmouth_type = (TextView) inflate.findViewById(R.id.tv_ecgmouth_type);
        tv_ecgmouth_type.setText(getResources().getString(R.string.Quarterly_HRR_Report));
        mLineChart = (FoldLineViewWithPoint) inflate.findViewById(R.id.spread_line_chart);
        //initChart();
        TextView tv_hrrmouth_date = (TextView) inflate.findViewById(R.id.tv_hrrmouth_date);
        tv_hrrmouth_date.setText(MyUtil.getCurrentYear()+"");


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
        if (MyReportActivity.mYearFullReport!=null){
            List<FullReport.HRRrepBean> hRrep = MyReportActivity.mYearFullReport.HRRrep;
            if (hRrep!=null && hRrep.size()>0){
                List<Integer> dataList = new ArrayList<>();
                List<String> labeList = new ArrayList<>();
                for (FullReport.HRRrepBean hrRrepBean:hRrep){

                        if (hrRrepBean.ra>0){
                            dataList.add(hrRrepBean.ra);

                            String monthString  =getResources().getString(R.string.month);
                            String dayString  =getResources().getString(R.string.day);
                            labeList.add( MyUtil.getSpecialFormatTime("MM"+monthString+"dd"+dayString,new Date(hrRrepBean.datatime)));
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
