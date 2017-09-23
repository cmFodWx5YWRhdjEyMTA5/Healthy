package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.FragmentListRateAdapter;
import com.amsu.healthy.fragment.historyrecord.ClothHistoryRecordFragment;
import com.amsu.healthy.fragment.historyrecord.InsoleHistoryRecordFragment;

import java.util.ArrayList;
import java.util.List;

public class HistoryRecordAllActivity extends BaseActivity {

    private TextView tv_consult_cloth;
    private TextView tv_consult_insole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record_all);

        initView();
    }

    private void initView() {
        ImageView iv_base_leftimage = (ImageView) findViewById(R.id.iv_base_leftimage);
        ImageView iv_base_rightimage = (ImageView) findViewById(R.id.iv_base_rightimage);

        iv_base_leftimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_base_rightimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRecordAllActivity.this,ConnectToWifiGudieActivity1.class));
            }
        });

        //vp_historyall_content = (ViewPager) findViewById(R.id.vp_historyall_content);

        tv_consult_cloth = (TextView) findViewById(R.id.tv_consult_cloth);
        tv_consult_insole = (TextView) findViewById(R.id.tv_consult_insole);

        MySwitchListener mySwitchListener = new MySwitchListener();
        tv_consult_cloth.setOnClickListener(mySwitchListener);
        tv_consult_insole.setOnClickListener(mySwitchListener);


        /*fragmentList = new ArrayList<>();
        fragmentList.add(new ClothHistoryRecordFragment());
        fragmentList.add(new InsoleHistoryRecordFragment());
*/
       /* FragmentListRateAdapter analysisRateAdapter = new FragmentListRateAdapter(getSupportFragmentManager(), fragmentList);
        vp_historyall_content.setAdapter(analysisRateAdapter);*/


    }

    private class  MySwitchListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_consult_cloth:
                    switchState(0);
                    break;
                case R.id.tv_consult_insole:
                    switchState(1);
                    break;
            }
        }
    }

    public void switchState(int viewPageItem){
        FragmentManager supportFragmentManager = getSupportFragmentManager();


        FragmentTransaction fragmentTransaction =  supportFragmentManager.beginTransaction();
        ClothHistoryRecordFragment clothHistoryRecordFragment = (ClothHistoryRecordFragment) supportFragmentManager.findFragmentById(R.id.fg_history_clonth);
        InsoleHistoryRecordFragment insoleHistoryRecordFragment = (InsoleHistoryRecordFragment) supportFragmentManager.findFragmentById(R.id.fg_history_insole);

        switch (viewPageItem){
            case 0:
                tv_consult_cloth.setTextColor(Color.parseColor("#FFFFFF"));
                tv_consult_cloth.setBackgroundResource(R.drawable.bg_care_switch_lefton);
                tv_consult_insole.setTextColor(Color.parseColor("#FFFFFF"));
                tv_consult_insole.setBackgroundResource(R.drawable.bg_care_switch_rightoff);

                fragmentTransaction.hide(insoleHistoryRecordFragment);
                fragmentTransaction.show(clothHistoryRecordFragment);
                fragmentTransaction.commit();
                break;

            case 1:
                tv_consult_cloth.setTextColor(Color.parseColor("#FFFFFF"));
                tv_consult_cloth.setBackgroundResource(R.drawable.bg_care_switch_leftoff);
                tv_consult_insole.setTextColor(Color.parseColor("#FFFFFF"));
                tv_consult_insole.setBackgroundResource(R.drawable.bg_care_switch_righton);


                fragmentTransaction.hide(clothHistoryRecordFragment);
                fragmentTransaction.show(insoleHistoryRecordFragment);
                fragmentTransaction.commit();
                break;
            default:
                break;
        }
    }

}
