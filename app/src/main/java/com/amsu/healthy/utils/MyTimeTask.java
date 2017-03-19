package com.amsu.healthy.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HP on 2017/3/16.
 */

public class MyTimeTask {
    private Timer mTimer;
    private long mTimeSpan;
    private TimerTask mTimerTask;
    private boolean mIsTimeerRunning;

    public MyTimeTask(long mTimeSpan, TimerTask mTimerTask) {
        this.mTimeSpan = mTimeSpan;
        this.mTimerTask = mTimerTask;
    }

    public void startTime(){
        if (mTimer==null){
            mTimer = new Timer();
            mTimer.schedule(mTimerTask,mTimeSpan,mTimeSpan);
        }
        mIsTimeerRunning = true;
    }

    public void stopTime(){
        if (!mIsTimeerRunning && mTimer!=null && mTimerTask!=null){
            /*mTimerTask.cancel();
            mTimerTask=null;*/
            mTimer.cancel();
            mTimer = null;
        }
    }

}
