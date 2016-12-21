package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP on 2016/12/19.
 */
public class HealthyPlan implements Parcelable {
    private String id;
    private String title;
    private String content;
    private String date;



    public HealthyPlan(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public HealthyPlan(String id, String title, String content, String date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(date);
    }

    public static final Parcelable.Creator<HealthyPlan> CREATOR = new Creator<HealthyPlan>() {
        @Override
        public HealthyPlan createFromParcel(Parcel source) {
            return new HealthyPlan(source.readString(),source.readString(),source.readString(),source.readString());
        }

        @Override
        public HealthyPlan[] newArray(int size) {
            return new HealthyPlan[size];
        }
    };
}
