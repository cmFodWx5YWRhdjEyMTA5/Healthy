package com.amsu.healthy.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
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
import com.amsu.healthy.activity.ConnectToWifiModuleGudieActivity1;
import com.amsu.healthy.activity.HealthyDataActivity;
import com.amsu.healthy.activity.LockScreenActivity;
import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.activity.MyDeviceActivity;
import com.amsu.healthy.activity.SplashActivity;
import com.amsu.healthy.activity.StartRunActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.utils.AppAbortDbAdapter;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.WakeLockUtil;
import com.amsu.healthy.utils.wifiTramit.DeviceOffLineFileUtil;
import com.ble.api.DataUtil;
import com.ble.ble.BleService;

import java.util.Date;
import java.util.List;

public class CommunicateToBleService extends Service {
    private static final String TAG = "CommunicateToBleService";

    public static String connecMac;   //当前连接的蓝牙mac地址
    public static boolean isConnectted  =false;
    private boolean isConnectting  =false;
    private DeviceOffLineFileUtil deviceOffLineFileUtil;
    private BluetoothAdapter mBluetoothAdapter;
    public static LeProxy mLeProxy;
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 5000;
    private Intent calCuelectricVPercentIntent;
    private static Service mContext;

    public static boolean isNeedStartRunningActivity;

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

            }

            stratListenScrrenBroadCast();
            //MyUtil.scheduleService(this,1,CommunicateToBleService.class.getName());

            //setServiceForegrounByNotify();
            init();


            List<AppAbortDataSave> abortDataListFromSP = AppAbortDbAdapter.getAbortDataListFromSP();
            Log.i(TAG,"abortDataListFromSP:"+abortDataListFromSP.size());
            if (abortDataListFromSP.size() == 1){
                isNeedStartRunningActivity = true;
                Log.i(TAG,"SplashActivity.isSplashActivityStarted:"+SplashActivity.isSplashActivityStarted);
                if (!SplashActivity.isSplashActivityStarted){
                    Intent intent1 = new Intent(this,StartRunActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtra(Constant.isNeedRecoverAbortData,true);
                    startActivity(intent1);
                }
                Log.i(TAG,"isNeedStartRunningActivity:"+isNeedStartRunningActivity);
            }
            else if (abortDataListFromSP.size() > 1){
                MyUtil.putStringValueFromSP("abortDatas","");
            }
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

        new Thread(){
            @Override
            public void run() {
                super.run();


                while (true){
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (mBluetoothAdapter!=null && mBluetoothAdapter.getState()==BluetoothAdapter.STATE_ON) {
                        if (!isBluetoothEnable){
                            scanLeDevice(true);
                            isBluetoothEnable = true;
                        }
                    }
                    else {
                        isBluetoothEnable = false;
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
            if ( !MyUtil.isEmpty(connecMac)){
                mLeProxy.send(connecMac, DataUtil.hexToByteArray(Constant.readDeviceIDOrder),true);
                Log.i(TAG,"MainActivity.mLeService.send");
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
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                Log.i(TAG,"startLeScan");
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
            //null,63:5C:3E:B6:A0:AE,null,10,0
            if (device==null)return;
            Log.i(TAG,"onLeScan  device:"+device.getName()+","+device.getAddress()+","+device.getUuids()+","+device.getBondState()+","+device.getType());
            String leName = device.getName();
            if (leName!=null && (leName.startsWith("BLE") || leName.startsWith("AMSU"))) {
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


                        isConnectting  = true;

                        scanLeDevice(false);

                        //boolean connect = mLeService.connect(connecMac, false);//链接
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(50);

                                    boolean connect = mLeProxy.connect(connecMac, false);
                                    Log.i(TAG,"connect:"+connect);

                                    if (!connect){
                                        isConnectting = false;
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

    private void dealwithLebDataChange(String hexData) {
        Log.i(TAG, "onCharacteristicChanged() - " + hexData);
        if (hexData.startsWith("FF 85")){
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
                                        showUploadOffLineData(MyApplication.mCurrApplicationActivity);
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
                        Thread.sleep(40);
                        Log.i(TAG, "查询设备信息");
                        sendLookEleInfoOrder();

                        Thread.sleep(40);
                        Log.i(TAG, "查询SD卡是否有数据");
                        mLeProxy.send(connecMac, DataUtil.hexToByteArray(Constant.checkIsHaveDataOrder),true);
                        Thread.sleep(200);
                        Log.i(TAG,"写配置");
                        String writeConfigureOrder = "FF010A"+ HealthyDataActivity.getDataHexString()+"0016";
                        Log.i(TAG,"writeConfigureOrder:"+writeConfigureOrder);
                        //mLeService.send(connecMac, Constant.writeConfigureOrder,true);
                        //mLeService.send(connecMac, writeConfigureOrder,true);

                        mLeProxy.send(connecMac, DataUtil.hexToByteArray(writeConfigureOrder),true);

                        Thread.sleep(100);
                        Log.i(TAG,"开启数据指令");
                        mLeProxy.send(connecMac, DataUtil.hexToByteArray(Constant.openDataTransmitOrder),true);
                        Thread.sleep(100);
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

                    boolean send = mLeProxy.send(connecMac, DataUtil.hexToByteArray(hexSynOrder), true);  //有可能会抛出android.os.DeadObjectException
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
        /*float electricV =  parseInt/1000f;
        Log.i(TAG,"电量10进制："+electricV);*/
        int calCuelectricVPercent = calCuelectricVPercent(parseInt);
        Log.i(TAG,"calCuelectricVPercent："+calCuelectricVPercent);
        MyApplication.calCuelectricVPercent = calCuelectricVPercent;


        calCuelectricVPercentIntent.putExtra("calCuelectricVPercent",calCuelectricVPercent);
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

            int leave = 0;

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
                if (!StartRunActivity.mIsRunning){
                    return;
                }

                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_SCREEN_OFF.equals(action)){
                    Log.i(TAG,"屏幕变化:");

                    mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    mKeyguardLock = mKeyguardManager.newKeyguardLock("");
                    mKeyguardLock.disableKeyguard();

                    Intent i = new Intent(context,LockScreenActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(i);
                }

                /*if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    mKeyguardLock.disableKeyguard();

                    Intent i = new Intent(context,LockScreenActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    mKeyguardLock = mKeyguardManager.newKeyguardLock("");


                    context.startActivity(i);
                }
                if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    Log.i(TAG,"锁屏:");

                    mKeyguardLock.disableKeyguard();

                    Intent i = new Intent(context,LockScreenActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    mKeyguardLock = mKeyguardManager.newKeyguardLock("");
                    mKeyguardLock.disableKeyguard();

                    context.startActivity(i);
                }*/
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
                    if (mBluetoothAdapter!=null){
                        scanLeDevice(false);//停止扫描
                    }
                    if (!MyApplication.isHaveDeviceConnectted){
                        MyApplication.isHaveDeviceConnectted = isConnectted = true;

                        MyUtil.showPopWindow(1);

                    }
                    isConnectting = false;
                    deviceOffLineFileUtil.stopTime();
                    MyApplication.connectedMacAddress = connecMac;

                    break;
                case LeProxy.ACTION_GATT_DISCONNECTED:
                    Log.w(TAG,"已断开 "+address);
                    Log.i(TAG,"MyApplication.isHaveDeviceConnectted:"+MyApplication.isHaveDeviceConnectted);
                    Log.i(TAG,"MyApplication.mCurrApplicationActivity:"+MyApplication.mCurrApplicationActivity);
                    isConnectted = false;
                    if (mBluetoothAdapter!=null){
                        scanLeDevice(true);//停止扫描
                    }
                    if (MyApplication.isHaveDeviceConnectted){
                        MyApplication.isHaveDeviceConnectted = false;
                        MyUtil.showPopWindow(0);
                    }
                    isConnectting = false;
                    mIsDataStart = false;
                    deviceOffLineFileUtil.stopTime();
                    MyApplication.connectedMacAddress = "";

                    calCuelectricVPercentIntent.putExtra("calCuelectricVPercent",-1);
                    sendBroadcast(calCuelectricVPercentIntent);
                    MyApplication.calCuelectricVPercent = -1;
                    break;
                case LeProxy.ACTION_CONNECT_ERROR:
                    Log.w(TAG,"连接异常 "+address);
                    isConnectted = false;
                    if (mBluetoothAdapter!=null){
                        scanLeDevice(true);//停止扫描
                    }
                    if (MyApplication.isHaveDeviceConnectted){
                        MyApplication.isHaveDeviceConnectted = false;
                        MyUtil.showPopWindow(0);
                    }

                    isConnectting = false;
                    mIsDataStart = false;
                    deviceOffLineFileUtil.stopTime();
                    MyApplication.connectedMacAddress = "";

                    calCuelectricVPercentIntent.putExtra("calCuelectricVPercent",-1);
                    sendBroadcast(calCuelectricVPercentIntent);
                    MyApplication.calCuelectricVPercent = -1;
                    break;
                case LeProxy.ACTION_CONNECT_TIMEOUT:
                    if (mBluetoothAdapter!=null){
                        scanLeDevice(true);//停止扫描
                    }
                    Log.w(TAG,"连接超时 "+address);
                    isConnectted = false;
                    if (MyApplication.isHaveDeviceConnectted){
                        MyApplication.isHaveDeviceConnectted = false;
                        MyUtil.showPopWindow(0);
                    }
                    isConnectting = false;
                    mIsDataStart = false;
                    deviceOffLineFileUtil.stopTime();
                    MyApplication.connectedMacAddress = "";

                    calCuelectricVPercentIntent.putExtra("calCuelectricVPercent",-1);
                    sendBroadcast(calCuelectricVPercentIntent);
                    MyApplication.calCuelectricVPercent = -1;
                    break;
                case LeProxy.ACTION_GATT_SERVICES_DISCOVERED:
                    Log.i(TAG,"Services discovered: " + address);

                    //数据交互指令放在线程中
                    deviceOffLineFileUtil.startTime();

                    sendStartDataTransmitOrderToBlueTooth();

                    /*new Thread(){
                        @Override
                        public void run() {
                            super.run();

                            try {
                                Thread.sleep(2000);
                                Log.i(TAG,"写配置");
                                String writeConfigureOrder = "FF010A"+ HealthyDataActivity.getDataHexString()+"0016";
                                Log.i(TAG,"writeConfigureOrder:"+writeConfigureOrder);
                                //mLeService.send(connecMac, Constant.writeConfigureOrder,true);
                                //mLeService.send(connecMac, writeConfigureOrder,true);

                                mLeProxy.send(connecMac, DataUtil.hexToByteArray(writeConfigureOrder),true);

                                Thread.sleep(3000);
                                Log.i(TAG,"开启数据指令");
                                mLeProxy.send(connecMac, DataUtil.hexToByteArray(Constant.openDataTransmitOrder),true);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();*/
                    break;

                case LeProxy.ACTION_RSSI_AVAILABLE:{// 更新rssi

                }
                break;

                case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                    byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);
                    dealwithLebDataChange(DataUtil.byteArrayToHex(data));
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
