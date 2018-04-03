package com.amsu.wear.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.wear.R;
import com.amsu.wear.bean.IndexData;
import com.amsu.wear.view.CircleRingView;

import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.adapter
 * @time 2018-03-12 3:33 PM
 * @describe
 */
public class IndexViewPageAdapter extends PagerAdapter {
    private List<IndexData> indexDataList;
    private Context mContext;


    public IndexViewPageAdapter(List<IndexData> indexDataList, Context mContext) {
        this.indexDataList = indexDataList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return indexDataList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View inflate =  View.inflate(mContext, R.layout.layout_indexdata, null);
        CircleRingView cr_item_page = inflate.findViewById(R.id.cr_item_page);
        TextView tv_index_value = inflate.findViewById(R.id.tv_item_value);
        TextView tv_index_dec = inflate.findViewById(R.id.tv_item_dec);


        IndexData indexData = indexDataList.get(position);
        if (indexData!=null){
            tv_index_dec.setText(indexData.getDec());
            float x = 360/100;
            switch (indexData.getValue()){
                case 0:
                    cr_item_page.setValue(20*x);
                    cr_item_page.updateColor(ContextCompat.getColor(mContext,R.color.color_green),ContextCompat.getColor(mContext,R.color.color_green_bg));
                    tv_index_value.setTextColor(ContextCompat.getColor(mContext,R.color.color_green));
                    tv_index_value.setText("正常");
                    break;
                case 1:
                    cr_item_page.setValue(60*x);
                    cr_item_page.updateColor(ContextCompat.getColor(mContext,R.color.color_orange),ContextCompat.getColor(mContext,R.color.color_yellow_bg));
                    tv_index_value.setTextColor(ContextCompat.getColor(mContext,R.color.color_orange));
                    tv_index_value.setText("黄色预警");
                    break;
                case 2:
                    cr_item_page.setValue(90*x);
                    cr_item_page.updateColor(ContextCompat.getColor(mContext,R.color.color_red_1),ContextCompat.getColor(mContext,R.color.color_red_bg));
                    tv_index_value.setTextColor(ContextCompat.getColor(mContext,R.color.color_red_1));
                    tv_index_value.setText("红色预警");
                    break;
            }
        }

        container.addView(inflate);
        return inflate;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
