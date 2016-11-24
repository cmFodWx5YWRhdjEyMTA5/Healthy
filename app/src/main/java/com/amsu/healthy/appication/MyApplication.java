package com.amsu.healthy.appication;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by HP on 2016/11/23.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());

    }
}
