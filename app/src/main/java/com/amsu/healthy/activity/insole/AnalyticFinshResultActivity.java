package com.amsu.healthy.activity.insole;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.adapter.FragmentListRateAdapter;
import com.amsu.healthy.fragment.analysis.ECGFragment;
import com.amsu.healthy.fragment.analysis.HRRFragment;
import com.amsu.healthy.fragment.analysis.HRVFragment;
import com.amsu.healthy.fragment.analysis.HeartRateFragment;
import com.amsu.healthy.fragment.analysis.SportFragment;
import com.amsu.healthy.fragment.analysis_insole.ResultDetailsFragment;
import com.amsu.healthy.fragment.analysis_insole.ResultSpeedFragment;
import com.amsu.healthy.fragment.analysis_insole.ResultStrideFragment;
import com.amsu.healthy.fragment.analysis_insole.ResultTrackFragment;
import com.amsu.healthy.fragment.report.MouthReprtFragment;
import com.amsu.healthy.fragment.report.QuarterReprtFragment;
import com.amsu.healthy.fragment.report.YearReprtFragment;
import com.amsu.healthy.utils.MyUtil;

import java.util.ArrayList;
import java.util.List;

public class AnalyticFinshResultActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "AnalyticFinshResult";
    private TextView tv_report_track;
    private TextView tv_report_details;
    private TextView tv_report_speed;
    private TextView tv_report_stride;
    private View v_analysis_select;

    private ViewPager vp_insoleresult_content;
    private List<Fragment> fragmentList;
    private int subFormAlCount = 0 ;  //当有四个fragment是为0，有5个fragment时为1

    private FragmentListRateAdapter mAnalysisRateAdapter;
    private float mOneTableWidth;
    private int mViewMarginTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytic_finsh_result);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("运动记录");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_report_track = (TextView) findViewById(R.id.tv_report_track);
        tv_report_details = (TextView) findViewById(R.id.tv_report_details);
        tv_report_speed = (TextView) findViewById(R.id.tv_report_speed);
        tv_report_stride = (TextView) findViewById(R.id.tv_report_stride);
        v_analysis_select =  findViewById(R.id.v_analysis_select);

        vp_insoleresult_content = (ViewPager) findViewById(R.id.vp_insoleresult_content);


        tv_report_track.setOnClickListener(this);
        tv_report_details.setOnClickListener(this);
        tv_report_speed.setOnClickListener(this);
        tv_report_stride.setOnClickListener(this);

        fragmentList = new ArrayList<>();


        //每一个小格的宽度
        mOneTableWidth = MyUtil.getScreeenWidth(this)/4;

        mViewMarginTop = (int) getResources().getDimension(R.dimen.y124);


        vp_insoleresult_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i(TAG,"onPageScrolled===position:"+position+",positionOffset:"+positionOffset+",positionOffsetPixels:"+positionOffsetPixels);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
                int floatWidth=  (int) (mOneTableWidth *(positionOffset+position));  //view向左的偏移量
                layoutParams.setMargins(floatWidth,mViewMarginTop,0,0); //4个参数按顺序分别是左上右下
                v_analysis_select.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                //Log.i(TAG,"onPageSelected===position:"+position);
                setViewPageTextColor(position+subFormAlCount);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Log.i(TAG,"onPageScrollStateChanged===state:"+state);
            }
        });

        adjustFeagmentCount(1);

    }

    private void adjustFeagmentCount(int state) {
        if (state==1){
            fragmentList.add(new ResultTrackFragment());
        }
        fragmentList.add(new ResultDetailsFragment());
        fragmentList.add(new ResultSpeedFragment());
        fragmentList.add(new ResultStrideFragment());


        mAnalysisRateAdapter = new FragmentListRateAdapter(getSupportFragmentManager(), fragmentList);

        vp_insoleresult_content.setAdapter(mAnalysisRateAdapter);

        Log.i(TAG,"adjustFeagmentCount");
        Log.i(TAG,"state:"+state);
        if (state==0 && tv_report_track.getVisibility()== View.VISIBLE){
            subFormAlCount = 1;
            tv_report_track.setVisibility(View.GONE);
            mOneTableWidth = MyUtil.getScreeenWidth(this)/3;

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
            params.width = (int) (MyUtil.getScreeenWidth(this)/3);
            v_analysis_select.setLayoutParams(params);
        }
    }

    //点击时设置选中条目
    public void setViewPageItem(int viewPageItem,int currentItem) {
        viewPageItem = viewPageItem-subFormAlCount;
        if (currentItem==viewPageItem){
            return;
        }
        vp_insoleresult_content.setCurrentItem(viewPageItem);
        RelativeLayout.LayoutParams layoutParams =   (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
        int floatWidth= (int) (mOneTableWidth*viewPageItem);  //view向左的偏移量
        layoutParams.setMargins(floatWidth,mViewMarginTop,0,0); //4个参数按顺序分别是左上右下
        v_analysis_select.setLayoutParams(layoutParams);

        setViewPageTextColor(viewPageItem+subFormAlCount);
        Log.i(TAG,"setViewPageItem:"+viewPageItem+","+currentItem);

    }

    //设置文本颜色
    private void setViewPageTextColor(int viewPageItem) {
        switch (viewPageItem){
            case 0:
                tv_report_track.setTextColor(Color.parseColor("#0c64b5"));
                tv_report_details.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_speed.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_stride.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 1:
                tv_report_track.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_details.setTextColor(Color.parseColor("#0c64b5"));
                tv_report_speed.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_stride.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 2:
                tv_report_track.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_details.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_speed.setTextColor(Color.parseColor("#0c64b5"));
                tv_report_stride.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 3:
                tv_report_track.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_details.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_speed.setTextColor(Color.parseColor("#FFFFFF"));
                tv_report_stride.setTextColor(Color.parseColor("#0c64b5"));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_report_track:
                setViewPageItem(0,vp_insoleresult_content.getCurrentItem());
                break;
            case R.id.tv_report_details:
                setViewPageItem(1,vp_insoleresult_content.getCurrentItem());
                break;
            case R.id.tv_report_speed:
                setViewPageItem(2,vp_insoleresult_content.getCurrentItem());
                break;
            case R.id.tv_report_stride:
                setViewPageItem(3,vp_insoleresult_content.getCurrentItem());
                break;
        }
    }
}
