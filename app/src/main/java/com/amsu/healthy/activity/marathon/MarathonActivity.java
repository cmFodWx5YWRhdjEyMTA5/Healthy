package com.amsu.healthy.activity.marathon;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.RunTimeCountdownActivity;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;

/**
 * author：WangLei
 * date:2017/10/24.
 * QQ:619321796
 * 马拉松
 */

public class MarathonActivity extends BaseActivity implements View.OnClickListener {
    public static Intent createIntent(Context context) {
        return new Intent(context, MarathonActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marathon);
        initHeadView();
        setLeftImage(R.drawable.back_icon);
        setCenterText(getResources().getString(R.string.marathon_sport));
        initEvents();
    }

    private void initEvents() {
        findViewById(R.id.ll_endurance).setOnClickListener(this);
        findViewById(R.id.ll_roadwork).setOnClickListener(this);
        findViewById(R.id.ll_histories).setOnClickListener(this);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_endurance:
                startActivity(EnduranceTestActivity.createIntent(this));
                break;
            case R.id.ll_roadwork:
                test();
                break;
            case R.id.ll_histories:
                startActivity(SportRecordActivity.createIntent(this));
                break;
        }
    }

    private void test() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            MyUtil.chooseOpenGps(this);
        } else {
            Intent intent = new Intent(this, RunTimeCountdownActivity.class);
            intent.putExtra(Constant.mIsOutDoor, true);
            intent.putExtra(Constant.sportType, Constant.sportType_Cloth);
            startActivity(intent);
        }
    }

}
