package com.amsu.healthy.activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.AnalysisRateAdapter;
import com.amsu.healthy.fragment.community.ClubsFragment;
import com.amsu.healthy.fragment.community.MyCommunityFragment;
import com.amsu.healthy.fragment.community.NewsFragment;
import com.amsu.healthy.utils.MyUtil;

import java.util.ArrayList;

public class SportCommunityActivity extends BaseActivity {

    private static final String TAG = "SportCommunity";
    private ViewPager vp_sport_content;
    private ArrayList<Fragment> fragmentList;
    private View v_sport_select;
    private TextView tv_sport_news;
    private TextView tv_sport_clubs;
    private TextView tv_sport_mycommunity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_community);
        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setHeadBackgroudColor("#60afe7");
        setLeftImage(R.drawable.back_icon);
        setCenterText("运动社区");

        vp_sport_content = (ViewPager) findViewById(R.id.vp_sport_content);
        v_sport_select = findViewById(R.id.v_sport_select);
        tv_sport_news = (TextView) findViewById(R.id.tv_sport_news);
        tv_sport_clubs = (TextView) findViewById(R.id.tv_sport_clubs);
        tv_sport_mycommunity = (TextView) findViewById(R.id.tv_sport_mycommunity);

        MyClickListener myClickListener = new MyClickListener();
        tv_sport_news.setOnClickListener(myClickListener);
        tv_sport_clubs.setOnClickListener(myClickListener);
        tv_sport_mycommunity.setOnClickListener(myClickListener);



        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //每一个小格的宽度
        final float oneTableWidth = MyUtil.getScreeenWidth(this)/3;

        vp_sport_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i(TAG,"onPageScrolled===position:"+position+",positionOffset:"+positionOffset+",positionOffsetPixels:"+positionOffsetPixels);
                RelativeLayout.LayoutParams layoutParams =   (RelativeLayout.LayoutParams) v_sport_select.getLayoutParams();
                int floatWidth=  (int) (oneTableWidth *(positionOffset+position));  //view向左的偏移量
                layoutParams.setMargins(floatWidth,0,0,0); //4个参数按顺序分别是左上右下
                v_sport_select.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                //Log.i(TAG,"onPageSelected===position:"+position);
                setViewPageTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Log.i(TAG,"onPageScrollStateChanged===state:"+state);
            }
        });
    }

    private void initData() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new NewsFragment());
        fragmentList.add(new ClubsFragment());
        fragmentList.add(new MyCommunityFragment());

        vp_sport_content.setAdapter(new AnalysisRateAdapter(getSupportFragmentManager(),fragmentList));

    }




    class MyClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Log.i(TAG,"vp_sport_content.getCurrentItem():"+vp_sport_content.getCurrentItem());
            switch (v.getId()) {

                case R.id.tv_sport_news:
                    setViewPageItem(0,vp_sport_content.getCurrentItem());
                    break;
                case R.id.tv_sport_clubs:
                    setViewPageItem(1,vp_sport_content.getCurrentItem());

                    break;
                case R.id.tv_sport_mycommunity:
                    setViewPageItem(2,vp_sport_content.getCurrentItem());

                    break;
            }
        }
    }

    //点击时设置选中条目
    public void setViewPageItem(int viewPageItem,int currentItem) {
        if (currentItem==viewPageItem){
            return;
        }
        vp_sport_content.setCurrentItem(viewPageItem);
        float oneTableWidth = MyUtil.getScreeenWidth(this)/3;
        RelativeLayout.LayoutParams layoutParams =   (RelativeLayout.LayoutParams) v_sport_select.getLayoutParams();
        int floatWidth= (int) (oneTableWidth*viewPageItem);  //view向左的偏移量
        layoutParams.setMargins(floatWidth,0,0,0); //4个参数按顺序分别是左上右下
        v_sport_select.setLayoutParams(layoutParams);

        setViewPageTextColor(viewPageItem);

    }

    //设置文本颜色
    private void setViewPageTextColor(int viewPageItem) {
        switch (viewPageItem){
            case 0:
                tv_sport_news.setTextColor(Color.parseColor("#0c64b5"));
                tv_sport_clubs.setTextColor(Color.parseColor("#999999"));
                tv_sport_mycommunity.setTextColor(Color.parseColor("#999999"));
                break;
            case 1:
                tv_sport_news.setTextColor(Color.parseColor("#999999"));
                tv_sport_clubs.setTextColor(Color.parseColor("#0c64b5"));
                tv_sport_mycommunity.setTextColor(Color.parseColor("#999999"));
                break;
            case 2:
                tv_sport_news.setTextColor(Color.parseColor("#999999"));
                tv_sport_clubs.setTextColor(Color.parseColor("#999999"));
                tv_sport_mycommunity.setTextColor(Color.parseColor("#0c64b5"));
                break;
        }
    }


}
