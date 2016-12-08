package com.amsu.healthy.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.User;

/**
 * Created by root on 10/25/16.
 */

public class MyUtil {
    private static ProgressDialog dialog;

    public static void showDialog(String message,Context context){
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(context);
                dialog.setCancelable(true);
            }
            dialog.setMessage(message);
            dialog.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    public static void hideDialog() {
        if (dialog != null && dialog.isShowing())
            try {
                dialog.dismiss();
            } catch (Exception e) {
            }
    }

    public static float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static float getScreeenWidth(Activity activity){
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;  //屏幕宽
        int height = dm.heightPixels;  //屏幕高
        return width;
    }



    public static void saveUserToSP(User user) {
        //将登陆用户信息保存在MyApplication类的sharedPreferences
        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
        edit.putBoolean("isLogin",true);
        edit.putString("phone",user.getPhone());
        edit.putString("username",user.getUsername());
        edit.putString("birthday",user.getBirthday());
        edit.putString("sex",user.getSex());
        edit.putString("weight",user.getWeight());
        edit.putString("height",user.getHeight());
        edit.putString("area",user.getArea());
        edit.putString("email",user.getEmail());
        edit.apply();
    }


    public static String getStringValueFromSP(String key){
        return MyApplication.sharedPreferences.getString(key,"");
    }

    public static boolean getBooleanValueFromSP(String key){
        return MyApplication.sharedPreferences.getBoolean(key,false);
    }

    public static int getIntValueFromSP(String key){
        return MyApplication.sharedPreferences.getInt(key,-1);
    }


    public static void putStringValueFromSP(String key,String value){
        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
        edit.putString(key,value).apply();
    }

    public static void putBooleanValueFromSP(String key,Boolean value){
        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
        edit.putBoolean(key,value).apply();
    }

    public static void putIntValueFromSP(String key,int value){
        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
        edit.putInt(key,value).apply();
    }
}
