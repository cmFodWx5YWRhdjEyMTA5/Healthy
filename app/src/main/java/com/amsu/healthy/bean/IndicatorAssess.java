package com.amsu.healthy.bean;

/**
 * Created by HP on 2017/2/19.
 */

public class IndicatorAssess {
    private int scre;
    private String typeName;
    private String state;
    private String suggestion;
    private String value;

    public IndicatorAssess(int scre, String typeName, String suggestion) {
        this.scre = scre;
        this.typeName = typeName;
        this.suggestion = suggestion;
    }

    public IndicatorAssess(int scre, String typeName, String state, String suggestion) {
        this.scre = scre;
        this.typeName = typeName;
        this.state = state;
        this.suggestion = suggestion;
    }

    public IndicatorAssess(int scre, String typeName, String state, String suggestion, String value) {
        this.scre = scre;
        this.typeName = typeName;
        this.state = state;
        this.suggestion = suggestion;
        this.value = value;
    }

    public int getScre() {
        return scre;
    }

    public void setScre(int scre) {
        this.scre = scre;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
