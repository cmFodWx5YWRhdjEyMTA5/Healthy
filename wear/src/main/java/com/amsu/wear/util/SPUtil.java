package com.amsu.wear.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.amsu.wear.application.MyApplication;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.bleinteractionlibrary.utils
 * @time 12/4/2017 3:50 PM
 * @describe
 */

public class SPUtil {

    public static final String spName = "spinfo";
    //创建一个写入器
    private static SharedPreferences mPreferences;

    public static void putIntValueToSP(String key,int value){
        if (mPreferences==null){
            mPreferences = MyApplication.getContext().getSharedPreferences(spName,Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putInt(key,value).apply();
    }

    public static int getIntValueFromSP(String key){
        if (mPreferences==null){
            mPreferences = MyApplication.getContext().getSharedPreferences(spName,Context.MODE_PRIVATE);
        }
        return mPreferences.getInt(key,-1);
    }

    public static int getIntValueFromSP(String key,int defValue ){
        if (mPreferences==null){
            mPreferences = MyApplication.getContext().getSharedPreferences(spName,Context.MODE_PRIVATE);
        }
        return mPreferences.getInt(key,defValue);
    }

    public static void putStringValueToSP(String key,String value){
        if (mPreferences==null){
            mPreferences = MyApplication.getContext().getSharedPreferences(spName,Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(key,value).apply();
    }

    public static String getStringValueFromSP(String key){
        if (mPreferences==null){
            mPreferences = MyApplication.getContext().getSharedPreferences(spName,Context.MODE_PRIVATE);
        }
        return mPreferences.getString(key,"");
    }

    public static void putBooleanValueToSP(String key,boolean value){
        if (mPreferences==null){
            mPreferences = MyApplication.getContext().getSharedPreferences(spName,Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean(key,value).apply();
    }

    public static boolean getBooleanValueFromSP(String key){
        if (mPreferences==null){
            mPreferences = MyApplication.getContext().getSharedPreferences(spName,Context.MODE_PRIVATE);
        }
        return mPreferences.getBoolean(key,false);
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     *
     * @param key
     * @param object
     */
    public static void put(String key, Object object) {
        SharedPreferences.Editor editor = mPreferences.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        }
        else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        }
        else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        }
        else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        }
        else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        }
        else {
            editor.putString(key, object.toString());
        }
        editor.apply();
    }

    //清楚SP里的所有数据
    public static void clearAllSPData(){
        mPreferences.edit().clear().apply();
    }

    //从SP获取数据对象
    //1.单个对象调用：User user = (User) SPUtil.getObjectFromSP("user", User.class);
    //2.列表调用：   List<User> usersfromSp = (List<User>) SPUtil.getObjectFromSP("users", new ArrayList<>().getClass());
    public static Object getObjectFromSP(String key, Class classz){
        Gson gson = new Gson();
        String objectString = getStringValueFromSP(key);
        if (!TextUtils.isEmpty(objectString)){
            return gson.fromJson(objectString,classz);
        }
        return null;
    }

    //数据对象存入SP
    public static void putObjectToSP(String key, Object object){
        if (object!=null){
            Gson gson = new Gson();
            String objectString = gson.toJson(object);
            putStringValueToSP(key,objectString);
        }
        else {
            putStringValueToSP(key,"");
        }
    }

    public static <T> void putListToSP(List<T> list, String key){
        if (list!=null && list.size()>0){
            Gson gson = new Gson();
            String listString = gson.toJson(list);
            SPUtil.putStringValueToSP(key,listString);
        }
    }

    public static <T> List<T> getListFromSP(String key, Type type){
        Gson gson = new Gson();
        String listString = SPUtil.getStringValueFromSP(key);
        if (!TextUtils.isEmpty(listString)){
            return gson.fromJson(listString, type);
        }
        return null;
    }

}