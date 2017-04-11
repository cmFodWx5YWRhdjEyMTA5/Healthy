package com.amsu.healthy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.AnalysisRateAdapter;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.fragment.analysis.ECGFragment;
import com.amsu.healthy.fragment.analysis.HRRFragment;
import com.amsu.healthy.fragment.analysis.HRVFragment;
import com.amsu.healthy.fragment.analysis.HeartRateFragment;
import com.amsu.healthy.fragment.analysis.SportFragment;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

public class RateAnalysisActivity extends BaseActivity {

    private static final String TAG = "RateAnalysisActivity";
    private ViewPager vp_analysis_content;
    private List<Fragment> fragmentList;
    private TextView tv_analysis_hrv;
    private TextView tv_analysis_rate;
    private TextView tv_analysis_ecg;
    private TextView tv_analysis_hrr;
    private View v_analysis_select;
    public static UploadRecord mUploadRecord;
    private TextView tv_analysis_sport;
    private AnalysisRateAdapter mAnalysisRateAdapter;
    private float mOneTableWidth;
    private int subFormAlCount = 0 ;  //当有四个fragment是为0，有5个fragment时为1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_analysis);
        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setLeftImage(R.drawable.back_icon);
        vp_analysis_content = (ViewPager) findViewById(R.id.vp_analysis_content);
        v_analysis_select = findViewById(R.id.v_analysis_select);

        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_analysis_hrv = (TextView) findViewById(R.id.tv_analysis_hrv);
        tv_analysis_rate = (TextView) findViewById(R.id.tv_analysis_rate);
        tv_analysis_ecg = (TextView) findViewById(R.id.tv_analysis_ecg);
        tv_analysis_hrr = (TextView) findViewById(R.id.tv_analysis_hrr);
        tv_analysis_sport = (TextView) findViewById(R.id.tv_analysis_sport);

        MyClickListener myClickListener = new MyClickListener();
        tv_analysis_hrv.setOnClickListener(myClickListener);
        tv_analysis_rate.setOnClickListener(myClickListener);
        tv_analysis_ecg.setOnClickListener(myClickListener);
        tv_analysis_hrr.setOnClickListener(myClickListener);
        tv_analysis_sport.setOnClickListener(myClickListener);


        //每一个小格的宽度
        mOneTableWidth = MyUtil.getScreeenWidth(this)/5;

        vp_analysis_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i(TAG,"onPageScrolled===position:"+position+",positionOffset:"+positionOffset+",positionOffsetPixels:"+positionOffsetPixels);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
                int floatWidth=  (int) (mOneTableWidth *(positionOffset+position));  //view向左的偏移量
                layoutParams.setMargins(floatWidth,0,0,0); //4个参数按顺序分别是左上右下
                v_analysis_select.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                //Log.i(TAG,"onPageSelected===position:"+position);
                setViewPageTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
               // Log.i(TAG,"onPageScrollStateChanged===state:"+state);
            }
        });

    }
    private void initData() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new SportFragment());
        fragmentList.add(new HRVFragment());
        fragmentList.add(new HeartRateFragment());
        fragmentList.add(new ECGFragment());
        fragmentList.add(new HRRFragment());

        mAnalysisRateAdapter = new AnalysisRateAdapter(getSupportFragmentManager(), fragmentList);
        vp_analysis_content.setAdapter(mAnalysisRateAdapter);


        Intent intent = getIntent();
        if (intent!=null){
            Bundle bundle = intent.getParcelableExtra("bundle");
            if (bundle!=null){
                HistoryRecord historyRecord = bundle.getParcelable("historyRecord");
                if (historyRecord!=null){
                    //根据历史记录id进行网络查询
                    setCenterText(historyRecord.getDatatime());  // 2016-10-28 10:56:04

                    HttpUtils httpUtils = new HttpUtils();
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("id",historyRecord.getID());
                    MyUtil.addCookieForHttp(params);

                    httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getHistoryReportDetailURL, params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result;
                            Log.i(TAG,"上传onSuccess==result:"+result);
                            Gson gson = new Gson();
                            JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                            Log.i(TAG,"jsonBase:"+jsonBase);
                            if (jsonBase.getRet()==1){
                                UploadRecordObject uUploadRecordObject = gson.fromJson(result, UploadRecordObject.class);
                                mUploadRecord = uUploadRecordObject.errDesc;
                                Log.i(TAG,"查询uploadRecord:"+mUploadRecord);

                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            MyUtil.hideDialog();
                            Log.i(TAG,"上传onFailure==s:"+s);
                        }
                    });
                }
                else {
                    //当前分析结果，直接显示
                    mUploadRecord = bundle.getParcelable("uploadRecord");
                    Log.i(TAG,"直接显示uploadRecord:"+mUploadRecord.toString());
                    //Log.i(TAG,"EC:"+mUploadRecord.EC);
                }

                if (mUploadRecord!=null){
                    if (mUploadRecord.getState().equals("0") && tv_analysis_sport.getVisibility()==View.VISIBLE){ //静止状态&& 可见
                        fragmentList.remove(0);
                        tv_analysis_sport.setVisibility(View.GONE);
                        mAnalysisRateAdapter.notifyDataSetChanged();
                    }
                }
            }

            if (intent.getIntExtra(Constant.sportState, -1)==0 && tv_analysis_sport.getVisibility()==View.VISIBLE){
                fragmentList.remove(0);
                tv_analysis_sport.setVisibility(View.GONE);
                mAnalysisRateAdapter.notifyDataSetChanged();
                mOneTableWidth = MyUtil.getScreeenWidth(this)/4;
                subFormAlCount = 1;
            }
        }
    }

    private class UploadRecordObject{
        UploadRecord errDesc;
    }

    private class MyClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_analysis_sport:
                    setViewPageItem(0,vp_analysis_content.getCurrentItem());
                    break;
                case R.id.tv_analysis_hrv:
                    setViewPageItem(1,vp_analysis_content.getCurrentItem());
                    break;
                case R.id.tv_analysis_rate:
                    setViewPageItem(2,vp_analysis_content.getCurrentItem());
                    break;
                case R.id.tv_analysis_ecg:
                    setViewPageItem(3,vp_analysis_content.getCurrentItem());
                    break;
                case R.id.tv_analysis_hrr:
                    setViewPageItem(4,vp_analysis_content.getCurrentItem());
                    break;

            }
        }
    }

    //点击时设置选中条目
    public void setViewPageItem(int viewPageItem,int currentItem) {
        viewPageItem = viewPageItem-subFormAlCount;
        if (currentItem==viewPageItem){
            return;
        }
        vp_analysis_content.setCurrentItem(viewPageItem);
        RelativeLayout.LayoutParams layoutParams =   (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
        int floatWidth= (int) (mOneTableWidth*viewPageItem);  //view向左的偏移量
        layoutParams.setMargins(floatWidth,0,0,0); //4个参数按顺序分别是左上右下
        v_analysis_select.setLayoutParams(layoutParams);

        setViewPageTextColor(viewPageItem);

    }

    //设置文本颜色
    private void setViewPageTextColor(int viewPageItem) {
        switch (viewPageItem){
            case 0:
                tv_analysis_hrv.setTextColor(Color.parseColor("#999999"));
                tv_analysis_rate.setTextColor(Color.parseColor("#999999"));
                tv_analysis_ecg.setTextColor(Color.parseColor("#999999"));
                tv_analysis_hrr.setTextColor(Color.parseColor("#999999"));
                tv_analysis_sport.setTextColor(Color.parseColor("#0c64b5"));
                break;
            case 1:
                tv_analysis_hrv.setTextColor(Color.parseColor("#0c64b5"));
                tv_analysis_rate.setTextColor(Color.parseColor("#999999"));
                tv_analysis_ecg.setTextColor(Color.parseColor("#999999"));
                tv_analysis_hrr.setTextColor(Color.parseColor("#999999"));
                tv_analysis_sport.setTextColor(Color.parseColor("#999999"));
                break;
            case 2:
                tv_analysis_hrv.setTextColor(Color.parseColor("#999999"));
                tv_analysis_rate.setTextColor(Color.parseColor("#0c64b5"));
                tv_analysis_ecg.setTextColor(Color.parseColor("#999999"));
                tv_analysis_hrr.setTextColor(Color.parseColor("#999999"));
                tv_analysis_sport.setTextColor(Color.parseColor("#999999"));
                break;
            case 3:
                tv_analysis_hrv.setTextColor(Color.parseColor("#999999"));
                tv_analysis_rate.setTextColor(Color.parseColor("#999999"));
                tv_analysis_ecg.setTextColor(Color.parseColor("#0c64b5"));
                tv_analysis_hrr.setTextColor(Color.parseColor("#999999"));
                tv_analysis_sport.setTextColor(Color.parseColor("#999999"));
                break;
            case 4:
                tv_analysis_hrv.setTextColor(Color.parseColor("#999999"));
                tv_analysis_rate.setTextColor(Color.parseColor("#999999"));
                tv_analysis_ecg.setTextColor(Color.parseColor("#999999"));
                tv_analysis_hrr.setTextColor(Color.parseColor("#0c64b5"));
                tv_analysis_sport.setTextColor(Color.parseColor("#999999"));
                break;

        }
    }


}
