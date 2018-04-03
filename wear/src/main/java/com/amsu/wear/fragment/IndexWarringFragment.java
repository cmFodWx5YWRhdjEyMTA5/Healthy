package com.amsu.wear.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.wear.R;
import com.amsu.wear.activity.WeekIndexDataActivity;
import com.amsu.wear.util.LogUtil;
import com.amsu.wear.util.SPUtil;
import com.amsu.wear.view.CircleRingView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndexWarringFragment extends BaseFragment {
    private static final String TAG = IndexWarringFragment.class.getSimpleName();
    @BindView(R.id.vr_strid_symmetry)
    CircleRingView vr_strid_symmetry;
    @BindView(R.id.tv_warring_value)
    TextView tv_warring_value;

    public IndexWarringFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.i(TAG,"onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.i(TAG,"onViewCreated");
        init();
    }

    private void init() {

    }

    @Override
    public void onResume() {
        LogUtil.i(TAG,"onResume");
        super.onResume();
        int healthyIindexvalue = SPUtil.getIntValueFromSP("healthyIindexvalue");
        if (healthyIindexvalue>0) {
            vr_strid_symmetry.setValue(healthyIindexvalue);
            tv_warring_value.setText(healthyIindexvalue+"");
        }
    }

    @OnClick(R.id.rl_warring_layout)
    public void onViewClicked(View view) {
        startActivity(new Intent(getActivity(), WeekIndexDataActivity.class));
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_index_warring;
    }

}
