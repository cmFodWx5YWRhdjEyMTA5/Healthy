package com.amsu.healthy.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.receiver.SmsReceiver;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.SosSendUtil;
import com.amsu.healthy.utils.ble.BleDataProxy;
import com.amsu.healthy.utils.ble.EcgAccDataUtil;
import com.amsu.healthy.utils.ble.LeProxy;
import com.amsu.healthy.view.EcgView;

import java.util.ArrayList;
import java.util.Arrays;

public class HealthyDataActivity extends BaseActivity {
    private static final String TAG = "HealthyDataActivity";

    private EcgView pv_healthydata_path;

    private TextView tv_healthydata_rate;
    private ArrayList<Integer> heartRateDates ;  // 心率数组
    private boolean isNeedDrawEcgData = true; //是否要画心电数据，在跳到下个界面时则不需要画
    private boolean isActivityFinsh = false; //

    private long startTimeMillis =-1;  //开始有心电数据时的秒数，作为心电文件命名。静态变量，在其他界面会用到
    private boolean mIsLookupECGDataFromSport;

    private SmsReceiver mReceiver01, mReceiver02;
    private ImageView iv_base_connectedstate;
    private TextView tv_base_charge;
    private int mCclothDeviceType;
    private LeProxy mLeProxy;
    private BleDataProxy mBleDataProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_data);
        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.stationary_ecg));
        setLeftImage(R.drawable.back_icon);
        setRightImage(R.drawable.yifu);

        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backJudge();
            }
        });
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HealthyDataActivity.this,MyDeviceActivity.class));
            }
        });

        pv_healthydata_path = (EcgView) findViewById(R.id.pv_healthydata_path);
        tv_healthydata_rate = (TextView) findViewById(R.id.tv_healthydata_rate);
        TextView tv_healthydata_analysis = (TextView) findViewById(R.id.tv_healthydata_analysis);

        iv_base_connectedstate = (ImageView) findViewById(R.id.iv_base_connectedstate);
        iv_base_connectedstate.setVisibility(View.VISIBLE);
        tv_base_charge = (TextView) findViewById(R.id.tv_base_charge);

        Intent intent = getIntent();

        mIsLookupECGDataFromSport = intent.getBooleanExtra(Constant.isLookupECGDataFromSport, false);
        if (mIsLookupECGDataFromSport){
            tv_healthydata_analysis.setVisibility(View.GONE);
            getTv_base_centerText().setVisibility(View.GONE);
        }

        LocalBroadcastManager.getInstance(HealthyDataActivity.this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());

        heartRateDates = new ArrayList<>();
        MyApplication.runningActivity = MyApplication.HealthyDataActivity;

        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_CHARGE_CHANGE);
        registerReceiver(mchargeReceiver, filter);

        if (MyApplication.clothCurrBatteryPowerPercent !=-1){
            tv_base_charge.setVisibility(View.VISIBLE);
            tv_base_charge.setText(MyApplication.clothCurrBatteryPowerPercent +"%");
        }

        mLeProxy = LeProxy.getInstance();
        mBleDataProxy = BleDataProxy.getInstance();
        mBleDataProxy.setRecordingStarted();

        mCclothDeviceType = mLeProxy.getClothDeviceType();
        Log.i(TAG,"mCclothDeviceType:"+mCclothDeviceType);

    }

    private final BroadcastReceiver mchargeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                Log.i(TAG,"onReceive:"+intent.getAction());
                int calCuelectricVPercent = intent.getIntExtra("clothCurrBatteryPowerPercent", -1);
                Log.i(TAG,"clothCurrBatteryPowerPercent:"+calCuelectricVPercent);
                if (calCuelectricVPercent==-1){
                    //设备已断开
                    tv_base_charge.setVisibility(View.GONE);
                }
                else {
                    tv_base_charge.setVisibility(View.VISIBLE);
                    tv_base_charge.setText(calCuelectricVPercent+"%");
                }
            }
        }
    };

    private void initData() {

    }

    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case LeProxy.ACTION_GATT_CONNECTED:
                    Log.i(TAG,"已连接 " );
                    iv_base_connectedstate.setImageResource(R.drawable.yilianjie);
                    break;
                case LeProxy.ACTION_GATT_DISCONNECTED:
                    Log.w(TAG,"已断开 ");
                    iv_base_connectedstate.setImageResource(R.drawable.duankai);
                    break;
                case LeProxy.ACTION_CONNECT_ERROR:
                    Log.w(TAG,"连接异常 ");
                    iv_base_connectedstate.setImageResource(R.drawable.duankai);
                    break;
                case LeProxy.ACTION_CONNECT_TIMEOUT:
                    Log.w(TAG,"连接超时 ");
                    iv_base_connectedstate.setImageResource(R.drawable.duankai);
                    break;
                case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                    //Log.i(TAG,"接收到从机数据");
                    /*if (isonResumeEd){
                        byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);
                        dealwithLebDataChange(DataUtil.byteArrayToHex(data));
                    }*/
                    dealwithLebDataChange(intent);
                    break;
            }
        }

    };

    private void dealwithLebDataChange(Intent intent) {
        int[] intArrayExtra = intent.getIntArrayExtra(BleDataProxy.EXTRA_ECG_DATA);
        int heartRate = intent.getIntExtra(BleDataProxy.EXTRA_HEART_DATA,-1);

        if (intArrayExtra!=null && intArrayExtra.length==EcgAccDataUtil.ecgOneGroupLength){
            Log.i(TAG,"heartRate:"+ Arrays.toString(intArrayExtra));
            dealWithEcgData(intArrayExtra);
        }
        else if (heartRate!=-1){
            Log.i(TAG,"heartRate:"+heartRate);
            updateUIECGHeartData(heartRate);
        }
    }

    //处理心电数据
    private void dealWithEcgData(int[] ecgData) {
        if (isActivityFinsh) return;

        if (isNeedDrawEcgData){
            if (startTimeMillis==-1){
                startTimeMillis = System.currentTimeMillis();
            }
            updateUIECGLineData(ecgData);
        }
    }

    private void updateUIECGHeartData(int heartRate) {
        updateNotify(heartRate);
        mBleDataProxy.updateHeartUI(heartRate,tv_healthydata_rate);
        heartRateDates.add(heartRate);
    }

    private void updateNotify(int heartRate) {
        String showHeartString = heartRate==0?"--":heartRate+"";
        if (!mIsLookupECGDataFromSport){
            CommunicateToBleService.setServiceForegrounByNotify("正在测试静态心率","心率："+showHeartString+" BPM",0);
        }
    }

    private void updateUIECGLineData(int[] ecgIntsForLine) {
        pv_healthydata_path.addEcgOnGroupData(ecgIntsForLine);
    }

    public void adjustLine(View view) {
        alertAdjustLineSeekBar();
    }

    private void alertAdjustLineSeekBar() {
        EcgAccDataUtil.showAlertAdjustLineSeekBar(pv_healthydata_path,getApplication());
    }

    //求助，暂时只发短信
    public void startSoS(View view) {
        SosSendUtil.startSoS(getApplicationContext());
    }

    //开始分析
    public void startAnalysis(View view) {
        Log.i(TAG,"startAnalysis");
        jumpToAnalysis();
    }

    private void jumpToAnalysis() {
        Log.i(TAG,"heartRateDates.size(): "+heartRateDates.size());
        Log.i(TAG,"heartRateDates: "+heartRateDates);

        boolean needAnalysis = isNeedAnalysis();

        if (needAnalysis){
            String ecgLocalFileName = mBleDataProxy.stopWriteEcgToFileAndGetFileName();

            Intent intent = new Intent(HealthyDataActivity.this, HeartRateAnalysisActivity.class);
            intent.putExtra(Constant.sportState,Constant.SPORTSTATE_STATIC);
            intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
            intent.putExtra(Constant.startTimeMillis,startTimeMillis);
            intent.putExtra(Constant.ecgLocalFileName, ecgLocalFileName);

            startActivity(intent);

            isNeedDrawEcgData = false;
            isActivityFinsh = true;
            heartRateDates.clear();
            finish();
        }
        else {
            MyUtil.showToask(this,R.string.HeartRate_suggetstion_nodata);
        }
    }

    //判断是否有数据，现在有一个正常心率则表示可以分析了
    private boolean isNeedAnalysis() {
        boolean needAnalysis = false;
        for (int i:heartRateDates){
            if (i>40){
                needAnalysis  =true;
                break;
            }
        }
        return needAnalysis;
    }

    boolean isonResumeEd ;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
        isNeedDrawEcgData = true;

        if (!isonResumeEd){
            if (MainActivity.mBluetoothAdapter!=null && !MainActivity.mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
            }
            isonResumeEd = true;
            //pv_healthydata_path.startThread();
        }

        if (MyApplication.isHaveDeviceConnectted){
            iv_base_connectedstate.setImageResource(R.drawable.yilianjie);
        }
        else {
            iv_base_connectedstate.setImageResource(R.drawable.duankai);
        }
        registerSmsReciver();
    }

    private void registerSmsReciver() {
        /* 自定义IntentFilter为SENT_SMS_ACTIOIN Receiver */
        IntentFilter mFilter01;
        mFilter01 = new IntentFilter(SmsReceiver.SMS_SEND_ACTIOIN);
        mReceiver01 = new SmsReceiver();
        registerReceiver(mReceiver01, mFilter01);

        /* 自定义IntentFilter为DELIVERED_SMS_ACTION Receiver */
        mFilter01 = new IntentFilter(SmsReceiver.SMS_DELIVERED_ACTION);
        mReceiver02 = new SmsReceiver();
        registerReceiver(mReceiver02, mFilter01);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause");
        isNeedDrawEcgData = false;

        /* 取消注册自定义Receiver */

        unregisterReceiver(mReceiver01);
        unregisterReceiver(mReceiver02);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");

        MyApplication.runningActivity = MyApplication.MainActivity;

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
        if (heartRateDates.size()>0){
            CommunicateToBleService.detoryServiceForegrounByNotify();
        }

        unregisterReceiver(mchargeReceiver);
    }

    //按返回键时的处理
    private void backJudge() {
        if (heartRateDates.size()>0 && !mIsLookupECGDataFromSport){
            ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(HealthyDataActivity.this);
            chooseAlertDialogUtil.setAlertDialogText(getResources().getString(R.string.testing_ecg_quit));
            chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
                @Override
                public void onConfirmClick() {
                    finish();
                }
            });
        }
        else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        backJudge();
        return super.onKeyDown(keyCode, event);
    }

}