package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.utils.ApkUtil;
import com.amsu.healthy.utils.AppAbortDbAdapter;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.OffLineDbAdapter;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    public static boolean isSplashActivityStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.i(TAG,"onCreate");

        isSplashActivityStarted = true;
        initView();


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

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

    private void initView() {

        //MyUtil.putStringValueFromSP("abortDatas","");
        AppAbortDataSave abortDataFromSP = AppAbortDbAdapter.getAbortDataFromSP();
        Log.i(TAG,"abortDataFromSP:"+abortDataFromSP);
        if (abortDataFromSP!=null){
            Intent intent1 = new Intent(this,MainActivity.class);
            intent1.putExtra(Constant.isNeedRecoverAbortData,true);
            startActivity(intent1);

            Intent intent2 = new Intent(this,StartRunActivity.class);
            intent2.putExtra(Constant.isNeedRecoverAbortData,true);
            startActivity(intent2);
            finish();
            return;
        }
        Log.i(TAG,"开启线程");
        TextView tv_splish_mark = (TextView) findViewById(R.id.tv_splish_mark);

        tv_splish_mark.setText(getResources().getString(R.string.app_name)+" "+ApkUtil.getVersionName(this));

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /*boolean isInstall = MyApplication.sharedPreferences.getBoolean("isInstall", false);
                if (isInstall){
                    startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                }
                else {
                    startActivity(new Intent(SplashActivity.this,InstallGuideActivity.class));
                    MyApplication.sharedPreferences.edit().putBoolean("isInstall",true).apply();
                }*/
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        }.start();

        initData();
    }

    private void initData() {
        downlaodWeekReport(-1,-1,false,null);
        ApkUtil.checkUpdate(this);

        boolean networkConnected = MyUtil.isNetworkConnected(this);
        if (networkConnected){
            //有网络连接
            new Thread(){
                @Override
                public void run() {
                    startUploadOffLineData(SplashActivity.this);
                }
            }.start();

        }
    }

    private void startUploadOffLineData(Context context) {
        OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(context);
        offLineDbAdapter.open();

        List<UploadRecord> uploadRecordsState = offLineDbAdapter.queryRecordByUploadState("0");
        Log.i(TAG,"uploadRecordsState:"+uploadRecordsState);
        Log.i(TAG,"uploadRecordsState.size():"+uploadRecordsState.size());

        for (UploadRecord uploadRecord:uploadRecordsState){
            HeartRateActivity.uploadRecordDataToServer(uploadRecord,context,true);
        }
    }

    //下载最新周报告
    public static void downlaodWeekReport(int year, int weekOfYear, final boolean isFromLogin, final Activity activity) {

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
                MyUtil.hideDialog(activity);
                if (isFromLogin){
                    activity.startActivity(new Intent(activity,MainActivity.class));
                    MyUtil.destoryAllAvtivity();
                }
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase.getRet()==0){
                    HealthIndicatorAssessActivity.WeekReport weekReport = gson.fromJson(result, HealthIndicatorAssessActivity.WeekReport.class);
                    Log.i(TAG,"weekReport:"+ weekReport.toString());
                    setIndicatorData(weekReport,activity);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog(activity);
                if (isFromLogin){
                    activity.startActivity(new Intent(activity,MainActivity.class));
                    MyUtil.destoryAllAvtivity();
                }
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }

    public static void setIndicatorData(HealthIndicatorAssessActivity.WeekReport weekReport,Activity activity){
        if (weekReport!=null){
            //BMI
            IndicatorAssess scoreBMI = HealthyIndexUtil.calculateScoreBMI(MyApplication.appContext);
            //储备心率
            IndicatorAssess scorehrReserve = HealthyIndexUtil.calculateScorehrReserve(MyApplication.appContext);

            List<String> huifuxinlv = weekReport.errDesc.huifuxinlv;
            int sum = 0;
            int count = 0;
            for (String s:huifuxinlv){
                if (Integer.parseInt(s)>0){
                    sum += Integer.parseInt(s);
                    count++;
                }
            }
            IndicatorAssess scoreHRR = null;
            if(count>0){
                int avHhrr = sum/count;
                //恢复心率HRR
                scoreHRR = HealthyIndexUtil.calculateScoreHRR(avHhrr,MyApplication.appContext);
            }
            else {
                scoreHRR = HealthyIndexUtil.calculateScoreHRR(0,MyApplication.appContext);
            }

            List<String> kangpilaozhishu = weekReport.errDesc.kangpilaozhishu;
            sum = 0;
            count = 0;
            for (String s:kangpilaozhishu){
                if (Integer.parseInt(s)>0){
                    sum += Integer.parseInt(s);
                    count++;
                }
            }
            IndicatorAssess scoreHRV = null;
            if(count>0){
                int avHhrv = sum/count;
                //抗疲劳指数HRV(心电分析算法得出)
                scoreHRV = HealthyIndexUtil.calculateScoreHRV(avHhrv,MyApplication.appContext);
            }
            else {
                scoreHRV = HealthyIndexUtil.calculateScoreHRV(0,MyApplication.appContext);
            }

            List<String> guosuguohuan = weekReport.errDesc.guosuguohuan;
            sum = 0;
            count = 0;
            for (String s:guosuguohuan){
                if (Integer.parseInt(s)>0){
                    count++;
                    sum += Integer.parseInt(s);
                }
            }
            IndicatorAssess scoreOver_slow = null;
            if(count>0){
                int over_slow = sum/count;
                Log.i(TAG,"over_slow:"+over_slow);
                //过缓/过速(心电分析算法得出)
                scoreOver_slow = HealthyIndexUtil.calculateScoreOver_slow(over_slow,MyApplication.appContext);
            }
            else {
                scoreOver_slow = HealthyIndexUtil.calculateScoreOver_slow(0,MyApplication.appContext);
            }

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
                scoreBeat = HealthyIndexUtil.calculateScoreBeat(zaobo,loubo,MyApplication.appContext);

                IndicatorAssess zaoboIndicatorAssess = HealthyIndexUtil.calculateTypeBeforeBeat(zaobo,MyApplication.appContext);
                IndicatorAssess louboIndicatorAssess = HealthyIndexUtil.calculateTypeMissBeat(loubo,MyApplication.appContext);
                MyUtil.putIntValueFromSP("zaoboIndicatorAssess",zaoboIndicatorAssess.getPercent());
                MyUtil.putIntValueFromSP("louboIndicatorAssess",louboIndicatorAssess.getPercent());
            }

            // 健康储备(按训练时间计算)
            IndicatorAssess scoreReserveHealth = HealthyIndexUtil.calculateScoreReserveHealth();

            int healthyIindexvalue = HealthyIndexUtil.calculateIndexvalue(scoreBMI, scorehrReserve, scoreHRR, scoreHRV, scoreOver_slow, scoreBeat, scoreReserveHealth);
            MyUtil.putIntValueFromSP("healthyIindexvalue",healthyIindexvalue);

            int physicalAge = HealthyIndexUtil.calculatePhysicalAge(scoreBMI, scorehrReserve, scoreHRR, scoreHRV, scoreReserveHealth);
            MyUtil.putIntValueFromSP("physicalAge",physicalAge);

            MyUtil.putIntValueFromSP("scoreOver_slowPercent",scoreOver_slow.getPercent());


        }
    }
}


