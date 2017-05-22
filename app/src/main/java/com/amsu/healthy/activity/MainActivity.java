package com.amsu.healthy.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Apk;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.bean.DeviceList;
import com.amsu.healthy.utils.ApkUtil;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.CircleRingView;
import com.amsu.healthy.view.DashboardView;
import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;

import java.util.Date;
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

    private Activity mActivity = this;

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
        if (!MyApplication.mActivities.contains(this)){
            MyApplication.mActivities.add(this);
        }

        //showUploadOffLineData();




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
            ApkUtil.checkAndUpdateVersion(Integer.parseInt(apkFromSP.versioncode),apkFromSP.path,this,false);
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
        MyApplication.mApplicationActivity = this;
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
        if (mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled()) {
            startLeScanBlue();
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
                    //startActivity(new Intent(MainActivity.this,SportCommunityActivity.class));
                    startActivity(new Intent(MainActivity.this,MotionDetectionActivity.class));
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
        //finish();
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

    private void showUploadOffLineData(){
        ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(this);
        chooseAlertDialogUtil.setAlertDialogText("发现有离线文件，是否现在进行上传","是","否");
        chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
            @Override
            public void onConfirmClick() {
                Intent intent = new Intent(MainActivity.this, ConnectToWifiModuleGudieActivity1.class);
                startActivity(intent);

            }
        });
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
        mActivity = null;
        MyApplication.mApplicationActivity = null;
       // ShowLocationOnMap.mMapView = null;
        android.os.Process.killProcess(android.os.Process.myPid());  //退出应用程序

        unbindService(mConnection);
        if (MainActivity.mBluetoothAdapter!=null){
            MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
        }
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
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void startLeScanBlue(){
        if (MainActivity.mBluetoothAdapter!=null){
            MainActivity.mBluetoothAdapter.startLeScan(mLeScanCallback);
            Log.i(TAG,"startLeScan");
            //绑定蓝牙，获取蓝牙服务
            bindService(new Intent(this, BleService.class), mConnection, BIND_AUTO_CREATE);
        }
    }

    //扫描蓝牙回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            //BLE#0x44A6E51FC5BF,44:A6:E5:1F:C5:BF,null,10,2
            //null,72:A8:23:AF:25:42,null,10,0
            //null,63:5C:3E:B6:A0:AE,null,10,0
            Log.i(TAG,"onLeScan  device:"+device.getName()+","+device.getAddress()+","+device.getUuids()+","+device.getBondState()+","+device.getType());
            String stringValueFromSP = MyUtil.getStringValueFromSP(Constant.currectDeviceLEName);
            Log.i(TAG,"stringValueFromSP:"+stringValueFromSP);
            Log.i(TAG,"isConnectted:"+isConnectted);
            Log.i(TAG,"isConnectting:"+isConnectting);

            String leName = device.getName();
            if (leName!=null && leName.startsWith("BLE")) {

                if (leName.equals(stringValueFromSP)){  //只有扫描到的蓝牙是sp里的当前设备时（激活状态），才能进行连接
                    //配对成功
                    connecMac = device.getAddress();
                    if (!isConnectted && !isConnectting){
                        //没有链接上，并且没有正在链接
                        Log.i(TAG,"device.getAddress():"+device.getAddress());
                        if (mLeService!=null){
                            mLeService.connect(device.getAddress(),true);  //链接

                            isConnectting  = true;
                            Log.i(TAG,"开始连接");
                        }

                    }
                }
            }
        }
    };


    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"onServiceDisconnected");
            mLeService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG,"onServiceConnected");
            mLeService = ((BleService.LocalBinder) service).getService(mBleCallBack);
            // mLeService.setMaxConnectedNumber(max);// 设置最大可连接从机数量，默认为4
            mLeService.setDecode(true);
            // 必须调用初始化函数
            mLeService.initialize();
        }
    };

    // ble数据交互的关键参数
    private final BleCallBack mBleCallBack = new BleCallBack() {

        @Override
        public void onConnected(String mac) {
            Log.i(TAG, "onConnected() - " + mac);
            mLeService.startReadRssi(mac, 1000);
            if (MainActivity.mBluetoothAdapter!=null){
                //MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
            }
            if (!MyApplication.isHaveDeviceConnectted){
                MyApplication.isHaveDeviceConnectted = isConnectted = true;
                MyUtil.showPopWindow(mActivity,getTv_base_rightText(),1);
            }

            isConnectting = false;

        }

        @Override
        public void onConnectTimeout(String mac) {
            Log.w(TAG, "onConnectTimeout() - " + mac);
            isConnectting = false;
        }

        @Override
        public void onConnectionError(String mac, int status, int newState) {
            Log.w(TAG, "onConnectionError() - " + mac + ", status = " + status + ", newState = " + newState);
            isConnectting = false;
        }

        @Override
        public void onDisconnected(String mac) {
            Log.w(TAG, "onDisconnected() - " + mac);
            if (MyApplication.isHaveDeviceConnectted){
                MyApplication.isHaveDeviceConnectted = isConnectted = false;
                MyUtil.showPopWindow(mActivity,getTv_base_rightText(),0);
            }
            isConnectting = false;
            mIsDataStart = false;
        }

        @Override
        public void onServicesDiscovered(String mac) {
            // !!!到这一步才可以与从机进行数据交互
            Log.i(TAG, "onServicesDiscovered() - " + mac);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        /*Thread.sleep(1000);
                        Log.i(TAG, "查询SD卡是否有数据");
                        mLeService.send(connecMac, Constant.checkIsHaveDataOrder,true);*/

                        mIsDataStart = false;
                        Thread.sleep(1000);
                        Log.i(TAG,"写配置");
                        String writeConfigureOrder = "FF010A"+HealthyDataActivity.getDataHexString()+"0016";
                        Log.i(TAG,"writeConfigureOrder:"+writeConfigureOrder);
                        //mLeService.send(connecMac, Constant.writeConfigureOrder,true);
                        mLeService.send(connecMac, writeConfigureOrder,true);

                        Thread.sleep(1000);
                        Log.i(TAG,"开启数据指令");
                        mLeService.send(connecMac, Constant.openDataTransmitOrder,true);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            //给蓝牙设备同步指令
            final int mCurrentHeartRate = 0;
            MyTimeTask.startTimeRiseTimerTask(MainActivity.this, 1000, new MyTimeTask.OnTimeChangeAtScendListener() {
                @Override
                public void onTimeChange(Date date) {
                    if (mIsDataStart){
                        int maxRate = 220- HealthyIndexUtil.getUserAge();
                        String hrateIndexHex = "02";
                        if (mCurrentHeartRate<=maxRate*0.75){
                            hrateIndexHex = "02";
                        }
                        else if (maxRate*0.75<mCurrentHeartRate && mCurrentHeartRate<=maxRate*0.95){
                            hrateIndexHex = "01";
                        }
                        else if (maxRate*0.95<mCurrentHeartRate ){
                            hrateIndexHex = "00";
                        }

                        String hexSynOrder = "FF070B"+HealthyDataActivity.getDataHexStringHaveScend()+hrateIndexHex+"16";


                        mLeService.send(connecMac, hexSynOrder,true);



                        Log.i(TAG,"同步指令connecMac:"+connecMac+",hrateIndexHex:"+hrateIndexHex);
                    }


                }
            });

            /*new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(1000);
                        Log.i(TAG,"写配置");
                        String writeConfigureOrder = "FF010A"+getDataHexString()+"0016";
                        Log.i(TAG,"writeConfigureOrder:"+writeConfigureOrder);
                        //mLeService.send(connecMac, Constant.writeConfigureOrder,true);
                        mLeService.send(connecMac, writeConfigureOrder,true);

                        Thread.sleep(1000);
                        Log.i(TAG,"开启数据指令");
                        mLeService.send(connecMac, Constant.openDataTransmitOrder,true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();*/
        }

        @Override
        public void onServicesUndiscovered(String mac, int status) {
            Log.e(TAG, "onServicesUndiscovered() - " + mac + ", status = " + status);
        }

        @Override
        public void onReadRemoteRssi(String mac, int rssi, int status) {
            //Log.i(TAG,"onReadRemoteRssi==="+"mac:"+mac+",rssi:"+rssi+",status:"+status);
        }

        @Override
        public void onCharacteristicChanged(String mac, android.bluetooth.BluetoothGattCharacteristic c) {
            // 接收到从机数据
            String uuid = c.getUuid().toString();
            String hexData = DataUtil.byteArrayToHex(c.getValue());
            Log.i(TAG, "onCharacteristicChanged() - " + mac + ", " + uuid + ", " + hexData);
            //4.2写配置信息   onCharacteristicChanged() - 44:A6:E5:1F:C5:BF, 00001002-0000-1000-8000-00805f9b34fb, FF 81 05 00 16
            //4.5App读主机设备的版本号  onCharacteristicChanged() - 44:A6:E5:1F:C5:BF, 00001002-0000-1000-8000-00805f9b34fb, FF 84 07 88 88 00 16
            /*数据：
                FF 83 0F 00 00 00 00 00 00 00 00 00 00 00 16
                FF 83 0F 00 00 00 00 00 00 00 00 00 00 01 16
                FF 83 0F 00 00 00 00 00 00 00 00 00 00 02 16
                只有倒数2位变化
            */

            //FF 85 06 00 00 16
            if (hexData.length() > 40) {
                mIsDataStart = true;
            }

            if (hexData.startsWith("FF 85")){
                if (hexData.split(" ")[3].equals("00")){
                    //有数据
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showUploadOffLineData();

                        }
                    });
                }
            }

        }
    };


    boolean mIsDataStart;



}
