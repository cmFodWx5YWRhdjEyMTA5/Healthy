package com.amsu.bleinteraction.utils;

import android.util.Log;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.bleinteraction.utils
 * @time 2018-01-18 11:40 AM
 * @describe
 */
public class LogUtil {
    private static final boolean isShow = false;

    public static void v(String tag, String msg){
        if (isShow){
            Log.v(tag,msg);
        }
    }

    public static void d(String tag, String msg){
        if (isShow){
            Log.d(tag,msg);
        }
    }

    public static void i(String tag, String msg){
        if (isShow){
            Log.i(tag,msg);
        }
    }

    public static void w(String tag, String msg){
        if (isShow){
            Log.w(tag,msg);
        }
    }

    public static void e(String tag, String msg){
        if (isShow){
            Log.e(tag,msg);
        }
    }

}
