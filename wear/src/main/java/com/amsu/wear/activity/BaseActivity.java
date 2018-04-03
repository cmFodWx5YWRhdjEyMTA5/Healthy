package com.amsu.wear.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import butterknife.ButterKnife;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.activity
 * @time 2018-03-08 3:46 PM
 * @describe
 */
public abstract class BaseActivity extends FragmentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(attachLayoutRes());
        ButterKnife.bind(this);
    }

    protected abstract int attachLayoutRes();
}
