package com.amsu.healthy.appication;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import cn.smssdk.SMSSDK;

/**
 * Created by HP on 2016/11/23.
 */
public class MyApplication extends Application{

    public static SharedPreferences sharedPreferences;
    public static List<Activity> mActivities;


    @Override
    public void onCreate() {
        super.onCreate();


        //sharesdk短信
        SMSSDK.initSDK(this, "1976143c3c888", "0c1784d4bf495891bf142767b314651c");

        sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        mActivities = new ArrayList<>();

    }
}
