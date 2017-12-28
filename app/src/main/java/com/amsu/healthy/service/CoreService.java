package com.amsu.healthy.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.bean.MessageEvent;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.utils.BleConstant;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.HealthyDataActivity;
import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.PopupWindowUtil;
import com.amsu.healthy.utils.ScreenManageUtil;
import com.amsu.healthy.utils.SportRecoveryUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CoreService extends Service {
    private static final String TAG = CoreService.class.getSimpleName();
    private BleConnectionProxy mBleConnectionProxy;

    public CoreService() {
    }

    public Context getInstance(){
        return this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");
        init();
        return START_STICKY;
    }

    private void init() {
        initBleDataTrasmit();

        if (MyApplication.getInstance().mCoreService==null){
            MyApplication.getInstance().mCoreService = this;
        }

        ScreenManageUtil.stratListenScrrenBroadCast(this);

        SportRecoveryUtil.judgeRecoverRunState(this);

        EventBus.getDefault().register(this);
    }

    private void initBleDataTrasmit(){
        mBleConnectionProxy = BleConnectionProxy.getInstance();

        int userAge = HealthyIndexUtil.getUserAge();
        boolean isAutoOffline = MyUtil.getBooleanValueFromSP("mIsAutoOffline");
        int deivceType = MyUtil.getIntValueFromSP(Constant.sportType,Constant.sportType_Cloth);
        int clothDeviceType = MyUtil.getIntValueFromSP(Constant.mClothDeviceType);
        Log.i(TAG,"保存的衣服设备类型 clothDeviceType："+clothDeviceType);

        boolean isNeedWriteFileHead = false;   //心电文件是否需要些写入文件头，暂时不需要
        mBleConnectionProxy.initConnectedConfiguration(new BleConnectionProxy.ConnectionConfiguration(userAge,isAutoOffline,deivceType,clothDeviceType,isNeedWriteFileHead),this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.messageType){
            case BleConnectionProxy.msgType_Connect:
                Log.i(TAG,"连接变化" );//event.singleValue为连接状态，等于BleConnectionProxy.connectTypeConnected为连接成功，BleConnectionProxy.connectTypeDisConnected为断开连接
                setDeviceConnectedState(event.singleValue);
                break;
            case BleConnectionProxy.msgType_BatteryPercent:
                Log.w(TAG,"电量变化");//event.singleValue为电量int值
                dealwithLebBatteryChange(event.singleValue);
                break;
            case BleConnectionProxy.msgType_OfflineFile:
                Log.w(TAG,"主机发现离线文件");//暂时不用做处理
                break;
        }
    }



    private void setDeviceConnectedState(int deviceConnectedState){
        BaseActivity baseActivity = MyApplication.getInstance().getmCurrApplicationActivity();
        if (baseActivity!=null){
            ImageView iv_base_connectedstate = (ImageView) baseActivity.findViewById(R.id.iv_base_connectedstate);
            TextView tv_base_charge = (TextView) baseActivity.findViewById(R.id.tv_base_charge);

            int deviceType = mBleConnectionProxy.getmConnectionConfiguration().deviceType;
            String msg = "";

            if (deviceConnectedState == BleConnectionProxy.connectTypeConnected){
                iv_base_connectedstate.setImageResource(R.drawable.yilianjie);
                if (deviceType==Constant.sportType_Cloth){
                    msg = getResources().getString(R.string.sportswear_connection_successful);
                }
                else if(deviceType==Constant.sportType_Insole){
                    msg = getResources().getString(R.string.insole_connection_successful);
                }
            }
            else if (deviceConnectedState == BleConnectionProxy.connectTypeDisConnected){
                iv_base_connectedstate.setImageResource(R.drawable.duankai);
                tv_base_charge.setVisibility(View.GONE);
                if (deviceType==Constant.sportType_Cloth){
                    msg = getResources().getString(R.string.sportswear_connection_disconnected);
                }
                else if(deviceType==Constant.sportType_Insole){
                    msg = getResources().getString(R.string.insole_connection_disconnected);
                }

            }

            if (!TextUtils.isEmpty(msg)){
                PopupWindowUtil.showDeviceConnectedChangePopWindow(deviceConnectedState,msg);
            }
        }

    }

    private void dealwithLebBatteryChange(int intExtra) {

        BaseActivity baseActivity = MyApplication.getInstance().getmCurrApplicationActivity();
        if (baseActivity instanceof MainActivity || baseActivity instanceof HealthyDataActivity){
            TextView tv_base_charge = (TextView) baseActivity.findViewById(R.id.tv_base_charge);
            Log.i(TAG,"电量："+intExtra);

            String batteryPercent = "";
            int deviceType = mBleConnectionProxy.getmConnectionConfiguration().deviceType;
            if (deviceType== BleConstant.sportType_Cloth){
                if (mBleConnectionProxy.getClothCurrBatteryPowerPercent()>0 && mBleConnectionProxy.getClothCurrBatteryPowerPercent()<=100){
                    batteryPercent = mBleConnectionProxy.getClothCurrBatteryPowerPercent()+"%";
                }
            }
            else if (deviceType== BleConstant.sportType_Insole){
                for (BleDevice bleDevice : mBleConnectionProxy.getmInsoleDeviceBatteryInfos().values()) {
                    if (bleDevice.getBattery()>0 && bleDevice.getBattery()<=100){
                        batteryPercent +="  "+ bleDevice.getBattery()+"%";
                    }
                }
            }

            if (!TextUtils.isEmpty(batteryPercent)){
                tv_base_charge.setVisibility(View.VISIBLE);
                tv_base_charge.setText(batteryPercent);
            }
            else {
                tv_base_charge.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
