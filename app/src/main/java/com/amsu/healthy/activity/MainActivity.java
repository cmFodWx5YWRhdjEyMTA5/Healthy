package com.amsu.healthy.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Apk;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.utils.ApkUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.wifiTramit.DeviceOffLineFileUtil;
import com.amsu.healthy.view.CircleRingView;
import com.amsu.healthy.view.DashboardView;
import com.ble.ble.BleService;

import java.util.List;

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
    private int physicalAge;
    private int scoreALL;

    public static BleService mLeService;
    public static String connecMac;   //当前连接的蓝牙mac地址
    public static boolean isConnectted  =false;
    private boolean isConnectting  =false;

    private BaseActivity mActivity = this;
    private DeviceOffLineFileUtil deviceOffLineFileUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        //List<AppAbortDataSaveUtil.AbortData> abortDataListFromSP = AppAbortDataSaveUtil.getAbortDataListFromSP();
        //Log.i(TAG,"abortDataListFromSP:"+abortDataListFromSP.toString());

        //AppAbortDataSaveUtil.putAbortDataListToSP(new ArrayList<AppAbortDataSaveUtil.AbortData>());


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




    }


    int i = 0;

    private void initView() {
        initHeadView();
        setLeftText(getResources().getString(R.string.app_name));
        setCenterText("");
        setHeadBackgroudColor("#0c64b5");
        setRightImage(R.drawable.yifu);
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MyDeviceActivity.class));
            }
        });
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



        MyOnClickListener myOnClickListener = new MyOnClickListener();

        rl_mian_start.setOnClickListener(myOnClickListener);

        rl_main_healthydata.setOnClickListener(myOnClickListener);
        rl_main_sportcheck.setOnClickListener(myOnClickListener);
        rl_main_sportarea.setOnClickListener(myOnClickListener);
        rl_main_me.setOnClickListener(myOnClickListener);
        rl_main_age.setOnClickListener(myOnClickListener);

        rl_main_healthyvalue.setOnClickListener(myOnClickListener);
        rl_main_warringindex.setOnClickListener(myOnClickListener);


        int id = rl_main_healthyvalue.getId();

        Log.i(TAG,"id:"+id);

        //showUploadOffLineData();


        //sendStartDataTransmitOrderToBlueTooth();
        //sendDeviceSynOrderToBlueTooth();

        /*DeviceOffLineFileUtil.setTransferTimeOverTime(new DeviceOffLineFileUtil.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                Log.i(TAG,"5s没有收到数据");
                //mIsDataStart = false;
                isConnectted = mIsDataStart = MyApplication.isHaveDeviceConnectted  = false;
            }
        },5);*/

        /*OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(this);
        offLineDbAdapter.open();

        UploadRecord uploadRecord = new UploadRecord();
        uploadRecord.setUploadState("0");
        uploadRecord.setFI("BBBBBBBB");
        uploadRecord.setId("1496398469312");

        offLineDbAdapter.createOrUpdateUploadReportObject(uploadRecord);

        List<UploadRecord> uploadRecordsState = offLineDbAdapter.queryRecordByUploadState("0");
        Log.i(TAG,"uploadRecordsState:"+uploadRecordsState);*/


        /*OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(this);
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

        final BaseActivity activity = MyApplication.mCurrApplicationActivity;
        Log.i(TAG,"MyApplication.mCurrApplicationActivity:"+MyApplication.mCurrApplicationActivity.getClass().getSimpleName());
        Log.i(TAG,"activity:"+activity.getClass().getSimpleName());


    }

    //给文本年龄设置文字动画
    private void setAgeTextAnimator(final TextView textView,int startAge,int endAge) {
        mValueAnimator = ValueAnimator.ofInt(startAge, endAge);
        mValueAnimator.setDuration(Constant.AnimatorDuration);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(animation.getAnimatedValue().toString());
            }
        });
    }

    private void initData() {
        checkAndOpenBLEFeature();
        checkIsNeedUpadteApk();

        int healthyIindexvalue = MyUtil.getIntValueFromSP("healthyIindexvalue");
        if (healthyIindexvalue!=-1){
            tv_main_indexvalue.setText(healthyIindexvalue+"");
        }
        physicalAge = MyUtil.getIntValueFromSP("physicalAge");
        if (healthyIindexvalue>0){
            setAgeTextAnimator(tv_main_age,0, physicalAge);
            dv_main_compass.setAgeData(physicalAge-10);
        }

        Log.i(TAG,"healthyIindexvalue:"+healthyIindexvalue+"  physicalAge:"+physicalAge);
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
        if (!isonResumeEd){
            if (mBluetoothAdapter!=null && !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            isonResumeEd = true;
        }

        if (mValueAnimator!=null){
            mValueAnimator.start();
            cv_mian_index.setValue(170);
            cv_mian_warring.setValue(270);
            if (scoreALL >0){
                dv_main_compass.setAgeData(physicalAge-10);
            }
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

        //mLeService.disconnect(connecMac);
    }

    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Log.i(TAG,"onClick:"+v.getId());
            boolean isLogin = MyUtil.getBooleanValueFromSP("isLogin");
            boolean isPrefectInfo = MyUtil.getBooleanValueFromSP("isPrefectInfo");
            if (!isLogin){
                showdialogToLogin();
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
                    //startActivity(new Intent(MainActivity.this,MotionDetectionActivity.class));
                    break;
                case R.id.rl_main_me:
                    startActivity(new Intent(MainActivity.this,MeActivity.class));
                    break;
                case R.id.rl_mian_start:
                    startActivity(new Intent(MainActivity.this,StartRunActivity.class));
                    break;
                case R.id.rl_main_age:
                    Intent intent = new Intent(MainActivity.this, PhysicalAgeActivity.class);
                    intent.putExtra("physicalAge",physicalAge);
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
            }
        }
    }

    public void showdialogToLogin(){
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
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
        new AlertDialog.Builder(this).setTitle("数据提醒")
                .setMessage("现在去完善资料")
                .setPositiveButton("等会再去", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("现在就去", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this,SupplyPersionDataActivity.class));
                        //finish();
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
                finish();

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
