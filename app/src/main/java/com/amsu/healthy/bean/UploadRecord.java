package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.amsu.healthy.utils.Constant;

/**
 * Created by HP on 2017/3/19.
 */

public class UploadRecord implements Parcelable {
    public String FI;                      //疲劳指数(Fatigue index)
    public String ES;                      //	情绪状态(emotional state)
    public String PI;         //压力指数(Pressure index)
    public String CC;       //抗压能力(Compressive capacity)
    public String HRVr;       //hrv分析结果(hrv result)
    public String HRVs;       //hrv健康建议(hrv suggest)
    public String AHR;       //平均心率(average heart rate)
    public String MaxHR;       //最大心率(maximal heart rate)
    public String MinHR;       //L最小心率(Minimum heart rate)
    public String HRr;       //心率分析结果(heart rater result)
    public String HRs;       //心率健康建议(heart rater suggest)
    public String EC;       //心电数据(electrocardio)
    public String ECr;       //心电分析结果(electrocardioresult)(1正常心电，2异常心电，3漏博，4早博)
    public String ECs;       //心电健康建议(electrocardio suggest)
    public String RA;                        //心率恢能力（recovery ability）
    public String timestamp;       //
    public String datatime;       //
    public String HR;                        //
    public String AE;                        //
    public String distance;       //
    public String time;                        //
    public String cadence;       //
    public String calorie;       //
    public String state;                        //
    public String zaobo;
    public String loubo;
    public String latitude_longitude;
    public String uploadState;

    public String id;
    public String serveId;

    public UploadRecord() {
        FI = Constant.uploadRecordDefaultString;
        ES = Constant.uploadRecordDefaultString;
        PI = Constant.uploadRecordDefaultString;
        CC = Constant.uploadRecordDefaultString;
        HRVr = Constant.uploadRecordDefaultString;
        HRVs = Constant.uploadRecordDefaultString;
        AHR = Constant.uploadRecordDefaultString;
        MaxHR = Constant.uploadRecordDefaultString;
        MinHR = Constant.uploadRecordDefaultString;
        HRr =Constant.uploadRecordDefaultString;
        HRs ="心率健康建议";
        EC = Constant.uploadRecordDefaultString;
        ECr ="1";
        ECs ="心电健康建议！";
        RA = Constant.uploadRecordDefaultString;
        timestamp =Constant.uploadRecordDefaultString;
        datatime = Constant.uploadRecordDefaultString;
        HR = "-1";
        AE = Constant.uploadRecordDefaultString;
        distance = Constant.uploadRecordDefaultString;
        time = Constant.uploadRecordDefaultString;
        cadence = Constant.uploadRecordDefaultString;
        calorie = Constant.uploadRecordDefaultString;
        state = Constant.uploadRecordDefaultString;
        zaobo = Constant.uploadRecordDefaultString;
        loubo = Constant.uploadRecordDefaultString;
        latitude_longitude = Constant.uploadRecordDefaultString;
    }

    public UploadRecord(String FI, String ES, String PI, String CC, String HRVr, String HRVs, String AHR, String maxHR, String minHR, String HRr, String HRs, String EC, String ECr, String ECs, String RA, String timestamp, String datatime) {
        this.FI = FI;
        this.ES = ES;
        this.PI = PI;
        this.CC = CC;
        this.HRVr = HRVr;
        this.HRVs = HRVs;
        this.AHR = AHR;
        MaxHR = maxHR;
        MinHR = minHR;
        this.HRr = HRr;
        this.HRs = HRs;
        this.EC = EC;
        this.ECr = ECr;
        this.ECs = ECs;
        this.RA = RA;
        this.timestamp = timestamp;
        this.datatime = datatime;
    }

    public UploadRecord(String FI, String ES, String PI, String CC, String HRVr, String HRVs, String AHR, String maxHR, String minHR, String HRr, String HRs, String EC, String ECr, String ECs, String RA, String timestamp, String datatime, String HR, String AE, String distance, String time, String cadence, String calorie, String state) {
        this.FI = FI;
        this.ES = ES;
        this.PI = PI;
        this.CC = CC;
        this.HRVr = HRVr;
        this.HRVs = HRVs;
        this.AHR = AHR;
        MaxHR = maxHR;
        MinHR = minHR;
        this.HRr = HRr;
        this.HRs = HRs;
        this.EC = EC;
        this.ECr = ECr;
        this.ECs = ECs;
        this.RA = RA;
        this.timestamp = timestamp;
        this.datatime = datatime;
        this.HR = HR;
        this.AE = AE;
        this.distance = distance;
        this.time = time;
        this.cadence = cadence;
        this.calorie = calorie;
        this.state = state;
    }

    public UploadRecord(String FI, String ES, String PI, String CC, String HRVr, String HRVs, String AHR, String maxHR, String minHR, String HRr, String HRs, String EC, String ECr, String ECs, String RA, String timestamp, String datatime, String HR, String AE, String distance, String time, String cadence, String calorie, String state, String zaobo, String loubo, String latitude_longitude) {
        this.FI = FI;
        this.ES = ES;
        this.PI = PI;
        this.CC = CC;
        this.HRVr = HRVr;
        this.HRVs = HRVs;
        this.AHR = AHR;
        MaxHR = maxHR;
        MinHR = minHR;
        this.HRr = HRr;
        this.HRs = HRs;
        this.EC = EC;
        this.ECr = ECr;
        this.ECs = ECs;
        this.RA = RA;
        this.timestamp = timestamp;
        this.datatime = datatime;
        this.HR = HR;
        this.AE = AE;
        this.distance = distance;
        this.time = time;
        this.cadence = cadence;
        this.calorie = calorie;
        this.state = state;
        this.zaobo = zaobo;
        this.loubo = loubo;
        this.latitude_longitude = latitude_longitude;
    }

    public String getFI() {
        return FI;
    }

    public void setFI(String FI) {
        this.FI = FI;
    }

    public String getES() {
        return ES;
    }

    public void setES(String ES) {
        this.ES = ES;
    }

    public String getPI() {
        return PI;
    }

    public void setPI(String PI) {
        this.PI = PI;
    }

    public String getCC() {
        return CC;
    }

    public void setCC(String CC) {
        this.CC = CC;
    }

    public String getHRVr() {
        return HRVr;
    }

    public void setHRVr(String HRVr) {
        this.HRVr = HRVr;
    }

    public String getHRVs() {
        return HRVs;
    }

    public void setHRVs(String HRVs) {
        this.HRVs = HRVs;
    }

    public String getAHR() {
        return AHR;
    }

    public void setAHR(String AHR) {
        this.AHR = AHR;
    }

    public String getMaxHR() {
        return MaxHR;
    }

    public void setMaxHR(String maxHR) {
        MaxHR = maxHR;
    }

    public String getMinHR() {
        return MinHR;
    }

    public void setMinHR(String minHR) {
        MinHR = minHR;
    }

    public String getHRr() {
        return HRr;
    }

    public void setHRr(String HRr) {
        this.HRr = HRr;
    }

    public String getHRs() {
        return HRs;
    }

    public void setHRs(String HRs) {
        this.HRs = HRs;
    }

    public String getEC() {
        return EC;
    }

    public void setEC(String EC) {
        this.EC = EC;
    }

    public String getECr() {
        return ECr;
    }

    public void setECr(String ECr) {
        this.ECr = ECr;
    }

    public String getECs() {
        return ECs;
    }

    public void setECs(String ECs) {
        this.ECs = ECs;
    }

    public String getRA() {
        return RA;
    }

    public void setRA(String RA) {
        this.RA = RA;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDatatime() {
        return datatime;
    }

    public void setDatatime(String datatime) {
        this.datatime = datatime;
    }

    public String getHR() {
        return HR;
    }

    public void setHR(String HR) {
        this.HR = HR;
    }

    public String getAE() {
        return AE;
    }

    public void setAE(String AE) {
        this.AE = AE;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCadence() {
        return cadence;
    }

    public void setCadence(String cadence) {
        this.cadence = cadence;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZaobo() {
        return zaobo;
    }

    public void setZaobo(String zaobo) {
        this.zaobo = zaobo;
    }

    public String getLoubo() {
        return loubo;
    }

    public void setLoubo(String loubo) {
        this.loubo = loubo;
    }

    public String getLatitude_longitude() {
        return latitude_longitude;
    }

    public void setLatitude_longitude(String latitude_longitude) {
        this.latitude_longitude = latitude_longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FI);
        dest.writeString(ES);
        dest.writeString(PI);
        dest.writeString(CC);
        dest.writeString(HRVr);
        dest.writeString(HRVs);
        dest.writeString(AHR);
        dest.writeString(MaxHR);
        dest.writeString(MinHR);
        dest.writeString(HRr);
        dest.writeString(HRs);
        dest.writeString(EC);
        dest.writeString(ECr);
        dest.writeString(ECs);
        dest.writeString(RA);
        dest.writeString(timestamp);
        dest.writeString(datatime);
        dest.writeString(HR);
        dest.writeString(AE);
        dest.writeString(distance);
        dest.writeString(time);
        dest.writeString(cadence);
        dest.writeString(calorie);
        dest.writeString(state);
        dest.writeString(zaobo);
        dest.writeString(loubo);
        dest.writeString(latitude_longitude);
    }

    public static final Creator<UploadRecord> CREATOR = new Creator<UploadRecord>() {
        @Override
        public UploadRecord createFromParcel(Parcel source) {
            return new UploadRecord(source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),
                    source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),
                    source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),
                    source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),
                    source.readString(),source.readString(),source.readString(),source.readString());
        }

        @Override
        public UploadRecord[] newArray(int size) {
            return new UploadRecord[0];
        }
    };

    @Override
    public String toString() {
        return "UploadRecord{" +
                "FI='" + FI + '\'' +
                ", ES='" + ES + '\'' +
                ", PI='" + PI + '\'' +
                ", CC='" + CC + '\'' +
                ", HRVr='" + HRVr + '\'' +
                ", HRVs='" + HRVs + '\'' +
                ", AHR='" + AHR + '\'' +
                ", MaxHR='" + MaxHR + '\'' +
                ", MinHR='" + MinHR + '\'' +
                ", HRr='" + HRr + '\'' +
                ", HRs='" + HRs + '\'' +
                ", EC='" + EC + '\'' +
                ", ECr='" + ECr + '\'' +
                ", ECs='" + ECs + '\'' +
                ", RA='" + RA + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", datatime='" + datatime + '\'' +
                ", HR='" + HR + '\'' +
                ", AE='" + AE + '\'' +
                ", distance='" + distance + '\'' +
                ", time='" + time + '\'' +
                ", cadence='" + cadence + '\'' +
                ", calorie='" + calorie + '\'' +
                ", state='" + state + '\'' +
                ", zaobo='" + zaobo + '\'' +
                ", loubo='" + loubo + '\'' +
                ", latitude_longitude='" + latitude_longitude + '\'' +
                ", uploadState='" + uploadState + '\'' +
                ", id=" + id +
                ", serveId=" + serveId +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUploadState() {
        return uploadState;
    }

    public void setUploadState(String uploadState) {
        this.uploadState = uploadState;
    }

    public String getServeId() {
        return serveId;
    }

    public void setServeId(String serveId) {
        this.serveId = serveId;
    }
}
