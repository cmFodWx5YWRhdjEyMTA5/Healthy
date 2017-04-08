package com.amsu.healthy.bean;

/**
 * Created by HP on 2016/12/27.
 */
public class Apk{
    public String ID;
    public int Version;
    public String VersionDateTime;
    public String Path;
    public String Remark;

    @Override
    public String toString() {
        return "Apk{" +
                "ID='" + ID + '\'' +
                ", Version=" + Version +
                ", VersionDateTime='" + VersionDateTime + '\'' +
                ", Path='" + Path + '\'' +
                ", Remark='" + Remark + '\'' +
                '}';
    }
}
