package com.amsu.bleinteraction.proxy;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.utils.BleConstant;
import com.amsu.bleinteraction.utils.EcgAccDataUtil;
import com.amsu.bleinteraction.utils.SharedPreferencesUtil;
import com.amsu.bleinteraction.utils.TimerTaskUtil;
import com.ble.api.DataUtil;
import com.ble.ble.BleService;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.bleinteractionlibrary.proxy
 * @time 12/4/2017 5:24 PM
 * @describe
 */
public class BleConnectionProxy {


    private static final String TAG = BleConnectionProxy.class.getSimpleName();
    private BleSacnEngine mBleSacnEngine;
    private LeProxy mLeProxy;
    private boolean mIsConnectted;
    private boolean mIsDataStart;
    private String mClothDeviceConnecedMac;
    private ConnectionConfiguration mConnectionConfiguration;
    private Map<String, BleDevice> mInsoleDeviceBatteryInfos;
    private int clothCurrBatteryPowerPercent = -1;
    private static BleConnectionProxy mBleConnectionProxy;
    private int mCurrentHeartRate;

    public static BleConnectionProxy getInstance(){
        if (mBleConnectionProxy ==null){
            mBleConnectionProxy = new BleConnectionProxy();
        }
        return mBleConnectionProxy;
    }

    public void init(Context context) {
        SharedPreferencesUtil.initSharedPreferences(context);

        //绑定蓝牙，获取蓝牙服务
        context.bindService(new Intent(context, BleService.class), mConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(context).registerReceiver(mLocalReceiver, LeProxy.makeFilter());
        mBleSacnEngine = BleSacnEngine.getInStance(context);
        mBleSacnEngine.startScan();

        mInsoleDeviceBatteryInfos = new HashMap<>();
        mLeProxy = LeProxy.getInstance();

        checkDeviceBatteryTimerTask();

        IntentFilter statusFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(mStatusReceive, statusFilter);

    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG,"onServiceConnected");
            boolean bleDataEncrypt = isBleDataEncrypt();
            LeProxy.getInstance().setBleService(service,bleDataEncrypt);
        }
    };

    //判断蓝牙数据是否需要加密
    private boolean isBleDataEncrypt(){
        BleDevice deviceFromSP = SharedPreferencesUtil.getDeviceFromSP(BleConstant.sportType_Cloth);
        //只有以BLE开头的数据需要加密
        return deviceFromSP != null && deviceFromSP.getLEName().startsWith("BLE");
    }

    public void initConnectedConfiguration(ConnectionConfiguration mConnectionConfiguration) {
        this.mConnectionConfiguration = mConnectionConfiguration;
    }


    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);
            String action = intent.getAction();
            onReceiveBleConnectionChange(address, action);
        }
    };

    //处理蓝牙连接、数据变化
    private void onReceiveBleConnectionChange(String address, String action) {
        switch (action){
            case LeProxy.ACTION_GATT_CONNECTED:
                Log.i(TAG,"已连接 " + " "+address + " ");
                onDeviceConnectSuccessful(address);
                break;
            case LeProxy.ACTION_GATT_DISCONNECTED:
                Log.w(TAG,"已断开 "+address);
                dealwithBelDisconnected(address);
                break;
            case LeProxy.ACTION_CONNECT_ERROR:
                Log.w(TAG,"连接异常 "+address);
                onDeviceConnectError(address);
                break;
            case LeProxy.ACTION_CONNECT_TIMEOUT:
                Log.w(TAG,"连接超时 "+address);
                onDeviceConnectError(address);
                onConnectedTimeOutCountAdd(address);
                break;
            case LeProxy.ACTION_GATT_SERVICES_DISCOVERED:
                Log.i(TAG,"Services discovered: " + address);

                break;

            case LeProxy.ACTION_RSSI_AVAILABLE:// 更新rssi
                break;

            case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                break;
        }
    }

    private int mConnectedTimeoutCount = 0;
    private String mTryConnectedMacAddress = "";
    private void onConnectedTimeOutCountAdd(String address) {
        if (mTryConnectedMacAddress.equals("")){
            mTryConnectedMacAddress = address;
        }
        else {
            if (address.equals(mTryConnectedMacAddress)){
                mConnectedTimeoutCount ++;
                if (mConnectedTimeoutCount==4){
                    mBleSacnEngine.restartPhoneBluetooth();
                    mConnectedTimeoutCount = 0;
                    mTryConnectedMacAddress = "";
                }
            }
        }
    }

    //新的设备连接成功
    private void onDeviceConnectSuccessful(String address) {
        mBleSacnEngine.setmIsConnectting(false);
        mBleSacnEngine.setmIsConnectted(true);

        mConnectedTimeoutCount = 0;
        mTryConnectedMacAddress = "";

        if (mConnectionConfiguration.deviceType== BleConstant.sportType_Cloth){
            mBleSacnEngine.scanLeDevice(false);//停止扫描
            if (!mIsConnectted){
                mIsConnectted = true;
                mLeProxy.updateBroadcast(LeProxy.ACTION_DEVICE_CONNECTED);
            }
            mClothDeviceConnecedMac = address ;

            int clothDeviceType = mConnectionConfiguration.clothDeviceType;

            Log.i(TAG,"clothDeviceType:"+clothDeviceType);

            if (clothDeviceType==BleConstant.clothDeviceType_old_encrypt || clothDeviceType==BleConstant.clothDeviceType_old_noEncrypt || clothDeviceType==BleConstant.clothDeviceType_AMSU_EStartWith){

                sendStartDataTransmitOrderToBlueTooth();
                startSynDeviceOrderTimerTask();

                if (clothDeviceType==BleConstant.clothDeviceType_AMSU_EStartWith){
                    //不加密数据可能2种：1、不加密旧版衣服，2、二代衣服      根据读取的设备版本信息来区分   （通用获取设备信息方法）
                    readSecondGenerationDeviceInfo(address,BleConstant.sportType_Cloth);
                }
            }
            else if (clothDeviceType==BleConstant.clothDeviceType_secondGeneration_IOE || clothDeviceType==BleConstant.clothDeviceType_secondGeneration_AMSU){
                readSecondGenerationDeviceInfo(address,BleConstant.sportType_Cloth);
            }
        }
        else if (mConnectionConfiguration.deviceType==BleConstant.sportType_Insole){
            //mInsoleConnectedCount++;
            Log.e(TAG,"鞋垫连接mInsoleConnectedCount："+mInsoleDeviceBatteryInfos.size());
            if (mInsoleDeviceBatteryInfos.size()==0){
                mIsConnectted = false;
                mBleSacnEngine.scanLeDevice(true);//继续扫描另一个鞋垫
                mInsoleDeviceBatteryInfos.put(address,new BleDevice());
                readSecondGenerationDeviceInfo(address, BleConstant.sportType_Insole);
            }
            else if (mInsoleDeviceBatteryInfos.size()==1){
                mBleSacnEngine.scanLeDevice(false); //停止扫描
                if (!mIsConnectted){
                    mIsConnectted = true;
                    mLeProxy.updateBroadcast(LeProxy.ACTION_DEVICE_CONNECTED);
                }

                mInsoleDeviceBatteryInfos.put(address,new BleDevice());
                readSecondGenerationDeviceInfo(address,BleConstant.sportType_Insole);
                Log.e(TAG,"2个鞋垫都连接成功============================================================================");
            }
        }
    }

    //设备连接异常（连接异常、连接超时）
    private void onDeviceConnectError(String address) {
        reStartScanBleDevice();
        mIsDataStart = false;

        if (mIsConnectted){
            mIsConnectted = false;
            mLeProxy.updateBroadcast(LeProxy.ACTION_DEVICE_DISCONNECTED);
        }

        if (mConnectionConfiguration.deviceType== BleConstant.sportType_Cloth){
            mClothDeviceConnecedMac = "";
            clothCurrBatteryPowerPercent = -1;
        }
        else if (mConnectionConfiguration.deviceType==BleConstant.sportType_Insole){
            mInsoleDeviceBatteryInfos.remove(address);
        }
    }


    //手机蓝牙状态接受者
    private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch(blueState){
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.i(TAG,"STATE_TURNING_ON");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.i(TAG,"STATE_ON");
                            //开始扫描
                            dealwithPhoneBleOpen();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            dealwithBelDisconnected(null);
                            dealwithPhoneBleClose();
                            Log.i(TAG,"STATE_TURNING_OFF");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.i(TAG,"STATE_OFF");
                            break;
                    }
                    break;
            }
        }
    };

    private void dealwithPhoneBleOpen() {
        mConnectedTimeoutCount = 0;
        mTryConnectedMacAddress = "";
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBleSacnEngine.startScan();
            }
        }.start();
    }

    private void dealwithPhoneBleClose() {
        //mBleSacnEngine.stopScan();
    }

    //重新扫描蓝牙设备
    private void reStartScanBleDevice(){
        mBleSacnEngine.setmIsConnectted(false);
        mBleSacnEngine.setmIsConnectting(false);
        mBleSacnEngine.scanLeDevice(true);//开始扫描
    }

    //处理蓝牙断开（address不为空表示当前连接设备断开，address为空表示手机蓝牙关闭，需要将连接的设备信息清空）
    private void dealwithBelDisconnected(String address){
        reStartScanBleDevice();

        mConnectedTimeoutCount = 0;
        mTryConnectedMacAddress = "";

        if (mConnectionConfiguration.deviceType== BleConstant.sportType_Cloth){
            mClothDeviceConnecedMac = "";
            clothCurrBatteryPowerPercent = -1;
            mIsDataStart = false;
        }
        else if (mConnectionConfiguration.deviceType==BleConstant.sportType_Insole){
            if (!TextUtils.isEmpty(address)){
                mInsoleDeviceBatteryInfos.remove(address);
            }
            if (address==null){  //address==null表示蓝牙关闭，需要将连接设备清空
                mInsoleDeviceBatteryInfos.clear();
                mInsoleDeviceBatteryInfos.clear();
            }
        }

        if (mIsConnectted){
            mIsConnectted = false;
            mLeProxy.updateBroadcast(LeProxy.ACTION_DEVICE_DISCONNECTED);
        }
    }

    //旧版主机需要发送配置指令、开始数据指令，才能开始传输数据。  旧版主机获取设备信息和电量需要在这里发送读取设备信息指令
    private void sendStartDataTransmitOrderToBlueTooth(){
        Log.i(TAG,"mIsConnectted:"+ mIsConnectted +"          mIsDataStart: "+mIsDataStart);
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (mIsConnectted && !mIsDataStart){
                    Log.i(TAG,"mIsConnectted:"+ mIsConnectted +"          mIsDataStart: "+mIsDataStart);

                    try {
                        Thread.sleep(100);

                        boolean isCheckIsHaveDataOrderSend = mLeProxy.send(mClothDeviceConnecedMac, DataUtil.hexToByteArray(BleConstant.checkIsHaveDataOrder));
                        Log.i(TAG, "查询SD卡是否有数据："+isCheckIsHaveDataOrderSend);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Thread.sleep(200);
                        }
                        else {
                            Thread.sleep(2000);
                        }

                        String writeConfigureOrder = EcgAccDataUtil.getWriteConfigureOrderHexString(mConnectionConfiguration.userAge, mConnectionConfiguration.isAutoOffline);
                        Log.i(TAG,"writeConfigureOrder:"+writeConfigureOrder);
                        boolean isWriteConfigureOrderSend = mLeProxy.send(mClothDeviceConnecedMac, DataUtil.hexToByteArray(writeConfigureOrder));
                        Log.i(TAG,"写配置:"+isWriteConfigureOrderSend);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Thread.sleep(200);
                        }
                        else {
                            Thread.sleep(2000);
                        }

                        boolean isOpenDataTransmitOrderSend = mLeProxy.send(mClothDeviceConnecedMac, DataUtil.hexToByteArray(BleConstant.openDataTransmitOrder));
                        Log.i(TAG,"开启数据指令:"+isOpenDataTransmitOrderSend);

                        Thread.sleep(100);
                        Log.i(TAG, "查询设备信息");
                        sendLookBleBatteryInfoOrder();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    //读取设备版本信息、电量信息，鞋垫和二代衣服用的同样的方式
    private void readSecondGenerationDeviceInfo(final String address, final int deviceType) {
        new Thread(){

            private boolean isReadBatterySendOK;
            private boolean isReadHardwareRevisionSendOK;
            private boolean isReadSoftwareRevisionSendOK;
            private int allLoopCount;

            @Override
            public void run() {

                while (true){
                    if (allLoopCount==0){
                        long sleepTime = 0;
                        if (deviceType == BleConstant.sportType_Cloth){
                            sleepTime = 100;
                        }
                        else if (deviceType == BleConstant.sportType_Insole){
                            sleepTime = 5000;   //鞋垫主机在连接成功后需要睡眠5秒，设备电量才能正确的读取到
                        }

                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (!isReadBatterySendOK){
                        UUID serUuid = UUID.fromString(BleConstant.readInsoleBatterySerUuid);
                        UUID charUuid = UUID.fromString(BleConstant.readInsoleBatteryCharUuid);
                        isReadBatterySendOK = mLeProxy.readCharacteristic(address, serUuid, charUuid);
                        Log.i(TAG,"isReadBatterySendOK:"+ isReadBatterySendOK);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    UUID readInsoleDeviceInfoSerUuid = UUID.fromString(BleConstant.readInsoleDeviceInfoSerUuid);
                    if (!isReadHardwareRevisionSendOK){
                        UUID readInsoleDeviceInfoHardwareRevisionCharUuid = UUID.fromString(BleConstant.readInsoleDeviceInfoHardwareRevisionCharUuid);
                        isReadHardwareRevisionSendOK = mLeProxy.readCharacteristic(address, readInsoleDeviceInfoSerUuid, readInsoleDeviceInfoHardwareRevisionCharUuid);
                        Log.i(TAG,"isReadHardwareRevisionSendOK:"+ isReadHardwareRevisionSendOK);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (!isReadSoftwareRevisionSendOK){
                        UUID readInsoleDeviceInfoSoftwareRevisionCharUuid = UUID.fromString(BleConstant.readInsoleDeviceInfoSoftwareRevisionCharUuid);
                        isReadSoftwareRevisionSendOK = mLeProxy.readCharacteristic(address, readInsoleDeviceInfoSerUuid, readInsoleDeviceInfoSoftwareRevisionCharUuid);
                        Log.i(TAG,"isReadSoftwareRevisionSendOK:"+ isReadSoftwareRevisionSendOK);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    allLoopCount++;
                    Log.i(TAG,"allLoopCount:"+allLoopCount);

                    if ((isReadBatterySendOK && isReadHardwareRevisionSendOK && isReadSoftwareRevisionSendOK) || allLoopCount==10){
                        //三次都发送成功或者已经循环10次（防止一直循环执行），则退出
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    //发送查询设备电量信息指令
    public void sendLookBleBatteryInfoOrder() {
        if (mConnectionConfiguration.deviceType==BleConstant.sportType_Cloth){
            if (mIsConnectted){
                if (!TextUtils.isEmpty(mClothDeviceConnecedMac)){
                    boolean send = mLeProxy.send(mClothDeviceConnecedMac, DataUtil.hexToByteArray(BleConstant.readDeviceIDOrder));
                    Log.i(TAG,"查看电量 send："+send);
                }
            }
        }
        else {
            UUID serUuid = UUID.fromString(BleConstant.readInsoleBatterySerUuid);
            UUID charUuid = UUID.fromString(BleConstant.readInsoleBatteryCharUuid);
            for (String address : mInsoleDeviceBatteryInfos.keySet()) {
                boolean isSendOK = mLeProxy.readCharacteristic(address, serUuid, charUuid);
                Log.i(TAG,"查看电量 isSendOK:"+isSendOK);
            }
        }
    }

    //检查设备电量   5分钟读一次电量
    private void checkDeviceBatteryTimerTask() {
        long timeSpan = 1000*60*5;
        TimerTaskUtil mReadDeviceBatteryTaskUtil = new TimerTaskUtil();
        mReadDeviceBatteryTaskUtil.startTimeRiseTimerTask(timeSpan, new TimerTask() {
            @Override
            public void run() {
                sendLookBleBatteryInfoOrder();
            }
        });
    }

    //给蓝牙设备同步指令
    private void startSynDeviceOrderTimerTask(){
        long timeSpan = 1000;
        TimerTaskUtil mSysOrderTimerTaskUtil = new TimerTaskUtil();
        mSysOrderTimerTaskUtil.startTimeRiseTimerTask(timeSpan, new TimerTask() {
            @Override
            public void run() {
                sendSynOrderToDevice();
            }
        });
    }

    private void sendSynOrderToDevice() {
        if (mIsConnectted && mIsDataStart){
            int maxRate = 220- mConnectionConfiguration.userAge;
            String hrateIndexHex = "02";
            if (mCurrentHeartRate <=maxRate*0.75){
                hrateIndexHex = "02";
            }
            else if (maxRate*0.75<mCurrentHeartRate && mCurrentHeartRate<=maxRate*0.95){
                hrateIndexHex = "01";
            }
            else if (maxRate*0.95<mCurrentHeartRate){
                hrateIndexHex = "00";
            }
            String hexSynOrder = "FF070B"+ EcgAccDataUtil.getDataHexStringHaveScend()+hrateIndexHex+"16";
            boolean send = mLeProxy.send(mClothDeviceConnecedMac, DataUtil.hexToByteArray(hexSynOrder));  //有可能会抛出android.os.DeadObjectException
            Log.i(TAG,"同步指令connecMac:"+ mClothDeviceConnecedMac +",hrateIndexHex:"+hrateIndexHex+"  send:"+send);
        }
    }

    public static class ConnectionConfiguration {
        public int userAge;  //用户年龄
        public boolean isAutoOffline;  //是否在蓝牙断开后自动进入离线
        public int deviceType;   // 设备类型，暂时有衣服、鞋垫
        public int clothDeviceType;   //衣服蓝牙硬件版本类型,暂时有 Ble，AMSU_E开头旧主机，AMSU_E开头神念主机，AMSU_E阿木主机
        public boolean isNeedWriteFileHead;  //写入文件时是否需要些写文件头

        public ConnectionConfiguration(int userAge, boolean isAutoOffline, int deviceType, int clothDeviceType,boolean isNeedWriteFileHead) {
            this.userAge = userAge;
            this.isAutoOffline = isAutoOffline;
            this.deviceType = deviceType;
            this.clothDeviceType = clothDeviceType;
            this.isNeedWriteFileHead = isNeedWriteFileHead;
        }
    }

    public void setmIsDataStart(boolean mIsDataStart) {
        this.mIsDataStart = mIsDataStart;
    }

    public int getClothCurrBatteryPowerPercent() {
        return clothCurrBatteryPowerPercent;
    }

    public void setClothCurrBatteryPowerPercent(int clothCurrBatteryPowerPercent) {
        this.clothCurrBatteryPowerPercent = clothCurrBatteryPowerPercent;
    }

    public ConnectionConfiguration getmConnectionConfiguration() {
        return mConnectionConfiguration;
    }

    public Map<String, BleDevice> getmInsoleDeviceBatteryInfos() {
        return mInsoleDeviceBatteryInfos;
    }

    public void setCurrentHeartRate(int currentHeartRate) {
        this.mCurrentHeartRate = currentHeartRate;
    }

    public boolean ismIsConnectted() {
        return mIsConnectted;
    }


    public String getmClothDeviceConnecedMac() {
        return mClothDeviceConnecedMac;
    }

    public void setmClothDeviceConnecedMac(String mClothDeviceConnecedMac) {
        this.mClothDeviceConnecedMac = mClothDeviceConnecedMac;
    }
}
