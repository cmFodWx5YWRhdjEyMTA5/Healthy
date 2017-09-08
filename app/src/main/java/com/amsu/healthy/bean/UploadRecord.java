package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2017/3/19.
 */

public class UploadRecord implements Parcelable,Cloneable {
    public int fi;                      //疲劳指数(Fatigue deviceType)
    public int es;                      //	情绪状态(emotional state)
    public int pi;         //压力指数(Pressure deviceType)
    public int cc;       //抗压能力(Compressive capacity)
    public String hrvr;       //hrv分析结果(hrv result)
    public String hrvs;       //hrv健康建议(hrv suggest)
    public int ahr;       //平均心率(average heart rate)
    public int maxhr;       //最大心率(maximal heart rate)
    public int minhr;       //L最小心率(Minimum heart rate)
    public String hrr;       //心率分析结果(heart rater result)
    public String hrs;       //心率健康建议(heart rater suggest)
    public String ec;       //心电数据(electrocardio)
    public int ecr;       //心电分析结果(electrocardioresult)(1正常心电，2异常心电，3漏博，4早博)
    public String ecs;       //心电健康建议(electrocardio suggest)
    public int ra;                        //心率恢能力（recovery ability）
    public long timestamp;       //
    public String datatime;       //
    public List<Integer> hr;                        //
    public List<Integer> ae;                        //
    public double distance;       //
    public long time;                        //
    public List<Integer> cadence;       //
    public List<String> calorie;       //
    public int state;                        //
    public int zaobo;
    public int loubo;
    public List<ParcelableDoubleList> latitudeLongitude;
    public int uploadState;
    public long id;
    public String localEcgFileName;
    public int inuse ;

    @Override
    public Object clone() throws CloneNotSupportedException {
        UploadRecord o = null;
        try{
            o = (UploadRecord)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return o;
    }

    public UploadRecord() {
        this.fi = 0;
        this.es = 0;
        this.pi = 0;
        this.cc = 0;
        this.hrvr = "";
        this.hrvs = "";
        this.ahr = 0;
        this.maxhr = 0;
        this.minhr = 0;
        this.hrr = "";
        this.hrs = "";
        this.ec = "";
        this.ecr = 0;
        this.ecs = "";
        this.ra = 0;
        this.timestamp = 0;
        this.datatime = "";
        this.hr = new ArrayList<>();
        this.ae = new ArrayList<>();
        this.distance = 0.0;
        this.time = 0;
        this.cadence = new ArrayList<>();
        this.calorie = new ArrayList<>();
        this.state = 0;
        this.zaobo = 0;
        this.loubo = 0;
        this.latitudeLongitude = new ArrayList<>();
        this.uploadState = 0;
        this.id = 0;
        this.localEcgFileName = "";
        this.inuse =  0;
    }

    /*public UploadRecord(Context context) {
        fi = Constant.uploadRecordDefaultInt;
        es = Constant.uploadRecordDefaultInt;
        pi = Constant.uploadRecordDefaultInt;
        cc = Constant.uploadRecordDefaultInt;
        hrvr = Constant.uploadRecordDefaultString;
        hrvs = context.getResources().getString(R.string.HeartRate_suggetstion_nodata);
        ahr = Constant.uploadRecordDefaultInt;
        maxhr = Constant.uploadRecordDefaultInt;
        minhr = Constant.uploadRecordDefaultInt;
        hrr =Constant.uploadRecordDefaultString;
        hrs =context.getResources().getString(R.string.HeartRate_suggetstion_nodata);
        ec = Constant.uploadRecordDefaultString;
        ecr =0;
        ecs =context.getResources().getString(R.string.HeartRate_suggetstion_nodata);
        ra = Constant.uploadRecordDefaultInt;
        timestamp =Constant.uploadRecordDefaultInt;
        datatime = Constant.uploadRecordDefaultInt;
        hr = "-1";
        ae = Constant.uploadRecordDefaultInt;
        distance = Constant.uploadRecordDefaultInt;
        time = Constant.uploadRecordDefaultInt;
        cadence = Constant.uploadRecordDefaultInt;
        calorie = Constant.uploadRecordDefaultInt;
        state = Constant.uploadRecordDefaultInt;
        zaobo = Constant.uploadRecordDefaultInt;
        loubo = Constant.uploadRecordDefaultInt;
        latitude_longitude = Constant.uploadRecordDefaultString;
    }*/

    public UploadRecord(int fi, int es, int pi, int cc, String hrvr, String hrvs, int ahr, int maxhr, int minhr, String hrr, String hrs, String ec, int ecr, String ecs, int ra, long timestamp, String datatime, List<Integer> hr, List<Integer> ae, double distance, long time, List<Integer> cadence, List<String> calorie, int state, int zaobo, int loubo, List<ParcelableDoubleList> latitudeLongitude, int uploadState, long id, String localEcgFileName, int inuse) {
        this.fi = fi;
        this.es = es;
        this.pi = pi;
        this.cc = cc;
        this.hrvr = hrvr;
        this.hrvs = hrvs;
        this.ahr = ahr;
        this.maxhr = maxhr;
        this.minhr = minhr;
        this.hrr = hrr;
        this.hrs = hrs;
        this.ec = ec;
        this.ecr = ecr;
        this.ecs = ecs;
        this.ra = ra;
        this.timestamp = timestamp;
        this.datatime = datatime;
        this.hr = hr;
        this.ae = ae;
        this.distance = distance;
        this.time = time;
        this.cadence = cadence;
        this.calorie = calorie;
        this.state = state;
        this.zaobo = zaobo;
        this.loubo = loubo;
        this.latitudeLongitude = latitudeLongitude;
        this.uploadState = uploadState;
        this.id = id;
        this.localEcgFileName = localEcgFileName;
        this.inuse = inuse;
    }


    @Override
    public String toString() {
        return "UploadRecord{" +
                "fi=" + fi +
                ", es=" + es +
                ", pi=" + pi +
                ", cc=" + cc +
                ", hrvr='" + hrvr + '\'' +
                ", hrvs='" + hrvs + '\'' +
                ", ahr=" + ahr +
                ", maxhr=" + maxhr +
                ", minhr=" + minhr +
                ", hrr='" + hrr + '\'' +
                ", hrs='" + hrs + '\'' +
                ", ec='" + ec + '\'' +
                ", ecr=" + ecr +
                ", ecs='" + ecs + '\'' +
                ", ra=" + ra +
                ", timestamp=" + timestamp +
                ", datatime='" + datatime + '\'' +
                ", hr=" + hr +
                ", ae=" + ae +
                ", distance=" + distance +
                ", time=" + time +
                ", cadence=" + cadence +
                ", calorie=" + calorie +
                ", state=" + state +
                ", zaobo=" + zaobo +
                ", loubo=" + loubo +
                ", latitudeLongitude=" + latitudeLongitude +
                ", uploadState=" + uploadState +
                ", id=" + id +
                ", localEcgFileName='" + localEcgFileName + '\'' +
                ", inuse=" + inuse +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(fi);
        dest.writeInt(es);
        dest.writeInt(pi);
        dest.writeInt(cc);
        dest.writeString(hrvr);
        dest.writeString(hrvs);
        dest.writeInt(ahr);
        dest.writeInt(maxhr);
        dest.writeInt(minhr);
        dest.writeString(hrr);
        dest.writeString(hrs);
        dest.writeString(ec);
        dest.writeInt(ecr);
        dest.writeString(ecs);
        dest.writeInt(ra);
        dest.writeLong(timestamp);
        dest.writeString(datatime);
        dest.writeList(hr);
        dest.writeList(ae);
        dest.writeDouble(distance);
        dest.writeLong(time);
        dest.writeList(cadence);
        dest.writeList(calorie);
        dest.writeInt(state);
        dest.writeInt(zaobo);
        dest.writeInt(loubo);
        dest.writeTypedList(latitudeLongitude);
        dest.writeInt(uploadState);
        dest.writeLong(id);
        dest.writeString(localEcgFileName);
        dest.writeInt(inuse);
    }

    public static final Creator<UploadRecord> CREATOR = new Creator<UploadRecord>() {
        @Override
        public UploadRecord createFromParcel(Parcel source) {
            return new UploadRecord(source);
        }

        @Override
        public UploadRecord[] newArray(int size) {
            return new UploadRecord[size];
        }
    };


    public UploadRecord(Parcel source) {
        this.fi = source.readInt();
        this.es = source.readInt();
        this.pi = source.readInt();
        this.cc = source.readInt();
        this.hrvr = source.readString();
        this.hrvs = source.readString();
        this.ahr = source.readInt();
        this.maxhr = source.readInt();
        this.minhr = source.readInt();
        this.hrr = source.readString();
        this.hrs = source.readString();
        this.ec = source.readString();
        this.ecr = source.readInt();
        this.ecs = source.readString();
        this.ra = source.readInt();
        this.timestamp = source.readLong();
        this.datatime = source.readString();

        this.hr = new ArrayList<>();
        source.readList(this.hr,null);

        this.ae = new ArrayList<>();
        source.readList(this.ae,null);

        this.distance = source.readDouble();
        this.time = source.readLong();

        this.cadence = new ArrayList<>();
        source.readList(this.cadence,null);

        this.calorie = new ArrayList<>();
        source.readList(this.calorie,null);

        this.state = source.readInt();
        this.zaobo = source.readInt();
        this.loubo = source.readInt();

        this.latitudeLongitude = new ArrayList<>();
        source.readTypedList(this.latitudeLongitude,ParcelableDoubleList.CREATOR);

        this.uploadState = source.readInt();
        this.id = source.readLong();
        this.localEcgFileName = source.readString();
        this.inuse =  source.readInt();
    }
}
