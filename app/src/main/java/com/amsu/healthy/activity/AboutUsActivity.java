package com.amsu.healthy.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;

public class AboutUsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.about_us));
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv_aboutus_version = (TextView) findViewById(R.id.tv_aboutus_version);

        String versionName = MyUtil.getVersionName(this);
        if (versionName!=null){
            tv_aboutus_version.setText(versionName);
        }


    }




}
