package com.amsu.healthy.activity.insole;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.adapter.FragmentListRateAdapter;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.fragment.inoutdoortype.InDoorRunFragment;
import com.amsu.healthy.fragment.inoutdoortype.OutDoorRunFragment;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class PrepareRunningActivity extends BaseActivity {

    private static final String TAG = "PrepareRunningActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_running);

        initView();

    }

    private void initView() {
        initHeadView();
        setCenterText("开始跑步");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewPager vp_prepare_item = (ViewPager) findViewById(R.id.vp_prepare_item);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new OutDoorRunFragment());
        fragmentList.add(new InDoorRunFragment());
        FragmentListRateAdapter mAnalysisRateAdapter = new FragmentListRateAdapter(getSupportFragmentManager(), fragmentList);
        vp_prepare_item.setAdapter(mAnalysisRateAdapter);
    }


    public void goStartRun(View view) {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        Log.i(TAG,"gps打开？:"+locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER));
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            MyUtil.chooseOpenGps(this);
        }
        else {
            getInsoleToken();
            Intent intent = new Intent(this,RunTimeCountdownActivity.class);
            intent.putExtra("mCurPosition",0);
            startActivity(intent);
        }
    }


    public void getInsoleToken() {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();

        params.addBodyParameter("username","amtek");
        params.addBodyParameter("password","12345");
        MyUtil.addCookieForHttp(params);


        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getInsoleTokenURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(PrepareRunningActivity.this);
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                 /*{
                "access_token": "eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiaW52b2tlciIsImlkIjoxMDYsImV4cCI6MTUwMTU4MzYxNiwiaWF0IjoxNTAxNTc2NDE2LCJ1c2VybmFtZSI6ImFtdGVrIn0.Pa5xoUWS6S5sUjeSyyr2p2wfFElhK4YiyulC8macitR3I9Rca3FQEZGO8xIMOafWOAXZzEiUHAnxo1EvLCtVXQ",
                "expires_in": 7200
            }*/
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String access_token = (String) jsonObject.get("access_token");
                    if (!MyUtil.isEmpty(access_token)){
                        MyApplication.insoleAccessToken = access_token;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog(PrepareRunningActivity.this);
                Log.i(TAG,"上传onFailure==result:"+e);
            }
        });


    }
}


