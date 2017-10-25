package com.amsu.healthy.activity.marathon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;

/**
 * author：WangLei
 * date:2017/10/25.
 * QQ:619321796
 */

public class SportRecordActivity extends BaseActivity {
    public static Intent createIntent(Context context) {
        return new Intent(context, SportRecordActivity.class);
    }

    private ExpandableListView mExpandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_record);
        initHeadView();
        setLeftImage(R.drawable.back_icon);
        setCenterText(getResources().getString(R.string.endurance_sport_record));
        initViews();
        initEvents();
    }

    private void initViews() {
        mExpandableListView = (ExpandableListView) findViewById(R.id.mExpandableListView);
        SportRecordAdapter adapter = new SportRecordAdapter(this);
        mExpandableListView.setAdapter(adapter);
        int groupCount = mExpandableListView.getCount();
        for (int i = 0; i < groupCount; i++) {
            mExpandableListView.expandGroup(i);
        }
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                startActivity(SportRecordDetailsActivity.createIntent(SportRecordActivity.this));
                return false;
            }
        });
    }

    private void initEvents() {
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
