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
import com.amsu.healthy.bean.RateRecord;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.db.DbAdapter;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyBitMapUtil;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.Gson;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeartRateActivity extends BaseActivity {

    private static final String TAG = "HeartRateActivity";
    private Animation animation;
    private FileInputStream fileInputStream;
    private String ecgDatatext = "";
    private int ecgRate;
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
                try {
                    String heartData = MyUtil.getStringValueFromSP("heartData");

                    if (!heartData.equals("")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                animation.cancel();
                                Intent intent = new Intent(HeartRateActivity.this, RateAnalysisActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        return;
                    }
                    Thread.sleep(1000);  //模拟下载数据 耗时
                    //String cacheFileName = MyUtil.getStringValueFromSP("cacheFileName");
                    //String cacheFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/20170220210301.ecg";
                    String cacheFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/20170223160327.ecg";
                    if (!cacheFileName.equals("")){
                        try {
                            if (fileInputStream==null){
                                File file = new File(cacheFileName);
                                if (file.exists()){
                                    fileInputStream = new FileInputStream(cacheFileName);
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    if (fileInputStream!=null){
                        byte [] mybyte = new byte[1024];
                        int length=0;
                        while (true) {
                            length = fileInputStream.read(mybyte,0,mybyte.length);
                            //Log.i(TAG,"length:"+length);
                            if (length!=-1) {
                                String s = new String(mybyte,0,length);
                                ecgDatatext +=s;
                            }else {
                                break;
                            }
                        }
                        Log.i(TAG,"ecgDatatext:"+ecgDatatext);
                        if (!ecgDatatext.equals("")){
                            String[] allGrounpData = ecgDatatext.split(",");
                            int[] calcuData = new int[allGrounpData.length];
                            for (int i=0;i<allGrounpData.length;i++){
                                calcuData[i] = Integer.parseInt(allGrounpData[i]);
                            }
                            analysisAndUpload(calcuData);
                        }

                    }
                    //分析完成
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animation.cancel();
                        }
                    });
                    Intent intent = new Intent(HeartRateActivity.this, RateAnalysisActivity.class);
                    if (uploadRecord!=null){
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("uploadRecord",uploadRecord);
                        intent.putExtra("bundle",bundle);
                    }
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void analysisAndUpload(int[] calcuData) {
        HeartRateResult heartRateResult = DiagnosisNDK.AnalysisEcg(calcuData, calcuData.length, 150);
        Log.i(TAG,"heartRateResult:"+heartRateResult.toString());
        //转化成json并存在sp里
        Gson gson = new Gson();
        String gsonHeartRateResult = gson.toJson(heartRateResult);
        //MyUtil.putStringValueFromSP("gsonHeartRateResult",gsonHeartRateResult);

        /*DbAdapter dbAdapter = new DbAdapter(HeartRateActivity.this);
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
        IndicatorAssess indicatorAssess = HealthyIndexUtil.calculateLFHFMoodIndex((int) (heartRateResult.LF / heartRateResult.HF));
        String ES = String.valueOf(indicatorAssess.getPercent());
        IndicatorAssess indicatorAssess1 = HealthyIndexUtil.calculateSDNNPressureIndex(heartRateResult.RR_SDNN);
        String PI = String.valueOf(indicatorAssess1.getPercent());
        IndicatorAssess indicatorAssess2 = HealthyIndexUtil.calculateSDNNSportIndex(heartRateResult.RR_SDNN);
        String FI = String.valueOf(indicatorAssess2.getPercent());

        String HRVs = indicatorAssess.getSuggestion()+indicatorAssess1.getSuggestion()+indicatorAssess2.getSuggestion();

        //int[] datas = MyUtil.getHeartRateListFromSP();
        int[] datas = new int[]{65,66,54,73,71,68,77,55,56,93,65,68,64,62,61,64,67,66,40,70,65};
        int MaxHR=datas[0];
        int MinHR=datas[0];
        int sum = 0;
        for (int i=0;i<datas.length;i++){
            if (datas[i]>MaxHR){
                MaxHR =datas[i];
            }
            if (datas[i]<MinHR){
                MinHR = datas[i];
            }
            sum += datas[i];
        }
        String AHR = String.valueOf(sum/datas.length);

       /* List<List<Integer>> sList = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        for (int i=0;i<calcuData.length;i++){
            temp = new ArrayList<>();
            temp.add(i+1);
            temp.add(calcuData[i]);
            sList.add(temp);
        }*/
        String  EC = "[";
        for (int i=0;i<datas.length;i++){
            EC += "["+i+","+calcuData[i]+"],";
        }
        EC += "]";

        String ECr = "1";
        if (heartRateResult.RR_Kuanbo>0){  //漏博
            ECr = "3";
        }
        else if (heartRateResult.RR_Apb+heartRateResult.RR_Pvc>0){ //早搏
            ECr="4";
        }
        String RA = "90";  //心率恢复能力
        String timestamp = String.valueOf(System.currentTimeMillis());
        String datatime = MyUtil.getSpecialFormatTime("yyyy/MM/dd H:m:s", new Date());

        uploadRecord = new UploadRecord(FI,ES,PI,"10","xxxxx",HRVs,AHR,String.valueOf(MaxHR),String.valueOf(MinHR),"xxxx","xxxx",EC,ECr,"xxxx",RA,timestamp,datatime);
        Log.i(TAG,"uploadRecord:"+uploadRecord);
        uploadData(uploadRecord);
    }

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
        finish();

    }
}
