package com.amsu.healthy.fragment.analysis_insole;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.R;
import com.amsu.healthy.view.CircleRingView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultStrideFragment extends Fragment {


    private View inflate;
    private CircleRingView vr_strid_symmetry;
    private CircleRingView vr_strid_variability;

    public ResultStrideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_result_stride, container, false);
        initView();
        return inflate;
    }

    private void initView() {
        vr_strid_symmetry = (CircleRingView)inflate.findViewById(R.id.vr_strid_symmetry);
        vr_strid_variability = (CircleRingView)inflate.findViewById(R.id.vr_strid_variability);
    }

    @Override
    public void onResume() {
        super.onResume();
        vr_strid_symmetry.setValue(320);
        vr_strid_variability.setValue(80);
    }

}
