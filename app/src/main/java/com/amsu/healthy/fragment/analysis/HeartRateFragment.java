package com.amsu.healthy.fragment.analysis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.HistoryRecordActivity;
import com.amsu.healthy.activity.MyReportActivity;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.FoldLineView;

import java.util.List;

public class HeartRateFragment extends Fragment {

    private View inflate;
    private FoldLineView fv_rate_line;
    private TextView tv_rate_max;
    private TextView tv_rate_average;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_heart_rate, null);
        initView();
        initData();
        return inflate;
    }

    private void initView() {
        fv_rate_line = (FoldLineView) inflate.findViewById(R.id.fv_rate_line);
        tv_rate_max = (TextView) inflate.findViewById(R.id.tv_rate_max);
        tv_rate_average = (TextView) inflate.findViewById(R.id.tv_rate_average);

        Button bt_hrv_history = (Button) inflate.findViewById(R.id.bt_hrv_history);
        Button bt_hrv_myreport = (Button) inflate.findViewById(R.id.bt_hrv_myreport);

        bt_hrv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HistoryRecordActivity.class));
            }
        });
        bt_hrv_myreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyReportActivity.class));
            }
        });


    }

    private void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();

        int[] datas = MyUtil.getHeartRateListFromSP();
        //int[] datas = new int[]{65,66,54,73,71,68,77,55,56,93,65,68,64,62,61,64,67,66,40,70,65};  //测试
        if (datas!=null && datas.length>0){
            int max  = datas[0];
            int average = 0;
            int sum = 0;
            for (int i=0;i<datas.length;i++){
                if (max<datas[i]){
                    max = datas[i];
                }
                sum += datas[i];
            }
            average = sum/datas.length;
            fv_rate_line.setData(datas,max);
            tv_rate_max.setText(max+"");
            tv_rate_average.setText(average+"");
        }


    }
}
