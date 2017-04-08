package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.UploadHistoryRecordAdapter;
import com.amsu.healthy.bean.HistoryRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2017/4/6.
 */

public class UploadOfflineFileActivity extends BaseActivity {

    private ListView lv_history_upload;
    private List<HistoryRecord> uploadHistoryRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        initView();
        initData();

    }

    private void initView() {
        initHeadView();
        setCenterText("同步离线文件");
        setLeftImage(R.drawable.back_icon);
        setRightImage(R.drawable.tongbu_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getIv_base_rightimage().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });
        lv_history_upload = (ListView) findViewById(R.id.lv_history_upload);
    }

    private void initData() {
        uploadHistoryRecords = new ArrayList<>();

        //UploadHistoryRecordAdapter uploadHistoryRecordAdapter = new UploadHistoryRecordAdapter();





    }

}








