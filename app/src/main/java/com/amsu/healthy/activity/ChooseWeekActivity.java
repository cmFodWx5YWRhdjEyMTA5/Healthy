package com.amsu.healthy.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.view.PickerView;

import java.util.ArrayList;
import java.util.List;

public class ChooseWeekActivity extends BaseActivity {

    private static final String TAG = "ChooseWeekActivity";
    private List<String> yearStringList;
    private List<String> weekStringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_week);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("选择日期");
        setLeftText("取消");
        setRightText("完成");

        getTv_base_leftText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        yearStringList = new ArrayList<>();
        weekStringList = new ArrayList<>();

        PickerView pv_choose_year = (PickerView) findViewById(R.id.pv_choose_year);
        PickerView pv_choose_week = (PickerView) findViewById(R.id.pv_choose_week);

/*
        pv_choose_year.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.i(TAG,"onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.i(TAG,"onScroll");
            }
        });*/

        yearStringList.add("2017");
        yearStringList.add("2016");
        yearStringList.add("2015");
        yearStringList.add("2014");
        yearStringList.add("2013");
        yearStringList.add("2012");

        //省份的数据
        pv_choose_year.setData(yearStringList);

        pv_choose_year.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.i(TAG,"选择了"+yearStringList.get(position));

            }
        });

    }

    private class ChooseAdapter extends BaseAdapter{
        List<String> dataStringList;
        Context context;

        ChooseAdapter(List<String> dataStringList, Context context) {
            this.dataStringList = dataStringList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return dataStringList.size();
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
            TextView textView = new TextView(context);


            textView.setText(dataStringList.get(position));
            return textView;
        }
    }




}
