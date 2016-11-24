package com.amsu.healthy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.healthy.R;

public class BaseActivity extends Activity {

    public ImageView iv_base_leftimage;
    public TextView tv_base_rightText;
    public TextView tv_base_centerText;
    public ImageView iv_base_rightimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //initHeadView();

    }


    public void initHeadView() {
        iv_base_leftimage = (ImageView) findViewById(R.id.iv_base_leftimage);
        tv_base_centerText = (TextView) findViewById(R.id.tv_base_centerText);
        tv_base_rightText = (TextView) findViewById(R.id.tv_base_rightText);
        iv_base_rightimage = (ImageView) findViewById(R.id.iv_base_rightimage);
    }

    public void setLeftImage(int resource) {
        iv_base_leftimage.setImageResource(resource);
        iv_base_leftimage.setVisibility(View.VISIBLE);
    }

    public void setRightImage(int resource) {
        iv_base_rightimage.setImageResource(resource);
        iv_base_rightimage.setVisibility(View.VISIBLE);
    }

    public void setCenterText(String text) {
        tv_base_centerText.setText(text);
    }

    public void setRightText(String text) {
        tv_base_rightText.setText(text);
        tv_base_rightText.setVisibility(View.VISIBLE);
    }
}
