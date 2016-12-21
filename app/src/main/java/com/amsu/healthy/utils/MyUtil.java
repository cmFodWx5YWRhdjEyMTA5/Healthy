package com.amsu.healthy.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.User;
import com.lidroid.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public static void showToask(Context context ,String text){
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }

    public static void saveUserToSP(User user) {
        //将登陆用户信息保存在MyApplication类的sharedPreferences
        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
        edit.putString("username",user.getUsername());
        edit.putString("birthday",user.getBirthday());
        edit.putString("sex",user.getSex());
        edit.putString("weight",user.getWeight());
        edit.putString("height",user.getHeight());
        edit.putString("area",user.getArea());
        edit.putString("email",user.getEmail());
        edit.putString("icon",user.getIcon());
        edit.apply();
    }

    public static User getUserFromSP(){
        String username = getStringValueFromSP("username");
        String phone = getStringValueFromSP("phone");
        String birthday = getStringValueFromSP("birthday");
        String sex = getStringValueFromSP("sex");
        String weight = getStringValueFromSP("weight");
        String height = getStringValueFromSP("height");
        String area = getStringValueFromSP("area");
        String email = getStringValueFromSP("email");
        String icon = getStringValueFromSP("icon");
        User user = null;
        if (!phone.equals("")){
            user = new User(phone,username,birthday,sex,weight,height,area,email,icon);
        }

        return user;
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

    public static boolean isEmpty(String text){
        if (text==null || text.equals("")){
            return true;
        }
        else {
            return false;
        }
    }

    //添加Cookie
    public static void addCookieForHttp(RequestParams requestParams){
        String cookie = getStringValueFromSP("Cookie");
        if (!MyUtil.isEmpty(cookie)){
            requestParams.addHeader("Cookie",cookie);
        }
        else {
            Log.e("com.amsu.healthy","Cookie is null");
        }
    }

    public static String getFormatTime(Date date){
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);  //07-12 15:10
        return format.format(date);
    }
}
