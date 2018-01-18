package com.amsu.bleinteraction.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.amsu.bleinteraction.proxy.BleConnectionProxy;

/**
 * Created by HP on 2016/12/23.
 */
public class BleDevice implements Parcelable {
    String name;
    String state;
    String mac;
    String LEName;
    int deviceType;
    Integer rssi;
    String hardWareVersion;
    String softWareVersion;
    String modelNumber;
    int battery;
    int clothDeviceType;
    BleConnectionProxy.DeviceBindByHardWareType bindType;
    boolean isJustDoSP;

    public BleDevice() {
        this.name = "";
        this.state = "";
        this.mac = "";
        this.LEName = "";
        this.rssi = -1;
        this.hardWareVersion = "";
        this.softWareVersion = "";
        this.modelNumber = "";
    }

    public BleDevice(String hardWareVersion, String softWareVersion) {
        this.hardWareVersion = hardWareVersion;
        this.softWareVersion = softWareVersion;
    }

    public BleDevice(String name, String state, String mac, String LEName, int deviceType, int rssi) {
        this.name = name;
        this.state = state;
        this.mac = mac;
        this.LEName = LEName;
        this.deviceType = deviceType;
        this.rssi = rssi;
    }

    public BleDevice(String name, String state, String mac, String LEName, int deviceType) {
        this.name = name;
        this.state = state;
        this.mac = mac;
        this.LEName = LEName;
        this.deviceType = deviceType;
    }

    public BleDevice(String name, String state, String mac, String LEName, int deviceType, String hardWareVersion, String softWareVersion, int battery) {
        this.name = name;
        this.state = state;
        this.mac = mac;
        this.LEName = LEName;
        this.deviceType = deviceType;
        this.hardWareVersion = hardWareVersion;
        this.softWareVersion = softWareVersion;
        this.battery = battery;
    }

    public BleDevice(String name, String state, String mac, String LEName, int deviceType, Integer rssi, String hardWareVersion, String softWareVersion, String modelNumber, int battery, int clothDeviceType, BleConnectionProxy.DeviceBindByHardWareType bindType) {
        this.name = name;
        this.state = state;
        this.mac = mac;
        this.LEName = LEName;
        this.deviceType = deviceType;
        this.rssi = rssi;
        this.hardWareVersion = hardWareVersion;
        this.softWareVersion = softWareVersion;
        this.modelNumber = modelNumber;
        this.battery = battery;
        this.clothDeviceType = clothDeviceType;
        this.bindType = bindType;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(state);
        dest.writeString(mac);
        dest.writeString(LEName);
        dest.writeInt(deviceType);
        dest.writeInt(rssi);
        dest.writeString(hardWareVersion);
        dest.writeString(softWareVersion);
        dest.writeString(modelNumber);
        dest.writeInt(battery);
        dest.writeInt(clothDeviceType);
        dest.writeInt(bindType.ordinal());
    }

    public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
        @Override
        public BleDevice createFromParcel(Parcel source) {
            return new BleDevice(source.readString(),source.readString(),source.readString(),source.readString(),source.readInt(),source.readInt(),source.readString(),source.readString(),source.readString(),
                    source.readInt(),source.readInt(), BleConnectionProxy.DeviceBindByHardWareType.values()[source.readInt()]);
        }

        @Override
        public BleDevice[] newArray(int size) {
            return new BleDevice[size];
        }
    };


    public BleDevice(String name, String state, String mac, String LEName) {
        this.name = name;
        this.state = state;
        this.mac = mac;
        this.LEName = LEName;
    }

    public String getLEName() {
        return LEName;
    }

    public void setLEName(String LEName) {
        this.LEName = LEName;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getHardWareVersion() {
        return hardWareVersion;
    }

    public void setHardWareVersion(String hardWareVersion) {
        this.hardWareVersion = hardWareVersion;
    }

    public String getSoftWareVersion() {
        return softWareVersion;
    }

    public void setSoftWareVersion(String softWareVersion) {
        this.softWareVersion = softWareVersion;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public BleConnectionProxy.DeviceBindByHardWareType getBindType() {
        return bindType;
    }

    public void setBindType(BleConnectionProxy.DeviceBindByHardWareType bindType) {
        this.bindType = bindType;
    }

    public int getClothDeviceType() {
        return clothDeviceType;
    }

    public void setClothDeviceType(int clothDeviceType) {
        this.clothDeviceType = clothDeviceType;
    }

    @Override
    public String toString() {
        return "BleDevice{" +
                "name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", mac='" + mac + '\'' +
                ", LEName='" + LEName + '\'' +
                ", deviceType=" + deviceType +
                ", clothDeviceType=" + clothDeviceType +
                ", rssi=" + rssi +
                ", hardWareVersion='" + hardWareVersion + '\'' +
                ", softWareVersion='" + softWareVersion + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", battery=" + battery +
                ", bindType=" + bindType +
                '}';
    }

    public boolean isJustDoSP() {
        return isJustDoSP;
    }

    public void setJustDoSP(boolean justDoSP) {
        isJustDoSP = justDoSP;
    }
}
