package com.amsu.healthy.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.activity.StartRunActivity;
import com.amsu.healthy.activity.insole.InsoleRunningActivity;
import com.amsu.healthy.appication.MyApplication;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.healthy.utils
 * @time 2017-12-21 4:50 PM
 * @describe
 */
public class SportRecoveryUtil {

    private static final String TAG = SportRecoveryUtil.class.getSimpleName();

    //判断是否要恢复到之前的运动状态
    public static int judgeRecoverRunState(Context context) {
        Log.i(TAG,"judgeRecoverRunState:"+context.toString());
        int sportRunningType = MyUtil.getIntValueFromSP(Constant.sportRunningType);
        boolean curRecoverRunningActivityStarted = ((MyApplication) context.getApplicationContext()).isCurRecoverRunningActivityStarted();
        if (sportRunningType!=-1 ){
            Intent intent1 = new Intent();
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra(Constant.isNeedRecoverAbortData,true);
            if (sportRunningType==Constant.sportType_Cloth){
                intent1.setClass(context,StartRunActivity.class);
            }
            else if (sportRunningType==Constant.sportType_Insole){
                intent1.setClass(context,InsoleRunningActivity.class);
            }
            context.startActivity(new Intent(context,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            context.startActivity(intent1);
            Log.i(TAG," startActivity(intent1);");

            ((MyApplication)context.getApplicationContext()).setCurRecoverRunningActivityStarted(true);
            return sportRunningType;
        }
        return -1;
    }



}
