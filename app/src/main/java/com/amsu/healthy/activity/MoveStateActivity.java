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

    public void sportDump(View view) {
        startActivity(new Intent(this,HeartRateActivity.class));
    }

    public void staticDump(View view) {
        startActivity(new Intent(this,HeartRateActivity.class));
    }
}
