package com.amsu.healthy.appication;

import android.app.Application;

import com.amsu.healthy.utils.LocationService;
import com.baidu.mapapi.SDKInitializer;

import cn.smssdk.SMSSDK;

/**
 * Created by HP on 2016/11/23.
 */
public class MyApplication extends Application{

    public static LocationService locationService;

    @Override
    public void onCreate() {
        super.onCreate();

        //百度地图
        SDKInitializer.initialize(getApplicationContext());

        //sharesdk短信
        SMSSDK.initSDK(this, "19729c0c696ad", "f5b9fe28ae503f2d7f9afc92e7515223");

        locationService = new LocationService(this);

    }
}
