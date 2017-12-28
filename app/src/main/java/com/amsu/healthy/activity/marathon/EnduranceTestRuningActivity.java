package com.amsu.healthy.activity.marathon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amsu.bleinteraction.bean.MessageEvent;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.proxy.BleDataProxy;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.HealthyDataActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.DateFormatUtils;
import com.amsu.healthy.utils.MarathonUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.UStringUtil;
import com.amsu.healthy.utils.map.Util;
import com.amsu.healthy.view.GlideRelativeView;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.amsu.healthy.utils.Constant.enduranceTest;

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

    public Date mCurrTimeDate;
    private String TAG = "EnduranceTestRuningActivity";
    private Date date;
    private ArrayList<Integer> heartRateDates = new ArrayList<>();  // 心率数组
    private View testButtons_rl;
    private View rl_run_lock;
    private GlideRelativeView rl_run_glide;
    private boolean isLockScreen;
    private CountDownTimer countDownTimer;

    public boolean mIsRunning = false;
    private ArrayList<Integer> mStridefreData = new ArrayList<>();
    private MyApplication application;
    private BleDataProxy mBleDataProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endurance_test_runing);
        initHeadView();
        initViews();
        initEvents();
        countDownTime();
        initMapLoationTrace();
        enduranceTest = true;
    }

    private void initViews() {
        setLeftImage(R.drawable.back_icon);
        setCenterText(getResources().getString(R.string.endurance_test));
        sportTime = (TextView) findViewById(R.id.sportTime);
        sport_distance_tv = (TextView) findViewById(R.id.sport_distance_tv);
        heartRate_tv = (TextView) findViewById(R.id.heartRate_tv);
        sport_speed_tv = (TextView) findViewById(R.id.sport_speed_tv);
        stride_frequency_tv = (TextView) findViewById(R.id.stride_frequency_tv);
        testButtons_rl = findViewById(R.id.testButtons_rl);
        rl_run_lock = findViewById(R.id.rl_run_lock);
        rl_run_glide = (GlideRelativeView) findViewById(R.id.rl_run_glide);

        EventBus.getDefault().register(this);

        application = (MyApplication) getApplication();
        application.setRunningRecoverType(Constant.sportType_Cloth);
        application.setRunningCurrTimeDate(mCurrTimeDate = new Date(0, 0, 0));
        mIsRunning = true;

        mBleDataProxy = BleDataProxy.getInstance();
        mBleDataProxy.setRecordingStarted();
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
                backJudge();
            }
        });
        setRightImage(R.drawable.xindiantu_icon);
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EnduranceTestRuningActivity.this, HealthyDataActivity.class);
                intent.putExtra(Constant.isLookupECGDataFromSport, true);
                startActivity(intent);
            }
        });
        findViewById(R.id.tv_run_lock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testButtons_rl.setVisibility(View.INVISIBLE);
                rl_run_lock.setVisibility(View.VISIBLE);
                getIv_base_leftimage().setVisibility(View.GONE);
                getTv_base_rightText().setClickable(false);
                isLockScreen = true;
            }
        });
        rl_run_glide.setOnONLockListener(new GlideRelativeView.OnONLockListener() {
            @Override
            public void onLock() {
                testButtons_rl.setVisibility(View.VISIBLE);
                rl_run_lock.setVisibility(View.GONE);
                getIv_base_leftimage().setVisibility(View.VISIBLE);
                getTv_base_rightText().setClickable(true);
                isLockScreen = false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isLockScreen) {
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            backJudge();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backJudge() {
        ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(this);
        chooseAlertDialogUtil.setAlertDialogText(getResources().getString(R.string.testing_runing_quit));
        chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
            @Override
            public void onConfirmClick() {
                mBleDataProxy.stopWriteEcgToFileAndGetFileName();
                finish();
            }
        });
    }

    private void countDownTime() {
        date = new Date();
        countDownTimer = new CountDownTimer(12 * 60 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                String time = DateFormatUtils.getFormatTime(l, DateFormatUtils.MM_SS);
                sportTime.setText(time);
                if (application != null) {
                    application.setRunningDate(time);
                }
            }

            @Override
            public void onFinish() {
                finishSport();
                if (application != null) {
                    application.setRunningDate("0:00");
                }
            }
        };
        countDownTimer.start();
    }

    /**
     * 结束运动
     */
    private void finishSport() {
        uploadData();
    }

    private String hr = "";
    private String strideFrequency = "";

    private void uploadData() {
        String[] fileNames = mBleDataProxy.stopWriteEcgToFileAndGetFileName();

        RequestParams params = new RequestParams();
        Gson gson = new Gson();

        if (!heartRateDates.isEmpty()) {
            hr = gson.toJson(heartRateDates);
        }

        if (!mStridefreData.isEmpty()) {
            strideFrequency = gson.toJson(mStridefreData);
        }
        String fileBase64 = null;
        try {
            fileBase64 = Util.encodeBase64File(fileNames[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String EC = "";
        if (!UStringUtil.isNullOrEmpty(fileBase64)) {
            EC = fileBase64;
        }
        final float speed = calculateSpeed();
        final String distance = sport_distance_tv.getText().toString();
        double dis = 0;
        try {
            dis = Double.parseDouble(distance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String AHR = "";
        int MaxHR = 0;
        int MinHR = 0;
        int averHeart = 0;
        if (heartRateDates.size() > 0) {
            MaxHR = heartRateDates.get(0);
            MinHR = heartRateDates.get(0);
            int sum = 0;
            int graterZeroCount = 0;
            for (int heart : heartRateDates) {
                if (heart > 0) {
                    if (heart > MaxHR) {
                        MaxHR = heart;
                    }
                    if (heart < MinHR) {
                        MinHR = heart;
                    }
                    sum += heart;
                    graterZeroCount++;
                }
            }
            if (graterZeroCount > 0) {
                averHeart = sum / graterZeroCount;
            }
            AHR = String.valueOf(averHeart);
        }
        double vo2;
        if (dis == 0) {
            vo2 = 0.0;
        } else {
            vo2 = 22.34 * dis - 11.29;
        }
        if (vo2 < 0) {
            vo2 = 0;
        }
        MyUtil.showDialog(getResources().getString(R.string.please_wait_a_moment), this);
        final double Vo2max = vo2;
        final String dateStr = DateFormatUtils.getFormatTime(date, DateFormatUtils.YYYY_MM_DD_HH_MM_SS_);
        final String enduranceLevel = MarathonUtil.getEnduranceLevel(dis);
        params.addBodyParameter("hr", hr);
        params.addBodyParameter("strideFrequency", strideFrequency);
        params.addBodyParameter("EC", EC);
        params.addBodyParameter("distance", String.valueOf(dis));
        params.addBodyParameter("date", dateStr);
        params.addBodyParameter("averagePace", String.valueOf(speed));
        params.addBodyParameter("enduranceLevel", enduranceLevel);
        params.addBodyParameter("ahr", AHR);
        params.addBodyParameter("vo2max", String.valueOf(Vo2max));
        params.addBodyParameter("maxhr", String.valueOf(MaxHR));
        MyUtil.addCookieForHttp(params);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.uploadEnduranceDataURL, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(EnduranceTestRuningActivity.this);
                String json = responseInfo.result;
                Log.e("json", json);
                Intent intent = EnduranceTestResultActivity.createIntent(EnduranceTestRuningActivity.this);
                intent.putExtra("date", dateStr);
                intent.putExtra("Vo2max", Vo2max);
                intent.putExtra("speed", speed);
                intent.putExtra("distance", distance);
                intent.putExtra("hr", hr);
                intent.putExtra("strideFrequency", strideFrequency);
                intent.putExtra("enduranceLevel", enduranceLevel);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog(EnduranceTestRuningActivity.this);
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        enduranceTest = false;
        countDownTimer.cancel();
        EventBus.getDefault().unregister(this);

        destorySportInfoTOAPP();
    }

    private void destorySportInfoTOAPP() {
        if (application != null) {
            application.setRunningCurrTimeDate(null);
            application.setRunningFinalFormatSpeed(null);
            application.setRunningRecoverType(-1);
            application.setRunningFormatDistance(null);
            application.setRunningDate(getString(R.string.null_value));
            application.setRunningmCurrentHeartRate(0);
            application = null;
        }
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
        if (speed > 50) {
            speed = 0;
        }
        speedList.add(speed);
        if (speed > 0) {
            locationList.add(aMapLocation);
        }
        double lat = aMapLocation.getLatitude();
        double lon = aMapLocation.getLongitude();
        if (lat != 0 && lon != 0) {
            MyUtil.putStringValueFromSP("lat", String.valueOf(lat));
            MyUtil.putStringValueFromSP("lon", String.valueOf(lon));
        }
        double distance = calculateDistance();
        String dis = UStringUtil.formatNumber(distance / 1000, 2);
        sport_distance_tv.setText(dis);
        String speedData = UStringUtil.getSpeed(speed);
        sport_speed_tv.setText(speedData);
        if (application != null) {
            application.setRunningFinalFormatSpeed(speedData);
            application.setRunningFormatDistance(dis);
        }
    }

    private float calculateSpeed() {
        float result = 0;
        for (Float aFloat : speedList) {
            result += aFloat;
        }
        int size = speedList.size();
        if (size > 0) {
            return result / size;
        }
        return 0;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.messageType){
            case BleConnectionProxy.msgType_HeartRate:
                updateUIECGHeartData(event.singleValue);
                break;
            case BleConnectionProxy.msgType_Stride:
                updateUIStrideData(event.singleValue);
                break;
        }
    }

    private void dealwithLebDataChange(Intent intent) {
        int stride = intent.getIntExtra(BleDataProxy.EXTRA_STRIDE_DATA, -1);
        int heartRate = intent.getIntExtra(BleDataProxy.EXTRA_HEART_DATA, -1);

        if (stride != -1) {
            Log.i(TAG, "stride:" + stride);
            updateUIStrideData(stride);
        } else if (heartRate != -1) {
            Log.i(TAG, "heartRate:" + heartRate);
            updateUIECGHeartData(heartRate);
        }
    }

    private void updateUIStrideData(int stride) {
        stride_frequency_tv.setText(String.valueOf(stride));
        mStridefreData.add(stride);
    }

    private void updateUIECGHeartData(int heartRate) {
        heartRate_tv.setText(String.valueOf(heartRate));
        heartRateDates.add(heartRate);
    }

}
