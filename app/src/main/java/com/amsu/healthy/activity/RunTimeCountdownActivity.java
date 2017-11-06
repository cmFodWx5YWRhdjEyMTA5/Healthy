package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.insole.InsoleRunningActivity;
import com.amsu.healthy.activity.marathon.EnduranceTestRuningActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;

import static com.amsu.healthy.utils.Constant.isMarathonSportType;

public class RunTimeCountdownActivity extends Activity {

    private int count;
    private TextView tv_countdown_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_time_countdown);

        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        tv_countdown_count = (TextView) findViewById(R.id.tv_countdown_count);

        new Thread() {
            @Override
            public void run() {
                super.run();
                count = 3;
                while (count > 0) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_countdown_count.setText(String.valueOf(count));
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count--;
                    if (count == 0) {
                        int intExtra = MyApplication.deivceType;
                        int sport = getIntent().getIntExtra(Constant.sportType, -1);
                        if (sport != -1) {
                            intExtra = sport;
                        }
                        Intent intent = new Intent();
                        boolean is = MyUtil.getBooleanValueFromSP(isMarathonSportType);
                        boolean isEnduranceTest = getIntent().getBooleanExtra(Constant.isEnduranceTest,false);
                        if (isEnduranceTest) {
                            intent.setClass(RunTimeCountdownActivity.this, EnduranceTestRuningActivity.class);
                        } else if (intExtra == Constant.sportType_Cloth || is) {
                            intent.setClass(RunTimeCountdownActivity.this, StartRunActivity.class);
                        } else if (intExtra == Constant.sportType_Insole) {
                            intent.setClass(RunTimeCountdownActivity.this, InsoleRunningActivity.class);
                        }
                        boolean booleanExtra = getIntent().getBooleanExtra(Constant.mIsOutDoor, false);
                        intent.putExtra(Constant.mIsOutDoor, booleanExtra);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }.start();
    }


}
