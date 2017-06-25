package com.amsu.healthy.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;

public class DisclaimerAssertsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer_asserts);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("免责申明");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WebView wb_asset_web = (WebView) findViewById(R.id.wb_asset_web);

        wb_asset_web.loadUrl(Constant.disclaimerAssertsURL);
    }

}
