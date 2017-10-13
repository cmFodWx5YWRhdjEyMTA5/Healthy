package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.insole.InsoleRunningActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.AppAbortDataSaveInsole;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.bean.WeekReport;
import com.amsu.healthy.utils.ApkUtil;
import com.amsu.healthy.utils.AppAbortDbAdapterUtil;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.i(TAG,"onCreate");
        initView();
    }

    private void initView() {

        boolean isNeedRecover = judgeRecoverRunState();
        if (isNeedRecover){
            return;
        }

        TextView tv_splish_mark = (TextView) findViewById(R.id.tv_splish_mark);
        tv_splish_mark.setText(getResources().getString(R.string.app_name)+" "+ApkUtil.getVersionName(this));

        Log.i(TAG,"开启线程");
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
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        int mCurrYear = calendar.get(Calendar.YEAR);
        int mCurrWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        downlaodWeekReport(mCurrYear,mCurrWeekOfYear,false,null);

        ApkUtil.checkUpdate(this);

        boolean networkConnected = MyUtil.isNetworkConnected(MyApplication.appContext);
        if (networkConnected){
            //有网络连接
            new Thread(){
                @Override
                public void run() {
                    startUploadOffLineData(MyApplication.appContext);
                }
            }.start();
        }
    }

    //判断是否要恢复到之前的运动状态
    private boolean judgeRecoverRunState() {
        String stringValueFromSP = MyUtil.getStringValueFromSP("abortDatas");
        Log.i(TAG,"stringValueFromSP:"+stringValueFromSP);
        if (!MyUtil.isEmpty(stringValueFromSP) ){

            Intent intent1 = new Intent();
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra(Constant.isNeedRecoverAbortData,true);

            String[] split = stringValueFromSP.split("&&");
            if (split.length==2 && !MyUtil.isEmpty(split[0])){
                int i = Integer.parseInt(split[0]);

                if (i==Constant.sportType_Cloth){
                    intent1.setClass(this,StartRunActivity.class);
                }
                else  if (i==Constant.sportType_Insole){
                    intent1.setClass(this,InsoleRunningActivity.class);
                }

                startActivity(new Intent(this,MainActivity.class));

                startActivity(intent1);
                Log.i(TAG," startActivity(intent1);");
                finish();
                return true;
            }
        }
        return false;
    }

    private void startUploadOffLineData(Context context) {
        Log.i(TAG,"startUploadOffLineData:");
        OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(context);
        try {
            offLineDbAdapter.open();
            List<UploadRecord> uploadRecordsState = offLineDbAdapter.queryRecordByUploadState("0");
            try {
                offLineDbAdapter.close();
            }catch (Exception e1){
                Log.e(TAG,"e1:"+e1);
            }
            Log.i(TAG,"uploadRecordsState:"+uploadRecordsState);
            Log.i(TAG,"uploadRecordsState.size():"+uploadRecordsState.size());

            for (UploadRecord uploadRecord:uploadRecordsState){
                HeartRateAnalysisActivity.uploadRecordDataToServer(uploadRecord,context,true);
            }
        }catch (Exception e){
            Log.i(TAG,"e:"+e);
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
                   WeekReport weekReport = gson.fromJson(result, WeekReport.class);
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

    public static void setIndicatorData(WeekReport weekReport, Activity activity){
        if (weekReport!=null && weekReport.errDesc!=null && weekReport.errDesc.guosuguohuan!=null){
            //BMI
            IndicatorAssess scoreBMI = HealthyIndexUtil.calculateScoreBMI(MyApplication.appContext);
            //储备心率
            IndicatorAssess scorehrReserve = HealthyIndexUtil.calculateScorehrReserve(MyApplication.appContext);

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
                scoreHRR = HealthyIndexUtil.calculateScoreHRR(avHhrr,MyApplication.appContext);
            }
            else {
                scoreHRR = HealthyIndexUtil.calculateScoreHRR(0,MyApplication.appContext);
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
                scoreHRV = HealthyIndexUtil.calculateScoreHRV(avHhrv,MyApplication.appContext);
            }
            else {
                scoreHRV = HealthyIndexUtil.calculateScoreHRV(0,MyApplication.appContext);
            }

            List<String> guosuguohuan = weekReport.errDesc.guosuguohuan;
            sum = 0;
            count = 0;
            for (String s:guosuguohuan){
                if (!MyUtil.isEmpty(s) && !s.equals("null") && Integer.parseInt(s)>0){
                    count++;
                    sum += Integer.parseInt(s);
                }
            }
            IndicatorAssess scoreOver_slow = null;
            IndicatorAssess scoreSlow = null;  //过缓
            IndicatorAssess scoreOver = null;  //过速

            if(count>0){
                int over_slow = sum/count;
                Log.i(TAG,"over_slow:"+over_slow);
                //过缓/过速(心电分析算法得出)
                scoreOver_slow = HealthyIndexUtil.calculateScoreOver_slow(over_slow,MyApplication.appContext);

                scoreSlow = HealthyIndexUtil.calculateTypeSlow(over_slow,MyApplication.appContext);
                scoreOver = HealthyIndexUtil.calculateTypeOver(over_slow,MyApplication.appContext);
            }
            else {
                scoreOver_slow = HealthyIndexUtil.calculateScoreOver_slow(0,MyApplication.appContext);
            }

            IndicatorAssess zaoboIndicatorAssess = null;
            IndicatorAssess louboIndicatorAssess = null;
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
                scoreBeat = HealthyIndexUtil.calculateScoreBeat(zaobo,loubo,MyApplication.appContext);

                zaoboIndicatorAssess = HealthyIndexUtil.calculateTypeBeforeBeat(zaobo,MyApplication.appContext);
                louboIndicatorAssess = HealthyIndexUtil.calculateTypeMissBeat(loubo,MyApplication.appContext);
                MyUtil.putIntValueFromSP("zaoboIndicatorAssess",zaoboIndicatorAssess.getPercent());
                MyUtil.putIntValueFromSP("louboIndicatorAssess",louboIndicatorAssess.getPercent());
            }

            HealthyIndexUtil.calcuIndexWarringHeartIcon(scoreSlow,scoreOver,zaoboIndicatorAssess,louboIndicatorAssess);

            // 健康储备(按训练时间计算)
            IndicatorAssess scoreReserveHealth = HealthyIndexUtil.calculateScoreReserveHealth();

            Log.i(TAG,"scoreBMI:"+scoreBMI);
            Log.i(TAG,"scorehrReserve:"+scorehrReserve);
            Log.i(TAG,"scorehrReserve:"+scorehrReserve);
            Log.i(TAG,"scoreHRV:"+scoreHRV);
            Log.i(TAG,"scoreOver_slow:"+scoreOver_slow);
            Log.i(TAG,"scoreBeat:"+scoreBeat);
            Log.i(TAG,"scoreReserveHealth:"+scoreReserveHealth);

            int healthyIindexvalue = HealthyIndexUtil.calculateIndexvalue(scoreBMI, scorehrReserve, scorehrReserve, scoreHRV, scoreOver_slow, scoreBeat, scoreReserveHealth);
            Log.i(TAG,"healthyIindexvalue:"+healthyIindexvalue);
            MyUtil.putIntValueFromSP("healthyIindexvalue",healthyIindexvalue);

            int physicalAge = HealthyIndexUtil.calculatePhysicalAge(scoreBMI, scorehrReserve, scoreHRR, scoreHRV, scoreReserveHealth);
            MyUtil.putIntValueFromSP("physicalAge",physicalAge);

            MyUtil.putIntValueFromSP("scoreOver_slowPercent",scoreOver_slow.getPercent());


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

}


