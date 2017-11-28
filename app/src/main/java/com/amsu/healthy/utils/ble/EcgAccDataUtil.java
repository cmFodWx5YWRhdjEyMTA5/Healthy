package com.amsu.healthy.utils.ble;

import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.EcgView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by HP on 2016/12/6.
 */
public class EcgAccDataUtil {
    private static final String TAG = "EcgAccDataUtil";
    public static final int calGroupCalcuLength = 180; //
    public static final int timeSpanGgroupCalcuLength = 60; //
    public static final int ecgOneGroupLength = 10; //
    public static final int accOneGroupLength = 12; //
    public static final int accDataLength = 1800;

    private static double ECGSCALE_MODE_HALF = 0.5;
    private static double ECGSCALE_MODE_ORIGINAL = 1;
    private static double ECGSCALE_MODE_DOUBLE = 2;
    private static double ECGSCALE_MODE_QUADRUPLE = 4;
    public static double ECGSCALE_MODE_CURRENT = ECGSCALE_MODE_ORIGINAL;

    public static final int fileExtensionType_ECG = 1; //
    public static final int fileExtensionType_ACC = 2; //


    public static void geIntEcgaArr(String hexString,String splitSring,int startIndex,int parseLength,int[] ecgInts) {
        //int [] intEcgaArr = new int[parseLength];
        String[] split = hexString.split(splitSring);
        for (int i = startIndex; i < startIndex+parseLength; i++) {
            ecgInts[i-startIndex] = Integer.parseInt(split[i],16);
        }
    }

    public static List<Integer> geIntEcgaArrList(String hexString,String splitSring,int startIndex,int parseLength) {
        List<Integer> intEcgaArr = new ArrayList<>();
        String[] split = hexString.split(splitSring);
        for (int i = startIndex; i < startIndex+parseLength; i++) {
            //System.out.println("i="+i+"="+split[i]);
            int parseInt = Integer.parseInt(split[i],16);
            intEcgaArr.add(parseInt);
        }
        return intEcgaArr;
    }



    public static int[] getValuableEcgACCData(String hexData, LeProxy mLeProxy){
        Log.i(TAG,"hexData:"+hexData);

        int [] ecgInts ;
        int [] accInts ;

        if (LeProxy.getInstance().getClothDeviceType()==Constant.clothDeviceType_old_encrypt || LeProxy.getInstance().getClothDeviceType()==Constant.clothDeviceType_old_noEncrypt){
            if(hexData.startsWith("FF 83") && hexData.length()==44){   //旧版心电数据
                //Log.i(TAG,"心电hexData:"+hexData);
                ecgInts = new int[ecgOneGroupLength];
                EcgAccDataUtil.geIntEcgaArr(hexData, " ", 3, ecgOneGroupLength,ecgInts); //一次的数据，10位
                return ecgInts;
            }
            else if (hexData.startsWith("FF 86") && hexData.length() == 50) {  //旧版加速度数据FF 86 11 00 A4 06 AC 1E 9D 00 A4 06 AC 1E 9D 11 16   长度50
                //Log.i(TAG,"加速度hexData:"+hexData);
                accInts = new int[accOneGroupLength];
                EcgAccDataUtil.geIntEcgaArr(hexData, " ", 3, accOneGroupLength, accInts); //一次的数据，12位
                //dealWithAccelerationgData();
                return accInts;

            }
        }

        else if (mLeProxy.getClothDeviceType()==Constant.clothDeviceType_secondGeneration || mLeProxy.getClothDeviceType()==Constant.clothDeviceType_secondGeneration_our) {
            if (hexData.length() == 59) {  // 新版心电数据  00 C3 00 A5 00 B8 00 D7 00 79 00 58 00 37 00 25 00 27 FF E2
                //心电数据
                ecgInts = new int[ecgOneGroupLength];
                String[] split = hexData.split(" ");
                for (int i=0;i<split.length/2;i++){
                    short i1 = (short) Integer.parseInt(split[2 * i] + split[2 * i + 1], 16);
                    //Log.i(TAG,""+i1);
                    if (mLeProxy.getClothDeviceType()==Constant.clothDeviceType_secondGeneration){
                        ecgInts[i] = i1 /256+128;
                    }
                    else {
                        ecgInts[i] = i1 /16;
                    }
                    //Log.i(TAG,"ecgInts[i]:"+ecgInts[i]);
                }
                return ecgInts;
            }
            else if (hexData.length() == 35) {  //新版加速度数据  FF C4 00 11 0F 94 FF BE 00 25 0F 9C
                //加速度数据
                accInts = new int[accOneGroupLength];
                EcgAccDataUtil.geIntEcgaArr(hexData, " ", 0, accOneGroupLength, accInts); //一次的数据，12位
                return accInts;
            }
        }
        return null;
    }

    public static String getDataHexString(){
        SimpleDateFormat formatter = new SimpleDateFormat("yy MM dd HH mm ss");
        Date curDate = new Date();
        String dateString = formatter.format(curDate);
        System.out.println(dateString);
        String[] split = dateString.split(" ");
        String dateHexString = "";
        for (String s:split){
            String hex = Integer.toHexString(Integer.parseInt(s));
            if (hex.length()==1){
                hex ="0"+hex;
            }
            dateHexString += hex;
        }

        int maxHeart  = (int) (0.95*(220- HealthyIndexUtil.getUserAge()));
        int minHeart  = (int) (0.75*(220-HealthyIndexUtil.getUserAge()));

        String maxHeartHex = Integer.toHexString(maxHeart);
        String minHeartHex = Integer.toHexString(minHeart);
        if (maxHeartHex!=null && maxHeartHex.length()==1){
            maxHeartHex = "0"+maxHeartHex;;
        }
        if (minHeartHex!=null && minHeartHex.length()==1){
            minHeartHex = "0"+minHeartHex;;
        }

        dateHexString += maxHeartHex+minHeartHex;


        boolean mIsAutoOffline = MyUtil.getBooleanValueFromSP("mIsAutoOffline");
        if (mIsAutoOffline){
            dateHexString += "01";
        }
        else {
            dateHexString += "00";
        }
        //Log.i(TAG,"dateHexString:"+dateHexString);
        return dateHexString;
    }


    public static String getDataHexStringHaveScend(){
        SimpleDateFormat formatter = new SimpleDateFormat("yy MM dd HH mm ss");
        Date curDate = new Date();
        String dateString = formatter.format(curDate);
        System.out.println(dateString);
        String[] split = dateString.split(" ");
        String dateHexString = "";
        for (String s:split){
            String hex = Integer.toHexString(Integer.parseInt(s));
            if (hex.length()==1){
                hex ="0"+hex;
            }
            dateHexString += hex;
        }
        //Log.i(TAG,"dateHexString:"+dateHexString);
        return dateHexString;
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
        Log.i(TAG,"currentType:"+ECGSCALE_MODE_CURRENT);
        if (endProgress<=20){
            type = ECGSCALE_MODE_HALF;
        }
        else if(20<endProgress && endProgress<=40){
            type = ECGSCALE_MODE_ORIGINAL;
        }
        else if(40<endProgress && endProgress<=60){
            type = ECGSCALE_MODE_DOUBLE;
        }
        else if(60<endProgress && endProgress<=80){
            type = ECGSCALE_MODE_QUADRUPLE;
        }

        if (type!=ECGSCALE_MODE_CURRENT){
            ECGSCALE_MODE_CURRENT = type;
            //重新绘图
            Log.i(TAG,"调在增益");
            pv_healthydata_path.setRateLineR(type);
        }
    }


}
