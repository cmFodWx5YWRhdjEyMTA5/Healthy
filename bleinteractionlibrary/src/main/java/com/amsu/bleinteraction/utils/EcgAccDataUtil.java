package com.amsu.bleinteraction.utils;

import android.util.Log;

import com.amsu.bleinteraction.proxy.BleConnectionProxy;

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

    public static double ECGSCALE_MODE_HALF = 0.5;
    public static double ECGSCALE_MODE_ORIGINAL = 1;
    public static double ECGSCALE_MODE_DOUBLE = 2;
    public static double ECGSCALE_MODE_QUADRUPLE = 4;
    public static double ECGSCALE_MODE_CURRENT = ECGSCALE_MODE_ORIGINAL;


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



    public static int[] getValuableEcgACCData(String hexData){
        Log.i(TAG,"hexData:"+hexData);
        int clothDeviceType = BleConnectionProxy.getInstance().getmConnectionConfiguration().clothDeviceType;

        int [] ecgInts ;
        int [] accInts ;

        if (clothDeviceType==BleConstant.clothDeviceType_old_encrypt || clothDeviceType==BleConstant.clothDeviceType_old_noEncrypt){
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

        else if (clothDeviceType==BleConstant.clothDeviceType_secondGeneration_IOE || clothDeviceType==BleConstant.clothDeviceType_secondGeneration_AMSU ||
                clothDeviceType==BleConstant.clothDeviceType_AMSU_EStartWith) {
            if (hexData.length() == 59) {  // 新版心电数据  00 C3 00 A5 00 B8 00 D7 00 79 00 58 00 37 00 25 00 27 FF E2
                //心电数据
                ecgInts = new int[ecgOneGroupLength];
                String[] split = hexData.split(" ");
                for (int i=0;i<split.length/2;i++){
                    short i1 = (short) Integer.parseInt(split[2 * i] + split[2 * i + 1], 16);
                    //Log.i(TAG,""+i1);
                    if (clothDeviceType==BleConstant.clothDeviceType_secondGeneration_IOE){
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

    public static String getWriteConfigureOrderHexString(int userAge,boolean isAutoOffline){
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

        int maxHeart  = (int) (0.95*(220- userAge));
        int minHeart  = (int) (0.75*(220-userAge));

        String maxHeartHex = Integer.toHexString(maxHeart);
        String minHeartHex = Integer.toHexString(minHeart);
        if (maxHeartHex!=null && maxHeartHex.length()==1){
            maxHeartHex = "0"+maxHeartHex;;
        }
        if (minHeartHex!=null && minHeartHex.length()==1){
            minHeartHex = "0"+minHeartHex;
        }

        dateHexString += maxHeartHex+minHeartHex;

        if (isAutoOffline){
            dateHexString += "01";
        }
        else {
            dateHexString += "00";
        }
        //Log.i(TAG,"dateHexString:"+dateHexString);

        String writeConfigureOrder = "FF010E"+ dateHexString +"0016";
        Log.i(TAG,"writeConfigureOrder:"+writeConfigureOrder);

        return writeConfigureOrder;
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





}
