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
import com.amsu.healthy.activity.ConnectToWifiGudieActivity1;
import com.amsu.healthy.activity.HeartRateAnalysisActivity;
import com.amsu.healthy.activity.HeartRateResultShowActivity;
import com.amsu.healthy.activity.HistoryRecordActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ClothHistoryRecordFragment extends Fragment {

    private static final String TAG = "ClothHistoryRecordFragment";
    private SwipeListView lv_history_all;
    private List<HistoryRecord> historyRecords;
    private int pageCount = 1;
    private HistoryRecordAdapter historyRecordAdapter;
    private List<AppAbortDataSave> abortDataListFromSP;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View inflate;

    public ClothHistoryRecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        inflate = inflater.inflate(R.layout.fragment_cloth_history_record, container, false);
        initView();
        initData();

        return inflate;
    }


    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.sr1);
        swipeRefreshLayout.setColorSchemeResources(R.color.app_Refresh_start_color,R.color.app_background_color);
        swipeRefreshLayout.setOnRefreshListener(new MySwipeRefreshLayoutListener());

        lv_history_all = (SwipeListView) inflate.findViewById(R.id.lv_history_all);
        lv_history_all.setLoadMorehDataListener(new LoadMoreListView.LoadMoreDataListener() {
            @Override
            public void loadMore() {
                loadData(pageCount,false);
            }
        });
    }



    private void initData() {
        historyRecords = new ArrayList<>();
        historyRecordAdapter = new HistoryRecordAdapter(historyRecords,getActivity(),lv_history_all.getRightViewWidth());
        lv_history_all.setAdapter(historyRecordAdapter);

        historyRecordAdapter.setOnRightItemClickListener(new HistoryRecordAdapter.onRightItemClickListener() {

            @Override
            public void onRightItemClick(View v, int position) {
                //Toast.makeText(getActivity(), "删除第  " + (position+1)+" 对话记录", Toast.LENGTH_SHORT).show();
                HistoryRecord historyRecord = historyRecords.get(position);
                deleteRecordByID(historyRecord.getId(),position);
            }
        });

        lv_history_all.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position<historyRecords.size()){
                    HistoryRecord historyRecord = historyRecords.get(position);
                    Log.i(TAG,"historyRecord:"+historyRecord.toString());
                    Intent intent;
                    if (historyRecord.getAnalysisState()==HistoryRecord.analysisState_abort){
                        intent = new Intent(getActivity(), HeartRateAnalysisActivity.class);
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
                        intent = new Intent(getActivity(), HeartRateResultShowActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("historyRecord",historyRecord);
                        intent.putExtra("bundle",bundle);
                        startActivity(intent);
                    }
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

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.deleteHistoryRecordURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                MyUtil.hideDialog(getActivity());
                String result = responseInfo.result;
                Log.i(TAG,"onSuccess==result:"+result);
                JsonBase jsonBase =  MyUtil.commonJsonParse(result,JsonBase.class);
                if (jsonBase!=null && jsonBase.ret==0){
                    MyUtil.showToask(getActivity(),getResources().getString(R.string.delete_successfully));
                    historyRecords.remove(position);
                }
                else {
                    MyUtil.showToask(getActivity(),getResources().getString(R.string.delete_failed));
                }
                lv_history_all.setAdapter(historyRecordAdapter);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"onFailure:"+e);
                //lv_history_all.loadMoreSuccessd();
                MyUtil.hideDialog(getActivity());
                MyUtil.showToask(getActivity(),Constant.noIntentNotifyMsg);
                Log.i(TAG,"上传onFailure==s:"+s);
                lv_history_all.setAdapter(historyRecordAdapter);
            }
        });
    }

    private void loadData(int page, final boolean isRefresh) {
        if (!isRefresh){
            MyUtil.showDialog(getResources().getString(R.string.loading),getActivity());
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
                MyUtil.hideDialog(getActivity());
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
                        MyUtil.showToask(getActivity(),"刷新成功");
                    }
                    //List<HistoryRecord> parseJsonArrayWithGson = MyUtil.parseJsonArrayWithGson(jsonBase,HistoryRecord[].class);
                    if (jsonBase.errDesc!=null && jsonBase.errDesc.size()>0){
                        historyRecords.addAll(jsonBase.errDesc);
                        historyRecordAdapter.notifyDataSetChanged();
                        pageCount++;
                    }
                    else if (jsonBase.errDesc!=null && jsonBase.errDesc.size()==0){
                        MyUtil.showToask(getActivity(),"没有查询到运动记录");
                    }
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
