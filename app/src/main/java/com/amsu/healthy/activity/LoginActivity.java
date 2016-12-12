package com.amsu.healthy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MD5Util;
import com.amsu.healthy.utils.MyUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private EditText et_login_phone;
    private EditText et_login_code;
    private Button bt_login_getcode;
    private CheckBox cb_login_isagree;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int MSG_UPDATE= 1;
    private int MSG_TOAST_FAIL= 3;
    private int MSG_TOAST_VERIFY= 0;
    private int MSG_TOAST_SUCCESS= 4;
    private int timeUpdate= 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();
    }



    private void initView() {
        initHeadView();
        setCenterText("登陆");
        setLeftImage(R.drawable.guanbi_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setRightText("快速注册");
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterSetp1Activity.class));
                finish();
            }
        });

        et_login_phone = (EditText) findViewById(R.id.et_login_phone);
        et_login_code = (EditText) findViewById(R.id.et_login_code);
        bt_login_getcode = (Button) findViewById(R.id.bt_login_getcode);
        cb_login_isagree = (CheckBox) findViewById(R.id.cb_login_isagree);
        final Button bt_login_nextstep = (Button) findViewById(R.id.bt_login_nextstep);

        cb_login_isagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_login_isagree.isChecked()){
                    bt_login_nextstep.setBackgroundResource(R.drawable.bg_bt_rec);
                }
                else {
                    bt_login_nextstep.setBackgroundColor(Color.parseColor("#c8c8c8"));
                }
            }
        });
    }

    private void initData() {
        mTimer = new Timer();
        EventHandler eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {
                Log.i(TAG,"afterEvent====="+"event:"+event+",result:"+result+",data:"+data.toString());

                if (result==SMSSDK.RESULT_COMPLETE){
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        myHandler.sendEmptyMessage(MSG_TOAST_SUCCESS);
                    }
                    else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        myHandler.sendEmptyMessage(MSG_TOAST_VERIFY);
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
                    String resultData = data.toString();
                    String josnData = resultData.split(":")[1]+":"+resultData.split(":")[2]+":"+resultData.split(":")[3];
                    Log.i(TAG,"josnData:"+josnData);
                    String detail ="";
                    int status = 0;
                    try {
                        JSONObject jsonObject = new JSONObject(josnData);
                        status = jsonObject.getInt("status");
                        detail = jsonObject.getString("detail");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG,"status:"+status+",detail:"+detail);

                    Message message = myHandler.obtainMessage(MSG_TOAST_FAIL);
                    message.obj = detail;
                    myHandler.sendMessage(message);
                }
            }


        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
        //定时器
        mTimerTask = new MyTimerTask();
    }


    public void login(View view) {
        String phone = et_login_phone.getText().toString();
        String inputVerifycode = et_login_code.getText().toString();

        if (phone.isEmpty()){
            Toast.makeText(this,"请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(inputVerifycode.isEmpty()){
            Toast.makeText(this,"请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        validateLogin(phone,inputVerifycode);

        
    }

    public void getVerifyCode(View view) {
        String phone = et_login_phone.getText().toString();
        if(!TextUtils.isEmpty(phone)){
            getMESCode("86",phone);  //86为国家代码
            MyUtil.showDialog("正在获取",this);
        }else{
            Toast.makeText(this,"输入手机号", Toast.LENGTH_SHORT).show();
        }
    }

    public void getMESCode(String country, final String phone) {
        SMSSDK.getVerificationCode(country, phone);

    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Message message = myHandler.obtainMessage(MSG_UPDATE);
            message.obj = timeUpdate;
            myHandler.sendMessage(message);
            if(timeUpdate==0){
                return;
            }
            timeUpdate--;
        }
    }

    Handler myHandler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what==MSG_TOAST_VERIFY){
                //成功
                //registerToDB();
            }
            else if (msg.what==MSG_UPDATE){
                int time = (int) msg.obj;
                if (time==0){
                    bt_login_getcode.setText("获取验证码");
                    bt_login_getcode.setClickable(true);
                    bt_login_getcode.setTextSize(10);
                    bt_login_getcode.setBackgroundResource(R.drawable.bg_button_verifycode);
                }

                else {
                    bt_login_getcode.setText(time+"");
                }
            }
            else if (msg.what==MSG_TOAST_FAIL){
                String detail = (String) msg.obj;
                Toast.makeText(LoginActivity.this,detail, Toast.LENGTH_SHORT).show();
                MyUtil.hideDialog();
            }
            else if (msg.what==MSG_TOAST_SUCCESS){
                bt_login_getcode.setClickable(false);
                bt_login_getcode.setBackgroundResource(R.drawable.bg_button_code_disable);
                bt_login_getcode.setTextSize(15);
                bt_login_getcode.setText(60+"");
                MyUtil.hideDialog();
                Toast.makeText(LoginActivity.this,"验证码发送成功", Toast.LENGTH_SHORT).show();
                timeUpdate = 60;

                if (mTimerTask != null){
                    mTimerTask.cancel();  //将原任务从队列中移除
                }
                mTimerTask = new MyTimerTask();
                mTimer.schedule(mTimerTask, 1000, 1000);
            }
        }
    };

    private void validateLogin(String phone, String inputVerifycode) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();

        params.addBodyParameter("phone",phone);

        String param = String.valueOf(System.currentTimeMillis());
        params.addBodyParameter("param", param);  //时间戳

        params.addBodyParameter("zone","86");  //区号
        params.addBodyParameter("code",inputVerifycode);  //验证码

        Log.i(TAG,"phone:"+phone+",param:"+param+",inputVerifycode:"+inputVerifycode);

        String oldface = "";
        try {
            oldface = MD5Util.getMD5(phone + param + Constant.tokenKey);
            params.addBodyParameter("oldface",oldface);  //token
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }



        String url = "https://bodylistener.amsu-new.com/intellingence/LoginController/phoneVerify"; //登陆
        httpUtils.send(HttpRequest.HttpMethod.POST, url,params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(TAG,"登陆onSuccess==result:"+result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"登陆onFailure==s:"+s);
            }
        });
        
    }
}
