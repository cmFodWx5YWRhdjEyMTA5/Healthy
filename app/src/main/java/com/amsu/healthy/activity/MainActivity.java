package com.amsu.healthy.activity;

import android.os.Bundle;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.ShowLocationOnMap;
import com.baidu.mapapi.map.MapView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
        initValue();
        initData();

    }


    private void initView() {
        initHeadView();
        MapView mv_main_bmapView = (MapView) findViewById(R.id.mv_main_bmapView);
        ShowLocationOnMap.startLocation(this,mv_main_bmapView);

    }

    private void initValue() {

    }

    private void initData() {
        setCenterText("倾听体语");
        setRightText("我的设备");

    }

}
