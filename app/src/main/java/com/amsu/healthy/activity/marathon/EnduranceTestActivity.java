package com.amsu.healthy.activity.marathon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.HealthyDataActivity;
import com.amsu.healthy.activity.MyDeviceActivity;
import com.amsu.healthy.activity.RunTimeCountdownActivity;
import com.amsu.healthy.appication.MyApplication;

/**
 * author：WangLei
 * date:2017/10/24.
 * QQ:619321796
 * 耐力测试
 */

public class EnduranceTestActivity extends BaseActivity implements View.OnClickListener {
    public static Intent createIntent(Context context) {
        return new Intent(context, EnduranceTestActivity.class);
    }

    private static final String TAG = "MarathonActivity";
    private ImageView iv_base_connectedstate;
    private boolean isConnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endurance_test);
        initHeadView();
        setLeftImage(R.drawable.back_icon);
        setCenterText(getResources().getString(R.string.endurance_test));
        setRightImage(R.drawable.yifu);
        iv_base_connectedstate = (ImageView) findViewById(R.id.iv_base_connectedstate);
        iv_base_connectedstate.setVisibility(View.VISIBLE);
        setHeadBackgroundResource(R.drawable.bg_gradual_blue);
        initEvents();

    }

    private void initEvents() {
        findViewById(R.id.fl_enduranceStart).setOnClickListener(this);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnect) {
                    startActivity(new Intent(EnduranceTestActivity.this, MyDeviceActivity.class));
                } else {
                    startActivity(new Intent(EnduranceTestActivity.this, HealthyDataActivity.class));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDeviceConnectedState(MyApplication.isHaveDeviceConnectted);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_enduranceStart:
                startActivity(new Intent(this, RunTimeCountdownActivity.class));
                break;
        }
    }

    public void setDeviceConnectedState(boolean deviceConnectedState) {
        if (deviceConnectedState) {
            isConnect = true;
            iv_base_connectedstate.setImageResource(R.drawable.yilianjie);
        } else {
            iv_base_connectedstate.setImageResource(R.drawable.duankai);
            isConnect = false;
        }
    }

}
