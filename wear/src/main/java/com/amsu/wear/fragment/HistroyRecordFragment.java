package com.amsu.wear.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.amsu.wear.R;
import com.amsu.wear.activity.HistoryDetailActivity;
import com.amsu.wear.adapter.HistoryAdapter;
import com.amsu.wear.bean.HistoryRecord;
import com.amsu.wear.bean.JsonBase;
import com.amsu.wear.bean.UploadRecord;
import com.amsu.wear.util.DialogUtil;
import com.amsu.wear.util.HttpUtil;
import com.amsu.wear.util.JsonUtil;
import com.amsu.wear.util.LogUtil;
import com.amsu.wear.util.ToastUtil;
import com.amsu.wear.util.UploadDataUtil;
import com.amsu.wear.view.SwipeListView;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistroyRecordFragment extends BaseFragment {
    private static final String TAG = HistroyRecordFragment.class.getSimpleName();
    @BindView(R.id.lv_history_list)
    SwipeListView lv_history_list;

    @BindView(R.id.sr_history_refresh)
    SwipeRefreshLayout sr_history_refresh;

    private List<HistoryRecord> historyRecords = new ArrayList<>();
    private int pageCount = 1;
    private HistoryAdapter historyRecordAdapter;
    private boolean isFirstComeThisPage = true;

    public HistroyRecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG,"onResume");
        isFirstComeThisPage = false;
    }

    private void initView() {

        sr_history_refresh.setColorSchemeResources(R.color.app_Refresh_start_color,R.color.app_Refresh_start_color);
        sr_history_refresh.setOnRefreshListener(new MySwipeRefreshLayoutListener());
        sr_history_refresh.setDistanceToTriggerSync((int) getResources().getDimension(R.dimen.x30));

        historyRecordAdapter = new HistoryAdapter(getContext(),historyRecords);
        lv_history_list.setAdapter(historyRecordAdapter);

        lv_history_list.setLoadMorehDataListener(new SwipeListView.LoadMoreDataListener() {
            @Override
            public void loadMore() {
                loadData(pageCount++,true);
            }
        });

        loadData(1,false);

        final List<UploadRecord> uploadFailDataFromLocalDB = new UploadDataUtil().findUploadFailDataFromLocalDB();
        if (uploadFailDataFromLocalDB!=null && uploadFailDataFromLocalDB.size()>0){
            for (UploadRecord uploadRecord:uploadFailDataFromLocalDB){
                historyRecords.add(new HistoryRecord("",uploadRecord.timestamp*1000,uploadRecord.state));
            }
            historyRecordAdapter.notifyDataSetChanged();
        }

        lv_history_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position<historyRecords.size()){
                    HistoryRecord historyRecord = historyRecords.get(position);
                    Intent intent = new Intent(getActivity(), HistoryDetailActivity.class);
                    Bundle bundle = new Bundle();
                    if (TextUtils.isEmpty(historyRecord.getId())){
                        UploadRecord uploadRecord = finObjectByTimetamp(uploadFailDataFromLocalDB,historyRecord.getDatatime());
                        if (uploadRecord!=null){
                            bundle.putParcelable("uploadRecord",uploadRecord);
                        }
                    }
                    else {
                        bundle.putParcelable("historyRecord",historyRecord);
                    }
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);
                }
            }
        });

        lv_history_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    sr_history_refresh.setEnabled(true);
                else
                    sr_history_refresh.setEnabled(false);
            }
        });
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_histroy_record;
    }

    private UploadRecord finObjectByTimetamp(List<UploadRecord> uploadRecordList,long timestamp){
        for (UploadRecord uploadRecord:uploadRecordList){
            if (uploadRecord.timestamp==timestamp){
                return uploadRecord;
            }
        }
        return null;
    }

    private void loadData(int page, final boolean isRefresh) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("record_number","30");
        params.addBodyParameter("page",page+"");
        //params.addBodyParameter("state", "3");
        HttpUtil.addCookieForHttp(params);
        params.setUri("http://www.amsu-new.com:8081/intellingence-web/getReportList.do");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                DialogUtil.hideDialog(getActivity());
                if (pageCount==1){
                    historyRecords.clear();
                    sr_history_refresh.setRefreshing(false);
                }
                else {
                    lv_history_list.loadMoreSuccessd();
                }

                Log.i(TAG,"上传onSuccess==result:"+result);
                JsonBase<List<HistoryRecord>> jsonBase = JsonUtil.commonJsonParse(result, new TypeToken<JsonBase<List<HistoryRecord>>>() {}.getType());
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase!=null&&jsonBase.getRet()==0){
                    if (isRefresh){
                        ToastUtil.showToask("刷新成功");
                    }
                    //List<HistoryRecord> parseJsonArrayWithGson = MyUtil.parseJsonArrayWithGson(jsonBase,HistoryRecord[].class);
                    if (jsonBase.errDesc!=null && jsonBase.errDesc.size()>0){
                        historyRecords.addAll(jsonBase.errDesc);
                        historyRecordAdapter.notifyDataSetChanged();
                        pageCount++;
                    }
                    else if (jsonBase.errDesc!=null && jsonBase.errDesc.size()==0){
                        ToastUtil.showToask("没有查询到运动记录");
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                sr_history_refresh.setRefreshing(false);
                Log.i(TAG,"onError:"+ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });




    }

    private void readLocalDB(){
        final List<UploadRecord> uploadFailDataFromLocalDB = new UploadDataUtil().findUploadFailDataFromLocalDB();
        if (uploadFailDataFromLocalDB!=null && uploadFailDataFromLocalDB.size()>0){
            for (UploadRecord uploadRecord:uploadFailDataFromLocalDB){
                historyRecords.add(new HistoryRecord("",uploadRecord.timestamp*1000,uploadRecord.state));
            }
            historyRecordAdapter.notifyDataSetChanged();
        }
    }

    private class MySwipeRefreshLayoutListener implements SwipeRefreshLayout.OnRefreshListener{
        @Override
        public void onRefresh() {
            pageCount=1;
            loadData(pageCount,true);
        }
    }

}
