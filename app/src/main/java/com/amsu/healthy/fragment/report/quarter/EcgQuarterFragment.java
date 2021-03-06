package com.amsu.healthy.fragment.report.quarter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.MyReportActivity;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.PieChart;

/**
 * A simple {@link Fragment} subclass.
 */
public class EcgQuarterFragment extends BaseFragment {


    private static final String TAG = "EcgYearFragment";
    private PieChart pc_ecg_chart;
    private View inflate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflate = inflater.inflate(R.layout.fragment_ecg_quarter, container, false);
        initView();
        return inflate;
    }

    private void initView() {

        TextView tv_ecgmouth_type = (TextView) inflate.findViewById(R.id.tv_ecgmouth_type);
        tv_ecgmouth_type.setText(getResources().getString(R.string.Quarterly_ECG_Report));
        pc_ecg_chart = (PieChart) inflate.findViewById(R.id.pc_ecg_chart);

        TextView tv_ecgmouth_date = (TextView) inflate.findViewById(R.id.tv_ecgmouth_date);
        tv_ecgmouth_date.setText(MyUtil.getCurrentYearAndQuarter(getContext()));

        /*int[] datas = {16,5,7,2};
        pc_ecg_chart.setDatas(datas);*/


    }

    private void initData() {
        if (MyReportActivity.mQuarterFullReport!=null){
            Log.i(TAG,"MyReportActivity.mQuarterFullReport:"+MyReportActivity.mQuarterFullReport.toString());
            if (MyReportActivity.mQuarterFullReport!=null){
                float[] eCrep = MyReportActivity.mQuarterFullReport.ECrep;
                if (eCrep!=null && eCrep.length>0){
                    pc_ecg_chart.setDatas(eCrep);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();


    }
}
