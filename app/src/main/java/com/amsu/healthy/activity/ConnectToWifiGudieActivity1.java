package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/4/5.
 */

public class ConnectToWifiGudieActivity1 extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wifi_gudie1);
        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.synced_data)+"1/2");
        setHeadBackgroudColor("#72D5F4");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void nextStep(View view) {
        startActivity(new Intent(this,ConnectToWifiGudieActivity2.class));
        finish();
    }
}
