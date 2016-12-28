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


    public Device(String name, String state) {
        this.name = name;
        this.state = state;
    }


    public Device(String name, String state, String mac) {
        this.name = name;
        this.state = state;
        this.mac = mac;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(state);
        dest.writeString(mac);
    }

    public static final Parcelable.Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel source) {
            return new Device(source.readString(),source.readString(),source.readString());
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
}
