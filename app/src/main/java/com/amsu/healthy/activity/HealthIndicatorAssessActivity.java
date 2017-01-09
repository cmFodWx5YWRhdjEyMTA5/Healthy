package com.amsu.healthy.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.view.RadarView;
import com.amsu.healthy.view.SelectDialog;

public class HealthIndicatorAssessActivity extends BaseActivity {

    private static final String TAG = "HealthIndicatorAssess";
    private RadarView rc_assess_radar;
    private View shape_point_blue;
    private float pointMargin;
    private float pointWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_indicator_assess);



        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("健康指标评分");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setRightImage(R.drawable.plan_calendar);
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SelectDialog selectDialog = new SelectDialog(HealthIndicatorAssessActivity.this,R.style.dialog);//创建Dialog并设置样式主题

                //设置尺寸
                Window win = selectDialog.getWindow();
                WindowManager.LayoutParams params = win.getAttributes();
                win.setGravity(Gravity.RIGHT | Gravity.TOP);
                params.x = new Float(HealthIndicatorAssessActivity.this.getResources().getDimension(R.dimen.x28)).intValue() ;//设置x坐标
                params.y =  new Float(HealthIndicatorAssessActivity.this.getResources().getDimension(R.dimen.x148)).intValue();//设置y坐标
                win.setAttributes(params);

                String[] data = {"11月第1周","11月第2周","11月第3周","11月第4周","12月第1周"};
                selectDialog.setData(data);

                selectDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.i(TAG,"position:"+position);
                        selectDialog.dismiss();


                    }
                });



                selectDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                selectDialog.show();

            }
        });

        rc_assess_radar = (RadarView) findViewById(R.id.rc_assess_radar);

        float[] data1 = {170, 180, 160, 170, 180,100,120};
        float[] data2 = {80, 120, 140, 170, 150,140,100};
        float[] data3 = {170, 180,100,120,170, 180, 160};

        rc_assess_radar.setDatas(data1,data2,data3);

        rc_assess_radar.setMyItemOnClickListener(new RadarView.MyItemOnClickListener() {
            @Override
            public void onClick(int i) {
                Log.i(TAG,"onClick:"+i);
                showAssessDialog(i);
            }
        });
        
        
        

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void showAssessDialog(int x) {
        View inflate = View.inflate(this, R.layout.dialog_assess_type, null);
        ViewPager vp_assess_float = (ViewPager) inflate.findViewById(R.id.vp_assess_float);
        vp_assess_float.setAdapter(new MyViewPageAdapter());

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(inflate).create();
        alertDialog.show();

        float width = getResources().getDimension(R.dimen.x800);
        float height = getResources().getDimension(R.dimen.x860);

        alertDialog.getWindow().setLayout(new Float(width).intValue(),new Float(height).intValue());



        LinearLayout ll_point_group = (LinearLayout) inflate.findViewById(R.id.ll_point_group);
        shape_point_blue = inflate.findViewById(R.id.view_blue_point);


        //初始化引导页的小圆点
        for (int i=0;i<7;i++ ){
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
            return 7;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View inflate = View.inflate(HealthIndicatorAssessActivity.this, R.layout.view_viewpage_item,null);

            container.addView(inflate);
            return inflate;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
