package com.amsu.healthy.activity;

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
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.UStringUtil;
import com.amsu.healthy.view.GlideRelativeView;

import java.util.Date;

import static com.amsu.healthy.utils.Constant.enduranceTest;

public class LockScreenActivity extends BaseActivity {

    private static final String TAG = "LockScreenActivity";
    private TextView tv_run_speed;
    private TextView tv_run_distance;
    private TextView tv_run_time;
    private TextView tv_run_heartrate;
    private GlideRelativeView rl_run_glide;
    private RelativeLayout rl_run_lock;
    private String runningDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        Log.i(TAG, "onCreate");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        initView();
    }


    private void initView() {

        /*bindService(new Intent(this, CommunicateToBleService.class),mConnection,BIND_AUTO_CREATE);
        isBind = true;*/
        initHeadView();
        tv_run_speed = (TextView) findViewById(R.id.tv_run_speed);
        tv_run_distance = (TextView) findViewById(R.id.tv_run_distance);
        tv_run_time = (TextView) findViewById(R.id.tv_run_time);
        tv_run_heartrate = (TextView) findViewById(R.id.tv_run_heartrate);

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
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent");

        boolean isScroonOn = intent.getBooleanExtra("isScroonOn", false);
        Log.i(TAG, "isScroonOn:" + isScroonOn);
        if (!isSetDated && isScroonOn) {
            isSetDated = true;
            setScreenData();
        }
    }


    private void setScreenData() {
        Log.i(TAG, "setScreenData:");

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

        if (application.getRunningCurrTimeDate() != null) {
            new Thread() {
                long time;
                int count = 0;

                @Override
                public void run() {
                    time = application.getRunningCurrTimeDate().getTime();
                    while (isNeedUpdateData) {
                        time += 1000;
                        runningDate = application.getRunningDate();
                        mSpecialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", new Date(time));
                        mHandler.sendEmptyMessage(1);

                        if (count == 10) {
                            mHandler.sendEmptyMessage(2);
                            if (!MyUtil.isEmpty(application.getRunningFinalFormatSpeed())) {
                                mRunningFinalFormatSpeed = application.getRunningFinalFormatSpeed();
                            }

                            if (!MyUtil.isEmpty(application.getRunningFormatDistance())) {
                                mRunningFormatDistance = application.getRunningFormatDistance();
                            }

                            if (application.getRunningmCurrentHeartRate() > 0) {
                                mRunningmCurrentHeartRate = application.getRunningmCurrentHeartRate() + "";
                            }
                            count = 0;
                            //startActivity(new Intent(LockScreenActivity.this,LockScreenActivity.class));
                            //Log.i(TAG,"10s设置数据  重启LockScreenActivity");
                        }

                        count++;

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
    private String mRunningFinalFormatSpeed;
    private String mRunningFormatDistance;
    private String mRunningmCurrentHeartRate;

    Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (enduranceTest) {
                        tv_run_time.setText(UStringUtil.isNullOrEmpty(runningDate) ? "——" : runningDate);
                    } else {
                        tv_run_time.setText(mSpecialFormatTime);
                    }
                    break;

                case 2:
                    tv_run_speed.setText(mRunningFinalFormatSpeed);
                    tv_run_distance.setText(mRunningFormatDistance);
                    tv_run_heartrate.setText(mRunningmCurrentHeartRate);

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
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
        /*if (isBind){
            unbindService(mConnection);
            isBind = false;
        }*/
    }
}

