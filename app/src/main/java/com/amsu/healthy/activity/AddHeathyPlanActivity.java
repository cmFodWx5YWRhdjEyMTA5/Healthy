package com.amsu.healthy.activity;

import android.content.Intent;
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
    private HealthyPlan mHealthyPlan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_healyh_plan);

        initView();

        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.new_health_plan));
        setLeftImage(R.drawable.back_icon);
        setRightText(getResources().getString(R.string.save));
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_addplan_title = (EditText) findViewById(R.id.et_addplan_title);
        et_addplan_content = (EditText) findViewById(R.id.et_addplan_content);
        tv_addplan_time = (TextView) findViewById(R.id.tv_addplan_time);
        dateTimeDialogOnlyYMD = new DateTimeDialogOnlyYMD(this, this, true, true, true);

        tv_addplan_time.setText(MyUtil.getPaceFormatTime(new Date()));
        tv_addplan_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeDialogOnlyYMD.hideOrShow();
            }
        });


        time = MyUtil.getPaceFormatTime(new Date());

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

                    String url = Constant.setHealthyPlanURL;
                    if (mHealthyPlan!=null){
                        //修改数据
                        url = Constant.modifyHealthyPlanURL;
                        params.addBodyParameter("id",mHealthyPlan.getId());
                    }

                    httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            MyUtil.hideDialog(AddHeathyPlanActivity.this);
                            String result = responseInfo.result;
                            Log.i(TAG,"上传onSuccess==result:"+result);
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(result);
                                int ret = jsonObject.getInt("ret");
                                String errDesc = jsonObject.getString("errDesc");
                                if (ret==0){
                                    MyUtil.showToask(AddHeathyPlanActivity.this,"成功");
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
                            MyUtil.hideDialog(AddHeathyPlanActivity.this);
                            Log.i(TAG,"上传onFailure==s:"+s);
                        }
                    });
                }
                else {
                    MyUtil.showToask(AddHeathyPlanActivity.this,"输入标题和内容");
                }


            }
        });



    }


    private void initData() {
        Intent intent = getIntent();
        if (intent!=null){
            Bundle bundle = intent.getBundleExtra("bundle");
            String formatDate = intent.getStringExtra("formatDate");
            if (bundle!=null){
                mHealthyPlan = bundle.getParcelable("mHealthyPlan");
                if (mHealthyPlan!=null){
                    et_addplan_title.setText(mHealthyPlan.getTitle());
                    et_addplan_content.setText(mHealthyPlan.getContent());
                    tv_addplan_time.setText(mHealthyPlan.getDate());
                }
            }
            if (!MyUtil.isEmpty(formatDate)){
                tv_addplan_time.setText(formatDate);
            }
        }
    }

    @Override
    public void onDateSet(Date date) {
        year = date.getYear() + 1900;
        month = date.getMonth() + 1;
        day = date.getDate();

        Log.i(TAG,"onDateSet:"+ year +","+ month +","+ day);
        tv_addplan_time.setText(year +"-"+ month +"-"+ day);   //
        time = year +"-"+ month +"-"+ day;
        if (month<10){
            time = year+"-0"+month+"-"+day;
        }
    }
}
