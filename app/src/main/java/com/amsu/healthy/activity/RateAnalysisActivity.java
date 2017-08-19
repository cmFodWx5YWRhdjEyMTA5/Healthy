package com.amsu.healthy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.FragmentListRateAdapter;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.fragment.analysis.ECGFragment;
import com.amsu.healthy.fragment.analysis.HRRFragment;
import com.amsu.healthy.fragment.analysis.HRVFragment;
import com.amsu.healthy.fragment.analysis.HeartRateFragment;
import com.amsu.healthy.fragment.analysis.SportFragment;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.OffLineDbAdapter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

public class RateAnalysisActivity extends BaseActivity {

    private static final String TAG = "RateAnalysisActivity";
    private ViewPager vp_analysis_content;
    private List<Fragment> fragmentList;
    private TextView tv_analysis_hrv;
    private TextView tv_analysis_rate;
    private TextView tv_analysis_ecg;
    private TextView tv_analysis_hrr;
    private View v_analysis_select;
    public static UploadRecord mUploadRecord;
    private TextView tv_analysis_sport;
    private FragmentListRateAdapter mAnalysisRateAdapter;
    private float mOneTableWidth;
    private int subFormAlCount = 0 ;  //当有四个fragment是为0，有5个fragment时为1
    public static String ecgLocalFileName;
    private ImageView iv_base_myreport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_analysis);
        Log.i(TAG,"onCreate");
        initView();
        initData();
    }

    private void initView() {
        fragmentList = new ArrayList<>();
        ecgLocalFileName = null;
        initHeadView();
        setLeftImage(R.drawable.back_icon);
        vp_analysis_content = (ViewPager) findViewById(R.id.vp_analysis_content);
        v_analysis_select = findViewById(R.id.v_analysis_select);

        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_analysis_hrv = (TextView) findViewById(R.id.tv_analysis_hrv);
        tv_analysis_rate = (TextView) findViewById(R.id.tv_analysis_rate);
        tv_analysis_ecg = (TextView) findViewById(R.id.tv_analysis_ecg);
        tv_analysis_hrr = (TextView) findViewById(R.id.tv_analysis_hrr);
        tv_analysis_sport = (TextView) findViewById(R.id.tv_analysis_sport);

        MyClickListener myClickListener = new MyClickListener();
        tv_analysis_hrv.setOnClickListener(myClickListener);
        tv_analysis_rate.setOnClickListener(myClickListener);
        tv_analysis_ecg.setOnClickListener(myClickListener);
        tv_analysis_hrr.setOnClickListener(myClickListener);
        tv_analysis_sport.setOnClickListener(myClickListener);

        iv_base_myreport = (ImageView) findViewById(R.id.iv_base_myreport);

        setRightImage(R.drawable.lishijilu);
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RateAnalysisActivity.this, HistoryRecordActivity.class));
            }
        });

        iv_base_myreport.setVisibility(View.VISIBLE);

        //每一个小格的宽度
        mOneTableWidth = MyUtil.getScreeenWidth(this)/5;

        vp_analysis_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i(TAG,"onPageScrolled===position:"+position+",positionOffset:"+positionOffset+",positionOffsetPixels:"+positionOffsetPixels);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
                int floatWidth=  (int) (mOneTableWidth *(positionOffset+position));  //view向左的偏移量
                layoutParams.setMargins(floatWidth,0,0,0); //4个参数按顺序分别是左上右下
                v_analysis_select.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                //Log.i(TAG,"onPageSelected===position:"+position);
                setViewPageTextColor(position+subFormAlCount);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
               // Log.i(TAG,"onPageScrollStateChanged===state:"+state);
            }
        });

        iv_base_myreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RateAnalysisActivity.this, MyReportActivity.class));
            }
        });
    }

    private void initData() {
        final Intent intent = getIntent();
        if (intent!=null){
            Bundle bundle = intent.getParcelableExtra("bundle");
            if (bundle!=null){
                HistoryRecord historyRecord = bundle.getParcelable("historyRecord");
                if (historyRecord!=null){
                    /*if (historyRecord.getState()==0 && tv_analysis_sport.getVisibility()==View.VISIBLE){ //静止状态&& 可见
                        fragmentList.remove(0);
                        subFormAlCount = 1;
                        tv_analysis_sport.setVisibility(View.GONE);
                        mOneTableWidth = MyUtil.getScreeenWidth(this)/4;
                        mAnalysisRateAdapter.notifyDataSetChanged();
                    }*/

                    Log.i(TAG,"historyRecord:"+historyRecord.toString());


                    adjustFeagmentCount(historyRecord.getState());

                    //根据历史记录id进行网络查询
                    String datatime = historyRecord.getDatatime();
                    if (datatime!=null && datatime.split(" ").length==2){
                        //setCenterText(datatime.split(" ")[0]);  // 2016-10-28 10:56:04
                        setCenterText(datatime.substring(0,datatime.length()-3));  // 2016-10-28 10:56:04
                    }
                    else {
                        setCenterText(datatime);  // 2016-10-28 10:56:04
                    }

                    //奔溃缓存策略
                    //先从本地数据库中根据datatime查询数据，没有的话根据id从服务器获取
                    OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(RateAnalysisActivity.this);
                    offLineDbAdapter.open();

                    if (offLineDbAdapter!=null){
                        UploadRecord uploadRecord = offLineDbAdapter.queryRecordByDatatime(datatime);  // 2017-06-28 11:01:33
                        offLineDbAdapter.close();

                        if (uploadRecord!=null){
                            //本地有数据
                            Log.i(TAG,"本地uploadRecord:"+uploadRecord);
                            mUploadRecord = uploadRecord;
                            Log.i(TAG,"mUploadRecord.state:"+mUploadRecord.state);
                            Log.i(TAG,"mUploadRecord.time:"+mUploadRecord.time);
                            Log.i(TAG,"mUploadRecord.calorie:"+mUploadRecord.calorie);
                            Log.i(TAG,"mUploadRecord.AE:"+mUploadRecord.AE);
                            ecgLocalFileName = uploadRecord.localEcgFileName;

                            return;
                        }
                    }




                    MyUtil.showDialog(getResources().getString(R.string.loading),RateAnalysisActivity.this);
                    HttpUtils httpUtils = new HttpUtils();
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("id",historyRecord.getID());
                    MyUtil.addCookieForHttp(params);

                    httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getHistoryReportDetailURL, params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            if (isActiivtyDestroy)return;
                            String result = responseInfo.result;
                            Log.i(TAG,"上传onSuccess==result:"+result);
                            Gson gson = new Gson();
                            JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                            Log.i(TAG,"jsonBase:"+jsonBase);
                            if (jsonBase.getRet()==0){

                               /* UploadRecordObject uUploadRecordObject = gson.fromJson(result, UploadRecordObject.class);
                                //mUploadRecord = uUploadRecordObject.errDesc;
                                Log.i(TAG,"uUploadRecordObject:"+uUploadRecordObject)*/;

                                try {
                                    JSONObject object = new JSONObject(result);
                                    JSONObject jsonObject =object.getJSONObject("errDesc");
                                    String FI    = jsonObject.getString("FI");
                                    String ES    = jsonObject.getString("ES");
                                    String PI    = jsonObject.getString("PI");
                                    String CC    = jsonObject.getString("CC");
                                    String HRVr  = jsonObject.getString("HRVr");
                                    String HRVs  = jsonObject.getString("HRVs");
                                    String AHR   = jsonObject.getString("AHR");
                                    String MaxHR = jsonObject.getString("MaxHR");
                                    String MinHR = jsonObject.getString("MinHR");
                                    String HRr   = jsonObject.getString("HRr");
                                    String HRs   = jsonObject.getString("HRs");
                                    String EC    = jsonObject.getString("EC");
                                    String ECr   = jsonObject.getString("ECr");
                                    String ECs   = jsonObject.getString("ECs");
                                    String RA    = jsonObject.getString("RA");
                                    String timestamp = jsonObject.getString("timestamp");
                                    String datatime  = jsonObject.getString("datatime");
                                    String HR    = jsonObject.getString("HR");
                                    String AE    = jsonObject.getString("AE");
                                    String distance  = jsonObject.getString("distance");
                                    String time  = jsonObject.getString("time");
                                    String cadence   = jsonObject.getString("cadence");
                                    String calorie   = jsonObject.getString("calorie");
                                    String state = jsonObject.getString("state");
                                    String zaobo = jsonObject.getString("zaobo");
                                    String loubo = jsonObject.getString("loubo");
                                    String latitude_longitude = jsonObject.getString("latitude_longitude");

                                    mUploadRecord = new UploadRecord(FI,ES,PI,CC,HRVr,HRVs,AHR,MaxHR,MinHR,HRr,HRs,EC,ECr,ECs,RA,timestamp,datatime,HR,
                                            AE,distance,time,cadence,calorie,state,zaobo,loubo,latitude_longitude);
                                    Log.i(TAG,"mUploadRecord:"+mUploadRecord);
                                    ecgLocalFileName = MyUtil.generateECGFilePath(RateAnalysisActivity.this, System.currentTimeMillis());

                                    OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(RateAnalysisActivity.this);
                                    offLineDbAdapter.open();
                                    mUploadRecord.datatime = mUploadRecord.datatime.replace("/", "-");  //将本地数据库时间改成和服务器一致，下次查看数据时，先从根据时间从本地查询
                                    mUploadRecord.localEcgFileName = ecgLocalFileName;
                                    long createOrUpdateUploadReportObject = offLineDbAdapter.createOrUpdateUploadReportObject(mUploadRecord);
                                    Log.i(TAG,"createOrUpdateUploadReportObject:"+createOrUpdateUploadReportObject);
                                    offLineDbAdapter.close();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            adjustFeagmentCount(Integer.parseInt(mUploadRecord.state));
                            MyUtil.hideDialog(RateAnalysisActivity.this);
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            if (isActiivtyDestroy)return;
                            addFragmentList(intent.getIntExtra(Constant.sportState,-1));
                            MyUtil.hideDialog(RateAnalysisActivity.this);
                            MyUtil.showToask(RateAnalysisActivity.this,"数据加载失败");
                            Log.i(TAG,"上传onFailure==s:"+s);
                        }
                    });
                }
                else {
                    //当前分析结果，直接显示
                    mUploadRecord = bundle.getParcelable("uploadRecord");
                    Log.i(TAG,"直接显示uploadRecord:"+mUploadRecord.toString());
                    ecgLocalFileName = intent.getStringExtra(Constant.ecgLocalFileName);
                    String replace = mUploadRecord.getDatatime().replace("/", "-");//2016/10/24 10:56:4
                    setCenterText(replace);
                    Log.i(TAG,"ecgLocalFileName:"+ecgLocalFileName);
                    adjustFeagmentCount(intent.getIntExtra(Constant.sportState,-1));
                }
            }
            else {
                adjustFeagmentCount(intent.getIntExtra(Constant.sportState,-1));
            }
        }
    }

    private void adjustFeagmentCount(int state) {
        if (state==1){
            fragmentList.add(new SportFragment());
        }
        fragmentList.add(new HRVFragment());
        fragmentList.add(new HeartRateFragment());
        fragmentList.add(new ECGFragment());
        if (state==1){
            fragmentList.add(new HRRFragment());
        }


        mAnalysisRateAdapter = new FragmentListRateAdapter(getSupportFragmentManager(), fragmentList);

        vp_analysis_content.setAdapter(mAnalysisRateAdapter);

        Log.i(TAG,"adjustFeagmentCount");
        Log.i(TAG,"state:"+state);
        if (state==0 && tv_analysis_sport.getVisibility()== View.VISIBLE){
            subFormAlCount = 1;
            tv_analysis_sport.setVisibility(View.GONE);
            tv_analysis_hrr.setVisibility(View.GONE);
            mOneTableWidth = MyUtil.getScreeenWidth(this)/3;

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
            params.width = (int) (MyUtil.getScreeenWidth(this)/3);
            v_analysis_select.setLayoutParams(params);
        }
    }

    private void addFragmentList(int state) {
        if (state==1){
            fragmentList.add(new SportFragment());
        }
        else {

        }
        fragmentList.add(new HRVFragment());
        fragmentList.add(new HeartRateFragment());
        fragmentList.add(new ECGFragment());
        fragmentList.add(new HRRFragment());

        mAnalysisRateAdapter = new FragmentListRateAdapter(getSupportFragmentManager(), fragmentList);
        vp_analysis_content.setAdapter(mAnalysisRateAdapter);
    }

    private class MyClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_analysis_sport:
                    setViewPageItem(0,vp_analysis_content.getCurrentItem());
                    break;
                case R.id.tv_analysis_hrv:
                    setViewPageItem(1,vp_analysis_content.getCurrentItem());
                    break;
                case R.id.tv_analysis_rate:
                    setViewPageItem(2,vp_analysis_content.getCurrentItem());
                    break;
                case R.id.tv_analysis_ecg:
                    setViewPageItem(3,vp_analysis_content.getCurrentItem());
                    break;
                case R.id.tv_analysis_hrr:
                    setViewPageItem(4,vp_analysis_content.getCurrentItem());
                    break;
            }
        }
    }

    //点击时设置选中条目
    public void setViewPageItem(int viewPageItem,int currentItem) {
        viewPageItem = viewPageItem-subFormAlCount;
        if (currentItem==viewPageItem){
            return;
        }
        vp_analysis_content.setCurrentItem(viewPageItem);
        RelativeLayout.LayoutParams layoutParams =   (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
        int floatWidth= (int) (mOneTableWidth*viewPageItem);  //view向左的偏移量
        layoutParams.setMargins(floatWidth,0,0,0); //4个参数按顺序分别是左上右下
        v_analysis_select.setLayoutParams(layoutParams);

        setViewPageTextColor(viewPageItem+subFormAlCount);

    }

    //设置文本颜色
    private void setViewPageTextColor(int viewPageItem) {
        switch (viewPageItem){
            case 0:
                tv_analysis_hrv.setTextColor(Color.parseColor("#999999"));
                tv_analysis_rate.setTextColor(Color.parseColor("#999999"));
                tv_analysis_ecg.setTextColor(Color.parseColor("#999999"));
                tv_analysis_hrr.setTextColor(Color.parseColor("#999999"));
                tv_analysis_sport.setTextColor(Color.parseColor("#0c64b5"));
                break;
            case 1:
                tv_analysis_hrv.setTextColor(Color.parseColor("#0c64b5"));
                tv_analysis_rate.setTextColor(Color.parseColor("#999999"));
                tv_analysis_ecg.setTextColor(Color.parseColor("#999999"));
                tv_analysis_hrr.setTextColor(Color.parseColor("#999999"));
                tv_analysis_sport.setTextColor(Color.parseColor("#999999"));
                break;
            case 2:
                tv_analysis_hrv.setTextColor(Color.parseColor("#999999"));
                tv_analysis_rate.setTextColor(Color.parseColor("#0c64b5"));
                tv_analysis_ecg.setTextColor(Color.parseColor("#999999"));
                tv_analysis_hrr.setTextColor(Color.parseColor("#999999"));
                tv_analysis_sport.setTextColor(Color.parseColor("#999999"));
                break;
            case 3:
                tv_analysis_hrv.setTextColor(Color.parseColor("#999999"));
                tv_analysis_rate.setTextColor(Color.parseColor("#999999"));
                tv_analysis_ecg.setTextColor(Color.parseColor("#0c64b5"));
                tv_analysis_hrr.setTextColor(Color.parseColor("#999999"));
                tv_analysis_sport.setTextColor(Color.parseColor("#999999"));
                break;
            case 4:
                tv_analysis_hrv.setTextColor(Color.parseColor("#999999"));
                tv_analysis_rate.setTextColor(Color.parseColor("#999999"));
                tv_analysis_ecg.setTextColor(Color.parseColor("#999999"));
                tv_analysis_hrr.setTextColor(Color.parseColor("#0c64b5"));
                tv_analysis_sport.setTextColor(Color.parseColor("#999999"));
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
    }

    private boolean isActiivtyDestroy;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        isActiivtyDestroy = true;
    }


    /*public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            startActivity(new Intent(RateAnalysisActivity.this,MainActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
*/
}
