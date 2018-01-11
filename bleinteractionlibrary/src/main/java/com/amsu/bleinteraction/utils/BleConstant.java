package com.amsu.bleinteraction.utils;

import java.util.UUID;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.bleinteractionlibrary.utils
 * @time 12/4/2017 2:18 PM
 * @describe
 */
public class BleConstant {

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
    public static final int clothDeviceType_secondGeneration_IOE = 3;  //二代衣服,神念
    public static final int clothDeviceType_secondGeneration_AMSU = 4;  //二代衣服，自己
    public static final int clothDeviceType_secondGeneration_AMSU_BindByHardware = 5;  //二代衣服，自己

    public static final int clothDeviceType_AMSU_EStartWith = -1;  //以AMSU开头的，不确定是旧主机还是二代主机
    public static final int clothDeviceType_Default_NO = -2;  //

    public static final String readInsoleBatterySerUuid = "0000180f-0000-1000-8000-00805f9b34fb";   //0x180F
    public static final String readInsoleBatteryCharUuid = "00002a19-0000-1000-8000-00805f9b34fb";  //0x2A19

    public static final UUID readInsoleBatterySerUuidUUID = UUID.fromString(BleConstant.readInsoleBatterySerUuid);   //0x180F
    public static final UUID readInsoleBatteryCharUuidUUID = UUID.fromString(BleConstant.readInsoleBatteryCharUuid);  //0x2A19

    public static final String readInsoleDeviceInfoSerUuid = "0000180a-0000-1000-8000-00805f9b34fb";  //0x180A
    public static final String readInsoleDeviceInfoHardwareRevisionCharUuid = "00002a27-0000-1000-8000-00805f9b34fb";  //Hardware Revision 0x2A27
    public static final String readInsoleDeviceInfoSoftwareRevisionCharUuid = "00002a28-0000-1000-8000-00805f9b34fb";  //Software Revision 0x2A28
    public static final String readInsoleDeviceInfoModelNumberCharUuid = "00002a24-0000-1000-8000-00805f9b34fb";  //Software Revision 0x2A24
                                                                        //00002a24-0000-1000-8000-00805f9b34fb

    public static final String readSecondGenerationInfoSerUuid = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String sendReceiveSecondGenerationClothCharUuid_1 = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String sendReceiveSecondGenerationClothCharUuid_2 = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String readSecondGenerationClothECGCharUuid = "6e400004-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String readSecondGenerationClothACCCharUuid = "6e400005-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String readSecondGenerationClothHeartRateCharUuid = "6e400006-b5a3-f393-e0a9-e50e24dcca9e";

    public static String writeConfigureOrder = "FF010A100C080E010016";
    public static String openDataTransmitOrder = "FF0206010016";
    public static String stopDataTransmitOrder = "FF0206000016";
    public static String readDeviceIDOrder = "FF04050016";
    public static String checkIsHaveDataOrder = "FF05050016";  //4.6	查询SD卡是否有数据
    public static String synchronizeOrder = "FF05050016";  //4.6	设备同步


    public static String hardWareVersion = "hardWareVersion";
    public static String softWareVersion = "softWareVersion";
    public static final String mClothDeviceType = "mClothDeviceType";
    public static int oneSecondFrame = 150; //帧为150，即1s有150个数据点

    public static final String blueLightAlwaysOnOrder = "42382B03FF";//蓝灯
    public static final String greenLightAlwaysOnOrder = "42382B02FF";//绿灯
    public static final String redLightAlwaysOnOrder = "42382B01FF";//红灯
    public static final String threenlightSpacedFlickerOrder = "42382BFF03";//3灯循环闪,间隔为0.3s（末尾03为间隔时间，可以自定义）




}
