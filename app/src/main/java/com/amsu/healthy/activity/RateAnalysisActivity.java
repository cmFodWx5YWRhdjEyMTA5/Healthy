package com.amsu.healthy.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.AnalysisRateAdapter;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.page.BasePage;
import com.amsu.healthy.page.HRVPage;
import com.amsu.healthy.page.HeartRatePage;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RateAnalysisActivity extends BaseActivity {

    private ViewPager vp_analysis_content;
    private TabPageIndicator ti_analysis_indicator;
    private List<BasePage> pageObjects;
    private String titleStrings[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_analysis);

        initView();

        initValue();

        initData();




    }

    private void initView() {
        ti_analysis_indicator = (TabPageIndicator) findViewById(R.id.ti_analysis_indicator);
        vp_analysis_content = (ViewPager) findViewById(R.id.vp_analysis_content);


    }

    private void initValue() {
        pageObjects = new ArrayList<>();
        pageObjects.add(new HRVPage(this));
        pageObjects.add(new HRVPage(this));
        pageObjects.add(new HRVPage(this));
        pageObjects.add(new HeartRatePage(this));

        titleStrings = new String[]{"HRV分析","心率分析","心电分析","HRR分析"};
    }

    private void initData() {
        vp_analysis_content.setAdapter(new AnalysisRateAdapter(pageObjects,titleStrings));

        ti_analysis_indicator.setViewPager(vp_analysis_content);
    }



}
