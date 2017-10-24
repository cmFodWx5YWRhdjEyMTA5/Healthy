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
 * 耐力测试水平
 */

public class EnduranceTestResultActivity extends BaseActivity  {
    public static Intent createIntent(Context context) {
        return new Intent(context, EnduranceTestResultActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endurance_test_result);
        initHeadView();
        setLeftImage(R.drawable.back_icon);
        setCenterText(getResources().getString(R.string.endurance_level));
        initEvents();

    }

    private void initEvents() {
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
