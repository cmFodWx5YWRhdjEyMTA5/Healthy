package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.HistoryRecordAdapter;
import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.LoadMoreListView;
import com.amsu.healthy.view.SwipeListView;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

public class HistoryRecordActivity extends BaseActivity {

    private static final String TAG = "HistoryRecordActivity";
    private SwipeListView lv_history_all;
    private List<HistoryRecord> historyRecords;
    private int pageCount = 1;
    private HistoryRecordAdapter historyRecordAdapter;
    private boolean indexwarringTO;
    private List<AppAbortDataSave> abortDataListFromSP;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record);

        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.my_history));
        setLeftImage(R.drawable.back_icon);
        setRightImage(R.drawable.download_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRecordActivity.this,ConnectToWifiGudieActivity1.class));
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sr1);
        swipeRefreshLayout.setColorSchemeResources(R.color.app_Refresh_start_color,R.color.app_background_color);
        swipeRefreshLayout.setOnRefreshListener(new MySwipeRefreshLayoutListener());

        lv_history_all = (SwipeListView) findViewById(R.id.lv_history_all);
        lv_history_all.setLoadMorehDataListener(new LoadMoreListView.LoadMoreDataListener() {
            @Override
            public void loadMore() {
                loadData(pageCount,false);
            }
        });
    }



    private void initData() {
        historyRecords = new ArrayList<>();
        historyRecordAdapter = new HistoryRecordAdapter(historyRecords,this,lv_history_all.getRightViewWidth());
        lv_history_all.setAdapter(historyRecordAdapter);

        historyRecordAdapter.setOnRightItemClickListener(new HistoryRecordAdapter.onRightItemClickListener() {

            @Override
            public void onRightItemClick(View v, int position) {
                //Toast.makeText(HistoryRecordActivity.this, "删除第  " + (position+1)+" 对话记录", Toast.LENGTH_SHORT).show();
                HistoryRecord historyRecord = historyRecords.get(position);
                deleteRecordByID(historyRecord.getId(),position);
            }
        });

        Intent intent = getIntent();
        indexwarringTO = intent.getBooleanExtra("indexwarringTO", false);

        /*final List<AppAbortDataSave> abortDataListFromSPCopy = new ArrayList<>();
        if (!indexwarringTO){
            abortDataListFromSP = AppAbortDbAdapterUtil.getAbortDataListFromSP();
            Log.i(TAG,"abortDataListFromSP:"+ abortDataListFromSP.toString());


            abortDataListFromSPCopy.addAll(abortDataListFromSP);
            int i=0;
            for (AppAbortDataSave abortData: abortDataListFromSPCopy){
                if (abortData.getSpeedStringList()!=null && abortData.getSpeedStringList().size()>40){  // 40*8=320 大概5分钟
                    if (abortData.getSpeedStringList()!=null)Log.i(TAG,"abortData.getSpeedStringList().size():"+abortData.getSpeedStringList().size());
                    //配速7s一个数据点，则7*9为一分钟，当异常中断数据大于1分钟时，在历史记录列表里显示，否则删除
                    String datatime = MyUtil.getSpecialFormatTime("yyyy-MM-dd HH:mm:ss", new Date(abortData.getStartTimeMillis()));
                    historyRecords.add(new HistoryRecord("",datatime,abortData.getState(),HistoryRecord.analysisState_abort));
                }
                else {
                    if (i<abortDataListFromSP.size()){
                        abortDataListFromSP.remove(i);
                    }
                }
                i++;
           *//* String datatime = MyUtil.getSpecialFormatTime("yyyy-MM-dd HH:mm:ss", new Date(abortData.getStartTimeMillis()));
            historyRecords.add(new HistoryRecord("",datatime,abortData.getState(),HistoryRecord.analysisState_abort));*//*
            }
            AppAbortDbAdapterUtil.putAbortDataListToSP(abortDataListFromSP);
        }*/

        lv_history_all.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position<historyRecords.size()){
                    HistoryRecord historyRecord = historyRecords.get(position);
                    Log.i(TAG,"historyRecord:"+historyRecord.toString());
                    Intent intent;
                    if (historyRecord.getAnalysisState()==HistoryRecord.analysisState_abort){
                        intent = new Intent(HistoryRecordActivity.this, HeartRateAnalysisActivity.class);
                        intent.putExtra(Constant.sportState,historyRecord.getState());
                        if (abortDataListFromSP !=null && position<abortDataListFromSP.size()){
                            AppAbortDataSave abortData = abortDataListFromSP.get(position);
                            intent.putExtra(Constant.ecgFiletimeMillis,abortData.getStartTimeMillis());
                            if (!MyUtil.isEmpty(abortData.getEcgFileName())){
                                intent.putExtra(Constant.ecgLocalFileName,abortData.getEcgFileName());
                            }
                            if (historyRecord.getState()==Constant.SPORTSTATE_ATHLETIC){
                                intent.putExtra(Constant.sportCreateRecordID,abortData.getMapTrackID());
                                if (!MyUtil.isEmpty(abortData.getEcgFileName())){
                                    intent.putExtra(Constant.accLocalFileName,abortData.getAccFileName());
                                }
                                if (abortData.getSpeedStringList()!=null && abortData.getSpeedStringList().size()>0){
                                    ArrayList<Integer> tempList = new ArrayList<Integer>();
                                    tempList.addAll(abortData.getSpeedStringList());
                                    intent.putIntegerArrayListExtra(Constant.mSpeedStringListData,tempList);
                                }
                            }

                            //abortDataListFromSPCopy.remove(abortDataListFromSP.size()-1);
                            //AppAbortDbAdapterUtil.putAbortDataListToSP(abortDataListFromSPCopy);
                            //historyRecords.remove(position);
                            historyRecords.get(position).setAnalysisState(HistoryRecord.analysisState_haveAnalysised);
                            historyRecordAdapter.notifyDataSetChanged();

                            startActivity(intent);
                        }
                    }
                    else {
                        intent = new Intent(HistoryRecordActivity.this, HeartRateResultShowActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("historyRecord",historyRecord);
                        intent.putExtra("bundle",bundle);
                        startActivity(intent);
                    }
                }

            }
        });

        if (indexwarringTO){
            setCenterText(getResources().getString(R.string.data_source));
            ArrayList<HistoryRecord> staticStateHistoryRecords = intent.getParcelableArrayListExtra("staticStateHistoryRecords");
            if (staticStateHistoryRecords!=null && staticStateHistoryRecords.size()>0){
                historyRecords.addAll(staticStateHistoryRecords);
                historyRecordAdapter.notifyDataSetChanged();
            }
            lv_history_all.setAllowLoadMore(false);
            swipeRefreshLayout.setEnabled(false);
        }
        else {
            loadData(pageCount,false);
        }
    }

    private void deleteRecordByID(String id, final int position) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("id",id);
        MyUtil.addCookieForHttp(params);

        MyUtil.showDialog(getResources().getString(R.string.deleting),this);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.deleteHistoryRecordURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                MyUtil.hideDialog(HistoryRecordActivity.this);
                String result = responseInfo.result;
                Log.i(TAG,"onSuccess==result:"+result);
                JsonBase jsonBase =  MyUtil.commonJsonParse(result,JsonBase.class);
                if (jsonBase!=null && jsonBase.ret==0){
                    MyUtil.showToask(HistoryRecordActivity.this,getResources().getString(R.string.delete_successfully));
                    historyRecords.remove(position);
                }
                else {
                    MyUtil.showToask(HistoryRecordActivity.this,getResources().getString(R.string.delete_failed));
                }
                lv_history_all.setAdapter(historyRecordAdapter);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"onFailure:"+e);
                //lv_history_all.loadMoreSuccessd();
                MyUtil.hideDialog(HistoryRecordActivity.this);
                MyUtil.showToask(HistoryRecordActivity.this,Constant.noIntentNotifyMsg);
                Log.i(TAG,"上传onFailure==s:"+s);
                lv_history_all.setAdapter(historyRecordAdapter);
            }
        });
    }

    private void loadData(int page, final boolean isRefresh) {
        if (!isRefresh){
            MyUtil.showDialog(getResources().getString(R.string.loading),this);
        }
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("record_number","50");
        params.addBodyParameter("page",page+"");
        MyUtil.addCookieForHttp(params);

        Log.i(TAG,"page:"+page);



        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getHistoryReportListURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(HistoryRecordActivity.this);
                if (pageCount==1){
                    historyRecords.clear();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    lv_history_all.loadMoreSuccessd();
                }
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                JsonBase<List<HistoryRecord>> jsonBase = MyUtil.commonJsonParse(result, new TypeToken<JsonBase<List<HistoryRecord>>>() {}.getType());
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase!=null&&jsonBase.getRet()==0){
                    if (isRefresh){
                        MyUtil.showToask(HistoryRecordActivity.this,"刷新成功");
                    }
                    //List<HistoryRecord> parseJsonArrayWithGson = MyUtil.parseJsonArrayWithGson(jsonBase,HistoryRecord[].class);
                    if (jsonBase.errDesc!=null && jsonBase.errDesc.size()>0){
                        historyRecords.addAll(jsonBase.errDesc);
                        historyRecordAdapter.notifyDataSetChanged();
                        pageCount++;
                    }
                    else if (jsonBase.errDesc!=null && jsonBase.errDesc.size()==0){
                        MyUtil.showToask(HistoryRecordActivity.this,"没有查询到运动记录");
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //lv_history_all.loadMoreSuccessd();
                MyUtil.hideDialog(HistoryRecordActivity.this);
                MyUtil.showToask(HistoryRecordActivity.this,Constant.noIntentNotifyMsg);
                Log.i(TAG,"上传onFailure==s:"+s);
                swipeRefreshLayout.setRefreshing(false);
                if (pageCount>1){
                    lv_history_all.loadMoreSuccessd();
                }
            }
        });
    }

    private class MySwipeRefreshLayoutListener implements SwipeRefreshLayout.OnRefreshListener{
        @Override
        public void onRefresh() {
            Log.i(TAG,"onRefresh");
            pageCount=1;
            loadData(pageCount,true);
        }
    }
}
