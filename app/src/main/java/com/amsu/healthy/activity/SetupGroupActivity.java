package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.ClubGroup;
import com.amsu.healthy.utils.MyUtil;

public class SetupGroupActivity extends BaseActivity {

    private EditText et_jiongroup_name;
    private EditText et_jiongroup_type;
    private EditText et_jiongroup_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_group);
        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("创建小组");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_jiongroup_name = (EditText) findViewById(R.id.et_jiongroup_name);
        et_jiongroup_type = (EditText) findViewById(R.id.et_jiongroup_type);
        et_jiongroup_description = (EditText) findViewById(R.id.et_jiongroup_description);
    }

    //提交
    public void submit(View view) {
        String name = et_jiongroup_name.getText().toString();
        String type = et_jiongroup_type.getText().toString();
        String description = et_jiongroup_description.getText().toString();

        if (MyUtil.isEmpty(name)){
            MyUtil.showToask(this,"输入名称");
        }
        else if (MyUtil.isEmpty(type)){
            MyUtil.showToask(this,"输入类型");
        }
        else if (MyUtil.isEmpty(description)){
            MyUtil.showToask(this,"输入概述");
        }
        else {
            //数据上传
            ClubGroup clubGroup = new ClubGroup(name,type,description,"0","");
            Intent intent = getIntent();
            Bundle bundle = new Bundle();
            bundle.putParcelable("clubGroup",clubGroup);
            intent.putExtra("bundle",bundle);

            setResult(RESULT_OK,intent);
            finish();

        }


    }
}
