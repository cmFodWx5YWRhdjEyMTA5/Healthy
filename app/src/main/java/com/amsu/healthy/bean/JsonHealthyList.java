package com.amsu.healthy.bean;

import java.util.List;

/**
 * Created by HP on 2016/12/21.
 */
public class JsonHealthyList extends JsonBase<List<HealthyPlan>>{


    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public List<HealthyPlan> getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(List<HealthyPlan> errDesc) {
        this.errDesc = errDesc;
    }
}
