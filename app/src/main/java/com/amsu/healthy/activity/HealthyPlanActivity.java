package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.HealthyPlanDataAdapter;
import com.amsu.healthy.bean.HealthyPlan;
import com.amsu.healthy.bean.JsonHealthyList;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HealthyPlanActivity extends BaseActivity {

    private static final String TAG = "HealthyPlanActivity";
    private ListView lv_healthplan_plan;
    private List<HealthyPlan> healthyPlanList;
    private HealthyPlanDataAdapter healthyPlanDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_plan);

        initView();
        initData();
    }



    private void initView() {
        initHeadView();
        setCenterText("健康计划");
        setLeftImage(R.drawable.back_icon);
        setRightImage(R.drawable.plan_calendar);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HealthyPlanActivity.this,HealthyPlanCalenActivity.class));
                finish();
            }
        });


        lv_healthplan_plan = (ListView) findViewById(R.id.lv_healthplan_plan);



    }

    private void initData() {
        healthyPlanList = new ArrayList<>();
        healthyPlanList.add(new HealthyPlan("hhh","hhhh","hhhh"));
        healthyPlanDataAdapter = new HealthyPlanDataAdapter(this,healthyPlanList);
        lv_healthplan_plan.setAdapter(healthyPlanDataAdapter);

        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        String formatTime = MyUtil.getFormatTime(new Date());
        params.addBodyParameter("date",formatTime);
        MyUtil.addCookieForHttp(params);
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getHealthyPlanListURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                JsonHealthyList jsonHealthyList = gson.fromJson(result, JsonHealthyList.class);
                List<HealthyPlan> errDesc = jsonHealthyList.getErrDesc();
                healthyPlanList = errDesc;
                healthyPlanDataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog();
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }


    public void addHealthyPlan(View view) {
        Intent intent = new Intent(HealthyPlanActivity.this, AddHeathyPlanActivity.class);
        startActivityForResult(intent,120);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==120 && resultCode==RESULT_OK){
            Bundle bundle = data.getBundleExtra("bundle");
            HealthyPlan healthyPlan = bundle.getParcelable("healthyPlan");
            healthyPlanList.add(healthyPlan);
            healthyPlanDataAdapter.notifyDataSetChanged();
        }

    }
}
