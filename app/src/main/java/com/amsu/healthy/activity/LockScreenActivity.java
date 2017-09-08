package com.amsu.healthy.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.GlideRelativeView;

import java.util.Date;

public class LockScreenActivity extends BaseActivity {

    private static final String TAG = "LockScreenActivity";
    private TextView tv_run_speed;
    private TextView tv_run_distance;
    private TextView tv_run_time;
    private TextView tv_run_heartrate;
    private GlideRelativeView rl_run_glide;
    private RelativeLayout rl_run_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        Log.i(TAG,"onCreate");

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

        if (!MyUtil.isEmpty(application.getRunningFinalFormatSpeed())){
            tv_run_speed.setText(application.getRunningFinalFormatSpeed());
        }

        if (!MyUtil.isEmpty(application.getRunningFormatDistance())){
            tv_run_distance.setText(application.getRunningFormatDistance());
        }
        if (application.getRunningmCurrentHeartRate()>0){
            tv_run_heartrate.setText(application.getRunningmCurrentHeartRate()+"");
        }

        Log.i(TAG,"isNeedUpdateData:"+isNeedUpdateData);
        Log.i(TAG,"application.getRunningCurrTimeDate():"+application.getRunningCurrTimeDate());

        if (application.getRunningCurrTimeDate() != null){
            String specialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", application.getRunningCurrTimeDate());
            Log.i(TAG,"specialFormatTime:"+specialFormatTime);
            tv_run_time.setText(specialFormatTime);

            new Thread(){
                long time = application.getRunningCurrTimeDate().getTime();
                int count = 0;
                @Override
                public void run() {
                    super.run();
                    while (isNeedUpdateData){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                time += 1000;
                                String specialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", new Date(time));
                                tv_run_time.setText(specialFormatTime);

                                if (count==10){
                                    if (!MyUtil.isEmpty(application.getRunningFinalFormatSpeed())){
                                        tv_run_speed.setText(application.getRunningFinalFormatSpeed());
                                    }

                                    if (!MyUtil.isEmpty(application.getRunningFormatDistance())){
                                        tv_run_distance.setText(application.getRunningFormatDistance());
                                    }
                                    if (application.getRunningmCurrentHeartRate()>0){
                                        tv_run_heartrate.setText(application.getRunningmCurrentHeartRate()+"");
                                    }
                                    count = 0;
                                    //startActivity(new Intent(LockScreenActivity.this,LockScreenActivity.class));
                                    //Log.i(TAG,"10s设置数据  重启LockScreenActivity");
                                }

                                count++;
                            }
                        });
                    }
                }
            }.start();
        }
    }

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

