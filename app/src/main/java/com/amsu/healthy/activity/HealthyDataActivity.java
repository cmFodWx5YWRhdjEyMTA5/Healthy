package com.amsu.healthy.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.AppAbortDbAdapter;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.EcgFilterUtil_1;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.EcgView;
import com.ble.api.DataUtil;
import com.google.gson.Gson;
import com.test.utils.DiagnosisNDK;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

public class HealthyDataActivity extends BaseActivity {

    private static final String TAG = "HealthyDataActivity";
    private static final String TAG1 = "startSoS";

    private EcgView pv_healthydata_path;
    private FileOutputStream fileOutputStream;
    public static int calGroupCalcuLength = 180; //
    public static int timeSpanGgroupCalcuLength = 60; //
    public static int oneGroupLength = 10; //
    public  int[] calcuEcgRate = new int[calGroupCalcuLength *oneGroupLength]; //1000条数据:（100组，一组有10个数据点）
    private int[] preCalcuEcgRate = new int[calGroupCalcuLength*oneGroupLength]; //前一次数的数据，12s
    private int[] fourCalcuEcgRate = new int[timeSpanGgroupCalcuLength*oneGroupLength]; //4s的数据*/

    /*private int preGroupCalcuLength = 12*15; //有多少组数据就进行计算心率，12s一次，每秒15次，共12*15组
    private int fourGroupCalcuLength = 4*15; //有多少组数据就进行更新，4s更新一次，每秒15次，共4*15组
    private int[] preCalcuEcgRate = new int[preGroupCalcuLength*10]; //前一次数的数据，12s
    private int[] currCalcuEcgRate = new int[preGroupCalcuLength*10]; //当前的数据，12s
    private int[] fourCalcuEcgRate = new int[fourGroupCalcuLength*10]; //4s的数据*/
    private boolean isFirstCalcu = true;  //是否是第一次计算心率，第一次要连续12秒的数据
    private int currentGroupIndex = 0;   //组的索引

    private TextView tv_healthydata_rate;
    private ArrayList<Integer> heartRateDates ;  // 心率数组
    private TextView tv_healthdaydata_adjust;
    HashMap<Double, Integer> mRateLineRItemCount = new HashMap<>();  //计算波峰时不同区间出现的次数，
    private int mCalcutRateLineRCount=0;  //心率曲线R波峰计数次数，在每次调整后将计数设为0
    private boolean isStartAdjustLineTimeTask;  //是否开始调整心率曲线R波峰
    private MyTimeTask mAdjustLineTimeTask; //文字闪烁计时器
    private boolean isFirstAdjust = true;
    private boolean isNeedDrawEcgData = true; //是否要画心电数据，在跳到下个界面时则不需要画
    private boolean isActivityFinsh = false; //是否要画心电数据，在跳到下个界面时则不需要画
    private DataOutputStream dataOutputStream;  //二进制文件输出流，写入文件
    private ByteBuffer byteBuffer;
    private long ecgFiletimeMillis =-1;  //开始有心电数据时的秒数，作为心电文件命名。静态变量，在其他界面会用到

    private static double ECGSCALE_MODE_HALF = 0.5;
    private static double ECGSCALE_MODE_ORIGINAL = 1;
    private static double ECGSCALE_MODE_DOUBLE = 2;
    private static double ECGSCALE_MODE_QUADRUPLE = 4;

    public static double ECGSCALE_MODE_CURRENT = ECGSCALE_MODE_ORIGINAL;

    private BottomSheetDialog mBottomAdjustRateLineDialog;
    private boolean isLookupECGDataFromSport;

    private ServiceReceiver mReceiver01, mReceiver02;
    private static String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    private static String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";
    private int mCurrentHeartRate= 0;
    private String ecgLocalFileName;
    private int D_valueMaxValue = 15;
    private ImageView iv_base_connectedstate;
    private TextView tv_base_charge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_data);
        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText("健康数据");
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
        tv_healthdaydata_adjust = (TextView) findViewById(R.id.tv_healthdaydata_adjust);
        TextView tv_healthydata_analysis = (TextView) findViewById(R.id.tv_healthydata_analysis);

        iv_base_connectedstate = (ImageView) findViewById(R.id.iv_base_connectedstate);
        iv_base_connectedstate.setVisibility(View.VISIBLE);
        tv_base_charge = (TextView) findViewById(R.id.tv_base_charge);

        mRateLineRItemCount.put(ECGSCALE_MODE_HALF,0);
        mRateLineRItemCount.put(ECGSCALE_MODE_ORIGINAL,0);
        mRateLineRItemCount.put(ECGSCALE_MODE_DOUBLE,0);
        mRateLineRItemCount.put(ECGSCALE_MODE_QUADRUPLE,0);



        Intent intent = getIntent();
        if (intent!=null){
            isLookupECGDataFromSport = intent.getBooleanExtra(Constant.isLookupECGDataFromSport, false);
            if (isLookupECGDataFromSport){
                tv_healthydata_analysis.setVisibility(View.GONE);
            }
        }

        heartRateDates = new ArrayList<>();
        MyApplication.runningActivity = MyApplication.HealthyDataActivity;



        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_CHARGE_CHANGE);
        registerReceiver(mchargeReceiver, filter);

        if (MyApplication.calCuelectricVPercent!=-1){
            tv_base_charge.setVisibility(View.VISIBLE);
            tv_base_charge.setText(MyApplication.calCuelectricVPercent+"%");
        }


        //LocalBroadcastManager.getInstance(HealthyDataActivity.this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());
        new Thread(){
            @Override
            public void run() {
                super.run();
                LocalBroadcastManager.getInstance(HealthyDataActivity.this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());
                /*try {
                    Thread.sleep(1000);
                    LocalBroadcastManager.getInstance(HealthyDataActivity.this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        }.start();
    }

    private final BroadcastReceiver mchargeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                Log.i(TAG,"onReceive:"+intent.getAction());
                int calCuelectricVPercent = intent.getIntExtra("calCuelectricVPercent", -1);
                Log.i(TAG,"calCuelectricVPercent:"+calCuelectricVPercent);
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

    //按返回键时的处理
    private void backJudge() {
        if (mIsHaveEcgDataReceived && !isLookupECGDataFromSport){
            ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(HealthyDataActivity.this);
            chooseAlertDialogUtil.setAlertDialogText("正在测试心电，是否退出？","按错了","退出测试");
            chooseAlertDialogUtil.setOnCancelClickListener(new ChooseAlertDialogUtil.OnCancelClickListener() {
                @Override
                public void onCancelClick() {
                    //将离线数据记录删除
                    /*List<AppAbortDataSave> abortDataListFromSP = AppAbortDbAdapter.getAbortDataListFromSP();
                    if (abortDataListFromSP!=null && abortDataListFromSP.size()>0){
                        abortDataListFromSP.remove(abortDataListFromSP.size()-1); // 删除最后一个（刚年纪大饿一条记录）
                    }
                    AppAbortDbAdapter.putAbortDataListToSP(abortDataListFromSP);*/
                    finish();
                }
            });
        }
        else {
            finish();
        }
    }

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
                    if (isonResumeEd){
                        byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);
                        dealwithLebDataChange(DataUtil.byteArrayToHex(data));
                    }
                    break;
            }
        }
    };

    //关闭心电数据传输
    public static void stopTransmitData(){

    }

    private void dealwithLebDataChange(String hexData) {
        if (hexData.length()<40){
            return;
        }

        if(hexData.startsWith("FF 83 0F")){
            //心电数据
            //Log.i(TAG,"心电hexData:"+hexData);
            dealWithEcgData(hexData);
            mIsHaveEcgDataReceived = true;
        }
        /*else if(hexData.startsWith("FF 86 11")){
            //加速度数据
            //Log.i(TAG,"加速度hexData:"+hexData);
            //dealWithAccelerationgData(hexData);
        }*/
    }

    private boolean mIsHaveEcgDataReceived;
    private int mPreHeartRate;
    private boolean isNeedUpdateHeartRate = false;

    //处理心电数据
    private void dealWithEcgData(String hexData) {
        if (isActivityFinsh) return;
        isNeedUpdateHeartRate = false;
        final int [] ints = ECGUtil.geIntEcgaArr(hexData, " ", 3, 10); //一次的数据，10位
        if (!isLookupECGDataFromSport){
            writeEcgDataToBinaryFile(ints);
        }

        //滤波处理
        for (int i=0;i<ints.length;i++){
            ints[i] = EcgFilterUtil_1.miniEcgFilterLp(EcgFilterUtil_1.miniEcgFilterHp (EcgFilterUtil_1.NotchPowerLine(ints[i], 1)));
            /*int temp = EcgFilterUtil_1.miniEcgFilterLp(ints[i], 0);
            temp = EcgFilterUtil.miniEcgFilterHp(temp, 0);
            ints[i] = temp;*/
            //Log.i(TAG,"temp:"+ints[i]);
        }

        //Log.i(TAG,"currentGroupIndex:"+currentGroupIndex);

        if (isFirstCalcu){
            if (currentGroupIndex< calGroupCalcuLength){
                //未到时间（1800个数据点计算一次心率）
                System.arraycopy(ints, 0, calcuEcgRate, currentGroupIndex * oneGroupLength, ints.length);
            }
            else{
                isNeedUpdateHeartRate = true;
                isFirstCalcu = false;
            }
        }
        else {
            if (currentGroupIndex<timeSpanGgroupCalcuLength){
                //未到4s
                System.arraycopy(ints, 0, fourCalcuEcgRate, currentGroupIndex * oneGroupLength, ints.length);
            }
            else {
                //到4s,需要前8s+当前4s

                int i=0;
                for (int j=timeSpanGgroupCalcuLength*oneGroupLength;j<preCalcuEcgRate.length;j++){
                    calcuEcgRate[i++] = preCalcuEcgRate[j];
                }
                System.arraycopy(fourCalcuEcgRate, 0, calcuEcgRate, i, fourCalcuEcgRate.length);
                isNeedUpdateHeartRate = true;


               /* String data1 = "";
                for (int j=0;j<calcuEcgRate.length;j++){
                    data1 += calcuEcgRate[j]+",";
                }
                Log.i(TAG,"data1:"+data1);
                Log.i(TAG,"currCalcuEcgRate.length:"+calcuEcgRate.length);*/

                //带入公式，计算心率
                /*mCurrentHeartRate = ECGUtil.countEcgRate(calcuEcgRate, calcuEcgRate.length, 150);
                Log.i(TAG,"mCurrentHeartRate:"+mCurrentHeartRate);
                //更新心率
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_healthydata_rate.setText(mCurrentHeartRate+"");
                    }
                });



                for (int n=0;n<calcuEcgRate.length;n++){
                    preCalcuEcgRate[n] = calcuEcgRate[n];
                }

                for (int j=0;j<ints.length;j++){
                    fourCalcuEcgRate[currentGroupIndex*10+j] = ints[j];
                }*/
            }
        }


        currentGroupIndex++;
        if (isNeedUpdateHeartRate){
            currentGroupIndex = 0;
            //计算、更新心率，到4s
            mCurrentHeartRate = DiagnosisNDK.ecgHeart(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
            Log.i(TAG,"mCurrentHeartRate:"+ mCurrentHeartRate);
            //calcuEcgRate = new int[calGroupCalcuLength*10];
            heartRateDates.add(mCurrentHeartRate);



            if (!isLookupECGDataFromSport && heartRateDates.size()==1){
                //saveAbortDatareordToSP(ecgFiletimeMillis,ecgLocalFileName,0);
            }

            //更新心率
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCurrentHeartRate ==0){
                        tv_healthydata_rate.setText("--");
                        if (!isLookupECGDataFromSport){
                            CommunicateToBleService.setServiceForegrounByNotify("正在测试静态心率","心率：--"+" BPM",0);
                        }
                    }
                    else if (mPreHeartRate>0){
                        int count = 0;
                        int temp = mCurrentHeartRate-mPreHeartRate;
                        if (temp>D_valueMaxValue) {
                            count = (temp) / D_valueMaxValue + 1;
                        }
                        else if (temp<-D_valueMaxValue){
                            count = (temp) / D_valueMaxValue - 1;
                        }
                        System.out.println(count);
                        if (count!=0){
                            mCurrentHeartRate = mPreHeartRate + Math.abs(temp)/count;
                        }

                        tv_healthydata_rate.setText(mCurrentHeartRate +"");
                        if (!isLookupECGDataFromSport){
                            CommunicateToBleService.setServiceForegrounByNotify("正在测试静态心率","心率："+mCurrentHeartRate+" BPM",0);
                        }
                    }

                    MyApplication.currentHeartRate = mCurrentHeartRate;
                }
            });

            //int ecgAmpSum = ECGUtil.countEcgR(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
            //Log.i(TAG,"calcuEcgRate.length:"+calcuEcgRate.length);
            //Log.i(TAG,"ecgAmpSum:"+ecgAmpSum);
            //setReteLineR(ecgAmpSum);
            mPreHeartRate = mCurrentHeartRate;

            System.arraycopy(calcuEcgRate, 0, preCalcuEcgRate, 0, calcuEcgRate.length);
        }

        if (isNeedDrawEcgData){
            //绘图
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.i(TAG,"绘图");
                    String intString = "";
                    for (int i:ints){
                        intString+=i+",";
                    }
                    //Log.i(TAG,"intString:"+intString);
                    pv_healthydata_path.addEcgOnGroupData(ints);
                }
            });
        }


        /*if (currentGroupIndex< calGroupCalcuLength){
            //未到时间（1000个数据点计算一次心率）
            System.arraycopy(ints, 0, calcuEcgRate, currentGroupIndex * oneGroupLength, ints.length);
        }
        else{
            currentGroupIndex = 0;
            String data0 = "";
            for (int aCalcuEcgRate : calcuEcgRate) {
                data0 += aCalcuEcgRate + ",";
            }

            //Log.i(TAG,"data:"+data0);
            //Log.i(TAG,"calcuEcgRate.length:"+calcuEcgRate.length);

            //带入公式，计算心率
            //mCurrentHeartRate = ECGUtil.countEcgRate(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
            mCurrentHeartRate = DiagnosisNDK.ecgHeart(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);



            Log.i(TAG,"mCurrentHeartRate:"+ mCurrentHeartRate);
            //calcuEcgRate = new int[calGroupCalcuLength*10];
            heartRateDates.add(mCurrentHeartRate);

            if (!isLookupECGDataFromSport && heartRateDates.size()==1){
                //saveAbortDatareordToSP(ecgFiletimeMillis,ecgLocalFileName,0);
            }

            //更新心率
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCurrentHeartRate ==0){
                        tv_healthydata_rate.setText("--");
                        if (!isLookupECGDataFromSport){
                            CommunicateToBleService.setServiceForegrounByNotify("正在测试静态心率","心率：--"+" BPM",0);
                        }
                    }
                    else if (mPreHeartRate>0){
                        *//*int count = 0;
                        int temp = mCurrentHeartRate-mPreHeartRate;
                        if (temp>D_valueMaxValue) {
                            count = (temp) / D_valueMaxValue + 1;
                        }
                        else if (temp<-D_valueMaxValue){
                            count = (temp) / D_valueMaxValue - 1;
                        }
                        System.out.println(count);
                        if (count!=0){
                            mCurrentHeartRate = mPreHeartRate + Math.abs(temp)/count;
                        }*//*

                        tv_healthydata_rate.setText(mCurrentHeartRate +"");
                        if (!isLookupECGDataFromSport){
                            CommunicateToBleService.setServiceForegrounByNotify("正在测试静态心率","心率："+mCurrentHeartRate+" BPM",0);
                        }
                    }

                    MyApplication.currentHeartRate = mCurrentHeartRate;
                }
            });
            System.arraycopy(ints, 0, calcuEcgRate, currentGroupIndex * 10 + 0, ints.length);

            int ecgAmpSum = ECGUtil.countEcgR(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
            //Log.i(TAG,"calcuEcgRate.length:"+calcuEcgRate.length);
            //Log.i(TAG,"ecgAmpSum:"+ecgAmpSum);
            //setReteLineR(ecgAmpSum);
            mPreHeartRate = mCurrentHeartRate;
        }
        currentGroupIndex++;
        if (isNeedDrawEcgData){
            //绘图
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.i(TAG,"绘图");
                    String intString = "";
                    for (int i:ints){
                        intString+=i+",";
                    }
                    Log.i(TAG,"intString:"+intString);
                    pv_healthydata_path.addEcgOnGroupData(ints);
                }
            });
        }*/
    }

    //写到文件里，二进制方式写入
    private void writeEcgDataToBinaryFile(int[] ints) {
        try {
            if (fileOutputStream==null){
                ecgFiletimeMillis = System.currentTimeMillis();
                //String filePath = MyUtil.generateECGFilePath(HealthyDataActivity.this, ecgFiletimeMillis); //随机生成一个ecg格式文件
                //String filePath = getCacheDir()+"/"+MyUtil.getECGFileNameDependFormatTime(new Date())+".ecg";  //随机生成一个文件
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/abluedata";
                File file = new File(filePath);
                if (!file.exists()) {
                    boolean mkdirs = file.mkdirs();
                    Log.i(TAG,"mkdirs:"+mkdirs);
                }
                String fileAbsolutePath = filePath+"/"+MyUtil.getECGFileNameDependFormatTime(new Date())+".ecg";
                Log.i(TAG,"fileAbsolutePath:"+fileAbsolutePath);
                fileOutputStream = new FileOutputStream(fileAbsolutePath,true);
                //MyUtil.putStringValueFromSP("cacheFileName",fileAbsolutePath);
                ecgLocalFileName = fileAbsolutePath;
                dataOutputStream = new DataOutputStream(fileOutputStream);
                byteBuffer = ByteBuffer.allocate(2);
                heartRateDates.clear();

            }
            for (int anInt : ints) {
                byteBuffer.clear();
                byteBuffer.putShort((short) anInt);
                dataOutputStream.writeByte(byteBuffer.get(1));
                dataOutputStream.writeByte(byteBuffer.get(0));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //异常中断时数据保存在本地
    private void saveAbortDatareordToSP(long ecgFiletimeMillis,String ecgLocalFileName, int state) {
        List<AppAbortDataSave> abortDataListFromSP = AppAbortDbAdapter.getAbortDataListFromSP();
        AppAbortDataSave abortData = new AppAbortDataSave(ecgFiletimeMillis,ecgLocalFileName,state);
        abortDataListFromSP.add(abortData);
        AppAbortDbAdapter.putAbortDataListToSP(abortDataListFromSP);
    }

    private void deleteAbortDataRecordFomeSP(long ecgFiletimeMillis){
        if (ecgFiletimeMillis!=-1){
            List<AppAbortDataSave> abortDataListFromSP = AppAbortDbAdapter.getAbortDataListFromSP();
            if (abortDataListFromSP.size()>0){
                abortDataListFromSP.remove(abortDataListFromSP.size()-1);
                AppAbortDbAdapter.putAbortDataListToSP(abortDataListFromSP);
            }
        }
    }

    //处理加速度数据
    private void dealWithAccelerationgData(String hexData) {
        final int [] ints = ECGUtil.geIntEcgaArr(hexData, " ", 3, 12); //一次的数据，12位
        //FF 42 04 77 0F 93 FF 26 04 74 0F 47
    }

    //根据当前计算出来的增益来调整心电曲线波峰
    private void setReteLineR(int ecgAmpSum) {
        /*
        *   在 ecgAmpSum < 5时 放大4倍
            在 5<=ecgAmpSum<12 时放大2倍
            在 12<=ecgAmpSum<26 时 不放大大不缩小。
            在ecgAmpSum>=26时 缩小两倍*/

        mCalcutRateLineRCount++;
        if (ecgAmpSum>=26){
            mRateLineRItemCount.put(ECGSCALE_MODE_HALF,mRateLineRItemCount.get(ECGSCALE_MODE_HALF)+1);
        }
        else if (12<=ecgAmpSum && ecgAmpSum<26){
            mRateLineRItemCount.put(ECGSCALE_MODE_ORIGINAL,mRateLineRItemCount.get(ECGSCALE_MODE_ORIGINAL)+1);
        }
        else if (5<=ecgAmpSum && ecgAmpSum<12){
            mRateLineRItemCount.put(ECGSCALE_MODE_DOUBLE,mRateLineRItemCount.get(ECGSCALE_MODE_DOUBLE)+1);
        }
        else if (ecgAmpSum<5){
            mRateLineRItemCount.put(ECGSCALE_MODE_QUADRUPLE,mRateLineRItemCount.get(ECGSCALE_MODE_QUADRUPLE)+1);
        }
        calcuAndAdjustRateLine();
    }

    //根据当前心率增益值计算是否需要调整曲线，需要的话讲‘调整’文字闪烁
    private void calcuAndAdjustRateLine() {
        if (mCalcutRateLineRCount>=5){
            boolean spangled = false;
            double adjustKey = -1;
            for (int i = 1; i <= 8; i=i*2) {
                double value = mRateLineRItemCount.get(0.5*i);
                if (mCalcutRateLineRCount<10 && value==mCalcutRateLineRCount){
                    spangled = true;  //当计数小于10时，某个键值对应的值和计相等时则调整
                    adjustKey = i;
                }
                if (mCalcutRateLineRCount>=10){
                    if (value/mCalcutRateLineRCount>0.7){  //当计数大于10时，某个键值对应的值/总数 大于0.7就开始调整
                        //开始
                        spangled = true;
                        adjustKey = 0.5*i;
                    }
                }
            }
            if (spangled){
                if (isFirstAdjust){
                    if (adjustKey != ECGSCALE_MODE_CURRENT){
                        startSpangleTextTimeTask();
                        isFirstAdjust = false;
                    }
                }else {
                    //startSpangleTextTimeTask();
                }
            }
            Log.i(TAG,"mCalcutRateLineRCount:"+mCalcutRateLineRCount+",spangled:"+spangled+",isFirstAdjust:"+isFirstAdjust);
            String a= "";
            for (int i=0;i<mRateLineRItemCount.size();i++){
                a += i+":"+mRateLineRItemCount.get(i)+",";
            }
            Log.i(TAG,"map:"+a);
        }
    }

    //需要调整增益，进行文字闪烁
    private void startSpangleTextTimeTask(){
        isStartAdjustLineTimeTask = true;
        if (mAdjustLineTimeTask==null){
            mAdjustLineTimeTask = new MyTimeTask(500, new TimerTask() {
                int clo;
                @Override
                public void run() {
                    if (!isStartAdjustLineTimeTask){
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (clo == 0) {
                                clo = 1;
                                tv_healthdaydata_adjust.setTextColor(Color.TRANSPARENT);
                            } else {
                                if (clo == 1) {
                                    clo = 2;
                                    tv_healthdaydata_adjust.setTextColor(Color.BLACK);
                                } else if (clo == 2) {
                                    clo = 0;
                                    tv_healthdaydata_adjust.setTextColor(Color.RED);
                                }
                            }
                        }
                    });
                }
            });
            mAdjustLineTimeTask.startTime();
        }
    }

    public void adjustLine(View view) {
        alertAdjustLineSeekBar();
    }

    private void alertAdjustLineSeekBar() {
        if (mBottomAdjustRateLineDialog==null){
            mBottomAdjustRateLineDialog = new BottomSheetDialog(HealthyDataActivity.this);
            View inflate = LayoutInflater.from(this).inflate(R.layout.view_adjustline, null);

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
                    adjustRateLineRToEcgView(endProgress);
                }
            });
        }
        mBottomAdjustRateLineDialog.show();
    }

    //根据进度给心电View设置放大的倍数
    private void adjustRateLineRToEcgView(int endProgress) {
        double type = 0;
                 /*
                *  可以在 ecgAmpSum < 5时 放大4倍
                    在 5<=ecgAmpSum<12 时放大2倍
                    在 12<=ecgAmpSum<26 时 不放大大不缩小。
                    在ecgAmpSum>=26时 缩小两倍
                * */
        Log.i(TAG,"currentType:"+ECGSCALE_MODE_CURRENT);
        if (endProgress<=20){
            type = ECGSCALE_MODE_HALF;
        }
        else if(20<endProgress && endProgress<=40){
            type = ECGSCALE_MODE_ORIGINAL;
        }
        else if(40<endProgress && endProgress<=60){
            type = ECGSCALE_MODE_DOUBLE;
        }
        else if(60<endProgress && endProgress<=80){
            type = ECGSCALE_MODE_QUADRUPLE;
        }

        if (type!=ECGSCALE_MODE_CURRENT){
            ECGSCALE_MODE_CURRENT = type;
            //重新绘图
            Log.i(TAG,"重新绘图");
            pv_healthydata_path.setRateLineR(type);
            mCalcutRateLineRCount = 0;
            isStartAdjustLineTimeTask = false;
        }
    }

    public void startSoS(View view) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
          /* 建立自定义Action常数的Intent(给PendingIntent参数之用) */
            Intent itSend = new Intent(SMS_SEND_ACTIOIN);
            Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);

          /* sentIntent参数为传送后接受的广播信息PendingIntent */
            PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, 0);

          /* deliveryIntent参数为送达后接受的广播信息PendingIntent */
            PendingIntent mDeliverPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itDeliver, 0);

          /* 发送SMS短信，注意倒数的两个PendingIntent参数 */

            List<SosActivity.SosNumber> sosNumberList = MyUtil.getSosNumberList();

            if (sosNumberList==null  || sosNumberList.size()==0){
                startActivity(new Intent(this,SosActivity.class));
                return;
            }
            String sosinfo = MyUtil.getStringValueFromSP(Constant.sosinfo);
            for (SosActivity.SosNumber sosNumber:sosNumberList){
                smsManager.sendTextMessage(sosNumber.phone, null, sosinfo, mSendPI, mDeliverPI);
            }
            MyUtil.showDialog("正在发送",this);
            Log.i(TAG,"sendTextMessage");
        }
        catch(Exception e) {
            Log.e(TAG,"e:"+e);
        }
    }

    //开始分析
    public void startAnalysis(View view) {
        Log.i(TAG,"startAnalysis");

        jumpToAnalysis();
    }

    private void jumpToAnalysis() {
        //saveHeartRateDatesToSP(heartRateDates);
        Intent intent = new Intent(HealthyDataActivity.this, HeartRateActivity.class);
        intent.putExtra(Constant.sportState,Constant.SPORTSTATE_STATIC);
        Log.i(TAG,"heartRateDates.size(): "+heartRateDates.size());
        Log.i(TAG,"heartRateDates: "+heartRateDates);

        if (heartRateDates.size()>0){
            intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
            intent.putExtra(Constant.ecgFiletimeMillis,ecgFiletimeMillis);
            intent.putExtra(Constant.ecgLocalFileName, ecgLocalFileName);
        }
        else {
            MyUtil.showToask(this,"采集数据不足");
            return;
        }

        //deleteAbortDataRecordFomeSP(ecgFiletimeMillis);


        startActivity(intent);

        isNeedDrawEcgData = false;
        isActivityFinsh = true;
        isStartAdjustLineTimeTask = false;
        fileOutputStream = null;
        heartRateDates.clear();
        finish();
    }

    //将心率数组保存到sp里
    private void saveHeartRateDatesToSP(List<Integer> heartRateDates) {
        String heartData = "";
        if (heartRateDates.size()>0){
            Gson gson = new Gson();
            heartData = gson.toJson(heartRateDates);  //   [1,2,3]
            Log.i(TAG,"heartData:"+heartData);
        }
        MyUtil.putStringValueFromSP("heartData",heartData);
        heartRateDates.clear();
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
        }

        if (MyApplication.isHaveDeviceConnectted){
            iv_base_connectedstate.setImageResource(R.drawable.yilianjie);
        }
        else {
            iv_base_connectedstate.setImageResource(R.drawable.duankai);
        }

    /* 自定义IntentFilter为SENT_SMS_ACTIOIN Receiver */
        IntentFilter mFilter01;
        mFilter01 = new IntentFilter(SMS_SEND_ACTIOIN);
        mReceiver01 = new ServiceReceiver();
        registerReceiver(mReceiver01, mFilter01);

    /* 自定义IntentFilter为DELIVERED_SMS_ACTION Receiver */
        mFilter01 = new IntentFilter(SMS_DELIVERED_ACTION);
        mReceiver02 = new ServiceReceiver();
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

    public void stopEcgData(View view) {
        //stopTransmitData();
        //mLeService.disconnect(connecMac);
    }

    /* 自定义mServiceReceiver重写BroadcastReceiver监听短信状态信息 */
    public class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            //mTextView01.setText(intent.getAction().toString());
            if (intent.getAction().equals(SMS_SEND_ACTIOIN)) {
                try {
                    /* android.content.BroadcastReceiver.getResultCode()方法 */
                    //Retrieve the current result code, as set by the previous receiver.
                    switch(getResultCode()) {
                        case Activity.RESULT_OK:
                            /* 发送短信成功 */
                            //mTextView01.setText(R.string.str_sms_sent_success);
                            MyUtil.showToask(HealthyDataActivity.this,"发送短信成功");
                            MyUtil.showDialog("发送短信成功",HealthyDataActivity.this);
                            MyUtil.hideDialog();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            /* 发送短信失败 */
                            //mTextView01.setText(R.string.str_sms_sent_failed);
                            MyUtil.showToask(HealthyDataActivity.this,"发送短信失败 ");
                            MyUtil.showDialog("发送短信失败",HealthyDataActivity.this);
                            MyUtil.hideDialog();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            break;
                    }
                }
                catch(Exception e) {
                    e.getStackTrace();
                }
            }
            else if(intent.getAction().equals(SMS_DELIVERED_ACTION))
            {
                try
                {
                    /* android.content.BroadcastReceiver.getResultCode()方法 */
                    switch(getResultCode())
                    {
                        case Activity.RESULT_OK:
                            /* 短信 */
                            //mTextView01.setText(R.string.str_sms_sent_success);
                            //MyUtil.showToask(HealthyDataActivity.this,"短信");
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            /* 短信未送达 */
                            //mTextView01.setText(R.string.str_sms_sent_failed);
                            //MyUtil.showToask(HealthyDataActivity.this,"短信未送达");
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            break;
                    }
                }
                catch(Exception e) {
                    e.getStackTrace();
                }
            }
        }
    }

    public static String getDataHexString(){
        SimpleDateFormat formatter = new SimpleDateFormat("yy MM dd HH mm");
        Date curDate = new Date();
        String dateString = formatter.format(curDate);
        System.out.println(dateString);
        String[] split = dateString.split(" ");
        String dateHexString = "";
        for (String s:split){
            String hex = Integer.toHexString(Integer.parseInt(s));
            if (hex.length()==1){
                hex ="0"+hex;
            }
            dateHexString += hex;
        }
        //Log.i(TAG,"dateHexString:"+dateHexString);
        return dateHexString;
    }

    public static String getDataHexStringHaveScend(){
        SimpleDateFormat formatter = new SimpleDateFormat("yy MM dd HH mm ss");
        Date curDate = new Date();
        String dateString = formatter.format(curDate);
        System.out.println(dateString);
        String[] split = dateString.split(" ");
        String dateHexString = "";
        for (String s:split){
            String hex = Integer.toHexString(Integer.parseInt(s));
            if (hex.length()==1){
                hex ="0"+hex;
            }
            dateHexString += hex;
        }
        //Log.i(TAG,"dateHexString:"+dateHexString);
        return dateHexString;
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

        if (mIsHaveEcgDataReceived){
            CommunicateToBleService.detoryServiceForegrounByNotify();
        }

        unregisterReceiver(mchargeReceiver);

        /*if (MainActivity.mBluetoothAdapter!=null){
            MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
        }*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        backJudge();
        return super.onKeyDown(keyCode, event);
    }

}