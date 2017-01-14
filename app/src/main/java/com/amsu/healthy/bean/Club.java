package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP on 2017/1/12.
 */

public class Club  implements Parcelable {
    private String name;
    private String type;
    private String number;
    private String simallImageUrl;

    public Club(String name, String type, String number, String simallImageUrl) {
        this.name = name;
        this.type = type;
        this.number = number;
        this.simallImageUrl = simallImageUrl;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSimallImageUrl() {
        return simallImageUrl;
    }

    public void setSimallImageUrl(String simallImageUrl) {
        this.simallImageUrl = simallImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(number);
        dest.writeString(simallImageUrl);
    }

    public static final Parcelable.Creator<Club> CREATOR = new Creator<Club>() {
        @Override
        public Club createFromParcel(Parcel source) {
            return new Club(source.readString(),source.readString(),source.readString(),source.readString());
        }

        @Override
        public Club[] newArray(int size) {
            return new Club[0];
        }
    };
}
