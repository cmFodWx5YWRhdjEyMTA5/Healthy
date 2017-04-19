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

import com.amsu.healthy.R;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.bean.FullReport;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.JsonBase;
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
    private List<IndicatorAssess> indicatorAssesses;
    private AlertDialog mAlertDialog;
    private ViewPager vp_assess_float;
    private WeekReport weekReport;
    private int mCurrYear;
    private int mCurrWeekOfYear;
    private MyViewPageAdapter myViewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_indicator_assess);

        initView();
        initData();
    }



    private void initView() {
        initHeadView();
        setCenterText("健康指标评分");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv_hrv_value = (TextView) findViewById(R.id.tv_hrv_value);
        Intent intent = getIntent();
        int scoreALL = intent.getIntExtra("scoreALL", 0);
        if (scoreALL!=0){
            tv_hrv_value.setText(scoreALL+"分（2.13-2.19）");
        }

        setRightImage(R.drawable.plan_calendar);
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SelectDialog selectDialog = new SelectDialog(HealthIndicatorAssessActivity.this,R.style.dialog);//创建Dialog并设置样式主题

                //设置尺寸
                Window win = selectDialog.getWindow();
                WindowManager.LayoutParams params = win.getAttributes();
                win.setGravity(Gravity.RIGHT | Gravity.TOP);
                params.x = new Float(HealthIndicatorAssessActivity.this.getResources().getDimension(R.dimen.x28)).intValue() ;//设置x坐标
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

                       /* if (position==0){
                            //测试
                            if (indicatorAssesses.size()==7){
                                indicatorAssesses.get(0).setPercent(60);
                                indicatorAssesses.get(1).setPercent(40);
                                indicatorAssesses.get(2).setPercent(70);
                                indicatorAssesses.get(3).setPercent(50);
                                indicatorAssesses.get(4).setPercent(20);
                                indicatorAssesses.get(5).setPercent(70);
                                indicatorAssesses.get(6).setPercent(80);
                                float data1[] = new float[7];
                                for (int i=0;i<indicatorAssesses.size();i++){
                                    data1[i] = indicatorAssesses.get(i).getPercent();
                                }
                                rc_assess_radar.setDatas(data1,null,null);
                            }

                        }
                        else {
                            //测试
                            if (indicatorAssesses.size()==7){
                                indicatorAssesses.get(0).setPercent(60);
                                indicatorAssesses.get(1).setPercent(80);
                                indicatorAssesses.get(2).setPercent(70);
                                indicatorAssesses.get(3).setPercent(50);
                                indicatorAssesses.get(4).setPercent(40);
                                indicatorAssesses.get(5).setPercent(70);
                                indicatorAssesses.get(6).setPercent(40);
                                float data1[] = new float[7];
                                for (int i=0;i<indicatorAssesses.size();i++){
                                    data1[i] = indicatorAssesses.get(i).getPercent();
                                }
                                rc_assess_radar.setDatas(data1,null,null);
                            }

                        }*/

                        int weekOfYear = mCurrWeekOfYear -(position+1);
                        downlaodWeekRepore(mCurrYear,weekOfYear);
                    }
                });

                selectDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                selectDialog.show();
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

        indicatorAssesses = new ArrayList<>();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
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
                indicatorAssesses.clear();
                float[] data1 = {0, 0, 0, 0, 0,0,0};
                rc_assess_radar.setDatas(data1,null,null);

                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase.getRet()==0){
                    weekReport = gson.fromJson(result, WeekReport.class);
                    Log.i(TAG,"weekReport:"+ weekReport.toString());
                    setIndicatorData();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog();

                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }

    private void setIndicatorData(){
        //BMI
        IndicatorAssess scoreBMI = HealthyIndexUtil.calculateScoreBMI();
        //储备心率
        IndicatorAssess scorehrReserve = HealthyIndexUtil.calculateScorehrReserve();

        if (weekReport!=null){
            List<String> huifuxinlv = weekReport.errDesc.huifuxinlv;
            int sum = 0;
            for (String s:huifuxinlv){
                sum += Integer.parseInt(s);
            }
            int avHhrr = sum/huifuxinlv.size();
            //恢复心率HRR
            IndicatorAssess scoreHRR = HealthyIndexUtil.calculateScoreHRR(avHhrr);


            List<String> kangpilaozhishu = weekReport.errDesc.kangpilaozhishu;
            sum = 0;
            for (String s:kangpilaozhishu){
                sum += Integer.parseInt(s);
            }
            int avHhrv = sum/kangpilaozhishu.size();
            //抗疲劳指数HRV(心电分析算法得出)
            IndicatorAssess scoreHRV = HealthyIndexUtil.calculateScoreHRV(avHhrv);

            List<String> guosuguohuan = weekReport.errDesc.guosuguohuan;
            sum = 0;
            for (String s:guosuguohuan){
                sum += Integer.parseInt(s);
            }
            int over_slow = sum/guosuguohuan.size();
            //过缓/过速(心电分析算法得出)
            IndicatorAssess scoreOver_slow = HealthyIndexUtil.calculateScoreOver_slow(over_slow);

            IndicatorAssess scoreBeat = null;
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
                scoreBeat = HealthyIndexUtil.calculateScoreBeat(zaobo,loubo);
            }

            // 健康储备(按训练时间计算)
            IndicatorAssess scoreReserveHealth = HealthyIndexUtil.calculateScoreReserveHealth();

            if (scoreOver_slow!=null){
                indicatorAssesses.add(scoreOver_slow);
            }
            if (scoreBeat!=null){
                indicatorAssesses.add(scoreBeat);
            }
            if (scoreReserveHealth!=null){
                indicatorAssesses.add(scoreReserveHealth);
            }
            if (scoreBMI!=null){
                indicatorAssesses.add(scoreBMI);
            }
            if (scorehrReserve!=null){
                indicatorAssesses.add(scorehrReserve);
            }
            if (scoreHRR!=null){
                indicatorAssesses.add(scoreHRR);
            }
            if (scoreHRV!=null){
                indicatorAssesses.add(scoreHRV);
            }

            float[] data1 = new float[7];
            for (int i=0;i<indicatorAssesses.size();i++){
                data1[i] = indicatorAssesses.get(i).getPercent();
            }
            rc_assess_radar.setDatas(data1,null,null);

            for (IndicatorAssess indicatorAssess:indicatorAssesses){
                Log.i(TAG,"indicatorAssess:"+indicatorAssess);
            }
            if (myViewPageAdapter!=null){
                myViewPageAdapter.notifyDataSetChanged();
            }
        }
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
        ;

        mAlertDialog = new AlertDialog.Builder(this).setView(inflate).create();
        mAlertDialog.show();

        float width = getResources().getDimension(R.dimen.x800);
        float height = getResources().getDimension(R.dimen.x860);

        mAlertDialog.getWindow().setLayout(new Float(width).intValue(),new Float(height).intValue());



        LinearLayout ll_point_group = (LinearLayout) inflate.findViewById(R.id.ll_point_group);
        shape_point_blue = inflate.findViewById(R.id.view_blue_point);


        //初始化引导页的小圆点
        for (int i=0;i<7;i++ ){
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

    private class MyViewPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return indicatorAssesses.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            IndicatorAssess indicatorAssess = indicatorAssesses.get(position);

            View inflate = View.inflate(HealthIndicatorAssessActivity.this, R.layout.view_viewpage_item,null);
            TextView tv_item_value = (TextView) inflate.findViewById(R.id.tv_item_value);
            TextView tv_item_typeName = (TextView) inflate.findViewById(R.id.tv_item_typeName);
            TextView tv_item_suggestion = (TextView) inflate.findViewById(R.id.tv_item_suggestion);
            TextView tv_item_unit = (TextView) inflate.findViewById(R.id.tv_item_unit);

            if (indicatorAssess!=null){
                if (indicatorAssess.getName().equals("储备心率") || indicatorAssess.getName().equals("恢复心率(HRR)")){
                    tv_item_unit.setText("bpm");
                    if (indicatorAssess.getValue()==0){
                        tv_item_value.setText("--");
                    }
                    else {
                        tv_item_value.setText(indicatorAssess.getValue()+"");
                    }
                }
                else if (indicatorAssess.getName().equals("BMI")){
                    tv_item_unit.setText("bmi");
                    if (indicatorAssess.getValue()==0){
                        tv_item_value.setText("--");
                    }
                    else {
                        tv_item_value.setText(indicatorAssess.getValue()+"");
                    }
                }
                else {
                    tv_item_unit.setText("分");
                    if (indicatorAssess.getPercent()==0){
                        tv_item_value.setText("--");
                    }
                    else {
                        tv_item_value.setText(indicatorAssess.getPercent()+"");
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

    class WeekReport{
        public String ret;
        public WeekReportResult errDesc;

         class WeekReportResult{
            public String chubeijiankang;
            public List<Integer> zaoboloubo;
            public List<String> guosuguohuan;
            public List<String> kangpilaozhishu;
            public List<String> huifuxinlv;
            public List<HistoryRecordItem> list;

            public class HistoryRecordItem {
                public String ID;
                public String timestamp;
                public String state;

                public HistoryRecordItem(String ID, String timestamp, String state) {
                    this.ID = ID;
                    this.timestamp = timestamp;
                    this.state = state;
                }

                @Override
                public String toString() {
                    return "HistoryRecordItem [ID=" + ID + ", timestamp="
                            + timestamp + ", state=" + state + "]";
                }
            }

            @Override
            public String toString() {
                return "WeekReport [chubeijiankang=" + chubeijiankang
                        + ", zaoboloubo=" + zaoboloubo + ", guosuguohuan="
                        + guosuguohuan + ", kangpilaozhishu=" + kangpilaozhishu
                        + ", huifuxinlv=" + huifuxinlv + ", list=" + list + "]";
            }
        }

        @Override
        public String toString() {
            return "WeekReport [ret=" + ret + ", errDesc=" + errDesc + "]";
        }
    }

}
