package com.amsu.healthy.activity;

import android.app.Activity;
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
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyTimeTask;
import com.amsu.healthy.utils.MyUtil;

import java.util.ArrayList;

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

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(StartRunActivity.action);
        registerReceiver(broadcastReceiver, filter);
    }

    private void initView() {
        tv_process_rate = (TextView) findViewById(R.id.tv_process_rate);
        ImageView iv_heartrate_rotateimage = (ImageView) findViewById(R.id.iv_heartrate_rotateimage);
        animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(-1);
        animation.setInterpolator(new LinearInterpolator());

        iv_heartrate_rotateimage.setAnimation(animation);


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


            HealthyDataActivity.stopTransmitData();

            Intent intent = new Intent(this, HeartRateActivity.class);
            if (isTimeOut){    //到一分钟，传递恢复心率数据
                int hrr = firstRate - currentRate;
                if (hrr<=0){
                    hrr = 0;
                }
                intent.putExtra("hrr",hrr);
            }
            Intent runIntent = getIntent();
            long createrecord = runIntent.getLongExtra(Constant.sportCreateRecordID,-1);
            if (createrecord!=-1){
                intent.putExtra(Constant.sportCreateRecordID,createrecord);
            }
            ArrayList<Integer> heartRateDates = runIntent.getIntegerArrayListExtra(Constant.heartDataList_static);
            if (heartRateDates!=null && heartRateDates.size()>0){
                intent.putIntegerArrayListExtra(Constant.heartDataList_static,heartRateDates);
            }
            long ecgFiletimeMillis = runIntent.getLongExtra(Constant.ecgFiletimeMillis,-1);
            String ecgLocalFileName = runIntent.getStringExtra(Constant.ecgLocalFileName);
            if (ecgFiletimeMillis!=-1){
                intent.putExtra(Constant.ecgFiletimeMillis,ecgFiletimeMillis);
            }
            if (!MyUtil.isEmpty(ecgLocalFileName)){
                intent.putExtra(Constant.ecgLocalFileName,ecgLocalFileName);
            }

            intent.putExtra(Constant.sportState,1);

            ArrayList<String> mKcalData = runIntent.getStringArrayListExtra(Constant.mKcalData);
            if (mKcalData!=null && mKcalData.size()>0){
                intent.putStringArrayListExtra(Constant.mKcalData,mKcalData);
            }
            ArrayList<Integer> mStridefreData = runIntent.getIntegerArrayListExtra(Constant.mStridefreData);
            if (mStridefreData!=null && mStridefreData.size()>0){
                intent.putIntegerArrayListExtra(Constant.mStridefreData,mStridefreData);
            }
            startActivity(intent);
            for (Activity activity:MyApplication.mActivities){
                if (activity.getClass()!=MainActivity.class){
                    activity.finish();
                }
            }
            MyApplication.mActivities.clear();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public void stopsearch(View view) {
        stopProcess();
    }

    boolean isFirst = true;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

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
