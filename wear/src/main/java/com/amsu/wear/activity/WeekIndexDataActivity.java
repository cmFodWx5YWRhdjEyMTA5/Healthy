package com.amsu.wear.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amsu.wear.R;
import com.amsu.wear.adapter.IndexViewPageAdapter;
import com.amsu.wear.bean.IndexData;
import com.amsu.wear.util.LogUtil;
import com.amsu.wear.util.SPUtil;
import com.amsu.wear.view.VerticalViewPager;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class WeekIndexDataActivity extends BaseActivity {
    private static final String TAG = WeekIndexDataActivity.class.getSimpleName();
    @BindView(R.id.indexLayout)
    LinearLayout indexLayout;

    @BindView(R.id.vp_index_page)
    VerticalViewPager vp_index_page;

    private ImageView[] mImageViews = null;
    private List<IndexData> indexDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        indexDataList = new ArrayList<>();
        indexDataList.add(new IndexData("心率过缓"));
        indexDataList.add(new IndexData("心率过速"));
        indexDataList.add(new IndexData("早搏"));
        indexDataList.add(new IndexData("漏搏"));

        initIndexView(4);

        vp_index_page.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mImageViews != null) {
                    int imgCount = mImageViews.length;
                    if (imgCount != 0) {
                        position = position % imgCount;
                    }
                    mImageViews[position].setBackgroundResource(R.drawable.banner_dian_1);
                    for (int i = 0; i < mImageViews.length; i++) {
                        if (position != i) {
                            mImageViews[i].setBackgroundResource(R.drawable.banner_dian_2);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        List<IndexData> indexdata =SPUtil.getListFromSP("indexdata", new TypeToken<List<IndexData>>() {}.getType());
        LogUtil.i(TAG,"indexdata:"+indexdata);

        if (indexdata!=null){
            indexDataList = indexdata;
        }

        IndexViewPageAdapter indexViewPageAdapter = new IndexViewPageAdapter(indexDataList,getApplicationContext());
        vp_index_page.setAdapter(indexViewPageAdapter);

        vp_index_page.setCurrentItem(0);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_week_index_data;
    }

    private void initIndexView(int imageCount) {
        indexLayout.removeAllViews();
        mImageViews = new ImageView[imageCount];
        Resources r = getResources();
        float x10 = r.getDimension(R.dimen.x10);
        int xI10 = Math.round(x10);
        for (int i = 0; i < imageCount; i++) {
            ImageView mImageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = xI10;
            params.bottomMargin = xI10;
            mImageView.setLayoutParams(params);
            mImageViews[i] = mImageView;
            if (i == 0) {
                mImageViews[i].setBackgroundResource(R.drawable.banner_dian_1);
            } else {
                mImageViews[i].setBackgroundResource(R.drawable.banner_dian_2);

            }
            indexLayout.addView(mImageViews[i]);
        }
    }

}
