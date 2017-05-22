package com.amsu.healthy.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IndexWarringActivity extends BaseActivity {

    private static final String TAG = "IndexWarringActivity";
    private ProgressBar pb_hrv_ratelow;
    private ProgressBar pb_hrv_rateover;
    private ProgressBar pb_hrv_morningrate;
    private ProgressBar pb_hrv_leaverate;
    private int mProgressNormal = 0;
    private int mProgressYellow = 10;
    private int mProgressRed = 30;
    private AlertDialog mAlertDialog;
    private ViewPager vp_assess_float;
    private View shape_point_blue;
    private float pointMargin;
    private float pointWidth;
    private List<IndicatorAssess> indicatorAssesses;
    private TextView tv_warring_state_slow;
    private TextView tv_warring_state_over;
    private TextView tv_warring_state_morningrate;
    private TextView tv_warring_state_leaverate;
    private TextView tv_hrv_suggestion;
    private String mSuggestion = "";
    private ListView lv_wring_fromdata;
    private List<HealthIndicatorAssessActivity.WeekReport.WeekReportResult.HistoryRecordItem> weekAllHistoryRecords;
    private ArrayList<HealthIndicatorAssessActivity.WeekReport.WeekReportResult.HistoryRecordItem> staticStateHistoryRecords;
    private MyListViewAdapter myListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_warring);

        initView();
        initData();
    }



    private void initView() {
        initHeadView();
        setCenterText("健康预警");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pb_hrv_ratelow = (ProgressBar) findViewById(R.id.pb_hrv_ratelow);
        pb_hrv_rateover = (ProgressBar) findViewById(R.id.pb_hrv_rateover);
        pb_hrv_morningrate = (ProgressBar) findViewById(R.id.pb_hrv_morningrate);
        pb_hrv_leaverate = (ProgressBar) findViewById(R.id.pb_hrv_leaverate);

        tv_warring_state_slow = (TextView) findViewById(R.id.tv_warring_state_slow);
        tv_warring_state_over = (TextView) findViewById(R.id.tv_warring_state_over);
        tv_warring_state_morningrate = (TextView) findViewById(R.id.tv_warring_state_morningrate);
        tv_warring_state_leaverate = (TextView) findViewById(R.id.tv_warring_state_leaverate);

        tv_hrv_suggestion = (TextView) findViewById(R.id.tv_hrv_suggestion);
        lv_wring_fromdata = (ListView) findViewById(R.id.lv_wring_fromdata);

        LinearLayout ll_warring_slow = (LinearLayout) findViewById(R.id.ll_warring_slow);
        LinearLayout ll_warring_over = (LinearLayout) findViewById(R.id.ll_warring_over);
        LinearLayout ll_warring_morningrate = (LinearLayout) findViewById(R.id.ll_warring_morningrate);
        LinearLayout ll_warring_leaverate = (LinearLayout) findViewById(R.id.ll_warring_leaverate);

        /*MyOcClickListener myOcClickListener = new MyOcClickListener();
        ll_warring_slow.setOnClickListener(myOcClickListener);
        ll_warring_over.setOnClickListener(myOcClickListener);
        ll_warring_morningrate.setOnClickListener(myOcClickListener);
        ll_warring_leaverate.setOnClickListener(myOcClickListener);*/
        indicatorAssesses = new ArrayList<>();
        weekAllHistoryRecords = new ArrayList<>();
        staticStateHistoryRecords = new ArrayList<>();

        myListViewAdapter = new MyListViewAdapter();
        lv_wring_fromdata.setAdapter(myListViewAdapter);

    }

    private void initData() {
        downlaodWeekRepore(-1,-1);
    }

    private void downlaodWeekRepore(int year,int weekOfYear) {
        MyUtil.showDialog("加载数据",this);
        Log.i(TAG,"year:"+year+"  weekOfYear:"+weekOfYear);
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        if (year!=-1){
            params.addBodyParameter("year",year+"");
        }
        if (weekOfYear!=-1){
            params.addBodyParameter("week",weekOfYear+"");
        }
        MyUtil.addCookieForHttp(params);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.downloadWeekReportURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog();

                String result = responseInfo.result;
                Log.i(TAG, "上传onSuccess==result:" + result);
                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG, "jsonBase:" + jsonBase);
                if (jsonBase.getRet() == 0) {
                    HealthIndicatorAssessActivity.WeekReport weekReport = gson.fromJson(result, HealthIndicatorAssessActivity.WeekReport.class);
                    Log.i(TAG, "weekReport:" + weekReport.toString());
                    if (weekReport!=null ){
                        setIndicatorData(weekReport);
                    }

                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog();

                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }

    public void setIndicatorData(HealthIndicatorAssessActivity.WeekReport weekReport) {
        List<String> guosuguohuan = weekReport.errDesc.guosuguohuan;

        /*for (int i=0;i<guosuguohuan.size();i++){
            IndicatorAssess indicatorAssess1 = HealthyIndexUtil.calculateTypeSlow(Integer.parseInt(guosuguohuan.get(i)));
            IndicatorAssess indicatorAssess2 = HealthyIndexUtil.calculateTypeOver(Integer.parseInt(guosuguohuan.get(i)));
            if (i==0){
                scoreSlow = indicatorAssess1;
                scoreOver = indicatorAssess2;
            }
            else{
                if (indicatorAssess1.getPercent()!=0){
                    scoreSlow = indicatorAssess1;
                    scoreSlowCount ++;
                }
                if (indicatorAssess2.getPercent()!=0){
                    scoreOver = indicatorAssess2;
                    scoreOverCount++;
                }
            }
        }*/



        IndicatorAssess scoreBeforeBeat = null;
        IndicatorAssess scoreMissBeat = null;
        List<Integer> zaoboloubo = weekReport.errDesc.zaoboloubo;
        if (zaoboloubo!=null && zaoboloubo.size()>1){
            int zaobo  = zaoboloubo.get(0);
            int loubo  = zaoboloubo.get(1);
            if (zaobo<0){
                zaobo = 0;
            }
            if (loubo<0){
                loubo = 0;
            }
            //早搏 包括房早搏APB和室早搏VPB，两者都记为早搏(心电分析算法得出)
            scoreBeforeBeat = HealthyIndexUtil.calculateTypeBeforeBeat(zaobo);
            scoreMissBeat = HealthyIndexUtil.calculateTypeMissBeat(loubo);
        }

        //过缓/过速(心电分析算法得出)
        IndicatorAssess scoreSlow = null;  //过缓
        IndicatorAssess scoreOver = null;  //过速
        int scoreSlowCount = 0;
        int scoreOverCount = 0;

        boolean isFirst =true;
        weekAllHistoryRecords = weekReport.errDesc.list;

        for (HealthIndicatorAssessActivity.WeekReport.WeekReportResult.HistoryRecordItem historyRecordItem:weekAllHistoryRecords){
            Log.i(TAG,"historyRecordItem:"+historyRecordItem.toString());
        }

        for (int i=0;i<weekAllHistoryRecords.size();i++){
            HealthIndicatorAssessActivity.WeekReport.WeekReportResult.HistoryRecordItem historyRecord = weekAllHistoryRecords.get(i);
            if (Integer.parseInt(historyRecord.state)==0){
                staticStateHistoryRecords.add(historyRecord);

                IndicatorAssess indicatorAssess1 = HealthyIndexUtil.calculateTypeSlow(Integer.parseInt(guosuguohuan.get(i)));
                IndicatorAssess indicatorAssess2 = HealthyIndexUtil.calculateTypeOver(Integer.parseInt(guosuguohuan.get(i)));
                if (isFirst){
                    scoreSlow = indicatorAssess1;
                    scoreOver = indicatorAssess2;
                    isFirst = false;
                }
                else{
                    if (indicatorAssess1.getPercent()!=0){
                        scoreSlow = indicatorAssess1;
                        scoreSlowCount ++;
                    }
                    if (indicatorAssess2.getPercent()!=0){
                        scoreOver = indicatorAssess2;
                        scoreOverCount++;
                    }
                }
            }
        }

        if (guosuguohuan!=null&& guosuguohuan.size()==0 && zaoboloubo!=null && zaoboloubo.size()==0){
            return;
        }

        myListViewAdapter.notifyDataSetChanged();



        for (HealthIndicatorAssessActivity.WeekReport.WeekReportResult.HistoryRecordItem historyRecordItem:staticStateHistoryRecords){
            Log.i(TAG,"staticStateHistory:"+historyRecordItem.toString());
        }

        Log.i(TAG,"mSuggestion:"+mSuggestion);

        if (scoreSlow!=null){
            indicatorAssesses.add(scoreSlow);
            setState(scoreSlow.getPercent(),scoreSlowCount,pb_hrv_ratelow,tv_warring_state_slow);
            mSuggestion += scoreSlow.getSuggestion();
        }
        if (scoreOver!=null){
            indicatorAssesses.add(scoreOver);
            setState(scoreOver.getPercent(),scoreOverCount,pb_hrv_rateover,tv_warring_state_over);
            mSuggestion += scoreOver.getSuggestion();
        }

        if (scoreBeforeBeat!=null){
            indicatorAssesses.add(scoreBeforeBeat);
            setState(scoreBeforeBeat.getPercent(),6,pb_hrv_morningrate,tv_warring_state_morningrate);
            mSuggestion += scoreBeforeBeat.getSuggestion();
        }
        if (scoreMissBeat!=null){
            indicatorAssesses.add(scoreMissBeat);
            setState(scoreMissBeat.getPercent(),12,pb_hrv_leaverate,tv_warring_state_leaverate);
            mSuggestion += scoreMissBeat.getSuggestion();
        }
        tv_hrv_suggestion.setText(mSuggestion);

        Log.i(TAG,"mSuggestion:"+mSuggestion);
        for (IndicatorAssess indicatorAssess:indicatorAssesses){
            Log.i(TAG,"indicatorAssess:"+indicatorAssess);
        }

    }

    private void setState(int score,int count, ProgressBar progressBar,TextView textView) {
        switch (score){
            case 0:
                progressBar.setProgress(mProgressNormal);
                textView.setText("正常");
                break;
            case 1:
                progressBar.setProgress(mProgressYellow+count*5);
                textView.setText("黄色预警");
                break;
            case 2:
                progressBar.setProgress(mProgressRed+count*5);
                textView.setText("红色预警");
                break;
        }
    }

    public void lookMore(View view) {
        Intent intent = new Intent(this,HistoryRecordActivity.class);
        intent.putExtra("indexwarringTO",true);
        ArrayList<HistoryRecord> historyRecords = new ArrayList<>();
        if (staticStateHistoryRecords!=null && staticStateHistoryRecords.size()>0){
            for (HealthIndicatorAssessActivity.WeekReport.WeekReportResult.HistoryRecordItem historyRecordItem:staticStateHistoryRecords){
                String cueMapDate = MyUtil.getCueMapDate(Long.parseLong(historyRecordItem.timestamp)*1000);
                Log.i(TAG,"cueMapDate:"+cueMapDate);
                historyRecords.add(new HistoryRecord(historyRecordItem.ID,cueMapDate,Integer.parseInt(historyRecordItem.state)));
            }
            intent.putParcelableArrayListExtra("staticStateHistoryRecords",historyRecords);
        }
        startActivity(intent);
    }

    class MyOcClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_warring_slow:
                    showAssessDialog(0);
                    break;
                case R.id.ll_warring_over:
                    showAssessDialog(1);
                    break;
                case R.id.ll_warring_morningrate:
                    showAssessDialog(2);
                    break;
                case R.id.ll_warring_leaverate:
                    showAssessDialog(3);
                    break;

            }
        }
    }

    private void showAssessDialog(int index) {
        if (mAlertDialog==null){
            initItemViewPageView();
            vp_assess_float.setCurrentItem(index);
        }
        else {
            vp_assess_float.setCurrentItem(index);
            mAlertDialog.show();
        }
    }

    private void initItemViewPageView() {
        View inflate = View.inflate(this, R.layout.dialog_assess_type, null);
        vp_assess_float = (ViewPager) inflate.findViewById(R.id.vp_assess_float);
        vp_assess_float.setAdapter(new MyViewPageAdapter());


        mAlertDialog = new AlertDialog.Builder(this).setView(inflate).create();
        mAlertDialog.show();

        float width = getResources().getDimension(R.dimen.x800);
        float height = getResources().getDimension(R.dimen.x860);

        mAlertDialog.getWindow().setLayout(new Float(width).intValue(),new Float(height).intValue());



        LinearLayout ll_point_group = (LinearLayout) inflate.findViewById(R.id.ll_point_group);
        shape_point_blue = inflate.findViewById(R.id.view_blue_point);


        //初始化引导页的小圆点
        for (int i=0;i<4;i++ ){
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
            if (indicatorAssesses.size()>2){
                return 2;
            }
            return indicatorAssesses.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            IndicatorAssess indicatorAssess = indicatorAssesses.get(position);

            View inflate = View.inflate(IndexWarringActivity.this, R.layout.view_viewpage_item,null);
            TextView tv_item_value = (TextView) inflate.findViewById(R.id.tv_item_value);
            TextView tv_item_typeName = (TextView) inflate.findViewById(R.id.tv_item_typeName);
            TextView tv_item_suggestion = (TextView) inflate.findViewById(R.id.tv_item_suggestion);

            /*int score = indicatorAssess.getPercent();
            String warring = "";
            if (score==0){
                warring = "正常";
            }
            else if (score==1){
                warring = "黄色预警";
            }
            else if (score==2){
                warring = "红色预警";
            }*/
            if (indicatorAssess!=null){
                tv_item_value.setText(indicatorAssess.getEvaluate());
                tv_item_typeName.setText(indicatorAssess.getName());
                tv_item_suggestion.setText(indicatorAssess.getSuggestion());
            }

            container.addView(inflate);
            return inflate;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private class MyListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if (staticStateHistoryRecords.size()>2){
                return 2;
            }
            return staticStateHistoryRecords.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HealthIndicatorAssessActivity.WeekReport.WeekReportResult.HistoryRecordItem historyRecord = staticStateHistoryRecords.get(position);
            View inflate = View.inflate(IndexWarringActivity.this, R.layout.list_indexwarring_datafrom_item, null);
            TextView tv_wring_type = (TextView) inflate.findViewById(R.id.tv_wring_type);
            TextView tv_wring_year = (TextView) inflate.findViewById(R.id.tv_wring_year);
            TextView tv_wring_day = (TextView) inflate.findViewById(R.id.tv_wring_day);

            String timestamp = historyRecord.timestamp;

            Date date = new Date(Long.parseLong(timestamp)*1000);
            int year = date.getYear()+1900;
            int month = date.getMonth()+1;
            int day = date.getDate();
            tv_wring_year.setText(year+"");
            tv_wring_day.setText(month+"月"+day);
            return inflate;
        }
    }

}
