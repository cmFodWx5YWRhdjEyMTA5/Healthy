package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
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


        initView();
        initValue();
        initData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart");
        //地图定位
        ShowLocationOnMap.startLocation(mv_main_bmapView,this);
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
        MyApplication.locationService.unregisterListener(ShowLocationOnMap.mListener); //注销掉监听
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
                    startActivity(new Intent(MainActivity.this,HealthyDataActivity.class));
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

}
