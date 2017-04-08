package com.amsu.healthy.activity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.TraceLocation;
import com.amsu.healthy.R;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.RunTimerTaskUtil;
import com.amsu.healthy.utils.map.PathRecord;
import com.amsu.healthy.utils.map.Util;
import com.amsu.healthy.view.GlideRelativeView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.amsu.healthy.R.id.bt_run_start;

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
    private boolean mIsRunning = false;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    public static AMapLocationClient mlocationClient;
    public static PathRecord record;    //存放未纠偏轨迹记录信息
    //private List<TraceLocation> mTracelocationlist = new ArrayList<>();   //偏轨后轨迹
    public static long mStartTime;
    private RunTimerTaskUtil runTimerTaskUtil;

    public static long createrecord =-1;
    private int calculateSpeedCount = 10;   //10次，一次2s,即为20s
    private RelativeLayout rl_run_bootom;
    private GlideRelativeView rl_run_glide;
    private RelativeLayout rl_run_lock;
    private double mAllDistance;
    private long mCurrentTimeMillis = 0;
    private boolean isThreeMit = false;   //是否到三分钟

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_run);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("运动检测");
        //setLeftImage(R.drawable.back_icon);
        setRightText("心电图");
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartRunActivity.this,HealthyDataActivity.class));
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
        
        MyOnClickListener myOnClickListener = new MyOnClickListener();
        bt_run_start.setOnClickListener(myOnClickListener);
        bt_run_location.setOnClickListener(myOnClickListener);
        bt_run_lock.setOnClickListener(myOnClickListener);


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
            }
        });

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.i(TAG,"onLocationChanged:"+aMapLocation.toString());
        Log.i(TAG,"getSpeed:"+aMapLocation.getSpeed());   // meters/second
        Log.i(TAG,"aMapLocation.getLocationType():"+aMapLocation.getLocationType());   // meters/second
        Log.i(TAG,"aMapLocation.getErrorCode():"+aMapLocation.getErrorCode());   // meters/second

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

    //计算跑步速度，在室内和室外统一采用地图返回的传感器速度
    private void calculateSpeed(AMapLocation aMapLocation) {
        float speed = aMapLocation.getSpeed()*3.6f;
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String formatSpeed=decimalFormat.format(speed);//format 返回的是字符串
        tv_run_speed.setText(formatSpeed);
    }

    private void calculateDistance(AMapLocation aMapLocation) {
         if (aMapLocation.getLocationType()==AMapLocation.LOCATION_TYPE_GPS){
            //室外,GPS定位数据，在室外。计算距离方式：1、衣服计步器，2、根据地图返回的距离 ，3、地图利用手机传感器，
             AMapLocation lastAMapLocation = record.getPathline().get(record.getPathline().size() - 1);
             double tempDistance = getTwoPointDistance(lastAMapLocation, aMapLocation);
             if (tempDistance<100){   //2个点之间距离小于100m为正常定位情况，负责为噪声去除
                 record.addpoint(aMapLocation);
                 mAllDistance += tempDistance;
             }
        }
        else {
            //室内,非GPS定位，说明信号差，属于室内定位。计算距离方式：1、衣服计步器，3、地图利用手机传感器计算移动速度
             if (mCurrentTimeMillis!=0.0){ //不是第一次
                 float tempDistance = aMapLocation.getSpeed() * ((System.currentTimeMillis() - mCurrentTimeMillis) / 1000); //s=vt,单位:m
                 mAllDistance += tempDistance;
             }
         }
        mCurrentTimeMillis = System.currentTimeMillis();
        tv_run_distance.setText(getFormatDistance((int) mAllDistance/1000));
    }


    /**根据高德地图定位的2个点来计算距离
     * @param aMapLocation
     */
    private double getTwoPointDistance(AMapLocation lastAMapLocation,AMapLocation aMapLocation) {
        LatLng thisLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        LatLng lastLatLng = new LatLng(lastAMapLocation.getLatitude(), lastAMapLocation.getLongitude());
        return (double) AMapUtils.calculateLineDistance(thisLatLng, lastLatLng);
    }

    private String getFormatDistance(int distance) {
        int secend = (distance%1000)/10;
        String myDistance = distance/1000+"."+secend;
        if (secend==0) {
            myDistance = distance/1000+".00";
        }
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String formatSpeed=decimalFormat.format(Double.parseDouble(myDistance));//format 返回的是字符串
        return formatSpeed;
    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_run_start:
                    startRunning();
                    break;

                case R.id.bt_run_lock:
                    rl_run_lock.setVisibility(View.VISIBLE);
                    rl_run_bootom.setVisibility(View.GONE);
                    break;

                case R.id.bt_run_location:
                    startActivity(new Intent(StartRunActivity.this,RunTrailMapActivity.class));
                    break;
            }
        }
    }

    //结束运动
    private void endRunning() {
        ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(this);
        if (isThreeMit){
            chooseAlertDialogUtil.setAlertDialogText("采集恢复心率需要继续采集一分钟的心电数据，是否进行采集？","是","否");
            chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
                @Override
                public void onConfirmClick() {
                    saveSportRecord();
                    Intent intent = new Intent(StartRunActivity.this, CalculateHRRProcessActivity.class);
                    intent.putExtra(Constant.sportState,1);
                    startActivity(intent);
                    finish();
                }
            });
            chooseAlertDialogUtil.setOnCancelClickListener(new ChooseAlertDialogUtil.OnCancelClickListener() {
                @Override
                public void onCancelClick() {
                    saveSportRecord();
                    Intent intent = new Intent(StartRunActivity.this, HeartRateActivity.class);
                    intent.putExtra(Constant.sportState,1);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else {
            chooseAlertDialogUtil.setAlertDialogText("跑步时间太短，无法保存记录，是否继续跑步？","继续跑步","结束跑步");
            chooseAlertDialogUtil.setOnCancelClickListener(new ChooseAlertDialogUtil.OnCancelClickListener() {
                @Override
                public void onCancelClick() {
                    mlocationClient.stopLocation();
                    finish();
                }
            });
        }

        /*if (createrecord!=-1){
            intent.putExtra("createrecord",createrecord);
        }*/

    }

    //记录运动休信息（和地图有关的）
    private void saveSportRecord() {
        createrecord = Util.saveRecord(record.getPathline(), record.getDate(), this,mStartTime);
        mlocationClient.stopLocation();
        mIsRunning = false;
        finish();
    }

    //开始运动
    private void startRunning() {
        if (!mIsRunning){
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // 判断GPS模块是否开启，如果没有则开启
            if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                chooseOpenGps();
            }
            else {
                bt_run_start.setText("长按结束");
                bt_run_lock.setVisibility(View.VISIBLE);
                mIsRunning  =true;

                //开启三分钟计时，保存记录最短为3分钟
                MyTimeTask.startCountDownTimerTask(1000 * 60 * 3, new MyTimeTask.OnTimeOutListener() {
                    @Override
                    public void onTomeOut() {
                        isThreeMit = true;
                    }
                });

                //开始计时，更新时间
                MyTimeTask.startTimeRiseTimerTask(this, 1000, new MyTimeTask.OnTimeChangeAtScendListener() {
                    @Override
                    public void onTimeChange(Date date) {
                        String specialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", date);
                        tv_run_time.setText(specialFormatTime);
                    }
                });

                initMapLoationTrace();
            }
        }
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

    //打开gps
    private void chooseOpenGps() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(StartRunActivity.this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.choose_opengps_dailog, null);

        bottomSheetDialog.setContentView(inflate);
        Window window = bottomSheetDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.opengpsdialogstyle);  //添加动画
        bottomSheetDialog.show();

        TextView bt_opengps_cancel = (TextView) inflate.findViewById(R.id.bt_opengps_cancel);
        TextView bt_opengps_ok = (TextView) inflate.findViewById(R.id.bt_opengps_ok);

        bt_opengps_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bt_opengps_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //跳到设置页面
                // 转到手机设置界面，用户设置GPS
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            if (mIsRunning){
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
