package com.amsu.healthy.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.ShowLocationOnMap;
import com.baidu.mapapi.map.MapView;
import com.ble.ble.BleService;

import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private ImageView iv_main_elf;
    private LinearLayout ll_main_floatcontent;
    public static BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG,"onCreate");
        //mv_main_bmapView.onCreate(this,savedInstanceState);

        initView();
        initValue();
        initData();



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    private void initView() {
        initHeadView();
        iv_main_elf = (ImageView) findViewById(R.id.iv_main_elf);
        ll_main_floatcontent = (LinearLayout) findViewById(R.id.ll_main_floatcontent);
        ImageView iv_main_healthdata = (ImageView) findViewById(R.id.iv_main_healthdata);
        ImageView iv_main_action = (ImageView) findViewById(R.id.iv_main_action);
        ImageView iv_main_community = (ImageView) findViewById(R.id.iv_main_community);
        ImageView iv_main_me = (ImageView) findViewById(R.id.iv_main_me);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        iv_main_elf.setOnClickListener(myOnClickListener);
        ll_main_floatcontent.setOnClickListener(myOnClickListener);
        iv_main_healthdata.setOnClickListener(myOnClickListener);
        iv_main_action.setOnClickListener(myOnClickListener);
        iv_main_community.setOnClickListener(myOnClickListener);
        iv_main_me.setOnClickListener(myOnClickListener);

    }

    private void initValue() {

    }

    private void initData() {
        setCenterText("倾听体语");
        setRightText("我的设备");

        checkAndOpenBLEFeature();

    }



    //检查是否支持蓝牙
    private void checkAndOpenBLEFeature() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "error_bluetooth_not_supported", Toast.LENGTH_SHORT).show();
            //finish();
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
        ShowLocationOnMap.stopLocation();  //停止定位服务
    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_main_elf:
                    iv_main_elf.setVisibility(View.INVISIBLE);
                    ll_main_floatcontent.setVisibility(View.VISIBLE);
                    break;
                case R.id.ll_main_floatcontent:

                    break;

                case R.id.iv_main_healthdata:
                    boolean isLogin = MyUtil.getBooleanValueFromSP("isLogin");
                    if (isLogin){
                        boolean isPrefectInfo = MyUtil.getBooleanValueFromSP("isPrefectInfo");
                        if (isPrefectInfo){
                            List<Device> deviceListFromSP = MyUtil.getDeviceListFromSP();
                            if (deviceListFromSP.size()==0){
                                //没有绑定设备，提示用户去绑定
                                startActivity(new Intent(MainActivity.this,MyDeviceActivity.class));
                            }
                            else {
                                startActivity(new Intent(MainActivity.this,HealthyDataActivity.class));
                            }

                        }
                        else {
                            showdialogToSupplyData();
                        }
                    }
                    else {
                        showdialogToLogin();
                    }


                    break;
                case R.id.iv_main_action:

                    break;
                case R.id.iv_main_community:

                    break;
                case R.id.iv_main_me:
                    startActivity(new Intent(MainActivity.this,MeActivity.class));
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
        android.os.Process.killProcess(android.os.Process.myPid());  //退出应用程序
    }
}
