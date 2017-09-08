package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.bean
 * @time 9/5/2017 11:35 AM
 * @describe 封装的ArrayList<Double>
 */
public class ParcelableDoubleList extends ArrayList<Double> implements Parcelable {

    private static final long serialVersionUID = -8516873361351845306L;

    public ParcelableDoubleList(){
        super();
    }

    protected ParcelableDoubleList(Parcel in) {
        in.readList(this, Double.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this);
    }

    public static final Parcelable.Creator<ParcelableDoubleList> CREATOR =
            new Parcelable.Creator<ParcelableDoubleList>() {
                public ParcelableDoubleList createFromParcel(Parcel in) {
                    return new ParcelableDoubleList(in);
                }

                public ParcelableDoubleList[] newArray(int size) {
                    return new ParcelableDoubleList[size];
                }
            };

}