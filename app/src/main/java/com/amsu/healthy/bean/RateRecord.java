package com.amsu.healthy.bean;

import java.util.List;

/**
 * Created by HP on 2017/3/17.
 */

public class RateRecord {

    /*
    * public static final String KEY_ROWID = "id";
	public static final String KEY_TIME = "time";
	public static final String KEY_LFHF = "lfhf";
	public static final String KEY_SDNN = "sdnn";
	public static final String KEY_RATELIST = "ratelist";
	public static final String KEY_RATEFILEPATH = "ratefilepath";
	public static final String KEY_HRR = "hrr";
	public static final String KEY_RATENORMALCOUNT = "ratenormalcount";
	public static final String KEY_RATEBADCOUNT = "ratebadcount";
	public static final String KEY_MISSBEAT = "missbeat";
	public static final String KEY_PREMATUREBEAT = "prematurebeat";
    *
    * */

    private int id;
    private String time;
    private int lfhf;
    private int sdnn;
    private List<Integer> ratelist;
    private String ratefilepath;
    private int hrr;
    private int ratenormalcount;
    private int ratebadcount;
    private int missbeat;
    private int prematurebeat;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLfhf() {
        return lfhf;
    }

    public void setLfhf(int lfhf) {
        this.lfhf = lfhf;
    }

    public int getSdnn() {
        return sdnn;
    }

    public void setSdnn(int sdnn) {
        this.sdnn = sdnn;
    }

    public List<Integer> getRatelist() {
        return ratelist;
    }

    public void setRatelist(List<Integer> ratelist) {
        this.ratelist = ratelist;
    }

    public String getRatefilepath() {
        return ratefilepath;
    }

    public void setRatefilepath(String ratefilepath) {
        this.ratefilepath = ratefilepath;
    }

    public int getHrr() {
        return hrr;
    }

    public void setHrr(int hrr) {
        this.hrr = hrr;
    }

    public int getRatenormalcount() {
        return ratenormalcount;
    }

    public void setRatenormalcount(int ratenormalcount) {
        this.ratenormalcount = ratenormalcount;
    }

    public int getRatebadcount() {
        return ratebadcount;
    }

    public void setRatebadcount(int ratebadcount) {
        this.ratebadcount = ratebadcount;
    }

    public int getMissbeat() {
        return missbeat;
    }

    public void setMissbeat(int missbeat) {
        this.missbeat = missbeat;
    }

    public int getPrematurebeat() {
        return prematurebeat;
    }

    public void setPrematurebeat(int prematurebeat) {
        this.prematurebeat = prematurebeat;
    }

    @Override
    public String toString() {
        return "RateRecord{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", lfhf=" + lfhf +
                ", sdnn=" + sdnn +
                ", ratelist=" + ratelist +
                ", ratefilepath='" + ratefilepath + '\'' +
                ", hrr=" + hrr +
                ", ratenormalcount=" + ratenormalcount +
                ", ratebadcount=" + ratebadcount +
                ", missbeat=" + missbeat +
                ", prematurebeat=" + prematurebeat +
                '}';
    }
}
