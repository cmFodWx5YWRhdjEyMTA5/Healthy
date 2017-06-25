package com.amsu.healthy.bean;

import java.util.ArrayList;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.bean
 * @time 2017/6/15 14:32
 * @describe
 */
public class AppAbortDataSave implements Cloneable {
    private long startTimeMillis;
    private String ecgFileName;
    private String accFileName;
    private long mapTrackID;
    private int state;
    private ArrayList<Integer> speedStringList;

    public AppAbortDataSave(long startTimeMillis, String ecgFileName, int state) {
        this.startTimeMillis = startTimeMillis;
        this.ecgFileName = ecgFileName;
        this.state = state;
    }

    public AppAbortDataSave(long startTimeMillis, String ecgFileName, String accFileName, long mapTrackID, int state, ArrayList<Integer> speedStringList) {
        this.startTimeMillis = startTimeMillis;
        this.ecgFileName = ecgFileName;
        this.accFileName = accFileName;
        this.mapTrackID = mapTrackID;
        this.state = state;
        this.speedStringList = speedStringList;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        UploadRecord o = null;
        try{
            o = (UploadRecord)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return o;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public String getEcgFileName() {
        return ecgFileName;
    }

    public void setEcgFileName(String ecgFileName) {
        this.ecgFileName = ecgFileName;
    }

    public String getAccFileName() {
        return accFileName;
    }

    public void setAccFileName(String accFileName) {
        this.accFileName = accFileName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getMapTrackID() {
        return mapTrackID;
    }

    public void setMapTrackID(long mapTrackID) {
        this.mapTrackID = mapTrackID;
    }

    public ArrayList<Integer> getSpeedStringList() {
        return speedStringList;
    }

    public void setSpeedStringList(ArrayList<Integer> speedStringList) {
        this.speedStringList = speedStringList;
    }

    @Override
    public String toString() {
        return "AbortData{" +
                "startTimeMillis=" + startTimeMillis +
                ", ecgFileName='" + ecgFileName + '\'' +
                ", accFileName='" + accFileName + '\'' +
                ", mapTrackID=" + mapTrackID +
                ", state=" + state +
                ", speedStringList=" + speedStringList +
                '}';
    }
}
