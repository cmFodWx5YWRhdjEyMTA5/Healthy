package com.amsu.healthy.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;

public class SystemSettingActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);

        initView();
    }

    private void initView() {
        RelativeLayout rl_persiondata_persiondata = (RelativeLayout) findViewById(R.id.rl_persiondata_persiondata);
        RelativeLayout rl_persiondata_device = (RelativeLayout) findViewById(R.id.rl_persiondata_device);
        RelativeLayout rl_persiondata_running = (RelativeLayout) findViewById(R.id.rl_persiondata_running);
        RelativeLayout rl_persiondata_update = (RelativeLayout) findViewById(R.id.rl_persiondata_update);
        RelativeLayout rl_persiondata_aboutus = (RelativeLayout) findViewById(R.id.rl_persiondata_aboutus);

        rl_persiondata_persiondata.setOnClickListener(this);
        rl_persiondata_device.setOnClickListener(this);
        rl_persiondata_running.setOnClickListener(this);
        rl_persiondata_update.setOnClickListener(this);
        rl_persiondata_aboutus.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_persiondata_persiondata:

                break;
            case R.id.rl_persiondata_device:

                break;
            case R.id.rl_persiondata_running:

                break;
            case R.id.rl_persiondata_update:

                break;
            case R.id.rl_persiondata_aboutus:

                break;

        }
    }
}
