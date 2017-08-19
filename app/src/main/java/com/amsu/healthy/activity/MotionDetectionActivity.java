package com.amsu.healthy.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;

/**
 * Created by HP on 2017/4/5.
 */

public class MotionDetectionActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "MotionDetectionActivity";
    private ImageView iv_detction_cloth;
    private ImageView iv_detction_insole;
    private ImageView iv_detction_insole1;

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

        RelativeLayout rl_dection_cloth = (RelativeLayout) findViewById(R.id.rl_dection_cloth);
        RelativeLayout rl_dection_insole = (RelativeLayout) findViewById(R.id.rl_dection_insole);

        rl_dection_cloth.setOnClickListener(this);
        rl_dection_insole.setOnClickListener(this);


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

        switch (v.getId()){
            case R.id.rl_dection_cloth:
                switchSelectedState(Constant.sportType_Cloth);
                break;
            case R.id.rl_dection_insole:
                switchSelectedState(Constant.sportType_Insole);
                break;
        }
    }

    private void switchSelectedState(int type) {
        if (type==mSportType){return;}

        switch (type){
            case Constant.sportType_Cloth:
                iv_detction_cloth.setBackgroundResource(R.drawable.bg_center_circle);
                iv_detction_insole1.setBackgroundResource(R.drawable.bg_sport_type);
                /*if (mSportType!=-1){
                    MyUtil.showToask(this,"衣服切换成功");
                }*/
                break;
            case Constant.sportType_Insole:
                iv_detction_cloth.setBackgroundResource(R.drawable.bg_sport_type);
                iv_detction_insole1.setBackgroundResource(R.drawable.bg_center_circle);
                /*if (mSportType!=-1){
                    MyUtil.showToask(this,"鞋垫切换成功");
                }*/
                break;
        }
        mSportType = type;
        MyUtil.putIntValueFromSP(Constant.sportType,mSportType);
        MyApplication.deivceType = mSportType;
    }
}
