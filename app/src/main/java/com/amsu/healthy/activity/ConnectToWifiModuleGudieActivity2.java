package com.amsu.healthy.activity;

import android.os.Bundle;
import android.view.View;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/4/5.
 */

public class ConnectToWifiModuleGudieActivity2 extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wifi_gudie2);
        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("同步数据2/2");
        setHeadBackgroudColor("#72D5F4");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void connectNow(View view) {

    }
}
