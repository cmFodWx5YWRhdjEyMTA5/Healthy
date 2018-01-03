package com.amsu.bleinteraction.bean;

import com.amsu.bleinteraction.proxy.BleConnectionProxy;

/**
 * @anthor haijun
 * @project name: MyApplication
 * @class name：com.amsu.myapplication.bean
 * @time 2017-12-26 5:47 PM
 * @describe
 */
public class MessageEvent {
    public BleConnectionProxy.MessageEventType messageType;
    public int singleValue;
    public int[] dataArray;
    public String address;
}
