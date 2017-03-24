package com.amsu.healthy.appication;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import com.amsu.healthy.utils.LocationService;
import com.baidu.mapapi.SDKInitializer;

import org.apache.http.client.CookieStore;

import java.util.ArrayList;
import java.util.List;

import cn.smssdk.SMSSDK;

/**
 * Created by HP on 2016/11/23.
 */
public class MyApplication extends Application{

    public static LocationService locationService;
    public static SharedPreferences sharedPreferences;
    public static List<Activity> mActivities;


    @Override
    public void onCreate() {
        super.onCreate();

        //百度地图
        SDKInitializer.initialize(getApplicationContext());

        //sharesdk短信
        SMSSDK.initSDK(this, "1976143c3c888", "0c1784d4bf495891bf142767b314651c");

        locationService = new LocationService(this);

        sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        mActivities = new ArrayList<>();

    }
}
