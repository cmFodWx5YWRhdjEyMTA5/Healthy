package com.amsu.healthy.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.WakeLockUtil;

public class LocalGuardService extends Service {
    private static final String TAG = "LocalGuardService";

    public LocalGuardService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
            testNewThread();
            WakeLockUtil.acquireWakeLock(this);
            isStartCommand = true;
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
