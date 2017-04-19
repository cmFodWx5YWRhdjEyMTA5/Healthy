package com.amsu.healthy.fragment.report;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.MouthReportAdapter;
import com.amsu.healthy.fragment.report.mouth.EcgMouthFragment;
import com.amsu.healthy.fragment.report.mouth.HRRMouthFragment;
import com.amsu.healthy.fragment.report.mouth.HRVMouthFragment;
import com.amsu.healthy.fragment.report.mouth.HeartRatemouthFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MouthReprtFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "MouthReprtFragment";
    public View inflate;
    public TextView tv_report_text1;
    public TextView tv_report_text2;
    public TextView tv_report_text3;
    public TextView tv_report_text4;
    private List<Fragment> fragmentList;
    private ViewPager vp_report_content;

    public MouthReprtFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_mouth_reprt, container, false);

        Log.i(TAG,"onCreateView");
        initView();
        initData();
        return inflate;
    }




    public void initView() {
        tv_report_text1 = (TextView) inflate.findViewById(R.id.tv_report_text1);
        tv_report_text2 = (TextView) inflate.findViewById(R.id.tv_report_text2);
        tv_report_text3 = (TextView) inflate.findViewById(R.id.tv_report_text3);
        tv_report_text4 = (TextView) inflate.findViewById(R.id.tv_report_text4);

        tv_report_text1.setOnClickListener(this);
        tv_report_text2.setOnClickListener(this);
        tv_report_text3.setOnClickListener(this);
        tv_report_text4.setOnClickListener(this);

        vp_report_content = (ViewPager) inflate.findViewById(R.id.vp_report_content);


    }

    private void initData() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new HeartRatemouthFragment());
        fragmentList.add(new EcgMouthFragment());
        fragmentList.add(new HRRMouthFragment());
        //fragmentList.add(new HRVMouthFragment());

        FragmentActivity activity = getActivity();
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();

        vp_report_content.setAdapter(new MouthReportAdapter(supportFragmentManager,fragmentList));

        vp_report_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchTextState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_report_text1:
                switchTextState(0);
                vp_report_content.setCurrentItem(0);
                break;
            case R.id.tv_report_text2:
                switchTextState(1);
                vp_report_content.setCurrentItem(1);
                break;
            case R.id.tv_report_text3:
                switchTextState(2);
                vp_report_content.setCurrentItem(2);
                break;
            case R.id.tv_report_text4:
                switchTextState(3);
                vp_report_content.setCurrentItem(3);
                break;
        }
    }

    private void switchTextState(int position){
        switch (position){
            case 0:
                tv_report_text1.setBackgroundResource(R.drawable.bg_select_text);
                tv_report_text2.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text3.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text4.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text1.setTextColor(Color.parseColor("#0c64b5"));
                tv_report_text2.setTextColor(Color.parseColor("#999999"));
                tv_report_text3.setTextColor(Color.parseColor("#999999"));
                tv_report_text4.setTextColor(Color.parseColor("#999999"));
                break;
            case 1:
                tv_report_text1.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text2.setBackgroundResource(R.drawable.bg_select_text);
                tv_report_text3.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text4.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text1.setTextColor(Color.parseColor("#999999"));
                tv_report_text2.setTextColor(Color.parseColor("#0c64b5"));
                tv_report_text3.setTextColor(Color.parseColor("#999999"));
                tv_report_text4.setTextColor(Color.parseColor("#999999"));
                break;
            case 2:
                tv_report_text1.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text2.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text3.setBackgroundResource(R.drawable.bg_select_text);
                tv_report_text4.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text1.setTextColor(Color.parseColor("#999999"));
                tv_report_text2.setTextColor(Color.parseColor("#999999"));
                tv_report_text3.setTextColor(Color.parseColor("#0c64b5"));
                tv_report_text4.setTextColor(Color.parseColor("#999999"));
                break;
            case 3:
                tv_report_text1.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text2.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text3.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text4.setBackgroundResource(R.drawable.bg_select_text);
                tv_report_text1.setTextColor(Color.parseColor("#999999"));
                tv_report_text2.setTextColor(Color.parseColor("#999999"));
                tv_report_text3.setTextColor(Color.parseColor("#999999"));
                tv_report_text4.setTextColor(Color.parseColor("#0c64b5"));
                break;
        }
    }


}
