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
import com.amsu.healthy.activity.RateAnalysisActivity;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;

public class HRVFragment extends BaseFragment {

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
        int progressWidth = (int) (MyUtil.getScreeenWidth(getActivity()) - 2 * getResources().getDimension(R.dimen.x12));
        UploadRecord mUploadRecord = RateAnalysisActivity.mUploadRecord;
        Log.i(TAG,"mUploadRecord:"+mUploadRecord);
        if (mUploadRecord!=null){
            if (!MyUtil.isEmpty(mUploadRecord.FI) && !MyUtil.isEmpty(mUploadRecord.PI) && !MyUtil.isEmpty(mUploadRecord.ES) && !mUploadRecord.FI.equals(Constant.uploadRecordDefaultString)){

                int FI = Integer.parseInt(mUploadRecord.FI);//运动疲劳
                int PI = Integer.parseInt(mUploadRecord.PI);//抗压指数
                int ES = Integer.parseInt(mUploadRecord.ES);//情绪指数

                Log.i(TAG,"FI:"+FI+",PI:"+PI+",ES:"+ES);

                IndicatorAssess ESIndicatorAssess = HealthyIndexUtil.calculateSDNNSportIndex(FI);
                int FINeed = ESIndicatorAssess.getPercent();
                IndicatorAssess PIIndicatorAssess = HealthyIndexUtil.calculateSDNNPressureIndex(PI);
                int PINeed = PIIndicatorAssess.getPercent();
                IndicatorAssess FIIndicatorAssess = HealthyIndexUtil.calculateLFHFMoodIndex(ES);
                int ESNeed = FIIndicatorAssess.getPercent();


                LinearLayout.LayoutParams tiredLayoutParams =   new LinearLayout.LayoutParams(iv_hrv_tired.getLayoutParams());
                tiredLayoutParams.setMargins((int) ((FINeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                iv_hrv_tired.setLayoutParams(tiredLayoutParams);

                LinearLayout.LayoutParams resistLayoutParams =   new LinearLayout.LayoutParams(iv_hrv_tired.getLayoutParams());
                resistLayoutParams.setMargins((int) ((PINeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                iv_hrv_resist.setLayoutParams(resistLayoutParams);

                LinearLayout.LayoutParams moodLayoutParams =   new LinearLayout.LayoutParams(iv_hrv_tired.getLayoutParams());
                moodLayoutParams.setMargins((int) ((ESNeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                iv_hrv_mood.setLayoutParams(moodLayoutParams);

                String HRVs = ESIndicatorAssess.getSuggestion()+PIIndicatorAssess.getSuggestion()+FIIndicatorAssess.getSuggestion();
                Log.i(TAG,"HRVs:"+HRVs);

                if (!MyUtil.isEmpty( mUploadRecord.HRVs)){
                    tv_hrv_suggestion.setText(mUploadRecord.HRVs);
                }


            }
        }
    }
}
