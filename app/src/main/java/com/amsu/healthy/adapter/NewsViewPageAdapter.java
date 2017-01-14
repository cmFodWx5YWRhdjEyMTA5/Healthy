package com.amsu.healthy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.NewsDetialActivity;
import com.amsu.healthy.bean.News;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by HP on 2017/1/10.
 */

public class NewsViewPageAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener{
    private Context context;

    private int sHeightPadding = 100;
    private String TAG="NewsViewPageAdapter";
    private Map<Integer,View> mViewList;
    private List<News> newsList;

    public NewsViewPageAdapter(Context context, List<News> newses) {
        this.context = context;
        sHeightPadding = (int) context.getResources().getDimension(R.dimen.x24);
        mViewList = new HashMap<>();
        this.newsList = newses;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        Log.i(TAG,"instantiateItem: newPosition:"+position);
        int newPosition = position % 4;
        final News news = newsList.get(newPosition);
        View inflate = View.inflate(context, R.layout.item_news_toppicture, null);
        ImageView iv_new_picture = (ImageView) inflate.findViewById(R.id.iv_new_picture);
        TextView tv_new_title = (TextView) inflate.findViewById(R.id.tv_new_title);

        BitmapUtils bitmapUtils = new BitmapUtils(context);
        bitmapUtils.display(iv_new_picture,news.getSmallPictureUrl());
        tv_new_title.setText(news.getTitle());

        //点击事件
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,NewsDetialActivity.class);
                intent.putExtra("url",news.getUrl());
                context.startActivity(intent);
            }
        });

        inflate.setPadding(0,sHeightPadding,0,sHeightPadding);
        mViewList.put(position,inflate);
        container.addView(inflate);

        return inflate;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Log.i(TAG,"onPageScrolled: position:"+position+",positionOffset:"+positionOffset);
        int outHeightPadding = (int) (positionOffset * sHeightPadding);
        View outView = mViewList.get(position);
        outView.setPadding(0,outHeightPadding,0,outHeightPadding);   // 当前显示页不断变小（向左滑）

        int inHeightPadding = (int) ((1-positionOffset) * sHeightPadding);
        int key = position + 1;
        View inView = mViewList.get(key);
        inView.setPadding(0,inHeightPadding,0,inHeightPadding);  // 下一页不断变大（向左滑）
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
