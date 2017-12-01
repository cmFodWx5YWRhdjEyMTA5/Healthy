package com.amsu.healthy.utils.ble;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.amsu.healthy.activity.ConnectToWifiGudieActivity1;
import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.activity.MyDeviceActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.WebSocketProxy;
import com.ble.api.DataUtil;
import com.test.objects.HeartRate;

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
    private Intent calCuelectricVPercentIntent;
    private int mPreHeartRate = -1;
    private final String CLOTH_CONNECTED_DROP_RECEIVER_DATA = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00";
    private EcgFilterUtil_1 mEcgFilterUtil_1;


    public static final String EXTRA_ECG_DATA = "EXTRA_ECG_DATA";
    public static final String EXTRA_ACC_DATA = "EXTRA_ACC_DATA";
    public static final String EXTRA_HEART_DATA = "EXTRA_HEART_DATA";
    public static final String EXTRA_STRIDE_DATA = "EXTRA_STRIDE_DATA";

    private ResultCalcuUtil mResultCalcuUtil;
    private CommunicateToBleService communicateToBleService;

    public static BleDataProxy getInstance(){
        if (mBleDataProxy==null){
            mBleDataProxy = new BleDataProxy();
        }
        return mBleDataProxy;
    }

    private BleDataProxy() {
        mLeProxy = LeProxy.getInstance();
        communicateToBleService = CommunicateToBleService.getInstance();
        calCuelectricVPercentIntent = new Intent(MainActivity.ACTION_CHARGE_CHANGE);
        mEcgFilterUtil_1 = new EcgFilterUtil_1();
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


    void bleCharacteristicChanged(String address, BluetoothGattCharacteristic characteristic){
        String hexData = DataUtil.byteArrayToHex(characteristic.getValue());
        String uuid = characteristic.getUuid().toString();
        //Log.i(TAG, "onCharacteristicChanged() - "+characteristic.getValue().length +"  " + hexData);

        int[] valuableEcgData = EcgAccDataUtil.getValuableEcgACCData(hexData,mLeProxy);
        if (valuableEcgData!=null){
            if (valuableEcgData.length== EcgAccDataUtil.ecgOneGroupLength){
                dealWithOnePackageEcgData(valuableEcgData,false);
            }
            else if (valuableEcgData.length== EcgAccDataUtil.accOneGroupLength){
                dealWithOnePackageAccData(valuableEcgData);
            }
        }
        else {
            if (hexData.startsWith("FF 85") && hexData.length()==17){
                oldClothDeviceOffLineFileCount(hexData);
            }
            else if (hexData.startsWith("FF 84") && hexData.length()==44){
                oldClothDeviceInfoAndBattery(hexData);
            }
            else if (hexData.length()==2 && MyApplication.deivceType==Constant.sportType_Insole){
                insoleDeviceBattery(address, hexData);
            }
            else if (hexData.length()==11 && hexData.startsWith("56")){
                oldNOEnptyDeviceVersionInfo(address, hexData, uuid);
            }
            else if (hexData.length()==14 && hexData.startsWith("42 39")){  //42 39 2B 4F 4B
                mLeProxy.updateBroadcast(address, characteristic);
            }
            else if (hexData.length()==14){
                deviceVersionInfo(address, hexData, uuid);
            }
            else if (hexData.length()==17){
                newClothDeviceVersionInfo(hexData, uuid);
            }
            else if (hexData.length()==32){
                newClothDeviceStateInfo(hexData);
            }
            else if (hexData.length()==2 && MyApplication.deivceType==Constant.sportType_Cloth) {
                newClothDeviceHeartOrBattery(address, hexData, uuid);
            }
            else {
                mLeProxy.updateBroadcast(address, characteristic);
            }
        }
    }

    private void dealWithOnePackageEcgData(int[] valuableEcgData,boolean isDeviceDropData) {
        communicateToBleService.setmIsDataStart(true);

        int[] clone = valuableEcgData.clone();
        if (!isDeviceDropData){
            mIsDeviceDroped = false;

            String intString = "";
            for (int i:valuableEcgData){
                intString+=i+",";
            }
            Log.i(TAG,"滤波前心电:"+intString +"  ");


            ecgDataFilter(valuableEcgData);

            String intStringA = "";
            for (int i:valuableEcgData){
                intStringA+=i+",";
            }
            Log.w(TAG,"滤波后心电:"+intStringA);
        }

        mResultCalcuUtil.calcuHeart(clone,valuableEcgData);

        Intent intent = new Intent(LeProxy.ACTION_DATA_AVAILABLE);
        intent.putExtra(EXTRA_ECG_DATA, valuableEcgData);
        mLeProxy.updateBroadcast(intent);

        if (mWebSocketProxy!=null){
            startRealTimeDataTrasmit(valuableEcgData);
        }
    }

    //滤波处理
    private void ecgDataFilter(int[] ecgIntsForFiliter) {
        for (int i=0;i<ecgIntsForFiliter.length;i++){
            ecgIntsForFiliter[i] = mEcgFilterUtil_1.miniEcgFilterLp(mEcgFilterUtil_1.miniEcgFilterHp (mEcgFilterUtil_1.NotchPowerLine(ecgIntsForFiliter[i], 1)));
        }
    }

    //收到心率，4s一次
    private void updateUIECGHeartData(int heartRate) {
        Intent intent = new Intent(LeProxy.ACTION_DATA_AVAILABLE);
        intent.putExtra(EXTRA_HEART_DATA, heartRate);
        mLeProxy.updateBroadcast(intent);
    }

    //收到连接是否稳定状态：  noiseLevel   1：不稳定，需要提示    0：正常
    private void updateDeviceConnectedUnstabitily(int noiseLevel) {

    }

    //收到步频
    private void updateUIStrideData(int stride) {
        Intent intent = new Intent(LeProxy.ACTION_DATA_AVAILABLE);
        intent.putExtra(EXTRA_STRIDE_DATA, stride);
        mLeProxy.updateBroadcast(intent);
    }

    public void setRecordingStarted(){
        mResultCalcuUtil.setmIsrecording(true);
    }

    public String stopWriteEcgToFileAndGetFileName(){
        return mResultCalcuUtil.stopRecording();
    }

    public void updateHeartUI(final int heartRate, final TextView tv_healthydata_rate){
        mResultCalcuUtil.updateHeartUI(heartRate,tv_healthydata_rate);
    }

    // 3E  新版衣服心率  电量
    private void newClothDeviceHeartOrBattery(String address, String hexData, String uuid) {
        int intValue = Integer.parseInt(hexData , 16);
        Log.i(TAG,"hexData:"+hexData);
        Log.i(TAG,"intValue:"+intValue);
        if (uuid.equals(Constant.readInsoleBatteryCharUuid)){  //新版衣服电量
            Log.i(TAG,"clothCurrBatteryPowerPercent："+intValue);
            MyApplication.clothCurrBatteryPowerPercent = intValue;
            calCuelectricVPercentIntent.putExtra("clothCurrBatteryPowerPercent",intValue);
            mLeProxy.updateBroadcast(calCuelectricVPercentIntent);
        }
        else if (uuid.equals(Constant.readSecondGenerationClothECGCharUuid)) {  //新版衣服吊链脱落：会发一个字节 00
            if (hexData.equals("00")){
                mIsDeviceDroped = true;
                int[] ecgDropData = new int[]{0,0,0,0,0,0,0,0,0,0};
                dealWithOnePackageEcgData(ecgDropData,true);
            }
        }
        else {
            if (intValue!=mPreHeartRate && intValue!=0 && !mIsDeviceDroped){
                //心率不一样则改变灯的闪烁状态,  42382B01FF(1号灯常亮)，42382B0100(1号灯关闭)
                int maxRate = 220- HealthyIndexUtil.getUserAge();
                String data  = "";
                if (intValue <=maxRate*0.75){
                    data  = "42382B03FF";  //导联脱落
                }
                else if (maxRate*0.75<intValue && intValue<=maxRate*0.95){
                    data  = "42382B02FF";  //导联脱落
                }
                else if (maxRate*0.95<intValue){
                    data  = "42382B01FF";  //导联脱落
                }

                //if (!MyUtil.isEmpty(data) && !data.equals(mPreControlLightOrder)){
                if (!MyUtil.isEmpty(data)){
                    Log.i(TAG,"data:"+data);
                    byte[] bytes = DataUtil.hexToByteArray(data);
                    UUID serUuid = UUID.fromString(Constant.readSecondGenerationInfoSerUuid);
                    UUID charUuid = UUID.fromString(Constant.sendReceiveSecondGenerationClothCharUuid_1);
                    boolean send = mLeProxy.send(address, serUuid, charUuid, bytes, false);
                    Log.i(TAG,"send:"+send);
                }
            }
            mPreHeartRate = intValue;
        }
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
        mResultCalcuUtil.calcuStride(valuableEcgData);
    }


    /*
    *   新主机硬件版本：
        神念主机版本：V2.0.1
        阿木新主机版本：V2.0.0

        新版衣服硬件版本  V2.0.1  软件版本  V1.0.1
        神念56 32 2E 30 2E 31
    * */
    private void newClothDeviceVersionInfo(String hexData, String uuid) {
        String deviceVersionString = MyUtil.convertHexToString(hexData);
        if (uuid.equals(Constant.readInsoleDeviceInfoHardwareRevisionCharUuid)){
            //硬件版本
            Log.i(TAG,"新版衣服硬件版本："+" deviceVersionString:"+deviceVersionString);
            MyUtil.putStringValueFromSP(Constant.hardWareVersion,deviceVersionString);
        }
        else if (uuid.equals(Constant.readInsoleDeviceInfoSoftwareRevisionCharUuid)){
            //软件版本
            Log.i(TAG,"新版衣服软件版本："+" deviceVersionString:"+deviceVersionString);
            MyUtil.putStringValueFromSP(Constant.softWareVersion,deviceVersionString);
        }
        Log.i(TAG,"mLeProxy.getClothDeviceType "+mLeProxy.getClothDeviceType());
        //sendReadDeviceState();

        /*if(MyApplication.deivceType==Constant.sportType_Cloth && mLeProxy.getClothDeviceType()==Constant.clothDeviceType_AMSU_EStartWith){
            if (uuid.equals(Constant.readInsoleDeviceInfoHardwareRevisionCharUuid) || uuid.equals(Constant.readInsoleDeviceInfoSoftwareRevisionCharUuid)){
                //旧版AMSU＿E开头的设备版本信息和鞋垫的相似
                mLeProxy.setmClothDeviceType(Constant.clothDeviceType_secondGeneration);
                MyUtil.putIntValueFromSP(Constant.mClothDeviceType,Constant.clothDeviceType_secondGeneration);
                //sendReadDeviceState();
            }

        }*/

        if(MyApplication.deivceType==Constant.sportType_Cloth){
            if (deviceVersionString.equals("V2.0.0")){
                mLeProxy.setmClothDeviceType(Constant.clothDeviceType_secondGeneration_our);
                MyUtil.putIntValueFromSP(Constant.mClothDeviceType,Constant.clothDeviceType_secondGeneration_our);
            }
            else if (deviceVersionString.equals("V2.0.1")){
                mLeProxy.setmClothDeviceType(Constant.clothDeviceType_secondGeneration);
                MyUtil.putIntValueFromSP(Constant.mClothDeviceType,Constant.clothDeviceType_secondGeneration);
            }
        }

    }

    //设备版本信息   56 32 2E 33
    private void oldNOEnptyDeviceVersionInfo(String address, String hexData, String uuid) {
        if (uuid.equals(Constant.readInsoleDeviceInfoSoftwareRevisionCharUuid)){
            mLeProxy.setmClothDeviceType(Constant.clothDeviceType_old_noEncrypt);
            MyUtil.putIntValueFromSP(Constant.mClothDeviceType,Constant.clothDeviceType_old_noEncrypt);
        }
    }

    //设备版本信息   32 2E 32 2E 30
    private void deviceVersionInfo(String address, String hexData, String uuid) {
        String deviceVersionString = MyUtil.convertHexToString(hexData);
        if (uuid.equals(Constant.readInsoleDeviceInfoHardwareRevisionCharUuid)){
            //硬件版本
            Log.i(TAG,"鞋垫硬件版本："+" deviceVersionString:"+deviceVersionString);
            Device device = CommunicateToBleService.mInsoleDeviceBatteryInfos.get(address);
            if (device==null){
                device = new Device();
            }
            device.setHardWareVersion(deviceVersionString);
            device.setMac(address);

            CommunicateToBleService.mInsoleDeviceBatteryInfos.put(address,device);

            //MyUtil.putStringValueFromSP(Constant.hardWareVersion_insole,deviceVersionString);

            MyUtil.putStringValueFromSP(Constant.hardWareVersion,deviceVersionString);
        }
        else if (uuid.equals(Constant.readInsoleDeviceInfoSoftwareRevisionCharUuid)){
            //软件版本
            Log.i(TAG,"鞋垫软件版本："+" deviceVersionString:"+deviceVersionString);
            Device device = CommunicateToBleService.mInsoleDeviceBatteryInfos.get(address);
            if (device==null){
                device = new Device();
            }
            device.setSoftWareVersion(deviceVersionString);
            device.setMac(address);

            CommunicateToBleService.mInsoleDeviceBatteryInfos.put(address,device);

            //MyUtil.putStringValueFromSP(Constant.softWareVersion_insole,deviceVersionString);

            MyUtil.putStringValueFromSP(Constant.softWareVersion,deviceVersionString);
        }

        if(MyApplication.deivceType==Constant.sportType_Cloth && mLeProxy.getClothDeviceType()==Constant.clothDeviceType_AMSU_EStartWith){
            //旧版AMSU＿E开头的设备版本信息和鞋垫的相似
            if (uuid.equals(Constant.readInsoleDeviceInfoHardwareRevisionCharUuid) || uuid.equals(Constant.readInsoleDeviceInfoSoftwareRevisionCharUuid)){
                mLeProxy.setmClothDeviceType(Constant.clothDeviceType_secondGeneration_our);
                MyUtil.putIntValueFromSP(Constant.mClothDeviceType,Constant.clothDeviceType_secondGeneration_our);
                //sendReadDeviceState();
            }
        }
    }

    // 3E  鞋垫电量
    private void insoleDeviceBattery(String address, String hexData) {
        int intPower = Integer.parseInt(hexData, 16);
        Log.i(TAG,"鞋垫电量："+" address:"+address+" hexData:"+hexData+" intPower:"+intPower);
        Device device =  CommunicateToBleService.mInsoleDeviceBatteryInfos.get(address);
        if (device==null){
            device = new Device();
        }
        device.setBattery(intPower);
        device.setMac(address);

        CommunicateToBleService.mInsoleDeviceBatteryInfos.put(address,device);


        mLeProxy.updateBroadcast(calCuelectricVPercentIntent);
    }

    //旧版衣服，获取到主机离线文件的个数，FF 85 06 01 00 16
    private void oldClothDeviceOffLineFileCount(String hexData) {
        Log.i(TAG,"离线文件 SDhexData："+hexData);
        String[] split = hexData.split(" ");
        if (split[3].equals("01")){
            //有离线数据
            Log.i(TAG,"MyApplication.mCurrApplicationActivity:"+ MyApplication.mCurrApplicationActivity);
            if (MyApplication.mCurrApplicationActivity !=null){
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        MyApplication.mCurrApplicationActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //boolean isMainActivityInstance = MyApplication.mCurrApplicationActivity.getClass().isInstance(new MainActivity()); //只有在MainActivity弹出提示
                                if (MyApplication.mCurrApplicationActivity instanceof MainActivity || MyApplication.mCurrApplicationActivity instanceof MyDeviceActivity){
                                    //提示用户上传离线数据
                                    //暂时注销
                                    //showUploadOffLineData(MyApplication.mCurrApplicationActivity);
                                }
                            }
                        });
                    }
                }.start();
            }
        }
    }


    private void clothBleDataReceive(String hexData) {
        int clothDeviceType = mLeProxy.getClothDeviceType();
    }

    private void insoleBleDataReceive(String hexData) {

    }

    //旧版衣服版本和电量信息，FF 84 0B 11 05 02 11 06 02 0C 90 00 16 FF 83
    private void oldClothDeviceInfoAndBattery(String hexData) {
        Log.i(TAG,"设备版本号："+hexData);
        String aString = hexData;
        String[] split = aString.split(" ");

        int [] ints = new int[8];
        EcgAccDataUtil.geIntEcgaArr(aString, " ", 3, 8,ints); //一次的数据，10位

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

        MyUtil.putStringValueFromSP(Constant.hardWareVersion,hardWareVersionString);

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
        MyUtil.putStringValueFromSP(Constant.softWareVersion,softWareVersionString);

        Log.i(TAG,"电量16进制："+split[9]+split[10]);
        int parseInt = Integer.parseInt(split[9]+split[10],16);

        Log.i(TAG,"电量10进制："+parseInt);
        /*float electricV =  parseInt/1000f;
        Log.i(TAG,"电量10进制："+electricV);*/
        int calCuelectricVPercent = calCuelectricVPercent(parseInt);
        Log.i(TAG,"clothCurrBatteryPowerPercent："+calCuelectricVPercent);
        MyApplication.clothCurrBatteryPowerPercent = calCuelectricVPercent;


        calCuelectricVPercentIntent.putExtra("clothCurrBatteryPowerPercent",calCuelectricVPercent);
        mLeProxy.updateBroadcast(calCuelectricVPercentIntent);

        int clothDeviceType = mLeProxy.getClothDeviceType();
        Log.i(TAG,"clothDeviceType:"+clothDeviceType);

        if (clothDeviceType==Constant.clothDeviceType_AMSU_EStartWith){
            mLeProxy.setmClothDeviceType(Constant.clothDeviceType_old_noEncrypt);
            MyUtil.putIntValueFromSP(Constant.mClothDeviceType,Constant.clothDeviceType_old_noEncrypt);
        }
    }

    public static int calCuelectricVPercent(int power) {
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

    private boolean isShowAlertDialog;

    private void showUploadOffLineData(Activity activity){
        if (!isShowAlertDialog){
            ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(activity);
            chooseAlertDialogUtil.setAlertDialogText("发现有离线文件，是否现在进行上传","是","否");
            chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
                @Override
                public void onConfirmClick() {
                    Intent intent = new Intent(MyApplication.mCurrApplicationActivity, ConnectToWifiGudieActivity1.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(intent);
                }
            });
            isShowAlertDialog = true;
        }
    }

    private WebSocketProxy mWebSocketProxy;

    private void initWebRealTimeMonitor(MyApplication myApplication) {
        int chooseMonitorShowIndex = MyUtil.getIntValueFromSP("chooseMonitorShowIndex");
        if (chooseMonitorShowIndex!=-1){
            mWebSocketProxy = myApplication.getWebSocketUtil();
            /*if (mWebSocketProxy!=null){
                mWebSocketClient = mWebSocketProxy.mWebSocketClient;
            }*/

            if (mWebSocketProxy !=null){
                Log.i(TAG,"mWebSocketClient:"+ mWebSocketProxy.mWebSocketClient);
                String sendStartRunningState = "A5,"+ mWebSocketProxy.mCurAppClientID;
                Log.i(TAG,"开始跑步："+sendStartRunningState);
                mWebSocketProxy.sendSocketMsg(sendStartRunningState);
            }
        }
    }

    //webSocket实施数据传输
    private void startRealTimeDataTrasmit(int [] ints) {
        if (mWebSocketProxy !=null && mWebSocketProxy.isStartDataTransfer){
            String intString = "A1," + mWebSocketProxy.mCurBrowserClientID + ",";
            for (int i:ints){
                intString +=i+",";
            }
            /*jsonBase.setRet(1);
            jsonBase.setErrDesc(intString.substring(0,intString.length()-1));
            sendSocketMsg(gson.toJson(jsonBase));*/
            //F0,28,18,6,-2,-3,0,3,5,1,-1
            //sendSocketMsg(intString);
            mWebSocketProxy.sendSocketMsg(intString);
        }
    }

    //上传用户实时运动数据
    private void startUserSportDataTrasmit(String userSportData) {
        if (mWebSocketProxy !=null && mWebSocketProxy.isStartDataTransfer){
            //sendSocketMsg(userSportData);
            mWebSocketProxy.sendSocketMsg(userSportData);
        }
    }




    /*boolean isStarted = false;
    //读取设备状态信息（导联脱落、正常、充电。。。后续版本） === 后续版本不需要，APP只控制灯连接上的三种状态
    private void sendReadDeviceState(){
        if (!isStarted){
            Log.i(TAG,"sendReadDeviceState");
            MyTimeTask.startTimeRiseTimerTask( 4000, new MyTimeTask.OnTimeChangeAtScendListener() {
                @Override
                public void onTimeChange(Date date) {
                    String hexSynOrder = "4131";
                    Log.i(TAG,"clothDeviceConnecedMac:"+clothDeviceConnecedMac);
                    UUID serUuid = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
                    UUID charUuid = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
                    boolean send = mLeProxy.send(clothDeviceConnecedMac, serUuid, charUuid, DataUtil.hexToByteArray(hexSynOrder), false);
                    Log.i(TAG,"sendReadDeviceState:"+ send);
                }
            });

            isStarted  =true;
        }
    }*/


}
