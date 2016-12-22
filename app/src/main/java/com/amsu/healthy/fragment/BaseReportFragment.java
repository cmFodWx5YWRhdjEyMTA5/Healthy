package com.amsu.healthy.fragment;

import android.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;

/**
 * Created by HP on 2016/12/22.
 */
public class BaseReportFragment extends Fragment implements View.OnClickListener {
    public View inflate;
    public TextView tv_report_text1;
    public TextView tv_report_text2;
    public TextView tv_report_text3;
    public TextView tv_report_text4;


    public void initView() {
        tv_report_text1 = (TextView) inflate.findViewById(R.id.tv_report_text1);
        tv_report_text2 = (TextView) inflate.findViewById(R.id.tv_report_text2);
        tv_report_text3 = (TextView) inflate.findViewById(R.id.tv_report_text3);
        tv_report_text4 = (TextView) inflate.findViewById(R.id.tv_report_text4);

        tv_report_text1.setOnClickListener(this);
        tv_report_text2.setOnClickListener(this);
        tv_report_text3.setOnClickListener(this);
        tv_report_text4.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_report_text1:
                tv_report_text1.setBackgroundResource(R.drawable.bg_select_text);
                tv_report_text2.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text3.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text4.setBackgroundResource(R.drawable.bg_noselest_text);
                break;
            case R.id.tv_report_text2:
                tv_report_text1.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text2.setBackgroundResource(R.drawable.bg_select_text);
                tv_report_text3.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text4.setBackgroundResource(R.drawable.bg_noselest_text);
                break;
            case R.id.tv_report_text3:
                tv_report_text1.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text2.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text3.setBackgroundResource(R.drawable.bg_select_text);
                tv_report_text4.setBackgroundResource(R.drawable.bg_noselest_text);
                break;
            case R.id.tv_report_text4:
                tv_report_text1.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text2.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text3.setBackgroundResource(R.drawable.bg_noselest_text);
                tv_report_text4.setBackgroundResource(R.drawable.bg_select_text);
                break;
        }
    }
}
