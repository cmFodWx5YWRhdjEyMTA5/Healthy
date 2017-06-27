package com.amsu.healthy.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.IntDef;
import android.util.Log;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.HealthyDataActivity;
import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.activity.StartRunActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.ble.ble.BleService;

import java.util.Date;

public class MyTestService extends Service {
    private static final String TAG = "MyTestService";
    private PowerManager.WakeLock wakeLock;

    public MyTestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, CommunicateToBleService.class.getName());
        wakeLock.acquire();
        Log.i(TAG,"锁屏激活");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");
        testNewThread();
        return START_STICKY;
    }



    private void testNewThread() {
        MyUtil.startAllService(MyTestService.this);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy");
        super.onDestroy();

    }
}
