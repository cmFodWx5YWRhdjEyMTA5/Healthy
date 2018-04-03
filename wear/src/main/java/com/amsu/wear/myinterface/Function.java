package com.amsu.wear.myinterface;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.myinterface
 * @time 2018-03-08 11:24 AM
 * @describe
 */
public interface Function<Result, Param> {

    Result function(Param... data);
}