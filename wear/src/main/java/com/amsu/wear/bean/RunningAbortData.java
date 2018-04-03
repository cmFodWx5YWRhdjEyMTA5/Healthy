package com.amsu.wear.bean;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.bean
 * @time 2017/6/15 14:32
 * @describe
 */
public class RunningAbortData implements Cloneable {
    private long startTimeMillis;
    private String ecgFileName;
    private String accFileName;
    private CopyOnWriteArrayList<Integer> paceStringList;
    private CopyOnWriteArrayList<String> kcalStringList;
    private CopyOnWriteArrayList<Integer> heartRateStringList;
    private CopyOnWriteArrayList<Integer> StridefreStringList;
    private boolean isOutDoor;
    private String distance;
    private long duration;
    private List<ParcelableDoubleList> pathline;

    @Override
    public Object clone() throws CloneNotSupportedException {
        RunningAbortData o = null;
        try {
            o = (RunningAbortData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }

    public RunningAbortData(long startTimeMillis, String ecgFileName, String accFileName, CopyOnWriteArrayList<Integer> paceStringList, CopyOnWriteArrayList<String> kcalStringList, CopyOnWriteArrayList<Integer> heartRateStringList, CopyOnWriteArrayList<Integer> stridefreStringList, boolean isOutDoor, String distance, long duration, List<ParcelableDoubleList> pathline) {
        this.startTimeMillis = startTimeMillis;
        this.ecgFileName = ecgFileName;
        this.accFileName = accFileName;
        this.paceStringList = paceStringList;
        this.kcalStringList = kcalStringList;
        this.heartRateStringList = heartRateStringList;
        StridefreStringList = stridefreStringList;
        this.isOutDoor = isOutDoor;
        this.distance = distance;
        this.duration = duration;
        this.pathline = pathline;
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

    public CopyOnWriteArrayList<Integer> getPaceStringList() {
        return paceStringList;
    }

    public void setPaceStringList(CopyOnWriteArrayList<Integer> paceStringList) {
        this.paceStringList = paceStringList;
    }

    public CopyOnWriteArrayList<String> getKcalStringList() {
        return kcalStringList;
    }

    public void setKcalStringList(CopyOnWriteArrayList<String> kcalStringList) {
        this.kcalStringList = kcalStringList;
    }

    public CopyOnWriteArrayList<Integer> getHeartRateStringList() {
        return heartRateStringList;
    }

    public void setHeartRateStringList(CopyOnWriteArrayList<Integer> heartRateStringList) {
        this.heartRateStringList = heartRateStringList;
    }

    public CopyOnWriteArrayList<Integer> getStridefreStringList() {
        return StridefreStringList;
    }

    public void setStridefreStringList(CopyOnWriteArrayList<Integer> stridefreStringList) {
        StridefreStringList = stridefreStringList;
    }

    public boolean isOutDoor() {
        return isOutDoor;
    }

    public void setOutDoor(boolean outDoor) {
        isOutDoor = outDoor;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<ParcelableDoubleList> getPathline() {
        return pathline;
    }

    public void setPathline(List<ParcelableDoubleList> pathline) {
        this.pathline = pathline;
    }
}