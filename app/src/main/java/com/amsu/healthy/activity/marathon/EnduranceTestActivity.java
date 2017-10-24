package com.amsu.healthy.activity.marathon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.RunTimeCountdownActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endurance_test);
        initHeadView();
        setLeftImage(R.drawable.back_icon);
        setCenterText(getResources().getString(R.string.endurance_test));
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_enduranceStart:
                startActivity(new Intent(this, RunTimeCountdownActivity.class));
                break;
        }
    }
}
