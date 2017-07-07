package com.amsu.healthy.utils;

import android.content.Context;
import android.os.PowerManager;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.utils
 * @time 7/7/2017 9:51 AM
 * @describe
 */
public class WakeLockUtil {
    private static PowerManager.WakeLock wakeLock;

    public static void acquireWakeLock(Context context) {
        if (wakeLock ==null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, context.getClass().getCanonicalName());
            wakeLock.acquire();
        }
    }

    public static void releaseWakeLock() {
        if (wakeLock !=null&& wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock =null;
        }

    }
}
