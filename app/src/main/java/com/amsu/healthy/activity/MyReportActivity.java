package com.amsu.healthy.activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.Apk;
import com.amsu.healthy.bean.FullReport;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.fragment.report.MouthReprtFragment;
import com.amsu.healthy.fragment.report.QuarterReprtFragment;
import com.amsu.healthy.fragment.report.YearReprtFragment;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.Date;

public class MyReportActivity extends BaseActivity {

    private static final String TAG = "MyReportActivity";
    private FragmentTransaction fragmentTransaction;
    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private View v_analysis_select;
    private TextView tv_report_mouth;
    private TextView tv_report_quarter;
    private TextView tv_report_year;
    private MouthReprtFragment mouthReprtFragment;
    private QuarterReprtFragment quarterReprtFragment;
    private YearReprtFragment yearReprtFragment;
    public static FullReport mMouthFullReport;
    public static FullReport mQuarterFullReport;
    public static FullReport mYearFullReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report);

        initView();
        iniData();

    }

    private void initView() {
        initHeadView();
        setCenterText("我的报告");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setRightText("历史纪录");
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyReportActivity.this,HistoryRecordActivity.class));
            }
        });


        tv_report_mouth = (TextView) findViewById(R.id.tv_report_mouth);
        tv_report_quarter = (TextView) findViewById(R.id.tv_report_quarter);
        tv_report_year = (TextView) findViewById(R.id.tv_report_year);
        v_analysis_select = (View) findViewById(R.id.v_analysis_select);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        tv_report_mouth.setOnClickListener(myOnClickListener);
        tv_report_quarter.setOnClickListener(myOnClickListener);
        tv_report_year.setOnClickListener(myOnClickListener);


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        mouthReprtFragment = new MouthReprtFragment();
        quarterReprtFragment = new QuarterReprtFragment();
        yearReprtFragment = new YearReprtFragment();



    }

    private void iniData() {

        loadMouthFullReportData();
        loadQuarterFullReportData();
        loadYearFullReportData();
    }

    private void loadMouthFullReportData() {
        MyUtil.showDialog("正在加载",this);
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        String formatTime = MyUtil.getSpecialFormatTime("yyyy-MM",new Date());
        params.addBodyParameter("reportType","FULL");
        params.addBodyParameter("reportTime",formatTime);
        MyUtil.addCookieForHttp(params);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.downloadMonthReportURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog();
                String result = responseInfo.result;
                /*result = "{\n" +
                        "    \"ret\": \"0\",\n" +
                        "    \"errDesc\": {\n" +
                        "        \"HRrep\": [\n" +
                        "            {\n" +
                        "                \"id\": \"157\",\n" +
                        "                \"datatime\": \"2016-10-25 12:49:24\",\n" +
                        "                \"AHR\": \"79\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"158\",\n" +
                        "                \"datatime\": \"2016-10-25 08:16:04\",\n" +
                        "                \"AHR\": \"79\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"159\",\n" +
                        "                \"datatime\": \"2016-10-27 12:02:44\",\n" +
                        "                \"AHR\": \"79\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"160\",\n" +
                        "                \"datatime\": \"2016-10-26 12:56:04\",\n" +
                        "                \"AHR\": \"79\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"ECrep\": [\n" +
                        "            0,\n" +
                        "            0,\n" +
                        "            0,\n" +
                        "            100\n" +
                        "        ],\n" +
                        "        \"HRRrep\": [\n" +
                        "            {\n" +
                        "                \"id\": \"157\",\n" +
                        "                \"datatime\": \"2016-10-25 12:49:24\",\n" +
                        "                \"RA\": \"96\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"158\",\n" +
                        "                \"datatime\": \"2016-10-25 08:16:04\",\n" +
                        "                \"RA\": \"96\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"159\",\n" +
                        "                \"datatime\": \"2016-10-27 12:02:44\",\n" +
                        "                \"RA\": \"96\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"160\",\n" +
                        "                \"datatime\": \"2016-10-26 12:56:04\",\n" +
                        "                \"RA\": \"96\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"HRVrep\": [\n" +
                        "            {\n" +
                        "                \"id\": \"157\",\n" +
                        "                \"datatime\": \"2016-10-25 12:49:24\",\n" +
                        "                \"FI\": \"30\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"158\",\n" +
                        "                \"datatime\": \"2016-10-25 08:16:04\",\n" +
                        "                \"FI\": \"30\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"159\",\n" +
                        "                \"datatime\": \"2016-10-27 12:02:44\",\n" +
                        "                \"FI\": \"30\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"160\",\n" +
                        "                \"datatime\": \"2016-10-26 12:56:04\",\n" +
                        "                \"FI\": \"30\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"HRlist\": [\n" +
                        "            [\n" +
                        "                \"2017-3-22\",\n" +
                        "                \"78\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"2017-3-23\",\n" +
                        "                \"82\"\n" +
                        "            ]\n" +
                        "        ]\n" +
                        "    }\n" +
                        "}";*/
                Log.i(TAG,"上传onSuccess==result:"+result);

                loadDataSucces();
                JsonBase<FullReport> jsonBase =  MyUtil.commonJsonParse(result,new TypeToken<JsonBase<FullReport>>() {}.getType());
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase!=null&&jsonBase.getRet()==0){
                    mMouthFullReport = jsonBase.errDesc;
                    Log.i(TAG,"mMouthFullReport:"+mMouthFullReport.toString());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog();
                loadDataSucces();
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }

    private void loadDataSucces() {
        fragmentTransaction.add(R.id.fragment_content, mouthReprtFragment).commit();
        currentFragment = mouthReprtFragment;
    }

    private void loadQuarterFullReportData() {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("reportType","FULL");
        params.addBodyParameter("quarter", String.valueOf(MyUtil.getCurrentQuertar()));
        params.addBodyParameter("year",String.valueOf(MyUtil.getCurrentYear()));
        MyUtil.addCookieForHttp(params);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.downloadQuarterReportURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                /*result = "{\n" +
                        "    \"ret\": \"0\",\n" +
                        "    \"errDesc\": {\n" +
                        "        \"HRrep\": [\n" +
                        "            {\n" +
                        "                \"id\": \"157\",\n" +
                        "                \"datatime\": \"2016-10-25 12:49:24\",\n" +
                        "                \"AHR\": \"79\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"158\",\n" +
                        "                \"datatime\": \"2016-10-25 08:16:04\",\n" +
                        "                \"AHR\": \"79\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"159\",\n" +
                        "                \"datatime\": \"2016-10-27 12:02:44\",\n" +
                        "                \"AHR\": \"79\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"160\",\n" +
                        "                \"datatime\": \"2016-10-26 12:56:04\",\n" +
                        "                \"AHR\": \"79\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"ECrep\": [\n" +
                        "            0,\n" +
                        "            0,\n" +
                        "            0,\n" +
                        "            100\n" +
                        "        ],\n" +
                        "        \"HRRrep\": [\n" +
                        "            {\n" +
                        "                \"id\": \"157\",\n" +
                        "                \"datatime\": \"2016-10-25 12:49:24\",\n" +
                        "                \"RA\": \"96\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"158\",\n" +
                        "                \"datatime\": \"2016-10-25 08:16:04\",\n" +
                        "                \"RA\": \"96\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"159\",\n" +
                        "                \"datatime\": \"2016-10-27 12:02:44\",\n" +
                        "                \"RA\": \"96\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"160\",\n" +
                        "                \"datatime\": \"2016-10-26 12:56:04\",\n" +
                        "                \"RA\": \"96\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"HRVrep\": [\n" +
                        "            {\n" +
                        "                \"id\": \"157\",\n" +
                        "                \"datatime\": \"2016-10-25 12:49:24\",\n" +
                        "                \"FI\": \"30\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"158\",\n" +
                        "                \"datatime\": \"2016-10-25 08:16:04\",\n" +
                        "                \"FI\": \"30\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"159\",\n" +
                        "                \"datatime\": \"2016-10-27 12:02:44\",\n" +
                        "                \"FI\": \"30\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"160\",\n" +
                        "                \"datatime\": \"2016-10-26 12:56:04\",\n" +
                        "                \"FI\": \"30\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"HRlist\": [\n" +
                        "            [\n" +
                        "                \"2017-3-22\",\n" +
                        "                \"78\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"2017-3-23\",\n" +
                        "                \"82\"\n" +
                        "            ]\n" +
                        "        ]\n" +
                        "    }\n" +
                        "}";*/

                JsonBase<FullReport> jsonBase =  MyUtil.commonJsonParse(result,new TypeToken<JsonBase<FullReport>>() {}.getType());
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase!=null&&jsonBase.getRet()==0){
                    mQuarterFullReport = jsonBase.errDesc;
                    Log.i(TAG,"mQuarterFullReport:"+mQuarterFullReport.toString());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }

    private void loadYearFullReportData() {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("reportType","FULL");
        params.addBodyParameter("year",String.valueOf(MyUtil.getCurrentYear()));
        MyUtil.addCookieForHttp(params);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.downloadYearReportURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);

                /*result = "{\n" +
                        "    \"ret\": \"0\",\n" +
                        "    \"errDesc\": {\n" +
                        "        \"HRrep\": [\n" +
                        "            {\n" +
                        "                \"id\": \"157\",\n" +
                        "                \"datatime\": \"2016-10-25 12:49:24\",\n" +
                        "                \"AHR\": \"79\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"158\",\n" +
                        "                \"datatime\": \"2016-10-25 08:16:04\",\n" +
                        "                \"AHR\": \"79\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"159\",\n" +
                        "                \"datatime\": \"2016-10-27 12:02:44\",\n" +
                        "                \"AHR\": \"79\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"160\",\n" +
                        "                \"datatime\": \"2016-10-26 12:56:04\",\n" +
                        "                \"AHR\": \"79\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"ECrep\": [\n" +
                        "            0,\n" +
                        "            0,\n" +
                        "            0,\n" +
                        "            100\n" +
                        "        ],\n" +
                        "        \"HRRrep\": [\n" +
                        "            {\n" +
                        "                \"id\": \"157\",\n" +
                        "                \"datatime\": \"2016-10-25 12:49:24\",\n" +
                        "                \"RA\": \"96\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"158\",\n" +
                        "                \"datatime\": \"2016-10-25 08:16:04\",\n" +
                        "                \"RA\": \"96\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"159\",\n" +
                        "                \"datatime\": \"2016-10-27 12:02:44\",\n" +
                        "                \"RA\": \"96\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"160\",\n" +
                        "                \"datatime\": \"2016-10-26 12:56:04\",\n" +
                        "                \"RA\": \"96\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"HRVrep\": [\n" +
                        "            {\n" +
                        "                \"id\": \"157\",\n" +
                        "                \"datatime\": \"2016-10-25 12:49:24\",\n" +
                        "                \"FI\": \"30\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"158\",\n" +
                        "                \"datatime\": \"2016-10-25 08:16:04\",\n" +
                        "                \"FI\": \"30\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"159\",\n" +
                        "                \"datatime\": \"2016-10-27 12:02:44\",\n" +
                        "                \"FI\": \"30\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"id\": \"160\",\n" +
                        "                \"datatime\": \"2016-10-26 12:56:04\",\n" +
                        "                \"FI\": \"30\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"HRlist\": [\n" +
                        "            [\n" +
                        "                \"2017-3-22\",\n" +
                        "                \"78\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"2017-3-23\",\n" +
                        "                \"82\"\n" +
                        "            ]\n" +
                        "        ]\n" +
                        "    }\n" +
                        "}";
*/
                JsonBase<FullReport> jsonBase =  MyUtil.commonJsonParse(result,new TypeToken<JsonBase<FullReport>>() {}.getType());
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase!=null&&jsonBase.getRet()==0){
                    mYearFullReport = jsonBase.errDesc;
                    Log.i(TAG,"mYearFullReport:"+mYearFullReport.toString());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }

    //Fragment切换
    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        if (currentFragment == fragment)
            return;
        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragment).add(R.id.fragment_content, fragment).commit();
        } else {
            transaction.hide(currentFragment).show(fragment).commit();
        }
        currentFragment = fragment;
    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            fragmentTransaction = fragmentManager.beginTransaction();  //Fragment事务，需要每次切换时重新赋值，不然会出现重复提交错误
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
            int measuredWidth = v_analysis_select.getMeasuredWidth();

            int topMargin = layoutParams.topMargin;
            switch (v.getId()){
                case R.id.tv_report_mouth:
                    addOrShowFragment(fragmentTransaction,mouthReprtFragment);
                    layoutParams.setMargins(0,topMargin,0,0); //4个参数按顺序分别是左上右下
                    tv_report_mouth.setTextColor(Color.parseColor("#0c64b5"));
                    tv_report_quarter.setTextColor(Color.parseColor("#999999"));
                    tv_report_year.setTextColor(Color.parseColor("#999999"));
                    break;
                case R.id.tv_report_quarter:
                    addOrShowFragment(fragmentTransaction,quarterReprtFragment);
                    layoutParams.setMargins(measuredWidth,topMargin,0,0); //4个参数按顺序分别是左上右下
                    tv_report_mouth.setTextColor(Color.parseColor("#999999"));
                    tv_report_quarter.setTextColor(Color.parseColor("#0c64b5"));
                    tv_report_year.setTextColor(Color.parseColor("#999999"));
                    break;
                case R.id.tv_report_year:
                    addOrShowFragment(fragmentTransaction,yearReprtFragment);
                    layoutParams.setMargins(measuredWidth*2,topMargin,0,0); //4个参数按顺序分别是左上右下
                    tv_report_mouth.setTextColor(Color.parseColor("#999999"));
                    tv_report_quarter.setTextColor(Color.parseColor("#999999"));
                    tv_report_year.setTextColor(Color.parseColor("#0c64b5"));
                    break;

            }
            v_analysis_select.setLayoutParams(layoutParams);
        }
    }
}
