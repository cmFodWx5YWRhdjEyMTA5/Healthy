package com.amsu.healthy.bean;

import com.amsu.bleinteraction.bean.BleDevice;

import java.util.List;

/**
 * Created by HP on 2016/12/26.
 */

public class DeviceList{
    List<BleDevice> bleDeviceList;

    public List<BleDevice> getBleDeviceList() {
        return bleDeviceList;
    }

    public void setBleDeviceList(List<BleDevice> bleDeviceList) {
        this.bleDeviceList = bleDeviceList;
    }

}