package com.amsu.healthy.service;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.ConnectToWifiGudieActivity1;
import com.amsu.healthy.activity.HealthyDataActivity;
import com.amsu.healthy.activity.LockScreenActivity;
import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.activity.MyDeviceActivity;
import com.amsu.healthy.activity.StartRunActivity;
import com.amsu.healthy.activity.insole.InsoleLockScreenActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.EcgFilterUtil_1;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.PopupWindowUtil;
import com.amsu.healthy.utils.WakeLockUtil;
import com.amsu.healthy.utils.wifiTramit.DeviceOffLineFileUtil;
import com.ble.api.DataUtil;
import com.ble.ble.BleService;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class CommunicateToBleService extends Service {
    private static final String TAG = "CommunicateToBleService";

    public static String clothDeviceConnecedMac;   //当前连接的蓝牙mac地址
    public static String mInsole_connecMac1;   //当前连接的蓝牙mac地址
    public static String mInsole_connecMac2;   //当前连接的蓝牙mac地址
    public static int mInsole_connecMac1_needCorrect = -1;   //当前连接的蓝牙mac地址
    public static int mInsole_connecMac2_needCorrect = -1;   //当前连接的蓝牙mac地址

    public static boolean mIsConnectted =false;
    private boolean mIsConnectting =false;
    private DeviceOffLineFileUtil deviceOffLineFileUtil;
    private BluetoothAdapter mBluetoothAdapter;
    public LeProxy mLeProxy;
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 5000;
    private Intent calCuelectricVPercentIntent;
    private static Service mContext;
    //public static int mInsoleConnectedCount = 0;


    private PopupWindowUtil popupWindowUtil;
    public static EcgFilterUtil_1 ecgFilterUtil_1;
    private MyApplication mApplication;
    private static Map<String, Device> mInsoleDeviceBatteryInfos;

    public CommunicateToBleService() {

    }

    /*@Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG,"onStartJob");
        MyUtil.startServices(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG,"onStopJob");
        MyUtil.scheduleService(this,1,CommunicateToBleService.class.getName());
        return false;
    }*/

    @Override
    public void onCreate() {
        Log.i(TAG,"onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");
        Log.i(TAG,"isThisServiceStarted:"+isThisServiceStarted);
        if (!isThisServiceStarted){
            isThisServiceStarted = false;
            mContext = this;
            WakeLockUtil.acquireWakeLock(this);
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
                mLeProxy = LeProxy.getInstance();
                Log.i(TAG,"mBluetoothAdapter.getState():"+mBluetoothAdapter.getState());
            }

            stratListenScrrenBroadCast();
            //MyUtil.scheduleService(this,1,CommunicateToBleService.class.getName());

            //setServiceForegrounByNotify();
            init();




            popupWindowUtil = new PopupWindowUtil();
            ecgFilterUtil_1 = new EcgFilterUtil_1();


            IntentFilter statusFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mStatusReceive, statusFilter);

            /*List<AppAbortDataSave> abortDataListFromSP = AppAbortDbAdapterUtil.getAbortDataListFromSP();
            Log.i(TAG,"abortDataListFromSP:"+abortDataListFromSP.size());
            if (abortDataListFromSP.size() == 1){
                mIsNeedStartRunningActivity = true;
                Log.i(TAG,"SplashActivity.isSplashActivityStarted:"+SplashActivity.isSplashActivityStarted);
                if (!SplashActivity.isSplashActivityStarted){
                    Intent intent1 = new Intent(this,StartRunActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtra(Constant.isNeedRecoverAbortData,true);
                    startActivity(intent1);
                }
                Log.i(TAG,"mIsNeedStartRunningActivity:"+ mIsNeedStartRunningActivity);
            }
            else if (abortDataListFromSP.size() > 1){
                MyUtil.putStringValueFromSP("abortDatas","");
            }*/
        }
        return START_STICKY;
    }



    private boolean isBluetoothEnable;
    private boolean isThisServiceStarted;

    public static IntentFilter makeFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(LeProxy.ACTION_GATT_CONNECTED);
        filter.addAction(LeProxy.ACTION_GATT_DISCONNECTED);
        filter.addAction(LeProxy.ACTION_CONNECT_ERROR);
        filter.addAction(LeProxy.ACTION_CONNECT_TIMEOUT);
        filter.addAction(LeProxy.ACTION_GATT_SERVICES_DISCOVERED);

        filter.addAction(LeProxy.ACTION_GATT_DISCONNECTED);
        filter.addAction(LeProxy.ACTION_RSSI_AVAILABLE);
        filter.addAction(LeProxy.ACTION_DATA_AVAILABLE);
        return filter;
    }

    private void init() {
        //绑定蓝牙，获取蓝牙服务
        bindService(new Intent(this, BleService.class), mConnection, BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, makeFilter());

        calCuelectricVPercentIntent = new Intent(MainActivity.ACTION_CHARGE_CHANGE);

        mApplication = (MyApplication) getApplication();
        Log.i(TAG,"mApplication:"+mApplication);
        mInsoleDeviceBatteryInfos = mApplication.getInsoleDeviceBatteryInfos();

        dealwithPhoneBleOpen();

        checkDeviceCharge();
        sendDeviceSynOrderToBlueTooth();  //当设备连接成功后才开始发送同步指令

        /*new Thread(){
            @Override
            public void run() {
                super.run();

                while (true){
                    //Log.i(TAG,"1s到");
                    //Log.i(TAG,"isBluetoothEnable:"+isBluetoothEnable);
                    //Log.i(TAG,"mBluetoothAdapter.getState():"+mBluetoothAdapter.getState());

                    if (mBluetoothAdapter!=null && mBluetoothAdapter.getState()==BluetoothAdapter.STATE_ON) {
                        if (!isBluetoothEnable){
                            scanLeDevice(true);
                            //Log.i(TAG,"重新扫描");
                            isBluetoothEnable = true;
                        }
                    }
                    else {
                        isBluetoothEnable = false;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
*/


    }

    private void dealwithPhoneBleOpen() {
        scanLeDevice(true);
    }

    private void dealwithPhoneBleClose() {
        scanLeDevice(false);
    }


    //检查衣服电量
    private void checkDeviceCharge() {
        deviceOffLineFileUtil = new DeviceOffLineFileUtil();
        deviceOffLineFileUtil.setTransferTimeOverTime(new DeviceOffLineFileUtil.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                Log.i(TAG,"onTomeOut 查看电量");
                sendLookEleInfoOrder(mLeProxy);
            }
        },60*5);//5分钟读一次电量
    }

    //发送查询设备配置信息指令
    public static void sendLookEleInfoOrder(LeProxy leProxy) {
        if (MyApplication.deivceType==Constant.sportType_Cloth){
            if (mIsConnectted){
                if ( !MyUtil.isEmpty(clothDeviceConnecedMac)){
                    boolean send = leProxy.send(clothDeviceConnecedMac, DataUtil.hexToByteArray(Constant.readDeviceIDOrder), true);
                    Log.i(TAG,"MainActivity.mLeService.send："+send);
                }
            }
        }
        else {
            UUID serUuid = UUID.fromString(Constant.readInsoleBatterySerUuid);
            UUID charUuid = UUID.fromString(Constant.readInsoleBatteryCharUuid);
            for (String address : mInsoleDeviceBatteryInfos.keySet()) {
                boolean isSendOK = leProxy.readCharacteristic(address, serUuid, charUuid);
                Log.i(TAG,"isSendOK:"+isSendOK);
            }
        }
    }

    boolean mScanning;

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            //if (mBluetoothAdapter.isEnabled()) {
            if (mBluetoothAdapter.getState()==BluetoothAdapter.STATE_ON) {
                if (mScanning)
                    return;
                mScanning = true;
                boolean b = mBluetoothAdapter.startLeScan(mLeScanCallback);
                Log.i(TAG,"startLeScan:"+b);
            } else {
                Log.i(TAG,"蓝牙未连接");
            }
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Log.i(TAG,"stopLeScan");
            mScanning = false;
        }
    }

    //扫描蓝牙回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            //BLE#0x44A6E51FC5BF,44:A6:E5:1F:C5:BF,null,10,2
            //null,72:A8:23:AF:25:42,null,10,0
            //null,63:5C:3E:B6:A0:ae,null,10,0
            if (device==null)return;
            Log.i(TAG,"onLeScan  device:"+device.getName()+","+device.getAddress()+","+device.getUuids()+","+device.getBondState()+","+device.getType());
            /*String scanRecordString = MyUtil.bytesToHexString(scanRecord);
            Log.i(TAG,"scanRecordString:"+scanRecordString);

            if (!TextUtils.isEmpty(scanRecordString)) {
                String usefulData = scanRecordString.substring(86, 106);//4252 3d6400cdff003a9e
                String strVersion = usefulData.substring(4,6);
                int intVersion = Integer.parseInt(strVersion, 16);
                String strPower = usefulData.substring(6,8);
                int intPower = Integer.parseInt(strPower, 16);
                Log.i(TAG, strVersion + "=strVersion回调方法-onLeScan()，版本值intVersion="+ intVersion);
                Log.i(TAG, strPower + "=strPower回调方法-onLeScan()，电量值intPower="+ intPower);

            }*/


            String leName = device.getName();
            if (MyUtil.isEmpty(leName))return;

            if (MyApplication.deivceType==Constant.sportType_Cloth && (leName.startsWith("BLE") || leName.startsWith("AMSU")) ) {
                //String stringValueFromSP = MyUtil.getStringValueFromSP(Constant.currectDeviceLEMac);
                Device deviceFromSP = MyUtil.getDeviceFromSP();
                if (deviceFromSP==null) return;
                if (device.getAddress().equals(deviceFromSP.getMac())){  //只有扫描到的蓝牙是sp里的当前设备时（激活状态），才能进行连接
                    Log.i(TAG,"stringValueFromSP:"+deviceFromSP.getMac());
                    Log.i(TAG,"mIsConnectted:"+ mIsConnectted);
                    Log.i(TAG,"mIsConnectting:"+ mIsConnectting);

                    //配对成功`
                    clothDeviceConnecedMac = device.getAddress();
                    if (!mIsConnectted && !mIsConnectting){
                        //没有链接上，并且没有正在链接
                        Log.i(TAG,"clothDeviceConnecedMac:"+ clothDeviceConnecedMac);

                        mIsConnectting = true;

                        scanLeDevice(false);

                        //boolean connect = mLeService.connect(clothDeviceConnecedMac, false);//链接
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(50);

                                    boolean connect = mLeProxy.connect(clothDeviceConnecedMac, false);
                                    Log.i(TAG,"connect:"+connect);

                                    if (!connect){
                                        mIsConnectting = false;
                                        scanLeDevice(true);
                                    }
                                    Log.i(TAG,"开始连接");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }
            }
            else if (MyApplication.deivceType==Constant.sportType_Insole && leName.startsWith("AMSU_P")){
                Device deviceFromSP = MyUtil.getDeviceFromSP(Constant.sportType_Insole);
                Log.i(TAG,"deviceFromSP:"+deviceFromSP);
                if (deviceFromSP==null) return;

                String[] split = deviceFromSP.getMac().split(",");

                //Log.i(TAG,"split.length:"+split.length);

                if (split.length==2 && (device.getAddress().equals(split[0]) || device.getAddress().equals(split[1]))){
                    Log.i(TAG,"AMSU_P_stringValueFromSP:"+deviceFromSP.getMac());
                    Log.i(TAG,"AMSU_P_isConnectted:"+ mIsConnectted);
                    Log.i(TAG,"AMSU_P_isConnectting:"+ mIsConnectting);


                    //配对成功
                    clothDeviceConnecedMac = device.getAddress();
                    if (!mIsConnectted && !mIsConnectting){
                        //没有链接上，并且没有正在链接
                        Log.i(TAG,"AMSU_P_connecMac:"+ clothDeviceConnecedMac);


                        mIsConnectting = true;

                        scanLeDevice(false);

                        //boolean connect = mLeService.connect(clothDeviceConnecedMac, false);//链接
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(50);

                                    boolean connect = mLeProxy.connect(clothDeviceConnecedMac, false);
                                    Log.i(TAG,"AMSU_P_connect:"+connect);

                                    if (!connect){
                                        mIsConnectting = false;
                                        scanLeDevice(true);
                                    }
                                    Log.i(TAG,"AMSU_P_开始连接");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    Log.i(TAG,"e:"+e);
                                }
                            }
                        }.start();
                    }
                }
            }

        }
    };


    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG,"onServiceConnected");
            LeProxy.getInstance().setBleService(service);
        }
    };

    int [] ecgInts = new int[HealthyDataActivity.oneGroupLength];
    int tempCount ;

    private void dealwithLebDataChange(String hexData,String address,String uuid) {
        //Log.i(TAG, "onCharacteristicChanged() - " + hexData);
        if(hexData.startsWith("FF 83")){
            if (tempCount<15){
                if (hexData.length()==44){
                    tempCount++;
                    ECGUtil.geIntEcgaArr(hexData, " ", 3, HealthyDataActivity.oneGroupLength,ecgInts); //一次的数据，10位
                    //滤波处理
                    for (int i=0;i<ecgInts.length;i++){
                        ecgInts[i] = ecgFilterUtil_1.miniEcgFilterLp(ecgFilterUtil_1.miniEcgFilterHp (ecgFilterUtil_1.NotchPowerLine(ecgInts[i], 1)));
                    }
                }
            }
        }
        else if (hexData.startsWith("FF 85")){
            Log.i(TAG,"SDhexData："+hexData);
            if (hexData.split(" ")[3].equals("01")){
                //有离线数据
                Log.i(TAG,"MyApplication.mCurrApplicationActivity:"+MyApplication.mCurrApplicationActivity);
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
        else if (hexData.startsWith("FF 84")){
            Log.i(TAG,"设备版本号："+hexData);
            //FF 84 0B 11 05 02 11 06 02 0C 90 00 16 FF 83
            dealwithDeviceInfo(hexData);
        }
        else if (hexData.length()==2){  // 3E
            int intPower = Integer.parseInt(hexData, 16);
            Log.i(TAG,"鞋垫电量："+" address:"+address+" hexData:"+hexData+" intPower:"+intPower);
            Device device = mInsoleDeviceBatteryInfos.get(address);
            if (device==null){
                device = new Device();
            }
            device.setBattery(intPower);

            mInsoleDeviceBatteryInfos.put(address,device);
            mApplication.setInsoleDeviceBatteryInfos(mInsoleDeviceBatteryInfos);

            sendBroadcast(calCuelectricVPercentIntent);
        }
        else if (hexData.length()==14){  // 32 2E 32 2E 30
            String deviceVersionString = MyUtil.convertHexToString(hexData);
            if (uuid.equals(Constant.readInsoleDeviceInfoHardwareRevisionCharUuid)){
                //硬件版本
                Log.i(TAG,"鞋垫硬件版本："+" deviceVersionString:"+deviceVersionString);
                Device device = mInsoleDeviceBatteryInfos.get(address);
                if (device==null){
                    device = new Device();
                }
                device.setHardWareVersion(deviceVersionString);

                mInsoleDeviceBatteryInfos.put(address,device);
                mApplication.setInsoleDeviceBatteryInfos(mInsoleDeviceBatteryInfos);

                //MyUtil.putStringValueFromSP(Constant.hardWareVersion_insole,deviceVersionString);
            }
            else if (uuid.equals(Constant.readInsoleDeviceInfoSoftwareRevisionCharUuid)){
                //软件版本
                Log.i(TAG,"鞋垫软件版本："+" deviceVersionString:"+deviceVersionString);
                Device device = mInsoleDeviceBatteryInfos.get(address);
                if (device==null){
                    device = new Device();
                }
                device.setSoftWareVersion(deviceVersionString);

                mInsoleDeviceBatteryInfos.put(address,device);
                mApplication.setInsoleDeviceBatteryInfos(mInsoleDeviceBatteryInfos);

                //MyUtil.putStringValueFromSP(Constant.softWareVersion_insole,deviceVersionString);
            }
        }
        /*else if (hexData.startsWith("AA")){
            if (mIsJumpTOCorrected)return;
            Log.i(TAG,"address:"+address+",mInsole_connecMac1:"+mInsole_connecMac1+",mInsole_connecMac2:"+mInsole_connecMac2);
            Log.i(TAG,"mInsole_connecMac1_needCorrect:"+mInsole_connecMac1_needCorrect+",mInsole_connecMac2_needCorrect:"+mInsole_connecMac2_needCorrect);

            //鞋垫数据，如果是以AA 00开头，需要校准
            if (address.equals(mInsole_connecMac1) && mInsole_connecMac1_needCorrect==-1){
                //一个脚
                if (hexData.startsWith("AA 00")){
                    mInsole_connecMac1_needCorrect  = 2;
                }
                else if (hexData.startsWith("AA 52") || hexData.startsWith("AA 4C")){
                    mInsole_connecMac1_needCorrect  = 1;
                }
            }
            else if (address.equals(mInsole_connecMac2) && mInsole_connecMac2_needCorrect==-1){
                if (hexData.startsWith("AA 00")){
                    mInsole_connecMac2_needCorrect  = 2;
                }
                else if (hexData.startsWith("AA 52") || hexData.startsWith("AA 4C")){
                    mInsole_connecMac2_needCorrect  = 1;
                }
            }

            //如果2个中的其中一个需要校准，则跳到校准页面
            if ((mInsole_connecMac1_needCorrect!=-1 && mInsole_connecMac2_needCorrect!=-1) && (mInsole_connecMac1_needCorrect==2 || mInsole_connecMac2_needCorrect==2)){
                Intent intent= new Intent(this, CorrectInsoleActivity.class);
                intent.putExtra("insole_connecMac1",mInsole_connecMac1);
                intent.putExtra("insole_connecMac2",mInsole_connecMac2);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                mIsJumpTOCorrected = true;
            }
        }*/
        else if (hexData.length() > 40) {
            if (!mIsDataStart){
                mIsDataStart = true;
                //sendDeviceSynOrderToBlueTooth();
            }

            /*if (mOneFrameEcgDataCount<15){
                mOneFrameEcgDataCount++;
            }
            else {
                mOneFrameEcgDataCount = 0;
                DeviceOffLineFileUtil.startTime();
            }*/
        }
    }



    private boolean mIsJumpTOCorrected;
    private boolean isShowAlertDialog;
    private int isNeedCorrectDevice = -1;

    private void showUploadOffLineData(Activity activity){
        if (!isShowAlertDialog){
            ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(activity);
            chooseAlertDialogUtil.setAlertDialogText("发现有离线文件，是否现在进行上传","是","否");
            chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
                @Override
                public void onConfirmClick() {
                    Intent intent = new Intent(MyApplication.mCurrApplicationActivity, ConnectToWifiGudieActivity1.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            isShowAlertDialog = true;
        }
    }

    int mOneFrameEcgDataCount = 0;

    //给蓝牙发开始数据传输指令
    private void sendStartDataTransmitOrderToBlueTooth(){
        Log.i(TAG,"sendStartDataTransmitOrderToBlueTooth");
        Log.i(TAG,"mIsConnectted:"+ mIsConnectted +"          mIsDataStart: "+mIsDataStart);
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (mIsConnectted && !mIsDataStart){
                    try {
                        /*Thread.sleep(80);
                        Log.i(TAG, "查询设备信息");
                        sendLookEleInfoOrder();*/

                        Thread.sleep(100);

                        boolean isCheckIsHaveDataOrderSend = mLeProxy.send(clothDeviceConnecedMac, DataUtil.hexToByteArray(Constant.checkIsHaveDataOrder), true);
                        Log.i(TAG, "查询SD卡是否有数据："+isCheckIsHaveDataOrderSend);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Thread.sleep(200);
                        }
                        else {
                            Thread.sleep(2000);
                        }


                        String writeConfigureOrder = "FF010E"+ HealthyDataActivity.getDataHexString()+"0016";
                        Log.i(TAG,"writeConfigureOrder:"+writeConfigureOrder);
                        //mLeService.send(clothDeviceConnecedMac, Constant.writeConfigureOrder,true);
                        //mLeService.send(clothDeviceConnecedMac, writeConfigureOrder,true);

                        boolean isWriteConfigureOrderSend = mLeProxy.send(clothDeviceConnecedMac, DataUtil.hexToByteArray(writeConfigureOrder), true);
                        Log.i(TAG,"写配置:"+isWriteConfigureOrderSend);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Thread.sleep(200);
                        }
                        else {
                            Thread.sleep(2000);
                        }

                        boolean isOpenDataTransmitOrderSend = mLeProxy.send(clothDeviceConnecedMac, DataUtil.hexToByteArray(Constant.openDataTransmitOrder), true);
                        Log.i(TAG,"开启数据指令:"+isOpenDataTransmitOrderSend);
                        Thread.sleep(100);

                        if (MyApplication.clothCurrBatteryPowerPercent==-1){
                            Log.i(TAG, "查询设备信息");
                            sendLookEleInfoOrder(mLeProxy);
                        }

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    //查询衣服设备电量
    private void sendStartQuantityOfElectricToBlueTooth(){
        Log.i(TAG,"sendStartQuantityOfElectricToBlueTooth");
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (mIsConnectted){
                    if (MyApplication.clothCurrBatteryPowerPercent!=-1){
                        try {
                            Thread.sleep(80);
                            Log.i(TAG, "查询设备信息");
                            sendLookEleInfoOrder(mLeProxy);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        break;
                    }
                }
            }
        }.start();
    }

    private void sendCorrectOrderToDevice(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1000);
                    UUID serUuid = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
                    UUID charUuid = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
                    String data  = "B9";
                    data = data.replaceAll("\r\n", "\n");
                    data = data.replaceAll("\n", "\r\n");
                    boolean send = mLeProxy.send(clothDeviceConnecedMac, serUuid, charUuid, data.getBytes(), false);
                    Log.i(TAG,"clothDeviceConnecedMac："+ clothDeviceConnecedMac);
                    Log.i(TAG,"发送校准指令："+send);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    int temp = 0;

    //给蓝牙设备同步指令
    private void sendDeviceSynOrderToBlueTooth(){
        Log.i(TAG,"sendDeviceSynOrderToBlueTooth");
        MyTimeTask.startTimeRiseTimerTask( 1000, new MyTimeTask.OnTimeChangeAtScendListener() {
            @Override
            public void onTimeChange(Date date) {
                if (mIsConnectted && mIsDataStart && MyApplication.isNeedSynMsgToDevice){
                    int maxRate = 220- HealthyIndexUtil.getUserAge();
                    String hrateIndexHex = "02";
                    if (MyApplication.currentHeartRate <=maxRate*0.75){
                        hrateIndexHex = "02";
                    }
                    else if (maxRate*0.75<MyApplication.currentHeartRate && MyApplication.currentHeartRate<=maxRate*0.95){
                        hrateIndexHex = "01";
                    }
                    else if (maxRate*0.95<MyApplication.currentHeartRate ){
                        hrateIndexHex = "00";
                    }
                    String hexSynOrder = "FF070B"+HealthyDataActivity.getDataHexStringHaveScend()+hrateIndexHex+"16";
                    boolean send = mLeProxy.send(clothDeviceConnecedMac, DataUtil.hexToByteArray(hexSynOrder), true);  //有可能会抛出android.os.DeadObjectException
                    Log.i(TAG,"同步指令connecMac:"+ clothDeviceConnecedMac +",hrateIndexHex:"+hrateIndexHex+"  send:"+send);
                }
            }
        });
    }

    private void dealwithDeviceInfo(String hexData) {
        String aString = hexData;
        String[] split = aString.split(" ");

        int [] ints = new int[8];
        ECGUtil.geIntEcgaArr(aString, " ", 3, 8,ints); //一次的数据，10位

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
        sendBroadcast(calCuelectricVPercentIntent);
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




        /*if (electricV<3) {
            return 0;
        }
        else if (3<electricV && electricV<3.45) {
            return 5;
        }
        else if (3.45<electricV && electricV<3.68) {
            return 10;
        }
        else if (3.68<electricV && electricV<3.74) {
            return 20;
        }
        else if (3.74<electricV && electricV<3.77) {
            return 30;
        }
        else if (3.77<electricV && electricV<3.79) {
            return 40;
        }
        else if (3.79<electricV && electricV<3.82) {
            return 50;
        }
        else if (3.82<electricV && electricV<3.87) {
            return 60;
        }
        else if (3.87<electricV && electricV<3.92) {
            return 70;
        }
        else if (3.92<electricV && electricV<3.98) {
            return 80;
        }
        else if (3.98<electricV && electricV<4.06) {
            return 90;
        }
        else if (4.06<electricV && electricV<4.20) {
            return 100;
        }
        else if (electricV>4.20) {
            return 100;
        }
        return 0;
         }
        */



    public static void setServiceForegrounByNotify(String title ,String content,int state) {

        if (mContext==null) return;

        //NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(mContext)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.logo_icon)
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.logo_icon))
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        //新建Intent，用在Activity传递数据，点击时跳到ShowArticleDetailActivity页面

        /*if (MyApplication.runningActivity==MyApplication.MainActivity){
            intent1 = new Intent(this, MainActivity.class);
        }
        else if (MyApplication.runningActivity==MyApplication.HealthyDataActivity){
            intent1 = new Intent(this, HealthyDataActivity.class);
        }
        else if (MyApplication.runningActivity==MyApplication.StartRunActivity){
            intent1 = new Intent(this, StartRunActivity.class);
        }*/
        //给另一个设置任务栈属性，FLAG_ACTIVITY_NEW_TASK表示新建一个任务栈来显示当前的Activity



        //PendingIntent 主要用于任务栏提醒和桌面weigetde 显示，

        //这里用4个参数需要注意下，130表示requestCode（请求马，自定义）
        //第三个参数书Intent对象，intent1是上面定义的 Intent对象
        //第四个对象是PendingIntent的标签属性，表叔显示方式，这里FLAG_UPDATE_CURRENT表示显示当前的通知，如果用新的通知时，更新当期的通知，这个属性注意下，如果不设置的话每次点击都是同一个通知

        if (state==1){
            Intent intent1 = new Intent(mContext, StartRunActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent activity = PendingIntent.getActivity(mContext, 130, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.contentIntent = activity;
        }
        else if (state==0){
            Intent intent1 = new Intent(mContext, HealthyDataActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent activity = PendingIntent.getActivity(mContext, 131, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.contentIntent = activity;
        }
        //nm.notify(0, notification);
        mContext.startForeground(1, notification); //将Service设置为前台服务
    }

    public static void  detoryServiceForegrounByNotify(){
        mContext.stopForeground(true);
    }

    boolean mIsDataStart = false;

    private KeyguardManager mKeyguardManager = null;
    private KeyguardManager.KeyguardLock mKeyguardLock = null;

    //监听屏幕锁屏，并启动自定义锁屏界面
    private void stratListenScrrenBroadCast() {
        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);

        BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                Log.i(TAG,"onReceive:"+intent.getAction());


                if (mApplication == null){
                    mApplication = (MyApplication) getApplication();
                }

                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_SCREEN_OFF.equals(action)){
                    Log.i(TAG,"屏幕变化:");

                    mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    mKeyguardLock = mKeyguardManager.newKeyguardLock("");
                    mKeyguardLock.disableKeyguard();

                    Intent i = new Intent();
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    if (Intent.ACTION_SCREEN_ON.equals(action)){
                        i.putExtra("isScroonOn",true);
                    }
                    else {
                        i.putExtra("isScroonOn",false);
                    }

                    int runningRecoverType = mApplication.getRunningRecoverType();
                    Log.i(TAG,"runningRecoverType:"+ runningRecoverType);

                    if (runningRecoverType>0){
                        if (runningRecoverType==Constant.sportType_Cloth){
                            i.setClass(context,LockScreenActivity.class);
                        }
                        else if (runningRecoverType==Constant.sportType_Insole){
                            i.setClass(context,InsoleLockScreenActivity.class);
                        }
                        context.startActivity(i);
                    }

                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);

    }

    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);

            switch (intent.getAction()){
                case LeProxy.ACTION_GATT_CONNECTED:
                    Log.i(TAG,"已连接 " + " "+address + " ");
                    Log.i(TAG,"MyApplication.isHaveDeviceConnectted:"+MyApplication.isHaveDeviceConnectted);
                    Log.i(TAG,"MyApplication.mCurrApplicationActivity:"+MyApplication.mCurrApplicationActivity);
                    mIsConnectting = false;
                    mIsConnectted = true;



                    if (MyApplication.deivceType==Constant.sportType_Cloth){
                        scanLeDevice(false);//停止扫描
                        if (!MyApplication.isHaveDeviceConnectted){
                            MyApplication.isHaveDeviceConnectted = mIsConnectted = true;
                            //MyUtil.showDeviceConnectedChangePopWindow(1,getResources().getString(R.string.sportswear_connection_successful));
                            if (popupWindowUtil!=null){
                                popupWindowUtil.showDeviceConnectedChangePopWindow(1,getResources().getString(R.string.sportswear_connection_successful));
                            }
                        }
                        MyApplication.clothConnectedMacAddress = clothDeviceConnecedMac;
                        sendStartQuantityOfElectricToBlueTooth();

                        deviceOffLineFileUtil.startTime();
                        sendStartDataTransmitOrderToBlueTooth();

                    }
                    else if (MyApplication.deivceType==Constant.sportType_Insole){
                        //mInsoleConnectedCount++;
                        Log.e(TAG,"鞋垫连接mInsoleConnectedCount："+MyApplication.insoleConnectedMacAddress.size());
                        if (MyApplication.insoleConnectedMacAddress.size()==0){
                            mIsConnectted = false;
                            scanLeDevice(true);//继续扫描另一个鞋垫
                            mInsole_connecMac1 = address;
                            MyApplication.insoleConnectedMacAddress.add(address);
                            mInsoleDeviceBatteryInfos.put(address,new Device());
                            readInsoleDeviceInfo(address,true,true,true);
                        }
                        else if (MyApplication.insoleConnectedMacAddress.size()==1){
                            scanLeDevice(false);//停止扫描
                            if (!MyApplication.isHaveDeviceConnectted){
                                MyApplication.isHaveDeviceConnectted = mIsConnectted = true;
                                //MyUtil.showDeviceConnectedChangePopWindow(1,getResources().getString(R.string.insole_connection_successful));
                                if (popupWindowUtil!=null){
                                    popupWindowUtil.showDeviceConnectedChangePopWindow(1,getResources().getString(R.string.insole_connection_successful));
                                }
                            }

                            //MyApplication.clothConnectedMacAddress = clothDeviceConnecedMac;
                            mInsole_connecMac2 = address;
                            MyApplication.insoleConnectedMacAddress.add(address);
                            mInsoleDeviceBatteryInfos.put(address,new Device());
                            readInsoleDeviceInfo(address,true,true,true);
                            Log.e(TAG,"2个鞋垫都连接成功====================================================================================================================");
                        }


                    }
                    break;
                case LeProxy.ACTION_GATT_DISCONNECTED:
                    dealwithBelDisconnected(address);
                    break;
                case LeProxy.ACTION_CONNECT_ERROR:
                    Log.w(TAG,"连接异常 "+address);
                    mIsConnectted = false;
                    mIsConnectting = false;
                    mIsDataStart = false;
                    scanLeDevice(true);//停止扫描

                    if (MyApplication.isHaveDeviceConnectted){
                        MyApplication.isHaveDeviceConnectted = false;
                        //MyUtil.showDeviceConnectedChangePopWindow(0,getResources().getString(R.string.sportswear_connection_disconnected));
                        if (popupWindowUtil!=null){
                            popupWindowUtil.showDeviceConnectedChangePopWindow(0,getResources().getString(R.string.sportswear_connection_disconnected));
                        }
                    }

                    if (MyApplication.deivceType==Constant.sportType_Cloth){
                        deviceOffLineFileUtil.stopTime();
                        MyApplication.clothConnectedMacAddress = "";

                        calCuelectricVPercentIntent.putExtra("clothCurrBatteryPowerPercent",-1);
                        sendBroadcast(calCuelectricVPercentIntent);
                        MyApplication.clothCurrBatteryPowerPercent = -1;
                    }
                    else if (MyApplication.deivceType==Constant.sportType_Insole){
                       // MyUtil.showDeviceConnectedChangePopWindow(0,getResources().getString(R.string.insole_connection_disconnected));
                        if (popupWindowUtil!=null){
                            popupWindowUtil.showDeviceConnectedChangePopWindow(0,getResources().getString(R.string.insole_connection_disconnected));
                        }
                        MyApplication.insoleConnectedMacAddress.remove(address);
                    }
                    break;
                case LeProxy.ACTION_CONNECT_TIMEOUT:
                    Log.w(TAG,"连接超时 "+address);
                    mIsConnectting = false;
                    mIsDataStart = false;
                    mIsConnectted = false;
                    scanLeDevice(true);//停止扫描

                    if (MyApplication.isHaveDeviceConnectted){
                        MyApplication.isHaveDeviceConnectted = false;
                        //MyUtil.showDeviceConnectedChangePopWindow(0,getResources().getString(R.string.sportswear_connection_disconnected));
                        if (popupWindowUtil!=null){
                            popupWindowUtil.showDeviceConnectedChangePopWindow(0,getResources().getString(R.string.sportswear_connection_disconnected));
                        }
                    }

                    if (MyApplication.deivceType==Constant.sportType_Cloth){
                        deviceOffLineFileUtil.stopTime();
                        MyApplication.clothConnectedMacAddress = "";

                        calCuelectricVPercentIntent.putExtra("clothCurrBatteryPowerPercent",-1);
                        sendBroadcast(calCuelectricVPercentIntent);
                        MyApplication.clothCurrBatteryPowerPercent = -1;
                    }
                    else if (MyApplication.deivceType==Constant.sportType_Insole){
                        //MyUtil.showDeviceConnectedChangePopWindow(0,getResources().getString(R.string.insole_connection_disconnected));
                        if (popupWindowUtil!=null){
                            popupWindowUtil.showDeviceConnectedChangePopWindow(0,getResources().getString(R.string.insole_connection_disconnected));
                        }
                        MyApplication.insoleConnectedMacAddress.remove(address);
                    }
                    break;
                case LeProxy.ACTION_GATT_SERVICES_DISCOVERED:
                    Log.i(TAG,"Services discovered: " + address);

                    if (MyApplication.deivceType==Constant.sportType_Cloth){
                        //数据交互指令放在线程中
                        /*deviceOffLineFileUtil.startTime();
                        sendStartDataTransmitOrderToBlueTooth();*/
                    }
                    else {

                    }
                    break;

                case LeProxy.ACTION_RSSI_AVAILABLE:// 更新rssi


                break;

                case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                    byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);
                    String uuid = intent.getStringExtra(LeProxy.EXTRA_UUID);
                    dealwithLebDataChange(DataUtil.byteArrayToHex(data),address,uuid);
                    break;


            }
        }
    };

    //读取鞋垫的设备信息
    private void readInsoleDeviceInfo(final String address, final boolean isReadBattery, final boolean isReadHardwareRevision, final boolean isReadSoftwareRevision) {
        new Thread(){

            private boolean isReadBatterySendOK;
            private boolean isReadHardwareRevisionSendOK;
            private boolean isReadSoftwareRevisionSendOK;
            private int allLoopCount;

            @Override
            public void run() {

                while (true){
                    if (allLoopCount==0){
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (isReadBattery && !isReadBatterySendOK){
                        UUID serUuid = UUID.fromString(Constant.readInsoleBatterySerUuid);
                        UUID charUuid = UUID.fromString(Constant.readInsoleBatteryCharUuid);
                        isReadBatterySendOK = mLeProxy.readCharacteristic(address, serUuid, charUuid);
                        Log.i(TAG,"isReadBatterySendOK:"+ isReadBatterySendOK);

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    UUID readInsoleDeviceInfoSerUuid = UUID.fromString(Constant.readInsoleDeviceInfoSerUuid);
                    if (isReadHardwareRevision && !isReadHardwareRevisionSendOK){
                        UUID readInsoleDeviceInfoHardwareRevisionCharUuid = UUID.fromString(Constant.readInsoleDeviceInfoHardwareRevisionCharUuid);
                        isReadHardwareRevisionSendOK = mLeProxy.readCharacteristic(address, readInsoleDeviceInfoSerUuid, readInsoleDeviceInfoHardwareRevisionCharUuid);
                        Log.i(TAG,"isReadHardwareRevisionSendOK:"+ isReadHardwareRevisionSendOK);

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (isReadSoftwareRevision && !isReadSoftwareRevisionSendOK){
                        UUID readInsoleDeviceInfoSoftwareRevisionCharUuid = UUID.fromString(Constant.readInsoleDeviceInfoSoftwareRevisionCharUuid);
                        isReadSoftwareRevisionSendOK = mLeProxy.readCharacteristic(address, readInsoleDeviceInfoSerUuid, readInsoleDeviceInfoSoftwareRevisionCharUuid);
                        Log.i(TAG,"isReadSoftwareRevisionSendOK:"+ isReadSoftwareRevisionSendOK);

                        try {
                            Thread.sleep(500);
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
                }
            }
        }.start();
    }


    private void dealwithBelDisconnected(String address){
        Log.w(TAG,"已断开 "+address);
        Log.i(TAG,"MyApplication.isHaveDeviceConnectted:"+ MyApplication.isHaveDeviceConnectted);
        Log.i(TAG,"MyApplication.mCurrApplicationActivity:"+MyApplication.mCurrApplicationActivity);

        mIsConnectted = false;
        mIsConnectting = false;
        scanLeDevice(true);//开始扫描
        if (MyApplication.isHaveDeviceConnectted){
            MyApplication.isHaveDeviceConnectted = false;
            //MyUtil.showDeviceConnectedChangePopWindow(0,getResources().getString(R.string.sportswear_connection_disconnected));
            if (popupWindowUtil!=null){
                popupWindowUtil.showDeviceConnectedChangePopWindow(0,getResources().getString(R.string.sportswear_connection_disconnected));
            }
        }

        if (MyApplication.deivceType== Constant.sportType_Cloth){
            MyApplication.clothConnectedMacAddress = "";
            deviceOffLineFileUtil.stopTime();
            mIsDataStart = false;
            calCuelectricVPercentIntent.putExtra("clothCurrBatteryPowerPercent",-1);
            sendBroadcast(calCuelectricVPercentIntent);
            MyApplication.clothCurrBatteryPowerPercent = -1;
        }
        else if (MyApplication.deivceType==Constant.sportType_Insole){
            //MyUtil.showDeviceConnectedChangePopWindow(0,getResources().getString(R.string.insole_connection_disconnected));
            if (popupWindowUtil!=null){
                popupWindowUtil.showDeviceConnectedChangePopWindow(0,getResources().getString(R.string.insole_connection_disconnected));
            }
            //mInsoleConnectedCount--;
            if (!MyUtil.isEmpty(address)){
                MyApplication.insoleConnectedMacAddress.remove(address);
            }

            if (address==null){
                MyApplication.insoleConnectedMacAddress.clear();
                mInsoleDeviceBatteryInfos.clear();
                mApplication.setInsoleDeviceBatteryInfos(mInsoleDeviceBatteryInfos);
            }

            mInsoleDeviceBatteryInfos.remove(address);
        }
    }


    //手机蓝牙状态监听
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");

        isThisServiceStarted = false;

        stopForeground(true);

        Intent intent = new Intent("com.amsu.healthy.servicedestroy");
        sendBroadcast(intent);

        WakeLockUtil.releaseWakeLock();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
