package com.amsu.healthy.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.bean.FullReport;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.WeekReport;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.RadarView;
import com.amsu.healthy.view.SelectDialog;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HealthIndicatorAssessActivity extends BaseActivity {

    private static final String TAG = "HealthIndicatorAssess";
    private RadarView rc_assess_radar;
    private View shape_point_blue;
    private float pointMargin;
    private float pointWidth;
    private List<IndicatorAssess> thisWeekIndicatorAssesses;
    private List<IndicatorAssess> lastWeekIndicatorAssesses;
    private AlertDialog mAlertDialog;
    private ViewPager vp_assess_float;
    private WeekReport thisWeekReport;
    private WeekReport lastWeekReport;
    private int mCurrYear;
    private int mCurrWeekOfYear;
    private MyViewPageAdapter myViewPageAdapter;
    private TextView tv_assess_compare;
    private float[] thisWeekdata;
    private TextView tv_assess_thisweek;
    private String mCompareWeek = "上周";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_indicator_assess);

        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.indicator));
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv_hrv_value = (TextView) findViewById(R.id.tv_hrv_value);
        tv_assess_compare = (TextView) findViewById(R.id.tv_assess_compare);
        tv_assess_thisweek = (TextView) findViewById(R.id.tv_assess_thisweek);
        Intent intent = getIntent();
        int scoreALL = intent.getIntExtra("scoreALL", 0);
        if (scoreALL!=0){
            tv_hrv_value.setText(scoreALL+"分（2.13-2.19）");
        }

        setRightImage(R.drawable.plan_calendar);
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* final SelectDialog selectDialog = new SelectDialog(HealthIndicatorAssessActivity.this,R.style.dialog);//创建Dialog并设置样式主题

                //设置尺寸
                Window win = selectDialog.getWindow();
                WindowManager.LayoutParams params = win.getAttributes();
                win.setGravity(Gravity.RIGHT | Gravity.TOP);
                params.circle_ring = new Float(HealthIndicatorAssessActivity.this.getResources().getDimension(R.dimen.x28)).intValue() ;//设置x坐标
                params.y =  new Float(HealthIndicatorAssessActivity.this.getResources().getDimension(R.dimen.x148)).intValue();//设置y坐标
                win.setAttributes(params);

                List<String> weekStringList = MyUtil.getWeekStringList(10);
                String[] data = new String[weekStringList.size()];

                int i=0;
                for (String s:weekStringList){
                    data[i++] = s;
                }

                selectDialog.setData(data);



                //切换周数据
                selectDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.i(TAG,"position:"+position);
                        selectDialog.dismiss();

                       *//* if (position==0){
                            //测试
                            if (thisWeekIndicatorAssesses.size()==7){
                                thisWeekIndicatorAssesses.get(0).setPercent(60);
                                thisWeekIndicatorAssesses.get(1).setPercent(40);
                                thisWeekIndicatorAssesses.get(2).setPercent(70);
                                thisWeekIndicatorAssesses.get(3).setPercent(50);
                                thisWeekIndicatorAssesses.get(4).setPercent(20);
                                thisWeekIndicatorAssesses.get(5).setPercent(70);
                                thisWeekIndicatorAssesses.get(6).setPercent(80);
                                float data1[] = new float[7];
                                for (int i=0;i<thisWeekIndicatorAssesses.size();i++){
                                    data1[i] = thisWeekIndicatorAssesses.get(i).getPercent();
                                }
                                rc_assess_radar.setDatas(data1,null,null);
                            }

                        }
                        else {
                            //测试
                            if (thisWeekIndicatorAssesses.size()==7){
                                thisWeekIndicatorAssesses.get(0).setPercent(60);
                                thisWeekIndicatorAssesses.get(1).setPercent(80);
                                thisWeekIndicatorAssesses.get(2).setPercent(70);
                                thisWeekIndicatorAssesses.get(3).setPercent(50);
                                thisWeekIndicatorAssesses.get(4).setPercent(40);
                                thisWeekIndicatorAssesses.get(5).setPercent(70);
                                thisWeekIndicatorAssesses.get(6).setPercent(40);
                                float data1[] = new float[7];
                                for (int i=0;i<thisWeekIndicatorAssesses.size();i++){
                                    data1[i] = thisWeekIndicatorAssesses.get(i).getPercent();
                                }
                                rc_assess_radar.setDatas(data1,null,null);
                            }

                        }*//*

                        int weekOfYear = mCurrWeekOfYear -(position+1);
                        downlaodWeekRepore(mCurrYear,weekOfYear);
                    }
                });

                selectDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                selectDialog.show();*/

                        startActivityForResult(new Intent(HealthIndicatorAssessActivity.this,ChooseWeekActivity.class),100);
            }
        });

        rc_assess_radar = (RadarView) findViewById(R.id.rc_assess_radar);
        rc_assess_radar.setMyItemOnClickListener(new RadarView.MyItemOnClickListener() {
            @Override
            public void onClick(int i) {
                Log.i(TAG,"onClick:"+i);
                showAssessDialog(i);
            }
        });

        thisWeekIndicatorAssesses = new ArrayList<>();
        lastWeekIndicatorAssesses = new ArrayList<>();

        thisWeekIndicatorAssesses.add(0,new IndicatorAssess(0,getResources().getString(R.string.too_fast_too_slow),getResources().getString(R.string.heart_rate_slow_fast_decrible)));
        thisWeekIndicatorAssesses.add(new IndicatorAssess(0,getResources().getString(R.string.premature_beat_missed_beat),getResources().getString(R.string.premature_beat_missed_beat_decrible)));
        thisWeekIndicatorAssesses.add(new IndicatorAssess(0,getResources().getString(R.string.health_reserve),getResources().getString(R.string.health_reserve_decrible)));
        thisWeekIndicatorAssesses.add(new IndicatorAssess(0,"BMI",getResources().getString(R.string.health_BMI_decrible)));
        thisWeekIndicatorAssesses.add(new IndicatorAssess(0,getResources().getString(R.string.heart_rate_reserve),getResources().getString(R.string.heart_rate_reserve_decrible)));
        thisWeekIndicatorAssesses.add(new IndicatorAssess(0,getResources().getString(R.string.heart_rate_recovery),getResources().getString(R.string.heart_rate_recovery_decrible)));
        thisWeekIndicatorAssesses.add(new IndicatorAssess(0,getResources().getString(R.string.indicator_for_resistance_to_fatigue),getResources().getString(R.string.heart_rate_variability_decrible)));

    }

    private void initData() {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

        downlaodWeekRepore(mCurrYear,mCurrWeekOfYear,true);
        downlaodWeekRepore(mCurrYear,mCurrWeekOfYear-1,false);
    }

    private void downlaodWeekRepore(final int year, final int weekOfYear, final boolean isThisWeek) {
        if (isThisWeek){
            MyUtil.showDialog(getResources().getString(R.string.loading),this);
        }
        Log.i(TAG,"downlaodWeekRepore=======year:"+year+"  weekOfYear:"+weekOfYear);

        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();

        params.addBodyParameter("year",year+"");
        params.addBodyParameter("week",weekOfYear+"");

        MyUtil.addCookieForHttp(params);

        String url;
        if (isThisWeek){
            url = Constant.downloadLatelyWeekReportURL;
        }
        else {
            url = Constant.downloadWeekReportURL;
        }

        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(HealthIndicatorAssessActivity.this);
                //rc_assess_radar.setDatas(data1,null,null);

                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase.getRet()==0){
                    if (isThisWeek){
                        //本周数据
                        thisWeekReport = gson.fromJson(result, WeekReport.class);
                        Log.i(TAG,"thisWeekReport:"+ thisWeekReport.toString());


                    }
                    else {
                        lastWeekReport = gson.fromJson(result, WeekReport.class);
                        Log.i(TAG,"lastWeekReport:"+ lastWeekReport.toString());
                    }
                    setIndicatorData();
                }
                /*else if (jsonBase.getRet()==-10001){
                    String toaskText = tv_assess_compare.getText()+getResources().getString(R.string.no_data);
                    if (weekOfYear==-1){
                        toaskText = getResources().getString(R.string.no_data_this_week);
                    }
                    MyUtil.showToask(HealthIndicatorAssessActivity.this,toaskText);
                    float[] data1 = {0, 0, 0, 0, 0,0,0};
                    rc_assess_radar.setDatas(thisWeekdata,data1,null);
                }*/
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog(HealthIndicatorAssessActivity.this);
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }

    private void setIndicatorData(){
        tv_assess_thisweek.setText("近七天");
        tv_assess_compare.setText(mCompareWeek);

        boolean isThisWeekHaveData = false;
        boolean isLastWeekHaveData = false;
        float[] lastWeekdata = new float[7];;
        if (thisWeekReport!=null && thisWeekReport.errDesc!=null && thisWeekReport.errDesc.guosuguohuan!=null && thisWeekReport.errDesc.guosuguohuan.size()>0){
            isThisWeekHaveData = true;
            thisWeekdata = dealWithWeekData(thisWeekReport,true);
        }
        if (lastWeekReport!=null && lastWeekReport.errDesc!=null && lastWeekReport.errDesc.guosuguohuan!=null && lastWeekReport.errDesc.guosuguohuan.size()>0){
            isLastWeekHaveData = true;
            lastWeekdata = dealWithWeekData(lastWeekReport,false);
        }

        Log.i(TAG,"isThisWeekHaveData:"+isThisWeekHaveData+",isLastWeekHaveData:"+isLastWeekHaveData);

        if (isThisWeekHaveData && !isLastWeekHaveData){
            //本周有数据
            rc_assess_radar.setDatas(thisWeekdata,null,null);
            tv_assess_compare.setText(tv_assess_compare.getText().toString()+"无数据");
        }
        else if (!isThisWeekHaveData && isLastWeekHaveData){
            //上周有数据
            rc_assess_radar.setDatas(null,lastWeekdata,null);
            tv_assess_thisweek.setText("近7天无数据");
        }
        else if (isThisWeekHaveData && isLastWeekHaveData){
            //都有
            rc_assess_radar.setDatas(thisWeekdata,lastWeekdata,null);
        }
        else if (!isThisWeekHaveData && !isLastWeekHaveData){
            //都没有
            rc_assess_radar.setDatas(null,null,null);
            tv_assess_thisweek.setText("近7天无数据");
            tv_assess_compare.setText(tv_assess_compare.getText().toString()+"无数据");
        }

       /* if(thisWeekReport!=null &&  lastWeekReport==null ){

            if (thisWeekReport.errDesc!=null && thisWeekReport.errDesc.guosuguohuan!=null && thisWeekReport.errDesc.guosuguohuan.size()>0){  //数量大于0
                thisWeekdata = dealWithWeekData(thisWeekReport,true);
                rc_assess_radar.setDatas(thisWeekdata,null,null);
            }
            else {
                rc_assess_radar.setDatas(null,null,null);
            }
        }
        else if(lastWeekReport!=null && thisWeekReport==null ){
            tv_assess_thiweek.setText("近7天无数据");
            MyUtil.showToask(HealthIndicatorAssessActivity.this,"近7天无数据");
            if (lastWeekReport.errDesc!=null && lastWeekReport.errDesc.guosuguohuan!=null && lastWeekReport.errDesc.guosuguohuan.size()>0){
                float[] lastWeekdata = dealWithWeekData(lastWeekReport,false);
                rc_assess_radar.setDatas(null,lastWeekdata,null);
            }
            else {
                rc_assess_radar.setDatas(null,null,null);
            }
        }
        else if (thisWeekReport!=null && lastWeekReport!=null ){
            //2周数据都不为空时
            if (thisWeekReport.errDesc!=null && thisWeekReport.errDesc.guosuguohuan!=null && thisWeekReport.errDesc.guosuguohuan.size()>0 &&
                    lastWeekReport.errDesc!=null && lastWeekReport.errDesc.guosuguohuan!=null && lastWeekReport.errDesc.guosuguohuan.size()>0){  //数量大于0
                thisWeekdata = dealWithWeekData(thisWeekReport,true);
                float[] lastWeekdata = dealWithWeekData(lastWeekReport,false);
                rc_assess_radar.setDatas(thisWeekdata,lastWeekdata,null);
            }
        }*/


        if (myViewPageAdapter!=null){
            myViewPageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            if (data!=null){
                int year = data.getIntExtra("year",-1);
                int currWeekOfYear = data.getIntExtra("currWeekOfYear",-1);
                String mChoosedweek = data.getStringExtra("mChoosedweek");
                mCompareWeek = mChoosedweek;

                Log.i(TAG,"year:"+year+", currWeekOfYear:"+currWeekOfYear+",mChoosedweek:"+mChoosedweek);

                if (year!=-1 && currWeekOfYear!=-1){
                    downlaodWeekRepore(year,currWeekOfYear,false);
                }
                if (!MyUtil.isEmpty(mChoosedweek)){
                    tv_assess_compare.setText(mChoosedweek);
                }
            }
        }
    }

    private  float[] dealWithWeekData(WeekReport weekReport, boolean isCurrWeek) {
        //BMI
        IndicatorAssess scoreBMI = HealthyIndexUtil.calculateScoreBMI(this);
        //储备心率
        IndicatorAssess scorehrReserve = HealthyIndexUtil.calculateScorehrReserve(this);

        if (weekReport!=null){
            List<String> huifuxinlv = weekReport.errDesc.huifuxinlv;
            int sum = 0;
            int count = 0;
            for (String s:huifuxinlv){
                if (!MyUtil.isEmpty(s)&& !s.equals("null") && Integer.parseInt(s)>0){
                    sum += Integer.parseInt(s);
                    count++;
                }
            }
            IndicatorAssess scoreHRR = null;
            if(count>0){
                int avHhrr = sum/count;
                //恢复心率HRR
                scoreHRR = HealthyIndexUtil.calculateScoreHRR(avHhrr,this);
            }
            else {
                scoreHRR = HealthyIndexUtil.calculateScoreHRR(0,this);
            }


            List<String> kangpilaozhishu = weekReport.errDesc.kangpilaozhishu;
            sum = 0;
            count = 0;
            for (String s:kangpilaozhishu){
                if (!MyUtil.isEmpty(s)&& !s.equals("null") && Integer.parseInt(s)>0){
                    sum += Integer.parseInt(s);
                    count++;
                }
            }
            IndicatorAssess scoreHRV = null;
            if(count>0){
                int avHhrv = sum/count;
                //抗疲劳指数HRV(心电分析算法得出)
                scoreHRV = HealthyIndexUtil.calculateScoreHRV(avHhrv,this);
            }
            else {
                scoreHRV = HealthyIndexUtil.calculateScoreHRV(0,this);
            }


            List<String> guosuguohuan = weekReport.errDesc.guosuguohuan;
            sum = 0;
            count = 0;
            for (String s:guosuguohuan){
                if (!MyUtil.isEmpty(s)&& !s.equals("null") && Integer.parseInt(s)>0){
                    count++;
                    sum += Integer.parseInt(s);
                }
            }
            IndicatorAssess scoreOver_slow = null;
            if(count>0){
                int over_slow = sum/count;
                Log.i(TAG,"over_slow:"+over_slow);
                //过缓/过速(心电分析算法得出)
                scoreOver_slow = HealthyIndexUtil.calculateScoreOver_slow(over_slow,this);
            }
            else {
                scoreOver_slow = HealthyIndexUtil.calculateScoreOver_slow(0,this);
            }


            IndicatorAssess scoreBeat = null;
            List<WeekReport.WeekReportResult.Zaoboloubo> zaoboloubo = weekReport.errDesc.zaoboloubo;
            if (zaoboloubo!=null && zaoboloubo.size()>0){
                int zaobo  = zaoboloubo.get(0).zaoboTimes;
                int loubo  = zaoboloubo.get(0).louboTimes;
                if (zaobo<0){
                    zaobo = 0;
                }
                if (loubo<0){
                    loubo = 0;
                }
                //早搏 包括房早搏APB和室早搏VPB，两者都记为早搏(心电分析算法得出)
                scoreBeat = HealthyIndexUtil.calculateScoreBeat(zaobo,loubo,this);
            }

            // 健康储备(按训练时间计算)
            IndicatorAssess scoreReserveHealth = HealthyIndexUtil.calculateScoreReserveHealth((int) (Float.parseFloat(weekReport.errDesc.chubeijiankang)/60),this);
            if (isCurrWeek){
                return getFloats(scoreBMI, scorehrReserve, scoreHRR, scoreHRV, scoreOver_slow, scoreBeat, scoreReserveHealth,thisWeekIndicatorAssesses,true);
            }
            else {
                return getFloats(scoreBMI, scorehrReserve, scoreHRR, scoreHRV, scoreOver_slow, scoreBeat, scoreReserveHealth,lastWeekIndicatorAssesses,false);
            }

        }
        return null;
    }

    private float[] getFloats(IndicatorAssess scoreBMI, IndicatorAssess scorehrReserve, IndicatorAssess scoreHRR, IndicatorAssess scoreHRV,
                              IndicatorAssess scoreOver_slow, IndicatorAssess scoreBeat, IndicatorAssess scoreReserveHealth ,
                              List<IndicatorAssess> weekIndicatorAssesses,boolean isCurrWeek) {

        weekIndicatorAssesses.clear();
        if (scoreOver_slow!=null){
            weekIndicatorAssesses.add(scoreOver_slow);
        }
        if (scoreBeat!=null){
            weekIndicatorAssesses.add(scoreBeat);
        }
        if (scoreReserveHealth!=null){
            weekIndicatorAssesses.add(scoreReserveHealth);
        }
        if (scoreBMI!=null){
            weekIndicatorAssesses.add(scoreBMI);
        }
        if (scorehrReserve!=null){
            weekIndicatorAssesses.add(scorehrReserve);
        }
        if (scoreHRR!=null){
            weekIndicatorAssesses.add(scoreHRR);
        }
        if (scoreHRV!=null){
            weekIndicatorAssesses.add(scoreHRV);
        }

        for (IndicatorAssess indicatorAssess:weekIndicatorAssesses){
            Log.i(TAG,"indicatorAssess:"+indicatorAssess);
        }

        float[] data = new float[7];
        for (int i=0;i<weekIndicatorAssesses.size();i++){
            data[i] = weekIndicatorAssesses.get(i).getPercent();
            if (!isCurrWeek && thisWeekIndicatorAssesses!=null && thisWeekIndicatorAssesses.size()>0){
                IndicatorAssess indicatorAssess = lastWeekIndicatorAssesses.get(i);
                int temp;
                if (indicatorAssess.getName().equals(getResources().getString(R.string.heart_rate_reserve)) || indicatorAssess.getName().equals(getResources().getString(R.string.heart_rate_recovery)) || indicatorAssess.getName().equals("BMI")){
                    temp = thisWeekIndicatorAssesses.get(i).getValue() - indicatorAssess.getValue();
                }
                else {
                    temp = thisWeekIndicatorAssesses.get(i).getPercent() - indicatorAssess.getPercent();
                }
                thisWeekIndicatorAssesses.get(i).setDifferenceValue(temp);
                Log.i(TAG,"last:"+indicatorAssess+", this:"+thisWeekIndicatorAssesses.get(i));
                Log.i(TAG,"temp:"+temp);
            }
        }
        return data;
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        myViewPageAdapter = new MyViewPageAdapter();
        vp_assess_float.setAdapter(myViewPageAdapter);

        mAlertDialog = new AlertDialog.Builder(this).setView(inflate).create();
        mAlertDialog.show();

        float width = getResources().getDimension(R.dimen.x880);
        float height = getResources().getDimension(R.dimen.x940);

        mAlertDialog.getWindow().setLayout(new Float(width).intValue(),new Float(height).intValue());

        LinearLayout ll_point_group = (LinearLayout) inflate.findViewById(R.id.ll_point_group);
        shape_point_blue = inflate.findViewById(R.id.view_blue_point);


        //初始化引导页的小圆点
        for (int i=0;i<7;i++ ){
            View point = new View(this);
            point.setBackgroundResource(R.drawable.shape_point_gray);
            pointWidth = getResources().getDimension(R.dimen.x16);
            pointMargin = getResources().getDimension(R.dimen.x16);

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
                //int marginFloat = new Float(pointMargin).intValue();

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

    private class MyViewPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return thisWeekIndicatorAssesses.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            IndicatorAssess indicatorAssess  = thisWeekIndicatorAssesses.get(position);


            View inflate = View.inflate(HealthIndicatorAssessActivity.this, R.layout.view_viewpage_item,null);
            TextView tv_item_value = (TextView) inflate.findViewById(R.id.tv_item_value);
            TextView tv_item_typeName = (TextView) inflate.findViewById(R.id.tv_item_typeName);
            TextView tv_item_suggestion = (TextView) inflate.findViewById(R.id.tv_item_suggestion);
            TextView tv_item_unit = (TextView) inflate.findViewById(R.id.tv_item_unit);



            if (indicatorAssess!=null){
                int differenceValue = indicatorAssess.getDifferenceValue();
                String differenceValueString ="";
                if (differenceValue>0){
                    differenceValueString = "+"+differenceValue;
                }else {
                    differenceValueString = differenceValue+"";
                }

                if (indicatorAssess.getName().equals(getResources().getString(R.string.heart_rate_reserve)) || indicatorAssess.getName().equals(getResources().getString(R.string.heart_rate_recovery))){
                    tv_item_unit.setText("bpm");
                    if (indicatorAssess.getValue()==0){
                        tv_item_value.setText("--");
                    }
                    else {
                        tv_item_value.setText(indicatorAssess.getValue()+"("+differenceValueString+")");
                    }
                }
                else if (indicatorAssess.getName().equals("BMI")){
                    tv_item_unit.setText("bmi");
                    if (indicatorAssess.getValue()==0){
                        tv_item_value.setText("--");
                    }
                    else {
                        tv_item_value.setText(indicatorAssess.getValue()+"("+differenceValueString+")");
                    }
                }
                else {
                    tv_item_unit.setText(getResources().getString(R.string.points));
                    if (indicatorAssess.getPercent()==0){
                        tv_item_value.setText("--");
                    }
                    else {
                        tv_item_value.setText(indicatorAssess.getPercent()+"("+differenceValueString+")");
                    }

                }

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



}
