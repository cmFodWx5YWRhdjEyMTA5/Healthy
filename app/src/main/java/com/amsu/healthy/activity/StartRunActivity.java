package com.amsu.healthy.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.TraceLocation;
import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.EcgFilterUtil;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.RunTimerTaskUtil;
import com.amsu.healthy.utils.map.PathRecord;
import com.amsu.healthy.utils.map.Util;
import com.amsu.healthy.view.GlideRelativeView;
import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.amsu.healthy.R.id.bt_run_start;

/**
 *
 */
public class StartRunActivity extends BaseActivity implements AMapLocationListener {
    private static final String TAG = "StartRunActivity";
    private TextView tv_run_speed;
    private TextView tv_run_distance;
    private TextView tv_run_time;
    private TextView tv_run_isoxygen;
    private TextView tv_run_rate;
    private TextView tv_run_stridefre;
    private TextView tv_run_kcal;
    private final int WHAT_TIME_UPDATE = 0;
    private Button bt_run_start;
    private RelativeLayout bt_run_location;
    private Button bt_run_lock;
    private boolean mIsRunning = false;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    public static AMapLocationClient mlocationClient;
    private PathRecord record;    //存放未纠偏轨迹记录信息
    //private List<TraceLocation> mTracelocationlist = new ArrayList<>();   //偏轨后轨迹
    public static long mStartTime;
    private RunTimerTaskUtil runTimerTaskUtil;

    public static long createrecord =-1;
    private int calculateSpeedCount = 10;   //10次，一次2s,即为20s
    private RelativeLayout rl_run_bootom;
    private GlideRelativeView rl_run_glide;
    private RelativeLayout rl_run_lock;
    private double mAllDistance;
    private long mCurrentTimeMillis = 0;
    private TextView tv_run_test;

    public static BleService mLeService;
    private static String connecMac;   //当前连接的蓝牙mac地址
    private boolean isConnectted  =false;
    private boolean isConnectting  =false;
    private ArrayList<Integer> heartRateDates = new ArrayList<>();  // 心率数组
    private boolean isThreeMit = false;   //是否到三分钟
    private boolean isStartThreeMitTimer;  //是否开始三分钟倒计时计时器
    private int currentGroupIndex = 0;   //组的索引
    private int groupCalcuLength = 100; //
    private int oneGroupLength = 10; //
    private int[] calcuEcgRate = new int[groupCalcuLength*oneGroupLength]; //1000条数据:（100组，一组有10个数据点）
    private DataOutputStream dataOutputStream;  //二进制文件输出流，写入文件
    private ByteBuffer byteBuffer;
    private Intent mSendHeartRateBroadcastIntent;
    public static final String action = "jason.broadcast.action";    //发送广播，将心率值以广播的方式放松出去，在其他Activity可以接受
    private FileOutputStream fileOutputStream;
    private long ecgFiletimeMillis =-1;  //开始有心电数据时的秒数，作为心电文件命名。静态变量，在其他界面会用到
    private boolean mHaveOutSideGpsLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_run);

        initView();

    }

    private void initView() {
        initHeadView();
        setCenterText("运动检测");
        //setLeftImage(R.drawable.back_icon);
        setRightText("心电图");
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartRunActivity.this, HealthyDataActivity.class);
                intent.putExtra(Constant.isLookupECGDataFromSport,true);
                startActivity(intent);
            }
        });

        tv_run_speed = (TextView) findViewById(R.id.tv_run_speed);
        tv_run_distance = (TextView) findViewById(R.id.tv_run_distance);
        tv_run_time = (TextView) findViewById(R.id.tv_run_time);
        tv_run_isoxygen = (TextView) findViewById(R.id.tv_run_isoxygen);
        tv_run_rate = (TextView) findViewById(R.id.tv_run_rate);
        tv_run_stridefre = (TextView) findViewById(R.id.tv_run_stridefre);
        tv_run_kcal = (TextView) findViewById(R.id.tv_run_kcal);

        bt_run_start = (Button) findViewById(R.id.bt_run_start);
        bt_run_location = (RelativeLayout) findViewById(R.id.bt_run_location);
        bt_run_lock = (Button) findViewById(R.id.bt_run_lock);

        rl_run_bootom = (RelativeLayout) findViewById(R.id.rl_run_bootom);
        rl_run_lock = (RelativeLayout) findViewById(R.id.rl_run_lock);
        rl_run_glide = (GlideRelativeView) findViewById(R.id.rl_run_glide);


        tv_run_test = (TextView) findViewById(R.id.tv_run_test);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        bt_run_start.setOnClickListener(myOnClickListener);
        bt_run_location.setOnClickListener(myOnClickListener);
        bt_run_lock.setOnClickListener(myOnClickListener);


        bt_run_start.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mIsRunning){
                    endRunning();
                }
                return false;
            }
        });

        rl_run_glide.setOnONLockListener(new GlideRelativeView.OnONLockListener() {
            @Override
            public void onLock() {
                rl_run_lock.setVisibility(View.GONE);
                rl_run_bootom.setVisibility(View.VISIBLE);
            }
        });

        //绑定蓝牙，获取蓝牙服务
        bindService(new Intent(this, BleService.class), mConnection, BIND_AUTO_CREATE);

    }

    Handler runEcgHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String hexData = (String) msg.obj;
            Log.i(TAG,"hexData:"+hexData);
            if (hexData.startsWith("FF 83 0F")) {
                //心电数据
                //Log.i(TAG,"心电hexData:"+hexData);
                dealWithEcgData(hexData);
            } else if (hexData.startsWith("FF 86 11")) {
                //加速度数据
                //Log.i(TAG,"加速度hexData:"+hexData);
                dealWithAccelerationgData(hexData);
            }

            return false;
        }
    });

    public Handler getRunEcgHandlerInstance(){
        return runEcgHandler;
    }



    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.i(TAG,"onLocationChanged:"+aMapLocation.toString());
        Log.i(TAG,"getSpeed:"+aMapLocation.getSpeed());   // meters/second
        /*
        * LOCATION_TYPE_GPS = 1;  GPS定位
        * LOCATION_TYPE_SAME_REQ = 2 缓存定位
        * LOCATION_TYPE_FIX_CACHE = 4  缓存
        * LOCATION_TYPE_WIFI = 5; WiFi定位
        * LOCATION_TYPE_CELL = 6  基站定位
        *
        * */

        Log.i(TAG,"aMapLocation.getLocationType():"+aMapLocation.getLocationType());   //
        Log.i(TAG,"aMapLocation.getErrorCode():"+aMapLocation.getErrorCode());   // meters/second

        String tyep =aMapLocation.getLocationType()+": ";
        if (aMapLocation.getLocationType()==1){
            tyep += "TYPE_GPS";
        }
        else if (aMapLocation.getLocationType()==2){
            tyep += "SAME_REQ";
        }
        else if (aMapLocation.getLocationType()==4){
            tyep += "FIX_CACHE";
        }
        else if (aMapLocation.getLocationType()==5){
            tyep += "WIFI";
        }
        else if (aMapLocation.getLocationType()==6){
            tyep += "CELL";
        }
        tv_run_test.setText(tyep);



        calculateSpeed(aMapLocation);
        calculateDistance(aMapLocation);


        /*if (isFirst){
            mMiddleTime = mStartTime;
            mDistance = 0;
            isFirst = false;
        }
        calculateSpeedCount--;

        if (calculateSpeedCount==0){
            long currentTime = System.currentTimeMillis();
            int tempDistance = (int) Util.getDistance(record.getPathline()) - mDistance;
            String average = Util.getAverage(tempDistance, mMiddleTime, currentTime);
            if (average!=null){
                average = average.equals("")?"0":average;
            }
            mMiddleTime = currentTime;
            mDistance = (int) Util.getDistance(record.getPathline());
            calculateSpeedCount = 10;
            tv_run_speed.setText(average);
            Log.i(TAG,"average2:"+average);
        }*/

    }

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

    //扫描蓝牙回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            //BLE#0x44A6E51FC5BF,44:A6:E5:1F:C5:BF,null,10,2
            //null,72:A8:23:AF:25:42,null,10,0
            //null,63:5C:3E:B6:A0:AE,null,10,0
            Log.i(TAG,"onLeScan  device:"+device.getName()+","+device.getAddress()+","+device.getUuids()+","+device.getBondState()+","+device.getType());
            String leName = device.getName();
            if (leName!=null && leName.startsWith("BLE")) {
                String stringValueFromSP = MyUtil.getStringValueFromSP(Constant.currectDeviceLEName);
                if (leName.equals(stringValueFromSP)){  //只有扫描到的蓝牙是sp里的当前设备时（激活状态），才能进行连接
                    //配对成功
                    connecMac = device.getAddress();
                    if (!isConnectted && !isConnectting){
                        //没有链接上，并且没有正在链接
                        mLeService.connect(device.getAddress(),true);  //链接
                        isConnectting  = true;
                        Log.i(TAG,"开始连接");
                    }
                }
            }
        }
    };

    // ble数据交互的关键参数
    private final BleCallBack mBleCallBack = new BleCallBack() {

        @Override
        public void onConnected(String mac) {
            Log.i(TAG, "onConnected() - " + mac);
            mLeService.startReadRssi(mac, 1000);
            if (MainActivity.mBluetoothAdapter != null) {
                MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
            }
            isConnectted = true;
            isConnectting = false;
        }

        @Override
        public void onConnectTimeout(String mac) {
            Log.w(TAG, "onConnectTimeout() - " + mac);
            isConnectting = false;
        }

        @Override
        public void onConnectionError(String mac, int status, int newState) {
            Log.w(TAG, "onConnectionError() - " + mac + ", status = " + status + ", newState = " + newState);
            isConnectting = false;
        }

        @Override
        public void onDisconnected(String mac) {
            Log.w(TAG, "onDisconnected() - " + mac);
            isConnectting = false;
        }

        @Override
        public void onServicesDiscovered(String mac) {
            // !!!到这一步才可以与从机进行数据交互
            Log.i(TAG, "onServicesDiscovered() - " + mac);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(1000);
                        Log.i(TAG, "写配置");
                        mLeService.send(connecMac, Constant.writeConfigureOrder, true);

                        Thread.sleep(1000);
                        Log.i(TAG, "开启数据指令");
                        mLeService.send(connecMac, Constant.openDataTransmitOrder, true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        @Override
        public void onServicesUndiscovered(String mac, int status) {
            Log.e(TAG, "onServicesUndiscovered() - " + mac + ", status = " + status);
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
            //Log.i(TAG, "onCharacteristicChanged() - " + mac + ", " + uuid + ", " + hexData);
            //4.2写配置信息   onCharacteristicChanged() - 44:A6:E5:1F:C5:BF, 00001002-0000-1000-8000-00805f9b34fb, FF 81 05 00 16
            //4.5App读主机设备的版本号  onCharacteristicChanged() - 44:A6:E5:1F:C5:BF, 00001002-0000-1000-8000-00805f9b34fb, FF 84 07 88 88 00 16
            /*数据：
                FF 83 0F 00 00 00 00 00 00 00 00 00 00 00 16
                FF 83 0F 00 00 00 00 00 00 00 00 00 00 01 16
                FF 83 0F 00 00 00 00 00 00 00 00 00 00 02 16
                只有倒数2位变化
            */

            if (hexData.length() < 40) {
                return;
            }

            if (!isStartThreeMitTimer) {
                isStartThreeMitTimer = true;
                startThreeMitTiming();
            }

            if (hexData.startsWith("FF 83 0F")) {
                //心电数据
                //Log.i(TAG,"心电hexData:"+hexData);
                dealWithEcgData(hexData);
            } else if (hexData.startsWith("FF 86 11")) {
                //加速度数据
                //Log.i(TAG,"加速度hexData:"+hexData);
                dealWithAccelerationgData(hexData);
            }
        }

    };

    //处理心电数据
    private void dealWithEcgData(String hexData) {
        final int [] ints = ECGUtil.geIntEcgaArr(hexData, " ", 3, 10); //一次的数据，10位
        writeEcgDataToBinaryFile(ints);

        //滤波处理
        for (int i=0;i<ints.length;i++){
            int temp = EcgFilterUtil.miniEcgFilterLp(ints[i], 0);
            temp = EcgFilterUtil.miniEcgFilterHp(temp, 0);
            ints[i] = temp;
        }

        //Log.i(TAG,"currentGroupIndex:"+currentGroupIndex);

        if (currentGroupIndex<groupCalcuLength){
            //未到时间（1000个数据点计算一次心率）
            System.arraycopy(ints, 0, calcuEcgRate, currentGroupIndex * oneGroupLength, ints.length);
        }
        else{
            currentGroupIndex = 0;
            //带入公式，计算心率
            final int heartRate = ECGUtil.countEcgRate(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
            Log.i(TAG,"heartRate0:"+heartRate);
            //calcuEcgRate = new int[groupCalcuLength*10];
            heartRateDates.add(heartRate);

            if (mSendHeartRateBroadcastIntent==null){
                mSendHeartRateBroadcastIntent = new Intent(action);
            }
            mSendHeartRateBroadcastIntent.putExtra("data", heartRate);
            sendBroadcast(mSendHeartRateBroadcastIntent);

            final String OxygenState = calcuOxygenState(heartRate);


            //更新心率
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_run_rate.setText(heartRate+"");
                    tv_run_isoxygen.setText(OxygenState);
                }
            });
            System.arraycopy(ints, 0, calcuEcgRate, currentGroupIndex * 10 + 0, ints.length);
        }
        currentGroupIndex++;
    }

    private String calcuOxygenState(int heartRate) {
        int userAge = HealthyIndexUtil.getUserAge();
        if (heartRate<220-userAge){//有氧
            return "有氧";
        }
        else {
            return "无氧";
        }
    }

    //写到文件里，二进制方式写入
    private void writeEcgDataToBinaryFile(int[] ints) {
        try {
            if (fileOutputStream==null){
                ecgFiletimeMillis = System.currentTimeMillis();
                //String filePath = MyUtil.generateECGFilePath(HealthyDataActivity.this, ecgFiletimeMillis); //随机生成一个ecg格式文件
                //String filePath = getCacheDir()+"/"+MyUtil.getECGFileNameDependFormatTime(new Date())+".ecg";  //随机生成一个文件
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ MyUtil.getECGFileNameDependFormatTime(new Date())+".ecg";
                fileOutputStream = new FileOutputStream(filePath,true);
                MyUtil.putStringValueFromSP("cacheFileName",filePath);
                dataOutputStream = new DataOutputStream(fileOutputStream);
                byteBuffer = ByteBuffer.allocate(2);
            }
            for (int anInt : ints) {
                byteBuffer.clear();
                byteBuffer.putShort((short) anInt);
                dataOutputStream.writeByte(byteBuffer.get(1));
                dataOutputStream.writeByte(byteBuffer.get(0));
                dataOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //处理加速度数据
    private void dealWithAccelerationgData(String hexData) {
        final int [] ints = ECGUtil.geIntEcgaArr(hexData, " ", 3, 12); //一次的数据，12位
        //FF 42 04 77 0F 93 FF 26 04 74 0F 47
        int xACC = 0;
        int yACC = 0;
        int zACC = 0;
        for (int i=0;i<ints.length;i++){
            if (i==0 || i==6){
                //X
                if(ints[i]>127)
                {
                    xACC=ints[i]*256+ints[i+1]-65536;
                }
                else
                {
                    xACC=ints[i]*256+ints[i+1];
                }
                //Log.i(TAG,"xACC:"+xACC);

            }
            else if (i==2 || i==8){
                //Y

                if(ints[i]>127)
                {
                    yACC=ints[i]*256+ints[i+1]-65536;
                }
                else
                {
                    yACC=ints[i]*256+ints[i+1];
                }
                //Log.i(TAG,"xACC:"+yACC);
            }
            else if (i==4 || i==10){
                //Z

                if(ints[i]>127)
                {
                    zACC=ints[i]*256+ints[i+1]-65536;
                }
                else
                {
                    zACC=ints[i]*256+ints[i+1];
                }
                //Log.i(TAG,"xACC:"+zACC);
            }
        }
        //Log.i(TAG,"xACC:"+xACC+",yACC:"+yACC+",zACC:"+zACC);
    }

    //开始扫描、连接蓝牙
    private void startConnectBluetooth(){
        if (MainActivity.mBluetoothAdapter!=null){
            MainActivity.mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    //计算跑步速度，在室内和室外统一采用地图返回的传感器速度
    private void calculateSpeed(AMapLocation aMapLocation) {
        float speed = aMapLocation.getSpeed()*3.6f;
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String formatSpeed=decimalFormat.format(speed);//format 返回的是字符串
        tv_run_speed.setText(formatSpeed);
    }

    private void calculateDistance(AMapLocation aMapLocation) {
        //不关室内室外，首次定位几个点策略：定位5个点，这5个点的距离在100m范围之内属于正常情况，否则为定位不准，重新定位前5个点
        if (record.getPathline().size()<=5){
            record.addpoint(aMapLocation);
            float distance = Util.getDistance(record.getPathline());
            if (distance>100){
                record.getPathline().clear();
            }
        }

        if(aMapLocation.getLocationType()==AMapLocation.LOCATION_TYPE_GPS){
            if (!mHaveOutSideGpsLocation){
                mHaveOutSideGpsLocation = true;
            }
            Log.i(TAG,"record.getPathline().size():"+record.getPathline().size());
            //室外,GPS定位数据，在室外。计算距离方式：1、衣服计步器，2、根据地图返回的距离 ，3、地图利用手机传感器，
            double tempDistance = 0;
            AMapLocation lastAMapLocation = record.getPathline().get(record.getPathline().size() - 1);
            tempDistance = getTwoPointDistance(lastAMapLocation, aMapLocation);
            Log.i(TAG,"tempDistance:"+tempDistance);
            if (tempDistance<500){   //2个点之间距离小于100m为正常定位情况，否则为噪声去除
                mAllDistance += tempDistance;
                record.addpoint(aMapLocation);
                Log.i(TAG,"tempDistance:"+tempDistance);
                Log.i(TAG,"mAllDistance:"+mAllDistance);
            }
        }
        else {
            if (mHaveOutSideGpsLocation){
                //有室外定位，则属于在室外跑步然后遇到GPS信号弱的情况，需要将其他方式定位的经纬度存入列表，防止定位中断
                double tempDistance = 0;
                AMapLocation lastAMapLocation = record.getPathline().get(record.getPathline().size() - 1);
                tempDistance = getTwoPointDistance(lastAMapLocation, aMapLocation);
                Log.i(TAG,"tempDistance:"+tempDistance);
                if (tempDistance<500){   //2个点之间距离小于100m为正常定位情况，否则为噪声去除
                    record.addpoint(aMapLocation);
                }
            }
            //室内,非GPS定位，说明信号差，属于室内定位。计算距离方式：1、衣服计步器，3、地图利用手机传感器计算移动速度
             if (mCurrentTimeMillis!=0.0){ //不是第一次
                 double tempDistance = aMapLocation.getSpeed() * ((System.currentTimeMillis() - mCurrentTimeMillis) / 1000f); //s=vt,单位:m
                 mAllDistance += tempDistance;
             }
         }

        mCurrentTimeMillis = System.currentTimeMillis();
        Log.i(TAG,"mAllDistance:"+mAllDistance);
        tv_run_distance.setText(getFormatDistance(mAllDistance));
    }


    /**根据高德地图定位的2个点来计算距离
     * @param aMapLocation
     */
    private double getTwoPointDistance(AMapLocation lastAMapLocation,AMapLocation aMapLocation) {
        LatLng thisLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        LatLng lastLatLng = new LatLng(lastAMapLocation.getLatitude(), lastAMapLocation.getLongitude());
        return (double) AMapUtils.calculateLineDistance(thisLatLng, lastLatLng);
    }

    public static String getFormatDistance(double distance) {
        DecimalFormat decimalFormat=new DecimalFormat("0.000");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String formatSpeed=decimalFormat.format(distance/1000);//format 返回的是字符串
        return formatSpeed;
    }


    //开始运动
    private void startRunning() {
        if (!mIsRunning){
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // 判断GPS模块是否开启，如果没有则开启
            if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                chooseOpenGps();
            }
            else {
                if (MainActivity.isConnectted){
                    //衣服链接成功，开始数据传输

                }

                bt_run_start.setText("长按结束");

                if (!mIsRunning){
                    startConnectBluetooth();
                }

                bt_run_lock.setVisibility(View.VISIBLE);
                mIsRunning  =true;
                isThreeMit = true;  //测试



                //开启三分钟计时，保存记录最短为3分钟
                MyTimeTask.startCountDownTimerTask(1000 * 60 * 3, new MyTimeTask.OnTimeOutListener() {
                    @Override
                    public void onTomeOut() {
                        isThreeMit = true;
                    }
                });

                //开始计时，更新时间
                MyTimeTask.startTimeRiseTimerTask(this, 1000, new MyTimeTask.OnTimeChangeAtScendListener() {
                    @Override
                    public void onTimeChange(Date date) {
                        String specialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", date);
                        tv_run_time.setText(specialFormatTime);
                    }
                });

                initMapLoationTrace();
            }
        }
    }

    //开始三分钟计时
    public void startThreeMitTiming(){
        MyTimeTask.startCountDownTimerTask(1000 * 60 * 3, new MyTimeTask.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                Log.i(TAG,"TimerTask:到点了");
                isThreeMit = true;
            }
        });
    }

    //结束运动
    private void endRunning() {
        ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(this);
        if (isThreeMit && mAllDistance>0){
            chooseAlertDialogUtil.setAlertDialogText("采集恢复心率需要继续采集一分钟的心电数据，是否进行采集？","是","否");
            chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
                @Override
                public void onConfirmClick() {
                    saveSportRecord();
                    Intent intent = new Intent(StartRunActivity.this, CalculateHRRProcessActivity.class);
                    intent.putExtra(Constant.sportState,Constant.SPORTSTATE_ATHLETIC);
                    if (createrecord!=-1){
                        intent.putExtra(Constant.sportCreateRecordID,createrecord);
                    }
                    if (heartRateDates.size()>0){
                        intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
                        intent.putExtra(Constant.ecgFiletimeMillis,ecgFiletimeMillis);
                    }
                    startActivity(intent);
                    MyApplication.mActivities.add(StartRunActivity.this);
                    //finish();
                }
            });
            chooseAlertDialogUtil.setOnCancelClickListener(new ChooseAlertDialogUtil.OnCancelClickListener() {
                @Override
                public void onCancelClick() {
                    saveSportRecord();
                    Intent intent = new Intent(StartRunActivity.this, HeartRateActivity.class);
                    intent.putExtra(Constant.sportState,Constant.SPORTSTATE_ATHLETIC);
                    if (createrecord!=-1){
                        intent.putExtra(Constant.sportCreateRecordID,createrecord);
                    }
                    if (heartRateDates.size()>0){
                        intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
                        intent.putExtra(Constant.ecgFiletimeMillis,ecgFiletimeMillis);
                    }
                    startActivity(intent);
                    finish();
                }
            });
        }
        else {
            chooseAlertDialogUtil.setAlertDialogText("跑步时间或距离太短，无法保存记录，是否继续跑步？","继续跑步","结束跑步");
            chooseAlertDialogUtil.setOnCancelClickListener(new ChooseAlertDialogUtil.OnCancelClickListener() {
                @Override
                public void onCancelClick() {
                    mlocationClient.stopLocation();
                    finish();
                }
            });
        }
    }

    //记录运动休信息（和地图有关的）
    private void saveSportRecord() {
        createrecord = Util.saveRecord(record.getPathline(), record.getDate(), this,mStartTime,mAllDistance);
        Log.i(TAG,"createrecord:"+createrecord);
        mlocationClient.stopLocation();
        mIsRunning = false;
    }

    //初始化定位
    private void initMapLoationTrace() {
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mLocationOption.setGpsFirst(true);
        mLocationOption.setSensorEnable(true);

        mlocationClient = new AMapLocationClient(this);
        mlocationClient.setLocationListener(this);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();

        if (record==null){
            record = new PathRecord();
            mStartTime = System.currentTimeMillis();
            record.setDate(MyUtil.getCueMapDate(mStartTime));
        }
    }

    //打开gps
    private void chooseOpenGps() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(StartRunActivity.this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.choose_opengps_dailog, null);

        bottomSheetDialog.setContentView(inflate);
        Window window = bottomSheetDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.opengpsdialogstyle);  //添加动画
        bottomSheetDialog.show();

        TextView bt_opengps_cancel = (TextView) inflate.findViewById(R.id.bt_opengps_cancel);
        TextView bt_opengps_ok = (TextView) inflate.findViewById(R.id.bt_opengps_ok);

        bt_opengps_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bt_opengps_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //跳到设置页面
                // 转到手机设置界面，用户设置GPS
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
            }
        });
    }

    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_run_start:
                    startRunning();
                    break;

                case R.id.bt_run_lock:
                    rl_run_lock.setVisibility(View.VISIBLE);
                    rl_run_bootom.setVisibility(View.GONE);
                    break;

                case R.id.bt_run_location:
                    startActivity(new Intent(StartRunActivity.this,RunTrailMapActivity.class));
                    break;
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            if (mIsRunning){
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MainActivity.mBluetoothAdapter!=null){
            MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
        }


        unbindService(mConnection);

    }
}
