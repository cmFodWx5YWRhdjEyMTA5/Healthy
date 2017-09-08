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

import com.amap.api.maps.model.LatLng;
import com.amsu.healthy.R;
import com.amsu.healthy.bean.ParcelableDoubleList;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.EcgFilterUtil;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.OffLineDbAdapter;
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
        final long startTimeMillis = intent.getLongExtra(Constant.startTimeMillis, -1);

        Log.i(TAG,"sportCreateRecordID:"+sportCreateRecordID);
        Log.i(TAG,"startTimeMillis:"+startTimeMillis);

        //final String ecgLocalFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/10-f3fbbf03-6925-49cd-881a-c2dad9e9b791";
        //final String ecgLocalFileName = "/storage/emulated/0/abluedata/20170711214450.ecg";
        //final String ecgLocalFileName =  Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/cloth/20170828165147.ecg";
        //final ArrayList<Integer> heartDataList_static = intent.getIntegerArrayListExtra(Constant.heartDataList_static);//静态心电心率，不为空则表示有静态心电数据
        //final String ecgLocalFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/20170516101758.ecg";

        //分析过程有可能耗时，在子线程中进行
        final String ecgLocalFileName = intent.getStringExtra(Constant.ecgLocalFileName); ///storage/emulated/0
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
                            int[] calcuEcgRate = new int[HealthyDataActivity.calGroupCalcuLength *HealthyDataActivity.oneGroupLength];
                            int heartCount = ecgDataList.size() / calcuEcgRate.length;


                            for (int j=0;j<heartCount;j++){
                                for (int i=0;i<calcuEcgRate.length;i++){
                                    calcuEcgRate[i] = ecgDataList.get(j*calcuEcgRate.length+i);
                                }
                                int mCurrentHeartRate = DiagnosisNDK.ecgHeart(calcuEcgRate, calcuEcgRate.length, Constant.oneSecondFrame);
                                //Log.i(TAG,"mCurrentHeartRate:"+ mCurrentHeartRate);
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

                        Log.i(TAG,"ecgDataList.size()"+ecgDataList.size());
                        final List<Integer> finalStridefreData = stridefreData;
                        generateUploadData(ecgDataList, fileBase64, heartDataList_static, sportState, sportCreateRecordID, hrr,
                                        startTimeMillis,intent,ecgLocalFileName, finalStridefreData,mKcalData);

                    }
                }.start();

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

    private void uploadDataAndJumpToShowPage(UploadRecord uploadRecord) {
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
                uploadRecordDataToServer(uploadRecordCopy,HeartRateAnalysisActivity.this,false);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        Intent intentToRateAnalysis = new Intent(HeartRateAnalysisActivity.this, HeartRateResultShowActivity.class);
        if (uploadRecord!=null){
            Bundle bundle = new Bundle();
            uploadRecord.ec = "";
            bundle.putParcelable("uploadRecord",uploadRecord);
            intentToRateAnalysis.putExtra("bundle",bundle);
            Log.i(TAG,"uploadRecord: putParcelable  "+uploadRecord);
        }
        //intentToRateAnalysis.putExtra(Constant.sportState,sportState);
        startActivity(intentToRateAnalysis);
        finish();
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
        if (AHR>30  && AHR<150){
            MyUtil.putIntValueFromSP(Constant.restingHR,AHR);
        }
        return new int[]{MaxHR,MinHR,AHR};
    }

    //生成上传数据，分为心电数据和运动数据
    private void generateUploadData(final List<Integer> ecgDataList, final String fileBase64, final ArrayList<Integer> heartDataList,
                                    final int sportState, long sportCreateRecordID, final int hrr,
                                    long startTimeMillis, Intent intent, final String ecgLocalFileName, List<Integer> stridefreData, List<String> calData) {
        final UploadRecord uploadRecord = new UploadRecord();

        long timestamp;
        String datatime ;
        if (startTimeMillis>0){
            timestamp = startTimeMillis/1000;
            //timestamp ="1501551888";
            datatime = MyUtil.getSpecialFormatTime("yyyy/MM/dd HH:mm:ss", new Date(startTimeMillis));
        }
        else {
            timestamp = System.currentTimeMillis()/1000;
            datatime = MyUtil.getSpecialFormatTime("yyyy/MM/dd HH:mm:ss", new Date());
        }

        uploadRecord.timestamp=timestamp;
        uploadRecord.datatime = datatime;
        uploadRecord.state = sportState;

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
            int ES = 0;
            int PI ;
            int FI ;
            int zaobo ;
            int loubo ;

            int ECr =1;
            String HRVs = getResources().getString(R.string.HeartRate_suggetstion_nodata);
            String ECs = getResources().getString(R.string.HeartRate_suggetstion_nodata);

            Log.i(TAG,"DiagnosisNDK.AnalysisEcg: =====================");
            HeartRateResult heartRateResult = DiagnosisNDK.AnalysisEcg(calcuData, calcuData.length, Constant.oneSecondFrame);
            Log.i(TAG,"heartRateResult:"+heartRateResult.toString());

            int allTimeAtSecond = (int) (ecgDataList.size()/(Constant.oneSecondFrame*1f));
            Log.i(TAG,"allTimeAtSecond:"+allTimeAtSecond);
            uploadRecord.time = allTimeAtSecond;

            if (heartRateResult.HF>0){
                ES = (int)(heartRateResult.LF / heartRateResult.HF);
            }

            PI = heartRateResult.RR_SDNN;
            FI = heartRateResult.RR_SDNN;

            if (PI>0 || ES>0){
                HRVs = HealthyIndexUtil.getHRVSuggetstion(PI, ES,this);
                Log.i(TAG,"hrvs:"+HRVs);
            }

            zaobo = heartRateResult.RR_Apb + heartRateResult.RR_Pvc;
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
            double distance = 0;
            long time = 0;
            List<ParcelableDoubleList> latitude_longitude;  //经纬度

            final ArrayList<String> mKcalData = intent.getStringArrayListExtra(Constant.mKcalData);//
            final ArrayList<Integer> mStridefreData = intent.getIntegerArrayListExtra(Constant.mStridefreData);//
            final ArrayList<Integer> mSpeedStringListData = intent.getIntegerArrayListExtra(Constant.mSpeedStringListData);//

            DbAdapter dbAdapter = new DbAdapter(this);
            dbAdapter.open();
            PathRecord pathRecord = dbAdapter.queryRecordById((int) sportCreateRecordID);
            dbAdapter.close();
            if (pathRecord!=null){
                Log.i(TAG,"pathRecord:"+pathRecord.toString());

                if (!MyUtil.isEmpty(pathRecord.getDistance())){
                    distance = Double.parseDouble(pathRecord.getDistance());
                }
                time = Long.parseLong(pathRecord.getDuration())/1000;
                latitude_longitude = getLatitude_longitudeString(pathRecord);

                uploadRecord.ae = mSpeedStringListData;
                uploadRecord.distance = distance;
                uploadRecord.time =time;
                if (mKcalData!=null ){
                    uploadRecord.calorie = mKcalData;
                }
                if (mStridefreData!=null){
                    uploadRecord.cadence = mStridefreData;
                }
                uploadRecord.latitudeLongitude = latitude_longitude;
            }
        }

        Log.i(TAG,"uploadRecord:"+uploadRecord);
        uploadDataAndJumpToShowPage(uploadRecord);


    }

    private List<ParcelableDoubleList> getLatitude_longitudeString(PathRecord pathRecord) {
        List<LatLng> latLngList = Util.parseLatLngList(pathRecord.getPathline());
        List<ParcelableDoubleList> listList = new ArrayList<>();
        for (LatLng latLng:latLngList){
            ParcelableDoubleList doubleList = new ParcelableDoubleList();
            doubleList.add(latLng.latitude);
            doubleList.add(latLng.longitude);
            listList.add(doubleList);
        }
        return listList;
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

    /**
     * @param uploadRecord
     * @param context
     * @param isSynLocalData
     */
    //上传分析结果
    public static void uploadRecordDataToServer(final UploadRecord uploadRecord, final Context context, boolean isSynLocalData) {
        if (uploadRecord!=null){
            Log.i(TAG,"uploadRecordDataToServer uploadRecord:"+uploadRecord);

            Log.i(TAG,"uploadRecord.localEcgFileName:"+uploadRecord.localEcgFileName);

            if (isSynLocalData){
                //同步本地文件
                if (!MyUtil.isEmpty(uploadRecord.localEcgFileName) && uploadRecord.localEcgFileName.endsWith("ecg")){
                    uploadRecord.ec = MyUtil.fileToBase64(new File(uploadRecord.localEcgFileName));
                    Log.i(TAG,"uploadRecord.ec:"+uploadRecord.ec);
                }else {
                    //return;
                }
            }

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
            params.addBodyParameter("es",uploadRecord.es+"");
            params.addBodyParameter("pi",uploadRecord.pi+"");
            params.addBodyParameter("cc",uploadRecord.cc+"");
            params.addBodyParameter("hrvr",uploadRecord.hrvr);
            params.addBodyParameter("hrvs",uploadRecord.hrvs);
            params.addBodyParameter("ahr",uploadRecord.ahr+"");
            params.addBodyParameter("maxhr",uploadRecord.maxhr+"");
            params.addBodyParameter("minhr",uploadRecord.minhr+"");
            params.addBodyParameter("hrr",uploadRecord.hrr);
            params.addBodyParameter("hrs",uploadRecord.hrs);
            params.addBodyParameter("ec",uploadRecord.ec);
            params.addBodyParameter("ecr",uploadRecord.ecr+"");
            params.addBodyParameter("ecs",uploadRecord.ecs);
            params.addBodyParameter("ra",uploadRecord.ra+"");
            params.addBodyParameter("timestamp",uploadRecord.timestamp+"");
            params.addBodyParameter("datatime",uploadRecord.datatime);

            String hr = "";
            if (uploadRecord.hr!=null){
                hr = uploadRecord.hr.toString();
            }

            params.addBodyParameter("hr",hr);

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

            params.addBodyParameter("ae",ae);
            params.addBodyParameter("cadence",cadence);
            params.addBodyParameter("calorie",calorie);
            params.addBodyParameter("latitudeLongitude",latitudeLongitude);

            params.addBodyParameter("time",uploadRecord.time+"");
            params.addBodyParameter("distance",uploadRecord.distance+"");
            params.addBodyParameter("state",uploadRecord.state+"");
            params.addBodyParameter("zaobo",uploadRecord.zaobo+"");
            params.addBodyParameter("loubo",uploadRecord.loubo+"");
            params.addBodyParameter("inuse",uploadRecord.inuse+"");

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
                    try {
                        offLineDbAdapter.open();
                    }catch (Exception ignored){
                    }
                    //uploadRecord.datatime = uploadRecord.datatime.replace("/", "-");  //将本地数据库时间改成和服务器一致，下次查看数据时，先从根据时间从本地查询

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        int ret = jsonObject.getInt("ret");
                        String errDesc = jsonObject.getString("errDesc");
                        if (ret==0){
                            uploadRecord.uploadState = 1;  //上传成功后，将状态改为已上传
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    long orUpdateUploadReportObject = offLineDbAdapter.createOrUpdateUploadReportObject(uploadRecord);
                    Log.i(TAG,"orUpdateUploadReportObject:"+orUpdateUploadReportObject);

                /*List<UploadRecord> uploadRecordsState = offLineDbAdapter.queryRecordByUploadState("0");
                Log.i(TAG,"uploadRecordsState:"+uploadRecordsState);*/

                    try {
                        offLineDbAdapter.close();
                    }catch (Exception e1){
                    }



                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i(TAG,"onFailure==s:"+s);

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
       return false;
    }

}
