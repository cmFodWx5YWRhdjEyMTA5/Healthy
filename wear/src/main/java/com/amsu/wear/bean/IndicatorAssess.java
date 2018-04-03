package com.amsu.wear.bean;

/**
 * Created by HP on 2017/2/19.
 */

public class IndicatorAssess {
    private int value;
    private int percent;
    private String name;
    private String suggestion;
    private String evaluate;
    private int differenceValue;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public IndicatorAssess(int value, String name, String suggestion) {
        this.value = value;
        this.name = name;
        this.suggestion = suggestion;
    }

    public IndicatorAssess(int value, int percent, String name, String suggestion, String evaluate) {
        this.value = value;
        this.percent = percent;
        this.name = name;
        this.suggestion = suggestion;
        this.evaluate = evaluate;
    }

    public IndicatorAssess(int value, int percent, String name, String suggestion) {
        this.value = value;
        this.percent = percent;
        this.name = name;
        this.suggestion = suggestion;
    }

    public IndicatorAssess(int value, String name, String suggestion, String evaluate) {
        this.value = value;
        this.name = name;
        this.suggestion = suggestion;
        this.evaluate = evaluate;
    }

    @Override
    public String toString() {
        return "IndicatorAssess{" +
                "value=" + value +
                ", percent=" + percent +
                ", name='" + name + '\'' +
                ", suggestion='" + suggestion + '\'' +
                ", evaluate='" + evaluate + '\'' +
                ", differenceValue='" + differenceValue + '\'' +
                '}';
    }

    public int getDifferenceValue() {
        return differenceValue;
    }

    public void setDifferenceValue(int differenceValue) {
        this.differenceValue = differenceValue;
    }
}
