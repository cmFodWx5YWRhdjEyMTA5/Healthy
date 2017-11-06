package com.amsu.healthy.utils;

/**
 * author：WangLei
 * date:2017/10/30.
 * QQ:619321796
 */

public class UStringUtil {
    /**
     * 是否为空或空字符串
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return null == str || str.trim().length() < 1 || str.equals("null");
    }
    public static String formatNumber(double x, int z) {
        return String.format("%." + z + "f", x);
    }
    public static String secondTime(int second) {
        int d = 0;
        int s = 0;
        d = second / 60;
        if (second % 60 != 0) {
            s = second % 60;
        }
        return d + "'" + s + "''";
    }

    /**
     * 计算速度 每小时/km
     */
    public static String getSpeed(float speed) {
        if (speed > 0) {
            double t = 1000 / speed;
            return secondTime((int) Math.round(t));
        }
        return "0'" + "00''";
    }

}
