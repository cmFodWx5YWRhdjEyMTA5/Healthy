package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP on 2017/2/21.
 */

public class HistoryRecord implements Parcelable{
    private String id;
    private long datatime;
    private int state;
    private int analysisState;  //""：已分析(正常，默认的)，1：未分析（在离线传输时），2：异常中断

    public static int analysisState_haveAnalysised = 0;
    public static int analysisState_noAnalysised = 1;
    public static int analysisState_abort = 2;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDatatime() {
        return datatime;
    }

    public void setDatatime(long datatime) {
        this.datatime = datatime;
    }

    public HistoryRecord(String ID, long datatime) {
        this.id = ID;
        this.datatime = datatime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public HistoryRecord(String ID, long datatime, int state) {
        this.id = ID;
        this.datatime = datatime;
        this.state = state;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(datatime);
        dest.writeInt(state);
    }
    public static final Creator<HistoryRecord> CREATOR = new Creator<HistoryRecord>() {
        @Override
        public HistoryRecord createFromParcel(Parcel source) {
            return new HistoryRecord(source.readString(),source.readLong(),source.readInt());
        }

        @Override
        public HistoryRecord[] newArray(int size) {
            return new HistoryRecord[0];
        }
    };

    public HistoryRecord(String ID, long datatime, int state, int analysisState) {
        this.id = ID;
        this.datatime = datatime;
        this.state = state;
        this.analysisState = analysisState;
    }

    public int getAnalysisState() {
        return analysisState;
    }

    public void setAnalysisState(int analysisState) {
        this.analysisState = analysisState;
    }


    @Override
    public String toString() {
        return "HistoryRecord{" +
                "id='" + id + '\'' +
                ", datatime='" + datatime + '\'' +
                ", state=" + state +
                ", analysisState='" + analysisState + '\'' +
                '}';
    }
}
