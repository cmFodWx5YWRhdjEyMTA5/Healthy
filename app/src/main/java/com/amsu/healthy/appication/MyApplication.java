package com.amsu.healthy.appication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.activity.RateAnalysisActivity;
import com.amsu.healthy.service.MyTestService;
import com.amsu.healthy.service.MyTestService2;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.OffLineDbAdapter;
import com.ble.ble.BleService;

import java.util.ArrayList;
import java.util.List;

import cn.smssdk.SMSSDK;
import im.fir.sdk.FIR;

/**
 * Created by HP on 2016/11/23.
 */
public class MyApplication extends Application{

    private static final String TAG = "MyApplication";
    public static SharedPreferences sharedPreferences;
    public static List<Activity> mActivities;
    public static boolean isHaveDeviceConnectted = false;
    public static BaseActivity mCurrApplicationActivity;
    public static boolean isBlueServiceWorked;
    public static final int MainActivity = 1;
    public static final int HealthyDataActivity = 2;
    public static final int StartRunActivity = 3;
    public static int runningActivity = 1;
    public static int calCuelectricVPercent = -1;
    public static int currentHeartRate;
    public static String connectedMacAddress;

    @Override
    public void onCreate() {
        super.onCreate();


        //sharesdk短信
        SMSSDK.initSDK(this, "1976143c3c888", "0c1784d4bf495891bf142767b314651c");

        sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        mActivities = new ArrayList<>();
        FIR.init(this);

        String currentProcessName = getCurrentProcessName(); //com.amsu.healthy
        Log.i(TAG,"currentProcessName:"+currentProcessName);

        if (currentProcessName.equals("com.amsu.healthy")){
            /*Intent intent = new Intent();
            intent.setClass(this, MyTestService.class);
            startService(intent);

            Intent intent2 = new Intent();
            intent2.setClass(this, MyTestService2.class);
            startService(intent2);

            Intent intent3 = new Intent();
            intent3.setClass(this, BleService.class);
            startService(intent3);*/

            MyUtil.startAllService(this);
        }


    }

    private String getCurrentProcessName() {
        String currentProcName = "";
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                currentProcName = processInfo.processName;
                break;
            }
        }
        return currentProcName;
    }
}
