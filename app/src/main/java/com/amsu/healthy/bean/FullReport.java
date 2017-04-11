package com.amsu.healthy.bean;

import java.util.Arrays;
import java.util.List;

/**
 * Created by HP on 2017/3/21.
 */

public class FullReport {
    public String ret;
    public Result errDesc;

    public class Result{
        /*List<ReportItem> HRrep;
        List<Integer> ECrep;
        List<ReportItem> HRRrep;
        List<ReportItem> HRVrep;*/
        public List<HRrepBean> HRrep;
        public int[] ECrep;
        public List<HRRrepBean> HRRrep;
        public List<HRVrepBean> HRVrep;
        public List<List<String>> HRlist;

		@Override
		public String toString() {
			return "Result [HRrep=" + HRrep + ", ECrep="
					+ Arrays.toString(ECrep) + ", HRRrep=" + HRRrep
					+ ", HRVrep=" + HRVrep + ", HRlist=" + HRlist + "]";
		}
    }



    public  class HRrepBean{
        public String id;
        public String datatime;
        public String AHR;

        @Override
        public String toString() {
            return "ReportItem{" +
                    "id='" + id + '\'' +
                    ", datatime='" + datatime + '\'' +
                    ", AHR='" + AHR + '\'' +
                    '}';
        }
    }

    public  class HRRrepBean{
        public String id;
        public String datatime;
        public String RA;

        @Override
        public String toString() {
            return "ReportItem{" +
                    "id='" + id + '\'' +
                    ", datatime='" + datatime + '\'' +
                    ", AHR='" + RA + '\'' +
                    '}';
        }
    }

    public  class HRVrepBean{
        public String id;
        public String datatime;
        public String FI;

        @Override
        public String toString() {
            return "ReportItem{" +
                    "id='" + id + '\'' +
                    ", datatime='" + datatime + '\'' +
                    ", AHR='" + FI + '\'' +
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
