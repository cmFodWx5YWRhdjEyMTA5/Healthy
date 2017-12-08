package com.amsu.healthy.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.utils.EcgAccDataUtil;
import com.amsu.healthy.R;
import com.amsu.healthy.view.EcgView;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.utils
 * @time 12/6/2017 4:42 PM
 * @describe
 */
public class HeartShowWayUtil {
    private static final String TAG = HeartShowWayUtil.class.getSimpleName();
    private static int mPreHeartRate;
    private static final int D_valueMaxValue = 20;

    //更新心率：4s更新一次，如果2个心率差值大于20，则递增显示
    public static void updateHeartUI(final int heartRate, final TextView tv_healthydata_rate, final Activity activity) {
        /*if (mPreHeartRate>0){
            int count = 0;
            final int d_value = heartRate - mPreHeartRate;

            if (d_value > D_valueMaxValue) {
                count = (d_value) / D_valueMaxValue + 1;
            } else if (d_value < -D_valueMaxValue) {
                count = (d_value) / D_valueMaxValue - 1;
            }
            Log.i(TAG,"count："+count);
            if (count != 0) {
                final int finalCount = count;
                new Thread(){
                    @Override
                    public void run() {
                        for (int i = 0; i< Math.abs(finalCount); i++){
                            final int heart ;
                            if (i==finalCount-1){
                                heart = heartRate;
                            }
                            else {
                                heart = mPreHeartRate + Math.abs(d_value) / finalCount*(i+1);
                            }

                            if (activity!=null && !activity.isFinishing()){
                                final int finalI = i;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_healthydata_rate.setText(heart +"");
                                        Log.i(TAG,"分段设置值 i:"+ finalI +",heart:"+heart);
                                    }
                                });
                            }
                            try {
                                long millis = (long) (1000*4f / Math.abs(finalCount));
                                Thread.sleep(millis);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }
            else {
                tv_healthydata_rate.setText(heartRate +"");
            }
            //tv_healthydata_rate.setText(heartRate +"");
        }*/

        String showHeartString = heartRate==0?"--":heartRate+"";
        tv_healthydata_rate.setText(showHeartString);

        Log.i(TAG,"mPreHeartRate:"+ mPreHeartRate);
        Log.i(TAG,"heartRate:"+ heartRate);
        mPreHeartRate = heartRate;
    }

    public static String calcuOxygenState(int heartRate, Context context) {
        int userAge = BleConnectionProxy.getInstance().getmConnectionConfiguration().userAge;
        int maxRate = 220- userAge;
        if (heartRate<=maxRate*0.6){
            return "平缓";
        }
        else if (maxRate*0.6<heartRate && heartRate<=maxRate*0.75){
            return "有氧";
        }
        else if (maxRate*0.75<heartRate && heartRate<=maxRate*0.95){
            return "无氧";
        }
        else if (maxRate*0.95<heartRate ){
            return "高危";
        }
        return "平缓";
    }


    private static BottomSheetDialog mBottomAdjustRateLineDialog;

    public static void showAlertAdjustLineSeekBar(final EcgView pv_healthydata_path, Context context) {
        if (mBottomAdjustRateLineDialog==null){
            mBottomAdjustRateLineDialog = new BottomSheetDialog(context);
            View inflate = LayoutInflater.from(context).inflate(R.layout.view_adjustline, null);

            mBottomAdjustRateLineDialog.setContentView(inflate);
            Window window = mBottomAdjustRateLineDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.mystyle);  //添加动画

            SeekBar sb_adjust = (SeekBar) inflate.findViewById(R.id.sb_adjust);
            sb_adjust.setMax(80);  //设置最大值，分成4个级别，0-20,20-40,40-60,60-80

            sb_adjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.i(TAG,"onProgressChanged:"+progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.i(TAG,"onStart:"+seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.i(TAG,"onStop:"+seekBar.getProgress());
                    int endProgress = seekBar.getProgress();
                    adjustRateLineRToEcgView(endProgress,pv_healthydata_path);
                }
            });
        }
        mBottomAdjustRateLineDialog.show();
    }

    /*根据进度给心电View设置放大的倍数
            *  可以在 ecgAmpSum < 5时 放大4倍
                在 5<=ecgAmpSum<12 时放大2倍
                在 12<=ecgAmpSum<26 时 不放大大不缩小。
                在ecgAmpSum>=26时 缩小两倍
            * */
    public static void adjustRateLineRToEcgView(int endProgress,EcgView pv_healthydata_path) {
        double type = 0;
        Log.i(TAG,"currentType:"+EcgAccDataUtil.ECGSCALE_MODE_CURRENT);
        if (endProgress<=20){
            type = EcgAccDataUtil.ECGSCALE_MODE_HALF;
        }
        else if(20<endProgress && endProgress<=40){
            type = EcgAccDataUtil.ECGSCALE_MODE_ORIGINAL;
        }
        else if(40<endProgress && endProgress<=60){
            type = EcgAccDataUtil.ECGSCALE_MODE_DOUBLE;
        }
        else if(60<endProgress && endProgress<=80){
            type = EcgAccDataUtil.ECGSCALE_MODE_QUADRUPLE;
        }

        if (type!=EcgAccDataUtil.ECGSCALE_MODE_CURRENT){
            EcgAccDataUtil.ECGSCALE_MODE_CURRENT = type;
            //重新绘图
            Log.i(TAG,"调在增益");
            pv_healthydata_path.setRateLineR(type);
        }
    }

}
