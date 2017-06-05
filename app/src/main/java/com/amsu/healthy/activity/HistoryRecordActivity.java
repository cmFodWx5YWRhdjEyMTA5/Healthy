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
import com.amsu.healthy.bean.HealthyPlan;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.JsonHealthyList;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.LoadMoreListView;
import com.google.gson.Gson;
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

        lv_history_all.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HistoryRecordActivity.this, RateAnalysisActivity.class);
                Bundle bundle = new Bundle();
                HistoryRecord historyRecord = historyRecords.get(position);
                bundle.putParcelable("historyRecord",historyRecord);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        boolean indexwarringTO = intent.getBooleanExtra("indexwarringTO", false);
        if (indexwarringTO){
            ArrayList<HistoryRecord> staticStateHistoryRecords = intent.getParcelableArrayListExtra("staticStateHistoryRecords");
            if (staticStateHistoryRecords!=null && staticStateHistoryRecords.size()>0){
                historyRecords.addAll(staticStateHistoryRecords);
                historyRecordAdapter.notifyDataSetChanged();
            }

        }
        else {
            loadData(pageCount);
        }

    }

    private void loadData(int page) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("record_number","10");
        params.addBodyParameter("page",page+"");
        MyUtil.addCookieForHttp(params);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getHistoryReportListURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (pageCount>1){
                    lv_history_all.loadMoreSuccessd();
                }
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase.getRet()==0){
                    HistoryRecordList historyRecordList = gson.fromJson(result, HistoryRecordList.class);
                    if (historyRecordList.errDesc!=null && historyRecordList.errDesc.size()>0){
                        historyRecords.addAll(historyRecordList.errDesc);
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

    class HistoryRecordList {
        String ret;
        List<HistoryRecord> errDesc;
    }

}
