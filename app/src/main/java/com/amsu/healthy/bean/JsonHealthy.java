package com.amsu.healthy.bean;

/**
 * Created by HP on 2016/12/22.
 */
public class JsonHealthy extends JsonBase<HealthyPlan> {
    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public HealthyPlan getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(HealthyPlan errDesc) {
        this.errDesc = errDesc;
    }
}
