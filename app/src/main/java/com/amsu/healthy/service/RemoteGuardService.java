package com.amsu.healthy.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.WakeLockUtil;

public class RemoteGuardService extends Service {

    public RemoteGuardService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    boolean isStartCommand;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isStartCommand){
            testNewThread();
            WakeLockUtil.acquireWakeLock(this);
            isStartCommand = true;
        }

        return START_STICKY;
    }

    private void testNewThread() {
        MyUtil.startAllService(RemoteGuardService.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("com.amsu.healthy.servicedestroy");
        sendBroadcast(intent);


        WakeLockUtil.releaseWakeLock();
    }
}
