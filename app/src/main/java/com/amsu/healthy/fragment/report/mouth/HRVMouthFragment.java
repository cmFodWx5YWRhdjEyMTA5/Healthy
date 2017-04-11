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

public class HRVMouthFragment extends Fragment {
    private FoldLineViewWithTextOne mLineChart;
    private View inflate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        inflate = inflater.inflate(R.layout.fragment_hrvmouth, container, false);
        initView();
        return inflate;
    }

    private void initView() {
        mLineChart = (FoldLineViewWithTextOne) inflate.findViewById(R.id.spread_line_chart);
        //initChart();

        TextView tv_hrvmouth_date = (TextView) inflate.findViewById(R.id.tv_hrvmouth_date);
        tv_hrvmouth_date.setText(MyUtil.getCurrentYearAndMouthr());

    }

    private void initData() {
        if (MyReportActivity.mMouthFullReport!=null){
            List<FullReport.HRVrepBean> hRrep = MyReportActivity.mMouthFullReport.errDesc.HRVrep;
            if (hRrep!=null && hRrep.size()>0){
                int[] datas = new int[hRrep.size()];
                String[] labels = new String[hRrep.size()];
                int i =0;
                for (FullReport.HRVrepBean hrVrepBean:hRrep){
                    datas[i] = Integer.parseInt(hrVrepBean.FI);
                    labels[i] = MyUtil.getReportDateStingForMouthAndDay(hrVrepBean.datatime);
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

       /* int[] datas = {88,79,77,72,95,50,81};  //心率数据
        String[] labels = {"2日","4日","5日","6日","9日","10日","12日"};  //心率数据，需要保证数据长度的一致
        mLineChart.setData(datas,labels);*/
    }


}
