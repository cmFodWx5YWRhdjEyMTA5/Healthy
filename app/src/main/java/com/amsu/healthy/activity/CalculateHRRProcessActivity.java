package com.amsu.healthy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyTimeTask;

import static com.amsu.healthy.R.id.textView;

public class CalculateHRRProcessActivity extends BaseActivity {

    private static final String TAG = "CalculateHRRProcess";
    private TextView tv_process_rate;
    private RotateAnimation animation;
    private int firstRate = 0;
    private int currentRate = 0;
    private boolean isTimeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_hrrprocess);
        initView();
    }

    private void initView() {
        tv_process_rate = (TextView) findViewById(R.id.tv_process_rate);
        ImageView iv_heartrate_rotateimage = (ImageView) findViewById(R.id.iv_heartrate_rotateimage);
        animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(-1);
        animation.setInterpolator(new LinearInterpolator());

        iv_heartrate_rotateimage.setAnimation(animation);

        IntentFilter filter = new IntentFilter(HealthyDataActivity.action);
        registerReceiver(broadcastReceiver, filter);

        MyTimeTask.startCountDownTimerTask(1000 * 60, new MyTimeTask.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                Log.i(TAG,"onTomeOut");
                isTimeOut = true;
                stopProcess();

            }
        });
    }

    private void stopProcess() {
        if (animation!=null){
            animation.cancel();
            animation = null;

            unregisterReceiver(broadcastReceiver);
            HealthyDataActivity.stopTransmitData();

            Intent intent = new Intent(this, HeartRateActivity.class);
            if (isTimeOut){    //到一分钟，传递恢复心率数据
                int hrr = currentRate - firstRate;
                intent.putExtra("hrr",hrr);
            }
            intent.putExtra(Constant.sportState,1);
            startActivity(intent);
            finish();
        }
    }

    public void stopsearch(View view) {
        stopProcess();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        boolean isFirst = true;
        @Override
        public void onReceive(Context context, Intent intent) {
            int data = intent.getExtras().getInt("data");
            Log.i(TAG,"data:"+ data);
            tv_process_rate.setText(data+"");
            currentRate = data;
            if (isFirst){
                firstRate = currentRate;
                isFirst = false;
            }
        }
    };


}
