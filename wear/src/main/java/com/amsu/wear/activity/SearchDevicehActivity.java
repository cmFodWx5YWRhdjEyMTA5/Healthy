package com.amsu.wear.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.utils.BleConstant;
import com.amsu.bleinteraction.utils.DeviceBindUtil;
import com.amsu.bleinteraction.utils.LogUtil;
import com.amsu.bleinteraction.utils.ThreadManager;
import com.amsu.wear.R;
import com.amsu.wear.util.TimerUtil;

import java.util.ArrayList;

public class SearchDevicehActivity extends Activity{
    private static final String TAG = "SearchDevicehActivity";
    private ArrayList<BleDevice> searchDeviceList;
    private BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searc_deviceh);
        initData();
    }

    private void initData() {
        searchDeviceList = new ArrayList<>();
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        new Thread(){
            @Override
            public void run() {
                super.run();

                while (true){
                    if (mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled()) {
                        if (!isBluetoothEnable){
                            scanLeDevice(true);
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

        TimerUtil.executeDelayTime(6*1000, new TimerUtil.DelayExecuteTimeListener() {
            @Override
            public void execute() {
                stopScan();
            }
        });

    }

    private void stopScan() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描

        Intent intent = getIntent();
        intent.putParcelableArrayListExtra("searchBleDeviceList",searchDeviceList);
        setResult(RESULT_OK, intent);
        finish();
        Log.i(TAG,"finish");
    }

    boolean mScanning;
    private boolean isBluetoothEnable;

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            if (mBluetoothAdapter.isEnabled()) {
                if (mScanning) {
                    return;
                }
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
            //null,63:5C:3E:B6:A0:ae,null,10,0
            LogUtil.i(TAG,"onLeScan:"+device.getName()+","+device.getAddress()+","+device.getUuids()+","+device.getBondState()+","+device.getType());

            Runnable updateUITask = new Runnable() {
                @Override
                public void run() {
                    dealwithScanReceive(device,rssi,scanRecord);
                }
            };
            ThreadManager.THREAD_POOL_EXECUTOR.execute(updateUITask);

        }
    };

    private void dealwithScanReceive(BluetoothDevice device, int rssi, byte[] scanRecord) {
        String leName = device.getName();
        if (leName!=null && (leName.startsWith("BLE") || leName.startsWith("AMSU")) && leName.length()<25){
            Log.i(TAG,"发现目标主机");

            boolean isAddToList = true;
            for (BleDevice device1:searchDeviceList){
                if (device1.getLEName().equals(leName)){
                    isAddToList = false;
                }
            }

            if (isAddToList){
                BleDevice bleDevice = new BleDevice("运动衣:" + leName, "", device.getAddress(), leName, BleConstant.sportType_Cloth, rssi);
                BleConnectionProxy.DeviceBindByHardWareType deviceBindTypeByBleBroadcastInfo = BleConnectionProxy.DeviceBindByHardWareType.devideNOSupport;
                if (BleConnectionProxy.getInstance().isSupportBindByHardware(device.getName())){
                    //需要通过硬件来进行绑定 AMSU_EADE4
                    deviceBindTypeByBleBroadcastInfo = DeviceBindUtil.getDeviceBindTypeByBleBroadcastInfo(scanRecord);
                    Log.i(TAG,"deviceBindTypeByBleBroadcastInfo:"+deviceBindTypeByBleBroadcastInfo);
                    bleDevice.setClothDeviceType(BleConstant.clothDeviceType_secondGeneration_AMSU_BindByHardware);
                }

                bleDevice.setBindType(deviceBindTypeByBleBroadcastInfo);

                Log.i(TAG,"bleDevice:"+bleDevice);
                searchDeviceList.add(bleDevice);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter!=null){
            mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
        }
    }



}
