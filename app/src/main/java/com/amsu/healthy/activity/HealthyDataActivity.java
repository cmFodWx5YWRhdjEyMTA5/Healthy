package com.amsu.healthy.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.EcgFilterUtil;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.EcgView;
import com.amsu.healthy.view.PathView;
import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class HealthyDataActivity extends BaseActivity {

    private static final String TAG = "HealthyDataActivity";
    private static final String TAG1 = "startSoS";

    public static BleService mLeService;
    private EcgView pv_healthydata_path;
    private FileOutputStream fileOutputStream;
    private int groupCalcuLength = 100; //
    private int oneGroupLength = 10; //
    private int[] calcuEcgRate = new int[groupCalcuLength*oneGroupLength]; //1000条数据:（100组，一组有10个数据点）

    /*private int preGroupCalcuLength = 12*15; //有多少组数据就进行计算心率，12s一次，每秒15次，共12*15组
    private int fourGroupCalcuLength = 4*15; //有多少组数据就进行更新，4s更新一次，每秒15次，共4*15组
    private int[] preCalcuEcgRate = new int[preGroupCalcuLength*10]; //前一次数的数据，12s
    private int[] currCalcuEcgRate = new int[preGroupCalcuLength*10]; //当前的数据，12s
    private int[] fourCalcuEcgRate = new int[fourGroupCalcuLength*10]; //4s的数据*/
    private boolean isFirstCalcu = true;  //是否是第一次计算心率，第一次要连续12秒的数据
    private int currentGroupIndex = 0;   //组的索引

    private TextView tv_healthydata_rate;
    private static String connecMac;   //当前连接的蓝牙mac地址
    private boolean isConnectted  =false;
    private boolean isConnectting  =false;
    private ArrayList<Integer> heartRateDates = new ArrayList<>();  // 心率数组
    private boolean isThreeMit = false;   //是否到三分钟
    private boolean isStartThreeMitTimer;  //是否开始三分钟倒计时计时器
    private TextView tv_healthdaydata_adjust;
    HashMap<Double, Integer> mRateLineRItemCount = new HashMap<>();  //计算波峰时不同区间出现的次数，
    private int mCalcutRateLineRCount=0;  //心率曲线R波峰计数次数，在每次调整后将计数设为0
    private boolean isStartAdjustLineTimeTask;  //是否开始调整心率曲线R波峰
    private MyTimeTask mAdjustLineTimeTask; //文字闪烁计时器
    private boolean isFirstAdjust = true;
    private boolean isNeedDrawEcgData = true; //是否要画心电数据，在跳到下个界面时则不需要画
    private DataOutputStream dataOutputStream;  //二进制文件输出流，写入文件
    private ByteBuffer byteBuffer;
    private long ecgFiletimeMillis =-1;  //开始有心电数据时的秒数，作为心电文件命名。静态变量，在其他界面会用到

    private static double ECGSCALE_MODE_HALF = 0.5;
    private static double ECGSCALE_MODE_ORIGINAL = 1;
    private static double ECGSCALE_MODE_DOUBLE = 2;
    private static double ECGSCALE_MODE_QUADRUPLE = 4;

   /* private static double ECGSCALE_MODE_HALF = 0;
    private static double ECGSCALE_MODE_ORIGINAL = 1;
    private static double ECGSCALE_MODE_DOUBLE = 2;
    private static double ECGSCALE_MODE_QUADRUPLE = 3;*/
    public static double ECGSCALE_MODE_CURRENT = ECGSCALE_MODE_QUADRUPLE;

    private BottomSheetDialog mBottomAdjustRateLineDialog;
    private boolean isLookupECGDataFromSport;

    private ServiceReceiver mReceiver01, mReceiver02;
    private static String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    private static String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_data);
        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText("健康数据");
        //setRightText("我的设备");
        setLeftImage(R.drawable.back_icon);
        setRightImage(R.drawable.yifu);

        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HealthyDataActivity.this,MyDeviceActivity.class));
            }
        });

        pv_healthydata_path = (EcgView) findViewById(R.id.pv_healthydata_path);
        tv_healthydata_rate = (TextView) findViewById(R.id.tv_healthydata_rate);
        tv_healthdaydata_adjust = (TextView) findViewById(R.id.tv_healthdaydata_adjust);
        TextView tv_healthydata_analysis = (TextView) findViewById(R.id.tv_healthydata_analysis);

        mRateLineRItemCount.put(ECGSCALE_MODE_HALF,0);
        mRateLineRItemCount.put(ECGSCALE_MODE_ORIGINAL,0);
        mRateLineRItemCount.put(ECGSCALE_MODE_DOUBLE,0);
        mRateLineRItemCount.put(ECGSCALE_MODE_QUADRUPLE,0);

        Intent intent = getIntent();
        if (intent!=null){
            isLookupECGDataFromSport = intent.getBooleanExtra(Constant.isLookupECGDataFromSport, false);
            if (isLookupECGDataFromSport){
                tv_healthydata_analysis.setVisibility(View.GONE);
            }
        }
    }

    /*Handler staticEcgHandler = new Handler(new Handler.Callback() {
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

    public Handler getStaticEcgHandlerInstance(){
        return staticEcgHandler;
    }
    */

    private void initData() {
        if (MainActivity.mBluetoothAdapter!=null){
            MainActivity.mBluetoothAdapter.startLeScan(mLeScanCallback);
            //绑定蓝牙，获取蓝牙服务
            bindService(new Intent(this, BleService.class), mConnection, BIND_AUTO_CREATE);
        }
    }

    //关闭心电数据传输
    public static void stopTransmitData(){
        if (mLeService!=null){
            mLeService.send(connecMac, Constant.stopDataTransmitOrder,true);  //关闭数据传输
        }
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
                        Log.i(TAG,"device.getAddress():"+device.getAddress());
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
            if (MainActivity.mBluetoothAdapter!=null){
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
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(1000);
                        Log.i(TAG,"写配置");
                        String writeConfigureOrder = "FF010A"+getDataHexString()+"0016";
                        //mLeService.send(connecMac, Constant.writeConfigureOrder,true);
                        mLeService.send(connecMac, writeConfigureOrder,true);

                        Thread.sleep(1000);
                        Log.i(TAG,"开启数据指令");
                        mLeService.send(connecMac, Constant.openDataTransmitOrder,true); 
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

            if (hexData.length()<40){
                return;
            }

            if (!isStartThreeMitTimer){
                isStartThreeMitTimer = true;
                startThreeMitTiming();
            }

            if(hexData.startsWith("FF 83 0F")){
                //心电数据
                //Log.i(TAG,"心电hexData:"+hexData);
                dealWithEcgData(hexData);
            }
            else if(hexData.startsWith("FF 86 11")){
                //加速度数据
                //Log.i(TAG,"加速度hexData:"+hexData);
                dealWithAccelerationgData(hexData);
            }
        }
    };


    //处理心电数据
    private void dealWithEcgData(String hexData) {
        final int [] ints = ECGUtil.geIntEcgaArr(hexData, " ", 3, 10); //一次的数据，10位
        if (!isLookupECGDataFromSport){
            writeEcgDataToBinaryFile(ints);
        }

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
            String data0 = "";
            for (int aCalcuEcgRate : calcuEcgRate) {
                data0 += aCalcuEcgRate + ",";
            }
            //Log.i(TAG,"data:"+data0);
            //Log.i(TAG,"calcuEcgRate.length:"+calcuEcgRate.length);

            //带入公式，计算心率
            final int heartRate = ECGUtil.countEcgRate(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
            Log.i(TAG,"heartRate0:"+heartRate);
            //calcuEcgRate = new int[groupCalcuLength*10];
            heartRateDates.add(heartRate);

            //更新心率
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (heartRate==0){
                        tv_healthydata_rate.setText("--");
                    }
                    else {
                        tv_healthydata_rate.setText(heartRate+"");
                    }
                }
            });
            System.arraycopy(ints, 0, calcuEcgRate, currentGroupIndex * 10 + 0, ints.length);

            int ecgAmpSum = ECGUtil.countEcgR(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
            //Log.i(TAG,"calcuEcgRate.length:"+calcuEcgRate.length);
            //Log.i(TAG,"ecgAmpSum:"+ecgAmpSum);
            setReteLineR(ecgAmpSum);
        }
        currentGroupIndex++;
        if (isNeedDrawEcgData){
            //绘图
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pv_healthydata_path.addEcgOnGroupData(ints);
                }
            });
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

    //根据当前计算出来的增益来调整心电曲线波峰
    private void setReteLineR(int ecgAmpSum) {
        /*
        *   在 ecgAmpSum < 5时 放大4倍
            在 5<=ecgAmpSum<12 时放大2倍
            在 12<=ecgAmpSum<26 时 不放大大不缩小。
            在ecgAmpSum>=26时 缩小两倍*/
        mCalcutRateLineRCount++;
        if (ecgAmpSum>=26){
            mRateLineRItemCount.put(ECGSCALE_MODE_HALF,mRateLineRItemCount.get(ECGSCALE_MODE_HALF)+1);
        }
        else if (12<=ecgAmpSum && ecgAmpSum<26){
            mRateLineRItemCount.put(ECGSCALE_MODE_ORIGINAL,mRateLineRItemCount.get(ECGSCALE_MODE_ORIGINAL)+1);
        }
        else if (5<=ecgAmpSum && ecgAmpSum<12){
            mRateLineRItemCount.put(ECGSCALE_MODE_DOUBLE,mRateLineRItemCount.get(ECGSCALE_MODE_DOUBLE)+1);
        }
        else if (ecgAmpSum<5){
            mRateLineRItemCount.put(ECGSCALE_MODE_QUADRUPLE,mRateLineRItemCount.get(ECGSCALE_MODE_QUADRUPLE)+1);
        }
        
        calcuAndAdjustRateLine();
    }

    //根据当前心率增益值计算是否需要调整曲线，需要的话讲‘调整’文字闪烁
    private void calcuAndAdjustRateLine() {
        if (mCalcutRateLineRCount>=5){
            boolean spangled = false;
            double adjustKey = -1;
            for (int i = 1; i <= 8; i=i*2) {
                double value = mRateLineRItemCount.get(0.5*i);
                if (mCalcutRateLineRCount<10 && value==mCalcutRateLineRCount){
                    spangled = true;  //当计数小于10时，某个键值对应的值和计相等时则调整
                    adjustKey = i;
                }
                if (mCalcutRateLineRCount>=10){
                    if (value/mCalcutRateLineRCount>0.7){  //当计数大于10时，某个键值对应的值/总数 大于0.7就开始调整
                        //开始
                        spangled = true;
                        adjustKey = 0.5*i;
                    }
                }
            }
            if (spangled){
                if (isFirstAdjust){
                    if (adjustKey != ECGSCALE_MODE_CURRENT){
                        startSpangleTextTimeTask();
                        isFirstAdjust = false;
                    }
                }else {
                    startSpangleTextTimeTask();
                }
            }
            Log.i(TAG,"mCalcutRateLineRCount:"+mCalcutRateLineRCount+",spangled:"+spangled+",isFirstAdjust:"+isFirstAdjust);
            String a= "";
            for (int i=0;i<mRateLineRItemCount.size();i++){
                a += i+":"+mRateLineRItemCount.get(i)+",";
            }
            Log.i(TAG,"map:"+a);
        }
    }

    //文字闪烁
    private void startSpangleTextTimeTask(){
        isStartAdjustLineTimeTask = true;
        if (mAdjustLineTimeTask==null){
            mAdjustLineTimeTask = new MyTimeTask(500, new TimerTask() {
                int clo;
                @Override
                public void run() {
                    if (!isStartAdjustLineTimeTask){
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (clo == 0) {
                                clo = 1;
                                tv_healthdaydata_adjust.setTextColor(Color.TRANSPARENT);
                            } else {
                                if (clo == 1) {
                                    clo = 2;
                                    tv_healthdaydata_adjust.setTextColor(Color.BLACK);
                                } else if (clo == 2) {
                                    clo = 0;
                                    tv_healthdaydata_adjust.setTextColor(Color.RED);
                                }
                            }
                        }
                    });
                }
            });
            mAdjustLineTimeTask.startTime();
        }
    }

    public void adjustLine(View view) {
        alertAdjustLineSeekBar();
    }

    private void alertAdjustLineSeekBar() {
        if (mBottomAdjustRateLineDialog==null){
            mBottomAdjustRateLineDialog = new BottomSheetDialog(HealthyDataActivity.this);
            View inflate = LayoutInflater.from(this).inflate(R.layout.view_adjustline, null);

            mBottomAdjustRateLineDialog.setContentView(inflate);
            Window window = mBottomAdjustRateLineDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.mystyle);  //添加动画

            SeekBar sb_adjust = (SeekBar) inflate.findViewById(R.id.sb_adjust);
            sb_adjust.setMax(80);  //设置最大值，分成4个级别，0-20,20-40,40-60,60-80

            sb_adjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.i(TAG,"onProgressChanged:"+progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.i(TAG,"onStart:"+seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.i(TAG,"onStop:"+seekBar.getProgress());
                    int endProgress = seekBar.getProgress();
                    adjustRateLineRToEcgView(endProgress);
                }
            });
        }
        mBottomAdjustRateLineDialog.show();
    }

    //根据进度给心电View设置放大的倍数
    private void adjustRateLineRToEcgView(int endProgress) {
        double type;
                 /*
                *  可以在 ecgAmpSum < 5时 放大4倍
                    在 5<=ecgAmpSum<12 时放大2倍
                    在 12<=ecgAmpSum<26 时 不放大大不缩小。
                    在ecgAmpSum>=26时 缩小两倍
                * */
        Log.i(TAG,"currentType:"+ECGSCALE_MODE_CURRENT);
        if (endProgress<80){
            type = ECGSCALE_MODE_HALF;
        }
        else if(20<=endProgress && endProgress<40){
            type = ECGSCALE_MODE_ORIGINAL;
        }
        else if(40<=endProgress && endProgress<60){
            type = ECGSCALE_MODE_DOUBLE;
        }
        else{
            type = ECGSCALE_MODE_QUADRUPLE;
        }

        if (type!=ECGSCALE_MODE_CURRENT){
            ECGSCALE_MODE_CURRENT = type;
            //重新绘图
            Log.i(TAG,"重新绘图");
            pv_healthydata_path.setRateLineR(type);
            mCalcutRateLineRCount = 0;
            isStartAdjustLineTimeTask = false;
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

        //给蓝牙设备同步指令
        MyTimeTask.startTimeRiseTimerTask(this, 1000, new MyTimeTask.OnTimeChangeAtScendListener() {
            @Override
            public void onTimeChange(Date date) {
                if (isNeedDrawEcgData){
                    String hexSynOrder = "FF070A"+getDataHexString()+"0016";
                    mLeService.send(connecMac, hexSynOrder,true);
                }
            }
        });
    }

    public void startSoS(View view) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
          /* 建立自定义Action常数的Intent(给PendingIntent参数之用) */
            Intent itSend = new Intent(SMS_SEND_ACTIOIN);
            Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);

          /* sentIntent参数为传送后接受的广播信息PendingIntent */
            PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, 0);

          /* deliveryIntent参数为送达后接受的广播信息PendingIntent */
            PendingIntent mDeliverPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itDeliver, 0);

          /* 发送SMS短信，注意倒数的两个PendingIntent参数 */

            List<SosActivity.SosNumber> sosNumberList = MyUtil.getSosNumberList();

            if (sosNumberList==null  || sosNumberList.size()==0){
                startActivity(new Intent(this,SosActivity.class));
                return;
            }
            String sosinfo = MyUtil.getStringValueFromSP(Constant.sosinfo);
            for (SosActivity.SosNumber sosNumber:sosNumberList){
                smsManager.sendTextMessage(sosNumber.phone, null, sosinfo, mSendPI, mDeliverPI);
            }
            MyUtil.showDialog("正在发送",this);
            Log.i(TAG,"sendTextMessage");
        }
        catch(Exception e) {
            Log.e(TAG,"e:"+e);
        }

        /*String SENT = "SMS_SEND";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getActivity(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getActivity(this, 0, new Intent(DELIVERED), 0);

        registerReceiver(new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("startSoS", "getResultCode():"+getResultCode());
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Log.i("startSoS", "Activity.RESULT_OK");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.i("startSoS", "RESULT_ERROR_GENERIC_FAILURE");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.i("startSoS", "RESULT_ERROR_NO_SERVICE");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.i("startSoS", "RESULT_ERROR_NULL_PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Log.i("startSoS", "RESULT_ERROR_RADIO_OFF");
                        break;
                }
            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                Log.i("startSoS", "getResultCode()::::"+getResultCode());
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Log.i("startSoS", "RESULT_OK");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("startSoS", "RESULT_CANCELED");
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));




        //一个继承自ContentObserver的监听器类
        class SmsObserver extends ContentObserver {

            public SmsObserver(Handler handler) {
                super(handler);
                // TODO Auto-generated constructor stub


            }
            @Override
            public void onChange(boolean selfChange) {
                Log.i(TAG1,"onChange:"+selfChange);
                // TODO Auto-generated method stub
                //查询发送向箱中的短信
                Cursor cursor=getContentResolver().query(Uri.parse(
                        "content://sms/outbox"), null, null, null, null);
                //遍历查询结果获取用户正在发送的短信
                while (cursor.moveToNext()) {
                    StringBuffer sb=new StringBuffer();
                    //获取短信的发送地址
                    sb.append("发送地址："+cursor.getString(cursor.getColumnIndex("address")));
                    //获取短信的标题
                    sb.append("\n标题："+cursor.getString(cursor.getColumnIndex("subject")));
                    //获取短信的内容
                    sb.append("\n内容："+cursor.getString(cursor.getColumnIndex("body")));
                    //获取短信的发送时间
                    Date date=new Date(cursor.getLong(cursor.getColumnIndex("date")));
                    //格式化以秒为单位的日期
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒");
                    sb.append("\n时间："+sdf.format(date));
                    System.out.println("查询到的正在发送的短信："+sb.toString());
                    Toast.makeText(HealthyDataActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                    //txtView.setText(sb.toString());
                    Log.i(TAG1,"sb.toString():"+sb.toString());
                }
                super.onChange(selfChange);
            }

        }




        SmsManager smsm = SmsManager.getDefault();
        smsm.sendTextMessage("+8613042439612", null, "44855", sentPI, deliveredPI);

        MyUtil.showToask(this,"已发送");




        getContentResolver().registerContentObserver(Uri.parse
                ("content://sms"), true, new SmsObserver(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.i(TAG1,"msg:"+msg.what+"," +msg.obj.toString());
            }
        }));*/
    }



    //开始分析
    public void startAnalysis(View view) {
        Log.i(TAG,"startAnalysis");
        if (!isThreeMit){
            Log.i(TAG,"!isThreeMit");
            ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(this);
            chooseAlertDialogUtil.setAlertDialogText("采集未满3分钟，无法分析出HRV的值，是否要继续采集？","直接分析","继续采集");
            chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
                @Override
                public void onConfirmClick() {
                    jumpToAnalysis();
                }
            });
        }
        else{
            jumpToAnalysis();
        }
    }

    private void jumpToAnalysis() {
        isNeedDrawEcgData = false;
        isStartAdjustLineTimeTask = false;
        //saveHeartRateDatesToSP(heartRateDates);
        Intent intent = new Intent(HealthyDataActivity.this, HeartRateActivity.class);
        intent.putExtra(Constant.sportState,Constant.SPORTSTATE_STATIC);
        if (heartRateDates.size()>0){
            intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
            intent.putExtra(Constant.ecgFiletimeMillis,ecgFiletimeMillis);
        }
        startActivity(intent);
    }

    //将心率数组保存到sp里
    private void saveHeartRateDatesToSP(List<Integer> heartRateDates) {
        String heartData = "";
        if (heartRateDates.size()>0){
            Gson gson = new Gson();
            heartData = gson.toJson(heartRateDates);  //   [1,2,3]
            Log.i(TAG,"heartData:"+heartData);
        }
        MyUtil.putStringValueFromSP("heartData",heartData);
        heartRateDates.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
        isNeedDrawEcgData = true;

/* 自定义IntentFilter为SENT_SMS_ACTIOIN Receiver */
        IntentFilter mFilter01;
        mFilter01 = new IntentFilter(SMS_SEND_ACTIOIN);
        mReceiver01 = new ServiceReceiver();
        registerReceiver(mReceiver01, mFilter01);

    /* 自定义IntentFilter为DELIVERED_SMS_ACTION Receiver */
        mFilter01 = new IntentFilter(SMS_DELIVERED_ACTION);
        mReceiver02 = new ServiceReceiver();
        registerReceiver(mReceiver02, mFilter01);


        //测试(模拟器上演示)
        /*FileInputStream fileInputStream = null;
        String ecgDatatext = "";
        String cacheFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/20170220210301.ecg";
        Log.i(TAG,"cacheFileName:"+cacheFileName);
        if (!cacheFileName.equals("")){
            try {
                if (fileInputStream==null){
                    File file = new File(cacheFileName);
                    if (file.exists()){
                        fileInputStream = new FileInputStream(cacheFileName);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (fileInputStream!=null){
            byte [] mybyte = new byte[1024];
            int length=0;
            try {
                while (true) {
                    length = fileInputStream.read(mybyte,0,mybyte.length);
                    Log.i(TAG,"length:"+length);
                    if (length!=-1) {
                        String s = new String(mybyte,0,length);
                        ecgDatatext +=s;
                    }else {
                        break;
                    }
                }
                Log.i(TAG,"ecgDatatext:"+ecgDatatext);
                if (!ecgDatatext.equals("")){
                    //此处的数据是滤波之后的，无需再进行滤波
                    String[] allGrounpData = ecgDatatext.split(",");
                    for (int i=0;i<allGrounpData.length;i++) {
                        datas.add(Integer.parseInt(allGrounpData[i]));
                    }
                    data0Q.addAll(datas);
                    startDrawSimulator();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

    }

    //开始画线
    /*private void startDrawSimulator(){
        mDrawWareTimer = new Timer();
        mDrawWareTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(pv_healthydata_path.isRunning){
                    if(data0Q.size() > 0){
                        pv_healthydata_path.addEcgCacheData(data0Q.poll());
                    }
                }
            }
        }, 0, 2);
    }*/



    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause");
        isNeedDrawEcgData = false;

        /* 取消注册自定义Receiver */

        unregisterReceiver(mReceiver01);
        unregisterReceiver(mReceiver02);
    }

    /* 自定义mServiceReceiver重写BroadcastReceiver监听短信状态信息 */
    public class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            //mTextView01.setText(intent.getAction().toString());
            if (intent.getAction().equals(SMS_SEND_ACTIOIN)) {
                try {
                    /* android.content.BroadcastReceiver.getResultCode()方法 */
                    //Retrieve the current result code, as set by the previous receiver.
                    switch(getResultCode()) {
                        case Activity.RESULT_OK:
                            /* 发送短信成功 */
                            //mTextView01.setText(R.string.str_sms_sent_success);
                            MyUtil.showToask(HealthyDataActivity.this,"发送短信成功");
                            MyUtil.showDialog("发送短信成功",HealthyDataActivity.this);
                            MyUtil.hideDialog();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            /* 发送短信失败 */
                            //mTextView01.setText(R.string.str_sms_sent_failed);
                            MyUtil.showToask(HealthyDataActivity.this,"发送短信失败 ");
                            MyUtil.showDialog("发送短信失败",HealthyDataActivity.this);
                            MyUtil.hideDialog();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            break;
                    }
                }
                catch(Exception e) {
                    e.getStackTrace();
                }
            }
            else if(intent.getAction().equals(SMS_DELIVERED_ACTION))
            {
                try
                {
                    /* android.content.BroadcastReceiver.getResultCode()方法 */
                    switch(getResultCode())
                    {
                        case Activity.RESULT_OK:
                            /* 短信 */
                            //mTextView01.setText(R.string.str_sms_sent_success);
                            //MyUtil.showToask(HealthyDataActivity.this,"短信");
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            /* 短信未送达 */
                            //mTextView01.setText(R.string.str_sms_sent_failed);
                            //MyUtil.showToask(HealthyDataActivity.this,"短信未送达");
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            break;
                    }
                }
                catch(Exception e) {
                    e.getStackTrace();
                }
            }
        }
    }

    public static String getDataHexString(){
        SimpleDateFormat formatter = new SimpleDateFormat("yy MM dd HH mm ss");
        Date curDate = new Date();
        String dateString = formatter.format(curDate);
        System.out.println(dateString);
        String[] split = dateString.split(" ");
        String dateHexString = "";
        for (String s:split){
            String hex = Integer.toHexString(Integer.parseInt(s));
            if (hex.length()==1){
                hex ="0"+hex;
            }
            dateHexString += hex;
        }
        Log.i(TAG,"dateHexString:"+dateHexString);
        return dateHexString;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
        /*if (mDrawWareTimer!=null){
            mDrawWareTimer.cancel();
            mDrawWareTimer = null;
        }*/
        /*if (mUpdateThread!=null){
            try {
                mUpdateThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        unbindService(mConnection);
        if (MainActivity.mBluetoothAdapter!=null){
            MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
        }
    }

    /*public void test(View view) {
        loadDatas();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(pv_healthydata_path.isRunning){
                    int [] oneGroupeData = new int[10];
                    for (int i=0;i<10;i++){
                        if (data0Q.size()>0){
                            oneGroupeData[i] = data0Q.poll();
                        }

                    }
                    pv_healthydata_path.addEcgOnGroupData(oneGroupeData);

                }
            }
        }, 0, 1000/15);


    }*/

    //开始整个，从文件中获取的情况
    /*private void simulator(){
        loadDatas();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(pv_healthydata_path.isRunning){
                    if(data0Q.size() > 0){
                        pv_healthydata_path.addEcgCacheData(data0Q.poll());
                    }
                }
            }
        }, 0, 2);
    }*/


    //画整个文件
    /*private void loadDatas(){
        try{
            String data0 = "";
            InputStream in = getResources().openRawResource(R.raw.ecgdata);
            int length = in.available();
            byte [] buffer = new byte[length];
            in.read(buffer);
            data0 = new String(buffer);
            in.close();
            String[] data0s = data0.split(",");
            for(String str : data0s){
                datas.add(Integer.parseInt(str));
            }

            data0Q.addAll(datas);
        }catch (Exception e){}

    }*/


}