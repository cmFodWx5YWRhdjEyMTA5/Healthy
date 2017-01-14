package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP on 2017/1/12.
 */

public class ClubCampaign implements Parcelable{
    private String name;
    private String date;
    private String time;
    private String type;
    private String jionNumber;
    private String allNumber;
    private String description;
    private String smiallImageUrl;

    public ClubCampaign(String name, String date, String time, String type, String jionNumber, String allNumber, String description, String smiallImageUrl) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.type = type;
        this.jionNumber = jionNumber;
        this.allNumber = allNumber;
        this.description = description;
        this.smiallImageUrl = smiallImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSmiallImageUrl() {
        return smiallImageUrl;
    }

    public void setSmiallImageUrl(String smiallImageUrl) {
        this.smiallImageUrl = smiallImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJionNumber() {
        return jionNumber;
    }

    public void setJionNumber(String jionNumber) {
        this.jionNumber = jionNumber;
    }

    public String getAllNumber() {
        return allNumber;
    }

    public void setAllNumber(String allNumber) {
        this.allNumber = allNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(type);
        dest.writeString(jionNumber);
        dest.writeString(allNumber);
        dest.writeString(description);
        dest.writeString(smiallImageUrl);
    }

    public static final Parcelable.Creator<ClubCampaign> CREATOR = new Creator<ClubCampaign>() {
        @Override
        public ClubCampaign createFromParcel(Parcel source) {
            return new ClubCampaign(source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),
                    source.readString(),source.readString(),source.readString());
        }

        @Override
        public ClubCampaign[] newArray(int size) {
            return new ClubCampaign[0];
        }
    };


    @Override
    public String toString() {
        return "ClubCampaign{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", jionNumber='" + jionNumber + '\'' +
                ", allNumber='" + allNumber + '\'' +
                ", description='" + description + '\'' +
                ", smiallImageUrl='" + smiallImageUrl + '\'' +
                '}';
    }
}
