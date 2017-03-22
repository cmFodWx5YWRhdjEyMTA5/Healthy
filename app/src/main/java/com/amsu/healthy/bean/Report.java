package com.amsu.healthy.bean;

import java.util.List;

/**
 * Created by HP on 2017/3/21.
 */

public class Report {
    private String ret;
    private Result errDesc;

    class Result{
        List<ReportItem> HRrep;
        List<Integer> ECrep;
        List<ReportItem> HRRrep;
        List<ReportItem> HRVrep;

        @Override
        public String toString() {
            return "Result{" +
                    "HRrep=" + HRrep +
                    ", ECrep=" + ECrep +
                    ", HRRrep=" + HRRrep +
                    ", HRVrep=" + HRVrep +
                    '}';
        }
    }
    class ReportItem{
        String id;
        String datatime;
        String AHR;

        @Override
        public String toString() {
            return "ReportItem{" +
                    "id='" + id + '\'' +
                    ", datatime='" + datatime + '\'' +
                    ", AHR='" + AHR + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Report{" +
                "ret='" + ret + '\'' +
                ", errDesc=" + errDesc +
                '}';
    }
}
