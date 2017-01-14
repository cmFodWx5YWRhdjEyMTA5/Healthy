package com.amsu.healthy.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;

public class ApplyJoinGroupActivity extends BaseActivity {

    private EditText et_jiongroup_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_join_group);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("申请加入小组");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_jiongroup_description = (EditText) findViewById(R.id.et_jiongroup_description);


    }

    public void submit(View view) {
        String description = et_jiongroup_description.getText().toString();

        //提交数据

        finish();
        MyUtil.showToask(this,"已加入");



    }
}
