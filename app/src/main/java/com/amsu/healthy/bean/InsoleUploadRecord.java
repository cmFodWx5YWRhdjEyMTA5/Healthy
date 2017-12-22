package com.amsu.healthy.bean;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.bean
 * @time 9/19/2017 3:46 PM
 * @describe
 */
public class InsoleUploadRecord {
    public int ret;
    public InsoleRecordResult errDesc;

    public InsoleUploadRecord() {
        this.errDesc = new InsoleRecordResult();
    }

    public class InsoleRecordResult{
        public ShoepadData ShoepadData;
        public InsoleAnalyResult ShoepadResult;

        public InsoleRecordResult() {
            ShoepadData = new ShoepadData();
            ShoepadResult = new InsoleAnalyResult();
        }

        public class ShoepadData{
            public long creationtime ;
            public float distance;
            public long duration;
            public float maxspeed;
            public float averagespeed;
            public String speedallocationarray;  //配速数组
            public float calorie;
            public String stepratearray;  //步频数组
            public String trajectory;  //轨迹数组
            public String stepheigh;  //
            public String tag;  //

            public ShoepadData() {
                this.creationtime = 0;
                this.distance = 0;
                this.duration = 0;
                this.maxspeed = 0;
                this.averagespeed = 0;
                this.speedallocationarray = "";
                this.calorie = 0;
                this.stepratearray = "";
                this.trajectory = "";
                this.stepheigh = "";
                this.tag = "";
            }

            @Override
            public String toString() {
                return "ShoepadData{" +
                        "creationtime=" + creationtime +
                        ", distance=" + distance +
                        ", duration=" + duration +
                        ", maxspeed=" + maxspeed +
                        ", averagespeed=" + averagespeed +
                        ", speedallocationarray='" + speedallocationarray + '\'' +
                        ", calorie=" + calorie +
                        ", stepratearray='" + stepratearray + '\'' +
                        ", trajectory='" + trajectory + '\'' +
                        ", stepheigh='" + stepheigh + '\'' +
                        ", tag='" + tag + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "InsoleRecordResult{" +
                    "ShoepadData=" + ShoepadData +
                    ", ShoepadResult=" + ShoepadResult +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "InsoleUploadRecord{" +
                "ret='" + ret + '\'' +
                ", errDesc=" + errDesc +
                '}';
    }
}
