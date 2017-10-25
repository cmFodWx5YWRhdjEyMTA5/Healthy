package com.amsu.healthy.fragment.marathon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.R;
import com.amsu.healthy.fragment.BaseFragment;

/**
 * author：WangLei
 * date:2017/10/25.
 * QQ:619321796
 * 马拉松运动记录详情 配速
 */

public class SportRecordSpeedFragment extends BaseFragment {
    public static SportRecordSpeedFragment newInstance() {
        return new SportRecordSpeedFragment();
    }

    private View inflate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_sport_record_speed, null);
        initView();
        return inflate;
    }

    private void initView() {

    }
}
