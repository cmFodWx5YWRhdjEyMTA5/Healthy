package com.amsu.wear.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.amsu.bleinteraction.bean.MessageEvent;
import com.amsu.bleinteraction.proxy.Ble;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.utils.BleConstant;
import com.amsu.wear.bean.User;
import com.amsu.wear.util.SPUtil;
import com.amsu.wear.util.ToastUtil;
import com.amsu.wear.util.UploadDataUtil;
import com.amsu.wear.util.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CoreService extends Service {

    private static final String TAG = CoreService.class.getSimpleName();

    public CoreService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onStartCommand");
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");

        return START_STICKY;
    }

    private void init() {
        initBleDataTrasmit();

        //ScreenManageUtil.stratListenScrrenBroadCast(this);
        //SportRecoveryUtil.judgeRecoverRunState(this);

        EventBus.getDefault().register(this);


        new UploadDataUtil().checkUploadFailData();
    }

    private void initBleDataTrasmit(){
        boolean isNeedWriteFileHead = true;   //心电文件是否需要些写入文件头
        int userAge =0;
        int deivceType = SPUtil.getIntValueFromSP(BleConstant.sportType,BleConstant.sportType_Cloth);
        User userFromSP = UserUtil.getUserInfo();
        String phone = "";
        if (userFromSP!=null){
            phone = userFromSP.getPhone();
        }

        BleConnectionProxy.BleConfiguration configuration = new BleConnectionProxy.BleConfiguration(
                userAge,
                isNeedWriteFileHead,
                deivceType,
                phone,
                BleConnectionProxy.userLoginWay.phone);
        try {
            if (userFromSP!=null){
                configuration.sex = Integer.parseInt(userFromSP.getSex());
                configuration.height = Integer.parseInt(userFromSP.getHeight());
                configuration.weight = Integer.parseInt(userFromSP.getWeight());
            }

        } catch (Exception e) {
        }

        Ble.init( this,configuration);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.messageType){
            case msgType_Connect:
                Log.i(TAG,"onDeviceConnect连接变化" );//event.singleValue为连接状态，等于BleConnectionProxy.connectTypeConnected为连接成功，BleConnectionProxy.connectTypeDisConnected为断开连接
                setDeviceConnectedState(event.singleValue);
                break;
            case msgType_BatteryPercent:
                Log.w(TAG,"电量变化");//event.singleValue为电量int值
                //dealwithLebBatteryChange(event.singleValue);
                break;
            case msgType_OfflineFile:
                Log.w(TAG,"主机发现离线文件");//暂时不用做处理
                break;
        }
    }

    private void setDeviceConnectedState(int deviceConnectedState){
        if (deviceConnectedState == BleConnectionProxy.connectTypeConnected){
            ToastUtil.showToask("设备已连接");
        }
        else if (deviceConnectedState == BleConnectionProxy.connectTypeDisConnected){
            ToastUtil.showToask("设备已断开");

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
