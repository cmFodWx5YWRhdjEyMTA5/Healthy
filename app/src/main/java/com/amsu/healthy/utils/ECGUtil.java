package com.amsu.healthy.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2016/12/6.
 */
public class ECGUtil {

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

    public static int intToHex(String hex){
        return 	Integer.parseInt(hex,16);
    }


    // 23阶FIR滤波器，用于计算心率值所用，该滤波器更能滤除信号的毛刺，有利于计算R波峰。
    private static double fly0;
    private static double[] flx0 = new double[23];
    private static double[] a = { 0.0008, 0.0025, 0.0057, 0.0109, 0.0183, 0.0280,
            0.0393, 0.0513, 0.0629, 0.0724, 0.0788, 0.0810, 0.0788, 0.0724,
            0.0629, 0.0513, 0.0393, 0.0280, 0.0183, 0.0109, 0.0057, 0.0025,
            0.0008 };

    // 计算心率
    public static  int countEcgRate(int[] ecg, int len, int s_rate) {
        int result;
        try {
            for (int i = 0; i < len; i++) {
                ecg[i] = fir(ecg[i]);
            }

            List<Integer> R = new ArrayList<Integer>();
            int[] soc = new int[len];
            int[] diff = new int[len];

            diff[0] = 0;
            diff[1] = 0;
            for (int j = 2; j < len - 2; j++) {
                diff[j] = (ecg[j - 2] - 2 * ecg[j] + ecg[j + 2]);
            }
            diff[len - 1] = 0;
            diff[len - 2] = 0;

            int num = len / s_rate;
            int[] min = new int[num];

            for (int i = 0; i < num; i++) {
                min[i] = diff[s_rate * i];
                for (int j = 0; j < s_rate; j++) {
                    if (min[i] > diff[s_rate * i + j])
                        min[i] = diff[s_rate * i + j];
                }
            }

            float[] threshold = new float[num];
            for (int j = 0; j < num; j++)
                threshold[j] = (float) ((min[j]) * 0.6);

            int n = 0;
            for (int i = 0; i < num; i++) {
                for (int j = 0; j < s_rate && (s_rate * i + j) < len - 3; j++) {
                    if (diff[s_rate * i + j] > threshold[i]
                            && diff[s_rate * i + j + 1] > threshold[i]
                            && diff[s_rate * i + j + 2] <= threshold[i]
                            && diff[s_rate * i + j + 3] <= threshold[i])
                        soc[n++] = s_rate * i + j;
                }
            }
            for (int i = 0; i < n; i++) {
                int p = soc[i];
                int res = ecg[p];
                R.add(p);
                for (int j = p - 5; j < p + 5 && p > 5 && j < len; j++) {
                    if (res < ecg[j]) {
                        res = ecg[j];
                        R.set(R.size() - 1, j);
                    }
                }
            }

            for (int j = 0; j < n - 1; j++) {
                if ((R.get(j + 1) - R.get(j)) < (s_rate / 5)) {
                    if (ecg[R.get(j + 1)] > ecg[R.get(j)]) {
                        R.remove(j);
                        n--;
                        j--;
                    } else {
                        R.remove(j + 1);
                        n--;
                    }

                } else if ((R.get(j + 1) - R.get(j)) > (s_rate * 12 / 10)) {
                    int res = diff[R.get(j) + 100];
                    int pos = R.get(j) + 100;
                    for (int t = R.get(j) + 100; t < (R.get(j + 1) - 100); t++) {
                        if (res < diff[t]) {
                            res = diff[t];
                            pos = t;
                        }
                    }
                    res = ecg[pos];
                    int p_pos = pos;
                    for (int t = pos - 5; t < pos + 5; t++) {
                        if (res < ecg[t]) {
                            res = ecg[t];
                            p_pos = t;
                        }
                    }
                    R.add(j + 1, p_pos);
                    n++;
                    j++;
                }
            }

            int RR[] = new int[n - 1];
            for (int j = 0; j < n - 1; j++) {
                RR[j] = R.get(j + 1) - R.get(j);
            }

            int size = RR.length;
            int sum = 0;
            for (int j = 0; j < size; j++) {
                sum = sum + RR[j];
            }
            int avel = sum / size;
            int rate = 60 * s_rate / avel;
            if (rate > 60 && rate < 100) {
                result = rate;
            } else {
                result = 0;
            }

        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

    public static int fir(int d) {
        int i;
        try {
            for (i = 0; i < 22; i++)
                flx0[22 - i] = flx0[21 - i];
            flx0[0] = d;
            fly0 = 0;
            for (i = 0; i < 23; i++)
                fly0 += a[i] * flx0[i];
        } catch (Exception e) {
            fly0 = 0;
        }

        return (int) fly0;
    }
}
