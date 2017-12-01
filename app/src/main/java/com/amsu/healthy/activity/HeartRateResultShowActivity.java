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
import com.amsu.healthy.bean.ParcelableDoubleList;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.fragment.analysis.ECGFragment;
import com.amsu.healthy.fragment.analysis.HRRFragment;
import com.amsu.healthy.fragment.analysis.HRVFragment;
import com.amsu.healthy.fragment.analysis.HeartRateFragment;
import com.amsu.healthy.fragment.analysis.SportFragment;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.OffLineDbAdapter;
import com.amsu.healthy.utils.map.DbAdapter;
import com.amsu.healthy.utils.map.PathRecord;
import com.amsu.healthy.utils.map.Util;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeartRateResultShowActivity extends BaseActivity {

    private static final String TAG = "HeartRateResultShowActivity";
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
    private ImageView iv_base_myreport;
    public static int state;

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
                startActivity(new Intent(HeartRateResultShowActivity.this, HistoryRecordActivity.class));
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
                startActivity(new Intent(HeartRateResultShowActivity.this, MyReportActivity.class));
            }
        });
    }

    private void initData() {
        final Intent intent = getIntent();
        if (intent!=null){
            Bundle bundle = intent.getParcelableExtra("bundle");
            if (bundle!=null){
                UploadRecord uploadRecord = bundle.getParcelable("uploadRecord");
                Log.i(TAG,"uploadRecord:"+uploadRecord);
                if (uploadRecord==null){
                    final HistoryRecord historyRecord = bundle.getParcelable("historyRecord");
                    if (historyRecord!=null){
                        Log.i(TAG,"historyRecord:"+historyRecord.toString());
                        String datatime = MyUtil.getSpecialFormatTime("yyyy-MM-dd HH:mm",new Date(historyRecord.getDatatime()));  //此处的datatime是时间戳（毫秒）
                        setCenterText(datatime);

                        state = historyRecord.getState();

                        //奔溃缓存策略
                        //先从本地数据库中根据datatime查询数据，没有的话根据id从服务器获取
                        OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(HeartRateResultShowActivity.this);
                        try {
                            offLineDbAdapter.open();
                        }catch (Exception ignored){
                        }

                        Log.i(TAG,"historyRecord.getDatatime():"+historyRecord.getDatatime());

                        uploadRecord = offLineDbAdapter.queryRecordByTimestamp(historyRecord.getDatatime()/1000);  // 2017-06-28 11:01:33
                        Log.i(TAG,"本地数据库uploadRecord:"+uploadRecord);
                        try {
                            offLineDbAdapter.close();
                        }catch (Exception ignored){
                        }

                        if (uploadRecord!=null){
                            mUploadRecord = uploadRecord;
                            adjustFeagmentCount(historyRecord.getState());
                        }
                        else {
                            //本地没有缓存,从服务器上取
                            getHistoryReportDetail(historyRecord);
                        }
                    }
                }
                else {
                    state = uploadRecord.state;
                    //当前分析结果，直接显示
                    if (uploadRecord.state==1 && uploadRecord.sportCreateRecordID>0){
                        DbAdapter dbAdapter = new DbAdapter(this);
                        dbAdapter.open();
                        PathRecord pathRecord = dbAdapter.queryRecordById((int) uploadRecord.sportCreateRecordID);
                        dbAdapter.close();
                        uploadRecord.latitudeLongitude = Util.getLatitude_longitudeString(pathRecord);
                    }

                    mUploadRecord = uploadRecord;
                    Log.i(TAG,"直接显示uploadRecord:"+uploadRecord.toString());
                    Log.i(TAG,"mUploadRecord.ae:"+uploadRecord.ae);

                    Log.i(TAG,"mUploadRecord.latitudeLongitude:"+uploadRecord.latitudeLongitude);
                    //String replace = mUploadRecord.getDatatime().replace("/", "-");//2016/10/24 10:56:4
                    String datatime = MyUtil.getSpecialFormatTime("yyyy-MM-dd HH:mm",new Date(uploadRecord.timestamp*1000));//此处的datatime是时间戳（秒）
                    Log.i(TAG,"mUploadRecord.timestamp:"+mUploadRecord.timestamp);
                    setCenterText(datatime);
                    //Log.i(TAG,"ecgLocalFileName:"+ecgLocalFileName);
                    adjustFeagmentCount(uploadRecord.state);
                }
            }
            else {
                adjustFeagmentCount(intent.getIntExtra(Constant.sportState,-1));
            }
        }
    }

    private void getHistoryReportDetail(final HistoryRecord historyRecord) {
        MyUtil.showDialog(getResources().getString(R.string.loading),HeartRateResultShowActivity.this);
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("id",historyRecord.getId());
        MyUtil.addCookieForHttp(params);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getHistoryReportDetailURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (isActiivtyDestroy)return;
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);

                //JsonBase<UploadRecord> jsonBase = MyUtil.commonJsonParse(result, new TypeToken<JsonBase<UploadRecord>>() {}.getType());
                //Log.i(TAG,"jsonBase："+jsonBase);

                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase.getRet()==0){
                    parseHealthData(result);
                    adjustFeagmentCount(historyRecord.getState());
                }
                else {
                    finish();
                    MyUtil.showToask(HeartRateResultShowActivity.this,"数据加载失败");
                }

                MyUtil.hideDialog(HeartRateResultShowActivity.this);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (isActiivtyDestroy)return;
                MyUtil.hideDialog(HeartRateResultShowActivity.this);
                MyUtil.showToask(HeartRateResultShowActivity.this,"数据加载失败");
                Log.i(TAG,"上传onFailure==s:"+s);
                finish();
            }
        });
    }

    private void parseHealthData(String result) {

        String iosDefaultString = "\"0\"";
        try {
            JSONObject object = new JSONObject(result);
            JSONObject jsonObject =object.getJSONObject("errDesc");
            String id    = jsonObject.getString("id");
            String fi    = jsonObject.getString("fi");
            String es    = jsonObject.getString("es");
            String pi    = jsonObject.getString("pi");
            String cc    = jsonObject.getString("cc");
            String hrvr  = jsonObject.getString("hrvr");
            String hrvs  = jsonObject.getString("hrvs");
            String ahr   = jsonObject.getString("ahr");
            String maxhr = jsonObject.getString("maxhr");
            String minhr = jsonObject.getString("minhr");
            String hrr   = jsonObject.getString("hrr");
            String hrs   = jsonObject.getString("hrs");
            String ec    = jsonObject.getString("ec");
            String ecr   = jsonObject.getString("ecr");
            String ecs   = jsonObject.getString("ecs");
            String ra    = jsonObject.getString("ra");
            String timestamp = jsonObject.getString("timestamp");
            String datatime  = jsonObject.getString("datatime");
            String hr    = jsonObject.getString("hr");
            String ae    = jsonObject.getString("ae");
            String distance  = jsonObject.getString("distance");
            String time  = jsonObject.getString("time");
            String cadence   = jsonObject.getString("cadence");
            String calorie   = jsonObject.getString("calorie");
            String state = jsonObject.getString("state");
            String zaobo = jsonObject.getString("zaobo");
            String loubo = jsonObject.getString("loubo");
            String latitudeLongitude = jsonObject.getString("latitudeLongitude");

            //后加字段
            String sdnn1 = jsonObject.getString("sdnn1");
            String sdnn2 = jsonObject.getString("sdnn2");
            String lf1 = jsonObject.getString("lf1");
            String lf2 = jsonObject.getString("lf2");
            String hf1 = jsonObject.getString("hf1");
            String hf2 = jsonObject.getString("hf2");
            String hf = jsonObject.getString("hf");
            String lf = jsonObject.getString("lf");
            String chaosPlotPoint = jsonObject.getString("chaosPlotPoint");
            String frequencyDomainDiagramPoint = jsonObject.getString("frequencyDomainDiagramPoint");
            String chaosPlotMajorAxis = jsonObject.getString("chaosPlotMajorAxis");
            String chaosPlotMinorAxis = jsonObject.getString("chaosPlotMinorAxis");


            UploadRecord uploadRecord = new UploadRecord();
            uploadRecord.id = Long.parseLong(id);
            uploadRecord.fi = Integer.parseInt(fi);
            uploadRecord.es = Integer.parseInt(es);
            uploadRecord.pi = Integer.parseInt(pi);
            uploadRecord.cc = Integer.parseInt(cc);
            uploadRecord.hrvr = hrvr;
            uploadRecord.hrvs = hrvs;
            uploadRecord.ahr = Integer.parseInt(ahr);
            uploadRecord.maxhr = Integer.parseInt(maxhr);
            uploadRecord.minhr = Integer.parseInt(minhr);
            uploadRecord.hrr = hrr;
            uploadRecord.hrs = hrs;
            uploadRecord.ec =ec;
            uploadRecord.ecr =Integer.parseInt(ecr);
            uploadRecord.ecs =ecs;
            uploadRecord.ra =Integer.parseInt(ra);
            uploadRecord.timestamp =Long.parseLong(timestamp);
            uploadRecord.datatime =datatime;

            Log.i(TAG,"ae:"+ae);

            /*if (!MyUtil.isEmpty(hr) && !hr.equals(Constant.uploadRecordDefaultString)  && !hr.equals("-1")){
                uploadRecord.hr = gson.fromJson(hr,new TypeToken<List<Integer>>() {}.getType());
            }*/
            /*if (!MyUtil.isEmpty(ae) && !ae.equals(Constant.uploadRecordDefaultString)){
                uploadRecord.ae =gson.fromJson(ae,new TypeToken<List<Integer>>() {}.getType());
            }*/

            uploadRecord.hr = MyUtil.parseListJson(hr,new TypeToken<List<Integer>>() {}.getType());
            uploadRecord.ae = MyUtil.parseListJson(ae,new TypeToken<List<Integer>>() {}.getType());
            uploadRecord.cadence = MyUtil.parseListJson(cadence,new TypeToken<List<Integer>>() {}.getType());
            uploadRecord.calorie =MyUtil.parseListJson(calorie,new TypeToken<List<String>>() {}.getType());
            uploadRecord.latitudeLongitude = MyUtil.parseListJson(latitudeLongitude,new TypeToken<List<ParcelableDoubleList>>() {}.getType());

            /*if (!MyUtil.isEmpty(cadence) && !cadence.equals(Constant.uploadRecordDefaultString) ){
                uploadRecord.cadence = gson.fromJson(cadence,new TypeToken<List<Integer>>() {}.getType());
            }
            if (!MyUtil.isEmpty(calorie) && !calorie.equals(Constant.uploadRecordDefaultString)){
                uploadRecord.calorie =gson.fromJson(calorie,new TypeToken<List<String>>() {}.getType());
            }*/

           /* Log.i(TAG,"latitudeLongitude:"+latitudeLongitude);
            Log.i(TAG,"latitudeLongitude:"+latitudeLongitude.length());
            if (!MyUtil.isEmpty(latitudeLongitude) && !latitudeLongitude.equals(Constant.uploadRecordDefaultString) && latitudeLongitude.length()>5){
                uploadRecord.latitudeLongitude = gson.fromJson(latitudeLongitude,new TypeToken<List<ParcelableDoubleList>>() {}.getType());
            }*/

            uploadRecord.distance = Float.parseFloat(distance);
            uploadRecord.time = (long) Float.parseFloat(time);
            uploadRecord.state =Integer.parseInt(state);
            uploadRecord.zaobo =Integer.parseInt(zaobo);
            uploadRecord.loubo =Integer.parseInt(loubo);

            uploadRecord.localEcgFileName = MyUtil.generateECGFilePath(HeartRateResultShowActivity.this, System.currentTimeMillis());

            if (!MyUtil.isEmpty(sdnn1) && !sdnn1.equals("null")){
                uploadRecord.sdnn1 = (int) Float.parseFloat(sdnn1);
            }
            if (!MyUtil.isEmpty(sdnn2) && !sdnn2.equals("null")){
                uploadRecord.sdnn2 = (int) Float.parseFloat(sdnn2);
            }

            if (!MyUtil.isEmpty(lf1) && !lf1.equals("null")){
                uploadRecord.lf1 = Double.parseDouble(lf1);
            }
            if (!MyUtil.isEmpty(lf2) && !lf2.equals("null")){
                uploadRecord.lf2 = Double.parseDouble(lf2);
            }
            if (!MyUtil.isEmpty(hf1) && !hf1.equals("null")){
                uploadRecord.hf1 = Double.parseDouble(hf1);
            }
            if (!MyUtil.isEmpty(hf2) && !hf2.equals("null")){
                uploadRecord.hf2 = Double.parseDouble(hf2);
            }
            if (!MyUtil.isEmpty(hf) && !hf.equals("null")){
                uploadRecord.hf = Double.parseDouble(hf);
            }
            if (!MyUtil.isEmpty(lf) && !lf.equals("null")){
                uploadRecord.lf = Double.parseDouble(lf);
            }

            uploadRecord.chaosPlotPoint = MyUtil.parseListJson(chaosPlotPoint,new TypeToken<List<Integer>>() {}.getType());
            uploadRecord.frequencyDomainDiagramPoint = MyUtil.parseListJson(frequencyDomainDiagramPoint,new TypeToken<List<Double>>() {}.getType());

            /*if (!MyUtil.isEmpty(chaosPlotPoint) && !chaosPlotPoint.equals(Constant.uploadRecordDefaultString) ){
                uploadRecord.chaosPlotPoint = gson.fromJson(chaosPlotPoint,new TypeToken<List<Integer>>() {}.getType());
            }

            if (!MyUtil.isEmpty(frequencyDomainDiagramPoint) && !frequencyDomainDiagramPoint.equals(Constant.uploadRecordDefaultString) ){
                uploadRecord.frequencyDomainDiagramPoint = gson.fromJson(frequencyDomainDiagramPoint,new TypeToken<List<Double>>() {}.getType());
            }*/

            if (!MyUtil.isEmpty(chaosPlotMajorAxis) && !chaosPlotMajorAxis.equals("null") && !chaosPlotMajorAxis.equals(iosDefaultString)){
                uploadRecord.chaosPlotMajorAxis =  (int) Float.parseFloat(chaosPlotMajorAxis);
            }
            if (!MyUtil.isEmpty(chaosPlotMinorAxis) && !chaosPlotMinorAxis.equals("null") && !chaosPlotMajorAxis.equals(iosDefaultString)){
                uploadRecord.chaosPlotMinorAxis =  (int) Float.parseFloat(chaosPlotMinorAxis);
            }

            uploadRecord.uploadState = 1;

            mUploadRecord = uploadRecord;

            //Log.i(TAG,"mUploadRecord:"+mUploadRecord);
            //Log.i(TAG,"mUploadRecord.ae:"+mUploadRecord.ae);

            OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(HeartRateResultShowActivity.this);
            offLineDbAdapter.open();
            //mUploadRecord.datatime = mUploadRecord.datatime.replace("/", "-");  //将本地数据库时间改成和服务器一致，下次查看数据时，先从根据时间从本地查询


            if (offLineDbAdapter!=null){
                long createOrUpdateUploadReportObject = offLineDbAdapter.createOrUpdateUploadReportObject(uploadRecord);
                Log.i(TAG,"createOrUpdateUploadReportObject:"+createOrUpdateUploadReportObject);
                offLineDbAdapter.close();
            }
            Log.i(TAG,"mUploadRecord:"+mUploadRecord);
            Log.i(TAG,"latitudeLongitude:"+latitudeLongitude);
            Log.i(TAG,"mUploadRecord.latitudeLongitude:"+mUploadRecord.latitudeLongitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NumberFormatException e){
            e.printStackTrace();
            Log.e(TAG,"e:"+e);
        }catch (JsonSyntaxException e){
            e.printStackTrace();
            Log.e(TAG,"e1:"+e);
        }
    }



    private void adjustFeagmentCount(int state) {
        fragmentList.clear();
        if (state!=0){
            fragmentList.add(new SportFragment());
        }
        fragmentList.add(new HRVFragment());
        fragmentList.add(new HeartRateFragment());
        fragmentList.add(new ECGFragment());
        if (state!=0){
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
        mUploadRecord  = null;
    }


    /*public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            startActivity(new Intent(HeartRateResultShowActivity.this,MainActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
*/
}
