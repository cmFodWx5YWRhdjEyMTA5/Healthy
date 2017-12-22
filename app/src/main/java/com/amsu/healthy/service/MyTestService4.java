package com.amsu.healthy.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.amsu.healthy.utils.MyUtil;

public class MyTestService4 extends Service {

    private PowerManager.WakeLock wakeLock;

    public MyTestService4() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, CoreService.class.getName());
        wakeLock.acquire();
        //Log.i(TAG,"锁屏激活");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        testNewThread();

        return START_STICKY;
    }

    private void testNewThread() {
        MyUtil.startAllService(MyTestService4.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       /* Intent intent = new Intent("com.amsu.healthy.servicedestroy");
        sendBroadcast(intent);*/

        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
