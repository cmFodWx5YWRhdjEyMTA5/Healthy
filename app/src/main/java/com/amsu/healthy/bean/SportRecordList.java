package com.amsu.healthy.bean;

import java.util.List;

/**
 * author：WangLei
 * date:2017/11/1.
 * QQ:619321796
 */

public class SportRecordList {
    private String key;
    private List<SportRecord> value;

    public SportRecordList(String key, List<SportRecord> value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<SportRecord> getValue() {
        return value;
    }

    public void setValue(List<SportRecord> value) {
        this.value = value;
    }
}
