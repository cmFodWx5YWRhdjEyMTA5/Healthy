package com.amsu.bleinteraction.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.bleinteractionlibrary.utils
 * @time 12/5/2017 4:32 PM
 * @describe
 */
public class TimerTaskUtil {

    private Timer mTimer;

    //时间计时器，timeSpan为时间间隔，会一直执行
    public void startTimeRiseTimerTask(final long timeSpan,TimerTask timerTask){
        mTimer = new Timer();
        mTimer.schedule(timerTask, timeSpan,timeSpan);
    }

    public void cancelTimerTask(){
        if (mTimer!=null){
            mTimer.cancel();
            mTimer = null;
        }
    }

}
