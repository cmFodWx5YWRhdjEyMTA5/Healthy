package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;
import com.baidu.platform.comapi.map.E;

public class NewsDetialActivity extends BaseActivity {

    private static final String TAG = "NewsDetialActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detial);

        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        Log.i(TAG,"url:"+url);
        WebView wv_detial_article = (WebView) findViewById(R.id.wv_detial_article);
        wv_detial_article.loadUrl(url);


        RelativeLayout rl_detial_back = (RelativeLayout) findViewById(R.id.rl_detial_back);
        RelativeLayout rl_detial_comment = (RelativeLayout) findViewById(R.id.rl_detial_comment);
        RelativeLayout rl_detial_collect = (RelativeLayout) findViewById(R.id.rl_detial_collect);
        RelativeLayout rl_detial_share = (RelativeLayout) findViewById(R.id.rl_detial_share);
        RelativeLayout rl_detial_other = (RelativeLayout) findViewById(R.id.rl_detial_other);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        rl_detial_back.setOnClickListener(myOnClickListener);
        rl_detial_comment.setOnClickListener(myOnClickListener);
        rl_detial_collect.setOnClickListener(myOnClickListener);
        rl_detial_share.setOnClickListener(myOnClickListener);
        rl_detial_other.setOnClickListener(myOnClickListener);

    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_detial_back:
                    finish();
                    break;
                case R.id.rl_detial_comment:
                    showComment();

                    break;
                case R.id.rl_detial_collect:
                    collectArticle();

                    break;
                case R.id.rl_detial_share:
                    shareArticle();

                    break;
                case R.id.rl_detial_other:


                    break;


            }
        }
    }



    private void showComment() {
        startActivity(new Intent(NewsDetialActivity.this,ShowCommentActivity.class));
    }

    private void collectArticle() {
        MyUtil.showToask(NewsDetialActivity.this,"已收藏");
        //加入到收藏

    }

    private void shareArticle() {

    }
}
