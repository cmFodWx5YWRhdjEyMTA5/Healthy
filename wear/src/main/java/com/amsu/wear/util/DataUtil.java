package com.amsu.wear.util;

import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.util
 * @time 2018-03-12 11:31 AM
 * @describe
 */
public class DataUtil {

    public static float[] listToFloatArray(List<String> list){
        if (list!=null && list.size()>0){
            float[] ret = new float[list.size()];
            for(int i = 0;i < ret.length;i++)
                ret[i] = Float.parseFloat(list.get(i));
            return ret;
        }
        return new float[0];
    }

    public static int[] listToIntArray(List<Integer> list){
        if (list!=null && list.size()>0){
            int[] ret = new int[list.size()];
            for(int i = 0;i < ret.length;i++)
                ret[i] = list.get(i);
            return ret;
        }
        return new int[0];
    }
}
