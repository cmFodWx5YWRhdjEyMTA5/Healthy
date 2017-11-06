package com.amsu.healthy.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amsu.healthy.R;


public class ViewFoodView extends LinearLayout implements OnClickListener {
    public View mFootView;// 尾部mFootView
    private View mLoadMoreView;// mFootView 的view(mFootView)
    private TextView mLoadMoreTextView;// 加载更多.(mFootView)
    private View mLoadingView;// 加载中...View(mFootView)
    private onLoadMoreClickListener more;

    // 点击加载更多枚举所有状态
    private enum DListViewLoadingMore {
        LV_NORMAL, // 普通状态
        LV_LOADING, // 加载状态
        LV_OVER; // 结束状态
    }

    private DListViewLoadingMore loadingMoreState = DListViewLoadingMore.LV_NORMAL;// 加载更多默认状态.

    public ViewFoodView(Context context) {
        super(context);
        initView(context);
    }

    public void initView(Context context) {
        mFootView = LayoutInflater.from(context).inflate(R.layout.common_list_footview, this);
        mLoadMoreView = findViewById(R.id.load_more_view);
        mLoadMoreTextView = (TextView) findViewById(R.id.load_more_tv);
        mLoadingView = findViewById(R.id.loading_layout);
        mLoadMoreView.setOnClickListener(this);
    }

    public void setFootViewTextColor(int color) {
        if (mLoadMoreTextView != null) {
            mLoadMoreTextView.setTextColor(color);
        }
    }

    public void setFootViewBgColor(int color) {
        if (mFootView != null) {
            mFootView.setBackgroundColor(color);
        }
    }

    // 更新Footview视图
    private void updateLoadMoreViewState(DListViewLoadingMore state) {
        switch (state) {
            // 普通状态
            case LV_NORMAL:
                mLoadingView.setVisibility(View.GONE);
                mLoadMoreTextView.setVisibility(View.VISIBLE);
                mLoadMoreTextView.setText("查看更多");
                break;
            // 加载中状态
            case LV_LOADING:
                mLoadingView.setVisibility(View.VISIBLE);
                mLoadMoreTextView.setVisibility(View.GONE);
                break;
            // 加载完毕状态
            case LV_OVER:
                mLoadingView.setVisibility(View.GONE);
                mLoadMoreTextView.setVisibility(View.VISIBLE);
                mLoadMoreTextView.setText("加载完毕");
                break;
            default:
                break;
        }
        loadingMoreState = state;
    }

    public void loadMore() {
        updateLoadMoreViewState(DListViewLoadingMore.LV_LOADING);
    }

    public void onLoadMore() {
        if (more != null && loadingMoreState == DListViewLoadingMore.LV_NORMAL) {
            updateLoadMoreViewState(DListViewLoadingMore.LV_LOADING);
            more.LoadMore();
        }
    }

    /*******
     * 是否加载完毕,如果没有加载完毕则可继续加载更多
     *
     * @param flag
     */
    public void loadOver(boolean flag) {
        if (flag) {
            updateLoadMoreViewState(DListViewLoadingMore.LV_OVER);
        } else {
            updateLoadMoreViewState(DListViewLoadingMore.LV_NORMAL);
        }
    }

    /**
     * 设置加载更多按钮事件
     *
     * @param more
     */
    public void setOnLoadMoreClickListener(onLoadMoreClickListener more) {
        this.more = more;
    }

    public interface onLoadMoreClickListener {
        void LoadMore();
    }

    @Override
    public void onClick(View v) {
        onLoadMore();
    }
}
