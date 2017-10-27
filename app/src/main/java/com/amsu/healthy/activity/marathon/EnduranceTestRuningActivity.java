package com.amsu.healthy.activity.marathon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.HealthyDataActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.DateFormatUtils;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.map.Util;
import com.ble.api.DataUtil;
import com.test.utils.DiagnosisNDK;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.amsu.healthy.activity.StartRunActivity.accOneGroupLength;
import static com.amsu.healthy.service.CommunicateToBleService.ecgFilterUtil_1;
import static com.amsu.healthy.utils.Constant.ecgLocalFileName;

/**
 * author：WangLei
 * date:2017/10/24.
 * QQ:619321796
 * 耐力测试运动中
 */

public class EnduranceTestRuningActivity extends BaseActivity implements AMapLocationListener {
    public static Intent createIntent(Context context) {
        return new Intent(context, EnduranceTestRuningActivity.class);
    }

    private TextView sportTime;
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    private List<AMapLocation> locationList = new ArrayList<>();
    private List<Float> speedList = new ArrayList<>();
    private TextView sport_distance_tv;
    private TextView heartRate_tv;
    private TextView sport_speed_tv;
    private TextView stride_frequency_tv;

    boolean isHaveDataTransfer;
    boolean mIsDataStart;
    public Date mCurrTimeDate;
    private String TAG = "EnduranceTestRuningActivity";

    private ArrayList<Integer> heartRateDates = new ArrayList<>();  // 心率数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endurance_test_runing);
        initHeadView();
        initViews();
        initEvents();
        countDownTime();
        initMapLoationTrace();
    }

    private void initViews() {
        setLeftImage(R.drawable.back_icon);
        setCenterText(getResources().getString(R.string.endurance_test));
        sportTime = (TextView) findViewById(R.id.sportTime);
        sport_distance_tv = (TextView) findViewById(R.id.sport_distance_tv);
        heartRate_tv = (TextView) findViewById(R.id.heartRate_tv);
        sport_speed_tv = (TextView) findViewById(R.id.sport_speed_tv);
        stride_frequency_tv = (TextView) findViewById(R.id.stride_frequency_tv);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());
        application = (MyApplication) getApplication();
        application.setRunningRecoverType(Constant.sportType_Cloth);
        mIsRunning = true;
        application.setRunningCurrTimeDate(mCurrTimeDate = new Date(0, 0, 0));
    }

    private void initEvents() {
        findViewById(R.id.fl_enduranceStart).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                finishSport();
                return false;
            }
        });
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void countDownTime() {
        CountDownTimer countDownTimer = new CountDownTimer(12 * 60 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                String time = DateFormatUtils.getFormatTime(l, DateFormatUtils.MM_SS);
                sportTime.setText(time);
            }

            @Override
            public void onFinish() {
                finishSport();
            }
        };

        countDownTimer.start();
    }

    private void finishSport() {
        startActivity(EnduranceTestResultActivity.createIntent(EnduranceTestRuningActivity.this));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
    }

    //初始化定位
    private void initMapLoationTrace() {
        if (mLocationOption == null) {
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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        float speed = aMapLocation.getSpeed();
        speedList.add(speed);
        if (speed > 0) {
            locationList.add(aMapLocation);
        }
        double distance = calculateDistance();
        float averageSpeed = calculateSpeed();
        String dis = Util.formatNumber(distance / 1000, 2);
        String s = Util.formatNumber(averageSpeed, 2);
        sport_distance_tv.setText(dis);
        sport_speed_tv.setText(s);
    }

    private float calculateSpeed() {
        float result = 0;
        for (Float aFloat : speedList) {
            result += aFloat;
        }
        return result / speedList.size();
    }

    /**
     * 计算距离
     */
    private double calculateDistance() {
        double distance = 0;
        for (int i = 0; i < locationList.size() - 1; i++) {
            AMapLocation aMapLocation1 = locationList.get(i);
            LatLng latLng1 = Util.getLatLng(aMapLocation1.getLatitude(), aMapLocation1.getLongitude());
            AMapLocation aMapLocation2 = locationList.get(i + 1);
            LatLng latLng2 = Util.getLatLng(aMapLocation2.getLatitude(), aMapLocation2.getLongitude());
            float x = AMapUtils.calculateLineDistance(latLng1, latLng2);
            distance += x;
        }
        return distance;
    }

    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
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
            if (hexData.length() == 44) {
                dealWithEcgData(hexData);
                isHaveDataTransfer = true;
                mIsDataStart = true;
            }
        } else if (hexData.startsWith("FF 86")) {
            //加速度数据
            //Log.i(TAG,"加速度hexData:"+hexData);
            //FF 86 11 00 A4 06 AC 1E 9D 00 A4 06 AC 1E 9D 11 16   长度50
            if (hexData.length() == 50) {
                dealWithAccelerationgData(hexData);
            }
        }
    }

    public static final int oneGroupLength = 10; //
    int[] ecgOneGroupDataInts = new int[oneGroupLength];
    private int mPreHeartRate;
    public static final int timeSpanGgroupCalcuLength = 60; //
    private boolean isNeedUpdateHeartRate = false;
    public boolean mIsRunning = false;
    private boolean isFirstCalcu = true;  //是否是第一次计算心率，第一次要连续12秒的数据
    private int currentGroupIndex = 0;   //组的索引
    public static final int calGroupCalcuLength = 180; //
    public int[] calcuEcgRate = new int[calGroupCalcuLength * oneGroupLength]; //1000条数据:（100组，一组有10个数据点）
    private int[] fourCalcuEcgRate = new int[timeSpanGgroupCalcuLength * oneGroupLength]; //4s的数据*/
    private int[] preCalcuEcgRate = new int[calGroupCalcuLength * oneGroupLength]; //前一次数的数据，12s
    public int mCurrentHeartRate = 0;
    private ArrayList<Integer> mStridefreData = new ArrayList<>();
    private MyApplication application;
    public static final int accDataLength = 1800;
    private int mTempStridefre;
    private DataOutputStream ecgDataOutputStream;  //二进制文件输出流，写入文件
    private DataOutputStream accDataOutputStream;  //二进制文件输出流，写入文件
    private ByteBuffer ecgByteBuffer;
    private ByteBuffer accByteBuffer;
    int[] accOneGroupDataInts = new int[accOneGroupLength];

    byte[] accByteData = new byte[accDataLength];
    private int accCalcuDataIndex = 0;


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
            if (application != null) {
                application.setRunningmCurrentHeartRate(mCurrentHeartRate);
            }

            Log.i(TAG, "mCurrentHeartRate:" + mCurrentHeartRate);
            //calcuEcgRate = new int[calGroupCalcuLength*10];

            System.arraycopy(calcuEcgRate, 0, preCalcuEcgRate, 0, calcuEcgRate.length);
            System.arraycopy(ecgOneGroupDataInts, 0, fourCalcuEcgRate, currentGroupIndex * 10, ecgOneGroupDataInts.length);

            mHandler.sendEmptyMessage(1);
        }
        currentGroupIndex++;
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    updateUIECGData();
                    break;
                case 2:
                    updateUIACCData();
                    break;
            }
            return false;
        }
    });

    private void updateUIACCData() {
        stride_frequency_tv.setText(String.valueOf(mTempStridefre));
    }

    //处理加速度数据
    private void dealWithAccelerationgData(String hexData) {
        if (!mIsRunning) return;
        ECGUtil.geIntEcgaArr(hexData, " ", 3, accOneGroupLength, accOneGroupDataInts); //一次的数据，12位

        if (mIsRunning) {
            writeAccDataToBinaryFile(accOneGroupDataInts);
        }

        if (accCalcuDataIndex < accDataLength) {
            for (int i : accOneGroupDataInts) {
                //accData.add(i);
                accByteData[accCalcuDataIndex++] = (byte) i;
            }
        } else {
            //计算
            /*byte[] bytes = new byte[accDataLength];
            for (int i=0;i<accData.size();i++){
                bytes[i] = (byte)(int)accData.get(i);
            }*/
            int[] results = new int[2];
            DiagnosisNDK.AnalysisPedo(accByteData, accDataLength, results);
            /*int state = -1;
            int pedoCount = -1;
            DiagnosisNDK.AnalysisPedo(bytes,accDataLength,state,pedoCount);
            Log.i(TAG,"state:"+state+",pedoCount:"+pedoCount);*/

            Log.i(TAG, "results: " + results[0] + "  " + results[1]);
            //每分钟的步数
            mTempStridefre = (int) (results[1] * 5.21);
            mStridefreData.add(mTempStridefre);
            mHandler.sendEmptyMessage(2);

            accCalcuDataIndex = 0;
            for (int i : accOneGroupDataInts) {
                accByteData[accCalcuDataIndex++] = (byte) i;
            }
        }
    }

    //acc数据写到文件里，二进制方式写入
    private void writeAccDataToBinaryFile(int[] ints) {
        try {
            if (accDataOutputStream == null) {
                String accLocalFileName = MyUtil.getClolthLocalFileName(2, new Date());
                Log.i(TAG, "accLocalFileName:" + accLocalFileName);
                //MyUtil.putStringValueFromSP("cacheFileName",fileAbsolutePath);
                accDataOutputStream = new DataOutputStream(new FileOutputStream(accLocalFileName, true));
                accByteBuffer = ByteBuffer.allocate(2);
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

    //ecg数据写到文件里，二进制方式写入
    private void writeEcgDataToBinaryFile(int[] ints) {
        try {
            if (ecgDataOutputStream == null) {
                ecgLocalFileName = MyUtil.getClolthLocalFileName(1, new Date());
                ;
                Log.i(TAG, "fileAbsolutePath:" + ecgLocalFileName);
                //MyUtil.putStringValueFromSP("cacheFileName",fileAbsolutePath);
                ecgDataOutputStream = new DataOutputStream(new FileOutputStream(ecgLocalFileName, true));
                ecgByteBuffer = ByteBuffer.allocate(2);
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

    //更新心率相关数据
    private void updateUIECGData() {
        String oxygenState = calcuOxygenState(mCurrentHeartRate);
        if (mCurrentHeartRate == 0) {
            heartRate_tv.setText("——");
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
            heartRate_tv.setText(String.valueOf(mCurrentHeartRate));
        }
        heartRateDates.add(mCurrentHeartRate);
        mPreHeartRate = mCurrentHeartRate;
    }

    private String calcuOxygenState(int heartRate) {
        int maxRate = 220 - HealthyIndexUtil.getUserAge();
        if (heartRate <= maxRate * 0.6) {
            return getResources().getString(R.string.exercise_flat);
        } else if (maxRate * 0.6 < heartRate && heartRate <= maxRate * 0.75) {
            return getResources().getString(R.string.exercise_oxygenated);
        } else if (maxRate * 0.75 < heartRate && heartRate <= maxRate * 0.95) {
            return getResources().getString(R.string.exercise_without_oxygen);
        } else if (maxRate * 0.95 < heartRate) {
            return getResources().getString(R.string.exercise_in_danger);
        }
        return getResources().getString(R.string.exercise_oxygenated);
    }
}
