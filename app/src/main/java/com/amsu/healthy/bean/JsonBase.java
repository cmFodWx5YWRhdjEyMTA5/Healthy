package com.amsu.healthy.bean;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by HP on 2016/12/21.
 */
public class JsonBase<T> {
    public int ret;
    public T errDesc;

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
