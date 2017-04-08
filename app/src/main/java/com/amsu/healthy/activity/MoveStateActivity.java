package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.amsu.healthy.R;

public class MoveStateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_state);

    }

    public void close(View view) {
        finish();
    }


    //静态情况
    public void staticDump(View view) {
        startActivity(new Intent(this,HeartRateActivity.class));
        finish();
    }

    //运动情况，要计算恢复心率
    public void sportDump(View view) {
        Intent intent = new Intent(this, CalculateHRRProcessActivity.class);
        startActivity(intent);
        finish();
    }
}
