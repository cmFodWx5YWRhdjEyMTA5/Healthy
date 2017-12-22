package com.amsu.healthy.activity.marathon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.SportRecord;
import com.amsu.healthy.bean.SportRecordList;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.SimpScrollListener;
import com.amsu.healthy.view.ViewFoodView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author：WangLei
 * date:2017/10/25.
 * QQ:619321796
 */

public class SportRecordActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, ViewFoodView.onLoadMoreClickListener {
    public static Intent createIntent(Context context) {
        return new Intent(context, SportRecordActivity.class);
    }

    private ExpandableListView mExpandableListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ViewFoodView viewFoodView;
    private int page = 1;
    /**
     * 每次加载最大列表长度
     */
    private int maxItemCount = 15;
    private boolean isBottom;
    private boolean inLoading;
    private SportRecordAdapter adapter;
    private List<SportRecordList> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_record);
        SportRecord.clear();
        initHeadView();
        setLeftImage(R.drawable.back_icon);
        setCenterText(getResources().getString(R.string.endurance_sport_record));
        setRightText(getResources().getString(R.string.endurance_level));
        initViews();
        initEvents();
        loadData();
    }

    private void initViews() {
        viewFoodView = new ViewFoodView(this);
        viewFoodView.setOnLoadMoreClickListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_Refresh_start_color, R.color.app_background_color);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mExpandableListView = (ExpandableListView) findViewById(R.id.mExpandableListView);
        adapter = new SportRecordAdapter(this, datas);
        addFooterView();
        mExpandableListView.setAdapter(adapter);
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });
        adapter.setOnItemDeleteListener(new SportRecordAdapter.OnItemDeleteListener() {
            @Override
            public void onDelete(int groupPosition, int childPosition) {
                SportRecord recordList = (SportRecord) adapter.getChild(groupPosition, childPosition);
                if (recordList != null) {
                    deleteRecordByID(String.valueOf(recordList.getId()), groupPosition, childPosition);
                }
            }
        });
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                SportRecord sportRecord = (SportRecord) adapter.getChild(i, i1);
                if (sportRecord != null) {
                    startActivity(SportRecordDetailsActivity.createIntent(SportRecordActivity.this, sportRecord.getId()));
                }
                return false;
            }
        });
    }


    private void initEvents() {
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mExpandableListView.setOnScrollListener(new SimpScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount >= maxItemCount && firstVisibleItem + visibleItemCount == totalItemCount) {
                    if (!isBottom && page != 1 && !inLoading) {
                        isBottom = true;
                        loadData();
                    }
                } else {
                    isBottom = false;
                }
            }
        });
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(EnduranceTestResultGradeActivity.createIntent(SportRecordActivity.this));
            }
        });
    }

    @Override
    public void onRefresh() {
        page = 1;
        SportRecord.clear();
        loadData();
    }

    private void loadData() {
        inLoading = true;
        removeFooterView();
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        MyUtil.addCookieForHttp(params);
        params.addBodyParameter("record_number", String.valueOf(maxItemCount));
        params.addBodyParameter("page", String.valueOf(page));
        params.addBodyParameter("state", "3");
        mSwipeRefreshLayout.setRefreshing(true);
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getReportListURL, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String json = responseInfo.result;
                Log.e("json", json);
                Map<String, List<SportRecord>> SportRecordMap = SportRecord.parse(json);
                if (SportRecordMap != null) {
                    Set<Map.Entry<String, List<SportRecord>>> entries = SportRecordMap.entrySet();
                    datas.clear();
                    for (Map.Entry<String, List<SportRecord>> entry : entries) {
                        List<SportRecord> list = entry.getValue();
                        String key = entry.getKey();
                        SportRecordList sportRecordList = new SportRecordList(key, list);
                        datas.add(sportRecordList);
                    }
                    if (SportRecordMap.size() < maxItemCount) {
                        removeFooterView();
                    } else {
                        addFooterView();
                    }
                }
                inLoading = false;
                refresh();
                mSwipeRefreshLayout.setRefreshing(false);
                page++;
            }

            @Override
            public void onFailure(HttpException e, String s) {
                mSwipeRefreshLayout.setRefreshing(false);
                inLoading = false;
            }
        });
    }

    private void deleteRecordByID(String id, final int groupPosition, final int childPosition) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("id", id);
        MyUtil.addCookieForHttp(params);

        MyUtil.showDialog(getResources().getString(R.string.deleting), this);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.deleteHistoryRecordURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                MyUtil.hideDialog(SportRecordActivity.this);
                String result = responseInfo.result;
                JsonBase jsonBase = MyUtil.commonJsonParse(result, JsonBase.class);
                if (jsonBase != null && jsonBase.ret == 0) {
                    MyUtil.showToask(SportRecordActivity.this, getResources().getString(R.string.delete_successfully));
                    adapter.removeChildItem(groupPosition, childPosition);
                } else {
                    MyUtil.showToask(SportRecordActivity.this, getResources().getString(R.string.delete_failed));
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //lv_history_all.loadMoreSuccessd();
                MyUtil.hideDialog(SportRecordActivity.this);
                MyUtil.showToask(SportRecordActivity.this, Constant.noIntentNotifyMsg);
            }
        });
    }

    private void refresh() {
        adapter.refresh();
        int groupCount = datas.size();
        for (int i = 0; i < groupCount; i++) {
            mExpandableListView.collapseGroup(i);
            mExpandableListView.expandGroup(i);
        }
    }

    private void removeFooterView() {
        if (mExpandableListView.getFooterViewsCount() != 0) {
            mExpandableListView.removeFooterView(viewFoodView);
        }
    }

    private void addFooterView() {
        if (mExpandableListView.getFooterViewsCount() == 0) {
            mExpandableListView.addFooterView(viewFoodView);
        }
    }

    @Override
    public void LoadMore() {
        loadData();
    }
}
