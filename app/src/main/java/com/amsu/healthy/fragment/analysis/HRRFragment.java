package com.amsu.healthy.fragment.analysis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.HistoryRecordActivity;
import com.amsu.healthy.activity.MyReportActivity;
import com.amsu.healthy.activity.RateAnalysisActivity;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.utils.HealthyIndexUtil;

public class HRRFragment extends Fragment {

    private static final String TAG = "HRRFragment";
    private View inflate;
    private TextView tv_hrr_value;
    private TextView tv_hrr_state;
    private TextView tv_hrr_suggestion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_hrr, null);

        initView();
        initData();
        return inflate;
    }

    private void initView() {
        tv_hrr_value = (TextView) inflate.findViewById(R.id.tv_hrr_value);
        //tv_hrr_state = (TextView) inflate.findViewById(R.id.tv_hrr_state);
        tv_hrr_suggestion = (TextView) inflate.findViewById(R.id.tv_hrr_suggestion);
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
        UploadRecord mUploadRecord = RateAnalysisActivity.mUploadRecord;
        Log.i(TAG,"mUploadRecord:"+mUploadRecord);
        if (mUploadRecord!=null){
            Log.i(TAG,"mUploadRecord:"+mUploadRecord);
            if (mUploadRecord.RA.equals("0")){
                tv_hrr_value.setText("--");
            }
            else {
                tv_hrr_value.setText(mUploadRecord.RA);  //注意：此处的是恢复数值，不是分数
            }
            tv_hrr_suggestion.setText(mUploadRecord.HRs);
        }
    }


}
