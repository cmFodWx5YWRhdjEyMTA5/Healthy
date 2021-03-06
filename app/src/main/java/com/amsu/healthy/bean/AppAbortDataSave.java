package com.amsu.healthy.bean;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.bean
 * @time 2017/6/15 14:32
 * @describe
 */
public class AppAbortDataSave implements Cloneable {
    public String id;
    public long startTimeMillis;
    public String ecgFileName;
    public String accFileName;
    public long mapTrackID;
    public int state;
    public CopyOnWriteArrayList<Integer> speedStringList;
    public CopyOnWriteArrayList<String> kcalStringList;
    public boolean isOutDoor;
    public int sportType;

    public AppAbortDataSave() {
    }

    public AppAbortDataSave(long startTimeMillis, String ecgFileName, int state) {
        this.startTimeMillis = startTimeMillis;
        this.ecgFileName = ecgFileName;
        this.state = state;
    }

    public AppAbortDataSave(long startTimeMillis, String ecgFileName, String accFileName, long mapTrackID, int state, CopyOnWriteArrayList<Integer> speedStringList) {
        this.startTimeMillis = startTimeMillis;
        this.ecgFileName = ecgFileName;
        this.accFileName = accFileName;
        this.mapTrackID = mapTrackID;
        this.state = state;
        this.speedStringList = speedStringList;
    }

    public AppAbortDataSave( long startTimeMillis, String ecgFileName, String accFileName, long mapTrackID, int state, CopyOnWriteArrayList<Integer> speedStringList, CopyOnWriteArrayList<String> kcalStringList) {
        this.startTimeMillis = startTimeMillis;
        this.ecgFileName = ecgFileName;
        this.accFileName = accFileName;
        this.mapTrackID = mapTrackID;
        this.state = state;
        this.speedStringList = speedStringList;
        this.kcalStringList = kcalStringList;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AppAbortDataSave o = null;
        try{
            o = (AppAbortDataSave)super.clone();
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

    public List<Integer> getSpeedStringList() {
        return speedStringList;
    }

    public void setSpeedStringList(CopyOnWriteArrayList<Integer> speedStringList) {
        this.speedStringList = speedStringList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CopyOnWriteArrayList<String> getKcalStringList() {
        return kcalStringList;
    }

    public void setKcalStringList(CopyOnWriteArrayList<String> kcalStringList) {
        this.kcalStringList = kcalStringList;
    }

    @Override
    public String toString() {
        return "AppAbortDataSave{" +
                "id='" + id + '\'' +
                ", startTimeMillis=" + startTimeMillis +
                ", ecgFileName='" + ecgFileName + '\'' +
                ", accFileName='" + accFileName + '\'' +
                ", mapTrackID=" + mapTrackID +
                ", state=" + state +
                ", speedStringList=" + speedStringList +
                ", kcalStringList=" + kcalStringList +
                ", isOutDoor=" + isOutDoor +
                ", sportType=" + sportType +
                '}';
    }

    public int getSportType() {
        return sportType;
    }

    public void setSportType(int sportType) {
        this.sportType = sportType;
    }
}
