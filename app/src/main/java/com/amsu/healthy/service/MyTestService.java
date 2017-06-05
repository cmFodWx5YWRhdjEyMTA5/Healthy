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
import android.support.annotation.IntDef;
import android.util.Log;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.HealthyDataActivity;
import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.activity.StartRunActivity;
import com.amsu.healthy.appication.MyApplication;

public class MyTestService extends Service {
    private static final String TAG = "MyTestService";

    public MyTestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("倾听体语")
                .setContentText("倾听体语正在运行")
                .setSmallIcon(R.drawable.logo_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo_icon))
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        //新建Intent，用在Activity传递数据，点击时跳到ShowArticleDetailActivity页面
        Intent intent1 = new Intent(this, MainActivity.class);
        /*if (MyApplication.runningActivity==MyApplication.MainActivity){
            intent1 = new Intent(this, MainActivity.class);
        }
        else if (MyApplication.runningActivity==MyApplication.HealthyDataActivity){
            intent1 = new Intent(this, HealthyDataActivity.class);
        }
        else if (MyApplication.runningActivity==MyApplication.StartRunActivity){
            intent1 = new Intent(this, StartRunActivity.class);
        }*/
        //给另一个设置任务栈属性，FLAG_ACTIVITY_NEW_TASK表示新建一个任务栈来显示当前的Activity
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        //PendingIntent 主要用于任务栏提醒和桌面weigetde 显示，

        //这里用4个参数需要注意下，130表示requestCode（请求马，自定义）
        //第三个参数书Intent对象，intent1是上面定义的 Intent对象
        //第四个对象是PendingIntent的标签属性，表叔显示方式，这里FLAG_UPDATE_CURRENT表示显示当前的通知，如果用新的通知时，更新当期的通知，这个属性注意下，如果不设置的话每次点击都是同一个通知
        PendingIntent activity = PendingIntent.getActivity(this, 130, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        //notification.contentIntent = activity;
        nm.notify(0, notification);

        testNewThread();
        return START_STICKY;
    }

    private void testNewThread() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1000);
                    System.out.println("test");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy");
        super.onDestroy();
        Intent intent = new Intent("com.amsu.healthy.servicedestroy");
        sendBroadcast(intent);
    }
}
