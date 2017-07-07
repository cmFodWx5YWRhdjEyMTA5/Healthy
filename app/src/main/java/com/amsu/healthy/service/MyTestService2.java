package com.amsu.healthy.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.WakeLockUtil;
import com.ble.ble.BleService;

import java.util.Date;

public class MyTestService2 extends Service {
    private static final String TAG = "MyTestService2";

    public MyTestService2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
            testNewThread();
            WakeLockUtil.acquireWakeLock(this);
            isStartCommand = true;
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
