package com.amsu.healthy.bean;

/**
 * author：WangLei
 * date:2017/11/1.
 * QQ:619321796
 */

public class SportRecordAE {
    float distance;
    int time;
    String hr;

    public SportRecordAE(float distance, int time, String hr) {
        this.distance = distance;
        this.time = time;
        this.hr = hr;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getHr() {
        return hr;
    }

    public void setHr(String hr) {
        this.hr = hr;
    }
}
