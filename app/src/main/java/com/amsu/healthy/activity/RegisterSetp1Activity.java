package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amsu.healthy.R;

public class RegisterSetp1Activity extends BaseActivity {

    private EditText et_step1_username;

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

        et_step1_username = (EditText) findViewById(R.id.et_step1_username);

    }

    private void initData() {
        setCenterText("快速注册");


    }

    public void nextStrep(View view) {
        String username = et_step1_username.getText().toString();
        if (username.isEmpty()){
            Toast.makeText(this,"请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, RegisterSetp2Activity.class);
        intent.putExtra("username",username);
        startActivity(intent);
    }


}
