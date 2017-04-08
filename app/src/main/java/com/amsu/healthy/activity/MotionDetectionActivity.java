package com.amsu.healthy.activity;

import android.os.Bundle;
import android.view.View;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/4/5.
 */

public class MotionDetectionActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_detction);

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
    }
}
