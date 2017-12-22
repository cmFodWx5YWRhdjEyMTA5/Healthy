package com.amsu.healthy.fragment.community;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.NewsDetialActivity;
import com.amsu.healthy.adapter.ArticlelistAdapter;
import com.amsu.healthy.adapter.NewsViewPageAdapter;
import com.amsu.healthy.bean.News;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.LastMsgListView;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {
    private static final String TAG = "NewsFragment";
    private View inflate;
    private Thread myThread;
    private List<News> topNewsList;
    private boolean isNeedLooperPicture = true;
    private ViewPager vp_news_toppicture;
    private LastMsgListView lv_news_list;
    private boolean mIsRefresh;
    private ArticlelistAdapter mArticlelistAdapter;
    private final int WHAT_LISTVIEW_REFRESH = 0;
    private final int WHAT_LISTVIEW_LOADMORE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflate = inflater.inflate(R.layout.fragment_news, container, false);
        initView();
        initData();
        return inflate;
    }



    private void initView() {
        View topView = View.inflate(getActivity(), R.layout.view_article_top, null);
        vp_news_toppicture = (ViewPager) topView.findViewById(R.id.vp_news_toppicture);
        lv_news_list = (LastMsgListView) inflate.findViewById(R.id.lv_news_list);
        lv_news_list.addHeaderView(topView);   //将顶部VIewPage作为头布局加入ListView

    }

    private void initData() {
        int margin = (int) MyUtil.getDimen(getActivity(), R.dimen.x24);
        vp_news_toppicture.setPageMargin(margin);
        //设置缓存的页面数量
        vp_news_toppicture.setOffscreenPageLimit(2);

        topNewsList = new LinkedList<>();
        topNewsList.add(new News("推荐五款头戴式耳机，颠覆你对音乐的看法！","1小时前","http://www.jianshu.com/p/41de7c409b1c?utm_campaign=haruki&utm_content=note&utm_medium=reader_share&utm_source=qq","http://upload-images.jianshu.io/upload_images/2822380-fdd45a36e2bd4fbc.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240","公司新闻"));
        topNewsList.add(new News("智能穿戴设备市场泛红 战争转入细分市场","1小时前","http://www.jianshu.com/p/65a5a549a1c7?utm_campaign=haruki&utm_content=note&utm_medium=reader_share&utm_source=qq","http://upload-images.jianshu.io/upload_images/656374-e7bce22771c06a9d.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240","公司新闻"));
        topNewsList.add(new News("北京穿戴展解读2016智能穿戴产品发展趋势","1小时前","http://www.jianshu.com/p/cc7285570328?utm_campaign=haruki&utm_content=note&utm_medium=reader_share&utm_source=qq","http://upload-images.jianshu.io/upload_images/1444184-f78f95ba17a63fa5.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240","公司新闻"));
        topNewsList.add(new News("超震撼！智能穿戴设备市场2017年这样走","1小时前","http://www.jianshu.com/p/dfcc12a4acc9?utm_campaign=haruki&utm_content=note&utm_medium=reader_share&utm_source=qq","http://upload-images.jianshu.io/upload_images/3790233-949529a9259158ad.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240","公司新闻"));


        NewsViewPageAdapter adapter = new NewsViewPageAdapter(getActivity(),topNewsList);
        vp_news_toppicture.setAdapter(adapter);
        vp_news_toppicture.addOnPageChangeListener(adapter);
        vp_news_toppicture.setCurrentItem(Integer.MAX_VALUE/2);

        //图片轮播，开启子线程，每隔2s改变一次
        myThread = new Thread() {
            @Override
            public void run() {
                while (isNeedLooperPicture) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                vp_news_toppicture.setCurrentItem(vp_news_toppicture.getCurrentItem() + 1);
                            }
                        });
                    }
                }
            }
        };
        myThread.start();

        topNewsList.addAll(topNewsList);
        topNewsList.addAll(topNewsList);
        topNewsList.addAll(topNewsList);
        mArticlelistAdapter = new ArticlelistAdapter(getActivity(),topNewsList);
        lv_news_list.setAdapter(mArticlelistAdapter);

        lv_news_list.setRefreshDataListener(new LastMsgListView.RefreshDataListener() {
            @Override
            public void refresh() {
                mIsRefresh = true;
                refreshData();
            }

            @Override
            public void loadMore() {
                Log.i(TAG,"loadMore");
                loadMoreData();

            }
        });

        lv_news_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = topNewsList.get(position);
                Intent intent = new Intent(getActivity(),NewsDetialActivity.class);
                intent.putExtra("url",news.getUrl());
                getActivity().startActivity(intent);

            }
        });

    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_LISTVIEW_REFRESH:
                    if (mIsRefresh) {
                        View viewHead = lv_news_list.getChildAt(0);
                        lv_news_list.setPadding(0, -viewHead.getMeasuredHeight(), 0, 0);
                        lv_news_list.state = 0;
                        lv_news_list.tv_head_msg.setText("下拉刷新");
                        lv_news_list.iv_head_icon.clearAnimation();
                        lv_news_list.iv_head_icon.setImageResource(R.drawable.indicator_arrow);

                        mArticlelistAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case WHAT_LISTVIEW_LOADMORE:
                    lv_news_list.iv_chatt_refresh.clearAnimation();
                    lv_news_list.viewFoot.setPadding(0,0,0,-lv_news_list.viewFoot.getMeasuredHeight());
                    
                    break;
            }
        }
    };

    //刷新，重新获取数据
    private void refreshData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                myHandler.sendEmptyMessage(WHAT_LISTVIEW_REFRESH);

            }
        }.start();
    }

    //加载更多
    private void loadMoreData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                myHandler.sendEmptyMessage(WHAT_LISTVIEW_LOADMORE);

            }
        }.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        isNeedLooperPicture = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }
}
