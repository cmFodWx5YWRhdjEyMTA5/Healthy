package com.amsu.healthy.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.amsu.healthy.R.id.bt_run_start;

public class StartRunActivity extends BaseActivity {

    private static final String TAG = "StartRunActivity";
    private TextView tv_run_speed;
    private TextView tv_run_distance;
    private TextView tv_run_time;
    private TextView tv_run_isoxygen;
    private TextView tv_run_rate;
    private TextView tv_run_stridefre;
    private TextView tv_run_kcal;
    private final int WHAT_TIME_UPDATE = 0;
    private TimerTask mTimerTask;
    private Button bt_run_start;
    private RelativeLayout bt_run_location;
    private Button bt_run_sos;
    private boolean mIsRunning = false;
    private boolean mIsFirstStart = true;
    private Date mNowDate;
    private Timer mTimer;
    private long mDScendDely =1000; //计时器间隔，1s
    private RunTimerTask mRunTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_run);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("运动检测");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_run_speed = (TextView) findViewById(R.id.tv_run_speed);
        tv_run_distance = (TextView) findViewById(R.id.tv_run_distance);
        tv_run_time = (TextView) findViewById(R.id.tv_run_time);
        tv_run_isoxygen = (TextView) findViewById(R.id.tv_run_isoxygen);
        tv_run_rate = (TextView) findViewById(R.id.tv_run_rate);
        tv_run_stridefre = (TextView) findViewById(R.id.tv_run_stridefre);
        tv_run_kcal = (TextView) findViewById(R.id.tv_run_kcal);

        bt_run_start = (Button) findViewById(R.id.bt_run_start);
        bt_run_location = (RelativeLayout) findViewById(R.id.bt_run_location);
        bt_run_sos = (Button) findViewById(R.id.bt_run_sos);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        bt_run_start.setOnClickListener(myOnClickListener);
        bt_run_location.setOnClickListener(myOnClickListener);
        bt_run_sos.setOnClickListener(myOnClickListener);

    }

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_TIME_UPDATE:
                    tv_run_time.setText((String)msg.obj);
                    break;
            }

        }
    };

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_run_start:
                    startRun();

                    break;

                case R.id.bt_run_sos:

                    break;

                case R.id.bt_run_location:

                    break;
            }
        }
    }

    private void startRun() {
        if (mIsRunning){
            bt_run_start.setText("开始");
            mTimer.cancel();
            mIsRunning = false;
        }
        else {
            bt_run_start.setText("暂停");
            mIsRunning  =true;

            mTimer = new Timer();
            mRunTimerTask = new RunTimerTask();
            mTimer.schedule(mRunTimerTask,mDScendDely,mDScendDely);
        }



    }

    //计时器，1s更新一次
    class RunTimerTask extends TimerTask{

        @Override
        public void run() {
            if (mIsFirstStart){
                mIsFirstStart = false;
                mNowDate = new Date();
                mNowDate.setHours(0);
                mNowDate.setMinutes(0);
                mNowDate.setSeconds(0);
            }

            Date curr= new Date(mNowDate.getTime()+mDScendDely);
            mNowDate = curr;
            String time = MyUtil.getSpecialFormatTime("HH:mm:ss", curr);
            Message message = myHandler.obtainMessage();
            message.what = WHAT_TIME_UPDATE;
            message.obj =time;
            myHandler.sendMessage(message);
            Log.i(TAG,"time:"+time);
        }
    }
}
