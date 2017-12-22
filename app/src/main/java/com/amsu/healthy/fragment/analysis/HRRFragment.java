package com.amsu.healthy.fragment.analysis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.HeartRateResultShowActivity;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.HealthyIndexUtil;

public class HRRFragment extends BaseFragment {

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


    }

    private void initData() {
        UploadRecord mUploadRecord = HeartRateResultShowActivity.mUploadRecord;
        Log.i(TAG,"mUploadRecord:"+mUploadRecord);
        if (mUploadRecord!=null && mUploadRecord.ra>0){
            Log.i(TAG,"mUploadRecord:"+mUploadRecord);
            tv_hrr_value.setText(mUploadRecord.ra+"");  //注意：此处的是恢复数值，不是分数

            IndicatorAssess indicatorAssess = HealthyIndexUtil.calculateScoreHRR(mUploadRecord.ra,getActivity());
            tv_hrr_suggestion.setText(indicatorAssess.getSuggestion());

        }
    }

}

