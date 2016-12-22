package com.amsu.healthy.activity;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.fragment.MouthReprtFragment;
import com.amsu.healthy.fragment.QuarterReprtFragment;
import com.amsu.healthy.fragment.YearReprtFragment;

public class MyReportActivity extends BaseActivity {

    private static final String TAG = "MyReportActivity";
    private FragmentTransaction fragmentTransaction;
    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private View v_analysis_select;
    private TextView tv_report_mouth;
    private TextView tv_report_quarter;
    private TextView tv_report_year;
    private MouthReprtFragment mouthReprtFragment;
    private QuarterReprtFragment quarterReprtFragment;
    private YearReprtFragment yearReprtFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report);

        initView();

    }

    private void initView() {
        initHeadView();
        setCenterText("我的报告");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_report_mouth = (TextView) findViewById(R.id.tv_report_mouth);
        tv_report_quarter = (TextView) findViewById(R.id.tv_report_quarter);
        tv_report_year = (TextView) findViewById(R.id.tv_report_year);
        v_analysis_select = (View) findViewById(R.id.v_analysis_select);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        tv_report_mouth.setOnClickListener(myOnClickListener);
        tv_report_quarter.setOnClickListener(myOnClickListener);
        tv_report_year.setOnClickListener(myOnClickListener);


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        mouthReprtFragment = new MouthReprtFragment();
        quarterReprtFragment = new QuarterReprtFragment();
        yearReprtFragment = new YearReprtFragment();

        currentFragment = mouthReprtFragment;
        addOrShowFragment(fragmentTransaction,currentFragment);


    }

    //Fragment切换
    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        if (currentFragment == fragment)
            return;
        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragment).add(R.id.fragment_content, fragment).commit();
        } else {
            transaction.hide(currentFragment).show(fragment).commit();
        }
        currentFragment = fragment;
    }



    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            fragmentTransaction = fragmentManager.beginTransaction();  //Fragment事务，需要每次切换时重新赋值，不然会出现重复提交错误
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
            int measuredWidth = v_analysis_select.getMeasuredWidth();

            int topMargin = layoutParams.topMargin;
            switch (v.getId()){
                case R.id.tv_report_mouth:
                    addOrShowFragment(fragmentTransaction,mouthReprtFragment);
                    layoutParams.setMargins(0,topMargin,0,0); //4个参数按顺序分别是左上右下
                    tv_report_mouth.setTextColor(Color.parseColor("#0c64b5"));
                    tv_report_quarter.setTextColor(Color.parseColor("#999999"));
                    tv_report_year.setTextColor(Color.parseColor("#999999"));
                    break;
                case R.id.tv_report_quarter:
                    addOrShowFragment(fragmentTransaction,quarterReprtFragment);
                    layoutParams.setMargins(measuredWidth,topMargin,0,0); //4个参数按顺序分别是左上右下
                    tv_report_mouth.setTextColor(Color.parseColor("#999999"));
                    tv_report_quarter.setTextColor(Color.parseColor("#0c64b5"));
                    tv_report_year.setTextColor(Color.parseColor("#999999"));
                    break;
                case R.id.tv_report_year:
                    addOrShowFragment(fragmentTransaction,yearReprtFragment);
                    layoutParams.setMargins(measuredWidth*2,topMargin,0,0); //4个参数按顺序分别是左上右下
                    tv_report_mouth.setTextColor(Color.parseColor("#999999"));
                    tv_report_quarter.setTextColor(Color.parseColor("#999999"));
                    tv_report_year.setTextColor(Color.parseColor("#0c64b5"));
                    break;

            }
            v_analysis_select.setLayoutParams(layoutParams);
        }
    }


}
