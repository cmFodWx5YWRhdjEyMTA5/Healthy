package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;

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
            setCenterText(getResources().getString(R.string.modify_nick_name));
            et_modify_value.setText(modifyValue);
        }
        else if (modifyType ==Constant.MODIFY_SEX){
            setCenterText(getResources().getString(R.string.modify_gender));
            if (!MyUtil.isEmpty(modifyValue)){
                if (modifyValue.equals("1")){
                    cb_modify_man.setChecked(true);
                }
                else {
                    cb_modify_woman.setChecked(true);
                }
            }

        }
        else if (modifyType ==Constant.MODIFY_EMAIL){
            setCenterText(getResources().getString(R.string.modify_email));
            et_modify_value.setText(modifyValue);

        }
        else if (modifyType ==Constant.MODIFY_PHONE){
            setCenterText(getResources().getString(R.string.modify_email));
            et_modify_value.setText(modifyValue);
        }
        else if (modifyType ==Constant.MODIFY_STILLRATE){
            setCenterText(getResources().getString(R.string.modify_rest_heart));
            et_modify_value.setText(modifyValue);
            et_modify_value.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        }


        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setRightText(getResources().getString(R.string.exit_confirm));
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
        else if (modifyType==Constant.MODIFY_STILLRATE){
            modifyValue= et_modify_value.getText().toString();
            if(!MyUtil.isEmpty(modifyValue)){
                boolean numeric = MyUtil.isNumeric(modifyValue);
                if (numeric){
                    int i = Integer.parseInt(modifyValue);
                    if (i<30 || i>170){
                        MyUtil.showToask(this,"静息心率不在合理范围里");
                        return;
                    }
                }
                else {
                    MyUtil.showToask(this,"静息心率必须是整数，请重新输入");
                    return;
                }
            }
            else {
                MyUtil.showToask(this,"请输入静息心率");
                return;
            }

        }
        else if (modifyType==Constant.MODIFY_USERNSME){
            modifyValue= et_modify_value.getText().toString();
            if (!MyUtil.isEmpty(modifyValue) ){
                if (modifyValue.length()>15){
                    MyUtil.showToask(this,"用户名太长了，请重新输入");
                }
            }
            else {
                MyUtil.showToask(this,"用户名为空，请重新输入");
            }
        }
        else if (modifyType==Constant.MODIFY_EMAIL){
            modifyValue= et_modify_value.getText().toString();
            boolean b = MyUtil.checkEmail(modifyValue);
            if (b){
                modifyValue= et_modify_value.getText().toString();
            }
            else {
               MyUtil.showToask(this,"邮箱格式不对，请重新输入");
               return;
            }
        }
        else {
            modifyValue= et_modify_value.getText().toString();
        }

        Log.i(TAG,"###modifyType:"+modifyType+",modifyValue:"+modifyValue);
        Intent intent = getIntent();
        if (MyUtil.isEmpty(modifyValue)){
            modifyValue = "0";
        }
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
