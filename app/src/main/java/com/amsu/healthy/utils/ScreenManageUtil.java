package com.amsu.healthy.utils;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.amsu.healthy.activity.LockScreenActivity;
import com.amsu.healthy.activity.insole.InsoleLockScreenActivity;
import com.amsu.healthy.appication.MyApplication;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.healthy.utils
 * @time 2017-12-21 3:51 PM
 * @describe
 */
public class ScreenManageUtil {
    private static final String TAG = ScreenManageUtil.class.getSimpleName();

    //监听屏幕锁屏，并启动自定义锁屏界面
    public static void stratListenScrrenBroadCast(Context context) {
        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);

        BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                Log.i(TAG,"onReceive:"+intent.getAction());

                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_SCREEN_OFF.equals(action)){
                    Log.i(TAG,"屏幕变化:");

                    KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
                    keyguardLock.disableKeyguard();

                    Intent i = new Intent();
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    if (Intent.ACTION_SCREEN_ON.equals(action)){
                        i.putExtra("isScroonOn",true);
                    }
                    else {
                        i.putExtra("isScroonOn",false);
                    }

                    int runningRecoverType = ((MyApplication) context.getApplicationContext()).getRunningRecoverType();
                    Log.i(TAG,"runningRecoverType:"+ runningRecoverType);

                    if (runningRecoverType>0){
                        if (runningRecoverType==Constant.sportType_Cloth){
                            i.setClass(context,LockScreenActivity.class);
                        }
                        else if (runningRecoverType==Constant.sportType_Insole){
                            i.setClass(context,InsoleLockScreenActivity.class);
                        }
                        context.startActivity(i);
                    }
                }
            }
        };
        context.registerReceiver(mBatInfoReceiver, filter);
    }

}
