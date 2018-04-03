package com.amsu.wear.map.proxy;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amsu.wear.util.FormatUtil;
import com.amsu.wear.util.TimerUtil;
import com.amsu.wear.map.MapUtil;
import com.amsu.wear.map.PathRecord;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.map
 * @time 2018-03-16 11:05 AM
 * @describe
 */

/*地图相关工具类
* 支持谷歌地图和高德地图
*
* */


public abstract class MapProxy {
    private static final String TAG = MapProxy.class.getSimpleName();

    Context mContext;
    private List<AMapLocation> mOutdoorCal8ScendSpeedList = new ArrayList<>();
    private List<Float> mIndoorCal8ScendSpeedList = new ArrayList<>();
    boolean mIsOutDoor = true;
    private long mCurrentTimeMillis = 0;
    private static final int trackSpeedOutdoorMAX = 10;  //户外行走的最大速度
    private static final int trackSpeedIndoorMAX = 10;  //室内行走的最大速度
    double mAllDistance;
    private double mAddDistance;
    private float mPreOutDoorDistance;  //之前的运动距离，当运动异常终止时，下次恢复时需要添加之前的距离
    private float preForOneKMPace = -1;
    PathRecord mPathRecord;    //存放未纠偏轨迹记录信息
    static final long GPSINTERVAL = 2500; //定位之间间隔
    private Timer mCalcuPacetimer;     //计算配速计时器
    AMapLocationClient mlocationClient; // 高德地图定位基础类
    GoogleApiClient mGoogleApiClient;  // 谷歌地图定位基础类

    public abstract void init();

    void startCalcuPace(){
        mPathRecord = new PathRecord();
        mPathRecord.setDate(FormatUtil.getSpecialFormatTime("yyyy-MM-dd HH:mm:ss ",new Date(System.currentTimeMillis())));

        //8秒执行一次操作
        mCalcuPacetimer = TimerUtil.executeIntervals(8 * 1000, new TimerUtil.DelayExecuteTimeListener() {
            @Override
            public void execute() {
                int forOneKMSecond = calcuPeriodPace();
                if (onMapDataListener!=null){
                    onMapDataListener.onReceivePace(forOneKMSecond);
                }
            }
        });
    }

    //计算跑步速度，在室内和室外统一采用地图返回的传感器速度
    void calculateSpeed(AMapLocation aMapLocation) {
        if (mIsOutDoor){
            mOutdoorCal8ScendSpeedList.add(aMapLocation);
        }
        else {
            mIndoorCal8ScendSpeedList.add(aMapLocation.getSpeed());
        }
    }

    void calculateDistance(AMapLocation aMapLocation) {
        //不关室内室外，首次定位几个点策略：定位5个点，这5个点的距离在100m范围之内属于正常情况，否则为定位不准，重新定位前5个点
        if (mPathRecord.getPathline().size()<5){
            mPathRecord.addpoint(aMapLocation);
            float distance = MapUtil.getDistance(mPathRecord.getPathline());
            if (distance>100){
                mPathRecord.getPathline().clear();
            }
            if (mPathRecord.getPathline().size()==5){
                mCurrentTimeMillis = System.currentTimeMillis();
            }
            return;
        }
        if (mCurrentTimeMillis==0) {
            mCurrentTimeMillis = System.currentTimeMillis();
        }

        double tempDistance;
        if (mIsOutDoor){
            AMapLocation lastAMapLocation = mPathRecord.getPathline().get(mPathRecord.getPathline().size() - 1);
            tempDistance = MapUtil.getTwoPointDistance(lastAMapLocation, aMapLocation);
            //Log.i(TAG,"tempDistance:"+tempDistance);
            if (tempDistance<=trackSpeedOutdoorMAX*((System.currentTimeMillis() - mCurrentTimeMillis) / 1000f) && aMapLocation.getSpeed()<=trackSpeedOutdoorMAX){
                mPathRecord.addpoint(aMapLocation);
                mAllDistance = MapUtil.getDistance(mPathRecord.getPathline())+mAddDistance-mPreOutDoorDistance;
                mCurrentTimeMillis = System.currentTimeMillis();
            }
        }
        else {
            //在室内跑步
            if (aMapLocation.getSpeed()<trackSpeedIndoorMAX){
                tempDistance = aMapLocation.getSpeed() * ((System.currentTimeMillis() - mCurrentTimeMillis) / 1000f); //s=vt,单位:m
                mAddDistance += tempDistance;
                mAllDistance = mAddDistance;
            }
            mCurrentTimeMillis = System.currentTimeMillis();
        }
        //mRunningData.setMileage(FormatUtil.getFormatDistance(mAllDistance));
    }

    private int calcuPeriodPace() {
        float forOneKMSecond = 0;
        Log.i(TAG,"mOutdoorCal8ScendSpeedList.size():"+ mOutdoorCal8ScendSpeedList.size());
        Log.i(TAG,"mIndoorCal8ScendSpeedList.size():"+ mIndoorCal8ScendSpeedList.size());

        float curPeriodAverageSpeed = 0;
        if(mIsOutDoor){
            float distance = MapUtil.getDistance(mOutdoorCal8ScendSpeedList);
            curPeriodAverageSpeed = distance / 8f;
        }
        else {
            if (mIndoorCal8ScendSpeedList.size()>0){
                float sum = 0;
                for (float i: mIndoorCal8ScendSpeedList){
                    sum += i;
                }
                curPeriodAverageSpeed = sum/ mIndoorCal8ScendSpeedList.size();
            }
        }

        Log.i(TAG,"for8SecondAverageSpeed:"+curPeriodAverageSpeed);

        if (curPeriodAverageSpeed>0){
            if (preForOneKMPace ==-1){
                preForOneKMPace = forOneKMSecond = (1/curPeriodAverageSpeed)*1000f;
            }
            else {
                forOneKMSecond = ((1/curPeriodAverageSpeed)*1000f+ preForOneKMPace)/2;
                preForOneKMPace = forOneKMSecond;
            }

            Log.i(TAG,"forOneKMSecond:"+forOneKMSecond);

            if (forOneKMSecond>30*60 && forOneKMSecond<50*60){ //配速范围：2~30(分钟/公里)  在30~50之间归为30，小于2归为2，大于50归为0.
                forOneKMSecond = 30*60;
            }
            else if(forOneKMSecond>0 && forOneKMSecond<2*60){
                forOneKMSecond = 2*60;
            }
            else if(forOneKMSecond>50*60){
                forOneKMSecond = 0;
            }
            Log.i(TAG,"forOneKMSecond:"+forOneKMSecond);
        }

        String formatPace = getFormatPace((int) forOneKMSecond);


        Log.i(TAG,"startCal8ScendSpeed:  speed:"+forOneKMSecond+",   formatPace:"+formatPace);

        //mSpeedStringList.add((int) forOneKMSecond);
        //mRunningData.setPace(formatSpeed);

        mIndoorCal8ScendSpeedList.clear();
        mOutdoorCal8ScendSpeedList.clear();

        //ObservableManager.newInstance().notify(TrackFragment.FUNCTION_WITH_PARAM_AND_RESULT, mPathRecord);

        return (int)forOneKMSecond;
    }

    public void stopGps(){
        if (mCalcuPacetimer!=null){
            mCalcuPacetimer.cancel();
            mCalcuPacetimer = null;
        }

        if (mlocationClient!=null){
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
            mlocationClient = null;
        }

       if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
           mGoogleApiClient.disconnect();
           mGoogleApiClient = null;
       }

    }


    //根据秒数获取配速格式
    public static String getFormatPace(int forOneKMSecond){
        if (forOneKMSecond==0){
            return "--";
        }
        else {
            return forOneKMSecond/60+"'"+forOneKMSecond%60+"''";
        }
    }

    GaodeMapProxy.OnMapDataListener onMapDataListener;
    public interface OnMapDataListener{
        void onReceivePace(int forOneKMSecond);
        void onReceiveLocation(AMapLocation aMapLocation, PathRecord pathRecord, double allDistance);
    }

    public void setOnMapDataListener(GaodeMapProxy.OnMapDataListener onMapDataListener) {
        this.onMapDataListener = onMapDataListener;
    }

}
