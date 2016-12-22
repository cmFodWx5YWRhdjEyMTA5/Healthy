package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.HealthyPlan;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.JsonHealthy;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class LookupHealthPlanActivity extends BaseActivity {

    private static final String TAG = "LookupHealthPlan";
    private TextView tv_addplan_title;
    private TextView tv_addplan_content;
    private TextView tv_addplan_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_health_plan);

        initView();
        initData();
    }



    private void initView() {
        initHeadView();
        setCenterText("健康计划");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_addplan_title = (TextView) findViewById(R.id.tv_addplan_title);
        tv_addplan_content = (TextView) findViewById(R.id.tv_addplan_content);
        tv_addplan_time = (TextView) findViewById(R.id.tv_addplan_time);



    }

    private void initData() {
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        HttpUtils httpUtils = new HttpUtils();

        RequestParams params = new RequestParams();
        params.addBodyParameter("id",id);
        MyUtil.addCookieForHttp(params);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getHealthyPlanContentURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                if (jsonBase.getRet()==0){
                    JsonHealthy jsonHealthy = gson.fromJson(result, JsonHealthy.class);
                    HealthyPlan errDesc = jsonHealthy.getErrDesc();
                    tv_addplan_title.setText(errDesc.getTitle());
                    tv_addplan_content.setText(errDesc.getContent());
                    tv_addplan_time.setText(errDesc.getDate());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });


    }
}
