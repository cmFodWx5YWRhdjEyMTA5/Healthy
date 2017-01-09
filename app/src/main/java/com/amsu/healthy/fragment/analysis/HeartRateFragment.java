package com.amsu.healthy.fragment.analysis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.R;
import com.amsu.healthy.view.FoldLineView;

public class HeartRateFragment extends Fragment {

    private View inflate;
    private FoldLineView fv_rate_line;

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



    }

    private void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        int[] datas = {67,99,78,77,55,80,81};  //心率数据
        int max  = 100;  //心率的最大值，待定，需要根据实际情况调整（曲线的调整）
        fv_rate_line.setData(datas,max);
    }
}
