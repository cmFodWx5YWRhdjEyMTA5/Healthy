package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.Constant;

public class QuestionnaireActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("问卷调查");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RelativeLayout rl_persiondata_questionnaire1 = (RelativeLayout) findViewById(R.id.rl_persiondata_questionnaire1);
        RelativeLayout rl_persiondata_questionnaire2 = (RelativeLayout) findViewById(R.id.rl_persiondata_questionnaire2);

        rl_persiondata_questionnaire1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Uri uri = Uri.parse(Constant.Questionnaire1URL);
                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });

        rl_persiondata_questionnaire2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Uri uri = Uri.parse(Constant.Questionnaire2URL);
                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });

        /*WebView wb_disclaimer_web = (WebView) findViewById(R.id.wb_disclaimer_web);
        wb_disclaimer_web.loadUrl(Constant.QuestionnaireURL);*/


    }


}
