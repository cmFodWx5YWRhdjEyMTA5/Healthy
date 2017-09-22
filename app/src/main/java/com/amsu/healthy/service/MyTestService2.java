package com.amsu.healthy.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.WakeLockUtil;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyTestService2 extends JobService {
    private static final String TAG = "MyTestService2";

    public MyTestService2() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG,"onStartJob");
        MyUtil.startServices(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG,"onStopJob");
        MyUtil.scheduleService(this,3,MyTestService2.class.getName());
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"锁屏激活");
    }

    boolean isStartCommand;
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        if (!isStartCommand){
            WakeLockUtil.acquireWakeLock(this);
            isStartCommand = true;
            //MyUtil.scheduleService(this,3,MyTestService2.class.getName());
        }
        return START_STICKY;
    }

    private void testNewThread() {
        MyUtil.startAllService(MyTestService2.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("com.amsu.healthy.servicedestroy");
        sendBroadcast(intent);

        WakeLockUtil.releaseWakeLock();
    }
}
