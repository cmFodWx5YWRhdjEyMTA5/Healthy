package com.amsu.healthy.utils;

/**
 * Created by HP on 2016/12/6.
 */
public class ECGUtil {

    public static int intToHex(String hex){
        return 	Integer.parseInt(hex,16);
    }


    public static int[] geIntEcgaArr(String hexString,String splitSring,int startIndex,int parseLength) {
        int [] intEcgaArr = new int[parseLength];
        String[] split = hexString.split(splitSring);
        for (int i = startIndex; i < startIndex+parseLength; i++) {
            //System.out.println("i="+i+"="+split[i]);
            int parseInt = Integer.parseInt(split[i],16);
            intEcgaArr[i-startIndex] = parseInt;
        }
        return intEcgaArr;
    }


}
