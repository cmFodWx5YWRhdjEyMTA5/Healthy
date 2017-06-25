package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.HealthyPlan;
import com.amsu.healthy.bean.JsonBase;
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
    private String mPlanId;
    private HealthyPlan mHealthyPlan;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_health_plan);

        initView();
        initData();
    }



    private void initView() {
        initHeadView();
        setCenterText("健康计划详情");
        setRightText("修改");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LookupHealthPlanActivity.this,AddHeathyPlanActivity.class);
                if (mHealthyPlan!=null){
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("mHealthyPlan",mHealthyPlan);
                    intent.putExtra("bundle",bundle);
                }
                startActivityForResult(intent,120);
            }
        });

        tv_addplan_title = (TextView) findViewById(R.id.tv_addplan_title);
        tv_addplan_content = (TextView) findViewById(R.id.tv_addplan_content);
        tv_addplan_time = (TextView) findViewById(R.id.tv_addplan_time);



    }

    private void initData() {
        mIntent = getIntent();
        if (mIntent !=null){
            mPlanId = mIntent.getStringExtra("id");

            HttpUtils httpUtils = new HttpUtils();

            RequestParams params = new RequestParams();
            params.addBodyParameter("id", mPlanId);
            MyUtil.addCookieForHttp(params);

            httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getHealthyPlanContentURL, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String result = responseInfo.result;
                    Log.i(TAG,"上传onSuccess==result:"+result);
                    Gson gson = new Gson();
                    JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                    if (jsonBase.getRet()==0){
                        HealthyPlan jsonHealthy = gson.fromJson(String.valueOf(jsonBase.errDesc), HealthyPlan.class);

                        mHealthyPlan = jsonHealthy;
                        mHealthyPlan.setId(mPlanId);
                        tv_addplan_title.setText(mHealthyPlan.getTitle());
                        tv_addplan_content.setText(mHealthyPlan.getContent());
                        tv_addplan_time.setText(mHealthyPlan.getDate());
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i(TAG,"上传onFailure==s:"+s);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG,"onActivityResult");
        if (requestCode==120 &  resultCode==RESULT_OK){
            Bundle bundle = data.getBundleExtra("bundle");
            HealthyPlan healthyPlan = bundle.getParcelable("healthyPlan");
            tv_addplan_title.setText(healthyPlan.getTitle());
            tv_addplan_content.setText(healthyPlan.getContent());
            tv_addplan_time.setText(healthyPlan.getDate());

            if (mIntent!=null){
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable("healthyPlan",healthyPlan);
                mIntent.putExtra("bundle",bundle1);
                setResult(RESULT_OK,mIntent);
                Log.i(TAG,"setResult:"+healthyPlan.toString());
            }
        }
    }
}