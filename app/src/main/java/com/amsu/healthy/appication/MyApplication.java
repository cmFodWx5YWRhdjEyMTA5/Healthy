package com.amsu.healthy.appication;

import android.app.Application;

import com.amsu.healthy.utils.LocationService;
import com.baidu.mapapi.SDKInitializer;

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

        locationService = new LocationService(this);

    }
}
