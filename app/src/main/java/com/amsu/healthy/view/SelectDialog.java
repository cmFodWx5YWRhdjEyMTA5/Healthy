package com.amsu.healthy.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/1/7.
 */

public class SelectDialog extends Dialog {   //继承Dialog或AlertDialog都可以

    private static final String TAG = "HealthIndicatorAssess";
    private ListView lv_slt_item;
    private String[] titles;

    public SelectDialog(Context context) {
        super(context);
    }

    public SelectDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.slt_cnt_type);
        lv_slt_item = (ListView) findViewById(R.id.lv_slt_item);
    }

    protected SelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    //设置填充数据
    public void setData(String[] data){
        titles = data;
        lv_slt_item.setAdapter(new MyListViewAdapter());
    }

    //设置监听事件
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener){
        lv_slt_item.setOnItemClickListener(onItemClickListener);
    }

    class MyListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return titles.length;
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
            View inflate = View.inflate(getContext(), R.layout.item_select_time, null);
            TextView tv_item_title = (TextView) inflate.findViewById(R.id.tv_item_title);
            String title = titles[position];
            tv_item_title.setText(title);
            return inflate;
        }
    }
}
