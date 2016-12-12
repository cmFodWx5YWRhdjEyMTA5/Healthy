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
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.ecganalysis.DiagnosisNDK;
import com.amsu.healthy.ecganalysis.HeartRateResult;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.PathView;
import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class HealthyDataActivity extends BaseActivity {

    private static final String TAG = "HealthyDataActivity";
    private static final int REQUEST_ENABLE_BT = 2;

    public static BleService mLeService;
    private BluetoothAdapter mBluetoothAdapter;
    private PathView pv_healthydata_path;
    private FileOutputStream fileOutputStream;

    private String cacheText = "";

    private int preGroupCalcuLength = 12*15;
    private int fourGroupCalcuLength = 4*15;
    private int[] preCalcuEcgRate = new int[preGroupCalcuLength*10]; //前一次数的数据，12s
    private int[] currCalcuEcgRate = new int[preGroupCalcuLength*10]; //当前的数据，12s
    private int[] fourCalcuEcgRate = new int[fourGroupCalcuLength*10]; //4s的数据
    private boolean isFirstCalcu = true;
    private int currentIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_data);


        checkBLEFeature();
        //绑定蓝牙，获取蓝牙服务
        bindService(new Intent(this, BleService.class), mConnection, BIND_AUTO_CREATE);

        initView();
        initData();

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
            Log.i(TAG, "onCharacteristicChanged() - " + mac + ", " + uuid + ", " + hexData);

            //4.2写配置信息   onCharacteristicChanged() - 44:A6:E5:1F:C5:BF, 00001002-0000-1000-8000-00805f9b34fb, FF 81 05 00 16
            //4.5App读主机设备的版本号  onCharacteristicChanged() - 44:A6:E5:1F:C5:BF, 00001002-0000-1000-8000-00805f9b34fb, FF 84 07 88 88 00 16

            /*数据：
                FF 83 0F 00 00 00 00 00 00 00 00 00 00 00 16
                FF 83 0F 00 00 00 00 00 00 00 00 00 00 01 16
                FF 83 0F 00 00 00 00 00 00 00 00 00 00 02 16

                只有倒数2位变化
            */


            final int [] ints = ECGUtil.geIntEcgaArr(hexData, " ", 3, 10); //一次的数据，10位
            String data = "";
            for (int i=0;i<ints.length;i++){
                if (i!=ints.length-1){
                    data += ints[i]+" ";
                }
                else {
                    data += ints[i] + ",";
                }
            }
            //cacheText += data;


            if (isFirstCalcu){
                //第一次计算，连续12秒数据
                if (currentIndex<preGroupCalcuLength){
                    //未到12s
                    for (int j=0;j<ints.length;j++){
                        currCalcuEcgRate[currentIndex*10+j] = ints[j];
                    }
                    currentIndex++;
                }
                else{
                    //到12s
                    isFirstCalcu = false;
                    currentIndex = 0;
                    preCalcuEcgRate = currCalcuEcgRate;
                    //带入公式，计算心率


                }

            }
            else {
                //第二次进来，采集4s数据
                if (currentIndex<fourGroupCalcuLength){
                    //未到4s
                    for (int j=0;j<ints.length;j++){
                        fourCalcuEcgRate[currentIndex*10+j] = ints[j];
                    }
                    currentIndex++;
                }
                else {
                    int i=0;
                    //到4s,需要前8s+当前4s
                    for (int j=7*15*10;j<preCalcuEcgRate.length;j++){
                        currCalcuEcgRate[i] = preCalcuEcgRate[j];
                        i++;
                    }
                    for (int k=0;k<fourCalcuEcgRate.length;k++){
                        currCalcuEcgRate[i] = fourCalcuEcgRate[k];
                        i++;
                    }
                    currentIndex = 0;
                    preCalcuEcgRate = currCalcuEcgRate;
                    //带入公式，计算心率

                    //注：这里有一组的数据遗漏

                }

            }




            //写到文件里
            try {
                if (fileOutputStream==null){
                    String filePath = getCacheDir()+"/"+"cacheFile";
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pv_healthydata_path.drawLine(ints);
                }
            });





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

    //检查是否支持蓝牙
    private void checkBLEFeature() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "error_bluetooth_not_supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }


    private void initView() {
        initHeadView();


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

        pv_healthydata_path = (PathView) findViewById(R.id.pv_healthydata_path);
        int[] ecgData = {101,106,130,106,77,199,133,222,111,87};

        //pv_healthydata_path.setOneDrawData(ecgData);




    }


    public void startSoS(View view) {

    }

    public void startAnalysis(View view) {
        startActivity(new Intent(this,MoveStateActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    public void scanBel(View view) {
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    public void sendOrder1(View view) {
        mLeService.send("44:A6:E5:1F:C5:E4","FF0109100C080E010016",true);
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
        int [] test1 = {50,100,60,99,51,111,66,100,50,100};
        int [] test2 = {50,100,55,100,50,100,77,100,50,100};
        int [] test3 = {63,64,64,65,65,66,66,66,67,67};
        int [] test4 = {100,68,68,69,69,69,70,70,71,71};
        int [] test5 = {72,72,72,73,73,73,74,74,74,74};
        int [] test6 = {74,74,74,74,145,74,80,80,80,90};
        int [] test7 = {90,90,89,89,88,88,88,87,87,80};
        int [] test8 = {80,70,70,70,69,69,68,68,67,67};
        int [] test9 = {111,66,65,63,62,12,58,57,54,53};

        pv_healthydata_path.drawLine(test1);
        pv_healthydata_path.drawLine(test2);
        pv_healthydata_path.drawLine(test3);
        pv_healthydata_path.drawLine(test4);
        pv_healthydata_path.drawLine(test5);
        pv_healthydata_path.drawLine(test6);
        pv_healthydata_path.drawLine(test7);
        pv_healthydata_path.drawLine(test8);
        pv_healthydata_path.drawLine(test9);
    }
}
