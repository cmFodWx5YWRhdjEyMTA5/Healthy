package com.amsu.healthy.activity.insole;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.GlideRelativeView;

import java.util.Date;

public class InsoleLockScreenActivity extends BaseActivity {

    private static final String TAG = "LockScreenActivity";
    private TextView tv_run_insoledistance;
    private TextView tv_run_insolestepcount;
    private TextView tv_run_insoletime;
    private TextView tv_run_insoleavespeed;
    private GlideRelativeView rl_run_glide;
    private RelativeLayout rl_run_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insole_lock_screen);

        Log.i(TAG,"onCreate");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        initView();
    }



    private void initView() {

        /*bindService(new Intent(this, CommunicateToBleService.class),mConnection,BIND_AUTO_CREATE);
        isBind = true;*/
        initHeadView();

        tv_run_insoledistance = (TextView) findViewById(R.id.tv_run_insoledistance);
        tv_run_insolestepcount = (TextView) findViewById(R.id.tv_run_insolestepcount);
        tv_run_insoletime = (TextView) findViewById(R.id.tv_run_insoletime);
        tv_run_insoleavespeed = (TextView) findViewById(R.id.tv_run_insoleavespeed);

        rl_run_lock = (RelativeLayout) findViewById(R.id.rl_run_lock);
        rl_run_glide = (GlideRelativeView) findViewById(R.id.rl_run_glide);

        rl_run_glide.setOnONLockListener(new GlideRelativeView.OnONLockListener() {
            @Override
            public void onLock() {
                rl_run_lock.setVisibility(View.GONE);
                isNeedUpdateData = false;
                finish();
            }
        });


    }



    private boolean isNeedUpdateData = true;
    boolean isSetDated = false;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG,"onNewIntent");

        boolean isScroonOn = intent.getBooleanExtra("isScroonOn", false);
        Log.i(TAG,"isScroonOn:"+isScroonOn);
        if (!isSetDated && isScroonOn){
            isSetDated = true;
            setScreenData();
        }
    }


    private void setScreenData() {
        Log.i(TAG,"setScreenData:");

        final MyApplication application = (MyApplication) getApplication();

       /* if (!MyUtil.isEmpty(application.getRunningFinalFormatSpeed())){
            tv_run_speed.setText(application.getRunningFinalFormatSpeed());
        }

        if (!MyUtil.isEmpty(application.getRunningFormatDistance())){
            tv_run_distance.setText(application.getRunningFormatDistance());
        }
        if (application.getRunningmCurrentHeartRate()>0){
            tv_run_heartrate.setText(application.getRunningmCurrentHeartRate()+"");
        }

        Log.i(TAG,"isNeedUpdateData:"+isNeedUpdateData);
        Log.i(TAG,"application.getRunningCurrTimeDate():"+application.getRunningCurrTimeDate());*/

        if (application.getRunningCurrTimeDate() != null){
            new Thread(){
                long time ;
                int count = 10;

                @Override
                public void run() {
                    time = application.getRunningCurrTimeDate().getTime();

                    while (isNeedUpdateData){
                        time += 1000;
                        mSpecialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", new Date(time));
                        mHandler.sendEmptyMessage(1);

                        if (count==10){
                            if (application.getRunningmCurrentStepCount()>0){
                                mRunningFinalFormatStepCount = application.getRunningmCurrentStepCount()+"";
                            }

                            if (!MyUtil.isEmpty(application.getRunningFormatDistance())){
                                mRunningFormatDistance = application.getRunningFormatDistance();
                            }

                            if (!MyUtil.isEmpty(application.getRunningmCurrentAvespeed())){
                                mRunningmCurrentAvespeed = application.getRunningmCurrentAvespeed();
                            }
                            mHandler.sendEmptyMessage(2);
                            //startActivity(new Intent(LockScreenActivity.this,LockScreenActivity.class));
                            //Log.i(TAG,"10s设置数据  重启LockScreenActivity");
                        }

                        count--;

                        if (count==0){
                            count = 10;
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }

    private String mSpecialFormatTime;

    private String mRunningFinalFormatStepCount;
    private String mRunningFormatDistance;
    private String mRunningmCurrentAvespeed;

    Handler mHandler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv_run_insoletime.setText(mSpecialFormatTime);
                    break;

                case 2:
                    tv_run_insolestepcount.setText(mRunningFinalFormatStepCount);
                    tv_run_insoledistance.setText(mRunningFormatDistance);
                    tv_run_insoleavespeed.setText(mRunningmCurrentAvespeed);

                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

    @Override
    protected void onStop() {
        Log.i(TAG,"onStop");
        super.onStop();
        /*if (isBind){
            unbindService(mConnection);
            isBind = false;
        }*/
    }
}
