package com.amsu.healthy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.amap.api.maps.model.LatLng;
import com.amsu.healthy.R;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.EcgFilterUtil;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.OffLineDbAdapter;
import com.amsu.healthy.utils.map.DbAdapter;
import com.amsu.healthy.utils.map.PathRecord;
import com.amsu.healthy.utils.map.Util;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HeartRateActivity extends BaseActivity {

    private static final String TAG = "HeartRateActivity";
    private Animation animation;
    private FileInputStream fileInputStream;
    private UploadRecord uploadRecord = null;
    private long mCalKcalCurrentTimeMillis = 0;

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
        final Intent intent = getIntent();

        //final ArrayList<Integer> heartDataList_static = new ArrayList<>();
        final int sportState = intent.getIntExtra(Constant.sportState, -1);  // 运动类型，需要传到下个界面
        final ArrayList<Integer> integerArrayListExtra = intent.getIntegerArrayListExtra(Constant.heartDataList_static);

        final int hrr = intent.getIntExtra(Constant.hrr, 0);  // 运动类型，需要传到下个界面
        final long sportCreateRecordID = intent.getLongExtra(Constant.sportCreateRecordID, -1);  //运动记录id
        final long ecgFiletimeMillis = intent.getLongExtra(Constant.ecgFiletimeMillis, -1);

        Log.i(TAG,"sportCreateRecordID:"+sportCreateRecordID);
        Log.i(TAG,"ecgFiletimeMillis:"+ecgFiletimeMillis);

        /*Bundle bundle = intent.getParcelableExtra("bundle");
        if (bundle!=null){
            HistoryRecord historyRecord = bundle.getParcelable("historyRecord");
            if (historyRecord!=null){
                Log.i(TAG,"historyRecord:"+historyRecord.toString());
                if (historyRecord.getAnalysisState()==HistoryRecord.analysisState_abort){
                    //异常中断数据

                }
            }
        }*/

        //heartDataList_static.add(90);

        /*if ((heartDataList_static==null || heartDataList_static.size()==0) && sportCreateRecordID==-1){
            //没有数据。直接跳转
            Intent intentToRateAnalysis = new Intent(HeartRateActivity.this, RateAnalysisActivity.class);
            intentToRateAnalysis.putExtra(Constant.sportState,sportState);
            startActivity(intentToRateAnalysis);
            finish();
            return;
        }*/

        //分析过程有可能耗时，在子线程中进行


            //String cacheFileName = MyUtil.getStringValueFromSP("cacheFileName");
        final String ecgLocalFileName = intent.getStringExtra(Constant.ecgLocalFileName);
        //final String ecgLocalFileName = "/storage/emulated/0/abluedata/20170710195415.ecg";


        //final ArrayList<Integer> heartDataList_static = intent.getIntegerArrayListExtra(Constant.heartDataList_static);//静态心电心率，不为空则表示有静态心电数据
        //final String ecgLocalFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/20170516101758.ecg";
            Log.i(TAG,"ecgLocalFileName:"+ecgLocalFileName);
        Log.i(TAG,"integerArrayListExtra: "+integerArrayListExtra);
            if (!MyUtil.isEmpty(ecgLocalFileName)) {
                //心电数据 or 心电+运动
                final File file = new File(ecgLocalFileName);
                if (file.exists()){
                    //String fileBase64 = "";
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            final ArrayList<Integer> heartDataList_static;


                            if (integerArrayListExtra !=null){
                                heartDataList_static = integerArrayListExtra;//静态心电心率，不为空则表示有静态心电数据
                            }
                            else {
                                heartDataList_static = new ArrayList<>();
                            }
                            final String fileBase64 = MyUtil.fileToBase64(file);
                            Log.i(TAG,"fileBase64:"+fileBase64);
                            final List<Integer> ecgDataList = readIntArrayDataFromFile(file);
                            Log.i(TAG,"ecgDataList.size()"+ecgDataList.size());

                            List<Integer> stridefreData = null;
                            final List<String> mKcalData = new ArrayList<>();
                            int mAllKcal = 0;
                            if (heartDataList_static.size() == 0){
                                //没有心率数组，则为离线分析
                                Log.i(TAG,"离线分析:");

                                //计算心率数组
                                int[] calcuEcgRate = HealthyDataActivity.calcuEcgRate;
                                int heartCount = ecgDataList.size() / calcuEcgRate.length;


                                for (int j=0;j<heartCount;j++){
                                    for (int i=0;i<calcuEcgRate.length;i++){
                                        calcuEcgRate[i] = ecgDataList.get(j*calcuEcgRate.length+i);
                                    }
                                    int mCurrentHeartRate = DiagnosisNDK.ecgHeart(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
                                    //Log.i(TAG,"mCurrentHeartRate:"+ mCurrentHeartRate);
                                    heartDataList_static.add(mCurrentHeartRate);

                                    if (mCalKcalCurrentTimeMillis==0){
                                        mCalKcalCurrentTimeMillis = System.currentTimeMillis();
                                    }else {
                                        /*long l = System.currentTimeMillis() - mCalKcalCurrentTimeMillis;
                                        mCalKcalCurrentTimeMillis = System.currentTimeMillis();*/
                                        float time =  6.6f/60f;
                                        //float time = (float) (timeLong / (1000 * 60.0));
                                        int userSex = MyUtil.getUserSex();
                                        int userAge = HealthyIndexUtil.getUserAge();
                                        int userWeight = MyUtil.getUserWeight();
                                        //Log.i(TAG,"time:"+time+",userSex:"+userSex+",userAge:"+userAge+",userWeight"+userWeight);
                                        float getkcal = DiagnosisNDK.getkcal(userSex, mCurrentHeartRate, userAge, userWeight, time);
                                        //Log.i(TAG,"getkcal:"+getkcal);
                                        if (getkcal<0){
                                            getkcal = 0;
                                        }
                                        mAllKcal += getkcal;
                                        mKcalData.add(getkcal+"");
                                    }
                                }

                                //Log.i(TAG,"calcuEcgRate:"+calcuEcgRate.length);

                                //计算加速度数据
                                String accLocalFileName = intent.getStringExtra(Constant.accLocalFileName);
                                //Log.i(TAG,"accLocalFileName:"+ accLocalFileName);
                                if (!MyUtil.isEmpty(accLocalFileName)){
                                    File file = new File(accLocalFileName);
                                    if (file.exists()){
                                        List<Integer> accDataList = readIntArrayDataFromFile(file);
                                        //Log.i(TAG,"accDataList.size():"+ accDataList.size());
                                        stridefreData = calcuAccStridefreData(accDataList);
                                        //Log.i(TAG,"stridefreData.size():"+stridefreData.size());
                                        //Log.i(TAG,"stridefreData:"+stridefreData);

                                        /*for (int i:stridefreData){
                                            Log.i(TAG,"stridefreData:"+i);
                                        }*/

                                    }
                                }

                            }

                    /*for (int i=0;i<ecgDataList.size();i++){
                        //Log.i(TAG,"ecgDataList:"+i+"  "+ecgDataList.get(i));
                    }*/
                            Log.i(TAG,"ecgDataList.size()"+ecgDataList.size());
                            final List<Integer> finalStridefreData = stridefreData;
                            generateUploadData(ecgDataList, fileBase64, heartDataList_static, sportState, sportCreateRecordID, hrr,
                                            ecgFiletimeMillis,intent,ecgLocalFileName, finalStridefreData,mKcalData);

                        }
                    }.start();

                }
            }
            else if (sportCreateRecordID!=-1){
                //只有运动数据
                generateUploadData(null, null, null, sportState, sportCreateRecordID, hrr,ecgFiletimeMillis,intent,null,null,null);
            }
            else {
                finish();
            }
    }

    private List<Integer> calcuAccStridefreData(List<Integer> accDataList) {
        List<Integer> stridefreData = new ArrayList<>();

        //计算步频数组
        int[] calcuEcgRate = new int[StartRunActivity.accDataLength];
        int stridefreCount = accDataList.size() / calcuEcgRate.length;
        for (int j=0;j<stridefreCount;j++){
            for (int i=0;i<calcuEcgRate.length;i++){
                calcuEcgRate[i] = accDataList.get(j*stridefreCount+i);
            }
            //int mCurrentHeartRate = ECGUtil.countEcgRate(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
            //Log.i(TAG,"mCurrentHeartRate:"+ mCurrentHeartRate);
            //stridefreData.add(mCurrentHeartRate);

            //计算
            byte[] bytes = new byte[calcuEcgRate.length];
            for (int i=0;i<calcuEcgRate.length;i++){
                bytes[i] = (byte)(int)calcuEcgRate[i];
            }
            int[] results = new int[2];

            DiagnosisNDK.AnalysisPedo(bytes,calcuEcgRate.length,results);
            //Log.i(TAG,"results: "+results[0]+"  "+results[1]);
            final int stridefre = (int) (results[1] * 5.21); //每分钟的步数
            stridefreData.add(stridefre);
        }

        return stridefreData;
    }

    private void uploadDataAndJumpToShowPage(UploadRecord uploadRecord,int sportState,String ecgLocalFileName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animation.cancel();
            }
        });

        if (uploadRecord!=null){
            UploadRecord uploadRecordCopy = null;
            try {
                uploadRecordCopy = (UploadRecord) uploadRecord.clone();   //后面会有对对象的重新改变值，所以获取该对象的克隆，以后对此对象的改变将不会影响克隆对象
                uploadRecordCopy.localEcgFileName = ecgLocalFileName;
                uploadRecordDataToServer(uploadRecordCopy,HeartRateActivity.this);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        Intent intentToRateAnalysis = new Intent(HeartRateActivity.this, RateAnalysisActivity.class);
        if (uploadRecord!=null){
            Bundle bundle = new Bundle();
            uploadRecord.EC = "";
            bundle.putParcelable("uploadRecord",uploadRecord);
            intentToRateAnalysis.putExtra("bundle",bundle);
            Log.i(TAG,"uploadRecord: putParcelable  "+uploadRecord);
        }
        intentToRateAnalysis.putExtra(Constant.sportState,sportState);
        intentToRateAnalysis.putExtra(Constant.ecgLocalFileName,ecgLocalFileName);
        startActivity(intentToRateAnalysis);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initData();
    }

    private void initData1() {

        String heartData = MyUtil.getStringValueFromSP("heartData");
        Log.i(TAG,"heartData:"+heartData);

                /*try {
                    Thread.sleep(100);  //模拟下载数据 耗时
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
        if (heartData.equals("") && StartRunActivity.createrecord==-1){ //静态心电数据和运动数据为空时，不进行数据上传
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

    //生成上传数据，分为心电数据和运动数据
    private void generateUploadData(final List<Integer> ecgDataList, final String fileBase64, final ArrayList<Integer> heartDataList, final int sportState, long sportCreateRecordID, final int hrr,
                                            long ecgFiletimeMillis, Intent intent, final String ecgLocalFileName, List<Integer> stridefreData, List<String> calData) {
        final UploadRecord uploadRecord = new UploadRecord();

        String timestamp;
        String datatime ;
        if (ecgFiletimeMillis>0){
            timestamp = (ecgFiletimeMillis/1000)+"";
            datatime = MyUtil.getSpecialFormatTime("yyyy/MM/dd HH:mm:ss", new Date(ecgFiletimeMillis));
        }
        else {
            timestamp = (System.currentTimeMillis()/1000)+"";
            datatime = MyUtil.getSpecialFormatTime("yyyy/MM/dd HH:mm:ss", new Date());
        }

        uploadRecord.setTimestamp(timestamp);
        uploadRecord.setDatatime(datatime);
        uploadRecord.setState(sportState+"");

        final Gson gson = new Gson();
        //设置心电数据
        if (ecgDataList!=null && ecgDataList.size()>0){
            Log.i(TAG,"ecgDataList.size(): ====================="+ecgDataList.size());
            final int[] calcuData = new int[ecgDataList.size()];
            for (int i=0;i<ecgDataList.size();i++ ){
                calcuData[i] = ecgDataList.get(i);
            }
            String ES = Constant.uploadRecordDefaultString;
            String PI = Constant.uploadRecordDefaultString;
            String FI = Constant.uploadRecordDefaultString;
            int zaobo = 0;
            int loubo = 0;

            String ECr = "1";
            String HRVs = "数据不足，无法得出分析结果";
            String ECs = Constant.uploadRecordDefaultString;

            Log.i(TAG,"DiagnosisNDK.AnalysisEcg: =====================");
            HeartRateResult heartRateResult = DiagnosisNDK.AnalysisEcg(calcuData, calcuData.length, Constant.oneSecondFrame);
            Log.i(TAG,"heartRateResult:"+heartRateResult.toString());

            int allTimeAtSecond = (int) (ecgDataList.size()/(Constant.oneSecondFrame*1f));
            Log.i(TAG,"allTimeAtSecond:"+allTimeAtSecond);
            uploadRecord.setTime(allTimeAtSecond+"");

            //转化成json并存在sp里


    /*IndicatorAssess ESIndicatorAssess = HealthyIndexUtil.calculateLFHFMoodIndex((int) (heartRateResult.LF / heartRateResult.HF));
    String ES = String.valueOf(ESIndicatorAssess.getPercent());
    IndicatorAssess PIIndicatorAssess = HealthyIndexUtil.calculateSDNNPressureIndex(heartRateResult.RR_SDNN);
    String PI = String.valueOf(PIIndicatorAssess.getPercent());
    IndicatorAssess FIIndicatorAssess = HealthyIndexUtil.calculateSDNNSportIndex(heartRateResult.RR_SDNN);
    String FI = String.valueOf(FIIndicatorAssess.getPercent());*/

            //String HRVs = ESIndicatorAssess.getSuggestion()+PIIndicatorAssess.getSuggestion()+FIIndicatorAssess.getSuggestion();

            if (heartRateResult.HF>0){
                ES = (int)(heartRateResult.LF / heartRateResult.HF)+"";
            }
            PI = heartRateResult.RR_SDNN+"";
            FI = heartRateResult.RR_SDNN+"";

            zaobo = heartRateResult.RR_Apb + heartRateResult.RR_Pvc;
            loubo = heartRateResult.RR_Boleakage;


            if (zaobo>0){
                ECs = "本次测量早搏"+zaobo+"次。早搏是指异位起搏点发出的过早冲动引起的心脏搏动。早搏除常见于各种器质性心脏病人外，健康人也会偶有发生，请您不必紧张，保持心情放松；当有明显症状或感觉身体不适时请及时就医检查。";
            }
            else if (loubo>0){
                ECs = "本次测量漏搏"+loubo+"次。漏搏与迷走神经张力增高有关，可见于正常人或运动员，也可见于急性心肌梗死、冠状动脉痉挛、心肌炎等情况。偶尔出现属正常现象，请不必过于紧张；当有明显症状或感觉身体不适时请及时就医检查。";
            }
            else {
                ECs = "正常心电图";
            }

            if (heartRateResult.RR_Kuanbo>0){  //漏博
                ECr = "3";
            }
            else if (heartRateResult.RR_Apb+heartRateResult.RR_Pvc>0){ //早搏
                ECr="4";
            }

            if (heartRateResult.HF>0){
                HRVs = HealthyIndexUtil.getHRVSuggetstion(heartRateResult.RR_SDNN, (int) (heartRateResult.LF / heartRateResult.HF));
                Log.i(TAG,"HRVs:"+HRVs);
            }

            String HR = gson.toJson(heartDataList);

            int MaxHR = 0;
            int MinHR  =0;
            int averHeart  =0;
            String AHR = Constant.uploadRecordDefaultString;

            if(heartDataList.size()>0){
                MaxHR= heartDataList.get(0);
                MinHR= heartDataList.get(0);

                int sum = 0;
                for (int heart: heartDataList){
                    if (heart>MaxHR){
                        MaxHR =heart;
                    }
                    if (heart<MinHR){
                        MinHR = heart;
                    }
                    sum += heart;
                }

                averHeart = sum / heartDataList.size();

                AHR = String.valueOf(averHeart);
                if (averHeart>30  && averHeart<150){
                    MyUtil.putIntValueFromSP(Constant.restingHR,averHeart);
                }
            }

            String  EC = Constant.uploadRecordDefaultString;
            if (!MyUtil.isEmpty(fileBase64)){
                EC = fileBase64;
            }

            String HRs = "数据不足，无法获得结果";  //心率健康建议
            if (averHeart>0){
                String heartRateSuggetstion = HealthyIndexUtil.getHeartRateSuggetstion(sportState, averHeart);
                if (!MyUtil.isEmpty(heartRateSuggetstion)){
                    HRs = heartRateSuggetstion;
                }
            }

            String RA = hrr+"";  //心率恢复能力

            //uploadRecord = new UploadRecord(FI,ES,PI,"10","xx",HRVs,AHR,String.valueOf(MaxHR),String.valueOf(MinHR),"xxxx",HRs,EC,ECr,"xxxx",RA);
            uploadRecord.setFI(FI);
            uploadRecord.setES(ES);
            uploadRecord.setPI(PI);
            uploadRecord.setHRVs(HRVs);
            uploadRecord.setAHR(AHR);
            uploadRecord.setMaxHR(String.valueOf(MaxHR));
            uploadRecord.setMinHR(String.valueOf(MinHR));
            uploadRecord.setHRs(HRs);
            uploadRecord.setEC(EC);
            uploadRecord.setECr(ECr);
            uploadRecord.setRA(RA);
            uploadRecord.setHR(HR);
            uploadRecord.setECs(ECs);
            uploadRecord.setZaobo(zaobo+"");
            uploadRecord.setLoubo(loubo+"");
            uploadRecord.setCC((220-HealthyIndexUtil.getUserAge())+"");
        }

        //设置跑步数据
        if (sportCreateRecordID!=-1){
            String AE = Constant.uploadRecordDefaultString;  //有氧无氧
            String distance = Constant.uploadRecordDefaultString;
            String time = Constant.uploadRecordDefaultString;
            String cadence = Constant.uploadRecordDefaultString;//步频
            String calorie = Constant.uploadRecordDefaultString;  //卡路里
            String latitude_longitude = Constant.uploadRecordDefaultString;  //经纬度

            final ArrayList<String> mKcalData = intent.getStringArrayListExtra(Constant.mKcalData);//
            final ArrayList<Integer> mStridefreData = intent.getIntegerArrayListExtra(Constant.mStridefreData);//
            final ArrayList<Integer> mSpeedStringListData = intent.getIntegerArrayListExtra(Constant.mSpeedStringListData);//

            if (mKcalData!=null && mKcalData.size()>0){
                calorie = gson.toJson(mKcalData);
            }
            if (mStridefreData!=null && mStridefreData.size()>0){
                cadence = gson.toJson(mStridefreData);
            }
            if (mSpeedStringListData!=null && mSpeedStringListData.size()>0){
                AE = gson.toJson(mSpeedStringListData);
            }

            DbAdapter dbAdapter = new DbAdapter(this);
            dbAdapter.open();
            PathRecord pathRecord = dbAdapter.queryRecordById((int) sportCreateRecordID);
            dbAdapter.close();
            if (pathRecord!=null){
                Log.i(TAG,"pathRecord:"+pathRecord.toString());

                if (!MyUtil.isEmpty(pathRecord.getDistance())){
                    distance = pathRecord.getDistance();
                }
                time = Long.parseLong(pathRecord.getDuration())/1000+"";
                latitude_longitude = getLatitude_longitudeString(pathRecord);

                uploadRecord.setAE(AE);
                uploadRecord.setDistance(distance);
                uploadRecord.setTime(time);
                uploadRecord.setCadence(cadence);
                uploadRecord.setCalorie(calorie);
                uploadRecord.setLatitude_longitude(latitude_longitude);
            }
        }
        if (stridefreData!=null && stridefreData.size()>0){
            String cadence = gson.toJson(stridefreData);
            uploadRecord.setCadence(cadence);
        }
        if (calData!=null && calData.size()>0){
            String cadence = gson.toJson(calData);
            uploadRecord.setCalorie(cadence);
        }

        Log.i(TAG,"uploadRecord:"+uploadRecord);
        uploadDataAndJumpToShowPage(uploadRecord,sportState, ecgLocalFileName);


    }

    private String getLatitude_longitudeString(PathRecord pathRecord) {
        List<LatLng> latLngList = Util.parseLatLngList(pathRecord.getPathline());
        List<List<Double>> listList = new ArrayList<>();
        for (LatLng latLng:latLngList){
            List<Double> doubleList = new ArrayList<>();
            doubleList.add(latLng.latitude);
            doubleList.add(latLng.longitude);
            listList.add(doubleList);
        }
        Gson gson = new Gson();
        return gson.toJson(listList);
    }

    //从文件中读取心电数据
    private List<Integer> readIntArrayDataFromFile(File file) {
        List<Integer> calcuData = new ArrayList<>();  //心电数据
        try {
            fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream); //读取二进制文件

            byte[] bytes = new byte[1024*1024];
            Log.i(TAG,"dataInputStream.available():"+dataInputStream.available());
            Log.i(TAG,"new Date(System.currentTimeMillis()):"+new Date(System.currentTimeMillis()));

            while(dataInputStream.available() >0){
                int read = dataInputStream.read(bytes);
                for (int i = 0; i < read/2-1; i++) {
                    bytes[0] = bytes[i*2];
                    bytes[1] = bytes[i*2+1];
                    //滤波处理
                    int temp = EcgFilterUtil.miniEcgFilterLp((int) MyUtil.getShortByTwoBytes(bytes[0],bytes[1]), 0);
                    temp = EcgFilterUtil.miniEcgFilterHp(temp, 0);
                    calcuData.add(temp);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return calcuData;
    }

    public void close(View view) {
        if (animation!=null){
            animation.cancel();
        }
        finish();
    }

    //上传分析结果
    public static void uploadRecordDataToServer(final UploadRecord uploadRecord, final Context context) {
        Log.i(TAG,"uploadRecord:"+uploadRecord);
        String ahr = uploadRecord.AHR;
        Log.i(TAG,"AHR:"+ahr);
        String eccString = uploadRecord.EC;

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
        //params.addBodyParameter("AHR","90");
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
        //params.addBodyParameter("HR","[]");
        params.addBodyParameter("AE",uploadRecord.getAE());
        params.addBodyParameter("distance",uploadRecord.getDistance());
        params.addBodyParameter("time",uploadRecord.getTime());
        params.addBodyParameter("cadence",uploadRecord.getCadence());
        params.addBodyParameter("calorie",uploadRecord.getCalorie());
        params.addBodyParameter("state",uploadRecord.getState());
        params.addBodyParameter("zaobo",uploadRecord.getZaobo());
        params.addBodyParameter("loubo",uploadRecord.getLoubo());
        params.addBodyParameter("latitude_longitude",uploadRecord.getLatitude_longitude());

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.uploadReportURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(TAG,"onSuccess==result:"+result);

                /*List<UploadRecord> uploadRecords = offLineDbAdapter.queryRecordAll();

                Log.i(TAG,"uploadRecords:"+uploadRecords);*/

                    /*{
                         {
                            "ret": "0",
                            "errDesc":"数据上传成！"
                          }
                    }*/

                OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(context);
                offLineDbAdapter.open();
                uploadRecord.datatime = uploadRecord.datatime.replace("/", "-");  //将本地数据库时间改成和服务器一致，下次查看数据时，先从根据时间从本地查询

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int ret = jsonObject.getInt("ret");
                    String errDesc = jsonObject.getString("errDesc");
                    if (ret==0){
                        uploadRecord.setUploadState("1");  //上传成功后，将状态改为已上传

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                offLineDbAdapter.createOrUpdateUploadReportObject(uploadRecord);
                offLineDbAdapter.close();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"onFailure==s:"+s);

                OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(context);
                offLineDbAdapter.open();

                uploadRecord.setUploadState("0");
                long orUpdateUploadReportObject = offLineDbAdapter.createOrUpdateUploadReportObject(uploadRecord);
                Log.i(TAG,"orUpdateUploadReportObject:"+orUpdateUploadReportObject);

                List<UploadRecord> uploadRecords = offLineDbAdapter.queryRecordAll();
                Log.i(TAG,"uploadRecords:"+uploadRecords);

                List<UploadRecord> uploadRecordsState = offLineDbAdapter.queryRecordByUploadState("0");
                Log.i(TAG,"uploadRecordsState:"+uploadRecordsState);
            }
        });
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
        String timestamp = String.valueOf(System.currentTimeMillis());
        //String timestamp = String.valueOf(HealthyDataActivity.ecgFiletimeMillis);
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

        //uploadRecordDataToServer(uploadRecord);

        finish();
    }
}
