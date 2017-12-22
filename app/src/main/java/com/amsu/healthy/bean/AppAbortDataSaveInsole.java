package com.amsu.healthy.bean;

import java.util.ArrayList;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.bean
 * @time 2017/6/15 14:32
 * @describe
 */
public class AppAbortDataSaveInsole implements Cloneable {
    public String id;
    public long startTimeMillis;
    public String mLeftInsoleFileAbsolutePath;
    public String mRightInsoleFileAbsolutePath;
    public long mapTrackID;
    public int state;
    public ArrayList<Integer> speedPaceList;
    public int kcal;
    public int stepCount;
    public boolean isOutDoor;
    public int sportType;
    public float maxSpeedKM_Hour;
    public int curLeftTime;
    public int curRightTime;
    public int preCacheLeftTime;
    public int preCacheRightTime;

    public AppAbortDataSaveInsole() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AppAbortDataSaveInsole o = null;
        try{
            o = (AppAbortDataSaveInsole)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return o;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public String getmLeftInsoleFileAbsolutePath() {
        return mLeftInsoleFileAbsolutePath;
    }

    public void setmLeftInsoleFileAbsolutePath(String mLeftInsoleFileAbsolutePath) {
        this.mLeftInsoleFileAbsolutePath = mLeftInsoleFileAbsolutePath;
    }

    public String getmRightInsoleFileAbsolutePath() {
        return mRightInsoleFileAbsolutePath;
    }

    public void setmRightInsoleFileAbsolutePath(String mRightInsoleFileAbsolutePath) {
        this.mRightInsoleFileAbsolutePath = mRightInsoleFileAbsolutePath;
    }

    public long getMapTrackID() {
        return mapTrackID;
    }

    public void setMapTrackID(long mapTrackID) {
        this.mapTrackID = mapTrackID;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ArrayList<Integer> getSpeedPaceList() {
        return speedPaceList;
    }

    public void setSpeedPaceList(ArrayList<Integer> speedPaceList) {
        this.speedPaceList = speedPaceList;
    }

    public int getKcal() {
        return kcal;
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public boolean isOutDoor() {
        return isOutDoor;
    }

    public void setOutDoor(boolean outDoor) {
        isOutDoor = outDoor;
    }

    public int getSportType() {
        return sportType;
    }

    public void setSportType(int sportType) {
        this.sportType = sportType;
    }


    public AppAbortDataSaveInsole(String id, long startTimeMillis, String mLeftInsoleFileAbsolutePath, String mRightInsoleFileAbsolutePath, long mapTrackID, int state, ArrayList<Integer> speedPaceList, int kcal, int stepCount, boolean isOutDoor, int sportType, float maxSpeedKM_Hour) {
        this.id = id;
        this.startTimeMillis = startTimeMillis;
        this.mLeftInsoleFileAbsolutePath = mLeftInsoleFileAbsolutePath;
        this.mRightInsoleFileAbsolutePath = mRightInsoleFileAbsolutePath;
        this.mapTrackID = mapTrackID;
        this.state = state;
        this.speedPaceList = speedPaceList;
        this.kcal = kcal;
        this.stepCount = stepCount;
        this.isOutDoor = isOutDoor;
        this.sportType = sportType;
        this.maxSpeedKM_Hour = maxSpeedKM_Hour;
    }

    public int getCurLeftTime() {
        return curLeftTime;
    }

    public void setCurLeftTime(int curLeftTime) {
        this.curLeftTime = curLeftTime;
    }

    public int getCurRightTime() {
        return curRightTime;
    }

    public void setCurRightTime(int curRightTime) {
        this.curRightTime = curRightTime;
    }

    public float getMaxSpeedKM_Hour() {
        return maxSpeedKM_Hour;
    }

    public void setMaxSpeedKM_Hour(float maxSpeedKM_Hour) {
        this.maxSpeedKM_Hour = maxSpeedKM_Hour;
    }

    public int getPreCacheLeftTime() {
        return preCacheLeftTime;
    }

    public void setPreCacheLeftTime(int preCacheLeftTime) {
        this.preCacheLeftTime = preCacheLeftTime;
    }

    public int getPreCacheRightTime() {
        return preCacheRightTime;
    }

    public void setPreCacheRightTime(int preCacheRightTime) {
        this.preCacheRightTime = preCacheRightTime;
    }

    @Override
    public String toString() {
        return "AppAbortDataSaveInsole{" +
                "id='" + id + '\'' +
                ", startTimeMillis=" + startTimeMillis +
                ", mLeftInsoleFileAbsolutePath='" + mLeftInsoleFileAbsolutePath + '\'' +
                ", mRightInsoleFileAbsolutePath='" + mRightInsoleFileAbsolutePath + '\'' +
                ", mapTrackID=" + mapTrackID +
                ", state=" + state +
                ", speedPaceList=" + speedPaceList +
                ", kcal=" + kcal +
                ", stepCount=" + stepCount +
                ", isOutDoor=" + isOutDoor +
                ", sportType=" + sportType +
                ", maxSpeedKM_Hour=" + maxSpeedKM_Hour +
                ", curLeftTime=" + curLeftTime +
                ", curRightTime=" + curRightTime +
                ", preCacheLeftTime=" + preCacheLeftTime +
                ", preCacheRightTime=" + preCacheRightTime +
                '}';
    }
}
