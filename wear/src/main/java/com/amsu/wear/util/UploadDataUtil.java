package com.amsu.wear.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amsu.wear.R;
import com.amsu.wear.application.MyApplication;
import com.amsu.wear.bean.UploadRecord;
import com.amsu.wear.map.MapUtil;
import com.amsu.wear.map.PathRecord;
import com.test.objects.HeartRateResult;
import com.test.utils.DiagnosisNDK;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.util
 * @time 2018-03-16 3:31 PM
 * @describe
 */
public class UploadDataUtil {

    private static final String TAG = UploadDataUtil.class.getSimpleName();


    //生成上传数据，分为心电数据和运动数据
    public void generateUploadData(String ecgFileName,PathRecord pathRecord, final ArrayList<Integer> heartDataList, final int sportState, final int hrr, long startTimeMillis,
                                    List<Integer> stridefreData, List<String> calData, Context context, ArrayList<Integer> mSpeedStringList) {

        final UploadRecord uploadRecord = new UploadRecord();

        File file = new File(ecgFileName);
        String fileBase64 = null;
        List<Integer> ecgDataList = null;
        if (file.exists()) {
            fileBase64 = FileUtil.fileToBase64(file);
            ecgDataList = FileUtil.readIntArrayDataFromFile(file);
        }

        uploadRecord.timestamp = startTimeMillis/1000;;
        uploadRecord.datatime = FormatUtil.getSpecialFormatTime("yyyy/MM/dd HH:mm:ss", new Date(startTimeMillis));
        uploadRecord.state = sportState;


        //设置心电数据
        if (ecgDataList!=null && heartDataList.size()>0){
            int MaxHR ;
            int MinHR ;
            int AHR;
            int[] ints = HeartUtil.setHeartMaxMinAverage(heartDataList);
            MaxHR = ints[0];
            MinHR = ints[1];
            AHR = ints[2];

            String  EC = Constant.uploadRecordDefaultString;
            if (!TextUtils.isEmpty(fileBase64)){
                EC = fileBase64;
            }

            String HRs = context.getResources().getString(R.string.HeartRate_suggetstion_nodata);  //心率健康建议
            if (AHR>0){
                String heartRateSuggetstion = HealthyIndexUtil.getHeartRateSuggetstion(sportState, AHR,context);
                if (!TextUtils.isEmpty(heartRateSuggetstion)){
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
            String HRVs = context.getResources().getString(R.string.HeartRate_suggetstion_nodata);
            String ECs = context.getResources().getString(R.string.HeartRate_suggetstion_nodata);

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
                HRVs = HealthyIndexUtil.getHRVSuggetstion(FI, ES,context);
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
            }

            zaobo = heartRateResult.RR_Apb+ heartRateResult.RR_Pvc + heartRateResult.RR_2 + heartRateResult.RR_3 + heartRateResult.RR_Iovp + heartRateResult.RR_double;
            loubo = heartRateResult.RR_Boleakage;

            if (zaobo>0){
                ECs = context.getResources().getString(R.string.premature_beat_times)+zaobo+context.getResources().getString(R.string.premature_beat_times_decrible);
            }
            if (loubo>0){
                ECs = context.getResources().getString(R.string.missed_beat_times)+loubo+context.getResources().getString(R.string.missed_beat_times_decrible);
            }
            else {
                if (AHR>0){
                    ECs = context.getResources().getString(R.string.abnormal_ecg);
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
            uploadRecord.cc = (220- UserUtil.getUserInfo().getAge());
        }

        //设置跑步数据

        Log.i(TAG,"pathRecord:"+pathRecord.toString());
        if (pathRecord!=null){
            if (!TextUtils.isEmpty(pathRecord.getDistance())){
                uploadRecord.distance = Float.parseFloat(pathRecord.getDistance());
            }
            if (!TextUtils.isEmpty(pathRecord.getDuration())){
                uploadRecord.time = Long.parseLong(pathRecord.getDuration())/1000;
            }
            uploadRecord.latitudeLongitude = MapUtil.getLatitude_longitudeString(pathRecord);
        }

        if (mSpeedStringList!=null && mSpeedStringList.size()>0){
            uploadRecord.ae = mSpeedStringList;
        }
        if (calData!=null && calData.size()>0){
            uploadRecord.calorie = calData;
        }
        if (stridefreData!=null && stridefreData.size()>0){  //在离线分析时会有步频
            uploadRecord.cadence = stridefreData;
        }

        Log.i(TAG,"uploadRecord:"+uploadRecord);
        uploadRecordDataToServer(uploadRecord,context);

    }

    //上传分析结果
    public void uploadRecordDataToServer(final UploadRecord uploadRecord, final Context context) {
        RequestParams params = new RequestParams();
        HttpUtil.addCookieForHttp(params);
        params.setUri("http://www.amsu-new.com:8081/intellingence-web/uploadReport.do");
        HttpUtil.addObjectToHttpParmHaveInt(uploadRecord,params,"es","distance","sdnn1","sdnn2","hf1","hf2","lf1","lf2","lf","hf");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.i(TAG,"onSuccess:"+result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int ret = jsonObject.getInt("ret");
                    String errDesc = jsonObject.getString("errDesc");
                    if (ret==0){
                        ToastUtil.showToask(context.getResources().getString(R.string.record_upload_success));
                    }
                    else {
                        ToastUtil.showToask(context.getResources().getString(R.string.record_upload_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //finish();
                EventBus.getDefault().post(HttpUtil.HttpUploadData_success);

                if (uploadRecord.uploadState==-1){
                    try {
                        DBUtil.getDbManager().deleteById(UploadRecord.class,uploadRecord.timestamp);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.i(TAG,"onError:"+ex);
                ToastUtil.showToask(context.getResources().getString(R.string.record_upload_fail)+ex);
                //finish();
                EventBus.getDefault().post(HttpUtil.HttpUploadData_fail);

                //上传失败，保存到本地数据库，需要在有网络时重新上传
                try {
                    uploadRecord.uploadState = -1;
                    DBUtil.getDbManager().saveOrUpdate(uploadRecord);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //finish();
                EventBus.getDefault().post(HttpUtil.HttpUploadData_fail);
            }

            @Override
            public void onFinished() {
                //finish();
                EventBus.getDefault().post(HttpUtil.HttpUploadData_fail);
            }

        });

    }

    //检查本地缓存数据库，如果有上次上传失败的，下次重新上传
    public void checkUploadFailData(){
        if (HttpUtil.isNetworkAvailable(MyApplication.getContext())){
            Log.i(TAG,"网络可用");
            List<UploadRecord> uploadRecords = findUploadFailDataFromLocalDB();
            if (uploadRecords!=null && uploadRecords.size()>0){
                for (UploadRecord uploadRecord:uploadRecords){
                    uploadRecordDataToServer(uploadRecord, MyApplication.getContext());
                }
            }
        }
    }

    public List<UploadRecord> findUploadFailDataFromLocalDB(){
        List<UploadRecord> uploadRecords = null;
        try {
            uploadRecords = DBUtil.getDbManager().selector(UploadRecord.class)
                    .where("uploadState","=","-1")
                    //.where("name","like","%kevin%")
                    //.and("email", "=", "caolbmail@gmail.com")
                    //.orderBy("regTime",true)
                    //.limit(2) //只查询两条记录
                    //.offset(2) //偏移两个,从第三个记录开始返回,limit配合offset达到sqlite的limit m,n的查询
                    .findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG,"uploadRecords:"+uploadRecords);
        return uploadRecords;
    }
}
