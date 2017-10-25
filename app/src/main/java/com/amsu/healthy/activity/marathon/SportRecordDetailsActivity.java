package com.amsu.healthy.activity.marathon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.appication.FragmentAdapter;
import com.amsu.healthy.fragment.marathon.SportRecordSpeedFragment;
import com.amsu.healthy.fragment.marathon.SportRecordStatisticsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * author：WangLei
 * date:2017/10/25.
 * QQ:619321796
 */

public class SportRecordDetailsActivity extends BaseActivity {
    public static Intent createIntent(Context context) {
        return new Intent(context, SportRecordDetailsActivity.class);
    }

    private ViewPager mViewPager;
    private View sportRecordDetailsStatistic;
    private View sportRecordDetailsSpeed;
    private LinearLayout buttonGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_record_details);
        initViews();
        initEvents();
    }

    private void initViews() {
        buttonGroup = (LinearLayout) findViewById(R.id.buttonGroup);
        sportRecordDetailsSpeed = findViewById(R.id.sportRecordDetailsSpeed);
        sportRecordDetailsStatistic = findViewById(R.id.sportRecordDetailsStatistic);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        SportRecordStatisticsFragment sportRecordStatisticsFragment = SportRecordStatisticsFragment.newInstance();
        SportRecordSpeedFragment sportRecordSpeedFragment = SportRecordSpeedFragment.newInstance();
        List<Fragment> fragmentActivityList = new ArrayList<>();
        fragmentActivityList.add(sportRecordStatisticsFragment);
        fragmentActivityList.add(sportRecordSpeedFragment);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentActivityList);
        mViewPager.setAdapter(fragmentAdapter);
        sportRecordDetailsStatistic.setSelected(true);
    }

    private void initEvents() {
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setAllSelectNon();
                buttonGroup.getChildAt(position).setSelected(true);
            }
        });
        sportRecordDetailsStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });
        sportRecordDetailsSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(1);
            }
        });
        findViewById(R.id.iv_base_leftimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setAllSelectNon() {
        sportRecordDetailsSpeed.setSelected(false);
        sportRecordDetailsStatistic.setSelected(false);
    }
}
