package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP on 2016/12/23.
 */
public class Device implements Parcelable {
    String name;
    String state;
    String mac;
    String LEName;
    int deviceType;
    Integer rssi;
    String hardWareVersion;
    String softWareVersion;
    int battery;

    public Device() {
    }

    public Device(String hardWareVersion, String softWareVersion) {
        this.hardWareVersion = hardWareVersion;
        this.softWareVersion = softWareVersion;
    }

    public Device(String name, String state, String mac, String LEName, int deviceType, int rssi) {
        this.name = name;
        this.state = state;
        this.mac = mac;
        this.LEName = LEName;
        this.deviceType = deviceType;
        this.rssi = rssi;
    }

    public Device(String name, String state, String mac, String LEName, int deviceType) {
        this.name = name;
        this.state = state;
        this.mac = mac;
        this.LEName = LEName;
        this.deviceType = deviceType;
    }

    public Device(String name, String state, String mac, String LEName, int deviceType, String hardWareVersion, String softWareVersion,int battery) {
        this.name = name;
        this.state = state;
        this.mac = mac;
        this.LEName = LEName;
        this.deviceType = deviceType;
        this.hardWareVersion = hardWareVersion;
        this.softWareVersion = softWareVersion;
        this.battery = battery;
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
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel source) {
            return new Device(source.readString(),source.readString(),source.readString(),source.readString(),source.readInt(),source.readInt());
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };


    public Device(String name, String state, String mac, String LEName) {
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

    @Override
    public String toString() {
        return "Device{" +
                "name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", mac='" + mac + '\'' +
                ", LEName='" + LEName + '\'' +
                ", deviceType=" + deviceType +
                ", rssi=" + rssi +
                ", hardWareVersion='" + hardWareVersion + '\'' +
                ", softWareVersion='" + softWareVersion + '\'' +
                ", battery=" + battery +
                '}';
    }
}
