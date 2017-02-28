package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.HistoryRecordAdapter;
import com.amsu.healthy.bean.HistoryRecord;

import java.util.ArrayList;
import java.util.List;

public class HistoryRecordActivity extends BaseActivity {

    private ListView lv_history_all;
    private List<HistoryRecord> historyRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record);

        initView();
        initData();

    }

    private void initView() {
        initHeadView();
        setCenterText("历史记录");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lv_history_all = (ListView) findViewById(R.id.lv_history_all);
    }

    private void initData() {
        historyRecords = new ArrayList<>();
        historyRecords.add(new HistoryRecord("2017年1月12日","16:34:00"));
        historyRecords.add(new HistoryRecord("2016年12月12日","16:34:00"));

        HistoryRecordAdapter historyRecordAdapter = new HistoryRecordAdapter(historyRecords,this);
        lv_history_all.setAdapter(historyRecordAdapter);

        lv_history_all.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(HistoryRecordActivity.this,RateAnalysisActivity.class));
            }
        });

    }

}
