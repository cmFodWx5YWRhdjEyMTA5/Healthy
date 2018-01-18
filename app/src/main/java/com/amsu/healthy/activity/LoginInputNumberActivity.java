package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.MyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

public class LoginInputNumberActivity extends BaseActivity {

    private static final String TAG = "LoginInputNumber";
    // 默认使用中国区号
    private static final String DEFAULT_COUNTRY_ID = "42";
    private TextView tv_login_zonecode;
    private TextView et_login_phone;
    private TextView tv_login_national_region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_input_number);

        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        initHeadView();
        setCenterText(getResources().getString(R.string.login));
        setLeftImage(R.drawable.guanbi_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtil.destoryAllAvtivity(LoginInputNumberActivity.this);
                startActivity(new Intent(LoginInputNumberActivity.this,MainActivity.class));
                finish();
            }
        });


        tv_login_national_region = (TextView) findViewById(R.id.tv_login_national_region);
        tv_login_zonecode = (TextView) findViewById(R.id.tv_login_zonecode);
        et_login_phone = (TextView) findViewById(R.id.et_login_phone);

        et_login_phone.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        List<Activity> mActivities = ((MyApplication) getApplication()).mActivities;
        if (!mActivities.contains(this)){
            mActivities.add(this);
        }

    }

    private void initData() {
        String[] currentCountry = getCurrentCountry();

        if(currentCountry!=null){
            Log.i(TAG,"currentCountry.length:"+currentCountry.length);
            for (String s:currentCountry){
                Log.i(TAG,"s:"+s);
            }
            if (currentCountry.length>=2){
                tv_login_national_region.setText(currentCountry[0]);
                tv_login_zonecode.setText("+"+currentCountry[1]);
            }
        }

        EventHandler eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {
                Log.i(TAG,"afterEvent====="+"event:"+event+",result:"+result+",data:"+data.toString());

                if (result==SMSSDK.RESULT_COMPLETE){
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //MyUtil.showToask(LoginInputNumberActivity.this, getResources().getString(R.string.verify_code_sent_successfully));
                                MyUtil.hideDialog(LoginInputNumberActivity.this);
                                Intent intent = new Intent(LoginInputNumberActivity.this, LoginActivity.class);
                                String zonecode = tv_login_zonecode.getText().toString().trim();
                                String phone = et_login_phone.getText().toString().trim();
                                intent.putExtra("zonecode",zonecode);
                                intent.putExtra("phone",phone);
                                startActivity(intent);
                            }
                        });
                    }
                    else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功

                    }
                    else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表

                    }
                    else{
                        ((Throwable)data).printStackTrace();
                    }
                }
                else {
                    //event:2,result:0,data:java.lang.Throwable: {"status":603,"detail":"请填写正确的手机号码"}


                    String[] split = data.toString().split(":");
                    String detail = null;
                    int status = 0;
                    if (split!=null &&split.length==4){
                        String josnData = split[1]+":"+split[2]+":"+split[3];
                        Log.i(TAG,"josnData:"+josnData);

                        try {
                            JSONObject jsonObject = new JSONObject(josnData);
                            status = jsonObject.getInt("status");
                            detail = jsonObject.getString("detail");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        detail ="网络异常，请重试";
                    }

                    Log.i(TAG,"status:"+status+",detail:"+detail);

                    final String finalDetail = detail;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyUtil.showToask(LoginInputNumberActivity.this, finalDetail);
                            MyUtil.hideDialog(LoginInputNumberActivity.this);
                        }
                    });
                }
            }


        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }



    private String[] getCurrentCountry() {
        String mcc = getMCC();
        String[] country = null;
        if (!TextUtils.isEmpty(mcc)) {
            country = SMSSDK.getCountryByMCC(mcc);
        }

        if (country == null) {
            SMSLog.getInstance().d("no country found by MCC: " + mcc);
            country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        }
        return country;
    }

    private String getMCC() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 返回当前手机注册的网络运营商所在国家的MCC+MNC. 如果没注册到网络就为空.
        String networkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator;
        }

        // 返回SIM卡运营商所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
        return tm.getSimOperator();
    }

    public void chooseCountry(View view) {
        startActivityForResult(new Intent(this,LoginCountryListActivity.class),100);
    }

    public void nextStep(View view) {
        String zonecode = tv_login_zonecode.getText().toString().trim();
        String phone = et_login_phone.getText().toString().trim();
        Log.i(TAG,"zonecode:"+zonecode+",phone:"+phone);

        boolean numeric = MyUtil.isNumeric(phone);

        if (phone.isEmpty() || !numeric){
            Toast.makeText(this,getResources().getString(R.string.enter_cell_phone_number), Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.equals("13392887820") || phone.equals("13923475272") || phone.equals("18026967001") || phone.equals("17704026793")){  //测试手机号13392887820，13923475272，18026967001
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("zonecode",zonecode);
            intent.putExtra("phone",phone);
            startActivity(intent);
        }
        else {
            getMESCode(zonecode,phone);  //86为国家代码
            MyUtil.showDialog(getResources().getString(R.string.getting_validation_code),this);
        }
    }

    public void getMESCode(String country, final String phone) {
        SMSSDK.getVerificationCode(country, phone);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"requestCode:"+requestCode+",resultCode:"+resultCode);
        if (requestCode==100 && resultCode==RESULT_OK){
            if (data!=null){
                String countryId = data.getStringExtra("countryId");
                String[] country = SMSSDK.getCountry(countryId);
                if (country!=null && country.length>=2){
                    tv_login_national_region.setText(country[0]);
                    tv_login_zonecode.setText("+"+country[1]);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            MyUtil.destoryAllAvtivity(LoginInputNumberActivity.this);
            startActivity(new Intent(LoginInputNumberActivity.this,MainActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}
