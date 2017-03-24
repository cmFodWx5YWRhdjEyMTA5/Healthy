package com.amsu.healthy.utils;

/**
 * Created by HP on 2016/11/30.
 */
public class Constant {
    public static String tokenKey = "7BmznrYNrA4CmhuqpdEs5doiLA3U";

    public static int MODIFY_USERNSME = 0;
    public static int MODIFY_SEX = 1;
    public static int MODIFY_EMAIL = 2;
    public static int MODIFY_PHONE = 3;

    public static String writeConfigureOrder = "FF010A100C080E010016";
    public static String openDataTransmitOrder = "FF0206010016";
    public static String stopDataTransmitOrder = "FF0206000016";
    public static String checkIsHaveDataOrder = "FF05050016";  //4.6	查询SD卡是否有数据

    //public static String loginURL = "https://bodylistener.amsu-new.com/intellingence/LoginController/phoneVerify";
    public static String loginURL = "http://192.168.0.109:8080/intellingence-web/phoneVerify.jsp";
    public static String uploadIconURL = "https://bodylistener.amsu-new.com/intellingence/UserinfoController/do_upload";
    public static String downloadPersionDataURL = "https://bodylistener.amsu-new.com/intellingence/UserinfoController/readUserinfo";
    public static String duploadPersionDataURL = "https://bodylistener.amsu-new.com/intellingence/UserinfoController/uploadUserinfo";

    public static String setHealthyPlanURL = "https://bodylistener.amsu-new.com/intellingence/PlanningController/setPlanning";
    public static String modifyHealthyPlanURL = "https://bodylistener.amsu-new.com/intellingence/PlanningController/modifyPlanning";
    public static String getHealthyPlanListURL = "https://bodylistener.amsu-new.com/intellingence/PlanningController/planningList";
    public static String getHealthyPlanContentURL = "https://bodylistener.amsu-new.com/intellingence/PlanningController/planningCont";
    public static String getOneDayHealthyPlanListURL = "https://bodylistener.amsu-new.com/intellingence/PlanningController/planningDateList";
    public static String getAfter20ItemHealthyPlanListURL = "https://bodylistener.amsu-new.com/intellingence/PlanningController/planningPastList";
    public static String uploadReportURL = "http://bodylistener.amsu-new.com:83/intellingence/ReportController/uploadReport";
    public static String downloadQuarterReportURL = "https://bodylistener.amsu-new.com/intellingence/ReportController/downloadQuarterReport";
    public static String downloadMonthReportURL = "https://bodylistener.amsu-new.com/intellingence/ReportController/downloadMonthReport";
    public static String downloadYearReportURL = "https://bodylistener.amsu-new.com/intellingence/ReportController/downloadYearReport";
    public static String getHistoryReportListURL = "https://bodylistener.amsu-new.com/intellingence/ReportController/getReportList";
    public static String getHistoryReportDetailURL = "https://bodylistener.amsu-new.com/intellingence/ReportController/getDetail";

    public static String checkAppUpdateURL = "https://bodylistener.amsu-new.com/intellingence/VersionController/getLastVersion";

    public static long AnimatorDuration = 2000;  //动画时长，2s

}
