package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP on 2017/1/13.
 */

public class ClubNumber implements Parcelable {
    private String pickName;
    private String realName;
    private String city;
    private String sex;
    private String phone;
    private String introduction;
    private String coachType ;
    private String smiallImageUrl;

    public ClubNumber(String pickName, String realName, String city, String sex, String phone, String introduction, String coachType, String smiallImageUrl) {
        this.pickName = pickName;
        this.realName = realName;
        this.city = city;
        this.sex = sex;
        this.phone = phone;
        this.introduction = introduction;
        this.coachType = coachType;
        this.smiallImageUrl = smiallImageUrl;
    }

    public String getSmiallImageUrl() {
        return smiallImageUrl;
    }

    public void setSmiallImageUrl(String smiallImageUrl) {
        this.smiallImageUrl = smiallImageUrl;
    }

    public String getPickName() {
        return pickName;
    }

    public void setPickName(String pickName) {
        this.pickName = pickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getCoachType() {
        return coachType;
    }

    public void setCoachType(String coachType) {
        this.coachType = coachType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pickName);
        dest.writeString(pickName);
        dest.writeString(city);
        dest.writeString(sex);
        dest.writeString(phone);
        dest.writeString(introduction);
        dest.writeString(coachType);
        dest.writeString(smiallImageUrl);
    }

    public static final Creator<ClubNumber> CREATOR = new Creator<ClubNumber>() {
        @Override
        public ClubNumber createFromParcel(Parcel source) {
            return new ClubNumber(source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),source.readString());
        }

        @Override
        public ClubNumber[] newArray(int size) {
            return new ClubNumber[0];
        }
    };
}
