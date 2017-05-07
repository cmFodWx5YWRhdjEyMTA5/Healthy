package com.amsu.healthy.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.HistoryRecordAdapter;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.FoldLineViewWithPoint;
import com.amsu.healthy.view.FoldLineViewWithTextOne;
import com.amsu.healthy.view.LoadMoreListView;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2017/4/14.
 */

public class TestActivity extends BaseActivity {

    private static final String TAG = "TestActivity";
    private LoadMoreListView lv_history_all;
    private List<HistoryRecord> historyRecords;
    private HistoryRecordAdapter historyRecordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        /*FoldLineViewWithPoint spread_line_chart = (FoldLineViewWithPoint) findViewById(R.id.spread_line_chart);
        final TextView tv_mouth_value = (TextView) findViewById(R.id.tv_mouth_value);
        final TextView tv_mouth_datetime = (TextView) findViewById(R.id.tv_mouth_datetime);

        int[] datas =    {67,59,54,67,60,60,61};  //心率数据
        String[] datetime =    {"10月1日","10月2日","10月3日","10月4日","10月5日","10月6日","10月7日"};  //心率数据
        spread_line_chart.setData(datas,datetime);

        spread_line_chart.setOnDateTimeChangeListener(new FoldLineViewWithPoint.OnDateTimeChangeListener() {
            @Override
            public void onDateTimeChange(int heartRate, String dateTime) {
                tv_mouth_value.setText(heartRate+"");
                tv_mouth_datetime.setText(dateTime);
            }
        });*/


        lv_history_all = (LoadMoreListView) findViewById(R.id.lv_list);

        historyRecords = new ArrayList<>();
        //historyRecords.add(new HistoryRecord("12","2017-03-20 17:30:45"));

        historyRecordAdapter = new HistoryRecordAdapter(historyRecords,this);
        lv_history_all.setAdapter(historyRecordAdapter);

        loadData(pageCount);
    }

    int pageCount = 1;
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
                    HistoryRecordActivity.HistoryRecordList historyRecordList = gson.fromJson(result, HistoryRecordActivity.HistoryRecordList.class);
                    if (historyRecordList.errDesc.size()>0){
                        //historyRecords.addAll(historyRecordList.errDesc);
                        historyRecords.add(new HistoryRecord("12","2017-03-20 17:30:45"));
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
