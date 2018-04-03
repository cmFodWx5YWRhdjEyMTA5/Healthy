package com.amsu.bleinteraction.utils;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.bleinteraction.utils
 * @time 2018-03-20 3:36 PM
 * @describe
 */
public class DeviceUtil {
    public static DeviceUtil deviceUtil;
    private static BleDevice bleClothDevice;
    private static BleDevice bleInsoleDevice;

    public static DeviceUtil getInstance(){
        if (deviceUtil==null){
            deviceUtil = new DeviceUtil();
        }
        return deviceUtil;
    }

    public BleDevice getClothDevice(){
        if (bleClothDevice!=null){
            return bleClothDevice;
        }
        else {
            return SharedPreferencesUtil.getDeviceFromSP(BleConstant.sportType_Cloth);
        }
    }

    public void setBleClothDevice(BleDevice bleClothDevice) {
        DeviceUtil.bleClothDevice = bleClothDevice;
    }

    public BleDevice getInsoleDevice(){
        if (bleClothDevice!=null){
            return bleClothDevice;
        }
        else {
            return SharedPreferencesUtil.getDeviceFromSP(BleConstant.sportType_Insole);
        }
    }

    public boolean isBindedByHardware(){
        BleDevice clothDevice = getClothDevice();
        if (clothDevice!=null){
            BleConnectionProxy.DeviceBindByHardWareType bindType = clothDevice.getBindType();
            if (bindType == BleConnectionProxy.DeviceBindByHardWareType.bindByWeiXin || bindType == BleConnectionProxy.DeviceBindByHardWareType.bindByPhone){
                return true;
            }
        }
        return false;
    }
}
