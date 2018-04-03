package com.amsu.wear.bean;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.bean
 * @time 2018-03-12 3:39 PM
 * @describe
 */
public class IndexData {
    private String dec;
    private String valueString;
    private int value;

    public IndexData(String dec) {
        this.dec = dec;
    }

    public IndexData(String dec, String valueString, int value) {
        this.dec = dec;
        this.valueString = valueString;
        this.value = value;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
