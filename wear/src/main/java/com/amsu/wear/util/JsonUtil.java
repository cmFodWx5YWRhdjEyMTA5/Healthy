package com.amsu.wear.util;

import android.text.TextUtils;

import com.amsu.wear.bean.JsonBase;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.util
 * @time 2018-03-09 5:51 PM
 * @describe
 */
public class JsonUtil {
    public static JsonBase commonJsonParse(String result, Type type){
        JsonBase jsonBase = new JsonBase();
        //try {
            Gson gson = new Gson();
            jsonBase = gson.fromJson(result, JsonBase.class);
            if (jsonBase!=null && jsonBase.ret==0) {
                return gson.fromJson(result, type);
            }
            return jsonBase;
        //}catch (Exception e){
        //}
        //return jsonBase;
    }

    public static  <T> List<T> parseListJson(String result, Type type){
        try {
            Gson gson = new Gson();
            if (!TextUtils.isEmpty(result) && !result.equals(Constant.uploadRecordDefaultString)){
                return gson.fromJson(result,type);
            }
        }catch (Exception e){
            //Log.i(TAG,"e:"+e);
        }
        return new ArrayList<>();
    }





}
