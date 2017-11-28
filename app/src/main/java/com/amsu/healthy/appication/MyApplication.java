package com.amsu.healthy.appication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.WebSocketProxy;
import com.mob.MobSDK;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


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
    public static Set<String> insoleConnectedMacAddress = new HashSet<>();
    public static boolean mIsOutDoor = true;

    public String runningFormatDistance;
    public String runningFinalFormatSpeed;
    public Date runningCurrTimeDate;
    public String runningDate;

    public int runningmCurrentHeartRate;
    public int runningmCurrentStepCount;
    public String runningmCurrentAvespeed;

    public int runningRecoverType = -1;

    public String getRunningmCurrentAvespeed() {
        return runningmCurrentAvespeed;
    }

    public void setRunningmCurrentAvespeed(String runningmCurrentAvespeed) {
        this.runningmCurrentAvespeed = runningmCurrentAvespeed;
    }

    public WebSocketProxy webSocketUtil;
    public static int IndexWarringHeartIconType = -1;

    private int insoleLeftCurrBatteryPowerPercent = -1;
    private int insoleRightCurrBatteryPowerPercent = -1;

    @Override
    public void onCreate() {
        super.onCreate();

        //sharesdk短信
        //MobSDK.init(this, "1976143c3c888", "0c1784d4bf495891bf142767b314651c");
        MobSDK.init(this);

        sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        mActivities = new ArrayList<>();


        String currentProcessName = getCurrentProcessName(); //com.amsu.healthy
        Log.i(TAG,"currentProcessName:"+currentProcessName);

        if (currentProcessName.equals("com.amsu.healthy")){
            MyUtil.startServices(this);
            isNeedSynMsgToDevice = true;

            CrashReport.initCrashReport(this, "d139ea916b", false);   //腾讯Bugly

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

            insoleAccessToken = MyUtil.getStringValueFromSP("insoleAccessToken");
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


    public Date getRunningCurrTimeDate() {
        return runningCurrTimeDate;
    }

    public void setRunningCurrTimeDate(Date runningCurrTimeDate) {
        this.runningCurrTimeDate = runningCurrTimeDate;
    }

    public String getRunningFormatDistance() {
        return runningFormatDistance;
    }

    public void setRunningFormatDistance(String runningFormatDistance) {
        this.runningFormatDistance = runningFormatDistance;
    }

    public String getRunningFinalFormatSpeed() {
        return runningFinalFormatSpeed;
    }

    public void setRunningFinalFormatSpeed(String runningFinalFormatSpeed) {
        this.runningFinalFormatSpeed = runningFinalFormatSpeed;
    }

    public String getRunningDate() {
        return runningDate;
    }

    public void setRunningDate(String runningDate) {
        this.runningDate = runningDate;
    }

    public int getRunningmCurrentHeartRate() {
        return runningmCurrentHeartRate;
    }

    public void setRunningmCurrentHeartRate(int runningmCurrentHeartRate) {
        this.runningmCurrentHeartRate = runningmCurrentHeartRate;
    }

    public WebSocketProxy getWebSocketUtil() {
        return webSocketUtil;
    }

    public void setWebSocketUtil(WebSocketProxy webSocketUtil) {
        this.webSocketUtil = webSocketUtil;
    }

    public int getInsoleLeftCurrBatteryPowerPercent() {
        return insoleLeftCurrBatteryPowerPercent;
    }

    public void setInsoleLeftCurrBatteryPowerPercent(int insoleLeftCurrBatteryPowerPercent) {
        this.insoleLeftCurrBatteryPowerPercent = insoleLeftCurrBatteryPowerPercent;
    }

    public int getInsoleRightCurrBatteryPowerPercent() {
        return insoleRightCurrBatteryPowerPercent;
    }

    public void setInsoleRightCurrBatteryPowerPercent(int insoleRightCurrBatteryPowerPercent) {
        this.insoleRightCurrBatteryPowerPercent = insoleRightCurrBatteryPowerPercent;
    }



    public int getRunningmCurrentStepCount() {
        return runningmCurrentStepCount;
    }

    public void setRunningmCurrentStepCount(int runningmCurrentStepCount) {
        this.runningmCurrentStepCount = runningmCurrentStepCount;
    }



    public int getRunningRecoverType() {
        return runningRecoverType;
    }

    public void setRunningRecoverType(int runningRecoverType) {
        this.runningRecoverType = runningRecoverType;
    }
}
