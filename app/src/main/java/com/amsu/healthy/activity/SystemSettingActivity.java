package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.utils.MyUtil;

public class SystemSettingActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("系统设置");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RelativeLayout rl_persiondata_persiondata = (RelativeLayout) findViewById(R.id.rl_persiondata_persiondata);
        RelativeLayout rl_persiondata_device = (RelativeLayout) findViewById(R.id.rl_persiondata_device);
        RelativeLayout rl_persiondata_running = (RelativeLayout) findViewById(R.id.rl_persiondata_running);
        RelativeLayout rl_persiondata_update = (RelativeLayout) findViewById(R.id.rl_persiondata_update);
        RelativeLayout rl_persiondata_aboutus = (RelativeLayout) findViewById(R.id.rl_persiondata_aboutus);
        RelativeLayout rl_persiondata_exit = (RelativeLayout) findViewById(R.id.rl_persiondata_exit);

        rl_persiondata_persiondata.setOnClickListener(this);
        rl_persiondata_device.setOnClickListener(this);
        rl_persiondata_running.setOnClickListener(this);
        rl_persiondata_update.setOnClickListener(this);
        rl_persiondata_aboutus.setOnClickListener(this);
        rl_persiondata_exit.setOnClickListener(this);

        if (!MyApplication.mActivities.contains(this)){
            MyApplication.mActivities.add(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_persiondata_persiondata:
                dumpToPersionData();
                break;
            case R.id.rl_persiondata_device:
                startActivity(new Intent(SystemSettingActivity.this,MyDeviceActivity.class));
                break;
            case R.id.rl_persiondata_running:
                startActivity(new Intent(SystemSettingActivity.this,DeviceRunActivity.class));
                break;
            case R.id.rl_persiondata_update:
                startActivity(new Intent(SystemSettingActivity.this,AppUpdateActivity.class));
                break;
            case R.id.rl_persiondata_aboutus:
                startActivity(new Intent(SystemSettingActivity.this,AboutUsActivity.class));
                break;
            case R.id.rl_persiondata_exit:
                MyUtil.putBooleanValueFromSP("isLogin",false);
                MyUtil.putBooleanValueFromSP("isPrefectInfo",false);
                MyUtil.saveUserToSP(new User());
                startActivity(new Intent(SystemSettingActivity.this,LoginActivity.class));
                for (Activity activity:MyApplication.mActivities){
                    activity.finish();
                }
                break;
        }
    }

    private void dumpToPersionData() {
        boolean isLogin = MyUtil.getBooleanValueFromSP("isLogin");
        if (isLogin){
            startActivity(new Intent(SystemSettingActivity.this,PersionDataActivity.class));
        }
        else {
            startActivity(new Intent(SystemSettingActivity.this,LoginActivity.class));
        }
    }
}
