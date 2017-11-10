package com.amsu.healthy.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Apk;
import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.ApkUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.wifiTramit.DeviceOffLineFileUtil;
import com.amsu.healthy.view.CircleRingView;
import com.amsu.healthy.view.DashboardView;
import com.ble.api.DataUtil;
import com.ble.ble.BleService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private ImageView iv_main_elf;
    private LinearLayout ll_main_floatcontent;
    public static BluetoothAdapter mBluetoothAdapter;
    public static final int REQUEST_ENABLE_BT = 2;
    private DashboardView dv_main_compass;
    private CircleRingView cv_mian_index;
    private CircleRingView cv_mian_warring;
    private ValueAnimator mValueAnimator;
    private TextView tv_main_age;
    private TextView tv_main_indexvalue;
    private int physicalAgeDValue;
    private int scoreALL;

    public static BleService mLeService;
    public static String connecMac;   //当前连接的蓝牙mac地址
    public static boolean isConnectted  =false;
    private boolean isConnectting  =false;

    private BaseActivity mActivity = this;
    private DeviceOffLineFileUtil deviceOffLineFileUtil;
    private ImageView iv_base_connectedstate;
    private TextView tv_base_charge;
    public static final String ACTION_CHARGE_CHANGE = "ACTION_CHARGE_CHANGE";
    private MyOnClickListener myOnClickListener;
    private ImageView iv_main_warring;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        Log.i(TAG,"onCreate");
        //mv_main_bmapView.onCreate(this,savedInstanceState);

        initView();
        initData();

        /*String text = MyUtil.encodeBase64String("haha");
        Log.i(TAG,"text:"+text);
        String decodeText = MyUtil.decodeBase64String(text);
        Log.i(TAG,"decodeText:"+decodeText);*/

        /*for (int i=0;i<10;i++){
            Message message = StartRunActivity.runEcgHandler.obtainMessage();
            String hexData = i+" xxxxxxxxxxxxxxxxxxx";
            message.obj = hexData;
            StartRunActivity.runEcgHandler.sendMessage(message);
        }*/




       /* new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Looper.prepare();
                                StartRunActivity startRunActivity = new StartRunActivity();
                                Handler runEcgHandlerInstance = startRunActivity.getRunEcgHandlerInstance();
                                Message message = runEcgHandlerInstance.obtainMessage();
                                message.obj = "xxxxxxxxxxxxxxxx";
                                runEcgHandlerInstance.sendMessage(message);
                                //Looper.loop();
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();*/

        /*View popupView = getLayoutInflater().inflate(R.layout.layout_popupwindow_onoffline, null);

        mPopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

        initHeadView();
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mPopupWindow.showAsDropDown(v);
                mPopupWindow.showAtLocation(getIv_base_leftimage(), Gravity.TOP,0,0);
            }
        });*/


        /*int[] test = new int[1800];
        for (int i=0;i<1800;i++){
            test[i] = 60+i%20;
        }
        for (int i=0;i<1800;i++){
            System.out.println(test[i]);
        }

        int currentHeartRate = DiagnosisNDK.ecgHeart(test, test.length, Constant.oneSecondFrame);

        Log.i(TAG,"currentHeartRate:"+currentHeartRate);*/

        /*int[] test = new int[180000];
        for (int i=0;i<180000;i++){
            test[i] = 60+i%20;
        }
        HeartRateResult heartRateResult = DiagnosisNDK.AnalysisEcg(test, test.length, Constant.oneSecondFrame);
        Log.i(TAG,"heartRateResult:"+heartRateResult.toString());*/

        //List<AppAbortDbAdapterUtil.AbortData> abortDataListFromSP = AppAbortDbAdapterUtil.getAbortDataListFromSP();
        //Log.i(TAG,"abortDataListFromSP:"+abortDataListFromSP.toString());

        //AppAbortDbAdapterUtil.putAbortDataListToSP(new ArrayList<AppAbortDbAdapterUtil.AbortData>());


        /*ActivityManager myManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
        //Log.i(TAG,"runningService:"+runningService);
        if (runningService!=null){
            for (int i = 0; i < runningService.size(); i++) {
                Log.i(TAG,"runningService:"+runningService.get(i).service.getClassName());
            }
        }*/


        /*DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.i(TAG,"heigth : " + dm.heightPixels);
        Log.i(TAG,"width : " + dm.widthPixels);*/


        /*List<Integer> ecgDataList =  new ArrayList<Integer>();
        for (int i = 0; i < 15507937; i++) {
            ecgDataList.add(i);
        }
        System.out.println(ecgDataList.size());

        int[] calcuEcgRate = new int[1000];
        System.out.println(calcuEcgRate.length);
        int heartCount = ecgDataList.size() / calcuEcgRate.length;
        System.out.println(heartCount);

        for (int j=0;j<heartCount;j++){
            for (int i=0;i<calcuEcgRate.length;i++){
                calcuEcgRate[i] = ecgDataList.get(j*calcuEcgRate.length+i);
            }
        }*/


        /*List<AppAbortDataSave> abortDatasCopy = new ArrayList<>();
        AppAbortDataSave  appAbortDataSave = new AppAbortDataSave(System.currentTimeMillis(),"aa",1);

        ArrayList<Integer> integerList = new ArrayList<>();

        for (int i = 0; i < 8*60*30; i++) {
            integerList.add(i);
        }
        appAbortDataSave.setSpeedStringList(integerList);

        abortDatasCopy.add(appAbortDataSave);
        abortDatasCopy.add(appAbortDataSave);

        List<AppAbortDataSave> abortDatasCopy1 = new ArrayList<>();
        abortDatasCopy1.addAll(abortDatasCopy);

        *//*Gson gson = new Gson();
        String  listString = gson.toJson(abortDatasCopy);
        Log.i(TAG,"listString:"+listString);
        MyUtil.putStringValueFromSP("ttt",listString);*//*

        putAbortDataListToSP(abortDatasCopy);
        putAbortDataListToSP(abortDatasCopy);*/


        /*new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(7000);
                    mWakelock.acquire(); // Wake up Screen and keep screen lighting
                    Log.i(TAG,"liangping");

                    Thread.sleep(5000);
                    mWakelock.release(); // release control.stop to keep screen lighting
                    Log.i(TAG,"heiping");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/


            /*final int[] calcuData = new int[262655*20];  //10小时

            Log.i(TAG,"DiagnosisNDK.AnalysisEcg: =====================");
            HeartRateResult heartRateResult = DiagnosisNDK.AnalysisEcg(calcuData, calcuData.length, Constant.oneSecondFrame);

            Log.i(TAG,"heartRateResult:"+heartRateResult.toString());*/

        /*final Camera camera = Camera.open();
        LightUtil.turnLightOff(camera);

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(5000);
                    LightUtil.turnLightOn(camera);
                    Log.i(TAG,"开灯");

                    Thread.sleep(5000);
                    LightUtil.turnLightOff(camera);
                    Log.i(TAG,"关灯");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/

        /*  此方法杀死应用后会马上重启，做不到杀死应用的效果
        Set<String> runningAppProcessInfoList = MyUtil.getRunningAppProcessInfoList(this);
        int i=0;

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (String s:runningAppProcessInfoList){
            Log.i(TAG,"runningAppProcessInfo: "+i+"  " +s);
            activityManager.killBackgroundProcesses(s);
            i++;
        }*/


        Log.i(TAG,"Build.MODEL: "+ Build.MODEL);
        /*String test =  "gwCBAH8AfgB9AHwAewB7AHsAewB7AHsAewB7AHwAewB7AHwAfAB8AH0AfQB+AH8AfwB+AH4AfQB9AHwAfAB8AHwAfAB8AH0AfQB9AH0AfwCEAI4AkgCDAHEAcgB5AHsAfAB9AH0AfgB/AIAAgACBAIEAggCEAIUAhwCJAIwAjgCPAI8AjwCOAIwAiQCGAIMAgQB/AH0AfAB8AHsAewB7AHsAewB7AHwAfAB8AHwAfAB8AH0AfQB9AH0AfgB/AH8AfgB9AH0AfQB9AH0AfQB+AH4AfgB9AH4AfgB/AIQAjACTAIkAdABxAHkAfQB+AH4AfwB/AIAAgACBAIIAgwCDAIQAhgCHAIoAjACOAJAAkACQAI8AjQCKAIcAhQCCAH8AfgB9AHwAfAB7AHsAewB7AHwAfAB8AHwAfAB8AHwAfAB8AHwAfAB9AH4AfgB+AH4AfQB9AHwAfAB8AH0AfAB8AH0AfQB8AHwAfgCCAIsAkwCKAHQAcQB4AHsAfAB8AHwAfQB+AH4AfwB/AIAAgQCCAIMAhQCHAIkAjACOAI4AjgCNAIsAiQCGAIMAgAB+AH0AfAB7AHsAegB6AHoAegB6AHoAewB6AHsAewB7AHsAfAB7AHwAfQB+AH4AfQB9AHwAfAB8AHwAfAB8AHwAfAB9AH0AfQB8AHwAgACIAJMAjgB4AHAAdgB6AHsAfAB8AHwAfQB+AH4AfwCAAIAAggCDAIUAhwCJAIwAjQCOAI8AjwCNAIoAhwCEAIIAgAA=\n";
        String s = MyUtil.decodeBase64String(test);
        Log.i(TAG,"s:"+s);*/


        /*测试计算心率算法
        int[] test = new int[1800];
        for (int i=0;i<1800;i++){
            test[i] = 60+i%20;
        }

        int i = DiagnosisNDK.ecgHeart(test, test.length, Constant.oneSecondFrame);
        Log.i(TAG,"心率:"+i);*/

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        Log.i(TAG,"absolutePath:"+absolutePath);



        //MyUtil.saveDeviceToSP(new Device("1","1","1","1",2),Constant.sportType_Insole);

        String locale = Locale.getDefault().toString();
        Log.i(TAG,"locale:"+locale);
        if(locale.equals(Locale.CHINA)){
            //中国

        }
        else {
            //国外

        }
    }

    private void selfStartManagerSettingIntent(Context context){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName componentName = new ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
        intent.setComponent(componentName);
        try{
            context.startActivity(intent);
        }catch (Exception e){//抛出异常就直接打开设置页面
            intent=new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }

    private PowerManager.WakeLock mWakelock;

    public static synchronized void putAbortDataListToSP(List<AppAbortDataSave> abortDatas){

        List<AppAbortDataSave> abortDatasCopy = new ArrayList<>();
        abortDatasCopy.addAll(abortDatas);

        Gson gson = new Gson();
        String  listString = gson.toJson(abortDatasCopy);
        Log.i(TAG,"listString:"+listString);
        MyUtil.putStringValueFromSP("ttt",listString);
    }


    int i = 0;

    private void initView() {
        initHeadView();
        setLeftText(getResources().getString(R.string.app_name));
        setCenterText("");
        setHeadBackgroudColor("#0c64b5");

        iv_base_connectedstate = (ImageView) findViewById(R.id.iv_base_connectedstate);

        dv_main_compass = (DashboardView) findViewById(R.id.dv_main_compass);
        cv_mian_index = (CircleRingView) findViewById(R.id.cv_mian_index);
        cv_mian_warring = (CircleRingView) findViewById(R.id.cv_mian_warring);



        RelativeLayout rl_mian_start = (RelativeLayout) findViewById(R.id.rl_mian_start);
        RelativeLayout rl_main_healthydata = (RelativeLayout) findViewById(R.id.rl_main_healthydata);
        RelativeLayout rl_main_sportcheck = (RelativeLayout) findViewById(R.id.rl_main_sportcheck);
        RelativeLayout rl_main_sportarea = (RelativeLayout) findViewById(R.id.rl_main_sportarea);
        RelativeLayout rl_main_me = (RelativeLayout) findViewById(R.id.rl_main_me);
        RelativeLayout rl_main_age = (RelativeLayout) findViewById(R.id.rl_main_age);
        RelativeLayout rl_main_healthyvalue = (RelativeLayout) findViewById(R.id.rl_main_healthyvalue);
        RelativeLayout rl_main_warringindex = (RelativeLayout) findViewById(R.id.rl_main_warringindex);

        tv_main_age = (TextView) findViewById(R.id.tv_main_age);
        tv_main_indexvalue = (TextView) findViewById(R.id.tv_main_indexvalue);
        tv_base_charge = (TextView) findViewById(R.id.tv_base_charge);
        iv_main_warring = (ImageView) findViewById(R.id.iv_main_warring);

        myOnClickListener = new MyOnClickListener();

        rl_mian_start.setOnClickListener(myOnClickListener);

        rl_main_healthydata.setOnClickListener(myOnClickListener);
        rl_main_sportcheck.setOnClickListener(myOnClickListener);
        rl_main_sportarea.setOnClickListener(myOnClickListener);
        rl_main_me.setOnClickListener(myOnClickListener);
        rl_main_age.setOnClickListener(myOnClickListener);

        rl_main_healthyvalue.setOnClickListener(myOnClickListener);
        rl_main_warringindex.setOnClickListener(myOnClickListener);

        getIv_base_rightimage().setOnClickListener(myOnClickListener);


        int id = rl_main_healthyvalue.getId();

        Log.i(TAG,"id:"+id);

        iv_base_connectedstate.setVisibility(View.VISIBLE);

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CHARGE_CHANGE);
        registerReceiver(mchargeReceiver, filter);

        MyApplication.isNeedSynMsgToDevice = true;

        //showUploadOffLineData();


        //sendStartDataTransmitOrderToBlueTooth();
        //sendDeviceSynOrderToBlueTooth();

        /*DeviceOffLineFileUtil.setTransferTimeOverTime(new DeviceOffLineFileUtil.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                Log.i(TAG,"5s没有收到数据");
                //mIsDataStart = false;
                mIsConnectted = mIsDataStart = MyApplication.isHaveDeviceConnectted  = false;
            }
        },5);*/

        /*AppAbortDbAdapterUtil offLineDbAdapter = new AppAbortDbAdapterUtil(this);
        offLineDbAdapter.open();

        UploadRecord uploadRecord = new UploadRecord();
        uploadRecord.setUploadState("0");
        uploadRecord.setFi("BBBBBBBB");
        uploadRecord.setId("1496398469312");

        offLineDbAdapter.createOrUpdateUploadReportObject(uploadRecord);

        List<UploadRecord> uploadRecordsState = offLineDbAdapter.queryRecordByUploadState("0");
        Log.i(TAG,"uploadRecordsState:"+uploadRecordsState);*/


        /*AppAbortDbAdapterUtil offLineDbAdapter = new AppAbortDbAdapterUtil(this);
        offLineDbAdapter.open();
        offLineDbAdapter.addColumnToTable("serveId","STRING");

        UploadRecord uploadRecord = new UploadRecord();
        uploadRecord.setUploadState("0");
        offLineDbAdapter.createOrUpdateUploadReportObject(uploadRecord);

        List<UploadRecord> uploadRecords = offLineDbAdapter.queryRecordAll();

        Log.i(TAG,"uploadRecords:"+uploadRecords);

        List<UploadRecord> uploadRecordsState = offLineDbAdapter.queryRecordByUploadState("0");
        Log.i(TAG,"uploadRecordsState:"+uploadRecordsState);

        boolean updateState = offLineDbAdapter.updateLocalRecordUploadState("1000", uploadRecordsState.get(0).id);
        Log.i(TAG,"updateState:"+updateState);

        List<UploadRecord> uploadRecordsState1 = offLineDbAdapter.queryRecordByUploadState("0");
        Log.i(TAG,"uploadRecordsState1:"+uploadRecordsState1);*/

        /*Intent service = new Intent(this, CommunicateToBleService.class);
        startService(service);*/

        /*Log.i(TAG,"Build.MODEL"+Build.MODEL);


        int []calcuEcgRate  = new int[1800];

        for (int i=0;i<1800;i++){
            calcuEcgRate[i] = i%20;
        }

        int mCurrentHeartRate = DiagnosisNDK.ecgHeart(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
        Log.i(TAG,"mCurrentHeartRate:"+mCurrentHeartRate);*/



        //计算
        /*byte[] bytes = new byte[1800];
        for (int i=0;i<1800;i++){
            bytes[i] = (byte)(int)calcuEcgRate[i];
        }
        int[] results = new int[2];

        DiagnosisNDK.AnalysisPedo(bytes,calcuEcgRate.length,results);

        Log.i(TAG,"results: "+results[0]+"  "+results[1]);   //results: 2  30*/



    }

    //给文本年龄设置文字动画
    private void setAgeTextAnimator(final TextView textView, int startAge, final int endAge) {
        Log.i(TAG,"setAgeTextAnimator");
        if (endAge>0){
            mValueAnimator = ValueAnimator.ofInt(startAge, endAge);
            mValueAnimator.setDuration(Constant.AnimatorDuration);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    textView.setText(animation.getAnimatedValue().toString());
                    if ((Integer)animation.getAnimatedValue()==endAge && mValueAnimator!=null){
                        mValueAnimator.cancel();
                        mValueAnimator = null;
                    }
                }
            });
        }
    }

    private void initData() {
        checkAndOpenBLEFeature();
        checkIsNeedUpadteApk();
    }

    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case LeProxy.ACTION_GATT_CONNECTED:
                    Log.i(TAG,"已连接 " );
                    if (MyApplication.deivceType==Constant.sportType_Cloth){
                        iv_base_connectedstate.setImageResource(R.drawable.yilianjie);
                    }
                    else if (MyApplication.deivceType==Constant.sportType_Insole){
                        if (MyApplication.insoleConnectedMacAddress.size()==2){
                            iv_base_connectedstate.setImageResource(R.drawable.yilianjie);
                        }
                    }
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
            }
        }
    };

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

    private void checkIsNeedUpadteApk() {
        Apk apkFromSP = ApkUtil.getApkFromSP();
        if (apkFromSP!=null && !MyUtil.isEmpty(apkFromSP.versioncode)){
            ApkUtil.checkAndUpdateVersion(Integer.parseInt(apkFromSP.versioncode),apkFromSP.path,this,false,apkFromSP.remark);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart");
    }

    boolean isonResumeEd ;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");

        if (MyApplication.deivceType==Constant.sportType_Cloth){
            setRightImage(R.drawable.yifu);
        }
        else if (MyApplication.deivceType==Constant.sportType_Insole){
            setRightImage(R.drawable.ydms_bt);
        }

        if (!isonResumeEd){
            if (mBluetoothAdapter!=null && !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            isonResumeEd = true;
        }

        if (MyApplication.isHaveDeviceConnectted){
            iv_base_connectedstate.setImageResource(R.drawable.yilianjie);
        }
        else {
            iv_base_connectedstate.setImageResource(R.drawable.duankai);
        }

        int healthyIindexvalue = MyUtil.getIntValueFromSP("healthyIindexvalue");
        if (healthyIindexvalue>0){
            tv_main_indexvalue.setText(healthyIindexvalue+"");
        }
        else {
            tv_main_indexvalue.setText("--");
        }
        physicalAgeDValue = MyUtil.getIntValueFromSP("physicalAgeDValue");
        int physicalAge = HealthyIndexUtil.getUserAge()-physicalAgeDValue;

        if (physicalAge >0){
            Log.i(TAG,"设置动画");
            setAgeTextAnimator(tv_main_age,0, physicalAge);
            dv_main_compass.setAgeData(physicalAge -10);
        }
        else {
            Log.i(TAG,"tv_main_age.setText");
            dv_main_compass.setAgeData(0);
            tv_main_age.setText("--");
        }

        Log.i(TAG,"healthyIindexvalue:"+healthyIindexvalue+"  physicalAgeDValue:"+ physicalAgeDValue);



        if (mValueAnimator!=null){
            mValueAnimator.start();
            cv_mian_index.setValue(170);
            cv_mian_warring.setValue(230);
            if (scoreALL >0){
                dv_main_compass.setAgeData(physicalAge -10);
            }
        }

        int indexWarringHeartIconType = MyUtil.getIntValueFromSP("IndexWarringHeartIconType");
        Log.i(TAG,"indexWarringHeartIconType==:"+indexWarringHeartIconType);
        if (indexWarringHeartIconType!=-1){
            if (indexWarringHeartIconType==1){
                iv_main_warring.setImageResource(R.drawable.healthy_green);
            }
            else if (indexWarringHeartIconType==2){
                iv_main_warring.setImageResource(R.drawable.healthy_yellow);
            }
            else if (indexWarringHeartIconType==3){
                iv_main_warring.setImageResource(R.drawable.healthy_orange);
            }
            else if (indexWarringHeartIconType==4){
                iv_main_warring.setImageResource(R.drawable.healthy_red);
            }
        }
    }

    //检查是否支持蓝牙
    private void checkAndOpenBLEFeature() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show();
            return;
            //finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "error_bluetooth_not_supported", Toast.LENGTH_SHORT).show();
            //finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");

        physicalAgeDValue = -1;

        //mLeService.disconnect(clothDeviceConnecedMac);
    }

    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Log.i(TAG,"onClick:"+v.getId());
            boolean isLogin = MyUtil.getBooleanValueFromSP("isLogin");
            boolean isPrefectInfo = MyUtil.getBooleanValueFromSP("isPrefectInfo");
            if (!MyApplication.mActivities.contains(MainActivity.this)){
                MyApplication.mActivities.add(MainActivity.this);
            }
            if (!isLogin){
                showdialogToLogin();
                //finish();
                return;
            }
            else if (!isPrefectInfo){
                showdialogToSupplyData();
                return;
            }

            switch (v.getId()){
                case R.id.rl_main_healthydata:
                    List<Device> deviceListFromSP = MyUtil.getDeviceListFromSP();
                    if (deviceListFromSP.size()==0){
                        //没有绑定设备，提示用户去绑定
                        //startActivity(new Intent(MainActivity.this,MyDeviceActivity.class));
                        startActivity(new Intent(MainActivity.this,HealthyDataActivity.class));  //测试
                    }
                    else {
                        startActivity(new Intent(MainActivity.this,HealthyDataActivity.class));
                    }
                    break;
                case R.id.rl_main_sportcheck:
                    startActivity(new Intent(MainActivity.this,MotionDetectionActivity.class));
                    break;
                case R.id.rl_main_sportarea:
                    startActivity(new Intent(MainActivity.this,SportCommunityActivity.class));
                    //startActivity(new Intent(MainActivity.this,PrepareRunningActivity.class));
                    break;
                case R.id.rl_main_me:
                    /*List<BluetoothDevice> connectedDevices = LeProxy.getInstance().getConnectedDevices();
                    Log.i(TAG,"connectedDevices:"+connectedDevices);
                    for (BluetoothDevice bluetoothDevice:connectedDevices){
                        Log.i(TAG,"bluetoothDevice:"+bluetoothDevice.toString());

                    }*/
                    startActivity(new Intent(MainActivity.this,MeActivity.class));

                    BluetoothGatt bluetoothGatt ;


                    break;
                case R.id.rl_mian_start:

                   /* DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    Log.i(TAG,"heigth : " + dm.heightPixels);
                    Log.i(TAG,"width : " + dm.widthPixels);

                    HashMap<String,String> map = new HashMap<>();
                    map.put("screen","设备："+Build.MODEL+",heigth:"+dm.heightPixels+",width:"+dm.widthPixels);
                    MobclickAgent.onEvent(MainActivity.this,"event_phonemodel",map);*/

                    startActivity(new Intent(MainActivity.this, PrepareRunningActivity.class));



                    /*int type = MyUtil.getIntValueFromSP(Constant.sportType);
                    if (type==Constant.sportType_Cloth){
                        chooseOnOffLineRun();
                    }
                    else if (type==Constant.sportType_Insole){
                        startActivity(new Intent(MainActivity.this,PrepareRunningActivity.class));
                    }
                    else {
                        startActivity(new Intent(MainActivity.this,StartRunActivity.class));
                    }*/

                    //selfStartManagerSettingIntent(MainActivity.this);


                    /*Intent intent2 = getIntent();
                    boolean isNeedRecoverAbortData = intent2.getBooleanExtra(Constant.isNeedRecoverAbortData, false);
                    if (isNeedRecoverAbortData){
                        finish();
                    }*/
                    break;
                case R.id.rl_main_age:
                    Intent intent = new Intent(MainActivity.this, PhysicalAgeActivity.class);
                    intent.putExtra("physicalAgeDValue", physicalAgeDValue);
                    startActivity(intent);
                    break;
                case R.id.rl_main_healthyvalue:
                    Intent intent1 = new Intent(MainActivity.this, HealthIndicatorAssessActivity.class);
                    intent1.putExtra("scoreALL",scoreALL);
                    startActivity(intent1);
                    break;
                case R.id.rl_main_warringindex:
                    startActivity(new Intent(MainActivity.this,IndexWarringActivity.class));
                    break;
                case R.id.iv_base_rightimage:
                    startActivity(new Intent(MainActivity.this,MyDeviceActivity.class));
                    break;
            }
        }
    }

    private void chooseOnOffLineRun() {
        View inflate = View.inflate(this, R.layout.view_choose_onoff, null);
        ImageView iv_choose_online = (ImageView) inflate.findViewById(R.id.iv_choose_online);
        Button bt_choose_offline = (Button) inflate.findViewById(R.id.bt_choose_offline);


        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.myCorDialog).setView(inflate).create();
        //alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        float width = getResources().getDimension(R.dimen.x900);
        float height = getResources().getDimension(R.dimen.x500);


        alertDialog.getWindow().setLayout(new Float(width).intValue(),new Float(height).intValue());

        iv_choose_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOnlineRun(alertDialog);
            }
        });

        bt_choose_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭数据传输
                stopEcgData(alertDialog);
            }
        });
    }

    private void startOnlineRun(AlertDialog alertDialog) {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        Log.i(TAG,"gps打开？:"+locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            MyUtil.chooseOpenGps(this);
        }
        else {
            alertDialog.dismiss();
            Intent intent = new Intent(MainActivity.this, PrepareRunningActivity.class);
            intent.putExtra(Constant.sportState,Constant.sportType_Cloth);
            startActivity(intent);
        }
    }

    private void stopEcgData(AlertDialog alertDialog) {
        Log.i(TAG,"关闭数据指令");
        if (!MyUtil.isEmpty(CommunicateToBleService.clothDeviceConnecedMac)){
            boolean send = LeProxy.getInstance().send(CommunicateToBleService.clothDeviceConnecedMac, DataUtil.hexToByteArray(Constant.stopDataTransmitOrder), true);
            if (send){
                alertDialog.dismiss();
                AlertDialog alertDialog_1 = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("主机已进入离线，快去跑步吧，记得回来同步跑步数据哦！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                alertDialog_1.setCanceledOnTouchOutside(false);
                alertDialog_1.show();
            }
        }
        else {
            MyUtil.showToask(this,"衣服未连接，无法进入离线跑步");
            alertDialog.dismiss();
        }

    }

    public void showdialogToLogin(){
        startActivity(new Intent(MainActivity.this,LoginInputNumberActivity.class));
        /*new AlertDialog.Builder(this).setTitle("登陆提醒")
                .setMessage("现在登陆")
                .setPositiveButton("等会再去", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("现在就去", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    }
                })
                .show();*/
    }

    public void showdialogToSupplyData(){
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.add_information))
                .setMessage(getResources().getString(R.string.add_information_dec))
                .setNegativeButton(getResources().getString(R.string.exit_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.exit_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this,SupplyPersionDataActivity.class));
                    }
                })
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
       // ShowLocationOnMap.mMapView = null;
        //android.os.Process.killProcess(android.os.Process.myPid());  //退出应用程序

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
        unregisterReceiver(mchargeReceiver);
        MyUtil.setDialogNull();
    }

    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
               /* NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(1);*/
                //finish();

               /* ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE); //获取应用程序管理器
                manager.killBackgroundProcesses("com.remote1"); //强制结束当前应用程序
                manager.killBackgroundProcesses("com.amsu.healthy:MyTestService2"); //强制结束当前应用程序
                manager.killBackgroundProcesses(getPackageName()); //强制结束当前应用程序*/

              /*  MyUtil.stopAllServices(this);
                android.os.Process.killProcess(android.os.Process.myPid());*//*
                android.os.Process.killProcess(android.os.Process.myPid());*/
                MyApplication.isNeedSynMsgToDevice = false;
                finish();

                //android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
