package com.amsu.healthy.fragment.analysis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.amsu.healthy.R;
import com.amsu.healthy.activity.HistoryRecordActivity;
import com.amsu.healthy.activity.MyReportActivity;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.utils.HealthyIndexUtil;

public class HRVFragment extends Fragment {

    private View inflate;
    private TextView tv_hrv_suggestion;
    private ProgressBar pb_hrv_tired;
    private ProgressBar pb_hrv_mood;
    private ProgressBar pb_hrv_resist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_hrv, null);
        initView();
        initData();

        return inflate;
    }

    private void initView() {
        pb_hrv_tired = (ProgressBar) inflate.findViewById(R.id.pb_hrv_tired);
        pb_hrv_mood = (ProgressBar) inflate.findViewById(R.id.pb_hrv_mood);
        pb_hrv_resist = (ProgressBar) inflate.findViewById(R.id.pb_hrv_resist);

        tv_hrv_suggestion = (TextView) inflate.findViewById(R.id.tv_hrv_suggestion);
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
        String suggestion = "";
        IndicatorAssess calculateMoodIndex = HealthyIndexUtil.calculateMoodBySDNNIndex();
        IndicatorAssess calculateSDNNIndex = HealthyIndexUtil.calculateSDNNIndex();
        IndicatorAssess calculateSDNNIndex1 = HealthyIndexUtil.calculateSDNNIndex1();
        if (calculateMoodIndex!=null){
            int scre = calculateMoodIndex.getScre();
            pb_hrv_mood.setProgress(scre);
            suggestion += calculateMoodIndex.getSuggestion();
        }

        if (calculateSDNNIndex!=null){
            int scre = calculateSDNNIndex.getScre();
            //scre =30;
            pb_hrv_resist.setProgress(scre);
        }

        if (calculateSDNNIndex1!=null){
            int scre = calculateSDNNIndex1.getScre();
            pb_hrv_tired.setProgress(scre);

            suggestion += calculateSDNNIndex1.getSuggestion();
        }

        tv_hrv_suggestion.setText(suggestion);




    }
}
