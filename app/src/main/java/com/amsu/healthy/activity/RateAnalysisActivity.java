package com.amsu.healthy.activity;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.AnalysisRateAdapter;
import com.amsu.healthy.fragment.ECGFragment;
import com.amsu.healthy.fragment.HRRFragment;
import com.amsu.healthy.fragment.HRVFragment;
import com.amsu.healthy.fragment.HeartRateFragment;
import com.amsu.healthy.utils.MyUtil;

import java.util.ArrayList;
import java.util.List;

public class RateAnalysisActivity extends BaseActivity {

    private static final String TAG = "RateAnalysisActivity";
    private ViewPager vp_analysis_content;
    //private TabPageIndicator ti_analysis_indicator;
    private List<Fragment> fragmentList;
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
        initHeadView();
        vp_analysis_content = (ViewPager) findViewById(R.id.vp_analysis_content);
        final View v_analysis_select = findViewById(R.id.v_analysis_select);

        setLeftImage(R.drawable.back_icon);
        setCenterText("16/09/08 14:32");

        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final float oneTableWidth = MyUtil.getScreeenWidth(this)/4;  //每一个小格的宽度

        vp_analysis_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i(TAG,"onPageScrolled===position:"+position+",positionOffset:"+positionOffset+",positionOffsetPixels:"+positionOffsetPixels);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
                int floatWidth=  (int) (oneTableWidth*(positionOffset+position));  //view向左的偏移量
                layoutParams.setMargins(floatWidth,0,0,0); //4个参数按顺序分别是左上右下
                v_analysis_select.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                //Log.i(TAG,"onPageSelected===position:"+position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
               // Log.i(TAG,"onPageScrollStateChanged===state:"+state);
            }
        });


    }

    private void initValue() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new HRVFragment());
        fragmentList.add(new HeartRateFragment());
        fragmentList.add(new ECGFragment());
        fragmentList.add(new HRRFragment());

        titleStrings = new String[]{"HRV分析","心率分析","心电分析","HRR分析"};
    }

    private void initData() {
        vp_analysis_content.setAdapter(new AnalysisRateAdapter(getSupportFragmentManager(),fragmentList));



    }



}
