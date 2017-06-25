package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.HistoryRecordAdapter;
import com.amsu.healthy.bean.Apk;
import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.HealthyPlan;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.utils.AppAbortDataSaveUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.LoadMoreListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryRecordActivity extends BaseActivity {

    private static final String TAG = "HistoryRecordActivity";
    private LoadMoreListView lv_history_all;
    private List<HistoryRecord> historyRecords;
    private int pageCount = 1;
    private HistoryRecordAdapter historyRecordAdapter;
    private boolean indexwarringTO;
    private List<AppAbortDataSave> abortDataListFromSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record);

        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText("历史记录");
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
                startActivity(new Intent(HistoryRecordActivity.this,ConnectToWifiModuleGudieActivity1.class));
            }
        });
        lv_history_all = (LoadMoreListView) findViewById(R.id.lv_history_all);
        lv_history_all.setLoadMorehDataListener(new LoadMoreListView.LoadMoreDataListener() {
            @Override
            public void loadMore() {
                loadData(pageCount);

            }
        });
    }

    private void initData() {
        historyRecords = new ArrayList<>();
        historyRecordAdapter = new HistoryRecordAdapter(historyRecords,this);
        lv_history_all.setAdapter(historyRecordAdapter);

        Intent intent = getIntent();
        indexwarringTO = intent.getBooleanExtra("indexwarringTO", false);

        if (!indexwarringTO){
            abortDataListFromSP = AppAbortDataSaveUtil.getAbortDataListFromSP();
            Log.i(TAG,"abortDataListFromSP:"+ abortDataListFromSP.toString());

            List<AppAbortDataSave> abortDataListFromSPCopy = new ArrayList<>();
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
           /* String datatime = MyUtil.getSpecialFormatTime("yyyy-MM-dd HH:mm:ss", new Date(abortData.getStartTimeMillis()));
            historyRecords.add(new HistoryRecord("",datatime,abortData.getState(),HistoryRecord.analysisState_abort));*/
            }
            AppAbortDataSaveUtil.putAbortDataListToSP(abortDataListFromSP);
        }

        lv_history_all.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HistoryRecord historyRecord = historyRecords.get(position);
                Log.i(TAG,"historyRecord:"+historyRecord.toString());
                Intent intent;
                if (historyRecord.getAnalysisState()==HistoryRecord.analysisState_abort){
                    intent = new Intent(HistoryRecordActivity.this, HeartRateActivity.class);
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
                                intent.putIntegerArrayListExtra(Constant.mSpeedStringListData,abortData.getSpeedStringList());
                            }
                        }

                        abortDataListFromSP.remove(abortDataListFromSP.size()-1);
                        AppAbortDataSaveUtil.putAbortDataListToSP(abortDataListFromSP);
                        //historyRecords.remove(position);
                        historyRecords.get(position).setAnalysisState(HistoryRecord.analysisState_haveAnalysised);
                        historyRecordAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    intent = new Intent(HistoryRecordActivity.this, RateAnalysisActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("historyRecord",historyRecord);
                    intent.putExtra("bundle",bundle);
                }
                startActivity(intent);
            }
        });


        if (indexwarringTO){
            ArrayList<HistoryRecord> staticStateHistoryRecords = intent.getParcelableArrayListExtra("staticStateHistoryRecords");
            if (staticStateHistoryRecords!=null && staticStateHistoryRecords.size()>0){
                historyRecords.addAll(staticStateHistoryRecords);
                historyRecordAdapter.notifyDataSetChanged();
            }
            lv_history_all.setAllowLoadMore(false);
        }
        else {
            loadData(pageCount);
        }
    }

    private void loadData(int page) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("record_number","50");
        params.addBodyParameter("page",page+"");
        MyUtil.addCookieForHttp(params);

        MyUtil.showDialog("正在查询",this);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getHistoryReportListURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog();
                if (pageCount>1){
                    lv_history_all.loadMoreSuccessd();
                }
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                JsonBase<List<HistoryRecord>> jsonBase = MyUtil.commonJsonParse(result, new TypeToken<JsonBase<List<HistoryRecord>>>() {}.getType());
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase!=null&&jsonBase.getRet()==0){
                    //List<HistoryRecord> parseJsonArrayWithGson = MyUtil.parseJsonArrayWithGson(jsonBase,HistoryRecord[].class);
                    if (jsonBase.errDesc!=null && jsonBase.errDesc.size()>0){
                        historyRecords.addAll(jsonBase.errDesc);
                        historyRecordAdapter.notifyDataSetChanged();
                        pageCount++;
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                lv_history_all.loadMoreSuccessd();
                MyUtil.hideDialog();
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }


}
