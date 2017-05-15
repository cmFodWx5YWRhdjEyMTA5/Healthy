package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Apk;
import com.amsu.healthy.bean.JsonApk;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.utils.ApkUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class SystemSettingActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SystemSettingActivity";

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
        RelativeLayout rl_persiondata_update = (RelativeLayout) findViewById(R.id.rl_persiondata_update);
        RelativeLayout rl_persiondata_aboutus = (RelativeLayout) findViewById(R.id.rl_persiondata_aboutus);
        RelativeLayout rl_persiondata_exit = (RelativeLayout) findViewById(R.id.rl_persiondata_exit);

        rl_persiondata_persiondata.setOnClickListener(this);
        rl_persiondata_device.setOnClickListener(this);
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
            case R.id.rl_persiondata_update:
                //startActivity(new Intent(SystemSettingActivity.this,AppUpdateActivity.class));
                ApkUtil.checkUpdate(SystemSettingActivity.this);
                break;
            case R.id.rl_persiondata_aboutus:
                startActivity(new Intent(SystemSettingActivity.this,AboutUsActivity.class));
                break;
            case R.id.rl_persiondata_exit:
                exit();
                break;
        }
    }

    private void exit() {
        Log.i(TAG,"isLogin:"+ MyUtil.getBooleanValueFromSP("isLogin"));
        Log.i(TAG,"isPrefectInfo:"+MyUtil.getBooleanValueFromSP("isPrefectInfo"));
        MyUtil.putBooleanValueFromSP("isLogin",false);
        MyUtil.putBooleanValueFromSP("isPrefectInfo",false);

        MyUtil.putStringValueFromSP(Constant.sosinfo,"");
        MyUtil.putStringValueFromSP(Constant.sosNumberList,"");

        Log.i(TAG,"isLogin:"+MyUtil.getBooleanValueFromSP("isLogin"));
        Log.i(TAG,"isPrefectInfo:"+MyUtil.getBooleanValueFromSP("isPrefectInfo"));
        MyUtil.saveUserToSP(new User());
        startActivity(new Intent(SystemSettingActivity.this,LoginActivity.class));
        for (Activity activity: MyApplication.mActivities){
            activity.finish();
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
