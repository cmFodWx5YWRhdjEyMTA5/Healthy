package com.amsu.healthy.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.EcgFilterUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.EcgView;
import com.amsu.healthy.view.PathView;
import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class HealthyDataActivity extends BaseActivity {

    private static final String TAG = "HealthyDataActivity";
    private static final int REQUEST_ENABLE_BT = 2;

    public static BleService mLeService;

    private EcgView pv_healthydata_path;
    private FileOutputStream fileOutputStream;

    private String cacheText = "";

    private int preGroupCalcuLength = 12*15; //有多少组数据就进行计算心率，12s一次，每秒15次，共12*15组
    private int fourGroupCalcuLength = 4*15; //有多少组数据就进行更新，4s更新一次，每秒15次，共4*15组
    private int[] preCalcuEcgRate = new int[preGroupCalcuLength*10]; //前一次数的数据，12s
    private int[] currCalcuEcgRate = new int[preGroupCalcuLength*10]; //当前的数据，12s
    private int[] fourCalcuEcgRate = new int[fourGroupCalcuLength*10]; //4s的数据
    private boolean isFirstCalcu = true;  //是否是第一次计算心率，第一次要连续12秒的数据
   private int currentIndex = 0;   //组的索引

    private List<Integer> datas = new ArrayList<>();

    private Queue<Integer> data0Q = new LinkedList<Integer>();
    private int heartRate;   //心率
    private TextView tv_healthydata_rate;


    private String test ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_data);

        initView();
        initData();
        //绑定蓝牙，获取蓝牙服务
        bindService(new Intent(this, BleService.class), mConnection, BIND_AUTO_CREATE);

       /* double []test = {45,40,67,89,23,45,56,65,45,45,33,66,77.80,80};
        HeartRateResult ecgResult = DiagnosisNDK.getEcgResult(test, test.length, 15);
        Log.i(TAG,"ecgResult:"+ecgResult.toString());*/

    }

    // ble数据交互的关键参数
    private final BleCallBack mBleCallBack = new BleCallBack() {

        @Override
        public void onConnected(String mac) {
            Log.i(TAG, "onConnected() - " + mac);
            //ToastUtil.showMsg(HealthyDataActivity.this, R.string.scan_connected, mac + " ");
            mLeService.startReadRssi(mac, 1000);
        }

        @Override
        public void onConnectTimeout(String mac) {
            Log.w(TAG, "onConnectTimeout() - " + mac);
            //ToastUtil.showMsg(HealthyDataActivity.this, R.string.scan_connect_timeout, mac + " ");
        }

        @Override
        public void onConnectionError(String mac, int status, int newState) {
            Log.w(TAG, "onConnectionError() - " + mac + ", status = " + status + ", newState = " + newState);
            //ToastUtil.showMsg(HealthyDataActivity.this, R.string.scan_connection_error,mac + "\nstatus:" + status + "\nnew state:" + newState + "\n");
        }

        @Override
        public void onDisconnected(String mac) {
            Log.w(TAG, "onDisconnected() - " + mac);
            //ToastUtil.showMsg(HealthyDataActivity.this, R.string.scan_disconnected, mac + " ");
        }

        @Override
        public void onServicesDiscovered(String mac) {
            Log.i(TAG, "onServicesDiscovered() - " + mac);
            // !!!到这一步才可以与从机进行数据交互
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
            final int [] ints = ECGUtil.geIntEcgaArr(hexData, " ", 3, 10); //一次的数据，10位

            String data = "";
            for (int i=0;i<ints.length;i++){
                data += ints[i]+",";
            }
            //Log.i(TAG,"onCharacteristicChanged滤波前:"+data);



            test += data;
            //Log.i(TAG,"onCharacteristicChanged滤波后:"+data);
            //cacheText += data;

            //Log.i(TAG,"currentIndex:"+currentIndex);
            if (isFirstCalcu){
                //第一次计算，连续12秒数据
                if (currentIndex<preGroupCalcuLength){
                    //未到12s
                    for (int j=0;j<ints.length;j++){
                        currCalcuEcgRate[currentIndex*10+j] = ints[j];
                    }
                }
                else{
                    //到12s
                    Log.i(TAG,"test:"+test);
                    test = "";
                    isFirstCalcu = false;
                    currentIndex = 0;
                    for (int n=0;n<currCalcuEcgRate.length;n++){
                        preCalcuEcgRate[n] = currCalcuEcgRate[n];
                    }

                    String data0 = "";
                    for (int j=0;j<currCalcuEcgRate.length;j++){
                        data0 += currCalcuEcgRate[j]+",";
                    }
                    Log.i(TAG,"data0:"+data0);
                    Log.i(TAG,"currCalcuEcgRate.length:"+currCalcuEcgRate.length);


                    //带入公式，计算心率
                    heartRate = ECGUtil.countEcgRate(currCalcuEcgRate, currCalcuEcgRate.length, 150);
                    Log.i(TAG,"heartRate0:"+heartRate);
                    //更新心率
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_healthydata_rate.setText(heartRate+"");
                        }
                    });
                    for (int j=0;j<ints.length;j++){
                        currCalcuEcgRate[currentIndex*10+j] = ints[j];
                    }
                }
                currentIndex++;
            }
            else {
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

                    currentIndex = 0;
                    for (int n=0;n<currCalcuEcgRate.length;n++){
                        preCalcuEcgRate[n] = currCalcuEcgRate[n];
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


                    for (int j=0;j<ints.length;j++){
                        fourCalcuEcgRate[currentIndex*10+j] = ints[j];
                    }
                }
                currentIndex++;
            }

            //滤波处理
            for (int i=0;i<ints.length;i++){
                int temp = EcgFilterUtil.miniEcgFilterLp(ints[i], 0);
                temp = EcgFilterUtil.miniEcgFilterHp(temp, 0);
                ints[i] = temp;
            }
            //绘图
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pv_healthydata_path.addEcgOnGroupData(ints);
                }
            });

            //写入文件时用到，以逗号分隔
            data = "";
            for (int i=0;i<ints.length;i++){
                data += ints[i]+",";
            }

            //写到文件里
            try {
                if (fileOutputStream==null){
                    String filePath = getCacheDir()+"/"+System.currentTimeMillis();  //随机生成一个文件
                    fileOutputStream = new FileOutputStream(filePath,true);
                    MyUtil.putStringValueFromSP("cacheFileName",filePath);
                }
                byte[] bytes = data.getBytes();
                fileOutputStream.write(bytes,0,bytes.length);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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


    //扫描蓝牙回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            Log.i(TAG,"onLeScan");
            Log.i(TAG,"device:"+device.getName()+","+device.getAddress()+","+device.getUuids()+","+device.getBondState()+","+device.getType());

            //BLE#0x44A6E51FC5BF,44:A6:E5:1F:C5:BF,null,10,2
            //null,72:A8:23:AF:25:42,null,10,0
            //null,63:5C:3E:B6:A0:AE,null,10,0


        }
    };




    private void initView() {
        initHeadView();
        pv_healthydata_path = (EcgView) findViewById(R.id.pv_healthydata_path);
        tv_healthydata_rate = (TextView) findViewById(R.id.tv_healthydata_rate);

    }

    private void initData() {
        setCenterText("健康数据");
        setRightText("我的设备");
        setLeftImage(R.drawable.back_icon);

        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //simulator();
    }


    public void startSoS(View view) {

    }

    public void startAnalysis(View view) {
        startActivity(new Intent(this,MoveStateActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    public void scanBel(View view) {
        //mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    public void sendOrder1(View view) {
        mLeService.send("44:A6:E5:1F:C5:E4","FF010A100C080E010016",true);
    }

    public void sendOrder2(View view) {
        mLeService.send("44:A6:E5:1F:C5:E4","FF0206010016",true);
    }
    public void sendOrder3(View view) {
        mLeService.send("44:A6:E5:1F:C5:E4","FF0206000016",true);
    }

    public void connect(View view) {
        Log.i(TAG,"链接");
        mLeService.connect("44:A6:E5:1F:C5:E4",false);
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
}
