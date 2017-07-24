package com.amsu.healthy.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Debug;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.activity.SosActivity;
import com.amsu.healthy.activity.StartRunActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.bean.DeviceList;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.service.LocalGuardService;
import com.amsu.healthy.service.MyTestService2;
import com.amsu.healthy.service.RemoteGuardService;
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
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.content.Context.ACTIVITY_SERVICE;

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
                dialog.setCanceledOnTouchOutside(false);
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
        if (context instanceof Activity) {
            Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
        }
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

    public static void saveDeviceToSP(Device device) {
        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
        if (device !=null){
            if (!MyUtil.isEmpty(device.getName())){
                edit.putString("name",device.getName());
            }
            if (!MyUtil.isEmpty(device.getLEName())){
                edit.putString("LEName",device.getLEName());
            }
            if (!MyUtil.isEmpty(device.getState())){
                edit.putString("state",device.getState());
            }
            if (!MyUtil.isEmpty(device.getMac())){
                edit.putString("mac",device.getMac());
            }
        }
        else {
            edit.putString("name","");
            edit.putString("LEName","");
            edit.putString("state","");
            edit.putString("mac","");
        }

        edit.apply();
    }

    public static Device getDeviceFromSP(){
        String name = getStringValueFromSP("name");
        String LEName = getStringValueFromSP("LEName");
        String state = getStringValueFromSP("state");
        String mac = getStringValueFromSP("mac");
        Device device = null;
        if (!LEName.equals("") && !mac.equals("")){
            device = new Device(name,state,mac,LEName,0);
        }
        return device;
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

    //清楚SP里的所有数据
    public static void clearAllSPData(){
        MyApplication.sharedPreferences.edit().clear().apply();
    }

    public static boolean isEmpty(String text){
        if (text==null || text.equals("")){
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean isListIntegerEmpty(List<Integer> objectList){
        if (objectList==null || objectList.size()>0){
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

            preWeekString =+preWeekFirstDayMouth+"."+preWeekFirstDay+" — "+preWeekListDayMouth+"."+pretWeekListDay; // 3.13-3.19
            weekStringLiStrings.add(preWeekString);
        }
        for(String w:weekStringLiStrings){
            System.out.println(w);
        }
        return weekStringLiStrings;
    }

    public static  List<String> getWeekStringList(int weekCount,int year) {
        List<String> weekStringLiStrings = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,11);
        calendar.set(Calendar.DAY_OF_MONTH,29);

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

            preWeekString =+preWeekFirstDayMouth+"."+preWeekFirstDay+" — "+preWeekListDayMouth+"."+pretWeekListDay; // 3.13-3.19
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
        return new int[0];
    }

    public static float[] listToFloatArray(List<String> list){
        if (list!=null && list.size()>0){
            float[] ret = new float[list.size()];
            for(int i = 0;i < ret.length;i++)
                ret[i] = Float.parseFloat(list.get(i));
            return ret;
        }
        return new float[0];
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
        String sosNumberListString = getStringValueFromSP(Constant.sosNumberList);
        Gson gson = new Gson();
        List<SosActivity.SosNumber> sosNumberList = gson.fromJson(sosNumberListString, new TypeToken<List<SosActivity.SosNumber>>() {
        }.getType());
        return sosNumberList;
    }

    public static void putSosNumberList(List<SosActivity.SosNumber> sosNumberList){
        Gson gson = new Gson();
        String sosNumberListString = gson.toJson(sosNumberList);
        MyUtil.putStringValueFromSP(Constant.sosNumberList,sosNumberListString);
    }

    public static void showPopWindow(final int connectType) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                //Log.i(TAG,"showPopWindow:"+activity.getClass());
                final BaseActivity activity = MyApplication.mCurrApplicationActivity;
                Log.i(TAG,"MyApplication.mCurrApplicationActivity:"+MyApplication.mCurrApplicationActivity);
                //Log.i(TAG,"activity:"+activity.getClass());

                final boolean isConnectedSuccess;

                if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;
                View popupView = View.inflate(activity, R.layout.layout_popupwindow_onoffline, null);
                ImageView iv_pop_icon = (ImageView) popupView.findViewById(R.id.iv_pop_icon);
                TextView tv_pop_text = (TextView) popupView.findViewById(R.id.tv_pop_text);
                if (connectType == 0) {
                    //断开
                    iv_pop_icon.setImageResource(R.drawable.duankai);
                    tv_pop_text.setText("设备连接已断开");
                    isConnectedSuccess = false;

                } else {
                    //连接
                    iv_pop_icon.setImageResource(R.drawable.yilianjie);
                    tv_pop_text.setText("设备已连接");

                    isConnectedSuccess = true;
                }

                final PopupWindow mPopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.setTouchable(true);
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), (Bitmap) null));

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //showToask(finalActivity,"设备连接或断开");
                        //mPopupWindow.showAsDropDown(activity.getTv_base_rightText());
                        if (activity.isFinishing() || activity.isDestroyed()) return;
                        if (!mPopupWindow.isShowing()) {
                            mPopupWindow.showAtLocation(activity.getTv_base_rightText(), Gravity.TOP, 0, 0);
                            Log.i(TAG, "PopupWindow.showAtLocation:");
                        }

                        if (activity.getClass().getSimpleName().equals("StartRunActivity")) {
                            StartRunActivity.setDeviceConnectedState(isConnectedSuccess);
                        }
                    }
                });


                try {
                    Thread.sleep(3000);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (activity.isFinishing() || activity.isDestroyed()) return;
                            if (mPopupWindow.isShowing()) {
                                mPopupWindow.dismiss();
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }


    public static short getShortByTwoBytes(byte argB1, byte argB2) {
        return (short) ((argB1 & 0xFF)| (argB2 << 8));
    }

    /**
     * 注释：short到字节数组的转换！
     * @param
     * @return
     */
    public static byte[] shortToByte(short number){
        int temp = number;
        byte[] b =new byte[2];
        for(int i =0; i < b.length; i++){
            b[i]=new Integer(temp &0xff).byteValue();
            // 将最低位保存在最低位
            temp = temp >>8;// 向右移8位
        }
        return b;
    }

    //判断一个服务是否正在运行
    public static boolean isServiceWorked(Context context, String serviceName) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);

        if (runningService!=null){
            for (int i = 0; i < runningService.size(); i++) {
                if (runningService.get(i).service.getClassName().equals(serviceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Set<String> getRunningAppProcessInfoList(Context context){
        Set<String> runningAppsInfo = new HashSet<>();

        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo service : runningServices) {
            Log.i(TAG,"service:"+service.process);

            String pkgName = service.process.split(":")[0];
            runningAppsInfo.add(service.process);
            /*try {
                ActivityManager.RunningAppProcessInfo item = new ActivityManager.RunningAppProcessInfo();
                item.pkgList = new String[] { pkgName };
                item.pid = service.pid;
                item.processName = service.process;
                item.uid = service.uid;

                runningAppsInfo.add(service.process);

            } catch (Exception e) {

            }*/
        }
        return runningAppsInfo;
    }

    // 正在运行的
    public static void KillRunningProcess(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        // 获取正在运行的应用
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo ra : runningAppProcesses) {
            Log.i(TAG,"ra.processName: "+ra.processName);
            // 这里主要是过滤系统的应用和电话应用，当然你也可以把它注释掉。
            if (ra.processName.equals("system") || ra.processName.equals("com.android.phone") || ra.processName.equals("com.amsu.healthy")
                    || ra.processName.equals("com.remote1") || ra.processName.equals("com.amsu.healthy:MyTestService2")) {
                continue;
            }
            activityManager.killBackgroundProcesses(ra.processName);
        }
    }

    // 将Json数组解析成相应的映射对象列表
    public static <T> List<T> parseJsonArrayWithGson(JsonBase jsonBase, Class<T[]> clazz) {
        Gson gson = new Gson();
        List<T> errDesc= (List<T>) jsonBase.errDesc;
        System.out.println(errDesc);
        String json = gson.toJson(errDesc);
        System.out.println(json);
        T[]  result = gson.fromJson(json, clazz);
        return  Arrays.asList(result);
    }

    public static <T> JsonBase<T> commonJsonParse(String result, Type type){
        Gson gson = new Gson();
        JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
        if (jsonBase!=null && jsonBase.ret==0) {
            return gson.fromJson(result, type);
        }
        return jsonBase;
    }

    public static void startAllService(final Context context) {
        new Thread(){
            @Override
            public void run() {
                startServices(context);
                //每隔1s判断其他2个服务是否运行，没有运行则开始运行
                MyTimeTask.startTimeRiseTimerTask(50, new MyTimeTask.OnTimeChangeAtScendListener() {
                    @Override
                    public void onTimeChange(Date date) {
                        startServices(context);
                    }
                });
            }
        }.start();
    }

    public static void scheduleService(Context context,int jobID,String className) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i(TAG,"scheduleService:"+className);
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo = new JobInfo.Builder(jobID, new ComponentName(context.getPackageName(),className))
                    .setPeriodic(100)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);
        }
        else {
        }
    }

    public static void startServices(Context context) {
        /*if(!MyUtil.isServiceWorked(context, "com.ble.ble.BleService")) {
            Intent service = new Intent(context, BleService.class);
            context.startService(service);
            Log.i(TAG, "Start BleService");
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(!MyUtil.isServiceWorked(context, "com.amsu.healthy.service.LocalGuardService")) {
                Intent service = new Intent(context, LocalGuardService.class);
                context.startService(service);
                Log.i(TAG, "Start LocalGuardService");
            }

            if(!MyUtil.isServiceWorked(context, "com.amsu.healthy.service.MyTestService2")) {
                Intent service = new Intent(context, MyTestService2.class);
                context.startService(service);
                Log.i(TAG, "Start MyTestService2");
            }

            if(!MyUtil.isServiceWorked(context, "com.amsu.healthy.service.RemoteGuardService")) {
                Intent service = new Intent(context, RemoteGuardService.class);
                context.startService(service);
                Log.i(TAG, "Start RemoteGuardService");
            }
        }
        else {
            if(!MyUtil.isServiceWorked(context, "com.amsu.healthy.service.MyTestService4")) {
                Intent service = new Intent(context, com.amsu.healthy.service.MyTestService4.class);
                context.startService(service);
                Log.i(TAG, "Start MyTestService4");
            }
        }

        if(!MyUtil.isServiceWorked(context, "com.amsu.healthy.service.CommunicateToBleService")) {
            Intent service = new Intent(context, com.amsu.healthy.service.CommunicateToBleService.class);
            context.startService(service);
            Log.i(TAG, "Start CommunicateToBleService");
        }
    }

    public static void stopServices(Context context) {
        /*if(!MyUtil.isServiceWorked(context, "com.ble.ble.BleService")) {
            Intent service = new Intent(context, BleService.class);
            context.startService(service);
            Log.i(TAG, "Start BleService");
        }*/

        if(MyUtil.isServiceWorked(context, "com.amsu.healthy.service.LocalGuardService")) {
            Intent service = new Intent(context, LocalGuardService.class);
            context.stopService(service);
            Log.i(TAG, "stop LocalGuardService");
        }

       /* if(!MyUtil.isServiceWorked(context, "com.amsu.healthy.service.MyTestService2")) {
            Intent service = new Intent(context, MyTestService2.class);
            context.startService(service);
            Log.i(TAG, "Start MyTestService2");
        }*/

        if(MyUtil.isServiceWorked(context, "com.amsu.healthy.service.RemoteGuardService")) {
            Intent service = new Intent(context, RemoteGuardService.class);
            context.stopService(service);
            Log.i(TAG, "stop RemoteGuardService");
        }

        /*if(!MyUtil.isServiceWorked(context, "com.amsu.healthy.service.MyTestService4")) {
            Intent service = new Intent(context, com.amsu.healthy.service.MyTestService4.class);
            context.startService(service);
            Log.i(TAG, "Start MyTestService4");
        }*/

        if(MyUtil.isServiceWorked(context, "com.amsu.healthy.service.CommunicateToBleService")) {
            Intent service = new Intent(context, com.amsu.healthy.service.CommunicateToBleService.class);
            context.stopService(service);
            Log.i(TAG, "stop CommunicateToBleService");
        }
    }

    public static void stopAllServices(Context context) {
        Intent service = new Intent(context, LocalGuardService.class);
        boolean b = context.stopService(service);
        Log.i(TAG, "stop LocalGuardService:"+b);


        Intent service1 = new Intent(context, RemoteGuardService.class);
        boolean b1 = context.stopService(service1);
        Log.i(TAG, "stop RemoteGuardService："+b1);

        Intent service2 = new Intent(context, com.amsu.healthy.service.CommunicateToBleService.class);
        boolean b2 = context.stopService(service2);
        Log.i(TAG, "stop CommunicateToBleService："+b2);
    }

    //判断一个字符是不是Long型
    public static long parseValidLong(String str){
        try{
            return Long.parseLong(str);
        }catch(NumberFormatException e){
            try{
                return (long) Float.parseFloat(str);
            }catch(NumberFormatException e1){
                return -1;
            }
        }
    }

    public static void destoryAllAvtivity(){
        for (Activity activity:MyApplication.mActivities){
            activity.finish();
        }
        MyApplication.mActivities.clear();
    }
}

