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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.amsu.healthy.R;
import com.amsu.healthy.activity.HistoryRecordActivity;
import com.amsu.healthy.activity.MyReportActivity;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;

public class HRVFragment extends Fragment {

    private static final String TAG = "HRVFragment";
    private View inflate;
    private TextView tv_hrv_suggestion;
    private ProgressBar pb_hrv_tired;
    private ImageView iv_hrv_tired;
    private ImageView iv_hrv_resist;
    private ImageView iv_hrv_mood;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_hrv, null);
        initView();
        initData();

        return inflate;
    }

    private void initView() {
        iv_hrv_tired = (ImageView) inflate.findViewById(R.id.iv_hrv_tired);
        iv_hrv_resist = (ImageView) inflate.findViewById(R.id.iv_hrv_resist);
        iv_hrv_mood = (ImageView) inflate.findViewById(R.id.iv_hrv_mood);

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
        float progressWidth = MyUtil.getScreeenWidth(getActivity()) - 2 * getResources().getDimension(R.dimen.x12);
        String suggestion = "";
        IndicatorAssess lFHFMood = HealthyIndexUtil.calculateLFHFMoodIndex();
        IndicatorAssess sDNNPressure = HealthyIndexUtil.calculateSDNNPressureIndex();
        IndicatorAssess sDNNSport = HealthyIndexUtil.calculateSDNNSportIndex();

        LinearLayout.LayoutParams layoutParams =   new LinearLayout.LayoutParams(iv_hrv_tired.getLayoutParams());
        if (lFHFMood!=null){
           int scre = lFHFMood.getPercent();
            int v = (int) ((scre / 100.0) * progressWidth);
            Log.i(TAG,"v:"+v);
            layoutParams.setMargins(v, (int) -getResources().getDimension(R.dimen.x23),0,0);
            iv_hrv_tired.setLayoutParams(layoutParams);
            suggestion += lFHFMood.getSuggestion();
        }

        if (sDNNPressure!=null){
            int scre = sDNNPressure.getPercent();
            int v = (int) ((scre / 100.0) * progressWidth);
            Log.i(TAG,"v:"+v);
            layoutParams.setMargins(v, (int) -getResources().getDimension(R.dimen.x23),0,0);
            iv_hrv_resist.setLayoutParams(layoutParams);
        }

        if (sDNNSport!=null){
            int scre = sDNNSport.getPercent();
            int v = (int) ((scre / 100.0) * progressWidth);
            Log.i(TAG,"v:"+v);
            layoutParams.setMargins(v, (int) -getResources().getDimension(R.dimen.x23),0,0);
            iv_hrv_resist.setLayoutParams(layoutParams);
            suggestion += sDNNSport.getSuggestion();
        }

        tv_hrv_suggestion.setText(suggestion);


        //LF=1075.28, HF=431.35  测试

    }
}
