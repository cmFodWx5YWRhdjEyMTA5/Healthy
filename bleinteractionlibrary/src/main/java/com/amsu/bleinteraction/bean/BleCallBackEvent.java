package com.amsu.bleinteraction.bean;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.bleinteraction.bean
 * @time 2017-12-27 9:52 AM
 * @describe
 */
public class BleCallBackEvent {
    public String address;
    public int messageType;
    public BluetoothGattCharacteristic characteristic;

    @Override
    public String toString() {
        return "BleCallBackEvent{" +
                "address='" + address + '\'' +
                ", messageType=" + messageType +
                ", characteristic=" + characteristic +
                '}';
    }
}
