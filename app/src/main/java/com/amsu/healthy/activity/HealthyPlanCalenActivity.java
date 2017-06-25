package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.HealthyPlan;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.MyCalendarView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HealthyPlanCalenActivity extends BaseActivity {

    private static final String TAG = "HealthyPlanCalenActivity";
    private MyCalendarView vl_healthycalen_calen;
    private TextView tv_plancalen_yearndmouth;
    private TextView tv_healthycalen_day;
    private List<HealthyPlan> mMonthHealthyPlanList;
    private TextView tv_healthycalen_title;
    private int[] planDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_plan_calen);

        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText("健康计划");
        setLeftImage(R.drawable.back_icon);
        setHeadBackgroudColor("#0c64b5");
        setRightImage(R.drawable.plan_list);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HealthyPlanCalenActivity.this,HealthyPlanActivity.class));
                finish();
            }
        });
        vl_healthycalen_calen = (MyCalendarView) findViewById(R.id.vl_healthycalen_calen);
        tv_plancalen_yearndmouth = (TextView) findViewById(R.id.tv_plancalen_yearndmouth);
        tv_healthycalen_title = (TextView) findViewById(R.id.tv_healthycalen_title);
        tv_healthycalen_day = (TextView) findViewById(R.id.tv_healthycalen_day);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //设置控件监听，可以监听到点击的每一天（大家也可以在控件中根据需求设定）
        vl_healthycalen_calen.setOnItemClickListener(new MyCalendarView.OnItemClickListener() {

            @Override
            public void OnItemClick(Date selectedStartDate, Date selectedEndDate, Date downDate) {
                if(vl_healthycalen_calen.isSelectMore()){

                    Log.i(TAG,"downDate:isSelectMore   "+simpleDateFormat.format(downDate));
                    //Toast.makeText(getApplicationContext(), format.format(selectedStartDate)+"到"+format.format(selectedEndDate), Toast.LENGTH_SHORT).show();
                }else{
                    Log.i(TAG,"downDate:"+simpleDateFormat.format(downDate));
                    //Toast.makeText(getApplicationContext(), format.format(downDate), Toast.LENGTH_SHORT).show();
                }
                tv_healthycalen_day.setText(downDate.getDate()+"");
                tv_healthycalen_title.setText("--");
                if (mMonthHealthyPlanList!=null && mMonthHealthyPlanList.size()>0){
                    for (HealthyPlan healthyPlan:mMonthHealthyPlanList){
                        /*String[] split = healthyPlan.getDate().split("-");
                        if (split[split.length-1].equals(downDate.getDate())){
                            tv_healthycalen_title.setText(healthyPlan.getTitle());
                        }*/
                        if (healthyPlan.getDate().equals(simpleDateFormat.format(downDate))){
                            tv_healthycalen_title.setText(healthyPlan.getTitle());
                        }
                    }
                }
            }
        });
        tv_healthycalen_day.setText(new Date().getDate()+"");

        vl_healthycalen_calen.setOnItemLongClickListener(new MyCalendarView.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(int dayInmonth) {
                Log.i(TAG,"dayInmonth:"+dayInmonth)     ;
                Date calendatData = vl_healthycalen_calen.getCalendatData();
                String formatDate = simpleDateFormat.format(calendatData);
                Log.i(TAG,"formatDate:"+ formatDate);


                boolean isNewAddPlan = true;
                /*String temp ="";
                if (planDays!=null){
                    for (int i:planDays){
                        temp += i+",";
                        if (dayInmonth==i){
                            isNewAddPlan = false;
                        }
                    }
                }
                 Log.i(TAG,"temp:"+temp);
                */

                String id = null;
                for (HealthyPlan healthyPlan:mMonthHealthyPlanList){
                    if (healthyPlan.getDate().equals(formatDate)){
                        isNewAddPlan = false;
                        id = healthyPlan.getId();
                    }
                }

                Log.i(TAG,"id:"+ id);
                if (isNewAddPlan){
                    Intent intent = new Intent(HealthyPlanCalenActivity.this, AddHeathyPlanActivity.class);
                    intent.putExtra("formatDate",formatDate);
                    startActivityForResult(intent,120);
                }
                else {
                    Intent intent = new Intent(HealthyPlanCalenActivity.this, LookupHealthPlanActivity.class);
                    if (!MyUtil.isEmpty(id)){
                        intent.putExtra("id",id);
                        startActivity(intent);
                    }
                }
            }
        });

    }

    private void initData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        Log.i(TAG,"year:"+year+",month:"+month);
        if (month<10){
            getHealthPlanData(year+"","0"+month+"");
        }
        else {
            getHealthPlanData(year+"",month+"");
        }
        tv_plancalen_yearndmouth.setText(year+"年"+month+"月");


    }

    private void getHealthPlanData(String year, String month) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("year",year);
        params.addBodyParameter("month",month);
        Log.i(TAG,"year:"+year+",month:"+month);
        MyUtil.addCookieForHttp(params);
        MyUtil.showDialog("加载数据",this);
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getHealthyPlanningMonthListURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog();
                String result = responseInfo.result;
                /*String result = "{\n" +
                        "\"ret\": \"0\",\n" +
                        "\"CountPage\": 2,   //总页数\n" +
                        "\"Page\": \"1\",  //当前请求页数\n" +
                        "\"errDesc\": [\n" +
                        "                {\n" +
                        "                \"id\": \"1\",\n" +
                        "                \"title\": \"健康计划1\",\n" +
                        "                \"date\": \"2017-05-25\",\n" +
                        "                \"content\": \"lasfl基本原则苛基本原则基本原则基本原则东奔西走基本原则基本原则茜\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                \"id\": \"5\",\n" +
                        "                \"title\": \"健康计划2\",\n" +
                        "                \"date\": \"2017-05-05\",\n" +
                        "                \"content\": \"lasfl基本原则苛基本原则基本原则基本原则东奔西走基本原则基本原则茜\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                \"id\": \"7\",\n" +
                        "                \"title\": \"健康计划3\",\n" +
                        "                \"date\": \"2017-05-21\",\n" +
                        "                \"content\": \"lasfl基本原则苛基本原则基本原则基本原则东奔西走基本原则基本原则茜\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                \"id\": \"8\",\n" +
                        "                \"title\": \"健康计划4\",\n" +
                        "                \"date\": \"2017-05-11\",\n" +
                        "                \"content\": \"lasfl基本原则苛基本原则基本原则基本原则东奔西走基本原则基本原则茜\"\n" +
                        "                }\n" +
                        "        ]\n" +
                        "}";
*/
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase.getRet()==0){
                    String errDesc = jsonBase.errDesc+"";
                    mMonthHealthyPlanList = gson.fromJson(errDesc, new TypeToken<List<HealthyPlan>>() {
                    }.getType());
                    updatePlanDaysList(mMonthHealthyPlanList);
                }
                else {
                    vl_healthycalen_calen.setPlanDays(new int[0]);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog();
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });

    }

    private void updatePlanDaysList(List<HealthyPlan> mMonthHealthyPlanList) {
        planDays = new int[mMonthHealthyPlanList.size()];
        int i=0;
        for (HealthyPlan healthyPlan: mMonthHealthyPlanList){
            String[] split = healthyPlan.getDate().split("-");
            planDays[i] = Integer.parseInt(split[split.length-1]);
            i++;
        }

        vl_healthycalen_calen.setPlanDays(planDays);
        Log.i(TAG, mMonthHealthyPlanList.toString());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==120 && resultCode==RESULT_OK){
            Bundle bundle = data.getBundleExtra("bundle");
            HealthyPlan healthyPlan = bundle.getParcelable("healthyPlan");
            mMonthHealthyPlanList.add(healthyPlan);
            updatePlanDaysList(mMonthHealthyPlanList);

            Log.i(TAG,"add");
        }
    }


    //点击上一月 同样返回年月
    public void preMouth(View view) {

        /*int[] planDays = {3,27};
        vl_healthycalen_calen.setPlanDays(planDays);*/

        tv_healthycalen_title.setText("--");
        String leftYearAndmonth = vl_healthycalen_calen.clickLeftMonth();
        String[] ya = leftYearAndmonth.split("-");
        tv_plancalen_yearndmouth.setText(ya[0]+"年"+ya[1]+"月");

        if (ya[1].length()==1){
            getHealthPlanData(ya[0],"0"+ya[1]);
        }
        else {
            getHealthPlanData(ya[0],ya[1]);
        }

    }

    //点击下一月
    public void nextMouth(View view) {

        tv_healthycalen_title.setText("--");
        String rightYearAndmonth = vl_healthycalen_calen.clickRightMonth();
        String[] ya = rightYearAndmonth.split("-");
        tv_plancalen_yearndmouth.setText(ya[0]+"年"+ya[1]+"月");

        if (ya[1].length()==1){
            getHealthPlanData(ya[0],"0"+ya[1]);
        }
        else {
            getHealthPlanData(ya[0],ya[1]);
        }

    }
}
