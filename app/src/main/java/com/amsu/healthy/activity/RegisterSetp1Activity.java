package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.amsu.healthy.R;

public class RegisterSetp1Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_setp1);

        initView();


    }

    private void initView() {
        initHeadView();
        setLeftImage(R.drawable.guanbi_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initData() {
        setCenterText("快速注册");


    }

    public void nextStrep(View view) {
        startActivity(new Intent(this,RegisterSetp2Activity.class));
    }


}
