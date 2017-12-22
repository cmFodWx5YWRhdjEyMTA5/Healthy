package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;

public class ClubMoreActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_more);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("更多");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RelativeLayout rl_clubmore_info = (RelativeLayout) findViewById(R.id.rl_clubmore_info);
        RelativeLayout rl_clubmore_reginfo = (RelativeLayout) findViewById(R.id.rl_clubmore_reginfo);
        RelativeLayout rl_clubmore_shareclub = (RelativeLayout) findViewById(R.id.rl_clubmore_shareclub);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        rl_clubmore_info.setOnClickListener(myOnClickListener);
        rl_clubmore_reginfo.setOnClickListener(myOnClickListener);
        rl_clubmore_shareclub.setOnClickListener(myOnClickListener);
    }

    public void quitClub(View view) {

    }


    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_clubmore_info:
                    startActivity(new Intent(ClubMoreActivity.this,ClubMyInfoActivity.class));

                break;
                case R.id.rl_clubmore_reginfo:
                    startActivity(new Intent(ClubMoreActivity.this,ClubMyEnterInfoActivity.class));
                    break;
                case R.id.rl_clubmore_shareclub:

                    break;

            }
        }
    }
}
