package com.amsu.healthy.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
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
import com.umeng.analytics.MobclickAgent;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.List;
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
    private boolean isAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.login));
        setLeftImage(R.drawable.guanbi_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtil.destoryAllAvtivity();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
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
                    isAgree = true;
                }
                else {
                    bt_login_nextstep.setBackgroundColor(Color.parseColor("#c8c8c8"));
                    isAgree = false;
                }
            }
        });

        MyApplication.mActivities.add(this);

        /*//监听软键盘的删除键
        et_login_phone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    num++;
                    //在这里加判断的原因是点击一次软键盘的删除键,会触发两次回调事件
                    if (num % 2 != 0) {
                        String s = et_login_phone.getText().toString();
                        if (!TextUtils.isEmpty(s)) {
                            et_login_phone.setText("" + s.substring(0, s.length() - 1));
                            //将光标移到最后
                            et_login_phone.setSelection(et_login_phone.getText().length());
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        //监听软键盘的删除键
        et_login_code.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    num_code++;
                    //在这里加判断的原因是点击一次软键盘的删除键,会触发两次回调事件
                    if (num_code % 2 != 0) {
                        String s = et_login_phone.getText().toString();
                        if (!TextUtils.isEmpty(s)) {
                            et_login_phone.setText("" + s.substring(0, s.length() - 1));
                            //将光标移到最后
                            et_login_phone.setSelection(et_login_phone.getText().length());
                        }
                    }
                    return true;
                }
                return false;
            }
        });*/
    }

    private int num = 0;
    private int num_code = 0;

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
                    MyUtil.hideDialog(LoginActivity.this);
                    String[] split = data.toString().split(":");
                    String detail ="";
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
        if (!isAgree){
            MyUtil.showToask(this,getResources().getString(R.string.non_consent_logon_protocol));
            return;
        }
        String phone = et_login_phone.getText().toString();
        String inputVerifycode = et_login_code.getText().toString();

        if (phone.isEmpty()){
            Toast.makeText(this,getResources().getString(R.string.enter_cell_phone_number), Toast.LENGTH_SHORT).show();
            return;
        }
        else if(inputVerifycode.isEmpty()){
            Toast.makeText(this,getResources().getString(R.string.input_validation_code), Toast.LENGTH_SHORT).show();
            return;
        }

        validateLogin(phone,inputVerifycode);
    }

    public void getVerifyCode(View view) {
        String phone = et_login_phone.getText().toString();
        if(!TextUtils.isEmpty(phone)){
            getMESCode("86",phone);  //86为国家代码
            MyUtil.showDialog(getResources().getString(R.string.getting_validation_code),this);
        }else{
            Toast.makeText(this,getResources().getString(R.string.input_validation_code), Toast.LENGTH_SHORT).show();
        }
    }

    public void getMESCode(String country, final String phone) {
        SMSSDK.getVerificationCode(country, phone);
    }

    public void lookAsset(View view) {
        startActivity(new Intent(this,DisclaimerAssertsActivity.class));
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
                    bt_login_getcode.setText(getResources().getString(R.string.code));
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
                MyUtil.hideDialog(LoginActivity.this);
            }
            else if (msg.what==MSG_TOAST_SUCCESS){
                bt_login_getcode.setClickable(false);
                bt_login_getcode.setBackgroundResource(R.drawable.bg_button_code_disable);
                bt_login_getcode.setTextSize(15);
                bt_login_getcode.setText(60+"");
                MyUtil.hideDialog(LoginActivity.this);
                Toast.makeText(LoginActivity.this,getResources().getString(R.string.verify_code_sent_successfully), Toast.LENGTH_SHORT).show();
                timeUpdate = 60;

                if (mTimerTask != null){
                    mTimerTask.cancel();  //将原任务从队列中移除
                }
                mTimerTask = new MyTimerTask();
                mTimer.schedule(mTimerTask, 1000, 1000);
            }
        }
    };

    private void validateLogin(final String phone, String inputVerifycode) {
        MyUtil.showDialog(getResources().getString(R.string.login),this);
        final HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();

        params.addBodyParameter("phone",phone);
        String param = String.valueOf(System.currentTimeMillis());
        params.addBodyParameter("param", param);  //时间戳

        params.addBodyParameter("zone","86");  //区号
        params.addBodyParameter("code",inputVerifycode);  //验证码
        params.addBodyParameter("mobtype","1");

        Log.i(TAG,"phone:"+phone+",param:"+param+",inputVerifycode:"+inputVerifycode);
        String oldface = "";
        try {
            oldface = MD5Util.getMD5(phone + param + Constant.tokenKey);
            params.addBodyParameter("oldface",oldface);  //token
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String url = "https://bodylistener.amsu-new.com/intellingence/LoginController/phoneVerify"; //登陆
        //String url = "http://192.168.0.107:8080/intellingence-web/phoneVerify.do"; //登陆
        httpUtils.send(HttpRequest.HttpMethod.POST, url,params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                String result = responseInfo.result;
                Log.i(TAG,"登陆onSuccess==result:"+result);
                //{"ret":"0","errDesc":"注册成功!"}
                //{"ret":"-468","errDesc":"验证码错误"}
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int ret = jsonObject.getInt("ret");
                    String errDesc = jsonObject.getString("errDesc");
                    //ret = 0;
                    //errDesc = "注册成功";
                    //MyUtil.showToask(LoginActivity.this,errDesc);
                    if (ret==0){
                        //注册成功
                        MyUtil.putBooleanValueFromSP("isLogin",true);
                        MyUtil.putStringValueFromSP("phone",phone);

                        saveCookieToSP(httpUtils);
                        
                        getUserBindInsole();

                        HttpUtils httpUtils1 = new HttpUtils();
                        RequestParams params = new RequestParams();
                        MyUtil.addCookieForHttp(params);

                        httpUtils1.send(HttpRequest.HttpMethod.POST, Constant.downloadPersionDataURL,params, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                String result = responseInfo.result;
                                Log.i(TAG,"result:"+result);
                                /*
                                * {
                                    "ret": "0",
                                    "errDesc": {
                                        "UserName": "张敏",
                                        "Sex": "1",
                                        "Birthday": "2016-12-07",
                                        "Weight": "36.1",
                                        "Height": "78.1",
                                        "Address": "广东省深圳市",
                                        "Phone": "15467893247",
                                        "Email": "222@c.com",
                                        "Icon": "http://xxxxx.com:83/xxx/xxx.jpg"    //例子
                                    }
                                }

                                {
                                    "ret": "0",
                                    "errDesc": {
                                        "UserName": "手机用户_ZUzf1",
                                        "Sex": null,
                                        "Birthday": null,
                                        "Weight": null,
                                        "Height": null,
                                        "Address": "",
                                        "Phone": "15321758382",
                                        "Email": "",
                                        "Icon": "http://119.29.201.120:83/"
                                    }
                                }
                                * */
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    int ret = jsonObject.getInt("ret");
                                    String errDesc = jsonObject.getString("errDesc");
                                    if (ret==0){
                                        JSONObject jsonObject1 = new JSONObject(errDesc);
                                        String birthday = jsonObject1.getString("Birthday");
                                        String weight = jsonObject1.getString("Weight");
                                        String userName = jsonObject1.getString("UserName");
                                        if (birthday.equals("null") && weight.equals("null")){
                                            //没有完善个人信息
                                            //showdialog();
                                            MyUtil.hideDialog(LoginActivity.this);
                                            MyUtil.destoryAllAvtivity();
                                            startActivity(new Intent(LoginActivity.this,SupplyPersionDataActivity.class));
                                        }
                                        else {
                                            MobclickAgent.onProfileSignIn(phone);  //友盟登陆账号统计

                                            String sex = jsonObject1.getString("Sex");
                                            String height = jsonObject1.getString("Height");
                                            String address = jsonObject1.getString("Address");
                                            String email = jsonObject1.getString("Email");
                                            String icon = jsonObject1.getString("Icon");
                                            String stillRate = jsonObject1.getString("RestingHeartRate");
                                            User user = new User(phone,userName,birthday,sex,weight,height,address,email,icon,stillRate);
                                            MyUtil.saveUserToSP(user);
                                            MyUtil.putBooleanValueFromSP("isPrefectInfo",true);
                                            SplashActivity.downlaodWeekReport(-1,-1,true,LoginActivity.this);
                                            MyUtil.showDialog(getResources().getString(R.string.login_successful_synchronizing_data),LoginActivity.this);
                                            //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                        }
                                    }
                                    else {
                                        //操作失败
                                        MyUtil.hideDialog(LoginActivity.this);
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                        MyUtil.destoryAllAvtivity();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    MyUtil.hideDialog(LoginActivity.this);
                                }
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                MyUtil.hideDialog(LoginActivity.this);
                            }
                        });
                    }
                    else {
                        MyUtil.hideDialog(LoginActivity.this);
                        MyUtil.showToask(LoginActivity.this,errDesc);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MyUtil.hideDialog(LoginActivity.this);
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog(LoginActivity.this);
                Log.i(TAG,"登陆onFailure==s:"+s);
                MyUtil.showToask(LoginActivity.this,getResources().getString(R.string.network_exception));
            }
        });
        
    }

    private void getUserBindInsole() {
        HttpUtils httpUtils1 = new HttpUtils();
        RequestParams params = new RequestParams();
        MyUtil.addCookieForHttp(params);

        httpUtils1.send(HttpRequest.HttpMethod.POST, Constant.getBangdingDetails,params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(TAG,"result:"+result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int ret = jsonObject.getInt("ret");
                    String errDesc = jsonObject.getString("errDesc");
                    if (ret==0){
                        JSONObject jsonObject1 = new JSONObject(errDesc);
                        String leftDeviceMAC = jsonObject1.getString("leftdevicemac");
                        String rightDeviceMAC = jsonObject1.getString("rightdevicemac");
                        Device device = new Device();
                        device.setMac(leftDeviceMAC+","+rightDeviceMAC);
                        device.setLEName("：鞋垫1("+leftDeviceMAC.substring(leftDeviceMAC.length()-2)+
                                ")+鞋垫2("+rightDeviceMAC.substring(rightDeviceMAC.length()-2)+")");
                        device.setName("鞋垫");
                        device.setDeviceType(Constant.sportType_Insole);
                        MyUtil.saveDeviceToSP(device,Constant.sportType_Insole);
                        Log.i(TAG,"device:"+device);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"onFailure:"+s);
            }
        });
    }

    private void saveCookieToSP(HttpUtils httpUtils) {
        /*ci_session,94efd294d41da5b13b0740f476a93950df0c4159
        uid,18689463192
        id,9
        token,66-7550512929-106-27-7353-46-269-33-31-109
        userParam,1481855331071*/

        //保存Cookie
        DefaultHttpClient httpClient = (DefaultHttpClient) httpUtils.getHttpClient();
        CookieStore cookieStore = httpClient.getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cookies.size(); i++) {
            String name = cookies.get(i).getName();
            String value = cookies.get(i).getValue();
            //Log.i(TAG,"cookies:"+ name +","+ value);
            if (!MyUtil.isEmpty(name) && !MyUtil.isEmpty(value)){
                sb.append(name + "=" );
                sb.append(value + ";" );
            }
        }
        Log. i("Cookie", sb.toString());
        MyUtil.putStringValueFromSP("Cookie", sb.toString());
    }

    public void showdialog(){
        new AlertDialog.Builder(LoginActivity.this).setTitle(getResources().getString(R.string.login_was_successful))
                .setMessage(getResources().getString(R.string.now_improve_data))
                .setPositiveButton(getResources().getString(R.string.exit_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.exit_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(LoginActivity.this,SupplyPersionDataActivity.class));
                        finish();
                    }
                })
                .show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            MyUtil.destoryAllAvtivity();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyUtil.setDialogNull();

    }
}
