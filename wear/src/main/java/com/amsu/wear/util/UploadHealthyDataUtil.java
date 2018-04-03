package com.amsu.wear.util;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.amsu.wear.application.MyApplication;
import com.amsu.wear.bean.IndexData;
import com.amsu.wear.bean.IndicatorAssess;
import com.amsu.wear.bean.JsonBase;
import com.amsu.wear.bean.WeekReport;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
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

        RequestParams params = new RequestParams();
        if (year!=-1){
            params.addBodyParameter("year",year+"");
        }
        if (weekOfYear!=-1){
            params.addBodyParameter("week",weekOfYear+"");
        }
        params.setUri("http://www.amsu-new.com:8081/intellingence-web/downloadLatelyWeekReport.do");
        HttpUtil.addCookieForHttp(params);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase.getRet()==0){
                    WeekReport weekReport = gson.fromJson(result, WeekReport.class);
                    Log.i(TAG,"weekReport:"+ weekReport.toString());
                    setIndicatorData(weekReport);
                    setIndexData(weekReport);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG,"上传onFailure==s:"+ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    public static void setIndicatorData(WeekReport weekReport){
        if (weekReport!=null && weekReport.errDesc!=null && weekReport.errDesc.guosuguohuan!=null){
            //BMI
            IndicatorAssess scoreBMI = HealthyIndexUtil.calculateScoreBMI(MyApplication.getContext());
            //储备心率
            IndicatorAssess scorehrReserve = HealthyIndexUtil.calculateScorehrReserve(MyApplication.getContext());

            List<String> huifuxinlv = weekReport.errDesc.huifuxinlv;
            int sum = 0;
            int count = 0;
            for (String s:huifuxinlv){
                if (!TextUtils.isEmpty(s)&& !s.equals("null") && Integer.parseInt(s)>0){
                    sum += Integer.parseInt(s);
                    count++;
                }
            }
            IndicatorAssess scoreHRR = null;
            if(count>0){
                int avHhrr = sum/count;
                //恢复心率HRR
                scoreHRR = HealthyIndexUtil.calculateScoreHRR(avHhrr,MyApplication.getContext());
            }
            else {
                scoreHRR = HealthyIndexUtil.calculateScoreHRR(0,MyApplication.getContext());
            }

            List<String> kangpilaozhishu = weekReport.errDesc.kangpilaozhishu;
            sum = 0;
            count = 0;
            for (String s:kangpilaozhishu){
                if (!TextUtils.isEmpty(s)&& !s.equals("null") && Integer.parseInt(s)>0){
                    sum += Integer.parseInt(s);
                    count++;
                }
            }
            IndicatorAssess scoreHRV = null;
            if(count>0){
                int avHhrv = sum/count;
                //抗疲劳指数HRV(心电分析算法得出)
                scoreHRV = HealthyIndexUtil.calculateScoreHRV(avHhrv,MyApplication.getContext());
            }
            else {
                scoreHRV = HealthyIndexUtil.calculateScoreHRV(0, MyApplication.getContext());
            }

            List<String> guosuguohuan = weekReport.errDesc.guosuguohuan;
            sum = 0;
            count = 0;
            for (String s:guosuguohuan){
                if (!TextUtils.isEmpty(s) && !s.equals("null") && Integer.parseInt(s)>0){
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
                scoreOver_slow = HealthyIndexUtil.calculateScoreOver_slow(over_slow,MyApplication.getContext());

                scoreSlow = HealthyIndexUtil.calculateTypeSlow(over_slow,MyApplication.getContext());
                scoreOver = HealthyIndexUtil.calculateTypeOver(over_slow,MyApplication.getContext());
            }
            else {
                scoreOver_slow = HealthyIndexUtil.calculateScoreOver_slow(0,MyApplication.getContext());
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
                scoreBeat = HealthyIndexUtil.calculateScoreBeat(zaobo,loubo,MyApplication.getContext());

                zaoboIndicatorAssess = HealthyIndexUtil.calculateTypeBeforeBeat(zaobo,MyApplication.getContext());
                louboIndicatorAssess = HealthyIndexUtil.calculateTypeMissBeat(loubo,MyApplication.getContext());
                SPUtil.putIntValueToSP("zaoboIndicatorAssess",zaoboIndicatorAssess.getPercent());
                SPUtil.putIntValueToSP("louboIndicatorAssess",louboIndicatorAssess.getPercent());
            }

            HealthyIndexUtil.calcuIndexWarringHeartIcon(scoreSlow,scoreOver,zaoboIndicatorAssess,louboIndicatorAssess);

            // 健康储备(按训练时间计算)
            //IndicatorAssess scoreReserveHealth = HealthyIndexUtil.calculateScoreReserveHealth();
            IndicatorAssess scoreReserveHealth = HealthyIndexUtil.calculateScoreReserveHealth((int) (Float.parseFloat(weekReport.errDesc.chubeijiankang)/60),MyApplication.getContext());


            Log.i(TAG,"scoreBMI:"+scoreBMI);
            Log.i(TAG,"scorehrReserve:"+scorehrReserve);
            Log.i(TAG,"scoreHRR:"+scoreHRR);
            Log.i(TAG,"scoreHRV:"+scoreHRV);
            Log.i(TAG,"scoreOver_slow:"+scoreOver_slow);
            Log.i(TAG,"scoreBeat:"+scoreBeat);
            Log.i(TAG,"scoreReserveHealth:"+scoreReserveHealth);


            int healthyIindexvalue = HealthyIndexUtil.calculateIndexvalue(scoreBMI, scorehrReserve, scoreHRR, scoreHRV, scoreOver_slow, scoreBeat, scoreReserveHealth);
            Log.i(TAG,"healthyIindexvalue:"+healthyIindexvalue);
            SPUtil.putIntValueToSP("healthyIindexvalue",healthyIindexvalue);

            int physicalAgeDValue = HealthyIndexUtil.calculatePhysicalAgeDValue(scoreBMI, scorehrReserve, scoreHRR, scoreHRV, scoreReserveHealth);
            SPUtil.putIntValueToSP("physicalAgeDValue",physicalAgeDValue);

            SPUtil.putIntValueToSP("scoreOver_slowPercent",scoreOver_slow.getPercent());
        }
    }

    public static void setIndexData(WeekReport weekReport) {
        if (weekReport.errDesc!=null && weekReport.errDesc.guosuguohuan!=null){
            List<String> guosuguohuan = weekReport.errDesc.guosuguohuan;

            //过缓/过速(心电分析算法得出)
            IndicatorAssess scoreSlow = null;  //过缓
            IndicatorAssess scoreOver = null;  //过速
            int scoreSlowCount = 0;
            int scoreOverCount = 0;

            boolean isFirst =true;
            List<WeekReport.WeekReportResult.HistoryRecordItem> weekAllHistoryRecords = weekReport.errDesc.list;

            Collections.reverse(weekAllHistoryRecords);

            for (WeekReport.WeekReportResult.HistoryRecordItem historyRecordItem:weekAllHistoryRecords){
                Log.i(TAG,"historyRecordItem:"+historyRecordItem.toString());
            }



            int sum = 0;
            int heartBiggerThanZeroCount = 0;
            for (String s:guosuguohuan){
                if (!TextUtils.isEmpty(s)&& !s.equals("null") && Integer.parseInt(s)>0){
                    heartBiggerThanZeroCount++;
                    sum += Integer.parseInt(s);
                }
            }

            IndicatorAssess scoreBeforeBeat = null;
            IndicatorAssess scoreMissBeat = null;

            if(heartBiggerThanZeroCount>0){  //当心率大于0的点有时才计算过速、过缓、早搏、漏搏
                int over_slow = sum/heartBiggerThanZeroCount;
                Log.i(TAG,"over_slow:"+over_slow);
                //过缓/过速(心电分析算法得出)
                scoreSlow = HealthyIndexUtil.calculateTypeSlow(over_slow,MyApplication.getContext());
                scoreOver = HealthyIndexUtil.calculateTypeOver(over_slow,MyApplication.getContext());


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
                    scoreBeforeBeat = HealthyIndexUtil.calculateTypeBeforeBeat(zaobo,MyApplication.getContext());
                    scoreMissBeat = HealthyIndexUtil.calculateTypeMissBeat(loubo,MyApplication.getContext());
                }

            }

            List<IndexData> indexDataList = new ArrayList<>();


            if (scoreSlow!=null){
                indexDataList.add(new IndexData("心率过缓","",scoreSlow.getPercent()));
            }
            if (scoreOver!=null){
                indexDataList.add(new IndexData("心率过速","",scoreOver.getPercent()));
            }

            if (scoreBeforeBeat!=null){
                indexDataList.add(new IndexData("早搏","",scoreBeforeBeat.getPercent()));
            }
            if (scoreMissBeat!=null){
                indexDataList.add(new IndexData("漏搏","",scoreMissBeat.getPercent()));
            }

            SPUtil.putListToSP(indexDataList,"indexdata");

        }

    }


}
