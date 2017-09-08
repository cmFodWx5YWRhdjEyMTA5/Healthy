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
import com.amsu.healthy.view.FoldLineViewWithTextOne;

import java.util.Date;
import java.util.List;

public class HRVYearFragment extends BaseFragment {
    private FoldLineViewWithPoint mLineChart;
    private View inflate;
    private TextView tv_mouth_value;
    private TextView tv_mouth_datetime;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        inflate = inflater.inflate(R.layout.fragment_hrv_year, container, false);
        initView();

        return inflate;
    }

    private void initView() {
        TextView tv_ecgmouth_type = (TextView) inflate.findViewById(R.id.tv_ecgmouth_type);
        tv_ecgmouth_type.setText("年度HRV报告");
        mLineChart = (FoldLineViewWithPoint) inflate.findViewById(R.id.spread_line_chart);
        //initChart();

        TextView tv_hrvmouth_date = (TextView) inflate.findViewById(R.id.tv_hrvmouth_date);
        tv_hrvmouth_date.setText(MyUtil.getCurrentYear()+"年");

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
            List<FullReport.HRVrepBean> hRrep = MyReportActivity.mQuarterFullReport.HRVrep;
            if (hRrep!=null && hRrep.size()>0){
                int[] datas = new int[hRrep.size()];
                String[] labels = new String[hRrep.size()];
                int i =0;
                for (FullReport.HRVrepBean hrVrepBean:hRrep){
                    datas[i] = hrVrepBean.fi;
                    labels[i] =  MyUtil.getSpecialFormatTime("MM月dd日",new Date(hrVrepBean.timestamp));
                    i++;
                }
                mLineChart.setData(datas,labels);
                tv_mouth_value.setText(datas[datas.length-1]+"");
                tv_mouth_datetime.setText(labels[labels.length-1]);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();

        int[] datas =    {67,59,54,67,60,60,61};  //心率数据
        String[] datetime =    {"10月1日","10月2日","10月3日","10月4日","10月5日","10月6日","10月7日"};  //心率数据
        mLineChart.setData(datas,datetime);

        mLineChart.setOnDateTimeChangeListener(new FoldLineViewWithPoint.OnDateTimeChangeListener() {
            @Override
            public void onDateTimeChange(int heartRate, String dateTime) {
                tv_mouth_value.setText(heartRate+"");
                tv_mouth_datetime.setText(dateTime);
            }
        });


    }


}
