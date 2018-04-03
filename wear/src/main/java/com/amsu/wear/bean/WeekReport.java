package com.amsu.wear.bean;

import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.bean
 * @time 9/2/2017 9:38 AM
 * @describe
 */
public  class WeekReport {
    public String ret;
    public WeekReportResult errDesc;

    public class WeekReportResult{
        public String chubeijiankang;
        public List<Zaoboloubo> zaoboloubo;
        public List<String> guosuguohuan;
        public List<String> kangpilaozhishu;
        public List<String> huifuxinlv;
        public List<HistoryRecordItem> list;

        public class HistoryRecordItem {
            public String id;
            public long timestamp;
            public int state;

            @Override
            public String toString() {
                return "HistoryRecordItem{" +
                        "ID=" + id +
                        ", timestamp=" + timestamp +
                        ", state=" + state +
                        '}';
            }
        }

        public class Zaoboloubo{
            public int zaoboTimes;
            public int louboTimes;

            @Override
            public String toString() {
                return "Zaoboloubo{" +
                        "zaoboTimes=" + zaoboTimes +
                        ", louboTimes=" + louboTimes +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "WeekReport [chubeijiankang=" + chubeijiankang
                    + ", zaoboloubo=" + zaoboloubo + ", guosuguohuan="
                    + guosuguohuan + ", kangpilaozhishu=" + kangpilaozhishu
                    + ", huifuxinlv=" + huifuxinlv + ", list=" + list + "]";
        }
    }

    @Override
    public String toString() {
        return "WeekReport [ret=" + ret + ", errDesc=" + errDesc + "]";
    }
}