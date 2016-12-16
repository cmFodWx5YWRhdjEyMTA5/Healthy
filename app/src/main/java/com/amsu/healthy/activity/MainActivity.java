package com.amsu.healthy.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.ShowLocationOnMap;
import com.baidu.mapapi.map.MapView;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private MapView mv_main_bmapView;
    private ImageView iv_main_elf;
    private LinearLayout ll_main_floatcontent;

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
        //地图定位
        ShowLocationOnMap.startLocation(mv_main_bmapView.getMap(),this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
        mv_main_bmapView.onResume();


    }

    private void initView() {
        initHeadView();
        mv_main_bmapView = (MapView) findViewById(R.id.mv_main_bmapView);
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
                            startActivity(new Intent(MainActivity.this,HealthyDataActivity.class));
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
        finish();
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
        mv_main_bmapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        mv_main_bmapView.onDestroy();
       // ShowLocationOnMap.mMapView = null;
        mv_main_bmapView = null;
        android.os.Process.killProcess(android.os.Process.myPid());  //退出应用程序
    }
}
