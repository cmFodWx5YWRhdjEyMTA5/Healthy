package com.amsu.healthy.bean;

import java.util.List;

/**
 * Created by HP on 2016/12/21.
 */
public class JsonHealthyList {
    String ret;
    List<HealthyPlan> errDesc;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public List<HealthyPlan> getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(List<HealthyPlan> errDesc) {
        this.errDesc = errDesc;
    }
}
