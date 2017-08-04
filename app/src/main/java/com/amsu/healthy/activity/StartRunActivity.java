package com.amsu.healthy.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialog;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.OnlineUser;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.AppAbortDbAdapter;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.EcgFilterUtil;
import com.amsu.healthy.utils.EcgFilterUtil_1;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.RunTimerTaskUtil;
import com.amsu.healthy.utils.map.DbAdapter;
import com.amsu.healthy.utils.map.PathRecord;
import com.amsu.healthy.utils.map.Util;
import com.amsu.healthy.utils.wifiTramit.DeviceOffLineFileUtil;
import com.amsu.healthy.view.GlideRelativeView;
import com.ble.api.DataUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.test.utils.DiagnosisNDK;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 */
public class StartRunActivity extends BaseActivity implements AMapLocationListener {
    private static final String TAG = "StartRunActivity";
    private TextView tv_run_speed;
    private TextView tv_run_distance;
    private TextView tv_run_time;
    private TextView tv_run_isoxygen;
    private TextView tv_run_rate;
    private TextView tv_run_stridefre;
    private TextView tv_run_kcal;
    private final int WHAT_TIME_UPDATE = 0;
    private Button bt_run_start;
    private RelativeLayout bt_run_location;
    private Button bt_run_lock;
    public static boolean mIsRunning = false;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    private PathRecord record;    //存放未纠偏轨迹记录信息
    //private List<TraceLocation> mTracelocationlist = new ArrayList<>();   //偏轨后轨迹
    public static long mStartTime;
    private RunTimerTaskUtil runTimerTaskUtil;

    public static long createrecord =-1;
    private int calculateSpeedCount = 10;   //10次，一次2s,即为20s
    private RelativeLayout rl_run_bootom;
    private GlideRelativeView rl_run_glide;
    private RelativeLayout rl_run_lock;
    public static double mAllDistance;
    private double mAddDistance;
    private long mCurrentTimeMillis = 0;
    private TextView tv_run_test;

    private ArrayList<Integer> heartRateDates = new ArrayList<>();  // 心率数组
    
    private boolean isFiveMit = false;   //是否到5分钟
    private boolean isStartThreeMitTimer;  //是否开始三分钟倒计时计时器

    private int currentGroupIndex = 0;   //组的索引
    public static int calGroupCalcuLength = 180; //
    public static int timeSpanGgroupCalcuLength = 60; //
    public static int oneGroupLength = 10; //
    public  int[] calcuEcgRate = new int[calGroupCalcuLength *oneGroupLength]; //1000条数据:（100组，一组有10个数据点）
    private int[] preCalcuEcgRate = new int[calGroupCalcuLength*oneGroupLength]; //前一次数的数据，12s
    private int[] fourCalcuEcgRate = new int[timeSpanGgroupCalcuLength*oneGroupLength]; //4s的数据*/
    private boolean isFirstCalcu = true;  //是否是第一次计算心率，第一次要连续12秒的数据

    private DataOutputStream ecgDataOutputStream;  //二进制文件输出流，写入文件
    private DataOutputStream accDataOutputStream;  //二进制文件输出流，写入文件
    private ByteBuffer ecgByteBuffer;
    private ByteBuffer accByteBuffer;
    private Intent mSendHeartRateBroadcastIntent;
    public static final String action = "jason.broadcast.action";    //发送广播，将心率值以广播的方式放松出去，在其他Activity可以接受
    private long ecgFiletimeMillis =-1;  //开始有心电数据时的秒数，作为心电文件命名。静态变量，在其他界面会用到
    private boolean mHaveOutSideGpsLocation;
    private long mCalKcalCurrentTimeMillis = 0;
    private float mAllKcal;
    private List<Integer> accData = new ArrayList<>();
    public static int accDataLength = 1800;
    private ArrayList<String> mKcalData = new ArrayList<>();
    private ArrayList<Integer> mStridefreData = new ArrayList<>();
    public static int mCurrentHeartRate = 0;
    private String ecgLocalFileName;
    private DeviceOffLineFileUtil deviceOffLineFileUtil;
    private AppAbortDataSave mAbortData;
    private DeviceOffLineFileUtil saveDeviceOffLineFileUtil;

    List<Integer> mSpeedStringList = new CopyOnWriteArrayList<>();
    List<Float> tempSpeedList = new ArrayList<>();
    List<AMapLocation> mGpsCal8ScendSpeedList = new ArrayList<>();
    List<Float> mIndoorCal8ScendSpeedList = new ArrayList<>();
    public static String mFinalFormatSpeed;
    private static ImageView iv_pop_icon;
    private static TextView tv_pop_text;
    int trackEviation_MAX = 20;  //2s内行走的最大距离
    private String mFormatDistance = "0.00";
    private List<AppAbortDataSave> abortDataListFromSP;
    private boolean isNeedRecoverAbortData;
    private long recoverTimeMillis = 0;
    private static final int saveDataTOLocalTimeSpanSecond = 60*1;  //数据持久化时间间隔 1分钟
    private static final int minimumLimitTimeMillis = 1000 * 10 * 1;  //最短时间限制 3分钟
    private long addDuration;
    private EcgFilterUtil_1 ecgFilterUtil_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_start_run);
        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("运动监测");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backJudge();
            }
        });
        setRightText("心电图");
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartRunActivity.this, HealthyDataActivity.class);
                intent.putExtra(Constant.isLookupECGDataFromSport,true);
                startActivity(intent);
            }
        });

        tv_run_speed = (TextView) findViewById(R.id.tv_run_speed);
        tv_run_distance = (TextView) findViewById(R.id.tv_run_distance);
        tv_run_time = (TextView) findViewById(R.id.tv_run_time);
        tv_run_isoxygen = (TextView) findViewById(R.id.tv_run_isoxygen);
        tv_run_rate = (TextView) findViewById(R.id.tv_run_rate);
        tv_run_stridefre = (TextView) findViewById(R.id.tv_run_stridefre);
        tv_run_kcal = (TextView) findViewById(R.id.tv_run_kcal);

        bt_run_start = (Button) findViewById(R.id.bt_run_start);
        bt_run_location = (RelativeLayout) findViewById(R.id.bt_run_location);
        bt_run_lock = (Button) findViewById(R.id.bt_run_lock);

        rl_run_bootom = (RelativeLayout) findViewById(R.id.rl_run_bootom);
        rl_run_lock = (RelativeLayout) findViewById(R.id.rl_run_lock);

        rl_run_glide = (GlideRelativeView) findViewById(R.id.rl_run_glide);


        tv_run_test = (TextView) findViewById(R.id.tv_run_test);

        iv_pop_icon = (ImageView) findViewById(R.id.iv_pop_icon);
        tv_pop_text = (TextView) findViewById(R.id.tv_pop_text);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        bt_run_start.setOnClickListener(myOnClickListener);
        bt_run_location.setOnClickListener(myOnClickListener);
        bt_run_lock.setOnClickListener(myOnClickListener);

        ecgFilterUtil_1 = new EcgFilterUtil_1();


        bt_run_start.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mIsRunning){
                    endRunning();
                }
                return false;
            }
        });

        rl_run_glide.setOnONLockListener(new GlideRelativeView.OnONLockListener() {
            @Override
            public void onLock() {
                rl_run_lock.setVisibility(View.GONE);
                rl_run_bootom.setVisibility(View.VISIBLE);
                getIv_base_leftimage().setClickable(true);
                getTv_base_rightText().setClickable(true);
                isLockScreen = false;
            }
        });
        MyApplication.runningActivity = MyApplication.StartRunActivity;


        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());

        Intent intent = getIntent();
        isNeedRecoverAbortData = intent.getBooleanExtra(Constant.isNeedRecoverAbortData, false);
        if (isNeedRecoverAbortData){
            //需要恢复到之前跑步时的状态

            abortDataListFromSP = AppAbortDbAdapter.getAbortDataListFromSP();
            Log.i(TAG,"abortDataListFromSP:"+abortDataListFromSP);
            Log.i(TAG,"abortDataListFromSP.size():"+abortDataListFromSP.size());
            if (abortDataListFromSP!=null && abortDataListFromSP.size()>0){
                mAbortData = abortDataListFromSP.get(0);
                createrecord = mAbortData.getMapTrackID();
                ecgFiletimeMillis = mAbortData.getStartTimeMillis();

                List<Integer> speedStringList = mAbortData.getSpeedStringList();
                if (speedStringList!=null && speedStringList.size()>0){
                    mSpeedStringList.addAll(speedStringList);
                }
            }

            if (createrecord!=0 && createrecord!=-1){
                DbAdapter dbAdapter = new DbAdapter(this);
                dbAdapter.open();
                record = dbAdapter.queryRecordById((int) createrecord);
                Log.i(TAG,"record:"+record);
                long l = MyUtil.parseValidLong(record.getDuration());
                if (l>0){
                    recoverTimeMillis = addDuration = l;
                }

                mStartTime = System.currentTimeMillis();
                dbAdapter.close();

                mAllDistance = mAddDistance = Double.parseDouble(record.getDistance());
                mFormatDistance = getFormatDistance(mAllDistance);
            }
            ArrayList<String> kcalStringList = mAbortData.getKcalStringList();
            if (kcalStringList!=null && kcalStringList.size()>0){
                mKcalData = kcalStringList;
                for (String s:kcalStringList){
                    mAllKcal += Float.parseFloat(s);
                }
                tv_run_kcal.setText((int)mAllKcal+"");
            }

            ecgLocalFileName  =mAbortData.getEcgFileName();
            ecgByteBuffer = ByteBuffer.allocate(2);
            accByteBuffer = ByteBuffer.allocate(2);

            try {
                if (!MyUtil.isEmpty(mAbortData.getEcgFileName())){
                    ecgDataOutputStream = new DataOutputStream(new FileOutputStream(mAbortData.getEcgFileName(),true));
                }
                if (!MyUtil.isEmpty(mAbortData.getAccFileName())){
                    accDataOutputStream = new DataOutputStream(new FileOutputStream(mAbortData.getAccFileName(),true));
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            setRunningParameter();
        }
    }

    public static void setDeviceConnectedState(boolean deviceConnectedState) {
        if (deviceConnectedState){
            //连接上
            iv_pop_icon.setImageResource(R.drawable.yilianjie);
            tv_pop_text.setText("设备已连接");
        }
        else {
            iv_pop_icon.setImageResource(R.drawable.duankai);
            tv_pop_text.setText("设备连接断开");
        }
    }

    private boolean isLockScreen;

    private void backJudge() {
        if (mIsRunning){
            ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(StartRunActivity.this);
            chooseAlertDialogUtil.setAlertDialogText("正在跑步，是否退出？","按错了","结束跑步");
            chooseAlertDialogUtil.setOnCancelClickListener(new ChooseAlertDialogUtil.OnCancelClickListener() {
                @Override
                public void onCancelClick() {
                    //将离线数据记录删除
                   /* List<AppAbortDataSave> abortDataListFromSP = AppAbortDbAdapter.getAbortDataListFromSP();
                    if (abortDataListFromSP!=null && abortDataListFromSP.size()>0){
                        abortDataListFromSP.remove(abortDataListFromSP.size()-1); // 删除最后一个（刚年纪大饿一条记录）
                    }
                    AppAbortDbAdapter.putAbortDataListToSP(abortDataListFromSP);*/
                    //startActivity(new Intent(StartRunActivity.this,MainActivity.class));
                    deleteAbortDataRecordFomeSP();
                    closeConnectWebSocket();

                    if (deviceOffLineFileUtil!=null){
                        deviceOffLineFileUtil.stopTime();
                        deviceOffLineFileUtil = null;
                    }
                    if (saveDeviceOffLineFileUtil!=null){
                        saveDeviceOffLineFileUtil.stopTime();
                        saveDeviceOffLineFileUtil = null;
                    }

                    finish();
                }
            });
        }
        else {
            //startActivity(new Intent(StartRunActivity.this,MainActivity.class));
            finish();
        }
    }

    boolean isonResumeEd ;

    @Override
    protected void onResume() {
        super.onResume();

        if (!isonResumeEd){
            if (MainActivity.mBluetoothAdapter!=null && !MainActivity.mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
            }
            isonResumeEd = true;
        }
        setDeviceConnectedState(MyApplication.isHaveDeviceConnectted);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.i(TAG,"onLocationChanged:"+aMapLocation.toString());
        Log.i(TAG,"getSpeed:"+aMapLocation.getSpeed());   // meters/second
        /*
        * LOCATION_TYPE_GPS = 1;  GPS定位
        * LOCATION_TYPE_SAME_REQ = 2 缓存定位
        * LOCATION_TYPE_FIX_CACHE = 4  缓存
        * LOCATION_TYPE_WIFI = 5; WiFi定位
        * LOCATION_TYPE_CELL = 6  基站定位
        *
        * */

        /*Log.i(TAG,"aMapLocation.getLocationType():"+aMapLocation.getLocationType());   //
        Log.i(TAG,"aMapLocation.getErrorCode():"+aMapLocation.getErrorCode());   // meters/second

        Log.i(TAG,"calculateSpeed:"+aMapLocation.getSpeed());
        String tyep =aMapLocation.getLocationType()+": ";
        if (aMapLocation.getLocationType()==1){
            tyep += "TYPE_GPS  "+aMapLocation.getSpeed();
        }
        else if (aMapLocation.getLocationType()==2){
            tyep += "SAME_REQ  "+aMapLocation.getSpeed();
        }
        else if (aMapLocation.getLocationType()==4){
            tyep += "FIX_CACHE  "+aMapLocation.getSpeed();
        }
        else if (aMapLocation.getLocationType()==5){
            tyep += "WIFI  "+aMapLocation.getSpeed();
        }
        else if (aMapLocation.getLocationType()==6){
            tyep += "CELL  "+aMapLocation.getSpeed();
        }
        tv_run_test.setText(tyep);*/


        calculateSpeed(aMapLocation);
        calculateDistance(aMapLocation);

        /*if (isFirst){
            mMiddleTime = mStartTime;
            mDistance = 0;
            isFirst = false;
        }
        calculateSpeedCount--;

        if (calculateSpeedCount==0){
            long currentTime = System.currentTimeMillis();
            int tempDistance = (int) Util.getDistance(record.getPathline()) - mDistance;
            String average = Util.getAverage(tempDistance, mMiddleTime, currentTime);
            if (average!=null){
                average = average.equals("")?"0":average;
            }
            mMiddleTime = currentTime;
            mDistance = (int) Util.getDistance(record.getPathline());
            calculateSpeedCount = 10;
            tv_run_speed.setText(average);
            Log.i(TAG,"average2:"+average);
        }*/

    }

    boolean mIsDataStart;

    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                    //if (!mIsRunning)return;
                    //byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);
                    dealwithLebDataChange(DataUtil.byteArrayToHex(intent.getByteArrayExtra(LeProxy.EXTRA_DATA)));
                    break;
            }
        }
    };


    private void dealwithLebDataChange(String hexData) {
        if (hexData.length() < 40) {
            return;
        }

        if (hexData.length() > 40) {
            mIsDataStart = true;
        }

        if (hexData.startsWith("FF 83")) {
            //心电数据
            //Log.i(TAG,"心电hexData:"+hexData);
            dealWithEcgData(hexData);
            isHaveDataTransfer = true;
        } else if (hexData.startsWith("FF 86")) {
            //加速度数据
            //Log.i(TAG,"加速度hexData:"+hexData);
            dealWithAccelerationgData(hexData);
        }
    }

    int [] ecgInts;
    private boolean mIsHaveEcgDataReceived;
    private int mPreHeartRate;
    private boolean isNeedUpdateHeartRate = false;

    //处理心电数据
    private void dealWithEcgData(String hexData) {
        isNeedUpdateHeartRate = false;
        ecgInts = ECGUtil.geIntEcgaArr(hexData, " ", 3, 10); //一次的数据，10位

        if (mIsRunning){
            writeEcgDataToBinaryFile(ecgInts);
        }

        //滤波处理
        for (int i=0;i<ecgInts.length;i++){
            ecgInts[i] = ecgFilterUtil_1.miniEcgFilterLp(ecgFilterUtil_1.miniEcgFilterHp(ecgFilterUtil_1.NotchPowerLine(ecgInts[i], 1)));
        }

        startRealTimeDataTrasmit(ecgInts);

        //Log.i(TAG,"currentGroupIndex:"+currentGroupIndex);

        if (isFirstCalcu){
            if (currentGroupIndex< calGroupCalcuLength){
                //未到时间（1800个数据点计算一次心率）
                System.arraycopy(ecgInts, 0, calcuEcgRate, currentGroupIndex * oneGroupLength, ecgInts.length);
            }
            else{
                isNeedUpdateHeartRate = true;
                isFirstCalcu = false;
            }
        }
        else {
            if (currentGroupIndex < timeSpanGgroupCalcuLength) { //未到4s
                System.arraycopy(ecgInts, 0, fourCalcuEcgRate, currentGroupIndex * oneGroupLength, ecgInts.length);
            } else { //到4s,需要前8s+当前4s
                int i = 0;
                for (int j = timeSpanGgroupCalcuLength * oneGroupLength; j < preCalcuEcgRate.length; j++) {
                    calcuEcgRate[i++] = preCalcuEcgRate[j];
                }
                System.arraycopy(fourCalcuEcgRate, 0, calcuEcgRate, i, fourCalcuEcgRate.length);
                isNeedUpdateHeartRate = true;
            }
        }

        if (isNeedUpdateHeartRate) {
            currentGroupIndex = 0;
            //计算、更新心率，到4s
            mCurrentHeartRate = DiagnosisNDK.ecgHeart(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
            Log.i(TAG, "mCurrentHeartRate:" + mCurrentHeartRate);
            //calcuEcgRate = new int[calGroupCalcuLength*10];

            System.arraycopy(calcuEcgRate, 0, preCalcuEcgRate, 0, calcuEcgRate.length);
            System.arraycopy(ecgInts, 0, fourCalcuEcgRate, currentGroupIndex * 10, ecgInts.length);


            //更新心率
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCurrentHeartRate ==0){
                        tv_run_rate.setText("--");
                        tv_run_isoxygen.setText("--");
                    }
                    else {
                        if (mPreHeartRate>0){
                            int count = 0;
                            int temp = mCurrentHeartRate-mPreHeartRate;
                            if (temp>HealthyDataActivity.D_valueMaxValue) {
                                count = (temp) / HealthyDataActivity.D_valueMaxValue + 1;
                            }
                            else if (temp<-HealthyDataActivity.D_valueMaxValue){
                                count = (temp) / HealthyDataActivity.D_valueMaxValue - 1;
                            }
                            System.out.println(count);
                            if (count>0) {
                                mCurrentHeartRate = mPreHeartRate + Math.abs(temp) / count;
                            }
                        }

                        final String OxygenState = calcuOxygenState(mCurrentHeartRate);
                        tv_run_rate.setText(mCurrentHeartRate+"");
                        tv_run_isoxygen.setText(OxygenState);
                    }
                    heartRateDates.add(mCurrentHeartRate);

                    mPreHeartRate = mCurrentHeartRate;

                    if (mSendHeartRateBroadcastIntent==null){
                        mSendHeartRateBroadcastIntent = new Intent(action);
                    }
                    mSendHeartRateBroadcastIntent.putExtra("data", mCurrentHeartRate);
                    sendBroadcast(mSendHeartRateBroadcastIntent);
                }
            });

            if (mIsRunning){
                if (mCalKcalCurrentTimeMillis==0){
                    mCalKcalCurrentTimeMillis = System.currentTimeMillis();
                }else {
                    long l = System.currentTimeMillis() - mCalKcalCurrentTimeMillis;
                    mCalKcalCurrentTimeMillis = System.currentTimeMillis();
                    float time = (float) (l / (1000 * 60.0));
                    int userSex = MyUtil.getUserSex();
                    int userAge = HealthyIndexUtil.getUserAge();
                    int userWeight = MyUtil.getUserWeight();
                    Log.i(TAG,"time:"+time+",userSex:"+userSex+",userAge:"+userAge+",userWeight"+userWeight);
                    float getkcal = DiagnosisNDK.getkcal(userSex, mCurrentHeartRate, userAge, userWeight, time);
                    Log.i(TAG,"getkcal:"+getkcal);
                    if (getkcal<0){
                        getkcal = 0;
                    }
                    //防止蓝牙断开又重新连上后时间太长导致卡路里很大
                    if (getkcal>6 && mKcalData.size()>0){
                        getkcal = Integer.parseInt(mKcalData.get(mKcalData.size()-1));
                    }

                    mAllKcal += getkcal;
                    mKcalData.add(getkcal+"");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_run_kcal.setText((int)mAllKcal+"");
                        }
                    });
                }
            }

        }

        currentGroupIndex++;


        /*if (currentGroupIndex<calGroupCalcuLength){
            //未到时间（1000个数据点计算一次心率）
            System.arraycopy(ecgInts, 0, calcuEcgRate, currentGroupIndex * oneGroupLength, ecgInts.length);
        }
        else{
            currentGroupIndex = 0;
            //带入公式，计算心率
            //mCurrentHeartRate = ECGUtil.countEcgRate(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
            mCurrentHeartRate = DiagnosisNDK.ecgHeart(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
            Log.i(TAG,"heartRate0:"+ mCurrentHeartRate);
            MyApplication.currentHeartRate = mCurrentHeartRate;
            //calcuEcgRate = new int[calGroupCalcuLength*10];

            heartRateDates.add(mCurrentHeartRate);

            if (mSendHeartRateBroadcastIntent==null){
                mSendHeartRateBroadcastIntent = new Intent(action);
            }
            mSendHeartRateBroadcastIntent.putExtra("data", mCurrentHeartRate);
            sendBroadcast(mSendHeartRateBroadcastIntent);

            if (!mIsRunning)return;

            final String OxygenState = calcuOxygenState(mCurrentHeartRate);

            //更新心率
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCurrentHeartRate ==0){
                        tv_run_rate.setText("--");
                        tv_run_isoxygen.setText("--");
                    }
                    else {

                        tv_run_rate.setText(mCurrentHeartRate+"");
                        tv_run_isoxygen.setText(OxygenState);
                    }


                }
            });
            System.arraycopy(ecgInts, 0, calcuEcgRate, currentGroupIndex * 10 + 0, ecgInts.length);
            if (mCalKcalCurrentTimeMillis==0){
                mCalKcalCurrentTimeMillis = System.currentTimeMillis();
            }else {
                long l = System.currentTimeMillis() - mCalKcalCurrentTimeMillis;
                mCalKcalCurrentTimeMillis = System.currentTimeMillis();
                float time = (float) (l / (1000 * 60.0));
                int userSex = MyUtil.getUserSex();
                int userAge = HealthyIndexUtil.getUserAge();
                int userWeight = MyUtil.getUserWeight();
                Log.i(TAG,"time:"+time+",userSex:"+userSex+",userAge:"+userAge+",userWeight"+userWeight);
                float getkcal = DiagnosisNDK.getkcal(userSex, mCurrentHeartRate, userAge, userWeight, time);
                Log.i(TAG,"getkcal:"+getkcal);
                if (getkcal<0){
                    getkcal = 0;
                }
                //防止蓝牙断开又重新连上后时间太长导致卡路里很大
                if (getkcal>6 && mKcalData.size()>0){
                    getkcal = Integer.parseInt(mKcalData.get(mKcalData.size()-1));
                }

                mAllKcal += getkcal;
                mKcalData.add(getkcal+"");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_run_kcal.setText((int)mAllKcal+"");
                    }
                });
            }
        }
        currentGroupIndex++;*/
    }

    private String calcuOxygenState(int heartRate) {
        int maxRate = 220-HealthyIndexUtil.getUserAge();
        if (heartRate<=maxRate*0.6){
            return "平缓";
        }
        else if (maxRate*0.6<heartRate && heartRate<=maxRate*0.75){
            return "有氧";
        }
        else if (maxRate*0.75<heartRate && heartRate<=maxRate*0.95){
            return "无氧";
        }
        else if (maxRate*0.95<heartRate ){
            return "高危";
        }
        return "有氧";
    }

    //ecg数据写到文件里，二进制方式写入
    private void writeEcgDataToBinaryFile(int[] ints) {
        try {
            if (ecgDataOutputStream==null){
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
                ecgLocalFileName = fileAbsolutePath;
                Log.i(TAG,"fileAbsolutePath:"+fileAbsolutePath);
                //MyUtil.putStringValueFromSP("cacheFileName",fileAbsolutePath);
                ecgDataOutputStream = new DataOutputStream(new FileOutputStream(fileAbsolutePath,true));
                ecgByteBuffer = ByteBuffer.allocate(2);
                if (mAbortData!=null){
                    mAbortData.setEcgFileName(ecgLocalFileName);
                    saveOrUpdateAbortDatareordToSP(mAbortData,false);
                }
                else {
                    mAbortData = new AppAbortDataSave(ecgFiletimeMillis, ecgLocalFileName, "", -1, 1,mSpeedStringList,mKcalData);
                    saveOrUpdateAbortDatareordToSP(mAbortData,true);
                }
            }
            for (int anInt : ints) {
                ecgByteBuffer.clear();
                ecgByteBuffer.putShort((short) anInt);
                ecgDataOutputStream.writeByte(ecgByteBuffer.get(1));
                ecgDataOutputStream.writeByte(ecgByteBuffer.get(0));
            }
            ecgDataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //acc数据写到文件里，二进制方式写入
    private void writeAccDataToBinaryFile(int[] ints) {
        try {
            if (accDataOutputStream==null){
                long accFiletimeMillis = System.currentTimeMillis();
                //String filePath = MyUtil.generateECGFilePath(HealthyDataActivity.this, ecgFiletimeMillis); //随机生成一个ecg格式文件
                //String filePath = getCacheDir()+"/"+MyUtil.getECGFileNameDependFormatTime(new Date())+".ecg";  //随机生成一个文件
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/abluedata";
                File file = new File(filePath);
                if (!file.exists()) {
                    boolean mkdirs = file.mkdirs();
                    Log.i(TAG,"mkdirs:"+mkdirs);
                }
                String fileAbsolutePath = filePath+"/"+MyUtil.getECGFileNameDependFormatTime(new Date())+".acc";
                String accLocalFileName = fileAbsolutePath;
                Log.i(TAG,"fileAbsolutePath:"+fileAbsolutePath);
                //MyUtil.putStringValueFromSP("cacheFileName",fileAbsolutePath);
                accDataOutputStream = new DataOutputStream(new FileOutputStream(fileAbsolutePath,true));
                accByteBuffer = ByteBuffer.allocate(2);
                if (mAbortData!=null){
                    mAbortData.setAccFileName(accLocalFileName);
                    saveOrUpdateAbortDatareordToSP(mAbortData,false);
                }
                else {
                    mAbortData = new AppAbortDataSave(accFiletimeMillis, "", accLocalFileName, -1, 1,mSpeedStringList,mKcalData);
                    saveOrUpdateAbortDatareordToSP(mAbortData,true);
                }
            }
            for (int anInt : ints) {
                accByteBuffer.clear();
                accByteBuffer.putShort((short) anInt);
                accDataOutputStream.writeByte(accByteBuffer.get(1));
                accDataOutputStream.writeByte(accByteBuffer.get(0));
                //accDataOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  @describe 异常中断时数据保存在本地
     *  @param abortData:异常数据对象
     *  @param isSave：保存还是修改 true为保存，false为修改时
     *  @return
     */
    private void saveOrUpdateAbortDatareordToSP(AppAbortDataSave abortData,boolean isSave) {
        if (abortDataListFromSP==null){
            abortDataListFromSP = AppAbortDbAdapter.getAbortDataListFromSP();
        }

        if (!isSave && abortDataListFromSP.size()>0){
            abortDataListFromSP.remove(abortDataListFromSP.size()-1);
        }
        abortDataListFromSP.add(abortData);
        AppAbortDbAdapter.putAbortDataListToSP(abortDataListFromSP);
    }

    private void deleteAbortDataRecordFomeSP(){
        /*Log.i(TAG,"abortDataListFromSP.size():"+abortDataListFromSP.size());
        if (abortDataListFromSP!=null && abortDataListFromSP.size()>0){
            Log.i(TAG,abortDataListFromSP.get(abortDataListFromSP.size()-1)+"");
            abortDataListFromSP.remove(abortDataListFromSP.size()-1);
            AppAbortDbAdapter.putAbortDataListToSP(abortDataListFromSP);
        }
        Log.i(TAG,"abortDataListFromSP.size():"+abortDataListFromSP.size());*/

        MyUtil.putStringValueFromSP("abortDatas","");


    }

    int [] ints;

    //处理加速度数据
    private void dealWithAccelerationgData(String hexData) {
        if (!mIsRunning)return;
        ints = ECGUtil.geIntEcgaArr(hexData, " ", 3, 12); //一次的数据，12位

        writeAccDataToBinaryFile(ints);

        if (accData.size()<accDataLength){
            for (int i:ints){
                accData.add(i);
            }
        }
        else {
            //计算
            byte[] bytes = new byte[accDataLength];
            for (int i=0;i<accData.size();i++){
                bytes[i] = (byte)(int)accData.get(i);
            }
            int[] results = new int[2];
            accData.clear();

            DiagnosisNDK.AnalysisPedo(bytes,accDataLength,results);
            /*int state = -1;
            int pedoCount = -1;

            DiagnosisNDK.AnalysisPedo(bytes,accDataLength,state,pedoCount);

            Log.i(TAG,"state:"+state+",pedoCount:"+pedoCount);*/

            Log.i(TAG,"results: "+results[0]+"  "+results[1]);
            final int stridefre = (int) (results[1] * 5.21); //每分钟的步数
            mStridefreData.add(stridefre);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_run_stridefre.setText(stridefre+"");
                }
            });
        }

        /*String test = "";
        for (int s:ints){
            test += s+" ";
        }
        Log.i(TAG,"test:"+test);*/

        //FF 42 04 77 0F 93 FF 26 04 74 0F 47
        /*int xACC = 0;
        int yACC = 0;
        int zACC = 0;
        for (int i=0;i<ints.length;i++){
            if (i==0 || i==6){
                //X
                if(ints[i]>127)
                {
                    xACC=ints[i]*256+ints[i+1]-65536;
                }
                else
                {
                    xACC=ints[i]*256+ints[i+1];
                }
                //Log.i(TAG,"xACC:"+xACC);

            }
            else if (i==2 || i==8){
                //Y

                if(ints[i]>127)
                {
                    yACC=ints[i]*256+ints[i+1]-65536;
                }
                else
                {
                    yACC=ints[i]*256+ints[i+1];
                }
                //Log.i(TAG,"xACC:"+yACC);
            }
            else if (i==4 || i==10){
                //Z

                if(ints[i]>127)
                {
                    zACC=ints[i]*256+ints[i+1]-65536;
                }
                else
                {
                    zACC=ints[i]*256+ints[i+1];
                }
                //Log.i(TAG,"xACC:"+zACC);
            }
        }*/
        //Log.i(TAG,"xACC:"+xACC+",yACC:"+yACC+",zACC:"+zACC);

    }

    float tempSpeed = 0;

    //计算跑步速度，在室内和室外统一采用地图返回的传感器速度
    private void calculateSpeed(AMapLocation aMapLocation) {
        //float speed = aMapLocation.getSpeed()*3.6f;
        if (aMapLocation.getLocationType()==AMapLocation.LOCATION_TYPE_GPS){
            mGpsCal8ScendSpeedList.add(aMapLocation);
        }
        else {
            mIndoorCal8ScendSpeedList.add(aMapLocation.getSpeed());
        }

        /*if (tempSpeedList.size()>6){
            tempSpeedList.remove(0);
        }
        tempSpeedList.add(aMapLocation.getSpeed());
        float sum = 0;
        for (float i:tempSpeedList){
            sum += i;
        }

        float mapRetrurnSpeed = sum/tempSpeedList.size();


        float speed = 0;
        String formatSpeed;

        if (mapRetrurnSpeed==0){
            formatSpeed = "0'00''";
        }
        else {

            speed = (1/mapRetrurnSpeed)*1000f;

            if (speed>50*60){
                speed = 0;
            }

            formatSpeed = (int)speed/60+"'"+(int)speed%60+"''";
        }

       *//*
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String formatSpeed=decimalFormat.format(speed);//format 返回的是字符串*//*

       *//*if (speed==0){
           speed = tempSpeed + 5;
       }
       else {
           tempSpeed = speed;
       }*//*
        tv_run_speed.setText(formatSpeed);
        mSpeedStringList.add((int) speed);*/
    }

    private int locationInAccurateCount = 1;

    private void calculateDistance(AMapLocation aMapLocation) {
        //不关室内室外，首次定位几个点策略：定位5个点，这5个点的距离在100m范围之内属于正常情况，否则为定位不准，重新定位前5个点
        if (record.getPathline().size()<=5){
            record.addpoint(aMapLocation);
            float distance = Util.getDistance(record.getPathline());
            if (distance>100){
                record.getPathline().clear();
            }
            return;
        }

        //有GPS则归为室外定位
        if(aMapLocation.getLocationType()==AMapLocation.LOCATION_TYPE_GPS){
            mHaveOutSideGpsLocation = true;
        }

        double tempDistance;
        if (mHaveOutSideGpsLocation){
            AMapLocation lastAMapLocation = record.getPathline().get(record.getPathline().size() - 1);
            tempDistance = getTwoPointDistance(lastAMapLocation, aMapLocation);
            Log.i(TAG,"tempDistance:"+tempDistance);

            if (tempDistance<=locationInAccurateCount*trackEviation_MAX){   //2个点之间距离小于20m为正常定位情况，否则为噪声去除(正常)
                record.addpoint(aMapLocation);
                locationInAccurateCount =1;
                //mAllDistance += tempDistance;
                mAllDistance = Util.getDistance(record.getPathline())+mAddDistance;
            }
            else {
                locationInAccurateCount++;
            }
        }
        else {
            //没有室外GPS定位，默认是在室内跑步
            if (mCurrentTimeMillis!=0.0){ //不是第一次
                tempDistance = aMapLocation.getSpeed() * ((System.currentTimeMillis() - mCurrentTimeMillis) / 1000f); //s=vt,单位:m
                mAddDistance += tempDistance;
                mAllDistance = mAddDistance;
            }
            mCurrentTimeMillis = System.currentTimeMillis();
        }

        Log.i(TAG,"mAllDistance:"+mAllDistance);
        mFormatDistance = getFormatDistance(mAllDistance);
        tv_run_distance.setText(mFormatDistance);

    }

    /*private PathRecord tempRecord = new PathRecord();

    int noNocationPointAddCount;

    private void calculateDistance(AMapLocation aMapLocation) {
        //不关室内室外，首次定位几个点策略：定位5个点，这5个点的距离在100m范围之内属于正常情况，否则为定位不准，重新定位前5个点
        int trackeviation_MAX = 100;
        if (tempRecord.getPathline().size()<=5){
            tempRecord.addpoint(aMapLocation);
            float distance = Util.getDistance(record.getPathline());
            if (distance> 2*trackeviation_MAX){
                tempRecord.getPathline().clear();
            }
            else {
                record.setPathline(tempRecord.getPathline());
            }
            return;
        }
        if(aMapLocation.getLocationType()==AMapLocation.LOCATION_TYPE_GPS){
            if (!mHaveOutSideGpsLocation){
                mHaveOutSideGpsLocation = true;
            }
            Log.i(TAG,"record.getPathline().size():"+record.getPathline().size());
            //室外,GPS定位数据，在室外。计算距离方式：1、衣服计步器，2、根据地图返回的距离 ，3、地图利用手机传感器，
            double tempDistance = 0;
            AMapLocation lastAMapLocation = record.getPathline().get(record.getPathline().size() - 1);
            tempDistance = getTwoPointDistance(lastAMapLocation, aMapLocation);
            Log.i(TAG,"tempDistance:"+tempDistance);
            if (tempDistance<100){   //2个点之间距离小于100m为正常定位情况，否则为噪声去除
                mAllDistance += tempDistance;
                record.addpoint(aMapLocation);
                Log.i(TAG,"tempDistance:"+tempDistance);
                Log.i(TAG,"mAllDistance:"+mAllDistance);

                noNocationPointAddCount = 0;
            }
            else {
                Log.i(TAG,"室外大于100,排除点");
                if (noNocationPointAddCount>=10){
                    //连续5次没有加入定位列表，则需要和之前线连接
                    tempRecord.addpoint(aMapLocation);
                    float distance = Util.getDistance(tempRecord.getPathline());
                    if (distance< 2*trackeviation_MAX){
                        for (AMapLocation a:tempRecord.getPathline()){
                            record.addpoint(a);
                        }
                    }
                    tempRecord.getPathline().clear();
                    noNocationPointAddCount = 0;
                }
                else {
                    noNocationPointAddCount++;
                }
            }
        }
        else {
            if (mHaveOutSideGpsLocation){
                //有室外定位，则属于在室外跑步然后遇到GPS信号弱的情况，需要将其他方式定位的经纬度存入列表，防止定位中断
                double tempDistance = 0;
                AMapLocation lastAMapLocation = record.getPathline().get(record.getPathline().size() - 1);
                tempDistance = getTwoPointDistance(lastAMapLocation, aMapLocation);
                Log.i(TAG,"tempDistance:"+tempDistance);
                if (tempDistance< trackeviation_MAX){   //2个点之间距离小于100m为正常定位情况，否则为噪声去除
                    record.addpoint(aMapLocation);
                    noNocationPointAddCount = 0;
                }
                else {
                    Log.i(TAG,"室内大于100,排除点");
                    if (noNocationPointAddCount>=10){
                        //连续5次没有加入定位列表，则需要和之前线连接
                        tempRecord.addpoint(aMapLocation);
                        float distance = Util.getDistance(tempRecord.getPathline());
                        if (distance< 2*trackeviation_MAX){
                            for (AMapLocation a:tempRecord.getPathline()){
                                record.addpoint(a);
                            }
                        }
                        tempRecord.getPathline().clear();
                        noNocationPointAddCount = 0;
                    }
                    else {
                        noNocationPointAddCount++;
                    }
                }
            }
            //室内,非GPS定位，说明信号差，属于室内定位。计算距离方式：1、衣服计步器，3、地图利用手机传感器计算移动速度
             if (mCurrentTimeMillis!=0.0){ //不是第一次
                 double tempDistance = aMapLocation.getSpeed() * ((System.currentTimeMillis() - mCurrentTimeMillis) / 1000f); //s=vt,单位:m
                 mAllDistance += tempDistance;
             }
         }

        mCurrentTimeMillis = System.currentTimeMillis();
        Log.i(TAG,"mAllDistance:"+mAllDistance);
        tv_run_distance.setText(getFormatDistance(mAllDistance));
    }*/


    /**根据高德地图定位的2个点来计算距离
     * @param aMapLocation
     */
    private double getTwoPointDistance(AMapLocation lastAMapLocation,AMapLocation aMapLocation) {
        LatLng thisLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        LatLng lastLatLng = new LatLng(lastAMapLocation.getLatitude(), lastAMapLocation.getLongitude());
        return (double) AMapUtils.calculateLineDistance(thisLatLng, lastLatLng);
    }

    public static String getFormatDistance(double distance) {
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String formatSpeed=decimalFormat.format(distance/1000);//format 返回的是字符串
        return formatSpeed;
    }


    //开始运动
    private void startRunning() {
        if (!mIsRunning){
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // 判断GPS模块是否开启，如果没有则开启
            Log.i(TAG,"gps打开？:"+locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER));
            if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                MyUtil.chooseOpenGps(this);
            }
            else {
                setRunningParameter();
            }
        }
    }

    public static Date mCurrTimeDate;


    private void setRunningParameter() {
        bt_run_start.setText("长按结束");
        bt_run_lock.setVisibility(View.VISIBLE);
        mIsRunning  =true;
        mCurrTimeDate = new Date(0,0,0);
        
        if (isNeedRecoverAbortData){
            isFiveMit = true;
        }
        else {
            //开启三分钟计时，保存记录最短为3分钟
            // MyTimeTask.startCountDownTimerTask(1000 * 60 * 1, new MyTimeTask.OnTimeOutListener() {
            MyTimeTask.startCountDownTimerTask(minimumLimitTimeMillis, new MyTimeTask.OnTimeOutListener() {
                @Override
                public void onTomeOut() {
                    isFiveMit = true;
                }
            });
        }
        tv_run_distance.setText(mFormatDistance);

        //开始计时，更新时间
        MyTimeTask.startTimeRiseTimerTask(this, 1000, new MyTimeTask.OnTimeChangeAtScendListener() {
            @Override
            public void onTimeChange(Date date) {
                mCurrTimeDate = new Date(date.getTime()+recoverTimeMillis);
                String specialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", mCurrTimeDate);
                tv_run_time.setText(specialFormatTime);
            }
        });

        //开始计时，保存数据到本地，防止app异常，每隔
        saveDeviceOffLineFileUtil = new DeviceOffLineFileUtil();
        saveDeviceOffLineFileUtil.setTransferTimeOverTime(new DeviceOffLineFileUtil.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                if (mIsRunning){
                    Log.i(TAG,"1min 保存数据到本地");
                    createrecord = Util.saveOrUdateRecord(record.getPathline(),addDuration, record.getDate(), StartRunActivity.this,mStartTime,mAllDistance,createrecord);
                    Log.i(TAG,"createrecord:"+createrecord);

                    if (createrecord==-1)return;

                    if (mAbortData==null){
                        mAbortData = new AppAbortDataSave(System.currentTimeMillis(), "", "", createrecord, 1,mSpeedStringList);
                        saveOrUpdateAbortDatareordToSP(mAbortData,true);
                    }
                    else {
                        if (mAbortData.getMapTrackID()==-1){
                            mAbortData.setMapTrackID(createrecord);
                        }
                        if (mAbortData.getStartTimeMillis()==0){
                            mAbortData.setStartTimeMillis(System.currentTimeMillis());
                        }
                        mAbortData.setSpeedStringList(mSpeedStringList);
                        mAbortData.setKcalStringList(mKcalData);
                        saveOrUpdateAbortDatareordToSP(mAbortData,false);
                    }
                /*else if (MyUtil.isEmpty(mAbortData.getMapTrackID())){
                    mAbortData.setMapTrackID(createrecord+"");
                    saveOrUpdateAbortDatareordToSP(mAbortData,false);
                }*/
                }
            }
        },saveDataTOLocalTimeSpanSecond);//1min 保存数据到本地
        saveDeviceOffLineFileUtil.startTime();

        initMapLoationTrace();
        startCalSpeedTimerStask();
        String specialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", mCurrTimeDate);
        CommunicateToBleService.setServiceForegrounByNotify("正在跑步","里程："+mFormatDistance+"KM         时长："+specialFormatTime,1);
        Log.i(TAG,"设置通知:"+specialFormatTime);

        connectWebSocket();
    }


    private WebSocketClient mWebSocketClient;
    private String address = "ws://192.168.0.105:8080/WebTest/websocket";
    private boolean isStartDataTransfer;


    /** 初始化WebSocketClient
     *
     * @throws URISyntaxException
     */
    private void initSocketClient() throws URISyntaxException {
        if(mWebSocketClient == null) {
            mWebSocketClient = new WebSocketClient(new URI(address)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    //连接成功
                    Log.i(TAG,"opened connection");

                    User userFromSP = MyUtil.getUserFromSP();
                    String testIconUrl = userFromSP.getIcon();
                    String username = userFromSP.getUsername();
                    String area = userFromSP.getArea();
                    String sex = userFromSP.getSex();
                    int userAge = HealthyIndexUtil.getUserAge();

                    /*OnlineUser onlineUser = new OnlineUser(testIconUrl,userFromSP.getUsername(),1);
                    Gson gson = new Gson();
                    JsonBase jsonBase = new JsonBase();
                    jsonBase.setRet(0);
                    jsonBase.setErrDesc(onlineUser);

                    String msg = gson.toJson(jsonBase);*/
                    //F1,http://119.29.201.120:83/usericons/f81241db11c869f3c8e57ff96538abbc.png,1,天空之城
                    String sexString;
                    if (sex.equals("1")){
                        sexString = "男";
                    }
                    else {
                        sexString = "女";
                    }
                    String msg = "F1,"+testIconUrl+",1,"+username+","+area+","+sexString+","+userAge+"岁";

                    sendSocketMsg(msg);
                }

                @Override
                public void onMessage(String s) {
                    //服务端消息
                    Log.i(TAG,"received:" + s);
                    //F1,服务器正常

                    /*Gson gson = new Gson();
                    JsonBase jsonBase = gson.fromJson(s, JsonBase.class);
                    Log.i(TAG,"jsonBase："+jsonBase.toString());
                    if (jsonBase.getRet()==1){
                        //开始实时数据传输
                        isStartDataTransfer = true;

                    }*/

                    String[] split = s.split(",");

                    if (split.length > 0 && split[0].equals("F1")){
                        //开始实时数据传输
                        isStartDataTransfer = true;
                    }
                    else if (split.length > 0 &&split[0].equals("F5")){
                        //关闭实时数据传输
                        isStartDataTransfer = false;
                    }
                }

                @Override
                public void onClose(int i, String s, boolean remote) {
                    //连接断开，remote判定是客户端断开还是服务端断开
                    Log.i(TAG,"Connection closed by " + ( remote ? "remote peer" : "us" ) + ", info=" + s);
                    //
                    //closeConnect();
                }


                @Override
                public void onError(Exception e) {
                    Log.i(TAG,"error:" + e);
                }
            };
        }
    }

    JsonBase jsonBase = new JsonBase();
    Gson gson = new Gson();
    String intString;
    private void startRealTimeDataTrasmit(int [] ints) {
        if (isStartDataTransfer){
            intString = "F0,";
            for (int i:ints){
                intString+=i+",";
            }
            /*jsonBase.setRet(1);
            jsonBase.setErrDesc(intString.substring(0,intString.length()-1));
            sendSocketMsg(gson.toJson(jsonBase));*/
            //F0,28,18,6,-2,-3,0,3,5,1,-1
            sendSocketMsg(intString);
        }

    }

    private void sendSocketMsg(String msg) {
        if (mWebSocketClient!=null && mWebSocketClient.isOpen()){
            mWebSocketClient.send(msg);
            Log.i(TAG,"msg:"+msg);
        }
    }

    //连接
    private void connectWebSocket() {
        try {
            initSocketClient();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        new Thread(){
            @Override
            public void run() {
                mWebSocketClient.connect();
            }
        }.start();
    }


    //断开连接
    private void closeConnectWebSocket() {
        try {
            if(mWebSocketClient!=null){
                mWebSocketClient.close();
                mWebSocketClient = null;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            mWebSocketClient = null;
            isStartDataTransfer = false;
        }
    }

    private void startCalSpeedTimerStask() {
        deviceOffLineFileUtil = new DeviceOffLineFileUtil();
        deviceOffLineFileUtil.setTransferTimeOverTime(new DeviceOffLineFileUtil.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                if (mIsRunning){
                    Log.i(TAG,"8s 计算速度");
                    startCal7ScendSpeed();
                }
            }
        },8);//8s 计算速度

        deviceOffLineFileUtil.startTime();
    }

    private void startCal7ScendSpeed() {
        String formatSpeed = "--";
        float forOneKMSecond = 0;
        Log.i(TAG,"mGpsCal8ScendSpeedList.size():"+ mGpsCal8ScendSpeedList.size());
        Log.i(TAG,"mIndoorCal8ScendSpeedList.size():"+ mIndoorCal8ScendSpeedList.size());

        float for8SecondAverageSpeed = 0;
        if (mGpsCal8ScendSpeedList.size() <= mIndoorCal8ScendSpeedList.size()){
            if (mIndoorCal8ScendSpeedList.size()>0){
                float sum = 0;
                for (float i: mIndoorCal8ScendSpeedList){
                    sum += i;
                }
                for8SecondAverageSpeed = sum/ mIndoorCal8ScendSpeedList.size();
            }
        }
        else {
            float distance = Util.getDistance(mGpsCal8ScendSpeedList);
            for8SecondAverageSpeed = distance / 8f;
        }

        if (for8SecondAverageSpeed>0){
            forOneKMSecond = (1/for8SecondAverageSpeed)*1000f;
            if (forOneKMSecond>30*60){
                forOneKMSecond = 0;
            }

            if (forOneKMSecond==0){
                formatSpeed = "--";
            }
            else {
                formatSpeed = (int)forOneKMSecond/60+"'"+(int)forOneKMSecond%60+"''";
            }
        }

        Log.i(TAG,"startCal7ScendSpeed:  speed:"+forOneKMSecond+",   formatSpeed:"+formatSpeed);

        mSpeedStringList.add((int) forOneKMSecond);  //speed为秒数，1公里所用的时间
        

        mIndoorCal8ScendSpeedList.clear();
        mGpsCal8ScendSpeedList.clear();

        mFinalFormatSpeed = formatSpeed;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_run_speed.setText(mFinalFormatSpeed);
                String specialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", mCurrTimeDate);
                CommunicateToBleService.setServiceForegrounByNotify("正在跑步","里程："+mFormatDistance+"KM         时长："+specialFormatTime,1);
                Log.i(TAG,"设置通知:"+specialFormatTime);
            }
        });
    }

    boolean isHaveDataTransfer;

    //结束运动
    private void endRunning() {
        Log.i(TAG,"isFiveMit:"+isFiveMit+", isHaveDataTransfer:"+isHaveDataTransfer);
        ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(this);
        if (isFiveMit){
            if (isHaveDataTransfer){
                chooseAlertDialogUtil.setAlertDialogText("采集恢复心率需要继续采集一分钟的心电数据，是否进行采集？","是","否");
                chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick() {
                        saveSportRecord(createrecord);
                        Intent intent = new Intent(StartRunActivity.this, CalculateHRRProcessActivity.class);
                        intent.putExtra(Constant.sportState,Constant.SPORTSTATE_ATHLETIC);
                        if (!MyUtil.isEmpty(ecgLocalFileName)){
                            intent.putExtra(Constant.ecgLocalFileName,ecgLocalFileName);
                        }
                        if (createrecord!=-1){
                            intent.putExtra(Constant.sportCreateRecordID,createrecord);
                        }
                        if (heartRateDates.size()>0){
                            intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
                            intent.putExtra(Constant.ecgFiletimeMillis,ecgFiletimeMillis);
                        }
                        if (mKcalData.size()>0){
                            intent.putStringArrayListExtra(Constant.mKcalData,mKcalData);
                        }
                        if (mStridefreData.size()>0){
                            intent.putIntegerArrayListExtra(Constant.mStridefreData,mStridefreData);
                        }
                        if (mSpeedStringList.size()>0){
                            /*List<Integer> a = new CopyOnWriteArrayList<Integer>();
                            ArrayList<Integer> b = new ArrayList<Integer>();
                            b.addAll(a);*/
                            ArrayList<Integer> tempList = new ArrayList<>();
                            tempList.addAll(mSpeedStringList);
                            intent.putIntegerArrayListExtra(Constant.mSpeedStringListData,tempList);
                        }
                        MyApplication.mActivities.add(StartRunActivity.this);
                        startActivity(intent);
                        //finish();
                    }
                });
                chooseAlertDialogUtil.setOnCancelClickListener(new ChooseAlertDialogUtil.OnCancelClickListener() {
                    @Override
                    public void onCancelClick() {
                        saveSportRecord(createrecord);
                        Intent intent = new Intent(StartRunActivity.this, HeartRateActivity.class);
                        intent.putExtra(Constant.sportState,Constant.SPORTSTATE_ATHLETIC);
                        if (!MyUtil.isEmpty(ecgLocalFileName)){
                            intent.putExtra(Constant.ecgLocalFileName,ecgLocalFileName);
                        }
                        if (createrecord!=-1){
                            intent.putExtra(Constant.sportCreateRecordID,createrecord);
                        }
                        if (heartRateDates.size()>0){
                            intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
                            intent.putExtra(Constant.ecgFiletimeMillis,ecgFiletimeMillis);
                        }
                        if (mKcalData.size()>0){
                            intent.putStringArrayListExtra(Constant.mKcalData,mKcalData);
                        }
                        if (mStridefreData.size()>0){
                            intent.putIntegerArrayListExtra(Constant.mStridefreData,mStridefreData);
                        }
                        if (mSpeedStringList.size()>0){
                            ArrayList<Integer> tempList = new ArrayList<>();
                            tempList.addAll(mSpeedStringList);
                            intent.putIntegerArrayListExtra(Constant.mSpeedStringListData,tempList);
                        }
                        startActivity(intent);
                        finish();
                    }
                });
            }else {
                saveSportRecord(createrecord);
                Intent intent = new Intent(StartRunActivity.this, HeartRateActivity.class);
                intent.putExtra(Constant.sportState,Constant.SPORTSTATE_ATHLETIC);
                if (!MyUtil.isEmpty(ecgLocalFileName)){
                    intent.putExtra(Constant.ecgLocalFileName,ecgLocalFileName);
                }
                if (createrecord!=-1){
                    intent.putExtra(Constant.sportCreateRecordID,createrecord);
                }
                if (heartRateDates.size()>0){
                    intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
                    intent.putExtra(Constant.ecgFiletimeMillis,ecgFiletimeMillis);
                }
                if (mKcalData.size()>0){
                    intent.putStringArrayListExtra(Constant.mKcalData,mKcalData);
                }
                if (mStridefreData.size()>0){
                    intent.putIntegerArrayListExtra(Constant.mStridefreData,mStridefreData);
                }
                if (mSpeedStringList.size()>0){
                    ArrayList<Integer> tempList = new ArrayList<Integer>();
                    tempList.addAll(mSpeedStringList);

                    intent.putIntegerArrayListExtra(Constant.mSpeedStringListData,tempList);
                }
                Log.i(TAG,"startActivity");
                startActivity(intent);
                finish();
            }
        }
        else {
            chooseAlertDialogUtil.setAlertDialogText("跑步时间或距离太短，无法保存记录，是否继续跑步？","继续跑步","结束跑步");
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
        mIsRunning = false;
        createrecord = Util.saveOrUdateRecord(record.getPathline(),addDuration, record.getDate(), this,mStartTime,mAllDistance,createrecord);
        Log.i(TAG,"createrecord:"+createrecord);
        if (mlocationClient!=null){
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
            mlocationClient = null;
        }

        ecgDataOutputStream = null;
        accDataOutputStream = null;
        if (deviceOffLineFileUtil!=null){
            deviceOffLineFileUtil.stopTime();
            deviceOffLineFileUtil = null;
        }
        if (saveDeviceOffLineFileUtil!=null){
            saveDeviceOffLineFileUtil.stopTime();
            saveDeviceOffLineFileUtil = null;
        }



        deleteAbortDataRecordFomeSP();
        CommunicateToBleService.detoryServiceForegrounByNotify();

        closeConnectWebSocket();

    }

    //初始化定位
    private void initMapLoationTrace() {
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mLocationOption.setGpsFirst(true);
        mLocationOption.setSensorEnable(true);

        mlocationClient = new AMapLocationClient(this);
        mlocationClient.setLocationListener(this);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();

        if (record==null){
            record = new PathRecord();
            mStartTime = System.currentTimeMillis();
            record.setDate(MyUtil.getCueMapDate(mStartTime));
        }
    }

    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_run_start:
                    startRunning();
                    break;

                case R.id.bt_run_lock:
                    rl_run_lock.setVisibility(View.VISIBLE);
                    rl_run_bootom.setVisibility(View.GONE);

                    getIv_base_leftimage().setClickable(false);
                    getTv_base_rightText().setClickable(false);
                    isLockScreen = true;
                    break;

                case R.id.bt_run_location:
                    startActivity(new Intent(StartRunActivity.this,RunTrailMapActivity.class));
                    break;
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*if (keyCode == KeyEvent.KEYCODE_BACK ){
            if (mIsRunning){
                return false;
            }
        }*/
        if (isLockScreen)return false;
        backJudge();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        mIsRunning = false;
        /*if (MainActivity.mBluetoothAdapter!=null){
            MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
        }*/

        MyApplication.runningActivity = MyApplication.MainActivity;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
        CommunicateToBleService.detoryServiceForegrounByNotify();
    }
}
