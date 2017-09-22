package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP on 2017/2/21.
 */

public class InsoleHistoryRecord implements Parcelable{
    private String id;
    private long creationtime;
    private long duration;
    private double distance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreationtime() {
        return creationtime;
    }

    public void setCreationtime(long creationtime) {
        this.creationtime = creationtime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(creationtime);
        dest.writeLong(duration);
        dest.writeDouble(distance);
    }

    public static final Creator<InsoleHistoryRecord> CREATOR = new Creator<InsoleHistoryRecord>() {
        @Override
        public InsoleHistoryRecord createFromParcel(Parcel source) {
            return new InsoleHistoryRecord(source);
        }

        @Override
        public InsoleHistoryRecord[] newArray(int size) {
            return new InsoleHistoryRecord[0];
        }
    };

    public InsoleHistoryRecord(Parcel source) {
        this.id = source.readString();
        this.creationtime = source.readLong();
        this.duration = source.readLong();
        this.distance = source.readDouble();
    }



}
