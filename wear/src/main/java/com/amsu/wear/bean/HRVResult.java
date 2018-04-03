package com.amsu.wear.bean;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.bean
 * @time 2018-03-12 2:35 PM
 * @describe
 */
public class HRVResult {
    public int state;
    public String suggestion;

    public HRVResult(int state, String suggestion) {
        this.state = state;
        this.suggestion = suggestion;
    }

    @Override
    public String toString() {
        return "HRVResult{" +
                "state=" + state +
                ", suggestion='" + suggestion + '\'' +
                '}';
    }
}
