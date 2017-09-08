package com.amsu.healthy.bean;

import java.util.Arrays;
import java.util.List;

/**
 * Created by HP on 2017/3/21.
 */

public class FullReport {

    public List<HRrepBean> HRrep;
    public float[] ECrep;
    public List<HRRrepBean> HRRrep;
    public List<HRVrepBean> HRVrep;
    public List<List<String>> HRlist;

    public  class HRrepBean{
        public String id;
        public long datatime;
        public int ahr;
        public long timestamp;

        @Override
        public String toString() {
            return "HRrepBean{" +
                    "id='" + id + '\'' +
                    ", datatime='" + datatime + '\'' +
                    ", ahr='" + ahr + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    public  class HRRrepBean{
        public String id;
        public long datatime;
        public int ra;
        public long timestamp;

        @Override
        public String toString() {
            return "ReportItem{" +
                    "id='" + id + '\'' +
                    ", datatime='" + datatime + '\'' +
                    ", ahr='" + ra + '\'' +
                    '}';
        }
    }

    public  class HRVrepBean{
        public String id;
        public long datatime;
        public int fi;
        public long timestamp;

        @Override
        public String toString() {
            return "ReportItem{" +
                    "id='" + id + '\'' +
                    ", datatime='" + datatime + '\'' +
                    ", ahr='" + fi + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FullReport{" +
                "HRrep=" + HRrep +
                ", ECrep=" + Arrays.toString(ECrep) +
                ", HRRrep=" + HRRrep +
                ", HRVrep=" + HRVrep +
                ", HRlist=" + HRlist +
                '}';
    }
}
