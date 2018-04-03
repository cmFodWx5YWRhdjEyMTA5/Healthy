package com.amsu.wear.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.util
 * @time 2018-03-07 11:26 AM
 * @describe
 */
public class TimerUtil {

    //在一段时间后执行某一操作
    public static void executeDelayTime(long delay, final DelayExecuteTimeListener delayExecuteTimeListener){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (delayExecuteTimeListener!=null){
                    delayExecuteTimeListener.execute();
                }
            }
        }, delay);
    }


    public static Timer executeIntervals(long period ,final DelayExecuteTimeListener delayExecuteTimeListener){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (delayExecuteTimeListener!=null){
                    delayExecuteTimeListener.execute();
                }
            }
        }, period,period);
        return timer;
    }

    public static void cancelTimer(Timer timer){
        if (timer!=null){
            timer.cancel();
        }
    }

    public interface DelayExecuteTimeListener{
        void execute();
    }


}
