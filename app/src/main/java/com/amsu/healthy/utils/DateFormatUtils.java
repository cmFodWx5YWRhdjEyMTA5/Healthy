package com.amsu.healthy.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * author：WangLei
 * date:2017/9/30.
 * QQ:619321796
 * tel:13257914320
 */

public class DateFormatUtils {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_ = "yyyy/MM/dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String MM_DD_HH_MM = "MM-dd HH:mm";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY = "yyyy";
    public static final String MM_DD = "MM-dd";
    public static final String HH_MM = "HH:mm";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String MM_SS = "mm:ss";
    private static final String EEE_HH_MM = "EEE HH:mm";
    public static final String MM = "MM";
    public static final String DD = "dd";
    public static final String YYYY_MM_CHINA = "yyyy年MM月";
    public static final String YYYY_MM_DD_CHINA = "yyyy年MM月dd日";
    public static final String YYYY_MM_DD_CHINA_HH_MM = "yyyy年MM月dd日 HH:mm";
    public static final String MM_DD_CHINA = "MM月dd日";
    public final static String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    public static DateFormat yyyyMMddHHmmssDateFormat;

    /**
     * <br>获取星期</br>
     * <p>
     * param date 2014-03-24
     * return
     */
    public static String getWeek(SimpleDateFormat formatYMD, String date) {
        String str = "";
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatYMD.parse(date));
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            str = dayNames[dayOfWeek - 1];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * yyyyMMddHHmmss 格式的日期解析器
     */
    public static DateFormat getyyyyMMddHHmmssDateFormat() {
        if (null == yyyyMMddHHmmssDateFormat) {
            yyyyMMddHHmmssDateFormat.setTimeZone(TimeZoneUtil.getLocalTimeZone());
        }
        return yyyyMMddHHmmssDateFormat;
    }

    /**
     * Description: 根据当前时间yyyyMMddHHmmss格式的字符串
     */
    public static String getDateTime(TimeZone timeZone) {
        DateFormat dateFormat = getyyyyMMddHHmmssDateFormat();
        dateFormat.setTimeZone(timeZone);
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        return dateFormat.format(date);
    }

    /**
     * 时间转换
     */
    public static String getFormatTime(long time, String format) {
        if (time == 0) {
            return "";
        }
        Date date = getLocalDateByUtc(time);
        return getFormatTime(date, format);
    }


    /**
     * 时间转换
     */
    public static String getFormatTime(Date date, String format) {
        String str = "";
        SimpleDateFormat sdFormat = new SimpleDateFormat(format);
        str = sdFormat.format(date);
        return str;
    }

    /**
     * 时间转换  格式当前时间
     */
    public static String getNowDateFormatTime(String format) {
        Date date = new Date(getUtcMillis());
        return getFormatTime(date, format);
    }

    /**
     * 根据utc的毫秒级获取本地日期
     */
    public static Date getLocalDateByUtc(long utcMillis) {
        return new Date(utcMillis);
    }

    public static long getUtcMillis() {
        return System.currentTimeMillis();
    }
}
