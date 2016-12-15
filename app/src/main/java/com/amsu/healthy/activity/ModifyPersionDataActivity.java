package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.Constant;

public class ModifyPersionDataActivity extends BaseActivity {

    private static final String TAG = "ModifyPersionData";
    private EditText et_modify_value;
    private ImageView et_modify_delete;
    private int modifyType;
    private RelativeLayout rl_persion_username;
    private LinearLayout ll_persion_sex;
    private CheckBox cb_modify_man;
    private CheckBox cb_modify_woman;
    private String modifyValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_persion_data);

        initView();
        initData();


    }

    private void initView() {
        initHeadView();
        Intent intent = getIntent();
        modifyType = intent.getIntExtra("modifyType", -1);
        modifyValue = intent.getStringExtra("modifyValue");

        MyOcClickListener l = new MyOcClickListener();

        if (modifyType==Constant.MODIFY_SEX){
            rl_persion_username = (RelativeLayout) findViewById(R.id.rl_persion_username);
            ll_persion_sex = (LinearLayout) findViewById(R.id.ll_persion_sex);

            ll_persion_sex.setVisibility(View.VISIBLE);
            rl_persion_username.setVisibility(View.GONE);
            RelativeLayout rl_persion_man = (RelativeLayout) findViewById(R.id.rl_persion_man);
            RelativeLayout rl_persion_woman = (RelativeLayout) findViewById(R.id.rl_persion_woman);

            cb_modify_man = (CheckBox) findViewById(R.id.cb_modify_man);
            cb_modify_woman = (CheckBox) findViewById(R.id.cb_modify_woman);


            rl_persion_man.setOnClickListener(l);
            rl_persion_woman.setOnClickListener(l);
        }
        else {
            et_modify_value = (EditText) findViewById(R.id.et_modify_value);
            et_modify_delete = (ImageView) findViewById(R.id.et_modify_delete);
            et_modify_delete.setOnClickListener(l);
        }

    }

    private void initData() {
        if (modifyType == Constant.MODIFY_USERNSME){
            setCenterText("修改姓名");
            et_modify_value.setText(modifyValue);
        }
        else if (modifyType ==Constant.MODIFY_SEX){
            setCenterText("修改性别");
            if (modifyValue.equals("1")){
                cb_modify_man.setChecked(true);
            }
            else {
                cb_modify_woman.setChecked(true);
            }
        }
        else if (modifyType ==Constant.MODIFY_EMAIL){
            setCenterText("修改邮箱");
            et_modify_value.setText(modifyValue);
        }
        else if (modifyType ==Constant.MODIFY_PHONE){
            setCenterText("修改手机");
            et_modify_value.setText(modifyValue);
        }



        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setRightText("确定");
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
    }

    public void confirm() {
        String modifyValue ="";
        if (modifyType==Constant.MODIFY_SEX){
            boolean checked = cb_modify_man.isChecked();
            if (checked){
                modifyValue = "男";
            }
            else {
                modifyValue = "女";
            }
        }
        else {
            modifyValue= et_modify_value.getText().toString();
        }
        Log.i(TAG,"###modifyType:"+modifyType+",modifyValue:"+modifyValue);
        Intent intent = getIntent();
        intent.putExtra("modifyValue",modifyValue);
        intent.putExtra("modifyType",modifyType);
        setResult(RESULT_OK,intent);
        finish();
    }

    class MyOcClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_persion_man:
                    cb_modify_man.setChecked(true);
                    cb_modify_woman.setChecked(false);
                    break;
                case R.id.rl_persion_woman:
                    cb_modify_man.setChecked(false);
                    cb_modify_woman.setChecked(true);
                    break;
                case R.id.et_modify_delete:
                    et_modify_value.setText("");
                    break;
            }
        }

    }
}
