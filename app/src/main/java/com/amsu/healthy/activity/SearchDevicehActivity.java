package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.Device;

public class SearchDevicehActivity extends BaseActivity {
    private static final String TAG = "HeartRateActivity";
    private Animation animation;
    private TextView tv_search_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searc_deviceh);

        initView();
        initDate();

    }



    private void initView() {
        ImageView iv_heartrate_rotateimage = (ImageView) findViewById(R.id.iv_heartrate_rotateimage);
        tv_search_state = (TextView) findViewById(R.id.tv_search_state);

        animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(-1);
        animation.setInterpolator(new LinearInterpolator());

        iv_heartrate_rotateimage.setAnimation(animation);

    }

    private void initDate() {
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_search_state.setText("查找成功");
                        //进行设备绑定
                        bindDevice();
                        animation.cancel();

                    }


                });
            }
        }.start();
    }

    private void bindDevice() {
        String mac = "44:A6:E5:1F:C5:E4";
        String name = "智能运动衣";
        String state = "已连接";
        Device device = new Device(name,state,mac);
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("device",device);
        intent.putExtra("bundle",bundle);
        setResult(RESULT_OK,intent);
        finish();

    }

    public void stopsearch(View view) {
        animation.cancel();

    }

}
