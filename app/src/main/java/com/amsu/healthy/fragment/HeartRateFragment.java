package com.amsu.healthy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.healthy.R;

public class HeartRateFragment extends Fragment {

    private View inflate;
    private TextView tv_rate_rate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_heart_rate, null);
        initView();
        initData();
        return inflate;
    }

    private void initView() {
        tv_rate_rate = (TextView) inflate.findViewById(R.id.tv_rate_rate);
    }

    private void initData() {
        Intent intent = getActivity().getIntent();
        int ecgRate = intent.getIntExtra("ecgRate",0);

        tv_rate_rate.setText(ecgRate+"");

    }


}
