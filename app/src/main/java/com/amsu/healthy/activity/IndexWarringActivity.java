package com.amsu.healthy.activity;

import android.app.AlertDialog;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.utils.HealthyIndexUtil;

import java.util.ArrayList;
import java.util.List;

public class IndexWarringActivity extends BaseActivity {

    private ProgressBar pb_hrv_ratelow;
    private ProgressBar pb_hrv_rateover;
    private ProgressBar pb_hrv_morningrate;
    private ProgressBar pb_hrv_leaverate;
    private int mProgressNormal;
    private int mProgressYellow;
    private int mProgressRed;
    private AlertDialog mAlertDialog;
    private ViewPager vp_assess_float;
    private View shape_point_blue;
    private float pointMargin;
    private float pointWidth;
    private List<IndicatorAssess> indicatorAssesses;
    private TextView tv_warring_state_slow;
    private TextView tv_warring_state_over;
    private TextView tv_warring_state_morningrate;
    private TextView tv_warring_state_leaverate;
    private TextView tv_hrv_ala4;
    private String mSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_warring);

        initView();
        initData();
    }



    private void initView() {
        initHeadView();
        setCenterText("健康预警");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pb_hrv_ratelow = (ProgressBar) findViewById(R.id.pb_hrv_ratelow);
        pb_hrv_rateover = (ProgressBar) findViewById(R.id.pb_hrv_rateover);
        pb_hrv_morningrate = (ProgressBar) findViewById(R.id.pb_hrv_morningrate);
        pb_hrv_leaverate = (ProgressBar) findViewById(R.id.pb_hrv_leaverate);

        tv_warring_state_slow = (TextView) findViewById(R.id.tv_warring_state_slow);
        tv_warring_state_over = (TextView) findViewById(R.id.tv_warring_state_over);
        tv_warring_state_morningrate = (TextView) findViewById(R.id.tv_warring_state_morningrate);
        tv_warring_state_leaverate = (TextView) findViewById(R.id.tv_warring_state_leaverate);

        tv_hrv_ala4 = (TextView) findViewById(R.id.tv_hrv_ala4);

        LinearLayout ll_warring_slow = (LinearLayout) findViewById(R.id.ll_warring_slow);
        LinearLayout ll_warring_over = (LinearLayout) findViewById(R.id.ll_warring_over);
        LinearLayout ll_warring_morningrate = (LinearLayout) findViewById(R.id.ll_warring_morningrate);
        LinearLayout ll_warring_leaverate = (LinearLayout) findViewById(R.id.ll_warring_leaverate);

        MyOcClickListener myOcClickListener = new MyOcClickListener();
        ll_warring_slow.setOnClickListener(myOcClickListener);
        ll_warring_over.setOnClickListener(myOcClickListener);
        ll_warring_morningrate.setOnClickListener(myOcClickListener);
        ll_warring_leaverate.setOnClickListener(myOcClickListener);
    }

    private void initData() {
        IndicatorAssess indicatorAssess1 = HealthyIndexUtil.calculateTypeSlow();
        IndicatorAssess indicatorAssess2 = HealthyIndexUtil.calculateTypeOver();
        IndicatorAssess indicatorAssess3 = HealthyIndexUtil.calculateTypeBeforeBeat();
        IndicatorAssess indicatorAssess4 = HealthyIndexUtil.calculateTypeMissBeat();

        indicatorAssesses = new ArrayList<>();

        mProgressNormal = 20;
        mProgressYellow = 60;
        mProgressRed = 80;
        mSuggestion = "";

        if (indicatorAssess1!=null){
            indicatorAssesses.add(indicatorAssess1);
            setState(indicatorAssess1.getPercent(),pb_hrv_ratelow,tv_warring_state_slow);
            mSuggestion += indicatorAssess1.getSuggestion();
        }
        if (indicatorAssess2!=null){
            indicatorAssesses.add(indicatorAssess2);
            setState(indicatorAssess2.getPercent(),pb_hrv_rateover,tv_warring_state_over);
            mSuggestion += indicatorAssess2.getSuggestion();
        }
        if (indicatorAssess3!=null){
            indicatorAssesses.add(indicatorAssess3);
            setState(indicatorAssess3.getPercent(),pb_hrv_morningrate,tv_warring_state_morningrate);
            mSuggestion += indicatorAssess3.getSuggestion();
        }
        if (indicatorAssess4!=null){
            indicatorAssesses.add(indicatorAssess4);
            setState(indicatorAssess4.getPercent(),pb_hrv_leaverate,tv_warring_state_leaverate);
            mSuggestion += indicatorAssess4.getSuggestion();
        }

        tv_hrv_ala4.setText(mSuggestion);

    }

    private void setState(int score,ProgressBar progressBar,TextView textView) {
        switch (score){
            case 0:
                progressBar.setProgress(mProgressNormal);
                textView.setText("正常");
                break;
            case 1:
                progressBar.setProgress(mProgressYellow);
                textView.setText("黄色预警");
                break;
            case 2:
                progressBar.setProgress(mProgressRed);
                textView.setText("红色预警");
                break;
        }
    }

    class MyOcClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_warring_slow:
                    showAssessDialog(0);
                    break;
                case R.id.ll_warring_over:
                    showAssessDialog(1);
                    break;
                case R.id.ll_warring_morningrate:
                    showAssessDialog(2);
                    break;
                case R.id.ll_warring_leaverate:
                    showAssessDialog(3);
                    break;

            }
        }
    }

    private void showAssessDialog(int index) {
        if (mAlertDialog==null){
            initItemViewPageView();
            vp_assess_float.setCurrentItem(index);
        }
        else {
            vp_assess_float.setCurrentItem(index);
            mAlertDialog.show();
        }
    }

    private void initItemViewPageView() {
        View inflate = View.inflate(this, R.layout.dialog_assess_type, null);
        vp_assess_float = (ViewPager) inflate.findViewById(R.id.vp_assess_float);
        vp_assess_float.setAdapter(new MyViewPageAdapter());
        ;

        mAlertDialog = new AlertDialog.Builder(this).setView(inflate).create();
        mAlertDialog.show();

        float width = getResources().getDimension(R.dimen.x800);
        float height = getResources().getDimension(R.dimen.x860);

        mAlertDialog.getWindow().setLayout(new Float(width).intValue(),new Float(height).intValue());



        LinearLayout ll_point_group = (LinearLayout) inflate.findViewById(R.id.ll_point_group);
        shape_point_blue = inflate.findViewById(R.id.view_blue_point);


        //初始化引导页的小圆点
        for (int i=0;i<4;i++ ){
            View point = new View(this);
            point.setBackgroundResource(R.drawable.shape_point_gray);
            pointWidth = getResources().getDimension(R.dimen.x16);
            pointMargin = getResources().getDimension(R.dimen.x12);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new Float(pointWidth).intValue(),new Float(pointWidth).intValue());
            if (i>0){
                //设置圆点间隔
                params.leftMargin = new Float(pointMargin).intValue() ;
            }
            //设置圆点大小
            point.setLayoutParams(params);
            //将圆点添加给线性布局
            ll_point_group.addView(point);
        }


        vp_assess_float.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int marginFloat = new Float(pointMargin).intValue() + new Float(pointWidth).intValue();

                int len =  (int) (marginFloat * positionOffset) + position*marginFloat;
                //获取当前红点的布局参数
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) shape_point_blue.getLayoutParams();
                //设置左边距
                params.leftMargin = len ;

                //重新给小蓝点设置布局参数
                shape_point_blue.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 7 -1){
                    //bt_start.setVisibility(View.VISIBLE);
                }else {
                    //bt_start.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    class MyViewPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return indicatorAssesses.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            IndicatorAssess indicatorAssess = indicatorAssesses.get(position);

            View inflate = View.inflate(IndexWarringActivity.this, R.layout.view_viewpage_item,null);
            TextView tv_item_value = (TextView) inflate.findViewById(R.id.tv_item_value);
            TextView tv_item_typeName = (TextView) inflate.findViewById(R.id.tv_item_typeName);
            TextView tv_item_suggestion = (TextView) inflate.findViewById(R.id.tv_item_suggestion);

            /*int score = indicatorAssess.getPercent();
            String warring = "";
            if (score==0){
                warring = "正常";
            }
            else if (score==1){
                warring = "黄色预警";
            }
            else if (score==2){
                warring = "红色预警";
            }*/
            if (indicatorAssess!=null){
                tv_item_value.setText(indicatorAssess.getEvaluate());
                tv_item_typeName.setText(indicatorAssess.getName());
                tv_item_suggestion.setText(indicatorAssess.getSuggestion());
            }

            container.addView(inflate);
            return inflate;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
