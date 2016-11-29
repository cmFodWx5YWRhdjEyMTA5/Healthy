package com.amsu.healthy.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;

import org.json.JSONException;
import org.json.JSONObject;

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
    private int MSG_TOAST_SUCCESS= 4;
    private int timeUpdate= 60;
    private Button bt_step3_getcode;

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

    }

    private void initData() {
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
                        myHandler.sendEmptyMessage(0);
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
            if (msg.what==0){
                //成功

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
            /*SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String sendTime = format.format(new Date());*/

           /* //产生6位验证码
            int round = (int) Math.round(Math.random() * (9999 - 1000) + 1000);
            verifycode = String.valueOf(round);
            Log.i(TAG,"验证码:"+ verifycode +"");  //"您的验证码为"+ verifycode +"，请及时验证！"*/

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

    }


}
