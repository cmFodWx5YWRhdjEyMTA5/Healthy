package com.amsu.healthy.utils;

import java.util.UUID;

/**
 * Created by HP on 2016/11/30.
 */
public class Constant {
    public static String loginTokenKey = "7BmznrYNrA4CmhuqpdEs5doiLA3U";

    public static String insoleAlgorithmUsername = "7a1b77fbe02947a293196d72d8d0c869";
    public static String insoleAlgorithmPassword = "622ff39cac1f41478c534d6b08058061";

    public static int MODIFY_USERNSME = 0;
    public static int MODIFY_SEX = 1;
    public static int MODIFY_EMAIL = 2;
    public static int MODIFY_PHONE = 3;
    public static int MODIFY_STILLRATE = 4;

    public static String writeConfigureOrder = "FF010A100C080E010016";
    public static String openDataTransmitOrder = "FF0206010016";
    public static String stopDataTransmitOrder = "FF0206000016";
    public static String readDeviceIDOrder = "FF04050016";
    public static String checkIsHaveDataOrder = "FF05050016";  //4.6	查询SD卡是否有数据
    public static String synchronizeOrder = "FF05050016";  //4.6	设备同步

    //public static String loginURL = "https://119.29.201.120/intellingence/LoginController/phoneVerify";
    public static String loginURL = "http://192.168.0.109:8080/intellingence-web/phoneVerify.jsp";
    public static String uploadIconURL = "https://119.29.201.120/intellingence/UserinfoController/do_upload";
    public static String downloadPersionDataURL = "https://119.29.201.120/intellingence/UserinfoController/readUserinfo";
    public static String phoneVerify = "https://119.29.201.120/intellingence/LoginController/phoneVerify";
    public static String duploadPersionDataURL = "https://119.29.201.120/intellingence/UserinfoController/uploadUserinfo";

    public static String setHealthyPlanURL = "https://119.29.201.120/intellingence/PlanningController/setPlanning";
    public static String modifyHealthyPlanURL = "https://119.29.201.120/intellingence/PlanningController/modifyPlanning";
    public static String getHealthyPlanListURL = "https://119.29.201.120/intellingence/PlanningController/planningList";
    public static String getHealthyPlanContentURL = "https://119.29.201.120/intellingence/PlanningController/planningCont";
    public static String getHealthyPlanningMonthListURL = "https://119.29.201.120/intellingence/PlanningController/planningMonthList";
    public static String getOneDayHealthyPlanListURL = "https://119.29.201.120/intellingence/PlanningController/planningDateList";
    public static String getAfter20ItemHealthyPlanListURL = "https://119.29.201.120/intellingence/PlanningController/planningPastList";
    //public static String uploadReportURL = "http://119.29.201.120:83/intellingence/ReportController/uploadReport";
    public static String uploadReportURL = "http://119.29.201.120:8081/intellingence-web/uploadReport.do";
    public static String uploadReportURL_new = "http://119.29.201.120:8081/intellingence-web/uploadReportNew.do";
    //public static String uploadReportURL = "http://192.168.1.124:8080/intellingence-web/uploadReport.do";
    //public static String downloadQuarterReportURL = "https://119.29.201.120/intellingence/ReportController/downloadQuarterReport";
    public static String downloadQuarterReportURL = "http://119.29.201.120:8081/intellingence-web/downloadQuarterReport.do";
    //public static String downloadMonthReportURL = "https://119.29.201.120/intellingence/ReportController/downloadMonthReport";
    public static String downloadMonthReportURL = "http://119.29.201.120:8081/intellingence-web/downloadMonthReport.do";
    //public static String downloadYearReportURL = "https://119.29.201.120/intellingence/ReportController/downloadYearReport";
    public static String downloadYearReportURL = "http://119.29.201.120:8081/intellingence-web/downloadYearReport.do";

    String x = "www.amsu-new.com:8081";

    //public static String getHistoryReportListURL = "https://119.29.201.120/intellingence/ReportController/getReportList";
    public static String getHistoryReportListURL = "http://119.29.201.120:8081/intellingence-web/getReportList.do";
    //public static String getHistoryReportDetailURL = "https://119.29.201.120/intellingence/ReportController/getDetail";
    public static String getHistoryReportDetailURL = "http://119.29.201.120:8081/intellingence-web/getDetail.do";
    public static String getShoepaddetailsURL = "http://119.29.201.120:8081/intellingence-web/getShoepaddetails.do";
    //public static String downloadWeekReportURL = "https://119.29.201.120/intellingence/ReportController/downloadWeekReport";
    //public static String downloadWeekReportURL = "http://119.29.201.120:8081/intellingence-web/downloadWeekReport.do";
    public static String downloadWeekReportURL = "http://119.29.201.120:8081/intellingence-web/downloadWeekReport.do";
    public static String downloadLatelyWeekReportURL = "http://119.29.201.120:8081/intellingence-web/downloadLatelyWeekReport.do";
    public static String addSosContact = "https://119.29.201.120/intellingence/UserinfoController/addContacts";
    public static String getAllContacts = "https://119.29.201.120/intellingence/UserinfoController/getContactsAndMessage";
    public static String setSosMessage = "https://119.29.201.120/intellingence/UserinfoController/setMessage";
    public static String deleteSosContact = "https://119.29.201.120/intellingence/UserinfoController/deleteContact";
    public static String bindingDeviceURL = "https://119.29.201.120/intellingence/BindingController/bindingDevice";
    /**
     * 获取耐力测试详情
     */
    public static String getLastEnduranceDetailURL = "http://119.29.201.120:8081/intellingence-web/getLastEnduranceDetail.do";
    /**
     * 获取耐力测试记录
     */
    public static String getReportListURL = "http://119.29.201.120:8081/intellingence-web/getReportList.do";
    /**
     * 上传耐力测试结果
     */
    public static String uploadEnduranceDataURL = "http://119.29.201.120:8081/intellingence-web/uploadEnduranceData.do";

    //public static String checkAppUpdateURL = "https://119.29.201.120/intellingence/VersionController/getLastVersion";
    public static String checkAppUpdateURL = "http://119.29.201.120:8081/intellingence-web/getLastVersion.do";
    public static String disclaimerAssertsURL = "http://119.29.201.120:8081/intellingence-web/upload/disclaimerAsserts.html";
    public static String Questionnaire1URL = "https://wj.qq.com/s/1203236/ee9b";
    public static String Questionnaire2URL = "https://wj.qq.com/s/1338652/a851";
    public static String getInsoleTokenURL = "http://119.29.201.120:8081/intellingence-web/getToken.do";
    public static String get30ScendInsoleAlanyDataURL = "http://119.29.201.120:8081/intellingence-web/getShoepadDatas30seconds.do";
    public static String getALLShoepadDatasURL = "http://119.29.201.120:8081/intellingence-web/getShoepadDatas.do";
    public static String ServerHostName = "http://119.29.201.120:8081/";
    //public static String ServerHostName = "http://192.168.0.117:8080/";
    public static String bindDeviceInsoleUrl = ServerHostName + "intellingence-web/bangdingshoepad.do";

    public static long AnimatorDuration = 2000;  //动画时长，2s

    public static int oneSecondFrame = 150; //帧为150，即1s有150个数据点

    public static String currectDeviceLEMac = "currectDeviceLEMac";
    public static String sportState = "sportState";
    public static String sportAe = "";
    public static String isLookupECGDataFromSport = "isLookupECGDataFromSport";
    public static String heartDataList_static = "heartDataListstatic";
    public static String paceList = "paceList";
    public static String heartDataList_athletic = "heartDataList_athletic";
    public static String mKcalData = "mKcalData";
    public static String mStridefreData = "mStridefreData";
    public static String mSpeedStringListData = "mSpeedStringListData";
    public static String sportCreateRecordID = "sportCreateRecordID";
    public static int SPORTSTATE_STATIC = 0;
    public static int SPORTSTATE_ATHLETIC = 1;
    public static int SPORTSTATE_INDOOR = 2;
    public static String hrr = "hrr";
    public static String ecgFiletimeMillis = "ecgFiletimeMillis";
    public static String startTimeMillis = "startTimeMillis";
    public static String restingHR = "stillRate";
    public static String sosinfo = "sosinfo";
    public static String sosNumberList = "sosNumberList";
    public static String ecgLocalFileName = "ecgLocalFileName";
    public static String accLocalFileName = "accLocalFileName";
    public static String hardWareVersion = "hardWareVersion";
    public static String softWareVersion = "softWareVersion";
    public static String hardWareVersion_insole = "hardWareVersion_insole";
    public static String softWareVersion_insole = "softWareVersion_insole";
    public static String myDeceiveName = "myDeceiveName";
    public static String isNeedRecoverAbortData = "isNeedRecoverAbortData";
    public static String sportType = "sportType";
    public static String isEnduranceTest = "EnduranceTest";
    public static String isMarathonSportType = "isMarathonSportType";
    public static String mIsOutDoor = "mIsOutDoor";
    public static String sportRunningType = "sportRunningType";    //鞋垫  衣服
    /**
     * 运动服/跑步
     */
    public static final int sportType_Cloth = 1;
    /**
     * 鞋垫/步态
     */
    public static final int sportType_Insole = 2;
    public static final int sportType_Marathon = 3;

    public static final int clothDeviceType_old_encrypt = 1;  //加密
    public static final int clothDeviceType_old_noEncrypt = 2;  //不加密
    public static final int clothDeviceType_secondGeneration = 3;  //二代衣服,神念
    public static final int clothDeviceType_secondGeneration_our = 4;  //二代衣服，自己

    public static final int clothDeviceType_AMSU_EStartWith = -1;  //二代衣服，自己
    public static final int clothDeviceType_Default_NO = -2;  //二代衣服，自己

    public static boolean enduranceTest;

    public static String uploadRecordDefaultString = "0";
    public static String uploadRecordDefaultString_1 = "-1";
    public static int uploadRecordDefaultInt = 0;

    public static String noIntentNotifyMsg = "请求失败，网络异常或服务器错误";

    public static String sendONLineMsgToServer = "http://192.168.137.1:8080/AmsuClothMonitor/AddUserInfoAction";
    public static String deleteONLineMsgToServer = "http://localhost:8080/AmsuClothMonitor/deleteOnlineUserAction";

    public static UUID insoleSerUuid = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static UUID insoleCharUuid = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    public static String deleteHistoryRecordURL = "http://119.29.201.120:8081/intellingence-web/deleteById.do";
    public static String deleteBangdingByUserId = "http://119.29.201.120:8081/intellingence-web/deleteBangdingByUserid.do";


    public static final String leftInsoleFileAbsolutePath = "leftInsoleFileAbsolutePath";
    public static final String rightInsoleFileAbsolutePath = "rightInsoleFileAbsolutePath";

    public static final String insoleAllKcal = "insoleAllKcal";
    public static final String maxSpeedKM_Hour = "maxSpeedKM_Hour";
    public static final String stridefreList = "stridefreList";
    public static final String insoleTag = "insoleTag";
    public static final String getShoepadList = "http://119.29.201.120:8081/intellingence-web/getShoepadList.do";
    public static final String deleteShoepadDataById = "http://119.29.201.120:8081/intellingence-web/deleteShoepadDataById.do";
    public static final String getBangdingDetails = "http://119.29.201.120:8081/intellingence-web/getBangdingDetails.do";
    public static final String checkDeviceUpdateUrl = "http://119.29.201.120:8081/intellingence-web/getHardwareVersion.do";

    public static final String readInsoleBatterySerUuid = "0000180f-0000-1000-8000-00805f9b34fb";   //0x180F
    public static final String readInsoleBatteryCharUuid = "00002a19-0000-1000-8000-00805f9b34fb";  //0x2A19

    public static final String readInsoleDeviceInfoSerUuid = "0000180a-0000-1000-8000-00805f9b34fb";  //0x180A
    public static final String readInsoleDeviceInfoHardwareRevisionCharUuid = "00002a27-0000-1000-8000-00805f9b34fb";  //Hardware Revision 0x2A27
    public static final String readInsoleDeviceInfoSoftwareRevisionCharUuid = "00002a28-0000-1000-8000-00805f9b34fb";  //Software Revision 0x2A28

    public static final String readSecondGenerationInfoSerUuid = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String sendReceiveSecondGenerationClothCharUuid_1 = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String sendReceiveSecondGenerationClothCharUuid_2 = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String readSecondGenerationClothECGCharUuid = "6e400004-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String readSecondGenerationClothACCCharUuid = "6e400005-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String readSecondGenerationClothHeartRateCharUuid = "6e400006-b5a3-f393-e0a9-e50e24dcca9e";

    public static final int saveDataTOLocalTimeSpanSecond = 10;  //数据持久化时间间隔 1分钟

    public static final String insoleDeviceBatteryInfos = "insoleDeviceBatteryInfos";



    public static final String mClothDeviceType = "mClothDeviceType";
    public static final String moduleIP = "moduleIP";
    public static final String isOpenReceiveDataTest = "isOpenReceiveDataTest";

}





