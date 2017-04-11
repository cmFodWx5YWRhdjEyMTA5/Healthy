package com.amsu.healthy.fragment.report.mouth;


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
import com.amsu.healthy.view.FoldLineViewWithText;
import com.amsu.healthy.view.FoldLineViewWithTextOne;

import java.util.List;

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
        TextView tv_hrrmouth_date = (TextView) inflate.findViewById(R.id.tv_hrrmouth_date);
        tv_hrrmouth_date.setText(MyUtil.getCurrentYearAndMouthr());


    }

    private void initData() {
        if (MyReportActivity.mMouthFullReport!=null){
            List<FullReport.HRRrepBean> hRrep = MyReportActivity.mMouthFullReport.errDesc.HRRrep;
            if (hRrep!=null && hRrep.size()>0){
                int[] datas = new int[hRrep.size()];
                String[] labels = new String[hRrep.size()];
                int i =0;
                for (FullReport.HRRrepBean hrRrepBean:hRrep){
                    datas[i] = Integer.parseInt(hrRrepBean.RA);
                    labels[i] = MyUtil.getReportDateStingForMouthAndDay(hrRrepBean.datatime);
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

        /*int[] datas =    {67,59,54,77,85,100,61,67,59,54,77,85,100,61,67,59,54,77,85,100,61,67,59,54,77,85,100,61};  //心率数据
        int[] data1 = new int[30];

        for (int i=0;i<data1.length;i++){
            data1[i] = (int) (Math.random()*(85-30) + 30);
        }

        String[] labels = new String[data1.length];
        for (int i=0;i<data1.length;i++){
            labels[i] = i+"日";
        }
        mLineChart.setData(data1,labels);*/
    }


}
