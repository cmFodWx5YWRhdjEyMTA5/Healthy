package com.test.objects;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.test.objects
 * @time 11/22/2017 5:03 PM
 * @describe
 */
public class HeartRate {
    public int rate;
    public int noiseLevel;

    @Override
    public String toString() {
        return "HeartRate{" +
                "rate=" + rate +
                ", noiseLevel=" + noiseLevel +
                '}';
    }

}
