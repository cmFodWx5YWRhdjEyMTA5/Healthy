package com.amsu.healthy.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.HealthyDataActivity;
import com.amsu.healthy.activity.StartRunActivity;
import com.amsu.healthy.activity.insole.InsoleRunningActivity;
import com.amsu.healthy.appication.MyApplication;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.utils
 * @time 12/6/2017 5:44 PM
 * @describe
 */
public class ShowNotificationBarUtil {
    public static final int notifyActivityIndex_StartRunActivity = 1;
    public static final int notifyActivityIndex_HealthyDataActivity = 2;
    public static final int notifyActivityIndex_InsoleRunningActivity = 3;


    public static void setServiceForegrounByNotify(String title ,String content,int state) {
        Service context  = MyApplication.getInstance().mCoreService ;
        if (context!=null){
            //NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.logo_icon)
                    .setOngoing(true)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_icon))
                    .build();
            notification.flags |= Notification.FLAG_NO_CLEAR;

            //新建Intent，用在Activity传递数据，点击时跳到ShowArticleDetailActivity页面

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



            //PendingIntent 主要用于任务栏提醒和桌面weigetde 显示，

            //这里用4个参数需要注意下，130表示requestCode（请求马，自定义）
            //第三个参数书Intent对象，intent1是上面定义的 Intent对象
            //第四个对象是PendingIntent的标签属性，表叔显示方式，这里FLAG_UPDATE_CURRENT表示显示当前的通知，如果用新的通知时，更新当期的通知，这个属性注意下，如果不设置的话每次点击都是同一个通知

            if (state==notifyActivityIndex_StartRunActivity){
                Intent intent1 = new Intent(context, StartRunActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent activity = PendingIntent.getActivity(context, 130, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.contentIntent = activity;
            }
            else if (state==notifyActivityIndex_HealthyDataActivity){
                Intent intent1 = new Intent(context, HealthyDataActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent activity = PendingIntent.getActivity(context, 131, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.contentIntent = activity;
            }
            else if (state==notifyActivityIndex_InsoleRunningActivity){
                Intent intent1 = new Intent(context, InsoleRunningActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent activity = PendingIntent.getActivity(context, 131, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.contentIntent = activity;
            }
            //nm.notify(0, notification);
            context.startForeground(1, notification); //将Service设置为前台服务
        }

    }

    public static void  detoryServiceForegrounByNotify(){
        Service context  = MyApplication.getInstance().mCoreService ;
        if (context!=null){
            context.stopForeground(true);
        }
    }


}
