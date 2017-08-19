package com.amsu.healthy.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.MyUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Locale;

public class BaseActivity extends FragmentActivity {

    public ImageView iv_base_leftimage;
    public TextView tv_base_rightText;
    public TextView tv_base_centerText;
    public ImageView iv_base_rightimage;
    private TextView tv_base_lefttext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //initHeadView();

    }


    public void initHeadView() {
        iv_base_leftimage = (ImageView) findViewById(R.id.iv_base_leftimage);
        tv_base_centerText = (TextView) findViewById(R.id.tv_base_centerText);
        tv_base_lefttext = (TextView) findViewById(R.id.tv_base_lefttext);
        tv_base_rightText = (TextView) findViewById(R.id.tv_base_rightText);
        iv_base_rightimage = (ImageView) findViewById(R.id.iv_base_rightimage);

        //changeAppLanguage();

    }

    public void changeAppLanguage() {
        String sta = MyUtil.getStringValueFromSP("language");//这是SharedPreferences工具类，用于保存设置，代码很简单，自己实现吧
        if (!MyUtil.isEmpty(sta)){
            // 本地语言设置
            Locale myLocale = new Locale(sta);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }
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

    public void setLeftText(String text) {
        tv_base_lefttext.setText(text);
        tv_base_lefttext.setVisibility(View.VISIBLE);
    }

    public void setRightText(String text) {
        tv_base_rightText.setText(text);
        tv_base_rightText.setVisibility(View.VISIBLE);
    }


    public ImageView getIv_base_leftimage() {
        return iv_base_leftimage;
    }

    public void setIv_base_leftimage(ImageView iv_base_leftimage) {
        this.iv_base_leftimage = iv_base_leftimage;
    }

    public TextView getTv_base_leftText() {
        return tv_base_lefttext;
    }


    public TextView getTv_base_rightText() {
        return tv_base_rightText;
    }

    public void setTv_base_rightText(TextView tv_base_rightText) {
        this.tv_base_rightText = tv_base_rightText;
    }

    public TextView getTv_base_centerText() {
        return tv_base_centerText;
    }

    public void setTv_base_centerText(TextView tv_base_centerText) {
        this.tv_base_centerText = tv_base_centerText;
    }

    public ImageView getIv_base_rightimage() {
        return iv_base_rightimage;
    }

    public void setIv_base_rightimage(ImageView iv_base_rightimage) {
        this.iv_base_rightimage = iv_base_rightimage;
    }

    public void setHeadBackgroudColor(String color) {
        RelativeLayout rl_base_head = (RelativeLayout) findViewById(R.id.rl_base_head);
        rl_base_head.setBackgroundColor(Color.parseColor(color));
    }

    public void setCenterTextColor(String color) {
        tv_base_centerText.setTextColor(Color.parseColor(color));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //MobclickAgent.onPageStart(this.getClass().getSimpleName());
        MyApplication.mCurrApplicationActivity = this;
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        MyApplication.mCurrApplicationActivity = null;
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
