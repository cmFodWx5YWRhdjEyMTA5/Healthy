package com.amsu.healthy.activity;

import android.animation.ValueAnimator;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.RateRecord;
import com.amsu.healthy.db.DbAdapter;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.ShowLocationOnMap;
import com.amsu.healthy.view.CircleRingView;
import com.amsu.healthy.view.DashboardView;
import com.baidu.mapapi.map.MapView;
import com.ble.ble.BleService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private ImageView iv_main_elf;
    private LinearLayout ll_main_floatcontent;
    public static BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 2;
    private DashboardView dv_main_compass;
    private CircleRingView cv_mian_index;
    private CircleRingView cv_mian_warring;
    private ValueAnimator mValueAnimator;
    private TextView tv_main_age;
    private TextView tv_main_indexvalue;
    private int physicalAge;
    private int scoreALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG,"onCreate");
        //mv_main_bmapView.onCreate(this,savedInstanceState);

        initView();
        initData();


        int[] calcuData = {3,5,7,8,9};
        List<List<Integer>> sList = new ArrayList<>();
        for (int i=0;i<calcuData.length;i++){
            List<Integer> temp = new ArrayList<Integer>();
            temp.add(i+1);
            temp.add(calcuData[i]);
            sList.add(temp);
        }

        Gson gson = new Gson();
        String  listString = gson.toJson(sList);
        Log.i(TAG,"listString:"+listString);




    }


    private void initView() {
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


        //生理年龄
        physicalAge = HealthyIndexUtil.calculatePhysicalAge();
        Log.i(TAG,"physicalAge:"+ physicalAge);
        if (physicalAge>0){
            setAgeTextAnimator(tv_main_age,0, physicalAge);
        }

        //健康指标
        scoreALL = HealthyIndexUtil.calculateIndexvalue();
        if (scoreALL >0){
            tv_main_indexvalue.setText(scoreALL +"");
        }



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

        if (mBluetoothAdapter!=null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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
            Log.i(TAG,"onClick:"+v.getId());
            boolean isLogin = MyUtil.getBooleanValueFromSP("isLogin");
            boolean isPrefectInfo = MyUtil.getBooleanValueFromSP("isPrefectInfo");
            if (!isLogin){
                showdialogToLogin();
                return;
            }
            /*else if (!isPrefectInfo){   测试
                showdialogToSupplyData();
                return;
            }*/

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

                    break;
                case R.id.rl_main_sportarea:
                    startActivity(new Intent(MainActivity.this,SportCommunityActivity.class));
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
