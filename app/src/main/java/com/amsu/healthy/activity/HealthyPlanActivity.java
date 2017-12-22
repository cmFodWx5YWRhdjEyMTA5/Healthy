package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.HealthyPlanDataAdapter;
import com.amsu.healthy.bean.HealthyPlan;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HealthyPlanActivity extends BaseActivity {

    private static final String TAG = "HealthyPlanActivity";
    private ListView lv_healthplan_plan;
    private List<HealthyPlan> healthyPlanList;
    private ArrayList<HealthyPlan> healthyPlanListFromLastYear;
    private HealthyPlanDataAdapter healthyPlanDataAdapter;
    private int mClickPositon = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_plan);

        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.health_plan));
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
                Intent intent = new Intent(HealthyPlanActivity.this, HealthyPlanCalenActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("healthyPlanListFromLastYear",healthyPlanListFromLastYear);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });

        lv_healthplan_plan = (ListView) findViewById(R.id.lv_healthplan_plan);
    }

    private void initData() {
        healthyPlanList = new ArrayList<>();
        healthyPlanListFromLastYear = new ArrayList<>();
        healthyPlanDataAdapter = new HealthyPlanDataAdapter(HealthyPlanActivity.this,healthyPlanList);
        lv_healthplan_plan.setAdapter(healthyPlanDataAdapter);
        lv_healthplan_plan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClickPositon = position;
                HealthyPlan healthyPlan = healthyPlanList.get(position);
                String healthyPlanId = healthyPlan.getId();
                Intent intent = new Intent(HealthyPlanActivity.this,LookupHealthPlanActivity.class);
                intent.putExtra("id",healthyPlanId);
                startActivityForResult(intent,111);
            }
        });

        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        Date date = new Date();
        date.setDate(date.getDate()-1);
        String formatTime = MyUtil.getPaceFormatTime(date);
        params.addBodyParameter("date",formatTime);
        params.addBodyParameter("page","1");
        MyUtil.addCookieForHttp(params);

        MyUtil.showDialog("正在获取健康计划列表",HealthyPlanActivity.this);
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getAfter20ItemHealthyPlanListURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(getApplication());
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                JsonBase<List<HealthyPlan>> jsonBase = MyUtil.commonJsonParse(result, new TypeToken<JsonBase<List<HealthyPlan>>>() {}.getType());
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase!=null&&jsonBase.getRet()==0){
                    if (jsonBase.errDesc!=null && jsonBase.errDesc.size()>0){
                        healthyPlanList.addAll(jsonBase.errDesc);
                        healthyPlanDataAdapter.notifyDataSetChanged();
                    }
                    Log.i(TAG,"healthyPlanList:"+healthyPlanList.size());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog(getApplication());
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });


        getHealthPlanDataFromLastYear();

    }

    private void getHealthPlanDataFromLastYear(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR)-1);
        String date = MyUtil.getSpecialFormatTime("yyyy-MM-dd", calendar.getTime());

        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("date",date);

        MyUtil.addCookieForHttp(params);
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getHealthyPlanListURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                JsonBase<List<HealthyPlan>> jsonBase = MyUtil.commonJsonParse(result, new TypeToken<JsonBase<List<HealthyPlan>>>() {}.getType());
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase!=null&&jsonBase.getRet()==0){
                    if (jsonBase.errDesc!=null && jsonBase.errDesc.size()>0){
                        healthyPlanListFromLastYear.addAll(jsonBase.errDesc);
                    }
                    Log.i(TAG,"healthyPlanListFromLastYear:"+healthyPlanListFromLastYear.size());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
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
        Log.i(TAG,"onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==120 && resultCode==RESULT_OK){
            Bundle bundle = data.getBundleExtra("bundle");
            HealthyPlan healthyPlan = bundle.getParcelable("healthyPlan");
            healthyPlanList.add(healthyPlan);
            healthyPlanDataAdapter.notifyDataSetChanged();

            Log.i(TAG,"add");
        }
        else if (requestCode==111 && resultCode==RESULT_OK){
            Bundle bundle = data.getBundleExtra("bundle");
            HealthyPlan healthyPlan = bundle.getParcelable("healthyPlan");

            healthyPlanDataAdapter.notifyDataSetChanged();
            if (mClickPositon!=-1){
                healthyPlanList.get(mClickPositon).setTitle(healthyPlan.getTitle());
                healthyPlanList.get(mClickPositon).setContent(healthyPlan.getContent());
                healthyPlanList.get(mClickPositon).setDate(healthyPlan.getDate());

            }

            Log.i(TAG,"add:"+healthyPlan.toString());
        }
    }
}
