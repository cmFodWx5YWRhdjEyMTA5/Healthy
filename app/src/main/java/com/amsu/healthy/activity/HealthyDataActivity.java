package com.amsu.healthy.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
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

    public static BleService mLeService;

    private EcgView pv_healthydata_path;
    private FileOutputStream fileOutputStream;


    private int groupCalcuLength = 100; //
    private int[] calcuEcgRate = new int[groupCalcuLength*10]; //1000条数据
    private boolean isNext = false;

    /*private int preGroupCalcuLength = 12*15; //有多少组数据就进行计算心率，12s一次，每秒15次，共12*15组
    private int fourGroupCalcuLength = 4*15; //有多少组数据就进行更新，4s更新一次，每秒15次，共4*15组
    private int[] preCalcuEcgRate = new int[preGroupCalcuLength*10]; //前一次数的数据，12s
    private int[] currCalcuEcgRate = new int[preGroupCalcuLength*10]; //当前的数据，12s
    private int[] fourCalcuEcgRate = new int[fourGroupCalcuLength*10]; //4s的数据*/
    private boolean isFirstCalcu = true;  //是否是第一次计算心率，第一次要连续12秒的数据
    private int currentIndex = 0;   //组的索引

    private List<Integer> datas = new ArrayList<>();


    private Queue<Integer> data0Q = new LinkedList<Integer>();
    private int heartRate;   //心率
    private TextView tv_healthydata_rate;

    private String test ="";
    private List<Device> deviceListFromSP;
    private static String connecMac;
    private boolean isConnectted  =false;
    private boolean isConnectting  =false;
    private List<Integer> heartRateDates = new ArrayList<>();
    private boolean isThreeMit = false;
    private Timer mDrawWareTimer;
    private boolean isStartEcgData;
    private BottomSheetDialog bottomSheetDialog;
    private TextView tv_healthdaydata_adjust;
    HashMap<Integer, Integer> rateLineRItemCount = new HashMap<>();
    private int calcutRateLineRCount=0;
    private boolean isStartTimeTask;
    private MyTimeTask mMyTimeTask;
    private boolean isFirstAdjust = true;
    public static final String action = "jason.broadcast.action";
    private boolean isDrawEcg = true;
    private DataOutputStream dataOutputStream;
    private ByteBuffer byteBuffer;
    public static long ecgFiletimeMillis =-1;

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
        setRightText("我的设备");
        setLeftImage(R.drawable.back_icon);

        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HealthyDataActivity.this,MyDeviceActivity.class));
            }
        });

        pv_healthydata_path = (EcgView) findViewById(R.id.pv_healthydata_path);
        tv_healthydata_rate = (TextView) findViewById(R.id.tv_healthydata_rate);
        //RelativeLayout rl_healyhdata_adjust = (RelativeLayout) findViewById(R.id.rl_healyhdata_adjust);
        tv_healthdaydata_adjust = (TextView) findViewById(R.id.tv_healthdaydata_adjust);

        startTiming();  //开始计时，测试


        rateLineRItemCount.put(0,0);
        rateLineRItemCount.put(1,0);
        rateLineRItemCount.put(2,0);
        rateLineRItemCount.put(3,0);
    }
    private void startTimeTask(){
        isStartTimeTask = true;
        if (mMyTimeTask==null){
            mMyTimeTask = new MyTimeTask(500, new TimerTask() {
                int clo;
                @Override
                public void run() {
                    if (!isStartTimeTask){
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

                                } /*else {
                                clo = 0;
                                tv_healthdaydata_adjust.setTextColor(Color.BLUE);
                            }*/
                            }
                        }
                    });

                }
            });
            mMyTimeTask.startTime();
        }

    }

    private void initData() {
        deviceListFromSP = MyUtil.getDeviceListFromSP();
        if (MainActivity.mBluetoothAdapter!=null){
            MainActivity.mBluetoothAdapter.startLeScan(mLeScanCallback);

            //绑定蓝牙，获取蓝牙服务
            bindService(new Intent(this, BleService.class), mConnection, BIND_AUTO_CREATE);
        }
    }

    public static void stopTransmitData(){
        mLeService.send(connecMac, Constant.stopDataTransmitOrder,true);  //关闭数据传输
    }

    // ble数据交互的关键参数
    private final BleCallBack mBleCallBack = new BleCallBack() {

        @Override
        public void onConnected(String mac) {
            Log.i(TAG, "onConnected() - " + mac);
            //ToastUtil.showMsg(HealthyDataActivity.this, R.string.scan_connected, mac + " ");
            mLeService.startReadRssi(mac, 1000);
            MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描

            isConnectted = true;
            isConnectting = false;
        }

        @Override
        public void onConnectTimeout(String mac) {
            Log.w(TAG, "onConnectTimeout() - " + mac);
            isConnectting = false;
            //ToastUtil.showMsg(HealthyDataActivity.this, R.string.scan_connect_timeout, mac + " ");
        }

        @Override
        public void onConnectionError(String mac, int status, int newState) {
            Log.w(TAG, "onConnectionError() - " + mac + ", status = " + status + ", newState = " + newState);
            isConnectting = false;
            //ToastUtil.showMsg(HealthyDataActivity.this, R.string.scan_connection_error,mac + "\nstatus:" + status + "\nnew state:" + newState + "\n");
        }

        @Override
        public void onDisconnected(String mac) {
            Log.w(TAG, "onDisconnected() - " + mac);
            isConnectting = false;
            //ToastUtil.showMsg(HealthyDataActivity.this, R.string.scan_disconnected, mac + " ");
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
                        mLeService.send(connecMac, Constant.writeConfigureOrder,true);  //开启数据传输

                        Thread.sleep(1000);
                        Log.i(TAG,"开启数据指令");
                        mLeService.send(connecMac, Constant.openDataTransmitOrder,true);  //开启数据传输
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

            startTiming();

            if(hexData.startsWith("FF 83 0F")){
                //心电数据
                //Log.i(TAG,"心电hexData:"+hexData);
                final int [] ints = ECGUtil.geIntEcgaArr(hexData, " ", 3, 10); //一次的数据，10位

                //写到文件里，二进制方式写入
                try {
                    if (fileOutputStream==null){
                        ecgFiletimeMillis = System.currentTimeMillis();
                        String filePath = MyUtil.generateECGFilePath(HealthyDataActivity.this, ecgFiletimeMillis); //随机生成一个ecg格式文件
                        //String filePath = getCacheDir()+"/"+MyUtil.getECGFileNameDependFormatTime(new Date())+".ecg";  //随机生成一个文件
                        //String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+MyUtil.getECGFileNameDependFormatTime(new Date())+".ecg";
                        fileOutputStream = new FileOutputStream(filePath,true);
                        MyUtil.putStringValueFromSP("cacheFileName",filePath);
                        //  RandomAccessFile
                        dataOutputStream = new DataOutputStream(fileOutputStream);
                        byteBuffer = ByteBuffer.allocate(2);
                    }
                    for (int anInt : ints) {
                        byteBuffer.clear();
                        byteBuffer.putShort((short) anInt);
                        dataOutputStream.writeByte(byteBuffer.get(1));
                        dataOutputStream.writeByte(byteBuffer.get(0));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Log.i(TAG,"onCharacteristicChanged滤波前:"+data);
                //滤波处理
                for (int i=0;i<ints.length;i++){
                    int temp = EcgFilterUtil.miniEcgFilterLp(ints[i], 0);
                    temp = EcgFilterUtil.miniEcgFilterHp(temp, 0);
                    ints[i] = temp;
                }

                //Log.i(TAG,"currentIndex:"+currentIndex);
                if (isFirstCalcu){
                    //第一次计算，连续12秒数据
                    if (currentIndex<groupCalcuLength){
                        //未到12s
                        for (int j=0;j<ints.length;j++){
                            calcuEcgRate[currentIndex*10+j] = ints[j];
                        }
                    }
                    else{
                        String ecgFileNameDependFormatTime = MyUtil.getECGFileNameDependFormatTime(new Date());
                        //Log.i(TAG,"ecgFileNameDependFormatTime:"+ecgFileNameDependFormatTime);
                        //到12s
                        //Log.i(TAG,"test:"+test);
                        test = "";
                        //isFirstCalcu = false;
                        currentIndex = 0;

                    /*for (int n=0;n<currCalcuEcgRate.length;n++){
                        preCalcuEcgRate[n] = currCalcuEcgRate[n];
                    }
*/
                        String data0 = "";
                        for (int j=0;j<calcuEcgRate.length;j++){
                            data0 += calcuEcgRate[j]+",";
                        }
                        //Log.i(TAG,"data:"+data0);
                        //Log.i(TAG,"calcuEcgRate.length:"+calcuEcgRate.length);

                        //带入公式，计算心率
                        heartRate = ECGUtil.countEcgRate1(calcuEcgRate, calcuEcgRate.length, 150);
                        Log.i(TAG,"heartRate0:"+heartRate);
                        //calcuEcgRate = new int[groupCalcuLength*10];
                        heartRateDates.add(heartRate);

                        Intent intent = new Intent(action);
                        intent.putExtra("data", heartRate);
                        sendBroadcast(intent);

                        int ecgAmpSum = ECGUtil.countEcgR(calcuEcgRate, calcuEcgRate.length, 150);
                        Log.i(TAG,"calcuEcgRate.length:"+calcuEcgRate.length);
                        Log.i(TAG,"ecgAmpSum:"+ecgAmpSum);
                        setReteLineR(ecgAmpSum);

                        //更新心率
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_healthydata_rate.setText(heartRate+"");
                            }
                        });
                        for (int j=0;j<ints.length;j++){
                            calcuEcgRate[currentIndex*10+j] = ints[j];
                        }
                    }
                    currentIndex++;
                }

            /*else {
                //第二次进来，采集4s数据
                if (currentIndex<fourGroupCalcuLength){
                    //未到4s
                    for (int j=0;j<ints.length;j++){
                        fourCalcuEcgRate[currentIndex*10+j] = ints[j];
                    }
                }
                else {
                    //到4s,需要前8s+当前4s
                    Log.i(TAG,"test:"+test);
                    test = "";
                    int i=0;
                    for (int j=4*15*10;j<preCalcuEcgRate.length;j++){
                        currCalcuEcgRate[i] = preCalcuEcgRate[j];
                        i++;
                    }
                    for (int k=0;k<fourCalcuEcgRate.length;k++){
                        currCalcuEcgRate[i] = fourCalcuEcgRate[k];
                        i++;
                    }



                    String data1 = "";
                    for (int j=0;j<currCalcuEcgRate.length;j++){
                        data1 += currCalcuEcgRate[j]+",";
                    }
                    Log.i(TAG,"data1:"+data1);
                    Log.i(TAG,"currCalcuEcgRate.length:"+currCalcuEcgRate.length);

                    //带入公式，计算心率
                    heartRate = ECGUtil.countEcgRate(currCalcuEcgRate, currCalcuEcgRate.length, 150);
                    Log.i(TAG,"heartRate:"+heartRate);
                    //更新心率
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_healthydata_rate.setText(heartRate+"");
                        }
                    });

                    currentIndex = 0;
                    for (int n=0;n<currCalcuEcgRate.length;n++){
                        preCalcuEcgRate[n] = currCalcuEcgRate[n];
                    }

                    for (int j=0;j<ints.length;j++){
                        fourCalcuEcgRate[currentIndex*10+j] = ints[j];
                    }
                }
                currentIndex++;
            }
*/
                if (isDrawEcg){
                    //绘图
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pv_healthydata_path.addEcgOnGroupData(ints);
                        }
                    });
                }
            }
            else if(hexData.startsWith("FF 86 11")){
                //加速度数据
                //Log.i(TAG,"加速度hexData:"+hexData);
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
        }

    };

    private void setReteLineR(int ecgAmpSum) {
        /*
        *   在 ecgAmpSum < 5时 放大4倍
            在 5<=ecgAmpSum<12 时放大2倍
            在 12<=ecgAmpSum<26 时 不放大大不缩小。
            在ecgAmpSum>=26时 缩小两倍*/
        calcutRateLineRCount++;
        if (ecgAmpSum>=26){
            rateLineRItemCount.put(0,rateLineRItemCount.get(0)+1);
        }
        else if (12<=ecgAmpSum && ecgAmpSum<26){
            rateLineRItemCount.put(1,rateLineRItemCount.get(1)+1);
        }
        else if (5<=ecgAmpSum && ecgAmpSum<12){
            rateLineRItemCount.put(2,rateLineRItemCount.get(2)+1);
        }
        else if (ecgAmpSum<5){
            rateLineRItemCount.put(3,rateLineRItemCount.get(3)+1);
        }

        if (calcutRateLineRCount>=5){
            boolean spangled = false;
            double adjustKey = -1;
            for (int i=0;i<rateLineRItemCount.size();i++){
                double value = rateLineRItemCount.get(i);
                if (calcutRateLineRCount<10 && value==calcutRateLineRCount){
                    spangled = true;
                    adjustKey = i;
                }
                if (calcutRateLineRCount>=10){
                    if (value/calcutRateLineRCount>0.7){
                        //开始
                        spangled = true;
                        adjustKey = i;
                    }
                }
            }
            if (spangled){
                if (isFirstAdjust){
                    if (adjustKey != 1){
                        startTimeTask();
                        isFirstAdjust = false;
                    }
                }else {
                    startTimeTask();
                }

            }
            Log.i(TAG,"calcutRateLineRCount:"+calcutRateLineRCount+",spangled:"+spangled+",isFirstAdjust:"+isFirstAdjust);
            String a= "";
            for (int i=0;i<rateLineRItemCount.size();i++){
                a += i+":"+rateLineRItemCount.get(i)+",";
            }
            Log.i(TAG,"map:"+a);
        }
    }

    //开始三分钟计时
    public void startTiming(){
        if (!isStartEcgData){
            isStartEcgData = true;
            final Timer timer = new Timer();
            TimerTask tt=new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG,"TimerTask:到点了");
                    isThreeMit = true;
                    timer.cancel();
                }
            };
            timer.schedule(tt, 1000*60*3);
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

            Log.i(TAG,"onLeScan");
            Log.i(TAG,"device:"+device.getName()+","+device.getAddress()+","+device.getUuids()+","+device.getBondState()+","+device.getType());
            String leName = device.getName();

            if (leName!=null && leName.startsWith("BLE")) {
                for (int i = 0; i < deviceListFromSP.size(); i++) {
                    if (leName.equals(deviceListFromSP.get(i).getLEName())){
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
        }
    };




    public void startSoS(View view) {

    }

    public void startAnalysis(View view) {
        Log.i(TAG,"startAnalysis");
        if (!isThreeMit){
            Log.i(TAG,"!isThreeMit");
            View inflate = View.inflate(this, R.layout.view_dialog_showchoose, null);
            TextView bt_choose_cancel = (TextView) inflate.findViewById(R.id.bt_choose_cancel);
            TextView bt_choose_ok = (TextView) inflate.findViewById(R.id.bt_choose_ok);


            final AlertDialog alertDialog = new AlertDialog.Builder(HealthyDataActivity.this)
                    .setView(inflate)
                    .create();
            alertDialog.show();
            float width = getResources().getDimension(R.dimen.x800);
            float height = getResources().getDimension(R.dimen.x500);

            alertDialog.getWindow().setLayout(new Float(width).intValue(),new Float(height).intValue());

                    /*.setTitle("采集未满3分钟，无法分析出HRV的值，是否要继续采集？")
                    .setPositiveButton("继续采集", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("直接分析", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(HealthyDataActivity.this,MoveStateActivity.class));
                            isNext = true;
                            if (heartRateDates.size()>0){
                                String heartData = "";
                                for (int i=0;i<heartRateDates.size();i++){
                                    heartData += heartRateDates.get(i)+",";
                                    MyUtil.putStringValueFromSP("heartData",heartData);
                                }
                            }

                        }
                    })
                    .create();*/

            bt_choose_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            bt_choose_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isDrawEcg = false;
                    startActivity(new Intent(HealthyDataActivity.this,MoveStateActivity.class));
                    isNext = true;
                    String heartData = "";
                    if (heartRateDates.size()>0){
                        for (int i=0;i<heartRateDates.size();i++){
                            heartData += heartRateDates.get(i)+",";
                        }
                    }
                    MyUtil.putStringValueFromSP("heartData",heartData);
                    alertDialog.dismiss();
                }
            });

            //alertDialog.setCanceledOnTouchOutside(false);

        }
        else{
            isDrawEcg = false;
            startActivity(new Intent(HealthyDataActivity.this,MoveStateActivity.class));
            String heartData = "";
            if (heartRateDates.size()>0){
                for (int i=0;i<heartRateDates.size();i++){
                    heartData += heartRateDates.get(i)+",";
                }
            }
            MyUtil.putStringValueFromSP("heartData",heartData);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        deviceListFromSP = MyUtil.getDeviceListFromSP();  //界面可见时重新获取设备列表，以更新，以便蓝牙扫描时在设备列表里

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
    private void startDrawSimulator(){
        mDrawWareTimer = new Timer();
        mDrawWareTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(EcgView.isRunning){
                    if(data0Q.size() > 0){
                        pv_healthydata_path.addEcgCacheData(data0Q.poll());
                    }
                }
            }
        }, 0, 2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDrawWareTimer!=null){
            mDrawWareTimer.cancel();
            mDrawWareTimer = null;
        }
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
        unbindService(mConnection);
        if (MainActivity.mBluetoothAdapter!=null){
            MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
        }
    }

    public void test(View view) {
        loadDatas();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(EcgView.isRunning){
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


    }

    //开始整个，从文件中获取的情况
    private void simulator(){
        loadDatas();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(EcgView.isRunning){
                    if(data0Q.size() > 0){
                        pv_healthydata_path.addEcgCacheData(data0Q.poll());
                    }
                }
            }
        }, 0, 2);
    }


    //画整个文件
    private void loadDatas(){
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

    }

    public void adjustLine(View view) {
        choosePicture();
    }

    private void choosePicture() {
        if (bottomSheetDialog==null){
            bottomSheetDialog = new BottomSheetDialog(HealthyDataActivity.this);
            View inflate = LayoutInflater.from(this).inflate(R.layout.view_adjustline, null);

            bottomSheetDialog.setContentView(inflate);
            Window window = bottomSheetDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.mystyle);  //添加动画

            SeekBar sb_adjust = (SeekBar) inflate.findViewById(R.id.sb_adjust);
            sb_adjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int startProgress = 0;
                double currentType=1;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.i(TAG,"onProgressChanged:"+progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.i(TAG,"onStart:"+seekBar.getProgress());
                    startProgress = seekBar.getProgress();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.i(TAG,"onStop:"+seekBar.getProgress());
                    int endProgress = seekBar.getProgress();
                    //endProgress-startProgress
                    double type = 2;
                     /*
                    *  可以在 ecgAmpSum < 5时 放大4倍
                        在 5<=ecgAmpSum<12 时放大2倍
                        在 12<=ecgAmpSum<26 时 不放大大不缩小。
                        在ecgAmpSum>=26时 缩小两倍
                    * */
                    Log.i(TAG,"currentType:"+currentType);
                    if (endProgress<10){
                        type = 0.5;
                    }
                    else if(10<=endProgress&&endProgress<20){
                        type = 1;
                    }
                    else if(20<=endProgress&&endProgress<30){
                        type = 2;
                    }
                    else if(30<=endProgress&&endProgress<=40){
                        type = 4;
                    }

                    if (type!=currentType){
                        currentType = type;
                        //重新绘图
                        Log.i(TAG,"重新绘图");
                        pv_healthydata_path.setRateLineR(type);
                        calcutRateLineRCount = 0;
                        isStartTimeTask = false;
                    }
                }
            });
        }

        bottomSheetDialog.show();




    }
}
