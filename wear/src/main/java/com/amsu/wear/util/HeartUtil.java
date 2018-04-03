package com.amsu.wear.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.amsu.bleinteraction.proxy.Ble;
import com.amsu.wear.bean.User;
import com.test.utils.DiagnosisNDK;

import java.util.ArrayList;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.utils
 * @time 12/6/2017 4:42 PM
 * @describe
 */
public class HeartUtil {
    private static final String TAG = HeartUtil.class.getSimpleName();
    private static int mPreHeartRate;
    private static final int D_valueMaxValue = 20;

    //更新心率：4s更新一次，如果2个心率差值大于20，则递增显示
    public static void updateHeartUI(final int heartRate, final TextView tv_healthydata_rate, final Activity activity) {
        //Log.i(TAG,"heartRate========================================："+heartRate);
        if (mPreHeartRate>0 && heartRate>0){
            int count = 0;
            final int d_value = heartRate - mPreHeartRate;

            if (d_value > D_valueMaxValue) {
                count = (d_value) / D_valueMaxValue + 1;
            } else if (d_value < -D_valueMaxValue) {
                count = (d_value) / D_valueMaxValue - 1;
            }
            //Log.i(TAG,"count："+count);
            if (count != 0) {
                final int finalCount = count;
                new Thread(){
                    @Override
                    public void run() {
                        for (int i = 0; i< Math.abs(finalCount); i++){
                            final int heart = mPreHeartRate + Math.abs(d_value) / finalCount*(i+1);

                            if (activity!=null && !activity.isFinishing()){
                                final int finalI = i;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_healthydata_rate.setText(heart +"");
                                        //Log.i(TAG,"分段设置值 i:"+ finalI +",heart:"+heart);
                                    }
                                });
                            }
                            try {
                                long millis = (long) (1000*3f / Math.abs(finalCount));
                                //Log.i(TAG,"睡眠:"+ millis);
                                Thread.sleep(millis);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (activity!=null && !activity.isFinishing()){
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setFinalHeartValue(heartRate, tv_healthydata_rate);
                                }
                            });
                        }

                    }
                }.start();
            }
            else {
                setFinalHeartValue(heartRate, tv_healthydata_rate);
            }
        }
        else {
            setFinalHeartValue(heartRate, tv_healthydata_rate);
        }
    }

    private static void setFinalHeartValue(int heartRate, TextView tv_healthydata_rate) {
        try {
            String showHeartString = heartRate==0?"--":heartRate+"";
            tv_healthydata_rate.setText(showHeartString);

            //Log.i(TAG,"mPreHeartRate:"+ mPreHeartRate);
            mPreHeartRate = heartRate;
        }catch (Exception e){
        }
    }

    public static String calcuOxygenState(int heartRate, Context context) {
        int userAge = Ble.configuration().userAge;
        int maxRate = 220- userAge;
        if (heartRate<=maxRate*0.6){
            return "平缓";
        }
        else if (maxRate*0.6<heartRate && heartRate<=maxRate*0.75){
            return "有氧";
        }
        else if (maxRate*0.75<heartRate && heartRate<=maxRate*0.95){
            return "无氧";
        }
        else if (maxRate*0.95<heartRate ){
            return "高危";
        }
        return "平缓";
    }

    private long mCalKcalCurrentTimeMillis = 0;
    private int mUserSex;
    private int mUserAge;
    private int mUserWeight;

    //计算卡路里，累加
    public float calcuAllkcal(int heartRate) {
        if (mCalKcalCurrentTimeMillis == 0) {
            mCalKcalCurrentTimeMillis = System.currentTimeMillis();
            User userInfo = UserUtil.getUserInfo();
            mUserSex = Integer.parseInt(userInfo.getSex());
            mUserAge = userInfo.getAge();
            mUserWeight = Integer.parseInt(userInfo.getWeight());
        } else {
            float time = (float) ((System.currentTimeMillis() - mCalKcalCurrentTimeMillis) / (1000 * 60.0));
            mCalKcalCurrentTimeMillis = System.currentTimeMillis();
            Log.i(TAG, "time:" + time + ",mUserSex:" + mUserSex + ",mUserAge:" + mUserAge + ",mUserWeight" + mUserWeight);
            float getkcal = DiagnosisNDK.getkcal(mUserSex, heartRate, mUserAge, mUserWeight, time);
            Log.i(TAG, "getkcal:" + getkcal);
            if (getkcal < 0) {
                getkcal = 0;
            }
            return getkcal;
        }
        return 0;
    }

    //计算最大、最小、平均心率
    public static int[]  setHeartMaxMinAverage(ArrayList<Integer> heartDataList){
        int MaxHR ;
        int MinHR ;
        int AHR = 0;
        MaxHR= heartDataList.get(0);
        MinHR= heartDataList.get(0);
        int sum = 0;
        int graterZeroCount = 0;
        for (int heart: heartDataList){
            if (heart>0){
                if (heart>MaxHR){
                    MaxHR =heart;
                }
                if (heart<MinHR){
                    MinHR = heart;
                }
                sum += heart;
                graterZeroCount++;
            }
        }
        if (graterZeroCount>0){
            AHR = sum / graterZeroCount;
        }
        /*if (AHR>40  && AHR<120){
            MyUtil.putStringValueFromSP(Constant.restingHR,AHR+"");
        }*/
        return new int[]{MaxHR,MinHR,AHR};
    }
}
