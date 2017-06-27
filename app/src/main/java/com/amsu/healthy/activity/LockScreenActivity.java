package com.amsu.healthy.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.GlideRelativeView;

import java.util.Date;

public class LockScreenActivity extends BaseActivity {

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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        initView();
    }

    private void initView() {
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
                finish();
            }
        });

        if (!MyUtil.isEmpty(StartRunActivity.mFinalFormatSpeed)){
            tv_run_speed.setText(StartRunActivity.mFinalFormatSpeed);
        }

        if (StartRunActivity.mAllDistance>0){
            tv_run_distance.setText(StartRunActivity.getFormatDistance(StartRunActivity.mAllDistance));
        }
        if (StartRunActivity.mCurrentHeartRate>0){
            tv_run_heartrate.setText(StartRunActivity.mCurrentHeartRate+"");
        }

        if (StartRunActivity.mCurrTimeDate!=null){
            String specialFormatTime = MyUtil.getSpecialFormatTime("HH:mm:ss", StartRunActivity.mCurrTimeDate);
            tv_run_time.setText(specialFormatTime);

            new Thread(){
                long time = StartRunActivity.mCurrTimeDate.getTime();

                @Override
                public void run() {
                    super.run();
                    while (true){
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
}

