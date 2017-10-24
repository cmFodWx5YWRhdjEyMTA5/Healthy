package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.bean
 * @time 10/18/2017 4:12 PM
 * @describe
 */
public class Insole3ScendCache implements Parcelable {
    private int footType;
    private int time;
    private short gyrX;
    private short gyrY;
    private short gyrZ;
    private short accX;
    private short accY;
    private short accZ;


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public short getGyrX() {
        return gyrX;
    }

    public void setGyrX(short gyrX) {
        this.gyrX = gyrX;
    }

    public short getGyrY() {
        return gyrY;
    }

    public void setGyrY(short gyrY) {
        this.gyrY = gyrY;
    }

    public short getGyrZ() {
        return gyrZ;
    }

    public void setGyrZ(short gyrZ) {
        this.gyrZ = gyrZ;
    }

    public short getAccX() {
        return accX;
    }

    public void setAccX(short accX) {
        this.accX = accX;
    }

    public short getAccY() {
        return accY;
    }

    public void setAccY(short accY) {
        this.accY = accY;
    }

    public short getAccZ() {
        return accZ;
    }

    public void setAccZ(short accZ) {
        this.accZ = accZ;
    }

    public Insole3ScendCache(int time, short gyrX, short gyrY, short gyrZ, short accX, short accY, short accZ) {
        this.time = time;
        this.gyrX = gyrX;
        this.gyrY = gyrY;
        this.gyrZ = gyrZ;
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
    }

    public Insole3ScendCache(int footType,int time, int gyrX, int gyrY, int gyrZ, int accX, int accY, int accZ) {
        this.footType = footType;
        this.time = time;
        this.gyrX = (short) gyrX;
        this.gyrY = (short) gyrY;
        this.gyrZ = (short) gyrZ;
        this.accX = (short) accX;
        this.accY = (short) accY;
        this.accZ = (short) accZ;
    }

    public int getFootType() {
        return footType;
    }

    public void setFootType(int footType) {
        this.footType = footType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(footType);
        dest.writeInt(time);
        dest.writeInt(gyrX);
        dest.writeInt(gyrY);
        dest.writeInt(gyrZ);
        dest.writeInt(accX);
        dest.writeInt(accY);
        dest.writeInt(accZ);
    }

    public static final Creator<Insole3ScendCache> CREATOR = new Creator<Insole3ScendCache>() {
        @Override
        public Insole3ScendCache createFromParcel(Parcel source) {
            return new Insole3ScendCache(source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readInt());
        }

        @Override
        public Insole3ScendCache[] newArray(int size) {
            return new Insole3ScendCache[0];
        }
    };

}
