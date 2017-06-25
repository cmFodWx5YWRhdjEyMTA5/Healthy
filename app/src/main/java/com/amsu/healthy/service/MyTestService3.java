package com.amsu.healthy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;

import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;

import java.util.Date;

public class MyTestService3 extends Service {
    public MyTestService3() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        testNewThread();
        return START_STICKY;
    }

    private void testNewThread() {
        MyUtil.startAllService(MyTestService3.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("com.amsu.healthy.servicedestroy");
        sendBroadcast(intent);
    }
}
