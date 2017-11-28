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
import android.util.DisplayMetrics;
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
import com.amsu.healthy.activity.marathon.MarathonActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Apk;
import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.ApkUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.ble.LeProxy;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.wifiTransmit.DeviceOffLineFileUtil;
import com.amsu.healthy.view.CircleRingView;
import com.amsu.healthy.view.DashboardView;
import com.ble.api.DataUtil;
import com.ble.ble.BleService;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.amsu.healthy.utils.Constant.isMarathonSportType;

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

        int[] calcuEcgRateAfterFilter = new int[1000];
        System.out.println(calcuEcgRateAfterFilter.length);
        int heartCount = ecgDataList.size() / calcuEcgRateAfterFilter.length;
        System.out.println(heartCount);

        for (int j=0;j<heartCount;j++){
            for (int i=0;i<calcuEcgRateAfterFilter.length;i++){
                calcuEcgRateAfterFilter[i] = ecgDataList.get(j*calcuEcgRateAfterFilter.length+i);
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


        int[] calcuEcgRate = {0,134,255,252,15,46,0,119,255,12,16,79,0,112,255,251,15,71,0,102,255,41,16,103,0,123,255,246,15,111,0,99,255,20,16,64,0,85,255,242,15,51,0,116,255,18,16,46,0,116,255,241,15,52,0,108,255,253,15,96,0,118,255,6,16,57,0,94,255,19,16,64,0,80,255,14,16,98,0,104,255,249,15,81,0,102,255,23,16,64,0,122,255,47,16,50,0,126,255,232,15,74,0,119,255,25,16,80,0,109,255,12,16,104,0,120,255,47,16,87,0,118,255,243,15,67,0,131,255,6,16,97,0,84,255,20,16,61,0,126,255,6,16,58,0,128,255,34,16,105,0,102,255,15,16,48,0,100,255,47,16,63,0,108,255,16,16,54,0,105,255,26,16,102,0,102,255,29,16,97,0,117,255,1,16,66,0,99,255,252,15,60,0,106,255,36,16,69,0,93,255,237,15,89,0,118,255,9,16,60,0,118,255,3,16,51,0,92,255,253,15,72,0,101,255,9,16,74,0,92,255,235,15,58,0,101,255,247,15,78,0,134,255,33,16,72,0,111,255,48,16,57,0,94,255,22,16,64,0,97,255,9,16,64,0,96,255,9,16,94,0,108,255,10,16,51,0,132,255,21,16,83,0,101,255,18,16,63,0,118,255,30,16,55,0,135,255,253,15,57,0,103,255,25,16,76,0,132,255,34,16,74,0,97,255,252,15,78,0,88,255,17,16,46,0,117,255,15,16,62,0,125,255,31,16,53,0,103,255,255,15,88,0,130,255,23,16,93,0,102,255,50,16,90,0,102,255,23,16,70,0,118,255,39,16,97,0,91,255,6,16,58,0,110,255,6,16,57,0,121,255,32,16,68,0,76,255,12,16,82,0,103,255,3,16,45,0,96,255,242,15,77,0,94,255,253,15,63,0,117,255,2,16,90,0,104,255,25,16,54,0,106,255,34,16,58,0,132,255,28,16,66,0,105,255,12,16,95,0,107,255,239,15,69,0,110,255,20,16,69,0,109,255,28,16,77,0,90,255,31,16,74,0,120,255,1,16,76,0,120,255,22,16,75,0,103,255,12,16,69,0,110,255,22,16,68,0,108,255,247,15,82,0,113,255,20,16,51,0,114,255,15,16,65,0,125,255,15,16,76,0,97,255,36,16,79,0,140,255,223,15,61,0,112,255,19,16,61,0,123,255,19,16,69,0,113,255,18,16,72,0,101,255,23,16,71,0,100,255,2,16,51,0,88,255,16,16,41,0,117,255,249,15,92,0,137,255,27,16,72,0,131,255,16,16,49,0,121,255,31,16,64,0,130,255,43,16,68,0,135,255,255,15,94,0,104,255,244,15,68,0,90,255,1,16,102,0,86,255,27,16,59,0,102,255,6,16,100,0,100,255,253,15,51,0,100,255,18,16,53,0,135,255,2,16,96,0,116,255,10,16,113,0,118,255,13,16,63,0,118,255,27,16,48,0,91,255,12,16,98,0,120,255,251,15,65,0,126,255,22,16,64,0,116,255,11,16,84,0,133,255,241,15,86,0,134,255,4,16,67,0,120,255,8,16,84,0,115,255,242,15,63,0,155,255,28,16,92,0,127,255,16,16,57,0,112,255,14,16,58,0,78,255,28,16,57,0,166,255,3,16,81,0,123,255,30,16,57,0,100,255,23,16,67,0,107,255,17,16,68,0,135,255,16,16,81,0,117,255,16,16,65,0,125,255,238,15,59,0,105,255,50,16,53,0,99,255,6,16,60,0,93,255,48,16,72,0,104,255,19,16,89,0,155,255,37,16,53,0,146,255,23,16,61,0,107,255,237,15,78,0,132,255,5,16,83,0,92,255,18,16,68,0,117,255,6,16,64,0,113,255,242,15,48,0,110,255,21,16,84,0,103,255,9,16,76,0,147,255,13,16,55,0,106,255,21,16,69,0,90,255,4,16,71,0,125,255,19,16,60,0,109,255,25,16,52,0,105,255,1,16,78,0,98,255,255,15,54,0,135,255,22,16,90,0,103,255,255,15,74,0,93,255,250,15,54,0,101,255,16,16,98,0,114,255,29,16,67,0,107,255,244,15,48,0,118,255,16,16,91,0,95,255,7,16,59,0,128,255,32,16,45,0,138,255,248,15,74,0,107,255,253,15,64,0,108,255,18,16,104,0,120,255,12,16,94,0,112,255,243,15,63,0,122,255,15,16,65,0,114,255,250,15,45,0,131,255,25,16,56,0,105,255,10,16,80,0,106,255,247,15,46,0,111,255,17,16,53,0,99,255,9,16,79,0,88,255,14,16,77,0,87,255,26,16,90,0,133,255,3,16,60,0,109,255,3,16,49,0,120,255,1,16,67,0,111,255,245,15,67,0,91,255,2,16,97,0,102,255,232,15,62,0,94,255,88,16,53,0,134,255,3,16,74,0,127,255,27,16,69,0,117,255,32,16,63,0,90,255,239,15,54,0,109,255,29,16,98,0,111,255,4,16,106,0,113,255,36,16,83,0,98,255,250,15,78,0,108,255,6,16,57,0,115,255,22,16,65,0,90,255,19,16,48,0,126,255,10,16,86,0,128,255,16,16,68,0,126,255,22,16,78,0,115,255,23,16,41,0,107,255,9,16,66,0,104,255,15,16,57,0,115,255,5,16,85,0,101,255,12,16,78,0,102,255,45,16,91,0,113,255,26,16,23,0,108,255,1,16,72,0,116,255,25,16,74,0,124,255,44,16,71,0,104,255,248,15,57,0,100,255,13,16,63,0,114,255,0,16,40,0,118,255,23,16,55,0,99,255,16,16,75,0,142,255,6,16,93,0,106,255,33,16,93,0,103,255,3,16,78,0,77,255,7,16,56,0,92,255,32,16,69,0,101,255,219,15,88,0,74,255,38,16,81,0,72,255,7,16,55,0,108,255,22,16,73,0,117,255,31,16,72,0,118,255,253,15,103,0,111,255,29,16,59,0,91,255,13,16,32,0,96,255,32,16,85,0,126,255,6,16,70,0,91,255,11,16,70,0,130,255,7,16,69,0,120,255,13,16,79,0,133,255,15,16,92,0,115,255,251,15,112,0,121,255,20,16,52,0,136,255,254,15,66,0,117,255,14,16,70,0,115,255,254,15,23,0,116,255,19,16,80,0,125,255,34,16,115,0,134,255,242,15,63,0,104,255,226,15,41,0,71,255,38,16,84,0,103,255,246,15,66,0,64,255,35,16,67,0,86,255,2,16,126,0,80,255,14,16,64,0,70,255,12,16,81,0,104,255,11,16,45,0,108,255,242,15,64,0,115,255,231,15,58,0,126,255,13,16,81,0,153,255,14,16,71,0,75,255,50,16,93,0,110,255,66,16,69,0,85,255,253,15,66,0,106,255,18,16,74,0,111,255,238,15,65,0,151,255,4,16,66,0,107,255,34,16,74,0,118,255,250,15,91,0,94,255,31,16,62,0,125,255,241,15,60,0,100,255,25,16,81,0,114,255,2,16,63,0,85,255,39,16,86,0,125,255,239,15,42,0,104,255,243,15,68,0,127,255,1,16,85,0,75,255,4,16,91,0,139,255,13,16,90,0,109,255,6,16,47,0,92,255,0,16,51,0,112,255,11,16,68,0,130,255,14,16,68,0,127,255,14,16,59,0,100,255,248,15,77,0,115,255,252,15,97,0,84,255,26,16,38,0,131,255,18,16,75,0,122,255,240,15,41,0,101,255,18,16,92,0,143,255,223,15,100,0,93,255,1,16,91,0,91,255,25,16,91,0,75,255,9,16,73,0,99,255,45,16,39,0,94,255,35,16,42,0,89,255,14,16,71,0,110,255,252,15,49,0,101,255,4,16,81,0,116,255,4,16,60,0,102,255,47,16,95,0,118,255,2,16,84,0,95,255,18,16,100,0,88,255,244,15,68,0,125,255,244,15,86,0,104,255,28,16,93,0,128,255,0,16,67,0,107,255,0,16,67,0,117,255,21,16,73,0,110,255,7,16,74,0,59,255,7,16,67,0,125,255,253,15,64,0,101,255,45,16,73,0,102,255,13,16,71,0,125,255,249,15,83};


        byte[] bytes = new byte[calcuEcgRate.length];
        for (int i=0;i<calcuEcgRate.length;i++){
            bytes[i] = (byte)(int)calcuEcgRate[i];
        }

        int stridefreByAccData = MyUtil.getStridefreByAccData(bytes);
        Log.i(TAG,"stridefreByAccData: "+stridefreByAccData);



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


        int []calcuEcgRateAfterFilter  = new int[1800];

        for (int i=0;i<1800;i++){
            calcuEcgRateAfterFilter[i] = i%20;
        }

        int mCurrentHeartRate = DiagnosisNDK.ecgHeart(calcuEcgRateAfterFilter, calcuEcgRateAfterFilter.length, Constant.oneSecondFrame);
        Log.i(TAG,"mCurrentHeartRate:"+mCurrentHeartRate);*/



        //计算
        /*byte[] bytes = new byte[1800];
        for (int i=0;i<1800;i++){
            bytes[i] = (byte)(int)calcuEcgRateAfterFilter[i];
        }
        int[] results = new int[2];

        DiagnosisNDK.AnalysisPedo(bytes,calcuEcgRateAfterFilter.length,results);

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
            //Log.i(TAG,"mLocalReceiver:"+intent.getAction());
            switch (intent.getAction()){
                case LeProxy.ACTION_GATT_CONNECTED:
                    Log.i(TAG,"已连接 " );
                    if (MyApplication.deivceType == Constant.sportType_Cloth) {
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
        else {
            iv_main_warring.setImageResource(R.drawable.jkzb_k);
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

                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    Log.i(TAG,"heigth : " + dm.heightPixels);
                    Log.i(TAG,"width : " + dm.widthPixels);

                    HashMap<String,String> map = new HashMap<>();
                    map.put("screen","设备："+Build.MODEL+",heigth:"+dm.heightPixels+",width:"+dm.widthPixels);
                    MobclickAgent.onEvent(MainActivity.this,"event_phonemodel",map);
                    boolean is = MyUtil.getBooleanValueFromSP(isMarathonSportType);
                    if (is){
                        startActivity(MarathonActivity.createIntent(MainActivity.this));
                    }else{
                        startActivity(new Intent(MainActivity.this, PrepareRunningActivity.class));
                    }


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
