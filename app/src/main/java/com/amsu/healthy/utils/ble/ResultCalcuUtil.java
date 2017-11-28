package com.amsu.healthy.utils.ble;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.wifiTransmit.uilt.WriteReadDataToBinaryFile;
import com.amsu.healthy.utils.wifiTransmit.uilt.WriteReadDataToFileStrategy;
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

    private boolean mIsrecording;

    private WriteReadDataToFileStrategy mWriteECGDataToFileStrategy;
    private WriteReadDataToFileStrategy mWritACCDataToFileStrategy;
    private int mPreHeartRate;
    private static final int D_valueMaxValue = 20;


    void calcuHeart(int[] beforeFilterTempEcgInts, int[] afterFilterTempEcgInts){
        if (mIsrecording){
            //在正在记录数据，需要写到文件里面
            if (mWriteECGDataToFileStrategy ==null){
                mWriteECGDataToFileStrategy = new WriteReadDataToBinaryFile(EcgAccDataUtil.fileExtensionType_ECG);
                CommunicateToBleService.getInstance().setmIsDataStart(true);
            }
            mWriteECGDataToFileStrategy.writeArrayDataToBinaryFile(beforeFilterTempEcgInts);
        }

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
            HeartRate heartRate = DiagnosisNDK.ecgHeart(calcuEcgRateBeforeFilter, calcuEcgRateAfterFilter, calcuEcgRateAfterFilter.length, Constant.oneSecondFrame);
            //int heartRate = DiagnosisNDK.ecgHeart( calcuEcgRateAfterFilter, calcuEcgRateAfterFilter.length, Constant.oneSecondFrame);
            Log.i(TAG,"heartRate:"+ heartRate);

            if (onHeartCalcuListener!=null && heartRate!=null){
                onHeartCalcuListener.onReceiveHeart(heartRate.rate);
            }
            /*if (onHeartCalcuListener!=null ){
                onHeartCalcuListener.onReceiveHeart(heartRate);
            }*/


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

    //更新心率：4s更新一次，如果2个心率差值大于20，则递增显示
    void updateHeartUI(final int heartRate, final TextView tv_healthydata_rate) {
        if (mPreHeartRate>0){
            int count = 0;
            final int d_value = heartRate - mPreHeartRate;

            if (d_value > D_valueMaxValue) {
                count = (d_value) / D_valueMaxValue + 1;
            } else if (d_value < -D_valueMaxValue) {
                count = (d_value) / D_valueMaxValue - 1;
            }
            Log.i(TAG,"count："+count);
            /*if (count != 0) {
                final int finalCount = count;
                new Thread(){
                    @Override
                    public void run() {
                        for (int i = 0; i< finalCount; i++){
                            final int heart ;
                            if (i==finalCount-1){
                                heart = heartRate;
                            }
                            else {
                                heart = mPreHeartRate + Math.abs(d_value) / finalCount*(i+1);
                            }

                            if (activity!=null && !activity.isFinishing()){
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_healthydata_rate.setText(heart +"");
                                    }
                                });
                            }
                        }
                    }
                }.start();
            }
            else {
                tv_healthydata_rate.setText(heartRate +"");
            }*/
            tv_healthydata_rate.setText(heartRate +"");
        }

        String showHeartString = heartRate==0?"--":heartRate+"";
        tv_healthydata_rate.setText(showHeartString);

        Log.i(TAG,"mPreHeartRate:"+ mPreHeartRate);
        Log.i(TAG,"heartRate:"+ heartRate);
        mPreHeartRate = heartRate;
        MyApplication.currentHeartRate = heartRate;
    }

    //处理加速度数据
    void calcuStride(int[] accOneGroupDataInts) {
        if (mIsrecording){
            //在正在记录数据，需要写到文件里面
            if (mWritACCDataToFileStrategy ==null){
                mWritACCDataToFileStrategy = new WriteReadDataToBinaryFile(EcgAccDataUtil.fileExtensionType_ACC);
            }
            mWritACCDataToFileStrategy.writeArrayDataToBinaryFile(accOneGroupDataInts);
        }

        if (accCalcuDataIndex<EcgAccDataUtil.accDataLength){
            for (int i: accOneGroupDataInts){
                //accData.add(i);
                accByteData[accCalcuDataIndex++] = (byte)i;
            }
        }
        else {
            //计算
            int tempStridefre = MyUtil.getStridefreByAccData(accByteData);
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

    void setmIsrecording(boolean mIsrecording) {
        this.mIsrecording = mIsrecording;
    }

    String stopRecording(){
        setmIsrecording(false);
        if (mWriteECGDataToFileStrategy !=null){
            return mWriteECGDataToFileStrategy.closeArrayDataStreamResource();  //关闭写入流，顺便得到写人文件的文件名
        }
        return null;
    }

    public static String calcuOxygenState(int heartRate, Context context) {
        int maxRate = 220- HealthyIndexUtil.getUserAge();
        if (heartRate<=maxRate*0.6){
            return context.getResources().getString(R.string.exercise_flat);
        }
        else if (maxRate*0.6<heartRate && heartRate<=maxRate*0.75){
            return context.getResources().getString(R.string.exercise_oxygenated);
        }
        else if (maxRate*0.75<heartRate && heartRate<=maxRate*0.95){
            return context.getResources().getString(R.string.exercise_without_oxygen);
        }
        else if (maxRate*0.95<heartRate ){
            return context.getResources().getString(R.string.exercise_in_danger);
        }
        return context.getResources().getString(R.string.exercise_oxygenated);
    }

    private OnHeartCalcuListener onHeartCalcuListener;

    public interface OnHeartCalcuListener{
        void onReceiveHeart(int currentHeartRate);
        void onReceiveStride(int currentHeartRate);
    }

    void setOnHeartCalcuListener(OnHeartCalcuListener onHeartCalcuListener){
        this.onHeartCalcuListener = onHeartCalcuListener;
    }


}
