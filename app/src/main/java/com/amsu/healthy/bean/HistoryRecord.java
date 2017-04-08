package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP on 2017/2/21.
 */

public class HistoryRecord implements Parcelable{
    private String ID;
    private String datatime;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDatatime() {
        return datatime;
    }

    public void setDatatime(String datatime) {
        this.datatime = datatime;
    }

    public HistoryRecord(String ID, String datatime) {
        this.ID = ID;
        this.datatime = datatime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(datatime);
    }
    public static final Creator<HistoryRecord> CREATOR = new Creator<HistoryRecord>() {
        @Override
        public HistoryRecord createFromParcel(Parcel source) {
            return new HistoryRecord(source.readString(),source.readString());
        }

        @Override
        public HistoryRecord[] newArray(int size) {
            return new HistoryRecord[0];
        }
    };
}
