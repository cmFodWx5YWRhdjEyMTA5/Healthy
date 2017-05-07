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
    public static int MODIFY_STILLRATE = 3;

    public static String writeConfigureOrder = "FF010A100C080E010016";
    public static String openDataTransmitOrder = "FF0206010016";
    public static String stopDataTransmitOrder = "FF0206000016";
    public static String checkIsHaveDataOrder = "FF05050016";  //4.6	查询SD卡是否有数据
    public static String synchronizeOrder = "FF05050016";  //4.6	设备同步

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
    public static String downloadWeekReportURL = "https://bodylistener.amsu-new.com/intellingence/ReportController/downloadWeekReport";
    public static String addSosContact = "https://bodylistener.amsu-new.com/intellingence/UserinfoController/addContacts";
    public static String getAllContacts = "https://bodylistener.amsu-new.com/intellingence/UserinfoController/getContactsAndMessage";
    public static String setSosMessage = "https://bodylistener.amsu-new.com/intellingence/UserinfoController/setMessage";
    public static String deleteSosContact = "https://bodylistener.amsu-new.com/intellingence/UserinfoController/deleteContact";

    //public static String checkAppUpdateURL = "https://bodylistener.amsu-new.com/intellingence/VersionController/getLastVersion";
    public static String checkAppUpdateURL = "http://www.amsu-new.com:8081/intellingence-web/getLastVersion.do";

    public static long AnimatorDuration = 2000;  //动画时长，2s

    public static int oneSecondFrame = 150; //帧为150，即1s有150个数据点

    public static String currectDeviceLEName = "currectDeviceLEName";
    public static String sportState = "sportState";
    public static String isLookupECGDataFromSport = "isLookupECGDataFromSport";
    public static String heartDataList_static = "heartDataList_static";
    public static String heartDataList_athletic = "heartDataList_athletic";
    public static String mKcalData = "mKcalData";
    public static String mStridefreData = "mStridefreData";
    public static String mSpeedStringListData = "mSpeedStringListData";
    public static String sportCreateRecordID = "sportCreateRecordID";
    public static int SPORTSTATE_STATIC = 0;
    public static int SPORTSTATE_ATHLETIC = 1;
    public static String hrr = "hrr";
    public static String ecgFiletimeMillis = "ecgFiletimeMillis";
    public static String restingHR = "restingHR";
    public static String sosinfo = "sosinfo";

    public static String uploadRecordDefaultString = "0";

}
