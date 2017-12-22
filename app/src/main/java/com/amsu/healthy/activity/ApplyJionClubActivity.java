package com.amsu.healthy.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;

public class ApplyJionClubActivity extends BaseActivity {

    private EditText et_jionclub_description;
    private EditText et_jionclub_apply;
    private EditText et_jionclub_phone;
    private EditText et_jionclub_realname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_jion_club);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("加入俱乐部申请");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_jionclub_apply = (EditText) findViewById(R.id.et_jionclub_apply);
        et_jionclub_phone = (EditText) findViewById(R.id.et_jionclub_phone);
        et_jionclub_realname = (EditText) findViewById(R.id.et_jionclub_realname);
        et_jionclub_description = (EditText) findViewById(R.id.et_jionclub_description);


    }

    public void submit(View view) {
        String apply = et_jionclub_apply.getText().toString();
        String phone = et_jionclub_phone.getText().toString();
        String realname = et_jionclub_realname.getText().toString();
        String description = et_jionclub_description.getText().toString();

        //提交数据

        finish();
        MyUtil.showToask(this,"已加入");



    }
}
