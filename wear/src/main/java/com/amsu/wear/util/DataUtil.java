package com.amsu.wear.util;

import com.amap.api.maps.model.LatLng;

import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.util
 * @time 2018-03-12 11:31 AM
 * @describe
 */
public class DataUtil {

    public static float[] listToFloatArray(List<String> list) {
        if (list != null && list.size() > 0) {
            float[] ret = new float[list.size()];
            for (int i = 0; i < ret.length; i++)
                ret[i] = Float.parseFloat(list.get(i));
            return ret;
        }
        return new float[0];
    }

    public static int[] listToIntArray(List<Integer> list) {
        if (list != null && list.size() > 0) {
            int[] ret = new int[list.size()];
            for (int i = 0; i < ret.length; i++)
                ret[i] = list.get(i);
            return ret;
        }
        return new int[0];
    }

    public static void saveLocation(double lat, double lng) {
        if (lat != 0 && lng != 0) {
            SPUtil.putStringValueToSP(Constant.lat, String.valueOf(lat));
            SPUtil.putStringValueToSP(Constant.lng, String.valueOf(lng));
        }
    }

    public static LatLng getLocation() {
        String latStr = SPUtil.getStringValueFromSP(Constant.lat);
        String lngStr = SPUtil.getStringValueFromSP(Constant.lng);
        if (latStr != null && latStr.length() > 0 && lngStr != null && lngStr.length() > 0) {
            double lat = Double.parseDouble(latStr);
            double lng = Double.parseDouble(lngStr);
            return getLatLng(lat, lng);
        }
        return null;
    }

    public static LatLng getLatLng(double l1, double l2) {
        double temp = 0;
        if (l1 > l2) {
            temp = l1;
            l1 = l2;
            l2 = temp;
        }
        return new LatLng(l1, l2);
    }
}
