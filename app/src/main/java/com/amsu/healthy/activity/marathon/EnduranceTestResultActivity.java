package com.amsu.healthy.activity.marathon;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.appication.FragmentAdapter;
import com.amsu.healthy.fragment.marathon.SportRecordStatisticsItem_1;
import com.amsu.healthy.fragment.marathon.SportRecordStatisticsItem_2;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.UStringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.mob.MobSDK.getContext;

/**
 * author：WangLei
 * date:2017/10/24.
 * QQ:619321796
 * 耐力测试水平
 */

public class EnduranceTestResultActivity extends BaseActivity {
    public static Intent createIntent(Context context) {
        return new Intent(context, EnduranceTestResultActivity.class);
    }

    TextView testDate;
    TextView testDistance;
    TextView testSpeed;
    TextView testVo2;
    TextView testLevel;

    private LinearLayout indexLayout;
    /**
     * 滚动图片指示视图列表
     */
    private ImageView[] mImageViews = null;
    SportRecordStatisticsItem_1 sportRecordStatisticsItem_1;
    SportRecordStatisticsItem_2 sportRecordStatisticsItem_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endurance_test_result);
        initHeadView();
        setLeftImage(R.drawable.back_icon);
        setCenterText(getResources().getString(R.string.endurance_level));
        initViews();
        initEvents();
    }

    private void initViews() {
        indexLayout = (LinearLayout) findViewById(R.id.indexLayout);
        testDate = (TextView) findViewById(R.id.testDate);
        testDistance = (TextView) findViewById(R.id.testDistance);
        testSpeed = (TextView) findViewById(R.id.testSpeed);
        testVo2 = (TextView) findViewById(R.id.testVo2);
        testLevel = (TextView) findViewById(R.id.testLevel);
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        double Vo2max = intent.getDoubleExtra("Vo2max", 0);
        float speed = intent.getFloatExtra("speed", 0);
        String enduranceLevel = intent.getStringExtra("enduranceLevel");
        String distance = intent.getStringExtra("distance");
        testDistance.setText(distance);
        testLevel.setText(enduranceLevel);
        testVo2.setText(UStringUtil.formatNumber(Vo2max, 2));
        testDate.setText(date);
        testSpeed.setText(UStringUtil.formatNumber(speed, 2));
        ViewPager mViewPager = (ViewPager) findViewById(R.id.mViewPagerStatistics);
        List<Fragment> fragmentList = new ArrayList<>();
        sportRecordStatisticsItem_1 = SportRecordStatisticsItem_1.newInstance();
        sportRecordStatisticsItem_2 = SportRecordStatisticsItem_2.newInstance();
        fragmentList.add(sportRecordStatisticsItem_1);
        fragmentList.add(sportRecordStatisticsItem_2);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
        });
        initIndexView(fragmentList.size());
        loadChart();
    }

    private void loadChart() {
        String hr = getIntent().getStringExtra("hr");
        String strideFrequency = getIntent().getStringExtra("strideFrequency");
        Gson gson = new Gson();
        List<Integer> hrList = new ArrayList<>();
        List<Integer> cadenceList = new ArrayList<>();
        if (!MyUtil.isEmpty(hr) && !hr.equals(Constant.uploadRecordDefaultString) && !hr.equals("-1")) {
            hrList = gson.fromJson(hr, new TypeToken<List<Integer>>() {
            }.getType());
        }
        if (!MyUtil.isEmpty(strideFrequency) && !strideFrequency.equals(Constant.uploadRecordDefaultString)) {
            cadenceList = gson.fromJson(strideFrequency, new TypeToken<List<Integer>>() {
            }.getType());
        }
        int[] heartData = MyUtil.listToIntArray(hrList);
        int[] stepData = MyUtil.listToIntArray(cadenceList);
        if (sportRecordStatisticsItem_1 != null) {
            sportRecordStatisticsItem_1.setData(stepData, 12*60);
        }
        if (sportRecordStatisticsItem_2 != null) {
            sportRecordStatisticsItem_2.setData(heartData, 10);
        }
    }

    private void initEvents() {
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initIndexView(int imageCount) {
        indexLayout.removeAllViews();
        mImageViews = new ImageView[imageCount];
        Resources r = getResources();
        float x10 = r.getDimension(R.dimen.x10);
        int xI10 = Math.round(x10);
        for (int i = 0; i < imageCount; i++) {
            ImageView mImageView = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = xI10;
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
