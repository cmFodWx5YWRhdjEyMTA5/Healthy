package com.amsu.healthy.activity;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.AppAbortDbAdapterUtil;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.EcgFilterUtil_1;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.RunTimerTaskUtil;
import com.amsu.healthy.utils.WebSocketUtil;
import com.amsu.healthy.utils.map.DbAdapter;
import com.amsu.healthy.utils.map.PathRecord;
import com.amsu.healthy.utils.map.Util;
import com.amsu.healthy.utils.wifiTramit.DeviceOffLineFileUtil;
import com.amsu.healthy.view.GlideRelativeView;
import com.ble.api.DataUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.test.utils.DiagnosisNDK;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 */
public class StartRunActivity extends BaseActivity implements AMapLocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private static final String TAG = "StartRunActivity";
    private static StartRunActivity mStartRunActivityInstance;
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
    public   boolean mIsRunning = false;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    private PathRecord pathRecord;    //存放未纠偏轨迹记录信息
    //private List<TraceLocation> mTracelocationlist = new ArrayList<>();   //偏轨后轨迹
    public long mStartTime;
    private RunTimerTaskUtil runTimerTaskUtil;

    public long createrecord =-1;
    private int calculateSpeedCount = 10;   //10次，一次2s,即为20s
    private RelativeLayout rl_run_bootom;
    private GlideRelativeView rl_run_glide;
    private RelativeLayout rl_run_lock;
    public  double mAllDistance;
    private double mAddDistance;
    private long mCurrentTimeMillis = 0;
    private TextView tv_run_test;

    private ArrayList<Integer> heartRateDates = new ArrayList<>();  // 心率数组

    private boolean isThreeMinute = false;   //是否到5分钟
    private boolean isStartThreeMitTimer;  //是否开始三分钟倒计时计时器


    public static final int accDataLength = 1800;
    private static final int saveDataTOLocalTimeSpanSecond = Constant.saveDataTOLocalTimeSpanSecond;  //数据持久化时间间隔 1分钟
    private static final int minimumLimitTimeMillis = 1000 * 10 * 1;  //最短时间限制 3分钟
    public static final String action = "jason.broadcast.action";    //发送广播，将心率值以广播的方式放松出去，在其他Activity可以接受

    private int currentGroupIndex = 0;   //组的索引
    public static final int calGroupCalcuLength = 180; //
    public static final int timeSpanGgroupCalcuLength = 60; //
    public static final int oneGroupLength = 10; //
    public static final int accOneGroupLength = 12; //
    public  int[] calcuEcgRate = new int[calGroupCalcuLength *oneGroupLength]; //1000条数据:（100组，一组有10个数据点）
    private int[] preCalcuEcgRate = new int[calGroupCalcuLength*oneGroupLength]; //前一次数的数据，12s
    private int[] fourCalcuEcgRate = new int[timeSpanGgroupCalcuLength*oneGroupLength]; //4s的数据*/
    private boolean isFirstCalcu = true;  //是否是第一次计算心率，第一次要连续12秒的数据

    private DataOutputStream ecgDataOutputStream;  //二进制文件输出流，写入文件
    private DataOutputStream accDataOutputStream;  //二进制文件输出流，写入文件
    private ByteBuffer ecgByteBuffer;
    private ByteBuffer accByteBuffer;
    private Intent mSendHeartRateBroadcastIntent;

    private long startTimeMillis =-1;  //开始有心电数据时的秒数，作为心电文件命名。静态变量，在其他界面会用到
    private boolean mHaveOutSideGpsLocation;
    private long mCalKcalCurrentTimeMillis = 0;
    private float mAllKcal;


    private List<String> mKcalData ;
    private ArrayList<Integer> mStridefreData ;
    public  int mCurrentHeartRate = 0;
    private String ecgLocalFileName;
    private DeviceOffLineFileUtil deviceOffLineFileUtil;
    private AppAbortDataSave mAbortData;
    private DeviceOffLineFileUtil saveDeviceOffLineFileUtil;

    private List<Integer> mSpeedStringList;
    private List<AMapLocation> mOutdoorCal8ScendSpeedList;
    private List<Float> mIndoorCal8ScendSpeedList ;
    public  String mFinalFormatSpeed;
    private ImageView iv_pop_icon;
    private TextView tv_pop_text;
    private final int trackSpeedOutdoorMAX = 10;  //1s内行走的最大速度
    private final int trackSpeedIndoorMAX = 10;  //1s内行走的最大速度
    public String mFormatDistance = "0.00";
    //private List<AppAbortDataSave> abortDataListFromSP;
    private boolean isNeedRecoverAbortData;
    private long recoverTimeMillis = 0;

    private long addDuration;
    private EcgFilterUtil_1 ecgFilterUtil_1;
    private float mPreOutDoorDistance;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private int mUserSex;
    private int mUserAge;
    private int mUserWeight;
    private int mTempStridefre;
    private MyApplication application;

    //private WebSocketClient mWebSocketClient;
    private WebSocketUtil mWebSocketUtil;
    private boolean mIsOutDoor;
    /*//private String address = "ws://192.168.0.108:8080/sportMonitor/websocket";
    private String address = "ws://192.168.0.110:8080//sportMonitor/websocket";
    private boolean isStartDataTransfer;
    private String mCurAppClientID;
    private String mCurBrowserClientID;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_start_run);
        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.motion_detection));
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backJudge();
            }
        });
        setRightText(getResources().getString(R.string.ecg_graph));
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

        mAllDistance = 0;
        mSpeedStringList = new CopyOnWriteArrayList<>();
        mOutdoorCal8ScendSpeedList = new ArrayList<>();
        mIndoorCal8ScendSpeedList = new ArrayList<>();
        mStridefreData = new ArrayList<>();
        mKcalData = new CopyOnWriteArrayList<>();

        mIsOutDoor = getIntent().getBooleanExtra(Constant.mIsOutDoor, false);
        Log.i(TAG,"mIsOutDoor:"+mIsOutDoor);

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());

        Intent intent = getIntent();
        isNeedRecoverAbortData = intent.getBooleanExtra(Constant.isNeedRecoverAbortData, false);
        if (isNeedRecoverAbortData){
            //需要恢复到之前跑步时的状态
            restoreLastRecord();
        }
        startRunning();
    }

    private void restoreLastRecord() {
        mAbortData = AppAbortDbAdapterUtil.getAbortDataFromSP(Constant.sportType_Cloth);
        Log.i(TAG,"mAbortData:"+mAbortData);
        if (mAbortData!=null){
            createrecord = mAbortData.getMapTrackID();
            startTimeMillis = mAbortData.getStartTimeMillis();
            mIsOutDoor = mAbortData.isOutDoor;

            List<Integer> speedStringList = mAbortData.getSpeedStringList();
            if (speedStringList!=null && speedStringList.size()>0){
                mSpeedStringList.addAll(speedStringList);
            }
            if (createrecord>0){
                DbAdapter dbAdapter = new DbAdapter(this);
                try {
                    dbAdapter.open();
                }catch (Exception ignored){
                }
                pathRecord = dbAdapter.queryRecordById((int) createrecord);
                try {
                    dbAdapter.close();
                }catch (Exception ignored){
                }
                Log.i(TAG,"pathRecord:"+pathRecord);
                long l = MyUtil.parseValidLong(pathRecord.getDuration());
                if (l>0){
                    recoverTimeMillis = addDuration = l;
                }

                mStartTime = System.currentTimeMillis();

                mAllDistance = mAddDistance = Double.parseDouble(pathRecord.getDistance());
                mPreOutDoorDistance = Util.getDistance(pathRecord.getPathline());
                mFormatDistance = MyUtil.getFormatDistance(mAllDistance);
                Log.i(TAG,"mPreOutDoorDistance:"+mPreOutDoorDistance);
                Log.i(TAG,"mAddDistance:"+mAddDistance);
            }
            List<String> kcalStringList = mAbortData.getKcalStringList();
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
        }
    }


    public  Date mCurrTimeDate;

    private void setRunningParameter() {
        bt_run_start.setText(R.string.long_press_end);
        bt_run_lock.setVisibility(View.VISIBLE);

        application = (MyApplication) getApplication();

        application.setRunningRecoverType(Constant.sportType_Cloth);
        mIsRunning  =true;
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
        MyTimeTask.startTimeRiseTimerTask(this, 1000, new MyTimeTask.OnTimeChangeAtScendListener() {
            @Override
            public void onTimeChange(Date date) {
                mCurrTimeDate = new Date(date.getTime()+recoverTimeMillis);
                if (application!=null){
                    application.setRunningCurrTimeDate(mCurrTimeDate);
                }
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

                    if (pathRecord!=null){
                        createrecord = Util.saveOrUdateRecord(pathRecord.getPathline(),addDuration, pathRecord.getDate(), StartRunActivity.this,mStartTime,mAllDistance,createrecord);
                        Log.i(TAG,"createrecord:"+createrecord);
                    }

                    if (mAbortData==null){
                        mAbortData = new AppAbortDataSave(startTimeMillis, "", "", createrecord, 1,mSpeedStringList);
                        mAbortData.isOutDoor = mIsOutDoor;
                        saveOrUpdateAbortDatareordToSP(mAbortData,true);
                    }
                    else {
                        if (mAbortData.getMapTrackID()==0){
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
        CommunicateToBleService.setServiceForegrounByNotify(getResources().getString(R.string.running),getResources().getString(R.string.distance)+": "+mFormatDistance+"KM       "+getResources().getString(R.string.exercise_time)+": "+specialFormatTime,1);
        Log.i(TAG,"设置通知:"+specialFormatTime);

        boolean mIsAutoMonitor = MyUtil.getBooleanValueFromSP("mIsAutoMonitor");
        if (mIsAutoMonitor){
            mWebSocketUtil = ((MyApplication) getApplication()).getWebSocketUtil();
            /*if (mWebSocketUtil!=null){
                mWebSocketClient = mWebSocketUtil.mWebSocketClient;
            }*/

            if (mWebSocketUtil!=null){
                Log.i(TAG,"mWebSocketClient:"+mWebSocketUtil.mWebSocketClient);
                String sendStartRunningState = "A5,"+mWebSocketUtil.mCurAppClientID;
                Log.i(TAG,"开始跑步："+sendStartRunningState);
                mWebSocketUtil.sendSocketMsg(sendStartRunningState);
            }
        }


    }


    public void setDeviceConnectedState(boolean deviceConnectedState) {
        if (deviceConnectedState){
            //连接上
            iv_pop_icon.setImageResource(R.drawable.yilianjie);
            tv_pop_text.setText(R.string.sportswear_connection_successful);
        }
        else {
            iv_pop_icon.setImageResource(R.drawable.duankai);
            tv_pop_text.setText(R.string.sportswear_connection_disconnected);
        }
    }

    private boolean isLockScreen;

    private void backJudge() {
        if (mIsRunning){
            ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(StartRunActivity.this);
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
                    //closeConnectWebSocket();

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

    //高德地图
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //Log.i(TAG,"onLocationChanged:"+aMapLocation.toString());
        //Log.i(TAG,"aMapLocation.getSpeed():"+aMapLocation.getSpeed());
        calculateSpeed(aMapLocation);
        calculateDistance(aMapLocation);

        //Log.i(TAG,"getSpeed:"+aMapLocation.getSpeed());   // meters/second
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
                case LeProxy.ACTION_GATT_CONNECTED:
                    Log.i(TAG,"已连接 " );
                    setDeviceConnectedState(true);
                    break;
                case LeProxy.ACTION_GATT_DISCONNECTED:
                    Log.w(TAG,"已断开 ");
                    setDeviceConnectedState(true);
                    break;
                case LeProxy.ACTION_CONNECT_ERROR:
                    Log.w(TAG,"连接异常 ");
                    setDeviceConnectedState(true);
                    break;
                case LeProxy.ACTION_CONNECT_TIMEOUT:
                    Log.w(TAG,"连接超时 ");
                    setDeviceConnectedState(true);
                    break;
                case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                    //if (!mIsRunning)return;
                    //byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);
                    dealwithLebDataChange(DataUtil.byteArrayToHex(intent.getByteArrayExtra(LeProxy.EXTRA_DATA)));
                    break;
            }
        }
    };

    private void dealwithLebDataChange(String hexData) {
        //Log.i(TAG,"hexData:"+hexData);
        if (hexData.startsWith("FF 83")) {
            //心电数据
            //Log.i(TAG,"心电hexData:"+hexData);
            //FF 83 0F FF FF FF FF FF FF FF FF FF FF 00 16  长度44
            if (hexData.length()==44){
                dealWithEcgData(hexData);
                isHaveDataTransfer = true;
                mIsDataStart = true;
            }
        } else if (hexData.startsWith("FF 86")) {
            //加速度数据
            //Log.i(TAG,"加速度hexData:"+hexData);
            //FF 86 11 00 A4 06 AC 1E 9D 00 A4 06 AC 1E 9D 11 16   长度50
            if (hexData.length()==50){
                dealWithAccelerationgData(hexData);
            }
        }
    }

    int [] ecgOneGroupDataInts = new int[oneGroupLength];
    private int mPreHeartRate;
    private boolean isNeedUpdateHeartRate = false;

    //处理心电数据
    private void dealWithEcgData(String hexData) {
        isNeedUpdateHeartRate = false;
        ECGUtil.geIntEcgaArr(hexData, " ", 3, oneGroupLength, ecgOneGroupDataInts); //一次的数据，10位

        if (mIsRunning) {
            writeEcgDataToBinaryFile(ecgOneGroupDataInts);
        }

        //滤波处理
        for (int i = 0; i < ecgOneGroupDataInts.length; i++) {
            ecgOneGroupDataInts[i] = ecgFilterUtil_1.miniEcgFilterLp(ecgFilterUtil_1.miniEcgFilterHp(ecgFilterUtil_1.NotchPowerLine(ecgOneGroupDataInts[i], 1)));
        }

        startRealTimeDataTrasmit(ecgOneGroupDataInts);

        //Log.i(TAG,"currentGroupIndex:"+currentGroupIndex);

        if (isFirstCalcu) {
            if (currentGroupIndex < calGroupCalcuLength) {
                //未到时间（1800个数据点计算一次心率）
                System.arraycopy(ecgOneGroupDataInts, 0, calcuEcgRate, currentGroupIndex * oneGroupLength, ecgOneGroupDataInts.length);
            } else {
                isNeedUpdateHeartRate = true;
                isFirstCalcu = false;
            }
        } else {
            if (currentGroupIndex < timeSpanGgroupCalcuLength) { //未到4s
                System.arraycopy(ecgOneGroupDataInts, 0, fourCalcuEcgRate, currentGroupIndex * oneGroupLength, ecgOneGroupDataInts.length);
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
            if (application!=null){
                application.setRunningmCurrentHeartRate(mCurrentHeartRate);
            }

            Log.i(TAG, "mCurrentHeartRate:" + mCurrentHeartRate);
            //calcuEcgRate = new int[calGroupCalcuLength*10];

            System.arraycopy(calcuEcgRate, 0, preCalcuEcgRate, 0, calcuEcgRate.length);
            System.arraycopy(ecgOneGroupDataInts, 0, fourCalcuEcgRate, currentGroupIndex * 10, ecgOneGroupDataInts.length);

            mHandler.sendEmptyMessage(1);

            //String specialFormatTime = tv_run_time.getText().toString();
            int stridefre = 0;
            if (mStridefreData.size() > 0) {
                stridefre = mStridefreData.get(mStridefreData.size() - 1);
            }

            String userSportData = "A4," + mFinalFormatSpeed + "," + mFormatDistance + "," + tv_run_time.getText().toString() + "," + tv_run_isoxygen.getText().toString() + "," + mCurrentHeartRate + "," + stridefre + "," + (int) mAllKcal;
            startUserSportDataTrasmit(userSportData);
        }
        currentGroupIndex++;
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    updateUIECGData();
                    break;
                case 2:
                    updateUIACCData();
                    break;
                case 3:
                    updateUISpeedData();
                    break;
            }
            return false;
        }
    });



    //更新心率相关数据
    private void updateUIECGData() {
        String oxygenState = calcuOxygenState(mCurrentHeartRate);
        if (mCurrentHeartRate == 0) {
            tv_run_rate.setText("--");
            tv_run_isoxygen.setText("--");
        } else {
            if (mPreHeartRate > 0) {
                int count = 0;
                int temp = mCurrentHeartRate - mPreHeartRate;
                if (temp > HealthyDataActivity.D_valueMaxValue) {
                    count = (temp) / HealthyDataActivity.D_valueMaxValue + 1;
                } else if (temp < -HealthyDataActivity.D_valueMaxValue) {
                    count = (temp) / HealthyDataActivity.D_valueMaxValue - 1;
                }
                System.out.println(count);
                if (count != 0) {
                    mCurrentHeartRate = mPreHeartRate + Math.abs(temp) / count;
                }
            }
            tv_run_rate.setText(mCurrentHeartRate + "");
            tv_run_isoxygen.setText(oxygenState);
        }
        heartRateDates.add(mCurrentHeartRate);
        mPreHeartRate = mCurrentHeartRate;

        if (mSendHeartRateBroadcastIntent == null) {
            mSendHeartRateBroadcastIntent = new Intent(action);
        }
        mSendHeartRateBroadcastIntent.putExtra("data", mCurrentHeartRate);
        sendBroadcast(mSendHeartRateBroadcastIntent);


        if (mIsRunning) {
            calcuAllkcal();

        }

        if (mWebSocketUtil!=null && !MyUtil.isEmpty(mWebSocketUtil.mCurAppClientID)) {
            String heartkcalData = "A3," + mWebSocketUtil.mCurAppClientID + "," + mCurrentHeartRate + "," + (int) mAllKcal;
            //sendSocketMsg(heartkcalData);
            mWebSocketUtil.sendSocketMsg(heartkcalData);
        }

        // A4,速度,距离,时间,有氧无氧,心率,步频,卡路里,

        //mCurrTimeDate = new Date(date.getTime()+recoverTimeMillis);

    }

    private void updateUIACCData() {
        tv_run_stridefre.setText(mTempStridefre +"");
    }



    private void updateUISpeedData() {
        tv_run_speed.setText(mFinalFormatSpeed);
        String specialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", mCurrTimeDate);
        CommunicateToBleService.setServiceForegrounByNotify(getResources().getString(R.string.running),getResources().getString(R.string.distance)+": "+mFormatDistance+"KM       "+getResources().getString(R.string.exercise_time)+": "+specialFormatTime,1);
        Log.i(TAG,"设置通知:"+specialFormatTime);
    }

    //ecg数据写到文件里，二进制方式写入
    private void writeEcgDataToBinaryFile(int[] ints) {
        try {
            if (ecgDataOutputStream==null){
                ecgLocalFileName = MyUtil.getClolthLocalFileName(1,new Date());;
                Log.i(TAG,"fileAbsolutePath:"+ecgLocalFileName);
                //MyUtil.putStringValueFromSP("cacheFileName",fileAbsolutePath);
                ecgDataOutputStream = new DataOutputStream(new FileOutputStream(ecgLocalFileName,true));
                ecgByteBuffer = ByteBuffer.allocate(2);
                if (mAbortData!=null){
                    mAbortData.setEcgFileName(ecgLocalFileName);
                    saveOrUpdateAbortDatareordToSP(mAbortData,false);
                }
                else {
                    mAbortData = new AppAbortDataSave(startTimeMillis, ecgLocalFileName, "", -1, 1,mSpeedStringList,mKcalData);
                    saveOrUpdateAbortDatareordToSP(mAbortData,true);
                }
            }
            for (int anInt : ints) {
                ecgByteBuffer.clear();
                ecgByteBuffer.putShort((short) anInt);
                ecgDataOutputStream.writeByte(ecgByteBuffer.get(1));
                ecgDataOutputStream.writeByte(ecgByteBuffer.get(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //计算卡路里，累加
    private void calcuAllkcal() {
        if (mCalKcalCurrentTimeMillis == 0) {
            mCalKcalCurrentTimeMillis = System.currentTimeMillis();
            mUserSex = MyUtil.getUserSex();
            mUserAge = HealthyIndexUtil.getUserAge();
            mUserWeight = MyUtil.getUserWeight();
        } else {
            float time = (float) ((System.currentTimeMillis() - mCalKcalCurrentTimeMillis) / (1000 * 60.0));
            mCalKcalCurrentTimeMillis = System.currentTimeMillis();
            Log.i(TAG, "time:" + time + ",mUserSex:" + mUserSex + ",mUserAge:" + mUserAge + ",mUserWeight" + mUserWeight);
            float getkcal = DiagnosisNDK.getkcal(mUserSex, mCurrentHeartRate, mUserAge, mUserWeight, time);
            Log.i(TAG, "getkcal:" + getkcal);
            if (getkcal < 0) {
                getkcal = 0;
            }
            //防止蓝牙断开又重新连上后时间太长导致卡路里很大
            if (getkcal > 6 && mKcalData.size() > 0) {
                getkcal = Float.parseFloat(mKcalData.get(mKcalData.size() - 1));

                if (getkcal>10){
                    getkcal = 0;
                }
            }

            mAllKcal += getkcal;
            mKcalData.add(getkcal + "");

            tv_run_kcal.setText((int)mAllKcal + "");

            Log.i(TAG, "getkcal:" + getkcal);
            Log.i(TAG,"mAllKcal： "+mAllKcal);
        }
    }

    private String calcuOxygenState(int heartRate) {
        int maxRate = 220-HealthyIndexUtil.getUserAge();
        if (heartRate<=maxRate*0.6){
            return getResources().getString(R.string.exercise_flat);
        }
        else if (maxRate*0.6<heartRate && heartRate<=maxRate*0.75){
            return getResources().getString(R.string.exercise_oxygenated);
        }
        else if (maxRate*0.75<heartRate && heartRate<=maxRate*0.95){
            return getResources().getString(R.string.exercise_without_oxygen);
        }
        else if (maxRate*0.95<heartRate ){
            return getResources().getString(R.string.exercise_in_danger);
        }
        return getResources().getString(R.string.exercise_oxygenated);
    }


    int [] accOneGroupDataInts = new int[accOneGroupLength];

    byte[] accByteData = new byte[accDataLength];
    private int accCalcuDataIndex = 0;
    //处理加速度数据
    private void dealWithAccelerationgData(String hexData) {
        if (!mIsRunning)return;
        ECGUtil.geIntEcgaArr(hexData, " ", 3, accOneGroupLength, accOneGroupDataInts); //一次的数据，12位

        if (mIsRunning) {
            writeAccDataToBinaryFile(accOneGroupDataInts);
        }

        if (accCalcuDataIndex<accDataLength){
            for (int i: accOneGroupDataInts){
                //accData.add(i);
                accByteData[accCalcuDataIndex++] = (byte)i;
            }
        }
        else {
            //计算
            /*byte[] bytes = new byte[accDataLength];
            for (int i=0;i<accData.size();i++){
                bytes[i] = (byte)(int)accData.get(i);
            }*/
            int[] results = new int[2];
            DiagnosisNDK.AnalysisPedo(accByteData,accDataLength,results);
            /*int state = -1;
            int pedoCount = -1;
            DiagnosisNDK.AnalysisPedo(bytes,accDataLength,state,pedoCount);
            Log.i(TAG,"state:"+state+",pedoCount:"+pedoCount);*/

            Log.i(TAG,"results: "+results[0]+"  "+results[1]);
            //每分钟的步数
            mTempStridefre = (int) (results[1] * 5.21);
            mStridefreData.add(mTempStridefre);
            mHandler.sendEmptyMessage(2);

            accCalcuDataIndex=0;
            for (int i: accOneGroupDataInts){
                accByteData[accCalcuDataIndex++] = (byte)i;
            }
        }

    }


    //acc数据写到文件里，二进制方式写入
    private void writeAccDataToBinaryFile(int[] ints) {
        try {
            if (accDataOutputStream==null){
                long accFiletimeMillis = System.currentTimeMillis();
                String accLocalFileName = MyUtil.getClolthLocalFileName(2,new Date());
                Log.i(TAG,"accLocalFileName:"+accLocalFileName);
                //MyUtil.putStringValueFromSP("cacheFileName",fileAbsolutePath);
                accDataOutputStream = new DataOutputStream(new FileOutputStream(accLocalFileName,true));
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
        /*if (abortDataListFromSP==null){
            abortDataListFromSP = AppAbortDbAdapterUtil.getAbortDataListFromSP();
        }

        if (!isSave && abortDataListFromSP.size()>0){
            abortDataListFromSP.remove(abortDataListFromSP.size()-1);
        }
        abortDataListFromSP.add(abortData);*/
        AppAbortDbAdapterUtil.putAbortDataToSP(abortData);
    }

    private void deleteAbortDataRecordFomeSP(){
        /*Log.i(TAG,"abortDataListFromSP.size():"+abortDataListFromSP.size());
        if (abortDataListFromSP!=null && abortDataListFromSP.size()>0){
            Log.i(TAG,abortDataListFromSP.get(abortDataListFromSP.size()-1)+"");
            abortDataListFromSP.remove(abortDataListFromSP.size()-1);
            AppAbortDbAdapterUtil.putAbortDataListToSP(abortDataListFromSP);
        }
        Log.i(TAG,"abortDataListFromSP.size():"+abortDataListFromSP.size());*/

        MyUtil.putStringValueFromSP("abortDatas","");
    }


    //计算跑步速度，在室内和室外统一采用地图返回的传感器速度
    private void calculateSpeed(AMapLocation aMapLocation) {
        //Log.i(TAG,"aMapLocation.getAccuracy():"+aMapLocation.getAccuracy());

        //float speed = aMapLocation.getSpeed()*3.6f;
        /*if (aMapLocation.getLocationType()==AMapLocation.LOCATION_TYPE_GPS){
            mOutdoorCal8ScendSpeedList.add(aMapLocation);
        }
        else {
            mIndoorCal8ScendSpeedList.add(aMapLocation.getSpeed());
        }*/

        Log.i(TAG,"mIsOutDoor:"+mIsOutDoor);

        if (mIsOutDoor){
            mOutdoorCal8ScendSpeedList.add(aMapLocation);
        }
        else {
            mIndoorCal8ScendSpeedList.add(aMapLocation.getSpeed());
        }

        /*if (aMapLocation.getSpeed()<=trackSpeedOutdoorMAX){
            mOutdoorCal8ScendSpeedList.add(aMapLocation);
        }*/

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

    //private int locationInAccurateCount = 1;
    //private int noGpsLocationCount = 0;

    private void calculateDistance(AMapLocation aMapLocation) {
        //不关室内室外，首次定位几个点策略：定位5个点，这5个点的距离在100m范围之内属于正常情况，否则为定位不准，重新定位前5个点
        if (pathRecord.getPathline().size()<5){
            pathRecord.addpoint(aMapLocation);
            float distance = Util.getDistance(pathRecord.getPathline());
            if (distance>100){
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

        double tempDistance;
        if (mIsOutDoor){
            AMapLocation lastAMapLocation = pathRecord.getPathline().get(pathRecord.getPathline().size() - 1);
            tempDistance = getTwoPointDistance(lastAMapLocation, aMapLocation);
            //Log.i(TAG,"tempDistance:"+tempDistance);
            if (tempDistance<=trackSpeedOutdoorMAX*((System.currentTimeMillis() - mCurrentTimeMillis) / 1000f) && aMapLocation.getSpeed()<=15 && aMapLocation.getAccuracy()<70){
                pathRecord.addpoint(aMapLocation);
                mAllDistance = Util.getDistance(pathRecord.getPathline())+mAddDistance-mPreOutDoorDistance;
                mCurrentTimeMillis = System.currentTimeMillis();
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
            }
            mCurrentTimeMillis = System.currentTimeMillis();
        }

        mFormatDistance = MyUtil.getFormatDistance(mAllDistance);
        if(application!=null){
            application.setRunningFormatDistance(mFormatDistance);
        }
        tv_run_distance.setText(mFormatDistance);
        Log.i(TAG,"mAllDistance:"+mAllDistance);


        //Log.i(TAG,"mAllDistance:"+mAllDistance);


    }

    /*private PathRecord tempRecord = new PathRecord();

    int noNocationPointAddCount;

    private void calculateDistance(AMapLocation aMapLocation) {
        //不关室内室外，首次定位几个点策略：定位5个点，这5个点的距离在100m范围之内属于正常情况，否则为定位不准，重新定位前5个点
        int trackSpeedOutdoorMAX = 100;
        if (tempRecord.getPathline().size()<=5){
            tempRecord.addpoint(aMapLocation);
            float distance = Util.getDistance(record.getPathline());
            if (distance> 2*trackSpeedOutdoorMAX){
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
                    if (distance< 2*trackSpeedOutdoorMAX){
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
                if (tempDistance< trackSpeedOutdoorMAX){   //2个点之间距离小于100m为正常定位情况，否则为噪声去除
                    record.addpoint(aMapLocation);
                    noNocationPointAddCount = 0;
                }
                else {
                    Log.i(TAG,"室内大于100,排除点");
                    if (noNocationPointAddCount>=10){
                        //连续5次没有加入定位列表，则需要和之前线连接
                        tempRecord.addpoint(aMapLocation);
                        float distance = Util.getDistance(tempRecord.getPathline());
                        if (distance< 2*trackSpeedOutdoorMAX){
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

    public static synchronized  StartRunActivity getInstance(){
        Log.i(TAG,"getInstance");
        if(mStartRunActivityInstance==null)//1
            synchronized(StartRunActivity.class){//2
                if(mStartRunActivityInstance==null)//3
                    mStartRunActivityInstance = new StartRunActivity();//4
            }
        return mStartRunActivityInstance;
    }

    //开始运动
    private void startRunning() {
        if (!mIsRunning){
            setRunningParameter();
            /*LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // 判断GPS模块是否开启，如果没有则开启
            Log.i(TAG,"gps打开？:"+locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER));
            if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                MyUtil.chooseOpenGps(this);
            }
            else {
                setRunningParameter();
            }*/
        }
    }



    JsonBase jsonBase = new JsonBase();
    Gson gson = new Gson();
    String intString;

    //webSocket实施数据传输
    private void startRealTimeDataTrasmit(int [] ints) {
        if (mWebSocketUtil!=null && mWebSocketUtil.isStartDataTransfer){
            intString = "A1,"+mWebSocketUtil.mCurBrowserClientID+",";
            for (int i:ints){
                intString+=i+",";
            }
            /*jsonBase.setRet(1);
            jsonBase.setErrDesc(intString.substring(0,intString.length()-1));
            sendSocketMsg(gson.toJson(jsonBase));*/
            //F0,28,18,6,-2,-3,0,3,5,1,-1
            //sendSocketMsg(intString);
            mWebSocketUtil.sendSocketMsg(intString);
        }
    }

    //上传用户实时运动数据
    private void startUserSportDataTrasmit(String userSportData) {
        if (mWebSocketUtil!=null && mWebSocketUtil.isStartDataTransfer){
            //sendSocketMsg(userSportData);
            mWebSocketUtil.sendSocketMsg(userSportData);
        }
    }

    private void startCalSpeedTimerStask() {
        deviceOffLineFileUtil = new DeviceOffLineFileUtil();
        deviceOffLineFileUtil.setTransferTimeOverTime(new DeviceOffLineFileUtil.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                if (mIsRunning){
                    Log.i(TAG,"8s 计算速度");
                    startCal8ScendSpeed();
                }
            }
        },8);//8s 计算速度

        deviceOffLineFileUtil.startTime();
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

        Log.i(TAG,"for8SecondAverageSpeed:"+for8SecondAverageSpeed);

        if (for8SecondAverageSpeed>0){
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

            Log.i(TAG,"forOneKMSecond:"+forOneKMSecond);

            if (forOneKMSecond==0){
                formatSpeed = "--";
            }
            else {
                formatSpeed = (int)forOneKMSecond/60+"'"+(int)forOneKMSecond%60+"''";
            }
        }

        Log.i(TAG,"startCal8ScendSpeed:  speed:"+forOneKMSecond+",   formatSpeed:"+formatSpeed);

        mSpeedStringList.add((int) forOneKMSecond);  //speed为秒数，1公里所用的时间


        mIndoorCal8ScendSpeedList.clear();
        mOutdoorCal8ScendSpeedList.clear();
        mFinalFormatSpeed = formatSpeed;
        if (application!=null){
            application.setRunningFinalFormatSpeed(mFinalFormatSpeed);
        }
        mHandler.sendEmptyMessage(3);
    }

    boolean isHaveDataTransfer;



    //初始化定位
    private void initMapLoationTrace() {
        if (mLocationOption==null){
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(1500);
            mLocationOption.setGpsFirst(true);
            mLocationOption.setSensorEnable(true);

            mlocationClient = new AMapLocationClient(this);
            mlocationClient.setLocationListener(this);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
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
                    String country = Locale.getDefault().getCountry();
                    Log.i(TAG,"country:"+country);Locale.CHINA.getCountry();
                    if(country.equals(Locale.CHINA.getCountry())){
                        //中国
                        startActivity(new Intent(StartRunActivity.this,RunTrailMapActivity.class));
                    }
                    else {
                        //国外
                        startActivity(new Intent(StartRunActivity.this,RunTrailMapGoogleActivity.class));
                    }
                    break;
            }
        }
    }

    //结束运动
    private void endRunning() {
        Log.i(TAG,"isThreeMinute:"+ isThreeMinute +", isHaveDataTransfer:"+isHaveDataTransfer);
        ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(this);
        if (isThreeMinute){
            if (isHaveDataTransfer){
                chooseAlertDialogUtil.setAlertDialogText(getResources().getString(R.string.necessary_to_collect_recovery),getResources().getString(R.string.collect_data),getResources().getString(R.string.skip));
                chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick() {
                        saveSportRecord(createrecord);
                        Intent intent = new Intent(StartRunActivity.this, CalculateHRRProcessActivity.class);
                        if (mIsOutDoor){
                            intent.putExtra(Constant.sportState,Constant.SPORTSTATE_ATHLETIC);
                        }
                        else {
                            intent.putExtra(Constant.sportState,Constant.SPORTSTATE_INDOOR);
                        }

                        if (!MyUtil.isEmpty(ecgLocalFileName)){
                            intent.putExtra(Constant.ecgLocalFileName,ecgLocalFileName);
                        }
                        if (createrecord!=-1){
                            intent.putExtra(Constant.sportCreateRecordID,createrecord);
                        }
                        if (heartRateDates.size()>0){
                            intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
                        }
                        intent.putExtra(Constant.startTimeMillis,startTimeMillis);
                        if (mKcalData.size()>0){
                            ArrayList<String> tempList = new ArrayList<>();
                            tempList.addAll(mKcalData);
                            intent.putStringArrayListExtra(Constant.mKcalData,tempList);
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
                        Intent intent = new Intent(StartRunActivity.this, HeartRateAnalysisActivity.class);
                        if (mIsOutDoor){
                            intent.putExtra(Constant.sportState,Constant.SPORTSTATE_ATHLETIC);
                        }
                        else {
                            intent.putExtra(Constant.sportState,Constant.SPORTSTATE_INDOOR);
                        }
                        if (!MyUtil.isEmpty(ecgLocalFileName)){
                            intent.putExtra(Constant.ecgLocalFileName,ecgLocalFileName);
                        }
                        if (createrecord!=-1){
                            intent.putExtra(Constant.sportCreateRecordID,createrecord);
                        }
                        if (heartRateDates.size()>0){
                            intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
                        }
                        intent.putExtra(Constant.startTimeMillis,startTimeMillis);
                        if (mKcalData.size()>0){
                            ArrayList<String> tempList = new ArrayList<>();
                            tempList.addAll(mKcalData);
                            intent.putStringArrayListExtra(Constant.mKcalData,tempList);
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
                Intent intent = new Intent(StartRunActivity.this, HeartRateAnalysisActivity.class);
                if (mIsOutDoor){
                    intent.putExtra(Constant.sportState,Constant.SPORTSTATE_ATHLETIC);
                }
                else {
                    intent.putExtra(Constant.sportState,Constant.SPORTSTATE_INDOOR);
                }
                if (!MyUtil.isEmpty(ecgLocalFileName)){
                    intent.putExtra(Constant.ecgLocalFileName,ecgLocalFileName);
                }
                if (createrecord!=-1){
                    intent.putExtra(Constant.sportCreateRecordID,createrecord);
                }
                if (heartRateDates.size()>0){
                    intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
                }
                intent.putExtra(Constant.startTimeMillis,startTimeMillis);
                if (mKcalData.size()>0){
                    ArrayList<String> tempList = new ArrayList<>();
                    tempList.addAll(mKcalData);
                    intent.putStringArrayListExtra(Constant.mKcalData,tempList);
                }
                if (mStridefreData.size()>0){
                    intent.putIntegerArrayListExtra(Constant.mStridefreData,mStridefreData);
                }
                if (mSpeedStringList.size()>0){
                    ArrayList<Integer> tempList = new ArrayList<>();
                    tempList.addAll(mSpeedStringList);
                    intent.putIntegerArrayListExtra(Constant.mSpeedStringListData,tempList);
                }
                Log.i(TAG,"startActivity");
                startActivity(intent);
                finish();
            }
        }
        else {
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
        if (pathRecord!=null && createrecord!=-1){
            createrecord = Util.saveOrUdateRecord(pathRecord.getPathline(),addDuration, pathRecord.getDate(), this,mStartTime,mAllDistance,createrecord);
            Log.i(TAG,"createrecord:"+createrecord);
        }

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
        mCurrTimeDate = null;

        deleteAbortDataRecordFomeSP();
        CommunicateToBleService.detoryServiceForegrounByNotify();

        /*if (mWebSocketUtil!=null){
            mWebSocketUtil.closeConnectWebSocket();
        }*/

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
        CommunicateToBleService.detoryServiceForegrounByNotify();
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

    //谷歌地图
    @Override
    public void onLocationChanged(Location location) {
        //实时位置信息
        //Log.i(TAG,"onLocationChanged:"+location.toString());
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
