package com.amsu.healthy.activity.marathon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;

/**
 * author：WangLei
 * date:2017/10/24.
 * QQ:619321796
 * 耐力测试运动中
 */

public class EnduranceTestRuningActivity extends BaseActivity {
    public static Intent createIntent(Context context) {
        return new Intent(context, EnduranceTestRuningActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endurance_test_runing);
        initHeadView();
        setLeftImage(R.drawable.back_icon);
        setCenterText(getResources().getString(R.string.endurance_test));
        initEvents();

    }

    private void initEvents() {
        findViewById(R.id.fl_enduranceStart).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(EnduranceTestResultActivity.createIntent(EnduranceTestRuningActivity.this));
                return false;
            }
        });
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
