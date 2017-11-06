package com.amsu.healthy.fragment.marathon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.R;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.view.HeightCurveView;

/**
 * author：WangLei
 * date:2017/10/31.
 * QQ:619321796
 */

public class SportRecordStatisticsItem_1 extends BaseFragment {
    public static SportRecordStatisticsItem_1 newInstance() {
        return new SportRecordStatisticsItem_1();
    }

    private View mView;
    private HeightCurveView hv_HeightCurveView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_sport_record_statistics_item_1, null);
        }
        ViewGroup parent = (ViewGroup) mView.getParent();
        if (parent != null) {
            parent.removeView(mView);
        }
        initView();
        return mView;
    }

    private void initView() {
        hv_HeightCurveView = (HeightCurveView) mView.findViewById(R.id.hv_HeightCurveView);
        setData(stepData,time);
    }

    int[] stepData;
    int time;

    public void setData(int[] stepData, int time) {
        this.stepData = stepData;
        this.time = time;
        if (hv_HeightCurveView != null) {
            hv_HeightCurveView.setData(stepData, time, HeightCurveView.LINETYPE_SETP);
        }
    }
}
