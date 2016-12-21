package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.HealthyPlan;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.DateTimeDialogOnlyYMD;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class AddHeathyPlanActivity extends BaseActivity implements DateTimeDialogOnlyYMD.MyOnDateSetListener{

    private EditText et_addplan_title;
    private EditText et_addplan_content;
    private TextView tv_addplan_time;

    private static final String TAG = "AddHeathyPlanActivity";
    private DateTimeDialogOnlyYMD dateTimeDialogOnlyYMD;
    private int year;
    private int month;
    private int day;
    String time = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_healyh_plan);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("健康计划");
        setLeftImage(R.drawable.back_icon);
        setRightText("保存");
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        time = MyUtil.getFormatTime(new Date());

        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_addplan_title.getText().toString();
                String content = et_addplan_content.getText().toString();
                Log.i(TAG,"time:"+time);
                if (!MyUtil.isEmpty(title) && !MyUtil.isEmpty(content)){
                    final HealthyPlan healthyPlan = new HealthyPlan(title,content,time);
                    MyUtil.showDialog("正在上传",AddHeathyPlanActivity.this);
                    HttpUtils httpUtils = new HttpUtils();
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("title",title);
                    params.addBodyParameter("content",content);
                    params.addBodyParameter("date",time);
                    MyUtil.addCookieForHttp(params);

                    httpUtils.send(HttpRequest.HttpMethod.POST, Constant.setHealthyPlanURL, params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            MyUtil.hideDialog();
                            String result = responseInfo.result;
                            Log.i(TAG,"上传onSuccess==result:"+result);
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(result);
                                int ret = jsonObject.getInt("ret");
                                String errDesc = jsonObject.getString("errDesc");
                                if (ret==0){
                                    MyUtil.showToask(AddHeathyPlanActivity.this,"添加成功");
                                    healthyPlan.setId(errDesc);
                                    Intent intent = getIntent();
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("healthyPlan",healthyPlan);
                                    intent.putExtra("bundle",bundle);
                                    setResult(RESULT_OK,intent);
                                    finish();

                                }
                                else {
                                    MyUtil.showToask(AddHeathyPlanActivity.this,errDesc);
                                }
                            } catch (JSONException e) {
                            }


                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            MyUtil.hideDialog();
                            Log.i(TAG,"上传onFailure==s:"+s);
                        }
                    });
                }


            }
        });

        et_addplan_title = (EditText) findViewById(R.id.et_addplan_title);
        et_addplan_content = (EditText) findViewById(R.id.et_addplan_content);
        tv_addplan_time = (TextView) findViewById(R.id.tv_addplan_time);
        dateTimeDialogOnlyYMD = new DateTimeDialogOnlyYMD(this, this, true, true, true);

        tv_addplan_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeDialogOnlyYMD.hideOrShow();
            }
        });


    }

    @Override
    public void onDateSet(Date date) {
        year = date.getYear() + 1900;
        month = date.getMonth() + 1;
        day = date.getDate();

        Log.i(TAG,"onDateSet:"+ year +","+ month +","+ day);
        tv_addplan_time.setText(year +"-"+ month +"-"+ day);   //
        time = year +"-"+ month +"-"+ day;

    }
}
