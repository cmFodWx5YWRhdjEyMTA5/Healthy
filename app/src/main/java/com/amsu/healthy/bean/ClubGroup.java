package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP on 2017/1/13.
 */

public class ClubGroup implements Parcelable {
    private String name;
    private String type;
    private String description;
    private String number;
    private String smiallImageUrl;


    public ClubGroup(String name, String type, String description, String number, String smiallImageUrl) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.number = number;
        this.smiallImageUrl = smiallImageUrl;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeString(number);
        dest.writeString(smiallImageUrl);
    }

    public static final Creator<ClubGroup> CREATOR = new Creator<ClubGroup>() {
        @Override
        public ClubGroup createFromParcel(Parcel source) {
            return new ClubGroup(source.readString(),source.readString(),source.readString(),source.readString(),source.readString());
        }

        @Override
        public ClubGroup[] newArray(int size) {
            return new ClubGroup[0];
        }
    };
}
