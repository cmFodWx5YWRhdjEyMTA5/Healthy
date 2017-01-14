package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;

public class EditProtocolActivity extends BaseActivity {

    private boolean isNeedOpen;
    private ImageView iv_campaign_switvh;
    private TextView et_protocol_title;
    private EditText et_protocol_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_protocol);

        initView();

    }

    private void initView() {
        initHeadView();
        setCenterText("参与活动协议");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        iv_campaign_switvh = (ImageView) findViewById(R.id.iv_campaign_switvh);
        et_protocol_title = (TextView) findViewById(R.id.et_protocol_title);
        et_protocol_content = (EditText) findViewById(R.id.et_protocol_content);

    }

    public void switchState(View view) {
        //切换自动分析状态
        if (!isNeedOpen){
            iv_campaign_switvh.setImageResource(R.drawable.switch_on);
            isNeedOpen = true;
            MyUtil.putBooleanValueFromSP("isAutoOpen",true);
        }
        else {
            iv_campaign_switvh.setImageResource(R.drawable.switch_of);
            isNeedOpen = false;
            MyUtil.putBooleanValueFromSP("isAutoOpen",false);
        }
    }


    public void submit(View view) {
        String title = et_protocol_title.getText().toString();
        String content = et_protocol_content.getText().toString();

        if (!MyUtil.isEmpty(content)){
            //传到上个界面
            Intent intent = getIntent();
            intent.putExtra("content",content);
            intent.putExtra("isNeedOpen",isNeedOpen);
            setResult(RESULT_OK,intent);

            finish();
        }
        else {
            MyUtil.showToask(this,"输入协议内容");
        }

    }
}
