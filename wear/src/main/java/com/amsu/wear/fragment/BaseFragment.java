package com.amsu.wear.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.fragment
 * @time 2018-03-08 3:47 PM
 * @describe
 */
public abstract class BaseFragment extends Fragment {
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(attachLayoutRes(), null);
            ButterKnife.bind(this, mRootView);
        }
        return mRootView;
    }

    protected abstract int attachLayoutRes();
}
