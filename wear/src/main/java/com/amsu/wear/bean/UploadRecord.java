package com.amsu.wear.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2017/3/19.
 */

/*  加注了@Table的实体类将被映射到sqlite中的数据库表,@Table注解有属性name和onCreated两个属性,name属性决定了该实体类映射的数据库表名,而onCreated属性则可以用来添加表一级的属性或约束,
例如创建联和唯一索引等
    加注了@Column的实体类属性将会映射到sqlite数据库中的字段,@Column注解有name、property、isId、autoGen属性,name属性决定了实体类属性对应的数据库字段名;
property属性可以用来添加数据库中字段一级的属性或约束条件例如not null,索引等;isId属性表示该字段是否是主键,默认为false;autoGen则表示如果一个字段为主键,是否自增长,默认为true,
所以该字段只有在isId属性为true时有效.
    未加注@Column注解的字段将不映射sqlite字段*/

@Table(name = "uploadRecord")
public class UploadRecord implements Parcelable,Cloneable {

    @Column(
            name = "timestamp",
            isId = true,
            autoGen = false
    )
    public long timestamp;   //当前开始运动时间，秒值

    @Column( name = "id")
    public long id;
    @Column(name = "fi")
    public int fi;                      //疲劳指数(Fatigue deviceType)
    @Column(name = "es")
    public double es;                      //	情绪状态(emotional state)
    @Column(name = "pi")
    public int pi;         //压力指数(Pressure deviceType)
    @Column(name = "cc")
    public int cc;       //抗压能力(Compressive capacity)
    @Column(name = "hrvr")
    public String hrvr;       //hrv分析结果(hrv result)
    @Column(name = "hrvs")
    public String hrvs;       //hrv健康建议(hrv suggest)
    @Column(name = "ahr")
    public int ahr;       //平均心率(average heart rate)
    @Column(name = "maxhr")
    public int maxhr;       //最大心率(maximal heart rate)
    @Column(name = "minhr")
    public int minhr;       //L最小心率(Minimum heart rate)
    @Column(name = "hrr")
    public String hrr;       //心率分析结果(heart rater result)
    @Column(name = "hrs")
    public String hrs;       //心率健康建议(heart rater suggest)
    @Column(name = "ec")
    public String ec;       //心电数据(electrocardio)
    @Column(name = "ecr")
    public int ecr;       //心电分析结果(electrocardioresult)(1正常心电，2异常心电，3漏博，4早博)
    @Column(name = "ecs")
    public String ecs;       //心电健康建议(electrocardio suggest)
    @Column(name = "ra")
    public int ra;                        //心率恢能力（recovery ability）
    @Column(name = "datatime")
    public String datatime;       //
    @Column(name = "hr")
    public List<Integer> hr;                        //
    @Column(name = "ae")
    public List<Integer> ae;                        //
    @Column(name = "aeMarathon")
    public String aeMarathon;                        //
    @Column(name = "distance")
    public float distance;       //
    @Column(name = "time")
    public long time;                        //
    @Column(name = "cadence")
    public List<Integer> cadence;       //
    @Column(name = "calorie")
    public List<String> calorie;       //
    @Column(name = "state")
    public int state;                        //
    @Column(name = "zaobo")
    public int zaobo;
    @Column(name = "loubo")
    public int loubo;
    @Column(name = "latitudeLongitude")
    public List<ParcelableDoubleList> latitudeLongitude;
    @Column(name = "uploadState")
    public int uploadState;
    @Column(name = "localEcgFileName")
    public String localEcgFileName;
    @Column(name = "inuse")
    public int inuse ;
    @Column(name = "sportCreateRecordID")
    public long sportCreateRecordID ;
    @Column(name = "sdnn1")
    /*public boolean isOutDoor;*/
    public int sdnn1;
    @Column(name = "sdnn2")
    public int sdnn2;
    @Column(name = "lf1")
    public double  lf1;
    @Column(name = "lf2")
    public double  lf2;
    @Column(name = "hf1")
    public double  hf1;
    @Column(name = "hf2")
    public double  hf2;
    @Column(name = "hf")
    public double  hf;
    @Column(name = "lf")
    public double  lf;
    @Column(name = "chaosPlotPoint")
    public List<Integer> chaosPlotPoint;
    @Column(name = "frequencyDomainDiagramPoint")
    public List<Double> frequencyDomainDiagramPoint;
    @Column(name = "chaosPlotMajorAxis")
    public int chaosPlotMajorAxis;
    @Column(name = "chaosPlotMinorAxis")
    public int chaosPlotMinorAxis;

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
        this.datatime = "2000/08/11 12:11:00";
        this.hr = new ArrayList<>();
        this.ae = new ArrayList<>();
        this.distance = 0.0f;
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
        this.sportCreateRecordID =  0;
        chaosPlotPoint = new ArrayList<>();
        frequencyDomainDiagramPoint = new ArrayList<>();

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

    public UploadRecord(int fi, int es, int pi, int cc, String hrvr, String hrvs, int ahr, int maxhr, int minhr, String hrr, String hrs, String ec, int ecr, String ecs, int ra, long timestamp, String datatime, List<Integer> hr, List<Integer> ae, float distance, long time, List<Integer> cadence, List<String> calorie, int state, int zaobo, int loubo, List<ParcelableDoubleList> latitudeLongitude, int uploadState, long id, String localEcgFileName, int inuse) {
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
                ", sportCreateRecordID=" + sportCreateRecordID +
                ", sdnn1=" + sdnn1 +
                ", sdnn2=" + sdnn2 +
                ", lf1=" + lf1 +
                ", lf2=" + lf2 +
                ", hf1=" + hf1 +
                ", hf2=" + hf2 +
                ", hf=" + hf +
                ", lf=" + lf +
                ", chaosPlotPoint=" + chaosPlotPoint +
                ", frequencyDomainDiagramPoint=" + frequencyDomainDiagramPoint +
                ", chaosPlotMajorAxis=" + chaosPlotMajorAxis +
                ", chaosPlotMinorAxis=" + chaosPlotMinorAxis +
                ", ec='" + ec + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(fi);
        dest.writeDouble(es);
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
        dest.writeFloat(distance);
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
        dest.writeLong(sportCreateRecordID);
        //dest.writeByte((byte) (isOutDoor ? 1 : 0));     //if myBoolean == true, byte == 1
        dest.writeInt(sdnn1);
        dest.writeInt(sdnn2);
        dest.writeDouble(lf1);
        dest.writeDouble(lf2);
        dest.writeDouble(hf1);
        dest.writeDouble(hf2);
        dest.writeDouble(hf);
        dest.writeDouble(lf);
        dest.writeList(chaosPlotPoint);
        dest.writeList(frequencyDomainDiagramPoint);
        dest.writeInt(chaosPlotMajorAxis);
        dest.writeInt(chaosPlotMinorAxis);
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
        this.es = source.readDouble();
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

        this.distance = source.readFloat();
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
        this.sportCreateRecordID = source.readLong();

        //this.isOutDoor = source.readByte() != 0;     //myBoolean == true if byte != 0
        this.sdnn1 = source.readInt();
        this.sdnn2 = source.readInt();
        this.lf1 = source.readDouble();
        this.lf2 = source.readDouble();
        this.hf1 = source.readDouble();
        this.hf2 = source.readDouble();
        this.hf = source.readDouble();
        this.lf = source.readDouble();

        this.chaosPlotPoint = new ArrayList<>();
        source.readList(this.chaosPlotPoint,null);

        this.frequencyDomainDiagramPoint = new ArrayList<>();
        source.readList(this.frequencyDomainDiagramPoint,null);

        this.chaosPlotMajorAxis = source.readInt();
        this.chaosPlotMinorAxis = source.readInt();
    }
}
