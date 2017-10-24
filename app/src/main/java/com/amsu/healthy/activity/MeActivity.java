package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.scancode.activity.CaptureActivity;
import com.amsu.healthy.view.CircleImageView;
import com.lidroid.xutils.BitmapUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 我的
 */
public class MeActivity extends BaseActivity {

    private static final String TAG = "MeActivity";
    private TextView tv_me_name;
    private TextView tv_me_city;
    private TextView tv_me_age;
    private CircleImageView iv_me_headicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        initView();

        initData();


    }



    private void initView() {
        initHeadView();

        ImageView iv_me_back = (ImageView) findViewById(R.id.iv_me_back);
        iv_me_headicon = (CircleImageView) findViewById(R.id.iv_me_headicon);

        RelativeLayout rl_me_historyrecord = (RelativeLayout) findViewById(R.id.rl_me_historyrecord);
        RelativeLayout rl_me_report = (RelativeLayout) findViewById(R.id.rl_me_report);
        RelativeLayout rl_me_healthplan = (RelativeLayout) findViewById(R.id.rl_me_healthplan);
        RelativeLayout rl_me_follow = (RelativeLayout) findViewById(R.id.rl_me_follow);
        RelativeLayout rl_me_help = (RelativeLayout) findViewById(R.id.rl_me_help);
        RelativeLayout rl_me_setting = (RelativeLayout) findViewById(R.id.rl_me_setting);
        RelativeLayout rl_me_scan = (RelativeLayout) findViewById(R.id.rl_me_scan);

        tv_me_name = (TextView) findViewById(R.id.tv_me_name);
        tv_me_city = (TextView) findViewById(R.id.tv_me_city);
        tv_me_age = (TextView) findViewById(R.id.tv_me_age);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        iv_me_back.setOnClickListener(myOnClickListener);
        iv_me_headicon.setOnClickListener(myOnClickListener);
        rl_me_historyrecord.setOnClickListener(myOnClickListener);
        rl_me_report.setOnClickListener(myOnClickListener);
        rl_me_healthplan.setOnClickListener(myOnClickListener);
        rl_me_follow.setOnClickListener(myOnClickListener);
        rl_me_help.setOnClickListener(myOnClickListener);
        rl_me_setting.setOnClickListener(myOnClickListener);
        rl_me_scan.setOnClickListener(myOnClickListener);

        if (!MyApplication.mActivities.contains(this)){
            MyApplication.mActivities.add(this);
        }
    }


    private void initData() {
        User userFromSP = MyUtil.getUserFromSP();
        if (userFromSP!=null){
            tv_me_name.setText(userFromSP.getUsername());

            String area = userFromSP.getArea();  //广东省深圳市
            if (!area.equals("") && area.contains("省")){
                String[] areas = area.split("省");
                tv_me_city.setText(areas[1]);
            }

            int userAge = HealthyIndexUtil.getUserAge();

            tv_me_age.setText(userAge+"");

            String iconUrl = userFromSP.getIcon();
            if (!iconUrl.equals("")){
                if (iconUrl.endsWith("jpg") || iconUrl.endsWith("png") || iconUrl.endsWith("jpeg") || iconUrl.endsWith("gif")){
                    BitmapUtils bitmapUtils = new BitmapUtils(this);
                    bitmapUtils.display(iv_me_headicon,iconUrl);
                }
            }
        }
    }

    /*public void test(View view) {
        HttpUtils httpUtils1 = new HttpUtils();
        //httpUtils1.configCookieStore(MyApplication.cookieStore);  //配置Cookie
        RequestParams params = new RequestParams();
        MyUtil.addCookieForHttp(params);

        httpUtils1.send(HttpRequest.HttpMethod.POST, Constant.downloadPersionDataURL,params, new RequestCallBack<String>() {
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
                        String userName = jsonObject1.getString("UserName");
                        if (userName.equals("")){
                            //没有完善个人信息

                        }
                        else {

                        }
                    }
                    else {
                        //操作失败
                        //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });

    }*/

    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_me_back:
                    //返回
                    finish();
                    break;
                case R.id.iv_me_headicon:
                    dumpToPersionData();
                    break;
                case R.id.rl_me_historyrecord:
                    startActivity(new Intent(MeActivity.this,HistoryRecordAllActivity.class));
                    break;
                case R.id.rl_me_report:
                    startActivity(new Intent(MeActivity.this,MyReportActivity.class));
                    break;
                case R.id.rl_me_healthplan:
                    startActivity(new Intent(MeActivity.this,HealthyPlanActivity.class));
                    break;
                case R.id.rl_me_follow:
                    startActivity(new Intent(MeActivity.this,MotionDetectionActivity.class));
                    break;
                case R.id.rl_me_help:
                    startActivity(new Intent(MeActivity.this,SosActivity.class));
                    break;
                case R.id.rl_me_setting:
                    startActivity(new Intent(MeActivity.this,SystemSettingActivity.class));
                    break;
                case R.id.rl_me_scan:
                    //startActivity(new Intent(MeActivity.this,QRCodeActivity.class));
                    startActivityForResult(new Intent(MeActivity.this, CaptureActivity.class),0);
                    break;
            }
        }
    }

    private void dumpToPersionData() {
        boolean isLogin = MyUtil.getBooleanValueFromSP("isLogin");
        if (isLogin){
            startActivityForResult(new Intent(MeActivity.this,PersionDataActivity.class),140);
        }
        else {
            startActivity(new Intent(MeActivity.this,LoginActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && requestCode==0){
            //alertDialog.setCanceledOnTouchOutside(false);

            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result=bundle.getString("result");
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String key = (String) jsonObject.get("key");
                    String url = (String) jsonObject.get("url");
                    Log.i(TAG,"key:"+key);
                    Log.i(TAG,"url:"+url);

                    if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(url)){
                        Intent intent = new Intent(MeActivity.this,ConfirmScanLoginActivity.class);
                        intent.putExtra("key",key);
                        intent.putExtra("url",url);
                        startActivity(intent);

                        MyUtil.showToask(MeActivity.this,"扫描成功");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    MyUtil.showToask(MeActivity.this,"扫描失败，请对准手表上的二维码");
                }
            }
        }
        else if (resultCode==RESULT_OK && requestCode==140){
            User userFromSP = MyUtil.getUserFromSP();
            if (userFromSP!=null){
                tv_me_name.setText(userFromSP.getUsername());

                String area = userFromSP.getArea();  //广东省深圳市
                if (!area.equals("") && area.contains("省")){
                    String[] areas = area.split("省");
                    tv_me_city.setText(areas[1]);
                }

                int userAge = HealthyIndexUtil.getUserAge();

                tv_me_age.setText(userAge+"");

                String iconUrl = userFromSP.getIcon();
                if (!iconUrl.equals("")){
                    if (iconUrl.endsWith("jpg") || iconUrl.endsWith("png") || iconUrl.endsWith("jpeg") || iconUrl.endsWith("gif")){
                        BitmapUtils bitmapUtils = new BitmapUtils(this);
                        bitmapUtils.display(iv_me_headicon,iconUrl);
                    }
                }
            }
        }
    }
}
