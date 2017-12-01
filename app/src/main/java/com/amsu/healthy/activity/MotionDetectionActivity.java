package com.amsu.healthy.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;

import static com.amsu.healthy.utils.Constant.isMarathonSportType;

/**
 * Created by HP on 2017/4/5.
 * 运动模式
 */

public class MotionDetectionActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MotionDetectionActivity";
    private ImageView iv_detction_cloth;
    private ImageView iv_detction_insole;
    private ImageView iv_detction_insole1;
    private ImageView iv_detction_marathon;

    private int mSportType = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_detction);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.active_mode));
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_detction_cloth = (ImageView) findViewById(R.id.iv_detction_cloth);
        iv_detction_insole1 = (ImageView) findViewById(R.id.iv_detction_insole);
        iv_detction_marathon = (ImageView) findViewById(R.id.iv_detction_marathon);

        RelativeLayout rl_dection_cloth = (RelativeLayout) findViewById(R.id.rl_dection_cloth);
        RelativeLayout rl_dection_insole = (RelativeLayout) findViewById(R.id.rl_dection_insole);
        RelativeLayout rl_marathon = (RelativeLayout) findViewById(R.id.rl_marathon);

        rl_dection_cloth.setOnClickListener(this);
        rl_dection_insole.setOnClickListener(this);
        rl_marathon.setOnClickListener(this);


        mSportType = MyUtil.getIntValueFromSP(Constant.sportType);
        switchSelectedState(MyApplication.deivceType);

    }

   /* public void defaultOnclick(View view) {
        MyUtil.showToask(this,"功能还在开发中");

    }*/

    @Override
    public void onClick(View v) {

        /*AlertDialog alertDialog = new AlertDialog.Builder(MotionDetectionActivity.this)

                .setTitle("切换成功")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();*/

        MyUtil.putBooleanValueFromSP(isMarathonSportType, false);

        switch (v.getId()) {
            case R.id.rl_dection_cloth:
                switchSelectedState(Constant.sportType_Cloth);
                break;
            case R.id.rl_dection_insole:
                switchSelectedState(Constant.sportType_Insole);
                break;
            case R.id.rl_marathon:
                MyUtil.putBooleanValueFromSP(isMarathonSportType, true);
                switchSelectedState(Constant.sportType_Cloth);
                break;
        }
    }

    int choosedType = -1;
    int preChoosedType = -1;

    private void switchSelectedState(int type) {
        switch (type) {
            case Constant.sportType_Cloth:
                iv_detction_cloth.setBackgroundResource(R.drawable.bg_center_circle);
                iv_detction_insole1.setBackgroundResource(R.drawable.bg_sport_type);
                iv_detction_marathon.setBackgroundResource(R.drawable.bg_sport_type);

                choosedType = 1;

                boolean is = MyUtil.getBooleanValueFromSP(isMarathonSportType);
                if (is) {
                    iv_detction_cloth.setBackgroundResource(R.drawable.bg_sport_type);
                    iv_detction_insole1.setBackgroundResource(R.drawable.bg_sport_type);
                    iv_detction_marathon.setBackgroundResource(R.drawable.bg_center_circle);
                    choosedType = 2;
                }
                break;

            case Constant.sportType_Insole:
                iv_detction_cloth.setBackgroundResource(R.drawable.bg_sport_type);
                iv_detction_insole1.setBackgroundResource(R.drawable.bg_center_circle);
                iv_detction_marathon.setBackgroundResource(R.drawable.bg_sport_type);
                choosedType = 3;
                break;
        }


        mSportType = type;
        MyUtil.putIntValueFromSP(Constant.sportType, mSportType);
        MyApplication.deivceType = mSportType;

        if (preChoosedType!=-1 && preChoosedType != choosedType){
            MyUtil.showToask(getApplication(),"运动模式切换成功");
        }
        preChoosedType = choosedType;

    }

    boolean isFirst = true;

}
