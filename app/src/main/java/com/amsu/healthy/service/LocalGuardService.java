package com.amsu.healthy.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.WakeLockUtil;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LocalGuardService extends JobService {
    private static final String TAG = "LocalGuardService";

    public LocalGuardService() {
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
        MyUtil.scheduleService(this,2,LocalGuardService.class.getName());
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    boolean isStartCommand;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");
        if (!isStartCommand){
            WakeLockUtil.acquireWakeLock(this);
            isStartCommand = true;
            MyUtil.scheduleService(this,2,LocalGuardService.class.getName());
        }
        return START_STICKY;
    }

    private void testNewThread() {
        MyUtil.startAllService(LocalGuardService.this);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy");
        super.onDestroy();
        Intent intent = new Intent("com.amsu.healthy.servicedestroy");
        sendBroadcast(intent);
        WakeLockUtil.releaseWakeLock();
    }
}
