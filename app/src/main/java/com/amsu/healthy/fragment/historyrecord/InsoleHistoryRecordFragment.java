package com.amsu.healthy.fragment.historyrecord;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.HeartRateAnalysisActivity;
import com.amsu.healthy.activity.HeartRateResultShowActivity;
import com.amsu.healthy.activity.HistoryRecordActivity;
import com.amsu.healthy.activity.insole.InsoleAnalyticFinshResultActivity;
import com.amsu.healthy.adapter.HistoryRecordAdapter;
import com.amsu.healthy.adapter.InsoleHistoryRecordAdapter;
import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.bean.InsoleHistoryRecord;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.LoadMoreListView;
import com.amsu.healthy.view.SwipeListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class InsoleHistoryRecordFragment extends Fragment {


    private static final String TAG = "InsoleHistoryRecordFragment";
    private View inflate;
    private SwipeListView lv_history_insole;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<InsoleHistoryRecord> insoleHistoryRecords;
    private InsoleHistoryRecordAdapter insoleHistoryRecordAdapter;
    private int pageCount = 1;


    public InsoleHistoryRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_insole_history_record, container, false);
        initView();
        initData();
        return inflate;
    }

    private void initView() {
        lv_history_insole = (SwipeListView) inflate.findViewById(R.id.lv_history_insole);

        swipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.sr1);
        swipeRefreshLayout.setColorSchemeResources(R.color.app_Refresh_start_color,R.color.app_background_color);
        swipeRefreshLayout.setOnRefreshListener(new MySwipeRefreshLayoutListener());


        lv_history_insole.setLoadMoreOpened(false);

        /*lv_history_insole.setLoadMorehDataListener(new LoadMoreListView.LoadMoreDataListener() {
            @Override
            public void loadMore() {
                loadData(pageCount,false);
            }
        });*/
    }

    private void initData() {
        insoleHistoryRecords = new ArrayList<>();
        insoleHistoryRecordAdapter = new InsoleHistoryRecordAdapter(insoleHistoryRecords, getActivity(), lv_history_insole.getRightViewWidth());
        lv_history_insole.setAdapter(insoleHistoryRecordAdapter);

        insoleHistoryRecordAdapter.setOnRightItemClickListener(new InsoleHistoryRecordAdapter.onRightItemClickListener() {

            @Override
            public void onRightItemClick(View v, int position) {
                //Toast.makeText(HistoryRecordActivity.this, "删除第  " + (position+1)+" 对话记录", Toast.LENGTH_SHORT).show();
                InsoleHistoryRecord historyRecord = insoleHistoryRecords.get(position);
                deleteRecordByID(historyRecord.getId(),position);
            }
        });



        lv_history_insole.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position<insoleHistoryRecords.size()){
                    Intent intent = new Intent(getActivity(), InsoleAnalyticFinshResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("insoleHistoryRecord", insoleHistoryRecords.get(position));
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);
                }

            }
        });

        loadData(pageCount,false);
    }

    private void deleteRecordByID(String id, final int position) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("id",id);
        MyUtil.addCookieForHttp(params);

        MyUtil.showDialog(getResources().getString(R.string.deleting),getActivity());

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.deleteShoepadDataById, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                MyUtil.hideDialog(getActivity());
                String result = responseInfo.result;
                Log.i(TAG,"onSuccess==result:"+result);
                JsonBase jsonBase =  MyUtil.commonJsonParse(result,JsonBase.class);
                if (jsonBase!=null && jsonBase.ret==0){
                    MyUtil.showToask(getActivity(),getResources().getString(R.string.delete_successfully));
                    insoleHistoryRecords.remove(position);
                }
                else {
                    MyUtil.showToask(getActivity(),getResources().getString(R.string.delete_failed));
                }
                lv_history_insole.setAdapter(insoleHistoryRecordAdapter);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"onFailure:"+e);
                //lv_history_all.loadMoreSuccessd();
                MyUtil.hideDialog(getActivity());
                MyUtil.showToask(getActivity(),Constant.noIntentNotifyMsg);
                Log.i(TAG,"上传onFailure==s:"+s);
                lv_history_insole.setAdapter(insoleHistoryRecordAdapter);
            }
        });
    }

    private void loadData(int page, final boolean isRefresh) {
        /*if (!isRefresh){
            MyUtil.showDialog(getResources().getString(R.string.loading),getActivity());
        }*/
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        /*params.addBodyParameter("record_number","50");
        params.addBodyParameter("page",page+"");*/
        MyUtil.addCookieForHttp(params);

        Log.i(TAG,"page:"+page);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getShoepadList, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(getActivity());
                if (pageCount==1){
                    insoleHistoryRecords.clear();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    lv_history_insole.loadMoreSuccessd();
                }
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                //JsonBase<List<InsoleHistoryRecord>> jsonBase = MyUtil.commonJsonParse(result, new TypeToken<JsonBase<List<InsoleHistoryRecord>>>() {}.getType());
                //Log.i(TAG,"jsonBase:"+jsonBase);
                Gson gson = new Gson();

                List<InsoleHistoryRecord> insoleHistoryRecordsTemp = gson.fromJson(result, new TypeToken<List<InsoleHistoryRecord>>() {}.getType());
                if (insoleHistoryRecordsTemp!=null&&insoleHistoryRecordsTemp.size()>0){
                    if (isRefresh){
                        MyUtil.showToask(getActivity(),"刷新成功");
                    }
                    insoleHistoryRecords.addAll(insoleHistoryRecordsTemp);
                    Collections.reverse(insoleHistoryRecords);
                    insoleHistoryRecordAdapter.notifyDataSetChanged();
                    pageCount++;
                }
                else {
                    //MyUtil.showToask(getActivity(),"没有查询到运动记录");
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //lv_history_all.loadMoreSuccessd();
                MyUtil.hideDialog(getActivity());
                MyUtil.showToask(getActivity(),Constant.noIntentNotifyMsg);
                Log.i(TAG,"上传onFailure==s:"+s);
                swipeRefreshLayout.setRefreshing(false);
                if (pageCount>1){
                    lv_history_insole.loadMoreSuccessd();
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
