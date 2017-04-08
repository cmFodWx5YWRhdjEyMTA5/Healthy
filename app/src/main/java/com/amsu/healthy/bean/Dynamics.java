package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP on 2017/1/16.
 */

public class Dynamics implements Parcelable{
    private String userIconurl;
    private String username;
    private String time;
    private String text;
    private String[] imageList;
    private String surnameCount;
    private String commentCount;


    public Dynamics(String userIconurl, String username, String time, String text, String[] imageList, String surnameCount, String commentCount) {
        this.userIconurl = userIconurl;
        this.username = username;
        this.time = time;
        this.text = text;
        this.imageList = imageList;
        this.surnameCount = surnameCount;
        this.commentCount = commentCount;
    }

    public String getUserIconurl() {
        return userIconurl;
    }

    public void setUserIconurl(String userIconurl) {
        this.userIconurl = userIconurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getImageList() {
        return imageList;
    }

    public void setImageList(String[] imageList) {
        this.imageList = imageList;
    }

    public String getSurnameCount() {
        return surnameCount;
    }

    public void setSurnameCount(String surnameCount) {
        this.surnameCount = surnameCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userIconurl);
        dest.writeString(username);
        dest.writeString(time);
        dest.writeString(text);
        dest.writeStringArray(imageList);
        dest.writeString(surnameCount);
        dest.writeString(commentCount);
    }

    public static final Creator<Dynamics> CREATOR = new Creator<Dynamics>() {
        @Override
        public Dynamics createFromParcel(Parcel source) {
            return new Dynamics(source.readString(),source.readString(),source.readString(),source.readString(),source.createStringArray(),source.readString(),source.readString());
        }

        @Override
        public Dynamics[] newArray(int size) {
            return new Dynamics[0];
        }
    };
}
