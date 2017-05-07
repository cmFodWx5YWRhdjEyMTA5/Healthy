package com.amsu.healthy.fragment.report.year;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
public class HeartRateYearFragment extends Fragment {

    private static final String TAG = "HeartRateQuarter";
    private FoldLineViewWithPoint mLineChart;
    private View inflate;
    private TextView tv_heartRatemouth_date;
    private TextView tv_mouth_value;
    private TextView tv_mouth_datetime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");
        inflate = inflater.inflate(R.layout.fragment_heartrate_year, container, false);
        initView();

        return inflate;
    }



    private void initView() {
        TextView tv_ecgmouth_type = (TextView) inflate.findViewById(R.id.tv_ecgmouth_type);
        tv_ecgmouth_type.setText("年度心率报告");
        tv_heartRatemouth_date = (TextView) inflate.findViewById(R.id.tv_heartRatemouth_date);
        mLineChart = (FoldLineViewWithPoint) inflate.findViewById(R.id.spread_line_chart);

        tv_heartRatemouth_date.setText(MyUtil.getCurrentYear()+"年");

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
            List<FullReport.HRrepBean> hRrep = MyReportActivity.mQuarterFullReport.errDesc.HRrep;
            if (hRrep!=null && hRrep.size()>0){
                List<Integer> dataIntegerList = new ArrayList<>();
                List<String> datetimesList = new ArrayList<>();
                int heart;
                for (FullReport.HRrepBean hRrepBean:hRrep){
                    heart = Integer.parseInt(hRrepBean.AHR);
                    if (heart>0) {
                        dataIntegerList.add(heart);
                        datetimesList.add(MyUtil.getReportDateStingForMouthAndDay(hRrepBean.datatime));
                    }
                }

                int[] datas = new int[dataIntegerList.size()];
                for (int i=0;i<dataIntegerList.size();i++){
                    datas[i] = dataIntegerList.get(i);
                }

                String[] datetimes = new String[datetimesList.size()];
                datetimesList.toArray(datetimes);

                mLineChart.setData(datas,datetimes);
                tv_mouth_value.setText(datas[datas.length-1]+"");
                tv_mouth_datetime.setText(datetimes[datetimes.length-1]);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
        initData();

        /*int[] datas =    {67,59,54,67,60,60,61};  //心率数据
        String[] datetime =    {"10月1日","10月2日","10月3日","10月4日","10月5日","10月6日","10月7日"};  //心率数据
        mLineChart.setData(datas,datetime);*/

        mLineChart.setOnDateTimeChangeListener(new FoldLineViewWithPoint.OnDateTimeChangeListener() {
            @Override
            public void onDateTimeChange(int heartRate, String dateTime) {
                tv_mouth_value.setText(heartRate+"");
                tv_mouth_datetime.setText(dateTime);
            }
        });






    }



}
