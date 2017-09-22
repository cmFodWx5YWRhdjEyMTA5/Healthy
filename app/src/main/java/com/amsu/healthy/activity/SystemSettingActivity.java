package com.amsu.healthy.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.utils.ApkUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.InputTextAlertDialogUtil;
import com.amsu.healthy.utils.MyUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Locale;

public class SystemSettingActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SystemSettingActivity";
    private boolean mIsAutoMonitor;
    private ImageView iv_persiondata_switvh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);
        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.settings));
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
        RelativeLayout rl_persiondata_Multilingual = (RelativeLayout) findViewById(R.id.rl_persiondata_Multilingual);

        iv_persiondata_switvh = (ImageView) findViewById(R.id.iv_persiondata_switvh);

        rl_persiondata_persiondata.setOnClickListener(this);
        rl_persiondata_device.setOnClickListener(this);
        rl_persiondata_update.setOnClickListener(this);
        rl_persiondata_aboutus.setOnClickListener(this);
        rl_persiondata_exit.setOnClickListener(this);
        rl_persiondata_questionnaire.setOnClickListener(this);
        rl_persiondata_Multilingual.setOnClickListener(this);

        if (!MyApplication.mActivities.contains(this)){
            MyApplication.mActivities.add(this);
        }

        mIsAutoMonitor = MyUtil.getBooleanValueFromSP("mIsAutoMonitor");
        if (mIsAutoMonitor){
            iv_persiondata_switvh.setImageResource(R.drawable.switch_on);
        }
        else {
            iv_persiondata_switvh.setImageResource(R.drawable.switch_of);
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
            case R.id.rl_persiondata_Multilingual:
                changeLanguage();
                break;
            case R.id.rl_persiondata_exit:
                exit();
                break;
        }
    }

    private void changeLanguage() {
        /*final String[] items = {"中文","english"};
        new AlertDialog.Builder(this)
                .setTitle("选择")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Resources resources = getResources();
                        DisplayMetrics dm = resources.getDisplayMetrics();
                        Configuration config = resources.getConfiguration();
                        switch (which){
                            case 0:
                                // 应用用户选择语言
                                config.locale = Locale.CHINESE;
                                resources.updateConfiguration(config, dm);
                                break;
                            case 1:
                                // 应用用户选择语言
                                config.locale = Locale.ENGLISH;
                                resources.updateConfiguration(config, dm);
                                break;
                        }
                        MyUtil.showToask(SystemSettingActivity.this, "已切换为 "+items[which]);
                    }
                })
                .show();*/

        final String[] items = {"中文","english"};
        new AlertDialog.Builder(this)
                .setTitle("选择")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Resources resources = getResources();
                        DisplayMetrics dm = resources.getDisplayMetrics();
                        Configuration config = resources.getConfiguration();

                        String saveLanguage = MyUtil.getStringValueFromSP("language");
                        String defaultLanguage = Locale.getDefault().getLanguage();
                        boolean isNeedChange = false;
                        switch (which){
                            case 0:
                                // 应用用户选择语言
                                if (defaultLanguage.equals(Locale.CHINESE.toString()) && (!MyUtil.isEmpty(saveLanguage) && !saveLanguage.equals(Locale.CHINESE.toString()))){
                                    isNeedChange =true;
                                }
                                else if (!defaultLanguage.equals(Locale.CHINESE.toString()) && (!MyUtil.isEmpty(saveLanguage) || !saveLanguage.equals(Locale.CHINESE.toString()))){
                                    isNeedChange =true;
                                }

                                if (isNeedChange){
                                    config.locale = Locale.CHINESE;
                                    MyUtil.putStringValueFromSP("language",Locale.CHINESE.toString());
                                }

                                break;
                            case 1:
                                // 应用用户选择语言
                                if (defaultLanguage.equals(Locale.ENGLISH.toString()) && (!MyUtil.isEmpty(saveLanguage) && !saveLanguage.equals(Locale.ENGLISH.toString()))){
                                    isNeedChange =true;
                                }
                                else if (!defaultLanguage.equals(Locale.ENGLISH.toString()) && (!MyUtil.isEmpty(saveLanguage) || !saveLanguage.equals(Locale.ENGLISH.toString()))){
                                    isNeedChange =true;
                                }

                                if (isNeedChange){
                                    config.locale = Locale.ENGLISH;
                                    MyUtil.putStringValueFromSP("language",Locale.ENGLISH.toString());
                                }
                                break;
                        }
                        if (isNeedChange){
                            resources.updateConfiguration(config, dm);
                            MyUtil.showToask(SystemSettingActivity.this, "已切换为 "+items[which]);
                            MyUtil.destoryAllAvtivity();
                            Intent intent = new Intent(SystemSettingActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .show();
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


    public void switchMonitorState(View view) {
        if (!mIsAutoMonitor){
            InputTextAlertDialogUtil textAlertDialogUtil = new InputTextAlertDialogUtil(this);
            textAlertDialogUtil.setAlertDialogText("输入密码",getResources().getString(R.string.exit_confirm),getResources().getString(R.string.exit_cancel));

            textAlertDialogUtil.setOnConfirmClickListener(new InputTextAlertDialogUtil.OnConfirmClickListener() {
                @Override
                public void onConfirmClick(String inputText) {
                    Log.i(TAG,"inputText:"+inputText);
                    if (inputText.equals("000")){
                        iv_persiondata_switvh.setImageResource(R.drawable.switch_on);
                        mIsAutoMonitor = true;
                        MyUtil.putBooleanValueFromSP("mIsAutoMonitor",true);
                        MyUtil.showToask(SystemSettingActivity.this,"开启成功");
                    }
                    else {
                        MyUtil.showToask(SystemSettingActivity.this,"输入错误，无法开启");
                    }
                }
            });
        }
        else {
            iv_persiondata_switvh.setImageResource(R.drawable.switch_of);
            mIsAutoMonitor = false;
            MyUtil.putBooleanValueFromSP("mIsAutoMonitor",false);
        }
    }
}
