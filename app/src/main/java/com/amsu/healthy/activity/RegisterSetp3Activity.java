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
import com.amsu.healthy.bean.User;
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

public class RegisterSetp3Activity extends BaseActivity {

    private static final String TAG = "RegisterSetp3Activity";
    private EditText et_step3_phone;
    private EditText et_step3_code;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int MSG_UPDATE= 1;
    private int MSG_TOAST_FAIL= 3;
    private int MSG_TOAST_VERIFY= 0;
    private int MSG_TOAST_SUCCESS= 4;
    private int timeUpdate= 60;
    private Button bt_step3_getcode;
    private CheckBox cb_step3_isagree;
    private String phone;
    private String inputVerifycode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_setp3);

        initView();
        initData();
    }



    private void initView() {
        initHeadView();
        setCenterText("快速注册");
        setLeftImage(R.drawable.guanbi_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_step3_phone = (EditText) findViewById(R.id.et_step3_phone);
        et_step3_code = (EditText) findViewById(R.id.et_step3_code);
        bt_step3_getcode = (Button) findViewById(R.id.bt_step3_getcode);
        cb_step3_isagree = (CheckBox) findViewById(R.id.cb_step3_isagree);
        final Button bt_step_nextstep = (Button) findViewById(R.id.bt_step_nextstep);

        cb_step3_isagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_step3_isagree.isChecked()){
                    bt_step_nextstep.setBackgroundResource(R.drawable.bg_bt_rec);
                }
                else {
                    bt_step_nextstep.setBackgroundColor(Color.parseColor("#c8c8c8"));
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

    class MyTimerTask extends TimerTask{
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
                registerToDB();
            }
            else if (msg.what==MSG_UPDATE){
                int time = (int) msg.obj;
                if (time==0){
                    bt_step3_getcode.setText("获取验证码");
                    bt_step3_getcode.setClickable(true);
                    bt_step3_getcode.setTextSize(10);
                    bt_step3_getcode.setBackgroundResource(R.drawable.bg_button_verifycode);
                }

                else {
                    bt_step3_getcode.setText(time+"");
                }
            }
            else if (msg.what==MSG_TOAST_FAIL){
                String detail = (String) msg.obj;
                Toast.makeText(RegisterSetp3Activity.this,detail, Toast.LENGTH_SHORT).show();
                MyUtil.hideDialog();
            }
            else if (msg.what==MSG_TOAST_SUCCESS){
                bt_step3_getcode.setClickable(false);
                bt_step3_getcode.setBackgroundResource(R.drawable.bg_button_code_disable);
                bt_step3_getcode.setTextSize(15);
                bt_step3_getcode.setText(60+"");
                MyUtil.hideDialog();
                Toast.makeText(RegisterSetp3Activity.this,"验证码发送成功", Toast.LENGTH_SHORT).show();
                timeUpdate = 60;

                if (mTimerTask != null){
                    mTimerTask.cancel();  //将原任务从队列中移除
                }
                mTimerTask = new MyTimerTask();
                mTimer.schedule(mTimerTask, 1000, 1000);
            }
        }
    };



    public void getVerifyCode(View view) {
        String phone = et_step3_phone.getText().toString();
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

    public void finshRigister(View view) {
        phone = et_step3_phone.getText().toString();
        inputVerifycode = et_step3_code.getText().toString();

        if (phone.isEmpty()){
            Toast.makeText(this,"请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(inputVerifycode.isEmpty()){
            Toast.makeText(this,"请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(inputVerifycode.isEmpty()){
            Toast.makeText(this,"请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        /*else {
            //先将短信提交到shareSDK进行短信验证
            SMSSDK.submitVerificationCode("86", phone, inputVerifycode);
            MyUtil.showDialog("正在校验",this);
        }*/

        registerToDB();   //测试


    }

    private void registerToDB() {
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

                //登陆成功，然后上传个人信息
                Intent intent = getIntent();
                final String username = intent.getStringExtra("username");
                final String birthday = intent.getStringExtra("birthday");
                final String sex = intent.getStringExtra("sex");
                final String weightValue = intent.getStringExtra("weightValue");
                final String heightValue = intent.getStringExtra("heightValue");
                final String area = intent.getStringExtra("area");
                final String email = "";

                HttpUtils httpUtils = new HttpUtils();

                RequestParams params = new RequestParams();
                params.addBodyParameter("UserName",username);
                params.addBodyParameter("Birthday",birthday);
                params.addBodyParameter("Sex",sex);
                params.addBodyParameter("Weight",weightValue);
                params.addBodyParameter("Height",heightValue);
                params.addBodyParameter("area",area);
                params.addBodyParameter("Phone",phone);
                params.addBodyParameter("Email",email);

                String url = "https://bodylistener.amsu-new.com/intellingence/UserinfoController/uploadUserinfo"; //上传个人信息
                httpUtils.send(HttpRequest.HttpMethod.POST, url,params, new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.i(TAG,"上传onSuccess==result:"+result);
                        //跳转


                        //保存用户信息
                        User user = new User(phone,username,birthday,sex,weightValue,heightValue,area,email);
                        MyUtil.saveUserToSP(user);


                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Log.i(TAG,"上传onFailure==s:"+s);
                    }
                });

            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"登陆onFailure==s:"+s);
            }
        });
    }



}
