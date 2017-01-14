package com.amsu.healthy.activity;

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
        RelativeLayout rl_clubmore_info = (RelativeLayout) findViewById(R.id.rl_clubmore_info);
        RelativeLayout rl_clubmore_reginfo = (RelativeLayout) findViewById(R.id.rl_clubmore_reginfo);
        RelativeLayout rl_clubmore_shareclub = (RelativeLayout) findViewById(R.id.rl_clubmore_shareclub);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        rl_clubmore_info.setOnClickListener(myOnClickListener);
        rl_clubmore_reginfo.setOnClickListener(myOnClickListener);
        rl_clubmore_shareclub.setOnClickListener(myOnClickListener);
    }


    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_clubmore_info:

                break;
                case R.id.rl_clubmore_reginfo:

                    break;
                case R.id.rl_clubmore_shareclub:

                    break;

            }
        }
    }
}
