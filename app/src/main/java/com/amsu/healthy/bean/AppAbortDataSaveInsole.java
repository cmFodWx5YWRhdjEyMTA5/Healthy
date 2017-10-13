package com.amsu.healthy.bean;

import java.util.ArrayList;
import java.util.List;

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

    public AppAbortDataSaveInsole() {
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
                '}';
    }

    public float getMaxSpeedKM_Hour() {
        return maxSpeedKM_Hour;
    }

    public void setMaxSpeedKM_Hour(float maxSpeedKM_Hour) {
        this.maxSpeedKM_Hour = maxSpeedKM_Hour;
    }
}
