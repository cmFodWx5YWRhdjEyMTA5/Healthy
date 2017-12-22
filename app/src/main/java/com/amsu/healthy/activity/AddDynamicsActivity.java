package com.amsu.healthy.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amsu.healthy.R;

public class AddDynamicsActivity extends BaseActivity {

    private static final String TAG = "AddDynamicsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dynamics);


        initView();

    }




    private void initView() {
        initHeadView();
        setCenterText("动态");
        setLeftText("取消");
        getTv_base_leftText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setRightText("发布");
        getTv_base_rightText().setTextColor(Color.parseColor("#999999"));
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        EditText et_add_input = (EditText) findViewById(R.id.et_add_input);

        et_add_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged:"+count);
                if (count>0){
                    getTv_base_rightText().setTextColor(Color.parseColor("#FFFFFF"));
                    getTv_base_rightText().setClickable(true);
                }
                else {
                    getTv_base_rightText().setTextColor(Color.parseColor("#999999"));
                    getTv_base_rightText().setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
}
