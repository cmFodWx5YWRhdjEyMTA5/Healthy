package com.amsu.wear.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @anthor haijun
 * @project name: Shop
 * @class name：com.haijun.shop.util
 * @time 2018-02-28 2:01 PM
 * @describe
 */
public class FormatUtil {

    //判断字符串是否为数字的方法:
    public static boolean isNumeric(String str){
        for (int i = 0; i < str.length(); i++){
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    //默认的数据格式
    public static String getDefaultFormatTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);  //2018-03-02
        return format.format(date);
    }

    //完整的默认的数据格式，带时分秒毫秒
    public static String getFullDefaultFormatTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);  //2018-03-02 14:56:28.636
        return format.format(date);
    }

    //自定义数据格式
    public static String getSpecialFormatTime(String stringFormat,Date date){
        SimpleDateFormat format = new SimpleDateFormat(stringFormat, Locale.CHINA);
        return format.format(date);
    }

    private static DecimalFormat mDecimalFormat;
    public static String getFormatDistance(double distance) {
        //构造方法的字符格式这里如果小数不足2位,会以0补足.
        if (mDecimalFormat==null){
            mDecimalFormat = new DecimalFormat("0.00");
        }
        return mDecimalFormat.format(distance/1000);
    }

    public static String getRunFormatTime(long duration) {
        //构造方法的字符格式这里如果小数不足2位,会以0补足.
        String myDuration;
        if (duration>60*60) {
            myDuration = duration/(60*60)+"h"+getFormatFloatValue((duration%(60*60))/60,"00")+"'"+getFormatFloatValue((duration%(60*60))%60,"00")+"''";
        }
        else {
            myDuration = getFormatFloatValue((duration%(60*60))/60,"00")+"'"+getFormatFloatValue((duration%(60*60))%60,"00")+"''";
        }
        return myDuration;
    }

    public static String getFormatFloatValue(double value,String format) {
        //构造方法的字符格式这里如果小数不足2位,会以0补足.
        return new DecimalFormat(format).format(value);
    }

    public static String getFormatRunPace(double distance, long duration) {
        float mapRetrurnSpeed =0;
        if (duration>0){
            mapRetrurnSpeed = (float) (distance / duration);
        }

        String formatSpeed;
        if (mapRetrurnSpeed==0){
            formatSpeed = "--";
        }
        else {
            int seconds = (int) ((1/mapRetrurnSpeed)*1000);
            if (seconds>=60*60*2){
                formatSpeed = "--";
            }
            else {
                formatSpeed = getFormatFloatValue(seconds/60,"00")+"'"+getFormatFloatValue(seconds%60,"00")+"''";
            }
        }
        return formatSpeed;
    }


    public static String getPaceFormatTime(long duration) {
        //构造方法的字符格式这里如果小数不足2位,会以0补足.
        String myDuration;
        if (duration>60*60) {
            myDuration = duration/(60*60)+"h"+getFormatFloatValue((duration%(60*60))/60,"00")+"'"+getFormatFloatValue((duration%(60*60))%60,"00")+"''";
        }
        else {
            myDuration = getFormatFloatValue((duration%(60*60))/60,"00")+"'"+getFormatFloatValue((duration%(60*60))%60,"00")+"''";
        }
        return myDuration;
    }
}
