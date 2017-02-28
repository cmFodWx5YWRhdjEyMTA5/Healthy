package com.amsu.healthy.bean;

/**
 * Created by HP on 2017/2/21.
 */

public class HistoryRecord {
    private String date;
    private String time;


    public HistoryRecord(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
