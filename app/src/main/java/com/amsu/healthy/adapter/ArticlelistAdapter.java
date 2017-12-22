package com.amsu.healthy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.News;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by HP on 2017/1/11.
 */

public class ArticlelistAdapter extends BaseAdapter {
    Context context;
    List<News> newsList;
    BitmapUtils bitmapUtils;

    public ArticlelistAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
        bitmapUtils = new BitmapUtils(context);
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final News news = newsList.get(position);
        View inflate;
        MyHolder myHolder;
        if (convertView!=null){
            inflate = convertView;
            myHolder = (MyHolder) inflate.getTag();
        }
        else {
            inflate = View.inflate(context, R.layout.list_article_item, null);
            myHolder = new MyHolder();
            myHolder.iv_readlist_artcileimage = (ImageView) inflate.findViewById(R.id.iv_readlist_artcileimage);
            myHolder.tv_readlist_title = (TextView) inflate.findViewById(R.id.tv_readlist_title);
            myHolder.tv_readlist_time = (TextView) inflate.findViewById(R.id.tv_readlist_time);
            myHolder.tv_readlist_type = (TextView) inflate.findViewById(R.id.tv_readlist_type);
            inflate.setTag(myHolder);
        }


        String imageurl = news.getSmallPictureUrl();
        bitmapUtils.display(myHolder.iv_readlist_artcileimage, imageurl);

        myHolder.tv_readlist_title.setText(news.getTitle());
        myHolder.tv_readlist_time.setText(news.getTime());
        myHolder.tv_readlist_type.setText(news.getType());

        return inflate;
    }

    class MyHolder {
        ImageView iv_readlist_artcileimage;
        TextView tv_readlist_title;
        TextView tv_readlist_time;
        TextView tv_readlist_type;
    }



}
