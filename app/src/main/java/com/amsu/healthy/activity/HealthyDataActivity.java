package com.amsu.healthy.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amsu.bleinteraction.bean.MessageEvent;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.proxy.BleDataProxy;
import com.amsu.bleinteraction.utils.EcgAccDataUtil;
import com.amsu.healthy.R;
import com.amsu.healthy.receiver.SmsReceiver;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HeartShowWayUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.ShowNotificationBarUtil;
import com.amsu.healthy.utils.SosSendUtil;
import com.amsu.healthy.view.EcgView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

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

    private BleDataProxy mBleDataProxy;
    private BleConnectionProxy mBleConnectionProxy;
    private TextView tv_healthydata_analysis;

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
        tv_healthydata_analysis = (TextView) findViewById(R.id.tv_healthydata_analysis);

        iv_base_connectedstate = (ImageView) findViewById(R.id.iv_base_connectedstate);
        iv_base_connectedstate.setVisibility(View.VISIBLE);
        tv_base_charge = (TextView) findViewById(R.id.tv_base_charge);
    }

    private void initData() {
        Intent intent = getIntent();

        mBleDataProxy = BleDataProxy.getInstance();
        mBleConnectionProxy = BleConnectionProxy.getInstance();

        mIsLookupECGDataFromSport = intent.getBooleanExtra(Constant.isLookupECGDataFromSport, false);
        if (mIsLookupECGDataFromSport){
            tv_healthydata_analysis.setVisibility(View.GONE);
            getTv_base_centerText().setVisibility(View.GONE);
        }
        else {
            mBleDataProxy.setRecordingStarted();
        }

        EventBus.getDefault().register(this);

        heartRateDates = new ArrayList<>();

        int clothCurrBatteryPowerPercent = BleConnectionProxy.getInstance().getClothCurrBatteryPowerPercent();

        if (clothCurrBatteryPowerPercent !=-1){
            tv_base_charge.setVisibility(View.VISIBLE);
            tv_base_charge.setText(String.valueOf(clothCurrBatteryPowerPercent));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart:");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.messageType){
            case BleConnectionProxy.msgType_HeartRate:
                updateUIECGHeartData(event.singleValue);
                break;
            case BleConnectionProxy.msgType_ecgDataArray:
                dealWithEcgData(event.dataArray);
                break;
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
        HeartShowWayUtil.updateHeartUI(heartRate,tv_healthydata_rate,this);
        //tv_healthydata_rate.setText(heartRate+"");
        heartRateDates.add(heartRate);
    }

    private void updateNotify(int heartRate) {
        String showHeartString = heartRate==0?"--":heartRate+"";
        if (!mIsLookupECGDataFromSport){
            ShowNotificationBarUtil.setServiceForegrounByNotify("正在测试静态心率","心率："+showHeartString+" BPM",ShowNotificationBarUtil.notifyActivityIndex_HealthyDataActivity);
        }
    }

    private void updateUIECGLineData(int[] ecgIntsForLine) {
        pv_healthydata_path.addEcgOnGroupData(ecgIntsForLine);
    }

    public void adjustLine(View view) {
        alertAdjustLineSeekBar();
    }

    private void alertAdjustLineSeekBar() {
        showAlertAdjustLineSeekBar(pv_healthydata_path,this);
    }

    //求助，暂时只发短信
    public void startSoS(View view) {
        SosSendUtil.startSoS(this);
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
            String[] fileNames = mBleDataProxy.stopWriteEcgToFileAndGetFileName();

            Intent intent = new Intent(HealthyDataActivity.this, HeartRateAnalysisActivity.class);
            intent.putExtra(Constant.sportState,Constant.SPORTSTATE_STATIC);
            intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
            intent.putExtra(Constant.startTimeMillis,startTimeMillis);
            intent.putExtra(Constant.ecgLocalFileName, fileNames[0]);

            startActivity(intent);

            isNeedDrawEcgData = false;
            isActivityFinsh = true;
            heartRateDates.clear();
            ShowNotificationBarUtil.detoryServiceForegrounByNotify();
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

        if (mBleConnectionProxy.ismIsConnectted()){
            iv_base_connectedstate.setImageResource(R.drawable.yilianjie);
            if (mBleConnectionProxy.getClothCurrBatteryPowerPercent() !=-1){
                tv_base_charge.setVisibility(View.VISIBLE);
                tv_base_charge.setText(String.valueOf(mBleConnectionProxy.getClothCurrBatteryPowerPercent()+"%"));
            }
        }
        else {
            iv_base_connectedstate.setImageResource(R.drawable.duankai);
            tv_base_charge.setVisibility(View.GONE);
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

        EventBus.getDefault().unregister(this);

        if (heartRateDates.size()>0){
            ShowNotificationBarUtil.detoryServiceForegrounByNotify();
        }

    }

    //按返回键时的处理
    private void backJudge() {
        if (heartRateDates.size()>0 && !mIsLookupECGDataFromSport){
            ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(HealthyDataActivity.this);
            chooseAlertDialogUtil.setAlertDialogText(getResources().getString(R.string.testing_ecg_quit));
            chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
                @Override
                public void onConfirmClick() {
                    ShowNotificationBarUtil.detoryServiceForegrounByNotify();
                    mBleDataProxy.stopWriteEcgToFileAndGetFileName();
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


    private  BottomSheetDialog mBottomAdjustRateLineDialog;

    public  void showAlertAdjustLineSeekBar(final EcgView pv_healthydata_path, Context context) {
        if (mBottomAdjustRateLineDialog==null){
            mBottomAdjustRateLineDialog = new BottomSheetDialog(context);
            View inflate = LayoutInflater.from(context).inflate(R.layout.view_adjustline, null);

            mBottomAdjustRateLineDialog.setContentView(inflate);
            Window window = mBottomAdjustRateLineDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.mystyle);  //添加动画

            SeekBar sb_adjust = (SeekBar) inflate.findViewById(R.id.sb_adjust);
            sb_adjust.setMax(80);  //设置最大值，分成4个级别，0-20,20-40,40-60,60-80

            sb_adjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.i(TAG,"onProgressChanged:"+progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.i(TAG,"onStart:"+seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.i(TAG,"onStop:"+seekBar.getProgress());
                    int endProgress = seekBar.getProgress();
                    adjustRateLineRToEcgView(endProgress,pv_healthydata_path);
                }
            });
        }
        mBottomAdjustRateLineDialog.show();
    }

    /*根据进度给心电View设置放大的倍数
            *  可以在 ecgAmpSum < 5时 放大4倍
                在 5<=ecgAmpSum<12 时放大2倍
                在 12<=ecgAmpSum<26 时 不放大大不缩小。
                在ecgAmpSum>=26时 缩小两倍
            * */
    public  void adjustRateLineRToEcgView(int endProgress,EcgView pv_healthydata_path) {
        double type = 0;
        Log.i(TAG,"currentType:"+EcgAccDataUtil.ECGSCALE_MODE_CURRENT);
        if (endProgress<=20){
            type = EcgAccDataUtil.ECGSCALE_MODE_HALF;
        }
        else if(20<endProgress && endProgress<=40){
            type = EcgAccDataUtil.ECGSCALE_MODE_ORIGINAL;
        }
        else if(40<endProgress && endProgress<=60){
            type = EcgAccDataUtil.ECGSCALE_MODE_DOUBLE;
        }
        else if(60<endProgress && endProgress<=80){
            type = EcgAccDataUtil.ECGSCALE_MODE_QUADRUPLE;
        }

        if (type!=EcgAccDataUtil.ECGSCALE_MODE_CURRENT){
            EcgAccDataUtil.ECGSCALE_MODE_CURRENT = type;
            //重新绘图
            Log.i(TAG,"调在增益");
            pv_healthydata_path.setRateLineR(type);
        }
    }

}