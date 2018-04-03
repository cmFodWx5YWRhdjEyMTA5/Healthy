package com.amsu.bleinteraction.proxy;

import android.content.Context;

import com.amsu.bleinteraction.utils.DeviceUtil;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.bleinteraction.proxy
 * @time 2018-03-15 11:33 AM
 * @describe
 */
public class Ble {
    public static void init(Context context, BleConnectionProxy.BleConfiguration connectionConfiguration){
        BleConnectionProxy.getInstance().init(context,connectionConfiguration);
    }

    public static BleDataProxy bleDataProxy(){
        return BleDataProxy.getInstance();
    }

    public static BleConnectionProxy bleConnectionProxy(){
        return BleConnectionProxy.getInstance();
    }

    public static BleConnectionProxy.BleConfiguration configuration(){
        return BleConnectionProxy.getInstance().getmConnectionConfiguration();
    }

    public static DeviceUtil device(){
        return DeviceUtil.getInstance();
    }



}
