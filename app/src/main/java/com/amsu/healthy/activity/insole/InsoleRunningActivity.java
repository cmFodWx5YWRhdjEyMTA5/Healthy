package com.amsu.healthy.activity.insole;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.proxy.LeProxy;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.RunTrailMapActivity;
import com.amsu.healthy.activity.StartRunActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.AppAbortDataSaveInsole;
import com.amsu.healthy.bean.Insole3ScendCache;
import com.amsu.healthy.utils.AppAbortDbAdapterUtil;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.RunTimerTaskUtil;
import com.amsu.healthy.utils.ShowNotificationBarUtil;
import com.amsu.healthy.utils.map.DbAdapter;
import com.amsu.healthy.utils.map.PathRecord;
import com.amsu.healthy.utils.map.Util;
import com.amsu.healthy.utils.wifiTransmit.DeviceOffLineFileUtil;
import com.amsu.healthy.view.GlideRelativeView;
import com.ble.api.DataUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class InsoleRunningActivity extends Activity implements View.OnClickListener,AMapLocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private static final String TAG = "InsoleRunningActivity";
    private RelativeLayout rl_run_continue;
    private RelativeLayout rl_run_stop;
    private RelativeLayout rl_insolerun_continue;
    private RelativeLayout rl_insolerun_end;
    private RelativeLayout rl_run_lock;
    private boolean isLockScreen;
    private TextView tv_run_lock;
    private DataOutputStream leftDataOutputStream;
    private ByteBuffer leftByteBuffer;
    private DataOutputStream rightDataOutputStream;
    private ByteBuffer rightByteBuffer;
    private long mCurrentTimeMillis =-1;
    //private long mCurrentTimeMillis_insoleTestPackage =-1;
    private long mCurrentTimeMillis_insoleTestPackage =-1;
    private String mLeftInsole30SencendFileAbsolutePath;
    private String mRightInsole30SencendFileAbsolutePath;
    private TextView tv_test;
    private String mLeftMacAddress;
    private String mRightMacAddress;


    private static StartRunActivity mStartRunActivityInstance;
    private TextView tv_run_speed;
    private TextView tv_run_distance;
    private TextView tv_run_time;
    private TextView tv_run_kcal;
    public boolean mIsRunning = false;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    private PathRecord pathRecord;    //存放未纠偏轨迹记录信息
    //private List<TraceLocation> mTracelocationlist = new ArrayList<>();   //偏轨后轨迹
    public long mStartTime;
    private RunTimerTaskUtil runTimerTaskUtil;

    public long sportCreateRecordID =-1;
    private int calculateSpeedCount = 10;   //10次，一次2s,即为20s
    private RelativeLayout rl_run_bootom;
    private GlideRelativeView rl_run_glide;
    public double mAllDistance;
    private double mAddDistance;
    private TextView tv_run_test;

    private ArrayList<Integer> heartRateDates = new ArrayList<>();  // 心率数组

    private boolean isThreeMinute = false;   //是否到5分钟
    private boolean isStartThreeMitTimer;  //是否开始三分钟倒计时计时器


    public static final int accDataLength = 1800;
    private static final int saveDataTOLocalTimeSpanSecond = Constant.saveDataTOLocalTimeSpanSecond;  //数据持久化时间间隔 1分钟
    private static final int minimumLimitTimeMillis = 1000 * 1 * 1;  //最短时间限制 3分钟
    public static final String action = "jason.broadcast.action";    //发送广播，将心率值以广播的方式放松出去，在其他Activity可以接受

    private float mAllKcal;

    private DeviceOffLineFileUtil deviceOffLineFileUtil;
    private AppAbortDataSaveInsole appAbortDataSaveInsole;
    private DeviceOffLineFileUtil saveDeviceOffLineFileUtil;

    private List<AMapLocation> mOutdoorCal8ScendSpeedList;
    private List<Float> mIndoorCal8ScendSpeedList ;
    public String mFinalFormatSpeed;
    private ImageView iv_pop_icon;
    private TextView tv_pop_text;
    private final int trackSpeedOutdoorMAX = 10;  //0.5s内行走的最大速度
    private final int trackSpeedIndoorMAX = 5;  //0.5s内行走的最大速度
    private String mFormatDistance = "0.00";
    //private List<AppAbortDataSave> abortDataListFromSP;
    private boolean isNeedRecoverAbortData;
    private long recoverTimeMillis = 0;

    private long addDuration;
    private float mPreOutDoorDistance;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    public Date mCurrTimeDate;
    private TextView tv_run_stride;
    private TextView tv_run_avespeed;
    private TextView tv_run_maxSpeedKM_Hour;
    private TextView tv_run_continue;
    private TextView tv_run_end;
    private TextView tv_run_stridefre;
    private final int insole_left = 1;
    private final int insole_right = 2;
    private FileWriter lefFfileWriter;
    private FileWriter rightfileWriter;
    private BufferedWriter rightBufferedWriter;
    private BufferedWriter leftBufferedWriter;

    private ArrayList<Integer> stridefreList;
    private int mAllStep;
    private boolean mIsOutDoor;
    private MyApplication application;

    private ArrayList<Integer> paceList;
    private long startTimeMillis =-1;
    private int mPrestepCount;
    private List<Float> mLeftReceiveCountRate;
    private List<Float> mRightReceiveCountRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insole_running);

        initView();
    }

    private void initView() {
        rl_run_continue = (RelativeLayout) findViewById(R.id.rl_run_continue);
        rl_run_stop = (RelativeLayout) findViewById(R.id.rl_run_stop);

        rl_insolerun_continue = (RelativeLayout) findViewById(R.id.rl_insolerun_continue);
        rl_insolerun_end = (RelativeLayout) findViewById(R.id.rl_insolerun_end);

        tv_run_distance = (TextView) findViewById(R.id.tv_run_mileage);
        tv_run_time = (TextView) findViewById(R.id.tv_run_time);
        tv_run_stride = (TextView) findViewById(R.id.tv_run_stride);
        tv_run_avespeed = (TextView) findViewById(R.id.tv_run_avespeed);
        tv_run_stridefre = (TextView) findViewById(R.id.tv_run_freqstride);
        tv_run_maxSpeedKM_Hour = (TextView) findViewById(R.id.tv_run_maxspeed);
        tv_run_kcal = (TextView) findViewById(R.id.tv_run_kcal);

        TextView tv_run_sptop = (TextView) findViewById(R.id.tv_run_sptop);
        tv_run_lock = (TextView) findViewById(R.id.tv_run_lock);
        ImageView iv_run_map = (ImageView) findViewById(R.id.iv_run_map);
        tv_run_continue = (TextView) findViewById(R.id.tv_run_continue);
        tv_run_end = (TextView) findViewById(R.id.tv_run_end);

        rl_run_lock = (RelativeLayout) findViewById(R.id.rl_run_lock);
        GlideRelativeView rl_run_glide = (GlideRelativeView) findViewById(R.id.rl_run_glide);

        tv_run_sptop.setOnClickListener(this);
        rl_run_stop.setOnClickListener(this);
        rl_insolerun_continue.setOnClickListener(this);
        rl_insolerun_end.setOnClickListener(this);
        tv_run_lock.setOnClickListener(this);
        iv_run_map.setOnClickListener(this);


        rl_run_glide.setOnONLockListener(new GlideRelativeView.OnONLockListener() {
            @Override
            public void onLock() {
                rl_run_lock.setVisibility(View.GONE);
                rl_run_continue.setVisibility(View.GONE);
                rl_run_stop.setVisibility(View.VISIBLE);
                tv_run_lock.setVisibility(View.VISIBLE);
                isLockScreen = false;
            }
        });

        mOutdoorCal8ScendSpeedList = new ArrayList<>();
        mIndoorCal8ScendSpeedList = new ArrayList<>();
        stridefreList = new ArrayList<>();
        paceList = new ArrayList<>();

        mLeftReceiveCountRate = new ArrayList<>();
        mRightReceiveCountRate = new ArrayList<>();


        //commitToServerAnaly("/storage/emulated/0/amsu/insole/20170802092924.is");

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, LeProxy.makeFilter());

        tv_test = (TextView) findViewById(R.id.tv_test);

        mIsOutDoor = getIntent().getBooleanExtra(Constant.mIsOutDoor, false);
        Log.i(TAG,"mIsOutDoor:"+mIsOutDoor);

        sendReadStepOrder();

        Intent intent = getIntent();
        isNeedRecoverAbortData = intent.getBooleanExtra(Constant.isNeedRecoverAbortData, false);
        if (isNeedRecoverAbortData){
            //需要恢复到之前跑步时的状态
            restoreLastRecord();
        }

        setRunningParameter();
    }

    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);
            switch (intent.getAction()){
                case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                    //if (!mIsRunning)return;
                    //byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);
                    if (mIsRunning){
                        try{
                            dealwithLebDataChange(DataUtil.byteArrayToHex(intent.getByteArrayExtra(LeProxy.EXTRA_DATA)),address);
                        }catch (Exception e){
                            Log.i(TAG,"e:"+e);
                        }
                    }

                    break;
            }
        }
    };

    private void dealwithLebDataChange(String hexData,String address) {
        //Log.i(TAG,"hexData："+hexData);

        String[] allDataSplit = hexData.split(" ");
        int startInt;
        if (!TextUtils.isEmpty(allDataSplit[0])){
            startInt = Integer.parseInt(allDataSplit[0], 16);
        }
        else {
            return;
        }

        //Log.i(TAG,"startInt:"+startInt);
        //Log.i(TAG,"allDataSplit.length:"+allDataSplit.length);



        //AA 4C FF FF FF FF FF FE FF 1A FF D6 10 1A 85 09 D8 8D  长度为53
        //4C 00 00 00 00 00 00 00 04 00 0A EF E9 A9 7A 7F BE  最新
        //6C FF FF 00 00 00 01 FF F8 00 03 EF FA E3 33 10 FF FF 00 00 00 00 FF EE 00 08 EF E9 E3 36 11 FF FF FF FF 00 00 FF D9 FF F6 EF ec E3 39 11 00 00 00 00 00 00 FF E7 00 01 F0 16 E3 3C 12

        if (allDataSplit.length==61){
            /*if (!isStartCollect){
                return;
            }*/

            if (MyUtil.isEmpty(mLeftMacAddress) || MyUtil.isEmpty(mRightMacAddress)){
                /*if (startInt>=50 && startInt<100){
                    //左侧数据
                    mLeftMacAddress = address;
                }
                else if (startInt>=100 && startInt<150){
                    //右侧数据
                    mRightMacAddress = address;
                }*/
                if (startInt<120){
                    //左侧数据
                    mLeftMacAddress = address;
                }
                else{
                    //右侧数据
                    mRightMacAddress = address;
                }
            }

            if (mCurrentTimeMillis_insoleTestPackage==-1){
                mCurrentTimeMillis_insoleTestPackage = System.currentTimeMillis();
            }

            if (System.currentTimeMillis()-mCurrentTimeMillis_insoleTestPackage>=1000*40) {
                Log.i("30ScendCount", "40秒到，上传计算");

                timeOutCalReceiveRate(2);
            }
            /*if (System.currentTimeMillis()-mCurrentTimeMillis_insoleTest>=1000*20){
                Log.i("30ScendCount","40秒到，上传计算");
                Log.i("30ScendCount","mLeftReceivePackageCount:"+mLeftReceivePackageCount);
                Log.i("30ScendCount","mRightReceivePackageCount:"+mRightReceivePackageCount);

                testText += mLeftMacAddress +"左脚   40秒到  count："+mLeftReceivePackageCount+"\n";
                testText += mRightMacAddress +"右脚   40秒到  count："+mRightReceivePackageCount+"\n";

                tv_test.setText(testText);

                mLeftReceivePackageCount=0;
                mRightReceivePackageCount=0;
                mCurrentTimeMillis_insoleTest = System.currentTimeMillis();
            }*/

            String[] oneGroupDdata1 = new String[15];
            String[] oneGroupDdata2 = new String[15];
            String[] oneGroupDdata3 = new String[15];
            String[] oneGroupDdata4 = new String[15];
            //1-长度/4
            int j=0;
            for (int i=1;i<16;i++){
                oneGroupDdata1[j++] = allDataSplit[i];
            }

            j=0;
            for (int i=16;i<31;i++){
                oneGroupDdata2[j++] = allDataSplit[i];
            }

            j=0;
            for (int i=31;i<46;i++){
                oneGroupDdata3[j++] = allDataSplit[i];
            }

            j=0;
            for (int i=46;i<61;i++){
                oneGroupDdata4[j++] = allDataSplit[i];
            }

            if (startInt<120){
                //左侧数据
                mLeftReceivePackageCount = mLeftReceivePackageCount+4;
                //Log.i(TAG,"hexData1："+hexData);
                parseAndWriteData(oneGroupDdata1,insole_left);
                parseAndWriteData(oneGroupDdata2,insole_left);
                parseAndWriteData(oneGroupDdata3,insole_left);
                parseAndWriteData(oneGroupDdata4,insole_left);
            }
            else{
                //右侧数据
                mRightReceivePackageCount = mRightReceivePackageCount+4;
                //Log.i(TAG,"hexData2："+hexData);
                parseAndWriteData(oneGroupDdata1,insole_right);
                parseAndWriteData(oneGroupDdata2,insole_right);
                parseAndWriteData(oneGroupDdata3,insole_right);
                parseAndWriteData(oneGroupDdata4,insole_right);
            }
        }

        /*if (hexData.length()==182){
            if (mCurrentTimeMillis==-1){
                mCurrentTimeMillis = System.currentTimeMillis();
            }
            if (System.currentTimeMillis()-mCurrentTimeMillis>=1000*40){
                //30s,上传到服务器分析
                if (leftDataOutputStream!=null){
                    try {
                        leftDataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    leftDataOutputStream = null;
                }
                if (rightDataOutputStream!=null){
                    try {
                        rightDataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    rightDataOutputStream = null;
                }
                mCurrentTimeMillis = System.currentTimeMillis();
                commitToServerAnaly(mLeftInsole30SencendFileAbsolutePath,mRightInsole30SencendFileAbsolutePath);
                mLeftReceivePackageCount=0;
                mRightReceivePackageCount=0;
            }

            String[] split = hexData.split(" ");
            double time = Integer.parseInt(split[14]+split[15]+split[16], 16)*0.000025;
            //Log.i(TAG,"time:"+time);

            //double time = Integer.parseInt(split[split.length-4]+split[split.length-3]+split[split.length-2], 16)*0.000025;

            float conventGyro  = 0.07f;
            float conventAcc = 9.8f*0.000244f;

            float gyrX = ((short) Integer.parseInt(split[2]+split[3], 16))*conventGyro;
            float gyrY = ((short) Integer.parseInt(split[4]+split[5], 16))*conventGyro;
            float gyrZ = ((short) Integer.parseInt(split[6]+split[7], 16))*conventGyro;

            float accX = ((short) Integer.parseInt(split[8]+split[9], 16))*conventAcc;
            float accY = ((short) Integer.parseInt(split[10]+split[11], 16))*conventAcc;
            float accZ = ((short) Integer.parseInt(split[12]+split[13], 16))*conventAcc;



            if (hexData.startsWith("4C")) {
                //L 左脚
                mLeftReceivePackageCount++;
                writeEcgDataToBinaryFile(time,gyrX,gyrY,gyrZ,accX,accY,accZ,insole_left);
                Log.i(TAG,"左脚 角速度："+gyrX+","+gyrY+","+gyrZ+",加速度:"+accX+","+accY+","+accZ);
            }
            else if (hexData.startsWith("52")) {
                //R 右脚
                mRightReceivePackageCount++;
                writeEcgDataToBinaryFile(time,gyrX,gyrY,gyrZ,accX,accY,accZ,insole_right);
                Log.i(TAG,"右脚 角速度："+gyrX+","+gyrY+","+gyrZ+",加速度:"+accX+","+accY+","+accZ);
            }
        }*/


        else if (hexData.length()==14 && hexData.startsWith("42 37 2B")){  //42 37 2B 00 00
            Log.i(TAG,"步数："+hexData);
            //鞋垫步数
            String[] split = hexData.split(" ");
            if (split.length==5){
                String stepCountString = split[3]+split[4];

                //Log.i("30ScendCount","步数 hexData:"+hexData);
                int tempStepCount = Integer.parseInt(stepCountString, 16);
                Log.i(TAG,"步数 "+address+", "+ tempStepCount);
                Log.i(TAG,"步数 mPreLeftStepCount:"+mPreLeftStepCount+", mPreRightStepCount:"+mPreRightStepCount);

                if (!MyUtil.isEmpty(mLeftMacAddress) && address.equals(mLeftMacAddress)){
                    if (mPreLeftStepCount==-1){
                        mPreLeftStepCount = tempStepCount;
                    }
                    else {
                        mCurLeftStepCount = tempStepCount-mPreLeftStepCount;
                        mLeftAllStepCount += mCurLeftStepCount;

                        //testText += "步数左脚：总数"+tempStepCount+"，上次"+mPreLeftStepCount+",这次"+mCurLeftStepCount+"\n";
                        mPreLeftStepCount = tempStepCount;
                        //tv_test.setText(testText);

                        if (mLeftNoReceiveCount>0){
                            mCurLeftStepCount = mCurLeftStepCount /mLeftNoReceiveCount;
                        }


                        if (mCurStride!=-1){
                            mCurStride = (int) ((mCurLeftStepCount + mCurRightStepCount)/2/8.0*60);
                        }
                        else {
                            mCurStride = (int) (mCurLeftStepCount /8.0*60);
                        }
                    }

                    mLeftNoReceiveCount = 0;

                }
                else if (!MyUtil.isEmpty(mRightMacAddress) && address.equals(mRightMacAddress)){
                    if (mPreRightStepCount==-1){
                        mPreRightStepCount = tempStepCount;
                    }
                    else {
                        mCurRightStepCount = tempStepCount-mPreRightStepCount;
                        mRightAllStepCount += mCurRightStepCount;

                        //testText += "步数右脚：总数"+tempStepCount+"，上次"+mPreRightStepCount+",这次"+mCurRightStepCount+"\n";
                        mPreRightStepCount = tempStepCount;
                        //tv_test.setText(testText);

                        if (mRightNoReceiveCount>0){
                            mCurRightStepCount = mCurRightStepCount /mRightNoReceiveCount;
                        }

                        if (mCurStride!=-1){
                            mCurStride = (int) ((mCurLeftStepCount + mCurRightStepCount)/2/8.0*60);
                        }
                        else {
                            mCurStride = (int) (mCurRightStepCount /8.0*60);
                        }
                    }

                    mRightNoReceiveCount = 0;
                }

                if (mCurStride>=0 && mCurStride<250){
                    tv_run_stridefre.setText(mCurStride+"");
                    stridefreList.add(mCurStride);
                    mAllStep = mCurStride;
                }

                mCurStride = -1;

                if (mLeftAllStepCount !=-1 && mRightAllStepCount ==-1){
                    mAllStep = mLeftAllStepCount;
                }
                else if (mLeftAllStepCount ==-1 && mRightAllStepCount !=-1){
                    mAllStep = mRightAllStepCount;
                }
                else if (mLeftAllStepCount !=-1 && mRightAllStepCount !=-1){
                    if (Math.abs(mRightAllStepCount - mLeftAllStepCount)>120){  //24*5= 120
                        //2个脚相差大于120的话，则用较大一个脚
                        if (mRightAllStepCount > mLeftAllStepCount){
                            mAllStep = mRightAllStepCount;
                        }
                        else {
                            mAllStep = mLeftAllStepCount;
                        }
                    }
                    else {
                        mAllStep = (mLeftAllStepCount + mRightAllStepCount)/2;
                    }
                }

                if (mAllStep>0){
                    tv_run_stride.setText((mAllStep+mPrestepCount)+"");

                    if(application !=null){
                        application.setRunningmCurrentStepCount(mAllStep);
                    }
                }
            }

        }
        else  if (allDataSplit.length==16){
            if (mCurrentTimeMillis_insoleTestPackage==-1){
                mCurrentTimeMillis_insoleTestPackage = System.currentTimeMillis();
            }
            if (System.currentTimeMillis()-mCurrentTimeMillis_insoleTestPackage>=1000*40){
                timeOutCalReceiveRate(1);
            }

            if (MyUtil.isEmpty(mLeftMacAddress) || MyUtil.isEmpty(mRightMacAddress)){
                /*if (startInt>=50 && startInt<100){
                    //左侧数据
                    mLeftMacAddress = address;
                }
                else if (startInt>=100 && startInt<150){
                    //右侧数据
                    mRightMacAddress = address;
                }*/

                if (startInt<120){
                    //左侧数据
                    mLeftMacAddress = address;
                }
                else{
                    //右侧数据
                    mRightMacAddress = address;
                }
            }
            String[] oneGroupDdata = new String[15];
            int j=0;
            for (int i=1;i<16;i++){
                oneGroupDdata[j++] = allDataSplit[i];
            }

            if (startInt<120){
                //左侧数据
                mLeftReceivePackageCount++;
                parseAndWriteData(oneGroupDdata,insole_left);
            }
            else{
                //右侧数据
                mRightReceivePackageCount++;
                parseAndWriteData(oneGroupDdata,insole_right);
            }


        }
    }

    private void timeOutCalReceiveRate(int packageType) {
        mLeftReceiveCountRate.add(mLeftReceivePackageCount/2080.f);
        mRightReceiveCountRate.add(mRightReceivePackageCount/2080.f);

        String[] leftRightReceiveCountRate = getLeftRightReceiveCountRate();

        if (packageType==1){ //1为短报
            testText += mLeftMacAddress +"==左脚短包 40秒："+mLeftReceivePackageCount+",比例"+leftRightReceiveCountRate[0]+"\n";
            testText += mRightMacAddress +"==右脚短包 40秒："+mRightReceivePackageCount+",比例"+leftRightReceiveCountRate[1]+"\n\n";
        }
        else {
            testText += mLeftMacAddress +"==左脚长包 40秒："+mLeftReceivePackageCount+",比例"+leftRightReceiveCountRate[0]+"\n";
            testText += mRightMacAddress +"==右脚长包 40秒："+mRightReceivePackageCount+",比例"+leftRightReceiveCountRate[1]+"\n\n";
        }

        tv_test.setText(testText);
        mLeftReceivePackageCount=0;
        mRightReceivePackageCount=0;
        mCurrentTimeMillis_insoleTestPackage = System.currentTimeMillis();
    }

    private String[] getLeftRightReceiveCountRate(){
        String[] leftRightReceiveCountRate = new String[]{"",""};
        float sumLeft = 0;
        for (float i: mLeftReceiveCountRate){
            if (i>1){
                i=1;
            }
            sumLeft += i;
        }

        float sumRight = 0;
        for (float i: mRightReceiveCountRate){
            if (i>1){
                i=1;
            }
            sumRight += i;
        }

        String formatFloatValue_left = "";
        if (mLeftReceiveCountRate.size()>0){
            formatFloatValue_left = MyUtil.getFormatFloatValue(sumLeft / mLeftReceiveCountRate.size()*100, "0.00");
        }
        String formatFloatValue_right = "";
        if (mRightReceiveCountRate.size()>0){
            formatFloatValue_right = MyUtil.getFormatFloatValue(sumRight / mRightReceiveCountRate.size()*100, "0.00");
        }
        leftRightReceiveCountRate[0] = formatFloatValue_left;
        leftRightReceiveCountRate[1] = formatFloatValue_right;
        return leftRightReceiveCountRate;
    }

    int mCurStride = -1;
    int mLeftNoReceiveCount = -1;
    int mRightNoReceiveCount  =-1;
    int mLeftAllStepCount = -1;
    int mRightAllStepCount = -1;

    private void parseAndWriteData(String[] data,int left_right) {
        //int time = (int) (Integer.parseInt(data[12]+data[13]+data[14], 16)*0.025);

        int time = (int) (Integer.parseInt(data[12]+data[13]+data[14], 16)*6.4);  //2017.11.6修改
        //Log.i(TAG,"time:"+time);

        //double time = Integer.parseInt(split[split.length-4]+split[split.length-3]+split[split.length-2], 16)*0.000025;

        /*float conventGyro  = 0.07f;
        float conventAcc = 9.8f*0.000244f;*/

        short gyrX = (short) Integer.parseInt(data[0]+data[1], 16);
        short gyrY = (short) Integer.parseInt(data[2]+data[3], 16);
        short gyrZ = (short) Integer.parseInt(data[4]+data[5], 16);

        float k = 1/6.273f;

        short accX = (short) ((short) Integer.parseInt(data[6]+data[7], 16)*k);
        short accY = (short) ((short) Integer.parseInt(data[8]+data[9], 16)*k);
        short accZ = (short) ((short) Integer.parseInt(data[10]+data[11], 16)*k);

        if (left_right==insole_left) {
            //L 左脚
            //Log.e(TAG,"mPreCacheLeftTime:"+mPreCacheLeftTime+"  mCurLeftTime:"+mCurLeftTime+"  time:"+time);
            if (!mIsLeftHaveData){
                if (mCurLeftTime>0){
                    mPreCacheLeftTime = mCurLeftTime-time;
                }
                else {
                    mCurLeftTime = time;
                }
                mIsLeftHaveData = true;
            }
            else if (mPreCacheLeftTime +time>= mCurLeftTime){ //时间到会重置，需要累加
                mCurLeftTime = mPreCacheLeftTime +time;
            }
            else {
                mPreCacheLeftTime = mCurLeftTime;
                mCurLeftTime += time;
            }

            writeEcgDataToBinaryFile(mCurLeftTime,gyrX,gyrY,gyrZ,accX,accY,accZ,insole_left);
            //Log.i(TAG,"time:"+time+", 左脚 角速度："+gyrX+","+gyrY+","+gyrZ+",  加速度:"+accX+","+accY+","+accZ);
        }
        else if (left_right==insole_right) {
            //R 右脚
            //Log.d(TAG,"============mPreCacheRightTime:"+mPreCacheRightTime+"  mCurRightTime:"+mCurRightTime+"  time:"+time);
            if (!mIsRightHaveData){
                if (mCurRightTime>0){
                    mPreCacheRightTime = mCurRightTime-time;
                }
                else {
                    mCurRightTime = time;
                }
                mIsRightHaveData = true;
            }
            else if (mPreCacheRightTime +time>= mCurRightTime){ //时间到会重置，需要累加
                mCurRightTime = mPreCacheRightTime +time;
            }
            else {
                mPreCacheRightTime = mCurRightTime;
                mCurRightTime += time;
            }

            writeEcgDataToBinaryFile(mCurRightTime,gyrX,gyrY,gyrZ,accX,accY,accZ,insole_right);
            //Log.e(TAG,"time:"+time+", 右脚 角速度："+gyrX+","+gyrY+","+gyrZ+",  加速度:"+accX+","+accY+","+accZ);
        }
    }

    int mPreLeftStepCount = -1;
    int mPreRightStepCount = -1;

    int mCurLeftStepCount = -1;
    int mCurRightStepCount = -1;



    //根据左右脚写道不同的文件里
    private void writeEcgDataToTextFile(double time,float gyrX,float gyrY,float gyrZ,float accX ,float accY,float accZ,int insoleType) {
        if (insoleType==insole_left){
            if (lefFfileWriter==null){
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/insole";
                File file = new File(filePath);
                if (!file.exists()) {
                    boolean mkdirs = file.mkdirs();
                    Log.i(TAG,"mkdirs:"+mkdirs);
                }
                String mLeftInsole30SencendFileAbsolutePath = filePath+"/"+ MyUtil.getECGFileNameDependFormatTime(new Date())+".lf";
                Log.i(TAG,"mLeftInsole30SencendFileAbsolutePath:"+ mLeftInsole30SencendFileAbsolutePath);
                try {
                    lefFfileWriter = new FileWriter(mLeftInsole30SencendFileAbsolutePath, true);
                    leftBufferedWriter = new BufferedWriter(lefFfileWriter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (leftBufferedWriter!=null){
                try {
                    leftBufferedWriter.write(accX+"\r\n");// 往已有的文件上添加字符串
                    leftBufferedWriter.write(accY+"\r\n");// 往已有的文件上添加字符串
                    leftBufferedWriter.write(accZ+"\r\n");// 往已有的文件上添加字符串
                    //bufferedWriter.close();
                    //bufferedWriter.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (insoleType==insole_right){
            if (rightfileWriter==null){
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/insole";
                File file = new File(filePath);
                if (!file.exists()) {
                    boolean mkdirs = file.mkdirs();
                    Log.i(TAG,"mkdirs:"+mkdirs);
                }
                String mLeftInsole30SencendFileAbsolutePath = filePath+"/"+ MyUtil.getECGFileNameDependFormatTime(new Date())+".rg";
                Log.i(TAG,"mLeftInsole30SencendFileAbsolutePath:"+ mLeftInsole30SencendFileAbsolutePath);
                try {
                    rightfileWriter = new FileWriter(mLeftInsole30SencendFileAbsolutePath, true);
                    rightBufferedWriter = new BufferedWriter(rightfileWriter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (rightBufferedWriter!=null){
                try {
                    rightBufferedWriter.write(accX+"\r\n");// 往已有的文件上添加字符串
                    rightBufferedWriter.write(accY+"\r\n");// 往已有的文件上添加字符串
                    rightBufferedWriter.write(accZ+"\r\n");// 往已有的文件上添加字符串
                    //bufferedWriter.close();
                    //bufferedWriter.close();
                } catch (FileNotFoundException e) {
                    //TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    String fileHead="0xc1";

    private int mCurLeftTime = -1;
    private int mCurRightTime = -1;

    private int mPreCacheLeftTime;
    private int mPreCacheRightTime;

    private boolean mIsLeftHaveData;
    private boolean mIsRightHaveData;

    //根据左右脚写道不同的文件里
    private void writeEcgDataToBinaryFile(int time,short gyrX,short gyrY,short gyrZ,short accX ,short accY,short accZ,int insoleType) {
        if (insoleType==insole_left){
            try {
                if (leftDataOutputStream==null){
                    mLeftInsole30SencendFileAbsolutePath = MyUtil.getInsoleLocalFileName(insoleType,new Date());
                    Log.i(TAG,"mLeftInsole30SencendFileAbsolutePath:"+ mLeftInsole30SencendFileAbsolutePath);
                    leftDataOutputStream = new DataOutputStream(new FileOutputStream(mLeftInsole30SencendFileAbsolutePath,true));

                    leftByteBuffer = ByteBuffer.allocate(1);
                    leftByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    leftByteBuffer.put(Integer.valueOf(Integer.decode(fileHead)).byteValue());

                    leftByteBuffer.flip();
                    leftDataOutputStream.write(leftByteBuffer.array());
                    leftByteBuffer.clear();

                    leftByteBuffer = ByteBuffer.allocate(4+2*6);
                    leftByteBuffer.order(ByteOrder.LITTLE_ENDIAN);  //小端模式写入数据



                    if (appAbortDataSaveInsole==null){
                        appAbortDataSaveInsole = new AppAbortDataSaveInsole();
                        appAbortDataSaveInsole.setStartTimeMillis(startTimeMillis);
                        appAbortDataSaveInsole.setOutDoor(mIsOutDoor);
                    }

                    appAbortDataSaveInsole.setmLeftInsoleFileAbsolutePath(mLeftInsole30SencendFileAbsolutePath);

                    writeCorrect3ScendData(insoleType);
                }
                byteBufferWriteData(leftByteBuffer, leftDataOutputStream, time, gyrX, gyrY, gyrZ, accX, accY, accZ);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (insoleType==insole_right){
            try {
                if (rightDataOutputStream==null){
                    mRightInsole30SencendFileAbsolutePath = MyUtil.getInsoleLocalFileName(insoleType,new Date());
                    Log.i(TAG,"mRightInsole30SencendFileAbsolutePath:"+ mRightInsole30SencendFileAbsolutePath);
                    rightDataOutputStream = new DataOutputStream(new FileOutputStream(mRightInsole30SencendFileAbsolutePath,true));

                    rightByteBuffer = ByteBuffer.allocate(1);
                    rightByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    rightByteBuffer.put(Integer.valueOf(Integer.decode(fileHead)).byteValue());

                    rightByteBuffer.flip();
                    rightDataOutputStream.write(rightByteBuffer.array());
                    rightByteBuffer.clear();

                    rightByteBuffer = ByteBuffer.allocate(4+2*6);
                    rightByteBuffer.order(ByteOrder.LITTLE_ENDIAN);  //小端模式写入数据

                    if (appAbortDataSaveInsole==null){
                        appAbortDataSaveInsole = new AppAbortDataSaveInsole();
                        appAbortDataSaveInsole.setStartTimeMillis(startTimeMillis);
                        appAbortDataSaveInsole.setOutDoor(mIsOutDoor);
                    }

                    appAbortDataSaveInsole.setmRightInsoleFileAbsolutePath(mRightInsole30SencendFileAbsolutePath);

                    writeCorrect3ScendData(insoleType);
                }
                byteBufferWriteData(rightByteBuffer, rightDataOutputStream, time, gyrX, gyrY, gyrZ, accX, accY, accZ);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //校准的3s补写到文件
    private void writeCorrect3ScendData(int insoleType) throws IOException {
        Intent intent = getIntent();
        ArrayList<Insole3ScendCache> insole3ScendCacheList = intent.getParcelableArrayListExtra("mInsole3ScendCacheList");

        if (insole3ScendCacheList!=null){
            for (Insole3ScendCache insole3ScendCache:insole3ScendCacheList){
                int time = insole3ScendCache.getTime();

                short gyrX = insole3ScendCache.getGyrX();
                short gyrY = insole3ScendCache.getGyrY();
                short gyrZ = insole3ScendCache.getGyrZ();

                short accX = insole3ScendCache.getAccX();
                short accY = insole3ScendCache.getAccY();
                short accZ = insole3ScendCache.getAccZ();

                if (insole3ScendCache.getFootType()==insole_left && insoleType==insole_left){
                    byteBufferWriteData(leftByteBuffer, leftDataOutputStream, time, gyrX, gyrY, gyrZ, accX, accY, accZ);
                }
                else if (insole3ScendCache.getFootType()==insole_right && insoleType==insole_right){
                    byteBufferWriteData(rightByteBuffer, rightDataOutputStream, time, gyrX, gyrY, gyrZ, accX, accY, accZ);
                }
            }
        }
    }

    private void byteBufferWriteData(ByteBuffer byteBuffer, DataOutputStream dataOutputStream, int time, short gyrX, short gyrY, short gyrZ, short accX, short accY, short accZ) throws IOException {
        if (byteBuffer!=null && dataOutputStream!=null){
            byteBuffer.putInt(time);
            byteBuffer.putShort(gyrX);
            byteBuffer.putShort(gyrY);
            byteBuffer.putShort(gyrZ);
            byteBuffer.putShort(accX);
            byteBuffer.putShort(accY);
            byteBuffer.putShort(accZ);

            byteBuffer.flip();
            dataOutputStream.write(byteBuffer.array());
            byteBuffer.clear();
        }
    }

    int mLeftReceivePackageCount=  0;
    int mRightReceivePackageCount=  0;
    int mLeftReceivePackageCountTest;
    int mRightReceivePackageCountTest;

    String testText="";

    private void sendReadStepOrder(){
        String data  = "B7";
        boolean send1 = LeProxy.getInstance().send(mLeftMacAddress, Constant.insoleSerUuid, Constant.insoleCharUuid, data.getBytes(), false);
        boolean send = LeProxy.getInstance().send(mRightMacAddress, Constant.insoleSerUuid, Constant.insoleCharUuid, data.getBytes(), false);
        Log.i(TAG,"send1:"+send1+",send:"+send);
        mLeftNoReceiveCount++;
        mRightNoReceiveCount++;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_run_sptop:
                rl_run_continue.setVisibility(View.VISIBLE);
                rl_run_stop.setVisibility(View.GONE);
                tv_run_lock.setVisibility(View.GONE);
                break;
            case R.id.rl_insolerun_continue:
                rl_run_continue.setVisibility(View.GONE);
                rl_run_stop.setVisibility(View.VISIBLE);
                tv_run_lock.setVisibility(View.VISIBLE);
                break;

            case R.id.rl_insolerun_end:
                stopRunning();
                break;

            case R.id.iv_run_map:
                startActivity(new Intent(this,RunTrailMapActivity.class));
                break;

            case R.id.tv_run_lock:
                rl_run_lock.setVisibility(View.VISIBLE);
                rl_run_continue.setVisibility(View.GONE);
                rl_run_stop.setVisibility(View.GONE);
                tv_run_lock.setVisibility(View.GONE);

                isLockScreen = true;
                break;
        }
    }

    private void restoreLastRecord() {
        appAbortDataSaveInsole = AppAbortDbAdapterUtil.getAbortDataFromSP(Constant.sportType_Insole);
        Log.i(TAG,"appAbortDataSaveInsole:"+appAbortDataSaveInsole);
        if (appAbortDataSaveInsole!=null){
            sportCreateRecordID = appAbortDataSaveInsole.getMapTrackID();

            if (sportCreateRecordID>0){
                DbAdapter dbAdapter = new DbAdapter(this);
                try {
                    dbAdapter.open();
                }catch (Exception ignored){
                }
                pathRecord = dbAdapter.queryRecordById((int) sportCreateRecordID);
                try {
                    dbAdapter.close();
                }catch (Exception ignored){
                }
                Log.i(TAG,"pathRecord:"+pathRecord);
                long l = MyUtil.parseValidLong(pathRecord.getDuration());
                if (l>0){
                    recoverTimeMillis = addDuration = l;

                    double speed =mAllDistance / l * 3.6;
                    String formatFloatValue = MyUtil.getFormatFloatValue(speed,"0.0");
                    tv_run_avespeed.setText(formatFloatValue);
                }

                mStartTime = System.currentTimeMillis();

                mAllDistance = mAddDistance = Double.parseDouble(pathRecord.getDistance());
                mPreOutDoorDistance = Util.getDistance(pathRecord.getPathline());
                mFormatDistance = MyUtil.getFormatDistance(mAllDistance);
                Log.i(TAG,"mPreOutDoorDistance:"+mPreOutDoorDistance);
                Log.i(TAG,"mAddDistance:"+mAddDistance);
            }

            int kcal = appAbortDataSaveInsole.getKcal();
            mAllKcal = kcal;
            if(mAllKcal>0){
                tv_run_kcal.setText((int)mAllKcal+"");
            }

            mAllStep = mPrestepCount = appAbortDataSaveInsole.getStepCount();

            if(mAllStep>0){
                tv_run_stride.setText(mAllStep+"");
            }

            ArrayList<Integer> speedPaceList = appAbortDataSaveInsole.getSpeedPaceList();
            if (speedPaceList!=null){
                paceList = speedPaceList;
            }

            maxSpeedKM_Hour = appAbortDataSaveInsole.getMaxSpeedKM_Hour();
            if (maxSpeedKM_Hour>0){
                tv_run_maxSpeedKM_Hour.setText(MyUtil.getFormatFloatValue(maxSpeedKM_Hour,"0.0"));
            }

            int curLeftTime = appAbortDataSaveInsole.getCurLeftTime();
            if (curLeftTime>0){
                mCurLeftTime = curLeftTime;
            }

            int curRightTime = appAbortDataSaveInsole.getCurRightTime();
            if (curRightTime>0){
                mCurRightTime = curRightTime;
            }

            int preCacheLeftTime = appAbortDataSaveInsole.getPreCacheLeftTime();
            int preCacheRightTime = appAbortDataSaveInsole.getPreCacheRightTime();
            if (preCacheLeftTime>0){
                mPreCacheLeftTime = preCacheLeftTime;
            }
            if (preCacheRightTime>0){
                mPreCacheRightTime = preCacheRightTime;
            }

            mIsOutDoor = appAbortDataSaveInsole.isOutDoor();

            leftByteBuffer = ByteBuffer.allocate(4+2*6);
            leftByteBuffer.order(ByteOrder.LITTLE_ENDIAN);  //小端模式写入数据

            rightByteBuffer = ByteBuffer.allocate(4+2*6);
            rightByteBuffer.order(ByteOrder.LITTLE_ENDIAN);  //小端模式写入数据

            try {
                if (!MyUtil.isEmpty(appAbortDataSaveInsole.getmLeftInsoleFileAbsolutePath())){
                    leftDataOutputStream = new DataOutputStream(new FileOutputStream(appAbortDataSaveInsole.getmLeftInsoleFileAbsolutePath(),true));
                }
                if (!MyUtil.isEmpty(appAbortDataSaveInsole.getmRightInsoleFileAbsolutePath())){
                    rightDataOutputStream = new DataOutputStream(new FileOutputStream(appAbortDataSaveInsole.getmRightInsoleFileAbsolutePath(),true));
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void setRunningParameter() {
        application = (MyApplication) getApplication();

        application.setRunningRecoverType(Constant.sportType_Insole);
        mIsRunning  =true;

        mCurrTimeDate = new Date(0,0,0);

        application.setRunningCurrTimeDate(mCurrTimeDate = new Date(0,0,0));

        if (startTimeMillis==-1){
            startTimeMillis = System.currentTimeMillis();
        }

        if (isNeedRecoverAbortData){
            isThreeMinute = true;
        }
        else {
            //开启三分钟计时，保存记录最短为3分钟
            // MyTimeTask.startCountDownTimerTask(1000 * 60 * 1, new MyTimeTask.OnTimeOutListener() {
            MyTimeTask.startCountDownTimerTask(minimumLimitTimeMillis, new MyTimeTask.OnTimeOutListener() {
                @Override
                public void onTomeOut() {
                    isThreeMinute = true;
                }
            });
        }
        tv_run_distance.setText(mFormatDistance);

        //开始计时，更新时间
        MyTimeTask.startTimeRiseTimerTask(1000, new MyTimeTask.OnTimeChangeAtScendListener() {
            @Override
            public void onTimeChange(Date date) {
                mCurrTimeDate = new Date(date.getTime()+recoverTimeMillis);
                if (application!=null){
                    application.setRunningCurrTimeDate(mCurrTimeDate);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String specialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", mCurrTimeDate);
                        tv_run_time.setText(specialFormatTime);
                    }
                });
            }
        });

        //开始计时，保存数据到本地，防止app异常，每隔
        saveDeviceOffLineFileUtil = new DeviceOffLineFileUtil();
        saveDeviceOffLineFileUtil.setTransferTimeOverTime(new DeviceOffLineFileUtil.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                if (mIsRunning){
                    Log.i(TAG,"1min 保存数据到本地");

                    if (pathRecord!=null) {
                        sportCreateRecordID = Util.saveOrUdateRecord(pathRecord.getPathline(), addDuration, pathRecord.getDate(), InsoleRunningActivity.this, mStartTime, mAllDistance, sportCreateRecordID);
                        Log.i(TAG, "sportCreateRecordID:" + sportCreateRecordID);


                    }

                    if (sportCreateRecordID!=-1){
                        if (appAbortDataSaveInsole==null){
                            appAbortDataSaveInsole = new AppAbortDataSaveInsole();
                            appAbortDataSaveInsole.setStartTimeMillis(startTimeMillis);
                            appAbortDataSaveInsole.setMapTrackID(sportCreateRecordID);
                            appAbortDataSaveInsole.setOutDoor(mIsOutDoor);

                            saveOrUpdateAbortDatareordToSP(appAbortDataSaveInsole);
                        }
                        else {
                            if (appAbortDataSaveInsole.getMapTrackID()==0){
                                appAbortDataSaveInsole.setMapTrackID(sportCreateRecordID);
                            }

                            if (appAbortDataSaveInsole.getStartTimeMillis()==0){
                                appAbortDataSaveInsole.setStartTimeMillis(System.currentTimeMillis());
                            }

                            appAbortDataSaveInsole.setSpeedPaceList(paceList);
                            appAbortDataSaveInsole.setStepCount(mAllStep+mPrestepCount);
                            appAbortDataSaveInsole.setKcal((int) mAllKcal);
                            appAbortDataSaveInsole.setMaxSpeedKM_Hour(maxSpeedKM_Hour);
                            appAbortDataSaveInsole.setCurLeftTime(mCurLeftTime);
                            appAbortDataSaveInsole.setCurRightTime(mCurRightTime);
                            appAbortDataSaveInsole.setPreCacheLeftTime(mPreCacheLeftTime);
                            appAbortDataSaveInsole.setPreCacheRightTime(mPreCacheRightTime);

                            saveOrUpdateAbortDatareordToSP(appAbortDataSaveInsole);
                        }
                    }


                /*else if (MyUtil.isEmpty(mAbortData.getMapTrackID())){
                    mAbortData.setMapTrackID(createrecord+"");
                    saveOrUpdateAbortDatareordToSP(mAbortData,false);
                }*/
                }
            }
        },saveDataTOLocalTimeSpanSecond);//1min 保存数据到本地
        saveDeviceOffLineFileUtil.startTime();

        String country = Locale.getDefault().getCountry();
        Log.i(TAG,"country:"+country);Locale.CHINA.getCountry();
        if(country.equals(Locale.CHINA.getCountry())){
            //中国
            initMapLoationTrace();
        }
        else {
            //国外
            buildGoogleApiClient();
        }

        if (pathRecord==null){
            pathRecord = new PathRecord();
            mStartTime = System.currentTimeMillis();
            pathRecord.setDate(MyUtil.getCueMapDate(mStartTime));
        }

        startCalSpeedTimerStask();
        String specialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", mCurrTimeDate);
        ShowNotificationBarUtil.setServiceForegrounByNotify(getResources().getString(R.string.running),getResources().getString(R.string.distance)+": "+mFormatDistance+"KM       "+getResources().getString(R.string.exercise_time)+": "+specialFormatTime,ShowNotificationBarUtil.notifyActivityIndex_InsoleRunningActivity);
        Log.i(TAG,"设置通知:"+specialFormatTime);


    }

    /**
     *  @describe 异常中断时数据保存在本地
     *  @param abortData:异常数据对象
     *  @return
     */
    private void saveOrUpdateAbortDatareordToSP(AppAbortDataSaveInsole abortData) {
        AppAbortDbAdapterUtil.putAbortDataToSP(abortData);
    }

    private void backJudge() {
        if (mIsRunning){
            ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(this);
            chooseAlertDialogUtil.setAlertDialogText(getResources().getString(R.string.testing_runing_quit));
            chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
                @Override
                public void onConfirmClick() {
                    //将离线数据记录删除
                   /* List<AppAbortDataSave> abortDataListFromSP = AppAbortDbAdapterUtil.getAbortDataListFromSP();
                    if (abortDataListFromSP!=null && abortDataListFromSP.size()>0){
                        abortDataListFromSP.remove(abortDataListFromSP.size()-1); // 删除最后一个（刚年纪大饿一条记录）
                    }
                    AppAbortDbAdapterUtil.putAbortDataListToSP(abortDataListFromSP);*/
                    //startActivity(new Intent(StartRunActivity.this,MainActivity.class));
                    mIsRunning = false;

                    if (deviceOffLineFileUtil!=null){
                        deviceOffLineFileUtil.stopTime();
                        deviceOffLineFileUtil = null;
                    }
                    if (saveDeviceOffLineFileUtil!=null){
                        saveDeviceOffLineFileUtil.stopTime();
                        saveDeviceOffLineFileUtil = null;
                    }
                    deleteAbortDataRecordFomeSP();

                    if (mlocationClient!=null){
                        mlocationClient.stopLocation();
                        mlocationClient = null;
                    }

                    destorySportInfoTOAPP();
                    finish();
                }
            });
        }
        else {
            //startActivity(new Intent(StartRunActivity.this,MainActivity.class));
            finish();
        }
    }

    private void deleteAbortDataRecordFomeSP(){
        /*Log.i(TAG,"abortDataListFromSP.size():"+abortDataListFromSP.size());
        if (abortDataListFromSP!=null && abortDataListFromSP.size()>0){
            Log.i(TAG,abortDataListFromSP.get(abortDataListFromSP.size()-1)+"");
            abortDataListFromSP.remove(abortDataListFromSP.size()-1);
            AppAbortDbAdapterUtil.putAbortDataListToSP(abortDataListFromSP);
        }
        Log.i(TAG,"abortDataListFromSP.size():"+abortDataListFromSP.size());*/

        //MyUtil.putStringValueFromSP("abortDatas","");
        AppAbortDbAdapterUtil.deleteAbortDataRecordFomeSP();
    }

    //高德地图
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.i(TAG,"onLocationChanged:"+aMapLocation.toString());
        calculateSpeed(aMapLocation);
        calculateDistance(aMapLocation);
    }

    //计算跑步速度，在室内和室外统一采用地图返回的传感器速度
    private void calculateSpeed(AMapLocation aMapLocation) {
        //float speed = aMapLocation.getSpeed()*3.6f;
        if (mIsOutDoor){
            mOutdoorCal8ScendSpeedList.add(aMapLocation);
        }
        else {
            mIndoorCal8ScendSpeedList.add(aMapLocation.getSpeed());
        }
    }

    private long mPaceCurrTimeMillis;
    private double mPaceCurrMeter;
    private int oneKM = 1000;

    private void calculateDistance(AMapLocation aMapLocation) {
        //不关室内室外，首次定位几个点策略：定位5个点，这5个点的距离在100m范围之内属于正常情况，否则为定位不准，重新定位前5个点
        if (pathRecord.getPathline().size()<5){
            pathRecord.addpoint(aMapLocation);
            float distance = Util.getDistance(pathRecord.getPathline());
            if (distance>50){
                pathRecord.getPathline().clear();
            }
            if (pathRecord.getPathline().size()==5){
                mCurrentTimeMillis = System.currentTimeMillis();
            }
            return;
        }

        if (mCurrentTimeMillis==0) {
            mCurrentTimeMillis = System.currentTimeMillis();
        }
        if (mPaceCurrTimeMillis==0) {
            mPaceCurrTimeMillis = System.currentTimeMillis();
        }

        double tempDistance;
        if (mIsOutDoor){
            AMapLocation lastAMapLocation = pathRecord.getPathline().get(pathRecord.getPathline().size() - 1);
            tempDistance = getTwoPointDistance(lastAMapLocation, aMapLocation);
            //Log.i(TAG,"tempDistance:"+tempDistance);
            if (tempDistance<=trackSpeedOutdoorMAX*((System.currentTimeMillis() - mCurrentTimeMillis) / 1000f) && aMapLocation.getSpeed()<=15 && aMapLocation.getAccuracy()<70){
                pathRecord.addpoint(aMapLocation);
                mAllDistance = Util.getDistance(pathRecord.getPathline())+mAddDistance-mPreOutDoorDistance;
                mCurrentTimeMillis = System.currentTimeMillis();

                mPaceCurrMeter += tempDistance;
            }



            /*if (tempDistance<=locationInAccurateCount*trackSpeedOutdoorMAX){   //2个点之间距离小于20m为正常定位情况，否则为噪声去除(正常)
                pathRecord.addpoint(aMapLocation);
                locationInAccurateCount =1;
                //mAllDistance += tempDistance;
                //现在总距离为：总的地图轨迹上距离（室外）+之前异常退出运动的距离（室内）-之前运动轨迹上的距离（室外）
                mAllDistance = Util.getDistance(pathRecord.getPathline())+mAddDistance-mPreOutDoorDistance;
            }
            else {
                locationInAccurateCount++;
            }*/
        }

        else {
            //在室内跑步
            if (aMapLocation.getSpeed()<trackSpeedIndoorMAX){
                tempDistance = aMapLocation.getSpeed() * ((System.currentTimeMillis() - mCurrentTimeMillis) / 1000f); //s=vt,单位:m
                mAddDistance += tempDistance;
                mAllDistance = mAddDistance;

                mPaceCurrMeter += tempDistance;
            }
            mCurrentTimeMillis = System.currentTimeMillis();
        }

        if (mPaceCurrMeter>=oneKM){
            //大于1000时开始计算配速
            int paceCount = (int) (mPaceCurrMeter/oneKM);
            Log.i(TAG,paceCount+"");

            for (int i = 0; i < paceCount; i++) {
                long l = (System.currentTimeMillis() - mPaceCurrTimeMillis) / 1000;
                int oneKMPace = (int) (l / paceCount);
                Log.i(TAG,"i:"+i+" "+oneKMPace);
                paceList.add(oneKMPace);
            }

            mPaceCurrTimeMillis = System.currentTimeMillis();
            mPaceCurrMeter = 0;
        }

        mFormatDistance = MyUtil.getFormatDistance(mAllDistance);
        if(application !=null){
            application.setRunningFormatDistance(mFormatDistance);
        }
        tv_run_distance.setText(mFormatDistance);
        Log.i(TAG,"mAllDistance:"+mAllDistance);

    }

    private double getTwoPointDistance(AMapLocation lastAMapLocation,AMapLocation aMapLocation) {
        LatLng thisLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        LatLng lastLatLng = new LatLng(lastAMapLocation.getLatitude(), lastAMapLocation.getLongitude());
        return (double) AMapUtils.calculateLineDistance(thisLatLng, lastLatLng);
    }

    public static StartRunActivity getInstance(){
        if (mStartRunActivityInstance==null){
            mStartRunActivityInstance = new StartRunActivity();
        }
        return mStartRunActivityInstance;
    }

    private void startCalSpeedTimerStask() {
        deviceOffLineFileUtil = new DeviceOffLineFileUtil();
        deviceOffLineFileUtil.setTransferTimeOverTime(new DeviceOffLineFileUtil.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                if (mIsRunning){
                    Log.i(TAG,"8s 计算速度");
                    startCal8ScendSpeed();
                    startCal8Stridefre();
                }
            }
        },8);//8s 计算速度
        deviceOffLineFileUtil.startTime();
    }

    private void startCal8Stridefre() {
        sendReadStepOrder();
    }

    private float preForOneKMSecond = -1;

    private void startCal8ScendSpeed() {
        String formatSpeed = "--";
        float forOneKMSecond = 0;
        Log.i(TAG,"mOutdoorCal8ScendSpeedList.size():"+ mOutdoorCal8ScendSpeedList.size());
        Log.i(TAG,"mIndoorCal8ScendSpeedList.size():"+ mIndoorCal8ScendSpeedList.size());

        float for8SecondAverageSpeed = 0;
        /*if (mOutdoorCal8ScendSpeedList.size() <= mIndoorCal8ScendSpeedList.size()){
            if (mIndoorCal8ScendSpeedList.size()>0){
                float sum = 0;
                for (float i: mIndoorCal8ScendSpeedList){
                    sum += i;
                }
                for8SecondAverageSpeed = sum/ mIndoorCal8ScendSpeedList.size();
            }
        }
        else {
            float distance = Util.getDistance(mOutdoorCal8ScendSpeedList);
            for8SecondAverageSpeed = distance / 8f;
        }*/

        if(mIsOutDoor){
            float distance = Util.getDistance(mOutdoorCal8ScendSpeedList);
            for8SecondAverageSpeed = distance / 8f;
        }
        else {
            if (mIndoorCal8ScendSpeedList.size()>0){
                float sum = 0;
                for (float i: mIndoorCal8ScendSpeedList){
                    sum += i;
                }
                for8SecondAverageSpeed = sum/ mIndoorCal8ScendSpeedList.size();
            }
        }

        mCurrSpeedKM_Hour = for8SecondAverageSpeed*3.6f;

        if (mCurrSpeedKM_Hour>maxSpeedKM_Hour && mCurrSpeedKM_Hour<10*3.6){
            maxSpeedKM_Hour = mCurrSpeedKM_Hour;
        }

        Log.i(TAG,"for8SecondAverageSpeed:"+for8SecondAverageSpeed);
        Log.i(TAG,"mCurrSpeedKM_Hour:"+mCurrSpeedKM_Hour);

        /*if (for8SecondAverageSpeed>0){
            if (preForOneKMSecond==-1){
                preForOneKMSecond = forOneKMSecond = (1/for8SecondAverageSpeed)*1000f;
            }
            else {
                forOneKMSecond = ((1/for8SecondAverageSpeed)*1000f+preForOneKMSecond)/2;
                preForOneKMSecond = forOneKMSecond;
            }

            Log.i(TAG,"forOneKMSecond:"+forOneKMSecond);

            if (forOneKMSecond>30*60 && forOneKMSecond<50*60){ //配速范围：2~30(分钟/公里)  在30~50之间归为30，小于2归为2，大于50归为0.
                forOneKMSecond = 30*60;
            }
            else if(forOneKMSecond>0 && forOneKMSecond<2*60){
                forOneKMSecond = 2*60;
            }
            else if(forOneKMSecond>50*60){
                forOneKMSecond = 0;
            }

            if (forOneKMSecond >0 && forOneKMSecond<maxSpeedKM_HourScendInt){
                maxSpeedKM_HourScendInt = (int) forOneKMSecond;
            }

            Log.i(TAG,"forOneKMSecond:"+forOneKMSecond);

            if (forOneKMSecond==0){
                formatSpeed = "--";
            }
            else {
                formatSpeed = (int)forOneKMSecond/60+"'"+(int)forOneKMSecond%60+"''";
            }
        }

        Log.i(TAG,"startCal8ScendSpeed:  speed:"+forOneKMSecond+",   formatSpeed:"+formatSpeed);

        mSpeedStringList.add((int) forOneKMSecond);  //speed为秒数，1公里所用的时间*/


        mIndoorCal8ScendSpeedList.clear();
        mOutdoorCal8ScendSpeedList.clear();
        mFinalFormatSpeed = formatSpeed;
        if (application!=null){
            application.setRunningFinalFormatSpeed(mFinalFormatSpeed);
        }
        mHandler.sendEmptyMessage(3);

    }


    private float maxSpeedKM_Hour = 0;
    private float mCurrSpeedKM_Hour = 0;

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 3:
                    updateUISpeedData();
                    break;
            }
            return false;
        }
    });

    private void updateUISpeedData() {

        //tv_run_speed.setText(mFinalFormatSpeed);
        String specialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", mCurrTimeDate);
        ShowNotificationBarUtil.setServiceForegrounByNotify(getResources().getString(R.string.running),getResources().getString(R.string.distance)+": "+mFormatDistance+"KM       "+getResources().getString(R.string.exercise_time)+": "+specialFormatTime,ShowNotificationBarUtil.notifyActivityIndex_InsoleRunningActivity);
        Log.i(TAG,"设置通知:"+specialFormatTime);


        /*float mapRetrurnSpeed =0;
        if (time>0){
            mapRetrurnSpeed = (float) (mAllDistance / time);
        }
        String averageFormatSpeed;
        if (mapRetrurnSpeed==0){
            averageFormatSpeed = "--";
        }
        else {
            float speed = (1/mapRetrurnSpeed)*1000f;
            if (speed>30*60 && speed<50*60){ //配速范围：2~30(分钟/公里)  在30~50之间归为30，小于2归为2，大于50归为0.
                speed = 30*60;
            }
            else if(speed<2*60){
                speed = 2*60;
            }
            else if(speed>50*60){
                speed = 0;
            }

            Log.i(TAG,"speed:"+speed);
            if (speed==0){
                averageFormatSpeed = "--";
            }
            else {
                averageFormatSpeed = (int)speed/60+"'"+(int)speed%60+"''";
            }
        }*/

        long time = (recoverTimeMillis+System.currentTimeMillis()-mStartTime)/1000;
        double speed =mAllDistance / time * 3.6;
        String formatFloatValue = MyUtil.getFormatFloatValue(speed,"0.0");
        tv_run_avespeed.setText(formatFloatValue);

        if (application!=null){
            application.setRunningmCurrentAvespeed(formatFloatValue);
        }



        /*String maxFormatSpeed = "--";
        if (maxSpeedKM_HourScendInt==0 || maxSpeedKM_HourScendInt == Integer.MAX_VALUE){
            maxFormatSpeed = "--";
        }
        else {
            maxFormatSpeed = (int)maxSpeedKM_HourScendInt/60+"'"+(int)maxSpeedKM_HourScendInt%60+"''";
        }*/

        tv_run_maxSpeedKM_Hour.setText(MyUtil.getFormatFloatValue(maxSpeedKM_Hour,"0.0"));



        calculateALlKcal();
    }

    private long preTimeMillis = -1;

    private void calculateALlKcal() {
        /*(1) 已知体重、时间和速度
        跑步热量（kcal）＝体重（kg）×运动时间（小时）×指数K
        指数K＝30÷速度（分钟/400米）
        例如：某人体重60公斤，长跑1小时，速度是3分钟/400米或8公里/小时，那么他跑步过程中消耗的热量＝60×1×30/3=600kcal(千卡)
        此种计算含盖了运动后由于基础代谢率提高所消耗的一部分热量，也就是运动后体温升高所产生的一部分热量。*/


        if (preTimeMillis==-1){
            preTimeMillis = mStartTime;
        }

        Log.i(TAG,"mCurrSpeedKM_Hour:"+mCurrSpeedKM_Hour);
        if (mCurrSpeedKM_Hour>0 && mCurrSpeedKM_Hour<10*3.6){
            float time =8f/60f/60f;  //小时
            preTimeMillis = System.currentTimeMillis();
            float k = 30/(mCurrSpeedKM_Hour*(3/8f));
            int kcal = (int) (MyUtil.getUserWeight()*time*k);

            if (kcal<20){
                mAllKcal += kcal;
                tv_run_kcal.setText(mAllKcal+"");
            }




            Log.i(TAG,"kcal:"+kcal);
            Log.i(TAG,"mAllKcal:"+mAllKcal);
        }


    }

    //初始化定位
    private void initMapLoationTrace() {
        if (mLocationOption==null){
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(2000);
            mLocationOption.setGpsFirst(true);
            mLocationOption.setSensorEnable(true);

            mlocationClient = new AMapLocationClient(this);
            mlocationClient.setLocationListener(this);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    //结束运动
    private void endRunning() {
        Log.i(TAG,"isThreeMinute:"+ isThreeMinute );

        if (isThreeMinute){
            saveSportRecord(sportCreateRecordID);

            Intent intent = new Intent(this, InsoleAnalysisActivity.class);
            intent.putExtra(Constant.leftInsoleFileAbsolutePath,mLeftInsole30SencendFileAbsolutePath);
            intent.putExtra(Constant.rightInsoleFileAbsolutePath,mRightInsole30SencendFileAbsolutePath);
            if (mIsOutDoor){
                intent.putExtra(Constant.sportState,Constant.SPORTSTATE_ATHLETIC);
            }
            else {
                intent.putExtra(Constant.sportState,Constant.SPORTSTATE_INDOOR);
            }
            if (sportCreateRecordID!=-1){
                intent.putExtra(Constant.sportCreateRecordID,sportCreateRecordID);
            }
            if (paceList.size()>0){
                intent.putIntegerArrayListExtra(Constant.paceList,paceList);
            }
            intent.putExtra(Constant.startTimeMillis,startTimeMillis);

            intent.putExtra(Constant.insoleAllKcal,(int) mAllKcal);
            
            intent.putExtra(Constant.maxSpeedKM_Hour,maxSpeedKM_Hour);
            intent.putIntegerArrayListExtra(Constant.stridefreList,stridefreList);

            String[] leftRightReceiveCountRate = getLeftRightReceiveCountRate();

            String mLeftMacAddressLast = "";
            String mRightMacAddressLast = "";
            if (!MyUtil.isEmpty(mLeftMacAddress)){
                mLeftMacAddressLast = mLeftMacAddress.substring(mLeftMacAddress.length() - 2);
            }
            if (!MyUtil.isEmpty(mRightMacAddress)){
                mRightMacAddressLast = mRightMacAddress.substring(mRightMacAddress.length() - 2);
            }


            String curAppVersionString = MyUtil.getVersionName(this);


            String leftDeviceHardware = "";
            String rightDeviceHardware = "";

            String leftDeviceSoftware = "";
            String rightDeviceSoftware = "";

            //MyApplication application = (MyApplication) getApplication();
            Map<String, BleDevice> insoleDeviceBatteryInfos = BleConnectionProxy.getInstance().getmInsoleDeviceBatteryInfos();
            for (BleDevice bleDevice : insoleDeviceBatteryInfos.values()) {
                if (!MyUtil.isEmpty(mLeftMacAddress) && mLeftMacAddress.equals(bleDevice.getMac())){
                    leftDeviceHardware = bleDevice.getHardWareVersion();
                    leftDeviceSoftware = bleDevice.getSoftWareVersion();
                }
                else if(!MyUtil.isEmpty(mRightMacAddress) && mRightMacAddress.equals(bleDevice.getMac())){
                    rightDeviceHardware = bleDevice.getHardWareVersion();
                    rightDeviceSoftware = bleDevice.getSoftWareVersion();
                }
            }

            //测试鞋垫数据收到比率
            /*String deviceVersionInfo = "设备版本{左脚(硬件:"+leftDeviceHardware+",软件:"+leftDeviceSoftware+"),右脚(硬件:"+rightDeviceHardware+",软件:"+rightDeviceSoftware+")}";

            String insoleTag = "手机型号:"+Build.MODEL+", 系统版本"+Build.VERSION.SDK_INT+", 收到数据比率(左脚"+mLeftMacAddressLast+":"+leftRightReceiveCountRate[0]+",右脚"+mRightMacAddressLast+":"+
                    leftRightReceiveCountRate[1]+"), "+deviceVersionInfo+", app版本:"+curAppVersionString;*/
            intent.putExtra(Constant.insoleTag,mAllStep+"");

            startActivity(intent);
            finish();
        }
        else {
            ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(this);
            chooseAlertDialogUtil.setAlertDialogText(getResources().getString(R.string.running_tim_or_distance),getResources().getString(R.string.keep_running),
                    getResources().getString(R.string.end_the_run));
            chooseAlertDialogUtil.setOnCancelClickListener(new ChooseAlertDialogUtil.OnCancelClickListener() {
                @Override
                public void onCancelClick() {
                    mlocationClient.stopLocation();

                    deleteAbortDataRecordFomeSP();
                    finish();
                }
            });
        }

    }

    //记录运动休信息（和地图有关的）
    private void saveSportRecord(long createrecord) {
        Log.i(TAG,"createrecord:"+createrecord);
        mIsRunning = false;
        destorySportInfoTOAPP();

        sportCreateRecordID = Util.saveOrUdateRecord(pathRecord.getPathline(),addDuration, pathRecord.getDate(), this,mStartTime,mAllDistance,createrecord);
        Log.i(TAG,"sportCreateRecordID:"+sportCreateRecordID);
        if (mlocationClient!=null){
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
            mlocationClient = null;
        }

        if (deviceOffLineFileUtil!=null){
            deviceOffLineFileUtil.stopTime();
            deviceOffLineFileUtil = null;
        }
        if (saveDeviceOffLineFileUtil!=null){
            saveDeviceOffLineFileUtil.stopTime();
            saveDeviceOffLineFileUtil = null;
        }
        mCurrTimeDate = null;

        deleteAbortDataRecordFomeSP();
        ShowNotificationBarUtil.detoryServiceForegrounByNotify();

        if ((System.currentTimeMillis()-mPaceCurrTimeMillis)/1000>10){
            paceList.add((int) ((System.currentTimeMillis()-mPaceCurrTimeMillis)/1000)); //最后一公里
        }

        if (leftDataOutputStream!=null){
            try {
                leftDataOutputStream.flush();
                leftDataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            leftDataOutputStream = null;
        }
        if (rightDataOutputStream!=null){
            try {
                rightDataOutputStream.flush();
                rightDataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            rightDataOutputStream = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*if (keyCode == KeyEvent.KEYCODE_BACK ){
            if (mIsRunning){
                return false;
            }
        }*/
        if (isLockScreen){
            return false;
        }
        else {
            backJudge();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        mIsRunning = false;

        MyApplication.runningActivity = MyApplication.MainActivity;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
        ShowNotificationBarUtil.detoryServiceForegrounByNotify();
    }

    //初始化谷歌地图连接
    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(800); //5 seconds
        mLocationRequest.setFastestInterval(800); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        startLocationUpdates();

        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG,"onLocationResult:"+locationResult);
            }
        });
    }

    /**
     * 开始监听位置变化
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    AMapLocation mATempMapLocation;
    @Override
    public void onLocationChanged(Location location) {
        //实时位置信息
        Log.i(TAG,"onLocationChanged:"+location.toString());
        //mTvAddress.setText(mTvAddress.getText().toString()+"\n"+ location.toString());
        mATempMapLocation = new AMapLocation(location);

        calculateSpeed(mATempMapLocation);
        calculateDistance(mATempMapLocation);

        /*LocationManager locationManager =  (LocationManager)getSystemService(LOCATION_SERVICE);
        List<String> list = locationManager.getAllProviders(); //mgr即LocationManager
        Criteria criteria = new Criteria();
        String providerName = locationManager.getBestProvider(criteria, true *//*enabledOnly*//*); //criteria不能填null，否则出现异常
        //LocationProvider provider = locationManager.getProvider(providerName);*/

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void stopRunning() {
        //MyUtil.showToask(this,"结束");
        Log.i(TAG,"mLeftReceivePackageCount:"+mLeftReceivePackageCount);
        Log.i(TAG,"mRightReceivePackageCount:"+mRightReceivePackageCount);

        endRunning();

    }

    boolean isStartCollect;

    public void startCollect(View view) {
        isStartCollect = true;
        mCurrentTimeMillis_insoleTestPackage = System.currentTimeMillis();
        mLeftReceivePackageCount=0;
        mRightReceivePackageCount=0;

        mLeftReceivePackageCountTest=0;
        mRightReceivePackageCountTest=0;

        testText += "开始采集"+"\n";
        tv_test.setText(testText);

        MyUtil.showToask(this,"开始采集");
    }

    public void stopCollect(View view) {
        isStartCollect = false;


        if (lefFfileWriter!=null){
            try {
                lefFfileWriter.close();
                leftBufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (rightfileWriter!=null){
            try {
                rightfileWriter.close();
                rightBufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        rightfileWriter = null;
        lefFfileWriter = null;

        Log.i("30ScendCount","mLeftReceivePackageCount:"+mLeftReceivePackageCount);
        Log.i("30ScendCount","mRightReceivePackageCount:"+mRightReceivePackageCount);

        /*testText += "\n"+ mLeftMacAddress +"左脚     count："+mLeftReceivePackageCount+"\n";
        testText += mRightMacAddress +"右脚     count："+mRightReceivePackageCount+"\n";
        testText += "采集时间:"+ (System.currentTimeMillis()-mCurrentTimeMillis_insoleTest)/(1000*60.0)+"分钟"+"\n";*/

        testText += "\n"+ mLeftMacAddress +"左脚     count："+mLeftReceivePackageCount+"\n";
        testText += mRightMacAddress +"右脚     count："+mRightReceivePackageCount+"\n";
        testText += "采集时间:"+ (System.currentTimeMillis()-mCurrentTimeMillis_insoleTestPackage)/(1000*60.0)+"分钟"+"\n";

        tv_test.setText(testText);



    }


    public void testStep(View view) {
        sendReadStepOrder();
    }



    private void destorySportInfoTOAPP(){
        if(application!=null){
            application.setRunningCurrTimeDate(null);
            application.setRunningFinalFormatSpeed(null);
            application.setRunningRecoverType(-1);
            application.setRunningFormatDistance(null);
            application.setRunningmCurrentHeartRate(0);
            application = null;
        }
    }
}
