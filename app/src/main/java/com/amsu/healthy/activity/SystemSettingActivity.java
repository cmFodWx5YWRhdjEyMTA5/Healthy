package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.utils.ApkUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.umeng.analytics.MobclickAgent;

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
        RelativeLayout rl_persiondata_questionnaire = (RelativeLayout) findViewById(R.id.rl_persiondata_questionnaire);

        rl_persiondata_persiondata.setOnClickListener(this);
        rl_persiondata_device.setOnClickListener(this);
        rl_persiondata_update.setOnClickListener(this);
        rl_persiondata_aboutus.setOnClickListener(this);
        rl_persiondata_exit.setOnClickListener(this);
        rl_persiondata_questionnaire.setOnClickListener(this);

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
            case R.id.rl_persiondata_questionnaire:
                startActivity(new Intent(SystemSettingActivity.this,QuestionnaireActivity.class));
                break;
            case R.id.rl_persiondata_exit:
                exit();
                break;
        }
    }

    private void exit() {
        Log.i(TAG,"isLogin:"+ MyUtil.getBooleanValueFromSP("isLogin"));
        Log.i(TAG,"isPrefectInfo:"+MyUtil.getBooleanValueFromSP("isPrefectInfo"));

       /* MyUtil.putBooleanValueFromSP("isLogin",false);
        MyUtil.putBooleanValueFromSP("isPrefectInfo",false);

        MyUtil.putStringValueFromSP(Constant.sosinfo,"");
        MyUtil.putStringValueFromSP(Constant.sosNumberList,"");
        MyUtil.putIntValueFromSP("healthyIindexvalue",-1);
        MyUtil.putIntValueFromSP("physicalAge",-1);

        Log.i(TAG,"isLogin:"+MyUtil.getBooleanValueFromSP("isLogin"));
        Log.i(TAG,"isPrefectInfo:"+MyUtil.getBooleanValueFromSP("isPrefectInfo"));
        MyUtil.saveUserToSP(new User());
        */

        MyUtil.clearAllSPData();
        startActivity(new Intent(SystemSettingActivity.this,LoginActivity.class));
        MobclickAgent.onProfileSignOff();  ////友盟退出登陆账号统计

        for (Activity activity: MyApplication.mActivities){
            activity.finish();
        }
        int healthyIindexvalue = MyUtil.getIntValueFromSP("healthyIindexvalue");
        int physicalAge = MyUtil.getIntValueFromSP("physicalAge");

        Log.i(TAG,"healthyIindexvalue:"+healthyIindexvalue+"  physicalAge:"+physicalAge);
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
