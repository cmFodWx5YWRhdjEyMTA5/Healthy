package com.amsu.healthy.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.view.DashboardView;

public class PhysicalAgeActivity extends BaseActivity {

    private DashboardView dv_main_compass;
    private ValueAnimator mValueAnimator;
    private int physicalAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_age);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("生理年龄");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dv_main_compass = (DashboardView) findViewById(R.id.dv_main_compass);
        TextView tv_physical_age = (TextView) findViewById(R.id.tv_physical_age);
        TextView tv_physical_agereal = (TextView) findViewById(R.id.tv_physical_agereal);

        Intent intent = getIntent();
        physicalAge = intent.getIntExtra("physicalAge", 0);
        physicalAge = 21;
        if (physicalAge >0){
            int userAge = HealthyIndexUtil.getUserAge();
            tv_physical_agereal.setText("实际年龄"+userAge);
            dv_main_compass.setAgeData(physicalAge -10);
            setAgeTextAnimator(tv_physical_age,0, physicalAge);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mValueAnimator!=null) {
            mValueAnimator.start();
            if (physicalAge >0){
                dv_main_compass.setAgeData(physicalAge-10);
            }
        }
    }

    //给文本年龄设置文字动画
    private void setAgeTextAnimator(final TextView textView,int startAge,int endAge) {
        mValueAnimator = ValueAnimator.ofInt(startAge, endAge);
        mValueAnimator.setDuration(Constant.AnimatorDuration);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(animation.getAnimatedValue().toString());
            }
        });
    }
}
