package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class ConfirmScanLoginActivity extends BaseActivity {

    private static final String TAG = "ConfirmScanLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_scan_login);

        initView();

    }

    private void initView() {
        initHeadView();
        setCenterText("确认登录");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void confirm(View view) {
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        String url = intent.getStringExtra("url");


        MyUtil.showDialog("正在进行服务器验证",this);

        final HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("passToken",key);
        MyUtil.addCookieForHttp(params);

        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(TAG,"onSuccess==result:"+result);
                MyUtil.hideDialog(ConfirmScanLoginActivity.this);
                MyUtil.showToask(ConfirmScanLoginActivity.this,"手机验证成功");
                finish();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"onFailure:"+e);
                MyUtil.hideDialog(ConfirmScanLoginActivity.this);
                MyUtil.showToask(ConfirmScanLoginActivity.this,"手机验证失败，请检查网络后重试");
            }
        });
    }

    public void cancel(View view) {
        finish();
    }
}
