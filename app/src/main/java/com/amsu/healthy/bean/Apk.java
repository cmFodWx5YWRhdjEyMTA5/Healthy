package com.amsu.healthy.bean;

/**
 * Created by HP on 2016/12/27.
 */
public class Apk{
    public String id;
    public String versioncode;
    public String versionname;
    public String versiondatetime;
    public String path;
    public String remark;

    @Override
    public String toString() {
        return "Apk{" +
                "id='" + id + '\'' +
                ", versioncode=" + versioncode +
                ", versionname=" + versionname +
                ", versiondatetime='" + versiondatetime + '\'' +
                ", path='" + path + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

    public Apk(String id, String versioncode, String versionname, String versiondatetime, String path, String remark) {
        this.id = id;
        this.versioncode = versioncode;
        this.versionname = versionname;
        this.versiondatetime = versiondatetime;
        this.path = path;
        this.remark = remark;
    }

    public Apk(String versioncode, String versionname, String versiondatetime, String path, String remark) {
        this.versioncode = versioncode;
        this.versionname = versionname;
        this.versiondatetime = versiondatetime;
        this.path = path;
        this.remark = remark;
    }
}
