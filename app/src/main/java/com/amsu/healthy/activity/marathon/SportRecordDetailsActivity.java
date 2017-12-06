package com.amsu.healthy.activity.marathon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.appication.FragmentAdapter;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.ParcelableDoubleList;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.fragment.marathon.SportRecordSpeedFragment;
import com.amsu.healthy.fragment.marathon.SportRecordStatisticsFragment;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.OffLineDbAdapter;
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
import java.util.List;

import static com.amsu.healthy.activity.HeartRateResultShowActivity.mUploadRecord;

/**
 * author：WangLei
 * date:2017/10/25.
 * QQ:619321796
 */

public class SportRecordDetailsActivity extends BaseActivity {
    public static Intent createIntent(Context context, int id) {
        Intent intent = new Intent(context, SportRecordDetailsActivity.class);
        intent.putExtra("id", id);
        return intent;
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, SportRecordDetailsActivity.class);
    }

    private ViewPager mViewPager;
    private View sportRecordDetailsStatistic;
    private View sportRecordDetailsSpeed;
    private LinearLayout buttonGroup;
    private String TAG = "SportRecordDetailsActivity";
    private SportRecordStatisticsFragment sportRecordStatisticsFragment;
    private SportRecordSpeedFragment sportRecordSpeedFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_record_details);
        initViews();
        initEvents();
        int id = getIntent().getIntExtra("id", 0);
        if (id != 0) {
            getHistoryReportDetail(id);
        }
    }

    private void initViews() {
        buttonGroup = (LinearLayout) findViewById(R.id.buttonGroup);
        sportRecordDetailsSpeed = findViewById(R.id.sportRecordDetailsSpeed);
        sportRecordDetailsStatistic = findViewById(R.id.sportRecordDetailsStatistic);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        sportRecordStatisticsFragment = SportRecordStatisticsFragment.newInstance();
        sportRecordSpeedFragment = SportRecordSpeedFragment.newInstance();
        List<Fragment> fragmentActivityList = new ArrayList<>();
        fragmentActivityList.add(sportRecordStatisticsFragment);
        fragmentActivityList.add(sportRecordSpeedFragment);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentActivityList);
        mViewPager.setAdapter(fragmentAdapter);
        sportRecordDetailsStatistic.setSelected(true);
    }

    private void initEvents() {
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setAllSelectNon();
                buttonGroup.getChildAt(position).setSelected(true);
            }
        });
        sportRecordDetailsStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });
        sportRecordDetailsSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(1);
            }
        });
        findViewById(R.id.iv_base_leftimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setAllSelectNon() {
        sportRecordDetailsSpeed.setSelected(false);
        sportRecordDetailsStatistic.setSelected(false);
    }

    private boolean isActivityDestroy;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityDestroy = true;
    }

    private void getHistoryReportDetail(final int id) {
        MyUtil.showDialog(getResources().getString(R.string.loading), this);
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("id", String.valueOf(id));
        MyUtil.addCookieForHttp(params);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getHistoryReportDetailURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (isActivityDestroy) return;
                String result = responseInfo.result;
                Log.i(TAG, "上传onSuccess==result:" + result);

                //JsonBase<UploadRecord> jsonBase = MyUtil.commonJsonParse(result, new TypeToken<JsonBase<UploadRecord>>() {}.getType());
                //Log.i(TAG,"jsonBase："+jsonBase);

                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG, "jsonBase:" + jsonBase);
                if (jsonBase.getRet() == 0) {
                    parseHealthData(result);
                } else {
                    finish();
                    MyUtil.showToask(SportRecordDetailsActivity.this, "数据加载失败");
                }

                MyUtil.hideDialog(SportRecordDetailsActivity.this);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (isActivityDestroy) return;
                MyUtil.hideDialog(SportRecordDetailsActivity.this);
                MyUtil.showToask(SportRecordDetailsActivity.this, "数据加载失败");
                Log.i(TAG, "上传onFailure==s:" + s);
                finish();
            }
        });
    }

    private void parseHealthData(String result) {
        String iosDefaultString = "\"0\"";
        try {
            JSONObject object = new JSONObject(result);
            JSONObject jsonObject = object.getJSONObject("errDesc");
            String id = jsonObject.getString("id");
            String fi = jsonObject.getString("fi");
            String es = jsonObject.getString("es");
            String pi = jsonObject.getString("pi");
            String cc = jsonObject.getString("cc");
            String hrvr = jsonObject.getString("hrvr");
            String hrvs = jsonObject.getString("hrvs");
            String ahr = jsonObject.getString("ahr");
            String maxhr = jsonObject.getString("maxhr");
            String minhr = jsonObject.getString("minhr");
            String hrr = jsonObject.getString("hrr");
            String hrs = jsonObject.getString("hrs");
            String ec = jsonObject.getString("ec");
            String ecr = jsonObject.getString("ecr");
            String ecs = jsonObject.getString("ecs");
            String ra = jsonObject.getString("ra");
            String timestamp = jsonObject.getString("timestamp");
            String datatime = jsonObject.getString("datatime");
            String hr = jsonObject.getString("hr");
            String ae = jsonObject.getString("ae");
            String distance = jsonObject.getString("distance");
            String time = jsonObject.getString("time");
            String cadence = jsonObject.getString("cadence");
            String calorie = jsonObject.getString("calorie");
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
            uploadRecord.ec = ec;
            uploadRecord.ecr = Integer.parseInt(ecr);
            uploadRecord.ecs = ecs;
            uploadRecord.ra = Integer.parseInt(ra);
            uploadRecord.timestamp = Long.parseLong(timestamp);
            uploadRecord.datatime = datatime;
            uploadRecord.aeMarathon = ae;
            Gson gson = new Gson();
            if (!MyUtil.isEmpty(hr) && !hr.equals(Constant.uploadRecordDefaultString) && !hr.equals("-1")) {
                uploadRecord.hr = gson.fromJson(hr, new TypeToken<List<Integer>>() {
                }.getType());
            }
            if (!MyUtil.isEmpty(cadence) && !cadence.equals(Constant.uploadRecordDefaultString)) {
                uploadRecord.cadence = gson.fromJson(cadence, new TypeToken<List<Integer>>() {
                }.getType());
            }
            if (!MyUtil.isEmpty(calorie) && !calorie.equals(Constant.uploadRecordDefaultString)) {
                uploadRecord.calorie = gson.fromJson(calorie, new TypeToken<List<String>>() {
                }.getType());
            }
            Log.i(TAG, "latitudeLongitude:" + latitudeLongitude);
            Log.i(TAG, "latitudeLongitude:" + latitudeLongitude.length());
            if (!MyUtil.isEmpty(latitudeLongitude) && !latitudeLongitude.equals(Constant.uploadRecordDefaultString) && latitudeLongitude.length() > 5) {
                uploadRecord.latitudeLongitude = gson.fromJson(latitudeLongitude, new TypeToken<List<ParcelableDoubleList>>() {
                }.getType());
            }

            uploadRecord.distance = Float.parseFloat(distance);
            uploadRecord.time = (long) Float.parseFloat(time);
            uploadRecord.state = Integer.parseInt(state);
            uploadRecord.zaobo = Integer.parseInt(zaobo);
            uploadRecord.loubo = Integer.parseInt(loubo);

            uploadRecord.localEcgFileName = MyUtil.generateECGFilePath(this, System.currentTimeMillis());

            if (!MyUtil.isEmpty(sdnn1) && !sdnn1.equals("null")) {
                uploadRecord.sdnn1 = (int) Float.parseFloat(sdnn1);
            }
            if (!MyUtil.isEmpty(sdnn2) && !sdnn2.equals("null")) {
                uploadRecord.sdnn2 = (int) Float.parseFloat(sdnn2);
            }

            if (!MyUtil.isEmpty(lf1) && !lf1.equals("null")) {
                uploadRecord.lf1 = Double.parseDouble(lf1);
            }
            if (!MyUtil.isEmpty(lf2) && !lf2.equals("null")) {
                uploadRecord.lf2 = Double.parseDouble(lf2);
            }
            if (!MyUtil.isEmpty(hf1) && !hf1.equals("null")) {
                uploadRecord.hf1 = Double.parseDouble(hf1);
            }
            if (!MyUtil.isEmpty(hf2) && !hf2.equals("null")) {
                uploadRecord.hf2 = Double.parseDouble(hf2);
            }
            if (!MyUtil.isEmpty(hf) && !hf.equals("null")) {
                uploadRecord.hf = Double.parseDouble(hf);
            }
            if (!MyUtil.isEmpty(lf) && !lf.equals("null")) {
                uploadRecord.lf = Double.parseDouble(lf);
            }

            if (!MyUtil.isEmpty(chaosPlotPoint) && !chaosPlotPoint.equals(Constant.uploadRecordDefaultString)) {
                uploadRecord.chaosPlotPoint = gson.fromJson(chaosPlotPoint, new TypeToken<List<Integer>>() {
                }.getType());
            }

            if (!MyUtil.isEmpty(frequencyDomainDiagramPoint) && !frequencyDomainDiagramPoint.equals(Constant.uploadRecordDefaultString)) {
                uploadRecord.frequencyDomainDiagramPoint = gson.fromJson(frequencyDomainDiagramPoint, new TypeToken<List<Double>>() {
                }.getType());
            }

            if (!MyUtil.isEmpty(chaosPlotMajorAxis) && !chaosPlotMajorAxis.equals("null") && !chaosPlotMajorAxis.equals(iosDefaultString)) {
                uploadRecord.chaosPlotMajorAxis = (int) Float.parseFloat(chaosPlotMajorAxis);
            }
            if (!MyUtil.isEmpty(chaosPlotMinorAxis) && !chaosPlotMinorAxis.equals("null") && !chaosPlotMajorAxis.equals(iosDefaultString)) {
                uploadRecord.chaosPlotMinorAxis = (int) Float.parseFloat(chaosPlotMinorAxis);
            }

            uploadRecord.uploadState = 1;

            mUploadRecord = uploadRecord;

            //Log.i(TAG,"mUploadRecord:"+mUploadRecord);
            //Log.i(TAG,"mUploadRecord.ae:"+mUploadRecord.ae);
            initUI();
            OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(this);
            offLineDbAdapter.open();
            long createOrUpdateUploadReportObject = offLineDbAdapter.createOrUpdateUploadReportObject(uploadRecord);
            Log.i(TAG, "createOrUpdateUploadReportObject:" + createOrUpdateUploadReportObject);
            offLineDbAdapter.close();
            Log.i(TAG, "mUploadRecord:" + mUploadRecord);
            Log.i(TAG, "latitudeLongitude:" + latitudeLongitude);
            Log.i(TAG, "mUploadRecord.latitudeLongitude:" + mUploadRecord.latitudeLongitude);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e(TAG, "e:" + e);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            Log.e(TAG, "e1:" + e);
        }
    }

    private void initUI() {
        if (sportRecordStatisticsFragment != null) {
            sportRecordStatisticsFragment.initUi();
        }
        if (sportRecordSpeedFragment != null) {
            sportRecordSpeedFragment.initUi();
        }
    }
}
