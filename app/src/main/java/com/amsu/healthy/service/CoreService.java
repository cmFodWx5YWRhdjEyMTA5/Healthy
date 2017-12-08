package com.amsu.healthy.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.proxy.BleDataProxy;
import com.amsu.bleinteraction.proxy.LeProxy;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.HealthyDataActivity;
import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.PopupWindowUtil;

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
        initBleDataTrasmit();

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, LeProxy.makeFilter());

        if (MyApplication.getInstance().mCoreService==null){
            MyApplication.getInstance().mCoreService = this;
        }

        return START_STICKY;
    }


    private void initBleDataTrasmit(){
        mBleConnectionProxy = BleConnectionProxy.getInstance();

        int userAge = HealthyIndexUtil.getUserAge();
        boolean isAutoOffline = MyUtil.getBooleanValueFromSP("mIsAutoOffline");
        int deivceType = MyUtil.getIntValueFromSP(Constant.sportType,Constant.sportType_Cloth);
        int clothDeviceType = MyUtil.getIntValueFromSP(Constant.mClothDeviceType);
        Log.i(TAG,"保存的衣服设备类型 clothDeviceType："+clothDeviceType);

        boolean isNeedWriteFileHead = false;   //心电文件是否需要些写入文件头，暂时不需要

        mBleConnectionProxy.initConnectedConfiguration(new BleConnectionProxy.ConnectionConfiguration(userAge,isAutoOffline,deivceType,clothDeviceType,isNeedWriteFileHead));
        mBleConnectionProxy.init(this);
    }

    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case LeProxy.ACTION_DEVICE_CONNECTED:
                    Log.i(TAG,"已连接 " );
                    setDeviceConnectedState(PopupWindowUtil.connectTypeConnected);
                    break;
                case LeProxy.ACTION_DEVICE_DISCONNECTED:
                    Log.w(TAG,"已断开 ");
                    setDeviceConnectedState(PopupWindowUtil.connectTypeDisConnected);
                    break;
                case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                    //dealwithLebDataChange(intent);
                    break;
                case LeProxy.ACTION_BATTERY_DATA_AVAILABLE:// 接收到电量数据
                    Log.w(TAG,"电量变化");
                    dealwithLebBatteryChange(intent);
                    break;
                case LeProxy.ACTION_RECEIVE_EXIT_OFFLINEFILE:
                    Log.w(TAG,"主机发现离线文件");
                    break;
            }
        }

    };

    private void setDeviceConnectedState(int deviceConnectedState){
        BaseActivity baseActivity = MyApplication.getInstance().getmCurrApplicationActivity();
        if (baseActivity!=null){
            ImageView iv_base_connectedstate = (ImageView) baseActivity.findViewById(R.id.iv_base_connectedstate);
            TextView tv_base_charge = (TextView) baseActivity.findViewById(R.id.tv_base_charge);

            int deviceType = mBleConnectionProxy.getmConnectionConfiguration().deviceType;
            String msg = "";

            if (deviceConnectedState == PopupWindowUtil.connectTypeConnected){
                iv_base_connectedstate.setImageResource(R.drawable.yilianjie);
                if (deviceType==Constant.sportType_Cloth){
                    msg = getResources().getString(R.string.sportswear_connection_successful);
                }
                else if(deviceType==Constant.sportType_Insole){
                    msg = getResources().getString(R.string.insole_connection_successful);
                }
            }
            else if (deviceConnectedState == PopupWindowUtil.connectTypeDisConnected){
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


    private void dealwithLebBatteryChange(Intent intent) {
        int intExtra = intent.getIntExtra(BleDataProxy.EXTRA_BATTERY_DATA, -1);


        BaseActivity baseActivity = MyApplication.getInstance().getmCurrApplicationActivity();
        if (baseActivity instanceof MainActivity || baseActivity instanceof HealthyDataActivity){
            TextView tv_base_charge = (TextView) baseActivity.findViewById(R.id.tv_base_charge);
            Log.i(TAG,"电量："+intExtra);
            if (intExtra==-1){
                //设备已断开
                tv_base_charge.setVisibility(View.GONE);
            }
            else {
                tv_base_charge.setVisibility(View.VISIBLE);
                tv_base_charge.setText(intExtra+"%");
            }
        }
        mBleConnectionProxy.setClothCurrBatteryPowerPercent(intExtra);
    }

}
