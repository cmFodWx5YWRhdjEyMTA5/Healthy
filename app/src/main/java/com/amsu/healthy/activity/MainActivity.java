package com.amsu.healthy.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.utils.BleConstant;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.marathon.MarathonActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Apk;
import com.amsu.healthy.utils.ApkUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.wifiTransmit.DeviceOffLineFileUtil;
import com.amsu.healthy.view.CircleRingView;
import com.amsu.healthy.view.DashboardView;
import com.ble.ble.BleService;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;

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
    private BleConnectionProxy mBleConnectionProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        Log.i(TAG,"onCreate");

        initView();
        initData();

    }

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

        mBleConnectionProxy = BleConnectionProxy.getInstance();


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.i(TAG,"heigth : " + dm.heightPixels);
        Log.i(TAG,"width : " + dm.widthPixels);
    }

    private void initData() {
        checkAndOpenBLEFeature();
        checkIsNeedUpadteApk();
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

        Log.i(TAG,"衣服电量："+mBleConnectionProxy.getClothCurrBatteryPowerPercent());

        if (mBleConnectionProxy.ismIsConnectted()){
            iv_base_connectedstate.setImageResource(R.drawable.yilianjie);

            String batteryPercent = "";
            int deviceType = mBleConnectionProxy.getmConnectionConfiguration().deviceType;
            if (deviceType== BleConstant.sportType_Cloth){
                if (mBleConnectionProxy.getClothCurrBatteryPowerPercent()>0 && mBleConnectionProxy.getClothCurrBatteryPowerPercent()<=100){
                    batteryPercent = mBleConnectionProxy.getClothCurrBatteryPowerPercent()+"%";
                }
            }
            else if (deviceType== BleConstant.sportType_Insole){
                for (BleDevice bleDevice : mBleConnectionProxy.getmInsoleDeviceBatteryInfos().values()) {
                    if (bleDevice.getBattery()>0 && bleDevice.getBattery()<=100){
                        batteryPercent +="  "+ bleDevice.getBattery()+"%";
                    }
                }
            }

            if (!TextUtils.isEmpty(batteryPercent)){
                tv_base_charge.setVisibility(View.VISIBLE);
                tv_base_charge.setText(batteryPercent);
            }
            else {
                tv_base_charge.setVisibility(View.GONE);
            }
        }
        else {
            iv_base_connectedstate.setImageResource(R.drawable.duankai);
            tv_base_charge.setVisibility(View.GONE);
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

        if (physicalAge >10){
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
            List<Activity> mActivities = ((MyApplication) getApplication()).mActivities;
            if (!mActivities.contains(MainActivity.this)){
                mActivities.add(MainActivity.this);
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
                    List<BleDevice> bleDeviceListFromSP = MyUtil.getDeviceListFromSP();
                    if (bleDeviceListFromSP.size()==0){
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

        MyUtil.setDialogNull();
    }

    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.One_more_exit_program), Toast.LENGTH_SHORT).show();
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
                finish();
                Process.killProcess(Process.myPid());

                //android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
