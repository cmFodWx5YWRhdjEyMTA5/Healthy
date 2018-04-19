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

    public static boolean healthyDataOpen = false;

    public static String writeConfigureOrder = "FF010A100C080E010016";
    public static String openDataTransmitOrder = "FF0206010016";
    public static String stopDataTransmitOrder = "FF0206000016";
    public static String readDeviceIDOrder = "FF04050016";
    public static String checkIsHaveDataOrder = "FF05050016";  //4.6	查询SD卡是否有数据
    public static String synchronizeOrder = "FF05050016";  //4.6	设备同步

    //public static String loginURL = "http://203.195.168.139:8081/intellingence-web/phoneVerify.do";
    public static String loginURL = "http://192.168.0.109:8080/intellingence-web/phoneVerify.do.jsp";
    public static String uploadIconURL = "http://203.195.168.139:8081/intellingence-web/do_upload.do";
    public static String downloadPersionDataURL = "http://203.195.168.139:8081/intellingence-web/readUserinfo.do";
    public static String phoneVerify = "http://203.195.168.139:8081/intellingence-web/phoneVerifyListen.do";
    public static String duploadPersionDataURL = "http://203.195.168.139:8081/intellingence-web/uploadUserinfo.do";

    public static String setHealthyPlanURL = "http://203.195.168.139:8081/intellingence-web/setPlanning.do";
    public static String modifyHealthyPlanURL = "http://203.195.168.139:8081/intellingence-web/modifyPlanning.do";
    public static String getHealthyPlanListURL = "http://203.195.168.139:8081/intellingence-web/planningList.do";
    public static String getHealthyPlanContentURL = "http://203.195.168.139:8081/intellingence-web/planningCont.do";
    public static String getHealthyPlanningMonthListURL = "http://203.195.168.139:8081/intellingence-web/planningMonthList.do";
    public static String getOneDayHealthyPlanListURL = "http://203.195.168.139:8081/intellingence-web/planningDateList.do";
    public static String getAfter20ItemHealthyPlanListURL = "http://203.195.168.139:8081/intellingence-web/planningPastList.do";
    //public static String uploadReportURL = "http://203.195.168.139:8081:83/intellingence/ReportController/uploadReport";
    public static String uploadReportURL = "http://203.195.168.139:8081/intellingence-web/uploadReport.do";
    public static String uploadReportURL_new = "http://203.195.168.139:8081/intellingence-web/uploadReportNew.do";
    //public static String uploadReportURL = "http://192.168.1.124:8080/intellingence-web/uploadReport.do";
    //public static String downloadQuarterReportURL = "http://203.195.168.139:8081/intellingence/ReportController/downloadQuarterReport";
    public static String downloadQuarterReportURL = "http://203.195.168.139:8081/intellingence-web/downloadQuarterReport.do";
    //public static String downloadMonthReportURL = "http://203.195.168.139:8081/intellingence/ReportController/downloadMonthReport";
    public static String downloadMonthReportURL = "http://203.195.168.139:8081/intellingence-web/downloadMonthReport.do";
    //public static String downloadYearReportURL = "http://203.195.168.139:8081/intellingence/ReportController/downloadYearReport";
    public static String downloadYearReportURL = "http://203.195.168.139:8081/intellingence-web/downloadYearReport.do";

    String x = "www.amsu-new.com:8081";

    //public static String getHistoryReportListURL = "http://203.195.168.139:8081/intellingence/ReportController/getReportList";
    public static String getHistoryReportListURL = "http://203.195.168.139:8081/intellingence-web/getReportList.do";
    //public static String getHistoryReportDetailURL = "http://203.195.168.139:8081/intellingence/ReportController/getDetail";
    public static String getHistoryReportDetailURL = "http://203.195.168.139:8081/intellingence-web/getDetail.do";
    public static String getShoepaddetailsURL = "http://203.195.168.139:8081/intellingence-web/getShoepaddetails.do";
    //public static String downloadWeekReportURL = "http://203.195.168.139:8081/intellingence/ReportController/downloadWeekReport";
    //public static String downloadWeekReportURL = "http://203.195.168.139:8081/intellingence-web/downloadWeekReport.do";
    public static String downloadWeekReportURL = "http://203.195.168.139:8081/intellingence-web/downloadWeekReport.do";
    public static String downloadLatelyWeekReportURL = "http://203.195.168.139:8081/intellingence-web/downloadLatelyWeekReport.do";
    public static String addSosContact = "http://203.195.168.139:8081/intellingence-web/addContacts.do";
    public static String getAllContacts = "http://203.195.168.139:8081/intellingence-web/getContactsAndMessage.do";
    public static String setSosMessage = "http://203.195.168.139:8081/intellingence-web/setMessage.do";
    public static String deleteSosContact = "http://203.195.168.139:8081/intellingence-web/deleteContact.do";
    public static String bindingDeviceURL = "http://203.195.168.139:8081/intellingence-web/bindingDevice.do";
    /**
     * 获取耐力测试详情
     */
    public static String getLastEnduranceDetailURL = "http://203.195.168.139:8081/intellingence-web/getLastEnduranceDetail.do";
    /**
     * 获取耐力测试记录
     */
    public static String getReportListURL = "http://203.195.168.139:8081/intellingence-web/getReportList.do";
    /**
     * 上传耐力测试结果
     */
    public static String uploadEnduranceDataURL = "http://203.195.168.139:8081/intellingence-web/uploadEnduranceData.do";

    //public static String checkAppUpdateURL = "http://203.195.168.139:8081/intellingence/VersionController/getLastVersion";
    public static String checkAppUpdateURL = "http://203.195.168.139:8081/intellingence-web/getLastVersion.do";
    public static String disclaimerAssertsURL = "http://203.195.168.139:8081/intellingence-web/upload/disclaimerAsserts.html";
    public static String Questionnaire1URL = "http://wj.qq.com/s/1203236/ee9b";
    public static String Questionnaire2URL = "http://wj.qq.com/s/1338652/a851";
    public static String getInsoleTokenURL = "http://203.195.168.139:8081/intellingence-web/getToken.do";
    public static String get30ScendInsoleAlanyDataURL = "http://203.195.168.139:8081/intellingence-web/getShoepadDatas30seconds.do";
    public static String getALLShoepadDatasURL = "http://203.195.168.139:8081/intellingence-web/getShoepadDatas.do";
    public static String ServerHostName = "http://203.195.168.139:8081/";
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

    public static String deleteHistoryRecordURL = "http://203.195.168.139:8081/intellingence-web/deleteById.do";
    public static String deleteBangdingByUserId = "http://203.195.168.139:8081/intellingence-web/deleteBangdingByUserid.do";


    public static final String leftInsoleFileAbsolutePath = "leftInsoleFileAbsolutePath";
    public static final String rightInsoleFileAbsolutePath = "rightInsoleFileAbsolutePath";

    public static final String insoleAllKcal = "insoleAllKcal";
    public static final String maxSpeedKM_Hour = "maxSpeedKM_Hour";
    public static final String stridefreList = "stridefreList";
    public static final String insoleTag = "insoleTag";
    public static final String getShoepadList = "http://203.195.168.139:8081/intellingence-web/getShoepadList.do";
    public static final String deleteShoepadDataById = "http://203.195.168.139:8081/intellingence-web/deleteShoepadDataById.do";
    public static final String getBangdingDetails = "http://203.195.168.139:8081/intellingence-web/getBangdingDetails.do";
    public static final String checkDeviceUpdateUrl = "http://203.195.168.139:8081/intellingence-web/getHardwareVersion.do";

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





