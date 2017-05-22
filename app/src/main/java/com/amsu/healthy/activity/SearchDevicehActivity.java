package com.amsu.healthy.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.bean.DeviceList;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SearchDevicehActivity extends BaseActivity {
    private static final String TAG = "SearchDevicehActivity";
    private Animation animation;
    private TextView tv_search_state;
    private List<Device> deviceListFromSP;
    DeviceList deviceList;
    private boolean timeTask10ScendOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searc_deviceh);

        initView();
        initDate();

    }

    private void initView() {
        deviceList = new DeviceList();
        deviceListFromSP = new ArrayList<>();


        ImageView iv_heartrate_rotateimage = (ImageView) findViewById(R.id.iv_heartrate_rotateimage);
        tv_search_state = (TextView) findViewById(R.id.tv_search_state);

        animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(-1);
        animation.setInterpolator(new LinearInterpolator());

        iv_heartrate_rotateimage.setAnimation(animation);

        MyTimeTask.startCountDownTimerTask(1000 * 10, new MyTimeTask.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                Log.i(TAG,"10秒钟定时器onTomeOut");
                timeTask10ScendOver = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopScan();
                    }
                });


            }
        });

        MyTimeTask.startCountDownTimerTask(1000 * 60, new MyTimeTask.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                Log.i(TAG,"1分钟定时器：onTomeOut");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (deviceListFromSP.size()>0){
                            return;
                        }
                        scanTimeOver();
                    }
                });
            }
        });
    }

    private void scanTimeOver() {
        Log.i(TAG,"没有扫描到设备");
        MyUtil.showToask(SearchDevicehActivity.this,"没有扫描到设备");
        animation.cancel();
        setResult(RESULT_OK,getIntent());
        finish();
    }

    private void stopScan() {
        deviceList.setDeviceList(deviceListFromSP);
        MyUtil.putDeviceListToSP(deviceList);

        Log.i(TAG,"deviceListFromSP:"+deviceListFromSP.toString());
        if (deviceListFromSP.size()==0){
            Log.i(TAG,"没有扫描到设备");
            MyUtil.showToask(SearchDevicehActivity.this,"没有扫描到设备");


        }
        else {
            if (deviceListFromSP.size()>1){
                //有新设备
                Log.i(TAG,"添加新设备成功");
                MyUtil.showToask(SearchDevicehActivity.this,"发现新设备,点击设置需要运行的设备");
            }
            else {
                MyUtil.putStringValueFromSP(Constant.currectDeviceLEName,deviceListFromSP.get(0).getLEName());
            }


            MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
            //tv_search_state.setText("查找成功");
            //MyUtil.putStringValueFromSP(Constant.currectDeviceLEName,leName);
            animation.cancel();
            //MyUtil.showToask(SearchDevicehActivity.this,"设备切换成功");
            setResult(RESULT_OK,getIntent());
            finish();
            Log.i(TAG,"finish");

        }

    }

    private void initDate() {
        if (MainActivity.mBluetoothAdapter!=null){
            boolean b = MainActivity.mBluetoothAdapter.startLeScan(mLeScanCallback);
            Log.i(TAG,"startLeScan:"+b);
        }
    }

    //扫描蓝牙回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            //BLE#0x44A6E51FC5BF,44:A6:E5:1F:C5:BF,null,10,2
            //null,72:A8:23:AF:25:42,null,10,0
            //null,63:5C:3E:B6:A0:AE,null,10,0

            Log.i(TAG,"onLeScan:"+device.getName()+","+device.getAddress()+","+device.getUuids()+","+device.getBondState()+","+device.getType());

            String leName = device.getName();
            if (leName!=null && leName.startsWith("BLE")){
                /*if (deviceListFromSP.size()==0){
                    deviceListFromSP.add(new Device("智能运动衣1","",device.getAddress(), leName,1));
                    DeviceList deviceList = new DeviceList();
                    deviceList.setDeviceList(deviceListFromSP);
                    MyUtil.putDeviceListToSP(deviceList);
                    Log.i(TAG,"添加新设备成功");
                    MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
                    //tv_search_state.setText("查找成功");
                    MyUtil.putStringValueFromSP(Constant.currectDeviceLEName,leName);
                    animation.cancel();
                    setResult(RESULT_OK,getIntent());
                    finish();
                    Log.i(TAG,"finish");
                }
                else {
                    boolean isAdded = false;
                    for (int i=0;i<deviceListFromSP.size();i++){
                        if (deviceListFromSP.get(i).getLEName().equals(leName)){
                            isAdded  = true;
                        }
                    }
                    if (!isAdded){
                        int currentIndex = deviceListFromSP.get(deviceListFromSP.size() - 1).getIndex()+1;
                        deviceListFromSP.add(new Device("智能运动衣"+currentIndex,"",device.getAddress(), leName,currentIndex));
                        DeviceList deviceList = new DeviceList();
                        deviceList.setDeviceList(deviceListFromSP);
                        MyUtil.putDeviceListToSP(deviceList);
                        Log.i(TAG,"添加新设备成功");
                        MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
                        //tv_search_state.setText("查找成功");
                        MyUtil.putStringValueFromSP(Constant.currectDeviceLEName,leName);
                        animation.cancel();
                        setResult(RESULT_OK,getIntent());
                        finish();
                        Log.i(TAG,"finish");
                    }
                }*/

               

                boolean isAddToList = true;
                for (Device d:deviceListFromSP){
                    if (d.getLEName().equals(leName)){
                        isAddToList = false;
                    }
                }
                
                if (isAddToList){
                    deviceListFromSP.add(new Device("智能运动衣","",device.getAddress(), leName,1));
                }

                if (timeTask10ScendOver){
                    stopScan();
                }


            }
        }
    };


    public void stopsearch(View view) {
        MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
        animation.cancel();
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animation.cancel();
        MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
    }
}
