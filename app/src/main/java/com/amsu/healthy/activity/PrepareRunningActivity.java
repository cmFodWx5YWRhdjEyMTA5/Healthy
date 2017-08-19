package com.amsu.healthy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.FragmentListRateAdapter;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.fragment.inoutdoortype.InDoorRunFragment;
import com.amsu.healthy.fragment.inoutdoortype.OutDoorRunFragment;
import com.amsu.healthy.fragment.inoutdoortype.OutDoorRunGoogleFragment;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MyUtil;
import com.ble.api.DataUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PrepareRunningActivity extends BaseActivity {

    private static final String TAG = "PrepareRunningActivity";
    private Button bt_choose_offline;
    private boolean mIsOutDoor = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_running);

        initView();

    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.start));
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewPager vp_prepare_item = (ViewPager) findViewById(R.id.vp_prepare_item);
        List<Fragment> fragmentList = new ArrayList<>();
        String country = Locale.getDefault().getCountry();
        Log.i(TAG,"country:"+country);Locale.CHINA.getCountry();
        /*Log.i(TAG,"Locale.CHINESE:"+Locale.CHINESE.toString());
        Log.i(TAG,"Locale.getDefault().getCountry():"+Locale.getDefault().getCountry());
        Log.i(TAG,"Locale.CHINA.getCountry():"+Locale.CHINA.getCountry())*/;
        if(country.equals(Locale.CHINA.getCountry())){
            //中国
            fragmentList.add(new OutDoorRunFragment());
        }
        else {
            //国外
            fragmentList.add(new OutDoorRunGoogleFragment());
        }

        fragmentList.add(new InDoorRunFragment());
        FragmentListRateAdapter mAnalysisRateAdapter = new FragmentListRateAdapter(getSupportFragmentManager(), fragmentList);
        vp_prepare_item.setAdapter(mAnalysisRateAdapter);

        vp_prepare_item.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position==0){
                    mIsOutDoor = true;
                }
                else {
                    mIsOutDoor = false;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bt_choose_offline = (Button) findViewById(R.id.bt_choose_offline);

        if (MyApplication.deivceType==Constant.sportType_Cloth){

        }
        else if (MyApplication.deivceType==Constant.sportType_Insole){
            bt_choose_offline.setVisibility(View.GONE);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());
    }

    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case LeProxy.ACTION_GATT_DISCONNECTED:
                    Log.w(TAG,"已断开 ");
                    isDeviceDisconnected =  true;
                    if (isSendOffLineOrder){
                        MyUtil.hideDialog(PrepareRunningActivity.this);
                        android.support.v7.app.AlertDialog alertDialog_1 = new android.support.v7.app.AlertDialog.Builder(PrepareRunningActivity.this)
                                .setTitle("主机已进入离线，快去跑步吧，记得回来同步跑步数据哦！")
                                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .create();
                        alertDialog_1.setCanceledOnTouchOutside(false);
                        alertDialog_1.show();
                    }
                    break;
            }
        }
    };

    public void goStartRun(View view) {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        Log.i(TAG,"gps打开？:"+locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER));
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            MyUtil.chooseOpenGps(this);
        }
        else {
            Intent intent = new Intent(this,RunTimeCountdownActivity.class);
            intent.putExtra("mIsOutDoor",mIsOutDoor);
            intent.putExtra(Constant.sportState,MyApplication.deivceType);

            if (MyApplication.deivceType==Constant.sportType_Cloth){

            }
            else if (MyApplication.deivceType==Constant.sportType_Insole){
                getInsoleToken();
            }
            startActivity(intent);
            finish();
        }
    }

    int mSendOrderCount;
    boolean isSendOffLineOrder;
    boolean isDeviceDisconnected;

    public void goStartOffLineRun(View view) {
        Log.i(TAG,"关闭数据指令");
        if (!MyUtil.isEmpty(CommunicateToBleService.clothDeviceConnecedMac)){


            MyUtil.showDialog("正在发送离线指令",this);
            mSendOrderCount = 0;
            isSendOffLineOrder = true;


            new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (mSendOrderCount <30 && !isDeviceDisconnected){
                        if (mSendOrderCount<15){
                            boolean send = LeProxy.getInstance().send(CommunicateToBleService.clothDeviceConnecedMac, DataUtil.hexToByteArray(Constant.stopDataTransmitOrder), true);
                            Log.i(TAG,"send:"+send);
                        }
                        mSendOrderCount++;
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (mSendOrderCount==30){
                            //结束
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MyUtil.showToask(PrepareRunningActivity.this,"进入离线失败，请点击重试");
                                    MyUtil.hideDialog(PrepareRunningActivity.this);
                                }
                            });
                        }
                    }
                }
            }.start();
        }
        else {
            MyUtil.showToask(this,"衣服未连接，无法进入离线跑步");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyUtil.dismissPopWindow();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
    }
}


