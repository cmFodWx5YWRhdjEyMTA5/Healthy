package com.amsu.healthy.utils;

import android.app.Activity;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HP on 2017/2/28.
 */

//计时器，timeSpan为时间间隔

public class RunTimerTaskUtil extends TimerTask {
    private static final String TAG = "RunTimerTaskUtil";
    private long timeSpan;
    private boolean mIsFirstStart = true;  //第一次开始
    private boolean mIsTimeerRunning;  //是否正在运行
    private Date mNowDate;
    private String format;
    private Timer mTimer;
    private long outTime;
    private Activity activity;

    public RunTimerTaskUtil(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        if (mIsTimeerRunning){
            if (mIsFirstStart){
                mIsFirstStart = false;
                mNowDate = new Date();
                mNowDate.setHours(0);
                mNowDate.setMinutes(0);
                mNowDate.setSeconds(0);
            }

            Date curr= new Date(mNowDate.getTime()+timeSpan);
            mNowDate = curr;
            //String time = MyUtil.getSpecialFormatTime("HH:mm:ss", curr);
            final String time = MyUtil.getSpecialFormatTime(format, curr);
            Log.i(TAG,"time:"+time);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onTimeChangeListerner.onFormatStringTimeChange(time);
                }
            });
        }
    }

    public void startTime(){
        if (mTimer==null){
            mTimer = new Timer();
            mTimer.schedule(this,timeSpan,timeSpan);
        }
        mIsTimeerRunning = true;
    }

    public void stopTime(){
        if (mIsTimeerRunning && mTimer!=null){
            mIsTimeerRunning = false;
        }
    }

    public void destoryTime(){
        if (!mIsTimeerRunning && mTimer!=null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void setOnTimeChangeListerner(String format,long timeSpan, OnTimeChangeListerner onTimeChangeListerner){
        this.format = format;
        this.timeSpan = timeSpan;
        this.onTimeChangeListerner = onTimeChangeListerner;
    }

    public void setOnTimeOutListerner(long outTime, OnTimeOutListerner onTimeOutListerner){
        this.outTime = outTime;
        this.onTimeOutListerner = onTimeOutListerner;
    }

    public interface OnTimeChangeListerner{
        String onFormatStringTimeChange(String formatTime);
    }

    public interface OnTimeOutListerner{
        void onTimeOut();
    }

    private OnTimeChangeListerner onTimeChangeListerner;
    private OnTimeOutListerner onTimeOutListerner;






}