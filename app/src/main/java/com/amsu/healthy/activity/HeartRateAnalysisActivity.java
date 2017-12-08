package com.amsu.healthy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.amsu.bleinteraction.utils.EcgAccDataUtil;
import com.amsu.bleinteraction.utils.EcgFilterUtil_1;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.marathon.SportRecordDetailsActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.ParcelableDoubleList;
import com.amsu.healthy.bean.ScoreInfo;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.DateFormatUtils;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.OffLineDbAdapter;
import com.amsu.healthy.utils.UStringUtil;
import com.amsu.healthy.utils.WebSocketProxy;
import com.amsu.healthy.utils.map.DbAdapter;
import com.amsu.healthy.utils.map.PathRecord;
import com.amsu.healthy.utils.map.Util;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.test.objects.HeartRate;
import com.test.objects.HeartRateResult;
import com.test.utils.DiagnosisNDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HeartRateAnalysisActivity extends BaseActivity {

    private static final String TAG = "HeartRateAnalysisActivity";
    private Animation animation;
    private FileInputStream fileInputStream;

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
        final long startTimeMillis = intent.getLongExtra(Constant.startTimeMillis, -1);  //此次运动的开始时间

        Log.i(TAG,"sportCreateRecordID:"+sportCreateRecordID);
        Log.i(TAG,"startTimeMillis:"+startTimeMillis);

        //final String ecgLocalFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/10-f3fbbf03-6925-49cd-881a-c2dad9e9b791";
        //final String ecgLocalFileName = "/storage/emulated/0/abluedata/20170711214450.ecg";
        //final String ecgLocalFileName =  Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/cloth/20170828165147.ecg";
        //final ArrayList<Integer> heartDataList_static = intent.getIntegerArrayListExtra(Constant.heartDataList_static);//静态心电心率，不为空则表示有静态心电数据
        //final String ecgLocalFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/20170516101758.ecg";

        //分析过程有可能耗时，在子线程中进行
        final String ecgLocalFileName = intent.getStringExtra(Constant.ecgLocalFileName); ///storage/emulated/0
        //final String ecgLocalFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/cloth/20171123144841.ecg"; ///storage/emulated/0
        //final String ecgLocalFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/20171109152200.ecg"; ///storage/emulated/0

        Log.i(TAG,"ecgLocalFileName:"+ecgLocalFileName);
        Log.i(TAG,"integerArrayListExtra: "+integerArrayListExtra);

        if (!MyUtil.isEmpty(ecgLocalFileName)) {
            //心电数据 or 心电+运动
            final File file = new File(ecgLocalFileName);
            if (file.exists()){
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
                            //int[] calcuEcgRate = new int[EcgAccDataUtil.calGroupCalcuLength *EcgAccDataUtil.ecgOneGroupLength];
                            int[] calcuEcgRate = new int[EcgAccDataUtil.calGroupCalcuLength * EcgAccDataUtil.ecgOneGroupLength];
                            int[] calcuEcgRateraw = new int[EcgAccDataUtil.calGroupCalcuLength * EcgAccDataUtil.ecgOneGroupLength];
                            int heartCount = ecgDataList.size() / calcuEcgRate.length;

                            //EcgFilterUtil_1 ecgFilterUtil_1 = CommunicateToBleService.ecgFilterUtil_1;
                            EcgFilterUtil_1 ecgFilterUtil_1 = EcgFilterUtil_1.getInstance();

                            Log.i(TAG,"ecgDataList"+ ecgDataList);

                            for (int j=0;j<heartCount;j++){
                                for (int i=0;i<calcuEcgRate.length;i++){
                                    calcuEcgRate[i] = ecgDataList.get(j*calcuEcgRate.length+i);
                                    calcuEcgRateraw[i] = ecgDataList.get(j*calcuEcgRate.length+i);
                                    calcuEcgRate[i] = ecgFilterUtil_1.miniEcgFilterLp(ecgFilterUtil_1.miniEcgFilterHp (ecgFilterUtil_1.NotchPowerLine(calcuEcgRate[i], 1)));  //滤波
                                }

                                Log.i(TAG,"计算数据calcuEcgRate"+ Arrays.toString(calcuEcgRate));
                                //int mCurrentHeartRate = DiagnosisNDK.ecgHeart(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);

                                HeartRate heartRate = DiagnosisNDK.ecgHeart(calcuEcgRateraw, calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
                                //int mCurrentHeartRate = DiagnosisNDK.ecgHeart(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
                                Log.i(TAG,"heartRate:"+ heartRate);

                                int mCurrentHeartRate = heartRate.rate;
                                Log.i(TAG,"mCurrentHeartRate:"+ mCurrentHeartRate);
                                heartDataList_static.add(mCurrentHeartRate);

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

                            //Log.i(TAG,"calcuEcgRateAfterFilter:"+calcuEcgRateAfterFilter.length);

                            //计算加速度数据
                            String accLocalFileName = intent.getStringExtra(Constant.accLocalFileName);
                            //String accLocalFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/20171109152200.acc"; ///storage/emulated/0
                            //Log.i(TAG,"accLocalFileName:"+ accLocalFileName);
                            if (!MyUtil.isEmpty(accLocalFileName)){
                                File file = new File(accLocalFileName);
                                if (file.exists()){
                                    List<Integer> accDataList = readIntArrayDataFromFile(file);
                                    Log.i(TAG,"accDataList:"+accDataList.toString());
                                    Log.i(TAG,"accDataList.size():"+ accDataList.size());
                                    stridefreData = calcuAccStridefreData(accDataList);
                                    Log.i(TAG,"stridefreData.size():"+stridefreData.size());
                                    Log.i(TAG,"stridefreData:"+stridefreData);

                                    /*for (int i:stridefreData){
                                        Log.i(TAG,"stridefreData:"+i);
                                    }*/

                                }
                            }
                        }

                        Log.i(TAG,"ecgDataList.size()"+ecgDataList.size());
                        final List<Integer> finalStridefreData = stridefreData;
                        generateUploadData(ecgDataList, fileBase64, heartDataList_static, sportState, sportCreateRecordID, hrr,
                                        startTimeMillis,intent,ecgLocalFileName, finalStridefreData,mKcalData);

                    }
                }.start();

            }
            else {
                MyUtil.showToask(this,"分析出现错误，本地文件不存在或者没有读写本地sd卡权限");
                finish();
            }
        }
        else if (sportCreateRecordID!=-1){
            //只有运动数据
            generateUploadData(null, null, null, sportState, sportCreateRecordID, hrr,startTimeMillis,intent,null,null,null);
        }
        else {
            MyUtil.showToask(this,"没有检测到数据，请检查网路是否打开或衣服是否连接");
            finish();
        }
    }

    private List<Integer> calcuAccStridefreData(List<Integer> accDataList) {
        List<Integer> stridefreData = new ArrayList<>();

        //计算步频数组
        int[] calcuEcgRate = new int[EcgAccDataUtil.accDataLength];
        int stridefreCount = accDataList.size() / calcuEcgRate.length;
        for (int j=0;j<stridefreCount;j++){
            for (int i=0;i<calcuEcgRate.length;i++){
                calcuEcgRate[i] = accDataList.get(j*stridefreCount+i);
            }
            //int mCurrentHeartRate = EcgAccDataUtil.countEcgRate(calcuEcgRateAfterFilter, calcuEcgRateAfterFilter.length, Constant.oneSecondFrame);
            //Log.i(TAG,"mCurrentHeartRate:"+ mCurrentHeartRate);
            //stridefreData.add(mCurrentHeartRate);

            //计算
            byte[] bytes = new byte[calcuEcgRate.length];
            for (int i=0;i<calcuEcgRate.length;i++){
                bytes[i] = (byte)(int)calcuEcgRate[i];
            }

            int stridefreByAccData = MyUtil.getStridefreByAccData(bytes);
            Log.i(TAG,"stridefreByAccData: "+stridefreByAccData);
            stridefreData.add(stridefreByAccData);

            /*int[] results = new int[2];

            DiagnosisNDK.AnalysisPedo(bytes,calcuEcgRateAfterFilter.length,results);
            //Log.i(TAG,"results: "+results[0]+"  "+results[1]);
            final int stridefre = (int) (results[1] * 5.21); //每分钟的步数
            Log.i(TAG,"results: "+results[0]+"  "+results[1]+",stridefre:"+stridefre);*/

        }

        return stridefreData;
    }

    private void uploadDataAndJumpToShowPage(UploadRecord uploadRecord,long sportCreateRecordID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animation.cancel();
            }
        });

        /*//测试
        List<ParcelableDoubleList> parcelableDoubleLists = new ArrayList<>();
        ParcelableDoubleList doubles = new ParcelableDoubleList();
        doubles.add(22.596084);
        doubles.add(113.990938);
        for (int i=0;i<75*60*10;i++){
            parcelableDoubleLists.add(doubles);
        }
        uploadRecord.latitudeLongitude = parcelableDoubleLists;*/

        if (uploadRecord!=null){
            UploadRecord uploadRecordCopy;
            try {
                uploadRecordCopy = (UploadRecord) uploadRecord.clone();   //后面会有对对象的重新改变值，所以获取该对象的克隆，以后对此对象的改变将不会影响克隆对象
                uploadRecordDataToServer(uploadRecordCopy,HeartRateAnalysisActivity.this,false);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        final boolean isMarathonSportType = MyUtil.getBooleanValueFromSP(Constant.isMarathonSportType);
        if (isMarathonSportType) {
            startActivity(SportRecordDetailsActivity.createIntent(this));
            finish();
        } else {
            Intent intentToRateAnalysis = new Intent(HeartRateAnalysisActivity.this, HeartRateResultShowActivity.class);
            if (uploadRecord != null) {
                Bundle bundle = new Bundle();
                uploadRecord.ec = "";
                if (uploadRecord.latitudeLongitude.size() > 0) {
                    uploadRecord.latitudeLongitude = new ArrayList<>();
                    uploadRecord.sportCreateRecordID = sportCreateRecordID;
                }
                bundle.putParcelable("uploadRecord", uploadRecord);
                intentToRateAnalysis.putExtra("bundle", bundle);
                Log.i(TAG, "uploadRecord: putParcelable  " + uploadRecord);
            }
            //intentToRateAnalysis.putExtra(Constant.sportState,sportState);
            startActivity(intentToRateAnalysis);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //计算最大、最小、平均心率
    private int[]  setHeartMaxMinAverage(ArrayList<Integer> heartDataList){
        int MaxHR ;
        int MinHR ;
        int AHR = 0;
        MaxHR= heartDataList.get(0);
        MinHR= heartDataList.get(0);
        int sum = 0;
        int graterZeroCount = 0;
        for (int heart: heartDataList){
            if (heart>0){
                if (heart>MaxHR){
                    MaxHR =heart;
                }
                if (heart<MinHR){
                    MinHR = heart;
                }
                sum += heart;
                graterZeroCount++;
            }
        }
        if (graterZeroCount>0){
            AHR = sum / graterZeroCount;
        }
        if (AHR>40  && AHR<120){
            MyUtil.putStringValueFromSP(Constant.restingHR,AHR+"");
        }
        return new int[]{MaxHR,MinHR,AHR};
    }

    //生成上传数据，分为心电数据和运动数据
    private void generateUploadData(final List<Integer> ecgDataList, final String fileBase64, final ArrayList<Integer> heartDataList,
                                    final int sportState, long sportCreateRecordID, final int hrr, long startTimeMillis, Intent intent,
                                    final String ecgLocalFileName, List<Integer> stridefreData, List<String> calData) {
        final UploadRecord uploadRecord = new UploadRecord();

        long timestamp;
        String datatime ;
        if (startTimeMillis>0){
            timestamp = startTimeMillis/1000;
            datatime = MyUtil.getSpecialFormatTime("yyyy/MM/dd HH:mm:ss", new Date(startTimeMillis));
        }
        else {
            timestamp = System.currentTimeMillis()/1000;
            datatime = MyUtil.getSpecialFormatTime("yyyy/MM/dd HH:mm:ss", new Date());
        }
        uploadRecord.timestamp=timestamp;
        uploadRecord.datatime = datatime;
        uploadRecord.state = sportState;

        if (calData!=null && calData.size()>0){
            uploadRecord.calorie = calData;
        }

        //设置心电数据

        if (ecgDataList!=null && heartDataList.size()>0){
            int MaxHR ;
            int MinHR ;
            int AHR;
            int[] ints = setHeartMaxMinAverage(heartDataList);
            MaxHR = ints[0];
            MinHR = ints[1];
            AHR = ints[2];

            String  EC = Constant.uploadRecordDefaultString;
            if (!MyUtil.isEmpty(fileBase64)){
                EC = fileBase64;
            }

            String HRs = getResources().getString(R.string.HeartRate_suggetstion_nodata);  //心率健康建议
            if (AHR>0){
                String heartRateSuggetstion = HealthyIndexUtil.getHeartRateSuggetstion(sportState, AHR,this);
                if (!MyUtil.isEmpty(heartRateSuggetstion)){
                    HRs = heartRateSuggetstion;
                }
            }

            Log.i(TAG,"ecgDataList.size(): ====================="+ecgDataList.size());

            if (ecgDataList.size()/(150*60)>3){  // 心电数据是否有用（以三分钟来计）
                uploadRecord.inuse = 1;
            }
            else {
                uploadRecord.inuse = 0;
            }

            if (!MyUtil.isEmpty(ecgLocalFileName)){
                uploadRecord.localEcgFileName = ecgLocalFileName;
            }

            final int[] calcuData = new int[ecgDataList.size()];
            for (int i=0;i<ecgDataList.size();i++ ){
                calcuData[i] = ecgDataList.get(i);
            }
            double ES = 0;
            int PI ;
            int FI ;
            int zaobo ;
            int loubo ;

            int ECr =1;
            String HRVs = getResources().getString(R.string.HeartRate_suggetstion_nodata);
            String ECs = getResources().getString(R.string.HeartRate_suggetstion_nodata);

            Log.i(TAG,"DiagnosisNDK.AnalysisEcg: 总分析");
            HeartRateResult heartRateResult = DiagnosisNDK.AnalysisEcg(calcuData, calcuData.length, Constant.oneSecondFrame);
            Log.i(TAG,"总分析heartRateResult:"+heartRateResult.toString());

            int allTimeAtSecond = (int) (ecgDataList.size()/(Constant.oneSecondFrame*1f));
            Log.i(TAG,"allTimeAtSecond:"+allTimeAtSecond);
            uploadRecord.time = allTimeAtSecond;

            if (heartRateResult.HF>0){
                ES = (heartRateResult.LF / heartRateResult.HF);
            }

            PI = FI = heartRateResult.RR_SDNN;

            if (FI>0 || ES>0){
                HRVs = HealthyIndexUtil.getHRVSuggetstion(FI, ES,this);
                Log.i(TAG,"hrvs:"+HRVs);
            }

            int timeOneMinuteCount = 150*60;
            int timeAnalysisMinuteCount = timeOneMinuteCount;

            if (calcuData.length<timeOneMinuteCount*4){
                //不足4分钟，提示时间不足
            }
            else if (calcuData.length>=timeOneMinuteCount*4 && calcuData.length<timeOneMinuteCount*6){
                //4~6分钟，取前后2分钟的数据
                timeAnalysisMinuteCount = timeOneMinuteCount*2;
            }
            else if (calcuData.length>=timeOneMinuteCount*6 && calcuData.length<timeOneMinuteCount*10){
                //6~8分钟，取前后3分钟的数据
                timeAnalysisMinuteCount = timeOneMinuteCount*3;
            }
            else if (calcuData.length>=timeOneMinuteCount*10){
                //大于10分钟，取前后5分钟的数据
                timeAnalysisMinuteCount = timeOneMinuteCount*5;
            }

            if (calcuData.length>=timeOneMinuteCount*4){   //大于4分钟则进行后2次分析
                final int[] analysisStartData  = new int[timeAnalysisMinuteCount];
                final int[] analysisEndData  = new int[timeAnalysisMinuteCount];

                for (int i=0;i<timeAnalysisMinuteCount;i++){
                    analysisStartData[i] = calcuData[i];  //原始数据calcuData的前面timeAnalysisMinuteCount个数据点
                    analysisEndData[timeAnalysisMinuteCount-1-i] = calcuData[calcuData.length-1-i];//原始数据calcuData的后面timeAnalysisMinuteCount个数据点
                }

                /*for (int i=0;i<timeAnalysisMinuteCount;i++){
                    analysisEndData[timeAnalysisMinuteCount-1-i] = calcuData[calcuData.length-1-i];//原始数据calcuData的后面timeAnalysisMinuteCount个数据点
                }*/

                HeartRateResult heartRateResult1 = DiagnosisNDK.AnalysisEcg(analysisStartData, analysisStartData.length, Constant.oneSecondFrame);
                Log.i(TAG,"heartRateResult1:"+heartRateResult1.toString());

                HeartRateResult heartRateResult2 = DiagnosisNDK.AnalysisEcg(analysisEndData, analysisEndData.length, Constant.oneSecondFrame);
                Log.i(TAG,"heartRateResult1:"+heartRateResult2.toString());

                uploadRecord.sdnn1 = heartRateResult1.RR_SDNN;
                uploadRecord.sdnn2 = heartRateResult2.RR_SDNN;
                uploadRecord. lf1 = heartRateResult1.LF;
                uploadRecord. lf2 = heartRateResult2.LF;
                uploadRecord.hf1 = heartRateResult1.HF;
                uploadRecord.hf2 = heartRateResult2.HF;
                uploadRecord.lf = heartRateResult.LF;
                uploadRecord.hf = heartRateResult.HF;

               /* HeartRateResult heartRateResult3 = DiagnosisNDK.AnalysisEcg(analysisStartData, analysisStartData.length, Constant.oneSecondFrame);
                Log.i(TAG,"heartRateResult3:"+heartRateResult3.toString());

                HeartRateResult heartRateResult4 = DiagnosisNDK.AnalysisEcg(analysisEndData, analysisEndData.length, Constant.oneSecondFrame);
                Log.i(TAG,"heartRateResult4:"+heartRateResult4.toString());*/

                /*int r = HealthyIndexUtil.judgeHRVMentalFatigueData(heartRateResult.HF,heartRateResult.LF,heartRateResult1.HF,heartRateResult1.LF,heartRateResult1.RR_SDNN,
                        heartRateResult2.HF,heartRateResult2.LF,heartRateResult2.RR_SDNN);


                if (sportState==0){
                    //静态
                    HealthyIndexUtil.judgeHRVPhysicalFatigueStatic(heartRateResult.HF,heartRateResult.LF,heartRateResult1.HF,heartRateResult1.LF,heartRateResult1.RR_SDNN,
                            heartRateResult2.HF,heartRateResult2.LF,heartRateResult2.RR_SDNN);
                }
                HealthyIndexUtil.judgeHRVPhysicalFatigueStatic(heartRateResult.HF,heartRateResult.LF,heartRateResult1.HF,heartRateResult1.LF,heartRateResult1.RR_SDNN,
                        heartRateResult2.HF,heartRateResult2.LF,heartRateResult2.RR_SDNN);*/
            }



            zaobo = heartRateResult.RR_Pvc;
            loubo = heartRateResult.RR_Boleakage;

            if (zaobo>0){
                ECs = getResources().getString(R.string.premature_beat_times)+zaobo+getResources().getString(R.string.premature_beat_times_decrible);
            }
            if (loubo>0){
                ECs = getResources().getString(R.string.missed_beat_times)+loubo+getResources().getString(R.string.missed_beat_times_decrible);
            }
            else {
                if (AHR>0){
                    ECs = getResources().getString(R.string.abnormal_ecg);
                }
            }

            if (heartRateResult.RR_Boleakage>0){  //漏博
                ECr = 3;
            }
            else if (heartRateResult.RR_Apb+heartRateResult.RR_Pvc>0){ //早搏
                ECr=4;
            }

            if (heartRateResult.RR_Kuanbo>0 && (heartRateResult.RR_Apb+heartRateResult.RR_Pvc>0)){
                //异常：早搏+漏博
                ECr=2;
            }

            //uploadRecord = new UploadRecord(fi,es,pi,"10","xx",hrvs,ahr,String.valueOf(maxhr),String.valueOf(minhr),"xxxx",hrs,ec,ecr,"xxxx",ra);
            uploadRecord.fi = FI;
            uploadRecord.es = ES;
            uploadRecord.pi = PI;
            uploadRecord.hrvs = HRVs;
            uploadRecord.ahr = AHR;
            uploadRecord.maxhr = MaxHR;
            uploadRecord.minhr = MinHR;
            uploadRecord.hrs = HRs ;
            uploadRecord.ec = EC ;
            uploadRecord.ecr = ECr;
            uploadRecord.ra = hrr;
            uploadRecord.hr = heartDataList;
            uploadRecord.ecs = ECs;
            uploadRecord.zaobo = zaobo;
            uploadRecord.loubo = loubo;
            uploadRecord.cc = (220-HealthyIndexUtil.getUserAge());
        }


        //设置跑步数据
        if (sportCreateRecordID!=-1){
            float distance = 0;
            long time = 0;
            List<ParcelableDoubleList> latitude_longitude;  //经纬度

            DbAdapter dbAdapter = new DbAdapter(this);
            dbAdapter.open();
            PathRecord pathRecord = dbAdapter.queryRecordById((int) sportCreateRecordID);
            dbAdapter.close();
            if (pathRecord!=null){
                Log.i(TAG,"pathRecord:"+pathRecord.toString());

                if (!MyUtil.isEmpty(pathRecord.getDistance())){
                    distance = Float.parseFloat(pathRecord.getDistance());
                }
                time = Long.parseLong(pathRecord.getDuration())/1000;
                latitude_longitude = Util.getLatitude_longitudeString(pathRecord);

                uploadRecord.distance = distance;
                uploadRecord.time =time;
                uploadRecord.latitudeLongitude = latitude_longitude;
            }
        }

        ArrayList<String> mKcalData = intent.getStringArrayListExtra(Constant.mKcalData);//
        ArrayList<Integer> mStridefreData = intent.getIntegerArrayListExtra(Constant.mStridefreData);//
        ArrayList<Integer> mSpeedStringListData = intent.getIntegerArrayListExtra(Constant.mSpeedStringListData);//

        if (mSpeedStringListData!=null && mSpeedStringListData.size()>0){
            uploadRecord.ae = mSpeedStringListData;
        }
        if (mKcalData!=null && mKcalData.size()>0){
            uploadRecord.calorie = mKcalData;
        }
        if (mStridefreData!=null && mStridefreData.size()>0){
            uploadRecord.cadence = mStridefreData;
        }

        Log.i(TAG,"mStridefreData:===================="+mStridefreData);

        if (stridefreData!=null && stridefreData.size()>0){  //在离线分析时会有步频
            uploadRecord.cadence = stridefreData;
        }


        Log.i(TAG,"uploadRecord:"+uploadRecord);
        uploadDataAndJumpToShowPage(uploadRecord,sportCreateRecordID);

        boolean mIsAutoMonitor = MyUtil.getBooleanValueFromSP("mIsAutoMonitor");
        if (mIsAutoMonitor){
            uploadAnlysisREsultToSocket(uploadRecord);
        }
        HeartRateResultShowActivity.mUploadRecord = uploadRecord;
        String datatime1 = HeartRateResultShowActivity.mUploadRecord.datatime;
        if (!UStringUtil.isNullOrEmpty(datatime1) && datatime1.contains(":")) {
            long longTime = DateFormatUtils.getFormatTime(datatime1, DateFormatUtils.YYYY_MM_DD_HH_MM_SS_);
            HeartRateResultShowActivity.mUploadRecord.datatime = String.valueOf(longTime);
        }
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
                Log.i(TAG,"read:"+read);
                for (int i = 0; i < read/2; i++) {
                    bytes[0] = bytes[i*2];
                    bytes[1] = bytes[i*2+1];
                    int temp =  MyUtil.getShortByTwoBytes(bytes[0],bytes[1]);
                    calcuData.add(temp);
                    /*if (calcuData.size()<1000){
                        //Log.i(TAG,calcuData.size()+":"+temp);
                    }*/
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

    /**
     * @param
     * @param context
     * @param isSynLocalData
     */
    //上传分析结果
    public static void uploadRecordDataToServer(final UploadRecord uploadRecord, final Context context, final boolean isSynLocalData) {
        if (uploadRecord!=null){
            Log.i(TAG,"uploadRecordDataToServer uploadRecord:"+uploadRecord);
            Log.i(TAG,"uploadRecord.localEcgFileName:"+uploadRecord.localEcgFileName);

            if (isSynLocalData){
                //同步本地文件
                if (!MyUtil.isEmpty(uploadRecord.localEcgFileName) && uploadRecord.localEcgFileName.endsWith("ecg")){
                    String s = MyUtil.fileToBase64(new File(uploadRecord.localEcgFileName));
                    if (s!=null){
                        uploadRecord.ec = s;
                    }
                    Log.i(TAG,"uploadRecord.ec:"+uploadRecord.ec);
                }else {
                    //return;
                }
            }

            //final UploadRecord uploadRecord = new UploadRecord();

            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();
            MyUtil.addCookieForHttp(params);


        /*params.addBodyParameter("fi",uploadRecord.fi);
        params.addBodyParameter("es",uploadRecord.es);
        params.addBodyParameter("pi",uploadRecord.pi);
        params.addBodyParameter("cc",uploadRecord.cc);
        params.addBodyParameter("hrvr",uploadRecord.hrvr);
        params.addBodyParameter("hrvs",uploadRecord.hrvs);
        params.addBodyParameter("ahr",uploadRecord.ahr);
        //params.addBodyParameter("ahr","90");
        params.addBodyParameter("maxhr",uploadRecord.maxhr);
        params.addBodyParameter("minhr",uploadRecord.minhr);
        params.addBodyParameter("hrr",uploadRecord.hrr);
        params.addBodyParameter("hrs",uploadRecord.hrs);
        params.addBodyParameter("ec",uploadRecord.ec);
        params.addBodyParameter("ecr",uploadRecord.ecr);
        params.addBodyParameter("ecs",uploadRecord.ecs);
        params.addBodyParameter("ra",uploadRecord.ra);
        params.addBodyParameter("timestamp",uploadRecord.timestamp);
        params.addBodyParameter("datatime",uploadRecord.datatime);

        params.addBodyParameter("hr",uploadRecord.getHr());
        //params.addBodyParameter("hr","[]");
        params.addBodyParameter("ae",uploadRecord.getAe());
        params.addBodyParameter("distance",uploadRecord.getDistance());
        params.addBodyParameter("time",uploadRecord.getTime());
        params.addBodyParameter("cadence",uploadRecord.getCadence());
        params.addBodyParameter("calorie",uploadRecord.getCalorie());
        params.addBodyParameter("state",uploadRecord.getState());
        params.addBodyParameter("zaobo",uploadRecord.getZaobo());
        params.addBodyParameter("loubo",uploadRecord.getLoubo());
        params.addBodyParameter("latitude_longitude",uploadRecord.getLatitude_longitude());*/

            params.addBodyParameter("fi",uploadRecord.fi+"");
            params.addBodyParameter("es",(int)uploadRecord.es+"");
            params.addBodyParameter("pi",uploadRecord.pi+"");
            params.addBodyParameter("cc",uploadRecord.cc+"");
            params.addBodyParameter("hrvr",uploadRecord.hrvr+"");
            params.addBodyParameter("hrvs",uploadRecord.hrvs+"");
            params.addBodyParameter("ahr",uploadRecord.ahr+"");
            params.addBodyParameter("maxhr",uploadRecord.maxhr+"");
            params.addBodyParameter("minhr",uploadRecord.minhr+"");
            params.addBodyParameter("hrr",uploadRecord.hrr+"");
            params.addBodyParameter("hrs",uploadRecord.hrs+"");
            params.addBodyParameter("ec",uploadRecord.ec+"");
            params.addBodyParameter("ecr",uploadRecord.ecr+"");
            params.addBodyParameter("ecs",uploadRecord.ecs+"");
            params.addBodyParameter("ra",uploadRecord.ra+"");
            params.addBodyParameter("timestamp",uploadRecord.timestamp+"");
            params.addBodyParameter("datatime",uploadRecord.datatime+"");

            String hr = "";
            if (uploadRecord.hr!=null){
                hr = uploadRecord.hr.toString();
            }
            String ae = "";
            if (uploadRecord.ae!=null){
                ae = uploadRecord.ae.toString();
            }
            String cadence = "";
            if (uploadRecord.cadence!=null){
                cadence = uploadRecord.cadence.toString();
            }
            String calorie = "";
            if (uploadRecord.calorie!=null){
                calorie = uploadRecord.calorie.toString();
            }
            String latitudeLongitude = "";
            if (uploadRecord.latitudeLongitude!=null){
                latitudeLongitude = uploadRecord.latitudeLongitude.toString();
            }

            params.addBodyParameter("hr",hr+"");
            params.addBodyParameter("cadence",cadence+"");
            params.addBodyParameter("calorie",calorie+"");
            params.addBodyParameter("latitudeLongitude",latitudeLongitude+"");
            params.addBodyParameter("time",uploadRecord.time+"");
            params.addBodyParameter("distance",(int)uploadRecord.distance+"");

            final boolean isMarathonSportType = MyUtil.getBooleanValueFromSP(Constant.isMarathonSportType);
            if (isMarathonSportType && uploadRecord.state!=0) {  //state=0为静态数据，归为衣服历史记录
                params.addBodyParameter("state", "3");
                params.addBodyParameter("ae", Constant.sportAe);
            } else {
                params.addBodyParameter("ae",ae+"");
                params.addBodyParameter("state", uploadRecord.state + "");
            }

            params.addBodyParameter("zaobo",uploadRecord.zaobo+"");
            params.addBodyParameter("loubo",uploadRecord.loubo+"");
            params.addBodyParameter("inuse",uploadRecord.inuse+"");

            params.addBodyParameter("chaosPlotPoint","[]");
            params.addBodyParameter("frequencyDomainDiagramPoint","[]");
            params.addBodyParameter("sdnn1",uploadRecord.sdnn1+"");
            params.addBodyParameter("sdnn2",uploadRecord.sdnn2+"");
            params.addBodyParameter("hf1",(int)uploadRecord.hf1+"");
            params.addBodyParameter("hf2",(int)uploadRecord.hf2+"");
            params.addBodyParameter("lf1",(int)uploadRecord.lf1+"");
            params.addBodyParameter("lf2",(int)uploadRecord.lf2+"");
            params.addBodyParameter("lf",(int)uploadRecord.lf+"");
            params.addBodyParameter("hf",(int)uploadRecord.hf+"");
            params.addBodyParameter("chaosPlotMajorAxis","0");
            params.addBodyParameter("chaosPlotMinorAxis","0");


            httpUtils.send(HttpRequest.HttpMethod.POST, Constant.uploadReportURL, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String result = responseInfo.result;
                    Log.i(TAG,"onSuccess==result:"+result);


                //List<UploadRecord> uploadRecords = offLineDbAdapter.queryRecordAll();

                //Log.i(TAG,"uploadRecords:"+uploadRecords);

                    /*{
                         {
                            "ret": "0",
                            "errDesc":"数据上传成！"
                          }
                    }*/

                    OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(context);
                    try {
                        offLineDbAdapter.open();
                    }catch (Exception ignored){
                    }
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        int ret = jsonObject.getInt("ret");
                        String errDesc = jsonObject.getString("errDesc");
                        if (ret==0){
                            uploadRecord.uploadState = 1;  //上传成功后，将状态改为已上传
                            if (!isSynLocalData){
                                MyUtil.showToask(context,"数据上传成功");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    long orUpdateUploadReportObject = offLineDbAdapter.createOrUpdateUploadReportObject(uploadRecord);
                    Log.i(TAG,"orUpdateUploadReportObject:"+orUpdateUploadReportObject);

                    List<UploadRecord> uploadRecords = offLineDbAdapter.queryRecordAll();
                    Log.i(TAG,"uploadRecords:"+uploadRecords);

                    try {
                        offLineDbAdapter.close();
                    }catch (Exception e1){
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i(TAG,"onFailure==s:"+s+"    e:"+e);
                    if (!isSynLocalData){
                        MyUtil.showToask(context,"数据上传失败，稍后有网络会自动上传");
                    }

                    OffLineDbAdapter offLineDbAdapter = new OffLineDbAdapter(context);
                    try {
                        offLineDbAdapter.open();
                    }catch (Exception ignored){
                    }

                    uploadRecord.uploadState = 0;
                    long orUpdateUploadReportObject = offLineDbAdapter.createOrUpdateUploadReportObject(uploadRecord);
                    Log.i(TAG,"orUpdateUploadReportObject:"+orUpdateUploadReportObject);

                /*List<UploadRecord> uploadRecords = offLineDbAdapter.queryRecordAll();
                Log.i(TAG,"uploadRecords:"+uploadRecords);

                List<UploadRecord> uploadRecordsState = offLineDbAdapter.queryRecordByUploadState("0");
                Log.i(TAG,"uploadRecordsState:"+uploadRecordsState);*/

                    try {
                        offLineDbAdapter.close();
                    }catch (Exception e1){
                    }
                }
            });
        }

    }

    private String iconUrl;
    private String username;
    private String province;
    private String sex;
    private String age;
    private String prematureCount;
    private String missCount;
    private String overScore;

    private String averageHeart;
    private String averageHeartScore;
    private String maxHeart;
    private String maxHeartScore;
    private String kcal;
    private String kcalScore;
    private String allscore;
    private String rank;

    private void uploadAnlysisREsultToSocket(UploadRecord uploadRecord) {
        //A6,1

        User userFromSP = MyUtil.getUserFromSP();
        String testIconUrl = userFromSP.getIcon();
        String username = userFromSP.getUsername();
        String area = userFromSP.getArea();
        String sex = userFromSP.getSex();
        int mUserAge = HealthyIndexUtil.getUserAge();

        String sexString;
        if (sex.equals("1")){
            sexString = "男";
        }
        else {
            sexString = "女";
        }

        int prematureCount = uploadRecord.zaobo;
        int missCount = uploadRecord.loubo;
        int overScore = calOverScore(uploadRecord.zaobo,uploadRecord.loubo);

        int averageHeart = uploadRecord.ahr;
        int averageHeartScore = calAverageHeartScore(uploadRecord.ahr);;
        int maxHeart = uploadRecord.maxhr;;
        int maxHeartScore = calMaxHeart(uploadRecord.maxhr);

        int kcal = 0;
        if (uploadRecord.calorie!=null && uploadRecord.calorie.size()>0){ //卡路里
            float allcalorie = 0 ;
            for (String i: uploadRecord.calorie){
                allcalorie+=Float.parseFloat(i);
            }
            kcal = (int) allcalorie;
        }

        int kcalScore = 3;
        int allscore = overScore+averageHeartScore+maxHeartScore+kcalScore;

        ScoreInfo scoreInfo = new ScoreInfo(testIconUrl,username,area,sexString,mUserAge,prematureCount,missCount,overScore,averageHeart,averageHeartScore,
                maxHeart,maxHeartScore,kcal,kcalScore,allscore,0);

        Gson gson = new Gson();

        //A6,{"iconUrl":"url","username":"天空之城","province":"深圳","sex":"男","age":25,"prematureCount":2,"missCount":3,"overScore":3,"averageHeart":100,"averageHeartScore":4,"maxHeart":121,"maxHeartScore":4,"kcal":45,"kcalScore":2,"allscore":23,"rank":2}


        String msg = "A6,"+gson.toJson(scoreInfo);

        Log.i(TAG,"msg:"+msg);


        WebSocketProxy webSocketUtil = ((MyApplication) getApplication()).getWebSocketUtil();
        if (webSocketUtil!=null){
            webSocketUtil.sendSocketMsg(msg,true);
        }

    }

    private int calOverScore(int zaobo,int loubo){
        int sum = zaobo+loubo;
        int score ;
        if (sum==0){
            score = 5;
        }
        else if (sum>0 && sum<=3){
            score = 4;
        }
        else if (sum>3 && sum<=7){
            score = 3;
        }
        else if (sum>7 && sum<=10){
            score = 2;
        }
        else {
            score = 1;
        }
        return score;
    }

    private int calAverageHeartScore(int averageHeartScore){

        int score ;
        if (averageHeartScore<=100){
            score = 5;
        }
        else if (averageHeartScore>100 && averageHeartScore<=115){
            score = 4;
        }
        else if (averageHeartScore>115 && averageHeartScore<=130){
            score = 3;
        }
        else if (averageHeartScore>130 && averageHeartScore<=145){
            score = 2;
        }
        else {
            score = 1;
        }
        return score;

    }

    private int calMaxHeart(int value){

        int score ;
        if (value<=120){
            score = 5;
        }
        else if (value>120 && value<=135){
            score = 4;
        }
        else if (value>135 && value<=150){
            score = 3;
        }
        else if (value>150 && value<=165){
            score = 2;
        }
        else {
            score = 1;
        }
        return score;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
       return false;
    }

}
