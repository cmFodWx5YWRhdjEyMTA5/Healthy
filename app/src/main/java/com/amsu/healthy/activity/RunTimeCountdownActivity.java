package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.insole.InsoleRunningActivity;
import com.amsu.healthy.utils.Constant;

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

        new Thread(){
            @Override
            public void run() {
                super.run();
                count = 3;
                while (count >1){
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_countdown_count.setText(count +"");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count--;
                    if (count==1){
                        Intent intent = getIntent();
                        int intExtra = intent.getIntExtra(Constant.sportState, -1);
                        if (intExtra==Constant.sportType_Cloth){
                            startActivity(new Intent(RunTimeCountdownActivity.this,StartRunActivity.class));
                        }
                        else if (intExtra==Constant.sportType_Insole){
                            startActivity(new Intent(RunTimeCountdownActivity.this,InsoleRunningActivity.class));
                        }
                        finish();
                    }
                }
            }
        }.start();
    }


}
