package com.amsu.healthy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.bean.WeekReport;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.healthy.utils
 * @time 2017-12-19 5:16 PM
 * @describe
 */
public class UploadHealthyDataUtil {

    private static final String TAG = "UploadHealthyDataUtil";

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

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.downloadLatelyWeekReportURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(activity);
                if (isFromLogin){
                    activity.startActivity(new Intent(activity,MainActivity.class));
                    MyUtil.destoryAllAvtivity(activity);
                }
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase.getRet()==0){
                    WeekReport weekReport = gson.fromJson(result, WeekReport.class);
                    Log.i(TAG,"weekReport:"+ weekReport.toString());
                    setIndicatorData(weekReport);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog(activity);
                if (isFromLogin){
                    activity.startActivity(new Intent(activity,MainActivity.class));
                    MyUtil.destoryAllAvtivity(activity);
                }
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }

    public static void setIndicatorData(WeekReport weekReport){
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
            //IndicatorAssess scoreReserveHealth = HealthyIndexUtil.calculateScoreReserveHealth();
            IndicatorAssess scoreReserveHealth = HealthyIndexUtil.calculateScoreReserveHealth((int) (Float.parseFloat(weekReport.errDesc.chubeijiankang)/60),MyApplication.appContext);


            Log.i(TAG,"scoreBMI:"+scoreBMI);
            Log.i(TAG,"scorehrReserve:"+scorehrReserve);
            Log.i(TAG,"scoreHRR:"+scoreHRR);
            Log.i(TAG,"scoreHRV:"+scoreHRV);
            Log.i(TAG,"scoreOver_slow:"+scoreOver_slow);
            Log.i(TAG,"scoreBeat:"+scoreBeat);
            Log.i(TAG,"scoreReserveHealth:"+scoreReserveHealth);


            int healthyIindexvalue = HealthyIndexUtil.calculateIndexvalue(scoreBMI, scorehrReserve, scoreHRR, scoreHRV, scoreOver_slow, scoreBeat, scoreReserveHealth);
            Log.i(TAG,"healthyIindexvalue:"+healthyIindexvalue);
            MyUtil.putIntValueFromSP("healthyIindexvalue",healthyIindexvalue);

            int physicalAgeDValue = HealthyIndexUtil.calculatePhysicalAgeDValue(scoreBMI, scorehrReserve, scoreHRR, scoreHRV, scoreReserveHealth);
            MyUtil.putIntValueFromSP("physicalAgeDValue",physicalAgeDValue);

            MyUtil.putIntValueFromSP("scoreOver_slowPercent",scoreOver_slow.getPercent());


        }
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
                                MyUtil.showToask(context,context.getResources().getString(R.string.record_upload_success));
                            }
                        }
                        else {
                            MyUtil.showToask(context,context.getResources().getString(R.string.record_upload_fail));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    long orUpdateUploadReportObject = offLineDbAdapter.createOrUpdateUploadReportObject(uploadRecord);
                    Log.i(TAG,"orUpdateUploadReportObject:"+orUpdateUploadReportObject);

                    try {
                        offLineDbAdapter.close();
                    }catch (Exception e1){
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i(TAG,"onFailure==s:"+s+"    e:"+e);
                    if (!isSynLocalData){
                        MyUtil.showToask(context,context.getResources().getString(R.string.record_upload_fail));
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


}
