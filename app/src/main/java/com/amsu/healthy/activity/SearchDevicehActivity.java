package com.amsu.healthy.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.healthy.R;
import com.amsu.healthy.bean.DeviceList;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;

import java.util.ArrayList;

public class SearchDevicehActivity extends BaseActivity {
    private static final String TAG = "SearchDevicehActivity";
    private Animation animation;
    private TextView tv_search_state;
    private ArrayList<BleDevice> searchDeviceList;
    DeviceList deviceList;
    private boolean timeTask10ScendOver;
    private BleDevice mDeviceFromSP;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searc_deviceh);


        initView();
        initDate();
    }

    private void initView() {
        initHeadView();
        deviceList = new DeviceList();
        //deviceListFromSP = MyUtil.getDeviceListFromSP();
        searchDeviceList = new ArrayList<>();
        mDeviceFromSP = MyUtil.getDeviceFromSP();

        ImageView iv_heartrate_rotateimage = (ImageView) findViewById(R.id.iv_heartrate_rotateimage);
        tv_search_state = (TextView) findViewById(R.id.tv_search_state);

        animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(1500);
        animation.setRepeatCount(-1);
        animation.setInterpolator(new LinearInterpolator());

        iv_heartrate_rotateimage.setAnimation(animation);

        MyTimeTask.startCountDownTimerTask(1000 * 3, new MyTimeTask.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                Log.i(TAG,"4秒钟定时器onTomeOut");
                timeTask10ScendOver = true;
                stopScan();
            }
        });

        MyTimeTask.startCountDownTimerTask(1000 * 12, new MyTimeTask.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                Log.i(TAG,"20s定时器：onTomeOut");
                if (searchDeviceList!=null && searchDeviceList.size()==0) {
                    scanTimeOver();
                }
            }
        });
    }

   /* boolean isonResumeEd ;

    @Override
    protected void onResume() {
        super.onResume();
        if (!isonResumeEd){
            if (MainActivity.mBluetoothAdapter!=null && !MainActivity.mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
            }
            isonResumeEd = true;
        }
    }*/

    //20s没有搜到设备，停止扫描
    private void scanTimeOver() {
        Log.i(TAG,"没有扫描到设备");
        //MyUtil.showToask(SearchDevicehActivity.this,"没有扫描到设备");
        //animation.cancel();
        animation = null;
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void stopScan() {
        if (searchDeviceList.size()==0){
            Log.i(TAG,"没有扫描到设备");
            //MyUtil.showToask(SearchDevicehActivity.this,"没有扫描到设备");
        }
        else {
            if (searchDeviceList.size()==1){
                //发现一个设备，有可能是当前设备，有可能是新的设备
                //MyUtil.putStringValueFromSP(Constant.currectDeviceLEMac,deviceListFromSP.get(0).getMac());
                //MyUtil.saveUserToSP(searchDeviceList.get(0));
            }
            else {
                //有新设备
                Log.i(TAG,"添加新设备成功");
                //MyUtil.showToask(SearchDevicehActivity.this,"发现多个设备,点击设置需要运行的设备");
            }

            mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
            //tv_search_state.setText("查找成功");
            //MyUtil.putStringValueFromSP(Constant.currectDeviceLEMac,leName);
            //animation.cancel();
            animation = null;
            //MyUtil.showToask(SearchDevicehActivity.this,"设备切换成功");

            Intent intent = getIntent();
            intent.putParcelableArrayListExtra("searchBleDeviceList",searchDeviceList);
            setResult(RESULT_OK, intent);
            finish();
            Log.i(TAG,"finish");
        }
    }

    private void initDate() {
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

    }

    boolean mScanning;
    private boolean isBluetoothEnable;

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            if (mBluetoothAdapter.isEnabled()) {
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
            //null,63:5C:3E:B6:A0:ae,null,10,0

            Log.i(TAG,"onLeScan:"+device.getName()+","+device.getAddress()+","+device.getUuids()+","+device.getBondState()+","+device.getType());

            String leName = device.getName();
            if (leName!=null && (leName.startsWith("BLE") || leName.startsWith("AMSU") || leName.startsWith("AMSU_P")) && device.getName().length()<25){

                boolean isAddToList = true;
                for (BleDevice device1:searchDeviceList){
                    if (device1.getLEName().equals(leName)){
                        isAddToList = false;
                    }
                }
                if (isAddToList){
                    if (leName.startsWith("AMSU_P")){
                        //鞋垫
                        searchDeviceList.add(new BleDevice(getResources().getString(R.string.insole),"",device.getAddress(), leName, Constant.sportType_Insole,rssi));
                    }
                    else {
                        searchDeviceList.add(new BleDevice(getResources().getString(R.string.sportswear)+":"+leName,"",device.getAddress(), leName,Constant.sportType_Cloth,rssi));
                    }
                }
                if (timeTask10ScendOver){
                    stopScan();
                }
            }
        }
    };

    public void stopsearch(View view) {
        scanTimeOver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animation!=null){
            animation.cancel();
        }
        if (mBluetoothAdapter!=null){
            mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
        }
    }
}
