package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.PickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ChooseWeekActivity extends BaseActivity {

    private static final String TAG = "ChooseWeekActivity";
    private List<String> yearStringList;
    private List<String> weekStringList;
    private PickerView pv_choose_week;
    private String year ;
    private String mChoosedweek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_week);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("选择日期");
        setLeftText("取消");
        setRightText("完成");

        getTv_base_leftText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyUtil.isEmpty(mChoosedweek)){
                    String[] split = mChoosedweek.split(" — ")[0].split("\\.");

                    System.out.println(split.length);
                    int mouth = Integer.parseInt(split[0])-1;
                    int day = Integer.parseInt(split[1]);



                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR,Integer.parseInt(year));
                    calendar.set(Calendar.MONTH,mouth);
                    calendar.set(Calendar.DATE,day);
                    int currWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

                    Log.i(TAG,"year:"+year+" ,currWeekOfYear:"+currWeekOfYear);

                    Intent intent = getIntent();
                    intent.putExtra("year",Integer.parseInt(year));
                    intent.putExtra("currWeekOfYear",currWeekOfYear);
                    intent.putExtra("mChoosedweek", mChoosedweek);
                    setResult(RESULT_OK,intent);
                }
                finish();
            }
        });

        yearStringList = new ArrayList<>();
        weekStringList = new ArrayList<>();

        PickerView pv_choose_year = (PickerView) findViewById(R.id.pv_choose_year);
        pv_choose_week = (PickerView) findViewById(R.id.pv_choose_week);

        final Calendar calendar = Calendar.getInstance();
        int currYear = calendar.get(Calendar.YEAR);

        yearStringList.add(currYear-1+"");
        yearStringList.add(currYear+"");
        yearStringList.add("");

        year = currYear+"";

        pv_choose_year.setData(yearStringList);

        pv_choose_year.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.i(TAG,"选择了年："+yearStringList.get(position));
                if (!MyUtil.isEmpty(yearStringList.get(position))){
                    weekStringList.clear();
                    List<String> weeks;
                    int tempYear = Integer.parseInt(yearStringList.get(position));
                    year = tempYear+"";
                    int i = calendar.get(Calendar.YEAR);
                    if (tempYear== i){
                        int currWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
                        weeks = MyUtil.getWeekStringList(currWeekOfYear);
                    }
                    else {
                       weeks = MyUtil.getWeekStringList(52,tempYear);
                    }
                    setYearWeekData(weeks);
                }
            }
        });

        int currWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        List<String> oneYearweeks = MyUtil.getWeekStringList(currWeekOfYear);

        Log.i(TAG,"oneYearweeks:"+oneYearweeks.size());
        Log.i(TAG,"oneYearweeks:"+oneYearweeks);

        setYearWeekData(oneYearweeks);

        pv_choose_week.setData(weekStringList);
        pv_choose_week.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.i(TAG,"选择了周："+weekStringList.get(position));
                //mChoosedweek = weekStringList.get(position));
                mChoosedweek = weekStringList.get(position);
            }
        });


        if (weekStringList.size()>0){
            mChoosedweek = weekStringList.get(weekStringList.size() / 2);
            Log.i(TAG,"mChoosedweek:"+mChoosedweek);
        }

    }

    private void setYearWeekData(List<String> oneYearweeks) {
        if (oneYearweeks==null || oneYearweeks.size()==0)return;
        weekStringList.addAll(oneYearweeks);
        Collections.reverse(weekStringList);
        weekStringList.add("");
        weekStringList.add("");
        weekStringList.add("");
        weekStringList.add("");
        weekStringList.add("");
        weekStringList.add("");
        weekStringList.add("");
        weekStringList.add("");
        weekStringList.add("");
        weekStringList.add("");
        weekStringList.add("");

        pv_choose_week.setData(weekStringList);
    }

}
