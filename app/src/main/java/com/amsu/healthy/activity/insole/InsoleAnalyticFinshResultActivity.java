package com.amsu.healthy.activity.insole;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.adapter.FragmentListRateAdapter;
import com.amsu.healthy.bean.InsoleHistoryRecord;
import com.amsu.healthy.bean.InsoleUploadRecord;
import com.amsu.healthy.fragment.analysis_insole.ResultDetailsFragment;
import com.amsu.healthy.fragment.analysis_insole.ResultSpeedFragment;
import com.amsu.healthy.fragment.analysis_insole.ResultStrideFragment;
import com.amsu.healthy.fragment.analysis_insole.ResultTrackFragment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InsoleAnalyticFinshResultActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "AnalyticFinshResult";
    private TextView tv_report_track;
    private TextView tv_report_details;
    private TextView tv_report_speed;
    private TextView tv_report_stride;
    private View v_analysis_select;

    private ViewPager vp_insoleresult_content;
    private List<Fragment> fragmentList;
    private int subFormAlCount = 0 ;  //当有四个fragment是为0，有5个fragment时为1

    private FragmentListRateAdapter mAnalysisRateAdapter;
    private float mOneTableWidth;
    private int mViewMarginTop;
    public static InsoleUploadRecord mInsoleUploadRecord;
    private Map<Integer,Integer> viewIDToViewPageIndex;   //第一个Integer为view的id，第二个为ViewPage所在索引

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytic_finsh_result);

        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText("运动记录");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_report_track = (TextView) findViewById(R.id.tv_report_track);
        tv_report_details = (TextView) findViewById(R.id.tv_report_details);
        tv_report_speed = (TextView) findViewById(R.id.tv_report_speed);
        tv_report_stride = (TextView) findViewById(R.id.tv_report_stride);
        v_analysis_select =  findViewById(R.id.v_analysis_select);

        vp_insoleresult_content = (ViewPager) findViewById(R.id.vp_insoleresult_content);


        tv_report_track.setOnClickListener(this);
        tv_report_details.setOnClickListener(this);
        tv_report_speed.setOnClickListener(this);
        tv_report_stride.setOnClickListener(this);

        fragmentList = new ArrayList<>();
        viewIDToViewPageIndex = new HashMap<>();

        //每一个小格的宽度
        mOneTableWidth = MyUtil.getScreeenWidth(this)/4;

        mViewMarginTop = (int) getResources().getDimension(R.dimen.y124);


        vp_insoleresult_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i(TAG,"onPageScrolled===position:"+position+",positionOffset:"+positionOffset+",positionOffsetPixels:"+positionOffsetPixels);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
                int floatWidth=  (int) (mOneTableWidth *(positionOffset+position));  //view向左的偏移量
                layoutParams.setMargins(floatWidth,mViewMarginTop,0,0); //4个参数按顺序分别是左上右下
                v_analysis_select.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG,"onPageSelected===position:"+position);
                int myWebSocketByIntegerValue = getMyWebSocketByIntegerValue(viewIDToViewPageIndex, position);
                setViewPageTextColor(myWebSocketByIntegerValue);
                //setViewPageTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Log.i(TAG,"onPageScrollStateChanged===state:"+state);
            }
        });

    }

    private void initData() {
        /*Intent intent = getIntent();

        int sportState = intent.getIntExtra(Constant.sportState, 1);  //室内室外
        int sportCreateRecordID = intent.getIntExtra(Constant.sportCreateRecordID, 1);  //室内室外,1室外，2室内
        ArrayList<Integer> paceList = intent.getIntegerArrayListExtra(Constant.paceList);
        long startTimeMillis = intent.getLongExtra(Constant.startTimeMillis, -1);
        int insoleAllKcal = intent.getIntExtra(Constant.insoleAllKcal, 0);

        String insoleAnalyResultString = MyUtil.getStringValueFromSP("insoleAnalyResultString");
        if (!MyUtil.isEmpty(insoleAnalyResultString)){
            Gson gson = new Gson();
            InsoleAnalyResult insoleAnalyResult = gson.fromJson(insoleAnalyResultString, InsoleAnalyResult.class);

        }*/

        int state = 1;
        boolean isHavePace = false;

        mInsoleUploadRecord = null;
        boolean isNeedGetDataFromServer = MyUtil.getBooleanValueFromSP("isDataFromCurrConnect");
        if (isNeedGetDataFromServer){
            String insoleUploadRecordString = MyUtil.getStringValueFromSP("mInsoleUploadRecord");
            if (!MyUtil.isEmpty(insoleUploadRecordString)){
                Gson gson = new Gson();
                mInsoleUploadRecord = gson.fromJson(insoleUploadRecordString, InsoleUploadRecord.class);
                Log.i(TAG,"mInsoleUploadRecord:"+ mInsoleUploadRecord);
                MyUtil.putStringValueFromSP("mInsoleUploadRecord","");
                MyUtil.putBooleanValueFromSP("isDataFromCurrConnect",false);
                if (mInsoleUploadRecord !=null){
                    if (!MyUtil.isEmpty(mInsoleUploadRecord.errDesc.ShoepadData.stepheigh)){
                        try {
                            state = Integer.parseInt(mInsoleUploadRecord.errDesc.ShoepadData.stepheigh);
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }
                    }

                    if (!MyUtil.isEmpty(mInsoleUploadRecord.errDesc.ShoepadData.speedallocationarray)){
                        List<Integer> paceList = gson.fromJson(mInsoleUploadRecord.errDesc.ShoepadData.speedallocationarray, new TypeToken<List<Integer>>() {
                        }.getType());
                        if (paceList!=null && paceList.size()>1){  //当大于1公里时才显示配速界面
                            isHavePace = true;
                        }
                    }
                }
            }
            adjustFeagmentCount(state,isHavePace);
        }
        else {
            final Intent intent = getIntent();
            Bundle bundle = intent.getParcelableExtra("bundle");
            if (bundle!=null){
                InsoleHistoryRecord insoleHistoryRecord = bundle.getParcelable("insoleHistoryRecord");
                Log.i(TAG,"insoleHistoryRecord:"+insoleHistoryRecord);
                getHistoryReportDetail(insoleHistoryRecord);
            }
        }

    }

    private void getHistoryReportDetail(final InsoleHistoryRecord insoleHistoryRecord) {
        MyUtil.showDialog(getResources().getString(R.string.loading),InsoleAnalyticFinshResultActivity.this);
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("id",insoleHistoryRecord.getId());
        MyUtil.addCookieForHttp(params);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getShoepaddetailsURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (isActiivtyDestroy)return;
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);

                Gson gson = new Gson();
                InsoleUploadRecord insoleUploadRecord = gson.fromJson(result, InsoleUploadRecord.class);
                Log.i(TAG,"mInsoleUploadRecord:"+insoleUploadRecord);

               /* Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);*/
                if (insoleUploadRecord!=null && insoleUploadRecord.ret==0){
                    mInsoleUploadRecord = insoleUploadRecord;
                    int state = 2;
                    if (!MyUtil.isEmpty(insoleUploadRecord.errDesc.ShoepadData.stepheigh)){
                        try {
                            state = Integer.parseInt(insoleUploadRecord.errDesc.ShoepadData.stepheigh);
                            if (state==0){
                                state = 2;
                            }
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }
                    }
                    boolean isHavePace = false;
                    if (!MyUtil.isEmpty(insoleUploadRecord.errDesc.ShoepadData.speedallocationarray)){
                        List<Integer> paceList = gson.fromJson(insoleUploadRecord.errDesc.ShoepadData.speedallocationarray, new TypeToken<List<Integer>>() {
                        }.getType());
                        if (paceList!=null && paceList.size()>0){
                            isHavePace = true;
                        }
                    }
                    adjustFeagmentCount(state,isHavePace);
                }
                else {
                    MyUtil.showToask(InsoleAnalyticFinshResultActivity.this,insoleUploadRecord.errDesc+"");
                    finish();

                }

                MyUtil.hideDialog(InsoleAnalyticFinshResultActivity.this);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (isActiivtyDestroy)return;
                MyUtil.hideDialog(InsoleAnalyticFinshResultActivity.this);
                MyUtil.showToask(InsoleAnalyticFinshResultActivity.this,"数据加载失败"+s);
                Log.i(TAG,"上传onFailure==s:"+s);
                finish();
            }
        });
    }

    private void adjustFeagmentCount(int state,boolean isHavePace) {
        int curIndex = 0;
        if (state==1){
            fragmentList.add(new ResultTrackFragment());
            viewIDToViewPageIndex.put(R.id.tv_report_track,curIndex);
            curIndex++;
        }
        fragmentList.add(new ResultDetailsFragment());
        viewIDToViewPageIndex.put(R.id.tv_report_details,curIndex);
        curIndex++;
        if(isHavePace){
            fragmentList.add(new ResultSpeedFragment());
            viewIDToViewPageIndex.put(R.id.tv_report_speed,curIndex);
            curIndex++;
        }
        fragmentList.add(new ResultStrideFragment());
        viewIDToViewPageIndex.put(R.id.tv_report_stride,curIndex);

        mAnalysisRateAdapter = new FragmentListRateAdapter(getSupportFragmentManager(), fragmentList);
        vp_insoleresult_content.setAdapter(mAnalysisRateAdapter);

        Log.i(TAG,"adjustFeagmentCount");
        Log.i(TAG,"state:"+state);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();

        if (state==2 && isHavePace){  //室内 & 有配速
            subFormAlCount = 1;
            tv_report_track.setVisibility(View.GONE);
            mOneTableWidth = MyUtil.getScreeenWidth(this)/3;

        }
        else if (state==2 && !isHavePace){ //室内 & 没配速
            subFormAlCount = 2;
            tv_report_track.setVisibility(View.GONE);
            tv_report_speed.setVisibility(View.GONE);
            mOneTableWidth = MyUtil.getScreeenWidth(this)/2;

        }
        else if (state==1 && !isHavePace){ //室外 & 没配速
            subFormAlCount = 1;
            tv_report_speed.setVisibility(View.GONE);
            mOneTableWidth = MyUtil.getScreeenWidth(this)/3;
        }
        params.width = (int) mOneTableWidth;
        v_analysis_select.setLayoutParams(params);

    }

    //点击时设置选中条目
    public void setViewPageItem(int viewPageItem,int currentItem) {
        if (currentItem==viewPageItem){
            return;
        }
        vp_insoleresult_content.setCurrentItem(viewPageItem);
        /*RelativeLayout.LayoutParams layoutParams =   (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
        int floatWidth= (int) (mOneTableWidth*viewPageItem);  //view向左的偏移量
        layoutParams.setMargins(floatWidth,mViewMarginTop,0,0); //4个参数按顺序分别是左上右下
        v_analysis_select.setLayoutParams(layoutParams);

        setViewPageTextColor(viewPageItem+subFormAlCount);
        Log.i(TAG,"setViewPageItem:"+viewPageItem+","+currentItem);*/

    }

    /*//设置文本颜色
    private void setViewPageTextColor(int viewPageItem) {
        switch (viewPageItem){
            case 0:
                tv_report_track.setTextColor(Color.parseColor("#0c64b5"));
                tv_report_details.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_speed.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_stride.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 1:
                tv_report_track.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_details.setTextColor(Color.parseColor("#0c64b5"));
                tv_report_speed.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_stride.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 2:
                tv_report_track.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_details.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_speed.setTextColor(Color.parseColor("#0c64b5"));
                tv_report_stride.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 3:
                tv_report_track.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_details.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_speed.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_stride.setTextColor(Color.parseColor("#0c64b5"));
                break;
        }
    }*/

    //设置文本颜色
    private void setViewPageTextColor(int id) {
        switch (id){
            case R.id.tv_report_track:
                tv_report_track.setTextColor(Color.parseColor("#0c64b5"));
                tv_report_details.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_speed.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_stride.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.tv_report_details:
                tv_report_track.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_details.setTextColor(Color.parseColor("#0c64b5"));
                tv_report_speed.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_stride.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.tv_report_speed:
                tv_report_track.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_details.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_speed.setTextColor(Color.parseColor("#0c64b5"));
                tv_report_stride.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.tv_report_stride:
                tv_report_track.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_details.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_speed.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_stride.setTextColor(Color.parseColor("#0c64b5"));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        tv_report_track.setTextColor(Color.parseColor("#FFFFFF"));
        tv_report_details.setTextColor(Color.parseColor("#FFFFFF"));
        tv_report_speed.setTextColor(Color.parseColor("#FFFFFF"));
        tv_report_stride.setTextColor(Color.parseColor("#FFFFFF"));
        int viewPageIndex = viewIDToViewPageIndex.get(v.getId());
        setViewPageItem(viewPageIndex,vp_insoleresult_content.getCurrentItem());

        switch (v.getId()){
            case R.id.tv_report_track:
                tv_report_track.setTextColor(Color.parseColor("#0c64b5"));
                break;
            case R.id.tv_report_details:
                tv_report_details.setTextColor(Color.parseColor("#0c64b5"));
                break;
            case R.id.tv_report_speed:
                tv_report_speed.setTextColor(Color.parseColor("#0c64b5"));
                break;
            case R.id.tv_report_stride:
                tv_report_stride.setTextColor(Color.parseColor("#0c64b5"));
                break;
        }
    }

    private boolean isActiivtyDestroy;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        isActiivtyDestroy = true;
        mInsoleUploadRecord = null;
    }

    //根据值获取key
    private Integer getMyWebSocketByIntegerValue(Map<Integer, Integer> onlineUserKey,int value) {
        Set<Integer> webSockets=onlineUserKey.keySet();
        for(Integer myWebSocket:webSockets){
            if(value==onlineUserKey.get(myWebSocket)){
                System.out.println(myWebSocket);
                return myWebSocket;
            }
        }
        return null;
    }


}
