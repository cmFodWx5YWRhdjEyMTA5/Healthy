package com.amsu.bleinteraction.proxy;

import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;
import android.util.Log;

import com.amsu.bleinteraction.bean.BleCallBackEvent;
import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.bean.MessageEvent;
import com.amsu.bleinteraction.utils.BleConstant;
import com.amsu.bleinteraction.utils.DataTypeConversionUtil;
import com.amsu.bleinteraction.utils.EcgAccDataUtil;
import com.amsu.bleinteraction.utils.EcgFilterUtil_1;
import com.amsu.bleinteraction.utils.FileWriteHelper;
import com.amsu.bleinteraction.utils.ResultCalcuUtil;
import com.amsu.bleinteraction.utils.SharedPreferencesUtil;
import com.ble.api.DataUtil;
import com.test.objects.HeartRate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.utils
 * @time 11/24/2017 9:28 AM
 * @describe
 */
public class BleDataProxy {
    private static final String TAG = "BleDataProxy";
    private LeProxy mLeProxy;
    private static BleDataProxy mBleDataProxy;
    private boolean mIsDeviceDroped = false;
    private int mPreHeartRate = -1;
    private final String CLOTH_CONNECTED_DROP_RECEIVER_DATA = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00";  //衣服导联脱落后，心电数据将只有00，所以要补充全为0的心电数据，保证心电波形有（为直线）
    public EcgFilterUtil_1 mEcgFilterUtil_1;


    public static final String EXTRA_ECG_DATA = "EXTRA_ECG_DATA";
    public static final String EXTRA_ACC_DATA = "EXTRA_ACC_DATA";
    public static final String EXTRA_HEART_DATA = "EXTRA_HEART_DATA";
    public static final String EXTRA_STRIDE_DATA = "EXTRA_STRIDE_DATA";
    public static final String EXTRA_BATTERY_DATA = "EXTRA_BATTERY_DATA";

    public static final String ACTION_CHARGE_CHANGE = "ACTION_CHARGE_CHANGE";

    private ResultCalcuUtil mResultCalcuUtil;
    private BleConnectionProxy mConnectionProxy;
    private MessageEvent mEcgMessageEvent;
    private MessageEvent mMessageEvent;


    public static BleDataProxy getInstance(){
        if (mBleDataProxy==null){
            mBleDataProxy = new BleDataProxy();
        }
        return mBleDataProxy;
    }

    private BleDataProxy() {
        mLeProxy = LeProxy.getInstance();
        mConnectionProxy = BleConnectionProxy.getInstance();
        mEcgFilterUtil_1 = EcgFilterUtil_1.getInstance();

        mMessageEvent = new MessageEvent();
        mEcgMessageEvent = new MessageEvent();

        mResultCalcuUtil = new ResultCalcuUtil();
        mResultCalcuUtil.setOnHeartCalcuListener(new ResultCalcuUtil.OnHeartCalcuListener() {
            @Override
            public void onReceiveHeart(HeartRate heartRate) {
                updateUIECGHeartData(heartRate.rate);
                updateDeviceConnectedUnstabitily(heartRate.noiseLevel);
            }

            @Override
            public void onReceiveStride(int stride) {
                updateUIStrideData(stride);
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BleCallBackEvent event) {
        Log.i(TAG, "event:"+event);
        switch (event.messageType){
            case msgType_Connect:

                break;
            case msgType_BatteryPercent:

                break;
        }
    }

    void bleCharacteristicChanged(String address, BluetoothGattCharacteristic characteristic){
        String hexData = DataUtil.byteArrayToHex(characteristic.getValue());
        String uuid = characteristic.getUuid().toString();
        //Log.i(TAG, "onCharacteristicChanged() - "+characteristic.getValue().length +"  " + hexData);

        if (uuid.equals(BleConstant.readInsoleDeviceInfoModelNumberCharUuid)){  //硬件模块名称
            String deviceVersionString = DataTypeConversionUtil.convertHexToString(hexData);
            Log.i(TAG, "硬件模块名称:"+deviceVersionString);
            BleDevice deviceFromSP = SharedPreferencesUtil.getDeviceFromSP(BleConstant.sportType_Cloth);
            if (deviceFromSP!=null){
                deviceFromSP.setModelNumber(deviceVersionString);
                SharedPreferencesUtil.saveDeviceToSP(deviceFromSP,BleConstant.sportType_Cloth);
            }
        }
        else {
            int[] valuableEcgData = EcgAccDataUtil.getValuableEcgACCData(hexData);
            if (valuableEcgData!=null){
                if (valuableEcgData.length== EcgAccDataUtil.ecgOneGroupLength){
                    dealWithOnePackageEcgData(valuableEcgData,address,hexData);
                }
                else if (valuableEcgData.length== EcgAccDataUtil.accOneGroupLength){
                    dealWithOnePackageAccData(valuableEcgData);
                }
            }
            else {
                if (hexData.startsWith("FF 85") && hexData.length()==17){
                    oldClothDeviceOffLineFileCount(hexData);
                }
                else if (hexData.startsWith("FF 84") ){
                    oldClothDeviceInfoAndBattery(hexData);
                }
                else if (hexData.length()==2 && mConnectionProxy.getmConnectionConfiguration().deviceType== BleConstant.sportType_Insole){
                    insoleDeviceBattery(address, hexData);
                }
                else if (hexData.length()==11 && hexData.startsWith("56")){
                    oldNOEnptyDeviceVersionInfo(address, hexData, uuid);
                }
                else if (hexData.length()==14 && hexData.startsWith("42 39")){  //42 39 2B 4F 4B
                    mLeProxy.updateBroadcast(address, characteristic);
                }
                else if (hexData.length()==14 && !hexData.startsWith("42 37 2B")){  //42 37 2B开头的是鞋垫步频
                    deviceVersionInfo(address, hexData, uuid);
                }
                else if (hexData.length()==17){
                    newClothDeviceVersionInfo(hexData, uuid);
                }
                else if (hexData.length()==32){
                    newClothDeviceStateInfo(hexData);
                }
                else if (hexData.length()==2 && mConnectionProxy.getmConnectionConfiguration().deviceType==BleConstant.sportType_Cloth) {
                    newClothDeviceHeartOrBattery(address, hexData, uuid);
                }
                else {
                    mLeProxy.updateBroadcast(address, characteristic);
                }
            }
        }


    }


    private void dealWithOnePackageEcgData(int[] valuableEcgData,String address,String hexData) {
        boolean ismIsDataStart = mConnectionProxy.ismIsDataStart();  //当连接上时ismIsDataStart为false，收到数据时把这个值设置为true
        mConnectionProxy.setmIsDataStart(true);

        if (hexData.equals(CLOTH_CONNECTED_DROP_RECEIVER_DATA)){
            //导联脱落,让硬件灯闪烁(注：在导联脱落时立马发送命令让灯闪烁，以后便收到这个数据不再发命令，而是根据收到心率后判断是否脱落，脱落就发送)
            if (!ismIsDataStart || !mIsDeviceDroped){
                Log.i(TAG,"脱落，开始循序闪");
                sendControlLightOrder(BleConstant.threenlightSpacedFlickerOrder,address);
                mIsDeviceDroped = true;
            }
        }
        else {
            if (!ismIsDataStart || mIsDeviceDroped){
                //之前是脱落，需要重置灯的状态，默认设置为蓝灯常亮
                sendControlLightOrder(BleConstant.blueLightAlwaysOnOrder,address);
                Log.i(TAG,"设备连接恢复正常");
                mIsDeviceDroped = false;
            }
        }

        int[] clone = valuableEcgData.clone();

        /*String intString = "";
        for (int i:valuableEcgData){
            intString+=i+",";
        }
        Log.i(TAG,"滤波前心电:"+intString +"  ");*/


        ecgDataFilter(valuableEcgData);

        /*String intStringA = "";
        for (int i:valuableEcgData){
            intStringA+=i+",";
        }
        Log.w(TAG,"滤波后心电:"+intStringA);*/

        mResultCalcuUtil.notifyReciveAcgPackageData(clone,valuableEcgData);

        /*Intent intent = new Intent(LeProxy.ACTION_DATA_AVAILABLE);
        intent.putExtra(EXTRA_ECG_DATA, valuableEcgData);
        mLeProxy.updateBroadcast(intent);*/

        postBleDataOnBus(BleConnectionProxy.MessageEventType.msgType_ecgDataArray,valuableEcgData);



    }

    private void postBleDataOnBus(BleConnectionProxy.MessageEventType messageType, int[] dataArray) {
        mEcgMessageEvent.messageType = messageType;
        mEcgMessageEvent.dataArray = dataArray;
        EventBus.getDefault().post(mEcgMessageEvent);
    }

    void postBleDataOnBus(BleConnectionProxy.MessageEventType messageType, int data) {
        mMessageEvent.messageType = messageType;
        mMessageEvent.singleValue = data;
        EventBus.getDefault().post(mMessageEvent);
    }

    //滤波处理
    private void ecgDataFilter(int[] ecgIntsForFiliter) {
        for (int i=0;i<ecgIntsForFiliter.length;i++){
            ecgIntsForFiliter[i] = mEcgFilterUtil_1.miniEcgFilterLp(mEcgFilterUtil_1.miniEcgFilterHp (mEcgFilterUtil_1.NotchPowerLine(ecgIntsForFiliter[i], 1)));
        }
    }

    //收到心率，4s一次
    private void updateUIECGHeartData(int heartRate) {
        /*Intent intent = new Intent(LeProxy.ACTION_DATA_AVAILABLE);
        intent.putExtra(EXTRA_HEART_DATA, heartRate);
        mLeProxy.updateBroadcast(intent);
        mConnectionProxy.setCurrentHeartRate(heartRate);
        updateLightStateByCurHeart(heartRate);*/

        //Log.i("HeartShowWayUtil","收到heartRate:"+heartRate);
        postBleDataOnBus(BleConnectionProxy.MessageEventType.msgType_HeartRate,heartRate);
    }

    private boolean isSetBleUnstabitily;
    //收到连接是否稳定状态：  noiseLevel   1：不稳定，需要提示    0：正常
    private void updateDeviceConnectedUnstabitily(int noiseLevel) {
        if (noiseLevel==1){
            //PopupWindowUtil.showDeviceConnectedChangePopWindow(PopupWindowUtil.connectTypeUnstabitily,"蓝牙连接不稳定");
            isSetBleUnstabitily = true;
        }
        else {
            if (isSetBleUnstabitily){
                //PopupWindowUtil.showDeviceConnectedChangePopWindow(PopupWindowUtil.connectTypeConnected,"蓝牙连接正常");
                isSetBleUnstabitily = false;
            }
        }
    }

    //收到步频
    private void updateUIStrideData(int stride) {
        /*Intent intent = new Intent(LeProxy.ACTION_DATA_AVAILABLE);
        intent.putExtra(EXTRA_STRIDE_DATA, stride);
        mLeProxy.updateBroadcast(intent);*/

        postBleDataOnBus(BleConnectionProxy.MessageEventType.msgType_Stride,stride);
    }

    //收到电量
    private void updateBatteryData(int batteryPercent) {
        /*Intent intent = new Intent(LeProxy.ACTION_BATTERY_DATA_AVAILABLE);
        intent.putExtra(EXTRA_BATTERY_DATA, percent);
        mLeProxy.updateBroadcast(intent);*/
        postBleDataOnBus(BleConnectionProxy.MessageEventType.msgType_BatteryPercent,batteryPercent);
    }

    /**文件名不传入（或传入null）时，会有默认的文件名.. 在传入文件时，表示之前有记录数据，异常停止时，需要再次追加写到之前的文件里
     *
     * @param ecgFileName 心电文件名
     * @param accFileName  加速度文件名
     * @return 返回写入文件数组String[]  长度位2，[0]表示心电文件名， [1]表示加速度文件名
     */
    public String[] setRecordingStarted(String ecgFileName,String accFileName){
        ecgFileName = TextUtils.isEmpty(ecgFileName)?FileWriteHelper.getClolthDeviceLocalFileName(FileWriteHelper.fileExtensionType_ECG):ecgFileName;
        accFileName = TextUtils.isEmpty(accFileName)?FileWriteHelper.getClolthDeviceLocalFileName(FileWriteHelper.fileExtensionType_ACC):accFileName;

        boolean isNeedWriteFileHead = mConnectionProxy.getmConnectionConfiguration().isNeedWriteFileHead;
        FileWriteHelper fileWriteHelper = FileWriteHelper.getFileWriteHelper();
        fileWriteHelper.startRecordingToFile(ecgFileName,accFileName,isNeedWriteFileHead);
        return new String[]{ecgFileName,accFileName};
    }

    //当没有指定文件名时，采用默认的文件名
    public void setRecordingStarted(){
        String ecgFileName = FileWriteHelper.getClolthDeviceLocalFileName(FileWriteHelper.fileExtensionType_ECG);
        String accFileName = FileWriteHelper.getClolthDeviceLocalFileName(FileWriteHelper.fileExtensionType_ACC);
        setRecordingStarted(ecgFileName,accFileName);
    }

    public String[] stopWriteEcgToFileAndGetFileName(){
        FileWriteHelper fileWriteHelper = FileWriteHelper.getFileWriteHelper();
        return fileWriteHelper.stopRecordingToFileAndGetEcgFileName();
    }

    // 3E  新版衣服心率  电量
    private void newClothDeviceHeartOrBattery(String address, String hexData, String uuid) {
        int intValue = Integer.parseInt(hexData , 16);
        Log.i(TAG,"hexData:"+hexData);
        Log.i(TAG,"intValue:"+intValue);
        if (uuid.equals(BleConstant.readInsoleBatteryCharUuid)){  //新版衣服电量
            Log.i(TAG,"clothCurrBatteryPowerPercent："+intValue);
            mConnectionProxy.setClothCurrBatteryPowerPercent(intValue);
            updateBatteryData(intValue);
        }
        else if (uuid.equals(BleConstant.readSecondGenerationClothECGCharUuid)) {  //新版衣服吊链脱落：会发一个字节 00
            if (hexData.equals("00")){
                mIsDeviceDroped = true;
                int[] ecgDropData = new int[]{0,0,0,0,0,0,0,0,0,0};
                dealWithOnePackageEcgData(ecgDropData,"",CLOTH_CONNECTED_DROP_RECEIVER_DATA);
            }
        }
        else {
            //心率，暂时保留
            //updateLightStateByCurHeart(address, intValue);
        }
    }

    //根据心率改变灯的状态
    public void updateLightStateByCurHeart(int curHeartRate) {
        String address = mConnectionProxy.getmClothDeviceConnecedMac();
        if (!mIsDeviceDroped){
            if (curHeartRate!=mPreHeartRate){
                //心率不一样则改变灯的闪烁状态,  42382B01FF(1号灯常亮)，42382B0100(1号灯关闭),   42382BFF03(导联脱落,03为间隔0.3s)
                int maxRate = 220- mConnectionProxy.getmConnectionConfiguration().userAge;
                String data  = "";
                if (curHeartRate <=maxRate*0.75){
                    data  = BleConstant.blueLightAlwaysOnOrder;  //蓝灯
                }
                else if (maxRate*0.75<curHeartRate && curHeartRate<=maxRate*0.95){
                    data  = BleConstant.greenLightAlwaysOnOrder;  //绿灯
                }
                else if (maxRate*0.95<curHeartRate){
                    data  = BleConstant.redLightAlwaysOnOrder;  //红灯
                }
                sendControlLightOrder(data,address);
            }
        }
        else {  //导联脱落
            sendControlLightOrder(BleConstant.threenlightSpacedFlickerOrder,address);
        }
        mPreHeartRate = curHeartRate;
    }

    //发送控制灯的命令
    private void sendControlLightOrder(String orderHex,String address){
        Log.i(TAG,"orderHex:"+orderHex);
        UUID serUuid = UUID.fromString(BleConstant.readSecondGenerationInfoSerUuid);
        UUID charUuid = UUID.fromString(BleConstant.sendReceiveSecondGenerationClothCharUuid_1);
        boolean send = mLeProxy.send(address, serUuid, charUuid, orderHex, false);
        Log.i(TAG,"send:"+send);
    }

    //主机状态信息
    private void newClothDeviceStateInfo(String hexData) {
        Log.i(TAG,"主机状态:"+hexData);
        String[] split = hexData.split(" ");
        Log.i(TAG,"split[3]:"+split[3]);
        int connectedState = Integer.parseInt(split[3], 16);

        if (connectedState==0){
            //脱落,让灯闪烁
            String hexSynOrder = "42382BFF01";
            UUID serUuid = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
            UUID charUuid = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
            //boolean send = mLeProxy.send(clothDeviceConnecedMac, serUuid, charUuid, DataUtil.hexToByteArray(hexSynOrder), false);
            //Log.i(TAG,"connectedStateSend:"+ send);
        }
    }


    private void dealWithOnePackageAccData(int[] valuableEcgData) {
        mResultCalcuUtil.notifyReciveAccPackageData(valuableEcgData);
    }


    /*
    *   新主机硬件版本：
        神念主机版本：V2.0.1
        阿木新主机版本：V2.0.0

        新版衣服硬件版本  V0.0.1  软件版本  V1.0.1
        神念56 32 2E 30 2E 31
    * */
    private void newClothDeviceVersionInfo(String hexData, String uuid) {
        String deviceVersionString = DataTypeConversionUtil.convertHexToString(hexData);
        BleDevice deviceFromSP = SharedPreferencesUtil.getDeviceFromSP(BleConstant.sportType_Cloth);

        if (uuid.equals(BleConstant.readInsoleDeviceInfoHardwareRevisionCharUuid)){
            //硬件版本
            Log.i(TAG,"新版衣服硬件版本："+" deviceVersionString:"+deviceVersionString);
            if (deviceFromSP!=null){
                deviceFromSP.setHardWareVersion(deviceVersionString);
            }
            //SharedPreferencesUtil.putStringValueFromSP(BleConstant.hardWareVersion,deviceVersionString);
        }
        else if (uuid.equals(BleConstant.readInsoleDeviceInfoSoftwareRevisionCharUuid)){
            //软件版本
            Log.i(TAG,"新版衣服软件版本："+" deviceVersionString:"+deviceVersionString);
            if (deviceFromSP!=null){
                deviceFromSP.setSoftWareVersion(deviceVersionString);
            }
            //SharedPreferencesUtil.putStringValueFromSP(BleConstant.softWareVersion,deviceVersionString);
        }
        SharedPreferencesUtil.saveDeviceToSP(deviceFromSP,BleConstant.sportType_Cloth);

        Log.i(TAG,"mLeProxy.getClothDeviceType "+mConnectionProxy.getmConnectionConfiguration().clothDeviceType);
        //sendReadDeviceState();

        /*if(MyApplication.deivceType==BleConstant.sportType_Cloth && mLeProxy.getClothDeviceType()==BleConstant.clothDeviceType_AMSU_EStartWith){
            if (uuid.equals(BleConstant.readInsoleDeviceInfoHardwareRevisionCharUuid) || uuid.equals(BleConstant.readInsoleDeviceInfoSoftwareRevisionCharUuid)){
                //旧版AMSU＿E开头的设备版本信息和鞋垫的相似
                mLeProxy.setmClothDeviceType(BleConstant.clothDeviceType_secondGeneration_IOE);
                MyUtil.putIntValueFromSP(BleConstant.mClothDeviceType,BleConstant.clothDeviceType_secondGeneration_IOE);
                //sendReadDeviceState();
            }
        }*/

        if(mConnectionProxy.getmConnectionConfiguration().deviceType==BleConstant.sportType_Cloth){
            if (deviceVersionString.equals("V0.2.1") || deviceVersionString.equals("V2.0.0")){ //amsu
                mConnectionProxy.getmConnectionConfiguration().clothDeviceType = BleConstant.clothDeviceType_secondGeneration_AMSU;
                SharedPreferencesUtil.putIntValueFromSP(BleConstant.mClothDeviceType,BleConstant.clothDeviceType_secondGeneration_AMSU);
            }
            else if (deviceVersionString.equals("V0.0.1") || deviceVersionString.equals("V2.0.1")){ //神念
                mConnectionProxy.getmConnectionConfiguration().clothDeviceType = BleConstant.clothDeviceType_secondGeneration_IOE;
                SharedPreferencesUtil.putIntValueFromSP(BleConstant.mClothDeviceType,BleConstant.clothDeviceType_secondGeneration_IOE);
            }
        }
    }

    //设备版本信息   56 32 2E 33
    private void oldNOEnptyDeviceVersionInfo(String address, String hexData, String uuid) {
        if (uuid.equals(BleConstant.readInsoleDeviceInfoSoftwareRevisionCharUuid)){
            mConnectionProxy.getmConnectionConfiguration().clothDeviceType = BleConstant.clothDeviceType_old_noEncrypt;
            SharedPreferencesUtil.putIntValueFromSP(BleConstant.mClothDeviceType,BleConstant.clothDeviceType_old_noEncrypt);
        }
    }

    //设备版本信息   32 2E 32 2E 30
    private void deviceVersionInfo(String address, String hexData, String uuid) {
        String deviceVersionString = DataTypeConversionUtil.convertHexToString(hexData);
        if (uuid.equals(BleConstant.readInsoleDeviceInfoHardwareRevisionCharUuid)){
            //硬件版本
            Log.i(TAG,"鞋垫硬件版本："+" deviceVersionString:"+deviceVersionString);
            BleDevice device = mConnectionProxy.getmInsoleDeviceBatteryInfos().get(address);
            if (device==null){
                device = new BleDevice();
            }
            device.setHardWareVersion(deviceVersionString);
            device.setMac(address);

            mConnectionProxy.getmInsoleDeviceBatteryInfos().put(address,device);

            //MyUtil.putStringValueFromSP(BleConstant.hardWareVersion_insole,deviceVersionString);

            //SharedPreferencesUtil.putStringValueFromSP(BleConstant.hardWareVersion,deviceVersionString);
        }
        else if (uuid.equals(BleConstant.readInsoleDeviceInfoSoftwareRevisionCharUuid)){
            //软件版本
            Log.i(TAG,"鞋垫软件版本："+" deviceVersionString:"+deviceVersionString);
            BleDevice device = mConnectionProxy.getmInsoleDeviceBatteryInfos().get(address);
            if (device==null){
                device = new BleDevice();
            }
            device.setSoftWareVersion(deviceVersionString);
            device.setMac(address);

            mConnectionProxy.getmInsoleDeviceBatteryInfos().put(address,device);

            //MyUtil.putStringValueFromSP(BleConstant.softWareVersion_insole,deviceVersionString);

            //SharedPreferencesUtil.putStringValueFromSP(BleConstant.softWareVersion,deviceVersionString);
        }

        if(mConnectionProxy.getmConnectionConfiguration().deviceType==BleConstant.sportType_Cloth && mConnectionProxy.getmConnectionConfiguration().clothDeviceType==BleConstant.clothDeviceType_AMSU_EStartWith){
            //旧版AMSU＿E开头的设备版本信息和鞋垫的相似
            if (uuid.equals(BleConstant.readInsoleDeviceInfoHardwareRevisionCharUuid) || uuid.equals(BleConstant.readInsoleDeviceInfoSoftwareRevisionCharUuid)){
                mConnectionProxy.getmConnectionConfiguration().clothDeviceType = BleConstant.clothDeviceType_secondGeneration_AMSU;
                SharedPreferencesUtil.putIntValueFromSP(BleConstant.mClothDeviceType,BleConstant.clothDeviceType_secondGeneration_AMSU);
                //sendReadDeviceState();
            }
        }
    }

    // 3E  鞋垫电量
    private void insoleDeviceBattery(String address, String hexData) {
        int intPower = Integer.parseInt(hexData, 16);
        Log.i(TAG,"鞋垫电量："+" address:"+address+" hexData:"+hexData+" intPower:"+intPower);
        BleDevice device =  mConnectionProxy.getmInsoleDeviceBatteryInfos().get(address);
        if (device==null){
            device = new BleDevice();
        }
        device.setBattery(intPower);
        device.setMac(address);

        mConnectionProxy.getmInsoleDeviceBatteryInfos().put(address,device);

        updateBatteryData(intPower);
    }

    //旧版衣服，获取到主机离线文件的个数，FF 85 06 01 00 16
    private void oldClothDeviceOffLineFileCount(String hexData) {
        Log.i(TAG,"离线文件 SDhexData："+hexData);
        String[] split = hexData.split(" ");
        if (split[3].equals("01")){
            //有离线数据，需要回调给显示端
            postBleDataOnBus(BleConnectionProxy.MessageEventType.msgType_OfflineFile,1);
            //mLeProxy.updateBroadcast(LeProxy.ACTION_RECEIVE_EXIT_OFFLINEFILE);
        }
    }

    //旧版衣服版本和电量信息，FF 84 0B 11 05 02 11 06 02 0C 90 00 16 FF 83
    private void oldClothDeviceInfoAndBattery(String hexData) {
        Log.i(TAG,"设备版本号："+hexData);
        String useHexInfo;
        if (hexData.length()>=38) {
            String substring = hexData.substring(0, 38);
            if (substring.endsWith("16")) {
                useHexInfo = substring;
            }
            else {
                return;
            }
        }
        else {
            return;
        }

        String[] split = useHexInfo.split(" ");

        int [] ints = new int[8];
        EcgAccDataUtil.geIntEcgaArr(useHexInfo, " ", 3, 8,ints); //一次的数据，10位

        System.out.println(ints.length);

        String hardWareVersionString = "20"+ints[0];
        if (ints[1]<10) {
            hardWareVersionString+="0"+ints[1];
        }
        else {
            hardWareVersionString+=+ints[1];
        }
        if (ints[2]<10) {
            hardWareVersionString+="0"+ints[2];
        }
        else {
            hardWareVersionString+=+ints[2];
        }
        Log.i(TAG,"硬件："+hardWareVersionString);

        //SharedPreferencesUtil.putStringValueFromSP(BleConstant.hardWareVersion,hardWareVersionString);

        String softWareVersionString = "20"+ints[3];
        if (ints[4]<10) {
            softWareVersionString+="0"+ints[4];
        }
        else {
            softWareVersionString+=+ints[4];
        }
        if (ints[5]<10) {
            softWareVersionString+="0"+ints[5];
        }
        else {
            softWareVersionString+=+ints[5];
        }

        Log.i(TAG,"软件："+softWareVersionString);
        //SharedPreferencesUtil.putStringValueFromSP(BleConstant.softWareVersion,softWareVersionString);

        BleDevice deviceFromSP = SharedPreferencesUtil.getDeviceFromSP(BleConstant.sportType_Cloth);
        if (deviceFromSP!=null){
            deviceFromSP.setSoftWareVersion(softWareVersionString);
            deviceFromSP.setHardWareVersion(hardWareVersionString);
            SharedPreferencesUtil.saveDeviceToSP(deviceFromSP,BleConstant.sportType_Cloth);
        }

        Log.i(TAG,"电量16进制："+split[9]+split[10]);
        int parseInt = Integer.parseInt(split[9]+split[10],16);

        Log.i(TAG,"电量10进制："+parseInt);
        /*float electricV =  parseInt/1000f;
        Log.i(TAG,"电量10进制："+electricV);*/
        int calCuelectricVPercent = calCuelectricVPercent(parseInt);
        Log.i(TAG,"clothCurrBatteryPowerPercent："+calCuelectricVPercent);
        mConnectionProxy.setClothCurrBatteryPowerPercent(calCuelectricVPercent);

        updateBatteryData(calCuelectricVPercent);

        int clothDeviceType = mConnectionProxy.getmConnectionConfiguration().clothDeviceType;
        Log.i(TAG,"clothDeviceType:"+clothDeviceType);

        if (clothDeviceType==BleConstant.clothDeviceType_AMSU_EStartWith){
            mConnectionProxy.getmConnectionConfiguration().clothDeviceType = BleConstant.clothDeviceType_old_noEncrypt;
            SharedPreferencesUtil.putIntValueFromSP(BleConstant.mClothDeviceType,BleConstant.clothDeviceType_old_noEncrypt);
        }
    }

    private static int calCuelectricVPercent(int power) {
		/*100%——4.20V

		　　90%——4.06V

		　　80%——3.98V

		　　70%——3.92V

		　　60%——3.87V

		　　50%——3.82V

		　　40%——3.79V

		　　30%——3.77V

		　　20%——3.74V

		　　10%——3.68V

		　　5%———3.45V

		  0%———3.00V*/

            /*int leave = 0;

            if (power >= 4200) {

                leave = 100;

            }else if (power<4200 && power>=4060){
                leave = (power-4060)/14+90;
            }else if (power < 4060 && power >=3980){

                leave = (power - 3980)/8 +80;

            }else if (power < 3980 && power>=3920){

                leave = (power - 3920)/6+70;
            }else if (power < 3920 && power>=3870){

                leave = (power - 3870)/5 +60;
            }else if (power < 3870 && power>=3820){

                leave = (power - 3820)/5 +50;
            }else if (power < 3820 && power>=3790){

                leave = (power - 3790)/3 +40;
            }else if (power < 3790 && power>=3770){

                leave = (power - 3770)/2 +30;
            }else if (power < 3770 && power>=3740){

                leave = (power - 3740)/3 +20;
            }else if (power < 3740 && power>=3680){

                leave = (power - 3680)/6 +10;
            }else if (power < 3680 && power>=3450){

                leave = (power - 3450)/23 +5;
            }else if (power < 3450 && power > 3000){

                leave = (power - 3000)/45;

            }else{

                leave = 0;
            }
            if (leave < 0 ) {
                leave = 0;
            }*/

        int leave = 0;

        if (power >= 4168) {

            leave = 100;

        }else if (power<4168 && power>=4064){

            leave = (power-4064)/10+90;


        }else if (power < 4064 && power >=3977){

            leave = (power - 3977)/10 +80;

        }else if (power < 3977 && power>=3900){

            leave = (power - 3900)/10+70;
        }else if (power < 3900 && power>=3830){

            leave = (power - 3830)/10 +60;
        }else if (power < 3830 && power>=3774){

            leave = (power - 3774)/10 +50;
        }else if (power < 3774 && power>=3732){

            leave = (power - 3732)/10 +40;
        }else if (power < 3732 && power>=3681){

            leave = (power - 3681)/10 +30;
        }else if (power < 3681 && power>=3596){

            leave = (power - 3596)/10 +20;
        }else if (power < 3596 && power>=3373){

            leave = (power - 3373)/10 +10;

        }else{

            leave = 0;
        }
        if (leave < 0 ) {
            leave = 0;
        }
        return leave;
    }


    public void setmIsDeviceDroped(boolean mIsDeviceDroped) {
        this.mIsDeviceDroped = mIsDeviceDroped;
    }
}
