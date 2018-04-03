package com.amsu.wear.bean;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.bean
 * @time 2018-03-08 5:23 PM
 * @describe
 */
public class RunningData {
    private String time;
    private String pace;
    private String mileage;
    private String aerobic;
    private String stride;
    private String calorie;
    private String heartRate;

    public RunningData() {
    }

    public RunningData(String time, String pace, String mileage, String aerobic, String stride, String calorie, String heartRate) {
        this.time = time;
        this.pace = pace;
        this.mileage = mileage;
        this.aerobic = aerobic;
        this.stride = stride;
        this.calorie = calorie;
        this.heartRate = heartRate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getAerobic() {
        return aerobic;
    }

    public void setAerobic(String aerobic) {
        this.aerobic = aerobic;
    }

    public String getStride() {
        return stride;
    }

    public void setStride(String stride) {
        this.stride = stride;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

}
