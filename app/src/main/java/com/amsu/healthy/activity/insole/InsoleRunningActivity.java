package com.amsu.healthy.activity.insole;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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
import com.amsu.healthy.R;
import com.amsu.healthy.activity.HeartRateAnalysisActivity;
import com.amsu.healthy.activity.RunTrailMapActivity;
import com.amsu.healthy.activity.StartRunActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.InsoleAnalyResult;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.AppAbortDbAdapterUtil;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.EcgFilterUtil_1;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MD5Util;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.RunTimerTaskUtil;
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
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private long mCurrentTimeMillis_insoleTest =-1;
    private String mLeftInsole30SencendFileAbsolutePath;
    private String mRightInsole30SencendFileAbsolutePath;
    private TextView tv_test;
    private final int insole_left = 1;
    private final int insole_right = 2;
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

    public long createrecord =-1;
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
    private static final int saveDataTOLocalTimeSpanSecond = 60;  //数据持久化时间间隔 1分钟
    private static final int minimumLimitTimeMillis = 1000 * 10 * 1;  //最短时间限制 3分钟
    public static final String action = "jason.broadcast.action";    //发送广播，将心率值以广播的方式放松出去，在其他Activity可以接受



    private boolean mHaveOutSideGpsLocation;
    private float mAllKcal;


    private DeviceOffLineFileUtil deviceOffLineFileUtil;
    private AppAbortDataSave mAbortData;
    private DeviceOffLineFileUtil saveDeviceOffLineFileUtil;

    private List<AMapLocation> mGpsCal8ScendSpeedList;
    private List<Float> mIndoorCal8ScendSpeedList ;
    private List<Integer> mSpeedStringList ;
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
    private EcgFilterUtil_1 ecgFilterUtil_1;
    private float mPreOutDoorDistance;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    public Date mCurrTimeDate;
    private TextView tv_run_stride;
    private TextView tv_run_avespeed;
    private TextView tv_run_maxspeed;
    private TextView tv_run_continue;
    private TextView tv_run_end;
    private TextView tv_run_stridefre;

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
        tv_run_maxspeed = (TextView) findViewById(R.id.tv_run_maxspeed);
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

        mSpeedStringList = new CopyOnWriteArrayList<>();
        mGpsCal8ScendSpeedList = new ArrayList<>();
        mIndoorCal8ScendSpeedList = new ArrayList<>();

        //commitToServerAnaly("/storage/emulated/0/amsu/insole/20170802092924.is");

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());

        tv_test = (TextView) findViewById(R.id.tv_test);

        String data  = "B7";
        boolean send1 = LeProxy.getInstance().send(CommunicateToBleService.mInsole_connecMac1, Constant.insoleSerUuid, Constant.insoleCharUuid, data.getBytes(), false);
        boolean send = LeProxy.getInstance().send(CommunicateToBleService.mInsole_connecMac2, Constant.insoleSerUuid, Constant.insoleCharUuid, data.getBytes(), false);
        Log.i("30ScendCount","send1:"+send1+",send:"+send);

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
                    dealwithLebDataChange(DataUtil.byteArrayToHex(intent.getByteArrayExtra(LeProxy.EXTRA_DATA)),address);
                    break;
            }
        }
    };

    private void dealwithLebDataChange(String hexData,String address) {
        Log.i(TAG,"hexData："+hexData);

        /**/

        String[] allDataSplit = hexData.split(" ");
        int startInt = Integer.parseInt(allDataSplit[0], 16);
        Log.i(TAG,"startInt:"+startInt);
        Log.i(TAG,"allDataSplit.length:"+allDataSplit.length);

        if (MyUtil.isEmpty(mLeftMacAddress) || MyUtil.isEmpty(mRightMacAddress)){
            if (allDataSplit.length==61){
                if (startInt>=50 && startInt<100){
                    //左侧数据
                    mLeftMacAddress = address;
                }
                else if (startInt>=100 && startInt<150){
                    //右侧数据
                    mRightMacAddress = address;
                }
            }
           /* if (hexData.startsWith("4C")){
                //L 左脚
                mLeftMacAddress = address;
            }
            else if (hexData.startsWith("52")) {
                mRightMacAddress = address;
            }*/
        }

        //AA 4C FF FF FF FF FF FE FF 1A FF D6 10 1A 85 09 D8 8D  长度为53
        //4C 00 00 00 00 00 00 00 04 00 0A EF E9 A9 7A 7F BE  最新
        //6C FF FF 00 00 00 01 FF F8 00 03 EF FA E3 33 10 FF FF 00 00 00 00 FF EE 00 08 EF E9 E3 36 11 FF FF FF FF 00 00 FF D9 FF F6 EF ec E3 39 11 00 00 00 00 00 00 FF E7 00 01 F0 16 E3 3C 12

        if (allDataSplit.length==61){
            if (mCurrentTimeMillis_insoleTest==-1){
                mCurrentTimeMillis_insoleTest = System.currentTimeMillis();
            }
            if (System.currentTimeMillis()-mCurrentTimeMillis_insoleTest>=1000*40){

                Log.i("30ScendCount","40秒到，上传计算");
                Log.i("30ScendCount","mLeftReceivePackageCount:"+mLeftReceivePackageCount);
                Log.i("30ScendCount","mRightReceivePackageCount:"+mRightReceivePackageCount);

                testText += mLeftMacAddress +"左脚   40秒到  count："+mLeftReceivePackageCount+"\n";
                testText += mRightMacAddress +"右脚   40秒到  count："+mRightReceivePackageCount+"\n";

                tv_test.setText(testText);

                mLeftReceivePackageCount=0;
                mRightReceivePackageCount=0;
                mCurrentTimeMillis_insoleTest = System.currentTimeMillis();
            }

            if (startInt>=50 && startInt<100){
                //左侧数据
                mLeftReceivePackageCount = mLeftReceivePackageCount+4;
                Log.i(TAG,"hexData1："+hexData);
            }
            else if (startInt>=100 && startInt<150){
                //右侧数据
                mRightReceivePackageCount = mRightReceivePackageCount+4;
                Log.i(TAG,"hexData2："+hexData);
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
        else if (hexData.length()==5){
            //鞋垫步数
            String[] split = hexData.split(" ");
            if (split.length==2){
                String stepCountString = split[0]+split[1];

                //Log.i("30ScendCount","步数 hexData:"+hexData);
                int tempStepCount = Integer.parseInt(stepCountString, 16);
                Log.i("30ScendCount","步数 "+address+", "+ tempStepCount);
                Log.i("30ScendCount","步数 mPreLeftStepCount:"+mPreLeftStepCount+", mPreRightStepCount:"+mPreRightStepCount);

                if (!MyUtil.isEmpty(mLeftMacAddress) && address.equals(mLeftMacAddress)){
                    if (mPreLeftStepCount!=-1){
                        mLeftStepCount = tempStepCount-mPreLeftStepCount;
                    }
                    mPreLeftStepCount = tempStepCount;
                }
                else if (!MyUtil.isEmpty(mRightMacAddress) && address.equals(mRightMacAddress)){
                    if (mPreRightStepCount!=-1){
                        mRightStepCount = tempStepCount-mPreRightStepCount;
                    }
                    mPreRightStepCount = tempStepCount;
                }

                //Log.i("30ScendCount","步数  左脚："+mLeftStepCount+",       右脚："+mRightStepCount+"\n");
                if (mLeftStepCount!=-1 && mRightStepCount!=-1 ){
                    testText += "步数  左脚："+mLeftStepCount+",       右脚："+mRightStepCount+"\n";
                    tv_test.setText(testText);
                    mLeftStepCount = mRightStepCount = -1;
                }

            }

        }

    }

    int mPreLeftStepCount = -1;
    int mPreRightStepCount = -1;

    int mLeftStepCount = -1;
    int mRightStepCount = -1;

    double mLeftTime = -1;
    double mRightTime = -1;


    //根据左右脚写道不同的文件里
    private void writeEcgDataToBinaryFile(double time,float gyrX,float gyrY,float gyrZ,float accX ,float accY,float accZ,int insoleType) {
        if (insoleType==insole_left){

            //时间到会重置，需要累加
            if (mLeftTime==-1){
                mLeftTime = time;
            }

            else if (time>mLeftTime){
                mLeftTime = time;
            }
            else {
                time += mLeftTime;
            }

            try {
                if (leftDataOutputStream==null){
                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/insole";
                    File file = new File(filePath);
                    if (!file.exists()) {
                        boolean mkdirs = file.mkdirs();
                        Log.i(TAG,"mkdirs:"+mkdirs);
                    }
                    mLeftInsole30SencendFileAbsolutePath = filePath+"/"+ MyUtil.getECGFileNameDependFormatTime(new Date())+".lf";
                    Log.i(TAG,"mLeftInsole30SencendFileAbsolutePath:"+ mLeftInsole30SencendFileAbsolutePath);
                    leftDataOutputStream = new DataOutputStream(new FileOutputStream(mLeftInsole30SencendFileAbsolutePath,true));

                    leftByteBuffer = ByteBuffer.allocate(1);
                    leftByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    String head="0xc0";
                    leftByteBuffer.put(Integer.valueOf(Integer.decode(head)).byteValue());

                    leftByteBuffer.flip();
                    leftDataOutputStream.write(leftByteBuffer.array());
                    leftByteBuffer.clear();

                    leftByteBuffer = ByteBuffer.allocate(8+4*6);
                    leftByteBuffer.order(ByteOrder.LITTLE_ENDIAN);  //小端模式写入数据
                }
                leftByteBuffer.putDouble(time);
                leftByteBuffer.putFloat(gyrX);
                leftByteBuffer.putFloat(gyrY);
                leftByteBuffer.putFloat(gyrZ);
                leftByteBuffer.putFloat(accX);
                leftByteBuffer.putFloat(accY);
                leftByteBuffer.putFloat(accZ);

                leftByteBuffer.flip();
                leftDataOutputStream.write(leftByteBuffer.array());
                leftByteBuffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (insoleType==insole_right){
            if (mRightTime==-1){
                mRightTime = time;
            }
            else if (time>mRightTime){
                mRightTime = time;
            }
            else {
                time += mRightTime;
            }

            try {
                if (rightDataOutputStream==null){
                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/insole";
                    File file = new File(filePath);
                    if (!file.exists()) {
                        boolean mkdirs = file.mkdirs();
                        Log.i(TAG,"mkdirs:"+mkdirs);
                    }
                    mRightInsole30SencendFileAbsolutePath = filePath+"/"+ MyUtil.getECGFileNameDependFormatTime(new Date())+".rg";
                    Log.i(TAG,"mRightInsole30SencendFileAbsolutePath:"+ mRightInsole30SencendFileAbsolutePath);
                    rightDataOutputStream = new DataOutputStream(new FileOutputStream(mRightInsole30SencendFileAbsolutePath,true));

                    rightByteBuffer = ByteBuffer.allocate(1);
                    rightByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    String head="0xc0";
                    rightByteBuffer.put(Integer.valueOf(Integer.decode(head)).byteValue());

                    rightByteBuffer.flip();
                    rightDataOutputStream.write(rightByteBuffer.array());
                    rightByteBuffer.clear();

                    rightByteBuffer = ByteBuffer.allocate(8+4*6);
                    rightByteBuffer.order(ByteOrder.LITTLE_ENDIAN);  //小端模式写入数据
                }
                rightByteBuffer.putDouble(time);
                rightByteBuffer.putFloat(gyrX);
                rightByteBuffer.putFloat(gyrY);
                rightByteBuffer.putFloat(gyrZ);
                rightByteBuffer.putFloat(accX);
                rightByteBuffer.putFloat(accY);
                rightByteBuffer.putFloat(accZ);

                rightByteBuffer.flip();
                rightDataOutputStream.write(rightByteBuffer.array());
                rightByteBuffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    int mLeftReceivePackageCount=  0;
    int mRightReceivePackageCount=  0;

    String testText="";

    private void commitToServerAnaly(String leftFilePath,String rightFilePath) {
        //String path = filePath;
        Log.i("30ScendCount","40秒到，上传计算");
        Log.i("30ScendCount","mLeftReceivePackageCount:"+mLeftReceivePackageCount);
        Log.i("30ScendCount","mRightReceivePackageCount:"+mRightReceivePackageCount);

        testText += mLeftMacAddress +"左脚   40秒到  count："+mLeftReceivePackageCount+"\n";
        testText += mRightMacAddress +"右脚   40秒到  count："+mRightReceivePackageCount+"\n";
        tv_test.setText(testText);


        String data  = "B7";
        boolean send1 = LeProxy.getInstance().send(CommunicateToBleService.mInsole_connecMac1, Constant.insoleSerUuid, Constant.insoleCharUuid, data.getBytes(), false);
        boolean send = LeProxy.getInstance().send(CommunicateToBleService.mInsole_connecMac2, Constant.insoleSerUuid, Constant.insoleCharUuid, data.getBytes(), false);
        Log.i("30ScendCount","send1:"+send1+",send:"+send);


        Log.i(TAG,"leftFilePath:"+leftFilePath);
        Log.i(TAG,"rightFilePath:"+rightFilePath);

        User userFromSP = MyUtil.getUserFromSP();
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();

        if (!MyUtil.isEmpty(MyApplication.insoleAccessToken)){
            params.addBodyParameter("access_token",MyApplication.insoleAccessToken);
        }
        else {
            return;
        }
        if(MyUtil.isEmpty(leftFilePath) &&!MyUtil.isEmpty(rightFilePath)){
            params.addBodyParameter("rightFile",new File(rightFilePath));
            params.addBodyParameter("leftFile",new File(rightFilePath));

            String leftFileMd5Message = MD5Util.getFileMd5Message(rightFilePath);
            params.addBodyParameter("leftchecksum",leftFileMd5Message);
            params.addBodyParameter("rightchecksum",leftFileMd5Message);
        }
        else if(!MyUtil.isEmpty(leftFilePath) && MyUtil.isEmpty(rightFilePath)){
            params.addBodyParameter("rightFile",new File(leftFilePath));
            params.addBodyParameter("leftFile",new File(leftFilePath));

            String leftFileMd5Message = MD5Util.getFileMd5Message(leftFilePath);
            params.addBodyParameter("leftchecksum",leftFileMd5Message);
            params.addBodyParameter("rightchecksum",leftFileMd5Message);
        }
        else if(!MyUtil.isEmpty(leftFilePath) && !MyUtil.isEmpty(rightFilePath)){
            params.addBodyParameter("rightFile",new File(rightFilePath));
            params.addBodyParameter("leftFile",new File(leftFilePath));

            String leftFileMd5Message = MD5Util.getFileMd5Message(leftFilePath);
            String rightFileMd5Message = MD5Util.getFileMd5Message(rightFilePath);
            params.addBodyParameter("leftchecksum",leftFileMd5Message);
            params.addBodyParameter("rightchecksum",rightFileMd5Message);

        }


        params.addBodyParameter("creationtime",System.currentTimeMillis()+"");
        params.addBodyParameter("type","walking");
        params.addBodyParameter("name",userFromSP.getPhone());

        String sex;
        if (userFromSP.getSex().equals("1")){
            //1=男 2=女
            sex = "male";
        }
        else {
            sex = "female";
        }

        params.addBodyParameter("gender",sex);
        params.addBodyParameter("age", HealthyIndexUtil.getUserAge()+"");
        params.addBodyParameter("height",userFromSP.getHeight());
        params.addBodyParameter("weight",userFromSP.getWeight());
        params.addBodyParameter("phone",userFromSP.getPhone());
        params.addBodyParameter("tag","鞋垫");

        MyUtil.addCookieForHttp(params);

        Log.i(TAG,"上传到服务器分析");
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.get30ScendInsoleAlanyDataURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(InsoleRunningActivity.this);
                String result = responseInfo.result;
                Log.i("30ScendCount","上传onSuccess==result:"+result);

                Gson gson = new Gson();
                InsoleAnalyResult fromJson = gson.fromJson(result, InsoleAnalyResult.class);
                InsoleAnalyResult.General general = fromJson.general;
                if (general!=null){
                    String rString = "质量:"+general.dataQuality+"\n步数:"+general.stepCount+"\n步频:"+general.stepRate+"\n步幅(米):"+general.strideLength+"\n对称性:"+general.symmetry+
                            "\n一致性:"+general.variability+"\n摆动宽度(左/右 米):"+fromJson.left.swingWidthMean+"/"+fromJson.right.swingWidthMean+"\n离地高度(左/右 米):"+fromJson.left.stepHeightMean+"/"+fromJson.right.stepHeightMean
                            +"\n触地时间(左/右 秒):"+fromJson.left.stanceDurationMean+"/"+fromJson.right.stanceDurationMean+"\n\n";

                    testText += rString+"\n\n";
                    tv_test.setText(testText);
                }

                Log.i("30ScendCount","fromJson:"+fromJson);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("30ScendCount","上传onFailure==result:"+e);
                testText += "上传onFailure==result:"+e+"\n\n";
                tv_test.setText(testText);
                MyUtil.hideDialog(InsoleRunningActivity.this);
            }

        });
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
        mAbortData = AppAbortDbAdapterUtil.getAbortDataFromSP();
        Log.i(TAG,"mAbortData:"+mAbortData);
        if (mAbortData!=null){
            createrecord = mAbortData.getMapTrackID();

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
                for (String s:kcalStringList){
                    mAllKcal += Float.parseFloat(s);
                }
                tv_run_kcal.setText((int)mAllKcal+"");
            }

        }
    }

    private void setRunningParameter() {

        mIsRunning  =true;
        mCurrTimeDate = new Date(0,0,0);

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
                        createrecord = Util.saveOrUdateRecord(pathRecord.getPathline(),addDuration, pathRecord.getDate(), InsoleRunningActivity.this,mStartTime,mAllDistance,createrecord);
                        Log.i(TAG,"createrecord:"+createrecord);

                        if (createrecord!=-1){
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

                                saveOrUpdateAbortDatareordToSP(mAbortData,false);
                            }
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
        CommunicateToBleService.setServiceForegrounByNotify(getResources().getString(R.string.running),getResources().getString(R.string.distance)+": "+mFormatDistance+"KM       "+getResources().getString(R.string.exercise_time)+": "+specialFormatTime,1);
        Log.i(TAG,"设置通知:"+specialFormatTime);


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

        MyUtil.putStringValueFromSP("abortDatas","");
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //Log.i(TAG,"onLocationChanged:"+aMapLocation.toString());
        calculateSpeed(aMapLocation);
        calculateDistance(aMapLocation);
    }

    //计算跑步速度，在室内和室外统一采用地图返回的传感器速度
    private void calculateSpeed(AMapLocation aMapLocation) {
        //float speed = aMapLocation.getSpeed()*3.6f;
        if (aMapLocation.getLocationType() == AMapLocation.LOCATION_TYPE_GPS) {
            mGpsCal8ScendSpeedList.add(aMapLocation);
        } else {
            mIndoorCal8ScendSpeedList.add(aMapLocation.getSpeed());
        }
    }

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

        //有GPS则归为室外定位
        if(aMapLocation.getLocationType()==AMapLocation.LOCATION_TYPE_GPS){
            mHaveOutSideGpsLocation = true;
        }


        double tempDistance;
        if (mCurrentTimeMillis==0) {
            mCurrentTimeMillis = System.currentTimeMillis();
        }else {
            if (mHaveOutSideGpsLocation){
                AMapLocation lastAMapLocation = pathRecord.getPathline().get(pathRecord.getPathline().size() - 1);
                tempDistance = getTwoPointDistance(lastAMapLocation, aMapLocation);
                //Log.i(TAG,"tempDistance:"+tempDistance);

                if (tempDistance<=trackSpeedOutdoorMAX*((System.currentTimeMillis() - mCurrentTimeMillis) / 1000f)){
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
                //没有室外GPS定位，默认是在室内跑步
                if (aMapLocation.getSpeed()<trackSpeedIndoorMAX){
                    tempDistance = aMapLocation.getSpeed() * ((System.currentTimeMillis() - mCurrentTimeMillis) / 1000f); //s=vt,单位:m
                    mAddDistance += tempDistance;
                    mAllDistance = mAddDistance;
                }
                mCurrentTimeMillis = System.currentTimeMillis();
            }
        }

        //Log.i(TAG,"mAllDistance:"+mAllDistance);
        mFormatDistance = MyUtil.getFormatDistance(mAllDistance);
        tv_run_distance.setText(mFormatDistance);

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
                }
            }
        },8);//8s 计算速度

        deviceOffLineFileUtil.startTime();
    }

    private float preForOneKMSecond = -1;

    private void startCal8ScendSpeed() {
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
            if (preForOneKMSecond==-1){
                preForOneKMSecond = forOneKMSecond = (1/for8SecondAverageSpeed)*1000f;
            }
            else {
                forOneKMSecond = ((1/for8SecondAverageSpeed)*1000f+preForOneKMSecond)/2;
                preForOneKMSecond = forOneKMSecond;
            }

            if (forOneKMSecond>30*60){
                forOneKMSecond = 30*60;
            }
            else if(forOneKMSecond<2*60){
                forOneKMSecond = 2*60;
            }

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
        mGpsCal8ScendSpeedList.clear();

        mFinalFormatSpeed = formatSpeed;

    }

    //初始化定位
    private void initMapLoationTrace() {
        if (mLocationOption==null){
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(500);
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
        ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(this);
        if (isThreeMinute){

                saveSportRecord(createrecord);
                Intent intent = new Intent(InsoleRunningActivity.this, HeartRateAnalysisActivity.class);

                Log.i(TAG,"startActivity");
                startActivity(intent);
                finish();

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
        createrecord = Util.saveOrUdateRecord(pathRecord.getPathline(),addDuration, pathRecord.getDate(), this,mStartTime,mAllDistance,createrecord);
        Log.i(TAG,"createrecord:"+createrecord);
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
        CommunicateToBleService.detoryServiceForegrounByNotify();


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
        MyUtil.showToask(this,"结束");
        startActivity(new Intent(this,AnalyticFinshResultActivity.class));
        finish();
    }

}
