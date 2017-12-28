package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.utils.ApkUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.OffLineDbAdapter;
import com.amsu.healthy.utils.SportRecoveryUtil;
import com.amsu.healthy.utils.UploadHealthyDataUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.i(TAG,"onCreate");
        initView();
    }

    private void initView() {
        //判断是否要恢复到之前的运动状态
        int recoverRunState = SportRecoveryUtil.judgeRecoverRunState(this);
        if (recoverRunState!=-1){
            finish();
            return;
        }

        TextView tv_splish_mark = (TextView) findViewById(R.id.tv_splish_mark);
        tv_splish_mark.setText(getResources().getString(R.string.app_name)+" "+ApkUtil.getVersionName(this));

        Log.i(TAG,"开启线程");
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /*boolean isInstall = MyApplication.sharedPreferences.getBoolean("isInstall", false);
                if (isInstall){
                    startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                }
                else {
                    startActivity(new Intent(SplashActivity.this,InstallGuideActivity.class));
                    MyApplication.sharedPreferences.edit().putBoolean("isInstall",true).apply();
                }*/
                Log.i(TAG,"跳转MainActivity");
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        }.start();

        initData();
    }

    private void initData() {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        int mCurrYear = calendar.get(Calendar.YEAR);
        int mCurrWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        UploadHealthyDataUtil.downlaodWeekReport(mCurrYear,mCurrWeekOfYear,false,null);

        if (Constant.isInnerUpdateAllowed){
            ApkUtil.checkUpdate(this);
        }

        boolean networkConnected = MyUtil.isNetworkConnected(MyApplication.appContext);
        if (networkConnected){
            //有网络连接
            new Thread(){
                @Override
                public void run() {
                    startUploadOffLineData(MyApplication.appContext);
                }
            }.start();
        }
    }

    private void startUploadOffLineData(Context context) {
        Log.i(TAG,"startUploadOffLineData:");
        OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(context);
        try {
            offLineDbAdapter.open();
            List<UploadRecord> uploadRecordsState = offLineDbAdapter.queryRecordByUploadState("0");
            try {
                offLineDbAdapter.close();
                offLineDbAdapter = null;
            }catch (Exception e1){
                Log.e(TAG,"e1:"+e1);
            }
            Log.i(TAG,"uploadRecordsState:"+uploadRecordsState);
            Log.i(TAG,"uploadRecordsState.size():"+uploadRecordsState.size());

            for (UploadRecord uploadRecord:uploadRecordsState){
                UploadHealthyDataUtil.uploadRecordDataToServer(uploadRecord,context,true);
            }
        }catch (Exception e){
            Log.i(TAG,"e:"+e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

}


