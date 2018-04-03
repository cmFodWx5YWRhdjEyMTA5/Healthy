package com.amsu.wear.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.amsu.wear.service.CoreService;
import com.amsu.wear.util.LogUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.xutils.x;

/**
 * @anthor haijun
 * @project name: Shop
 * @class name：com.haijun.shop.application
 * @time 2018-01-31 6:18 PM
 * @describe
 */
public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    private static Context mContext;
    private static MyApplication myApplication;
    private boolean isRunning;

    @Override
    public void onCreate() {
        super.onCreate();


        String currentProcessName = getCurrentProcessName(); //com.amsu.healthy
        LogUtil.i(TAG,"currentProcessName:"+currentProcessName);

        if (currentProcessName.equals("com.amsu.wear")){
            init();
        }
    }

    private void init() {
        x.Ext.init(this);  //xUtil初始化

        mContext = getApplicationContext();
        CrashReport.initCrashReport(getApplicationContext(), "710960387e", false);

        Intent service = new Intent(this, CoreService.class);
        startService(service);
    }

    public static Context getContext(){
        return mContext;
    }

    public static MyApplication getInstance(){
        if (myApplication==null){
            myApplication = new MyApplication();
        }
        return myApplication;
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

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
