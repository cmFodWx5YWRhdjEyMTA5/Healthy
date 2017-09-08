package com.amsu.healthy.fragment.analysis_insole;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.healthy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultDetailsFragment extends Fragment {


    private View inflate;

    public ResultDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_result_details, container, false);
        initView();
        return inflate;
    }

    private void initView() {
        TextView tv_detail_mileage = (TextView) inflate.findViewById(R.id.tv_detail_mileage);
        TextView tv_detail_time = (TextView) inflate.findViewById(R.id.tv_detail_time);
        TextView tv_detail_stride = (TextView) inflate.findViewById(R.id.tv_detail_stride);
        TextView tv_detail_avespeed = (TextView) inflate.findViewById(R.id.tv_detail_avespeed);
        TextView tv_detail_freqstride = (TextView) inflate.findViewById(R.id.tv_detail_freqstride);
        TextView tv_detail_maxspeed = (TextView) inflate.findViewById(R.id.tv_detail_maxspeed);
        TextView tv_detail_minspeed = (TextView) inflate.findViewById(R.id.tv_detail_minspeed);
        TextView tv_detail_kcal = (TextView) inflate.findViewById(R.id.tv_detail_kcal);
        TextView tv_detail_reachway = (TextView) inflate.findViewById(R.id.tv_detail_reachway);
        TextView tv_detail_outturnver = (TextView) inflate.findViewById(R.id.tv_detail_outturnver);
        TextView tv_detail_singlestable = (TextView) inflate.findViewById(R.id.tv_detail_singlestable);
    }

}
