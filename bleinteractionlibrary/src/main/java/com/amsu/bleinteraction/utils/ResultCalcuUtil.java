package com.amsu.bleinteraction.utils;

import android.util.Log;

import com.test.objects.HeartRate;
import com.test.utils.DiagnosisNDK;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.utils
 * @time 11/22/2017 4:02 PM
 * @describe
 */



public class ResultCalcuUtil {
    private static final String TAG = "ResultCalcuUtil";
    private int[] calcuEcgRateAfterFilter = new int[EcgAccDataUtil.calGroupCalcuLength *EcgAccDataUtil.ecgOneGroupLength]; //1000条数据:（100组，一组有10个数据点）
    private int[] preCalcuEcgRateAfterFilter = new int[EcgAccDataUtil.calGroupCalcuLength*EcgAccDataUtil.ecgOneGroupLength]; //前一次数的数据，12s
    private int[] fourCalcuEcgRateAfterFilter = new int[EcgAccDataUtil.timeSpanGgroupCalcuLength*EcgAccDataUtil.ecgOneGroupLength]; //4s的数据*/

    private int[] calcuEcgRateBeforeFilter = new int[EcgAccDataUtil.calGroupCalcuLength *EcgAccDataUtil.ecgOneGroupLength]; //1000条数据:（100组，一组有10个数据点）
    private int[] preCalcuEcgRateBeforeFilter = new int[EcgAccDataUtil.calGroupCalcuLength*EcgAccDataUtil.ecgOneGroupLength]; //前一次数的数据，12s
    private int[] fourCalcuEcgRateBeforeFilter = new int[EcgAccDataUtil.timeSpanGgroupCalcuLength*EcgAccDataUtil.ecgOneGroupLength]; //4s的数据*/

    private byte[] accByteData = new byte[EcgAccDataUtil.accDataLength];
    private int accCalcuDataIndex = 0;

    private boolean isFirstCalcu;
    private int currentGroupIndex = 0;   //组的索引
    private boolean isNeedUpdateHeartRate;

    private FileWriteHelper mFileWriteHelper;

    public ResultCalcuUtil() {
        mFileWriteHelper = FileWriteHelper.getFileWriteHelper();
    }

    //计算心率，滤波前和滤波后的数据
    public void notifyReciveAcgPackageData(int[] beforeFilterTempEcgInts, int[] afterFilterTempEcgInts){
        mFileWriteHelper.writeEcgDataToFile(beforeFilterTempEcgInts);  //写入滤波前数据

        if (isFirstCalcu){
            if (currentGroupIndex< EcgAccDataUtil.calGroupCalcuLength){
                //未到时间（1800个数据点计算一次心率）
                System.arraycopy(afterFilterTempEcgInts, 0, calcuEcgRateAfterFilter, currentGroupIndex * EcgAccDataUtil.ecgOneGroupLength, afterFilterTempEcgInts.length);
                System.arraycopy(beforeFilterTempEcgInts, 0, calcuEcgRateBeforeFilter, currentGroupIndex * EcgAccDataUtil.ecgOneGroupLength, beforeFilterTempEcgInts.length);
            } else{
                isNeedUpdateHeartRate = true;
                isFirstCalcu = false;
            }
        }
        else {
            if (currentGroupIndex<EcgAccDataUtil.timeSpanGgroupCalcuLength){ //未到4s
                System.arraycopy(afterFilterTempEcgInts, 0, fourCalcuEcgRateAfterFilter, currentGroupIndex * EcgAccDataUtil.ecgOneGroupLength, afterFilterTempEcgInts.length);
                System.arraycopy(beforeFilterTempEcgInts, 0, fourCalcuEcgRateBeforeFilter, currentGroupIndex * EcgAccDataUtil.ecgOneGroupLength, beforeFilterTempEcgInts.length);
            }
            else {//到4s,需要前8s+当前4s
                int i=0;
                for (int j = EcgAccDataUtil.timeSpanGgroupCalcuLength*EcgAccDataUtil.ecgOneGroupLength; j< preCalcuEcgRateAfterFilter.length; j++){
                    calcuEcgRateAfterFilter[i++] = preCalcuEcgRateAfterFilter[j];
                }
                System.arraycopy(fourCalcuEcgRateAfterFilter, 0, calcuEcgRateAfterFilter, i, fourCalcuEcgRateAfterFilter.length);

                int k=0;
                for (int j = EcgAccDataUtil.timeSpanGgroupCalcuLength*EcgAccDataUtil.ecgOneGroupLength; j< preCalcuEcgRateBeforeFilter.length; j++){
                    calcuEcgRateBeforeFilter[k++] = preCalcuEcgRateBeforeFilter[j];
                }
                System.arraycopy(fourCalcuEcgRateBeforeFilter, 0, calcuEcgRateBeforeFilter, k, fourCalcuEcgRateBeforeFilter.length);
                isNeedUpdateHeartRate = true;
            }
        }

        if (isNeedUpdateHeartRate){
            currentGroupIndex = 0;
            //计算、更新心率，到4s
            Log.i(TAG,"计算心率:");
            HeartRate heartRate = DiagnosisNDK.ecgHeart(calcuEcgRateBeforeFilter, calcuEcgRateAfterFilter, calcuEcgRateAfterFilter.length, BleConstant.oneSecondFrame);
            //int heartRate = DiagnosisNDK.ecgHeart( calcuEcgRateAfterFilter, calcuEcgRateAfterFilter.length, BleConstant.oneSecondFrame);
            Log.i(TAG,"heartRate:"+ heartRate);

            if (onHeartCalcuListener!=null && heartRate!=null){
                onHeartCalcuListener.onReceiveHeart(heartRate);
            }

            //新版衣服在导联脱落时发上来的心电数据是00，所以不会有心率计算结果出来
            /*if (mCommunicateToBleService.mIsDeviceDroped){  //在导联脱落时心率计算偏差较大，设为0,
                mCurrentHeartRate = 0;
            }*/

            System.arraycopy(calcuEcgRateAfterFilter, 0, preCalcuEcgRateAfterFilter, 0, calcuEcgRateAfterFilter.length);
            System.arraycopy(afterFilterTempEcgInts, 0, fourCalcuEcgRateAfterFilter, currentGroupIndex * 10, afterFilterTempEcgInts.length);

            System.arraycopy(calcuEcgRateBeforeFilter, 0, preCalcuEcgRateBeforeFilter, 0, calcuEcgRateBeforeFilter.length);
            System.arraycopy(beforeFilterTempEcgInts, 0, fourCalcuEcgRateBeforeFilter, currentGroupIndex * 10, beforeFilterTempEcgInts.length);

            isNeedUpdateHeartRate = false;
        }
        currentGroupIndex++;
    }

    //处理加速度数据
    public void notifyReciveAccPackageData(int[] accOneGroupDataInts) {
        //mFileWriteHelper.writeAccDataToFile(accOneGroupDataInts);

        if (accCalcuDataIndex<EcgAccDataUtil.accDataLength){
            for (int i: accOneGroupDataInts){
                //accData.add(i);
                accByteData[accCalcuDataIndex++] = (byte)i;
            }
        }
        else {
            //计算
            int tempStridefre = getStridefreByAccData(accByteData);
            Log.i(TAG,"tempStridefre: "+tempStridefre);

            accCalcuDataIndex=0;
            for (int i: accOneGroupDataInts){
                accByteData[accCalcuDataIndex++] = (byte)i;
            }

            if (onHeartCalcuListener!=null){
                onHeartCalcuListener.onReceiveStride(tempStridefre);
            }
        }
    }

    private int getStridefreByAccData(byte[] accByteData){
        //int clothDeviceType = BleConnectionProxy.getInstance().getmConnectionConfiguration().clothDeviceType;
        int[] results = new int[2];

        int fs = 26;

        //神念新版主机频率为26
        //新版主机步频计算频率为52
        /*if (clothDeviceType==BleConstant.clothDeviceType_secondGeneration_IOE || clothDeviceType==BleConstant.clothDeviceType_secondGeneration_AMSU){
            fs = 52;
        }*/

        DiagnosisNDK.AnalysisPedo(accByteData,accByteData.length,results,fs);
        Log.i(TAG,"results: "+results[0]+"  "+results[1]);

        //每分钟的步数
        float step = results[1] * 5.21f;

        if (fs == 52){
            step *= 2;
        }
        return (int) step;     //
    }

    private OnHeartCalcuListener onHeartCalcuListener;

    public interface OnHeartCalcuListener{
        void onReceiveHeart(HeartRate heartRate);
        void onReceiveStride(int currentHeartRate);
    }

    public void setOnHeartCalcuListener(OnHeartCalcuListener onHeartCalcuListener){
        this.onHeartCalcuListener = onHeartCalcuListener;
    }

}
