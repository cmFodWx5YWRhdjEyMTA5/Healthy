package com.amsu.healthy.appication;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.amsu.healthy.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import cn.smssdk.SMSSDK;
import im.fir.sdk.FIR;

/**
 * Created by HP on 2016/11/23.
 */
public class MyApplication extends Application{

    public static SharedPreferences sharedPreferences;
    public static List<Activity> mActivities;
    public static boolean isHaveDeviceConnectted = false;
    public static BaseActivity mApplicationActivity;
    public static boolean isBlueServiceWorked;
    public static final int MainActivity = 1;
    public static final int HealthyDataActivity = 2;
    public static final int StartRunActivity = 3;
    public static int runningActivity = 1;

    @Override
    public void onCreate() {
        super.onCreate();


        //sharesdk短信
        SMSSDK.initSDK(this, "1976143c3c888", "0c1784d4bf495891bf142767b314651c");

        sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        mActivities = new ArrayList<>();


        FIR.init(this);
    }
}
