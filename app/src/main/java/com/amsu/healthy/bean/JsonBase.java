package com.amsu.healthy.bean;

/**
 * Created by HP on 2016/12/21.
 */
public class JsonBase<T> {
    int ret;
    T errDesc;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public T getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(T errDesc) {
        this.errDesc = errDesc;
    }

    @Override
    public String toString() {
        return "JsonBase{" +
                "ret='" + ret + '\'' +
                ", errDesc='" + errDesc + '\'' +
                '}';
    }
}
