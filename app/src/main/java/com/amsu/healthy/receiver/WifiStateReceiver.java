package com.amsu.healthy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.amsu.healthy.activity.HeartRateActivity;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.OffLineDbAdapter;
import com.amsu.healthy.utils.wifiTramit.DeviceOffLineFileUtil;

import java.util.List;

public class WifiStateReceiver extends BroadcastReceiver {

    private static final String TAG = "WifiStateReceiver";
    private boolean mIsStartUpload;
    private boolean mIsSetTimerTask;
    private DeviceOffLineFileUtil deviceOffLineFileUtil;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"onReceive:"+intent.getAction());

        //设置60秒定时器，60秒之后默认数据传输完毕
        if (!mIsSetTimerTask){
            deviceOffLineFileUtil = new DeviceOffLineFileUtil();
            deviceOffLineFileUtil.setTransferTimeOverTime(new DeviceOffLineFileUtil.OnTimeOutListener() {
                @Override
                public void onTomeOut() {
                    Log.i(TAG,"60秒时间到了");
                    mIsStartUpload = false;
                }
            },30);
            mIsSetTimerTask = true;
        }
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            //CONNECTIVITY_CHANGE 某个wifi or 基站信号 连接或断开
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetInfo != null) {
                // 判断是wifi连接
                if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    Log.i(TAG, "wifi连接状态:" + activeNetInfo.getState());
                    // 连接成功
                    if (NetworkInfo.State.CONNECTED == activeNetInfo.getState()) {
                        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                        //获取当前wifi名称
                        String wifiName = wifiInfo.getSSID();
                        Log.i(TAG,"连接到网络 " + wifiName);
                        if (!MyUtil.isEmpty(wifiName) && !wifiName.equals("ESP8266")){//不是底座WiFi模块，则可以进行数据上传
                            //启动开始数据
                            Log.i(TAG, "wifi已连上，开始传输数据");

                            if (!mIsStartUpload){
                                mIsStartUpload = true;
                                if (deviceOffLineFileUtil!=null){
                                    deviceOffLineFileUtil.startTime();
                                }
                                startUploadOffLineData(context);
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    //开始上传数据
    private void startUploadOffLineData(Context context) {
        OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(context);
        offLineDbAdapter.open();

        List<UploadRecord> uploadRecordsState = offLineDbAdapter.queryRecordByUploadState("0");
        Log.i(TAG,"uploadRecordsState:"+uploadRecordsState);

        for (UploadRecord uploadRecord:uploadRecordsState){
            HeartRateActivity.uploadRecordDataToServer(uploadRecord,context);
        }

    }
}
