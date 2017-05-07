package com.amsu.healthy.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.amsu.healthy.activity.SosActivity;
import com.amsu.healthy.activity.SystemSettingActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.bean.DeviceList;
import com.amsu.healthy.bean.HeartRateList;
import com.amsu.healthy.bean.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.RequestParams;
import com.test.objects.HeartRateResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 10/25/16.
 */

public class MyUtil {
    private static final String TAG = "MyUtil";
    private static ProgressDialog dialog;

    public static void showDialog(String message,Context context){
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(context);
                dialog.setCancelable(true);
            }
            dialog.setMessage(message);
            dialog.show();
            Log.i(TAG,"dialog.show();");
        } catch (Exception e) {
            e.printStackTrace();
            // 在其他线程调用dialog会报错
        }
        Log.i(TAG,"showDialog:"+dialog.isShowing());
    }

    public static void hideDialog() {
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
            Log.i(TAG,"hideDialog:"+dialog.isShowing());
            dialog = null;
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
        return width;
    }

    public static float getScreeenHeight(Activity activity){
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;  //屏幕高
        return height;
    }

    public static void showToask(Context context ,String text){
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }

    public static void saveUserToSP(User user) {
        //将登陆用户信息保存在MyApplication类的sharedPreferences
        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
        if (!MyUtil.isEmpty(user.getUsername())){
            edit.putString("username",user.getUsername());
        }
        if (!MyUtil.isEmpty(user.getBirthday())){
            edit.putString("birthday",user.getBirthday());
        }
        if (!MyUtil.isEmpty(user.getSex())){
            edit.putString("sex",user.getSex());
        }
        if (!MyUtil.isEmpty(user.getWeight())){
            edit.putString("weight",user.getWeight());
        }
        if (!MyUtil.isEmpty(user.getHeight())){
            edit.putString("height",user.getHeight());
        }
        if (!MyUtil.isEmpty(user.getArea())){
            edit.putString("area",user.getArea());
        }
        if (!MyUtil.isEmpty(user.getEmail())){
            edit.putString("email",user.getEmail());
        }
        if (!MyUtil.isEmpty(user.getIcon())){
            edit.putString("icon",user.getIcon());
        }
        if (!MyUtil.isEmpty(user.getStillRate())){
            edit.putString("stillRate",user.getStillRate());
        }
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
        String stillRate = getStringValueFromSP("stillRate");
        User user = null;
        if (!phone.equals("") && !username.equals("")){
            user = new User(phone,username,birthday,sex,weight,height,area,email,icon,stillRate);
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
        Log.i(TAG,"cookie:"+cookie);
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

    public static String getSpecialFormatTime(String stringFormat,Date date){
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        SimpleDateFormat format = new SimpleDateFormat(stringFormat, Locale.CHINA);  //07-12 15:10
        return format.format(date);
    }


    public static String getECGFileNameDependFormatTime(Date date){
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);  //07-12 15:10
        return format.format(date);
    }

    public static List<Device> getDeviceListFromSP(){
        String name = "devicelist";
        String stringValueFromSP = MyUtil.getStringValueFromSP(name);
        Log.i("stringValueFromSP",stringValueFromSP);
        Gson gson = new Gson();

        DeviceList deviceList = gson.fromJson(stringValueFromSP, DeviceList.class);

        if (deviceList!=null){
            return deviceList.getDeviceList();
        }
        else{
            return new ArrayList<>();
        }
    }

    public static void putDeviceListToSP(DeviceList deviceList){
        Gson gson = new Gson();
        String  listString = gson.toJson(deviceList);
        MyUtil.putStringValueFromSP("devicelist",listString);
    }


    public static int[] getHeartRateListFromSP(){
        String name = "heartData";
        String stringValueFromSP = MyUtil.getStringValueFromSP(name);
        Log.i(TAG,"heartData:"+stringValueFromSP);
        if (!stringValueFromSP.equals("")){
            String[] split = stringValueFromSP.split(",");
            int []heartData = null;
            if (split.length>0){
                heartData = new int[split.length];
                for (int i=0;i<split.length;i++){
                    heartData[i] = Integer.parseInt(split[i]);
                }
            }
            return heartData;
        }

        return null;

    }

    public static HeartRateResult getHeartRateResultFromSP(){
        String gsonHeartRateResult = MyUtil.getStringValueFromSP("gsonHeartRateResult");
        Gson gson = new Gson();
        HeartRateResult heartRateResult = gson.fromJson(gsonHeartRateResult, HeartRateResult.class);
        return heartRateResult;
    }

    public static void putHeartRateListToSP(List<Integer> arry){
        Gson gson = new Gson();
        String  listString = gson.toJson(arry);
        MyUtil.putStringValueFromSP("heartRateList",listString);
    }

    //获取app的版本名称
    public static String getVersionName(Context context) {
        try{
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取app的版本号
    public static int getVersionCode(Context context) {
        try{
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static float getDimen(Context context,int resource){
        float dimension = context.getResources().getDimension(resource);
        return dimension;
    }

    /*
    * 编码：
        String oneBaseEncoder = Base64.encode(msg.getBytes());
    解码：
        String oneBaseDecoder = new String(Base64.decode(msg));
    * */
    public static String encodeBase64String(String text){
        String oneBaseEncoder = Base64.encodeToString(text.getBytes(),Base64.DEFAULT);
        return oneBaseEncoder;
    }

    public static String decodeBase64String(String text){
        String oneBaseDecoder = new String(Base64.decode(text,Base64.DEFAULT));
        return oneBaseDecoder;
    }

    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

    public static File base64ToFile(String base64, String fileName) {
        File file= null;
        FileOutputStream out = null;
        try {
            file= new File(fileName);
            if (!file.exists()) { // 文件夹
                file.createNewFile();
            }
            //byte[] bytes = new BASE64Decoder().decodeBuffer(base64);
            byte[] bytes = Base64.decode(base64,Base64.DEFAULT);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[1024];
            out = new FileOutputStream(file);
            int bytesum = 0;
            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread); // 文件写操作
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    //根据当前时间生成一个ecg格式文件
    public static String generateECGFilePath(Context context,Long timeMillis){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);  //07-12 15:10
        String fileName = format.format(new Date(timeMillis));
        return context.getCacheDir()+"/"+fileName+".ecg";
    }

    public static  List<String> getWeekStringList(int weekCount) {
        List<String> weekStringLiStrings = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int onWeekDayCount = 7;

        int weekOfDay = calendar.get(Calendar.DAY_OF_WEEK)-1;  //星期几
        int day = calendar.get(Calendar.DAY_OF_MONTH);   //哪一日
        int currMouth = calendar.get(Calendar.MONTH)+1;
        int currYear = calendar.get(Calendar.YEAR);

        int weekFirstDay = day-(weekOfDay-1);
        int weeklistDay = day-weekOfDay+1+6;

        int preWeekFirstDay ;
        int pretWeekListDay ;
        int preWeekFirstDayMouth = currMouth ;
        int preWeekListDayMouth = currMouth ;

        String preWeekString = "";

        for (int i = 0; i < weekCount; i++) {
            int currDayOfPreMouth = getDaysByYearMonth(currYear, currMouth-1);
            preWeekFirstDay = weekFirstDay -onWeekDayCount;
            pretWeekListDay = weeklistDay -onWeekDayCount;
            int tempMouth = currMouth;
            if (preWeekFirstDay<=0) {
                preWeekFirstDay = currDayOfPreMouth +preWeekFirstDay;
                preWeekFirstDayMouth = tempMouth-1;
            }

            if (pretWeekListDay<=0) {
                pretWeekListDay = currDayOfPreMouth + pretWeekListDay;
                preWeekListDayMouth = tempMouth-1;
                currMouth = currMouth-1;
            }

            if (currMouth==1) {
                currYear = currYear-1;
                currMouth = 13;
            }

            weekFirstDay = preWeekFirstDay;
            weeklistDay = pretWeekListDay;

            preWeekString =+preWeekFirstDayMouth+"."+preWeekFirstDay+" - "+preWeekListDayMouth+"."+pretWeekListDay; // 3.13-3.19
            weekStringLiStrings.add(preWeekString);
        }
        for(String w:weekStringLiStrings){
            System.out.println(w);
        }
        return weekStringLiStrings;
    }

    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    public static String getCueMapDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        Date curDate = new Date(time);
        String date = formatter.format(curDate);
        return date;
    }

    public static int[] listToIntArray(List<Integer> list){
        if (list!=null && list.size()>0){
            int[] ret = new int[list.size()];
            for(int i = 0;i < ret.length;i++)
                ret[i] = list.get(i);
            return ret;
        }
        return null;
    }

    public static String[] listToStringArray(List<String> list){
        if (list!=null && list.size()>0){
            String[] ret = new String[list.size()];
            for(int i = 0;i < ret.length;i++)
                ret[i] = list.get(i);
            return ret;
        }
        return null;
    }


    //获取当前季度
    public static int getCurrentQuertar(){
        /*第一季度：1月－3月
        第二季度：4月－6月
        第三季度：7月－9月
        第四季度：10月－12月
        * */
        Date date = new Date();
        int mouth = date.getMonth() + 1;
        if (1<=mouth && mouth<=3){
            return 1;
        }
        else if (4<=mouth && mouth<=6){
            return 2;
        }
        else if (7<=mouth && mouth<=9){
            return 3;
        }
        else {
            return 4;
        }
    }

    //获取年份
    public static int getCurrentYear(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    //获取 年 月
    public static String getCurrentYearAndMouthr(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM");
        Date curDate = new Date();
        String date = formatter.format(curDate);
        return date;
    }

    //获取 年 季度
    public static String getCurrentYearAndQuarter(){
        String restult = getCurrentYear()+"第"+getCurrentQuertar()+"季度";
        return restult;
    }

    public static String getReportDateStingForMouthAndDay(String datatime) {
        String[] split = datatime.split(" ");
        String[] split2 = split[0].split("-");
        return split2[1]+"月"+split2[2]+"日";
    }

    public static int getUserSex(){
        String sex = getStringValueFromSP("sex");
        return Integer.parseInt(sex);
    }

    public static int getUserWeight(){
        String weight = getStringValueFromSP("weight");
        return Integer.parseInt(weight);
    }

    public static List<SosActivity.SosNumber> getSosNumberList(){
        String sosNumberListString = getStringValueFromSP("sosNumberList");
        Gson gson = new Gson();
        List<SosActivity.SosNumber> sosNumberList = gson.fromJson(sosNumberListString, new TypeToken<List<SosActivity.SosNumber>>() {
        }.getType());
        return sosNumberList;
    }

    public static void putSosNumberList(List<SosActivity.SosNumber> sosNumberList){
        Gson gson = new Gson();
        String sosNumberListString = gson.toJson(sosNumberList);
        MyUtil.putStringValueFromSP("sosNumberList",sosNumberListString);
    }

}

