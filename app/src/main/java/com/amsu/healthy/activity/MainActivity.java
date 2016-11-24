package com.amsu.healthy.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.ShowLocationOnMap;
import com.baidu.mapapi.map.MapView;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private MapView mv_main_bmapView;

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


}
