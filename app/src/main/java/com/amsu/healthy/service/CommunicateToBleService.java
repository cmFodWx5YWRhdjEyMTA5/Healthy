package com.amsu.healthy.service;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.ConnectToWifiModuleGudieActivity1;
import com.amsu.healthy.activity.HealthyDataActivity;
import com.amsu.healthy.activity.LockScreenActivity;
import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.activity.StartRunActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.wifiTramit.DeviceOffLineFileUtil;
import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;

import java.util.Date;

public class CommunicateToBleService extends Service {
    private static final String TAG = "CommunicateToBleService";

    private static BleService mLeService;
    public static String connecMac;   //当前连接的蓝牙mac地址
    public static boolean isConnectted  =false;
    private boolean isConnectting  =false;
    private DeviceOffLineFileUtil deviceOffLineFileUtil;
    private PowerManager.WakeLock wakeLock;
    private BluetoothAdapter mBluetoothAdapter;

    public CommunicateToBleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, CommunicateToBleService.class.getName());
        wakeLock.acquire();
        Log.i(TAG,"锁屏激活");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");
        if (!isThisServiceStarted){
            isThisServiceStarted = true;

            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
            }

            stratListenScrrenBroadCast();

            setServiceForegrounByNotify();
            init();
        }

       /* if (!isBluetoothEnable){
            startLeScanBlue();
            isBluetoothEnable = true;

            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
            }



            if (mBluetoothAdapter!=null){
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                Log.i(TAG,"开始扫描");
            }


            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mBluetoothAdapter!=null){
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        Log.i(TAG,"停止扫描");
                    }

                }
            }.start();
        }*/



        return START_STICKY;
    }

    private boolean isBluetoothEnable;
    private boolean isThisServiceStarted;

    private void init() {
        //绑定蓝牙，获取蓝牙服务
        bindService(new Intent(this, BleService.class), mConnection, BIND_AUTO_CREATE);

        new Thread(){
            @Override
            public void run() {
                super.run();


                while (true){
                    if (mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled()) {
                        if (!isBluetoothEnable){
                            startLeScanBlue();
                            isBluetoothEnable = true;
                        }
                    }
                    else {
                        isBluetoothEnable = false;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }.start();

        checkDeviceCharge();
        sendDeviceSynOrderToBlueTooth();  //当设备连接成功后才开始发送同步指令
    }

    //检查衣服电量
    private void checkDeviceCharge() {
        deviceOffLineFileUtil = new DeviceOffLineFileUtil();
        deviceOffLineFileUtil.setTransferTimeOverTime(new DeviceOffLineFileUtil.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                Log.i(TAG,"onTomeOut 查看电量");
                sendLookEleInfoOrder();
            }
        },60*5);//5分钟读一次电量
    }

    //发送查询设备配置信息指令
    public static void sendLookEleInfoOrder() {
        if (isConnectted){
            if (mLeService!=null && !MyUtil.isEmpty(connecMac)){
                mLeService.send(connecMac, Constant.readDeviceIDOrder,true);
                Log.i(TAG,"MainActivity.mLeService.send");
            }
        }
    }

    //开始扫描蓝牙
    public void startLeScanBlue(){
        if (mBluetoothAdapter!=null){
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            Log.i(TAG,"startLeScan");
            isBindService = true;
        }
    }

    boolean isBindService;

    //扫描蓝牙回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            //BLE#0x44A6E51FC5BF,44:A6:E5:1F:C5:BF,null,10,2
            //null,72:A8:23:AF:25:42,null,10,0
            //null,63:5C:3E:B6:A0:AE,null,10,0
            if (device==null)return;
            Log.i(TAG,"onLeScan  device:"+device.getName()+","+device.getAddress()+","+device.getUuids()+","+device.getBondState()+","+device.getType());
            String leName = device.getName();
            if (leName!=null && leName.startsWith("BLE")) {
                //String stringValueFromSP = MyUtil.getStringValueFromSP(Constant.currectDeviceLEMac);
                Device deviceFromSP = MyUtil.getDeviceFromSP();
                if (deviceFromSP==null) return;
                if (device.getAddress().equals(deviceFromSP.getMac())){  //只有扫描到的蓝牙是sp里的当前设备时（激活状态），才能进行连接
                    Log.i(TAG,"stringValueFromSP:"+deviceFromSP.getMac());
                    Log.i(TAG,"isConnectted:"+isConnectted);
                    Log.i(TAG,"isConnectting:"+isConnectting);


                    //配对成功
                    connecMac = device.getAddress();
                    if (!isConnectted && !isConnectting){
                        //没有链接上，并且没有正在链接
                        Log.i(TAG,"connecMac:"+connecMac);
                        if (mLeService!=null){

                            isConnectting  = true;

                            boolean connect = mLeService.connect(connecMac, false);//链接
                            Log.i(TAG,"connect:"+connect);

                            if (!connect){
                                isConnectting = false;
                            }
                            Log.i(TAG,"开始连接");
                        }
                    }
                }
            }
        }
    };

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"onServiceDisconnected");
            mLeService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG,"onServiceConnected");
            mLeService = ((BleService.LocalBinder) service).getService(mBleCallBack);
            // mLeService.setMaxConnectedNumber(max);// 设置最大可连接从机数量，默认为4
            mLeService.setDecode(true);
            // 必须调用初始化函数
            mLeService.initialize();
        }
    };

    // ble数据交互的关键参数
    private final BleCallBack mBleCallBack = new BleCallBack() {

        @Override
        public void onConnected(String mac) {
            //if ()
            Log.i(TAG, "onConnected() - " + mac);
            mLeService.startReadRssi(mac, 1000);
            if (mBluetoothAdapter!=null){
                mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
            }
            if (!MyApplication.isHaveDeviceConnectted){
                MyApplication.isHaveDeviceConnectted = isConnectted = true;
                MyUtil.showPopWindow(1);
            }
            isConnectting = false;
            deviceOffLineFileUtil.stopTime();
            MyApplication.connectedMacAddress = connecMac;
        }

        @Override
        public void onConnectTimeout(String mac) {
            Log.w(TAG, "onConnectTimeout() - " + mac);
            isConnectted = false;
            if (MyApplication.isHaveDeviceConnectted){
                MyApplication.isHaveDeviceConnectted = false;
                MyUtil.showPopWindow(0);
            }
            isConnectting = false;
            mIsDataStart = false;
            deviceOffLineFileUtil.stopTime();
            MyApplication.connectedMacAddress = "";
        }

        @Override
        public void onConnectionError(String mac, int status, int newState) {
            Log.w(TAG, "onConnectionError() - " + mac + ", status = " + status + ", newState = " + newState);
            isConnectted = false;
            if (MyApplication.isHaveDeviceConnectted){
                MyApplication.isHaveDeviceConnectted = false;
                MyUtil.showPopWindow(0);
            }
            isConnectting = false;
            mIsDataStart = false;
            deviceOffLineFileUtil.stopTime();
            MyApplication.connectedMacAddress = "";
        }

        @Override
        public void onDisconnected(String mac) {
            Log.w(TAG, "onDisconnected() - " + mac);
            isConnectted = false;
            if (mBluetoothAdapter!=null){
                mBluetoothAdapter.startLeScan(mLeScanCallback);//停止扫描
            }
            if (MyApplication.isHaveDeviceConnectted){
                MyApplication.isHaveDeviceConnectted = false;
                MyUtil.showPopWindow(0);
            }
            isConnectting = false;
            mIsDataStart = false;
            deviceOffLineFileUtil.stopTime();
            MyApplication.connectedMacAddress = "";
        }

        @Override
        public void onServicesDiscovered(String mac) {
            // !!!到这一步才可以与从机进行数据交互
            Log.i(TAG, "onServicesDiscovered() - " + mac);
            //数据交互指令放在线程中
            deviceOffLineFileUtil.startTime();
            sendStartDataTransmitOrderToBlueTooth();
        }

        @Override
        public void onServicesUndiscovered(String mac, int status) {
            Log.e(TAG, "onServicesUndiscovered() - " + mac + ", status = " + status);
            if (MyApplication.isHaveDeviceConnectted){
                MyApplication.isHaveDeviceConnectted = isConnectted = false;
                MyUtil.showPopWindow(0);
            }
            isConnectting = false;
            mIsDataStart = false;
            deviceOffLineFileUtil.stopTime();
        }

        @Override
        public void onReadRemoteRssi(String mac, int rssi, int status) {
            //Log.i(TAG,"onReadRemoteRssi==="+"mac:"+mac+",rssi:"+rssi+",status:"+status);
        }

        @Override
        public void onCharacteristicChanged(String mac, android.bluetooth.BluetoothGattCharacteristic c) {
            // 接收到从机数据
            String uuid = c.getUuid().toString();
            String hexData = DataUtil.byteArrayToHex(c.getValue());
            //Log.i(TAG,"c.getValue().length:"+c.getValue().length);

            Log.i(TAG, "onCharacteristicChanged() - " + mac + ", " + uuid + ", " + hexData);
            //4.2写配置信息   onCharacteristicChanged() - 44:A6:E5:1F:C5:BF, 00001002-0000-1000-8000-00805f9b34fb, FF 81 05 00 16
            //4.5App读主机设备的版本号  onCharacteristicChanged() - 44:A6:E5:1F:C5:BF, 00001002-0000-1000-8000-00805f9b34fb, FF 84 07 88 88 00 16
            /*数据：
                FF 83 0F 00 00 00 00 00 00 00 00 00 00 00 16
                FF 83 0F 00 00 00 00 00 00 00 00 00 00 01 16
                FF 83 0F 00 00 00 00 00 00 00 00 00 00 02 16
                只有倒数2位变化
            */

            //FF 85 06 00 00 16

            if (hexData.startsWith("FF 85")){
                Log.i(TAG,"SDhexData："+hexData);
                if (hexData.split(" ")[3].equals("01")){
                    //有离线数据
                    if (MyApplication.mCurrApplicationActivity !=null){
                        MyApplication.mCurrApplicationActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean isMainActivityInstance = MyApplication.mCurrApplicationActivity.getClass().isInstance(new MainActivity()); //只有在MainActivity弹出提示
                                if (isMainActivityInstance){
                                    showUploadOffLineData(MyApplication.mCurrApplicationActivity);
                                }

                            }
                        });

                    }
                }
            }
            else if (hexData.startsWith("FF 84")){
                Log.i(TAG,"设备版本号："+hexData);
                //FF 84 0B 11 05 02 11 06 02 0C 90 00 16 FF 83

                dealwithDeviceInfo(hexData);

            }
            else {
                if (hexData.length() > 40) {
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

        }
    };



    private boolean isShowAlertDialog;

    private void showUploadOffLineData(Activity activity){
        if (!isShowAlertDialog){
            ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(activity);
            chooseAlertDialogUtil.setAlertDialogText("发现有离线文件，是否现在进行上传","是","否");
            chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
                @Override
                public void onConfirmClick() {
                    Intent intent = new Intent(MyApplication.mCurrApplicationActivity, ConnectToWifiModuleGudieActivity1.class);
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
        Log.i(TAG,"isConnectted:"+isConnectted+"          mIsDataStart: "+mIsDataStart);
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (isConnectted && !mIsDataStart){
                    try {
                        /*Thread.sleep(40);
                        Log.i(TAG, "查询设备信息");
                        sendLookEleInfoOrder();

                        Thread.sleep(40);
                        Log.i(TAG, "查询SD卡是否有数据");
                        mLeService.send(connecMac, Constant.checkIsHaveDataOrder,true);*/

                        Thread.sleep(40);
                        Log.i(TAG,"写配置");
                        String writeConfigureOrder = "FF010A"+ HealthyDataActivity.getDataHexString()+"0016";
                        Log.i(TAG,"writeConfigureOrder:"+writeConfigureOrder);
                        //mLeService.send(connecMac, Constant.writeConfigureOrder,true);
                        mLeService.send(connecMac, writeConfigureOrder,true);

                        Thread.sleep(40);
                        Log.i(TAG,"开启数据指令");
                        mLeService.send(connecMac, Constant.openDataTransmitOrder,true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
                if (isConnectted && mIsDataStart){
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


                    /*try {
                        boolean send = mLeService.send(connecMac, hexSynOrder, true);  //有可能会抛出android.os.DeadObjectException
                        Log.i(TAG,"同步指令connecMac:"+connecMac+",hrateIndexHex:"+hrateIndexHex+"  send:"+send);


                        temp ++;
                        if (temp==5){
                            Log.e(TAG,"抛出异常");
                            mLeService.onDestroy();
                            throw new DeadObjectException();
                        }
                    }
                    catch (Exception e){
                        Log.e(TAG,"捕获异常 e: "+e);
                        //bindService(new Intent(CommunicateToBleService.this, BleService.class), mConnection, BIND_AUTO_CREATE);
                        //onRebind(new Intent(CommunicateToBleService.this, BleService.class));
                    }*/

                    boolean send = mLeService.send(connecMac, hexSynOrder, true);  //有可能会抛出android.os.DeadObjectException
                    Log.i(TAG,"同步指令connecMac:"+connecMac+",hrateIndexHex:"+hrateIndexHex+"  send:"+send);



                    //mLeService.stopSelf();

                    //throw new DeadObjectException();


                    /*if (isBindService){
                        unbindService(mConnection);
                    }*/
                    //bindService(new Intent(MainActivity.this, BleService.class), mConnection, BIND_AUTO_CREATE); //抛出异常是重新绑定获取服务


                    //boolean send = mLeService.send(connecMac, hexSynOrder, true);
                    //Log.i(TAG,"同步指令connecMac:"+connecMac+",hrateIndexHex:"+hrateIndexHex+"  send:"+send);

                    /*boolean blueServiceWorked = isBlueServiceWorked();
                    Log.i(TAG,"blueServiceWorked:"+blueServiceWorked);
                    if (blueServiceWorked){
                        boolean send = mLeService.send(connecMac, hexSynOrder, true);
                        Log.i(TAG,"同步指令connecMac:"+connecMac+",hrateIndexHex:"+hrateIndexHex+"  send:"+send);
                        MyApplication.isBlueServiceWorked = true;
                    }
                    else {
                        MyApplication.isBlueServiceWorked = false;
                    }*/
                }
            }
        });
    }

    private void dealwithDeviceInfo(String hexData) {
        String aString = hexData;
        String[] split = aString.split(" ");

        int [] ints = ECGUtil.geIntEcgaArr(aString, " ", 3, 8); //一次的数据，10位

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
        float electricV =  parseInt/1000f;
        Log.i(TAG,"电量10进制："+electricV);
        int calCuelectricVPercent = calCuelectricVPercent(electricV);
        Log.i(TAG,"calCuelectricVPercent："+calCuelectricVPercent);
        MyApplication.calCuelectricVPercent = calCuelectricVPercent;
    }

    public static int calCuelectricVPercent(float electricV) {
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

        if (electricV<3) {
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

    private void setServiceForegrounByNotify() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("倾听体语")
                .setContentText("倾听体语正在运行")
                .setSmallIcon(R.drawable.logo_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo_icon))
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        //新建Intent，用在Activity传递数据，点击时跳到ShowArticleDetailActivity页面
        Intent intent1 = new Intent(this, MainActivity.class);
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
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        //PendingIntent 主要用于任务栏提醒和桌面weigetde 显示，

        //这里用4个参数需要注意下，130表示requestCode（请求马，自定义）
        //第三个参数书Intent对象，intent1是上面定义的 Intent对象
        //第四个对象是PendingIntent的标签属性，表叔显示方式，这里FLAG_UPDATE_CURRENT表示显示当前的通知，如果用新的通知时，更新当期的通知，这个属性注意下，如果不设置的话每次点击都是同一个通知
        PendingIntent activity = PendingIntent.getActivity(this, 130, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        //notification.contentIntent = activity;
        //nm.notify(0, notification);
        startForeground(1, notification); //将Service设置为前台服务
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
                if (!StartRunActivity.mIsRunning){
                    return;
                }
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    Log.i(TAG,"亮屏:");
                    Intent i = new Intent(context,LockScreenActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    mKeyguardLock = mKeyguardManager.newKeyguardLock("");
                    mKeyguardLock.disableKeyguard();

                    context.startActivity(i);
                }
                if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    Log.i(TAG,"锁屏:");
                    Intent i = new Intent(context,LockScreenActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);


                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);

        Intent intent = new Intent("com.amsu.healthy.servicedestroy");
        sendBroadcast(intent);

        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
