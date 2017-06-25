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
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.PieChart;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EcgMouthFragment extends BaseFragment {


    private static final String TAG = "EcgYearFragment";
    private PieChart pc_ecg_chart;
    private View inflate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflate = inflater.inflate(R.layout.fragment_ecg_mouth, container, false);
        initView();

        return inflate;
    }

    private void initView() {

        pc_ecg_chart = (PieChart) inflate.findViewById(R.id.pc_ecg_chart);

        TextView tv_ecgmouth_date = (TextView) inflate.findViewById(R.id.tv_ecgmouth_date);
        tv_ecgmouth_date.setText(MyUtil.getCurrentYearAndMouthr());

        /*int[] datas = {16,5,7,2};
        pc_ecg_chart.setDatas(datas);*/


    }

    private void initData() {
        if (MyReportActivity.mMouthFullReport!=null){
            if (MyReportActivity.mMouthFullReport!=null){
                float[] eCrep = MyReportActivity.mMouthFullReport.ECrep;
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
