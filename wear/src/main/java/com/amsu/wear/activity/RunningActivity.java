package com.amsu.wear.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amsu.bleinteraction.bean.MessageEvent;
import com.amsu.bleinteraction.proxy.Ble;
import com.amsu.wear.R;
import com.amsu.wear.adapter.FragmentListAdapter;
import com.amsu.wear.application.MyApplication;
import com.amsu.wear.bean.RunningData;
import com.amsu.wear.fragment.running.AerobicFragment;
import com.amsu.wear.fragment.running.BaseDataFragment;
import com.amsu.wear.fragment.running.CalorieFragment;
import com.amsu.wear.fragment.running.MileageFragment;
import com.amsu.wear.fragment.running.PaceFragment;
import com.amsu.wear.fragment.running.StrideFragment;
import com.amsu.wear.fragment.running.TimeFragment;
import com.amsu.wear.fragment.running.TrackFragment;
import com.amsu.wear.map.MapUtil;
import com.amsu.wear.map.PathRecord;
import com.amsu.wear.map.proxy.GaodeMapProxy;
import com.amsu.wear.map.proxy.MapProxy;
import com.amsu.wear.myinterface.Function;
import com.amsu.wear.myinterface.ObservableManager;
import com.amsu.wear.util.FormatUtil;
import com.amsu.wear.util.HeartUtil;
import com.amsu.wear.util.HttpUtil;
import com.amsu.wear.util.LogUtil;
import com.amsu.wear.util.TimerUtil;
import com.amsu.wear.util.UploadDataUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class RunningActivity extends BaseActivity implements Function  {
    @BindView(R.id.fl_running_finsh)
    FrameLayout fl_running_finsh;

    @BindView(R.id.vp_running_info)
    ViewPager vp_running_info;

    @BindView(R.id.tv_running_countdown)
    TextView tv_running_countdown;

    @BindView(R.id.sportDistance)
    TextView sportDistance;
    @BindView(R.id.sportFinishTime)
    TextView sportFinishTime;

    @BindView(R.id.ll_running_uload)
    LinearLayout ll_running_uload;

    private static final String TAG = RunningActivity.class.getSimpleName();
    public static final String FUNCTION_WITH_PARAM_AND_RESULT = "FUNCTION_WITH_PARAM_AND_RESULT_ACTIVITY";

    private long mStartTime;
    private RunningData mRunningData;
    private Timer mShowTimetimer;
    private ArrayList<Integer> mSpeedStringList;  //配速数组,表示一公里所用的秒数
    public PathRecord mPathRecord;    //存放未纠偏轨迹记录信息
    private double mAllDistance;
    private static final long saveDataToLocalTimeMillis = 1000*60;  //数据缓存到本地的时间间隔

    private ArrayList<Integer> mHeartRateDates = new ArrayList<>();  // 心率数组
    private HeartUtil heartUtil;
    private CopyOnWriteArrayList<String> mKcalData = new CopyOnWriteArrayList<>();
    private ArrayList<Integer> mStridefreData = new ArrayList<>();
    private float mAllKcal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_running;
    }

    private void initView() {
        countDownTime();

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new PaceFragment());
        fragmentList.add(new TimeFragment());
        fragmentList.add(new MileageFragment());
        fragmentList.add(new AerobicFragment());
        fragmentList.add(new StrideFragment());
        fragmentList.add(new CalorieFragment());
        fragmentList.add(new TrackFragment());

        vp_running_info.setAdapter(new FragmentListAdapter(getSupportFragmentManager(), fragmentList));
        vp_running_info.setCurrentItem(1);
        mRunningData = new RunningData("--","--","--","--","--","--","--");
    }

    private void countDownTime() {
        new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                startAnimation(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                tv_running_countdown.setVisibility(View.GONE);
                vp_running_info.setVisibility(View.VISIBLE);
                startUpdateTime();
            }
        }.start();
    }

    private void startUpdateTime() {
        //更新时间，1秒更新一次
        mShowTimetimer = TimerUtil.executeIntervals(1000, new TimerUtil.DelayExecuteTimeListener() {
            Date date = new Date(0, 0, 0, 0, 0, 0);

            @Override
            public void execute() {
                mRunningData.setTime(FormatUtil.getSpecialFormatTime("HH:mm:ss", date));
                ObservableManager.newInstance().notify(BaseDataFragment.FUNCTION_WITH_PARAM_AND_RESULT, mRunningData);
                date = new Date(date.getTime() + 1000);
                checkSaveDataToLoacl();
            }
        });

        Ble.bleDataProxy().startRecording();
        initMapLoationTrace();

        // 注册事件,Activity实现Function接口
        ObservableManager.newInstance().registerObserver(FUNCTION_WITH_PARAM_AND_RESULT, this);
        EventBus.getDefault().register(this);

        MyApplication.getInstance().setRunning(true);
    }

    private long preTimeMillis;
    private void checkSaveDataToLoacl() {
        if (preTimeMillis!=-1 && (System.currentTimeMillis()-preTimeMillis)>saveDataToLocalTimeMillis){
            //保存数据到本地
            saveDataToLocal();
        }
        preTimeMillis = System.currentTimeMillis();
    }

    private void saveDataToLocal() {

    }

    private int mPreGpsGpsAccuracyStatus = -1;

    //初始化定位
    private void initMapLoationTrace() {
        MapProxy mapProxy = new GaodeMapProxy(this,true);
        mapProxy.init();

        mapProxy.setOnMapDataListener(new MapProxy.OnMapDataListener() {
            @Override
            public void onReceivePace(int forOneKMSecond) {
                mSpeedStringList.add(forOneKMSecond);
                mRunningData.setPace(MapProxy.getFormatPace(forOneKMSecond));
            }

            @Override
            public void onReceiveLocation(AMapLocation aMapLocation, PathRecord pathRecord, double allDistance) {
                mRunningData.setMileage(FormatUtil.getFormatDistance(allDistance));
                ObservableManager.newInstance().notify(TrackFragment.FUNCTION_WITH_PARAM_AND_RESULT, pathRecord);
                mAllDistance = allDistance;
                mPathRecord = pathRecord;

                //GPS当前精度
                int gpsAccuracyStatus = aMapLocation.getGpsAccuracyStatus();
                LogUtil.i(TAG,"gpsAccuracyStatus:"+gpsAccuracyStatus);
                if (gpsAccuracyStatus!=mPreGpsGpsAccuracyStatus){
                    //更新GPS
                    ObservableManager.newInstance().notify(BaseDataFragment.FUNCTION_WITH_PARAM_AND_RESULT, gpsAccuracyStatus);
                    mPreGpsGpsAccuracyStatus = gpsAccuracyStatus;
                }
            }
        });

        mStartTime = System.currentTimeMillis();
        mSpeedStringList = new ArrayList<>();
        mPathRecord = new PathRecord();

    }

    private void startAnimation(long millisUntilFinished) {
        if (millisUntilFinished / 1000>=1){
            tv_running_countdown.setText(String.valueOf(millisUntilFinished / 1000));
        }
        else {
            tv_running_countdown.setText("GO");
        }
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(tv_running_countdown, "scaleX", 2.0f, 1.0f), ObjectAnimator.ofFloat(tv_running_countdown, "scaleY", 2.0f, 1.0f));
        set.setDuration(500).start();
    }

    @Override
    public Object function(Object[] data) {
        List<Object> objects = Arrays.asList(data);
        LogUtil.i(TAG,"function:"+objects);

        if (objects.size()>0){
            if (objects.get(0) instanceof Integer){
                Integer fragmentIdnex = (Integer) objects.get(0);
                switch (fragmentIdnex){
                    case TrackFragment.TRACKFRAGMENT_DATA:
                        ObservableManager.newInstance().notify(TrackFragment.FUNCTION_WITH_PARAM_AND_RESULT, mPathRecord);
                        break;
                    case BaseDataFragment.TIMEFRAGMENT_DATA:
                        ObservableManager.newInstance().notify(BaseDataFragment.FUNCTION_WITH_PARAM_AND_RESULT, mRunningData);
                        break;
                    case BaseDataFragment.TIMEFRAGMENT_STOP:
                        showIsStopPage();
                        break;
                }
            }
        }
        return "我是activity的返回结果";
    }

    //显示结束页面
    private void showIsStopPage() {
        fl_running_finsh.setVisibility(View.VISIBLE);
        vp_running_info.setVisibility(View.GONE);
        sportDistance.setText(FormatUtil.getFormatDistance(mAllDistance));

        String runFormatTime = FormatUtil.getRunFormatTime((System.currentTimeMillis() - mStartTime) / 1000);
        sportFinishTime.setText(runFormatTime+"");
    }

    @OnClick({R.id.sportContinue, R.id.sportFinish})
    public void onViewClicked(View view) {
        fl_running_finsh.setVisibility(View.GONE);
        switch (view.getId()) {
            case R.id.sportContinue:
                vp_running_info.setVisibility(View.VISIBLE);
                break;
            case R.id.sportFinish:
                ll_running_uload.setVisibility(View.VISIBLE);
                uploadSportData();
                break;
        }
    }

    private void uploadSportData() {
        if (mShowTimetimer!=null){
            mShowTimetimer.cancel();
            mShowTimetimer = null;
        }

        MyApplication.getInstance().setRunning(false);

        new Thread(){
            @Override
            public void run() {
                super.run();
                prepareUploadData();
            }
        }.start();
    }

    private void prepareUploadData() {
        String[] fileNames = Ble.bleDataProxy().stopRecording();
        Log.i(TAG,"fileNames:"+fileNames);
        if (fileNames!=null&& !TextUtils.isEmpty(fileNames[0])){
            long createrecord = MapUtil.saveOrUdateRecord(mPathRecord.getPathline(),0, mPathRecord.getDate(), this,mStartTime,mAllDistance,-1);
            UploadDataUtil uploadDataUtil = new UploadDataUtil();
            uploadDataUtil.generateUploadData(fileNames[0],mPathRecord,mHeartRateDates,1,0,mStartTime,mStridefreData,mKcalData,this,mSpeedStringList);
        }
    }

    //蓝牙数据相关通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Log.i(TAG,"MessageEvent:"+event );
        switch (event.messageType){
            case msgType_Connect:
                Log.i(TAG,"连接变化" );
                //boolean isConnected = event.singleValue == BleConnectionProxy.connectTypeConnected;
                //setDeviceConnectedState(isConnected);
                break;
            case msgType_HeartRate:
                updateUIECGHeartData(event.singleValue);
                break;
            case msgType_Stride:
                updateUIStrideData(event.singleValue);
                break;
        }
    }

    //数据上传完成相关通知
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(Integer event) {
        Log.i(TAG,"event:"+event );
        switch (event){
            case HttpUtil.HttpUploadData_success:
                finish();
                break;
            case HttpUtil.HttpUploadData_fail:
                finish();
                break;
        }
    }

    private void updateUIECGHeartData(int heartRate) {
       /* String showHeartString = heartRate==0?"--":heartRate+"";
        tv_run_rate.setText(showHeartString);*/
        //HeartUtil.updateHeartUI(heartRate,tv_run_rate,this);
        calcuAllkcal(heartRate);
        mHeartRateDates.add(heartRate);

        String oxygenState = HeartUtil.calcuOxygenState(heartRate,this);
        mRunningData.setHeartRate(heartRate+"");
        mRunningData.setAerobic(oxygenState);
    }

    //计算卡路里，累加
    private void calcuAllkcal(int heartRate) {
        if (heartUtil==null){
            heartUtil = new HeartUtil();
        }
        float getkcal = heartUtil.calcuAllkcal(heartRate);
        //防止蓝牙断开又重新连上后时间太长导致卡路里很大
        if (getkcal > 6 && mKcalData.size() > 0) {
            getkcal = Float.parseFloat(mKcalData.get(mKcalData.size() - 1));
            if (getkcal>10){
                getkcal = 0;
            }
        }
        mAllKcal += getkcal;
        mKcalData.add(getkcal + "");
        mRunningData.setCalorie((int)mAllKcal+"");
    }

    private void updateUIStrideData(int stride) {
        mRunningData.setStride(stride+"");
        mStridefreData.add(stride);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        if (mShowTimetimer!=null){
            mShowTimetimer.cancel();
            mShowTimetimer = null;
        }
        MyApplication.getInstance().setRunning(false);
        //ToastUtil.showToask("运动已结束");
    }


    //屏蔽返回键，不能返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
       showIsStopPage();
        return false;
        //return super.onKeyDown(keyCode, event);
    }



}
