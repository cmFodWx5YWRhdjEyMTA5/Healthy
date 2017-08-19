package com.amsu.healthy.appication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.fragment.inoutdoortype.OutDoorRunFragment;
import com.amsu.healthy.fragment.inoutdoortype.OutDoorRunGoogleFragment;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.service.LocalGuardService;
import com.amsu.healthy.service.MyTestService2;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.smssdk.SMSSDK;

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
    public static int clothCurrBatteryPowerPercent = -1;
    public static int currentHeartRate;
    public static String clothConnectedMacAddress;
    private Handler handler;
    public static boolean isNeedSynMsgToDevice;
    public static String insoleAccessToken;
    public static int deivceType = Constant.sportType_Cloth;
    public static int languageType = 1;
    public static int language_ch = 1;  //中文
    public static int language_en = 2;  //英文
    public static Context appContext ;

    @Override
    public void onCreate() {
        super.onCreate();




        //sharesdk短信
        SMSSDK.initSDK(this, "1976143c3c888", "0c1784d4bf495891bf142767b314651c");

        sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        mActivities = new ArrayList<>();


        String currentProcessName = getCurrentProcessName(); //com.amsu.healthy
        Log.i(TAG,"currentProcessName:"+currentProcessName);

        if (currentProcessName.equals("com.amsu.healthy")){
            MyUtil.startServices(this);
            isNeedSynMsgToDevice = true;

            CrashReport.initCrashReport(getApplicationContext(), "d139ea916b", false);   //腾讯Bugly

            int type = MyUtil.getIntValueFromSP(Constant.sportType);

            if (type==Constant.sportType_Cloth){
               deivceType = Constant.sportType_Cloth;
            }
            else if (type==Constant.sportType_Insole){
                deivceType = Constant.sportType_Insole;
            }

            String country = Locale.getDefault().getCountry();
            Log.i(TAG,"country:"+country);Locale.CHINA.getCountry();
            if(country.equals(Locale.CHINA.getCountry())){
                //中国
                languageType = 1;
            }
            else {
                //国外
                languageType = 2;
            }

            appContext = getApplicationContext();
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
