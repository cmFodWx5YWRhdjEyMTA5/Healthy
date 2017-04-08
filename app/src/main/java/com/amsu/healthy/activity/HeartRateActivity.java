package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.EcgFilterUtil;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.map.DbAdapter;
import com.amsu.healthy.utils.map.PathRecord;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.test.objects.HeartRateResult;
import com.test.utils.DiagnosisNDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeartRateActivity extends BaseActivity {

    private static final String TAG = "HeartRateActivity";
    private Animation animation;
    private FileInputStream fileInputStream;
    private UploadRecord uploadRecord = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        initView();
        initData();
    }

    private void initView() {
        ImageView iv_heartrate_rotateimage = (ImageView) findViewById(R.id.iv_heartrate_rotateimage);
        animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(-1);
        animation.setInterpolator(new LinearInterpolator());

        iv_heartrate_rotateimage.setAnimation(animation);

    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                String heartData = MyUtil.getStringValueFromSP("heartData");
                Log.i(TAG,"heartData:"+heartData);

                /*try {
                    Thread.sleep(100);  //模拟下载数据 耗时
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                if (heartData.equals("")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animation.cancel();
                            Intent intent = new Intent(HeartRateActivity.this, RateAnalysisActivity.class);
                            int sportState = getIntent().getIntExtra(Constant.sportState, -1);
                            intent.putExtra(Constant.sportState,sportState);
                            startActivity(intent);
                            finish();
                        }
                    });
                    return;
                }

                String cacheFileName = MyUtil.getStringValueFromSP("cacheFileName");
                //String cacheFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/20170220210301.ecg";
                //String cacheFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/20170223160327.ecg";

                String fileBase64;
                if (!cacheFileName.equals("")){
                    File file = new File(cacheFileName);
                    try {
                        if (file.exists()){
                            fileBase64 = MyUtil.fileToBase64(file);
                            fileInputStream = new FileInputStream(cacheFileName);
                            DataInputStream dataInputStream = new DataInputStream(fileInputStream); //读取二进制文件
                            List<Integer> calcuData = new ArrayList<>();

                            byte[] bytes = new byte[2];
                            ByteBuffer buffer=  ByteBuffer.wrap(bytes);
                            while( dataInputStream.available() >0){
                                bytes[1] = dataInputStream.readByte();
                                bytes[0] = dataInputStream.readByte();
                                short readCsharpInt = buffer.getShort();
                                buffer.clear();
                                //滤波处理
                                int temp = EcgFilterUtil.miniEcgFilterLp(readCsharpInt, 0);
                                temp = EcgFilterUtil.miniEcgFilterHp(temp, 0);
                                calcuData.add(temp);
                            }
                            Log.i(TAG,"over  fileBase64:"+fileBase64);
                            analysisAndUpload(calcuData,fileBase64);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    private void analysisAndUpload(List<Integer> rateData,String fileBase64) {
        int[] calcuData = new int[rateData.size()];
        for (int i=0;i<rateData.size();i++ ){
            calcuData[i] = rateData.get(i);
        }
        HeartRateResult heartRateResult = DiagnosisNDK.AnalysisEcg(calcuData, calcuData.length, Constant.oneSecondFrame);
        Log.i(TAG,"heartRateResult:"+heartRateResult.toString());
        //转化成json并存在sp里
        Gson gson = new Gson();
        String gsonHeartRateResult = gson.toJson(heartRateResult);
        //MyUtil.putStringValueFromSP("gsonHeartRateResult",gsonHeartRateResult);

        /*HistoryDbAdapter dbAdapter = new HistoryDbAdapter(HeartRateActivity.this);
        dbAdapter.open();
        String specialFormatTime = MyUtil.getSpecialFormatTime("yyyy-MM-dd H:m:s", new Date());
        dbAdapter.createRecord(specialFormatTime, (int) (heartRateResult.LF/heartRateResult.HF),heartRateResult.RR_SDNN,heartData,cacheFileName,
                0,heartRateResult.RR_Normal,heartRateResult.RR_Sum-heartRateResult.RR_Normal,heartRateResult.RR_Boleakage,heartRateResult.RR_Apb+heartRateResult.RR_Pvc);

        List<RateRecord> rateRecords = dbAdapter.queryRecordAll();
        Log.i(TAG,"rateRecords.size()"+rateRecords.size());
        dbAdapter.close();
        for (int i=0;i<rateRecords.size();i++){
            Log.i(TAG,rateRecords.get(i).toString());
        }*/
        IndicatorAssess ESIndicatorAssess = HealthyIndexUtil.calculateLFHFMoodIndex((int) (heartRateResult.LF / heartRateResult.HF));
        String ES = String.valueOf(ESIndicatorAssess.getPercent());
        IndicatorAssess PIIndicatorAssess = HealthyIndexUtil.calculateSDNNPressureIndex(heartRateResult.RR_SDNN);
        String PI = String.valueOf(PIIndicatorAssess.getPercent());
        IndicatorAssess FIIndicatorAssess = HealthyIndexUtil.calculateSDNNSportIndex(heartRateResult.RR_SDNN);
        String FI = String.valueOf(FIIndicatorAssess.getPercent());

        String HRVs = ESIndicatorAssess.getSuggestion()+PIIndicatorAssess.getSuggestion()+FIIndicatorAssess.getSuggestion();

        String HR = MyUtil.getStringValueFromSP("heartData");

        List<Integer> heartDatas = gson.fromJson(HR, new TypeToken<List<Integer>>() {
        }.getType());

        int MaxHR=heartDatas.get(0);
        int MinHR=heartDatas.get(0);
        int sum = 0;
        for (int heart:heartDatas){
            if (heart>MaxHR){
                MaxHR =heart;
            }
            if (heart<MinHR){
                MinHR = heart;
            }
            sum += heart;
        }
        String AHR = String.valueOf(sum/heartDatas.size());
        //String  EC = MyUtil.encodeBase64String(calcuDataString);
        String  EC = "";
        if (!MyUtil.isEmpty(fileBase64)){
            EC = fileBase64;
        }

        String ECr = "1";
        if (heartRateResult.RR_Kuanbo>0){  //漏博
            ECr = "3";
        }
        else if (heartRateResult.RR_Apb+heartRateResult.RR_Pvc>0){ //早搏
            ECr="4";
        }
        String RA = "0";  //心率恢复能力
        String HRs = "未测出恢复心率";  //心率恢复能力健康意见

        String state = "-1";

        Intent intent = getIntent();
        if (intent!=null){
            int hrr = intent.getIntExtra("hrr", 0);
            int sportState = intent.getIntExtra(Constant.sportState, -1);
            if (hrr>0){
                IndicatorAssess hrrIndicatorAssess = HealthyIndexUtil.calculateScoreHRR(hrr);
                HRs = hrrIndicatorAssess.getSuggestion();
            }
            RA = hrr+"";
            if (sportState!=-1){
                state = sportState+"";
            }
        }
        //String timestamp = String.valueOf(System.currentTimeMillis());
        String timestamp = String.valueOf(HealthyDataActivity.ecgFiletimeMillis);
        String datatime = MyUtil.getSpecialFormatTime("yyyy/MM/dd H:m:s", new Date());

        String AE = "1";  //有氧无氧
        String distance = "-1";
        String time = "-1";
        String cadence = "-1";//步频
        String calorie = "-1";  //卡路里


        if (StartRunActivity.createrecord!=-1){
            DbAdapter dbAdapter = new DbAdapter(this);
            dbAdapter.open();
            PathRecord pathRecord = dbAdapter.queryRecordById((int) StartRunActivity.createrecord);
            dbAdapter.close();
            Log.i(TAG,"pathRecord:"+pathRecord.toString());

            distance = pathRecord.getDistance();
            time = pathRecord.getDuration();
        }

        uploadRecord = new UploadRecord(FI,ES,PI,"10","xx",HRVs,AHR,String.valueOf(MaxHR),String.valueOf(MinHR),"xxxx",HRs,EC,ECr,"xxxx",RA,timestamp,datatime,
                HR,AE,distance,time,cadence,calorie,state);
        Log.i(TAG,"uploadRecord:"+uploadRecord);

        //分析完成
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (animation!=null){
                    animation.cancel();
                }
            }
        });

        Intent intentToRateAnalysis = new Intent(HeartRateActivity.this, RateAnalysisActivity.class);
        if (uploadRecord!=null){
            Bundle bundle = new Bundle();
            bundle.putParcelable("uploadRecord",uploadRecord);
            intentToRateAnalysis.putExtra("bundle",bundle);
            Log.i(TAG,"uploadRecord: putParcelable  "+uploadRecord);
        }
        startActivity(intentToRateAnalysis);

        uploadData(uploadRecord);

        finish();
    }

    //上传分析结果
    private void uploadData(UploadRecord uploadRecord) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        MyUtil.addCookieForHttp(params);

        params.addBodyParameter("FI",uploadRecord.FI);
        params.addBodyParameter("ES",uploadRecord.ES);
        params.addBodyParameter("PI",uploadRecord.PI);
        params.addBodyParameter("CC",uploadRecord.CC);
        params.addBodyParameter("HRVr",uploadRecord.HRVr);
        params.addBodyParameter("HRVs",uploadRecord.HRVs);
        params.addBodyParameter("AHR",uploadRecord.AHR);
        params.addBodyParameter("MaxHR",uploadRecord.MaxHR);
        params.addBodyParameter("MinHR",uploadRecord.MinHR);
        params.addBodyParameter("HRr",uploadRecord.HRr);
        params.addBodyParameter("HRs",uploadRecord.HRs);
        params.addBodyParameter("EC",uploadRecord.EC);
        params.addBodyParameter("ECr",uploadRecord.ECr);
        params.addBodyParameter("ECs",uploadRecord.ECs);
        params.addBodyParameter("RA",uploadRecord.RA);
        params.addBodyParameter("timestamp",uploadRecord.timestamp);
        params.addBodyParameter("datatime",uploadRecord.datatime);

        params.addBodyParameter("HR",uploadRecord.getHR());
        params.addBodyParameter("AE",uploadRecord.getAE());
        params.addBodyParameter("distance",uploadRecord.getDistance());
        params.addBodyParameter("time",uploadRecord.getTime());
        params.addBodyParameter("cadence",uploadRecord.getCadence());
        params.addBodyParameter("calorie",uploadRecord.getCalorie());
        params.addBodyParameter("state",uploadRecord.getState());

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.uploadReportURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(TAG,"onSuccess==result:"+result);
                    /*{
                         {
                            "ret": "0",
                            "errDesc":"数据上传成！"
                          }
                    }*/
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int ret = jsonObject.getInt("ret");
                    String errDesc = jsonObject.getString("errDesc");
                    if (ret==0){

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"onFailure==s:"+s);
            }
        });
    }

    public void close(View view) {
        if (animation!=null){
            animation.cancel();
        }
        finish();
    }
}
